/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
import * as nls from '../../nls.js';
var ModifierLabelProvider = /** @class */ (function () {
    function ModifierLabelProvider(mac, windows, linux) {
        if (linux === void 0) { linux = windows; }
        this.modifierLabels = [null]; // index 0 will never me accessed.
        this.modifierLabels[2 /* Macintosh */] = mac;
        this.modifierLabels[1 /* Windows */] = windows;
        this.modifierLabels[3 /* Linux */] = linux;
    }
    ModifierLabelProvider.prototype.toLabel = function (OS, parts, keyLabelProvider) {
        if (parts.length === 0) {
            return null;
        }
        var result = [];
        for (var i = 0, len = parts.length; i < len; i++) {
            var part = parts[i];
            var keyLabel = keyLabelProvider(part);
            if (keyLabel === null) {
                // this keybinding cannot be expressed...
                return null;
            }
            result[i] = _simpleAsString(part, keyLabel, this.modifierLabels[OS]);
        }
        return result.join(' ');
    };
    return ModifierLabelProvider;
}());
export { ModifierLabelProvider };
/**
 * A label provider that prints modifiers in a suitable format for displaying in the UI.
 */
export var UILabelProvider = new ModifierLabelProvider({
    ctrlKey: '⌃',
    shiftKey: '⇧',
    altKey: '⌥',
    metaKey: '⌘',
    separator: '',
}, {
    ctrlKey: nls.localize({ key: 'ctrlKey', comment: ['This is the short form for the Control key on the keyboard'] }, "Ctrl"),
    shiftKey: nls.localize({ key: 'shiftKey', comment: ['This is the short form for the Shift key on the keyboard'] }, "Shift"),
    altKey: nls.localize({ key: 'altKey', comment: ['This is the short form for the Alt key on the keyboard'] }, "Alt"),
    metaKey: nls.localize({ key: 'windowsKey', comment: ['This is the short form for the Windows key on the keyboard'] }, "Windows"),
    separator: '+',
}, {
    ctrlKey: nls.localize({ key: 'ctrlKey', comment: ['This is the short form for the Control key on the keyboard'] }, "Ctrl"),
    shiftKey: nls.localize({ key: 'shiftKey', comment: ['This is the short form for the Shift key on the keyboard'] }, "Shift"),
    altKey: nls.localize({ key: 'altKey', comment: ['This is the short form for the Alt key on the keyboard'] }, "Alt"),
    metaKey: nls.localize({ key: 'superKey', comment: ['This is the short form for the Super key on the keyboard'] }, "Super"),
    separator: '+',
});
/**
 * A label provider that prints modifiers in a suitable format for ARIA.
 */
export var AriaLabelProvider = new ModifierLabelProvider({
    ctrlKey: nls.localize({ key: 'ctrlKey.long', comment: ['This is the long form for the Control key on the keyboard'] }, "Control"),
    shiftKey: nls.localize({ key: 'shiftKey.long', comment: ['This is the long form for the Shift key on the keyboard'] }, "Shift"),
    altKey: nls.localize({ key: 'altKey.long', comment: ['This is the long form for the Alt key on the keyboard'] }, "Alt"),
    metaKey: nls.localize({ key: 'cmdKey.long', comment: ['This is the long form for the Command key on the keyboard'] }, "Command"),
    separator: '+',
}, {
    ctrlKey: nls.localize({ key: 'ctrlKey.long', comment: ['This is the long form for the Control key on the keyboard'] }, "Control"),
    shiftKey: nls.localize({ key: 'shiftKey.long', comment: ['This is the long form for the Shift key on the keyboard'] }, "Shift"),
    altKey: nls.localize({ key: 'altKey.long', comment: ['This is the long form for the Alt key on the keyboard'] }, "Alt"),
    metaKey: nls.localize({ key: 'windowsKey.long', comment: ['This is the long form for the Windows key on the keyboard'] }, "Windows"),
    separator: '+',
}, {
    ctrlKey: nls.localize({ key: 'ctrlKey.long', comment: ['This is the long form for the Control key on the keyboard'] }, "Control"),
    shiftKey: nls.localize({ key: 'shiftKey.long', comment: ['This is the long form for the Shift key on the keyboard'] }, "Shift"),
    altKey: nls.localize({ key: 'altKey.long', comment: ['This is the long form for the Alt key on the keyboard'] }, "Alt"),
    metaKey: nls.localize({ key: 'superKey.long', comment: ['This is the long form for the Super key on the keyboard'] }, "Super"),
    separator: '+',
});
function _simpleAsString(modifiers, key, labels) {
    if (key === null) {
        return '';
    }
    var result = [];
    // translate modifier keys: Ctrl-Shift-Alt-Meta
    if (modifiers.ctrlKey) {
        result.push(labels.ctrlKey);
    }
    if (modifiers.shiftKey) {
        result.push(labels.shiftKey);
    }
    if (modifiers.altKey) {
        result.push(labels.altKey);
    }
    if (modifiers.metaKey) {
        result.push(labels.metaKey);
    }
    // the actual key
    result.push(key);
    return result.join(labels.separator);
}
