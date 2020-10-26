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
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __param = (this && this.__param) || function (paramIndex, decorator) {
    return function (target, key) { decorator(target, key, paramIndex); }
};
import { LRUCache, TernarySearchTree } from '../../../base/common/map.js';
import { IStorageService } from '../../../platform/storage/common/storage.js';
import { completionKindFromString } from '../../common/modes.js';
import { Disposable } from '../../../base/common/lifecycle.js';
import { RunOnceScheduler } from '../../../base/common/async.js';
import { createDecorator } from '../../../platform/instantiation/common/instantiation.js';
import { IConfigurationService } from '../../../platform/configuration/common/configuration.js';
import { registerSingleton } from '../../../platform/instantiation/common/extensions.js';
var Memory = /** @class */ (function () {
    function Memory() {
    }
    Memory.prototype.select = function (model, pos, items) {
        if (items.length === 0) {
            return 0;
        }
        var topScore = items[0].score;
        for (var i = 1; i < items.length; i++) {
            var _a = items[i], score = _a.score, suggestion = _a.completion;
            if (score !== topScore) {
                // stop when leaving the group of top matches
                break;
            }
            if (suggestion.preselect) {
                // stop when seeing an auto-select-item
                return i;
            }
        }
        return 0;
    };
    return Memory;
}());
export { Memory };
var NoMemory = /** @class */ (function (_super) {
    __extends(NoMemory, _super);
    function NoMemory() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    NoMemory.prototype.memorize = function (model, pos, item) {
        // no-op
    };
    NoMemory.prototype.toJSON = function () {
        return undefined;
    };
    NoMemory.prototype.fromJSON = function () {
        //
    };
    return NoMemory;
}(Memory));
export { NoMemory };
var LRUMemory = /** @class */ (function (_super) {
    __extends(LRUMemory, _super);
    function LRUMemory() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this._cache = new LRUCache(300, 0.66);
        _this._seq = 0;
        return _this;
    }
    LRUMemory.prototype.memorize = function (model, pos, item) {
        var label = item.completion.label;
        var key = model.getLanguageIdentifier().language + "/" + label;
        this._cache.set(key, {
            touch: this._seq++,
            type: item.completion.kind,
            insertText: item.completion.insertText
        });
    };
    LRUMemory.prototype.select = function (model, pos, items) {
        // in order of completions, select the first
        // that has been used in the past
        var word = model.getWordUntilPosition(pos).word;
        if (word.length !== 0) {
            return _super.prototype.select.call(this, model, pos, items);
        }
        var lineSuffix = model.getLineContent(pos.lineNumber).substr(pos.column - 10, pos.column - 1);
        if (/\s$/.test(lineSuffix)) {
            return _super.prototype.select.call(this, model, pos, items);
        }
        var res = -1;
        var seq = -1;
        for (var i = 0; i < items.length; i++) {
            var suggestion = items[i].completion;
            var key = model.getLanguageIdentifier().language + "/" + suggestion.label;
            var item = this._cache.get(key);
            if (item && item.touch > seq && item.type === suggestion.kind && item.insertText === suggestion.insertText) {
                seq = item.touch;
                res = i;
            }
        }
        if (res === -1) {
            return _super.prototype.select.call(this, model, pos, items);
        }
        else {
            return res;
        }
    };
    LRUMemory.prototype.toJSON = function () {
        var data = [];
        this._cache.forEach(function (value, key) {
            data.push([key, value]);
        });
        return data;
    };
    LRUMemory.prototype.fromJSON = function (data) {
        this._cache.clear();
        var seq = 0;
        for (var _i = 0, data_1 = data; _i < data_1.length; _i++) {
            var _a = data_1[_i], key = _a[0], value = _a[1];
            value.touch = seq;
            value.type = typeof value.type === 'number' ? value.type : completionKindFromString(value.type);
            this._cache.set(key, value);
        }
        this._seq = this._cache.size;
    };
    return LRUMemory;
}(Memory));
export { LRUMemory };
var PrefixMemory = /** @class */ (function (_super) {
    __extends(PrefixMemory, _super);
    function PrefixMemory() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this._trie = TernarySearchTree.forStrings();
        _this._seq = 0;
        return _this;
    }
    PrefixMemory.prototype.memorize = function (model, pos, item) {
        var word = model.getWordUntilPosition(pos).word;
        var key = model.getLanguageIdentifier().language + "/" + word;
        this._trie.set(key, {
            type: item.completion.kind,
            insertText: item.completion.insertText,
            touch: this._seq++
        });
    };
    PrefixMemory.prototype.select = function (model, pos, items) {
        var word = model.getWordUntilPosition(pos).word;
        if (!word) {
            return _super.prototype.select.call(this, model, pos, items);
        }
        var key = model.getLanguageIdentifier().language + "/" + word;
        var item = this._trie.get(key);
        if (!item) {
            item = this._trie.findSubstr(key);
        }
        if (item) {
            for (var i = 0; i < items.length; i++) {
                var _a = items[i].completion, kind = _a.kind, insertText = _a.insertText;
                if (kind === item.type && insertText === item.insertText) {
                    return i;
                }
            }
        }
        return _super.prototype.select.call(this, model, pos, items);
    };
    PrefixMemory.prototype.toJSON = function () {
        var entries = [];
        this._trie.forEach(function (value, key) { return entries.push([key, value]); });
        // sort by last recently used (touch), then
        // take the top 200 item and normalize their
        // touch
        entries
            .sort(function (a, b) { return -(a[1].touch - b[1].touch); })
            .forEach(function (value, i) { return value[1].touch = i; });
        return entries.slice(0, 200);
    };
    PrefixMemory.prototype.fromJSON = function (data) {
        this._trie.clear();
        if (data.length > 0) {
            this._seq = data[0][1].touch + 1;
            for (var _i = 0, data_2 = data; _i < data_2.length; _i++) {
                var _a = data_2[_i], key = _a[0], value = _a[1];
                value.type = typeof value.type === 'number' ? value.type : completionKindFromString(value.type);
                this._trie.set(key, value);
            }
        }
    };
    return PrefixMemory;
}(Memory));
export { PrefixMemory };
var SuggestMemoryService = /** @class */ (function (_super) {
    __extends(SuggestMemoryService, _super);
    function SuggestMemoryService(_storageService, _configService) {
        var _this = _super.call(this) || this;
        _this._storageService = _storageService;
        _this._configService = _configService;
        _this._storagePrefix = 'suggest/memories';
        var update = function () {
            var mode = _this._configService.getValue('editor.suggestSelection');
            var share = _this._configService.getValue('editor.suggest.shareSuggestSelections');
            _this._update(mode, share, false);
        };
        _this._persistSoon = _this._register(new RunOnceScheduler(function () { return _this._saveState(); }, 500));
        _this._register(_storageService.onWillSaveState(function () { return _this._saveState(); }));
        _this._register(_this._configService.onDidChangeConfiguration(function (e) {
            if (e.affectsConfiguration('editor.suggestSelection') || e.affectsConfiguration('editor.suggest.shareSuggestSelections')) {
                update();
            }
        }));
        _this._register(_this._storageService.onDidChangeStorage(function (e) {
            if (e.scope === 0 /* GLOBAL */ && e.key.indexOf(_this._storagePrefix) === 0) {
                if (!document.hasFocus()) {
                    // windows that aren't focused have to drop their current
                    // storage value and accept what's stored now
                    _this._update(_this._mode, _this._shareMem, true);
                }
            }
        }));
        update();
        return _this;
    }
    SuggestMemoryService.prototype._update = function (mode, shareMem, force) {
        if (!force && this._mode === mode && this._shareMem === shareMem) {
            return;
        }
        this._shareMem = shareMem;
        this._mode = mode;
        this._strategy = mode === 'recentlyUsedByPrefix' ? new PrefixMemory() : mode === 'recentlyUsed' ? new LRUMemory() : new NoMemory();
        try {
            var scope = shareMem ? 0 /* GLOBAL */ : 1 /* WORKSPACE */;
            var raw = this._storageService.get(this._storagePrefix + "/" + this._mode, scope);
            if (raw) {
                this._strategy.fromJSON(JSON.parse(raw));
            }
        }
        catch (e) {
            // things can go wrong with JSON...
        }
    };
    SuggestMemoryService.prototype.memorize = function (model, pos, item) {
        this._strategy.memorize(model, pos, item);
        this._persistSoon.schedule();
    };
    SuggestMemoryService.prototype.select = function (model, pos, items) {
        return this._strategy.select(model, pos, items);
    };
    SuggestMemoryService.prototype._saveState = function () {
        var raw = JSON.stringify(this._strategy);
        var scope = this._shareMem ? 0 /* GLOBAL */ : 1 /* WORKSPACE */;
        this._storageService.store(this._storagePrefix + "/" + this._mode, raw, scope);
    };
    SuggestMemoryService = __decorate([
        __param(0, IStorageService),
        __param(1, IConfigurationService)
    ], SuggestMemoryService);
    return SuggestMemoryService;
}(Disposable));
export { SuggestMemoryService };
export var ISuggestMemoryService = createDecorator('ISuggestMemories');
registerSingleton(ISuggestMemoryService, SuggestMemoryService, true);
