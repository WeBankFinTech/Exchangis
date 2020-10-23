/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
import './tree.css';
import * as TreeDefaults from './treeDefaults.js';
import * as Model from './treeModel.js';
import * as View from './treeView.js';
import { Emitter, Relay } from '../../../common/event.js';
import { Color } from '../../../common/color.js';
import { mixin } from '../../../common/objects.js';
var TreeContext = /** @class */ (function () {
    function TreeContext(tree, configuration, options) {
        if (options === void 0) { options = {}; }
        this.tree = tree;
        this.configuration = configuration;
        this.options = options;
        if (!configuration.dataSource) {
            throw new Error('You must provide a Data Source to the tree.');
        }
        this.dataSource = configuration.dataSource;
        this.renderer = configuration.renderer;
        this.controller = configuration.controller || new TreeDefaults.DefaultController({ clickBehavior: 1 /* ON_MOUSE_UP */, keyboardSupport: typeof options.keyboardSupport !== 'boolean' || options.keyboardSupport });
        this.dnd = configuration.dnd || new TreeDefaults.DefaultDragAndDrop();
        this.filter = configuration.filter || new TreeDefaults.DefaultFilter();
        this.sorter = configuration.sorter;
        this.accessibilityProvider = configuration.accessibilityProvider || new TreeDefaults.DefaultAccessibilityProvider();
        this.styler = configuration.styler;
    }
    return TreeContext;
}());
export { TreeContext };
var defaultStyles = {
    listFocusBackground: Color.fromHex('#073655'),
    listActiveSelectionBackground: Color.fromHex('#0E639C'),
    listActiveSelectionForeground: Color.fromHex('#FFFFFF'),
    listFocusAndSelectionBackground: Color.fromHex('#094771'),
    listFocusAndSelectionForeground: Color.fromHex('#FFFFFF'),
    listInactiveSelectionBackground: Color.fromHex('#3F3F46'),
    listHoverBackground: Color.fromHex('#2A2D2E'),
    listDropBackground: Color.fromHex('#383B3D')
};
var Tree = /** @class */ (function () {
    function Tree(container, configuration, options) {
        if (options === void 0) { options = {}; }
        this._onDidChangeFocus = new Relay();
        this.onDidChangeFocus = this._onDidChangeFocus.event;
        this._onDidChangeSelection = new Relay();
        this.onDidChangeSelection = this._onDidChangeSelection.event;
        this._onHighlightChange = new Relay();
        this._onDidExpandItem = new Relay();
        this._onDidCollapseItem = new Relay();
        this._onDispose = new Emitter();
        this.onDidDispose = this._onDispose.event;
        this.container = container;
        mixin(options, defaultStyles, false);
        options.twistiePixels = typeof options.twistiePixels === 'number' ? options.twistiePixels : 32;
        options.showTwistie = options.showTwistie === false ? false : true;
        options.indentPixels = typeof options.indentPixels === 'number' ? options.indentPixels : 12;
        options.alwaysFocused = options.alwaysFocused === true ? true : false;
        options.useShadows = options.useShadows === false ? false : true;
        options.paddingOnRow = options.paddingOnRow === false ? false : true;
        options.showLoading = options.showLoading === false ? false : true;
        this.context = new TreeContext(this, configuration, options);
        this.model = new Model.TreeModel(this.context);
        this.view = new View.TreeView(this.context, this.container);
        this.view.setModel(this.model);
        this._onDidChangeFocus.input = this.model.onDidFocus;
        this._onDidChangeSelection.input = this.model.onDidSelect;
        this._onHighlightChange.input = this.model.onDidHighlight;
        this._onDidExpandItem.input = this.model.onDidExpandItem;
        this._onDidCollapseItem.input = this.model.onDidCollapseItem;
    }
    Tree.prototype.style = function (styles) {
        this.view.applyStyles(styles);
    };
    Object.defineProperty(Tree.prototype, "onDidFocus", {
        get: function () {
            return this.view && this.view.onDOMFocus;
        },
        enumerable: true,
        configurable: true
    });
    Tree.prototype.getHTMLElement = function () {
        return this.view.getHTMLElement();
    };
    Tree.prototype.layout = function (height, width) {
        this.view.layout(height, width);
    };
    Tree.prototype.domFocus = function () {
        this.view.focus();
    };
    Tree.prototype.isDOMFocused = function () {
        return this.view.isFocused();
    };
    Tree.prototype.domBlur = function () {
        this.view.blur();
    };
    Tree.prototype.setInput = function (element) {
        return this.model.setInput(element);
    };
    Tree.prototype.getInput = function () {
        return this.model.getInput();
    };
    Tree.prototype.expand = function (element) {
        return this.model.expand(element);
    };
    Tree.prototype.collapse = function (element, recursive) {
        if (recursive === void 0) { recursive = false; }
        return this.model.collapse(element, recursive);
    };
    Tree.prototype.toggleExpansion = function (element, recursive) {
        if (recursive === void 0) { recursive = false; }
        return this.model.toggleExpansion(element, recursive);
    };
    Tree.prototype.isExpanded = function (element) {
        return this.model.isExpanded(element);
    };
    Tree.prototype.reveal = function (element, relativeTop) {
        if (relativeTop === void 0) { relativeTop = null; }
        return this.model.reveal(element, relativeTop);
    };
    Tree.prototype.getHighlight = function () {
        return this.model.getHighlight();
    };
    Tree.prototype.clearHighlight = function (eventPayload) {
        this.model.setHighlight(null, eventPayload);
    };
    Tree.prototype.setSelection = function (elements, eventPayload) {
        this.model.setSelection(elements, eventPayload);
    };
    Tree.prototype.getSelection = function () {
        return this.model.getSelection();
    };
    Tree.prototype.clearSelection = function (eventPayload) {
        this.model.setSelection([], eventPayload);
    };
    Tree.prototype.setFocus = function (element, eventPayload) {
        this.model.setFocus(element, eventPayload);
    };
    Tree.prototype.getFocus = function () {
        return this.model.getFocus();
    };
    Tree.prototype.focusNext = function (count, eventPayload) {
        this.model.focusNext(count, eventPayload);
    };
    Tree.prototype.focusPrevious = function (count, eventPayload) {
        this.model.focusPrevious(count, eventPayload);
    };
    Tree.prototype.focusParent = function (eventPayload) {
        this.model.focusParent(eventPayload);
    };
    Tree.prototype.focusFirstChild = function (eventPayload) {
        this.model.focusFirstChild(eventPayload);
    };
    Tree.prototype.focusFirst = function (eventPayload, from) {
        this.model.focusFirst(eventPayload, from);
    };
    Tree.prototype.focusNth = function (index, eventPayload) {
        this.model.focusNth(index, eventPayload);
    };
    Tree.prototype.focusLast = function (eventPayload, from) {
        this.model.focusLast(eventPayload, from);
    };
    Tree.prototype.focusNextPage = function (eventPayload) {
        this.view.focusNextPage(eventPayload);
    };
    Tree.prototype.focusPreviousPage = function (eventPayload) {
        this.view.focusPreviousPage(eventPayload);
    };
    Tree.prototype.clearFocus = function (eventPayload) {
        this.model.setFocus(null, eventPayload);
    };
    Tree.prototype.dispose = function () {
        this._onDispose.fire();
        if (this.model !== null) {
            this.model.dispose();
            this.model = null; // StrictNullOverride Nulling out ok in dispose
        }
        if (this.view !== null) {
            this.view.dispose();
            this.view = null; // StrictNullOverride Nulling out ok in dispose
        }
        this._onDidChangeFocus.dispose();
        this._onDidChangeSelection.dispose();
        this._onHighlightChange.dispose();
        this._onDidExpandItem.dispose();
        this._onDidCollapseItem.dispose();
        this._onDispose.dispose();
    };
    return Tree;
}());
export { Tree };
