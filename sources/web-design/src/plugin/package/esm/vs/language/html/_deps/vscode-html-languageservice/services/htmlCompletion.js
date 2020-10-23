/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
import { Position, CompletionItemKind, Range, TextEdit, InsertTextFormat } from '../../vscode-languageserver-types/main.js';
import { createScanner } from '../parser/htmlScanner.js';
import { ScannerState, TokenType } from '../htmlLanguageTypes.js';
import { entities } from '../parser/htmlEntities.js';
import * as nls from '../../../fillers/vscode-nls.js';
import { isLetterOrDigit, endsWith, startsWith } from '../utils/strings.js';
import { getAllDataProviders } from '../languageFacts/builtinDataProviders.js';
import { isVoidElement } from '../languageFacts/fact.js';
var localize = nls.loadMessageBundle();
var HTMLCompletion = /** @class */ (function () {
    function HTMLCompletion() {
        this.completionParticipants = [];
    }
    HTMLCompletion.prototype.setCompletionParticipants = function (registeredCompletionParticipants) {
        this.completionParticipants = registeredCompletionParticipants || [];
    };
    HTMLCompletion.prototype.doComplete = function (document, position, htmlDocument, settings) {
        var result = {
            isIncomplete: false,
            items: []
        };
        var completionParticipants = this.completionParticipants;
        var dataProviders = getAllDataProviders().filter(function (p) { return p.isApplicable(document.languageId) && (!settings || settings[p.getId()] !== false); });
        var text = document.getText();
        var offset = document.offsetAt(position);
        var node = htmlDocument.findNodeBefore(offset);
        if (!node) {
            return result;
        }
        var scanner = createScanner(text, node.start);
        var currentTag = '';
        var currentAttributeName;
        function getReplaceRange(replaceStart, replaceEnd) {
            if (replaceEnd === void 0) { replaceEnd = offset; }
            if (replaceStart > offset) {
                replaceStart = offset;
            }
            return { start: document.positionAt(replaceStart), end: document.positionAt(replaceEnd) };
        }
        function collectOpenTagSuggestions(afterOpenBracket, tagNameEnd) {
            var range = getReplaceRange(afterOpenBracket, tagNameEnd);
            dataProviders.forEach(function (provider) {
                provider.provideTags().forEach(function (tag) {
                    result.items.push({
                        label: tag.name,
                        kind: CompletionItemKind.Property,
                        documentation: tag.description,
                        textEdit: TextEdit.replace(range, tag.name),
                        insertTextFormat: InsertTextFormat.PlainText
                    });
                });
            });
            return result;
        }
        function getLineIndent(offset) {
            var start = offset;
            while (start > 0) {
                var ch = text.charAt(start - 1);
                if ("\n\r".indexOf(ch) >= 0) {
                    return text.substring(start, offset);
                }
                if (!isWhiteSpace(ch)) {
                    return null;
                }
                start--;
            }
            return text.substring(0, offset);
        }
        function collectCloseTagSuggestions(afterOpenBracket, inOpenTag, tagNameEnd) {
            if (tagNameEnd === void 0) { tagNameEnd = offset; }
            var range = getReplaceRange(afterOpenBracket, tagNameEnd);
            var closeTag = isFollowedBy(text, tagNameEnd, ScannerState.WithinEndTag, TokenType.EndTagClose) ? '' : '>';
            var curr = node;
            if (inOpenTag) {
                curr = curr.parent; // don't suggest the own tag, it's not yet open
            }
            while (curr) {
                var tag = curr.tag;
                if (tag && (!curr.closed || curr.endTagStart && (curr.endTagStart > offset))) {
                    var item = {
                        label: '/' + tag,
                        kind: CompletionItemKind.Property,
                        filterText: '/' + tag + closeTag,
                        textEdit: TextEdit.replace(range, '/' + tag + closeTag),
                        insertTextFormat: InsertTextFormat.PlainText
                    };
                    var startIndent = getLineIndent(curr.start);
                    var endIndent = getLineIndent(afterOpenBracket - 1);
                    if (startIndent !== null && endIndent !== null && startIndent !== endIndent) {
                        var insertText = startIndent + '</' + tag + closeTag;
                        item.textEdit = TextEdit.replace(getReplaceRange(afterOpenBracket - 1 - endIndent.length), insertText);
                        item.filterText = endIndent + '</' + tag + closeTag;
                    }
                    result.items.push(item);
                    return result;
                }
                curr = curr.parent;
            }
            if (inOpenTag) {
                return result;
            }
            dataProviders.forEach(function (provider) {
                provider.provideTags().forEach(function (tag) {
                    result.items.push({
                        label: '/' + tag.name,
                        kind: CompletionItemKind.Property,
                        documentation: tag.description,
                        filterText: '/' + tag + closeTag,
                        textEdit: TextEdit.replace(range, '/' + tag + closeTag),
                        insertTextFormat: InsertTextFormat.PlainText
                    });
                });
            });
            return result;
        }
        function collectAutoCloseTagSuggestion(tagCloseEnd, tag) {
            if (settings && settings.hideAutoCompleteProposals) {
                return result;
            }
            if (!isVoidElement(tag)) {
                var pos = document.positionAt(tagCloseEnd);
                result.items.push({
                    label: '</' + tag + '>',
                    kind: CompletionItemKind.Property,
                    filterText: '</' + tag + '>',
                    textEdit: TextEdit.insert(pos, '$0</' + tag + '>'),
                    insertTextFormat: InsertTextFormat.Snippet
                });
            }
            return result;
        }
        function collectTagSuggestions(tagStart, tagEnd) {
            collectOpenTagSuggestions(tagStart, tagEnd);
            collectCloseTagSuggestions(tagStart, true, tagEnd);
            return result;
        }
        function collectAttributeNameSuggestions(nameStart, nameEnd) {
            if (nameEnd === void 0) { nameEnd = offset; }
            var replaceEnd = offset;
            while (replaceEnd < nameEnd && text[replaceEnd] !== '<') { // < is a valid attribute name character, but we rather assume the attribute name ends. See #23236.
                replaceEnd++;
            }
            var range = getReplaceRange(nameStart, replaceEnd);
            var value = isFollowedBy(text, nameEnd, ScannerState.AfterAttributeName, TokenType.DelimiterAssign) ? '' : '="$1"';
            var tag = currentTag.toLowerCase();
            var seenAttributes = Object.create(null);
            dataProviders.forEach(function (provider) {
                provider.provideAttributes(tag).forEach(function (attr) {
                    if (seenAttributes[attr.name]) {
                        return;
                    }
                    seenAttributes[attr.name] = true;
                    var codeSnippet = attr.name;
                    var command;
                    if (attr.valueSet !== 'v' && value.length) {
                        codeSnippet = codeSnippet + value;
                        if (attr.valueSet) {
                            command = {
                                title: 'Suggest',
                                command: 'editor.action.triggerSuggest'
                            };
                        }
                    }
                    result.items.push({
                        label: attr.name,
                        kind: attr.valueSet === 'handler' ? CompletionItemKind.Function : CompletionItemKind.Value,
                        documentation: attr.description,
                        textEdit: TextEdit.replace(range, codeSnippet),
                        insertTextFormat: InsertTextFormat.Snippet,
                        command: command
                    });
                });
            });
            collectDataAttributesSuggestions(range, seenAttributes);
            return result;
        }
        function collectDataAttributesSuggestions(range, seenAttributes) {
            var dataAttr = 'data-';
            var dataAttributes = {};
            dataAttributes[dataAttr] = dataAttr + "$1=\"$2\"";
            function addNodeDataAttributes(node) {
                node.attributeNames.forEach(function (attr) {
                    if (startsWith(attr, dataAttr) && !dataAttributes[attr] && !seenAttributes[attr]) {
                        dataAttributes[attr] = attr + '="$1"';
                    }
                });
                node.children.forEach(function (child) { return addNodeDataAttributes(child); });
            }
            if (htmlDocument) {
                htmlDocument.roots.forEach(function (root) { return addNodeDataAttributes(root); });
            }
            Object.keys(dataAttributes).forEach(function (attr) { return result.items.push({
                label: attr,
                kind: CompletionItemKind.Value,
                textEdit: TextEdit.replace(range, dataAttributes[attr]),
                insertTextFormat: InsertTextFormat.Snippet
            }); });
        }
        function collectAttributeValueSuggestions(valueStart, valueEnd) {
            if (valueEnd === void 0) { valueEnd = offset; }
            var range;
            var addQuotes;
            var valuePrefix;
            if (offset > valueStart && offset <= valueEnd && isQuote(text[valueStart])) {
                // inside quoted attribute
                var valueContentStart = valueStart + 1;
                var valueContentEnd = valueEnd;
                // valueEnd points to the char after quote, which encloses the replace range
                if (valueEnd > valueStart && text[valueEnd - 1] === text[valueStart]) {
                    valueContentEnd--;
                }
                var wsBefore = getWordStart(text, offset, valueContentStart);
                var wsAfter = getWordEnd(text, offset, valueContentEnd);
                range = getReplaceRange(wsBefore, wsAfter);
                valuePrefix = offset >= valueContentStart && offset <= valueContentEnd ? text.substring(valueContentStart, offset) : '';
                addQuotes = false;
            }
            else {
                range = getReplaceRange(valueStart, valueEnd);
                valuePrefix = text.substring(valueStart, offset);
                addQuotes = true;
            }
            var tag = currentTag.toLowerCase();
            var attribute = currentAttributeName.toLowerCase();
            if (completionParticipants.length > 0) {
                var fullRange = getReplaceRange(valueStart, valueEnd);
                for (var _i = 0, completionParticipants_1 = completionParticipants; _i < completionParticipants_1.length; _i++) {
                    var participant = completionParticipants_1[_i];
                    if (participant.onHtmlAttributeValue) {
                        participant.onHtmlAttributeValue({ document: document, position: position, tag: tag, attribute: attribute, value: valuePrefix, range: fullRange });
                    }
                }
            }
            dataProviders.forEach(function (provider) {
                provider.provideValues(tag, attribute).forEach(function (value) {
                    var insertText = addQuotes ? '"' + value.name + '"' : value.name;
                    result.items.push({
                        label: value.name,
                        filterText: insertText,
                        kind: CompletionItemKind.Unit,
                        textEdit: TextEdit.replace(range, insertText),
                        insertTextFormat: InsertTextFormat.PlainText
                    });
                });
            });
            collectCharacterEntityProposals();
            return result;
        }
        function scanNextForEndPos(nextToken) {
            if (offset === scanner.getTokenEnd()) {
                token = scanner.scan();
                if (token === nextToken && scanner.getTokenOffset() === offset) {
                    return scanner.getTokenEnd();
                }
            }
            return offset;
        }
        function collectInsideContent() {
            for (var _i = 0, completionParticipants_2 = completionParticipants; _i < completionParticipants_2.length; _i++) {
                var participant = completionParticipants_2[_i];
                if (participant.onHtmlContent) {
                    participant.onHtmlContent({ document: document, position: position });
                }
            }
            return collectCharacterEntityProposals();
        }
        function collectCharacterEntityProposals() {
            // character entities
            var k = offset - 1;
            var characterStart = position.character;
            while (k >= 0 && isLetterOrDigit(text, k)) {
                k--;
                characterStart--;
            }
            if (k >= 0 && text[k] === '&') {
                var range = Range.create(Position.create(position.line, characterStart - 1), position);
                for (var entity in entities) {
                    if (endsWith(entity, ';')) {
                        var label = '&' + entity;
                        result.items.push({
                            label: label,
                            kind: CompletionItemKind.Keyword,
                            documentation: localize('entity.propose', "Character entity representing '" + entities[entity] + "'"),
                            textEdit: TextEdit.replace(range, label),
                            insertTextFormat: InsertTextFormat.PlainText
                        });
                    }
                }
            }
            return result;
        }
        function suggestDoctype(replaceStart, replaceEnd) {
            var range = getReplaceRange(replaceStart, replaceEnd);
            result.items.push({
                label: '!DOCTYPE',
                kind: CompletionItemKind.Property,
                documentation: 'A preamble for an HTML document.',
                textEdit: TextEdit.replace(range, '!DOCTYPE html>'),
                insertTextFormat: InsertTextFormat.PlainText
            });
        }
        var token = scanner.scan();
        while (token !== TokenType.EOS && scanner.getTokenOffset() <= offset) {
            switch (token) {
                case TokenType.StartTagOpen:
                    if (scanner.getTokenEnd() === offset) {
                        var endPos = scanNextForEndPos(TokenType.StartTag);
                        if (position.line === 0) {
                            suggestDoctype(offset, endPos);
                        }
                        return collectTagSuggestions(offset, endPos);
                    }
                    break;
                case TokenType.StartTag:
                    if (scanner.getTokenOffset() <= offset && offset <= scanner.getTokenEnd()) {
                        return collectOpenTagSuggestions(scanner.getTokenOffset(), scanner.getTokenEnd());
                    }
                    currentTag = scanner.getTokenText();
                    break;
                case TokenType.AttributeName:
                    if (scanner.getTokenOffset() <= offset && offset <= scanner.getTokenEnd()) {
                        return collectAttributeNameSuggestions(scanner.getTokenOffset(), scanner.getTokenEnd());
                    }
                    currentAttributeName = scanner.getTokenText();
                    break;
                case TokenType.DelimiterAssign:
                    if (scanner.getTokenEnd() === offset) {
                        var endPos = scanNextForEndPos(TokenType.AttributeValue);
                        return collectAttributeValueSuggestions(offset, endPos);
                    }
                    break;
                case TokenType.AttributeValue:
                    if (scanner.getTokenOffset() <= offset && offset <= scanner.getTokenEnd()) {
                        return collectAttributeValueSuggestions(scanner.getTokenOffset(), scanner.getTokenEnd());
                    }
                    break;
                case TokenType.Whitespace:
                    if (offset <= scanner.getTokenEnd()) {
                        switch (scanner.getScannerState()) {
                            case ScannerState.AfterOpeningStartTag:
                                var startPos = scanner.getTokenOffset();
                                var endTagPos = scanNextForEndPos(TokenType.StartTag);
                                return collectTagSuggestions(startPos, endTagPos);
                            case ScannerState.WithinTag:
                            case ScannerState.AfterAttributeName:
                                return collectAttributeNameSuggestions(scanner.getTokenEnd());
                            case ScannerState.BeforeAttributeValue:
                                return collectAttributeValueSuggestions(scanner.getTokenEnd());
                            case ScannerState.AfterOpeningEndTag:
                                return collectCloseTagSuggestions(scanner.getTokenOffset() - 1, false);
                            case ScannerState.WithinContent:
                                return collectInsideContent();
                        }
                    }
                    break;
                case TokenType.EndTagOpen:
                    if (offset <= scanner.getTokenEnd()) {
                        var afterOpenBracket = scanner.getTokenOffset() + 1;
                        var endOffset = scanNextForEndPos(TokenType.EndTag);
                        return collectCloseTagSuggestions(afterOpenBracket, false, endOffset);
                    }
                    break;
                case TokenType.EndTag:
                    if (offset <= scanner.getTokenEnd()) {
                        var start = scanner.getTokenOffset() - 1;
                        while (start >= 0) {
                            var ch = text.charAt(start);
                            if (ch === '/') {
                                return collectCloseTagSuggestions(start, false, scanner.getTokenEnd());
                            }
                            else if (!isWhiteSpace(ch)) {
                                break;
                            }
                            start--;
                        }
                    }
                    break;
                case TokenType.StartTagClose:
                    if (offset <= scanner.getTokenEnd()) {
                        if (currentTag) {
                            return collectAutoCloseTagSuggestion(scanner.getTokenEnd(), currentTag);
                        }
                    }
                    break;
                case TokenType.Content:
                    if (offset <= scanner.getTokenEnd()) {
                        return collectInsideContent();
                    }
                    break;
                default:
                    if (offset <= scanner.getTokenEnd()) {
                        return result;
                    }
                    break;
            }
            token = scanner.scan();
        }
        return result;
    };
    HTMLCompletion.prototype.doTagComplete = function (document, position, htmlDocument) {
        var offset = document.offsetAt(position);
        if (offset <= 0) {
            return null;
        }
        var char = document.getText().charAt(offset - 1);
        if (char === '>') {
            var node = htmlDocument.findNodeBefore(offset);
            if (node && node.tag && !isVoidElement(node.tag) && node.start < offset && (!node.endTagStart || node.endTagStart > offset)) {
                var scanner = createScanner(document.getText(), node.start);
                var token = scanner.scan();
                while (token !== TokenType.EOS && scanner.getTokenEnd() <= offset) {
                    if (token === TokenType.StartTagClose && scanner.getTokenEnd() === offset) {
                        return "$0</" + node.tag + ">";
                    }
                    token = scanner.scan();
                }
            }
        }
        else if (char === '/') {
            var node = htmlDocument.findNodeBefore(offset);
            while (node && node.closed) {
                node = node.parent;
            }
            if (node && node.tag) {
                var scanner = createScanner(document.getText(), node.start);
                var token = scanner.scan();
                while (token !== TokenType.EOS && scanner.getTokenEnd() <= offset) {
                    if (token === TokenType.EndTagOpen && scanner.getTokenEnd() === offset) {
                        return node.tag + ">";
                    }
                    token = scanner.scan();
                }
            }
        }
        return null;
    };
    return HTMLCompletion;
}());
export { HTMLCompletion };
function isQuote(s) {
    return /^["']*$/.test(s);
}
function isWhiteSpace(s) {
    return /^\s*$/.test(s);
}
function isFollowedBy(s, offset, intialState, expectedToken) {
    var scanner = createScanner(s, offset, intialState);
    var token = scanner.scan();
    while (token === TokenType.Whitespace) {
        token = scanner.scan();
    }
    return token === expectedToken;
}
function getWordStart(s, offset, limit) {
    while (offset > limit && !isWhiteSpace(s[offset - 1])) {
        offset--;
    }
    return offset;
}
function getWordEnd(s, offset, limit) {
    while (offset < limit && !isWhiteSpace(s[offset])) {
        offset++;
    }
    return offset;
}
//# sourceMappingURL=htmlCompletion.js.map