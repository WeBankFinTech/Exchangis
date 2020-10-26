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
import * as browser from '../../../base/browser/browser.js';
import { Emitter } from '../../../base/common/event.js';
import { Disposable } from '../../../base/common/lifecycle.js';
import * as platform from '../../../base/common/platform.js';
import { CharWidthRequest, readCharWidths } from './charWidthReader.js';
import { ElementSizeObserver } from './elementSizeObserver.js';
import { CommonEditorConfiguration } from '../../common/config/commonEditorConfig.js';
import { FontInfo } from '../../common/config/fontInfo.js';
var CSSBasedConfigurationCache = /** @class */ (function () {
    function CSSBasedConfigurationCache() {
        this._keys = Object.create(null);
        this._values = Object.create(null);
    }
    CSSBasedConfigurationCache.prototype.has = function (item) {
        var itemId = item.getId();
        return !!this._values[itemId];
    };
    CSSBasedConfigurationCache.prototype.get = function (item) {
        var itemId = item.getId();
        return this._values[itemId];
    };
    CSSBasedConfigurationCache.prototype.put = function (item, value) {
        var itemId = item.getId();
        this._keys[itemId] = item;
        this._values[itemId] = value;
    };
    CSSBasedConfigurationCache.prototype.remove = function (item) {
        var itemId = item.getId();
        delete this._keys[itemId];
        delete this._values[itemId];
    };
    CSSBasedConfigurationCache.prototype.getValues = function () {
        var _this = this;
        return Object.keys(this._keys).map(function (id) { return _this._values[id]; });
    };
    return CSSBasedConfigurationCache;
}());
export function clearAllFontInfos() {
    CSSBasedConfiguration.INSTANCE.clearCache();
}
var CSSBasedConfiguration = /** @class */ (function (_super) {
    __extends(CSSBasedConfiguration, _super);
    function CSSBasedConfiguration() {
        var _this = _super.call(this) || this;
        _this._onDidChange = _this._register(new Emitter());
        _this.onDidChange = _this._onDidChange.event;
        _this._cache = new CSSBasedConfigurationCache();
        _this._evictUntrustedReadingsTimeout = -1;
        return _this;
    }
    CSSBasedConfiguration.prototype.dispose = function () {
        if (this._evictUntrustedReadingsTimeout !== -1) {
            clearTimeout(this._evictUntrustedReadingsTimeout);
            this._evictUntrustedReadingsTimeout = -1;
        }
        _super.prototype.dispose.call(this);
    };
    CSSBasedConfiguration.prototype.clearCache = function () {
        this._cache = new CSSBasedConfigurationCache();
        this._onDidChange.fire();
    };
    CSSBasedConfiguration.prototype._writeToCache = function (item, value) {
        var _this = this;
        this._cache.put(item, value);
        if (!value.isTrusted && this._evictUntrustedReadingsTimeout === -1) {
            // Try reading again after some time
            this._evictUntrustedReadingsTimeout = setTimeout(function () {
                _this._evictUntrustedReadingsTimeout = -1;
                _this._evictUntrustedReadings();
            }, 5000);
        }
    };
    CSSBasedConfiguration.prototype._evictUntrustedReadings = function () {
        var values = this._cache.getValues();
        var somethingRemoved = false;
        for (var i = 0, len = values.length; i < len; i++) {
            var item = values[i];
            if (!item.isTrusted) {
                somethingRemoved = true;
                this._cache.remove(item);
            }
        }
        if (somethingRemoved) {
            this._onDidChange.fire();
        }
    };
    CSSBasedConfiguration.prototype.readConfiguration = function (bareFontInfo) {
        if (!this._cache.has(bareFontInfo)) {
            var readConfig = CSSBasedConfiguration._actualReadConfiguration(bareFontInfo);
            if (readConfig.typicalHalfwidthCharacterWidth <= 2 || readConfig.typicalFullwidthCharacterWidth <= 2 || readConfig.spaceWidth <= 2 || readConfig.maxDigitWidth <= 2) {
                // Hey, it's Bug 14341 ... we couldn't read
                readConfig = new FontInfo({
                    zoomLevel: browser.getZoomLevel(),
                    fontFamily: readConfig.fontFamily,
                    fontWeight: readConfig.fontWeight,
                    fontSize: readConfig.fontSize,
                    lineHeight: readConfig.lineHeight,
                    letterSpacing: readConfig.letterSpacing,
                    isMonospace: readConfig.isMonospace,
                    typicalHalfwidthCharacterWidth: Math.max(readConfig.typicalHalfwidthCharacterWidth, 5),
                    typicalFullwidthCharacterWidth: Math.max(readConfig.typicalFullwidthCharacterWidth, 5),
                    canUseHalfwidthRightwardsArrow: readConfig.canUseHalfwidthRightwardsArrow,
                    spaceWidth: Math.max(readConfig.spaceWidth, 5),
                    maxDigitWidth: Math.max(readConfig.maxDigitWidth, 5),
                }, false);
            }
            this._writeToCache(bareFontInfo, readConfig);
        }
        return this._cache.get(bareFontInfo);
    };
    CSSBasedConfiguration.createRequest = function (chr, type, all, monospace) {
        var result = new CharWidthRequest(chr, type);
        all.push(result);
        if (monospace) {
            monospace.push(result);
        }
        return result;
    };
    CSSBasedConfiguration._actualReadConfiguration = function (bareFontInfo) {
        var all = [];
        var monospace = [];
        var typicalHalfwidthCharacter = this.createRequest('n', 0 /* Regular */, all, monospace);
        var typicalFullwidthCharacter = this.createRequest('\uff4d', 0 /* Regular */, all, null);
        var space = this.createRequest(' ', 0 /* Regular */, all, monospace);
        var digit0 = this.createRequest('0', 0 /* Regular */, all, monospace);
        var digit1 = this.createRequest('1', 0 /* Regular */, all, monospace);
        var digit2 = this.createRequest('2', 0 /* Regular */, all, monospace);
        var digit3 = this.createRequest('3', 0 /* Regular */, all, monospace);
        var digit4 = this.createRequest('4', 0 /* Regular */, all, monospace);
        var digit5 = this.createRequest('5', 0 /* Regular */, all, monospace);
        var digit6 = this.createRequest('6', 0 /* Regular */, all, monospace);
        var digit7 = this.createRequest('7', 0 /* Regular */, all, monospace);
        var digit8 = this.createRequest('8', 0 /* Regular */, all, monospace);
        var digit9 = this.createRequest('9', 0 /* Regular */, all, monospace);
        // monospace test: used for whitespace rendering
        var rightwardsArrow = this.createRequest('→', 0 /* Regular */, all, monospace);
        var halfwidthRightwardsArrow = this.createRequest('￫', 0 /* Regular */, all, null);
        this.createRequest('·', 0 /* Regular */, all, monospace);
        // monospace test: some characters
        this.createRequest('|', 0 /* Regular */, all, monospace);
        this.createRequest('/', 0 /* Regular */, all, monospace);
        this.createRequest('-', 0 /* Regular */, all, monospace);
        this.createRequest('_', 0 /* Regular */, all, monospace);
        this.createRequest('i', 0 /* Regular */, all, monospace);
        this.createRequest('l', 0 /* Regular */, all, monospace);
        this.createRequest('m', 0 /* Regular */, all, monospace);
        // monospace italic test
        this.createRequest('|', 1 /* Italic */, all, monospace);
        this.createRequest('_', 1 /* Italic */, all, monospace);
        this.createRequest('i', 1 /* Italic */, all, monospace);
        this.createRequest('l', 1 /* Italic */, all, monospace);
        this.createRequest('m', 1 /* Italic */, all, monospace);
        this.createRequest('n', 1 /* Italic */, all, monospace);
        // monospace bold test
        this.createRequest('|', 2 /* Bold */, all, monospace);
        this.createRequest('_', 2 /* Bold */, all, monospace);
        this.createRequest('i', 2 /* Bold */, all, monospace);
        this.createRequest('l', 2 /* Bold */, all, monospace);
        this.createRequest('m', 2 /* Bold */, all, monospace);
        this.createRequest('n', 2 /* Bold */, all, monospace);
        readCharWidths(bareFontInfo, all);
        var maxDigitWidth = Math.max(digit0.width, digit1.width, digit2.width, digit3.width, digit4.width, digit5.width, digit6.width, digit7.width, digit8.width, digit9.width);
        var isMonospace = true;
        var referenceWidth = monospace[0].width;
        for (var i = 1, len = monospace.length; i < len; i++) {
            var diff = referenceWidth - monospace[i].width;
            if (diff < -0.001 || diff > 0.001) {
                isMonospace = false;
                break;
            }
        }
        var canUseHalfwidthRightwardsArrow = true;
        if (isMonospace && halfwidthRightwardsArrow.width !== referenceWidth) {
            // using a halfwidth rightwards arrow would break monospace...
            canUseHalfwidthRightwardsArrow = false;
        }
        if (halfwidthRightwardsArrow.width > rightwardsArrow.width) {
            // using a halfwidth rightwards arrow would paint a larger arrow than a regular rightwards arrow
            canUseHalfwidthRightwardsArrow = false;
        }
        // let's trust the zoom level only 2s after it was changed.
        var canTrustBrowserZoomLevel = (browser.getTimeSinceLastZoomLevelChanged() > 2000);
        return new FontInfo({
            zoomLevel: browser.getZoomLevel(),
            fontFamily: bareFontInfo.fontFamily,
            fontWeight: bareFontInfo.fontWeight,
            fontSize: bareFontInfo.fontSize,
            lineHeight: bareFontInfo.lineHeight,
            letterSpacing: bareFontInfo.letterSpacing,
            isMonospace: isMonospace,
            typicalHalfwidthCharacterWidth: typicalHalfwidthCharacter.width,
            typicalFullwidthCharacterWidth: typicalFullwidthCharacter.width,
            canUseHalfwidthRightwardsArrow: canUseHalfwidthRightwardsArrow,
            spaceWidth: space.width,
            maxDigitWidth: maxDigitWidth
        }, canTrustBrowserZoomLevel);
    };
    CSSBasedConfiguration.INSTANCE = new CSSBasedConfiguration();
    return CSSBasedConfiguration;
}(Disposable));
var Configuration = /** @class */ (function (_super) {
    __extends(Configuration, _super);
    function Configuration(isSimpleWidget, options, referenceDomElement, accessibilityService) {
        if (referenceDomElement === void 0) { referenceDomElement = null; }
        var _this = _super.call(this, isSimpleWidget, options) || this;
        _this.accessibilityService = accessibilityService;
        _this._elementSizeObserver = _this._register(new ElementSizeObserver(referenceDomElement, function () { return _this._onReferenceDomElementSizeChanged(); }));
        _this._register(CSSBasedConfiguration.INSTANCE.onDidChange(function () { return _this._onCSSBasedConfigurationChanged(); }));
        if (_this._validatedOptions.automaticLayout) {
            _this._elementSizeObserver.startObserving();
        }
        _this._register(browser.onDidChangeZoomLevel(function (_) { return _this._recomputeOptions(); }));
        _this._register(_this.accessibilityService.onDidChangeAccessibilitySupport(function () { return _this._recomputeOptions(); }));
        _this._recomputeOptions();
        return _this;
    }
    Configuration.applyFontInfoSlow = function (domNode, fontInfo) {
        domNode.style.fontFamily = fontInfo.getMassagedFontFamily();
        domNode.style.fontWeight = fontInfo.fontWeight;
        domNode.style.fontSize = fontInfo.fontSize + 'px';
        domNode.style.lineHeight = fontInfo.lineHeight + 'px';
        domNode.style.letterSpacing = fontInfo.letterSpacing + 'px';
    };
    Configuration.applyFontInfo = function (domNode, fontInfo) {
        domNode.setFontFamily(fontInfo.getMassagedFontFamily());
        domNode.setFontWeight(fontInfo.fontWeight);
        domNode.setFontSize(fontInfo.fontSize);
        domNode.setLineHeight(fontInfo.lineHeight);
        domNode.setLetterSpacing(fontInfo.letterSpacing);
    };
    Configuration.prototype._onReferenceDomElementSizeChanged = function () {
        this._recomputeOptions();
    };
    Configuration.prototype._onCSSBasedConfigurationChanged = function () {
        this._recomputeOptions();
    };
    Configuration.prototype.observeReferenceElement = function (dimension) {
        this._elementSizeObserver.observe(dimension);
    };
    Configuration.prototype.dispose = function () {
        _super.prototype.dispose.call(this);
    };
    Configuration.prototype._getExtraEditorClassName = function () {
        var extra = '';
        if (!browser.isSafari && !browser.isWebkitWebView) {
            // Use user-select: none in all browsers except Safari and native macOS WebView
            extra += 'no-user-select ';
        }
        if (platform.isMacintosh) {
            extra += 'mac ';
        }
        return extra;
    };
    Configuration.prototype._getEnvConfiguration = function () {
        return {
            extraEditorClassName: this._getExtraEditorClassName(),
            outerWidth: this._elementSizeObserver.getWidth(),
            outerHeight: this._elementSizeObserver.getHeight(),
            emptySelectionClipboard: browser.isWebKit || browser.isFirefox,
            pixelRatio: browser.getPixelRatio(),
            zoomLevel: browser.getZoomLevel(),
            accessibilitySupport: this.accessibilityService.getAccessibilitySupport()
        };
    };
    Configuration.prototype.readConfiguration = function (bareFontInfo) {
        return CSSBasedConfiguration.INSTANCE.readConfiguration(bareFontInfo);
    };
    return Configuration;
}(CommonEditorConfiguration));
export { Configuration };
