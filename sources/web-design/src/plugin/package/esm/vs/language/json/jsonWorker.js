/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
'use strict';
import * as jsonService from './_deps/vscode-json-languageservice/jsonLanguageService.js';
import * as ls from './_deps/vscode-languageserver-types/main.js';
var defaultSchemaRequestService;
if (typeof fetch !== 'undefined') {
    defaultSchemaRequestService = function (url) { return fetch(url).then(function (response) { return response.text(); }); };
}
var PromiseAdapter = /** @class */ (function () {
    function PromiseAdapter(executor) {
        this.wrapped = new Promise(executor);
    }
    PromiseAdapter.prototype.then = function (onfulfilled, onrejected) {
        var thenable = this.wrapped;
        return thenable.then(onfulfilled, onrejected);
    };
    PromiseAdapter.prototype.getWrapped = function () {
        return this.wrapped;
    };
    PromiseAdapter.resolve = function (v) {
        return Promise.resolve(v);
    };
    PromiseAdapter.reject = function (v) {
        return Promise.reject(v);
    };
    PromiseAdapter.all = function (values) {
        return Promise.all(values);
    };
    return PromiseAdapter;
}());
var JSONWorker = /** @class */ (function () {
    function JSONWorker(ctx, createData) {
        this._ctx = ctx;
        this._languageSettings = createData.languageSettings;
        this._languageId = createData.languageId;
        this._languageService = jsonService.getLanguageService({
            schemaRequestService: createData.enableSchemaRequest && defaultSchemaRequestService,
            promiseConstructor: PromiseAdapter
        });
        this._languageService.configure(this._languageSettings);
    }
    JSONWorker.prototype.doValidation = function (uri) {
        var document = this._getTextDocument(uri);
        if (document) {
            var jsonDocument = this._languageService.parseJSONDocument(document);
            return this._languageService.doValidation(document, jsonDocument);
        }
        return Promise.resolve([]);
    };
    JSONWorker.prototype.doComplete = function (uri, position) {
        var document = this._getTextDocument(uri);
        var jsonDocument = this._languageService.parseJSONDocument(document);
        return this._languageService.doComplete(document, position, jsonDocument);
    };
    JSONWorker.prototype.doResolve = function (item) {
        return this._languageService.doResolve(item);
    };
    JSONWorker.prototype.doHover = function (uri, position) {
        var document = this._getTextDocument(uri);
        var jsonDocument = this._languageService.parseJSONDocument(document);
        return this._languageService.doHover(document, position, jsonDocument);
    };
    JSONWorker.prototype.format = function (uri, range, options) {
        var document = this._getTextDocument(uri);
        var textEdits = this._languageService.format(document, range, options);
        return Promise.resolve(textEdits);
    };
    JSONWorker.prototype.resetSchema = function (uri) {
        return Promise.resolve(this._languageService.resetSchema(uri));
    };
    JSONWorker.prototype.findDocumentSymbols = function (uri) {
        var document = this._getTextDocument(uri);
        var jsonDocument = this._languageService.parseJSONDocument(document);
        var symbols = this._languageService.findDocumentSymbols(document, jsonDocument);
        return Promise.resolve(symbols);
    };
    JSONWorker.prototype.findDocumentColors = function (uri) {
        var document = this._getTextDocument(uri);
        var stylesheet = this._languageService.parseJSONDocument(document);
        var colorSymbols = this._languageService.findDocumentColors(document, stylesheet);
        return Promise.resolve(colorSymbols);
    };
    JSONWorker.prototype.getColorPresentations = function (uri, color, range) {
        var document = this._getTextDocument(uri);
        var stylesheet = this._languageService.parseJSONDocument(document);
        var colorPresentations = this._languageService.getColorPresentations(document, stylesheet, color, range);
        return Promise.resolve(colorPresentations);
    };
    JSONWorker.prototype.provideFoldingRanges = function (uri, context) {
        var document = this._getTextDocument(uri);
        var ranges = this._languageService.getFoldingRanges(document, context);
        return Promise.resolve(ranges);
    };
    JSONWorker.prototype._getTextDocument = function (uri) {
        var models = this._ctx.getMirrorModels();
        for (var _i = 0, models_1 = models; _i < models_1.length; _i++) {
            var model = models_1[_i];
            if (model.uri.toString() === uri) {
                return ls.TextDocument.create(uri, this._languageId, model.version, model.getValue());
            }
        }
        return null;
    };
    return JSONWorker;
}());
export { JSONWorker };
export function create(ctx, createData) {
    return new JSONWorker(ctx, createData);
}
