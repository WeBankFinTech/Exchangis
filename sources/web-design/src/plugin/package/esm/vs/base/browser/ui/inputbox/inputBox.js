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
import './inputBox.css';
import * as nls from '../../../../nls.js';
import * as Bal from '../../browser.js';
import * as dom from '../../dom.js';
import { renderFormattedText, renderText } from '../../htmlContentRenderer.js';
import * as aria from '../aria/aria.js';
import { ActionBar } from '../actionbar/actionbar.js';
import { Emitter } from '../../../common/event.js';
import { Widget } from '../widget.js';
import { Color } from '../../../common/color.js';
import { mixin } from '../../../common/objects.js';
import { HistoryNavigator } from '../../../common/history.js';
var $ = dom.$;
var defaultOpts = {
    inputBackground: Color.fromHex('#3C3C3C'),
    inputForeground: Color.fromHex('#CCCCCC'),
    inputValidationInfoBorder: Color.fromHex('#55AAFF'),
    inputValidationInfoBackground: Color.fromHex('#063B49'),
    inputValidationWarningBorder: Color.fromHex('#B89500'),
    inputValidationWarningBackground: Color.fromHex('#352A05'),
    inputValidationErrorBorder: Color.fromHex('#BE1100'),
    inputValidationErrorBackground: Color.fromHex('#5A1D1D')
};
var InputBox = /** @class */ (function (_super) {
    __extends(InputBox, _super);
    function InputBox(container, contextViewProvider, options) {
        var _this = _super.call(this) || this;
        _this.state = 'idle';
        _this._onDidChange = _this._register(new Emitter());
        _this.onDidChange = _this._onDidChange.event;
        _this._onDidHeightChange = _this._register(new Emitter());
        _this.contextViewProvider = contextViewProvider;
        _this.options = options || Object.create(null);
        mixin(_this.options, defaultOpts, false);
        _this.message = null;
        _this.cachedHeight = null;
        _this.placeholder = _this.options.placeholder || '';
        _this.ariaLabel = _this.options.ariaLabel || '';
        _this.inputBackground = _this.options.inputBackground;
        _this.inputForeground = _this.options.inputForeground;
        _this.inputBorder = _this.options.inputBorder;
        _this.inputValidationInfoBorder = _this.options.inputValidationInfoBorder;
        _this.inputValidationInfoBackground = _this.options.inputValidationInfoBackground;
        _this.inputValidationInfoForeground = _this.options.inputValidationInfoForeground;
        _this.inputValidationWarningBorder = _this.options.inputValidationWarningBorder;
        _this.inputValidationWarningBackground = _this.options.inputValidationWarningBackground;
        _this.inputValidationWarningForeground = _this.options.inputValidationWarningForeground;
        _this.inputValidationErrorBorder = _this.options.inputValidationErrorBorder;
        _this.inputValidationErrorBackground = _this.options.inputValidationErrorBackground;
        _this.inputValidationErrorForeground = _this.options.inputValidationErrorForeground;
        if (_this.options.validationOptions) {
            _this.validation = _this.options.validationOptions.validation;
        }
        _this.element = dom.append(container, $('.monaco-inputbox.idle'));
        var tagName = _this.options.flexibleHeight ? 'textarea' : 'input';
        var wrapper = dom.append(_this.element, $('.wrapper'));
        _this.input = dom.append(wrapper, $(tagName + '.input'));
        _this.input.setAttribute('autocorrect', 'off');
        _this.input.setAttribute('autocapitalize', 'off');
        _this.input.setAttribute('spellcheck', 'false');
        _this.onfocus(_this.input, function () { return dom.addClass(_this.element, 'synthetic-focus'); });
        _this.onblur(_this.input, function () { return dom.removeClass(_this.element, 'synthetic-focus'); });
        if (_this.options.flexibleHeight) {
            _this.mirror = dom.append(wrapper, $('div.mirror'));
            _this.mirror.innerHTML = '&nbsp;';
        }
        else {
            _this.input.type = _this.options.type || 'text';
            _this.input.setAttribute('wrap', 'off');
        }
        if (_this.ariaLabel) {
            _this.input.setAttribute('aria-label', _this.ariaLabel);
        }
        if (_this.placeholder) {
            _this.setPlaceHolder(_this.placeholder);
        }
        _this.oninput(_this.input, function () { return _this.onValueChange(); });
        _this.onblur(_this.input, function () { return _this.onBlur(); });
        _this.onfocus(_this.input, function () { return _this.onFocus(); });
        // Add placeholder shim for IE because IE decides to hide the placeholder on focus (we dont want that!)
        if (_this.placeholder && Bal.isIE) {
            _this.onclick(_this.input, function (e) {
                dom.EventHelper.stop(e, true);
                _this.input.focus();
            });
        }
        setTimeout(function () {
            if (!_this.input) {
                return;
            }
            _this.updateMirror();
        }, 0);
        // Support actions
        if (_this.options.actions) {
            _this.actionbar = _this._register(new ActionBar(_this.element));
            _this.actionbar.push(_this.options.actions, { icon: true, label: false });
        }
        _this.applyStyles();
        return _this;
    }
    InputBox.prototype.onBlur = function () {
        this._hideMessage();
    };
    InputBox.prototype.onFocus = function () {
        this._showMessage();
    };
    InputBox.prototype.setPlaceHolder = function (placeHolder) {
        if (this.input) {
            this.input.setAttribute('placeholder', placeHolder);
            this.input.title = placeHolder;
        }
    };
    InputBox.prototype.setAriaLabel = function (label) {
        this.ariaLabel = label;
        if (this.input) {
            if (label) {
                this.input.setAttribute('aria-label', this.ariaLabel);
            }
            else {
                this.input.removeAttribute('aria-label');
            }
        }
    };
    Object.defineProperty(InputBox.prototype, "mirrorElement", {
        get: function () {
            return this.mirror;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(InputBox.prototype, "inputElement", {
        get: function () {
            return this.input;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(InputBox.prototype, "value", {
        get: function () {
            return this.input.value;
        },
        set: function (newValue) {
            if (this.input.value !== newValue) {
                this.input.value = newValue;
                this.onValueChange();
            }
        },
        enumerable: true,
        configurable: true
    });
    InputBox.prototype.focus = function () {
        this.input.focus();
    };
    InputBox.prototype.blur = function () {
        this.input.blur();
    };
    InputBox.prototype.hasFocus = function () {
        return document.activeElement === this.input;
    };
    InputBox.prototype.select = function (range) {
        if (range === void 0) { range = null; }
        this.input.select();
        if (range) {
            this.input.setSelectionRange(range.start, range.end);
        }
    };
    InputBox.prototype.enable = function () {
        this.input.removeAttribute('disabled');
    };
    InputBox.prototype.disable = function () {
        this.input.disabled = true;
        this._hideMessage();
    };
    InputBox.prototype.setEnabled = function (enabled) {
        if (enabled) {
            this.enable();
        }
        else {
            this.disable();
        }
    };
    Object.defineProperty(InputBox.prototype, "width", {
        get: function () {
            return dom.getTotalWidth(this.input);
        },
        set: function (width) {
            this.input.style.width = width + 'px';
            if (this.mirror) {
                this.mirror.style.width = width + 'px';
            }
        },
        enumerable: true,
        configurable: true
    });
    InputBox.prototype.showMessage = function (message, force) {
        this.message = message;
        dom.removeClass(this.element, 'idle');
        dom.removeClass(this.element, 'info');
        dom.removeClass(this.element, 'warning');
        dom.removeClass(this.element, 'error');
        dom.addClass(this.element, this.classForType(message.type));
        var styles = this.stylesForType(this.message.type);
        this.element.style.border = styles.border ? "1px solid " + styles.border : null;
        // ARIA Support
        var alertText;
        if (message.type === 3 /* ERROR */) {
            alertText = nls.localize('alertErrorMessage', "Error: {0}", message.content);
        }
        else if (message.type === 2 /* WARNING */) {
            alertText = nls.localize('alertWarningMessage', "Warning: {0}", message.content);
        }
        else {
            alertText = nls.localize('alertInfoMessage', "Info: {0}", message.content);
        }
        aria.alert(alertText);
        if (this.hasFocus() || force) {
            this._showMessage();
        }
    };
    InputBox.prototype.hideMessage = function () {
        this.message = null;
        dom.removeClass(this.element, 'info');
        dom.removeClass(this.element, 'warning');
        dom.removeClass(this.element, 'error');
        dom.addClass(this.element, 'idle');
        this._hideMessage();
        this.applyStyles();
    };
    InputBox.prototype.validate = function () {
        var errorMsg = null;
        if (this.validation) {
            errorMsg = this.validation(this.value);
            if (errorMsg) {
                this.inputElement.setAttribute('aria-invalid', 'true');
                this.showMessage(errorMsg);
            }
            else if (this.inputElement.hasAttribute('aria-invalid')) {
                this.inputElement.removeAttribute('aria-invalid');
                this.hideMessage();
            }
        }
        return !errorMsg;
    };
    InputBox.prototype.stylesForType = function (type) {
        switch (type) {
            case 1 /* INFO */: return { border: this.inputValidationInfoBorder, background: this.inputValidationInfoBackground, foreground: this.inputValidationInfoForeground };
            case 2 /* WARNING */: return { border: this.inputValidationWarningBorder, background: this.inputValidationWarningBackground, foreground: this.inputValidationWarningForeground };
            default: return { border: this.inputValidationErrorBorder, background: this.inputValidationErrorBackground, foreground: this.inputValidationErrorForeground };
        }
    };
    InputBox.prototype.classForType = function (type) {
        switch (type) {
            case 1 /* INFO */: return 'info';
            case 2 /* WARNING */: return 'warning';
            default: return 'error';
        }
    };
    InputBox.prototype._showMessage = function () {
        var _this = this;
        if (!this.contextViewProvider || !this.message) {
            return;
        }
        var div;
        var layout = function () { return div.style.width = dom.getTotalWidth(_this.element) + 'px'; };
        this.state = 'open';
        this.contextViewProvider.showContextView({
            getAnchor: function () { return _this.element; },
            anchorAlignment: 1 /* RIGHT */,
            render: function (container) {
                if (!_this.message) {
                    return null;
                }
                div = dom.append(container, $('.monaco-inputbox-container'));
                layout();
                var renderOptions = {
                    inline: true,
                    className: 'monaco-inputbox-message'
                };
                var spanElement = (_this.message.formatContent
                    ? renderFormattedText(_this.message.content, renderOptions)
                    : renderText(_this.message.content, renderOptions));
                dom.addClass(spanElement, _this.classForType(_this.message.type));
                var styles = _this.stylesForType(_this.message.type);
                spanElement.style.backgroundColor = styles.background ? styles.background.toString() : null;
                spanElement.style.color = styles.foreground ? styles.foreground.toString() : null;
                spanElement.style.border = styles.border ? "1px solid " + styles.border : null;
                dom.append(div, spanElement);
                return null;
            },
            layout: layout
        });
    };
    InputBox.prototype._hideMessage = function () {
        if (!this.contextViewProvider || this.state !== 'open') {
            return;
        }
        this.state = 'idle';
        this.contextViewProvider.hideContextView();
    };
    InputBox.prototype.onValueChange = function () {
        this._onDidChange.fire(this.value);
        this.validate();
        this.updateMirror();
        if (this.state === 'open' && this.contextViewProvider) {
            this.contextViewProvider.layout();
        }
    };
    InputBox.prototype.updateMirror = function () {
        if (!this.mirror) {
            return;
        }
        var value = this.value || this.placeholder;
        var lastCharCode = value.charCodeAt(value.length - 1);
        var suffix = lastCharCode === 10 ? ' ' : '';
        var mirrorTextContent = value + suffix;
        if (mirrorTextContent) {
            this.mirror.textContent = value + suffix;
        }
        else {
            this.mirror.innerHTML = '&nbsp;';
        }
        this.layout();
    };
    InputBox.prototype.style = function (styles) {
        this.inputBackground = styles.inputBackground;
        this.inputForeground = styles.inputForeground;
        this.inputBorder = styles.inputBorder;
        this.inputValidationInfoBackground = styles.inputValidationInfoBackground;
        this.inputValidationInfoForeground = styles.inputValidationInfoForeground;
        this.inputValidationInfoBorder = styles.inputValidationInfoBorder;
        this.inputValidationWarningBackground = styles.inputValidationWarningBackground;
        this.inputValidationWarningForeground = styles.inputValidationWarningForeground;
        this.inputValidationWarningBorder = styles.inputValidationWarningBorder;
        this.inputValidationErrorBackground = styles.inputValidationErrorBackground;
        this.inputValidationErrorForeground = styles.inputValidationErrorForeground;
        this.inputValidationErrorBorder = styles.inputValidationErrorBorder;
        this.applyStyles();
    };
    InputBox.prototype.applyStyles = function () {
        if (this.element) {
            var background = this.inputBackground ? this.inputBackground.toString() : null;
            var foreground = this.inputForeground ? this.inputForeground.toString() : null;
            var border = this.inputBorder ? this.inputBorder.toString() : null;
            this.element.style.backgroundColor = background;
            this.element.style.color = foreground;
            this.input.style.backgroundColor = background;
            this.input.style.color = foreground;
            this.element.style.borderWidth = border ? '1px' : null;
            this.element.style.borderStyle = border ? 'solid' : null;
            this.element.style.borderColor = border;
        }
    };
    InputBox.prototype.layout = function () {
        if (!this.mirror) {
            return;
        }
        var previousHeight = this.cachedHeight;
        this.cachedHeight = dom.getTotalHeight(this.mirror);
        if (previousHeight !== this.cachedHeight) {
            this.input.style.height = this.cachedHeight + 'px';
            this._onDidHeightChange.fire(this.cachedHeight);
        }
    };
    InputBox.prototype.dispose = function () {
        this._hideMessage();
        this.element = null; // StrictNullOverride: nulling out ok in dispose
        this.input = null; // StrictNullOverride: nulling out ok in dispose
        this.contextViewProvider = undefined;
        this.message = null;
        this.validation = undefined;
        this.state = null;
        this.actionbar = undefined;
        _super.prototype.dispose.call(this);
    };
    return InputBox;
}(Widget));
export { InputBox };
var HistoryInputBox = /** @class */ (function (_super) {
    __extends(HistoryInputBox, _super);
    function HistoryInputBox(container, contextViewProvider, options) {
        var _this = _super.call(this, container, contextViewProvider, options) || this;
        _this.history = new HistoryNavigator(options.history, 100);
        return _this;
    }
    HistoryInputBox.prototype.addToHistory = function () {
        if (this.value && this.value !== this.getCurrentValue()) {
            this.history.add(this.value);
        }
    };
    HistoryInputBox.prototype.showNextValue = function () {
        if (!this.history.has(this.value)) {
            this.addToHistory();
        }
        var next = this.getNextValue();
        if (next) {
            next = next === this.value ? this.getNextValue() : next;
        }
        if (next) {
            this.value = next;
            aria.status(this.value);
        }
    };
    HistoryInputBox.prototype.showPreviousValue = function () {
        if (!this.history.has(this.value)) {
            this.addToHistory();
        }
        var previous = this.getPreviousValue();
        if (previous) {
            previous = previous === this.value ? this.getPreviousValue() : previous;
        }
        if (previous) {
            this.value = previous;
            aria.status(this.value);
        }
    };
    HistoryInputBox.prototype.getCurrentValue = function () {
        var currentValue = this.history.current();
        if (!currentValue) {
            currentValue = this.history.last();
            this.history.next();
        }
        return currentValue;
    };
    HistoryInputBox.prototype.getPreviousValue = function () {
        return this.history.previous() || this.history.first();
    };
    HistoryInputBox.prototype.getNextValue = function () {
        return this.history.next() || this.history.last();
    };
    return HistoryInputBox;
}(InputBox));
export { HistoryInputBox };
