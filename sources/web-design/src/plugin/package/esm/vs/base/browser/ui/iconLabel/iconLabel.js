/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
import './iconlabel.css';
import * as dom from '../../dom.js';
import { HighlightedLabel } from '../highlightedlabel/highlightedLabel.js';
import { Disposable } from '../../../common/lifecycle.js';
var FastLabelNode = /** @class */ (function () {
    function FastLabelNode(_element) {
        this._element = _element;
    }
    Object.defineProperty(FastLabelNode.prototype, "element", {
        get: function () {
            return this._element;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(FastLabelNode.prototype, "textContent", {
        set: function (content) {
            if (this.disposed || content === this._textContent) {
                return;
            }
            this._textContent = content;
            this._element.textContent = content;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(FastLabelNode.prototype, "className", {
        set: function (className) {
            if (this.disposed || className === this._className) {
                return;
            }
            this._className = className;
            this._element.className = className;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(FastLabelNode.prototype, "title", {
        set: function (title) {
            if (this.disposed || title === this._title) {
                return;
            }
            this._title = title;
            if (this._title) {
                this._element.title = title;
            }
            else {
                this._element.removeAttribute('title');
            }
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(FastLabelNode.prototype, "empty", {
        set: function (empty) {
            if (this.disposed || empty === this._empty) {
                return;
            }
            this._empty = empty;
            this._element.style.marginLeft = empty ? '0' : null;
        },
        enumerable: true,
        configurable: true
    });
    FastLabelNode.prototype.dispose = function () {
        this.disposed = true;
    };
    return FastLabelNode;
}());
var IconLabel = /** @class */ (function (_super) {
    __extends(IconLabel, _super);
    function IconLabel(container, options) {
        var _this = _super.call(this) || this;
        _this.domNode = _this._register(new FastLabelNode(dom.append(container, dom.$('.monaco-icon-label'))));
        _this.labelDescriptionContainer = _this._register(new FastLabelNode(dom.append(_this.domNode.element, dom.$('.monaco-icon-label-description-container'))));
        if (options && options.supportHighlights) {
            _this.labelNode = new HighlightedLabel(dom.append(_this.labelDescriptionContainer.element, dom.$('a.label-name')), !options.donotSupportOcticons);
        }
        else {
            _this.labelNode = _this._register(new FastLabelNode(dom.append(_this.labelDescriptionContainer.element, dom.$('a.label-name'))));
        }
        if (options && options.supportDescriptionHighlights) {
            _this.descriptionNodeFactory = function () { return new HighlightedLabel(dom.append(_this.labelDescriptionContainer.element, dom.$('span.label-description')), !options.donotSupportOcticons); };
        }
        else {
            _this.descriptionNodeFactory = function () { return _this._register(new FastLabelNode(dom.append(_this.labelDescriptionContainer.element, dom.$('span.label-description')))); };
        }
        return _this;
    }
    IconLabel.prototype.setLabel = function (label, description, options) {
        var classes = ['monaco-icon-label'];
        if (options) {
            if (options.extraClasses) {
                classes.push.apply(classes, options.extraClasses);
            }
            if (options.italic) {
                classes.push('italic');
            }
        }
        this.domNode.className = classes.join(' ');
        this.domNode.title = options && options.title ? options.title : '';
        if (this.labelNode instanceof HighlightedLabel) {
            this.labelNode.set(label || '', options ? options.matches : undefined, options && options.title ? options.title : undefined, options && options.labelEscapeNewLines);
        }
        else {
            this.labelNode.textContent = label || '';
        }
        if (description || this.descriptionNode) {
            if (!this.descriptionNode) {
                this.descriptionNode = this.descriptionNodeFactory(); // description node is created lazily on demand
            }
            if (this.descriptionNode instanceof HighlightedLabel) {
                this.descriptionNode.set(description || '', options ? options.descriptionMatches : undefined);
                if (options && options.descriptionTitle) {
                    this.descriptionNode.element.title = options.descriptionTitle;
                }
                else {
                    this.descriptionNode.element.removeAttribute('title');
                }
            }
            else {
                this.descriptionNode.textContent = description || '';
                this.descriptionNode.title = options && options.descriptionTitle ? options.descriptionTitle : '';
                this.descriptionNode.empty = !description;
            }
        }
    };
    return IconLabel;
}(Disposable));
export { IconLabel };
