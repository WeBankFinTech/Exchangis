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
export var FIN = { done: true, value: undefined };
export var Iterator;
(function (Iterator) {
    var _empty = {
        next: function () {
            return FIN;
        }
    };
    function empty() {
        return _empty;
    }
    Iterator.empty = empty;
    function fromArray(array, index, length) {
        if (index === void 0) { index = 0; }
        if (length === void 0) { length = array.length; }
        return {
            next: function () {
                if (index >= length) {
                    return FIN;
                }
                return { done: false, value: array[index++] };
            }
        };
    }
    Iterator.fromArray = fromArray;
    function from(elements) {
        if (!elements) {
            return Iterator.empty();
        }
        else if (Array.isArray(elements)) {
            return Iterator.fromArray(elements);
        }
        else {
            return elements;
        }
    }
    Iterator.from = from;
    function map(iterator, fn) {
        return {
            next: function () {
                var element = iterator.next();
                if (element.done) {
                    return FIN;
                }
                else {
                    return { done: false, value: fn(element.value) };
                }
            }
        };
    }
    Iterator.map = map;
    function filter(iterator, fn) {
        return {
            next: function () {
                while (true) {
                    var element = iterator.next();
                    if (element.done) {
                        return FIN;
                    }
                    if (fn(element.value)) {
                        return { done: false, value: element.value };
                    }
                }
            }
        };
    }
    Iterator.filter = filter;
    function forEach(iterator, fn) {
        for (var next = iterator.next(); !next.done; next = iterator.next()) {
            fn(next.value);
        }
    }
    Iterator.forEach = forEach;
    function collect(iterator) {
        var result = [];
        forEach(iterator, function (value) { return result.push(value); });
        return result;
    }
    Iterator.collect = collect;
})(Iterator || (Iterator = {}));
export function getSequenceIterator(arg) {
    if (Array.isArray(arg)) {
        return Iterator.fromArray(arg);
    }
    else {
        return arg;
    }
}
var ArrayIterator = /** @class */ (function () {
    function ArrayIterator(items, start, end, index) {
        if (start === void 0) { start = 0; }
        if (end === void 0) { end = items.length; }
        if (index === void 0) { index = start - 1; }
        this.items = items;
        this.start = start;
        this.end = end;
        this.index = index;
    }
    ArrayIterator.prototype.next = function () {
        this.index = Math.min(this.index + 1, this.end);
        return this.current();
    };
    ArrayIterator.prototype.current = function () {
        if (this.index === this.start - 1 || this.index === this.end) {
            return null;
        }
        return this.items[this.index];
    };
    return ArrayIterator;
}());
export { ArrayIterator };
var ArrayNavigator = /** @class */ (function (_super) {
    __extends(ArrayNavigator, _super);
    function ArrayNavigator(items, start, end, index) {
        if (start === void 0) { start = 0; }
        if (end === void 0) { end = items.length; }
        if (index === void 0) { index = start - 1; }
        return _super.call(this, items, start, end, index) || this;
    }
    ArrayNavigator.prototype.current = function () {
        return _super.prototype.current.call(this);
    };
    ArrayNavigator.prototype.previous = function () {
        this.index = Math.max(this.index - 1, this.start - 1);
        return this.current();
    };
    ArrayNavigator.prototype.first = function () {
        this.index = this.start;
        return this.current();
    };
    ArrayNavigator.prototype.last = function () {
        this.index = this.end - 1;
        return this.current();
    };
    ArrayNavigator.prototype.parent = function () {
        return null;
    };
    return ArrayNavigator;
}(ArrayIterator));
export { ArrayNavigator };
var MappedIterator = /** @class */ (function () {
    function MappedIterator(iterator, fn) {
        this.iterator = iterator;
        this.fn = fn;
        // noop
    }
    MappedIterator.prototype.next = function () { return this.fn(this.iterator.next()); };
    return MappedIterator;
}());
export { MappedIterator };
