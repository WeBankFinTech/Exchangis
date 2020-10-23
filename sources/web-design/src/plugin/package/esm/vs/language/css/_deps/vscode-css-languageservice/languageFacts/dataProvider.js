/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
'use strict';
var CSSDataProvider = /** @class */ (function () {
    /**
     * Currently, unversioned data uses the V1 implementation
     * In the future when the provider handles multiple versions of HTML custom data,
     * use the latest implementation for unversioned data
     */
    function CSSDataProvider(data) {
        this._properties = [];
        this._atDirectives = [];
        this._pseudoClasses = [];
        this._pseudoElements = [];
        this.addData(data);
    }
    CSSDataProvider.prototype.provideProperties = function () {
        return this._properties;
    };
    CSSDataProvider.prototype.provideAtDirectives = function () {
        return this._atDirectives;
    };
    CSSDataProvider.prototype.providePseudoClasses = function () {
        return this._pseudoClasses;
    };
    CSSDataProvider.prototype.providePseudoElements = function () {
        return this._pseudoElements;
    };
    CSSDataProvider.prototype.addData = function (data) {
        if (data.properties) {
            this._properties = this._properties.concat(data.properties);
        }
        if (data.atDirectives) {
            this._atDirectives = this._atDirectives.concat(data.atDirectives);
        }
        if (data.pseudoClasses) {
            this._pseudoClasses = this._pseudoClasses.concat(data.pseudoClasses);
        }
        if (data.pseudoElements) {
            this._pseudoElements = this._pseudoElements.concat(data.pseudoElements);
        }
    };
    return CSSDataProvider;
}());
export { CSSDataProvider };
