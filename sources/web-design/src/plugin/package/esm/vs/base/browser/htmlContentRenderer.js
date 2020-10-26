/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
import * as DOM from './dom.js';
import { defaultGenerator } from '../common/idGenerator.js';
import { escape } from '../common/strings.js';
import { removeMarkdownEscapes } from '../common/htmlContent.js';
import * as marked from '../common/marked/marked.js';
import { onUnexpectedError } from '../common/errors.js';
import { URI } from '../common/uri.js';
import { parse } from '../common/marshalling.js';
import { cloneAndChange } from '../common/objects.js';
function createElement(options) {
    var tagName = options.inline ? 'span' : 'div';
    var element = document.createElement(tagName);
    if (options.className) {
        element.className = options.className;
    }
    return element;
}
export function renderText(text, options) {
    if (options === void 0) { options = {}; }
    var element = createElement(options);
    element.textContent = text;
    return element;
}
export function renderFormattedText(formattedText, options) {
    if (options === void 0) { options = {}; }
    var element = createElement(options);
    _renderFormattedText(element, parseFormattedText(formattedText), options.actionHandler);
    return element;
}
/**
 * Create html nodes for the given content element.
 */
export function renderMarkdown(markdown, options) {
    if (options === void 0) { options = {}; }
    var element = createElement(options);
    var _uriMassage = function (part) {
        var data;
        try {
            data = parse(decodeURIComponent(part));
        }
        catch (e) {
            // ignore
        }
        if (!data) {
            return part;
        }
        data = cloneAndChange(data, function (value) {
            if (markdown.uris && markdown.uris[value]) {
                return URI.revive(markdown.uris[value]);
            }
            else {
                return undefined;
            }
        });
        return encodeURIComponent(JSON.stringify(data));
    };
    var _href = function (href) {
        var data = markdown.uris && markdown.uris[href];
        if (!data) {
            return href;
        }
        var uri = URI.revive(data);
        if (uri.query) {
            uri = uri.with({ query: _uriMassage(uri.query) });
        }
        if (data) {
            href = uri.toString(true);
        }
        return href;
    };
    // signal to code-block render that the
    // element has been created
    var signalInnerHTML;
    var withInnerHTML = new Promise(function (c) { return signalInnerHTML = c; });
    var renderer = new marked.Renderer();
    renderer.image = function (href, title, text) {
        href = _href(href);
        var dimensions = [];
        if (href) {
            var splitted = href.split('|').map(function (s) { return s.trim(); });
            href = splitted[0];
            var parameters = splitted[1];
            if (parameters) {
                var heightFromParams = /height=(\d+)/.exec(parameters);
                var widthFromParams = /width=(\d+)/.exec(parameters);
                var height = heightFromParams ? heightFromParams[1] : '';
                var width = widthFromParams ? widthFromParams[1] : '';
                var widthIsFinite = isFinite(parseInt(width));
                var heightIsFinite = isFinite(parseInt(height));
                if (widthIsFinite) {
                    dimensions.push("width=\"" + width + "\"");
                }
                if (heightIsFinite) {
                    dimensions.push("height=\"" + height + "\"");
                }
            }
        }
        var attributes = [];
        if (href) {
            attributes.push("src=\"" + href + "\"");
        }
        if (text) {
            attributes.push("alt=\"" + text + "\"");
        }
        if (title) {
            attributes.push("title=\"" + title + "\"");
        }
        if (dimensions.length) {
            attributes = attributes.concat(dimensions);
        }
        return '<img ' + attributes.join(' ') + '>';
    };
    renderer.link = function (href, title, text) {
        // Remove markdown escapes. Workaround for https://github.com/chjj/marked/issues/829
        if (href === text) { // raw link case
            text = removeMarkdownEscapes(text);
        }
        href = _href(href);
        title = removeMarkdownEscapes(title);
        href = removeMarkdownEscapes(href);
        if (!href
            || href.match(/^data:|javascript:/i)
            || (href.match(/^command:/i) && !markdown.isTrusted)
            || href.match(/^command:(\/\/\/)?_workbench\.downloadResource/i)) {
            // drop the link
            return text;
        }
        else {
            // HTML Encode href
            href = href.replace(/&/g, '&amp;')
                .replace(/</g, '&lt;')
                .replace(/>/g, '&gt;')
                .replace(/"/g, '&quot;')
                .replace(/'/g, '&#39;');
            return "<a href=\"#\" data-href=\"" + href + "\" title=\"" + (title || href) + "\">" + text + "</a>";
        }
    };
    renderer.paragraph = function (text) {
        return "<p>" + text + "</p>";
    };
    if (options.codeBlockRenderer) {
        renderer.code = function (code, lang) {
            var value = options.codeBlockRenderer(lang, code);
            // when code-block rendering is async we return sync
            // but update the node with the real result later.
            var id = defaultGenerator.nextId();
            var promise = Promise.all([value, withInnerHTML]).then(function (values) {
                var strValue = values[0];
                var span = element.querySelector("div[data-code=\"" + id + "\"]");
                if (span) {
                    span.innerHTML = strValue;
                }
            }).catch(function (err) {
                // ignore
            });
            if (options.codeBlockRenderCallback) {
                promise.then(options.codeBlockRenderCallback);
            }
            return "<div class=\"code\" data-code=\"" + id + "\">" + escape(code) + "</div>";
        };
    }
    if (options.actionHandler) {
        options.actionHandler.disposeables.push(DOM.addStandardDisposableListener(element, 'click', function (event) {
            var target = event.target;
            if (target.tagName !== 'A') {
                target = target.parentElement;
                if (!target || target.tagName !== 'A') {
                    return;
                }
            }
            try {
                var href = target.dataset['href'];
                if (href) {
                    options.actionHandler.callback(href, event);
                }
            }
            catch (err) {
                onUnexpectedError(err);
            }
            finally {
                event.preventDefault();
            }
        }));
    }
    var markedOptions = {
        sanitize: true,
        renderer: renderer
    };
    element.innerHTML = marked.parse(markdown.value, markedOptions);
    signalInnerHTML();
    return element;
}
// --- formatted string parsing
var StringStream = /** @class */ (function () {
    function StringStream(source) {
        this.source = source;
        this.index = 0;
    }
    StringStream.prototype.eos = function () {
        return this.index >= this.source.length;
    };
    StringStream.prototype.next = function () {
        var next = this.peek();
        this.advance();
        return next;
    };
    StringStream.prototype.peek = function () {
        return this.source[this.index];
    };
    StringStream.prototype.advance = function () {
        this.index++;
    };
    return StringStream;
}());
function _renderFormattedText(element, treeNode, actionHandler) {
    var child;
    if (treeNode.type === 2 /* Text */) {
        child = document.createTextNode(treeNode.content || '');
    }
    else if (treeNode.type === 3 /* Bold */) {
        child = document.createElement('b');
    }
    else if (treeNode.type === 4 /* Italics */) {
        child = document.createElement('i');
    }
    else if (treeNode.type === 5 /* Action */ && actionHandler) {
        var a = document.createElement('a');
        a.href = '#';
        actionHandler.disposeables.push(DOM.addStandardDisposableListener(a, 'click', function (event) {
            actionHandler.callback(String(treeNode.index), event);
        }));
        child = a;
    }
    else if (treeNode.type === 7 /* NewLine */) {
        child = document.createElement('br');
    }
    else if (treeNode.type === 1 /* Root */) {
        child = element;
    }
    if (child && element !== child) {
        element.appendChild(child);
    }
    if (child && Array.isArray(treeNode.children)) {
        treeNode.children.forEach(function (nodeChild) {
            _renderFormattedText(child, nodeChild, actionHandler);
        });
    }
}
function parseFormattedText(content) {
    var root = {
        type: 1 /* Root */,
        children: []
    };
    var actionItemIndex = 0;
    var current = root;
    var stack = [];
    var stream = new StringStream(content);
    while (!stream.eos()) {
        var next = stream.next();
        var isEscapedFormatType = (next === '\\' && formatTagType(stream.peek()) !== 0 /* Invalid */);
        if (isEscapedFormatType) {
            next = stream.next(); // unread the backslash if it escapes a format tag type
        }
        if (!isEscapedFormatType && isFormatTag(next) && next === stream.peek()) {
            stream.advance();
            if (current.type === 2 /* Text */) {
                current = stack.pop();
            }
            var type = formatTagType(next);
            if (current.type === type || (current.type === 5 /* Action */ && type === 6 /* ActionClose */)) {
                current = stack.pop();
            }
            else {
                var newCurrent = {
                    type: type,
                    children: []
                };
                if (type === 5 /* Action */) {
                    newCurrent.index = actionItemIndex;
                    actionItemIndex++;
                }
                current.children.push(newCurrent);
                stack.push(current);
                current = newCurrent;
            }
        }
        else if (next === '\n') {
            if (current.type === 2 /* Text */) {
                current = stack.pop();
            }
            current.children.push({
                type: 7 /* NewLine */
            });
        }
        else {
            if (current.type !== 2 /* Text */) {
                var textCurrent = {
                    type: 2 /* Text */,
                    content: next
                };
                current.children.push(textCurrent);
                stack.push(current);
                current = textCurrent;
            }
            else {
                current.content += next;
            }
        }
    }
    if (current.type === 2 /* Text */) {
        current = stack.pop();
    }
    if (stack.length) {
        // incorrectly formatted string literal
    }
    return root;
}
function isFormatTag(char) {
    return formatTagType(char) !== 0 /* Invalid */;
}
function formatTagType(char) {
    switch (char) {
        case '*':
            return 3 /* Bold */;
        case '_':
            return 4 /* Italics */;
        case '[':
            return 5 /* Action */;
        case ']':
            return 6 /* ActionClose */;
        default:
            return 0 /* Invalid */;
    }
}
