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
import { Emitter } from './event.js';
import { Disposable } from './lifecycle.js';
var ScrollState = /** @class */ (function () {
    function ScrollState(width, scrollWidth, scrollLeft, height, scrollHeight, scrollTop) {
        width = width | 0;
        scrollWidth = scrollWidth | 0;
        scrollLeft = scrollLeft | 0;
        height = height | 0;
        scrollHeight = scrollHeight | 0;
        scrollTop = scrollTop | 0;
        if (width < 0) {
            width = 0;
        }
        if (scrollLeft + width > scrollWidth) {
            scrollLeft = scrollWidth - width;
        }
        if (scrollLeft < 0) {
            scrollLeft = 0;
        }
        if (height < 0) {
            height = 0;
        }
        if (scrollTop + height > scrollHeight) {
            scrollTop = scrollHeight - height;
        }
        if (scrollTop < 0) {
            scrollTop = 0;
        }
        this.width = width;
        this.scrollWidth = scrollWidth;
        this.scrollLeft = scrollLeft;
        this.height = height;
        this.scrollHeight = scrollHeight;
        this.scrollTop = scrollTop;
    }
    ScrollState.prototype.equals = function (other) {
        return (this.width === other.width
            && this.scrollWidth === other.scrollWidth
            && this.scrollLeft === other.scrollLeft
            && this.height === other.height
            && this.scrollHeight === other.scrollHeight
            && this.scrollTop === other.scrollTop);
    };
    ScrollState.prototype.withScrollDimensions = function (update) {
        return new ScrollState((typeof update.width !== 'undefined' ? update.width : this.width), (typeof update.scrollWidth !== 'undefined' ? update.scrollWidth : this.scrollWidth), this.scrollLeft, (typeof update.height !== 'undefined' ? update.height : this.height), (typeof update.scrollHeight !== 'undefined' ? update.scrollHeight : this.scrollHeight), this.scrollTop);
    };
    ScrollState.prototype.withScrollPosition = function (update) {
        return new ScrollState(this.width, this.scrollWidth, (typeof update.scrollLeft !== 'undefined' ? update.scrollLeft : this.scrollLeft), this.height, this.scrollHeight, (typeof update.scrollTop !== 'undefined' ? update.scrollTop : this.scrollTop));
    };
    ScrollState.prototype.createScrollEvent = function (previous) {
        var widthChanged = (this.width !== previous.width);
        var scrollWidthChanged = (this.scrollWidth !== previous.scrollWidth);
        var scrollLeftChanged = (this.scrollLeft !== previous.scrollLeft);
        var heightChanged = (this.height !== previous.height);
        var scrollHeightChanged = (this.scrollHeight !== previous.scrollHeight);
        var scrollTopChanged = (this.scrollTop !== previous.scrollTop);
        return {
            width: this.width,
            scrollWidth: this.scrollWidth,
            scrollLeft: this.scrollLeft,
            height: this.height,
            scrollHeight: this.scrollHeight,
            scrollTop: this.scrollTop,
            widthChanged: widthChanged,
            scrollWidthChanged: scrollWidthChanged,
            scrollLeftChanged: scrollLeftChanged,
            heightChanged: heightChanged,
            scrollHeightChanged: scrollHeightChanged,
            scrollTopChanged: scrollTopChanged,
        };
    };
    return ScrollState;
}());
export { ScrollState };
var Scrollable = /** @class */ (function (_super) {
    __extends(Scrollable, _super);
    function Scrollable(smoothScrollDuration, scheduleAtNextAnimationFrame) {
        var _this = _super.call(this) || this;
        _this._onScroll = _this._register(new Emitter());
        _this.onScroll = _this._onScroll.event;
        _this._smoothScrollDuration = smoothScrollDuration;
        _this._scheduleAtNextAnimationFrame = scheduleAtNextAnimationFrame;
        _this._state = new ScrollState(0, 0, 0, 0, 0, 0);
        _this._smoothScrolling = null;
        return _this;
    }
    Scrollable.prototype.dispose = function () {
        if (this._smoothScrolling) {
            this._smoothScrolling.dispose();
            this._smoothScrolling = null;
        }
        _super.prototype.dispose.call(this);
    };
    Scrollable.prototype.setSmoothScrollDuration = function (smoothScrollDuration) {
        this._smoothScrollDuration = smoothScrollDuration;
    };
    Scrollable.prototype.validateScrollPosition = function (scrollPosition) {
        return this._state.withScrollPosition(scrollPosition);
    };
    Scrollable.prototype.getScrollDimensions = function () {
        return this._state;
    };
    Scrollable.prototype.setScrollDimensions = function (dimensions) {
        var newState = this._state.withScrollDimensions(dimensions);
        this._setState(newState);
        // Validate outstanding animated scroll position target
        if (this._smoothScrolling) {
            this._smoothScrolling.acceptScrollDimensions(this._state);
        }
    };
    /**
     * Returns the final scroll position that the instance will have once the smooth scroll animation concludes.
     * If no scroll animation is occurring, it will return the current scroll position instead.
     */
    Scrollable.prototype.getFutureScrollPosition = function () {
        if (this._smoothScrolling) {
            return this._smoothScrolling.to;
        }
        return this._state;
    };
    /**
     * Returns the current scroll position.
     * Note: This result might be an intermediate scroll position, as there might be an ongoing smooth scroll animation.
     */
    Scrollable.prototype.getCurrentScrollPosition = function () {
        return this._state;
    };
    Scrollable.prototype.setScrollPositionNow = function (update) {
        // no smooth scrolling requested
        var newState = this._state.withScrollPosition(update);
        // Terminate any outstanding smooth scrolling
        if (this._smoothScrolling) {
            this._smoothScrolling.dispose();
            this._smoothScrolling = null;
        }
        this._setState(newState);
    };
    Scrollable.prototype.setScrollPositionSmooth = function (update) {
        var _this = this;
        if (this._smoothScrollDuration === 0) {
            // Smooth scrolling not supported.
            return this.setScrollPositionNow(update);
        }
        if (this._smoothScrolling) {
            // Combine our pending scrollLeft/scrollTop with incoming scrollLeft/scrollTop
            update = {
                scrollLeft: (typeof update.scrollLeft === 'undefined' ? this._smoothScrolling.to.scrollLeft : update.scrollLeft),
                scrollTop: (typeof update.scrollTop === 'undefined' ? this._smoothScrolling.to.scrollTop : update.scrollTop)
            };
            // Validate `update`
            var validTarget = this._state.withScrollPosition(update);
            if (this._smoothScrolling.to.scrollLeft === validTarget.scrollLeft && this._smoothScrolling.to.scrollTop === validTarget.scrollTop) {
                // No need to interrupt or extend the current animation since we're going to the same place
                return;
            }
            var newSmoothScrolling = this._smoothScrolling.combine(this._state, validTarget, this._smoothScrollDuration);
            this._smoothScrolling.dispose();
            this._smoothScrolling = newSmoothScrolling;
        }
        else {
            // Validate `update`
            var validTarget = this._state.withScrollPosition(update);
            this._smoothScrolling = SmoothScrollingOperation.start(this._state, validTarget, this._smoothScrollDuration);
        }
        // Begin smooth scrolling animation
        this._smoothScrolling.animationFrameDisposable = this._scheduleAtNextAnimationFrame(function () {
            if (!_this._smoothScrolling) {
                return;
            }
            _this._smoothScrolling.animationFrameDisposable = null;
            _this._performSmoothScrolling();
        });
    };
    Scrollable.prototype._performSmoothScrolling = function () {
        var _this = this;
        if (!this._smoothScrolling) {
            return;
        }
        var update = this._smoothScrolling.tick();
        var newState = this._state.withScrollPosition(update);
        this._setState(newState);
        if (update.isDone) {
            this._smoothScrolling.dispose();
            this._smoothScrolling = null;
            return;
        }
        // Continue smooth scrolling animation
        this._smoothScrolling.animationFrameDisposable = this._scheduleAtNextAnimationFrame(function () {
            if (!_this._smoothScrolling) {
                return;
            }
            _this._smoothScrolling.animationFrameDisposable = null;
            _this._performSmoothScrolling();
        });
    };
    Scrollable.prototype._setState = function (newState) {
        var oldState = this._state;
        if (oldState.equals(newState)) {
            // no change
            return;
        }
        this._state = newState;
        this._onScroll.fire(this._state.createScrollEvent(oldState));
    };
    return Scrollable;
}(Disposable));
export { Scrollable };
var SmoothScrollingUpdate = /** @class */ (function () {
    function SmoothScrollingUpdate(scrollLeft, scrollTop, isDone) {
        this.scrollLeft = scrollLeft;
        this.scrollTop = scrollTop;
        this.isDone = isDone;
    }
    return SmoothScrollingUpdate;
}());
export { SmoothScrollingUpdate };
function createEaseOutCubic(from, to) {
    var delta = to - from;
    return function (completion) {
        return from + delta * easeOutCubic(completion);
    };
}
function createComposed(a, b, cut) {
    return function (completion) {
        if (completion < cut) {
            return a(completion / cut);
        }
        return b((completion - cut) / (1 - cut));
    };
}
var SmoothScrollingOperation = /** @class */ (function () {
    function SmoothScrollingOperation(from, to, startTime, duration) {
        this.from = from;
        this.to = to;
        this.duration = duration;
        this._startTime = startTime;
        this.animationFrameDisposable = null;
        this._initAnimations();
    }
    SmoothScrollingOperation.prototype._initAnimations = function () {
        this.scrollLeft = this._initAnimation(this.from.scrollLeft, this.to.scrollLeft, this.to.width);
        this.scrollTop = this._initAnimation(this.from.scrollTop, this.to.scrollTop, this.to.height);
    };
    SmoothScrollingOperation.prototype._initAnimation = function (from, to, viewportSize) {
        var delta = Math.abs(from - to);
        if (delta > 2.5 * viewportSize) {
            var stop1 = void 0, stop2 = void 0;
            if (from < to) {
                // scroll to 75% of the viewportSize
                stop1 = from + 0.75 * viewportSize;
                stop2 = to - 0.75 * viewportSize;
            }
            else {
                stop1 = from - 0.75 * viewportSize;
                stop2 = to + 0.75 * viewportSize;
            }
            return createComposed(createEaseOutCubic(from, stop1), createEaseOutCubic(stop2, to), 0.33);
        }
        return createEaseOutCubic(from, to);
    };
    SmoothScrollingOperation.prototype.dispose = function () {
        if (this.animationFrameDisposable !== null) {
            this.animationFrameDisposable.dispose();
            this.animationFrameDisposable = null;
        }
    };
    SmoothScrollingOperation.prototype.acceptScrollDimensions = function (state) {
        this.to = state.withScrollPosition(this.to);
        this._initAnimations();
    };
    SmoothScrollingOperation.prototype.tick = function () {
        return this._tick(Date.now());
    };
    SmoothScrollingOperation.prototype._tick = function (now) {
        var completion = (now - this._startTime) / this.duration;
        if (completion < 1) {
            var newScrollLeft = this.scrollLeft(completion);
            var newScrollTop = this.scrollTop(completion);
            return new SmoothScrollingUpdate(newScrollLeft, newScrollTop, false);
        }
        return new SmoothScrollingUpdate(this.to.scrollLeft, this.to.scrollTop, true);
    };
    SmoothScrollingOperation.prototype.combine = function (from, to, duration) {
        return SmoothScrollingOperation.start(from, to, duration);
    };
    SmoothScrollingOperation.start = function (from, to, duration) {
        // +10 / -10 : pretend the animation already started for a quicker response to a scroll request
        duration = duration + 10;
        var startTime = Date.now() - 10;
        return new SmoothScrollingOperation(from, to, startTime, duration);
    };
    return SmoothScrollingOperation;
}());
export { SmoothScrollingOperation };
function easeInCubic(t) {
    return Math.pow(t, 3);
}
function easeOutCubic(t) {
    return 1 - easeInCubic(1 - t);
}
