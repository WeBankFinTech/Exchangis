/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
// THIS IS A GENERATED FILE. DO NOT EDIT DIRECTLY.
export var MarkerTag;
(function (MarkerTag) {
    MarkerTag[MarkerTag["Unnecessary"] = 1] = "Unnecessary";
})(MarkerTag || (MarkerTag = {}));
export var MarkerSeverity;
(function (MarkerSeverity) {
    MarkerSeverity[MarkerSeverity["Hint"] = 1] = "Hint";
    MarkerSeverity[MarkerSeverity["Info"] = 2] = "Info";
    MarkerSeverity[MarkerSeverity["Warning"] = 4] = "Warning";
    MarkerSeverity[MarkerSeverity["Error"] = 8] = "Error";
})(MarkerSeverity || (MarkerSeverity = {}));
/**
 * Virtual Key Codes, the value does not hold any inherent meaning.
 * Inspired somewhat from https://msdn.microsoft.com/en-us/library/windows/desktop/dd375731(v=vs.85).aspx
 * But these are "more general", as they should work across browsers & OS`s.
 */
export var KeyCode;
(function (KeyCode) {
    /**
     * Placed first to cover the 0 value of the enum.
     */
    KeyCode[KeyCode["Unknown"] = 0] = "Unknown";
    KeyCode[KeyCode["Backspace"] = 1] = "Backspace";
    KeyCode[KeyCode["Tab"] = 2] = "Tab";
    KeyCode[KeyCode["Enter"] = 3] = "Enter";
    KeyCode[KeyCode["Shift"] = 4] = "Shift";
    KeyCode[KeyCode["Ctrl"] = 5] = "Ctrl";
    KeyCode[KeyCode["Alt"] = 6] = "Alt";
    KeyCode[KeyCode["PauseBreak"] = 7] = "PauseBreak";
    KeyCode[KeyCode["CapsLock"] = 8] = "CapsLock";
    KeyCode[KeyCode["Escape"] = 9] = "Escape";
    KeyCode[KeyCode["Space"] = 10] = "Space";
    KeyCode[KeyCode["PageUp"] = 11] = "PageUp";
    KeyCode[KeyCode["PageDown"] = 12] = "PageDown";
    KeyCode[KeyCode["End"] = 13] = "End";
    KeyCode[KeyCode["Home"] = 14] = "Home";
    KeyCode[KeyCode["LeftArrow"] = 15] = "LeftArrow";
    KeyCode[KeyCode["UpArrow"] = 16] = "UpArrow";
    KeyCode[KeyCode["RightArrow"] = 17] = "RightArrow";
    KeyCode[KeyCode["DownArrow"] = 18] = "DownArrow";
    KeyCode[KeyCode["Insert"] = 19] = "Insert";
    KeyCode[KeyCode["Delete"] = 20] = "Delete";
    KeyCode[KeyCode["KEY_0"] = 21] = "KEY_0";
    KeyCode[KeyCode["KEY_1"] = 22] = "KEY_1";
    KeyCode[KeyCode["KEY_2"] = 23] = "KEY_2";
    KeyCode[KeyCode["KEY_3"] = 24] = "KEY_3";
    KeyCode[KeyCode["KEY_4"] = 25] = "KEY_4";
    KeyCode[KeyCode["KEY_5"] = 26] = "KEY_5";
    KeyCode[KeyCode["KEY_6"] = 27] = "KEY_6";
    KeyCode[KeyCode["KEY_7"] = 28] = "KEY_7";
    KeyCode[KeyCode["KEY_8"] = 29] = "KEY_8";
    KeyCode[KeyCode["KEY_9"] = 30] = "KEY_9";
    KeyCode[KeyCode["KEY_A"] = 31] = "KEY_A";
    KeyCode[KeyCode["KEY_B"] = 32] = "KEY_B";
    KeyCode[KeyCode["KEY_C"] = 33] = "KEY_C";
    KeyCode[KeyCode["KEY_D"] = 34] = "KEY_D";
    KeyCode[KeyCode["KEY_E"] = 35] = "KEY_E";
    KeyCode[KeyCode["KEY_F"] = 36] = "KEY_F";
    KeyCode[KeyCode["KEY_G"] = 37] = "KEY_G";
    KeyCode[KeyCode["KEY_H"] = 38] = "KEY_H";
    KeyCode[KeyCode["KEY_I"] = 39] = "KEY_I";
    KeyCode[KeyCode["KEY_J"] = 40] = "KEY_J";
    KeyCode[KeyCode["KEY_K"] = 41] = "KEY_K";
    KeyCode[KeyCode["KEY_L"] = 42] = "KEY_L";
    KeyCode[KeyCode["KEY_M"] = 43] = "KEY_M";
    KeyCode[KeyCode["KEY_N"] = 44] = "KEY_N";
    KeyCode[KeyCode["KEY_O"] = 45] = "KEY_O";
    KeyCode[KeyCode["KEY_P"] = 46] = "KEY_P";
    KeyCode[KeyCode["KEY_Q"] = 47] = "KEY_Q";
    KeyCode[KeyCode["KEY_R"] = 48] = "KEY_R";
    KeyCode[KeyCode["KEY_S"] = 49] = "KEY_S";
    KeyCode[KeyCode["KEY_T"] = 50] = "KEY_T";
    KeyCode[KeyCode["KEY_U"] = 51] = "KEY_U";
    KeyCode[KeyCode["KEY_V"] = 52] = "KEY_V";
    KeyCode[KeyCode["KEY_W"] = 53] = "KEY_W";
    KeyCode[KeyCode["KEY_X"] = 54] = "KEY_X";
    KeyCode[KeyCode["KEY_Y"] = 55] = "KEY_Y";
    KeyCode[KeyCode["KEY_Z"] = 56] = "KEY_Z";
    KeyCode[KeyCode["Meta"] = 57] = "Meta";
    KeyCode[KeyCode["ContextMenu"] = 58] = "ContextMenu";
    KeyCode[KeyCode["F1"] = 59] = "F1";
    KeyCode[KeyCode["F2"] = 60] = "F2";
    KeyCode[KeyCode["F3"] = 61] = "F3";
    KeyCode[KeyCode["F4"] = 62] = "F4";
    KeyCode[KeyCode["F5"] = 63] = "F5";
    KeyCode[KeyCode["F6"] = 64] = "F6";
    KeyCode[KeyCode["F7"] = 65] = "F7";
    KeyCode[KeyCode["F8"] = 66] = "F8";
    KeyCode[KeyCode["F9"] = 67] = "F9";
    KeyCode[KeyCode["F10"] = 68] = "F10";
    KeyCode[KeyCode["F11"] = 69] = "F11";
    KeyCode[KeyCode["F12"] = 70] = "F12";
    KeyCode[KeyCode["F13"] = 71] = "F13";
    KeyCode[KeyCode["F14"] = 72] = "F14";
    KeyCode[KeyCode["F15"] = 73] = "F15";
    KeyCode[KeyCode["F16"] = 74] = "F16";
    KeyCode[KeyCode["F17"] = 75] = "F17";
    KeyCode[KeyCode["F18"] = 76] = "F18";
    KeyCode[KeyCode["F19"] = 77] = "F19";
    KeyCode[KeyCode["NumLock"] = 78] = "NumLock";
    KeyCode[KeyCode["ScrollLock"] = 79] = "ScrollLock";
    /**
     * Used for miscellaneous characters; it can vary by keyboard.
     * For the US standard keyboard, the ';:' key
     */
    KeyCode[KeyCode["US_SEMICOLON"] = 80] = "US_SEMICOLON";
    /**
     * For any country/region, the '+' key
     * For the US standard keyboard, the '=+' key
     */
    KeyCode[KeyCode["US_EQUAL"] = 81] = "US_EQUAL";
    /**
     * For any country/region, the ',' key
     * For the US standard keyboard, the ',<' key
     */
    KeyCode[KeyCode["US_COMMA"] = 82] = "US_COMMA";
    /**
     * For any country/region, the '-' key
     * For the US standard keyboard, the '-_' key
     */
    KeyCode[KeyCode["US_MINUS"] = 83] = "US_MINUS";
    /**
     * For any country/region, the '.' key
     * For the US standard keyboard, the '.>' key
     */
    KeyCode[KeyCode["US_DOT"] = 84] = "US_DOT";
    /**
     * Used for miscellaneous characters; it can vary by keyboard.
     * For the US standard keyboard, the '/?' key
     */
    KeyCode[KeyCode["US_SLASH"] = 85] = "US_SLASH";
    /**
     * Used for miscellaneous characters; it can vary by keyboard.
     * For the US standard keyboard, the '`~' key
     */
    KeyCode[KeyCode["US_BACKTICK"] = 86] = "US_BACKTICK";
    /**
     * Used for miscellaneous characters; it can vary by keyboard.
     * For the US standard keyboard, the '[{' key
     */
    KeyCode[KeyCode["US_OPEN_SQUARE_BRACKET"] = 87] = "US_OPEN_SQUARE_BRACKET";
    /**
     * Used for miscellaneous characters; it can vary by keyboard.
     * For the US standard keyboard, the '\|' key
     */
    KeyCode[KeyCode["US_BACKSLASH"] = 88] = "US_BACKSLASH";
    /**
     * Used for miscellaneous characters; it can vary by keyboard.
     * For the US standard keyboard, the ']}' key
     */
    KeyCode[KeyCode["US_CLOSE_SQUARE_BRACKET"] = 89] = "US_CLOSE_SQUARE_BRACKET";
    /**
     * Used for miscellaneous characters; it can vary by keyboard.
     * For the US standard keyboard, the ''"' key
     */
    KeyCode[KeyCode["US_QUOTE"] = 90] = "US_QUOTE";
    /**
     * Used for miscellaneous characters; it can vary by keyboard.
     */
    KeyCode[KeyCode["OEM_8"] = 91] = "OEM_8";
    /**
     * Either the angle bracket key or the backslash key on the RT 102-key keyboard.
     */
    KeyCode[KeyCode["OEM_102"] = 92] = "OEM_102";
    KeyCode[KeyCode["NUMPAD_0"] = 93] = "NUMPAD_0";
    KeyCode[KeyCode["NUMPAD_1"] = 94] = "NUMPAD_1";
    KeyCode[KeyCode["NUMPAD_2"] = 95] = "NUMPAD_2";
    KeyCode[KeyCode["NUMPAD_3"] = 96] = "NUMPAD_3";
    KeyCode[KeyCode["NUMPAD_4"] = 97] = "NUMPAD_4";
    KeyCode[KeyCode["NUMPAD_5"] = 98] = "NUMPAD_5";
    KeyCode[KeyCode["NUMPAD_6"] = 99] = "NUMPAD_6";
    KeyCode[KeyCode["NUMPAD_7"] = 100] = "NUMPAD_7";
    KeyCode[KeyCode["NUMPAD_8"] = 101] = "NUMPAD_8";
    KeyCode[KeyCode["NUMPAD_9"] = 102] = "NUMPAD_9";
    KeyCode[KeyCode["NUMPAD_MULTIPLY"] = 103] = "NUMPAD_MULTIPLY";
    KeyCode[KeyCode["NUMPAD_ADD"] = 104] = "NUMPAD_ADD";
    KeyCode[KeyCode["NUMPAD_SEPARATOR"] = 105] = "NUMPAD_SEPARATOR";
    KeyCode[KeyCode["NUMPAD_SUBTRACT"] = 106] = "NUMPAD_SUBTRACT";
    KeyCode[KeyCode["NUMPAD_DECIMAL"] = 107] = "NUMPAD_DECIMAL";
    KeyCode[KeyCode["NUMPAD_DIVIDE"] = 108] = "NUMPAD_DIVIDE";
    /**
     * Cover all key codes when IME is processing input.
     */
    KeyCode[KeyCode["KEY_IN_COMPOSITION"] = 109] = "KEY_IN_COMPOSITION";
    KeyCode[KeyCode["ABNT_C1"] = 110] = "ABNT_C1";
    KeyCode[KeyCode["ABNT_C2"] = 111] = "ABNT_C2";
    /**
     * Placed last to cover the length of the enum.
     * Please do not depend on this value!
     */
    KeyCode[KeyCode["MAX_VALUE"] = 112] = "MAX_VALUE";
})(KeyCode || (KeyCode = {}));
/**
 * The direction of a selection.
 */
export var SelectionDirection;
(function (SelectionDirection) {
    /**
     * The selection starts above where it ends.
     */
    SelectionDirection[SelectionDirection["LTR"] = 0] = "LTR";
    /**
     * The selection starts below where it ends.
     */
    SelectionDirection[SelectionDirection["RTL"] = 1] = "RTL";
})(SelectionDirection || (SelectionDirection = {}));
export var ScrollbarVisibility;
(function (ScrollbarVisibility) {
    ScrollbarVisibility[ScrollbarVisibility["Auto"] = 1] = "Auto";
    ScrollbarVisibility[ScrollbarVisibility["Hidden"] = 2] = "Hidden";
    ScrollbarVisibility[ScrollbarVisibility["Visible"] = 3] = "Visible";
})(ScrollbarVisibility || (ScrollbarVisibility = {}));
/**
 * Vertical Lane in the overview ruler of the editor.
 */
export var OverviewRulerLane;
(function (OverviewRulerLane) {
    OverviewRulerLane[OverviewRulerLane["Left"] = 1] = "Left";
    OverviewRulerLane[OverviewRulerLane["Center"] = 2] = "Center";
    OverviewRulerLane[OverviewRulerLane["Right"] = 4] = "Right";
    OverviewRulerLane[OverviewRulerLane["Full"] = 7] = "Full";
})(OverviewRulerLane || (OverviewRulerLane = {}));
/**
 * End of line character preference.
 */
export var EndOfLinePreference;
(function (EndOfLinePreference) {
    /**
     * Use the end of line character identified in the text buffer.
     */
    EndOfLinePreference[EndOfLinePreference["TextDefined"] = 0] = "TextDefined";
    /**
     * Use line feed (\n) as the end of line character.
     */
    EndOfLinePreference[EndOfLinePreference["LF"] = 1] = "LF";
    /**
     * Use carriage return and line feed (\r\n) as the end of line character.
     */
    EndOfLinePreference[EndOfLinePreference["CRLF"] = 2] = "CRLF";
})(EndOfLinePreference || (EndOfLinePreference = {}));
/**
 * The default end of line to use when instantiating models.
 */
export var DefaultEndOfLine;
(function (DefaultEndOfLine) {
    /**
     * Use line feed (\n) as the end of line character.
     */
    DefaultEndOfLine[DefaultEndOfLine["LF"] = 1] = "LF";
    /**
     * Use carriage return and line feed (\r\n) as the end of line character.
     */
    DefaultEndOfLine[DefaultEndOfLine["CRLF"] = 2] = "CRLF";
})(DefaultEndOfLine || (DefaultEndOfLine = {}));
/**
 * End of line character preference.
 */
export var EndOfLineSequence;
(function (EndOfLineSequence) {
    /**
     * Use line feed (\n) as the end of line character.
     */
    EndOfLineSequence[EndOfLineSequence["LF"] = 0] = "LF";
    /**
     * Use carriage return and line feed (\r\n) as the end of line character.
     */
    EndOfLineSequence[EndOfLineSequence["CRLF"] = 1] = "CRLF";
})(EndOfLineSequence || (EndOfLineSequence = {}));
/**
 * Describes the behavior of decorations when typing/editing near their edges.
 * Note: Please do not edit the values, as they very carefully match `DecorationRangeBehavior`
 */
export var TrackedRangeStickiness;
(function (TrackedRangeStickiness) {
    TrackedRangeStickiness[TrackedRangeStickiness["AlwaysGrowsWhenTypingAtEdges"] = 0] = "AlwaysGrowsWhenTypingAtEdges";
    TrackedRangeStickiness[TrackedRangeStickiness["NeverGrowsWhenTypingAtEdges"] = 1] = "NeverGrowsWhenTypingAtEdges";
    TrackedRangeStickiness[TrackedRangeStickiness["GrowsOnlyWhenTypingBefore"] = 2] = "GrowsOnlyWhenTypingBefore";
    TrackedRangeStickiness[TrackedRangeStickiness["GrowsOnlyWhenTypingAfter"] = 3] = "GrowsOnlyWhenTypingAfter";
})(TrackedRangeStickiness || (TrackedRangeStickiness = {}));
export var ScrollType;
(function (ScrollType) {
    ScrollType[ScrollType["Smooth"] = 0] = "Smooth";
    ScrollType[ScrollType["Immediate"] = 1] = "Immediate";
})(ScrollType || (ScrollType = {}));
/**
 * Describes the reason the cursor has changed its position.
 */
export var CursorChangeReason;
(function (CursorChangeReason) {
    /**
     * Unknown or not set.
     */
    CursorChangeReason[CursorChangeReason["NotSet"] = 0] = "NotSet";
    /**
     * A `model.setValue()` was called.
     */
    CursorChangeReason[CursorChangeReason["ContentFlush"] = 1] = "ContentFlush";
    /**
     * The `model` has been changed outside of this cursor and the cursor recovers its position from associated markers.
     */
    CursorChangeReason[CursorChangeReason["RecoverFromMarkers"] = 2] = "RecoverFromMarkers";
    /**
     * There was an explicit user gesture.
     */
    CursorChangeReason[CursorChangeReason["Explicit"] = 3] = "Explicit";
    /**
     * There was a Paste.
     */
    CursorChangeReason[CursorChangeReason["Paste"] = 4] = "Paste";
    /**
     * There was an Undo.
     */
    CursorChangeReason[CursorChangeReason["Undo"] = 5] = "Undo";
    /**
     * There was a Redo.
     */
    CursorChangeReason[CursorChangeReason["Redo"] = 6] = "Redo";
})(CursorChangeReason || (CursorChangeReason = {}));
export var RenderMinimap;
(function (RenderMinimap) {
    RenderMinimap[RenderMinimap["None"] = 0] = "None";
    RenderMinimap[RenderMinimap["Small"] = 1] = "Small";
    RenderMinimap[RenderMinimap["Large"] = 2] = "Large";
    RenderMinimap[RenderMinimap["SmallBlocks"] = 3] = "SmallBlocks";
    RenderMinimap[RenderMinimap["LargeBlocks"] = 4] = "LargeBlocks";
})(RenderMinimap || (RenderMinimap = {}));
/**
 * Describes how to indent wrapped lines.
 */
export var WrappingIndent;
(function (WrappingIndent) {
    /**
     * No indentation => wrapped lines begin at column 1.
     */
    WrappingIndent[WrappingIndent["None"] = 0] = "None";
    /**
     * Same => wrapped lines get the same indentation as the parent.
     */
    WrappingIndent[WrappingIndent["Same"] = 1] = "Same";
    /**
     * Indent => wrapped lines get +1 indentation toward the parent.
     */
    WrappingIndent[WrappingIndent["Indent"] = 2] = "Indent";
    /**
     * DeepIndent => wrapped lines get +2 indentation toward the parent.
     */
    WrappingIndent[WrappingIndent["DeepIndent"] = 3] = "DeepIndent";
})(WrappingIndent || (WrappingIndent = {}));
/**
 * The kind of animation in which the editor's cursor should be rendered.
 */
export var TextEditorCursorBlinkingStyle;
(function (TextEditorCursorBlinkingStyle) {
    /**
     * Hidden
     */
    TextEditorCursorBlinkingStyle[TextEditorCursorBlinkingStyle["Hidden"] = 0] = "Hidden";
    /**
     * Blinking
     */
    TextEditorCursorBlinkingStyle[TextEditorCursorBlinkingStyle["Blink"] = 1] = "Blink";
    /**
     * Blinking with smooth fading
     */
    TextEditorCursorBlinkingStyle[TextEditorCursorBlinkingStyle["Smooth"] = 2] = "Smooth";
    /**
     * Blinking with prolonged filled state and smooth fading
     */
    TextEditorCursorBlinkingStyle[TextEditorCursorBlinkingStyle["Phase"] = 3] = "Phase";
    /**
     * Expand collapse animation on the y axis
     */
    TextEditorCursorBlinkingStyle[TextEditorCursorBlinkingStyle["Expand"] = 4] = "Expand";
    /**
     * No-Blinking
     */
    TextEditorCursorBlinkingStyle[TextEditorCursorBlinkingStyle["Solid"] = 5] = "Solid";
})(TextEditorCursorBlinkingStyle || (TextEditorCursorBlinkingStyle = {}));
/**
 * The style in which the editor's cursor should be rendered.
 */
export var TextEditorCursorStyle;
(function (TextEditorCursorStyle) {
    /**
     * As a vertical line (sitting between two characters).
     */
    TextEditorCursorStyle[TextEditorCursorStyle["Line"] = 1] = "Line";
    /**
     * As a block (sitting on top of a character).
     */
    TextEditorCursorStyle[TextEditorCursorStyle["Block"] = 2] = "Block";
    /**
     * As a horizontal line (sitting under a character).
     */
    TextEditorCursorStyle[TextEditorCursorStyle["Underline"] = 3] = "Underline";
    /**
     * As a thin vertical line (sitting between two characters).
     */
    TextEditorCursorStyle[TextEditorCursorStyle["LineThin"] = 4] = "LineThin";
    /**
     * As an outlined block (sitting on top of a character).
     */
    TextEditorCursorStyle[TextEditorCursorStyle["BlockOutline"] = 5] = "BlockOutline";
    /**
     * As a thin horizontal line (sitting under a character).
     */
    TextEditorCursorStyle[TextEditorCursorStyle["UnderlineThin"] = 6] = "UnderlineThin";
})(TextEditorCursorStyle || (TextEditorCursorStyle = {}));
export var RenderLineNumbersType;
(function (RenderLineNumbersType) {
    RenderLineNumbersType[RenderLineNumbersType["Off"] = 0] = "Off";
    RenderLineNumbersType[RenderLineNumbersType["On"] = 1] = "On";
    RenderLineNumbersType[RenderLineNumbersType["Relative"] = 2] = "Relative";
    RenderLineNumbersType[RenderLineNumbersType["Interval"] = 3] = "Interval";
    RenderLineNumbersType[RenderLineNumbersType["Custom"] = 4] = "Custom";
})(RenderLineNumbersType || (RenderLineNumbersType = {}));
/**
 * A positioning preference for rendering content widgets.
 */
export var ContentWidgetPositionPreference;
(function (ContentWidgetPositionPreference) {
    /**
     * Place the content widget exactly at a position
     */
    ContentWidgetPositionPreference[ContentWidgetPositionPreference["EXACT"] = 0] = "EXACT";
    /**
     * Place the content widget above a position
     */
    ContentWidgetPositionPreference[ContentWidgetPositionPreference["ABOVE"] = 1] = "ABOVE";
    /**
     * Place the content widget below a position
     */
    ContentWidgetPositionPreference[ContentWidgetPositionPreference["BELOW"] = 2] = "BELOW";
})(ContentWidgetPositionPreference || (ContentWidgetPositionPreference = {}));
/**
 * A positioning preference for rendering overlay widgets.
 */
export var OverlayWidgetPositionPreference;
(function (OverlayWidgetPositionPreference) {
    /**
     * Position the overlay widget in the top right corner
     */
    OverlayWidgetPositionPreference[OverlayWidgetPositionPreference["TOP_RIGHT_CORNER"] = 0] = "TOP_RIGHT_CORNER";
    /**
     * Position the overlay widget in the bottom right corner
     */
    OverlayWidgetPositionPreference[OverlayWidgetPositionPreference["BOTTOM_RIGHT_CORNER"] = 1] = "BOTTOM_RIGHT_CORNER";
    /**
     * Position the overlay widget in the top center
     */
    OverlayWidgetPositionPreference[OverlayWidgetPositionPreference["TOP_CENTER"] = 2] = "TOP_CENTER";
})(OverlayWidgetPositionPreference || (OverlayWidgetPositionPreference = {}));
/**
 * Type of hit element with the mouse in the editor.
 */
export var MouseTargetType;
(function (MouseTargetType) {
    /**
     * Mouse is on top of an unknown element.
     */
    MouseTargetType[MouseTargetType["UNKNOWN"] = 0] = "UNKNOWN";
    /**
     * Mouse is on top of the textarea used for input.
     */
    MouseTargetType[MouseTargetType["TEXTAREA"] = 1] = "TEXTAREA";
    /**
     * Mouse is on top of the glyph margin
     */
    MouseTargetType[MouseTargetType["GUTTER_GLYPH_MARGIN"] = 2] = "GUTTER_GLYPH_MARGIN";
    /**
     * Mouse is on top of the line numbers
     */
    MouseTargetType[MouseTargetType["GUTTER_LINE_NUMBERS"] = 3] = "GUTTER_LINE_NUMBERS";
    /**
     * Mouse is on top of the line decorations
     */
    MouseTargetType[MouseTargetType["GUTTER_LINE_DECORATIONS"] = 4] = "GUTTER_LINE_DECORATIONS";
    /**
     * Mouse is on top of the whitespace left in the gutter by a view zone.
     */
    MouseTargetType[MouseTargetType["GUTTER_VIEW_ZONE"] = 5] = "GUTTER_VIEW_ZONE";
    /**
     * Mouse is on top of text in the content.
     */
    MouseTargetType[MouseTargetType["CONTENT_TEXT"] = 6] = "CONTENT_TEXT";
    /**
     * Mouse is on top of empty space in the content (e.g. after line text or below last line)
     */
    MouseTargetType[MouseTargetType["CONTENT_EMPTY"] = 7] = "CONTENT_EMPTY";
    /**
     * Mouse is on top of a view zone in the content.
     */
    MouseTargetType[MouseTargetType["CONTENT_VIEW_ZONE"] = 8] = "CONTENT_VIEW_ZONE";
    /**
     * Mouse is on top of a content widget.
     */
    MouseTargetType[MouseTargetType["CONTENT_WIDGET"] = 9] = "CONTENT_WIDGET";
    /**
     * Mouse is on top of the decorations overview ruler.
     */
    MouseTargetType[MouseTargetType["OVERVIEW_RULER"] = 10] = "OVERVIEW_RULER";
    /**
     * Mouse is on top of a scrollbar.
     */
    MouseTargetType[MouseTargetType["SCROLLBAR"] = 11] = "SCROLLBAR";
    /**
     * Mouse is on top of an overlay widget.
     */
    MouseTargetType[MouseTargetType["OVERLAY_WIDGET"] = 12] = "OVERLAY_WIDGET";
    /**
     * Mouse is outside of the editor.
     */
    MouseTargetType[MouseTargetType["OUTSIDE_EDITOR"] = 13] = "OUTSIDE_EDITOR";
})(MouseTargetType || (MouseTargetType = {}));
/**
 * Describes what to do with the indentation when pressing Enter.
 */
export var IndentAction;
(function (IndentAction) {
    /**
     * Insert new line and copy the previous line's indentation.
     */
    IndentAction[IndentAction["None"] = 0] = "None";
    /**
     * Insert new line and indent once (relative to the previous line's indentation).
     */
    IndentAction[IndentAction["Indent"] = 1] = "Indent";
    /**
     * Insert two new lines:
     *  - the first one indented which will hold the cursor
     *  - the second one at the same indentation level
     */
    IndentAction[IndentAction["IndentOutdent"] = 2] = "IndentOutdent";
    /**
     * Insert new line and outdent once (relative to the previous line's indentation).
     */
    IndentAction[IndentAction["Outdent"] = 3] = "Outdent";
})(IndentAction || (IndentAction = {}));
export var CompletionItemKind;
(function (CompletionItemKind) {
    CompletionItemKind[CompletionItemKind["Method"] = 0] = "Method";
    CompletionItemKind[CompletionItemKind["Function"] = 1] = "Function";
    CompletionItemKind[CompletionItemKind["Constructor"] = 2] = "Constructor";
    CompletionItemKind[CompletionItemKind["Field"] = 3] = "Field";
    CompletionItemKind[CompletionItemKind["Variable"] = 4] = "Variable";
    CompletionItemKind[CompletionItemKind["Class"] = 5] = "Class";
    CompletionItemKind[CompletionItemKind["Struct"] = 6] = "Struct";
    CompletionItemKind[CompletionItemKind["Interface"] = 7] = "Interface";
    CompletionItemKind[CompletionItemKind["Module"] = 8] = "Module";
    CompletionItemKind[CompletionItemKind["Property"] = 9] = "Property";
    CompletionItemKind[CompletionItemKind["Event"] = 10] = "Event";
    CompletionItemKind[CompletionItemKind["Operator"] = 11] = "Operator";
    CompletionItemKind[CompletionItemKind["Unit"] = 12] = "Unit";
    CompletionItemKind[CompletionItemKind["Value"] = 13] = "Value";
    CompletionItemKind[CompletionItemKind["Constant"] = 14] = "Constant";
    CompletionItemKind[CompletionItemKind["Enum"] = 15] = "Enum";
    CompletionItemKind[CompletionItemKind["EnumMember"] = 16] = "EnumMember";
    CompletionItemKind[CompletionItemKind["Keyword"] = 17] = "Keyword";
    CompletionItemKind[CompletionItemKind["Text"] = 18] = "Text";
    CompletionItemKind[CompletionItemKind["Color"] = 19] = "Color";
    CompletionItemKind[CompletionItemKind["File"] = 20] = "File";
    CompletionItemKind[CompletionItemKind["Reference"] = 21] = "Reference";
    CompletionItemKind[CompletionItemKind["Customcolor"] = 22] = "Customcolor";
    CompletionItemKind[CompletionItemKind["Folder"] = 23] = "Folder";
    CompletionItemKind[CompletionItemKind["TypeParameter"] = 24] = "TypeParameter";
    CompletionItemKind[CompletionItemKind["Snippet"] = 25] = "Snippet";
})(CompletionItemKind || (CompletionItemKind = {}));
export var CompletionItemInsertTextRule;
(function (CompletionItemInsertTextRule) {
    /**
     * Adjust whitespace/indentation of multiline insert texts to
     * match the current line indentation.
     */
    CompletionItemInsertTextRule[CompletionItemInsertTextRule["KeepWhitespace"] = 1] = "KeepWhitespace";
    /**
     * `insertText` is a snippet.
     */
    CompletionItemInsertTextRule[CompletionItemInsertTextRule["InsertAsSnippet"] = 4] = "InsertAsSnippet";
})(CompletionItemInsertTextRule || (CompletionItemInsertTextRule = {}));
/**
 * How a suggest provider was triggered.
 */
export var CompletionTriggerKind;
(function (CompletionTriggerKind) {
    CompletionTriggerKind[CompletionTriggerKind["Invoke"] = 0] = "Invoke";
    CompletionTriggerKind[CompletionTriggerKind["TriggerCharacter"] = 1] = "TriggerCharacter";
    CompletionTriggerKind[CompletionTriggerKind["TriggerForIncompleteCompletions"] = 2] = "TriggerForIncompleteCompletions";
})(CompletionTriggerKind || (CompletionTriggerKind = {}));
export var SignatureHelpTriggerKind;
(function (SignatureHelpTriggerKind) {
    SignatureHelpTriggerKind[SignatureHelpTriggerKind["Invoke"] = 1] = "Invoke";
    SignatureHelpTriggerKind[SignatureHelpTriggerKind["TriggerCharacter"] = 2] = "TriggerCharacter";
    SignatureHelpTriggerKind[SignatureHelpTriggerKind["ContentChange"] = 3] = "ContentChange";
})(SignatureHelpTriggerKind || (SignatureHelpTriggerKind = {}));
/**
 * A document highlight kind.
 */
export var DocumentHighlightKind;
(function (DocumentHighlightKind) {
    /**
     * A textual occurrence.
     */
    DocumentHighlightKind[DocumentHighlightKind["Text"] = 0] = "Text";
    /**
     * Read-access of a symbol, like reading a variable.
     */
    DocumentHighlightKind[DocumentHighlightKind["Read"] = 1] = "Read";
    /**
     * Write-access of a symbol, like writing to a variable.
     */
    DocumentHighlightKind[DocumentHighlightKind["Write"] = 2] = "Write";
})(DocumentHighlightKind || (DocumentHighlightKind = {}));
/**
 * A symbol kind.
 */
export var SymbolKind;
(function (SymbolKind) {
    SymbolKind[SymbolKind["File"] = 0] = "File";
    SymbolKind[SymbolKind["Module"] = 1] = "Module";
    SymbolKind[SymbolKind["Namespace"] = 2] = "Namespace";
    SymbolKind[SymbolKind["Package"] = 3] = "Package";
    SymbolKind[SymbolKind["Class"] = 4] = "Class";
    SymbolKind[SymbolKind["Method"] = 5] = "Method";
    SymbolKind[SymbolKind["Property"] = 6] = "Property";
    SymbolKind[SymbolKind["Field"] = 7] = "Field";
    SymbolKind[SymbolKind["Constructor"] = 8] = "Constructor";
    SymbolKind[SymbolKind["Enum"] = 9] = "Enum";
    SymbolKind[SymbolKind["Interface"] = 10] = "Interface";
    SymbolKind[SymbolKind["Function"] = 11] = "Function";
    SymbolKind[SymbolKind["Variable"] = 12] = "Variable";
    SymbolKind[SymbolKind["Constant"] = 13] = "Constant";
    SymbolKind[SymbolKind["String"] = 14] = "String";
    SymbolKind[SymbolKind["Number"] = 15] = "Number";
    SymbolKind[SymbolKind["Boolean"] = 16] = "Boolean";
    SymbolKind[SymbolKind["Array"] = 17] = "Array";
    SymbolKind[SymbolKind["Object"] = 18] = "Object";
    SymbolKind[SymbolKind["Key"] = 19] = "Key";
    SymbolKind[SymbolKind["Null"] = 20] = "Null";
    SymbolKind[SymbolKind["EnumMember"] = 21] = "EnumMember";
    SymbolKind[SymbolKind["Struct"] = 22] = "Struct";
    SymbolKind[SymbolKind["Event"] = 23] = "Event";
    SymbolKind[SymbolKind["Operator"] = 24] = "Operator";
    SymbolKind[SymbolKind["TypeParameter"] = 25] = "TypeParameter";
})(SymbolKind || (SymbolKind = {}));
