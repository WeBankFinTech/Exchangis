/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
import * as arrays from './arrays.js';
import * as strings from './strings.js';
import * as extpath from './extpath.js';
import * as paths from './path.js';
import { LRUCache } from './map.js';
import { isThenable } from './async.js';
var GLOBSTAR = '**';
var GLOB_SPLIT = '/';
var PATH_REGEX = '[/\\\\]'; // any slash or backslash
var NO_PATH_REGEX = '[^/\\\\]'; // any non-slash and non-backslash
var ALL_FORWARD_SLASHES = /\//g;
function starsToRegExp(starCount) {
    switch (starCount) {
        case 0:
            return '';
        case 1:
            return NO_PATH_REGEX + "*?"; // 1 star matches any number of characters except path separator (/ and \) - non greedy (?)
        default:
            // Matches:  (Path Sep OR Path Val followed by Path Sep OR Path Sep followed by Path Val) 0-many times
            // Group is non capturing because we don't need to capture at all (?:...)
            // Overall we use non-greedy matching because it could be that we match too much
            return "(?:" + PATH_REGEX + "|" + NO_PATH_REGEX + "+" + PATH_REGEX + "|" + PATH_REGEX + NO_PATH_REGEX + "+)*?";
    }
}
export function splitGlobAware(pattern, splitChar) {
    if (!pattern) {
        return [];
    }
    var segments = [];
    var inBraces = false;
    var inBrackets = false;
    var curVal = '';
    for (var _i = 0, pattern_1 = pattern; _i < pattern_1.length; _i++) {
        var char = pattern_1[_i];
        switch (char) {
            case splitChar:
                if (!inBraces && !inBrackets) {
                    segments.push(curVal);
                    curVal = '';
                    continue;
                }
                break;
            case '{':
                inBraces = true;
                break;
            case '}':
                inBraces = false;
                break;
            case '[':
                inBrackets = true;
                break;
            case ']':
                inBrackets = false;
                break;
        }
        curVal += char;
    }
    // Tail
    if (curVal) {
        segments.push(curVal);
    }
    return segments;
}
function parseRegExp(pattern) {
    if (!pattern) {
        return '';
    }
    var regEx = '';
    // Split up into segments for each slash found
    var segments = splitGlobAware(pattern, GLOB_SPLIT);
    // Special case where we only have globstars
    if (segments.every(function (s) { return s === GLOBSTAR; })) {
        regEx = '.*';
    }
    // Build regex over segments
    else {
        var previousSegmentWasGlobStar_1 = false;
        segments.forEach(function (segment, index) {
            // Globstar is special
            if (segment === GLOBSTAR) {
                // if we have more than one globstar after another, just ignore it
                if (!previousSegmentWasGlobStar_1) {
                    regEx += starsToRegExp(2);
                    previousSegmentWasGlobStar_1 = true;
                }
                return;
            }
            // States
            var inBraces = false;
            var braceVal = '';
            var inBrackets = false;
            var bracketVal = '';
            for (var _i = 0, segment_1 = segment; _i < segment_1.length; _i++) {
                var char = segment_1[_i];
                // Support brace expansion
                if (char !== '}' && inBraces) {
                    braceVal += char;
                    continue;
                }
                // Support brackets
                if (inBrackets && (char !== ']' || !bracketVal) /* ] is literally only allowed as first character in brackets to match it */) {
                    var res = void 0;
                    // range operator
                    if (char === '-') {
                        res = char;
                    }
                    // negation operator (only valid on first index in bracket)
                    else if ((char === '^' || char === '!') && !bracketVal) {
                        res = '^';
                    }
                    // glob split matching is not allowed within character ranges
                    // see http://man7.org/linux/man-pages/man7/glob.7.html
                    else if (char === GLOB_SPLIT) {
                        res = '';
                    }
                    // anything else gets escaped
                    else {
                        res = strings.escapeRegExpCharacters(char);
                    }
                    bracketVal += res;
                    continue;
                }
                switch (char) {
                    case '{':
                        inBraces = true;
                        continue;
                    case '[':
                        inBrackets = true;
                        continue;
                    case '}':
                        var choices = splitGlobAware(braceVal, ',');
                        // Converts {foo,bar} => [foo|bar]
                        var braceRegExp = "(?:" + choices.map(function (c) { return parseRegExp(c); }).join('|') + ")";
                        regEx += braceRegExp;
                        inBraces = false;
                        braceVal = '';
                        break;
                    case ']':
                        regEx += ('[' + bracketVal + ']');
                        inBrackets = false;
                        bracketVal = '';
                        break;
                    case '?':
                        regEx += NO_PATH_REGEX; // 1 ? matches any single character except path separator (/ and \)
                        continue;
                    case '*':
                        regEx += starsToRegExp(1);
                        continue;
                    default:
                        regEx += strings.escapeRegExpCharacters(char);
                }
            }
            // Tail: Add the slash we had split on if there is more to come and the remaining pattern is not a globstar
            // For example if pattern: some/**/*.js we want the "/" after some to be included in the RegEx to prevent
            // a folder called "something" to match as well.
            // However, if pattern: some/**, we tolerate that we also match on "something" because our globstar behaviour
            // is to match 0-N segments.
            if (index < segments.length - 1 && (segments[index + 1] !== GLOBSTAR || index + 2 < segments.length)) {
                regEx += PATH_REGEX;
            }
            // reset state
            previousSegmentWasGlobStar_1 = false;
        });
    }
    return regEx;
}
// regexes to check for trival glob patterns that just check for String#endsWith
var T1 = /^\*\*\/\*\.[\w\.-]+$/; // **/*.something
var T2 = /^\*\*\/([\w\.-]+)\/?$/; // **/something
var T3 = /^{\*\*\/[\*\.]?[\w\.-]+\/?(,\*\*\/[\*\.]?[\w\.-]+\/?)*}$/; // {**/*.something,**/*.else} or {**/package.json,**/project.json}
var T3_2 = /^{\*\*\/[\*\.]?[\w\.-]+(\/(\*\*)?)?(,\*\*\/[\*\.]?[\w\.-]+(\/(\*\*)?)?)*}$/; // Like T3, with optional trailing /**
var T4 = /^\*\*((\/[\w\.-]+)+)\/?$/; // **/something/else
var T5 = /^([\w\.-]+(\/[\w\.-]+)*)\/?$/; // something/else
var CACHE = new LRUCache(10000); // bounded to 10000 elements
var FALSE = function () {
    return false;
};
var NULL = function () {
    return null;
};
function parsePattern(arg1, options) {
    if (!arg1) {
        return NULL;
    }
    // Handle IRelativePattern
    var pattern;
    if (typeof arg1 !== 'string') {
        pattern = arg1.pattern;
    }
    else {
        pattern = arg1;
    }
    // Whitespace trimming
    pattern = pattern.trim();
    // Check cache
    var patternKey = pattern + "_" + !!options.trimForExclusions;
    var parsedPattern = CACHE.get(patternKey);
    if (parsedPattern) {
        return wrapRelativePattern(parsedPattern, arg1);
    }
    // Check for Trivias
    var match;
    if (T1.test(pattern)) { // common pattern: **/*.txt just need endsWith check
        var base_1 = pattern.substr(4); // '**/*'.length === 4
        parsedPattern = function (path, basename) {
            return typeof path === 'string' && strings.endsWith(path, base_1) ? pattern : null;
        };
    }
    else if (match = T2.exec(trimForExclusions(pattern, options))) { // common pattern: **/some.txt just need basename check
        parsedPattern = trivia2(match[1], pattern);
    }
    else if ((options.trimForExclusions ? T3_2 : T3).test(pattern)) { // repetition of common patterns (see above) {**/*.txt,**/*.png}
        parsedPattern = trivia3(pattern, options);
    }
    else if (match = T4.exec(trimForExclusions(pattern, options))) { // common pattern: **/something/else just need endsWith check
        parsedPattern = trivia4and5(match[1].substr(1), pattern, true);
    }
    else if (match = T5.exec(trimForExclusions(pattern, options))) { // common pattern: something/else just need equals check
        parsedPattern = trivia4and5(match[1], pattern, false);
    }
    // Otherwise convert to pattern
    else {
        parsedPattern = toRegExp(pattern);
    }
    // Cache
    CACHE.set(patternKey, parsedPattern);
    return wrapRelativePattern(parsedPattern, arg1);
}
function wrapRelativePattern(parsedPattern, arg2) {
    if (typeof arg2 === 'string') {
        return parsedPattern;
    }
    return function (path, basename) {
        if (!extpath.isEqualOrParent(path, arg2.base)) {
            return null;
        }
        return parsedPattern(paths.relative(arg2.base, path), basename);
    };
}
function trimForExclusions(pattern, options) {
    return options.trimForExclusions && strings.endsWith(pattern, '/**') ? pattern.substr(0, pattern.length - 2) : pattern; // dropping **, tailing / is dropped later
}
// common pattern: **/some.txt just need basename check
function trivia2(base, originalPattern) {
    var slashBase = "/" + base;
    var backslashBase = "\\" + base;
    var parsedPattern = function (path, basename) {
        if (typeof path !== 'string') {
            return null;
        }
        if (basename) {
            return basename === base ? originalPattern : null;
        }
        return path === base || strings.endsWith(path, slashBase) || strings.endsWith(path, backslashBase) ? originalPattern : null;
    };
    var basenames = [base];
    parsedPattern.basenames = basenames;
    parsedPattern.patterns = [originalPattern];
    parsedPattern.allBasenames = basenames;
    return parsedPattern;
}
// repetition of common patterns (see above) {**/*.txt,**/*.png}
function trivia3(pattern, options) {
    var parsedPatterns = aggregateBasenameMatches(pattern.slice(1, -1).split(',')
        .map(function (pattern) { return parsePattern(pattern, options); })
        .filter(function (pattern) { return pattern !== NULL; }), pattern);
    var n = parsedPatterns.length;
    if (!n) {
        return NULL;
    }
    if (n === 1) {
        return parsedPatterns[0];
    }
    var parsedPattern = function (path, basename) {
        for (var i = 0, n_1 = parsedPatterns.length; i < n_1; i++) {
            if (parsedPatterns[i](path, basename)) {
                return pattern;
            }
        }
        return null;
    };
    var withBasenames = arrays.first(parsedPatterns, function (pattern) { return !!pattern.allBasenames; });
    if (withBasenames) {
        parsedPattern.allBasenames = withBasenames.allBasenames;
    }
    var allPaths = parsedPatterns.reduce(function (all, current) { return current.allPaths ? all.concat(current.allPaths) : all; }, []);
    if (allPaths.length) {
        parsedPattern.allPaths = allPaths;
    }
    return parsedPattern;
}
// common patterns: **/something/else just need endsWith check, something/else just needs and equals check
function trivia4and5(path, pattern, matchPathEnds) {
    var nativePath = paths.sep !== paths.posix.sep ? path.replace(ALL_FORWARD_SLASHES, paths.sep) : path;
    var nativePathEnd = paths.sep + nativePath;
    var parsedPattern = matchPathEnds ? function (path, basename) {
        return typeof path === 'string' && (path === nativePath || strings.endsWith(path, nativePathEnd)) ? pattern : null;
    } : function (path, basename) {
        return typeof path === 'string' && path === nativePath ? pattern : null;
    };
    parsedPattern.allPaths = [(matchPathEnds ? '*/' : './') + path];
    return parsedPattern;
}
function toRegExp(pattern) {
    try {
        var regExp_1 = new RegExp("^" + parseRegExp(pattern) + "$");
        return function (path, basename) {
            regExp_1.lastIndex = 0; // reset RegExp to its initial state to reuse it!
            return typeof path === 'string' && regExp_1.test(path) ? pattern : null;
        };
    }
    catch (error) {
        return NULL;
    }
}
export function match(arg1, path, hasSibling) {
    if (!arg1 || typeof path !== 'string') {
        return false;
    }
    return parse(arg1)(path, undefined, hasSibling);
}
export function parse(arg1, options) {
    if (options === void 0) { options = {}; }
    if (!arg1) {
        return FALSE;
    }
    // Glob with String
    if (typeof arg1 === 'string' || isRelativePattern(arg1)) {
        var parsedPattern_1 = parsePattern(arg1, options);
        if (parsedPattern_1 === NULL) {
            return FALSE;
        }
        var resultPattern = function (path, basename) {
            return !!parsedPattern_1(path, basename);
        };
        if (parsedPattern_1.allBasenames) {
            resultPattern.allBasenames = parsedPattern_1.allBasenames;
        }
        if (parsedPattern_1.allPaths) {
            resultPattern.allPaths = parsedPattern_1.allPaths;
        }
        return resultPattern;
    }
    // Glob with Expression
    return parsedExpression(arg1, options);
}
export function isRelativePattern(obj) {
    var rp = obj;
    return rp && typeof rp.base === 'string' && typeof rp.pattern === 'string';
}
function parsedExpression(expression, options) {
    var parsedPatterns = aggregateBasenameMatches(Object.getOwnPropertyNames(expression)
        .map(function (pattern) { return parseExpressionPattern(pattern, expression[pattern], options); })
        .filter(function (pattern) { return pattern !== NULL; }));
    var n = parsedPatterns.length;
    if (!n) {
        return NULL;
    }
    if (!parsedPatterns.some(function (parsedPattern) { return !!parsedPattern.requiresSiblings; })) {
        if (n === 1) {
            return parsedPatterns[0];
        }
        var resultExpression_1 = function (path, basename) {
            for (var i = 0, n_2 = parsedPatterns.length; i < n_2; i++) {
                // Pattern matches path
                var result = parsedPatterns[i](path, basename);
                if (result) {
                    return result;
                }
            }
            return null;
        };
        var withBasenames_1 = arrays.first(parsedPatterns, function (pattern) { return !!pattern.allBasenames; });
        if (withBasenames_1) {
            resultExpression_1.allBasenames = withBasenames_1.allBasenames;
        }
        var allPaths_1 = parsedPatterns.reduce(function (all, current) { return current.allPaths ? all.concat(current.allPaths) : all; }, []);
        if (allPaths_1.length) {
            resultExpression_1.allPaths = allPaths_1;
        }
        return resultExpression_1;
    }
    var resultExpression = function (path, basename, hasSibling) {
        var name = undefined;
        for (var i = 0, n_3 = parsedPatterns.length; i < n_3; i++) {
            // Pattern matches path
            var parsedPattern = parsedPatterns[i];
            if (parsedPattern.requiresSiblings && hasSibling) {
                if (!basename) {
                    basename = paths.basename(path);
                }
                if (!name) {
                    name = basename.substr(0, basename.length - paths.extname(path).length);
                }
            }
            var result = parsedPattern(path, basename, name, hasSibling);
            if (result) {
                return result;
            }
        }
        return null;
    };
    var withBasenames = arrays.first(parsedPatterns, function (pattern) { return !!pattern.allBasenames; });
    if (withBasenames) {
        resultExpression.allBasenames = withBasenames.allBasenames;
    }
    var allPaths = parsedPatterns.reduce(function (all, current) { return current.allPaths ? all.concat(current.allPaths) : all; }, []);
    if (allPaths.length) {
        resultExpression.allPaths = allPaths;
    }
    return resultExpression;
}
function parseExpressionPattern(pattern, value, options) {
    if (value === false) {
        return NULL; // pattern is disabled
    }
    var parsedPattern = parsePattern(pattern, options);
    if (parsedPattern === NULL) {
        return NULL;
    }
    // Expression Pattern is <boolean>
    if (typeof value === 'boolean') {
        return parsedPattern;
    }
    // Expression Pattern is <SiblingClause>
    if (value) {
        var when_1 = value.when;
        if (typeof when_1 === 'string') {
            var result = function (path, basename, name, hasSibling) {
                if (!hasSibling || !parsedPattern(path, basename)) {
                    return null;
                }
                var clausePattern = when_1.replace('$(basename)', name);
                var matched = hasSibling(clausePattern);
                return isThenable(matched) ?
                    matched.then(function (m) { return m ? pattern : null; }) :
                    matched ? pattern : null;
            };
            result.requiresSiblings = true;
            return result;
        }
    }
    // Expression is Anything
    return parsedPattern;
}
function aggregateBasenameMatches(parsedPatterns, result) {
    var basenamePatterns = parsedPatterns.filter(function (parsedPattern) { return !!parsedPattern.basenames; });
    if (basenamePatterns.length < 2) {
        return parsedPatterns;
    }
    var basenames = basenamePatterns.reduce(function (all, current) {
        var basenames = current.basenames;
        return basenames ? all.concat(basenames) : all;
    }, []);
    var patterns;
    if (result) {
        patterns = [];
        for (var i = 0, n = basenames.length; i < n; i++) {
            patterns.push(result);
        }
    }
    else {
        patterns = basenamePatterns.reduce(function (all, current) {
            var patterns = current.patterns;
            return patterns ? all.concat(patterns) : all;
        }, []);
    }
    var aggregate = function (path, basename) {
        if (typeof path !== 'string') {
            return null;
        }
        if (!basename) {
            var i = void 0;
            for (i = path.length; i > 0; i--) {
                var ch = path.charCodeAt(i - 1);
                if (ch === 47 /* Slash */ || ch === 92 /* Backslash */) {
                    break;
                }
            }
            basename = path.substr(i);
        }
        var index = basenames.indexOf(basename);
        return index !== -1 ? patterns[index] : null;
    };
    aggregate.basenames = basenames;
    aggregate.patterns = patterns;
    aggregate.allBasenames = basenames;
    var aggregatedPatterns = parsedPatterns.filter(function (parsedPattern) { return !parsedPattern.basenames; });
    aggregatedPatterns.push(aggregate);
    return aggregatedPatterns;
}
