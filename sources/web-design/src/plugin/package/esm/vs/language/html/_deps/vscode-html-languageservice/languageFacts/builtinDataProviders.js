/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
import { getHTML5DataProvider } from './data/html5.js';
export var builtinDataProviders = [
    getHTML5DataProvider()
];
var customDataProviders = [];
export function getAllDataProviders() {
    return builtinDataProviders.concat(customDataProviders);
}
export function handleCustomDataProviders(providers) {
    providers.forEach(function (p) {
        customDataProviders.push(p);
    });
}
//# sourceMappingURL=builtinDataProviders.js.map