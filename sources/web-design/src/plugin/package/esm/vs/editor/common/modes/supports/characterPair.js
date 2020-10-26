/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
import { StandardAutoClosingPairConditional } from '../languageConfiguration.js';
var CharacterPairSupport = /** @class */ (function () {
    function CharacterPairSupport(config) {
        if (config.autoClosingPairs) {
            this._autoClosingPairs = config.autoClosingPairs.map(function (el) { return new StandardAutoClosingPairConditional(el); });
        }
        else if (config.brackets) {
            this._autoClosingPairs = config.brackets.map(function (b) { return new StandardAutoClosingPairConditional({ open: b[0], close: b[1] }); });
        }
        else {
            this._autoClosingPairs = [];
        }
        this._autoCloseBefore = typeof config.autoCloseBefore === 'string' ? config.autoCloseBefore : CharacterPairSupport.DEFAULT_AUTOCLOSE_BEFORE_LANGUAGE_DEFINED;
        this._surroundingPairs = config.surroundingPairs || this._autoClosingPairs;
    }
    CharacterPairSupport.prototype.getAutoClosingPairs = function () {
        return this._autoClosingPairs;
    };
    CharacterPairSupport.prototype.getAutoCloseBeforeSet = function () {
        return this._autoCloseBefore;
    };
    CharacterPairSupport.prototype.shouldAutoClosePair = function (character, context, column) {
        // Always complete on empty line
        if (context.getTokenCount() === 0) {
            return true;
        }
        var tokenIndex = context.findTokenIndexAtOffset(column - 2);
        var standardTokenType = context.getStandardTokenType(tokenIndex);
        for (var _i = 0, _a = this._autoClosingPairs; _i < _a.length; _i++) {
            var autoClosingPair = _a[_i];
            if (autoClosingPair.open === character) {
                return autoClosingPair.isOK(standardTokenType);
            }
        }
        return false;
    };
    CharacterPairSupport.prototype.getSurroundingPairs = function () {
        return this._surroundingPairs;
    };
    CharacterPairSupport.DEFAULT_AUTOCLOSE_BEFORE_LANGUAGE_DEFINED = ';:.,=}])> \n\t';
    return CharacterPairSupport;
}());
export { CharacterPairSupport };
