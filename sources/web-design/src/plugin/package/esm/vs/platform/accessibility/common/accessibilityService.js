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
import { Emitter } from '../../../base/common/event.js';
import { Disposable } from '../../../base/common/lifecycle.js';
var BrowserAccessibilityService = /** @class */ (function (_super) {
    __extends(BrowserAccessibilityService, _super);
    function BrowserAccessibilityService() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this._accessibilitySupport = 0 /* Unknown */;
        _this._onDidChangeAccessibilitySupport = new Emitter();
        _this.onDidChangeAccessibilitySupport = _this._onDidChangeAccessibilitySupport.event;
        return _this;
    }
    BrowserAccessibilityService.prototype.getAccessibilitySupport = function () {
        return this._accessibilitySupport;
    };
    return BrowserAccessibilityService;
}(Disposable));
export { BrowserAccessibilityService };
