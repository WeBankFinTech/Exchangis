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
import * as dom from '../../../base/browser/dom.js';
import { GlobalMouseMoveMonitor, standardMouseMoveMerger } from '../../../base/browser/globalMouseMoveMonitor.js';
import { CancellationTokenSource } from '../../../base/common/cancellation.js';
import { Emitter } from '../../../base/common/event.js';
import { Disposable } from '../../../base/common/lifecycle.js';
import './lightBulbWidget.css';
import { TextModel } from '../../common/model/textModel.js';
import { CodeActionsState } from './codeActionModel.js';
var LightBulbWidget = /** @class */ (function (_super) {
    __extends(LightBulbWidget, _super);
    function LightBulbWidget(editor) {
        var _this = _super.call(this) || this;
        _this._onClick = _this._register(new Emitter());
        _this.onClick = _this._onClick.event;
        _this._state = CodeActionsState.Empty;
        _this._futureFixes = new CancellationTokenSource();
        _this._domNode = document.createElement('div');
        _this._domNode.className = 'lightbulb-glyph';
        _this._editor = editor;
        _this._editor.addContentWidget(_this);
        _this._register(_this._editor.onDidChangeModel(function (_) { return _this._futureFixes.cancel(); }));
        _this._register(_this._editor.onDidChangeModelLanguage(function (_) { return _this._futureFixes.cancel(); }));
        _this._register(_this._editor.onDidChangeModelContent(function (_) {
            // cancel when the line in question has been removed
            var editorModel = _this._editor.getModel();
            if (_this._state.type !== 1 /* Triggered */ || !editorModel || _this._state.position.lineNumber >= editorModel.getLineCount()) {
                _this._futureFixes.cancel();
            }
        }));
        _this._register(dom.addStandardDisposableListener(_this._domNode, 'click', function (e) {
            if (_this._state.type !== 1 /* Triggered */) {
                return;
            }
            // Make sure that focus / cursor location is not lost when clicking widget icon
            _this._editor.focus();
            // a bit of extra work to make sure the menu
            // doesn't cover the line-text
            var _a = dom.getDomNodePagePosition(_this._domNode), top = _a.top, height = _a.height;
            var lineHeight = _this._editor.getConfiguration().lineHeight;
            var pad = Math.floor(lineHeight / 3);
            if (_this._position && _this._position.position !== null && _this._position.position.lineNumber < _this._state.position.lineNumber) {
                pad += lineHeight;
            }
            _this._onClick.fire({
                x: e.posx,
                y: top + height + pad,
                state: _this._state
            });
        }));
        _this._register(dom.addDisposableListener(_this._domNode, 'mouseenter', function (e) {
            if ((e.buttons & 1) !== 1) {
                return;
            }
            // mouse enters lightbulb while the primary/left button
            // is being pressed -> hide the lightbulb and block future
            // showings until mouse is released
            _this.hide();
            var monitor = new GlobalMouseMoveMonitor();
            monitor.startMonitoring(standardMouseMoveMerger, function () { }, function () {
                monitor.dispose();
            });
        }));
        _this._register(_this._editor.onDidChangeConfiguration(function (e) {
            // hide when told to do so
            if (e.contribInfo && !_this._editor.getConfiguration().contribInfo.lightbulbEnabled) {
                _this.hide();
            }
        }));
        return _this;
    }
    LightBulbWidget.prototype.dispose = function () {
        _super.prototype.dispose.call(this);
        this._editor.removeContentWidget(this);
    };
    LightBulbWidget.prototype.getId = function () {
        return 'LightBulbWidget';
    };
    LightBulbWidget.prototype.getDomNode = function () {
        return this._domNode;
    };
    LightBulbWidget.prototype.getPosition = function () {
        return this._position;
    };
    LightBulbWidget.prototype.tryShow = function (newState) {
        var _this = this;
        if (newState.type !== 1 /* Triggered */ || this._position && (!newState.position || this._position.position && this._position.position.lineNumber !== newState.position.lineNumber)) {
            // hide when getting a 'hide'-request or when currently
            // showing on another line
            this.hide();
        }
        else if (this._futureFixes) {
            // cancel pending show request in any case
            this._futureFixes.cancel();
        }
        this._futureFixes = new CancellationTokenSource();
        var token = this._futureFixes.token;
        this._state = newState;
        if (this._state.type === CodeActionsState.Empty.type) {
            return;
        }
        var selection = this._state.rangeOrSelection;
        this._state.actions.then(function (fixes) {
            if (!token.isCancellationRequested && fixes.actions.length > 0 && selection) {
                _this._show(fixes);
            }
            else {
                _this.hide();
            }
        }).catch(function () {
            _this.hide();
        });
    };
    Object.defineProperty(LightBulbWidget.prototype, "title", {
        get: function () {
            return this._domNode.title;
        },
        set: function (value) {
            this._domNode.title = value;
        },
        enumerable: true,
        configurable: true
    });
    LightBulbWidget.prototype._show = function (codeActions) {
        var _this = this;
        var config = this._editor.getConfiguration();
        if (!config.contribInfo.lightbulbEnabled) {
            return;
        }
        if (this._state.type !== 1 /* Triggered */) {
            return;
        }
        var _a = this._state.position, lineNumber = _a.lineNumber, column = _a.column;
        var model = this._editor.getModel();
        if (!model) {
            return;
        }
        var tabSize = model.getOptions().tabSize;
        var lineContent = model.getLineContent(lineNumber);
        var indent = TextModel.computeIndentLevel(lineContent, tabSize);
        var lineHasSpace = config.fontInfo.spaceWidth * indent > 22;
        var isFolded = function (lineNumber) {
            return lineNumber > 2 && _this._editor.getTopForLineNumber(lineNumber) === _this._editor.getTopForLineNumber(lineNumber - 1);
        };
        var effectiveLineNumber = lineNumber;
        if (!lineHasSpace) {
            if (lineNumber > 1 && !isFolded(lineNumber - 1)) {
                effectiveLineNumber -= 1;
            }
            else if (!isFolded(lineNumber + 1)) {
                effectiveLineNumber += 1;
            }
            else if (column * config.fontInfo.spaceWidth < 22) {
                // cannot show lightbulb above/below and showing
                // it inline would overlay the cursor...
                this.hide();
                return;
            }
        }
        this._position = {
            position: { lineNumber: effectiveLineNumber, column: 1 },
            preference: LightBulbWidget._posPref
        };
        dom.toggleClass(this._domNode, 'autofixable', codeActions.hasAutoFix);
        this._editor.layoutContentWidget(this);
    };
    LightBulbWidget.prototype.hide = function () {
        this._position = null;
        this._state = CodeActionsState.Empty;
        this._futureFixes.cancel();
        this._editor.layoutContentWidget(this);
    };
    LightBulbWidget._posPref = [0 /* EXACT */];
    return LightBulbWidget;
}(Disposable));
export { LightBulbWidget };
