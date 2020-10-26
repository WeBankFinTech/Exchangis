/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
import './dnd.css';
import { dispose } from '../../../base/common/lifecycle.js';
import { isMacintosh } from '../../../base/common/platform.js';
import { registerEditorContribution } from '../../browser/editorExtensions.js';
import { Position } from '../../common/core/position.js';
import { Range } from '../../common/core/range.js';
import { Selection } from '../../common/core/selection.js';
import { DragAndDropCommand } from './dragAndDropCommand.js';
import { ModelDecorationOptions } from '../../common/model/textModel.js';
function hasTriggerModifier(e) {
    if (isMacintosh) {
        return e.altKey;
    }
    else {
        return e.ctrlKey;
    }
}
var DragAndDropController = /** @class */ (function () {
    function DragAndDropController(editor) {
        var _this = this;
        this._editor = editor;
        this._toUnhook = [];
        this._toUnhook.push(this._editor.onMouseDown(function (e) { return _this._onEditorMouseDown(e); }));
        this._toUnhook.push(this._editor.onMouseUp(function (e) { return _this._onEditorMouseUp(e); }));
        this._toUnhook.push(this._editor.onMouseDrag(function (e) { return _this._onEditorMouseDrag(e); }));
        this._toUnhook.push(this._editor.onMouseDrop(function (e) { return _this._onEditorMouseDrop(e); }));
        this._toUnhook.push(this._editor.onKeyDown(function (e) { return _this.onEditorKeyDown(e); }));
        this._toUnhook.push(this._editor.onKeyUp(function (e) { return _this.onEditorKeyUp(e); }));
        this._toUnhook.push(this._editor.onDidBlurEditorWidget(function () { return _this.onEditorBlur(); }));
        this._dndDecorationIds = [];
        this._mouseDown = false;
        this._modifierPressed = false;
        this._dragSelection = null;
    }
    DragAndDropController.prototype.onEditorBlur = function () {
        this._removeDecoration();
        this._dragSelection = null;
        this._mouseDown = false;
        this._modifierPressed = false;
    };
    DragAndDropController.prototype.onEditorKeyDown = function (e) {
        if (!this._editor.getConfiguration().dragAndDrop) {
            return;
        }
        if (hasTriggerModifier(e)) {
            this._modifierPressed = true;
        }
        if (this._mouseDown && hasTriggerModifier(e)) {
            this._editor.updateOptions({
                mouseStyle: 'copy'
            });
        }
    };
    DragAndDropController.prototype.onEditorKeyUp = function (e) {
        if (!this._editor.getConfiguration().dragAndDrop) {
            return;
        }
        if (hasTriggerModifier(e)) {
            this._modifierPressed = false;
        }
        if (this._mouseDown && e.keyCode === DragAndDropController.TRIGGER_KEY_VALUE) {
            this._editor.updateOptions({
                mouseStyle: 'default'
            });
        }
    };
    DragAndDropController.prototype._onEditorMouseDown = function (mouseEvent) {
        this._mouseDown = true;
    };
    DragAndDropController.prototype._onEditorMouseUp = function (mouseEvent) {
        this._mouseDown = false;
        // Whenever users release the mouse, the drag and drop operation should finish and the cursor should revert to text.
        this._editor.updateOptions({
            mouseStyle: 'text'
        });
    };
    DragAndDropController.prototype._onEditorMouseDrag = function (mouseEvent) {
        var target = mouseEvent.target;
        if (this._dragSelection === null) {
            var selections = this._editor.getSelections() || [];
            var possibleSelections = selections.filter(function (selection) { return target.position && selection.containsPosition(target.position); });
            if (possibleSelections.length === 1) {
                this._dragSelection = possibleSelections[0];
            }
            else {
                return;
            }
        }
        if (hasTriggerModifier(mouseEvent.event)) {
            this._editor.updateOptions({
                mouseStyle: 'copy'
            });
        }
        else {
            this._editor.updateOptions({
                mouseStyle: 'default'
            });
        }
        if (target.position) {
            if (this._dragSelection.containsPosition(target.position)) {
                this._removeDecoration();
            }
            else {
                this.showAt(target.position);
            }
        }
    };
    DragAndDropController.prototype._onEditorMouseDrop = function (mouseEvent) {
        if (mouseEvent.target && (this._hitContent(mouseEvent.target) || this._hitMargin(mouseEvent.target)) && mouseEvent.target.position) {
            var newCursorPosition_1 = new Position(mouseEvent.target.position.lineNumber, mouseEvent.target.position.column);
            if (this._dragSelection === null) {
                var newSelections = null;
                if (mouseEvent.event.shiftKey) {
                    var primarySelection = this._editor.getSelection();
                    if (primarySelection) {
                        var selectionStartLineNumber = primarySelection.selectionStartLineNumber, selectionStartColumn = primarySelection.selectionStartColumn;
                        newSelections = [new Selection(selectionStartLineNumber, selectionStartColumn, newCursorPosition_1.lineNumber, newCursorPosition_1.column)];
                    }
                }
                else {
                    newSelections = (this._editor.getSelections() || []).map(function (selection) {
                        if (selection.containsPosition(newCursorPosition_1)) {
                            return new Selection(newCursorPosition_1.lineNumber, newCursorPosition_1.column, newCursorPosition_1.lineNumber, newCursorPosition_1.column);
                        }
                        else {
                            return selection;
                        }
                    });
                }
                // Use `mouse` as the source instead of `api`.
                this._editor.setSelections(newSelections || [], 'mouse');
            }
            else if (!this._dragSelection.containsPosition(newCursorPosition_1) ||
                ((hasTriggerModifier(mouseEvent.event) ||
                    this._modifierPressed) && (this._dragSelection.getEndPosition().equals(newCursorPosition_1) || this._dragSelection.getStartPosition().equals(newCursorPosition_1)) // we allow users to paste content beside the selection
                )) {
                this._editor.pushUndoStop();
                this._editor.executeCommand(DragAndDropController.ID, new DragAndDropCommand(this._dragSelection, newCursorPosition_1, hasTriggerModifier(mouseEvent.event) || this._modifierPressed));
                this._editor.pushUndoStop();
            }
        }
        this._editor.updateOptions({
            mouseStyle: 'text'
        });
        this._removeDecoration();
        this._dragSelection = null;
        this._mouseDown = false;
    };
    DragAndDropController.prototype.showAt = function (position) {
        var newDecorations = [{
                range: new Range(position.lineNumber, position.column, position.lineNumber, position.column),
                options: DragAndDropController._DECORATION_OPTIONS
            }];
        this._dndDecorationIds = this._editor.deltaDecorations(this._dndDecorationIds, newDecorations);
        this._editor.revealPosition(position, 1 /* Immediate */);
    };
    DragAndDropController.prototype._removeDecoration = function () {
        this._dndDecorationIds = this._editor.deltaDecorations(this._dndDecorationIds, []);
    };
    DragAndDropController.prototype._hitContent = function (target) {
        return target.type === 6 /* CONTENT_TEXT */ ||
            target.type === 7 /* CONTENT_EMPTY */;
    };
    DragAndDropController.prototype._hitMargin = function (target) {
        return target.type === 2 /* GUTTER_GLYPH_MARGIN */ ||
            target.type === 3 /* GUTTER_LINE_NUMBERS */ ||
            target.type === 4 /* GUTTER_LINE_DECORATIONS */;
    };
    DragAndDropController.prototype.getId = function () {
        return DragAndDropController.ID;
    };
    DragAndDropController.prototype.dispose = function () {
        this._removeDecoration();
        this._dragSelection = null;
        this._mouseDown = false;
        this._modifierPressed = false;
        this._toUnhook = dispose(this._toUnhook);
    };
    DragAndDropController.ID = 'editor.contrib.dragAndDrop';
    DragAndDropController.TRIGGER_KEY_VALUE = isMacintosh ? 6 /* Alt */ : 5 /* Ctrl */;
    DragAndDropController._DECORATION_OPTIONS = ModelDecorationOptions.register({
        className: 'dnd-target'
    });
    return DragAndDropController;
}());
export { DragAndDropController };
registerEditorContribution(DragAndDropController);
