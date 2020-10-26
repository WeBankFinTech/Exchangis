define(["require", "exports", "../_.contribution"], function (require, exports, __contribution_1) {
    /*---------------------------------------------------------------------------------------------
     *  Copyright (c) Microsoft Corporation. All rights reserved.
     *  Licensed under the MIT License. See License.txt in the project root for license information.
     *--------------------------------------------------------------------------------------------*/
    'use strict';
    Object.defineProperty(exports, "__esModule", { value: true });
    __contribution_1.registerLanguage({
        id: 'html',
        extensions: ['.html', '.htm', '.shtml', '.xhtml', '.mdoc', '.jsp', '.asp', '.aspx', '.jshtm'],
        aliases: ['HTML', 'htm', 'html', 'xhtml'],
        mimetypes: ['text/html', 'text/x-jshtm', 'text/template', 'text/ng-template'],
        loader: function () { return new Promise(function (resolve_1, reject_1) { require(['./html'], resolve_1, reject_1); }); }
    });
});
