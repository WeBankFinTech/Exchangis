/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
import { createScanner } from '../parser/htmlScanner.js';
import { Range } from '../../vscode-languageserver-types/main.js';
import * as strings from '../utils/strings.js';
import Uri from '../../vscode-uri/index.js';
import { TokenType } from '../htmlLanguageTypes.js';
function normalizeRef(url, languageId) {
    var first = url[0];
    var last = url[url.length - 1];
    if (first === last && (first === '\'' || first === '\"')) {
        url = url.substr(1, url.length - 2);
    }
    return url;
}
function validateRef(url, languageId) {
    if (!url.length) {
        return false;
    }
    if (languageId === 'handlebars' && /{{.*}}/.test(url)) {
        return false;
    }
    try {
        return !!Uri.parse(url);
    }
    catch (e) {
        return false;
    }
}
function getWorkspaceUrl(documentUri, tokenContent, documentContext, base) {
    if (/^\s*javascript\:/i.test(tokenContent) || /^\s*\#/i.test(tokenContent) || /[\n\r]/.test(tokenContent)) {
        return null;
    }
    tokenContent = tokenContent.replace(/^\s*/g, '');
    if (/^https?:\/\//i.test(tokenContent) || /^file:\/\//i.test(tokenContent)) {
        // Absolute link that needs no treatment
        return tokenContent;
    }
    if (/^\/\//i.test(tokenContent)) {
        // Absolute link (that does not name the protocol)
        var pickedScheme = strings.startsWith(documentUri, 'https://') ? 'https' : 'http';
        return pickedScheme + ':' + tokenContent.replace(/^\s*/g, '');
    }
    if (documentContext) {
        return documentContext.resolveReference(tokenContent, base || documentUri);
    }
    return tokenContent;
}
function createLink(document, documentContext, attributeValue, startOffset, endOffset, base) {
    var tokenContent = normalizeRef(attributeValue, document.languageId);
    if (!validateRef(tokenContent, document.languageId)) {
        return null;
    }
    if (tokenContent.length < attributeValue.length) {
        startOffset++;
        endOffset--;
    }
    var workspaceUrl = getWorkspaceUrl(document.uri, tokenContent, documentContext, base);
    if (!workspaceUrl || !isValidURI(workspaceUrl)) {
        return null;
    }
    return {
        range: Range.create(document.positionAt(startOffset), document.positionAt(endOffset)),
        target: workspaceUrl
    };
}
function isValidURI(uri) {
    try {
        Uri.parse(uri);
        return true;
    }
    catch (e) {
        return false;
    }
}
export function findDocumentLinks(document, documentContext) {
    var newLinks = [];
    var rootAbsoluteUrl = null;
    var scanner = createScanner(document.getText(), 0);
    var token = scanner.scan();
    var afterHrefOrSrc = false;
    var afterBase = false;
    var base = void 0;
    while (token !== TokenType.EOS) {
        switch (token) {
            case TokenType.StartTag:
                if (!base) {
                    var tagName = scanner.getTokenText().toLowerCase();
                    afterBase = tagName === 'base';
                }
                break;
            case TokenType.AttributeName:
                var attributeName = scanner.getTokenText().toLowerCase();
                afterHrefOrSrc = attributeName === 'src' || attributeName === 'href';
                break;
            case TokenType.AttributeValue:
                if (afterHrefOrSrc) {
                    var attributeValue = scanner.getTokenText();
                    if (!afterBase) { // don't highlight the base link itself
                        var link = createLink(document, documentContext, attributeValue, scanner.getTokenOffset(), scanner.getTokenEnd(), base);
                        if (link) {
                            newLinks.push(link);
                        }
                    }
                    if (afterBase && typeof base === 'undefined') {
                        base = normalizeRef(attributeValue, document.languageId);
                        if (base && documentContext) {
                            base = documentContext.resolveReference(base, document.uri);
                        }
                    }
                    afterBase = false;
                    afterHrefOrSrc = false;
                }
                break;
        }
        token = scanner.scan();
    }
    return newLinks;
}
//# sourceMappingURL=htmlLinks.js.map