/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
'use strict';
import { Range, TextEdit, Position } from '../vscode-languageserver-types/main.js';
export { Range, TextEdit, Position };
// #region Proposed types, remove once added to vscode-languageserver-types
/**
 * Enum of known selection range kinds
 */
export var SelectionRangeKind;
(function (SelectionRangeKind) {
    /**
     * Empty Kind.
     */
    SelectionRangeKind["Empty"] = "";
    /**
     * The statment kind, its value is `statement`, possible extensions can be
     * `statement.if` etc
     */
    SelectionRangeKind["Statement"] = "statement";
    /**
     * The declaration kind, its value is `declaration`, possible extensions can be
     * `declaration.function`, `declaration.class` etc.
     */
    SelectionRangeKind["Declaration"] = "declaration";
})(SelectionRangeKind || (SelectionRangeKind = {}));
