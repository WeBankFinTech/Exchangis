/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
import { FoldingRegions, MAX_LINE_NUMBER } from './foldingRanges.js';
import { TextModel } from '../../common/model/textModel.js';
import { LanguageConfigurationRegistry } from '../../common/modes/languageConfigurationRegistry.js';
var MAX_FOLDING_REGIONS_FOR_INDENT_LIMIT = 5000;
export var ID_INDENT_PROVIDER = 'indent';
var IndentRangeProvider = /** @class */ (function () {
    function IndentRangeProvider(editorModel) {
        this.editorModel = editorModel;
        this.id = ID_INDENT_PROVIDER;
    }
    IndentRangeProvider.prototype.dispose = function () {
    };
    IndentRangeProvider.prototype.compute = function (cancelationToken) {
        var foldingRules = LanguageConfigurationRegistry.getFoldingRules(this.editorModel.getLanguageIdentifier().id);
        var offSide = foldingRules && !!foldingRules.offSide;
        var markers = foldingRules && foldingRules.markers;
        return Promise.resolve(computeRanges(this.editorModel, offSide, markers));
    };
    return IndentRangeProvider;
}());
export { IndentRangeProvider };
// public only for testing
var RangesCollector = /** @class */ (function () {
    function RangesCollector(foldingRangesLimit) {
        this._startIndexes = [];
        this._endIndexes = [];
        this._indentOccurrences = [];
        this._length = 0;
        this._foldingRangesLimit = foldingRangesLimit;
    }
    RangesCollector.prototype.insertFirst = function (startLineNumber, endLineNumber, indent) {
        if (startLineNumber > MAX_LINE_NUMBER || endLineNumber > MAX_LINE_NUMBER) {
            return;
        }
        var index = this._length;
        this._startIndexes[index] = startLineNumber;
        this._endIndexes[index] = endLineNumber;
        this._length++;
        if (indent < 1000) {
            this._indentOccurrences[indent] = (this._indentOccurrences[indent] || 0) + 1;
        }
    };
    RangesCollector.prototype.toIndentRanges = function (model) {
        if (this._length <= this._foldingRangesLimit) {
            // reverse and create arrays of the exact length
            var startIndexes = new Uint32Array(this._length);
            var endIndexes = new Uint32Array(this._length);
            for (var i = this._length - 1, k = 0; i >= 0; i--, k++) {
                startIndexes[k] = this._startIndexes[i];
                endIndexes[k] = this._endIndexes[i];
            }
            return new FoldingRegions(startIndexes, endIndexes);
        }
        else {
            var entries = 0;
            var maxIndent = this._indentOccurrences.length;
            for (var i = 0; i < this._indentOccurrences.length; i++) {
                var n = this._indentOccurrences[i];
                if (n) {
                    if (n + entries > this._foldingRangesLimit) {
                        maxIndent = i;
                        break;
                    }
                    entries += n;
                }
            }
            var tabSize = model.getOptions().tabSize;
            // reverse and create arrays of the exact length
            var startIndexes = new Uint32Array(this._foldingRangesLimit);
            var endIndexes = new Uint32Array(this._foldingRangesLimit);
            for (var i = this._length - 1, k = 0; i >= 0; i--) {
                var startIndex = this._startIndexes[i];
                var lineContent = model.getLineContent(startIndex);
                var indent = TextModel.computeIndentLevel(lineContent, tabSize);
                if (indent < maxIndent || (indent === maxIndent && entries++ < this._foldingRangesLimit)) {
                    startIndexes[k] = startIndex;
                    endIndexes[k] = this._endIndexes[i];
                    k++;
                }
            }
            return new FoldingRegions(startIndexes, endIndexes);
        }
    };
    return RangesCollector;
}());
export { RangesCollector };
export function computeRanges(model, offSide, markers, foldingRangesLimit) {
    if (foldingRangesLimit === void 0) { foldingRangesLimit = MAX_FOLDING_REGIONS_FOR_INDENT_LIMIT; }
    var tabSize = model.getOptions().tabSize;
    var result = new RangesCollector(foldingRangesLimit);
    var pattern = undefined;
    if (markers) {
        pattern = new RegExp("(" + markers.start.source + ")|(?:" + markers.end.source + ")");
    }
    var previousRegions = [];
    previousRegions.push({ indent: -1, line: model.getLineCount() + 1, marker: false }); // sentinel, to make sure there's at least one entry
    for (var line = model.getLineCount(); line > 0; line--) {
        var lineContent = model.getLineContent(line);
        var indent = TextModel.computeIndentLevel(lineContent, tabSize);
        var previous = previousRegions[previousRegions.length - 1];
        if (indent === -1) {
            if (offSide && !previous.marker) {
                // for offSide languages, empty lines are associated to the next block
                previous.line = line;
            }
            continue; // only whitespace
        }
        var m = void 0;
        if (pattern && (m = lineContent.match(pattern))) {
            // folding pattern match
            if (m[1]) { // start pattern match
                // discard all regions until the folding pattern
                var i = previousRegions.length - 1;
                while (i > 0 && !previousRegions[i].marker) {
                    i--;
                }
                if (i > 0) {
                    previousRegions.length = i + 1;
                    previous = previousRegions[i];
                    // new folding range from pattern, includes the end line
                    result.insertFirst(line, previous.line, indent);
                    previous.marker = false;
                    previous.indent = indent;
                    previous.line = line;
                    continue;
                }
                else {
                    // no end marker found, treat line as a regular line
                }
            }
            else { // end pattern match
                previousRegions.push({ indent: -2, line: line, marker: true });
                continue;
            }
        }
        if (previous.indent > indent) {
            // discard all regions with larger indent
            do {
                previousRegions.pop();
                previous = previousRegions[previousRegions.length - 1];
            } while (previous.indent > indent);
            // new folding range
            var endLineNumber = previous.line - 1;
            if (endLineNumber - line >= 1) { // needs at east size 1
                result.insertFirst(line, endLineNumber, indent);
            }
        }
        if (previous.indent === indent) {
            previous.line = line;
        }
        else { // previous.indent < indent
            // new region with a bigger indent
            previousRegions.push({ indent: indent, line: line, marker: false });
        }
    }
    return result.toIndentRanges(model);
}
