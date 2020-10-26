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
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __param = (this && this.__param) || function (paramIndex, decorator) {
    return function (target, key) { decorator(target, key, paramIndex); }
};
import './accessibilityHelp.css';
import * as browser from '../../../../base/browser/browser.js';
import * as dom from '../../../../base/browser/dom.js';
import { createFastDomNode } from '../../../../base/browser/fastDomNode.js';
import { renderFormattedText } from '../../../../base/browser/htmlContentRenderer.js';
import { alert } from '../../../../base/browser/ui/aria/aria.js';
import { Widget } from '../../../../base/browser/ui/widget.js';
import { Disposable } from '../../../../base/common/lifecycle.js';
import * as platform from '../../../../base/common/platform.js';
import * as strings from '../../../../base/common/strings.js';
import { URI } from '../../../../base/common/uri.js';
import { EditorAction, EditorCommand, registerEditorAction, registerEditorCommand, registerEditorContribution } from '../../../browser/editorExtensions.js';
import { EditorContextKeys } from '../../../common/editorContextKeys.js';
import { ToggleTabFocusModeAction } from '../../../contrib/toggleTabFocusMode/toggleTabFocusMode.js';
import { IContextKeyService, RawContextKey } from '../../../../platform/contextkey/common/contextkey.js';
import { IInstantiationService } from '../../../../platform/instantiation/common/instantiation.js';
import { IKeybindingService } from '../../../../platform/keybinding/common/keybinding.js';
import { IOpenerService } from '../../../../platform/opener/common/opener.js';
import { contrastBorder, editorWidgetBackground, widgetShadow } from '../../../../platform/theme/common/colorRegistry.js';
import { registerThemingParticipant } from '../../../../platform/theme/common/themeService.js';
import { AccessibilityHelpNLS } from '../../../common/standaloneStrings.js';
var CONTEXT_ACCESSIBILITY_WIDGET_VISIBLE = new RawContextKey('accessibilityHelpWidgetVisible', false);
var AccessibilityHelpController = /** @class */ (function (_super) {
    __extends(AccessibilityHelpController, _super);
    function AccessibilityHelpController(editor, instantiationService) {
        var _this = _super.call(this) || this;
        _this._editor = editor;
        _this._widget = _this._register(instantiationService.createInstance(AccessibilityHelpWidget, _this._editor));
        return _this;
    }
    AccessibilityHelpController.get = function (editor) {
        return editor.getContribution(AccessibilityHelpController.ID);
    };
    AccessibilityHelpController.prototype.getId = function () {
        return AccessibilityHelpController.ID;
    };
    AccessibilityHelpController.prototype.show = function () {
        this._widget.show();
    };
    AccessibilityHelpController.prototype.hide = function () {
        this._widget.hide();
    };
    AccessibilityHelpController.ID = 'editor.contrib.accessibilityHelpController';
    AccessibilityHelpController = __decorate([
        __param(1, IInstantiationService)
    ], AccessibilityHelpController);
    return AccessibilityHelpController;
}(Disposable));
function getSelectionLabel(selections, charactersSelected) {
    if (!selections || selections.length === 0) {
        return AccessibilityHelpNLS.noSelection;
    }
    if (selections.length === 1) {
        if (charactersSelected) {
            return strings.format(AccessibilityHelpNLS.singleSelectionRange, selections[0].positionLineNumber, selections[0].positionColumn, charactersSelected);
        }
        return strings.format(AccessibilityHelpNLS.singleSelection, selections[0].positionLineNumber, selections[0].positionColumn);
    }
    if (charactersSelected) {
        return strings.format(AccessibilityHelpNLS.multiSelectionRange, selections.length, charactersSelected);
    }
    if (selections.length > 0) {
        return strings.format(AccessibilityHelpNLS.multiSelection, selections.length);
    }
    return '';
}
var AccessibilityHelpWidget = /** @class */ (function (_super) {
    __extends(AccessibilityHelpWidget, _super);
    function AccessibilityHelpWidget(editor, _contextKeyService, _keybindingService, _openerService) {
        var _this = _super.call(this) || this;
        _this._contextKeyService = _contextKeyService;
        _this._keybindingService = _keybindingService;
        _this._openerService = _openerService;
        _this._editor = editor;
        _this._isVisibleKey = CONTEXT_ACCESSIBILITY_WIDGET_VISIBLE.bindTo(_this._contextKeyService);
        _this._domNode = createFastDomNode(document.createElement('div'));
        _this._domNode.setClassName('accessibilityHelpWidget');
        _this._domNode.setDisplay('none');
        _this._domNode.setAttribute('role', 'dialog');
        _this._domNode.setAttribute('aria-hidden', 'true');
        _this._contentDomNode = createFastDomNode(document.createElement('div'));
        _this._contentDomNode.setAttribute('role', 'document');
        _this._domNode.appendChild(_this._contentDomNode);
        _this._isVisible = false;
        _this._register(_this._editor.onDidLayoutChange(function () {
            if (_this._isVisible) {
                _this._layout();
            }
        }));
        // Intentionally not configurable!
        _this._register(dom.addStandardDisposableListener(_this._contentDomNode.domNode, 'keydown', function (e) {
            if (!_this._isVisible) {
                return;
            }
            if (e.equals(2048 /* CtrlCmd */ | 35 /* KEY_E */)) {
                alert(AccessibilityHelpNLS.emergencyConfOn);
                _this._editor.updateOptions({
                    accessibilitySupport: 'on'
                });
                dom.clearNode(_this._contentDomNode.domNode);
                _this._buildContent();
                _this._contentDomNode.domNode.focus();
                e.preventDefault();
                e.stopPropagation();
            }
            if (e.equals(2048 /* CtrlCmd */ | 38 /* KEY_H */)) {
                alert(AccessibilityHelpNLS.openingDocs);
                var url = _this._editor.getRawConfiguration().accessibilityHelpUrl;
                if (typeof url === 'undefined') {
                    url = 'https://go.microsoft.com/fwlink/?linkid=852450';
                }
                _this._openerService.open(URI.parse(url));
                e.preventDefault();
                e.stopPropagation();
            }
        }));
        _this.onblur(_this._contentDomNode.domNode, function () {
            _this.hide();
        });
        _this._editor.addOverlayWidget(_this);
        return _this;
    }
    AccessibilityHelpWidget.prototype.dispose = function () {
        this._editor.removeOverlayWidget(this);
        _super.prototype.dispose.call(this);
    };
    AccessibilityHelpWidget.prototype.getId = function () {
        return AccessibilityHelpWidget.ID;
    };
    AccessibilityHelpWidget.prototype.getDomNode = function () {
        return this._domNode.domNode;
    };
    AccessibilityHelpWidget.prototype.getPosition = function () {
        return {
            preference: null
        };
    };
    AccessibilityHelpWidget.prototype.show = function () {
        if (this._isVisible) {
            return;
        }
        this._isVisible = true;
        this._isVisibleKey.set(true);
        this._layout();
        this._domNode.setDisplay('block');
        this._domNode.setAttribute('aria-hidden', 'false');
        this._contentDomNode.domNode.tabIndex = 0;
        this._buildContent();
        this._contentDomNode.domNode.focus();
    };
    AccessibilityHelpWidget.prototype._descriptionForCommand = function (commandId, msg, noKbMsg) {
        var kb = this._keybindingService.lookupKeybinding(commandId);
        if (kb) {
            return strings.format(msg, kb.getAriaLabel());
        }
        return strings.format(noKbMsg, commandId);
    };
    AccessibilityHelpWidget.prototype._buildContent = function () {
        var opts = this._editor.getConfiguration();
        var selections = this._editor.getSelections();
        var charactersSelected = 0;
        if (selections) {
            var model_1 = this._editor.getModel();
            if (model_1) {
                selections.forEach(function (selection) {
                    charactersSelected += model_1.getValueLengthInRange(selection);
                });
            }
        }
        var text = getSelectionLabel(selections, charactersSelected);
        if (opts.wrappingInfo.inDiffEditor) {
            if (opts.readOnly) {
                text += AccessibilityHelpNLS.readonlyDiffEditor;
            }
            else {
                text += AccessibilityHelpNLS.editableDiffEditor;
            }
        }
        else {
            if (opts.readOnly) {
                text += AccessibilityHelpNLS.readonlyEditor;
            }
            else {
                text += AccessibilityHelpNLS.editableEditor;
            }
        }
        var turnOnMessage = (platform.isMacintosh
            ? AccessibilityHelpNLS.changeConfigToOnMac
            : AccessibilityHelpNLS.changeConfigToOnWinLinux);
        switch (opts.accessibilitySupport) {
            case 0 /* Unknown */:
                text += '\n\n - ' + turnOnMessage;
                break;
            case 2 /* Enabled */:
                text += '\n\n - ' + AccessibilityHelpNLS.auto_on;
                break;
            case 1 /* Disabled */:
                text += '\n\n - ' + AccessibilityHelpNLS.auto_off;
                text += ' ' + turnOnMessage;
                break;
        }
        if (opts.tabFocusMode) {
            text += '\n\n - ' + this._descriptionForCommand(ToggleTabFocusModeAction.ID, AccessibilityHelpNLS.tabFocusModeOnMsg, AccessibilityHelpNLS.tabFocusModeOnMsgNoKb);
        }
        else {
            text += '\n\n - ' + this._descriptionForCommand(ToggleTabFocusModeAction.ID, AccessibilityHelpNLS.tabFocusModeOffMsg, AccessibilityHelpNLS.tabFocusModeOffMsgNoKb);
        }
        var openDocMessage = (platform.isMacintosh
            ? AccessibilityHelpNLS.openDocMac
            : AccessibilityHelpNLS.openDocWinLinux);
        text += '\n\n - ' + openDocMessage;
        text += '\n\n' + AccessibilityHelpNLS.outroMsg;
        this._contentDomNode.domNode.appendChild(renderFormattedText(text));
        // Per https://www.w3.org/TR/wai-aria/roles#document, Authors SHOULD provide a title or label for documents
        this._contentDomNode.domNode.setAttribute('aria-label', text);
    };
    AccessibilityHelpWidget.prototype.hide = function () {
        if (!this._isVisible) {
            return;
        }
        this._isVisible = false;
        this._isVisibleKey.reset();
        this._domNode.setDisplay('none');
        this._domNode.setAttribute('aria-hidden', 'true');
        this._contentDomNode.domNode.tabIndex = -1;
        dom.clearNode(this._contentDomNode.domNode);
        this._editor.focus();
    };
    AccessibilityHelpWidget.prototype._layout = function () {
        var editorLayout = this._editor.getLayoutInfo();
        var w = Math.max(5, Math.min(AccessibilityHelpWidget.WIDTH, editorLayout.width - 40));
        var h = Math.max(5, Math.min(AccessibilityHelpWidget.HEIGHT, editorLayout.height - 40));
        this._domNode.setWidth(w);
        this._domNode.setHeight(h);
        var top = Math.round((editorLayout.height - h) / 2);
        this._domNode.setTop(top);
        var left = Math.round((editorLayout.width - w) / 2);
        this._domNode.setLeft(left);
    };
    AccessibilityHelpWidget.ID = 'editor.contrib.accessibilityHelpWidget';
    AccessibilityHelpWidget.WIDTH = 500;
    AccessibilityHelpWidget.HEIGHT = 300;
    AccessibilityHelpWidget = __decorate([
        __param(1, IContextKeyService),
        __param(2, IKeybindingService),
        __param(3, IOpenerService)
    ], AccessibilityHelpWidget);
    return AccessibilityHelpWidget;
}(Widget));
var ShowAccessibilityHelpAction = /** @class */ (function (_super) {
    __extends(ShowAccessibilityHelpAction, _super);
    function ShowAccessibilityHelpAction() {
        return _super.call(this, {
            id: 'editor.action.showAccessibilityHelp',
            label: AccessibilityHelpNLS.showAccessibilityHelpAction,
            alias: 'Show Accessibility Help',
            precondition: null,
            kbOpts: {
                kbExpr: EditorContextKeys.focus,
                primary: (browser.isIE ? 2048 /* CtrlCmd */ | 59 /* F1 */ : 512 /* Alt */ | 59 /* F1 */),
                weight: 100 /* EditorContrib */
            }
        }) || this;
    }
    ShowAccessibilityHelpAction.prototype.run = function (accessor, editor) {
        var controller = AccessibilityHelpController.get(editor);
        if (controller) {
            controller.show();
        }
    };
    return ShowAccessibilityHelpAction;
}(EditorAction));
registerEditorContribution(AccessibilityHelpController);
registerEditorAction(ShowAccessibilityHelpAction);
var AccessibilityHelpCommand = EditorCommand.bindToContribution(AccessibilityHelpController.get);
registerEditorCommand(new AccessibilityHelpCommand({
    id: 'closeAccessibilityHelp',
    precondition: CONTEXT_ACCESSIBILITY_WIDGET_VISIBLE,
    handler: function (x) { return x.hide(); },
    kbOpts: {
        weight: 100 /* EditorContrib */ + 100,
        kbExpr: EditorContextKeys.focus,
        primary: 9 /* Escape */,
        secondary: [1024 /* Shift */ | 9 /* Escape */]
    }
}));
registerThemingParticipant(function (theme, collector) {
    var widgetBackground = theme.getColor(editorWidgetBackground);
    if (widgetBackground) {
        collector.addRule(".monaco-editor .accessibilityHelpWidget { background-color: " + widgetBackground + "; }");
    }
    var widgetShadowColor = theme.getColor(widgetShadow);
    if (widgetShadowColor) {
        collector.addRule(".monaco-editor .accessibilityHelpWidget { box-shadow: 0 2px 8px " + widgetShadowColor + "; }");
    }
    var hcBorder = theme.getColor(contrastBorder);
    if (hcBorder) {
        collector.addRule(".monaco-editor .accessibilityHelpWidget { border: 2px solid " + hcBorder + "; }");
    }
});
