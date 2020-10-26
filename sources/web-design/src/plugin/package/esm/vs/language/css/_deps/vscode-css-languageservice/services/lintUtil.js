/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
'use strict';
import { includes } from '../utils/arrays.js';
var Element = /** @class */ (function () {
    function Element(text, data) {
        this.name = text;
        this.node = data;
    }
    return Element;
}());
export { Element };
function setSide(model, side, value, property) {
    var state = model[side];
    state.value = value;
    if (value) {
        if (!includes(state.properties, property)) {
            state.properties.push(property);
        }
    }
}
function setAllSides(model, value, property) {
    setSide(model, 'top', value, property);
    setSide(model, 'right', value, property);
    setSide(model, 'bottom', value, property);
    setSide(model, 'left', value, property);
}
function updateModelWithValue(model, side, value, property) {
    if (side === 'top' || side === 'right' ||
        side === 'bottom' || side === 'left') {
        setSide(model, side, value, property);
    }
    else {
        setAllSides(model, value, property);
    }
}
function updateModelWithList(model, values, property) {
    switch (values.length) {
        case 1:
            updateModelWithValue(model, undefined, values[0], property);
            break;
        case 2:
            updateModelWithValue(model, 'top', values[0], property);
            updateModelWithValue(model, 'bottom', values[0], property);
            updateModelWithValue(model, 'right', values[1], property);
            updateModelWithValue(model, 'left', values[1], property);
            break;
        case 3:
            updateModelWithValue(model, 'top', values[0], property);
            updateModelWithValue(model, 'right', values[1], property);
            updateModelWithValue(model, 'left', values[1], property);
            updateModelWithValue(model, 'bottom', values[2], property);
            break;
        case 4:
            updateModelWithValue(model, 'top', values[0], property);
            updateModelWithValue(model, 'right', values[1], property);
            updateModelWithValue(model, 'bottom', values[2], property);
            updateModelWithValue(model, 'left', values[3], property);
            break;
    }
}
/**
 * @param allowsKeywords whether the initial value of property is zero, so keywords `initial` and `unset` count as zero
 * @return `true` if this node represents a non-zero border; otherwise, `false`
 */
function checkLineWidth(value, allowsKeywords) {
    if (allowsKeywords === void 0) { allowsKeywords = true; }
    if (allowsKeywords && includes(['initial', 'unset'], value)) {
        return false;
    }
    // a <length> is a value and a unit
    // so use `parseFloat` to strip the unit
    return parseFloat(value) !== 0;
}
function checkLineWidthList(nodes, allowsKeywords) {
    if (allowsKeywords === void 0) { allowsKeywords = true; }
    return nodes.map(function (node) { return checkLineWidth(node.getText(), allowsKeywords); });
}
/**
 * @param allowsKeywords whether keywords `initial` and `unset` count as zero
 * @return `true` if this node represents a non-zero border; otherwise, `false`
 */
function checkLineStyle(value, allowsKeywords) {
    if (allowsKeywords === void 0) { allowsKeywords = true; }
    if (includes(['none', 'hidden'], value)) {
        return false;
    }
    if (allowsKeywords && includes(['initial', 'unset'], value)) {
        return false;
    }
    return true;
}
function checkLineStyleList(nodes, allowsKeywords) {
    if (allowsKeywords === void 0) { allowsKeywords = true; }
    return nodes.map(function (node) { return checkLineStyle(node.getText(), allowsKeywords); });
}
function checkBorderShorthand(node) {
    var children = node.getChildren();
    // the only child can be a keyword, a <line-width>, or a <line-style>
    // if either check returns false, the result is no border
    if (children.length === 1) {
        var value = children[0].getText();
        return checkLineWidth(value) && checkLineStyle(value);
    }
    // multiple children can't contain keywords
    // if any child means no border, the result is no border
    for (var _i = 0, children_1 = children; _i < children_1.length; _i++) {
        var child = children_1[_i];
        var value = child.getText();
        if (!checkLineWidth(value, /* allowsKeywords: */ false) ||
            !checkLineStyle(value, /* allowsKeywords: */ false)) {
            return false;
        }
    }
    return true;
}
export default function calculateBoxModel(propertyTable) {
    var model = {
        top: { value: false, properties: [] },
        right: { value: false, properties: [] },
        bottom: { value: false, properties: [] },
        left: { value: false, properties: [] },
    };
    for (var _i = 0, propertyTable_1 = propertyTable; _i < propertyTable_1.length; _i++) {
        var property = propertyTable_1[_i];
        var value = property.node.value;
        if (typeof value === 'undefined') {
            continue;
        }
        switch (property.name) {
            case 'box-sizing':
                // has `box-sizing`, bail out
                return {
                    top: { value: false, properties: [] },
                    right: { value: false, properties: [] },
                    bottom: { value: false, properties: [] },
                    left: { value: false, properties: [] },
                };
            case 'width':
                model.width = property;
                break;
            case 'height':
                model.height = property;
                break;
            default:
                var segments = property.name.split('-');
                switch (segments[0]) {
                    case 'border':
                        switch (segments[1]) {
                            case undefined:
                            case 'top':
                            case 'right':
                            case 'bottom':
                            case 'left':
                                switch (segments[2]) {
                                    case undefined:
                                        updateModelWithValue(model, segments[1], checkBorderShorthand(value), property);
                                        break;
                                    case 'width':
                                        // the initial value of `border-width` is `medium`, not zero
                                        updateModelWithValue(model, segments[1], checkLineWidth(value.getText(), false), property);
                                        break;
                                    case 'style':
                                        // the initial value of `border-style` is `none`
                                        updateModelWithValue(model, segments[1], checkLineStyle(value.getText(), true), property);
                                        break;
                                }
                                break;
                            case 'width':
                                // the initial value of `border-width` is `medium`, not zero
                                updateModelWithList(model, checkLineWidthList(value.getChildren(), false), property);
                                break;
                            case 'style':
                                // the initial value of `border-style` is `none`
                                updateModelWithList(model, checkLineStyleList(value.getChildren(), true), property);
                                break;
                        }
                        break;
                    case 'padding':
                        if (segments.length === 1) {
                            // the initial value of `padding` is zero
                            updateModelWithList(model, checkLineWidthList(value.getChildren(), true), property);
                        }
                        else {
                            // the initial value of `padding` is zero
                            updateModelWithValue(model, segments[1], checkLineWidth(value.getText(), true), property);
                        }
                        break;
                }
                break;
        }
    }
    return model;
}
