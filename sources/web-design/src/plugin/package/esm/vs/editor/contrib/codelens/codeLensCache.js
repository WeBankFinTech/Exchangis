/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __param = (this && this.__param) || function (paramIndex, decorator) {
    return function (target, key) { decorator(target, key, paramIndex); }
};
import { createDecorator } from '../../../platform/instantiation/common/instantiation.js';
import { registerSingleton } from '../../../platform/instantiation/common/extensions.js';
import { LRUCache, values } from '../../../base/common/map.js';
import { IStorageService } from '../../../platform/storage/common/storage.js';
import { Range } from '../../common/core/range.js';
export var ICodeLensCache = createDecorator('ICodeLensCache');
var CacheItem = /** @class */ (function () {
    function CacheItem(lineCount, data) {
        this.lineCount = lineCount;
        this.data = data;
    }
    return CacheItem;
}());
var CodeLensCache = /** @class */ (function () {
    function CodeLensCache(storageService) {
        var _this = this;
        this._fakeProvider = new /** @class */ (function () {
            function class_1() {
            }
            class_1.prototype.provideCodeLenses = function () {
                throw new Error('not supported');
            };
            return class_1;
        }());
        this._cache = new LRUCache(20, 0.75);
        var key = 'codelens/cache';
        // restore lens data on start
        var raw = storageService.get(key, 1 /* WORKSPACE */, '{}');
        this._deserialize(raw);
        // store lens data on shutdown
        var listener = storageService.onWillSaveState(function () {
            storageService.store(key, _this._serialize(), 1 /* WORKSPACE */);
            listener.dispose();
        });
    }
    CodeLensCache.prototype.put = function (model, data) {
        var _this = this;
        var item = new CacheItem(model.getLineCount(), data.map(function (item) {
            return {
                symbol: item.symbol,
                provider: _this._fakeProvider
            };
        }));
        this._cache.set(model.uri.toString(), item);
    };
    CodeLensCache.prototype.get = function (model) {
        var item = this._cache.get(model.uri.toString());
        return item && item.lineCount === model.getLineCount() ? item.data : undefined;
    };
    CodeLensCache.prototype.delete = function (model) {
        this._cache.delete(model.uri.toString());
    };
    // --- persistence
    CodeLensCache.prototype._serialize = function () {
        var data = Object.create(null);
        this._cache.forEach(function (value, key) {
            var lines = new Set();
            for (var _i = 0, _a = value.data; _i < _a.length; _i++) {
                var d = _a[_i];
                lines.add(d.symbol.range.startLineNumber);
            }
            data[key] = {
                lineCount: value.lineCount,
                lines: values(lines)
            };
        });
        return JSON.stringify(data);
    };
    CodeLensCache.prototype._deserialize = function (raw) {
        try {
            var data = JSON.parse(raw);
            for (var key in data) {
                var element = data[key];
                var symbols = [];
                for (var _i = 0, _a = element.lines; _i < _a.length; _i++) {
                    var line = _a[_i];
                    symbols.push({
                        provider: this._fakeProvider,
                        symbol: { range: new Range(line, 1, line, 11) }
                    });
                }
                this._cache.set(key, new CacheItem(element.lineCount, symbols));
            }
        }
        catch (_b) {
            // ignore...
        }
    };
    CodeLensCache = __decorate([
        __param(0, IStorageService)
    ], CodeLensCache);
    return CodeLensCache;
}());
export { CodeLensCache };
registerSingleton(ICodeLensCache, CodeLensCache);
