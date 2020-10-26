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
import { Event, PauseableEmitter } from '../../../base/common/event.js';
import { dispose } from '../../../base/common/lifecycle.js';
import { keys } from '../../../base/common/map.js';
import { CommandsRegistry } from '../../commands/common/commands.js';
import { IConfigurationService } from '../../configuration/common/configuration.js';
import { IContextKeyService, SET_CONTEXT_COMMAND_ID } from '../common/contextkey.js';
import { KeybindingResolver } from '../../keybinding/common/keybindingResolver.js';
var KEYBINDING_CONTEXT_ATTR = 'data-keybinding-context';
var Context = /** @class */ (function () {
    function Context(id, parent) {
        this._id = id;
        this._parent = parent;
        this._value = Object.create(null);
        this._value['_contextId'] = id;
    }
    Context.prototype.setValue = function (key, value) {
        // console.log('SET ' + key + ' = ' + value + ' ON ' + this._id);
        if (this._value[key] !== value) {
            this._value[key] = value;
            return true;
        }
        return false;
    };
    Context.prototype.removeValue = function (key) {
        // console.log('REMOVE ' + key + ' FROM ' + this._id);
        if (key in this._value) {
            delete this._value[key];
            return true;
        }
        return false;
    };
    Context.prototype.getValue = function (key) {
        var ret = this._value[key];
        if (typeof ret === 'undefined' && this._parent) {
            return this._parent.getValue(key);
        }
        return ret;
    };
    return Context;
}());
export { Context };
var NullContext = /** @class */ (function (_super) {
    __extends(NullContext, _super);
    function NullContext() {
        return _super.call(this, -1, null) || this;
    }
    NullContext.prototype.setValue = function (key, value) {
        return false;
    };
    NullContext.prototype.removeValue = function (key) {
        return false;
    };
    NullContext.prototype.getValue = function (key) {
        return undefined;
    };
    NullContext.INSTANCE = new NullContext();
    return NullContext;
}(Context));
var ConfigAwareContextValuesContainer = /** @class */ (function (_super) {
    __extends(ConfigAwareContextValuesContainer, _super);
    function ConfigAwareContextValuesContainer(id, _configurationService, emitter) {
        var _this = _super.call(this, id, null) || this;
        _this._configurationService = _configurationService;
        _this._values = new Map();
        _this._listener = _this._configurationService.onDidChangeConfiguration(function (event) {
            if (event.source === 6 /* DEFAULT */) {
                // new setting, reset everything
                var allKeys = keys(_this._values);
                _this._values.clear();
                emitter.fire(new ArrayContextKeyChangeEvent(allKeys));
            }
            else {
                var changedKeys = [];
                for (var _i = 0, _a = event.affectedKeys; _i < _a.length; _i++) {
                    var configKey = _a[_i];
                    var contextKey = "config." + configKey;
                    if (_this._values.has(contextKey)) {
                        _this._values.delete(contextKey);
                        changedKeys.push(contextKey);
                    }
                }
                emitter.fire(new ArrayContextKeyChangeEvent(changedKeys));
            }
        });
        return _this;
    }
    ConfigAwareContextValuesContainer.prototype.dispose = function () {
        this._listener.dispose();
    };
    ConfigAwareContextValuesContainer.prototype.getValue = function (key) {
        if (key.indexOf(ConfigAwareContextValuesContainer._keyPrefix) !== 0) {
            return _super.prototype.getValue.call(this, key);
        }
        if (this._values.has(key)) {
            return this._values.get(key);
        }
        var configKey = key.substr(ConfigAwareContextValuesContainer._keyPrefix.length);
        var configValue = this._configurationService.getValue(configKey);
        var value = undefined;
        switch (typeof configValue) {
            case 'number':
            case 'boolean':
            case 'string':
                value = configValue;
                break;
        }
        this._values.set(key, value);
        return value;
    };
    ConfigAwareContextValuesContainer.prototype.setValue = function (key, value) {
        return _super.prototype.setValue.call(this, key, value);
    };
    ConfigAwareContextValuesContainer.prototype.removeValue = function (key) {
        return _super.prototype.removeValue.call(this, key);
    };
    ConfigAwareContextValuesContainer._keyPrefix = 'config.';
    return ConfigAwareContextValuesContainer;
}(Context));
var ContextKey = /** @class */ (function () {
    function ContextKey(service, key, defaultValue) {
        this._service = service;
        this._key = key;
        this._defaultValue = defaultValue;
        this.reset();
    }
    ContextKey.prototype.set = function (value) {
        this._service.setContext(this._key, value);
    };
    ContextKey.prototype.reset = function () {
        if (typeof this._defaultValue === 'undefined') {
            this._service.removeContext(this._key);
        }
        else {
            this._service.setContext(this._key, this._defaultValue);
        }
    };
    ContextKey.prototype.get = function () {
        return this._service.getContextKeyValue(this._key);
    };
    return ContextKey;
}());
var SimpleContextKeyChangeEvent = /** @class */ (function () {
    function SimpleContextKeyChangeEvent(key) {
        this.key = key;
    }
    SimpleContextKeyChangeEvent.prototype.affectsSome = function (keys) {
        return keys.has(this.key);
    };
    return SimpleContextKeyChangeEvent;
}());
var ArrayContextKeyChangeEvent = /** @class */ (function () {
    function ArrayContextKeyChangeEvent(keys) {
        this.keys = keys;
    }
    ArrayContextKeyChangeEvent.prototype.affectsSome = function (keys) {
        for (var _i = 0, _a = this.keys; _i < _a.length; _i++) {
            var key = _a[_i];
            if (keys.has(key)) {
                return true;
            }
        }
        return false;
    };
    return ArrayContextKeyChangeEvent;
}());
var CompositeContextKeyChangeEvent = /** @class */ (function () {
    function CompositeContextKeyChangeEvent(events) {
        this.events = events;
    }
    CompositeContextKeyChangeEvent.prototype.affectsSome = function (keys) {
        for (var _i = 0, _a = this.events; _i < _a.length; _i++) {
            var e = _a[_i];
            if (e.affectsSome(keys)) {
                return true;
            }
        }
        return false;
    };
    return CompositeContextKeyChangeEvent;
}());
var AbstractContextKeyService = /** @class */ (function () {
    function AbstractContextKeyService(myContextId) {
        this._onDidChangeContext = new PauseableEmitter({ merge: function (input) { return new CompositeContextKeyChangeEvent(input); } });
        this._isDisposed = false;
        this._myContextId = myContextId;
    }
    AbstractContextKeyService.prototype.createKey = function (key, defaultValue) {
        if (this._isDisposed) {
            throw new Error("AbstractContextKeyService has been disposed");
        }
        return new ContextKey(this, key, defaultValue);
    };
    Object.defineProperty(AbstractContextKeyService.prototype, "onDidChangeContext", {
        get: function () {
            return this._onDidChangeContext.event;
        },
        enumerable: true,
        configurable: true
    });
    AbstractContextKeyService.prototype.bufferChangeEvents = function (callback) {
        this._onDidChangeContext.pause();
        try {
            callback();
        }
        finally {
            this._onDidChangeContext.resume();
        }
    };
    AbstractContextKeyService.prototype.createScoped = function (domNode) {
        if (this._isDisposed) {
            throw new Error("AbstractContextKeyService has been disposed");
        }
        return new ScopedContextKeyService(this, domNode);
    };
    AbstractContextKeyService.prototype.contextMatchesRules = function (rules) {
        if (this._isDisposed) {
            throw new Error("AbstractContextKeyService has been disposed");
        }
        var context = this.getContextValuesContainer(this._myContextId);
        var result = KeybindingResolver.contextMatchesRules(context, rules);
        // console.group(rules.serialize() + ' -> ' + result);
        // rules.keys().forEach(key => { console.log(key, ctx[key]); });
        // console.groupEnd();
        return result;
    };
    AbstractContextKeyService.prototype.getContextKeyValue = function (key) {
        if (this._isDisposed) {
            return undefined;
        }
        return this.getContextValuesContainer(this._myContextId).getValue(key);
    };
    AbstractContextKeyService.prototype.setContext = function (key, value) {
        if (this._isDisposed) {
            return;
        }
        var myContext = this.getContextValuesContainer(this._myContextId);
        if (!myContext) {
            return;
        }
        if (myContext.setValue(key, value)) {
            this._onDidChangeContext.fire(new SimpleContextKeyChangeEvent(key));
        }
    };
    AbstractContextKeyService.prototype.removeContext = function (key) {
        if (this._isDisposed) {
            return;
        }
        if (this.getContextValuesContainer(this._myContextId).removeValue(key)) {
            this._onDidChangeContext.fire(new SimpleContextKeyChangeEvent(key));
        }
    };
    AbstractContextKeyService.prototype.getContext = function (target) {
        if (this._isDisposed) {
            return NullContext.INSTANCE;
        }
        return this.getContextValuesContainer(findContextAttr(target));
    };
    return AbstractContextKeyService;
}());
export { AbstractContextKeyService };
var ContextKeyService = /** @class */ (function (_super) {
    __extends(ContextKeyService, _super);
    function ContextKeyService(configurationService) {
        var _this = _super.call(this, 0) || this;
        _this._contexts = new Map();
        _this._toDispose = [];
        _this._lastContextId = 0;
        var myContext = new ConfigAwareContextValuesContainer(_this._myContextId, configurationService, _this._onDidChangeContext);
        _this._contexts.set(_this._myContextId, myContext);
        _this._toDispose.push(myContext);
        return _this;
        // Uncomment this to see the contexts continuously logged
        // let lastLoggedValue: string | null = null;
        // setInterval(() => {
        // 	let values = Object.keys(this._contexts).map((key) => this._contexts[key]);
        // 	let logValue = values.map(v => JSON.stringify(v._value, null, '\t')).join('\n');
        // 	if (lastLoggedValue !== logValue) {
        // 		lastLoggedValue = logValue;
        // 		console.log(lastLoggedValue);
        // 	}
        // }, 2000);
    }
    ContextKeyService.prototype.dispose = function () {
        this._isDisposed = true;
        this._toDispose = dispose(this._toDispose);
    };
    ContextKeyService.prototype.getContextValuesContainer = function (contextId) {
        if (this._isDisposed) {
            return NullContext.INSTANCE;
        }
        return this._contexts.get(contextId) || NullContext.INSTANCE;
    };
    ContextKeyService.prototype.createChildContext = function (parentContextId) {
        if (parentContextId === void 0) { parentContextId = this._myContextId; }
        if (this._isDisposed) {
            throw new Error("ContextKeyService has been disposed");
        }
        var id = (++this._lastContextId);
        this._contexts.set(id, new Context(id, this.getContextValuesContainer(parentContextId)));
        return id;
    };
    ContextKeyService.prototype.disposeContext = function (contextId) {
        if (!this._isDisposed) {
            this._contexts.delete(contextId);
        }
    };
    ContextKeyService = __decorate([
        __param(0, IConfigurationService)
    ], ContextKeyService);
    return ContextKeyService;
}(AbstractContextKeyService));
export { ContextKeyService };
var ScopedContextKeyService = /** @class */ (function (_super) {
    __extends(ScopedContextKeyService, _super);
    function ScopedContextKeyService(parent, domNode) {
        var _this = _super.call(this, parent.createChildContext()) || this;
        _this._parent = parent;
        if (domNode) {
            _this._domNode = domNode;
            _this._domNode.setAttribute(KEYBINDING_CONTEXT_ATTR, String(_this._myContextId));
        }
        return _this;
    }
    ScopedContextKeyService.prototype.dispose = function () {
        this._isDisposed = true;
        this._parent.disposeContext(this._myContextId);
        if (this._domNode) {
            this._domNode.removeAttribute(KEYBINDING_CONTEXT_ATTR);
            this._domNode = undefined;
        }
    };
    Object.defineProperty(ScopedContextKeyService.prototype, "onDidChangeContext", {
        get: function () {
            return Event.any(this._parent.onDidChangeContext, this._onDidChangeContext.event);
        },
        enumerable: true,
        configurable: true
    });
    ScopedContextKeyService.prototype.getContextValuesContainer = function (contextId) {
        if (this._isDisposed) {
            return NullContext.INSTANCE;
        }
        return this._parent.getContextValuesContainer(contextId);
    };
    ScopedContextKeyService.prototype.createChildContext = function (parentContextId) {
        if (parentContextId === void 0) { parentContextId = this._myContextId; }
        if (this._isDisposed) {
            throw new Error("ScopedContextKeyService has been disposed");
        }
        return this._parent.createChildContext(parentContextId);
    };
    ScopedContextKeyService.prototype.disposeContext = function (contextId) {
        if (this._isDisposed) {
            return;
        }
        this._parent.disposeContext(contextId);
    };
    return ScopedContextKeyService;
}(AbstractContextKeyService));
function findContextAttr(domNode) {
    while (domNode) {
        if (domNode.hasAttribute(KEYBINDING_CONTEXT_ATTR)) {
            var attr = domNode.getAttribute(KEYBINDING_CONTEXT_ATTR);
            if (attr) {
                return parseInt(attr, 10);
            }
            return NaN;
        }
        domNode = domNode.parentElement;
    }
    return 0;
}
CommandsRegistry.registerCommand(SET_CONTEXT_COMMAND_ID, function (accessor, contextKey, contextValue) {
    accessor.get(IContextKeyService).createKey(String(contextKey), contextValue);
});
