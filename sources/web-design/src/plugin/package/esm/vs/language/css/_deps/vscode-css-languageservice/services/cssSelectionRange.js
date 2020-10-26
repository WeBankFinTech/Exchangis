/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
'use strict';
import { Range } from '../../vscode-languageserver-types/main.js';
import { NodeType } from '../parser/cssNodes.js';
import { SelectionRangeKind } from '../cssLanguageTypes.js';
export function getSelectionRanges(document, positions, stylesheet) {
    function getSelectionRange(position) {
        var applicableRanges = getApplicableRanges(position);
        var ranges = applicableRanges.map(function (pair) {
            return {
                range: Range.create(document.positionAt(pair[0]), document.positionAt(pair[1])),
                kind: SelectionRangeKind.Statement
            };
        });
        return ranges;
    }
    return positions.map(getSelectionRange);
    function getApplicableRanges(position) {
        var currNode = stylesheet.findChildAtOffset(document.offsetAt(position), true);
        if (!currNode) {
            return [];
        }
        var result = [];
        while (currNode) {
            if (currNode.parent &&
                currNode.offset === currNode.parent.offset &&
                currNode.end === currNode.parent.end) {
                currNode = currNode.parent;
                continue;
            }
            if (currNode.type === NodeType.Declarations) {
                result.push([currNode.offset + 1, currNode.end - 1]);
            }
            else {
                result.push([currNode.offset, currNode.end]);
            }
            currNode = currNode.parent;
        }
        return result;
    }
}
