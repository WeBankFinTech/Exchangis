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
import * as nls from '../../../nls.js';
import { IntervalTimer } from '../../../base/common/async.js';
import { Emitter, Event } from '../../../base/common/event.js';
import { Disposable } from '../../../base/common/lifecycle.js';
import { withNullAsUndefined } from '../../../base/common/types.js';
var AbstractKeybindingService = /** @class */ (function (_super) {
    __extends(AbstractKeybindingService, _super);
    function AbstractKeybindingService(contextKeyService, commandService, telemetryService, notificationService, statusService) {
        var _this = _super.call(this) || this;
        _this._contextKeyService = contextKeyService;
        _this._commandService = commandService;
        _this._telemetryService = telemetryService;
        _this._statusService = statusService;
        _this._notificationService = notificationService;
        _this._currentChord = null;
        _this._currentChordChecker = new IntervalTimer();
        _this._currentChordStatusMessage = null;
        _this._onDidUpdateKeybindings = _this._register(new Emitter());
        return _this;
    }
    AbstractKeybindingService.prototype.dispose = function () {
        _super.prototype.dispose.call(this);
    };
    Object.defineProperty(AbstractKeybindingService.prototype, "onDidUpdateKeybindings", {
        get: function () {
            return this._onDidUpdateKeybindings ? this._onDidUpdateKeybindings.event : Event.None; // Sinon stubbing walks properties on prototype
        },
        enumerable: true,
        configurable: true
    });
    AbstractKeybindingService.prototype.lookupKeybinding = function (commandId) {
        var result = this._getResolver().lookupPrimaryKeybinding(commandId);
        if (!result) {
            return undefined;
        }
        return withNullAsUndefined(result.resolvedKeybinding);
    };
    AbstractKeybindingService.prototype.softDispatch = function (e, target) {
        var keybinding = this.resolveKeyboardEvent(e);
        if (keybinding.isChord()) {
            console.warn('Unexpected keyboard event mapped to a chord');
            return null;
        }
        var firstPart = keybinding.getDispatchParts()[0];
        if (firstPart === null) {
            // cannot be dispatched, probably only modifier keys
            return null;
        }
        var contextValue = this._contextKeyService.getContext(target);
        var currentChord = this._currentChord ? this._currentChord.keypress : null;
        return this._getResolver().resolve(contextValue, currentChord, firstPart);
    };
    AbstractKeybindingService.prototype._enterChordMode = function (firstPart, keypressLabel) {
        var _this = this;
        this._currentChord = {
            keypress: firstPart,
            label: keypressLabel
        };
        if (this._statusService) {
            this._currentChordStatusMessage = this._statusService.setStatusMessage(nls.localize('first.chord', "({0}) was pressed. Waiting for second key of chord...", keypressLabel));
        }
        var chordEnterTime = Date.now();
        this._currentChordChecker.cancelAndSet(function () {
            if (!_this._documentHasFocus()) {
                // Focus has been lost => leave chord mode
                _this._leaveChordMode();
                return;
            }
            if (Date.now() - chordEnterTime > 5000) {
                // 5 seconds elapsed => leave chord mode
                _this._leaveChordMode();
            }
        }, 500);
    };
    AbstractKeybindingService.prototype._leaveChordMode = function () {
        if (this._currentChordStatusMessage) {
            this._currentChordStatusMessage.dispose();
            this._currentChordStatusMessage = null;
        }
        this._currentChordChecker.cancel();
        this._currentChord = null;
    };
    AbstractKeybindingService.prototype._dispatch = function (e, target) {
        return this._doDispatch(this.resolveKeyboardEvent(e), target);
    };
    AbstractKeybindingService.prototype._doDispatch = function (keybinding, target) {
        var _this = this;
        var shouldPreventDefault = false;
        if (keybinding.isChord()) {
            console.warn('Unexpected keyboard event mapped to a chord');
            return false;
        }
        var firstPart = keybinding.getDispatchParts()[0];
        if (firstPart === null) {
            // cannot be dispatched, probably only modifier keys
            return shouldPreventDefault;
        }
        var contextValue = this._contextKeyService.getContext(target);
        var currentChord = this._currentChord ? this._currentChord.keypress : null;
        var keypressLabel = keybinding.getLabel();
        var resolveResult = this._getResolver().resolve(contextValue, currentChord, firstPart);
        if (resolveResult && resolveResult.enterChord) {
            shouldPreventDefault = true;
            this._enterChordMode(firstPart, keypressLabel);
            return shouldPreventDefault;
        }
        if (this._statusService && this._currentChord) {
            if (!resolveResult || !resolveResult.commandId) {
                this._statusService.setStatusMessage(nls.localize('missing.chord', "The key combination ({0}, {1}) is not a command.", this._currentChord.label, keypressLabel), 10 * 1000 /* 10s */);
                shouldPreventDefault = true;
            }
        }
        this._leaveChordMode();
        if (resolveResult && resolveResult.commandId) {
            if (!resolveResult.bubble) {
                shouldPreventDefault = true;
            }
            if (typeof resolveResult.commandArgs === 'undefined') {
                this._commandService.executeCommand(resolveResult.commandId).then(undefined, function (err) { return _this._notificationService.warn(err); });
            }
            else {
                this._commandService.executeCommand(resolveResult.commandId, resolveResult.commandArgs).then(undefined, function (err) { return _this._notificationService.warn(err); });
            }
            /* __GDPR__
                "workbenchActionExecuted" : {
                    "id" : { "classification": "SystemMetaData", "purpose": "FeatureInsight" },
                    "from": { "classification": "SystemMetaData", "purpose": "FeatureInsight" }
                }
            */
            this._telemetryService.publicLog('workbenchActionExecuted', { id: resolveResult.commandId, from: 'keybinding' });
        }
        return shouldPreventDefault;
    };
    AbstractKeybindingService.prototype.mightProducePrintableCharacter = function (event) {
        if (event.ctrlKey || event.metaKey) {
            // ignore ctrl/cmd-combination but not shift/alt-combinatios
            return false;
        }
        // weak check for certain ranges. this is properly implemented in a subclass
        // with access to the KeyboardMapperFactory.
        if ((event.keyCode >= 31 /* KEY_A */ && event.keyCode <= 56 /* KEY_Z */)
            || (event.keyCode >= 21 /* KEY_0 */ && event.keyCode <= 30 /* KEY_9 */)) {
            return true;
        }
        return false;
    };
    return AbstractKeybindingService;
}(Disposable));
export { AbstractKeybindingService };
