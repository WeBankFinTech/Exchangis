/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
'use strict';
import { registerLanguage } from '../_.contribution.js';
registerLanguage({
    id: 'sol',
    extensions: ['.sol'],
    aliases: ['sol', 'solidity', 'Solidity'],
    loader: function () { return import('./solidity.js'); }
});
