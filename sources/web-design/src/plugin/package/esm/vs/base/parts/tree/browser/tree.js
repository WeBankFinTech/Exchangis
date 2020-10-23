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
var ContextMenuEvent = /** @class */ (function () {
    function ContextMenuEvent(posx, posy, target) {
        this._posx = posx;
        this._posy = posy;
        this._target = target;
    }
    ContextMenuEvent.prototype.preventDefault = function () {
        // no-op
    };
    ContextMenuEvent.prototype.stopPropagation = function () {
        // no-op
    };
    Object.defineProperty(ContextMenuEvent.prototype, "target", {
        get: function () {
            return this._target;
        },
        enumerable: true,
        configurable: true
    });
    return ContextMenuEvent;
}());
export { ContextMenuEvent };
var MouseContextMenuEvent = /** @class */ (function (_super) {
    __extends(MouseContextMenuEvent, _super);
    function MouseContextMenuEvent(originalEvent) {
        var _this = _super.call(this, originalEvent.posx, originalEvent.posy, originalEvent.target) || this;
        _this.originalEvent = originalEvent;
        return _this;
    }
    MouseContextMenuEvent.prototype.preventDefault = function () {
        this.originalEvent.preventDefault();
    };
    MouseContextMenuEvent.prototype.stopPropagation = function () {
        this.originalEvent.stopPropagation();
    };
    return MouseContextMenuEvent;
}(ContextMenuEvent));
export { MouseContextMenuEvent };
var KeyboardContextMenuEvent = /** @class */ (function (_super) {
    __extends(KeyboardContextMenuEvent, _super);
    function KeyboardContextMenuEvent(posx, posy, originalEvent) {
        var _this = _super.call(this, posx, posy, originalEvent.target) || this;
        _this.originalEvent = originalEvent;
        return _this;
    }
    KeyboardContextMenuEvent.prototype.preventDefault = function () {
        this.originalEvent.preventDefault();
    };
    KeyboardContextMenuEvent.prototype.stopPropagation = function () {
        this.originalEvent.stopPropagation();
    };
    return KeyboardContextMenuEvent;
}(ContextMenuEvent));
export { KeyboardContextMenuEvent };
