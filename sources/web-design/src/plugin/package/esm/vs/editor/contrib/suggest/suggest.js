/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
import { first } from '../../../base/common/async.js';
import { assign } from '../../../base/common/objects.js';
import { onUnexpectedExternalError, canceled, isPromiseCanceledError } from '../../../base/common/errors.js';
import { registerDefaultLanguageCommand } from '../../browser/editorExtensions.js';
import * as modes from '../../common/modes.js';
import { RawContextKey } from '../../../platform/contextkey/common/contextkey.js';
import { CancellationToken } from '../../../base/common/cancellation.js';
import { Range } from '../../common/core/range.js';
import { FuzzyScore } from '../../../base/common/filters.js';
export var Context = {
    Visible: new RawContextKey('suggestWidgetVisible', false),
    MultipleSuggestions: new RawContextKey('suggestWidgetMultipleSuggestions', false),
    MakesTextEdit: new RawContextKey('suggestionMakesTextEdit', true),
    AcceptSuggestionsOnEnter: new RawContextKey('acceptSuggestionOnEnter', true)
};
var CompletionItem = /** @class */ (function () {
    function CompletionItem(position, completion, container, provider, model) {
        this.position = position;
        this.completion = completion;
        this.container = container;
        this.provider = provider;
        // sorting, filtering
        this.score = FuzzyScore.Default;
        this.distance = 0;
        // ensure lower-variants (perf)
        this.labelLow = completion.label.toLowerCase();
        this.sortTextLow = completion.sortText && completion.sortText.toLowerCase();
        this.filterTextLow = completion.filterText && completion.filterText.toLowerCase();
        // create the suggestion resolver
        var resolveCompletionItem = provider.resolveCompletionItem;
        if (typeof resolveCompletionItem !== 'function') {
            this.resolve = function () { return Promise.resolve(); };
        }
        else {
            var cached_1;
            this.resolve = function (token) {
                if (!cached_1) {
                    var isDone_1 = false;
                    cached_1 = Promise.resolve(resolveCompletionItem.call(provider, model, position, completion, token)).then(function (value) {
                        assign(completion, value);
                        isDone_1 = true;
                    }, function (err) {
                        if (isPromiseCanceledError(err)) {
                            // the IPC queue will reject the request with the
                            // cancellation error -> reset cached
                            cached_1 = undefined;
                        }
                    });
                    token.onCancellationRequested(function () {
                        if (!isDone_1) {
                            // cancellation after the request has been
                            // dispatched -> reset cache
                            cached_1 = undefined;
                        }
                    });
                }
                return cached_1;
            };
        }
    }
    return CompletionItem;
}());
export { CompletionItem };
var CompletionOptions = /** @class */ (function () {
    function CompletionOptions(snippetSortOrder, kindFilter, providerFilter) {
        if (snippetSortOrder === void 0) { snippetSortOrder = 2 /* Bottom */; }
        if (kindFilter === void 0) { kindFilter = new Set(); }
        if (providerFilter === void 0) { providerFilter = new Set(); }
        this.snippetSortOrder = snippetSortOrder;
        this.kindFilter = kindFilter;
        this.providerFilter = providerFilter;
    }
    CompletionOptions.default = new CompletionOptions();
    return CompletionOptions;
}());
export { CompletionOptions };
var _snippetSuggestSupport;
export function getSnippetSuggestSupport() {
    return _snippetSuggestSupport;
}
export function provideSuggestionItems(model, position, options, context, token) {
    if (options === void 0) { options = CompletionOptions.default; }
    if (context === void 0) { context = { triggerKind: 0 /* Invoke */ }; }
    if (token === void 0) { token = CancellationToken.None; }
    var allSuggestions = [];
    var wordUntil = model.getWordUntilPosition(position);
    var defaultRange = new Range(position.lineNumber, wordUntil.startColumn, position.lineNumber, wordUntil.endColumn);
    position = position.clone();
    // get provider groups, always add snippet suggestion provider
    var supports = modes.CompletionProviderRegistry.orderedGroups(model);
    // add snippets provider unless turned off
    if (!options.kindFilter.has(25 /* Snippet */) && _snippetSuggestSupport) {
        supports.unshift([_snippetSuggestSupport]);
    }
    // add suggestions from contributed providers - providers are ordered in groups of
    // equal score and once a group produces a result the process stops
    var hasResult = false;
    var factory = supports.map(function (supports) { return function () {
        // for each support in the group ask for suggestions
        return Promise.all(supports.map(function (provider) {
            if (options.providerFilter.size > 0 && !options.providerFilter.has(provider)) {
                return undefined;
            }
            return Promise.resolve(provider.provideCompletionItems(model, position, context, token)).then(function (container) {
                var len = allSuggestions.length;
                if (container) {
                    for (var _i = 0, _a = container.suggestions || []; _i < _a.length; _i++) {
                        var suggestion = _a[_i];
                        if (!options.kindFilter.has(suggestion.kind)) {
                            // fill in default range when missing
                            if (!suggestion.range) {
                                suggestion.range = defaultRange;
                            }
                            allSuggestions.push(new CompletionItem(position, suggestion, container, provider, model));
                        }
                    }
                }
                if (len !== allSuggestions.length && provider !== _snippetSuggestSupport) {
                    hasResult = true;
                }
            }, onUnexpectedExternalError);
        }));
    }; });
    var result = first(factory, function () {
        // stop on result or cancellation
        return hasResult || token.isCancellationRequested;
    }).then(function () {
        if (token.isCancellationRequested) {
            return Promise.reject(canceled());
        }
        return allSuggestions.sort(getSuggestionComparator(options.snippetSortOrder));
    });
    // result.then(items => {
    // 	console.log(model.getWordUntilPosition(position), items.map(item => `${item.suggestion.label}, type=${item.suggestion.type}, incomplete?${item.container.incomplete}, overwriteBefore=${item.suggestion.overwriteBefore}`));
    // 	return items;
    // }, err => {
    // 	console.warn(model.getWordUntilPosition(position), err);
    // });
    return result;
}
function defaultComparator(a, b) {
    // check with 'sortText'
    if (a.sortTextLow && b.sortTextLow) {
        if (a.sortTextLow < b.sortTextLow) {
            return -1;
        }
        else if (a.sortTextLow > b.sortTextLow) {
            return 1;
        }
    }
    // check with 'label'
    if (a.completion.label < b.completion.label) {
        return -1;
    }
    else if (a.completion.label > b.completion.label) {
        return 1;
    }
    // check with 'type'
    return a.completion.kind - b.completion.kind;
}
function snippetUpComparator(a, b) {
    if (a.completion.kind !== b.completion.kind) {
        if (a.completion.kind === 25 /* Snippet */) {
            return -1;
        }
        else if (b.completion.kind === 25 /* Snippet */) {
            return 1;
        }
    }
    return defaultComparator(a, b);
}
function snippetDownComparator(a, b) {
    if (a.completion.kind !== b.completion.kind) {
        if (a.completion.kind === 25 /* Snippet */) {
            return 1;
        }
        else if (b.completion.kind === 25 /* Snippet */) {
            return -1;
        }
    }
    return defaultComparator(a, b);
}
var _snippetComparators = new Map();
_snippetComparators.set(0 /* Top */, snippetUpComparator);
_snippetComparators.set(2 /* Bottom */, snippetDownComparator);
_snippetComparators.set(1 /* Inline */, defaultComparator);
export function getSuggestionComparator(snippetConfig) {
    return _snippetComparators.get(snippetConfig);
}
registerDefaultLanguageCommand('_executeCompletionItemProvider', function (model, position, args) {
    var result = {
        incomplete: false,
        suggestions: []
    };
    var resolving = [];
    var maxItemsToResolve = args['maxItemsToResolve'] || 0;
    return provideSuggestionItems(model, position).then(function (items) {
        for (var _i = 0, items_1 = items; _i < items_1.length; _i++) {
            var item = items_1[_i];
            if (resolving.length < maxItemsToResolve) {
                resolving.push(item.resolve(CancellationToken.None));
            }
            result.incomplete = result.incomplete || item.container.incomplete;
            result.suggestions.push(item.completion);
        }
    }).then(function () {
        return Promise.all(resolving);
    }).then(function () {
        return result;
    });
});
var _provider = new /** @class */ (function () {
    function class_1() {
        this.onlyOnceSuggestions = [];
    }
    class_1.prototype.provideCompletionItems = function () {
        var suggestions = this.onlyOnceSuggestions.slice(0);
        var result = { suggestions: suggestions };
        this.onlyOnceSuggestions.length = 0;
        return result;
    };
    return class_1;
}());
modes.CompletionProviderRegistry.register('*', _provider);
export function showSimpleSuggestions(editor, suggestions) {
    setTimeout(function () {
        var _a;
        (_a = _provider.onlyOnceSuggestions).push.apply(_a, suggestions);
        editor.getContribution('editor.contrib.suggestController').triggerSuggest(new Set().add(_provider));
    }, 0);
}
