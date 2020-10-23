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
import { KeyChord } from '../../../base/common/keyCodes.js';
import { EditorAction, registerEditorAction } from '../../browser/editorExtensions.js';
import { EditorContextKeys } from '../../common/editorContextKeys.js';
import { BlockCommentCommand } from './blockCommentCommand.js';
import { LineCommentCommand } from './lineCommentCommand.js';
var CommentLineAction = /** @class */ (function (_super) {
    __extends(CommentLineAction, _super);
    function CommentLineAction(type, opts) {
        var _this = _super.call(this, opts) || this;
        _this._type = type;
        return _this;
    }
    CommentLineAction.prototype.run = function (accessor, editor) {
        if (!editor.hasModel()) {
            return;
        }
        var model = editor.getModel();
        var commands = [];
        var selections = editor.getSelections();
        var opts = model.getOptions();
        for (var _i = 0, selections_1 = selections; _i < selections_1.length; _i++) {
            var selection = selections_1[_i];
            commands.push(new LineCommentCommand(selection, opts.tabSize, this._type));
        }
        editor.pushUndoStop();
        editor.executeCommands(this.id, commands);
        editor.pushUndoStop();
    };
    return CommentLineAction;
}(EditorAction));
var ToggleCommentLineAction = /** @class */ (function (_super) {
    __extends(ToggleCommentLineAction, _super);
    function ToggleCommentLineAction() {
        return _super.call(this, 0 /* Toggle */, {
            id: 'editor.action.commentLine',
            label: nls.localize('comment.line', "Toggle Line Comment"),
            alias: 'Toggle Line Comment',
            precondition: EditorContextKeys.writable,
            kbOpts: {
                kbExpr: EditorContextKeys.editorTextFocus,
                primary: 2048 /* CtrlCmd */ | 85 /* US_SLASH */,
                weight: 100 /* EditorContrib */
            },
            menubarOpts: {
                menuId: 14 /* MenubarEditMenu */,
                group: '5_insert',
                title: nls.localize({ key: 'miToggleLineComment', comment: ['&& denotes a mnemonic'] }, "&&Toggle Line Comment"),
                order: 1
            }
        }) || this;
    }
    return ToggleCommentLineAction;
}(CommentLineAction));
var AddLineCommentAction = /** @class */ (function (_super) {
    __extends(AddLineCommentAction, _super);
    function AddLineCommentAction() {
        return _super.call(this, 1 /* ForceAdd */, {
            id: 'editor.action.addCommentLine',
            label: nls.localize('comment.line.add', "Add Line Comment"),
            alias: 'Add Line Comment',
            precondition: EditorContextKeys.writable,
            kbOpts: {
                kbExpr: EditorContextKeys.editorTextFocus,
                primary: KeyChord(2048 /* CtrlCmd */ | 41 /* KEY_K */, 2048 /* CtrlCmd */ | 33 /* KEY_C */),
                weight: 100 /* EditorContrib */
            }
        }) || this;
    }
    return AddLineCommentAction;
}(CommentLineAction));
var RemoveLineCommentAction = /** @class */ (function (_super) {
    __extends(RemoveLineCommentAction, _super);
    function RemoveLineCommentAction() {
        return _super.call(this, 2 /* ForceRemove */, {
            id: 'editor.action.removeCommentLine',
            label: nls.localize('comment.line.remove', "Remove Line Comment"),
            alias: 'Remove Line Comment',
            precondition: EditorContextKeys.writable,
            kbOpts: {
                kbExpr: EditorContextKeys.editorTextFocus,
                primary: KeyChord(2048 /* CtrlCmd */ | 41 /* KEY_K */, 2048 /* CtrlCmd */ | 51 /* KEY_U */),
                weight: 100 /* EditorContrib */
            }
        }) || this;
    }
    return RemoveLineCommentAction;
}(CommentLineAction));
var BlockCommentAction = /** @class */ (function (_super) {
    __extends(BlockCommentAction, _super);
    function BlockCommentAction() {
        return _super.call(this, {
            id: 'editor.action.blockComment',
            label: nls.localize('comment.block', "Toggle Block Comment"),
            alias: 'Toggle Block Comment',
            precondition: EditorContextKeys.writable,
            kbOpts: {
                kbExpr: EditorContextKeys.editorTextFocus,
                primary: 1024 /* Shift */ | 512 /* Alt */ | 31 /* KEY_A */,
                linux: { primary: 2048 /* CtrlCmd */ | 1024 /* Shift */ | 31 /* KEY_A */ },
                weight: 100 /* EditorContrib */
            },
            menubarOpts: {
                menuId: 14 /* MenubarEditMenu */,
                group: '5_insert',
                title: nls.localize({ key: 'miToggleBlockComment', comment: ['&& denotes a mnemonic'] }, "Toggle &&Block Comment"),
                order: 2
            }
        }) || this;
    }
    BlockCommentAction.prototype.run = function (accessor, editor) {
        if (!editor.hasModel()) {
            return;
        }
        var commands = [];
        var selections = editor.getSelections();
        for (var _i = 0, selections_2 = selections; _i < selections_2.length; _i++) {
            var selection = selections_2[_i];
            commands.push(new BlockCommentCommand(selection));
        }
        editor.pushUndoStop();
        editor.executeCommands(this.id, commands);
        editor.pushUndoStop();
    };
    return BlockCommentAction;
}(EditorAction));
registerEditorAction(ToggleCommentLineAction);
registerEditorAction(AddLineCommentAction);
registerEditorAction(RemoveLineCommentAction);
registerEditorAction(BlockCommentAction);
