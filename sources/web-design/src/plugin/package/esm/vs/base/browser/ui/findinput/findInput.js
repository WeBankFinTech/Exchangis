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
import './findInput.css';
import * as nls from '../../../../nls.js';
import * as dom from '../../dom.js';
import { HistoryInputBox } from '../inputbox/inputBox.js';
import { Widget } from '../widget.js';
import { Emitter } from '../../../common/event.js';
import { CaseSensitiveCheckbox, WholeWordsCheckbox, RegexCheckbox } from './findInputCheckboxes.js';
var NLS_DEFAULT_LABEL = nls.localize('defaultLabel', "input");
var FindInput = /** @class */ (function (_super) {
    __extends(FindInput, _super);
    function FindInput(parent, contextViewProvider, _showOptionButtons, options) {
        var _this = _super.call(this) || this;
        _this._showOptionButtons = _showOptionButtons;
        _this.fixFocusOnOptionClickEnabled = true;
        _this._onDidOptionChange = _this._register(new Emitter());
        _this.onDidOptionChange = _this._onDidOptionChange.event;
        _this._onKeyDown = _this._register(new Emitter());
        _this.onKeyDown = _this._onKeyDown.event;
        _this._onMouseDown = _this._register(new Emitter());
        _this.onMouseDown = _this._onMouseDown.event;
        _this._onInput = _this._register(new Emitter());
        _this._onKeyUp = _this._register(new Emitter());
        _this._onCaseSensitiveKeyDown = _this._register(new Emitter());
        _this.onCaseSensitiveKeyDown = _this._onCaseSensitiveKeyDown.event;
        _this._onRegexKeyDown = _this._register(new Emitter());
        _this._lastHighlightFindOptions = 0;
        _this.contextViewProvider = contextViewProvider;
        _this.placeholder = options.placeholder || '';
        _this.validation = options.validation;
        _this.label = options.label || NLS_DEFAULT_LABEL;
        _this.inputActiveOptionBorder = options.inputActiveOptionBorder;
        _this.inputBackground = options.inputBackground;
        _this.inputForeground = options.inputForeground;
        _this.inputBorder = options.inputBorder;
        _this.inputValidationInfoBorder = options.inputValidationInfoBorder;
        _this.inputValidationInfoBackground = options.inputValidationInfoBackground;
        _this.inputValidationInfoForeground = options.inputValidationInfoForeground;
        _this.inputValidationWarningBorder = options.inputValidationWarningBorder;
        _this.inputValidationWarningBackground = options.inputValidationWarningBackground;
        _this.inputValidationWarningForeground = options.inputValidationWarningForeground;
        _this.inputValidationErrorBorder = options.inputValidationErrorBorder;
        _this.inputValidationErrorBackground = options.inputValidationErrorBackground;
        _this.inputValidationErrorForeground = options.inputValidationErrorForeground;
        _this.buildDomNode(options.appendCaseSensitiveLabel || '', options.appendWholeWordsLabel || '', options.appendRegexLabel || '', options.history || [], !!options.flexibleHeight);
        if (parent) {
            parent.appendChild(_this.domNode);
        }
        _this.onkeydown(_this.inputBox.inputElement, function (e) { return _this._onKeyDown.fire(e); });
        _this.onkeyup(_this.inputBox.inputElement, function (e) { return _this._onKeyUp.fire(e); });
        _this.oninput(_this.inputBox.inputElement, function (e) { return _this._onInput.fire(); });
        _this.onmousedown(_this.inputBox.inputElement, function (e) { return _this._onMouseDown.fire(e); });
        return _this;
    }
    FindInput.prototype.enable = function () {
        dom.removeClass(this.domNode, 'disabled');
        this.inputBox.enable();
        this.regex.enable();
        this.wholeWords.enable();
        this.caseSensitive.enable();
    };
    FindInput.prototype.disable = function () {
        dom.addClass(this.domNode, 'disabled');
        this.inputBox.disable();
        this.regex.disable();
        this.wholeWords.disable();
        this.caseSensitive.disable();
    };
    FindInput.prototype.setFocusInputOnOptionClick = function (value) {
        this.fixFocusOnOptionClickEnabled = value;
    };
    FindInput.prototype.setEnabled = function (enabled) {
        if (enabled) {
            this.enable();
        }
        else {
            this.disable();
        }
    };
    FindInput.prototype.getValue = function () {
        return this.inputBox.value;
    };
    FindInput.prototype.setValue = function (value) {
        if (this.inputBox.value !== value) {
            this.inputBox.value = value;
        }
    };
    FindInput.prototype.style = function (styles) {
        this.inputActiveOptionBorder = styles.inputActiveOptionBorder;
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
    FindInput.prototype.applyStyles = function () {
        if (this.domNode) {
            var checkBoxStyles = {
                inputActiveOptionBorder: this.inputActiveOptionBorder,
            };
            this.regex.style(checkBoxStyles);
            this.wholeWords.style(checkBoxStyles);
            this.caseSensitive.style(checkBoxStyles);
            var inputBoxStyles = {
                inputBackground: this.inputBackground,
                inputForeground: this.inputForeground,
                inputBorder: this.inputBorder,
                inputValidationInfoBackground: this.inputValidationInfoBackground,
                inputValidationInfoForeground: this.inputValidationInfoForeground,
                inputValidationInfoBorder: this.inputValidationInfoBorder,
                inputValidationWarningBackground: this.inputValidationWarningBackground,
                inputValidationWarningForeground: this.inputValidationWarningForeground,
                inputValidationWarningBorder: this.inputValidationWarningBorder,
                inputValidationErrorBackground: this.inputValidationErrorBackground,
                inputValidationErrorForeground: this.inputValidationErrorForeground,
                inputValidationErrorBorder: this.inputValidationErrorBorder
            };
            this.inputBox.style(inputBoxStyles);
        }
    };
    FindInput.prototype.select = function () {
        this.inputBox.select();
    };
    FindInput.prototype.focus = function () {
        this.inputBox.focus();
    };
    FindInput.prototype.getCaseSensitive = function () {
        return this.caseSensitive.checked;
    };
    FindInput.prototype.setCaseSensitive = function (value) {
        this.caseSensitive.checked = value;
    };
    FindInput.prototype.getWholeWords = function () {
        return this.wholeWords.checked;
    };
    FindInput.prototype.setWholeWords = function (value) {
        this.wholeWords.checked = value;
    };
    FindInput.prototype.getRegex = function () {
        return this.regex.checked;
    };
    FindInput.prototype.setRegex = function (value) {
        this.regex.checked = value;
        this.validate();
    };
    FindInput.prototype.focusOnCaseSensitive = function () {
        this.caseSensitive.focus();
    };
    FindInput.prototype.highlightFindOptions = function () {
        dom.removeClass(this.domNode, 'highlight-' + (this._lastHighlightFindOptions));
        this._lastHighlightFindOptions = 1 - this._lastHighlightFindOptions;
        dom.addClass(this.domNode, 'highlight-' + (this._lastHighlightFindOptions));
    };
    FindInput.prototype.buildDomNode = function (appendCaseSensitiveLabel, appendWholeWordsLabel, appendRegexLabel, history, flexibleHeight) {
        var _this = this;
        this.domNode = document.createElement('div');
        dom.addClass(this.domNode, 'monaco-findInput');
        this.inputBox = this._register(new HistoryInputBox(this.domNode, this.contextViewProvider, {
            placeholder: this.placeholder || '',
            ariaLabel: this.label || '',
            validationOptions: {
                validation: this.validation
            },
            inputBackground: this.inputBackground,
            inputForeground: this.inputForeground,
            inputBorder: this.inputBorder,
            inputValidationInfoBackground: this.inputValidationInfoBackground,
            inputValidationInfoForeground: this.inputValidationInfoForeground,
            inputValidationInfoBorder: this.inputValidationInfoBorder,
            inputValidationWarningBackground: this.inputValidationWarningBackground,
            inputValidationWarningForeground: this.inputValidationWarningForeground,
            inputValidationWarningBorder: this.inputValidationWarningBorder,
            inputValidationErrorBackground: this.inputValidationErrorBackground,
            inputValidationErrorForeground: this.inputValidationErrorForeground,
            inputValidationErrorBorder: this.inputValidationErrorBorder,
            history: history,
            flexibleHeight: flexibleHeight
        }));
        this.regex = this._register(new RegexCheckbox({
            appendTitle: appendRegexLabel,
            isChecked: false,
            inputActiveOptionBorder: this.inputActiveOptionBorder
        }));
        this._register(this.regex.onChange(function (viaKeyboard) {
            _this._onDidOptionChange.fire(viaKeyboard);
            if (!viaKeyboard && _this.fixFocusOnOptionClickEnabled) {
                _this.inputBox.focus();
            }
            _this.validate();
        }));
        this._register(this.regex.onKeyDown(function (e) {
            _this._onRegexKeyDown.fire(e);
        }));
        this.wholeWords = this._register(new WholeWordsCheckbox({
            appendTitle: appendWholeWordsLabel,
            isChecked: false,
            inputActiveOptionBorder: this.inputActiveOptionBorder
        }));
        this._register(this.wholeWords.onChange(function (viaKeyboard) {
            _this._onDidOptionChange.fire(viaKeyboard);
            if (!viaKeyboard && _this.fixFocusOnOptionClickEnabled) {
                _this.inputBox.focus();
            }
            _this.validate();
        }));
        this.caseSensitive = this._register(new CaseSensitiveCheckbox({
            appendTitle: appendCaseSensitiveLabel,
            isChecked: false,
            inputActiveOptionBorder: this.inputActiveOptionBorder
        }));
        this._register(this.caseSensitive.onChange(function (viaKeyboard) {
            _this._onDidOptionChange.fire(viaKeyboard);
            if (!viaKeyboard && _this.fixFocusOnOptionClickEnabled) {
                _this.inputBox.focus();
            }
            _this.validate();
        }));
        this._register(this.caseSensitive.onKeyDown(function (e) {
            _this._onCaseSensitiveKeyDown.fire(e);
        }));
        if (this._showOptionButtons) {
            var paddingRight = (this.caseSensitive.width() + this.wholeWords.width() + this.regex.width()) + 'px';
            this.inputBox.inputElement.style.paddingRight = paddingRight;
            if (this.inputBox.mirrorElement) {
                this.inputBox.mirrorElement.style.paddingRight = paddingRight;
            }
        }
        // Arrow-Key support to navigate between options
        var indexes = [this.caseSensitive.domNode, this.wholeWords.domNode, this.regex.domNode];
        this.onkeydown(this.domNode, function (event) {
            if (event.equals(15 /* LeftArrow */) || event.equals(17 /* RightArrow */) || event.equals(9 /* Escape */)) {
                var index = indexes.indexOf(document.activeElement);
                if (index >= 0) {
                    var newIndex = -1;
                    if (event.equals(17 /* RightArrow */)) {
                        newIndex = (index + 1) % indexes.length;
                    }
                    else if (event.equals(15 /* LeftArrow */)) {
                        if (index === 0) {
                            newIndex = indexes.length - 1;
                        }
                        else {
                            newIndex = index - 1;
                        }
                    }
                    if (event.equals(9 /* Escape */)) {
                        indexes[index].blur();
                    }
                    else if (newIndex >= 0) {
                        indexes[newIndex].focus();
                    }
                    dom.EventHelper.stop(event, true);
                }
            }
        });
        var controls = document.createElement('div');
        controls.className = 'controls';
        controls.style.display = this._showOptionButtons ? 'block' : 'none';
        controls.appendChild(this.caseSensitive.domNode);
        controls.appendChild(this.wholeWords.domNode);
        controls.appendChild(this.regex.domNode);
        this.domNode.appendChild(controls);
    };
    FindInput.prototype.validate = function () {
        if (this.inputBox) {
            this.inputBox.validate();
        }
    };
    FindInput.prototype.clearMessage = function () {
        if (this.inputBox) {
            this.inputBox.hideMessage();
        }
    };
    FindInput.prototype.dispose = function () {
        _super.prototype.dispose.call(this);
    };
    return FindInput;
}(Widget));
export { FindInput };
