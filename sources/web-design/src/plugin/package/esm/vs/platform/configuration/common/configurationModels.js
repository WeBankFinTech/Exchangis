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
import { ResourceMap } from '../../../base/common/map.js';
import * as arrays from '../../../base/common/arrays.js';
import * as types from '../../../base/common/types.js';
import * as objects from '../../../base/common/objects.js';
import { OVERRIDE_PROPERTY_PATTERN } from './configurationRegistry.js';
import { overrideIdentifierFromKey, addToValueTree, toValuesTree, getConfigurationValue, getDefaultValues, getConfigurationKeys, removeFromValueTree } from './configuration.js';
var ConfigurationModel = /** @class */ (function () {
    function ConfigurationModel(_contents, _keys, _overrides) {
        if (_contents === void 0) { _contents = {}; }
        if (_keys === void 0) { _keys = []; }
        if (_overrides === void 0) { _overrides = []; }
        this._contents = _contents;
        this._keys = _keys;
        this._overrides = _overrides;
        this.isFrozen = false;
    }
    Object.defineProperty(ConfigurationModel.prototype, "contents", {
        get: function () {
            return this.checkAndFreeze(this._contents);
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ConfigurationModel.prototype, "overrides", {
        get: function () {
            return this.checkAndFreeze(this._overrides);
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ConfigurationModel.prototype, "keys", {
        get: function () {
            return this.checkAndFreeze(this._keys);
        },
        enumerable: true,
        configurable: true
    });
    ConfigurationModel.prototype.isEmpty = function () {
        return this._keys.length === 0 && Object.keys(this._contents).length === 0 && this._overrides.length === 0;
    };
    ConfigurationModel.prototype.getValue = function (section) {
        return section ? getConfigurationValue(this.contents, section) : this.contents;
    };
    ConfigurationModel.prototype.override = function (identifier) {
        var overrideContents = this.getContentsForOverrideIdentifer(identifier);
        if (!overrideContents || typeof overrideContents !== 'object' || !Object.keys(overrideContents).length) {
            // If there are no valid overrides, return self
            return this;
        }
        var contents = {};
        for (var _i = 0, _a = arrays.distinct(Object.keys(this.contents).concat(Object.keys(overrideContents))); _i < _a.length; _i++) {
            var key = _a[_i];
            var contentsForKey = this.contents[key];
            var overrideContentsForKey = overrideContents[key];
            // If there are override contents for the key, clone and merge otherwise use base contents
            if (overrideContentsForKey) {
                // Clone and merge only if base contents and override contents are of type object otherwise just override
                if (typeof contentsForKey === 'object' && typeof overrideContentsForKey === 'object') {
                    contentsForKey = objects.deepClone(contentsForKey);
                    this.mergeContents(contentsForKey, overrideContentsForKey);
                }
                else {
                    contentsForKey = overrideContentsForKey;
                }
            }
            contents[key] = contentsForKey;
        }
        return new ConfigurationModel(contents);
    };
    ConfigurationModel.prototype.merge = function () {
        var others = [];
        for (var _i = 0; _i < arguments.length; _i++) {
            others[_i] = arguments[_i];
        }
        var contents = objects.deepClone(this.contents);
        var overrides = objects.deepClone(this.overrides);
        var keys = this.keys.slice();
        for (var _a = 0, others_1 = others; _a < others_1.length; _a++) {
            var other = others_1[_a];
            this.mergeContents(contents, other.contents);
            var _loop_1 = function (otherOverride) {
                var override = overrides.filter(function (o) { return arrays.equals(o.identifiers, otherOverride.identifiers); })[0];
                if (override) {
                    this_1.mergeContents(override.contents, otherOverride.contents);
                }
                else {
                    overrides.push(objects.deepClone(otherOverride));
                }
            };
            var this_1 = this;
            for (var _b = 0, _c = other.overrides; _b < _c.length; _b++) {
                var otherOverride = _c[_b];
                _loop_1(otherOverride);
            }
            for (var _d = 0, _e = other.keys; _d < _e.length; _d++) {
                var key = _e[_d];
                if (keys.indexOf(key) === -1) {
                    keys.push(key);
                }
            }
        }
        return new ConfigurationModel(contents, keys, overrides);
    };
    ConfigurationModel.prototype.freeze = function () {
        this.isFrozen = true;
        return this;
    };
    ConfigurationModel.prototype.mergeContents = function (source, target) {
        for (var _i = 0, _a = Object.keys(target); _i < _a.length; _i++) {
            var key = _a[_i];
            if (key in source) {
                if (types.isObject(source[key]) && types.isObject(target[key])) {
                    this.mergeContents(source[key], target[key]);
                    continue;
                }
            }
            source[key] = objects.deepClone(target[key]);
        }
    };
    ConfigurationModel.prototype.checkAndFreeze = function (data) {
        if (this.isFrozen && !Object.isFrozen(data)) {
            return objects.deepFreeze(data);
        }
        return data;
    };
    ConfigurationModel.prototype.getContentsForOverrideIdentifer = function (identifier) {
        for (var _i = 0, _a = this.overrides; _i < _a.length; _i++) {
            var override = _a[_i];
            if (override.identifiers.indexOf(identifier) !== -1) {
                return override.contents;
            }
        }
        return null;
    };
    ConfigurationModel.prototype.toJSON = function () {
        return {
            contents: this.contents,
            overrides: this.overrides,
            keys: this.keys
        };
    };
    // Update methods
    ConfigurationModel.prototype.setValue = function (key, value) {
        this.addKey(key);
        addToValueTree(this.contents, key, value, function (e) { throw new Error(e); });
    };
    ConfigurationModel.prototype.removeValue = function (key) {
        if (this.removeKey(key)) {
            removeFromValueTree(this.contents, key);
        }
    };
    ConfigurationModel.prototype.addKey = function (key) {
        var index = this.keys.length;
        for (var i = 0; i < index; i++) {
            if (key.indexOf(this.keys[i]) === 0) {
                index = i;
            }
        }
        this.keys.splice(index, 1, key);
    };
    ConfigurationModel.prototype.removeKey = function (key) {
        var index = this.keys.indexOf(key);
        if (index !== -1) {
            this.keys.splice(index, 1);
            return true;
        }
        return false;
    };
    return ConfigurationModel;
}());
export { ConfigurationModel };
var DefaultConfigurationModel = /** @class */ (function (_super) {
    __extends(DefaultConfigurationModel, _super);
    function DefaultConfigurationModel() {
        var _this = this;
        var contents = getDefaultValues();
        var keys = getConfigurationKeys();
        var overrides = [];
        for (var _i = 0, _a = Object.keys(contents); _i < _a.length; _i++) {
            var key = _a[_i];
            if (OVERRIDE_PROPERTY_PATTERN.test(key)) {
                overrides.push({
                    identifiers: [overrideIdentifierFromKey(key).trim()],
                    contents: toValuesTree(contents[key], function (message) { return console.error("Conflict in default settings file: " + message); })
                });
            }
        }
        _this = _super.call(this, contents, keys, overrides) || this;
        return _this;
    }
    return DefaultConfigurationModel;
}(ConfigurationModel));
export { DefaultConfigurationModel };
var Configuration = /** @class */ (function () {
    function Configuration(_defaultConfiguration, _localUserConfiguration, _remoteUserConfiguration, _workspaceConfiguration, _folderConfigurations, _memoryConfiguration, _memoryConfigurationByResource, _freeze) {
        if (_remoteUserConfiguration === void 0) { _remoteUserConfiguration = new ConfigurationModel(); }
        if (_workspaceConfiguration === void 0) { _workspaceConfiguration = new ConfigurationModel(); }
        if (_folderConfigurations === void 0) { _folderConfigurations = new ResourceMap(); }
        if (_memoryConfiguration === void 0) { _memoryConfiguration = new ConfigurationModel(); }
        if (_memoryConfigurationByResource === void 0) { _memoryConfigurationByResource = new ResourceMap(); }
        if (_freeze === void 0) { _freeze = true; }
        this._defaultConfiguration = _defaultConfiguration;
        this._localUserConfiguration = _localUserConfiguration;
        this._remoteUserConfiguration = _remoteUserConfiguration;
        this._workspaceConfiguration = _workspaceConfiguration;
        this._folderConfigurations = _folderConfigurations;
        this._memoryConfiguration = _memoryConfiguration;
        this._memoryConfigurationByResource = _memoryConfigurationByResource;
        this._freeze = _freeze;
        this._workspaceConsolidatedConfiguration = null;
        this._foldersConsolidatedConfigurations = new ResourceMap();
    }
    Configuration.prototype.getValue = function (section, overrides, workspace) {
        var consolidateConfigurationModel = this.getConsolidateConfigurationModel(overrides, workspace);
        return consolidateConfigurationModel.getValue(section);
    };
    Configuration.prototype.updateValue = function (key, value, overrides) {
        if (overrides === void 0) { overrides = {}; }
        var memoryConfiguration;
        if (overrides.resource) {
            memoryConfiguration = this._memoryConfigurationByResource.get(overrides.resource);
            if (!memoryConfiguration) {
                memoryConfiguration = new ConfigurationModel();
                this._memoryConfigurationByResource.set(overrides.resource, memoryConfiguration);
            }
        }
        else {
            memoryConfiguration = this._memoryConfiguration;
        }
        if (value === undefined) {
            memoryConfiguration.removeValue(key);
        }
        else {
            memoryConfiguration.setValue(key, value);
        }
        if (!overrides.resource) {
            this._workspaceConsolidatedConfiguration = null;
        }
    };
    Configuration.prototype.inspect = function (key, overrides, workspace) {
        var consolidateConfigurationModel = this.getConsolidateConfigurationModel(overrides, workspace);
        var folderConfigurationModel = this.getFolderConfigurationModelForResource(overrides.resource, workspace);
        var memoryConfigurationModel = overrides.resource ? this._memoryConfigurationByResource.get(overrides.resource) || this._memoryConfiguration : this._memoryConfiguration;
        return {
            default: overrides.overrideIdentifier ? this._defaultConfiguration.freeze().override(overrides.overrideIdentifier).getValue(key) : this._defaultConfiguration.freeze().getValue(key),
            user: overrides.overrideIdentifier ? this.userConfiguration.freeze().override(overrides.overrideIdentifier).getValue(key) : this.userConfiguration.freeze().getValue(key),
            userLocal: overrides.overrideIdentifier ? this.localUserConfiguration.freeze().override(overrides.overrideIdentifier).getValue(key) : this.localUserConfiguration.freeze().getValue(key),
            userRemote: overrides.overrideIdentifier ? this.remoteUserConfiguration.freeze().override(overrides.overrideIdentifier).getValue(key) : this.remoteUserConfiguration.freeze().getValue(key),
            workspace: workspace ? overrides.overrideIdentifier ? this._workspaceConfiguration.freeze().override(overrides.overrideIdentifier).getValue(key) : this._workspaceConfiguration.freeze().getValue(key) : undefined,
            workspaceFolder: folderConfigurationModel ? overrides.overrideIdentifier ? folderConfigurationModel.freeze().override(overrides.overrideIdentifier).getValue(key) : folderConfigurationModel.freeze().getValue(key) : undefined,
            memory: overrides.overrideIdentifier ? memoryConfigurationModel.override(overrides.overrideIdentifier).getValue(key) : memoryConfigurationModel.getValue(key),
            value: consolidateConfigurationModel.getValue(key)
        };
    };
    Object.defineProperty(Configuration.prototype, "userConfiguration", {
        get: function () {
            if (!this._userConfiguration) {
                this._userConfiguration = this._remoteUserConfiguration.isEmpty() ? this._localUserConfiguration : this._localUserConfiguration.merge(this._remoteUserConfiguration);
                if (this._freeze) {
                    this._userConfiguration.freeze();
                }
            }
            return this._userConfiguration;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Configuration.prototype, "localUserConfiguration", {
        get: function () {
            return this._localUserConfiguration;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Configuration.prototype, "remoteUserConfiguration", {
        get: function () {
            return this._remoteUserConfiguration;
        },
        enumerable: true,
        configurable: true
    });
    Configuration.prototype.getConsolidateConfigurationModel = function (overrides, workspace) {
        var configurationModel = this.getConsolidatedConfigurationModelForResource(overrides, workspace);
        return overrides.overrideIdentifier ? configurationModel.override(overrides.overrideIdentifier) : configurationModel;
    };
    Configuration.prototype.getConsolidatedConfigurationModelForResource = function (_a, workspace) {
        var resource = _a.resource;
        var consolidateConfiguration = this.getWorkspaceConsolidatedConfiguration();
        if (workspace && resource) {
            var root = workspace.getFolder(resource);
            if (root) {
                consolidateConfiguration = this.getFolderConsolidatedConfiguration(root.uri) || consolidateConfiguration;
            }
            var memoryConfigurationForResource = this._memoryConfigurationByResource.get(resource);
            if (memoryConfigurationForResource) {
                consolidateConfiguration = consolidateConfiguration.merge(memoryConfigurationForResource);
            }
        }
        return consolidateConfiguration;
    };
    Configuration.prototype.getWorkspaceConsolidatedConfiguration = function () {
        if (!this._workspaceConsolidatedConfiguration) {
            this._workspaceConsolidatedConfiguration = this._defaultConfiguration.merge(this.userConfiguration, this._workspaceConfiguration, this._memoryConfiguration);
            if (this._freeze) {
                this._workspaceConfiguration = this._workspaceConfiguration.freeze();
            }
        }
        return this._workspaceConsolidatedConfiguration;
    };
    Configuration.prototype.getFolderConsolidatedConfiguration = function (folder) {
        var folderConsolidatedConfiguration = this._foldersConsolidatedConfigurations.get(folder);
        if (!folderConsolidatedConfiguration) {
            var workspaceConsolidateConfiguration = this.getWorkspaceConsolidatedConfiguration();
            var folderConfiguration = this._folderConfigurations.get(folder);
            if (folderConfiguration) {
                folderConsolidatedConfiguration = workspaceConsolidateConfiguration.merge(folderConfiguration);
                if (this._freeze) {
                    folderConsolidatedConfiguration = folderConsolidatedConfiguration.freeze();
                }
                this._foldersConsolidatedConfigurations.set(folder, folderConsolidatedConfiguration);
            }
            else {
                folderConsolidatedConfiguration = workspaceConsolidateConfiguration;
            }
        }
        return folderConsolidatedConfiguration;
    };
    Configuration.prototype.getFolderConfigurationModelForResource = function (resource, workspace) {
        if (workspace && resource) {
            var root = workspace.getFolder(resource);
            if (root) {
                return types.withUndefinedAsNull(this._folderConfigurations.get(root.uri));
            }
        }
        return null;
    };
    return Configuration;
}());
export { Configuration };
