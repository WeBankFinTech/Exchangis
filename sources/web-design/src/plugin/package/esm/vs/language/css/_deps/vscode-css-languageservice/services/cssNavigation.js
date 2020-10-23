/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
'use strict';
import { DocumentHighlightKind, Location, Range, SymbolKind, TextEdit } from '../../vscode-languageserver-types/main.js';
import * as nls from '../../../fillers/vscode-nls.js';
import * as nodes from '../parser/cssNodes.js';
import { Symbols } from '../parser/cssSymbolScope.js';
import { getColorValue, hslFromColor } from '../languageFacts/facts.js';
import { endsWith, startsWith } from '../utils/strings.js';
var localize = nls.loadMessageBundle();
var CSSNavigation = /** @class */ (function () {
    function CSSNavigation() {
    }
    CSSNavigation.prototype.findDefinition = function (document, position, stylesheet) {
        var symbols = new Symbols(stylesheet);
        var offset = document.offsetAt(position);
        var node = nodes.getNodeAtOffset(stylesheet, offset);
        if (!node) {
            return null;
        }
        var symbol = symbols.findSymbolFromNode(node);
        if (!symbol) {
            return null;
        }
        return {
            uri: document.uri,
            range: getRange(symbol.node, document)
        };
    };
    CSSNavigation.prototype.findReferences = function (document, position, stylesheet) {
        var highlights = this.findDocumentHighlights(document, position, stylesheet);
        return highlights.map(function (h) {
            return {
                uri: document.uri,
                range: h.range
            };
        });
    };
    CSSNavigation.prototype.findDocumentHighlights = function (document, position, stylesheet) {
        var result = [];
        var offset = document.offsetAt(position);
        var node = nodes.getNodeAtOffset(stylesheet, offset);
        if (!node || node.type === nodes.NodeType.Stylesheet || node.type === nodes.NodeType.Declarations) {
            return result;
        }
        if (node.type === nodes.NodeType.Identifier && node.parent && node.parent.type === nodes.NodeType.ClassSelector) {
            node = node.parent;
        }
        var symbols = new Symbols(stylesheet);
        var symbol = symbols.findSymbolFromNode(node);
        var name = node.getText();
        stylesheet.accept(function (candidate) {
            if (symbol) {
                if (symbols.matchesSymbol(candidate, symbol)) {
                    result.push({
                        kind: getHighlightKind(candidate),
                        range: getRange(candidate, document)
                    });
                    return false;
                }
            }
            else if (node.type === candidate.type && node.length === candidate.length && name === candidate.getText()) {
                // Same node type and data
                result.push({
                    kind: getHighlightKind(candidate),
                    range: getRange(candidate, document)
                });
            }
            return true;
        });
        return result;
    };
    CSSNavigation.prototype.findDocumentLinks = function (document, stylesheet, documentContext) {
        var result = [];
        stylesheet.accept(function (candidate) {
            if (candidate.type === nodes.NodeType.URILiteral) {
                var link = uriLiteralNodeToDocumentLink(document, candidate, documentContext);
                if (link) {
                    result.push(link);
                }
                return false;
            }
            /**
             * In @import, it is possible to include links that do not use `url()`
             * For example, `@import 'foo.css';`
             */
            if (candidate.parent && candidate.parent.type === nodes.NodeType.Import) {
                var rawText = candidate.getText();
                if (startsWith(rawText, "'") || startsWith(rawText, "\"")) {
                    result.push(uriStringNodeToDocumentLink(document, candidate, documentContext));
                }
                return false;
            }
            return true;
        });
        return result;
    };
    CSSNavigation.prototype.findDocumentSymbols = function (document, stylesheet) {
        var result = [];
        stylesheet.accept(function (node) {
            var entry = {
                name: null,
                kind: SymbolKind.Class,
                location: null
            };
            var locationNode = node;
            if (node instanceof nodes.Selector) {
                entry.name = node.getText();
                locationNode = node.findAParent(nodes.NodeType.Ruleset, nodes.NodeType.ExtendsReference);
                if (locationNode) {
                    entry.location = Location.create(document.uri, getRange(locationNode, document));
                    result.push(entry);
                }
                return false;
            }
            else if (node instanceof nodes.VariableDeclaration) {
                entry.name = node.getName();
                entry.kind = SymbolKind.Variable;
            }
            else if (node instanceof nodes.MixinDeclaration) {
                entry.name = node.getName();
                entry.kind = SymbolKind.Method;
            }
            else if (node instanceof nodes.FunctionDeclaration) {
                entry.name = node.getName();
                entry.kind = SymbolKind.Function;
            }
            else if (node instanceof nodes.Keyframe) {
                entry.name = localize('literal.keyframes', "@keyframes {0}", node.getName());
            }
            else if (node instanceof nodes.FontFace) {
                entry.name = localize('literal.fontface', "@font-face");
            }
            if (entry.name) {
                entry.location = Location.create(document.uri, getRange(locationNode, document));
                result.push(entry);
            }
            return true;
        });
        return result;
    };
    CSSNavigation.prototype.findDocumentColors = function (document, stylesheet) {
        var result = [];
        stylesheet.accept(function (node) {
            var colorInfo = getColorInformation(node, document);
            if (colorInfo) {
                result.push(colorInfo);
            }
            return true;
        });
        return result;
    };
    CSSNavigation.prototype.getColorPresentations = function (document, stylesheet, color, range) {
        var result = [];
        var red256 = Math.round(color.red * 255), green256 = Math.round(color.green * 255), blue256 = Math.round(color.blue * 255);
        var label;
        if (color.alpha === 1) {
            label = "rgb(" + red256 + ", " + green256 + ", " + blue256 + ")";
        }
        else {
            label = "rgba(" + red256 + ", " + green256 + ", " + blue256 + ", " + color.alpha + ")";
        }
        result.push({ label: label, textEdit: TextEdit.replace(range, label) });
        if (color.alpha === 1) {
            label = "#" + toTwoDigitHex(red256) + toTwoDigitHex(green256) + toTwoDigitHex(blue256);
        }
        else {
            label = "#" + toTwoDigitHex(red256) + toTwoDigitHex(green256) + toTwoDigitHex(blue256) + toTwoDigitHex(Math.round(color.alpha * 255));
        }
        result.push({ label: label, textEdit: TextEdit.replace(range, label) });
        var hsl = hslFromColor(color);
        if (hsl.a === 1) {
            label = "hsl(" + hsl.h + ", " + Math.round(hsl.s * 100) + "%, " + Math.round(hsl.l * 100) + "%)";
        }
        else {
            label = "hsla(" + hsl.h + ", " + Math.round(hsl.s * 100) + "%, " + Math.round(hsl.l * 100) + "%, " + hsl.a + ")";
        }
        result.push({ label: label, textEdit: TextEdit.replace(range, label) });
        return result;
    };
    CSSNavigation.prototype.doRename = function (document, position, newName, stylesheet) {
        var _a;
        var highlights = this.findDocumentHighlights(document, position, stylesheet);
        var edits = highlights.map(function (h) { return TextEdit.replace(h.range, newName); });
        return {
            changes: (_a = {}, _a[document.uri] = edits, _a)
        };
    };
    return CSSNavigation;
}());
export { CSSNavigation };
function getColorInformation(node, document) {
    var color = getColorValue(node);
    if (color) {
        var range = getRange(node, document);
        return { color: color, range: range };
    }
    return null;
}
function uriLiteralNodeToDocumentLink(document, uriLiteralNode, documentContext) {
    if (uriLiteralNode.getChildren().length === 0) {
        return null;
    }
    var uriStringNode = uriLiteralNode.getChild(0);
    return uriStringNodeToDocumentLink(document, uriStringNode, documentContext);
}
function uriStringNodeToDocumentLink(document, uriStringNode, documentContext) {
    var rawUri = uriStringNode.getText();
    var range = getRange(uriStringNode, document);
    // Make sure the range is not empty
    if (range.start.line === range.end.line && range.start.character === range.end.character) {
        return null;
    }
    if (startsWith(rawUri, "'") || startsWith(rawUri, "\"")) {
        rawUri = rawUri.slice(1, -1);
    }
    var target;
    if (startsWith(rawUri, 'http://') || startsWith(rawUri, 'https://')) {
        target = rawUri;
    }
    else if (/^\w+:\/\//g.test(rawUri)) {
        target = rawUri;
    }
    else {
        /**
         * In SCSS, @import 'foo' could be referring to `_foo.scss`, if none of the following is true:
         * - The file's extension is .css.
         * - The filename begins with http://.
         * - The filename is a url().
         * - The @import has any media queries.
         */
        if (document.languageId === 'scss') {
            if (!endsWith(rawUri, '.css') &&
                !startsWith(rawUri, 'http://') && !startsWith(rawUri, 'https://') &&
                !(uriStringNode.parent && uriStringNode.parent.type === nodes.NodeType.URILiteral) &&
                uriStringNode.parent.getChildren().length === 1) {
                target = toScssPartialUri(documentContext.resolveReference(rawUri, document.uri));
            }
            else {
                target = documentContext.resolveReference(rawUri, document.uri);
            }
        }
        else {
            target = documentContext.resolveReference(rawUri, document.uri);
        }
    }
    return {
        range: range,
        target: target
    };
}
function toScssPartialUri(uri) {
    return uri.replace(/\/(\w+)(.scss)?$/gm, function (match, fileName) {
        return '/_' + fileName + '.scss';
    });
}
function getRange(node, document) {
    return Range.create(document.positionAt(node.offset), document.positionAt(node.end));
}
function getHighlightKind(node) {
    if (node.type === nodes.NodeType.Selector) {
        return DocumentHighlightKind.Write;
    }
    if (node instanceof nodes.Identifier) {
        if (node.parent && node.parent instanceof nodes.Property) {
            if (node.isCustomProperty) {
                return DocumentHighlightKind.Write;
            }
        }
    }
    if (node.parent) {
        switch (node.parent.type) {
            case nodes.NodeType.FunctionDeclaration:
            case nodes.NodeType.MixinDeclaration:
            case nodes.NodeType.Keyframe:
            case nodes.NodeType.VariableDeclaration:
            case nodes.NodeType.FunctionParameter:
                return DocumentHighlightKind.Write;
        }
    }
    return DocumentHighlightKind.Read;
}
function toTwoDigitHex(n) {
    var r = n.toString(16);
    return r.length !== 2 ? '0' + r : r;
}
