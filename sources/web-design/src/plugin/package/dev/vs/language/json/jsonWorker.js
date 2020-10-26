(function (factory) {
    if (typeof module === "object" && typeof module.exports === "object") {
        var v = factory(require, exports);
        if (v !== undefined) module.exports = v;
    }
    else if (typeof define === "function" && define.amd) {
        define('vscode-languageserver-types/main',["require", "exports"], factory);
    }
})(function (require, exports) {
    /* --------------------------------------------------------------------------------------------
     * Copyright (c) Microsoft Corporation. All rights reserved.
     * Licensed under the MIT License. See License.txt in the project root for license information.
     * ------------------------------------------------------------------------------------------ */
    'use strict';
    Object.defineProperty(exports, "__esModule", { value: true });
    /**
     * The Position namespace provides helper functions to work with
     * [Position](#Position) literals.
     */
    var Position;
    (function (Position) {
        /**
         * Creates a new Position literal from the given line and character.
         * @param line The position's line.
         * @param character The position's character.
         */
        function create(line, character) {
            return { line: line, character: character };
        }
        Position.create = create;
        /**
         * Checks whether the given liternal conforms to the [Position](#Position) interface.
         */
        function is(value) {
            var candidate = value;
            return Is.objectLiteral(candidate) && Is.number(candidate.line) && Is.number(candidate.character);
        }
        Position.is = is;
    })(Position = exports.Position || (exports.Position = {}));
    /**
     * The Range namespace provides helper functions to work with
     * [Range](#Range) literals.
     */
    var Range;
    (function (Range) {
        function create(one, two, three, four) {
            if (Is.number(one) && Is.number(two) && Is.number(three) && Is.number(four)) {
                return { start: Position.create(one, two), end: Position.create(three, four) };
            }
            else if (Position.is(one) && Position.is(two)) {
                return { start: one, end: two };
            }
            else {
                throw new Error("Range#create called with invalid arguments[" + one + ", " + two + ", " + three + ", " + four + "]");
            }
        }
        Range.create = create;
        /**
         * Checks whether the given literal conforms to the [Range](#Range) interface.
         */
        function is(value) {
            var candidate = value;
            return Is.objectLiteral(candidate) && Position.is(candidate.start) && Position.is(candidate.end);
        }
        Range.is = is;
    })(Range = exports.Range || (exports.Range = {}));
    /**
     * The Location namespace provides helper functions to work with
     * [Location](#Location) literals.
     */
    var Location;
    (function (Location) {
        /**
         * Creates a Location literal.
         * @param uri The location's uri.
         * @param range The location's range.
         */
        function create(uri, range) {
            return { uri: uri, range: range };
        }
        Location.create = create;
        /**
         * Checks whether the given literal conforms to the [Location](#Location) interface.
         */
        function is(value) {
            var candidate = value;
            return Is.defined(candidate) && Range.is(candidate.range) && (Is.string(candidate.uri) || Is.undefined(candidate.uri));
        }
        Location.is = is;
    })(Location = exports.Location || (exports.Location = {}));
    /**
     * The LocationLink namespace provides helper functions to work with
     * [LocationLink](#LocationLink) literals.
     */
    var LocationLink;
    (function (LocationLink) {
        /**
         * Creates a LocationLink literal.
         * @param targetUri The definition's uri.
         * @param targetRange The full range of the definition.
         * @param targetSelectionRange The span of the symbol definition at the target.
         * @param originSelectionRange The span of the symbol being defined in the originating source file.
         */
        function create(targetUri, targetRange, targetSelectionRange, originSelectionRange) {
            return { targetUri: targetUri, targetRange: targetRange, targetSelectionRange: targetSelectionRange, originSelectionRange: originSelectionRange };
        }
        LocationLink.create = create;
        /**
         * Checks whether the given literal conforms to the [LocationLink](#LocationLink) interface.
         */
        function is(value) {
            var candidate = value;
            return Is.defined(candidate) && Range.is(candidate.targetRange) && Is.string(candidate.targetUri)
                && (Range.is(candidate.targetSelectionRange) || Is.undefined(candidate.targetSelectionRange))
                && (Range.is(candidate.originSelectionRange) || Is.undefined(candidate.originSelectionRange));
        }
        LocationLink.is = is;
    })(LocationLink = exports.LocationLink || (exports.LocationLink = {}));
    /**
     * The Color namespace provides helper functions to work with
     * [Color](#Color) literals.
     */
    var Color;
    (function (Color) {
        /**
         * Creates a new Color literal.
         */
        function create(red, green, blue, alpha) {
            return {
                red: red,
                green: green,
                blue: blue,
                alpha: alpha,
            };
        }
        Color.create = create;
        /**
         * Checks whether the given literal conforms to the [Color](#Color) interface.
         */
        function is(value) {
            var candidate = value;
            return Is.number(candidate.red)
                && Is.number(candidate.green)
                && Is.number(candidate.blue)
                && Is.number(candidate.alpha);
        }
        Color.is = is;
    })(Color = exports.Color || (exports.Color = {}));
    /**
     * The ColorInformation namespace provides helper functions to work with
     * [ColorInformation](#ColorInformation) literals.
     */
    var ColorInformation;
    (function (ColorInformation) {
        /**
         * Creates a new ColorInformation literal.
         */
        function create(range, color) {
            return {
                range: range,
                color: color,
            };
        }
        ColorInformation.create = create;
        /**
         * Checks whether the given literal conforms to the [ColorInformation](#ColorInformation) interface.
         */
        function is(value) {
            var candidate = value;
            return Range.is(candidate.range) && Color.is(candidate.color);
        }
        ColorInformation.is = is;
    })(ColorInformation = exports.ColorInformation || (exports.ColorInformation = {}));
    /**
     * The Color namespace provides helper functions to work with
     * [ColorPresentation](#ColorPresentation) literals.
     */
    var ColorPresentation;
    (function (ColorPresentation) {
        /**
         * Creates a new ColorInformation literal.
         */
        function create(label, textEdit, additionalTextEdits) {
            return {
                label: label,
                textEdit: textEdit,
                additionalTextEdits: additionalTextEdits,
            };
        }
        ColorPresentation.create = create;
        /**
         * Checks whether the given literal conforms to the [ColorInformation](#ColorInformation) interface.
         */
        function is(value) {
            var candidate = value;
            return Is.string(candidate.label)
                && (Is.undefined(candidate.textEdit) || TextEdit.is(candidate))
                && (Is.undefined(candidate.additionalTextEdits) || Is.typedArray(candidate.additionalTextEdits, TextEdit.is));
        }
        ColorPresentation.is = is;
    })(ColorPresentation = exports.ColorPresentation || (exports.ColorPresentation = {}));
    /**
     * Enum of known range kinds
     */
    var FoldingRangeKind;
    (function (FoldingRangeKind) {
        /**
         * Folding range for a comment
         */
        FoldingRangeKind["Comment"] = "comment";
        /**
         * Folding range for a imports or includes
         */
        FoldingRangeKind["Imports"] = "imports";
        /**
         * Folding range for a region (e.g. `#region`)
         */
        FoldingRangeKind["Region"] = "region";
    })(FoldingRangeKind = exports.FoldingRangeKind || (exports.FoldingRangeKind = {}));
    /**
     * The folding range namespace provides helper functions to work with
     * [FoldingRange](#FoldingRange) literals.
     */
    var FoldingRange;
    (function (FoldingRange) {
        /**
         * Creates a new FoldingRange literal.
         */
        function create(startLine, endLine, startCharacter, endCharacter, kind) {
            var result = {
                startLine: startLine,
                endLine: endLine
            };
            if (Is.defined(startCharacter)) {
                result.startCharacter = startCharacter;
            }
            if (Is.defined(endCharacter)) {
                result.endCharacter = endCharacter;
            }
            if (Is.defined(kind)) {
                result.kind = kind;
            }
            return result;
        }
        FoldingRange.create = create;
        /**
         * Checks whether the given literal conforms to the [FoldingRange](#FoldingRange) interface.
         */
        function is(value) {
            var candidate = value;
            return Is.number(candidate.startLine) && Is.number(candidate.startLine)
                && (Is.undefined(candidate.startCharacter) || Is.number(candidate.startCharacter))
                && (Is.undefined(candidate.endCharacter) || Is.number(candidate.endCharacter))
                && (Is.undefined(candidate.kind) || Is.string(candidate.kind));
        }
        FoldingRange.is = is;
    })(FoldingRange = exports.FoldingRange || (exports.FoldingRange = {}));
    /**
     * The DiagnosticRelatedInformation namespace provides helper functions to work with
     * [DiagnosticRelatedInformation](#DiagnosticRelatedInformation) literals.
     */
    var DiagnosticRelatedInformation;
    (function (DiagnosticRelatedInformation) {
        /**
         * Creates a new DiagnosticRelatedInformation literal.
         */
        function create(location, message) {
            return {
                location: location,
                message: message
            };
        }
        DiagnosticRelatedInformation.create = create;
        /**
         * Checks whether the given literal conforms to the [DiagnosticRelatedInformation](#DiagnosticRelatedInformation) interface.
         */
        function is(value) {
            var candidate = value;
            return Is.defined(candidate) && Location.is(candidate.location) && Is.string(candidate.message);
        }
        DiagnosticRelatedInformation.is = is;
    })(DiagnosticRelatedInformation = exports.DiagnosticRelatedInformation || (exports.DiagnosticRelatedInformation = {}));
    /**
     * The diagnostic's severity.
     */
    var DiagnosticSeverity;
    (function (DiagnosticSeverity) {
        /**
         * Reports an error.
         */
        DiagnosticSeverity.Error = 1;
        /**
         * Reports a warning.
         */
        DiagnosticSeverity.Warning = 2;
        /**
         * Reports an information.
         */
        DiagnosticSeverity.Information = 3;
        /**
         * Reports a hint.
         */
        DiagnosticSeverity.Hint = 4;
    })(DiagnosticSeverity = exports.DiagnosticSeverity || (exports.DiagnosticSeverity = {}));
    /**
     * The Diagnostic namespace provides helper functions to work with
     * [Diagnostic](#Diagnostic) literals.
     */
    var Diagnostic;
    (function (Diagnostic) {
        /**
         * Creates a new Diagnostic literal.
         */
        function create(range, message, severity, code, source, relatedInformation) {
            var result = { range: range, message: message };
            if (Is.defined(severity)) {
                result.severity = severity;
            }
            if (Is.defined(code)) {
                result.code = code;
            }
            if (Is.defined(source)) {
                result.source = source;
            }
            if (Is.defined(relatedInformation)) {
                result.relatedInformation = relatedInformation;
            }
            return result;
        }
        Diagnostic.create = create;
        /**
         * Checks whether the given literal conforms to the [Diagnostic](#Diagnostic) interface.
         */
        function is(value) {
            var candidate = value;
            return Is.defined(candidate)
                && Range.is(candidate.range)
                && Is.string(candidate.message)
                && (Is.number(candidate.severity) || Is.undefined(candidate.severity))
                && (Is.number(candidate.code) || Is.string(candidate.code) || Is.undefined(candidate.code))
                && (Is.string(candidate.source) || Is.undefined(candidate.source))
                && (Is.undefined(candidate.relatedInformation) || Is.typedArray(candidate.relatedInformation, DiagnosticRelatedInformation.is));
        }
        Diagnostic.is = is;
    })(Diagnostic = exports.Diagnostic || (exports.Diagnostic = {}));
    /**
     * The Command namespace provides helper functions to work with
     * [Command](#Command) literals.
     */
    var Command;
    (function (Command) {
        /**
         * Creates a new Command literal.
         */
        function create(title, command) {
            var args = [];
            for (var _i = 2; _i < arguments.length; _i++) {
                args[_i - 2] = arguments[_i];
            }
            var result = { title: title, command: command };
            if (Is.defined(args) && args.length > 0) {
                result.arguments = args;
            }
            return result;
        }
        Command.create = create;
        /**
         * Checks whether the given literal conforms to the [Command](#Command) interface.
         */
        function is(value) {
            var candidate = value;
            return Is.defined(candidate) && Is.string(candidate.title) && Is.string(candidate.command);
        }
        Command.is = is;
    })(Command = exports.Command || (exports.Command = {}));
    /**
     * The TextEdit namespace provides helper function to create replace,
     * insert and delete edits more easily.
     */
    var TextEdit;
    (function (TextEdit) {
        /**
         * Creates a replace text edit.
         * @param range The range of text to be replaced.
         * @param newText The new text.
         */
        function replace(range, newText) {
            return { range: range, newText: newText };
        }
        TextEdit.replace = replace;
        /**
         * Creates a insert text edit.
         * @param position The position to insert the text at.
         * @param newText The text to be inserted.
         */
        function insert(position, newText) {
            return { range: { start: position, end: position }, newText: newText };
        }
        TextEdit.insert = insert;
        /**
         * Creates a delete text edit.
         * @param range The range of text to be deleted.
         */
        function del(range) {
            return { range: range, newText: '' };
        }
        TextEdit.del = del;
        function is(value) {
            var candidate = value;
            return Is.objectLiteral(candidate)
                && Is.string(candidate.newText)
                && Range.is(candidate.range);
        }
        TextEdit.is = is;
    })(TextEdit = exports.TextEdit || (exports.TextEdit = {}));
    /**
     * The TextDocumentEdit namespace provides helper function to create
     * an edit that manipulates a text document.
     */
    var TextDocumentEdit;
    (function (TextDocumentEdit) {
        /**
         * Creates a new `TextDocumentEdit`
         */
        function create(textDocument, edits) {
            return { textDocument: textDocument, edits: edits };
        }
        TextDocumentEdit.create = create;
        function is(value) {
            var candidate = value;
            return Is.defined(candidate)
                && VersionedTextDocumentIdentifier.is(candidate.textDocument)
                && Array.isArray(candidate.edits);
        }
        TextDocumentEdit.is = is;
    })(TextDocumentEdit = exports.TextDocumentEdit || (exports.TextDocumentEdit = {}));
    var CreateFile;
    (function (CreateFile) {
        function create(uri, options) {
            var result = {
                kind: 'create',
                uri: uri
            };
            if (options !== void 0 && (options.overwrite !== void 0 || options.ignoreIfExists !== void 0)) {
                result.options = options;
            }
            return result;
        }
        CreateFile.create = create;
        function is(value) {
            var candidate = value;
            return candidate && candidate.kind === 'create' && Is.string(candidate.uri) &&
                (candidate.options === void 0 ||
                    ((candidate.options.overwrite === void 0 || Is.boolean(candidate.options.overwrite)) && (candidate.options.ignoreIfExists === void 0 || Is.boolean(candidate.options.ignoreIfExists))));
        }
        CreateFile.is = is;
    })(CreateFile = exports.CreateFile || (exports.CreateFile = {}));
    var RenameFile;
    (function (RenameFile) {
        function create(oldUri, newUri, options) {
            var result = {
                kind: 'rename',
                oldUri: oldUri,
                newUri: newUri
            };
            if (options !== void 0 && (options.overwrite !== void 0 || options.ignoreIfExists !== void 0)) {
                result.options = options;
            }
            return result;
        }
        RenameFile.create = create;
        function is(value) {
            var candidate = value;
            return candidate && candidate.kind === 'rename' && Is.string(candidate.oldUri) && Is.string(candidate.newUri) &&
                (candidate.options === void 0 ||
                    ((candidate.options.overwrite === void 0 || Is.boolean(candidate.options.overwrite)) && (candidate.options.ignoreIfExists === void 0 || Is.boolean(candidate.options.ignoreIfExists))));
        }
        RenameFile.is = is;
    })(RenameFile = exports.RenameFile || (exports.RenameFile = {}));
    var DeleteFile;
    (function (DeleteFile) {
        function create(uri, options) {
            var result = {
                kind: 'delete',
                uri: uri
            };
            if (options !== void 0 && (options.recursive !== void 0 || options.ignoreIfNotExists !== void 0)) {
                result.options = options;
            }
            return result;
        }
        DeleteFile.create = create;
        function is(value) {
            var candidate = value;
            return candidate && candidate.kind === 'delete' && Is.string(candidate.uri) &&
                (candidate.options === void 0 ||
                    ((candidate.options.recursive === void 0 || Is.boolean(candidate.options.recursive)) && (candidate.options.ignoreIfNotExists === void 0 || Is.boolean(candidate.options.ignoreIfNotExists))));
        }
        DeleteFile.is = is;
    })(DeleteFile = exports.DeleteFile || (exports.DeleteFile = {}));
    var WorkspaceEdit;
    (function (WorkspaceEdit) {
        function is(value) {
            var candidate = value;
            return candidate &&
                (candidate.changes !== void 0 || candidate.documentChanges !== void 0) &&
                (candidate.documentChanges === void 0 || candidate.documentChanges.every(function (change) {
                    if (Is.string(change.kind)) {
                        return CreateFile.is(change) || RenameFile.is(change) || DeleteFile.is(change);
                    }
                    else {
                        return TextDocumentEdit.is(change);
                    }
                }));
        }
        WorkspaceEdit.is = is;
    })(WorkspaceEdit = exports.WorkspaceEdit || (exports.WorkspaceEdit = {}));
    var TextEditChangeImpl = /** @class */ (function () {
        function TextEditChangeImpl(edits) {
            this.edits = edits;
        }
        TextEditChangeImpl.prototype.insert = function (position, newText) {
            this.edits.push(TextEdit.insert(position, newText));
        };
        TextEditChangeImpl.prototype.replace = function (range, newText) {
            this.edits.push(TextEdit.replace(range, newText));
        };
        TextEditChangeImpl.prototype.delete = function (range) {
            this.edits.push(TextEdit.del(range));
        };
        TextEditChangeImpl.prototype.add = function (edit) {
            this.edits.push(edit);
        };
        TextEditChangeImpl.prototype.all = function () {
            return this.edits;
        };
        TextEditChangeImpl.prototype.clear = function () {
            this.edits.splice(0, this.edits.length);
        };
        return TextEditChangeImpl;
    }());
    /**
     * A workspace change helps constructing changes to a workspace.
     */
    var WorkspaceChange = /** @class */ (function () {
        function WorkspaceChange(workspaceEdit) {
            var _this = this;
            this._textEditChanges = Object.create(null);
            if (workspaceEdit) {
                this._workspaceEdit = workspaceEdit;
                if (workspaceEdit.documentChanges) {
                    workspaceEdit.documentChanges.forEach(function (change) {
                        if (TextDocumentEdit.is(change)) {
                            var textEditChange = new TextEditChangeImpl(change.edits);
                            _this._textEditChanges[change.textDocument.uri] = textEditChange;
                        }
                    });
                }
                else if (workspaceEdit.changes) {
                    Object.keys(workspaceEdit.changes).forEach(function (key) {
                        var textEditChange = new TextEditChangeImpl(workspaceEdit.changes[key]);
                        _this._textEditChanges[key] = textEditChange;
                    });
                }
            }
        }
        Object.defineProperty(WorkspaceChange.prototype, "edit", {
            /**
             * Returns the underlying [WorkspaceEdit](#WorkspaceEdit) literal
             * use to be returned from a workspace edit operation like rename.
             */
            get: function () {
                return this._workspaceEdit;
            },
            enumerable: true,
            configurable: true
        });
        WorkspaceChange.prototype.getTextEditChange = function (key) {
            if (VersionedTextDocumentIdentifier.is(key)) {
                if (!this._workspaceEdit) {
                    this._workspaceEdit = {
                        documentChanges: []
                    };
                }
                if (!this._workspaceEdit.documentChanges) {
                    throw new Error('Workspace edit is not configured for document changes.');
                }
                var textDocument = key;
                var result = this._textEditChanges[textDocument.uri];
                if (!result) {
                    var edits = [];
                    var textDocumentEdit = {
                        textDocument: textDocument,
                        edits: edits
                    };
                    this._workspaceEdit.documentChanges.push(textDocumentEdit);
                    result = new TextEditChangeImpl(edits);
                    this._textEditChanges[textDocument.uri] = result;
                }
                return result;
            }
            else {
                if (!this._workspaceEdit) {
                    this._workspaceEdit = {
                        changes: Object.create(null)
                    };
                }
                if (!this._workspaceEdit.changes) {
                    throw new Error('Workspace edit is not configured for normal text edit changes.');
                }
                var result = this._textEditChanges[key];
                if (!result) {
                    var edits = [];
                    this._workspaceEdit.changes[key] = edits;
                    result = new TextEditChangeImpl(edits);
                    this._textEditChanges[key] = result;
                }
                return result;
            }
        };
        WorkspaceChange.prototype.createFile = function (uri, options) {
            this.checkDocumentChanges();
            this._workspaceEdit.documentChanges.push(CreateFile.create(uri, options));
        };
        WorkspaceChange.prototype.renameFile = function (oldUri, newUri, options) {
            this.checkDocumentChanges();
            this._workspaceEdit.documentChanges.push(RenameFile.create(oldUri, newUri, options));
        };
        WorkspaceChange.prototype.deleteFile = function (uri, options) {
            this.checkDocumentChanges();
            this._workspaceEdit.documentChanges.push(DeleteFile.create(uri, options));
        };
        WorkspaceChange.prototype.checkDocumentChanges = function () {
            if (!this._workspaceEdit || !this._workspaceEdit.documentChanges) {
                throw new Error('Workspace edit is not configured for document changes.');
            }
        };
        return WorkspaceChange;
    }());
    exports.WorkspaceChange = WorkspaceChange;
    /**
     * The TextDocumentIdentifier namespace provides helper functions to work with
     * [TextDocumentIdentifier](#TextDocumentIdentifier) literals.
     */
    var TextDocumentIdentifier;
    (function (TextDocumentIdentifier) {
        /**
         * Creates a new TextDocumentIdentifier literal.
         * @param uri The document's uri.
         */
        function create(uri) {
            return { uri: uri };
        }
        TextDocumentIdentifier.create = create;
        /**
         * Checks whether the given literal conforms to the [TextDocumentIdentifier](#TextDocumentIdentifier) interface.
         */
        function is(value) {
            var candidate = value;
            return Is.defined(candidate) && Is.string(candidate.uri);
        }
        TextDocumentIdentifier.is = is;
    })(TextDocumentIdentifier = exports.TextDocumentIdentifier || (exports.TextDocumentIdentifier = {}));
    /**
     * The VersionedTextDocumentIdentifier namespace provides helper functions to work with
     * [VersionedTextDocumentIdentifier](#VersionedTextDocumentIdentifier) literals.
     */
    var VersionedTextDocumentIdentifier;
    (function (VersionedTextDocumentIdentifier) {
        /**
         * Creates a new VersionedTextDocumentIdentifier literal.
         * @param uri The document's uri.
         * @param uri The document's text.
         */
        function create(uri, version) {
            return { uri: uri, version: version };
        }
        VersionedTextDocumentIdentifier.create = create;
        /**
         * Checks whether the given literal conforms to the [VersionedTextDocumentIdentifier](#VersionedTextDocumentIdentifier) interface.
         */
        function is(value) {
            var candidate = value;
            return Is.defined(candidate) && Is.string(candidate.uri) && (candidate.version === null || Is.number(candidate.version));
        }
        VersionedTextDocumentIdentifier.is = is;
    })(VersionedTextDocumentIdentifier = exports.VersionedTextDocumentIdentifier || (exports.VersionedTextDocumentIdentifier = {}));
    /**
     * The TextDocumentItem namespace provides helper functions to work with
     * [TextDocumentItem](#TextDocumentItem) literals.
     */
    var TextDocumentItem;
    (function (TextDocumentItem) {
        /**
         * Creates a new TextDocumentItem literal.
         * @param uri The document's uri.
         * @param languageId The document's language identifier.
         * @param version The document's version number.
         * @param text The document's text.
         */
        function create(uri, languageId, version, text) {
            return { uri: uri, languageId: languageId, version: version, text: text };
        }
        TextDocumentItem.create = create;
        /**
         * Checks whether the given literal conforms to the [TextDocumentItem](#TextDocumentItem) interface.
         */
        function is(value) {
            var candidate = value;
            return Is.defined(candidate) && Is.string(candidate.uri) && Is.string(candidate.languageId) && Is.number(candidate.version) && Is.string(candidate.text);
        }
        TextDocumentItem.is = is;
    })(TextDocumentItem = exports.TextDocumentItem || (exports.TextDocumentItem = {}));
    /**
     * Describes the content type that a client supports in various
     * result literals like `Hover`, `ParameterInfo` or `CompletionItem`.
     *
     * Please note that `MarkupKinds` must not start with a `$`. This kinds
     * are reserved for internal usage.
     */
    var MarkupKind;
    (function (MarkupKind) {
        /**
         * Plain text is supported as a content format
         */
        MarkupKind.PlainText = 'plaintext';
        /**
         * Markdown is supported as a content format
         */
        MarkupKind.Markdown = 'markdown';
    })(MarkupKind = exports.MarkupKind || (exports.MarkupKind = {}));
    (function (MarkupKind) {
        /**
         * Checks whether the given value is a value of the [MarkupKind](#MarkupKind) type.
         */
        function is(value) {
            var candidate = value;
            return candidate === MarkupKind.PlainText || candidate === MarkupKind.Markdown;
        }
        MarkupKind.is = is;
    })(MarkupKind = exports.MarkupKind || (exports.MarkupKind = {}));
    var MarkupContent;
    (function (MarkupContent) {
        /**
         * Checks whether the given value conforms to the [MarkupContent](#MarkupContent) interface.
         */
        function is(value) {
            var candidate = value;
            return Is.objectLiteral(value) && MarkupKind.is(candidate.kind) && Is.string(candidate.value);
        }
        MarkupContent.is = is;
    })(MarkupContent = exports.MarkupContent || (exports.MarkupContent = {}));
    /**
     * The kind of a completion entry.
     */
    var CompletionItemKind;
    (function (CompletionItemKind) {
        CompletionItemKind.Text = 1;
        CompletionItemKind.Method = 2;
        CompletionItemKind.Function = 3;
        CompletionItemKind.Constructor = 4;
        CompletionItemKind.Field = 5;
        CompletionItemKind.Variable = 6;
        CompletionItemKind.Class = 7;
        CompletionItemKind.Interface = 8;
        CompletionItemKind.Module = 9;
        CompletionItemKind.Property = 10;
        CompletionItemKind.Unit = 11;
        CompletionItemKind.Value = 12;
        CompletionItemKind.Enum = 13;
        CompletionItemKind.Keyword = 14;
        CompletionItemKind.Snippet = 15;
        CompletionItemKind.Color = 16;
        CompletionItemKind.File = 17;
        CompletionItemKind.Reference = 18;
        CompletionItemKind.Folder = 19;
        CompletionItemKind.EnumMember = 20;
        CompletionItemKind.Constant = 21;
        CompletionItemKind.Struct = 22;
        CompletionItemKind.Event = 23;
        CompletionItemKind.Operator = 24;
        CompletionItemKind.TypeParameter = 25;
    })(CompletionItemKind = exports.CompletionItemKind || (exports.CompletionItemKind = {}));
    /**
     * Defines whether the insert text in a completion item should be interpreted as
     * plain text or a snippet.
     */
    var InsertTextFormat;
    (function (InsertTextFormat) {
        /**
         * The primary text to be inserted is treated as a plain string.
         */
        InsertTextFormat.PlainText = 1;
        /**
         * The primary text to be inserted is treated as a snippet.
         *
         * A snippet can define tab stops and placeholders with `$1`, `$2`
         * and `${3:foo}`. `$0` defines the final tab stop, it defaults to
         * the end of the snippet. Placeholders with equal identifiers are linked,
         * that is typing in one will update others too.
         *
         * See also: https://github.com/Microsoft/vscode/blob/master/src/vs/editor/contrib/snippet/common/snippet.md
         */
        InsertTextFormat.Snippet = 2;
    })(InsertTextFormat = exports.InsertTextFormat || (exports.InsertTextFormat = {}));
    /**
     * The CompletionItem namespace provides functions to deal with
     * completion items.
     */
    var CompletionItem;
    (function (CompletionItem) {
        /**
         * Create a completion item and seed it with a label.
         * @param label The completion item's label
         */
        function create(label) {
            return { label: label };
        }
        CompletionItem.create = create;
    })(CompletionItem = exports.CompletionItem || (exports.CompletionItem = {}));
    /**
     * The CompletionList namespace provides functions to deal with
     * completion lists.
     */
    var CompletionList;
    (function (CompletionList) {
        /**
         * Creates a new completion list.
         *
         * @param items The completion items.
         * @param isIncomplete The list is not complete.
         */
        function create(items, isIncomplete) {
            return { items: items ? items : [], isIncomplete: !!isIncomplete };
        }
        CompletionList.create = create;
    })(CompletionList = exports.CompletionList || (exports.CompletionList = {}));
    var MarkedString;
    (function (MarkedString) {
        /**
         * Creates a marked string from plain text.
         *
         * @param plainText The plain text.
         */
        function fromPlainText(plainText) {
            return plainText.replace(/[\\`*_{}[\]()#+\-.!]/g, "\\$&"); // escape markdown syntax tokens: http://daringfireball.net/projects/markdown/syntax#backslash
        }
        MarkedString.fromPlainText = fromPlainText;
        /**
         * Checks whether the given value conforms to the [MarkedString](#MarkedString) type.
         */
        function is(value) {
            var candidate = value;
            return Is.string(candidate) || (Is.objectLiteral(candidate) && Is.string(candidate.language) && Is.string(candidate.value));
        }
        MarkedString.is = is;
    })(MarkedString = exports.MarkedString || (exports.MarkedString = {}));
    var Hover;
    (function (Hover) {
        /**
         * Checks whether the given value conforms to the [Hover](#Hover) interface.
         */
        function is(value) {
            var candidate = value;
            return !!candidate && Is.objectLiteral(candidate) && (MarkupContent.is(candidate.contents) ||
                MarkedString.is(candidate.contents) ||
                Is.typedArray(candidate.contents, MarkedString.is)) && (value.range === void 0 || Range.is(value.range));
        }
        Hover.is = is;
    })(Hover = exports.Hover || (exports.Hover = {}));
    /**
     * The ParameterInformation namespace provides helper functions to work with
     * [ParameterInformation](#ParameterInformation) literals.
     */
    var ParameterInformation;
    (function (ParameterInformation) {
        /**
         * Creates a new parameter information literal.
         *
         * @param label A label string.
         * @param documentation A doc string.
         */
        function create(label, documentation) {
            return documentation ? { label: label, documentation: documentation } : { label: label };
        }
        ParameterInformation.create = create;
        ;
    })(ParameterInformation = exports.ParameterInformation || (exports.ParameterInformation = {}));
    /**
     * The SignatureInformation namespace provides helper functions to work with
     * [SignatureInformation](#SignatureInformation) literals.
     */
    var SignatureInformation;
    (function (SignatureInformation) {
        function create(label, documentation) {
            var parameters = [];
            for (var _i = 2; _i < arguments.length; _i++) {
                parameters[_i - 2] = arguments[_i];
            }
            var result = { label: label };
            if (Is.defined(documentation)) {
                result.documentation = documentation;
            }
            if (Is.defined(parameters)) {
                result.parameters = parameters;
            }
            else {
                result.parameters = [];
            }
            return result;
        }
        SignatureInformation.create = create;
    })(SignatureInformation = exports.SignatureInformation || (exports.SignatureInformation = {}));
    /**
     * A document highlight kind.
     */
    var DocumentHighlightKind;
    (function (DocumentHighlightKind) {
        /**
         * A textual occurrence.
         */
        DocumentHighlightKind.Text = 1;
        /**
         * Read-access of a symbol, like reading a variable.
         */
        DocumentHighlightKind.Read = 2;
        /**
         * Write-access of a symbol, like writing to a variable.
         */
        DocumentHighlightKind.Write = 3;
    })(DocumentHighlightKind = exports.DocumentHighlightKind || (exports.DocumentHighlightKind = {}));
    /**
     * DocumentHighlight namespace to provide helper functions to work with
     * [DocumentHighlight](#DocumentHighlight) literals.
     */
    var DocumentHighlight;
    (function (DocumentHighlight) {
        /**
         * Create a DocumentHighlight object.
         * @param range The range the highlight applies to.
         */
        function create(range, kind) {
            var result = { range: range };
            if (Is.number(kind)) {
                result.kind = kind;
            }
            return result;
        }
        DocumentHighlight.create = create;
    })(DocumentHighlight = exports.DocumentHighlight || (exports.DocumentHighlight = {}));
    /**
     * A symbol kind.
     */
    var SymbolKind;
    (function (SymbolKind) {
        SymbolKind.File = 1;
        SymbolKind.Module = 2;
        SymbolKind.Namespace = 3;
        SymbolKind.Package = 4;
        SymbolKind.Class = 5;
        SymbolKind.Method = 6;
        SymbolKind.Property = 7;
        SymbolKind.Field = 8;
        SymbolKind.Constructor = 9;
        SymbolKind.Enum = 10;
        SymbolKind.Interface = 11;
        SymbolKind.Function = 12;
        SymbolKind.Variable = 13;
        SymbolKind.Constant = 14;
        SymbolKind.String = 15;
        SymbolKind.Number = 16;
        SymbolKind.Boolean = 17;
        SymbolKind.Array = 18;
        SymbolKind.Object = 19;
        SymbolKind.Key = 20;
        SymbolKind.Null = 21;
        SymbolKind.EnumMember = 22;
        SymbolKind.Struct = 23;
        SymbolKind.Event = 24;
        SymbolKind.Operator = 25;
        SymbolKind.TypeParameter = 26;
    })(SymbolKind = exports.SymbolKind || (exports.SymbolKind = {}));
    var SymbolInformation;
    (function (SymbolInformation) {
        /**
         * Creates a new symbol information literal.
         *
         * @param name The name of the symbol.
         * @param kind The kind of the symbol.
         * @param range The range of the location of the symbol.
         * @param uri The resource of the location of symbol, defaults to the current document.
         * @param containerName The name of the symbol containing the symbol.
         */
        function create(name, kind, range, uri, containerName) {
            var result = {
                name: name,
                kind: kind,
                location: { uri: uri, range: range }
            };
            if (containerName) {
                result.containerName = containerName;
            }
            return result;
        }
        SymbolInformation.create = create;
    })(SymbolInformation = exports.SymbolInformation || (exports.SymbolInformation = {}));
    /**
     * Represents programming constructs like variables, classes, interfaces etc.
     * that appear in a document. Document symbols can be hierarchical and they
     * have two ranges: one that encloses its definition and one that points to
     * its most interesting range, e.g. the range of an identifier.
     */
    var DocumentSymbol = /** @class */ (function () {
        function DocumentSymbol() {
        }
        return DocumentSymbol;
    }());
    exports.DocumentSymbol = DocumentSymbol;
    (function (DocumentSymbol) {
        /**
         * Creates a new symbol information literal.
         *
         * @param name The name of the symbol.
         * @param detail The detail of the symbol.
         * @param kind The kind of the symbol.
         * @param range The range of the symbol.
         * @param selectionRange The selectionRange of the symbol.
         * @param children Children of the symbol.
         */
        function create(name, detail, kind, range, selectionRange, children) {
            var result = {
                name: name,
                detail: detail,
                kind: kind,
                range: range,
                selectionRange: selectionRange
            };
            if (children !== void 0) {
                result.children = children;
            }
            return result;
        }
        DocumentSymbol.create = create;
        /**
         * Checks whether the given literal conforms to the [DocumentSymbol](#DocumentSymbol) interface.
         */
        function is(value) {
            var candidate = value;
            return candidate &&
                Is.string(candidate.name) && Is.number(candidate.kind) &&
                Range.is(candidate.range) && Range.is(candidate.selectionRange) &&
                (candidate.detail === void 0 || Is.string(candidate.detail)) &&
                (candidate.deprecated === void 0 || Is.boolean(candidate.deprecated)) &&
                (candidate.children === void 0 || Array.isArray(candidate.children));
        }
        DocumentSymbol.is = is;
    })(DocumentSymbol = exports.DocumentSymbol || (exports.DocumentSymbol = {}));
    exports.DocumentSymbol = DocumentSymbol;
    /**
     * A set of predefined code action kinds
     */
    var CodeActionKind;
    (function (CodeActionKind) {
        /**
         * Base kind for quickfix actions: 'quickfix'
         */
        CodeActionKind.QuickFix = 'quickfix';
        /**
         * Base kind for refactoring actions: 'refactor'
         */
        CodeActionKind.Refactor = 'refactor';
        /**
         * Base kind for refactoring extraction actions: 'refactor.extract'
         *
         * Example extract actions:
         *
         * - Extract method
         * - Extract function
         * - Extract variable
         * - Extract interface from class
         * - ...
         */
        CodeActionKind.RefactorExtract = 'refactor.extract';
        /**
         * Base kind for refactoring inline actions: 'refactor.inline'
         *
         * Example inline actions:
         *
         * - Inline function
         * - Inline variable
         * - Inline constant
         * - ...
         */
        CodeActionKind.RefactorInline = 'refactor.inline';
        /**
         * Base kind for refactoring rewrite actions: 'refactor.rewrite'
         *
         * Example rewrite actions:
         *
         * - Convert JavaScript function to class
         * - Add or remove parameter
         * - Encapsulate field
         * - Make method static
         * - Move method to base class
         * - ...
         */
        CodeActionKind.RefactorRewrite = 'refactor.rewrite';
        /**
         * Base kind for source actions: `source`
         *
         * Source code actions apply to the entire file.
         */
        CodeActionKind.Source = 'source';
        /**
         * Base kind for an organize imports source action: `source.organizeImports`
         */
        CodeActionKind.SourceOrganizeImports = 'source.organizeImports';
    })(CodeActionKind = exports.CodeActionKind || (exports.CodeActionKind = {}));
    /**
     * The CodeActionContext namespace provides helper functions to work with
     * [CodeActionContext](#CodeActionContext) literals.
     */
    var CodeActionContext;
    (function (CodeActionContext) {
        /**
         * Creates a new CodeActionContext literal.
         */
        function create(diagnostics, only) {
            var result = { diagnostics: diagnostics };
            if (only !== void 0 && only !== null) {
                result.only = only;
            }
            return result;
        }
        CodeActionContext.create = create;
        /**
         * Checks whether the given literal conforms to the [CodeActionContext](#CodeActionContext) interface.
         */
        function is(value) {
            var candidate = value;
            return Is.defined(candidate) && Is.typedArray(candidate.diagnostics, Diagnostic.is) && (candidate.only === void 0 || Is.typedArray(candidate.only, Is.string));
        }
        CodeActionContext.is = is;
    })(CodeActionContext = exports.CodeActionContext || (exports.CodeActionContext = {}));
    var CodeAction;
    (function (CodeAction) {
        function create(title, commandOrEdit, kind) {
            var result = { title: title };
            if (Command.is(commandOrEdit)) {
                result.command = commandOrEdit;
            }
            else {
                result.edit = commandOrEdit;
            }
            if (kind !== void null) {
                result.kind = kind;
            }
            return result;
        }
        CodeAction.create = create;
        function is(value) {
            var candidate = value;
            return candidate && Is.string(candidate.title) &&
                (candidate.diagnostics === void 0 || Is.typedArray(candidate.diagnostics, Diagnostic.is)) &&
                (candidate.kind === void 0 || Is.string(candidate.kind)) &&
                (candidate.edit !== void 0 || candidate.command !== void 0) &&
                (candidate.command === void 0 || Command.is(candidate.command)) &&
                (candidate.edit === void 0 || WorkspaceEdit.is(candidate.edit));
        }
        CodeAction.is = is;
    })(CodeAction = exports.CodeAction || (exports.CodeAction = {}));
    /**
     * The CodeLens namespace provides helper functions to work with
     * [CodeLens](#CodeLens) literals.
     */
    var CodeLens;
    (function (CodeLens) {
        /**
         * Creates a new CodeLens literal.
         */
        function create(range, data) {
            var result = { range: range };
            if (Is.defined(data))
                result.data = data;
            return result;
        }
        CodeLens.create = create;
        /**
         * Checks whether the given literal conforms to the [CodeLens](#CodeLens) interface.
         */
        function is(value) {
            var candidate = value;
            return Is.defined(candidate) && Range.is(candidate.range) && (Is.undefined(candidate.command) || Command.is(candidate.command));
        }
        CodeLens.is = is;
    })(CodeLens = exports.CodeLens || (exports.CodeLens = {}));
    /**
     * The FormattingOptions namespace provides helper functions to work with
     * [FormattingOptions](#FormattingOptions) literals.
     */
    var FormattingOptions;
    (function (FormattingOptions) {
        /**
         * Creates a new FormattingOptions literal.
         */
        function create(tabSize, insertSpaces) {
            return { tabSize: tabSize, insertSpaces: insertSpaces };
        }
        FormattingOptions.create = create;
        /**
         * Checks whether the given literal conforms to the [FormattingOptions](#FormattingOptions) interface.
         */
        function is(value) {
            var candidate = value;
            return Is.defined(candidate) && Is.number(candidate.tabSize) && Is.boolean(candidate.insertSpaces);
        }
        FormattingOptions.is = is;
    })(FormattingOptions = exports.FormattingOptions || (exports.FormattingOptions = {}));
    /**
     * A document link is a range in a text document that links to an internal or external resource, like another
     * text document or a web site.
     */
    var DocumentLink = /** @class */ (function () {
        function DocumentLink() {
        }
        return DocumentLink;
    }());
    exports.DocumentLink = DocumentLink;
    /**
     * The DocumentLink namespace provides helper functions to work with
     * [DocumentLink](#DocumentLink) literals.
     */
    (function (DocumentLink) {
        /**
         * Creates a new DocumentLink literal.
         */
        function create(range, target, data) {
            return { range: range, target: target, data: data };
        }
        DocumentLink.create = create;
        /**
         * Checks whether the given literal conforms to the [DocumentLink](#DocumentLink) interface.
         */
        function is(value) {
            var candidate = value;
            return Is.defined(candidate) && Range.is(candidate.range) && (Is.undefined(candidate.target) || Is.string(candidate.target));
        }
        DocumentLink.is = is;
    })(DocumentLink = exports.DocumentLink || (exports.DocumentLink = {}));
    exports.DocumentLink = DocumentLink;
    exports.EOL = ['\n', '\r\n', '\r'];
    var TextDocument;
    (function (TextDocument) {
        /**
         * Creates a new ITextDocument literal from the given uri and content.
         * @param uri The document's uri.
         * @param languageId  The document's language Id.
         * @param content The document's content.
         */
        function create(uri, languageId, version, content) {
            return new FullTextDocument(uri, languageId, version, content);
        }
        TextDocument.create = create;
        /**
         * Checks whether the given literal conforms to the [ITextDocument](#ITextDocument) interface.
         */
        function is(value) {
            var candidate = value;
            return Is.defined(candidate) && Is.string(candidate.uri) && (Is.undefined(candidate.languageId) || Is.string(candidate.languageId)) && Is.number(candidate.lineCount)
                && Is.func(candidate.getText) && Is.func(candidate.positionAt) && Is.func(candidate.offsetAt) ? true : false;
        }
        TextDocument.is = is;
        function applyEdits(document, edits) {
            var text = document.getText();
            var sortedEdits = mergeSort(edits, function (a, b) {
                var diff = a.range.start.line - b.range.start.line;
                if (diff === 0) {
                    return a.range.start.character - b.range.start.character;
                }
                return diff;
            });
            var lastModifiedOffset = text.length;
            for (var i = sortedEdits.length - 1; i >= 0; i--) {
                var e = sortedEdits[i];
                var startOffset = document.offsetAt(e.range.start);
                var endOffset = document.offsetAt(e.range.end);
                if (endOffset <= lastModifiedOffset) {
                    text = text.substring(0, startOffset) + e.newText + text.substring(endOffset, text.length);
                }
                else {
                    throw new Error('Overlapping edit');
                }
                lastModifiedOffset = startOffset;
            }
            return text;
        }
        TextDocument.applyEdits = applyEdits;
        function mergeSort(data, compare) {
            if (data.length <= 1) {
                // sorted
                return data;
            }
            var p = (data.length / 2) | 0;
            var left = data.slice(0, p);
            var right = data.slice(p);
            mergeSort(left, compare);
            mergeSort(right, compare);
            var leftIdx = 0;
            var rightIdx = 0;
            var i = 0;
            while (leftIdx < left.length && rightIdx < right.length) {
                var ret = compare(left[leftIdx], right[rightIdx]);
                if (ret <= 0) {
                    // smaller_equal -> take left to preserve order
                    data[i++] = left[leftIdx++];
                }
                else {
                    // greater -> take right
                    data[i++] = right[rightIdx++];
                }
            }
            while (leftIdx < left.length) {
                data[i++] = left[leftIdx++];
            }
            while (rightIdx < right.length) {
                data[i++] = right[rightIdx++];
            }
            return data;
        }
    })(TextDocument = exports.TextDocument || (exports.TextDocument = {}));
    /**
     * Represents reasons why a text document is saved.
     */
    var TextDocumentSaveReason;
    (function (TextDocumentSaveReason) {
        /**
         * Manually triggered, e.g. by the user pressing save, by starting debugging,
         * or by an API call.
         */
        TextDocumentSaveReason.Manual = 1;
        /**
         * Automatic after a delay.
         */
        TextDocumentSaveReason.AfterDelay = 2;
        /**
         * When the editor lost focus.
         */
        TextDocumentSaveReason.FocusOut = 3;
    })(TextDocumentSaveReason = exports.TextDocumentSaveReason || (exports.TextDocumentSaveReason = {}));
    var FullTextDocument = /** @class */ (function () {
        function FullTextDocument(uri, languageId, version, content) {
            this._uri = uri;
            this._languageId = languageId;
            this._version = version;
            this._content = content;
            this._lineOffsets = null;
        }
        Object.defineProperty(FullTextDocument.prototype, "uri", {
            get: function () {
                return this._uri;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(FullTextDocument.prototype, "languageId", {
            get: function () {
                return this._languageId;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(FullTextDocument.prototype, "version", {
            get: function () {
                return this._version;
            },
            enumerable: true,
            configurable: true
        });
        FullTextDocument.prototype.getText = function (range) {
            if (range) {
                var start = this.offsetAt(range.start);
                var end = this.offsetAt(range.end);
                return this._content.substring(start, end);
            }
            return this._content;
        };
        FullTextDocument.prototype.update = function (event, version) {
            this._content = event.text;
            this._version = version;
            this._lineOffsets = null;
        };
        FullTextDocument.prototype.getLineOffsets = function () {
            if (this._lineOffsets === null) {
                var lineOffsets = [];
                var text = this._content;
                var isLineStart = true;
                for (var i = 0; i < text.length; i++) {
                    if (isLineStart) {
                        lineOffsets.push(i);
                        isLineStart = false;
                    }
                    var ch = text.charAt(i);
                    isLineStart = (ch === '\r' || ch === '\n');
                    if (ch === '\r' && i + 1 < text.length && text.charAt(i + 1) === '\n') {
                        i++;
                    }
                }
                if (isLineStart && text.length > 0) {
                    lineOffsets.push(text.length);
                }
                this._lineOffsets = lineOffsets;
            }
            return this._lineOffsets;
        };
        FullTextDocument.prototype.positionAt = function (offset) {
            offset = Math.max(Math.min(offset, this._content.length), 0);
            var lineOffsets = this.getLineOffsets();
            var low = 0, high = lineOffsets.length;
            if (high === 0) {
                return Position.create(0, offset);
            }
            while (low < high) {
                var mid = Math.floor((low + high) / 2);
                if (lineOffsets[mid] > offset) {
                    high = mid;
                }
                else {
                    low = mid + 1;
                }
            }
            // low is the least x for which the line offset is larger than the current offset
            // or array.length if no line offset is larger than the current offset
            var line = low - 1;
            return Position.create(line, offset - lineOffsets[line]);
        };
        FullTextDocument.prototype.offsetAt = function (position) {
            var lineOffsets = this.getLineOffsets();
            if (position.line >= lineOffsets.length) {
                return this._content.length;
            }
            else if (position.line < 0) {
                return 0;
            }
            var lineOffset = lineOffsets[position.line];
            var nextLineOffset = (position.line + 1 < lineOffsets.length) ? lineOffsets[position.line + 1] : this._content.length;
            return Math.max(Math.min(lineOffset + position.character, nextLineOffset), lineOffset);
        };
        Object.defineProperty(FullTextDocument.prototype, "lineCount", {
            get: function () {
                return this.getLineOffsets().length;
            },
            enumerable: true,
            configurable: true
        });
        return FullTextDocument;
    }());
    var Is;
    (function (Is) {
        var toString = Object.prototype.toString;
        function defined(value) {
            return typeof value !== 'undefined';
        }
        Is.defined = defined;
        function undefined(value) {
            return typeof value === 'undefined';
        }
        Is.undefined = undefined;
        function boolean(value) {
            return value === true || value === false;
        }
        Is.boolean = boolean;
        function string(value) {
            return toString.call(value) === '[object String]';
        }
        Is.string = string;
        function number(value) {
            return toString.call(value) === '[object Number]';
        }
        Is.number = number;
        function func(value) {
            return toString.call(value) === '[object Function]';
        }
        Is.func = func;
        function objectLiteral(value) {
            // Strictly speaking class instances pass this check as well. Since the LSP
            // doesn't use classes we ignore this for now. If we do we need to add something
            // like this: `Object.getPrototypeOf(Object.getPrototypeOf(x)) === null`
            return value !== null && typeof value === 'object';
        }
        Is.objectLiteral = objectLiteral;
        function typedArray(value, check) {
            return Array.isArray(value) && value.every(check);
        }
        Is.typedArray = typedArray;
    })(Is || (Is = {}));
});

define('vscode-languageserver-types', ['vscode-languageserver-types/main'], function (main) { return main; });

(function (factory) {
    if (typeof module === "object" && typeof module.exports === "object") {
        var v = factory(require, exports);
        if (v !== undefined) module.exports = v;
    }
    else if (typeof define === "function" && define.amd) {
        define('jsonc-parser/impl/scanner',["require", "exports"], factory);
    }
})(function (require, exports) {
    /*---------------------------------------------------------------------------------------------
     *  Copyright (c) Microsoft Corporation. All rights reserved.
     *  Licensed under the MIT License. See License.txt in the project root for license information.
     *--------------------------------------------------------------------------------------------*/
    'use strict';
    Object.defineProperty(exports, "__esModule", { value: true });
    /**
     * Creates a JSON scanner on the given text.
     * If ignoreTrivia is set, whitespaces or comments are ignored.
     */
    function createScanner(text, ignoreTrivia) {
        if (ignoreTrivia === void 0) { ignoreTrivia = false; }
        var pos = 0, len = text.length, value = '', tokenOffset = 0, token = 16 /* Unknown */, scanError = 0 /* None */;
        function scanHexDigits(count, exact) {
            var digits = 0;
            var value = 0;
            while (digits < count || !exact) {
                var ch = text.charCodeAt(pos);
                if (ch >= 48 /* _0 */ && ch <= 57 /* _9 */) {
                    value = value * 16 + ch - 48 /* _0 */;
                }
                else if (ch >= 65 /* A */ && ch <= 70 /* F */) {
                    value = value * 16 + ch - 65 /* A */ + 10;
                }
                else if (ch >= 97 /* a */ && ch <= 102 /* f */) {
                    value = value * 16 + ch - 97 /* a */ + 10;
                }
                else {
                    break;
                }
                pos++;
                digits++;
            }
            if (digits < count) {
                value = -1;
            }
            return value;
        }
        function setPosition(newPosition) {
            pos = newPosition;
            value = '';
            tokenOffset = 0;
            token = 16 /* Unknown */;
            scanError = 0 /* None */;
        }
        function scanNumber() {
            var start = pos;
            if (text.charCodeAt(pos) === 48 /* _0 */) {
                pos++;
            }
            else {
                pos++;
                while (pos < text.length && isDigit(text.charCodeAt(pos))) {
                    pos++;
                }
            }
            if (pos < text.length && text.charCodeAt(pos) === 46 /* dot */) {
                pos++;
                if (pos < text.length && isDigit(text.charCodeAt(pos))) {
                    pos++;
                    while (pos < text.length && isDigit(text.charCodeAt(pos))) {
                        pos++;
                    }
                }
                else {
                    scanError = 3 /* UnexpectedEndOfNumber */;
                    return text.substring(start, pos);
                }
            }
            var end = pos;
            if (pos < text.length && (text.charCodeAt(pos) === 69 /* E */ || text.charCodeAt(pos) === 101 /* e */)) {
                pos++;
                if (pos < text.length && text.charCodeAt(pos) === 43 /* plus */ || text.charCodeAt(pos) === 45 /* minus */) {
                    pos++;
                }
                if (pos < text.length && isDigit(text.charCodeAt(pos))) {
                    pos++;
                    while (pos < text.length && isDigit(text.charCodeAt(pos))) {
                        pos++;
                    }
                    end = pos;
                }
                else {
                    scanError = 3 /* UnexpectedEndOfNumber */;
                }
            }
            return text.substring(start, end);
        }
        function scanString() {
            var result = '', start = pos;
            while (true) {
                if (pos >= len) {
                    result += text.substring(start, pos);
                    scanError = 2 /* UnexpectedEndOfString */;
                    break;
                }
                var ch = text.charCodeAt(pos);
                if (ch === 34 /* doubleQuote */) {
                    result += text.substring(start, pos);
                    pos++;
                    break;
                }
                if (ch === 92 /* backslash */) {
                    result += text.substring(start, pos);
                    pos++;
                    if (pos >= len) {
                        scanError = 2 /* UnexpectedEndOfString */;
                        break;
                    }
                    ch = text.charCodeAt(pos++);
                    switch (ch) {
                        case 34 /* doubleQuote */:
                            result += '\"';
                            break;
                        case 92 /* backslash */:
                            result += '\\';
                            break;
                        case 47 /* slash */:
                            result += '/';
                            break;
                        case 98 /* b */:
                            result += '\b';
                            break;
                        case 102 /* f */:
                            result += '\f';
                            break;
                        case 110 /* n */:
                            result += '\n';
                            break;
                        case 114 /* r */:
                            result += '\r';
                            break;
                        case 116 /* t */:
                            result += '\t';
                            break;
                        case 117 /* u */:
                            var ch_1 = scanHexDigits(4, true);
                            if (ch_1 >= 0) {
                                result += String.fromCharCode(ch_1);
                            }
                            else {
                                scanError = 4 /* InvalidUnicode */;
                            }
                            break;
                        default:
                            scanError = 5 /* InvalidEscapeCharacter */;
                    }
                    start = pos;
                    continue;
                }
                if (ch >= 0 && ch <= 0x1f) {
                    if (isLineBreak(ch)) {
                        result += text.substring(start, pos);
                        scanError = 2 /* UnexpectedEndOfString */;
                        break;
                    }
                    else {
                        scanError = 6 /* InvalidCharacter */;
                        // mark as error but continue with string
                    }
                }
                pos++;
            }
            return result;
        }
        function scanNext() {
            value = '';
            scanError = 0 /* None */;
            tokenOffset = pos;
            if (pos >= len) {
                // at the end
                tokenOffset = len;
                return token = 17 /* EOF */;
            }
            var code = text.charCodeAt(pos);
            // trivia: whitespace
            if (isWhiteSpace(code)) {
                do {
                    pos++;
                    value += String.fromCharCode(code);
                    code = text.charCodeAt(pos);
                } while (isWhiteSpace(code));
                return token = 15 /* Trivia */;
            }
            // trivia: newlines
            if (isLineBreak(code)) {
                pos++;
                value += String.fromCharCode(code);
                if (code === 13 /* carriageReturn */ && text.charCodeAt(pos) === 10 /* lineFeed */) {
                    pos++;
                    value += '\n';
                }
                return token = 14 /* LineBreakTrivia */;
            }
            switch (code) {
                // tokens: []{}:,
                case 123 /* openBrace */:
                    pos++;
                    return token = 1 /* OpenBraceToken */;
                case 125 /* closeBrace */:
                    pos++;
                    return token = 2 /* CloseBraceToken */;
                case 91 /* openBracket */:
                    pos++;
                    return token = 3 /* OpenBracketToken */;
                case 93 /* closeBracket */:
                    pos++;
                    return token = 4 /* CloseBracketToken */;
                case 58 /* colon */:
                    pos++;
                    return token = 6 /* ColonToken */;
                case 44 /* comma */:
                    pos++;
                    return token = 5 /* CommaToken */;
                // strings
                case 34 /* doubleQuote */:
                    pos++;
                    value = scanString();
                    return token = 10 /* StringLiteral */;
                // comments
                case 47 /* slash */:
                    var start = pos - 1;
                    // Single-line comment
                    if (text.charCodeAt(pos + 1) === 47 /* slash */) {
                        pos += 2;
                        while (pos < len) {
                            if (isLineBreak(text.charCodeAt(pos))) {
                                break;
                            }
                            pos++;
                        }
                        value = text.substring(start, pos);
                        return token = 12 /* LineCommentTrivia */;
                    }
                    // Multi-line comment
                    if (text.charCodeAt(pos + 1) === 42 /* asterisk */) {
                        pos += 2;
                        var safeLength = len - 1; // For lookahead.
                        var commentClosed = false;
                        while (pos < safeLength) {
                            var ch = text.charCodeAt(pos);
                            if (ch === 42 /* asterisk */ && text.charCodeAt(pos + 1) === 47 /* slash */) {
                                pos += 2;
                                commentClosed = true;
                                break;
                            }
                            pos++;
                        }
                        if (!commentClosed) {
                            pos++;
                            scanError = 1 /* UnexpectedEndOfComment */;
                        }
                        value = text.substring(start, pos);
                        return token = 13 /* BlockCommentTrivia */;
                    }
                    // just a single slash
                    value += String.fromCharCode(code);
                    pos++;
                    return token = 16 /* Unknown */;
                // numbers
                case 45 /* minus */:
                    value += String.fromCharCode(code);
                    pos++;
                    if (pos === len || !isDigit(text.charCodeAt(pos))) {
                        return token = 16 /* Unknown */;
                    }
                // found a minus, followed by a number so
                // we fall through to proceed with scanning
                // numbers
                case 48 /* _0 */:
                case 49 /* _1 */:
                case 50 /* _2 */:
                case 51 /* _3 */:
                case 52 /* _4 */:
                case 53 /* _5 */:
                case 54 /* _6 */:
                case 55 /* _7 */:
                case 56 /* _8 */:
                case 57 /* _9 */:
                    value += scanNumber();
                    return token = 11 /* NumericLiteral */;
                // literals and unknown symbols
                default:
                    // is a literal? Read the full word.
                    while (pos < len && isUnknownContentCharacter(code)) {
                        pos++;
                        code = text.charCodeAt(pos);
                    }
                    if (tokenOffset !== pos) {
                        value = text.substring(tokenOffset, pos);
                        // keywords: true, false, null
                        switch (value) {
                            case 'true': return token = 8 /* TrueKeyword */;
                            case 'false': return token = 9 /* FalseKeyword */;
                            case 'null': return token = 7 /* NullKeyword */;
                        }
                        return token = 16 /* Unknown */;
                    }
                    // some
                    value += String.fromCharCode(code);
                    pos++;
                    return token = 16 /* Unknown */;
            }
        }
        function isUnknownContentCharacter(code) {
            if (isWhiteSpace(code) || isLineBreak(code)) {
                return false;
            }
            switch (code) {
                case 125 /* closeBrace */:
                case 93 /* closeBracket */:
                case 123 /* openBrace */:
                case 91 /* openBracket */:
                case 34 /* doubleQuote */:
                case 58 /* colon */:
                case 44 /* comma */:
                case 47 /* slash */:
                    return false;
            }
            return true;
        }
        function scanNextNonTrivia() {
            var result;
            do {
                result = scanNext();
            } while (result >= 12 /* LineCommentTrivia */ && result <= 15 /* Trivia */);
            return result;
        }
        return {
            setPosition: setPosition,
            getPosition: function () { return pos; },
            scan: ignoreTrivia ? scanNextNonTrivia : scanNext,
            getToken: function () { return token; },
            getTokenValue: function () { return value; },
            getTokenOffset: function () { return tokenOffset; },
            getTokenLength: function () { return pos - tokenOffset; },
            getTokenError: function () { return scanError; }
        };
    }
    exports.createScanner = createScanner;
    function isWhiteSpace(ch) {
        return ch === 32 /* space */ || ch === 9 /* tab */ || ch === 11 /* verticalTab */ || ch === 12 /* formFeed */ ||
            ch === 160 /* nonBreakingSpace */ || ch === 5760 /* ogham */ || ch >= 8192 /* enQuad */ && ch <= 8203 /* zeroWidthSpace */ ||
            ch === 8239 /* narrowNoBreakSpace */ || ch === 8287 /* mathematicalSpace */ || ch === 12288 /* ideographicSpace */ || ch === 65279 /* byteOrderMark */;
    }
    function isLineBreak(ch) {
        return ch === 10 /* lineFeed */ || ch === 13 /* carriageReturn */ || ch === 8232 /* lineSeparator */ || ch === 8233 /* paragraphSeparator */;
    }
    function isDigit(ch) {
        return ch >= 48 /* _0 */ && ch <= 57 /* _9 */;
    }
});
//# sourceMappingURL=scanner.js.map;
(function (factory) {
    if (typeof module === "object" && typeof module.exports === "object") {
        var v = factory(require, exports);
        if (v !== undefined) module.exports = v;
    }
    else if (typeof define === "function" && define.amd) {
        define('jsonc-parser/impl/format',["require", "exports", "./scanner"], factory);
    }
})(function (require, exports) {
    /*---------------------------------------------------------------------------------------------
     *  Copyright (c) Microsoft Corporation. All rights reserved.
     *  Licensed under the MIT License. See License.txt in the project root for license information.
     *--------------------------------------------------------------------------------------------*/
    'use strict';
    Object.defineProperty(exports, "__esModule", { value: true });
    var scanner_1 = require("./scanner");
    function format(documentText, range, options) {
        var initialIndentLevel;
        var formatText;
        var formatTextStart;
        var rangeStart;
        var rangeEnd;
        if (range) {
            rangeStart = range.offset;
            rangeEnd = rangeStart + range.length;
            formatTextStart = rangeStart;
            while (formatTextStart > 0 && !isEOL(documentText, formatTextStart - 1)) {
                formatTextStart--;
            }
            var endOffset = rangeEnd;
            while (endOffset < documentText.length && !isEOL(documentText, endOffset)) {
                endOffset++;
            }
            formatText = documentText.substring(formatTextStart, endOffset);
            initialIndentLevel = computeIndentLevel(formatText, options);
        }
        else {
            formatText = documentText;
            initialIndentLevel = 0;
            formatTextStart = 0;
            rangeStart = 0;
            rangeEnd = documentText.length;
        }
        var eol = getEOL(options, documentText);
        var lineBreak = false;
        var indentLevel = 0;
        var indentValue;
        if (options.insertSpaces) {
            indentValue = repeat(' ', options.tabSize || 4);
        }
        else {
            indentValue = '\t';
        }
        var scanner = scanner_1.createScanner(formatText, false);
        var hasError = false;
        function newLineAndIndent() {
            return eol + repeat(indentValue, initialIndentLevel + indentLevel);
        }
        function scanNext() {
            var token = scanner.scan();
            lineBreak = false;
            while (token === 15 /* Trivia */ || token === 14 /* LineBreakTrivia */) {
                lineBreak = lineBreak || (token === 14 /* LineBreakTrivia */);
                token = scanner.scan();
            }
            hasError = token === 16 /* Unknown */ || scanner.getTokenError() !== 0 /* None */;
            return token;
        }
        var editOperations = [];
        function addEdit(text, startOffset, endOffset) {
            if (!hasError && startOffset < rangeEnd && endOffset > rangeStart && documentText.substring(startOffset, endOffset) !== text) {
                editOperations.push({ offset: startOffset, length: endOffset - startOffset, content: text });
            }
        }
        var firstToken = scanNext();
        if (firstToken !== 17 /* EOF */) {
            var firstTokenStart = scanner.getTokenOffset() + formatTextStart;
            var initialIndent = repeat(indentValue, initialIndentLevel);
            addEdit(initialIndent, formatTextStart, firstTokenStart);
        }
        while (firstToken !== 17 /* EOF */) {
            var firstTokenEnd = scanner.getTokenOffset() + scanner.getTokenLength() + formatTextStart;
            var secondToken = scanNext();
            var replaceContent = '';
            while (!lineBreak && (secondToken === 12 /* LineCommentTrivia */ || secondToken === 13 /* BlockCommentTrivia */)) {
                // comments on the same line: keep them on the same line, but ignore them otherwise
                var commentTokenStart = scanner.getTokenOffset() + formatTextStart;
                addEdit(' ', firstTokenEnd, commentTokenStart);
                firstTokenEnd = scanner.getTokenOffset() + scanner.getTokenLength() + formatTextStart;
                replaceContent = secondToken === 12 /* LineCommentTrivia */ ? newLineAndIndent() : '';
                secondToken = scanNext();
            }
            if (secondToken === 2 /* CloseBraceToken */) {
                if (firstToken !== 1 /* OpenBraceToken */) {
                    indentLevel--;
                    replaceContent = newLineAndIndent();
                }
            }
            else if (secondToken === 4 /* CloseBracketToken */) {
                if (firstToken !== 3 /* OpenBracketToken */) {
                    indentLevel--;
                    replaceContent = newLineAndIndent();
                }
            }
            else {
                switch (firstToken) {
                    case 3 /* OpenBracketToken */:
                    case 1 /* OpenBraceToken */:
                        indentLevel++;
                        replaceContent = newLineAndIndent();
                        break;
                    case 5 /* CommaToken */:
                    case 12 /* LineCommentTrivia */:
                        replaceContent = newLineAndIndent();
                        break;
                    case 13 /* BlockCommentTrivia */:
                        if (lineBreak) {
                            replaceContent = newLineAndIndent();
                        }
                        else {
                            // symbol following comment on the same line: keep on same line, separate with ' '
                            replaceContent = ' ';
                        }
                        break;
                    case 6 /* ColonToken */:
                        replaceContent = ' ';
                        break;
                    case 10 /* StringLiteral */:
                        if (secondToken === 6 /* ColonToken */) {
                            replaceContent = '';
                            break;
                        }
                    // fall through
                    case 7 /* NullKeyword */:
                    case 8 /* TrueKeyword */:
                    case 9 /* FalseKeyword */:
                    case 11 /* NumericLiteral */:
                    case 2 /* CloseBraceToken */:
                    case 4 /* CloseBracketToken */:
                        if (secondToken === 12 /* LineCommentTrivia */ || secondToken === 13 /* BlockCommentTrivia */) {
                            replaceContent = ' ';
                        }
                        else if (secondToken !== 5 /* CommaToken */ && secondToken !== 17 /* EOF */) {
                            hasError = true;
                        }
                        break;
                    case 16 /* Unknown */:
                        hasError = true;
                        break;
                }
                if (lineBreak && (secondToken === 12 /* LineCommentTrivia */ || secondToken === 13 /* BlockCommentTrivia */)) {
                    replaceContent = newLineAndIndent();
                }
            }
            var secondTokenStart = scanner.getTokenOffset() + formatTextStart;
            addEdit(replaceContent, firstTokenEnd, secondTokenStart);
            firstToken = secondToken;
        }
        return editOperations;
    }
    exports.format = format;
    function repeat(s, count) {
        var result = '';
        for (var i = 0; i < count; i++) {
            result += s;
        }
        return result;
    }
    function computeIndentLevel(content, options) {
        var i = 0;
        var nChars = 0;
        var tabSize = options.tabSize || 4;
        while (i < content.length) {
            var ch = content.charAt(i);
            if (ch === ' ') {
                nChars++;
            }
            else if (ch === '\t') {
                nChars += tabSize;
            }
            else {
                break;
            }
            i++;
        }
        return Math.floor(nChars / tabSize);
    }
    function getEOL(options, text) {
        for (var i = 0; i < text.length; i++) {
            var ch = text.charAt(i);
            if (ch === '\r') {
                if (i + 1 < text.length && text.charAt(i + 1) === '\n') {
                    return '\r\n';
                }
                return '\r';
            }
            else if (ch === '\n') {
                return '\n';
            }
        }
        return (options && options.eol) || '\n';
    }
    function isEOL(text, offset) {
        return '\r\n'.indexOf(text.charAt(offset)) !== -1;
    }
    exports.isEOL = isEOL;
});
//# sourceMappingURL=format.js.map;
(function (factory) {
    if (typeof module === "object" && typeof module.exports === "object") {
        var v = factory(require, exports);
        if (v !== undefined) module.exports = v;
    }
    else if (typeof define === "function" && define.amd) {
        define('jsonc-parser/impl/parser',["require", "exports", "./scanner"], factory);
    }
})(function (require, exports) {
    /*---------------------------------------------------------------------------------------------
     *  Copyright (c) Microsoft Corporation. All rights reserved.
     *  Licensed under the MIT License. See License.txt in the project root for license information.
     *--------------------------------------------------------------------------------------------*/
    'use strict';
    Object.defineProperty(exports, "__esModule", { value: true });
    var scanner_1 = require("./scanner");
    var ParseOptions;
    (function (ParseOptions) {
        ParseOptions.DEFAULT = {
            allowTrailingComma: false
        };
    })(ParseOptions || (ParseOptions = {}));
    /**
     * For a given offset, evaluate the location in the JSON document. Each segment in the location path is either a property name or an array index.
     */
    function getLocation(text, position) {
        var segments = []; // strings or numbers
        var earlyReturnException = new Object();
        var previousNode = void 0;
        var previousNodeInst = {
            value: {},
            offset: 0,
            length: 0,
            type: 'object',
            parent: void 0
        };
        var isAtPropertyKey = false;
        function setPreviousNode(value, offset, length, type) {
            previousNodeInst.value = value;
            previousNodeInst.offset = offset;
            previousNodeInst.length = length;
            previousNodeInst.type = type;
            previousNodeInst.colonOffset = void 0;
            previousNode = previousNodeInst;
        }
        try {
            visit(text, {
                onObjectBegin: function (offset, length) {
                    if (position <= offset) {
                        throw earlyReturnException;
                    }
                    previousNode = void 0;
                    isAtPropertyKey = position > offset;
                    segments.push(''); // push a placeholder (will be replaced)
                },
                onObjectProperty: function (name, offset, length) {
                    if (position < offset) {
                        throw earlyReturnException;
                    }
                    setPreviousNode(name, offset, length, 'property');
                    segments[segments.length - 1] = name;
                    if (position <= offset + length) {
                        throw earlyReturnException;
                    }
                },
                onObjectEnd: function (offset, length) {
                    if (position <= offset) {
                        throw earlyReturnException;
                    }
                    previousNode = void 0;
                    segments.pop();
                },
                onArrayBegin: function (offset, length) {
                    if (position <= offset) {
                        throw earlyReturnException;
                    }
                    previousNode = void 0;
                    segments.push(0);
                },
                onArrayEnd: function (offset, length) {
                    if (position <= offset) {
                        throw earlyReturnException;
                    }
                    previousNode = void 0;
                    segments.pop();
                },
                onLiteralValue: function (value, offset, length) {
                    if (position < offset) {
                        throw earlyReturnException;
                    }
                    setPreviousNode(value, offset, length, getLiteralNodeType(value));
                    if (position <= offset + length) {
                        throw earlyReturnException;
                    }
                },
                onSeparator: function (sep, offset, length) {
                    if (position <= offset) {
                        throw earlyReturnException;
                    }
                    if (sep === ':' && previousNode && previousNode.type === 'property') {
                        previousNode.colonOffset = offset;
                        isAtPropertyKey = false;
                        previousNode = void 0;
                    }
                    else if (sep === ',') {
                        var last = segments[segments.length - 1];
                        if (typeof last === 'number') {
                            segments[segments.length - 1] = last + 1;
                        }
                        else {
                            isAtPropertyKey = true;
                            segments[segments.length - 1] = '';
                        }
                        previousNode = void 0;
                    }
                }
            });
        }
        catch (e) {
            if (e !== earlyReturnException) {
                throw e;
            }
        }
        return {
            path: segments,
            previousNode: previousNode,
            isAtPropertyKey: isAtPropertyKey,
            matches: function (pattern) {
                var k = 0;
                for (var i = 0; k < pattern.length && i < segments.length; i++) {
                    if (pattern[k] === segments[i] || pattern[k] === '*') {
                        k++;
                    }
                    else if (pattern[k] !== '**') {
                        return false;
                    }
                }
                return k === pattern.length;
            }
        };
    }
    exports.getLocation = getLocation;
    /**
     * Parses the given text and returns the object the JSON content represents. On invalid input, the parser tries to be as fault tolerant as possible, but still return a result.
     * Therefore always check the errors list to find out if the input was valid.
     */
    function parse(text, errors, options) {
        if (errors === void 0) { errors = []; }
        if (options === void 0) { options = ParseOptions.DEFAULT; }
        var currentProperty = null;
        var currentParent = [];
        var previousParents = [];
        function onValue(value) {
            if (Array.isArray(currentParent)) {
                currentParent.push(value);
            }
            else if (currentProperty) {
                currentParent[currentProperty] = value;
            }
        }
        var visitor = {
            onObjectBegin: function () {
                var object = {};
                onValue(object);
                previousParents.push(currentParent);
                currentParent = object;
                currentProperty = null;
            },
            onObjectProperty: function (name) {
                currentProperty = name;
            },
            onObjectEnd: function () {
                currentParent = previousParents.pop();
            },
            onArrayBegin: function () {
                var array = [];
                onValue(array);
                previousParents.push(currentParent);
                currentParent = array;
                currentProperty = null;
            },
            onArrayEnd: function () {
                currentParent = previousParents.pop();
            },
            onLiteralValue: onValue,
            onError: function (error, offset, length) {
                errors.push({ error: error, offset: offset, length: length });
            }
        };
        visit(text, visitor, options);
        return currentParent[0];
    }
    exports.parse = parse;
    /**
     * Parses the given text and returns a tree representation the JSON content. On invalid input, the parser tries to be as fault tolerant as possible, but still return a result.
     */
    function parseTree(text, errors, options) {
        if (errors === void 0) { errors = []; }
        if (options === void 0) { options = ParseOptions.DEFAULT; }
        var currentParent = { type: 'array', offset: -1, length: -1, children: [], parent: void 0 }; // artificial root
        function ensurePropertyComplete(endOffset) {
            if (currentParent.type === 'property') {
                currentParent.length = endOffset - currentParent.offset;
                currentParent = currentParent.parent;
            }
        }
        function onValue(valueNode) {
            currentParent.children.push(valueNode);
            return valueNode;
        }
        var visitor = {
            onObjectBegin: function (offset) {
                currentParent = onValue({ type: 'object', offset: offset, length: -1, parent: currentParent, children: [] });
            },
            onObjectProperty: function (name, offset, length) {
                currentParent = onValue({ type: 'property', offset: offset, length: -1, parent: currentParent, children: [] });
                currentParent.children.push({ type: 'string', value: name, offset: offset, length: length, parent: currentParent });
            },
            onObjectEnd: function (offset, length) {
                currentParent.length = offset + length - currentParent.offset;
                currentParent = currentParent.parent;
                ensurePropertyComplete(offset + length);
            },
            onArrayBegin: function (offset, length) {
                currentParent = onValue({ type: 'array', offset: offset, length: -1, parent: currentParent, children: [] });
            },
            onArrayEnd: function (offset, length) {
                currentParent.length = offset + length - currentParent.offset;
                currentParent = currentParent.parent;
                ensurePropertyComplete(offset + length);
            },
            onLiteralValue: function (value, offset, length) {
                onValue({ type: getLiteralNodeType(value), offset: offset, length: length, parent: currentParent, value: value });
                ensurePropertyComplete(offset + length);
            },
            onSeparator: function (sep, offset, length) {
                if (currentParent.type === 'property') {
                    if (sep === ':') {
                        currentParent.colonOffset = offset;
                    }
                    else if (sep === ',') {
                        ensurePropertyComplete(offset);
                    }
                }
            },
            onError: function (error, offset, length) {
                errors.push({ error: error, offset: offset, length: length });
            }
        };
        visit(text, visitor, options);
        var result = currentParent.children[0];
        if (result) {
            delete result.parent;
        }
        return result;
    }
    exports.parseTree = parseTree;
    /**
     * Finds the node at the given path in a JSON DOM.
     */
    function findNodeAtLocation(root, path) {
        if (!root) {
            return void 0;
        }
        var node = root;
        for (var _i = 0, path_1 = path; _i < path_1.length; _i++) {
            var segment = path_1[_i];
            if (typeof segment === 'string') {
                if (node.type !== 'object' || !Array.isArray(node.children)) {
                    return void 0;
                }
                var found = false;
                for (var _a = 0, _b = node.children; _a < _b.length; _a++) {
                    var propertyNode = _b[_a];
                    if (Array.isArray(propertyNode.children) && propertyNode.children[0].value === segment) {
                        node = propertyNode.children[1];
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return void 0;
                }
            }
            else {
                var index = segment;
                if (node.type !== 'array' || index < 0 || !Array.isArray(node.children) || index >= node.children.length) {
                    return void 0;
                }
                node = node.children[index];
            }
        }
        return node;
    }
    exports.findNodeAtLocation = findNodeAtLocation;
    /**
     * Gets the JSON path of the given JSON DOM node
     */
    function getNodePath(node) {
        if (!node.parent || !node.parent.children) {
            return [];
        }
        var path = getNodePath(node.parent);
        if (node.parent.type === 'property') {
            var key = node.parent.children[0].value;
            path.push(key);
        }
        else if (node.parent.type === 'array') {
            var index = node.parent.children.indexOf(node);
            if (index !== -1) {
                path.push(index);
            }
        }
        return path;
    }
    exports.getNodePath = getNodePath;
    /**
     * Evaluates the JavaScript object of the given JSON DOM node
     */
    function getNodeValue(node) {
        switch (node.type) {
            case 'array':
                return node.children.map(getNodeValue);
            case 'object':
                var obj = Object.create(null);
                for (var _i = 0, _a = node.children; _i < _a.length; _i++) {
                    var prop = _a[_i];
                    var valueNode = prop.children[1];
                    if (valueNode) {
                        obj[prop.children[0].value] = getNodeValue(valueNode);
                    }
                }
                return obj;
            case 'null':
            case 'string':
            case 'number':
            case 'boolean':
                return node.value;
            default:
                return void 0;
        }
    }
    exports.getNodeValue = getNodeValue;
    function contains(node, offset, includeRightBound) {
        if (includeRightBound === void 0) { includeRightBound = false; }
        return (offset >= node.offset && offset < (node.offset + node.length)) || includeRightBound && (offset === (node.offset + node.length));
    }
    exports.contains = contains;
    /**
     * Finds the most inner node at the given offset. If includeRightBound is set, also finds nodes that end at the given offset.
     */
    function findNodeAtOffset(node, offset, includeRightBound) {
        if (includeRightBound === void 0) { includeRightBound = false; }
        if (contains(node, offset, includeRightBound)) {
            var children = node.children;
            if (Array.isArray(children)) {
                for (var i = 0; i < children.length && children[i].offset <= offset; i++) {
                    var item = findNodeAtOffset(children[i], offset, includeRightBound);
                    if (item) {
                        return item;
                    }
                }
            }
            return node;
        }
        return void 0;
    }
    exports.findNodeAtOffset = findNodeAtOffset;
    /**
     * Parses the given text and invokes the visitor functions for each object, array and literal reached.
     */
    function visit(text, visitor, options) {
        if (options === void 0) { options = ParseOptions.DEFAULT; }
        var _scanner = scanner_1.createScanner(text, false);
        function toNoArgVisit(visitFunction) {
            return visitFunction ? function () { return visitFunction(_scanner.getTokenOffset(), _scanner.getTokenLength()); } : function () { return true; };
        }
        function toOneArgVisit(visitFunction) {
            return visitFunction ? function (arg) { return visitFunction(arg, _scanner.getTokenOffset(), _scanner.getTokenLength()); } : function () { return true; };
        }
        var onObjectBegin = toNoArgVisit(visitor.onObjectBegin), onObjectProperty = toOneArgVisit(visitor.onObjectProperty), onObjectEnd = toNoArgVisit(visitor.onObjectEnd), onArrayBegin = toNoArgVisit(visitor.onArrayBegin), onArrayEnd = toNoArgVisit(visitor.onArrayEnd), onLiteralValue = toOneArgVisit(visitor.onLiteralValue), onSeparator = toOneArgVisit(visitor.onSeparator), onComment = toNoArgVisit(visitor.onComment), onError = toOneArgVisit(visitor.onError);
        var disallowComments = options && options.disallowComments;
        var allowTrailingComma = options && options.allowTrailingComma;
        function scanNext() {
            while (true) {
                var token = _scanner.scan();
                switch (_scanner.getTokenError()) {
                    case 4 /* InvalidUnicode */:
                        handleError(14 /* InvalidUnicode */);
                        break;
                    case 5 /* InvalidEscapeCharacter */:
                        handleError(15 /* InvalidEscapeCharacter */);
                        break;
                    case 3 /* UnexpectedEndOfNumber */:
                        handleError(13 /* UnexpectedEndOfNumber */);
                        break;
                    case 1 /* UnexpectedEndOfComment */:
                        if (!disallowComments) {
                            handleError(11 /* UnexpectedEndOfComment */);
                        }
                        break;
                    case 2 /* UnexpectedEndOfString */:
                        handleError(12 /* UnexpectedEndOfString */);
                        break;
                    case 6 /* InvalidCharacter */:
                        handleError(16 /* InvalidCharacter */);
                        break;
                }
                switch (token) {
                    case 12 /* LineCommentTrivia */:
                    case 13 /* BlockCommentTrivia */:
                        if (disallowComments) {
                            handleError(10 /* InvalidCommentToken */);
                        }
                        else {
                            onComment();
                        }
                        break;
                    case 16 /* Unknown */:
                        handleError(1 /* InvalidSymbol */);
                        break;
                    case 15 /* Trivia */:
                    case 14 /* LineBreakTrivia */:
                        break;
                    default:
                        return token;
                }
            }
        }
        function handleError(error, skipUntilAfter, skipUntil) {
            if (skipUntilAfter === void 0) { skipUntilAfter = []; }
            if (skipUntil === void 0) { skipUntil = []; }
            onError(error);
            if (skipUntilAfter.length + skipUntil.length > 0) {
                var token = _scanner.getToken();
                while (token !== 17 /* EOF */) {
                    if (skipUntilAfter.indexOf(token) !== -1) {
                        scanNext();
                        break;
                    }
                    else if (skipUntil.indexOf(token) !== -1) {
                        break;
                    }
                    token = scanNext();
                }
            }
        }
        function parseString(isValue) {
            var value = _scanner.getTokenValue();
            if (isValue) {
                onLiteralValue(value);
            }
            else {
                onObjectProperty(value);
            }
            scanNext();
            return true;
        }
        function parseLiteral() {
            switch (_scanner.getToken()) {
                case 11 /* NumericLiteral */:
                    var value = 0;
                    try {
                        value = JSON.parse(_scanner.getTokenValue());
                        if (typeof value !== 'number') {
                            handleError(2 /* InvalidNumberFormat */);
                            value = 0;
                        }
                    }
                    catch (e) {
                        handleError(2 /* InvalidNumberFormat */);
                    }
                    onLiteralValue(value);
                    break;
                case 7 /* NullKeyword */:
                    onLiteralValue(null);
                    break;
                case 8 /* TrueKeyword */:
                    onLiteralValue(true);
                    break;
                case 9 /* FalseKeyword */:
                    onLiteralValue(false);
                    break;
                default:
                    return false;
            }
            scanNext();
            return true;
        }
        function parseProperty() {
            if (_scanner.getToken() !== 10 /* StringLiteral */) {
                handleError(3 /* PropertyNameExpected */, [], [2 /* CloseBraceToken */, 5 /* CommaToken */]);
                return false;
            }
            parseString(false);
            if (_scanner.getToken() === 6 /* ColonToken */) {
                onSeparator(':');
                scanNext(); // consume colon
                if (!parseValue()) {
                    handleError(4 /* ValueExpected */, [], [2 /* CloseBraceToken */, 5 /* CommaToken */]);
                }
            }
            else {
                handleError(5 /* ColonExpected */, [], [2 /* CloseBraceToken */, 5 /* CommaToken */]);
            }
            return true;
        }
        function parseObject() {
            onObjectBegin();
            scanNext(); // consume open brace
            var needsComma = false;
            while (_scanner.getToken() !== 2 /* CloseBraceToken */ && _scanner.getToken() !== 17 /* EOF */) {
                if (_scanner.getToken() === 5 /* CommaToken */) {
                    if (!needsComma) {
                        handleError(4 /* ValueExpected */, [], []);
                    }
                    onSeparator(',');
                    scanNext(); // consume comma
                    if (_scanner.getToken() === 2 /* CloseBraceToken */ && allowTrailingComma) {
                        break;
                    }
                }
                else if (needsComma) {
                    handleError(6 /* CommaExpected */, [], []);
                }
                if (!parseProperty()) {
                    handleError(4 /* ValueExpected */, [], [2 /* CloseBraceToken */, 5 /* CommaToken */]);
                }
                needsComma = true;
            }
            onObjectEnd();
            if (_scanner.getToken() !== 2 /* CloseBraceToken */) {
                handleError(7 /* CloseBraceExpected */, [2 /* CloseBraceToken */], []);
            }
            else {
                scanNext(); // consume close brace
            }
            return true;
        }
        function parseArray() {
            onArrayBegin();
            scanNext(); // consume open bracket
            var needsComma = false;
            while (_scanner.getToken() !== 4 /* CloseBracketToken */ && _scanner.getToken() !== 17 /* EOF */) {
                if (_scanner.getToken() === 5 /* CommaToken */) {
                    if (!needsComma) {
                        handleError(4 /* ValueExpected */, [], []);
                    }
                    onSeparator(',');
                    scanNext(); // consume comma
                    if (_scanner.getToken() === 4 /* CloseBracketToken */ && allowTrailingComma) {
                        break;
                    }
                }
                else if (needsComma) {
                    handleError(6 /* CommaExpected */, [], []);
                }
                if (!parseValue()) {
                    handleError(4 /* ValueExpected */, [], [4 /* CloseBracketToken */, 5 /* CommaToken */]);
                }
                needsComma = true;
            }
            onArrayEnd();
            if (_scanner.getToken() !== 4 /* CloseBracketToken */) {
                handleError(8 /* CloseBracketExpected */, [4 /* CloseBracketToken */], []);
            }
            else {
                scanNext(); // consume close bracket
            }
            return true;
        }
        function parseValue() {
            switch (_scanner.getToken()) {
                case 3 /* OpenBracketToken */:
                    return parseArray();
                case 1 /* OpenBraceToken */:
                    return parseObject();
                case 10 /* StringLiteral */:
                    return parseString(true);
                default:
                    return parseLiteral();
            }
        }
        scanNext();
        if (_scanner.getToken() === 17 /* EOF */) {
            return true;
        }
        if (!parseValue()) {
            handleError(4 /* ValueExpected */, [], []);
            return false;
        }
        if (_scanner.getToken() !== 17 /* EOF */) {
            handleError(9 /* EndOfFileExpected */, [], []);
        }
        return true;
    }
    exports.visit = visit;
    /**
     * Takes JSON with JavaScript-style comments and remove
     * them. Optionally replaces every none-newline character
     * of comments with a replaceCharacter
     */
    function stripComments(text, replaceCh) {
        var _scanner = scanner_1.createScanner(text), parts = [], kind, offset = 0, pos;
        do {
            pos = _scanner.getPosition();
            kind = _scanner.scan();
            switch (kind) {
                case 12 /* LineCommentTrivia */:
                case 13 /* BlockCommentTrivia */:
                case 17 /* EOF */:
                    if (offset !== pos) {
                        parts.push(text.substring(offset, pos));
                    }
                    if (replaceCh !== void 0) {
                        parts.push(_scanner.getTokenValue().replace(/[^\r\n]/g, replaceCh));
                    }
                    offset = _scanner.getPosition();
                    break;
            }
        } while (kind !== 17 /* EOF */);
        return parts.join('');
    }
    exports.stripComments = stripComments;
    function getLiteralNodeType(value) {
        switch (typeof value) {
            case 'boolean': return 'boolean';
            case 'number': return 'number';
            case 'string': return 'string';
            default: return 'null';
        }
    }
});
//# sourceMappingURL=parser.js.map;
(function (factory) {
    if (typeof module === "object" && typeof module.exports === "object") {
        var v = factory(require, exports);
        if (v !== undefined) module.exports = v;
    }
    else if (typeof define === "function" && define.amd) {
        define('jsonc-parser/impl/edit',["require", "exports", "./format", "./parser"], factory);
    }
})(function (require, exports) {
    /*---------------------------------------------------------------------------------------------
     *  Copyright (c) Microsoft Corporation. All rights reserved.
     *  Licensed under the MIT License. See License.txt in the project root for license information.
     *--------------------------------------------------------------------------------------------*/
    'use strict';
    Object.defineProperty(exports, "__esModule", { value: true });
    var format_1 = require("./format");
    var parser_1 = require("./parser");
    function removeProperty(text, path, formattingOptions) {
        return setProperty(text, path, void 0, formattingOptions);
    }
    exports.removeProperty = removeProperty;
    function setProperty(text, originalPath, value, formattingOptions, getInsertionIndex) {
        var _a;
        var path = originalPath.slice();
        var errors = [];
        var root = parser_1.parseTree(text, errors);
        var parent = void 0;
        var lastSegment = void 0;
        while (path.length > 0) {
            lastSegment = path.pop();
            parent = parser_1.findNodeAtLocation(root, path);
            if (parent === void 0 && value !== void 0) {
                if (typeof lastSegment === 'string') {
                    value = (_a = {}, _a[lastSegment] = value, _a);
                }
                else {
                    value = [value];
                }
            }
            else {
                break;
            }
        }
        if (!parent) {
            // empty document
            if (value === void 0) { // delete
                throw new Error('Can not delete in empty document');
            }
            return withFormatting(text, { offset: root ? root.offset : 0, length: root ? root.length : 0, content: JSON.stringify(value) }, formattingOptions);
        }
        else if (parent.type === 'object' && typeof lastSegment === 'string' && Array.isArray(parent.children)) {
            var existing = parser_1.findNodeAtLocation(parent, [lastSegment]);
            if (existing !== void 0) {
                if (value === void 0) { // delete
                    if (!existing.parent) {
                        throw new Error('Malformed AST');
                    }
                    var propertyIndex = parent.children.indexOf(existing.parent);
                    var removeBegin = void 0;
                    var removeEnd = existing.parent.offset + existing.parent.length;
                    if (propertyIndex > 0) {
                        // remove the comma of the previous node
                        var previous = parent.children[propertyIndex - 1];
                        removeBegin = previous.offset + previous.length;
                    }
                    else {
                        removeBegin = parent.offset + 1;
                        if (parent.children.length > 1) {
                            // remove the comma of the next node
                            var next = parent.children[1];
                            removeEnd = next.offset;
                        }
                    }
                    return withFormatting(text, { offset: removeBegin, length: removeEnd - removeBegin, content: '' }, formattingOptions);
                }
                else {
                    // set value of existing property
                    return withFormatting(text, { offset: existing.offset, length: existing.length, content: JSON.stringify(value) }, formattingOptions);
                }
            }
            else {
                if (value === void 0) { // delete
                    return []; // property does not exist, nothing to do
                }
                var newProperty = JSON.stringify(lastSegment) + ": " + JSON.stringify(value);
                var index = getInsertionIndex ? getInsertionIndex(parent.children.map(function (p) { return p.children[0].value; })) : parent.children.length;
                var edit = void 0;
                if (index > 0) {
                    var previous = parent.children[index - 1];
                    edit = { offset: previous.offset + previous.length, length: 0, content: ',' + newProperty };
                }
                else if (parent.children.length === 0) {
                    edit = { offset: parent.offset + 1, length: 0, content: newProperty };
                }
                else {
                    edit = { offset: parent.offset + 1, length: 0, content: newProperty + ',' };
                }
                return withFormatting(text, edit, formattingOptions);
            }
        }
        else if (parent.type === 'array' && typeof lastSegment === 'number' && Array.isArray(parent.children)) {
            var insertIndex = lastSegment;
            if (insertIndex === -1) {
                // Insert
                var newProperty = "" + JSON.stringify(value);
                var edit = void 0;
                if (parent.children.length === 0) {
                    edit = { offset: parent.offset + 1, length: 0, content: newProperty };
                }
                else {
                    var previous = parent.children[parent.children.length - 1];
                    edit = { offset: previous.offset + previous.length, length: 0, content: ',' + newProperty };
                }
                return withFormatting(text, edit, formattingOptions);
            }
            else {
                if (value === void 0 && parent.children.length >= 0) {
                    //Removal
                    var removalIndex = lastSegment;
                    var toRemove = parent.children[removalIndex];
                    var edit = void 0;
                    if (parent.children.length === 1) {
                        // only item
                        edit = { offset: parent.offset + 1, length: parent.length - 2, content: '' };
                    }
                    else if (parent.children.length - 1 === removalIndex) {
                        // last item
                        var previous = parent.children[removalIndex - 1];
                        var offset = previous.offset + previous.length;
                        var parentEndOffset = parent.offset + parent.length;
                        edit = { offset: offset, length: parentEndOffset - 2 - offset, content: '' };
                    }
                    else {
                        edit = { offset: toRemove.offset, length: parent.children[removalIndex + 1].offset - toRemove.offset, content: '' };
                    }
                    return withFormatting(text, edit, formattingOptions);
                }
                else {
                    throw new Error('Array modification not supported yet');
                }
            }
        }
        else {
            throw new Error("Can not add " + (typeof lastSegment !== 'number' ? 'index' : 'property') + " to parent of type " + parent.type);
        }
    }
    exports.setProperty = setProperty;
    function withFormatting(text, edit, formattingOptions) {
        // apply the edit
        var newText = applyEdit(text, edit);
        // format the new text
        var begin = edit.offset;
        var end = edit.offset + edit.content.length;
        if (edit.length === 0 || edit.content.length === 0) { // insert or remove
            while (begin > 0 && !format_1.isEOL(newText, begin - 1)) {
                begin--;
            }
            while (end < newText.length && !format_1.isEOL(newText, end)) {
                end++;
            }
        }
        var edits = format_1.format(newText, { offset: begin, length: end - begin }, formattingOptions);
        // apply the formatting edits and track the begin and end offsets of the changes
        for (var i = edits.length - 1; i >= 0; i--) {
            var edit_1 = edits[i];
            newText = applyEdit(newText, edit_1);
            begin = Math.min(begin, edit_1.offset);
            end = Math.max(end, edit_1.offset + edit_1.length);
            end += edit_1.content.length - edit_1.length;
        }
        // create a single edit with all changes
        var editLength = text.length - (newText.length - end) - begin;
        return [{ offset: begin, length: editLength, content: newText.substring(begin, end) }];
    }
    function applyEdit(text, edit) {
        return text.substring(0, edit.offset) + edit.content + text.substring(edit.offset + edit.length);
    }
    exports.applyEdit = applyEdit;
    function isWS(text, offset) {
        return '\r\n \t'.indexOf(text.charAt(offset)) !== -1;
    }
    exports.isWS = isWS;
});
//# sourceMappingURL=edit.js.map;
(function (factory) {
    if (typeof module === "object" && typeof module.exports === "object") {
        var v = factory(require, exports);
        if (v !== undefined) module.exports = v;
    }
    else if (typeof define === "function" && define.amd) {
        define('jsonc-parser/main',["require", "exports", "./impl/format", "./impl/edit", "./impl/scanner", "./impl/parser"], factory);
    }
})(function (require, exports) {
    /*---------------------------------------------------------------------------------------------
     *  Copyright (c) Microsoft Corporation. All rights reserved.
     *  Licensed under the MIT License. See License.txt in the project root for license information.
     *--------------------------------------------------------------------------------------------*/
    'use strict';
    Object.defineProperty(exports, "__esModule", { value: true });
    var formatter = require("./impl/format");
    var edit = require("./impl/edit");
    var scanner = require("./impl/scanner");
    var parser = require("./impl/parser");
    /**
     * Creates a JSON scanner on the given text.
     * If ignoreTrivia is set, whitespaces or comments are ignored.
     */
    exports.createScanner = scanner.createScanner;
    /**
     * For a given offset, evaluate the location in the JSON document. Each segment in the location path is either a property name or an array index.
     */
    exports.getLocation = parser.getLocation;
    /**
     * Parses the given text and returns the object the JSON content represents. On invalid input, the parser tries to be as fault tolerant as possible, but still return a result.
     * Therefore always check the errors list to find out if the input was valid.
     */
    exports.parse = parser.parse;
    /**
     * Parses the given text and returns a tree representation the JSON content. On invalid input, the parser tries to be as fault tolerant as possible, but still return a result.
     */
    exports.parseTree = parser.parseTree;
    /**
     * Finds the node at the given path in a JSON DOM.
     */
    exports.findNodeAtLocation = parser.findNodeAtLocation;
    /**
     * Finds the most inner node at the given offset. If includeRightBound is set, also finds nodes that end at the given offset.
     */
    exports.findNodeAtOffset = parser.findNodeAtOffset;
    /**
     * Gets the JSON path of the given JSON DOM node
     */
    exports.getNodePath = parser.getNodePath;
    /**
     * Evaluates the JavaScript object of the given JSON DOM node
     */
    exports.getNodeValue = parser.getNodeValue;
    /**
     * Parses the given text and invokes the visitor functions for each object, array and literal reached.
     */
    exports.visit = parser.visit;
    /**
     * Takes JSON with JavaScript-style comments and remove
     * them. Optionally replaces every none-newline character
     * of comments with a replaceCharacter
     */
    exports.stripComments = parser.stripComments;
    function printParseErrorCode(code) {
        switch (code) {
            case 1 /* InvalidSymbol */: return 'InvalidSymbol';
            case 2 /* InvalidNumberFormat */: return 'InvalidNumberFormat';
            case 3 /* PropertyNameExpected */: return 'PropertyNameExpected';
            case 4 /* ValueExpected */: return 'ValueExpected';
            case 5 /* ColonExpected */: return 'ColonExpected';
            case 6 /* CommaExpected */: return 'CommaExpected';
            case 7 /* CloseBraceExpected */: return 'CloseBraceExpected';
            case 8 /* CloseBracketExpected */: return 'CloseBracketExpected';
            case 9 /* EndOfFileExpected */: return 'EndOfFileExpected';
            case 10 /* InvalidCommentToken */: return 'InvalidCommentToken';
            case 11 /* UnexpectedEndOfComment */: return 'UnexpectedEndOfComment';
            case 12 /* UnexpectedEndOfString */: return 'UnexpectedEndOfString';
            case 13 /* UnexpectedEndOfNumber */: return 'UnexpectedEndOfNumber';
            case 14 /* InvalidUnicode */: return 'InvalidUnicode';
            case 15 /* InvalidEscapeCharacter */: return 'InvalidEscapeCharacter';
            case 16 /* InvalidCharacter */: return 'InvalidCharacter';
        }
        return '<unknown ParseErrorCode>';
    }
    exports.printParseErrorCode = printParseErrorCode;
    /**
     * Computes the edits needed to format a JSON document.
     *
     * @param documentText The input text
     * @param range The range to format or `undefined` to format the full content
     * @param options The formatting options
     * @returns A list of edit operations describing the formatting changes to the original document. Edits can be either inserts, replacements or
     * removals of text segments. All offsets refer to the original state of the document. No two edits must change or remove the same range of
     * text in the original document. However, multiple edits can have
     * the same offset, for example multiple inserts, or an insert followed by a remove or replace. The order in the array defines which edit is applied first.
     * To apply edits to an input, you can use `applyEdits`
     */
    function format(documentText, range, options) {
        return formatter.format(documentText, range, options);
    }
    exports.format = format;
    /**
     * Computes the edits needed to modify a value in the JSON document.
     *
     * @param documentText The input text
     * @param path The path of the value to change. The path represents either to the document root, a property or an array item.
     * If the path points to an non-existing property or item, it will be created.
     * @param value The new value for the specified property or item. If the value is undefined,
     * the property or item will be removed.
     * @param options Options
     * @returns A list of edit operations describing the formatting changes to the original document. Edits can be either inserts, replacements or
     * removals of text segments. All offsets refer to the original state of the document. No two edits must change or remove the same range of
     * text in the original document. However, multiple edits can have
     * the same offset, for example multiple inserts, or an insert followed by a remove or replace. The order in the array defines which edit is applied first.
     * To apply edits to an input, you can use `applyEdits`
     */
    function modify(text, path, value, options) {
        return edit.setProperty(text, path, value, options.formattingOptions, options.getInsertionIndex);
    }
    exports.modify = modify;
    /**
     * Applies edits to a input string.
     */
    function applyEdits(text, edits) {
        for (var i = edits.length - 1; i >= 0; i--) {
            text = edit.applyEdit(text, edits[i]);
        }
        return text;
    }
    exports.applyEdits = applyEdits;
});
//# sourceMappingURL=main.js.map;
define('jsonc-parser', ['jsonc-parser/main'], function (main) { return main; });

/*---------------------------------------------------------------------------------------------
*  Copyright (c) Microsoft Corporation. All rights reserved.
*  Licensed under the MIT License. See License.txt in the project root for license information.
*--------------------------------------------------------------------------------------------*/
(function (factory) {
    if (typeof module === "object" && typeof module.exports === "object") {
        var v = factory(require, exports);
        if (v !== undefined) module.exports = v;
    }
    else if (typeof define === "function" && define.amd) {
        define('vscode-json-languageservice/utils/objects',["require", "exports"], factory);
    }
})(function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    function equals(one, other) {
        if (one === other) {
            return true;
        }
        if (one === null || one === undefined || other === null || other === undefined) {
            return false;
        }
        if (typeof one !== typeof other) {
            return false;
        }
        if (typeof one !== 'object') {
            return false;
        }
        if ((Array.isArray(one)) !== (Array.isArray(other))) {
            return false;
        }
        var i, key;
        if (Array.isArray(one)) {
            if (one.length !== other.length) {
                return false;
            }
            for (i = 0; i < one.length; i++) {
                if (!equals(one[i], other[i])) {
                    return false;
                }
            }
        }
        else {
            var oneKeys = [];
            for (key in one) {
                oneKeys.push(key);
            }
            oneKeys.sort();
            var otherKeys = [];
            for (key in other) {
                otherKeys.push(key);
            }
            otherKeys.sort();
            if (!equals(oneKeys, otherKeys)) {
                return false;
            }
            for (i = 0; i < oneKeys.length; i++) {
                if (!equals(one[oneKeys[i]], other[oneKeys[i]])) {
                    return false;
                }
            }
        }
        return true;
    }
    exports.equals = equals;
    function isNumber(val) {
        return typeof val === 'number';
    }
    exports.isNumber = isNumber;
    function isDefined(val) {
        return typeof val !== 'undefined';
    }
    exports.isDefined = isDefined;
    function isBoolean(val) {
        return typeof val === 'boolean';
    }
    exports.isBoolean = isBoolean;
    function isString(val) {
        return typeof val === 'string';
    }
    exports.isString = isString;
});

(function (factory) {
    if (typeof module === "object" && typeof module.exports === "object") {
        var v = factory(require, exports);
        if (v !== undefined) module.exports = v;
    }
    else if (typeof define === "function" && define.amd) {
        define('vscode-json-languageservice/jsonLanguageTypes',["require", "exports", "vscode-languageserver-types"], factory);
    }
})(function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    var vscode_languageserver_types_1 = require("vscode-languageserver-types");
    exports.Range = vscode_languageserver_types_1.Range;
    exports.TextEdit = vscode_languageserver_types_1.TextEdit;
    exports.Color = vscode_languageserver_types_1.Color;
    exports.ColorInformation = vscode_languageserver_types_1.ColorInformation;
    exports.ColorPresentation = vscode_languageserver_types_1.ColorPresentation;
    exports.FoldingRange = vscode_languageserver_types_1.FoldingRange;
    exports.FoldingRangeKind = vscode_languageserver_types_1.FoldingRangeKind;
    // #region Proposed types, remove once added to vscode-languageserver-types
    /**
     * Enum of known selection range kinds
     */
    var SelectionRangeKind;
    (function (SelectionRangeKind) {
        /**
         * Empty Kind.
         */
        SelectionRangeKind["Empty"] = "";
        /**
         * The statment kind, its value is `statement`, possible extensions can be
         * `statement.if` etc
         */
        SelectionRangeKind["Statement"] = "statement";
        /**
         * The declaration kind, its value is `declaration`, possible extensions can be
         * `declaration.function`, `declaration.class` etc.
         */
        SelectionRangeKind["Declaration"] = "declaration";
    })(SelectionRangeKind = exports.SelectionRangeKind || (exports.SelectionRangeKind = {}));
    // #endregion
    /**
     * Error codes used by diagnostics
     */
    var ErrorCode;
    (function (ErrorCode) {
        ErrorCode[ErrorCode["Undefined"] = 0] = "Undefined";
        ErrorCode[ErrorCode["EnumValueMismatch"] = 1] = "EnumValueMismatch";
        ErrorCode[ErrorCode["UnexpectedEndOfComment"] = 257] = "UnexpectedEndOfComment";
        ErrorCode[ErrorCode["UnexpectedEndOfString"] = 258] = "UnexpectedEndOfString";
        ErrorCode[ErrorCode["UnexpectedEndOfNumber"] = 259] = "UnexpectedEndOfNumber";
        ErrorCode[ErrorCode["InvalidUnicode"] = 260] = "InvalidUnicode";
        ErrorCode[ErrorCode["InvalidEscapeCharacter"] = 261] = "InvalidEscapeCharacter";
        ErrorCode[ErrorCode["InvalidCharacter"] = 262] = "InvalidCharacter";
        ErrorCode[ErrorCode["PropertyExpected"] = 513] = "PropertyExpected";
        ErrorCode[ErrorCode["CommaExpected"] = 514] = "CommaExpected";
        ErrorCode[ErrorCode["ColonExpected"] = 515] = "ColonExpected";
        ErrorCode[ErrorCode["ValueExpected"] = 516] = "ValueExpected";
        ErrorCode[ErrorCode["CommaOrCloseBacketExpected"] = 517] = "CommaOrCloseBacketExpected";
        ErrorCode[ErrorCode["CommaOrCloseBraceExpected"] = 518] = "CommaOrCloseBraceExpected";
        ErrorCode[ErrorCode["TrailingComma"] = 519] = "TrailingComma";
        ErrorCode[ErrorCode["DuplicateKey"] = 520] = "DuplicateKey";
        ErrorCode[ErrorCode["CommentNotPermitted"] = 521] = "CommentNotPermitted";
        ErrorCode[ErrorCode["SchemaResolveError"] = 768] = "SchemaResolveError";
    })(ErrorCode = exports.ErrorCode || (exports.ErrorCode = {}));
    var ClientCapabilities;
    (function (ClientCapabilities) {
        ClientCapabilities.LATEST = {
            textDocument: {
                completion: {
                    completionItem: {
                        documentationFormat: [vscode_languageserver_types_1.MarkupKind.Markdown, vscode_languageserver_types_1.MarkupKind.PlainText]
                    }
                }
            }
        };
    })(ClientCapabilities = exports.ClientCapabilities || (exports.ClientCapabilities = {}));
});

/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
define('vscode-nls/vscode-nls',["require", "exports"], function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    function format(message, args) {
        var result;
        if (args.length === 0) {
            result = message;
        }
        else {
            result = message.replace(/\{(\d+)\}/g, function (match, rest) {
                var index = rest[0];
                return typeof args[index] !== 'undefined' ? args[index] : match;
            });
        }
        return result;
    }
    function localize(key, message) {
        var args = [];
        for (var _i = 2; _i < arguments.length; _i++) {
            args[_i - 2] = arguments[_i];
        }
        return format(message, args);
    }
    function loadMessageBundle(file) {
        return localize;
    }
    exports.loadMessageBundle = loadMessageBundle;
    function config(opt) {
        return loadMessageBundle;
    }
    exports.config = config;
});

define('vscode-nls', ['vscode-nls/vscode-nls'], function (main) { return main; });

var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
(function (factory) {
    if (typeof module === "object" && typeof module.exports === "object") {
        var v = factory(require, exports);
        if (v !== undefined) module.exports = v;
    }
    else if (typeof define === "function" && define.amd) {
        define('vscode-uri/index',["require", "exports"], factory);
    }
})(function (require, exports) {
    /*---------------------------------------------------------------------------------------------
     *  Copyright (c) Microsoft Corporation. All rights reserved.
     *  Licensed under the MIT License. See License.txt in the project root for license information.
     *--------------------------------------------------------------------------------------------*/
    'use strict';
    Object.defineProperty(exports, "__esModule", { value: true });
    var isWindows;
    if (typeof process === 'object') {
        isWindows = process.platform === 'win32';
    }
    else if (typeof navigator === 'object') {
        var userAgent = navigator.userAgent;
        isWindows = userAgent.indexOf('Windows') >= 0;
    }
    //#endregion
    var _schemePattern = /^\w[\w\d+.-]*$/;
    var _singleSlashStart = /^\//;
    var _doubleSlashStart = /^\/\//;
    function _validateUri(ret) {
        // scheme, https://tools.ietf.org/html/rfc3986#section-3.1
        // ALPHA *( ALPHA / DIGIT / "+" / "-" / "." )
        if (ret.scheme && !_schemePattern.test(ret.scheme)) {
            throw new Error('[UriError]: Scheme contains illegal characters.');
        }
        // path, http://tools.ietf.org/html/rfc3986#section-3.3
        // If a URI contains an authority component, then the path component
        // must either be empty or begin with a slash ("/") character.  If a URI
        // does not contain an authority component, then the path cannot begin
        // with two slash characters ("//").
        if (ret.path) {
            if (ret.authority) {
                if (!_singleSlashStart.test(ret.path)) {
                    throw new Error('[UriError]: If a URI contains an authority component, then the path component must either be empty or begin with a slash ("/") character');
                }
            }
            else {
                if (_doubleSlashStart.test(ret.path)) {
                    throw new Error('[UriError]: If a URI does not contain an authority component, then the path cannot begin with two slash characters ("//")');
                }
            }
        }
    }
    // implements a bit of https://tools.ietf.org/html/rfc3986#section-5
    function _referenceResolution(scheme, path) {
        // the slash-character is our 'default base' as we don't
        // support constructing URIs relative to other URIs. This
        // also means that we alter and potentially break paths.
        // see https://tools.ietf.org/html/rfc3986#section-5.1.4
        switch (scheme) {
            case 'https':
            case 'http':
            case 'file':
                if (!path) {
                    path = _slash;
                }
                else if (path[0] !== _slash) {
                    path = _slash + path;
                }
                break;
        }
        return path;
    }
    var _empty = '';
    var _slash = '/';
    var _regexp = /^(([^:/?#]+?):)?(\/\/([^/?#]*))?([^?#]*)(\?([^#]*))?(#(.*))?/;
    /**
     * Uniform Resource Identifier (URI) http://tools.ietf.org/html/rfc3986.
     * This class is a simple parser which creates the basic component parts
     * (http://tools.ietf.org/html/rfc3986#section-3) with minimal validation
     * and encoding.
     *
     *       foo://example.com:8042/over/there?name=ferret#nose
     *       \_/   \______________/\_________/ \_________/ \__/
     *        |           |            |            |        |
     *     scheme     authority       path        query   fragment
     *        |   _____________________|__
     *       / \ /                        \
     *       urn:example:animal:ferret:nose
     */
    var URI = (function () {
        /**
         * @internal
         */
        function URI(schemeOrData, authority, path, query, fragment) {
            if (typeof schemeOrData === 'object') {
                this.scheme = schemeOrData.scheme || _empty;
                this.authority = schemeOrData.authority || _empty;
                this.path = schemeOrData.path || _empty;
                this.query = schemeOrData.query || _empty;
                this.fragment = schemeOrData.fragment || _empty;
                // no validation because it's this URI
                // that creates uri components.
                // _validateUri(this);
            }
            else {
                this.scheme = schemeOrData || _empty;
                this.authority = authority || _empty;
                this.path = _referenceResolution(this.scheme, path || _empty);
                this.query = query || _empty;
                this.fragment = fragment || _empty;
                _validateUri(this);
            }
        }
        URI.isUri = function (thing) {
            if (thing instanceof URI) {
                return true;
            }
            if (!thing) {
                return false;
            }
            return typeof thing.authority === 'string'
                && typeof thing.fragment === 'string'
                && typeof thing.path === 'string'
                && typeof thing.query === 'string'
                && typeof thing.scheme === 'string';
        };
        Object.defineProperty(URI.prototype, "fsPath", {
            // ---- filesystem path -----------------------
            /**
             * Returns a string representing the corresponding file system path of this URI.
             * Will handle UNC paths, normalizes windows drive letters to lower-case, and uses the
             * platform specific path separator.
             *
             * * Will *not* validate the path for invalid characters and semantics.
             * * Will *not* look at the scheme of this URI.
             * * The result shall *not* be used for display purposes but for accessing a file on disk.
             *
             *
             * The *difference* to `URI#path` is the use of the platform specific separator and the handling
             * of UNC paths. See the below sample of a file-uri with an authority (UNC path).
             *
             * ```ts
                const u = URI.parse('file://server/c$/folder/file.txt')
                u.authority === 'server'
                u.path === '/shares/c$/file.txt'
                u.fsPath === '\\server\c$\folder\file.txt'
            ```
             *
             * Using `URI#path` to read a file (using fs-apis) would not be enough because parts of the path,
             * namely the server name, would be missing. Therefore `URI#fsPath` exists - it's sugar to ease working
             * with URIs that represent files on disk (`file` scheme).
             */
            get: function () {
                // if (this.scheme !== 'file') {
                // 	console.warn(`[UriError] calling fsPath with scheme ${this.scheme}`);
                // }
                return _makeFsPath(this);
            },
            enumerable: true,
            configurable: true
        });
        // ---- modify to new -------------------------
        URI.prototype.with = function (change) {
            if (!change) {
                return this;
            }
            var scheme = change.scheme, authority = change.authority, path = change.path, query = change.query, fragment = change.fragment;
            if (scheme === void 0) {
                scheme = this.scheme;
            }
            else if (scheme === null) {
                scheme = _empty;
            }
            if (authority === void 0) {
                authority = this.authority;
            }
            else if (authority === null) {
                authority = _empty;
            }
            if (path === void 0) {
                path = this.path;
            }
            else if (path === null) {
                path = _empty;
            }
            if (query === void 0) {
                query = this.query;
            }
            else if (query === null) {
                query = _empty;
            }
            if (fragment === void 0) {
                fragment = this.fragment;
            }
            else if (fragment === null) {
                fragment = _empty;
            }
            if (scheme === this.scheme
                && authority === this.authority
                && path === this.path
                && query === this.query
                && fragment === this.fragment) {
                return this;
            }
            return new _URI(scheme, authority, path, query, fragment);
        };
        // ---- parse & validate ------------------------
        /**
         * Creates a new URI from a string, e.g. `http://www.msft.com/some/path`,
         * `file:///usr/home`, or `scheme:with/path`.
         *
         * @param value A string which represents an URI (see `URI#toString`).
         */
        URI.parse = function (value) {
            var match = _regexp.exec(value);
            if (!match) {
                return new _URI(_empty, _empty, _empty, _empty, _empty);
            }
            return new _URI(match[2] || _empty, decodeURIComponent(match[4] || _empty), decodeURIComponent(match[5] || _empty), decodeURIComponent(match[7] || _empty), decodeURIComponent(match[9] || _empty));
        };
        /**
         * Creates a new URI from a file system path, e.g. `c:\my\files`,
         * `/usr/home`, or `\\server\share\some\path`.
         *
         * The *difference* between `URI#parse` and `URI#file` is that the latter treats the argument
         * as path, not as stringified-uri. E.g. `URI.file(path)` is **not the same as**
         * `URI.parse('file://' + path)` because the path might contain characters that are
         * interpreted (# and ?). See the following sample:
         * ```ts
        const good = URI.file('/coding/c#/project1');
        good.scheme === 'file';
        good.path === '/coding/c#/project1';
        good.fragment === '';
    
        const bad = URI.parse('file://' + '/coding/c#/project1');
        bad.scheme === 'file';
        bad.path === '/coding/c'; // path is now broken
        bad.fragment === '/project1';
        ```
         *
         * @param path A file system path (see `URI#fsPath`)
         */
        URI.file = function (path) {
            var authority = _empty;
            // normalize to fwd-slashes on windows,
            // on other systems bwd-slashes are valid
            // filename character, eg /f\oo/ba\r.txt
            if (isWindows) {
                path = path.replace(/\\/g, _slash);
            }
            // check for authority as used in UNC shares
            // or use the path as given
            if (path[0] === _slash && path[1] === _slash) {
                var idx = path.indexOf(_slash, 2);
                if (idx === -1) {
                    authority = path.substring(2);
                    path = _slash;
                }
                else {
                    authority = path.substring(2, idx);
                    path = path.substring(idx) || _slash;
                }
            }
            return new _URI('file', authority, path, _empty, _empty);
        };
        URI.from = function (components) {
            return new _URI(components.scheme, components.authority, components.path, components.query, components.fragment);
        };
        // ---- printing/externalize ---------------------------
        /**
         * Creates a string presentation for this URI. It's guardeed that calling
         * `URI.parse` with the result of this function creates an URI which is equal
         * to this URI.
         *
         * * The result shall *not* be used for display purposes but for externalization or transport.
         * * The result will be encoded using the percentage encoding and encoding happens mostly
         * ignore the scheme-specific encoding rules.
         *
         * @param skipEncoding Do not encode the result, default is `false`
         */
        URI.prototype.toString = function (skipEncoding) {
            if (skipEncoding === void 0) { skipEncoding = false; }
            return _asFormatted(this, skipEncoding);
        };
        URI.prototype.toJSON = function () {
            return this;
        };
        URI.revive = function (data) {
            if (!data) {
                return data;
            }
            else if (data instanceof URI) {
                return data;
            }
            else {
                var result = new _URI(data);
                result._fsPath = data.fsPath;
                result._formatted = data.external;
                return result;
            }
        };
        return URI;
    }());
    exports.default = URI;
    // tslint:disable-next-line:class-name
    var _URI = (function (_super) {
        __extends(_URI, _super);
        function _URI() {
            var _this = _super !== null && _super.apply(this, arguments) || this;
            _this._formatted = null;
            _this._fsPath = null;
            return _this;
        }
        Object.defineProperty(_URI.prototype, "fsPath", {
            get: function () {
                if (!this._fsPath) {
                    this._fsPath = _makeFsPath(this);
                }
                return this._fsPath;
            },
            enumerable: true,
            configurable: true
        });
        _URI.prototype.toString = function (skipEncoding) {
            if (skipEncoding === void 0) { skipEncoding = false; }
            if (!skipEncoding) {
                if (!this._formatted) {
                    this._formatted = _asFormatted(this, false);
                }
                return this._formatted;
            }
            else {
                // we don't cache that
                return _asFormatted(this, true);
            }
        };
        _URI.prototype.toJSON = function () {
            var res = {
                $mid: 1
            };
            // cached state
            if (this._fsPath) {
                res.fsPath = this._fsPath;
            }
            if (this._formatted) {
                res.external = this._formatted;
            }
            // uri components
            if (this.path) {
                res.path = this.path;
            }
            if (this.scheme) {
                res.scheme = this.scheme;
            }
            if (this.authority) {
                res.authority = this.authority;
            }
            if (this.query) {
                res.query = this.query;
            }
            if (this.fragment) {
                res.fragment = this.fragment;
            }
            return res;
        };
        return _URI;
    }(URI));
    // reserved characters: https://tools.ietf.org/html/rfc3986#section-2.2
    var encodeTable = (_a = {},
        _a[58 /* Colon */] = '%3A',
        _a[47 /* Slash */] = '%2F',
        _a[63 /* QuestionMark */] = '%3F',
        _a[35 /* Hash */] = '%23',
        _a[91 /* OpenSquareBracket */] = '%5B',
        _a[93 /* CloseSquareBracket */] = '%5D',
        _a[64 /* AtSign */] = '%40',
        _a[33 /* ExclamationMark */] = '%21',
        _a[36 /* DollarSign */] = '%24',
        _a[38 /* Ampersand */] = '%26',
        _a[39 /* SingleQuote */] = '%27',
        _a[40 /* OpenParen */] = '%28',
        _a[41 /* CloseParen */] = '%29',
        _a[42 /* Asterisk */] = '%2A',
        _a[43 /* Plus */] = '%2B',
        _a[44 /* Comma */] = '%2C',
        _a[59 /* Semicolon */] = '%3B',
        _a[61 /* Equals */] = '%3D',
        _a[32 /* Space */] = '%20',
        _a);
    function encodeURIComponentFast(uriComponent, allowSlash) {
        var res = undefined;
        var nativeEncodePos = -1;
        for (var pos = 0; pos < uriComponent.length; pos++) {
            var code = uriComponent.charCodeAt(pos);
            // unreserved characters: https://tools.ietf.org/html/rfc3986#section-2.3
            if ((code >= 97 /* a */ && code <= 122 /* z */)
                || (code >= 65 /* A */ && code <= 90 /* Z */)
                || (code >= 48 /* Digit0 */ && code <= 57 /* Digit9 */)
                || code === 45 /* Dash */
                || code === 46 /* Period */
                || code === 95 /* Underline */
                || code === 126 /* Tilde */
                || (allowSlash && code === 47 /* Slash */)) {
                // check if we are delaying native encode
                if (nativeEncodePos !== -1) {
                    res += encodeURIComponent(uriComponent.substring(nativeEncodePos, pos));
                    nativeEncodePos = -1;
                }
                // check if we write into a new string (by default we try to return the param)
                if (res !== undefined) {
                    res += uriComponent.charAt(pos);
                }
            }
            else {
                // encoding needed, we need to allocate a new string
                if (res === undefined) {
                    res = uriComponent.substr(0, pos);
                }
                // check with default table first
                var escaped = encodeTable[code];
                if (escaped !== undefined) {
                    // check if we are delaying native encode
                    if (nativeEncodePos !== -1) {
                        res += encodeURIComponent(uriComponent.substring(nativeEncodePos, pos));
                        nativeEncodePos = -1;
                    }
                    // append escaped variant to result
                    res += escaped;
                }
                else if (nativeEncodePos === -1) {
                    // use native encode only when needed
                    nativeEncodePos = pos;
                }
            }
        }
        if (nativeEncodePos !== -1) {
            res += encodeURIComponent(uriComponent.substring(nativeEncodePos));
        }
        return res !== undefined ? res : uriComponent;
    }
    function encodeURIComponentMinimal(path) {
        var res = undefined;
        for (var pos = 0; pos < path.length; pos++) {
            var code = path.charCodeAt(pos);
            if (code === 35 /* Hash */ || code === 63 /* QuestionMark */) {
                if (res === undefined) {
                    res = path.substr(0, pos);
                }
                res += encodeTable[code];
            }
            else {
                if (res !== undefined) {
                    res += path[pos];
                }
            }
        }
        return res !== undefined ? res : path;
    }
    /**
     * Compute `fsPath` for the given uri
     * @param uri
     */
    function _makeFsPath(uri) {
        var value;
        if (uri.authority && uri.path.length > 1 && uri.scheme === 'file') {
            // unc path: file://shares/c$/far/boo
            value = "//" + uri.authority + uri.path;
        }
        else if (uri.path.charCodeAt(0) === 47 /* Slash */
            && (uri.path.charCodeAt(1) >= 65 /* A */ && uri.path.charCodeAt(1) <= 90 /* Z */ || uri.path.charCodeAt(1) >= 97 /* a */ && uri.path.charCodeAt(1) <= 122 /* z */)
            && uri.path.charCodeAt(2) === 58 /* Colon */) {
            // windows drive letter: file:///c:/far/boo
            value = uri.path[1].toLowerCase() + uri.path.substr(2);
        }
        else {
            // other path
            value = uri.path;
        }
        if (isWindows) {
            value = value.replace(/\//g, '\\');
        }
        return value;
    }
    /**
     * Create the external version of a uri
     */
    function _asFormatted(uri, skipEncoding) {
        var encoder = !skipEncoding
            ? encodeURIComponentFast
            : encodeURIComponentMinimal;
        var res = '';
        var scheme = uri.scheme, authority = uri.authority, path = uri.path, query = uri.query, fragment = uri.fragment;
        if (scheme) {
            res += scheme;
            res += ':';
        }
        if (authority || scheme === 'file') {
            res += _slash;
            res += _slash;
        }
        if (authority) {
            var idx = authority.indexOf('@');
            if (idx !== -1) {
                // <user>@<auth>
                var userinfo = authority.substr(0, idx);
                authority = authority.substr(idx + 1);
                idx = userinfo.indexOf(':');
                if (idx === -1) {
                    res += encoder(userinfo, false);
                }
                else {
                    // <user>:<pass>@<auth>
                    res += encoder(userinfo.substr(0, idx), false);
                    res += ':';
                    res += encoder(userinfo.substr(idx + 1), false);
                }
                res += '@';
            }
            authority = authority.toLowerCase();
            idx = authority.indexOf(':');
            if (idx === -1) {
                res += encoder(authority, false);
            }
            else {
                // <auth>:<port>
                res += encoder(authority.substr(0, idx), false);
                res += authority.substr(idx);
            }
        }
        if (path) {
            // lower-case windows drive letters in /C:/fff or C:/fff
            if (path.length >= 3 && path.charCodeAt(0) === 47 /* Slash */ && path.charCodeAt(2) === 58 /* Colon */) {
                var code = path.charCodeAt(1);
                if (code >= 65 /* A */ && code <= 90 /* Z */) {
                    path = "/" + String.fromCharCode(code + 32) + ":" + path.substr(3); // "/c:".length === 3
                }
            }
            else if (path.length >= 2 && path.charCodeAt(1) === 58 /* Colon */) {
                var code = path.charCodeAt(0);
                if (code >= 65 /* A */ && code <= 90 /* Z */) {
                    path = String.fromCharCode(code + 32) + ":" + path.substr(2); // "/c:".length === 3
                }
            }
            // encode the rest of the path
            res += encoder(path, true);
        }
        if (query) {
            res += '?';
            res += encoder(query, false);
        }
        if (fragment) {
            res += '#';
            res += !skipEncoding ? encodeURIComponentFast(fragment, false) : fragment;
        }
        return res;
    }
    var _a;
});

define('vscode-uri', ['vscode-uri/index'], function (main) { return main; });

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
    }
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
(function (factory) {
    if (typeof module === "object" && typeof module.exports === "object") {
        var v = factory(require, exports);
        if (v !== undefined) module.exports = v;
    }
    else if (typeof define === "function" && define.amd) {
        define('vscode-json-languageservice/parser/jsonParser',["require", "exports", "jsonc-parser", "../utils/objects", "../jsonLanguageTypes", "vscode-nls", "vscode-uri", "vscode-languageserver-types"], factory);
    }
})(function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    var Json = require("jsonc-parser");
    var objects_1 = require("../utils/objects");
    var jsonLanguageTypes_1 = require("../jsonLanguageTypes");
    var nls = require("vscode-nls");
    var vscode_uri_1 = require("vscode-uri");
    var vscode_languageserver_types_1 = require("vscode-languageserver-types");
    var localize = nls.loadMessageBundle();
    var colorHexPattern = /^#([0-9A-Fa-f]{3,4}|([0-9A-Fa-f]{2}){3,4})$/;
    var emailPattern = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    var ASTNodeImpl = /** @class */ (function () {
        function ASTNodeImpl(parent, offset, length) {
            this.offset = offset;
            this.length = length;
            this.parent = parent;
        }
        Object.defineProperty(ASTNodeImpl.prototype, "children", {
            get: function () {
                return [];
            },
            enumerable: true,
            configurable: true
        });
        ASTNodeImpl.prototype.toString = function () {
            return 'type: ' + this.type + ' (' + this.offset + '/' + this.length + ')' + (this.parent ? ' parent: {' + this.parent.toString() + '}' : '');
        };
        return ASTNodeImpl;
    }());
    exports.ASTNodeImpl = ASTNodeImpl;
    var NullASTNodeImpl = /** @class */ (function (_super) {
        __extends(NullASTNodeImpl, _super);
        function NullASTNodeImpl(parent, offset) {
            var _this = _super.call(this, parent, offset) || this;
            _this.type = 'null';
            _this.value = null;
            return _this;
        }
        return NullASTNodeImpl;
    }(ASTNodeImpl));
    exports.NullASTNodeImpl = NullASTNodeImpl;
    var BooleanASTNodeImpl = /** @class */ (function (_super) {
        __extends(BooleanASTNodeImpl, _super);
        function BooleanASTNodeImpl(parent, boolValue, offset) {
            var _this = _super.call(this, parent, offset) || this;
            _this.type = 'boolean';
            _this.value = boolValue;
            return _this;
        }
        return BooleanASTNodeImpl;
    }(ASTNodeImpl));
    exports.BooleanASTNodeImpl = BooleanASTNodeImpl;
    var ArrayASTNodeImpl = /** @class */ (function (_super) {
        __extends(ArrayASTNodeImpl, _super);
        function ArrayASTNodeImpl(parent, offset) {
            var _this = _super.call(this, parent, offset) || this;
            _this.type = 'array';
            _this.items = [];
            return _this;
        }
        Object.defineProperty(ArrayASTNodeImpl.prototype, "children", {
            get: function () {
                return this.items;
            },
            enumerable: true,
            configurable: true
        });
        return ArrayASTNodeImpl;
    }(ASTNodeImpl));
    exports.ArrayASTNodeImpl = ArrayASTNodeImpl;
    var NumberASTNodeImpl = /** @class */ (function (_super) {
        __extends(NumberASTNodeImpl, _super);
        function NumberASTNodeImpl(parent, offset) {
            var _this = _super.call(this, parent, offset) || this;
            _this.type = 'number';
            _this.isInteger = true;
            _this.value = Number.NaN;
            return _this;
        }
        return NumberASTNodeImpl;
    }(ASTNodeImpl));
    exports.NumberASTNodeImpl = NumberASTNodeImpl;
    var StringASTNodeImpl = /** @class */ (function (_super) {
        __extends(StringASTNodeImpl, _super);
        function StringASTNodeImpl(parent, offset, length) {
            var _this = _super.call(this, parent, offset, length) || this;
            _this.type = 'string';
            _this.value = '';
            return _this;
        }
        return StringASTNodeImpl;
    }(ASTNodeImpl));
    exports.StringASTNodeImpl = StringASTNodeImpl;
    var PropertyASTNodeImpl = /** @class */ (function (_super) {
        __extends(PropertyASTNodeImpl, _super);
        function PropertyASTNodeImpl(parent, offset) {
            var _this = _super.call(this, parent, offset) || this;
            _this.type = 'property';
            _this.colonOffset = -1;
            return _this;
        }
        Object.defineProperty(PropertyASTNodeImpl.prototype, "children", {
            get: function () {
                return this.valueNode ? [this.keyNode, this.valueNode] : [this.keyNode];
            },
            enumerable: true,
            configurable: true
        });
        return PropertyASTNodeImpl;
    }(ASTNodeImpl));
    exports.PropertyASTNodeImpl = PropertyASTNodeImpl;
    var ObjectASTNodeImpl = /** @class */ (function (_super) {
        __extends(ObjectASTNodeImpl, _super);
        function ObjectASTNodeImpl(parent, offset) {
            var _this = _super.call(this, parent, offset) || this;
            _this.type = 'object';
            _this.properties = [];
            return _this;
        }
        Object.defineProperty(ObjectASTNodeImpl.prototype, "children", {
            get: function () {
                return this.properties;
            },
            enumerable: true,
            configurable: true
        });
        return ObjectASTNodeImpl;
    }(ASTNodeImpl));
    exports.ObjectASTNodeImpl = ObjectASTNodeImpl;
    function asSchema(schema) {
        if (objects_1.isBoolean(schema)) {
            return schema ? {} : { "not": {} };
        }
        return schema;
    }
    exports.asSchema = asSchema;
    var EnumMatch;
    (function (EnumMatch) {
        EnumMatch[EnumMatch["Key"] = 0] = "Key";
        EnumMatch[EnumMatch["Enum"] = 1] = "Enum";
    })(EnumMatch = exports.EnumMatch || (exports.EnumMatch = {}));
    var SchemaCollector = /** @class */ (function () {
        function SchemaCollector(focusOffset, exclude) {
            if (focusOffset === void 0) { focusOffset = -1; }
            if (exclude === void 0) { exclude = null; }
            this.focusOffset = focusOffset;
            this.exclude = exclude;
            this.schemas = [];
        }
        SchemaCollector.prototype.add = function (schema) {
            this.schemas.push(schema);
        };
        SchemaCollector.prototype.merge = function (other) {
            var _a;
            (_a = this.schemas).push.apply(_a, other.schemas);
        };
        SchemaCollector.prototype.include = function (node) {
            return (this.focusOffset === -1 || contains(node, this.focusOffset)) && (node !== this.exclude);
        };
        SchemaCollector.prototype.newSub = function () {
            return new SchemaCollector(-1, this.exclude);
        };
        return SchemaCollector;
    }());
    var NoOpSchemaCollector = /** @class */ (function () {
        function NoOpSchemaCollector() {
        }
        Object.defineProperty(NoOpSchemaCollector.prototype, "schemas", {
            get: function () { return []; },
            enumerable: true,
            configurable: true
        });
        NoOpSchemaCollector.prototype.add = function (schema) { };
        NoOpSchemaCollector.prototype.merge = function (other) { };
        NoOpSchemaCollector.prototype.include = function (node) { return true; };
        NoOpSchemaCollector.prototype.newSub = function () { return this; };
        NoOpSchemaCollector.instance = new NoOpSchemaCollector();
        return NoOpSchemaCollector;
    }());
    var ValidationResult = /** @class */ (function () {
        function ValidationResult() {
            this.problems = [];
            this.propertiesMatches = 0;
            this.propertiesValueMatches = 0;
            this.primaryValueMatches = 0;
            this.enumValueMatch = false;
            this.enumValues = null;
        }
        ValidationResult.prototype.hasProblems = function () {
            return !!this.problems.length;
        };
        ValidationResult.prototype.mergeAll = function (validationResults) {
            for (var _i = 0, validationResults_1 = validationResults; _i < validationResults_1.length; _i++) {
                var validationResult = validationResults_1[_i];
                this.merge(validationResult);
            }
        };
        ValidationResult.prototype.merge = function (validationResult) {
            this.problems = this.problems.concat(validationResult.problems);
        };
        ValidationResult.prototype.mergeEnumValues = function (validationResult) {
            if (!this.enumValueMatch && !validationResult.enumValueMatch && this.enumValues && validationResult.enumValues) {
                this.enumValues = this.enumValues.concat(validationResult.enumValues);
                for (var _i = 0, _a = this.problems; _i < _a.length; _i++) {
                    var error = _a[_i];
                    if (error.code === jsonLanguageTypes_1.ErrorCode.EnumValueMismatch) {
                        error.message = localize('enumWarning', 'Value is not accepted. Valid values: {0}.', this.enumValues.map(function (v) { return JSON.stringify(v); }).join(', '));
                    }
                }
            }
        };
        ValidationResult.prototype.mergePropertyMatch = function (propertyValidationResult) {
            this.merge(propertyValidationResult);
            this.propertiesMatches++;
            if (propertyValidationResult.enumValueMatch || !propertyValidationResult.hasProblems() && propertyValidationResult.propertiesMatches) {
                this.propertiesValueMatches++;
            }
            if (propertyValidationResult.enumValueMatch && propertyValidationResult.enumValues && propertyValidationResult.enumValues.length === 1) {
                this.primaryValueMatches++;
            }
        };
        ValidationResult.prototype.compare = function (other) {
            var hasProblems = this.hasProblems();
            if (hasProblems !== other.hasProblems()) {
                return hasProblems ? -1 : 1;
            }
            if (this.enumValueMatch !== other.enumValueMatch) {
                return other.enumValueMatch ? -1 : 1;
            }
            if (this.primaryValueMatches !== other.primaryValueMatches) {
                return this.primaryValueMatches - other.primaryValueMatches;
            }
            if (this.propertiesValueMatches !== other.propertiesValueMatches) {
                return this.propertiesValueMatches - other.propertiesValueMatches;
            }
            return this.propertiesMatches - other.propertiesMatches;
        };
        return ValidationResult;
    }());
    exports.ValidationResult = ValidationResult;
    function newJSONDocument(root, diagnostics) {
        if (diagnostics === void 0) { diagnostics = []; }
        return new JSONDocument(root, diagnostics, []);
    }
    exports.newJSONDocument = newJSONDocument;
    function getNodeValue(node) {
        return Json.getNodeValue(node);
    }
    exports.getNodeValue = getNodeValue;
    function getNodePath(node) {
        return Json.getNodePath(node);
    }
    exports.getNodePath = getNodePath;
    function contains(node, offset, includeRightBound) {
        if (includeRightBound === void 0) { includeRightBound = false; }
        return offset >= node.offset && offset < (node.offset + node.length) || includeRightBound && offset === (node.offset + node.length);
    }
    exports.contains = contains;
    var JSONDocument = /** @class */ (function () {
        function JSONDocument(root, syntaxErrors, comments) {
            if (syntaxErrors === void 0) { syntaxErrors = []; }
            if (comments === void 0) { comments = []; }
            this.root = root;
            this.syntaxErrors = syntaxErrors;
            this.comments = comments;
        }
        JSONDocument.prototype.getNodeFromOffset = function (offset, includeRightBound) {
            if (includeRightBound === void 0) { includeRightBound = false; }
            if (this.root) {
                return Json.findNodeAtOffset(this.root, offset, includeRightBound);
            }
            return void 0;
        };
        JSONDocument.prototype.visit = function (visitor) {
            if (this.root) {
                var doVisit_1 = function (node) {
                    var ctn = visitor(node);
                    var children = node.children;
                    if (Array.isArray(children)) {
                        for (var i = 0; i < children.length && ctn; i++) {
                            ctn = doVisit_1(children[i]);
                        }
                    }
                    return ctn;
                };
                doVisit_1(this.root);
            }
        };
        JSONDocument.prototype.validate = function (textDocument, schema) {
            if (this.root && schema) {
                var validationResult = new ValidationResult();
                validate(this.root, schema, validationResult, NoOpSchemaCollector.instance);
                return validationResult.problems.map(function (p) {
                    var range = vscode_languageserver_types_1.Range.create(textDocument.positionAt(p.location.offset), textDocument.positionAt(p.location.offset + p.location.length));
                    return vscode_languageserver_types_1.Diagnostic.create(range, p.message, p.severity, p.code);
                });
            }
            return null;
        };
        JSONDocument.prototype.getMatchingSchemas = function (schema, focusOffset, exclude) {
            if (focusOffset === void 0) { focusOffset = -1; }
            if (exclude === void 0) { exclude = null; }
            var matchingSchemas = new SchemaCollector(focusOffset, exclude);
            if (this.root && schema) {
                validate(this.root, schema, new ValidationResult(), matchingSchemas);
            }
            return matchingSchemas.schemas;
        };
        return JSONDocument;
    }());
    exports.JSONDocument = JSONDocument;
    function validate(node, schema, validationResult, matchingSchemas) {
        if (!node || !matchingSchemas.include(node)) {
            return;
        }
        switch (node.type) {
            case 'object':
                _validateObjectNode(node, schema, validationResult, matchingSchemas);
                break;
            case 'array':
                _validateArrayNode(node, schema, validationResult, matchingSchemas);
                break;
            case 'string':
                _validateStringNode(node, schema, validationResult, matchingSchemas);
                break;
            case 'number':
                _validateNumberNode(node, schema, validationResult, matchingSchemas);
                break;
            case 'property':
                return validate(node.valueNode, schema, validationResult, matchingSchemas);
        }
        _validateNode();
        matchingSchemas.add({ node: node, schema: schema });
        function _validateNode() {
            function matchesType(type) {
                return node.type === type || (type === 'integer' && node.type === 'number' && node.isInteger);
            }
            if (Array.isArray(schema.type)) {
                if (!schema.type.some(matchesType)) {
                    validationResult.problems.push({
                        location: { offset: node.offset, length: node.length },
                        severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                        message: schema.errorMessage || localize('typeArrayMismatchWarning', 'Incorrect type. Expected one of {0}.', schema.type.join(', '))
                    });
                }
            }
            else if (schema.type) {
                if (!matchesType(schema.type)) {
                    validationResult.problems.push({
                        location: { offset: node.offset, length: node.length },
                        severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                        message: schema.errorMessage || localize('typeMismatchWarning', 'Incorrect type. Expected "{0}".', schema.type)
                    });
                }
            }
            if (Array.isArray(schema.allOf)) {
                for (var _i = 0, _a = schema.allOf; _i < _a.length; _i++) {
                    var subSchemaRef = _a[_i];
                    validate(node, asSchema(subSchemaRef), validationResult, matchingSchemas);
                }
            }
            var notSchema = asSchema(schema.not);
            if (notSchema) {
                var subValidationResult = new ValidationResult();
                var subMatchingSchemas = matchingSchemas.newSub();
                validate(node, notSchema, subValidationResult, subMatchingSchemas);
                if (!subValidationResult.hasProblems()) {
                    validationResult.problems.push({
                        location: { offset: node.offset, length: node.length },
                        severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                        message: localize('notSchemaWarning', "Matches a schema that is not allowed.")
                    });
                }
                for (var _b = 0, _c = subMatchingSchemas.schemas; _b < _c.length; _b++) {
                    var ms = _c[_b];
                    ms.inverted = !ms.inverted;
                    matchingSchemas.add(ms);
                }
            }
            var testAlternatives = function (alternatives, maxOneMatch) {
                var matches = [];
                // remember the best match that is used for error messages
                var bestMatch = null;
                for (var _i = 0, alternatives_1 = alternatives; _i < alternatives_1.length; _i++) {
                    var subSchemaRef = alternatives_1[_i];
                    var subSchema = asSchema(subSchemaRef);
                    var subValidationResult = new ValidationResult();
                    var subMatchingSchemas = matchingSchemas.newSub();
                    validate(node, subSchema, subValidationResult, subMatchingSchemas);
                    if (!subValidationResult.hasProblems()) {
                        matches.push(subSchema);
                    }
                    if (!bestMatch) {
                        bestMatch = { schema: subSchema, validationResult: subValidationResult, matchingSchemas: subMatchingSchemas };
                    }
                    else {
                        if (!maxOneMatch && !subValidationResult.hasProblems() && !bestMatch.validationResult.hasProblems()) {
                            // no errors, both are equally good matches
                            bestMatch.matchingSchemas.merge(subMatchingSchemas);
                            bestMatch.validationResult.propertiesMatches += subValidationResult.propertiesMatches;
                            bestMatch.validationResult.propertiesValueMatches += subValidationResult.propertiesValueMatches;
                        }
                        else {
                            var compareResult = subValidationResult.compare(bestMatch.validationResult);
                            if (compareResult > 0) {
                                // our node is the best matching so far
                                bestMatch = { schema: subSchema, validationResult: subValidationResult, matchingSchemas: subMatchingSchemas };
                            }
                            else if (compareResult === 0) {
                                // there's already a best matching but we are as good
                                bestMatch.matchingSchemas.merge(subMatchingSchemas);
                                bestMatch.validationResult.mergeEnumValues(subValidationResult);
                            }
                        }
                    }
                }
                if (matches.length > 1 && maxOneMatch) {
                    validationResult.problems.push({
                        location: { offset: node.offset, length: 1 },
                        severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                        message: localize('oneOfWarning', "Matches multiple schemas when only one must validate.")
                    });
                }
                if (bestMatch !== null) {
                    validationResult.merge(bestMatch.validationResult);
                    validationResult.propertiesMatches += bestMatch.validationResult.propertiesMatches;
                    validationResult.propertiesValueMatches += bestMatch.validationResult.propertiesValueMatches;
                    matchingSchemas.merge(bestMatch.matchingSchemas);
                }
                return matches.length;
            };
            if (Array.isArray(schema.anyOf)) {
                testAlternatives(schema.anyOf, false);
            }
            if (Array.isArray(schema.oneOf)) {
                testAlternatives(schema.oneOf, true);
            }
            var testBranch = function (schema) {
                var subValidationResult = new ValidationResult();
                var subMatchingSchemas = matchingSchemas.newSub();
                validate(node, asSchema(schema), subValidationResult, subMatchingSchemas);
                validationResult.merge(subValidationResult);
                validationResult.propertiesMatches += subValidationResult.propertiesMatches;
                validationResult.propertiesValueMatches += subValidationResult.propertiesValueMatches;
                matchingSchemas.merge(subMatchingSchemas);
            };
            var testCondition = function (ifSchema, thenSchema, elseSchema) {
                var subSchema = asSchema(ifSchema);
                var subValidationResult = new ValidationResult();
                var subMatchingSchemas = matchingSchemas.newSub();
                validate(node, subSchema, subValidationResult, subMatchingSchemas);
                matchingSchemas.merge(subMatchingSchemas);
                if (!subValidationResult.hasProblems()) {
                    if (thenSchema) {
                        testBranch(thenSchema);
                    }
                }
                else if (elseSchema) {
                    testBranch(elseSchema);
                }
            };
            var ifSchema = asSchema(schema.if);
            if (ifSchema) {
                testCondition(ifSchema, asSchema(schema.then), asSchema(schema.else));
            }
            if (Array.isArray(schema.enum)) {
                var val = getNodeValue(node);
                var enumValueMatch = false;
                for (var _d = 0, _e = schema.enum; _d < _e.length; _d++) {
                    var e = _e[_d];
                    if (objects_1.equals(val, e)) {
                        enumValueMatch = true;
                        break;
                    }
                }
                validationResult.enumValues = schema.enum;
                validationResult.enumValueMatch = enumValueMatch;
                if (!enumValueMatch) {
                    validationResult.problems.push({
                        location: { offset: node.offset, length: node.length },
                        severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                        code: jsonLanguageTypes_1.ErrorCode.EnumValueMismatch,
                        message: schema.errorMessage || localize('enumWarning', 'Value is not accepted. Valid values: {0}.', schema.enum.map(function (v) { return JSON.stringify(v); }).join(', '))
                    });
                }
            }
            if (objects_1.isDefined(schema.const)) {
                var val = getNodeValue(node);
                if (!objects_1.equals(val, schema.const)) {
                    validationResult.problems.push({
                        location: { offset: node.offset, length: node.length },
                        severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                        code: jsonLanguageTypes_1.ErrorCode.EnumValueMismatch,
                        message: schema.errorMessage || localize('constWarning', 'Value must be {0}.', JSON.stringify(schema.const))
                    });
                    validationResult.enumValueMatch = false;
                }
                else {
                    validationResult.enumValueMatch = true;
                }
                validationResult.enumValues = [schema.const];
            }
            if (schema.deprecationMessage && node.parent) {
                validationResult.problems.push({
                    location: { offset: node.parent.offset, length: node.parent.length },
                    severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                    message: schema.deprecationMessage
                });
            }
        }
        function _validateNumberNode(node, schema, validationResult, matchingSchemas) {
            var val = node.value;
            if (objects_1.isNumber(schema.multipleOf)) {
                if (val % schema.multipleOf !== 0) {
                    validationResult.problems.push({
                        location: { offset: node.offset, length: node.length },
                        severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                        message: localize('multipleOfWarning', 'Value is not divisible by {0}.', schema.multipleOf)
                    });
                }
            }
            function getExclusiveLimit(limit, exclusive) {
                if (objects_1.isNumber(exclusive)) {
                    return exclusive;
                }
                if (objects_1.isBoolean(exclusive) && exclusive) {
                    return limit;
                }
                return void 0;
            }
            function getLimit(limit, exclusive) {
                if (!objects_1.isBoolean(exclusive) || !exclusive) {
                    return limit;
                }
                return void 0;
            }
            var exclusiveMinimum = getExclusiveLimit(schema.minimum, schema.exclusiveMinimum);
            if (objects_1.isNumber(exclusiveMinimum) && val <= exclusiveMinimum) {
                validationResult.problems.push({
                    location: { offset: node.offset, length: node.length },
                    severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                    message: localize('exclusiveMinimumWarning', 'Value is below the exclusive minimum of {0}.', exclusiveMinimum)
                });
            }
            var exclusiveMaximum = getExclusiveLimit(schema.maximum, schema.exclusiveMaximum);
            if (objects_1.isNumber(exclusiveMaximum) && val >= exclusiveMaximum) {
                validationResult.problems.push({
                    location: { offset: node.offset, length: node.length },
                    severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                    message: localize('exclusiveMaximumWarning', 'Value is above the exclusive maximum of {0}.', exclusiveMaximum)
                });
            }
            var minimum = getLimit(schema.minimum, schema.exclusiveMinimum);
            if (objects_1.isNumber(minimum) && val < minimum) {
                validationResult.problems.push({
                    location: { offset: node.offset, length: node.length },
                    severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                    message: localize('minimumWarning', 'Value is below the minimum of {0}.', minimum)
                });
            }
            var maximum = getLimit(schema.maximum, schema.exclusiveMaximum);
            if (objects_1.isNumber(maximum) && val > maximum) {
                validationResult.problems.push({
                    location: { offset: node.offset, length: node.length },
                    severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                    message: localize('maximumWarning', 'Value is above the maximum of {0}.', maximum)
                });
            }
        }
        function _validateStringNode(node, schema, validationResult, matchingSchemas) {
            if (objects_1.isNumber(schema.minLength) && node.value.length < schema.minLength) {
                validationResult.problems.push({
                    location: { offset: node.offset, length: node.length },
                    severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                    message: localize('minLengthWarning', 'String is shorter than the minimum length of {0}.', schema.minLength)
                });
            }
            if (objects_1.isNumber(schema.maxLength) && node.value.length > schema.maxLength) {
                validationResult.problems.push({
                    location: { offset: node.offset, length: node.length },
                    severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                    message: localize('maxLengthWarning', 'String is longer than the maximum length of {0}.', schema.maxLength)
                });
            }
            if (objects_1.isString(schema.pattern)) {
                var regex = new RegExp(schema.pattern);
                if (!regex.test(node.value)) {
                    validationResult.problems.push({
                        location: { offset: node.offset, length: node.length },
                        severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                        message: schema.patternErrorMessage || schema.errorMessage || localize('patternWarning', 'String does not match the pattern of "{0}".', schema.pattern)
                    });
                }
            }
            if (schema.format) {
                switch (schema.format) {
                    case 'uri':
                    case 'uri-reference':
                        {
                            var errorMessage = void 0;
                            if (!node.value) {
                                errorMessage = localize('uriEmpty', 'URI expected.');
                            }
                            else {
                                try {
                                    var uri = vscode_uri_1.default.parse(node.value);
                                    if (!uri.scheme && schema.format === 'uri') {
                                        errorMessage = localize('uriSchemeMissing', 'URI with a scheme is expected.');
                                    }
                                }
                                catch (e) {
                                    errorMessage = e.message;
                                }
                            }
                            if (errorMessage) {
                                validationResult.problems.push({
                                    location: { offset: node.offset, length: node.length },
                                    severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                                    message: schema.patternErrorMessage || schema.errorMessage || localize('uriFormatWarning', 'String is not a URI: {0}', errorMessage)
                                });
                            }
                        }
                        break;
                    case 'email':
                        {
                            if (!node.value.match(emailPattern)) {
                                validationResult.problems.push({
                                    location: { offset: node.offset, length: node.length },
                                    severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                                    message: schema.patternErrorMessage || schema.errorMessage || localize('emailFormatWarning', 'String is not an e-mail address.')
                                });
                            }
                        }
                        break;
                    case 'color-hex':
                        {
                            if (!node.value.match(colorHexPattern)) {
                                validationResult.problems.push({
                                    location: { offset: node.offset, length: node.length },
                                    severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                                    message: schema.patternErrorMessage || schema.errorMessage || localize('colorHexFormatWarning', 'Invalid color format. Use #RGB, #RGBA, #RRGGBB or #RRGGBBAA.')
                                });
                            }
                        }
                        break;
                    default:
                }
            }
        }
        function _validateArrayNode(node, schema, validationResult, matchingSchemas) {
            if (Array.isArray(schema.items)) {
                var subSchemas = schema.items;
                for (var index = 0; index < subSchemas.length; index++) {
                    var subSchemaRef = subSchemas[index];
                    var subSchema = asSchema(subSchemaRef);
                    var itemValidationResult = new ValidationResult();
                    var item = node.items[index];
                    if (item) {
                        validate(item, subSchema, itemValidationResult, matchingSchemas);
                        validationResult.mergePropertyMatch(itemValidationResult);
                    }
                    else if (node.items.length >= subSchemas.length) {
                        validationResult.propertiesValueMatches++;
                    }
                }
                if (node.items.length > subSchemas.length) {
                    if (typeof schema.additionalItems === 'object') {
                        for (var i = subSchemas.length; i < node.items.length; i++) {
                            var itemValidationResult = new ValidationResult();
                            validate(node.items[i], schema.additionalItems, itemValidationResult, matchingSchemas);
                            validationResult.mergePropertyMatch(itemValidationResult);
                        }
                    }
                    else if (schema.additionalItems === false) {
                        validationResult.problems.push({
                            location: { offset: node.offset, length: node.length },
                            severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                            message: localize('additionalItemsWarning', 'Array has too many items according to schema. Expected {0} or fewer.', subSchemas.length)
                        });
                    }
                }
            }
            else {
                var itemSchema = asSchema(schema.items);
                if (itemSchema) {
                    for (var _i = 0, _a = node.items; _i < _a.length; _i++) {
                        var item = _a[_i];
                        var itemValidationResult = new ValidationResult();
                        validate(item, itemSchema, itemValidationResult, matchingSchemas);
                        validationResult.mergePropertyMatch(itemValidationResult);
                    }
                }
            }
            var containsSchema = asSchema(schema.contains);
            if (containsSchema) {
                var doesContain = node.items.some(function (item) {
                    var itemValidationResult = new ValidationResult();
                    validate(item, containsSchema, itemValidationResult, NoOpSchemaCollector.instance);
                    return !itemValidationResult.hasProblems();
                });
                if (!doesContain) {
                    validationResult.problems.push({
                        location: { offset: node.offset, length: node.length },
                        severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                        message: schema.errorMessage || localize('requiredItemMissingWarning', 'Array does not contain required item.')
                    });
                }
            }
            if (objects_1.isNumber(schema.minItems) && node.items.length < schema.minItems) {
                validationResult.problems.push({
                    location: { offset: node.offset, length: node.length },
                    severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                    message: localize('minItemsWarning', 'Array has too few items. Expected {0} or more.', schema.minItems)
                });
            }
            if (objects_1.isNumber(schema.maxItems) && node.items.length > schema.maxItems) {
                validationResult.problems.push({
                    location: { offset: node.offset, length: node.length },
                    severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                    message: localize('maxItemsWarning', 'Array has too many items. Expected {0} or fewer.', schema.maxItems)
                });
            }
            if (schema.uniqueItems === true) {
                var values_1 = getNodeValue(node);
                var duplicates = values_1.some(function (value, index) {
                    return index !== values_1.lastIndexOf(value);
                });
                if (duplicates) {
                    validationResult.problems.push({
                        location: { offset: node.offset, length: node.length },
                        severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                        message: localize('uniqueItemsWarning', 'Array has duplicate items.')
                    });
                }
            }
        }
        function _validateObjectNode(node, schema, validationResult, matchingSchemas) {
            var seenKeys = Object.create(null);
            var unprocessedProperties = [];
            for (var _i = 0, _a = node.properties; _i < _a.length; _i++) {
                var propertyNode = _a[_i];
                var key = propertyNode.keyNode.value;
                seenKeys[key] = propertyNode.valueNode;
                unprocessedProperties.push(key);
            }
            if (Array.isArray(schema.required)) {
                for (var _b = 0, _c = schema.required; _b < _c.length; _b++) {
                    var propertyName = _c[_b];
                    if (!seenKeys[propertyName]) {
                        var keyNode = node.parent && node.parent.type === 'property' && node.parent.keyNode;
                        var location = keyNode ? { offset: keyNode.offset, length: keyNode.length } : { offset: node.offset, length: 1 };
                        validationResult.problems.push({
                            location: location,
                            severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                            message: localize('MissingRequiredPropWarning', 'Missing property "{0}".', propertyName)
                        });
                    }
                }
            }
            var propertyProcessed = function (prop) {
                var index = unprocessedProperties.indexOf(prop);
                while (index >= 0) {
                    unprocessedProperties.splice(index, 1);
                    index = unprocessedProperties.indexOf(prop);
                }
            };
            if (schema.properties) {
                for (var _d = 0, _e = Object.keys(schema.properties); _d < _e.length; _d++) {
                    var propertyName = _e[_d];
                    propertyProcessed(propertyName);
                    var propertySchema = schema.properties[propertyName];
                    var child = seenKeys[propertyName];
                    if (child) {
                        if (objects_1.isBoolean(propertySchema)) {
                            if (!propertySchema) {
                                var propertyNode = child.parent;
                                validationResult.problems.push({
                                    location: { offset: propertyNode.keyNode.offset, length: propertyNode.keyNode.length },
                                    severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                                    message: schema.errorMessage || localize('DisallowedExtraPropWarning', 'Property {0} is not allowed.', propertyName)
                                });
                            }
                            else {
                                validationResult.propertiesMatches++;
                                validationResult.propertiesValueMatches++;
                            }
                        }
                        else {
                            var propertyValidationResult = new ValidationResult();
                            validate(child, propertySchema, propertyValidationResult, matchingSchemas);
                            validationResult.mergePropertyMatch(propertyValidationResult);
                        }
                    }
                }
            }
            if (schema.patternProperties) {
                for (var _f = 0, _g = Object.keys(schema.patternProperties); _f < _g.length; _f++) {
                    var propertyPattern = _g[_f];
                    var regex = new RegExp(propertyPattern);
                    for (var _h = 0, _j = unprocessedProperties.slice(0); _h < _j.length; _h++) {
                        var propertyName = _j[_h];
                        if (regex.test(propertyName)) {
                            propertyProcessed(propertyName);
                            var child = seenKeys[propertyName];
                            if (child) {
                                var propertySchema = schema.patternProperties[propertyPattern];
                                if (objects_1.isBoolean(propertySchema)) {
                                    if (!propertySchema) {
                                        var propertyNode = child.parent;
                                        validationResult.problems.push({
                                            location: { offset: propertyNode.keyNode.offset, length: propertyNode.keyNode.length },
                                            severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                                            message: schema.errorMessage || localize('DisallowedExtraPropWarning', 'Property {0} is not allowed.', propertyName)
                                        });
                                    }
                                    else {
                                        validationResult.propertiesMatches++;
                                        validationResult.propertiesValueMatches++;
                                    }
                                }
                                else {
                                    var propertyValidationResult = new ValidationResult();
                                    validate(child, propertySchema, propertyValidationResult, matchingSchemas);
                                    validationResult.mergePropertyMatch(propertyValidationResult);
                                }
                            }
                        }
                    }
                }
            }
            if (typeof schema.additionalProperties === 'object') {
                for (var _k = 0, unprocessedProperties_1 = unprocessedProperties; _k < unprocessedProperties_1.length; _k++) {
                    var propertyName = unprocessedProperties_1[_k];
                    var child = seenKeys[propertyName];
                    if (child) {
                        var propertyValidationResult = new ValidationResult();
                        validate(child, schema.additionalProperties, propertyValidationResult, matchingSchemas);
                        validationResult.mergePropertyMatch(propertyValidationResult);
                    }
                }
            }
            else if (schema.additionalProperties === false) {
                if (unprocessedProperties.length > 0) {
                    for (var _l = 0, unprocessedProperties_2 = unprocessedProperties; _l < unprocessedProperties_2.length; _l++) {
                        var propertyName = unprocessedProperties_2[_l];
                        var child = seenKeys[propertyName];
                        if (child) {
                            var propertyNode = child.parent;
                            validationResult.problems.push({
                                location: { offset: propertyNode.keyNode.offset, length: propertyNode.keyNode.length },
                                severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                                message: schema.errorMessage || localize('DisallowedExtraPropWarning', 'Property {0} is not allowed.', propertyName)
                            });
                        }
                    }
                }
            }
            if (objects_1.isNumber(schema.maxProperties)) {
                if (node.properties.length > schema.maxProperties) {
                    validationResult.problems.push({
                        location: { offset: node.offset, length: node.length },
                        severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                        message: localize('MaxPropWarning', 'Object has more properties than limit of {0}.', schema.maxProperties)
                    });
                }
            }
            if (objects_1.isNumber(schema.minProperties)) {
                if (node.properties.length < schema.minProperties) {
                    validationResult.problems.push({
                        location: { offset: node.offset, length: node.length },
                        severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                        message: localize('MinPropWarning', 'Object has fewer properties than the required number of {0}', schema.minProperties)
                    });
                }
            }
            if (schema.dependencies) {
                for (var _m = 0, _o = Object.keys(schema.dependencies); _m < _o.length; _m++) {
                    var key = _o[_m];
                    var prop = seenKeys[key];
                    if (prop) {
                        var propertyDep = schema.dependencies[key];
                        if (Array.isArray(propertyDep)) {
                            for (var _p = 0, propertyDep_1 = propertyDep; _p < propertyDep_1.length; _p++) {
                                var requiredProp = propertyDep_1[_p];
                                if (!seenKeys[requiredProp]) {
                                    validationResult.problems.push({
                                        location: { offset: node.offset, length: node.length },
                                        severity: vscode_languageserver_types_1.DiagnosticSeverity.Warning,
                                        message: localize('RequiredDependentPropWarning', 'Object is missing property {0} required by property {1}.', requiredProp, key)
                                    });
                                }
                                else {
                                    validationResult.propertiesValueMatches++;
                                }
                            }
                        }
                        else {
                            var propertySchema = asSchema(propertyDep);
                            if (propertySchema) {
                                var propertyValidationResult = new ValidationResult();
                                validate(node, propertySchema, propertyValidationResult, matchingSchemas);
                                validationResult.mergePropertyMatch(propertyValidationResult);
                            }
                        }
                    }
                }
            }
            var propertyNames = asSchema(schema.propertyNames);
            if (propertyNames) {
                for (var _q = 0, _r = node.properties; _q < _r.length; _q++) {
                    var f = _r[_q];
                    var key = f.keyNode;
                    if (key) {
                        validate(key, propertyNames, validationResult, NoOpSchemaCollector.instance);
                    }
                }
            }
        }
    }
    function parse(textDocument, config) {
        var problems = [];
        var lastProblemOffset = -1;
        var text = textDocument.getText();
        var scanner = Json.createScanner(text, false);
        var commentRanges = config && config.collectComments ? [] : void 0;
        function _scanNext() {
            while (true) {
                var token_1 = scanner.scan();
                _checkScanError();
                switch (token_1) {
                    case 12 /* LineCommentTrivia */:
                    case 13 /* BlockCommentTrivia */:
                        if (Array.isArray(commentRanges)) {
                            commentRanges.push(vscode_languageserver_types_1.Range.create(textDocument.positionAt(scanner.getTokenOffset()), textDocument.positionAt(scanner.getTokenOffset() + scanner.getTokenLength())));
                        }
                        break;
                    case 15 /* Trivia */:
                    case 14 /* LineBreakTrivia */:
                        break;
                    default:
                        return token_1;
                }
            }
        }
        function _accept(token) {
            if (scanner.getToken() === token) {
                _scanNext();
                return true;
            }
            return false;
        }
        function _errorAtRange(message, code, startOffset, endOffset, severity) {
            if (severity === void 0) { severity = vscode_languageserver_types_1.DiagnosticSeverity.Error; }
            if (problems.length === 0 || startOffset !== lastProblemOffset) {
                var range = vscode_languageserver_types_1.Range.create(textDocument.positionAt(startOffset), textDocument.positionAt(endOffset));
                problems.push(vscode_languageserver_types_1.Diagnostic.create(range, message, severity, code, textDocument.languageId));
                lastProblemOffset = startOffset;
            }
        }
        function _error(message, code, node, skipUntilAfter, skipUntil) {
            if (node === void 0) { node = null; }
            if (skipUntilAfter === void 0) { skipUntilAfter = []; }
            if (skipUntil === void 0) { skipUntil = []; }
            var start = scanner.getTokenOffset();
            var end = scanner.getTokenOffset() + scanner.getTokenLength();
            if (start === end && start > 0) {
                start--;
                while (start > 0 && /\s/.test(text.charAt(start))) {
                    start--;
                }
                end = start + 1;
            }
            _errorAtRange(message, code, start, end);
            if (node) {
                _finalize(node, false);
            }
            if (skipUntilAfter.length + skipUntil.length > 0) {
                var token_2 = scanner.getToken();
                while (token_2 !== 17 /* EOF */) {
                    if (skipUntilAfter.indexOf(token_2) !== -1) {
                        _scanNext();
                        break;
                    }
                    else if (skipUntil.indexOf(token_2) !== -1) {
                        break;
                    }
                    token_2 = _scanNext();
                }
            }
            return node;
        }
        function _checkScanError() {
            switch (scanner.getTokenError()) {
                case 4 /* InvalidUnicode */:
                    _error(localize('InvalidUnicode', 'Invalid unicode sequence in string.'), jsonLanguageTypes_1.ErrorCode.InvalidUnicode);
                    return true;
                case 5 /* InvalidEscapeCharacter */:
                    _error(localize('InvalidEscapeCharacter', 'Invalid escape character in string.'), jsonLanguageTypes_1.ErrorCode.InvalidEscapeCharacter);
                    return true;
                case 3 /* UnexpectedEndOfNumber */:
                    _error(localize('UnexpectedEndOfNumber', 'Unexpected end of number.'), jsonLanguageTypes_1.ErrorCode.UnexpectedEndOfNumber);
                    return true;
                case 1 /* UnexpectedEndOfComment */:
                    _error(localize('UnexpectedEndOfComment', 'Unexpected end of comment.'), jsonLanguageTypes_1.ErrorCode.UnexpectedEndOfComment);
                    return true;
                case 2 /* UnexpectedEndOfString */:
                    _error(localize('UnexpectedEndOfString', 'Unexpected end of string.'), jsonLanguageTypes_1.ErrorCode.UnexpectedEndOfString);
                    return true;
                case 6 /* InvalidCharacter */:
                    _error(localize('InvalidCharacter', 'Invalid characters in string. Control characters must be escaped.'), jsonLanguageTypes_1.ErrorCode.InvalidCharacter);
                    return true;
            }
            return false;
        }
        function _finalize(node, scanNext) {
            node.length = scanner.getTokenOffset() + scanner.getTokenLength() - node.offset;
            if (scanNext) {
                _scanNext();
            }
            return node;
        }
        function _parseArray(parent) {
            if (scanner.getToken() !== 3 /* OpenBracketToken */) {
                return null;
            }
            var node = new ArrayASTNodeImpl(parent, scanner.getTokenOffset());
            _scanNext(); // consume OpenBracketToken
            var count = 0;
            var needsComma = false;
            while (scanner.getToken() !== 4 /* CloseBracketToken */ && scanner.getToken() !== 17 /* EOF */) {
                if (scanner.getToken() === 5 /* CommaToken */) {
                    if (!needsComma) {
                        _error(localize('ValueExpected', 'Value expected'), jsonLanguageTypes_1.ErrorCode.ValueExpected);
                    }
                    var commaOffset = scanner.getTokenOffset();
                    _scanNext(); // consume comma
                    if (scanner.getToken() === 4 /* CloseBracketToken */) {
                        if (needsComma) {
                            _errorAtRange(localize('TrailingComma', 'Trailing comma'), jsonLanguageTypes_1.ErrorCode.TrailingComma, commaOffset, commaOffset + 1);
                        }
                        continue;
                    }
                }
                else if (needsComma) {
                    _error(localize('ExpectedComma', 'Expected comma'), jsonLanguageTypes_1.ErrorCode.CommaExpected);
                }
                var item = _parseValue(node, count++);
                if (!item) {
                    _error(localize('PropertyExpected', 'Value expected'), jsonLanguageTypes_1.ErrorCode.ValueExpected, null, [], [4 /* CloseBracketToken */, 5 /* CommaToken */]);
                }
                else {
                    node.items.push(item);
                }
                needsComma = true;
            }
            if (scanner.getToken() !== 4 /* CloseBracketToken */) {
                return _error(localize('ExpectedCloseBracket', 'Expected comma or closing bracket'), jsonLanguageTypes_1.ErrorCode.CommaOrCloseBacketExpected, node);
            }
            return _finalize(node, true);
        }
        function _parseProperty(parent, keysSeen) {
            var node = new PropertyASTNodeImpl(parent, scanner.getTokenOffset());
            var key = _parseString(node);
            if (!key) {
                if (scanner.getToken() === 16 /* Unknown */) {
                    // give a more helpful error message
                    _error(localize('DoubleQuotesExpected', 'Property keys must be doublequoted'), jsonLanguageTypes_1.ErrorCode.Undefined);
                    var keyNode = new StringASTNodeImpl(node, scanner.getTokenOffset(), scanner.getTokenLength());
                    keyNode.value = scanner.getTokenValue();
                    key = keyNode;
                    _scanNext(); // consume Unknown
                }
                else {
                    return null;
                }
            }
            node.keyNode = key;
            var seen = keysSeen[key.value];
            if (seen) {
                _errorAtRange(localize('DuplicateKeyWarning', "Duplicate object key"), jsonLanguageTypes_1.ErrorCode.DuplicateKey, node.keyNode.offset, node.keyNode.offset + node.keyNode.length, vscode_languageserver_types_1.DiagnosticSeverity.Warning);
                if (typeof seen === 'object') {
                    _errorAtRange(localize('DuplicateKeyWarning', "Duplicate object key"), jsonLanguageTypes_1.ErrorCode.DuplicateKey, seen.keyNode.offset, seen.keyNode.offset + seen.keyNode.length, vscode_languageserver_types_1.DiagnosticSeverity.Warning);
                }
                keysSeen[key.value] = true; // if the same key is duplicate again, avoid duplicate error reporting
            }
            else {
                keysSeen[key.value] = node;
            }
            if (scanner.getToken() === 6 /* ColonToken */) {
                node.colonOffset = scanner.getTokenOffset();
                _scanNext(); // consume ColonToken
            }
            else {
                _error(localize('ColonExpected', 'Colon expected'), jsonLanguageTypes_1.ErrorCode.ColonExpected);
                if (scanner.getToken() === 10 /* StringLiteral */ && textDocument.positionAt(key.offset + key.length).line < textDocument.positionAt(scanner.getTokenOffset()).line) {
                    node.length = key.length;
                    return node;
                }
            }
            var value = _parseValue(node, key.value);
            if (!value) {
                return _error(localize('ValueExpected', 'Value expected'), jsonLanguageTypes_1.ErrorCode.ValueExpected, node, [], [2 /* CloseBraceToken */, 5 /* CommaToken */]);
            }
            node.valueNode = value;
            node.length = value.offset + value.length - node.offset;
            return node;
        }
        function _parseObject(parent) {
            if (scanner.getToken() !== 1 /* OpenBraceToken */) {
                return null;
            }
            var node = new ObjectASTNodeImpl(parent, scanner.getTokenOffset());
            var keysSeen = Object.create(null);
            _scanNext(); // consume OpenBraceToken
            var needsComma = false;
            while (scanner.getToken() !== 2 /* CloseBraceToken */ && scanner.getToken() !== 17 /* EOF */) {
                if (scanner.getToken() === 5 /* CommaToken */) {
                    if (!needsComma) {
                        _error(localize('PropertyExpected', 'Property expected'), jsonLanguageTypes_1.ErrorCode.PropertyExpected);
                    }
                    var commaOffset = scanner.getTokenOffset();
                    _scanNext(); // consume comma
                    if (scanner.getToken() === 2 /* CloseBraceToken */) {
                        if (needsComma) {
                            _errorAtRange(localize('TrailingComma', 'Trailing comma'), jsonLanguageTypes_1.ErrorCode.TrailingComma, commaOffset, commaOffset + 1);
                        }
                        continue;
                    }
                }
                else if (needsComma) {
                    _error(localize('ExpectedComma', 'Expected comma'), jsonLanguageTypes_1.ErrorCode.CommaExpected);
                }
                var property = _parseProperty(node, keysSeen);
                if (!property) {
                    _error(localize('PropertyExpected', 'Property expected'), jsonLanguageTypes_1.ErrorCode.PropertyExpected, null, [], [2 /* CloseBraceToken */, 5 /* CommaToken */]);
                }
                else {
                    node.properties.push(property);
                }
                needsComma = true;
            }
            if (scanner.getToken() !== 2 /* CloseBraceToken */) {
                return _error(localize('ExpectedCloseBrace', 'Expected comma or closing brace'), jsonLanguageTypes_1.ErrorCode.CommaOrCloseBraceExpected, node);
            }
            return _finalize(node, true);
        }
        function _parseString(parent) {
            if (scanner.getToken() !== 10 /* StringLiteral */) {
                return null;
            }
            var node = new StringASTNodeImpl(parent, scanner.getTokenOffset());
            node.value = scanner.getTokenValue();
            return _finalize(node, true);
        }
        function _parseNumber(parent) {
            if (scanner.getToken() !== 11 /* NumericLiteral */) {
                return null;
            }
            var node = new NumberASTNodeImpl(parent, scanner.getTokenOffset());
            if (scanner.getTokenError() === 0 /* None */) {
                var tokenValue = scanner.getTokenValue();
                try {
                    var numberValue = JSON.parse(tokenValue);
                    if (!objects_1.isNumber(numberValue)) {
                        return _error(localize('InvalidNumberFormat', 'Invalid number format.'), jsonLanguageTypes_1.ErrorCode.Undefined, node);
                    }
                    node.value = numberValue;
                }
                catch (e) {
                    return _error(localize('InvalidNumberFormat', 'Invalid number format.'), jsonLanguageTypes_1.ErrorCode.Undefined, node);
                }
                node.isInteger = tokenValue.indexOf('.') === -1;
            }
            return _finalize(node, true);
        }
        function _parseLiteral(parent) {
            var node;
            switch (scanner.getToken()) {
                case 7 /* NullKeyword */:
                    return _finalize(new NullASTNodeImpl(parent, scanner.getTokenOffset()), true);
                case 8 /* TrueKeyword */:
                    return _finalize(new BooleanASTNodeImpl(parent, true, scanner.getTokenOffset()), true);
                case 9 /* FalseKeyword */:
                    return _finalize(new BooleanASTNodeImpl(parent, false, scanner.getTokenOffset()), true);
                default:
                    return null;
            }
        }
        function _parseValue(parent, name) {
            return _parseArray(parent) || _parseObject(parent) || _parseString(parent) || _parseNumber(parent) || _parseLiteral(parent);
        }
        var _root = null;
        var token = _scanNext();
        if (token !== 17 /* EOF */) {
            _root = _parseValue(null, null);
            if (!_root) {
                _error(localize('Invalid symbol', 'Expected a JSON object, array or literal.'), jsonLanguageTypes_1.ErrorCode.Undefined);
            }
            else if (scanner.getToken() !== 17 /* EOF */) {
                _error(localize('End of file expected', 'End of file expected.'), jsonLanguageTypes_1.ErrorCode.Undefined);
            }
        }
        return new JSONDocument(_root, problems, commentRanges);
    }
    exports.parse = parse;
});

/*---------------------------------------------------------------------------------------------
*  Copyright (c) Microsoft Corporation. All rights reserved.
*  Licensed under the MIT License. See License.txt in the project root for license information.
*--------------------------------------------------------------------------------------------*/
(function (factory) {
    if (typeof module === "object" && typeof module.exports === "object") {
        var v = factory(require, exports);
        if (v !== undefined) module.exports = v;
    }
    else if (typeof define === "function" && define.amd) {
        define('vscode-json-languageservice/utils/json',["require", "exports"], factory);
    }
})(function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    function stringifyObject(obj, indent, stringifyLiteral) {
        if (obj !== null && typeof obj === 'object') {
            var newIndent = indent + '\t';
            if (Array.isArray(obj)) {
                if (obj.length === 0) {
                    return '[]';
                }
                var result = '[\n';
                for (var i = 0; i < obj.length; i++) {
                    result += newIndent + stringifyObject(obj[i], newIndent, stringifyLiteral);
                    if (i < obj.length - 1) {
                        result += ',';
                    }
                    result += '\n';
                }
                result += indent + ']';
                return result;
            }
            else {
                var keys = Object.keys(obj);
                if (keys.length === 0) {
                    return '{}';
                }
                var result = '{\n';
                for (var i = 0; i < keys.length; i++) {
                    var key = keys[i];
                    result += newIndent + JSON.stringify(key) + ': ' + stringifyObject(obj[key], newIndent, stringifyLiteral);
                    if (i < keys.length - 1) {
                        result += ',';
                    }
                    result += '\n';
                }
                result += indent + '}';
                return result;
            }
        }
        return stringifyLiteral(obj);
    }
    exports.stringifyObject = stringifyObject;
});

/*---------------------------------------------------------------------------------------------
*  Copyright (c) Microsoft Corporation. All rights reserved.
*  Licensed under the MIT License. See License.txt in the project root for license information.
*--------------------------------------------------------------------------------------------*/
(function (factory) {
    if (typeof module === "object" && typeof module.exports === "object") {
        var v = factory(require, exports);
        if (v !== undefined) module.exports = v;
    }
    else if (typeof define === "function" && define.amd) {
        define('vscode-json-languageservice/utils/strings',["require", "exports"], factory);
    }
})(function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    function startsWith(haystack, needle) {
        if (haystack.length < needle.length) {
            return false;
        }
        for (var i = 0; i < needle.length; i++) {
            if (haystack[i] !== needle[i]) {
                return false;
            }
        }
        return true;
    }
    exports.startsWith = startsWith;
    /**
     * Determines if haystack ends with needle.
     */
    function endsWith(haystack, needle) {
        var diff = haystack.length - needle.length;
        if (diff > 0) {
            return haystack.lastIndexOf(needle) === diff;
        }
        else if (diff === 0) {
            return haystack === needle;
        }
        else {
            return false;
        }
    }
    exports.endsWith = endsWith;
    function convertSimple2RegExpPattern(pattern) {
        return pattern.replace(/[\-\\\{\}\+\?\|\^\$\.\,\[\]\(\)\#\s]/g, '\\$&').replace(/[\*]/g, '.*');
    }
    exports.convertSimple2RegExpPattern = convertSimple2RegExpPattern;
    function repeat(value, count) {
        var s = '';
        while (count > 0) {
            if ((count & 1) === 1) {
                s += value;
            }
            value += value;
            count = count >>> 1;
        }
        return s;
    }
    exports.repeat = repeat;
});

/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
(function (factory) {
    if (typeof module === "object" && typeof module.exports === "object") {
        var v = factory(require, exports);
        if (v !== undefined) module.exports = v;
    }
    else if (typeof define === "function" && define.amd) {
        define('vscode-json-languageservice/services/jsonCompletion',["require", "exports", "../parser/jsonParser", "jsonc-parser", "../utils/json", "../utils/strings", "../utils/objects", "vscode-languageserver-types", "vscode-nls"], factory);
    }
})(function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    var Parser = require("../parser/jsonParser");
    var Json = require("jsonc-parser");
    var json_1 = require("../utils/json");
    var strings_1 = require("../utils/strings");
    var objects_1 = require("../utils/objects");
    var vscode_languageserver_types_1 = require("vscode-languageserver-types");
    var nls = require("vscode-nls");
    var localize = nls.loadMessageBundle();
    var JSONCompletion = /** @class */ (function () {
        function JSONCompletion(schemaService, contributions, promiseConstructor, clientCapabilities) {
            if (contributions === void 0) { contributions = []; }
            if (promiseConstructor === void 0) { promiseConstructor = Promise; }
            if (clientCapabilities === void 0) { clientCapabilities = {}; }
            this.schemaService = schemaService;
            this.contributions = contributions;
            this.promiseConstructor = promiseConstructor;
            this.clientCapabilities = clientCapabilities;
            this.templateVarIdCounter = 0;
        }
        JSONCompletion.prototype.doResolve = function (item) {
            for (var i = this.contributions.length - 1; i >= 0; i--) {
                if (this.contributions[i].resolveCompletion) {
                    var resolver = this.contributions[i].resolveCompletion(item);
                    if (resolver) {
                        return resolver;
                    }
                }
            }
            return this.promiseConstructor.resolve(item);
        };
        JSONCompletion.prototype.doComplete = function (document, position, doc) {
            var _this = this;
            var result = {
                items: [],
                isIncomplete: false
            };
            var offset = document.offsetAt(position);
            var node = doc.getNodeFromOffset(offset, true);
            if (this.isInComment(document, node ? node.offset : 0, offset)) {
                return Promise.resolve(result);
            }
            var currentWord = this.getCurrentWord(document, offset);
            var overwriteRange = null;
            if (node && (node.type === 'string' || node.type === 'number' || node.type === 'boolean' || node.type === 'null')) {
                overwriteRange = vscode_languageserver_types_1.Range.create(document.positionAt(node.offset), document.positionAt(node.offset + node.length));
            }
            else {
                var overwriteStart = offset - currentWord.length;
                if (overwriteStart > 0 && document.getText()[overwriteStart - 1] === '"') {
                    overwriteStart--;
                }
                overwriteRange = vscode_languageserver_types_1.Range.create(document.positionAt(overwriteStart), position);
            }
            var proposed = {};
            var collector = {
                add: function (suggestion) {
                    var existing = proposed[suggestion.label];
                    if (!existing) {
                        proposed[suggestion.label] = suggestion;
                        if (overwriteRange) {
                            suggestion.textEdit = vscode_languageserver_types_1.TextEdit.replace(overwriteRange, suggestion.insertText);
                        }
                        result.items.push(suggestion);
                    }
                    else if (!existing.documentation) {
                        existing.documentation = suggestion.documentation;
                    }
                },
                setAsIncomplete: function () {
                    result.isIncomplete = true;
                },
                error: function (message) {
                    console.error(message);
                },
                log: function (message) {
                    console.log(message);
                },
                getNumberOfProposals: function () {
                    return result.items.length;
                }
            };
            return this.schemaService.getSchemaForResource(document.uri, doc).then(function (schema) {
                var collectionPromises = [];
                var addValue = true;
                var currentKey = '';
                var currentProperty = null;
                if (node) {
                    if (node.type === 'string') {
                        var parent = node.parent;
                        if (parent && parent.type === 'property' && parent.keyNode === node) {
                            addValue = !parent.valueNode;
                            currentProperty = parent;
                            currentKey = document.getText().substr(node.offset + 1, node.length - 2);
                            if (parent) {
                                node = parent.parent;
                            }
                        }
                    }
                }
                // proposals for properties
                if (node && node.type === 'object') {
                    // don't suggest keys when the cursor is just before the opening curly brace
                    if (node.offset === offset) {
                        return result;
                    }
                    // don't suggest properties that are already present
                    var properties = node.properties;
                    properties.forEach(function (p) {
                        if (!currentProperty || currentProperty !== p) {
                            proposed[p.keyNode.value] = vscode_languageserver_types_1.CompletionItem.create('__');
                        }
                    });
                    var separatorAfter_1 = '';
                    if (addValue) {
                        separatorAfter_1 = _this.evaluateSeparatorAfter(document, document.offsetAt(overwriteRange.end));
                    }
                    if (schema) {
                        // property proposals with schema
                        _this.getPropertyCompletions(schema, doc, node, addValue, separatorAfter_1, collector);
                    }
                    else {
                        // property proposals without schema
                        _this.getSchemaLessPropertyCompletions(doc, node, currentKey, collector);
                    }
                    var location_1 = Parser.getNodePath(node);
                    _this.contributions.forEach(function (contribution) {
                        var collectPromise = contribution.collectPropertyCompletions(document.uri, location_1, currentWord, addValue, separatorAfter_1 === '', collector);
                        if (collectPromise) {
                            collectionPromises.push(collectPromise);
                        }
                    });
                    if ((!schema && currentWord.length > 0 && document.getText().charAt(offset - currentWord.length - 1) !== '"')) {
                        collector.add({
                            kind: vscode_languageserver_types_1.CompletionItemKind.Property,
                            label: _this.getLabelForValue(currentWord),
                            insertText: _this.getInsertTextForProperty(currentWord, null, false, separatorAfter_1),
                            insertTextFormat: vscode_languageserver_types_1.InsertTextFormat.Snippet, documentation: '',
                        });
                        collector.setAsIncomplete();
                    }
                }
                // proposals for values
                var types = {};
                if (schema) {
                    // value proposals with schema
                    _this.getValueCompletions(schema, doc, node, offset, document, collector, types);
                }
                else {
                    // value proposals without schema
                    _this.getSchemaLessValueCompletions(doc, node, offset, document, collector);
                }
                if (_this.contributions.length > 0) {
                    _this.getContributedValueCompletions(doc, node, offset, document, collector, collectionPromises);
                }
                return _this.promiseConstructor.all(collectionPromises).then(function () {
                    if (collector.getNumberOfProposals() === 0) {
                        var offsetForSeparator = offset;
                        if (node && (node.type === 'string' || node.type === 'number' || node.type === 'boolean' || node.type === 'null')) {
                            offsetForSeparator = node.offset + node.length;
                        }
                        var separatorAfter = _this.evaluateSeparatorAfter(document, offsetForSeparator);
                        _this.addFillerValueCompletions(types, separatorAfter, collector);
                    }
                    return result;
                });
            });
        };
        JSONCompletion.prototype.getPropertyCompletions = function (schema, doc, node, addValue, separatorAfter, collector) {
            var _this = this;
            var matchingSchemas = doc.getMatchingSchemas(schema.schema, node.offset);
            matchingSchemas.forEach(function (s) {
                if (s.node === node && !s.inverted) {
                    var schemaProperties_1 = s.schema.properties;
                    if (schemaProperties_1) {
                        Object.keys(schemaProperties_1).forEach(function (key) {
                            var propertySchema = schemaProperties_1[key];
                            if (typeof propertySchema === 'object' && !propertySchema.deprecationMessage && !propertySchema.doNotSuggest) {
                                var proposal = {
                                    kind: vscode_languageserver_types_1.CompletionItemKind.Property,
                                    label: _this.sanitizeLabel(key),
                                    insertText: _this.getInsertTextForProperty(key, propertySchema, addValue, separatorAfter),
                                    insertTextFormat: vscode_languageserver_types_1.InsertTextFormat.Snippet,
                                    filterText: _this.getFilterTextForValue(key),
                                    documentation: _this.fromMarkup(propertySchema.markdownDescription) || propertySchema.description || '',
                                };
                                if (strings_1.endsWith(proposal.insertText, "$1" + separatorAfter)) {
                                    proposal.command = {
                                        title: 'Suggest',
                                        command: 'editor.action.triggerSuggest'
                                    };
                                }
                                collector.add(proposal);
                            }
                        });
                    }
                }
            });
        };
        JSONCompletion.prototype.getSchemaLessPropertyCompletions = function (doc, node, currentKey, collector) {
            var _this = this;
            var collectCompletionsForSimilarObject = function (obj) {
                obj.properties.forEach(function (p) {
                    var key = p.keyNode.value;
                    collector.add({
                        kind: vscode_languageserver_types_1.CompletionItemKind.Property,
                        label: _this.sanitizeLabel(key),
                        insertText: _this.getInsertTextForValue(key, ''),
                        insertTextFormat: vscode_languageserver_types_1.InsertTextFormat.Snippet,
                        filterText: _this.getFilterTextForValue(key),
                        documentation: ''
                    });
                });
            };
            if (node.parent) {
                if (node.parent.type === 'property') {
                    // if the object is a property value, check the tree for other objects that hang under a property of the same name
                    var parentKey_1 = node.parent.keyNode.value;
                    doc.visit(function (n) {
                        if (n.type === 'property' && n !== node.parent && n.keyNode.value === parentKey_1 && n.valueNode && n.valueNode.type === 'object') {
                            collectCompletionsForSimilarObject(n.valueNode);
                        }
                        return true;
                    });
                }
                else if (node.parent.type === 'array') {
                    // if the object is in an array, use all other array elements as similar objects
                    node.parent.items.forEach(function (n) {
                        if (n.type === 'object' && n !== node) {
                            collectCompletionsForSimilarObject(n);
                        }
                    });
                }
            }
            else if (node.type === 'object') {
                collector.add({
                    kind: vscode_languageserver_types_1.CompletionItemKind.Property,
                    label: '$schema',
                    insertText: this.getInsertTextForProperty('$schema', null, true, ''),
                    insertTextFormat: vscode_languageserver_types_1.InsertTextFormat.Snippet, documentation: '',
                    filterText: this.getFilterTextForValue("$schema")
                });
            }
        };
        JSONCompletion.prototype.getSchemaLessValueCompletions = function (doc, node, offset, document, collector) {
            var _this = this;
            var offsetForSeparator = offset;
            if (node && (node.type === 'string' || node.type === 'number' || node.type === 'boolean' || node.type === 'null')) {
                offsetForSeparator = node.offset + node.length;
                node = node.parent;
            }
            if (!node) {
                collector.add({
                    kind: this.getSuggestionKind('object'),
                    label: 'Empty object',
                    insertText: this.getInsertTextForValue({}, ''),
                    insertTextFormat: vscode_languageserver_types_1.InsertTextFormat.Snippet,
                    documentation: ''
                });
                collector.add({
                    kind: this.getSuggestionKind('array'),
                    label: 'Empty array',
                    insertText: this.getInsertTextForValue([], ''),
                    insertTextFormat: vscode_languageserver_types_1.InsertTextFormat.Snippet,
                    documentation: ''
                });
                return;
            }
            var separatorAfter = this.evaluateSeparatorAfter(document, offsetForSeparator);
            var collectSuggestionsForValues = function (value) {
                if (!Parser.contains(value.parent, offset, true)) {
                    collector.add({
                        kind: _this.getSuggestionKind(value.type),
                        label: _this.getLabelTextForMatchingNode(value, document),
                        insertText: _this.getInsertTextForMatchingNode(value, document, separatorAfter),
                        insertTextFormat: vscode_languageserver_types_1.InsertTextFormat.Snippet, documentation: ''
                    });
                }
                if (value.type === 'boolean') {
                    _this.addBooleanValueCompletion(!value.value, separatorAfter, collector);
                }
            };
            if (node.type === 'property') {
                if (offset > node.colonOffset) {
                    var valueNode = node.valueNode;
                    if (valueNode && (offset > (valueNode.offset + valueNode.length) || valueNode.type === 'object' || valueNode.type === 'array')) {
                        return;
                    }
                    // suggest values at the same key
                    var parentKey_2 = node.keyNode.value;
                    doc.visit(function (n) {
                        if (n.type === 'property' && n.keyNode.value === parentKey_2 && n.valueNode) {
                            collectSuggestionsForValues(n.valueNode);
                        }
                        return true;
                    });
                    if (parentKey_2 === '$schema' && node.parent && !node.parent.parent) {
                        this.addDollarSchemaCompletions(separatorAfter, collector);
                    }
                }
            }
            if (node.type === 'array') {
                if (node.parent && node.parent.type === 'property') {
                    // suggest items of an array at the same key
                    var parentKey_3 = node.parent.keyNode.value;
                    doc.visit(function (n) {
                        if (n.type === 'property' && n.keyNode.value === parentKey_3 && n.valueNode && n.valueNode.type === 'array') {
                            n.valueNode.items.forEach(collectSuggestionsForValues);
                        }
                        return true;
                    });
                }
                else {
                    // suggest items in the same array
                    node.items.forEach(collectSuggestionsForValues);
                }
            }
        };
        JSONCompletion.prototype.getValueCompletions = function (schema, doc, node, offset, document, collector, types) {
            var _this = this;
            var offsetForSeparator = offset;
            var parentKey = null;
            var valueNode = null;
            if (node && (node.type === 'string' || node.type === 'number' || node.type === 'boolean' || node.type === 'null')) {
                offsetForSeparator = node.offset + node.length;
                valueNode = node;
                node = node.parent;
            }
            if (!node) {
                this.addSchemaValueCompletions(schema.schema, '', collector, types);
                return;
            }
            if ((node.type === 'property') && offset > node.colonOffset) {
                var valueNode_1 = node.valueNode;
                if (valueNode_1 && offset > (valueNode_1.offset + valueNode_1.length)) {
                    return; // we are past the value node
                }
                parentKey = node.keyNode.value;
                node = node.parent;
            }
            if (node && (parentKey !== null || node.type === 'array')) {
                var separatorAfter_2 = this.evaluateSeparatorAfter(document, offsetForSeparator);
                var matchingSchemas = doc.getMatchingSchemas(schema.schema, node.offset, valueNode);
                matchingSchemas.forEach(function (s) {
                    if (s.node === node && !s.inverted && s.schema) {
                        if (node.type === 'array' && s.schema.items) {
                            if (Array.isArray(s.schema.items)) {
                                var index = _this.findItemAtOffset(node, document, offset);
                                if (index < s.schema.items.length) {
                                    _this.addSchemaValueCompletions(s.schema.items[index], separatorAfter_2, collector, types);
                                }
                            }
                            else {
                                _this.addSchemaValueCompletions(s.schema.items, separatorAfter_2, collector, types);
                            }
                        }
                        if (s.schema.properties) {
                            var propertySchema = s.schema.properties[parentKey];
                            if (propertySchema) {
                                _this.addSchemaValueCompletions(propertySchema, separatorAfter_2, collector, types);
                            }
                        }
                    }
                });
                if (parentKey === '$schema' && !node.parent) {
                    this.addDollarSchemaCompletions(separatorAfter_2, collector);
                }
                if (types['boolean']) {
                    this.addBooleanValueCompletion(true, separatorAfter_2, collector);
                    this.addBooleanValueCompletion(false, separatorAfter_2, collector);
                }
                if (types['null']) {
                    this.addNullValueCompletion(separatorAfter_2, collector);
                }
            }
        };
        JSONCompletion.prototype.getContributedValueCompletions = function (doc, node, offset, document, collector, collectionPromises) {
            if (!node) {
                this.contributions.forEach(function (contribution) {
                    var collectPromise = contribution.collectDefaultCompletions(document.uri, collector);
                    if (collectPromise) {
                        collectionPromises.push(collectPromise);
                    }
                });
            }
            else {
                if (node.type === 'string' || node.type === 'number' || node.type === 'boolean' || node.type === 'null') {
                    node = node.parent;
                }
                if ((node.type === 'property') && offset > node.colonOffset) {
                    var parentKey_4 = node.keyNode.value;
                    var valueNode = node.valueNode;
                    if (!valueNode || offset <= (valueNode.offset + valueNode.length)) {
                        var location_2 = Parser.getNodePath(node.parent);
                        this.contributions.forEach(function (contribution) {
                            var collectPromise = contribution.collectValueCompletions(document.uri, location_2, parentKey_4, collector);
                            if (collectPromise) {
                                collectionPromises.push(collectPromise);
                            }
                        });
                    }
                }
            }
        };
        JSONCompletion.prototype.addSchemaValueCompletions = function (schema, separatorAfter, collector, types) {
            var _this = this;
            if (typeof schema === 'object') {
                this.addEnumValueCompletions(schema, separatorAfter, collector);
                this.addDefaultValueCompletions(schema, separatorAfter, collector);
                this.collectTypes(schema, types);
                if (Array.isArray(schema.allOf)) {
                    schema.allOf.forEach(function (s) { return _this.addSchemaValueCompletions(s, separatorAfter, collector, types); });
                }
                if (Array.isArray(schema.anyOf)) {
                    schema.anyOf.forEach(function (s) { return _this.addSchemaValueCompletions(s, separatorAfter, collector, types); });
                }
                if (Array.isArray(schema.oneOf)) {
                    schema.oneOf.forEach(function (s) { return _this.addSchemaValueCompletions(s, separatorAfter, collector, types); });
                }
            }
        };
        JSONCompletion.prototype.addDefaultValueCompletions = function (schema, separatorAfter, collector, arrayDepth) {
            var _this = this;
            if (arrayDepth === void 0) { arrayDepth = 0; }
            var hasProposals = false;
            if (objects_1.isDefined(schema.default)) {
                var type = schema.type;
                var value = schema.default;
                for (var i = arrayDepth; i > 0; i--) {
                    value = [value];
                    type = 'array';
                }
                collector.add({
                    kind: this.getSuggestionKind(type),
                    label: this.getLabelForValue(value),
                    insertText: this.getInsertTextForValue(value, separatorAfter),
                    insertTextFormat: vscode_languageserver_types_1.InsertTextFormat.Snippet,
                    detail: localize('json.suggest.default', 'Default value')
                });
                hasProposals = true;
            }
            if (Array.isArray(schema.examples)) {
                schema.examples.forEach(function (example) {
                    var type = schema.type;
                    var value = example;
                    for (var i = arrayDepth; i > 0; i--) {
                        value = [value];
                        type = 'array';
                    }
                    collector.add({
                        kind: _this.getSuggestionKind(type),
                        label: _this.getLabelForValue(value),
                        insertText: _this.getInsertTextForValue(value, separatorAfter),
                        insertTextFormat: vscode_languageserver_types_1.InsertTextFormat.Snippet
                    });
                    hasProposals = true;
                });
            }
            if (Array.isArray(schema.defaultSnippets)) {
                schema.defaultSnippets.forEach(function (s) {
                    var type = schema.type;
                    var value = s.body;
                    var label = s.label;
                    var insertText;
                    var filterText;
                    if (objects_1.isDefined(value)) {
                        var type_1 = schema.type;
                        for (var i = arrayDepth; i > 0; i--) {
                            value = [value];
                            type_1 = 'array';
                        }
                        insertText = _this.getInsertTextForSnippetValue(value, separatorAfter);
                        filterText = _this.getFilterTextForSnippetValue(value);
                        label = label || _this.getLabelForSnippetValue(value);
                    }
                    else if (typeof s.bodyText === 'string') {
                        var prefix = '', suffix = '', indent = '';
                        for (var i = arrayDepth; i > 0; i--) {
                            prefix = prefix + indent + '[\n';
                            suffix = suffix + '\n' + indent + ']';
                            indent += '\t';
                            type = 'array';
                        }
                        insertText = prefix + indent + s.bodyText.split('\n').join('\n' + indent) + suffix + separatorAfter;
                        label = label || _this.sanitizeLabel(insertText),
                            filterText = insertText.replace(/[\n]/g, ''); // remove new lines
                    }
                    collector.add({
                        kind: _this.getSuggestionKind(type),
                        label: label,
                        documentation: _this.fromMarkup(s.markdownDescription) || s.description,
                        insertText: insertText,
                        insertTextFormat: vscode_languageserver_types_1.InsertTextFormat.Snippet,
                        filterText: filterText
                    });
                    hasProposals = true;
                });
            }
            if (!hasProposals && typeof schema.items === 'object' && !Array.isArray(schema.items)) {
                this.addDefaultValueCompletions(schema.items, separatorAfter, collector, arrayDepth + 1);
            }
        };
        JSONCompletion.prototype.addEnumValueCompletions = function (schema, separatorAfter, collector) {
            if (objects_1.isDefined(schema.const)) {
                collector.add({
                    kind: this.getSuggestionKind(schema.type),
                    label: this.getLabelForValue(schema.const),
                    insertText: this.getInsertTextForValue(schema.const, separatorAfter),
                    insertTextFormat: vscode_languageserver_types_1.InsertTextFormat.Snippet,
                    documentation: this.fromMarkup(schema.markdownDescription) || schema.description
                });
            }
            if (Array.isArray(schema.enum)) {
                for (var i = 0, length = schema.enum.length; i < length; i++) {
                    var enm = schema.enum[i];
                    var documentation = this.fromMarkup(schema.markdownDescription) || schema.description;
                    if (schema.markdownEnumDescriptions && i < schema.markdownEnumDescriptions.length && this.doesSupportMarkdown()) {
                        documentation = this.fromMarkup(schema.markdownEnumDescriptions[i]);
                    }
                    else if (schema.enumDescriptions && i < schema.enumDescriptions.length) {
                        documentation = schema.enumDescriptions[i];
                    }
                    collector.add({
                        kind: this.getSuggestionKind(schema.type),
                        label: this.getLabelForValue(enm),
                        insertText: this.getInsertTextForValue(enm, separatorAfter),
                        insertTextFormat: vscode_languageserver_types_1.InsertTextFormat.Snippet,
                        documentation: documentation
                    });
                }
            }
        };
        JSONCompletion.prototype.collectTypes = function (schema, types) {
            if (Array.isArray(schema.enum) || objects_1.isDefined(schema.const)) {
                return;
            }
            var type = schema.type;
            if (Array.isArray(type)) {
                type.forEach(function (t) { return types[t] = true; });
            }
            else {
                types[type] = true;
            }
        };
        JSONCompletion.prototype.addFillerValueCompletions = function (types, separatorAfter, collector) {
            if (types['object']) {
                collector.add({
                    kind: this.getSuggestionKind('object'),
                    label: '{}',
                    insertText: this.getInsertTextForGuessedValue({}, separatorAfter),
                    insertTextFormat: vscode_languageserver_types_1.InsertTextFormat.Snippet,
                    detail: localize('defaults.object', 'New object'),
                    documentation: ''
                });
            }
            if (types['array']) {
                collector.add({
                    kind: this.getSuggestionKind('array'),
                    label: '[]',
                    insertText: this.getInsertTextForGuessedValue([], separatorAfter),
                    insertTextFormat: vscode_languageserver_types_1.InsertTextFormat.Snippet,
                    detail: localize('defaults.array', 'New array'),
                    documentation: ''
                });
            }
        };
        JSONCompletion.prototype.addBooleanValueCompletion = function (value, separatorAfter, collector) {
            collector.add({
                kind: this.getSuggestionKind('boolean'),
                label: value ? 'true' : 'false',
                insertText: this.getInsertTextForValue(value, separatorAfter),
                insertTextFormat: vscode_languageserver_types_1.InsertTextFormat.Snippet,
                documentation: ''
            });
        };
        JSONCompletion.prototype.addNullValueCompletion = function (separatorAfter, collector) {
            collector.add({
                kind: this.getSuggestionKind('null'),
                label: 'null',
                insertText: 'null' + separatorAfter,
                insertTextFormat: vscode_languageserver_types_1.InsertTextFormat.Snippet,
                documentation: ''
            });
        };
        JSONCompletion.prototype.addDollarSchemaCompletions = function (separatorAfter, collector) {
            var _this = this;
            var schemaIds = this.schemaService.getRegisteredSchemaIds(function (schema) { return schema === 'http' || schema === 'https'; });
            schemaIds.forEach(function (schemaId) { return collector.add({
                kind: vscode_languageserver_types_1.CompletionItemKind.Module,
                label: _this.getLabelForValue(schemaId),
                filterText: _this.getFilterTextForValue(schemaId),
                insertText: _this.getInsertTextForValue(schemaId, separatorAfter),
                insertTextFormat: vscode_languageserver_types_1.InsertTextFormat.Snippet, documentation: ''
            }); });
        };
        JSONCompletion.prototype.sanitizeLabel = function (label) {
            label = label.replace(/[\n]/g, '↵');
            if (label.length > 57) {
                label = label.substr(0, 57).trim() + '...';
            }
            return label;
        };
        JSONCompletion.prototype.getLabelForValue = function (value) {
            return this.sanitizeLabel(JSON.stringify(value));
        };
        JSONCompletion.prototype.getFilterTextForValue = function (value) {
            return JSON.stringify(value);
        };
        JSONCompletion.prototype.getFilterTextForSnippetValue = function (value) {
            return JSON.stringify(value).replace(/\$\{\d+:([^}]+)\}|\$\d+/g, '$1');
        };
        JSONCompletion.prototype.getLabelForSnippetValue = function (value) {
            var label = JSON.stringify(value);
            label = label.replace(/\$\{\d+:([^}]+)\}|\$\d+/g, '$1');
            return this.sanitizeLabel(label);
        };
        JSONCompletion.prototype.getInsertTextForPlainText = function (text) {
            return text.replace(/[\\\$\}]/g, '\\$&'); // escape $, \ and } 
        };
        JSONCompletion.prototype.getInsertTextForValue = function (value, separatorAfter) {
            var text = JSON.stringify(value, null, '\t');
            if (text === '{}') {
                return '{$1}' + separatorAfter;
            }
            else if (text === '[]') {
                return '[$1]' + separatorAfter;
            }
            return this.getInsertTextForPlainText(text + separatorAfter);
        };
        JSONCompletion.prototype.getInsertTextForSnippetValue = function (value, separatorAfter) {
            var replacer = function (value) {
                if (typeof value === 'string') {
                    if (value[0] === '^') {
                        return value.substr(1);
                    }
                }
                return JSON.stringify(value);
            };
            return json_1.stringifyObject(value, '', replacer) + separatorAfter;
        };
        JSONCompletion.prototype.getInsertTextForGuessedValue = function (value, separatorAfter) {
            switch (typeof value) {
                case 'object':
                    if (value === null) {
                        return '${1:null}' + separatorAfter;
                    }
                    return this.getInsertTextForValue(value, separatorAfter);
                case 'string':
                    var snippetValue = JSON.stringify(value);
                    snippetValue = snippetValue.substr(1, snippetValue.length - 2); // remove quotes
                    snippetValue = this.getInsertTextForPlainText(snippetValue); // escape \ and }
                    return '"${1:' + snippetValue + '}"' + separatorAfter;
                case 'number':
                case 'boolean':
                    return '${1:' + JSON.stringify(value) + '}' + separatorAfter;
            }
            return this.getInsertTextForValue(value, separatorAfter);
        };
        JSONCompletion.prototype.getSuggestionKind = function (type) {
            if (Array.isArray(type)) {
                var array = type;
                type = array.length > 0 ? array[0] : null;
            }
            if (!type) {
                return vscode_languageserver_types_1.CompletionItemKind.Value;
            }
            switch (type) {
                case 'string': return vscode_languageserver_types_1.CompletionItemKind.Value;
                case 'object': return vscode_languageserver_types_1.CompletionItemKind.Module;
                case 'property': return vscode_languageserver_types_1.CompletionItemKind.Property;
                default: return vscode_languageserver_types_1.CompletionItemKind.Value;
            }
        };
        JSONCompletion.prototype.getLabelTextForMatchingNode = function (node, document) {
            switch (node.type) {
                case 'array':
                    return '[]';
                case 'object':
                    return '{}';
                default:
                    var content = document.getText().substr(node.offset, node.length);
                    return content;
            }
        };
        JSONCompletion.prototype.getInsertTextForMatchingNode = function (node, document, separatorAfter) {
            switch (node.type) {
                case 'array':
                    return this.getInsertTextForValue([], separatorAfter);
                case 'object':
                    return this.getInsertTextForValue({}, separatorAfter);
                default:
                    var content = document.getText().substr(node.offset, node.length) + separatorAfter;
                    return this.getInsertTextForPlainText(content);
            }
        };
        JSONCompletion.prototype.getInsertTextForProperty = function (key, propertySchema, addValue, separatorAfter) {
            var propertyText = this.getInsertTextForValue(key, '');
            if (!addValue) {
                return propertyText;
            }
            var resultText = propertyText + ': ';
            var value;
            var nValueProposals = 0;
            if (propertySchema) {
                if (Array.isArray(propertySchema.defaultSnippets)) {
                    if (propertySchema.defaultSnippets.length === 1) {
                        var body = propertySchema.defaultSnippets[0].body;
                        if (objects_1.isDefined(body)) {
                            value = this.getInsertTextForSnippetValue(body, '');
                        }
                    }
                    nValueProposals += propertySchema.defaultSnippets.length;
                }
                if (propertySchema.enum) {
                    if (!value && propertySchema.enum.length === 1) {
                        value = this.getInsertTextForGuessedValue(propertySchema.enum[0], '');
                    }
                    nValueProposals += propertySchema.enum.length;
                }
                if (objects_1.isDefined(propertySchema.default)) {
                    if (!value) {
                        value = this.getInsertTextForGuessedValue(propertySchema.default, '');
                    }
                    nValueProposals++;
                }
                if (nValueProposals === 0) {
                    var type = Array.isArray(propertySchema.type) ? propertySchema.type[0] : propertySchema.type;
                    if (!type) {
                        if (propertySchema.properties) {
                            type = 'object';
                        }
                        else if (propertySchema.items) {
                            type = 'array';
                        }
                    }
                    switch (type) {
                        case 'boolean':
                            value = '$1';
                            break;
                        case 'string':
                            value = '"$1"';
                            break;
                        case 'object':
                            value = '{$1}';
                            break;
                        case 'array':
                            value = '[$1]';
                            break;
                        case 'number':
                        case 'integer':
                            value = '${1:0}';
                            break;
                        case 'null':
                            value = '${1:null}';
                            break;
                        default:
                            return propertyText;
                    }
                }
            }
            if (!value || nValueProposals > 1) {
                value = '$1';
            }
            return resultText + value + separatorAfter;
        };
        JSONCompletion.prototype.getCurrentWord = function (document, offset) {
            var i = offset - 1;
            var text = document.getText();
            while (i >= 0 && ' \t\n\r\v":{[,]}'.indexOf(text.charAt(i)) === -1) {
                i--;
            }
            return text.substring(i + 1, offset);
        };
        JSONCompletion.prototype.evaluateSeparatorAfter = function (document, offset) {
            var scanner = Json.createScanner(document.getText(), true);
            scanner.setPosition(offset);
            var token = scanner.scan();
            switch (token) {
                case 5 /* CommaToken */:
                case 2 /* CloseBraceToken */:
                case 4 /* CloseBracketToken */:
                case 17 /* EOF */:
                    return '';
                default:
                    return ',';
            }
        };
        JSONCompletion.prototype.findItemAtOffset = function (node, document, offset) {
            var scanner = Json.createScanner(document.getText(), true);
            var children = node.items;
            for (var i = children.length - 1; i >= 0; i--) {
                var child = children[i];
                if (offset > child.offset + child.length) {
                    scanner.setPosition(child.offset + child.length);
                    var token = scanner.scan();
                    if (token === 5 /* CommaToken */ && offset >= scanner.getTokenOffset() + scanner.getTokenLength()) {
                        return i + 1;
                    }
                    return i;
                }
                else if (offset >= child.offset) {
                    return i;
                }
            }
            return 0;
        };
        JSONCompletion.prototype.isInComment = function (document, start, offset) {
            var scanner = Json.createScanner(document.getText(), false);
            scanner.setPosition(start);
            var token = scanner.scan();
            while (token !== 17 /* EOF */ && (scanner.getTokenOffset() + scanner.getTokenLength() < offset)) {
                token = scanner.scan();
            }
            return (token === 12 /* LineCommentTrivia */ || token === 13 /* BlockCommentTrivia */) && scanner.getTokenOffset() <= offset;
        };
        JSONCompletion.prototype.fromMarkup = function (markupString) {
            if (markupString && this.doesSupportMarkdown()) {
                return {
                    kind: vscode_languageserver_types_1.MarkupKind.Markdown,
                    value: markupString
                };
            }
            return undefined;
        };
        JSONCompletion.prototype.doesSupportMarkdown = function () {
            if (!objects_1.isDefined(this.supportsMarkdown)) {
                var completion = this.clientCapabilities.textDocument && this.clientCapabilities.textDocument.completion;
                this.supportsMarkdown = completion && completion.completionItem && Array.isArray(completion.completionItem.documentationFormat) && completion.completionItem.documentationFormat.indexOf(vscode_languageserver_types_1.MarkupKind.Markdown) !== -1;
            }
            return this.supportsMarkdown;
        };
        return JSONCompletion;
    }());
    exports.JSONCompletion = JSONCompletion;
});

/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
(function (factory) {
    if (typeof module === "object" && typeof module.exports === "object") {
        var v = factory(require, exports);
        if (v !== undefined) module.exports = v;
    }
    else if (typeof define === "function" && define.amd) {
        define('vscode-json-languageservice/services/jsonHover',["require", "exports", "../parser/jsonParser", "vscode-languageserver-types"], factory);
    }
})(function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    var Parser = require("../parser/jsonParser");
    var vscode_languageserver_types_1 = require("vscode-languageserver-types");
    var JSONHover = /** @class */ (function () {
        function JSONHover(schemaService, contributions, promiseConstructor) {
            if (contributions === void 0) { contributions = []; }
            this.schemaService = schemaService;
            this.contributions = contributions;
            this.promise = promiseConstructor || Promise;
        }
        JSONHover.prototype.doHover = function (document, position, doc) {
            var offset = document.offsetAt(position);
            var node = doc.getNodeFromOffset(offset);
            if (!node || (node.type === 'object' || node.type === 'array') && offset > node.offset + 1 && offset < node.offset + node.length - 1) {
                return this.promise.resolve(null);
            }
            var hoverRangeNode = node;
            // use the property description when hovering over an object key
            if (node.type === 'string') {
                var parent = node.parent;
                if (parent && parent.type === 'property' && parent.keyNode === node) {
                    node = parent.valueNode;
                    if (!node) {
                        return this.promise.resolve(null);
                    }
                }
            }
            var hoverRange = vscode_languageserver_types_1.Range.create(document.positionAt(hoverRangeNode.offset), document.positionAt(hoverRangeNode.offset + hoverRangeNode.length));
            var createHover = function (contents) {
                var result = {
                    contents: contents,
                    range: hoverRange
                };
                return result;
            };
            var location = Parser.getNodePath(node);
            for (var i = this.contributions.length - 1; i >= 0; i--) {
                var contribution = this.contributions[i];
                var promise = contribution.getInfoContribution(document.uri, location);
                if (promise) {
                    return promise.then(function (htmlContent) { return createHover(htmlContent); });
                }
            }
            return this.schemaService.getSchemaForResource(document.uri, doc).then(function (schema) {
                if (schema) {
                    var matchingSchemas = doc.getMatchingSchemas(schema.schema, node.offset);
                    var title_1 = null;
                    var markdownDescription_1 = null;
                    var markdownEnumValueDescription_1 = null, enumValue_1 = null;
                    matchingSchemas.every(function (s) {
                        if (s.node === node && !s.inverted && s.schema) {
                            title_1 = title_1 || s.schema.title;
                            markdownDescription_1 = markdownDescription_1 || s.schema.markdownDescription || toMarkdown(s.schema.description);
                            if (s.schema.enum) {
                                var idx = s.schema.enum.indexOf(Parser.getNodeValue(node));
                                if (s.schema.markdownEnumDescriptions) {
                                    markdownEnumValueDescription_1 = s.schema.markdownEnumDescriptions[idx];
                                }
                                else if (s.schema.enumDescriptions) {
                                    markdownEnumValueDescription_1 = toMarkdown(s.schema.enumDescriptions[idx]);
                                }
                                if (markdownEnumValueDescription_1) {
                                    enumValue_1 = s.schema.enum[idx];
                                    if (typeof enumValue_1 !== 'string') {
                                        enumValue_1 = JSON.stringify(enumValue_1);
                                    }
                                }
                            }
                        }
                        return true;
                    });
                    var result = '';
                    if (title_1) {
                        result = toMarkdown(title_1);
                    }
                    if (markdownDescription_1) {
                        if (result.length > 0) {
                            result += "\n\n";
                        }
                        result += markdownDescription_1;
                    }
                    if (markdownEnumValueDescription_1) {
                        if (result.length > 0) {
                            result += "\n\n";
                        }
                        result += "`" + toMarkdown(enumValue_1) + "`: " + markdownEnumValueDescription_1;
                    }
                    return createHover([result]);
                }
                return null;
            });
        };
        return JSONHover;
    }());
    exports.JSONHover = JSONHover;
    function toMarkdown(plain) {
        if (plain) {
            var res = plain.replace(/([^\n\r])(\r?\n)([^\n\r])/gm, '$1\n\n$3'); // single new lines to \n\n (Markdown paragraph)
            return res.replace(/[\\`*_{}[\]()#+\-.!]/g, "\\$&"); // escape markdown syntax tokens: http://daringfireball.net/projects/markdown/syntax#backslash
        }
        return void 0;
    }
});

/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
(function (factory) {
    if (typeof module === "object" && typeof module.exports === "object") {
        var v = factory(require, exports);
        if (v !== undefined) module.exports = v;
    }
    else if (typeof define === "function" && define.amd) {
        define('vscode-json-languageservice/services/jsonSchemaService',["require", "exports", "jsonc-parser", "vscode-uri", "../utils/strings", "../parser/jsonParser", "vscode-nls"], factory);
    }
})(function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    var Json = require("jsonc-parser");
    var vscode_uri_1 = require("vscode-uri");
    var Strings = require("../utils/strings");
    var Parser = require("../parser/jsonParser");
    var nls = require("vscode-nls");
    var localize = nls.loadMessageBundle();
    var FilePatternAssociation = /** @class */ (function () {
        function FilePatternAssociation(pattern) {
            try {
                this.patternRegExp = new RegExp(Strings.convertSimple2RegExpPattern(pattern) + '$');
            }
            catch (e) {
                // invalid pattern
                this.patternRegExp = null;
            }
            this.schemas = [];
        }
        FilePatternAssociation.prototype.addSchema = function (id) {
            this.schemas.push(id);
        };
        FilePatternAssociation.prototype.matchesPattern = function (fileName) {
            return this.patternRegExp && this.patternRegExp.test(fileName);
        };
        FilePatternAssociation.prototype.getSchemas = function () {
            return this.schemas;
        };
        return FilePatternAssociation;
    }());
    var SchemaHandle = /** @class */ (function () {
        function SchemaHandle(service, url, unresolvedSchemaContent) {
            this.service = service;
            this.url = url;
            this.dependencies = {};
            if (unresolvedSchemaContent) {
                this.unresolvedSchema = this.service.promise.resolve(new UnresolvedSchema(unresolvedSchemaContent));
            }
        }
        SchemaHandle.prototype.getUnresolvedSchema = function () {
            if (!this.unresolvedSchema) {
                this.unresolvedSchema = this.service.loadSchema(this.url);
            }
            return this.unresolvedSchema;
        };
        SchemaHandle.prototype.getResolvedSchema = function () {
            var _this = this;
            if (!this.resolvedSchema) {
                this.resolvedSchema = this.getUnresolvedSchema().then(function (unresolved) {
                    return _this.service.resolveSchemaContent(unresolved, _this.url, _this.dependencies);
                });
            }
            return this.resolvedSchema;
        };
        SchemaHandle.prototype.clearSchema = function () {
            this.resolvedSchema = null;
            this.unresolvedSchema = null;
            this.dependencies = {};
        };
        return SchemaHandle;
    }());
    var UnresolvedSchema = /** @class */ (function () {
        function UnresolvedSchema(schema, errors) {
            if (errors === void 0) { errors = []; }
            this.schema = schema;
            this.errors = errors;
        }
        return UnresolvedSchema;
    }());
    exports.UnresolvedSchema = UnresolvedSchema;
    var ResolvedSchema = /** @class */ (function () {
        function ResolvedSchema(schema, errors) {
            if (errors === void 0) { errors = []; }
            this.schema = schema;
            this.errors = errors;
        }
        ResolvedSchema.prototype.getSection = function (path) {
            return Parser.asSchema(this.getSectionRecursive(path, this.schema));
        };
        ResolvedSchema.prototype.getSectionRecursive = function (path, schema) {
            if (!schema || typeof schema === 'boolean' || path.length === 0) {
                return schema;
            }
            var next = path.shift();
            if (schema.properties && typeof schema.properties[next]) {
                return this.getSectionRecursive(path, schema.properties[next]);
            }
            else if (schema.patternProperties) {
                for (var _i = 0, _a = Object.keys(schema.patternProperties); _i < _a.length; _i++) {
                    var pattern = _a[_i];
                    var regex = new RegExp(pattern);
                    if (regex.test(next)) {
                        return this.getSectionRecursive(path, schema.patternProperties[pattern]);
                    }
                }
            }
            else if (typeof schema.additionalProperties === 'object') {
                return this.getSectionRecursive(path, schema.additionalProperties);
            }
            else if (next.match('[0-9]+')) {
                if (Array.isArray(schema.items)) {
                    var index = parseInt(next, 10);
                    if (!isNaN(index) && schema.items[index]) {
                        return this.getSectionRecursive(path, schema.items[index]);
                    }
                }
                else if (schema.items) {
                    return this.getSectionRecursive(path, schema.items);
                }
            }
            return null;
        };
        return ResolvedSchema;
    }());
    exports.ResolvedSchema = ResolvedSchema;
    var JSONSchemaService = /** @class */ (function () {
        function JSONSchemaService(requestService, contextService, promiseConstructor) {
            this.contextService = contextService;
            this.requestService = requestService;
            this.promiseConstructor = promiseConstructor || Promise;
            this.callOnDispose = [];
            this.contributionSchemas = {};
            this.contributionAssociations = {};
            this.schemasById = {};
            this.filePatternAssociations = [];
            this.filePatternAssociationById = {};
            this.registeredSchemasIds = {};
        }
        JSONSchemaService.prototype.getRegisteredSchemaIds = function (filter) {
            return Object.keys(this.registeredSchemasIds).filter(function (id) {
                var scheme = vscode_uri_1.default.parse(id).scheme;
                return scheme !== 'schemaservice' && (!filter || filter(scheme));
            });
        };
        Object.defineProperty(JSONSchemaService.prototype, "promise", {
            get: function () {
                return this.promiseConstructor;
            },
            enumerable: true,
            configurable: true
        });
        JSONSchemaService.prototype.dispose = function () {
            while (this.callOnDispose.length > 0) {
                this.callOnDispose.pop()();
            }
        };
        JSONSchemaService.prototype.onResourceChange = function (uri) {
            var _this = this;
            var hasChanges = false;
            uri = this.normalizeId(uri);
            var toWalk = [uri];
            var all = Object.keys(this.schemasById).map(function (key) { return _this.schemasById[key]; });
            while (toWalk.length) {
                var curr = toWalk.pop();
                for (var i = 0; i < all.length; i++) {
                    var handle = all[i];
                    if (handle && (handle.url === curr || handle.dependencies[curr])) {
                        if (handle.url !== curr) {
                            toWalk.push(handle.url);
                        }
                        handle.clearSchema();
                        all[i] = undefined;
                        hasChanges = true;
                    }
                }
            }
            return hasChanges;
        };
        JSONSchemaService.prototype.normalizeId = function (id) {
            // remove trailing '#', normalize drive capitalization
            return vscode_uri_1.default.parse(id).toString();
        };
        JSONSchemaService.prototype.setSchemaContributions = function (schemaContributions) {
            if (schemaContributions.schemas) {
                var schemas = schemaContributions.schemas;
                for (var id in schemas) {
                    var normalizedId = this.normalizeId(id);
                    this.contributionSchemas[normalizedId] = this.addSchemaHandle(normalizedId, schemas[id]);
                }
            }
            if (schemaContributions.schemaAssociations) {
                var schemaAssociations = schemaContributions.schemaAssociations;
                for (var pattern in schemaAssociations) {
                    var associations = schemaAssociations[pattern];
                    this.contributionAssociations[pattern] = associations;
                    var fpa = this.getOrAddFilePatternAssociation(pattern);
                    for (var _i = 0, associations_1 = associations; _i < associations_1.length; _i++) {
                        var schemaId = associations_1[_i];
                        var id = this.normalizeId(schemaId);
                        fpa.addSchema(id);
                    }
                }
            }
        };
        JSONSchemaService.prototype.addSchemaHandle = function (id, unresolvedSchemaContent) {
            var schemaHandle = new SchemaHandle(this, id, unresolvedSchemaContent);
            this.schemasById[id] = schemaHandle;
            return schemaHandle;
        };
        JSONSchemaService.prototype.getOrAddSchemaHandle = function (id, unresolvedSchemaContent) {
            return this.schemasById[id] || this.addSchemaHandle(id, unresolvedSchemaContent);
        };
        JSONSchemaService.prototype.getOrAddFilePatternAssociation = function (pattern) {
            var fpa = this.filePatternAssociationById[pattern];
            if (!fpa) {
                fpa = new FilePatternAssociation(pattern);
                this.filePatternAssociationById[pattern] = fpa;
                this.filePatternAssociations.push(fpa);
            }
            return fpa;
        };
        JSONSchemaService.prototype.registerExternalSchema = function (uri, filePatterns, unresolvedSchemaContent) {
            if (filePatterns === void 0) { filePatterns = null; }
            var id = this.normalizeId(uri);
            this.registeredSchemasIds[id] = true;
            if (filePatterns) {
                for (var _i = 0, filePatterns_1 = filePatterns; _i < filePatterns_1.length; _i++) {
                    var pattern = filePatterns_1[_i];
                    this.getOrAddFilePatternAssociation(pattern).addSchema(id);
                }
            }
            return unresolvedSchemaContent ? this.addSchemaHandle(id, unresolvedSchemaContent) : this.getOrAddSchemaHandle(id);
        };
        JSONSchemaService.prototype.clearExternalSchemas = function () {
            this.schemasById = {};
            this.filePatternAssociations = [];
            this.filePatternAssociationById = {};
            this.registeredSchemasIds = {};
            for (var id in this.contributionSchemas) {
                this.schemasById[id] = this.contributionSchemas[id];
                this.registeredSchemasIds[id] = true;
            }
            for (var pattern in this.contributionAssociations) {
                var fpa = this.getOrAddFilePatternAssociation(pattern);
                for (var _i = 0, _a = this.contributionAssociations[pattern]; _i < _a.length; _i++) {
                    var schemaId = _a[_i];
                    var id = this.normalizeId(schemaId);
                    fpa.addSchema(id);
                }
            }
        };
        JSONSchemaService.prototype.getResolvedSchema = function (schemaId) {
            var id = this.normalizeId(schemaId);
            var schemaHandle = this.schemasById[id];
            if (schemaHandle) {
                return schemaHandle.getResolvedSchema();
            }
            return this.promise.resolve(null);
        };
        JSONSchemaService.prototype.loadSchema = function (url) {
            if (!this.requestService) {
                var errorMessage = localize('json.schema.norequestservice', 'Unable to load schema from \'{0}\'. No schema request service available', toDisplayString(url));
                return this.promise.resolve(new UnresolvedSchema({}, [errorMessage]));
            }
            return this.requestService(url).then(function (content) {
                if (!content) {
                    var errorMessage = localize('json.schema.nocontent', 'Unable to load schema from \'{0}\': No content.', toDisplayString(url));
                    return new UnresolvedSchema({}, [errorMessage]);
                }
                var schemaContent = {};
                var jsonErrors = [];
                schemaContent = Json.parse(content, jsonErrors);
                var errors = jsonErrors.length ? [localize('json.schema.invalidFormat', 'Unable to parse content from \'{0}\': Parse error at offset {1}.', toDisplayString(url), jsonErrors[0].offset)] : [];
                return new UnresolvedSchema(schemaContent, errors);
            }, function (error) {
                var errorMessage = error.toString();
                var errorSplit = error.toString().split('Error: ');
                if (errorSplit.length > 1) {
                    // more concise error message, URL and context are attached by caller anyways
                    errorMessage = errorSplit[1];
                }
                return new UnresolvedSchema({}, [errorMessage]);
            });
        };
        JSONSchemaService.prototype.resolveSchemaContent = function (schemaToResolve, schemaURL, dependencies) {
            var _this = this;
            var resolveErrors = schemaToResolve.errors.slice(0);
            var schema = schemaToResolve.schema;
            var contextService = this.contextService;
            var findSection = function (schema, path) {
                if (!path) {
                    return schema;
                }
                var current = schema;
                if (path[0] === '/') {
                    path = path.substr(1);
                }
                path.split('/').some(function (part) {
                    current = current[part];
                    return !current;
                });
                return current;
            };
            var merge = function (target, sourceRoot, sourceURI, path) {
                var section = findSection(sourceRoot, path);
                if (section) {
                    for (var key in section) {
                        if (section.hasOwnProperty(key) && !target.hasOwnProperty(key)) {
                            target[key] = section[key];
                        }
                    }
                }
                else {
                    resolveErrors.push(localize('json.schema.invalidref', '$ref \'{0}\' in \'{1}\' can not be resolved.', path, sourceURI));
                }
            };
            var resolveExternalLink = function (node, uri, linkPath, parentSchemaURL, parentSchemaDependencies) {
                if (contextService && !/^\w+:\/\/.*/.test(uri)) {
                    uri = contextService.resolveRelativePath(uri, parentSchemaURL);
                }
                uri = _this.normalizeId(uri);
                var referencedHandle = _this.getOrAddSchemaHandle(uri);
                return referencedHandle.getUnresolvedSchema().then(function (unresolvedSchema) {
                    parentSchemaDependencies[uri] = true;
                    if (unresolvedSchema.errors.length) {
                        var loc = linkPath ? uri + '#' + linkPath : uri;
                        resolveErrors.push(localize('json.schema.problemloadingref', 'Problems loading reference \'{0}\': {1}', loc, unresolvedSchema.errors[0]));
                    }
                    merge(node, unresolvedSchema.schema, uri, linkPath);
                    return resolveRefs(node, unresolvedSchema.schema, uri, referencedHandle.dependencies);
                });
            };
            var resolveRefs = function (node, parentSchema, parentSchemaURL, parentSchemaDependencies) {
                if (!node || typeof node !== 'object') {
                    return Promise.resolve(null);
                }
                var toWalk = [node];
                var seen = [];
                var openPromises = [];
                var collectEntries = function () {
                    var entries = [];
                    for (var _i = 0; _i < arguments.length; _i++) {
                        entries[_i] = arguments[_i];
                    }
                    for (var _a = 0, entries_1 = entries; _a < entries_1.length; _a++) {
                        var entry = entries_1[_a];
                        if (typeof entry === 'object') {
                            toWalk.push(entry);
                        }
                    }
                };
                var collectMapEntries = function () {
                    var maps = [];
                    for (var _i = 0; _i < arguments.length; _i++) {
                        maps[_i] = arguments[_i];
                    }
                    for (var _a = 0, maps_1 = maps; _a < maps_1.length; _a++) {
                        var map = maps_1[_a];
                        if (typeof map === 'object') {
                            for (var key in map) {
                                var entry = map[key];
                                if (typeof entry === 'object') {
                                    toWalk.push(entry);
                                }
                            }
                        }
                    }
                };
                var collectArrayEntries = function () {
                    var arrays = [];
                    for (var _i = 0; _i < arguments.length; _i++) {
                        arrays[_i] = arguments[_i];
                    }
                    for (var _a = 0, arrays_1 = arrays; _a < arrays_1.length; _a++) {
                        var array = arrays_1[_a];
                        if (Array.isArray(array)) {
                            for (var _b = 0, array_1 = array; _b < array_1.length; _b++) {
                                var entry = array_1[_b];
                                if (typeof entry === 'object') {
                                    toWalk.push(entry);
                                }
                            }
                        }
                    }
                };
                var handleRef = function (next) {
                    var seenRefs = [];
                    while (next.$ref) {
                        var ref = next.$ref;
                        var segments = ref.split('#', 2);
                        delete next.$ref;
                        if (segments[0].length > 0) {
                            openPromises.push(resolveExternalLink(next, segments[0], segments[1], parentSchemaURL, parentSchemaDependencies));
                            return;
                        }
                        else {
                            if (seenRefs.indexOf(ref) === -1) {
                                merge(next, parentSchema, parentSchemaURL, segments[1]); // can set next.$ref again, use seenRefs to avoid circle
                                seenRefs.push(ref);
                            }
                        }
                    }
                    collectEntries(next.items, next.additionalProperties, next.not, next.contains, next.propertyNames, next.if, next.then, next.else);
                    collectMapEntries(next.definitions, next.properties, next.patternProperties, next.dependencies);
                    collectArrayEntries(next.anyOf, next.allOf, next.oneOf, next.items);
                };
                while (toWalk.length) {
                    var next = toWalk.pop();
                    if (seen.indexOf(next) >= 0) {
                        continue;
                    }
                    seen.push(next);
                    handleRef(next);
                }
                return _this.promise.all(openPromises);
            };
            return resolveRefs(schema, schema, schemaURL, dependencies).then(function (_) { return new ResolvedSchema(schema, resolveErrors); });
        };
        JSONSchemaService.prototype.getSchemaForResource = function (resource, document) {
            // first use $schema if present
            if (document && document.root && document.root.type === 'object') {
                var schemaProperties = document.root.properties.filter(function (p) { return (p.keyNode.value === '$schema') && p.valueNode && p.valueNode.type === 'string'; });
                if (schemaProperties.length > 0) {
                    var schemeId = Parser.getNodeValue(schemaProperties[0].valueNode);
                    if (schemeId && Strings.startsWith(schemeId, '.') && this.contextService) {
                        schemeId = this.contextService.resolveRelativePath(schemeId, resource);
                    }
                    if (schemeId) {
                        var id = this.normalizeId(schemeId);
                        return this.getOrAddSchemaHandle(id).getResolvedSchema();
                    }
                }
            }
            var seen = Object.create(null);
            var schemas = [];
            for (var _i = 0, _a = this.filePatternAssociations; _i < _a.length; _i++) {
                var entry = _a[_i];
                if (entry.matchesPattern(resource)) {
                    for (var _b = 0, _c = entry.getSchemas(); _b < _c.length; _b++) {
                        var schemaId = _c[_b];
                        if (!seen[schemaId]) {
                            schemas.push(schemaId);
                            seen[schemaId] = true;
                        }
                    }
                }
            }
            if (schemas.length > 0) {
                return this.createCombinedSchema(resource, schemas).getResolvedSchema();
            }
            return this.promise.resolve(null);
        };
        JSONSchemaService.prototype.createCombinedSchema = function (resource, schemaIds) {
            if (schemaIds.length === 1) {
                return this.getOrAddSchemaHandle(schemaIds[0]);
            }
            else {
                var combinedSchemaId = 'schemaservice://combinedSchema/' + encodeURIComponent(resource);
                var combinedSchema = {
                    allOf: schemaIds.map(function (schemaId) { return ({ $ref: schemaId }); })
                };
                return this.addSchemaHandle(combinedSchemaId, combinedSchema);
            }
        };
        return JSONSchemaService;
    }());
    exports.JSONSchemaService = JSONSchemaService;
    function toDisplayString(url) {
        try {
            var uri = vscode_uri_1.default.parse(url);
            if (uri.scheme === 'file') {
                return uri.fsPath;
            }
        }
        catch (e) {
            // ignore
        }
        return url;
    }
});

/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
(function (factory) {
    if (typeof module === "object" && typeof module.exports === "object") {
        var v = factory(require, exports);
        if (v !== undefined) module.exports = v;
    }
    else if (typeof define === "function" && define.amd) {
        define('vscode-json-languageservice/services/jsonValidation',["require", "exports", "./jsonSchemaService", "vscode-languageserver-types", "../jsonLanguageTypes", "vscode-nls"], factory);
    }
})(function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    var jsonSchemaService_1 = require("./jsonSchemaService");
    var vscode_languageserver_types_1 = require("vscode-languageserver-types");
    var jsonLanguageTypes_1 = require("../jsonLanguageTypes");
    var nls = require("vscode-nls");
    var localize = nls.loadMessageBundle();
    var JSONValidation = /** @class */ (function () {
        function JSONValidation(jsonSchemaService, promiseConstructor) {
            this.jsonSchemaService = jsonSchemaService;
            this.promise = promiseConstructor;
            this.validationEnabled = true;
        }
        JSONValidation.prototype.configure = function (raw) {
            if (raw) {
                this.validationEnabled = raw.validate;
                this.commentSeverity = raw.allowComments ? void 0 : vscode_languageserver_types_1.DiagnosticSeverity.Error;
            }
        };
        JSONValidation.prototype.doValidation = function (textDocument, jsonDocument, documentSettings, schema) {
            var _this = this;
            if (!this.validationEnabled) {
                return this.promise.resolve([]);
            }
            var diagnostics = [];
            var added = {};
            var addProblem = function (problem) {
                // remove duplicated messages
                var signature = problem.range.start.line + ' ' + problem.range.start.character + ' ' + problem.message;
                if (!added[signature]) {
                    added[signature] = true;
                    diagnostics.push(problem);
                }
            };
            var getDiagnostics = function (schema) {
                var trailingCommaSeverity = documentSettings ? toDiagnosticSeverity(documentSettings.trailingCommas) : vscode_languageserver_types_1.DiagnosticSeverity.Error;
                var commentSeverity = documentSettings ? toDiagnosticSeverity(documentSettings.comments) : _this.commentSeverity;
                if (schema) {
                    if (schema.errors.length && jsonDocument.root) {
                        var astRoot = jsonDocument.root;
                        var property = astRoot.type === 'object' ? astRoot.properties[0] : null;
                        if (property && property.keyNode.value === '$schema') {
                            var node = property.valueNode || property;
                            var range = vscode_languageserver_types_1.Range.create(textDocument.positionAt(node.offset), textDocument.positionAt(node.offset + node.length));
                            addProblem(vscode_languageserver_types_1.Diagnostic.create(range, schema.errors[0], vscode_languageserver_types_1.DiagnosticSeverity.Warning, jsonLanguageTypes_1.ErrorCode.SchemaResolveError));
                        }
                        else {
                            var range = vscode_languageserver_types_1.Range.create(textDocument.positionAt(astRoot.offset), textDocument.positionAt(astRoot.offset + 1));
                            addProblem(vscode_languageserver_types_1.Diagnostic.create(range, schema.errors[0], vscode_languageserver_types_1.DiagnosticSeverity.Warning, jsonLanguageTypes_1.ErrorCode.SchemaResolveError));
                        }
                    }
                    else {
                        var semanticErrors = jsonDocument.validate(textDocument, schema.schema);
                        if (semanticErrors) {
                            semanticErrors.forEach(addProblem);
                        }
                    }
                    if (schemaAllowsComments(schema.schema)) {
                        trailingCommaSeverity = commentSeverity = void 0;
                    }
                }
                for (var _i = 0, _a = jsonDocument.syntaxErrors; _i < _a.length; _i++) {
                    var p = _a[_i];
                    if (p.code === jsonLanguageTypes_1.ErrorCode.TrailingComma) {
                        if (typeof trailingCommaSeverity !== 'number') {
                            continue;
                        }
                        p.severity = trailingCommaSeverity;
                    }
                    addProblem(p);
                }
                if (typeof commentSeverity === 'number') {
                    var message_1 = localize('InvalidCommentToken', 'Comments are not permitted in JSON.');
                    jsonDocument.comments.forEach(function (c) {
                        addProblem(vscode_languageserver_types_1.Diagnostic.create(c, message_1, commentSeverity, jsonLanguageTypes_1.ErrorCode.CommentNotPermitted));
                    });
                }
                return diagnostics;
            };
            if (schema) {
                var id = schema.id || ('schemaservice://untitled/' + idCounter++);
                return this.jsonSchemaService.resolveSchemaContent(new jsonSchemaService_1.UnresolvedSchema(schema), id, {}).then(function (resolvedSchema) {
                    return getDiagnostics(resolvedSchema);
                });
            }
            return this.jsonSchemaService.getSchemaForResource(textDocument.uri, jsonDocument).then(function (schema) {
                return getDiagnostics(schema);
            });
        };
        return JSONValidation;
    }());
    exports.JSONValidation = JSONValidation;
    var idCounter = 0;
    function schemaAllowsComments(schemaRef) {
        if (schemaRef && typeof schemaRef === 'object') {
            if (schemaRef.allowComments) {
                return true;
            }
            if (schemaRef.allOf) {
                return schemaRef.allOf.some(schemaAllowsComments);
            }
        }
        return false;
    }
    function toDiagnosticSeverity(severityLevel) {
        switch (severityLevel) {
            case 'error': return vscode_languageserver_types_1.DiagnosticSeverity.Error;
            case 'warning': return vscode_languageserver_types_1.DiagnosticSeverity.Warning;
            case 'ignore': return void 0;
        }
        return void 0;
    }
});

/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
(function (factory) {
    if (typeof module === "object" && typeof module.exports === "object") {
        var v = factory(require, exports);
        if (v !== undefined) module.exports = v;
    }
    else if (typeof define === "function" && define.amd) {
        define('vscode-json-languageservice/utils/colors',["require", "exports"], factory);
    }
})(function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    var Digit0 = 48;
    var Digit9 = 57;
    var A = 65;
    var a = 97;
    var f = 102;
    function hexDigit(charCode) {
        if (charCode < Digit0) {
            return 0;
        }
        if (charCode <= Digit9) {
            return charCode - Digit0;
        }
        if (charCode < a) {
            charCode += (a - A);
        }
        if (charCode >= a && charCode <= f) {
            return charCode - a + 10;
        }
        return 0;
    }
    exports.hexDigit = hexDigit;
    function colorFromHex(text) {
        if (text[0] !== '#') {
            return null;
        }
        switch (text.length) {
            case 4:
                return {
                    red: (hexDigit(text.charCodeAt(1)) * 0x11) / 255.0,
                    green: (hexDigit(text.charCodeAt(2)) * 0x11) / 255.0,
                    blue: (hexDigit(text.charCodeAt(3)) * 0x11) / 255.0,
                    alpha: 1
                };
            case 5:
                return {
                    red: (hexDigit(text.charCodeAt(1)) * 0x11) / 255.0,
                    green: (hexDigit(text.charCodeAt(2)) * 0x11) / 255.0,
                    blue: (hexDigit(text.charCodeAt(3)) * 0x11) / 255.0,
                    alpha: (hexDigit(text.charCodeAt(4)) * 0x11) / 255.0,
                };
            case 7:
                return {
                    red: (hexDigit(text.charCodeAt(1)) * 0x10 + hexDigit(text.charCodeAt(2))) / 255.0,
                    green: (hexDigit(text.charCodeAt(3)) * 0x10 + hexDigit(text.charCodeAt(4))) / 255.0,
                    blue: (hexDigit(text.charCodeAt(5)) * 0x10 + hexDigit(text.charCodeAt(6))) / 255.0,
                    alpha: 1
                };
            case 9:
                return {
                    red: (hexDigit(text.charCodeAt(1)) * 0x10 + hexDigit(text.charCodeAt(2))) / 255.0,
                    green: (hexDigit(text.charCodeAt(3)) * 0x10 + hexDigit(text.charCodeAt(4))) / 255.0,
                    blue: (hexDigit(text.charCodeAt(5)) * 0x10 + hexDigit(text.charCodeAt(6))) / 255.0,
                    alpha: (hexDigit(text.charCodeAt(7)) * 0x10 + hexDigit(text.charCodeAt(8))) / 255.0
                };
        }
        return null;
    }
    exports.colorFromHex = colorFromHex;
    function colorFrom256RGB(red, green, blue, alpha) {
        if (alpha === void 0) { alpha = 1.0; }
        return {
            red: red / 255.0,
            green: green / 255.0,
            blue: blue / 255.0,
            alpha: alpha
        };
    }
    exports.colorFrom256RGB = colorFrom256RGB;
});

/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
(function (factory) {
    if (typeof module === "object" && typeof module.exports === "object") {
        var v = factory(require, exports);
        if (v !== undefined) module.exports = v;
    }
    else if (typeof define === "function" && define.amd) {
        define('vscode-json-languageservice/services/jsonDocumentSymbols',["require", "exports", "../parser/jsonParser", "../utils/strings", "../utils/colors", "vscode-languageserver-types"], factory);
    }
})(function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    var Parser = require("../parser/jsonParser");
    var Strings = require("../utils/strings");
    var colors_1 = require("../utils/colors");
    var vscode_languageserver_types_1 = require("vscode-languageserver-types");
    var JSONDocumentSymbols = /** @class */ (function () {
        function JSONDocumentSymbols(schemaService) {
            this.schemaService = schemaService;
        }
        JSONDocumentSymbols.prototype.findDocumentSymbols = function (document, doc) {
            var _this = this;
            var root = doc.root;
            if (!root) {
                return null;
            }
            // special handling for key bindings
            var resourceString = document.uri;
            if ((resourceString === 'vscode://defaultsettings/keybindings.json') || Strings.endsWith(resourceString.toLowerCase(), '/user/keybindings.json')) {
                if (root.type === 'array') {
                    var result_1 = [];
                    root.items.forEach(function (item) {
                        if (item.type === 'object') {
                            for (var _i = 0, _a = item.properties; _i < _a.length; _i++) {
                                var property = _a[_i];
                                if (property.keyNode.value === 'key') {
                                    if (property.valueNode) {
                                        if (property.valueNode) {
                                            var location = vscode_languageserver_types_1.Location.create(document.uri, getRange(document, item));
                                            result_1.push({ name: Parser.getNodeValue(property.valueNode), kind: vscode_languageserver_types_1.SymbolKind.Function, location: location });
                                        }
                                        return;
                                    }
                                }
                            }
                        }
                    });
                    return result_1;
                }
            }
            var collectOutlineEntries = function (result, node, containerName) {
                if (node.type === 'array') {
                    node.items.forEach(function (node) { return collectOutlineEntries(result, node, containerName); });
                }
                else if (node.type === 'object') {
                    node.properties.forEach(function (property) {
                        var location = vscode_languageserver_types_1.Location.create(document.uri, getRange(document, property));
                        var valueNode = property.valueNode;
                        if (valueNode) {
                            var childContainerName = containerName ? containerName + '.' + property.keyNode.value : property.keyNode.value;
                            result.push({ name: _this.getKeyLabel(property), kind: _this.getSymbolKind(valueNode.type), location: location, containerName: containerName });
                            collectOutlineEntries(result, valueNode, childContainerName);
                        }
                    });
                }
                return result;
            };
            var result = collectOutlineEntries([], root, void 0);
            return result;
        };
        JSONDocumentSymbols.prototype.findDocumentSymbols2 = function (document, doc) {
            var _this = this;
            var root = doc.root;
            if (!root) {
                return null;
            }
            // special handling for key bindings
            var resourceString = document.uri;
            if ((resourceString === 'vscode://defaultsettings/keybindings.json') || Strings.endsWith(resourceString.toLowerCase(), '/user/keybindings.json')) {
                if (root.type === 'array') {
                    var result_2 = [];
                    root.items.forEach(function (item) {
                        if (item.type === 'object') {
                            for (var _i = 0, _a = item.properties; _i < _a.length; _i++) {
                                var property = _a[_i];
                                if (property.keyNode.value === 'key') {
                                    if (property.valueNode) {
                                        var range = getRange(document, item);
                                        var selectionRange = getRange(document, property.keyNode);
                                        result_2.push({ name: Parser.getNodeValue(property.valueNode), kind: vscode_languageserver_types_1.SymbolKind.Function, range: range, selectionRange: selectionRange });
                                    }
                                    return;
                                }
                            }
                        }
                    });
                    return result_2;
                }
            }
            var collectOutlineEntries = function (result, node) {
                if (node.type === 'array') {
                    node.items.forEach(function (node, index) {
                        if (node) {
                            var range = getRange(document, node);
                            var selectionRange = range;
                            var name = String(index);
                            var children = collectOutlineEntries([], node);
                            result.push({ name: name, kind: _this.getSymbolKind(node.type), range: range, selectionRange: selectionRange, children: children });
                        }
                    });
                }
                else if (node.type === 'object') {
                    node.properties.forEach(function (property) {
                        var valueNode = property.valueNode;
                        if (valueNode) {
                            var range = getRange(document, property);
                            var selectionRange = getRange(document, property.keyNode);
                            var children = collectOutlineEntries([], valueNode);
                            result.push({ name: _this.getKeyLabel(property), kind: _this.getSymbolKind(valueNode.type), range: range, selectionRange: selectionRange, children: children });
                        }
                    });
                }
                return result;
            };
            var result = collectOutlineEntries([], root);
            return result;
        };
        JSONDocumentSymbols.prototype.getSymbolKind = function (nodeType) {
            switch (nodeType) {
                case 'object':
                    return vscode_languageserver_types_1.SymbolKind.Module;
                case 'string':
                    return vscode_languageserver_types_1.SymbolKind.String;
                case 'number':
                    return vscode_languageserver_types_1.SymbolKind.Number;
                case 'array':
                    return vscode_languageserver_types_1.SymbolKind.Array;
                case 'boolean':
                    return vscode_languageserver_types_1.SymbolKind.Boolean;
                default: // 'null'
                    return vscode_languageserver_types_1.SymbolKind.Variable;
            }
        };
        JSONDocumentSymbols.prototype.getKeyLabel = function (property) {
            var name = property.keyNode.value;
            if (name) {
                name = name.replace(/[\n]/g, '↵');
            }
            if (name && name.trim()) {
                return name;
            }
            return "\"" + name + "\"";
        };
        JSONDocumentSymbols.prototype.findDocumentColors = function (document, doc) {
            return this.schemaService.getSchemaForResource(document.uri, doc).then(function (schema) {
                var result = [];
                if (schema) {
                    var matchingSchemas = doc.getMatchingSchemas(schema.schema);
                    var visitedNode = {};
                    for (var _i = 0, matchingSchemas_1 = matchingSchemas; _i < matchingSchemas_1.length; _i++) {
                        var s = matchingSchemas_1[_i];
                        if (!s.inverted && s.schema && (s.schema.format === 'color' || s.schema.format === 'color-hex') && s.node && s.node.type === 'string') {
                            var nodeId = String(s.node.offset);
                            if (!visitedNode[nodeId]) {
                                var color = colors_1.colorFromHex(Parser.getNodeValue(s.node));
                                if (color) {
                                    var range = getRange(document, s.node);
                                    result.push({ color: color, range: range });
                                }
                                visitedNode[nodeId] = true;
                            }
                        }
                    }
                }
                return result;
            });
        };
        JSONDocumentSymbols.prototype.getColorPresentations = function (document, doc, color, range) {
            var result = [];
            var red256 = Math.round(color.red * 255), green256 = Math.round(color.green * 255), blue256 = Math.round(color.blue * 255);
            function toTwoDigitHex(n) {
                var r = n.toString(16);
                return r.length !== 2 ? '0' + r : r;
            }
            var label;
            if (color.alpha === 1) {
                label = "#" + toTwoDigitHex(red256) + toTwoDigitHex(green256) + toTwoDigitHex(blue256);
            }
            else {
                label = "#" + toTwoDigitHex(red256) + toTwoDigitHex(green256) + toTwoDigitHex(blue256) + toTwoDigitHex(Math.round(color.alpha * 255));
            }
            result.push({ label: label, textEdit: vscode_languageserver_types_1.TextEdit.replace(range, JSON.stringify(label)) });
            return result;
        };
        return JSONDocumentSymbols;
    }());
    exports.JSONDocumentSymbols = JSONDocumentSymbols;
    function getRange(document, node) {
        return vscode_languageserver_types_1.Range.create(document.positionAt(node.offset), document.positionAt(node.offset + node.length));
    }
});

/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
(function (factory) {
    if (typeof module === "object" && typeof module.exports === "object") {
        var v = factory(require, exports);
        if (v !== undefined) module.exports = v;
    }
    else if (typeof define === "function" && define.amd) {
        define('vscode-json-languageservice/services/configuration',["require", "exports", "vscode-nls"], factory);
    }
})(function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    var nls = require("vscode-nls");
    var localize = nls.loadMessageBundle();
    exports.schemaContributions = {
        schemaAssociations: {},
        schemas: {
            // bundle the schema-schema to include (localized) descriptions
            'http://json-schema.org/draft-04/schema#': {
                'title': localize('schema.json', 'Describes a JSON file using a schema. See json-schema.org for more info.'),
                '$schema': 'http://json-schema.org/draft-04/schema#',
                'definitions': {
                    'schemaArray': {
                        'type': 'array',
                        'minItems': 1,
                        'items': {
                            '$ref': '#'
                        }
                    },
                    'positiveInteger': {
                        'type': 'integer',
                        'minimum': 0
                    },
                    'positiveIntegerDefault0': {
                        'allOf': [
                            {
                                '$ref': '#/definitions/positiveInteger'
                            },
                            {
                                'default': 0
                            }
                        ]
                    },
                    'simpleTypes': {
                        'type': 'string',
                        'enum': [
                            'array',
                            'boolean',
                            'integer',
                            'null',
                            'number',
                            'object',
                            'string'
                        ]
                    },
                    'stringArray': {
                        'type': 'array',
                        'items': {
                            'type': 'string'
                        },
                        'minItems': 1,
                        'uniqueItems': true
                    }
                },
                'type': 'object',
                'properties': {
                    'id': {
                        'type': 'string',
                        'format': 'uri'
                    },
                    '$schema': {
                        'type': 'string',
                        'format': 'uri'
                    },
                    'title': {
                        'type': 'string'
                    },
                    'description': {
                        'type': 'string'
                    },
                    'default': {},
                    'multipleOf': {
                        'type': 'number',
                        'minimum': 0,
                        'exclusiveMinimum': true
                    },
                    'maximum': {
                        'type': 'number'
                    },
                    'exclusiveMaximum': {
                        'type': 'boolean',
                        'default': false
                    },
                    'minimum': {
                        'type': 'number'
                    },
                    'exclusiveMinimum': {
                        'type': 'boolean',
                        'default': false
                    },
                    'maxLength': {
                        'allOf': [
                            {
                                '$ref': '#/definitions/positiveInteger'
                            }
                        ]
                    },
                    'minLength': {
                        'allOf': [
                            {
                                '$ref': '#/definitions/positiveIntegerDefault0'
                            }
                        ]
                    },
                    'pattern': {
                        'type': 'string',
                        'format': 'regex'
                    },
                    'additionalItems': {
                        'anyOf': [
                            {
                                'type': 'boolean'
                            },
                            {
                                '$ref': '#'
                            }
                        ],
                        'default': {}
                    },
                    'items': {
                        'anyOf': [
                            {
                                '$ref': '#'
                            },
                            {
                                '$ref': '#/definitions/schemaArray'
                            }
                        ],
                        'default': {}
                    },
                    'maxItems': {
                        'allOf': [
                            {
                                '$ref': '#/definitions/positiveInteger'
                            }
                        ]
                    },
                    'minItems': {
                        'allOf': [
                            {
                                '$ref': '#/definitions/positiveIntegerDefault0'
                            }
                        ]
                    },
                    'uniqueItems': {
                        'type': 'boolean',
                        'default': false
                    },
                    'maxProperties': {
                        'allOf': [
                            {
                                '$ref': '#/definitions/positiveInteger'
                            }
                        ]
                    },
                    'minProperties': {
                        'allOf': [
                            {
                                '$ref': '#/definitions/positiveIntegerDefault0'
                            }
                        ]
                    },
                    'required': {
                        'allOf': [
                            {
                                '$ref': '#/definitions/stringArray'
                            }
                        ]
                    },
                    'additionalProperties': {
                        'anyOf': [
                            {
                                'type': 'boolean'
                            },
                            {
                                '$ref': '#'
                            }
                        ],
                        'default': {}
                    },
                    'definitions': {
                        'type': 'object',
                        'additionalProperties': {
                            '$ref': '#'
                        },
                        'default': {}
                    },
                    'properties': {
                        'type': 'object',
                        'additionalProperties': {
                            '$ref': '#'
                        },
                        'default': {}
                    },
                    'patternProperties': {
                        'type': 'object',
                        'additionalProperties': {
                            '$ref': '#'
                        },
                        'default': {}
                    },
                    'dependencies': {
                        'type': 'object',
                        'additionalProperties': {
                            'anyOf': [
                                {
                                    '$ref': '#'
                                },
                                {
                                    '$ref': '#/definitions/stringArray'
                                }
                            ]
                        }
                    },
                    'enum': {
                        'type': 'array',
                        'minItems': 1,
                        'uniqueItems': true
                    },
                    'type': {
                        'anyOf': [
                            {
                                '$ref': '#/definitions/simpleTypes'
                            },
                            {
                                'type': 'array',
                                'items': {
                                    '$ref': '#/definitions/simpleTypes'
                                },
                                'minItems': 1,
                                'uniqueItems': true
                            }
                        ]
                    },
                    'format': {
                        'anyOf': [
                            {
                                'type': 'string',
                                'enum': [
                                    'date-time',
                                    'uri',
                                    'email',
                                    'hostname',
                                    'ipv4',
                                    'ipv6',
                                    'regex'
                                ]
                            },
                            {
                                'type': 'string'
                            }
                        ]
                    },
                    'allOf': {
                        'allOf': [
                            {
                                '$ref': '#/definitions/schemaArray'
                            }
                        ]
                    },
                    'anyOf': {
                        'allOf': [
                            {
                                '$ref': '#/definitions/schemaArray'
                            }
                        ]
                    },
                    'oneOf': {
                        'allOf': [
                            {
                                '$ref': '#/definitions/schemaArray'
                            }
                        ]
                    },
                    'not': {
                        'allOf': [
                            {
                                '$ref': '#'
                            }
                        ]
                    }
                },
                'dependencies': {
                    'exclusiveMaximum': [
                        'maximum'
                    ],
                    'exclusiveMinimum': [
                        'minimum'
                    ]
                },
                'default': {}
            },
            'http://json-schema.org/draft-07/schema#': {
                'title': localize('schema.json', 'Describes a JSON file using a schema. See json-schema.org for more info.'),
                'definitions': {
                    'schemaArray': {
                        'type': 'array',
                        'minItems': 1,
                        'items': { '$ref': '#' }
                    },
                    'nonNegativeInteger': {
                        'type': 'integer',
                        'minimum': 0
                    },
                    'nonNegativeIntegerDefault0': {
                        'allOf': [
                            { '$ref': '#/definitions/nonNegativeInteger' },
                            { 'default': 0 }
                        ]
                    },
                    'simpleTypes': {
                        'enum': [
                            'array',
                            'boolean',
                            'integer',
                            'null',
                            'number',
                            'object',
                            'string'
                        ]
                    },
                    'stringArray': {
                        'type': 'array',
                        'items': { 'type': 'string' },
                        'uniqueItems': true,
                        'default': []
                    }
                },
                'type': ['object', 'boolean'],
                'properties': {
                    '$id': {
                        'type': 'string',
                        'format': 'uri-reference'
                    },
                    '$schema': {
                        'type': 'string',
                        'format': 'uri'
                    },
                    '$ref': {
                        'type': 'string',
                        'format': 'uri-reference'
                    },
                    '$comment': {
                        'type': 'string'
                    },
                    'title': {
                        'type': 'string'
                    },
                    'description': {
                        'type': 'string'
                    },
                    'default': true,
                    'readOnly': {
                        'type': 'boolean',
                        'default': false
                    },
                    'examples': {
                        'type': 'array',
                        'items': true
                    },
                    'multipleOf': {
                        'type': 'number',
                        'exclusiveMinimum': 0
                    },
                    'maximum': {
                        'type': 'number'
                    },
                    'exclusiveMaximum': {
                        'type': 'number'
                    },
                    'minimum': {
                        'type': 'number'
                    },
                    'exclusiveMinimum': {
                        'type': 'number'
                    },
                    'maxLength': { '$ref': '#/definitions/nonNegativeInteger' },
                    'minLength': { '$ref': '#/definitions/nonNegativeIntegerDefault0' },
                    'pattern': {
                        'type': 'string',
                        'format': 'regex'
                    },
                    'additionalItems': { '$ref': '#' },
                    'items': {
                        'anyOf': [
                            { '$ref': '#' },
                            { '$ref': '#/definitions/schemaArray' }
                        ],
                        'default': true
                    },
                    'maxItems': { '$ref': '#/definitions/nonNegativeInteger' },
                    'minItems': { '$ref': '#/definitions/nonNegativeIntegerDefault0' },
                    'uniqueItems': {
                        'type': 'boolean',
                        'default': false
                    },
                    'contains': { '$ref': '#' },
                    'maxProperties': { '$ref': '#/definitions/nonNegativeInteger' },
                    'minProperties': { '$ref': '#/definitions/nonNegativeIntegerDefault0' },
                    'required': { '$ref': '#/definitions/stringArray' },
                    'additionalProperties': { '$ref': '#' },
                    'definitions': {
                        'type': 'object',
                        'additionalProperties': { '$ref': '#' },
                        'default': {}
                    },
                    'properties': {
                        'type': 'object',
                        'additionalProperties': { '$ref': '#' },
                        'default': {}
                    },
                    'patternProperties': {
                        'type': 'object',
                        'additionalProperties': { '$ref': '#' },
                        'propertyNames': { 'format': 'regex' },
                        'default': {}
                    },
                    'dependencies': {
                        'type': 'object',
                        'additionalProperties': {
                            'anyOf': [
                                { '$ref': '#' },
                                { '$ref': '#/definitions/stringArray' }
                            ]
                        }
                    },
                    'propertyNames': { '$ref': '#' },
                    'const': true,
                    'enum': {
                        'type': 'array',
                        'items': true,
                        'minItems': 1,
                        'uniqueItems': true
                    },
                    'type': {
                        'anyOf': [
                            { '$ref': '#/definitions/simpleTypes' },
                            {
                                'type': 'array',
                                'items': { '$ref': '#/definitions/simpleTypes' },
                                'minItems': 1,
                                'uniqueItems': true
                            }
                        ]
                    },
                    'format': { 'type': 'string' },
                    'contentMediaType': { 'type': 'string' },
                    'contentEncoding': { 'type': 'string' },
                    'if': { '$ref': '#' },
                    'then': { '$ref': '#' },
                    'else': { '$ref': '#' },
                    'allOf': { '$ref': '#/definitions/schemaArray' },
                    'anyOf': { '$ref': '#/definitions/schemaArray' },
                    'oneOf': { '$ref': '#/definitions/schemaArray' },
                    'not': { '$ref': '#' }
                },
                'default': true
            }
        }
    };
    var descriptions = {
        id: localize('schema.json.id', "A unique identifier for the schema."),
        $schema: localize('schema.json.$schema', "The schema to verify this document against."),
        title: localize('schema.json.title', "A descriptive title of the element."),
        description: localize('schema.json.description', "A long description of the element. Used in hover menus and suggestions."),
        default: localize('schema.json.default', "A default value. Used by suggestions."),
        multipleOf: localize('schema.json.multipleOf', "A number that should cleanly divide the current value (i.e. have no remainder)."),
        maximum: localize('schema.json.maximum', "The maximum numerical value, inclusive by default."),
        exclusiveMaximum: localize('schema.json.exclusiveMaximum', "Makes the maximum property exclusive."),
        minimum: localize('schema.json.minimum', "The minimum numerical value, inclusive by default."),
        exclusiveMinimum: localize('schema.json.exclusiveMininum', "Makes the minimum property exclusive."),
        maxLength: localize('schema.json.maxLength', "The maximum length of a string."),
        minLength: localize('schema.json.minLength', "The minimum length of a string."),
        pattern: localize('schema.json.pattern', "A regular expression to match the string against. It is not implicitly anchored."),
        additionalItems: localize('schema.json.additionalItems', "For arrays, only when items is set as an array. If it is a schema, then this schema validates items after the ones specified by the items array. If it is false, then additional items will cause validation to fail."),
        items: localize('schema.json.items', "For arrays. Can either be a schema to validate every element against or an array of schemas to validate each item against in order (the first schema will validate the first element, the second schema will validate the second element, and so on."),
        maxItems: localize('schema.json.maxItems', "The maximum number of items that can be inside an array. Inclusive."),
        minItems: localize('schema.json.minItems', "The minimum number of items that can be inside an array. Inclusive."),
        uniqueItems: localize('schema.json.uniqueItems', "If all of the items in the array must be unique. Defaults to false."),
        maxProperties: localize('schema.json.maxProperties', "The maximum number of properties an object can have. Inclusive."),
        minProperties: localize('schema.json.minProperties', "The minimum number of properties an object can have. Inclusive."),
        required: localize('schema.json.required', "An array of strings that lists the names of all properties required on this object."),
        additionalProperties: localize('schema.json.additionalProperties', "Either a schema or a boolean. If a schema, then used to validate all properties not matched by 'properties' or 'patternProperties'. If false, then any properties not matched by either will cause this schema to fail."),
        definitions: localize('schema.json.definitions', "Not used for validation. Place subschemas here that you wish to reference inline with $ref."),
        properties: localize('schema.json.properties', "A map of property names to schemas for each property."),
        patternProperties: localize('schema.json.patternProperties', "A map of regular expressions on property names to schemas for matching properties."),
        dependencies: localize('schema.json.dependencies', "A map of property names to either an array of property names or a schema. An array of property names means the property named in the key depends on the properties in the array being present in the object in order to be valid. If the value is a schema, then the schema is only applied to the object if the property in the key exists on the object."),
        enum: localize('schema.json.enum', "The set of literal values that are valid."),
        type: localize('schema.json.type', "Either a string of one of the basic schema types (number, integer, null, array, object, boolean, string) or an array of strings specifying a subset of those types."),
        format: localize('schema.json.format', "Describes the format expected for the value."),
        allOf: localize('schema.json.allOf', "An array of schemas, all of which must match."),
        anyOf: localize('schema.json.anyOf', "An array of schemas, where at least one must match."),
        oneOf: localize('schema.json.oneOf', "An array of schemas, exactly one of which must match."),
        not: localize('schema.json.not', "A schema which must not match."),
        $id: localize('schema.json.$id', "A unique identifier for the schema."),
        $ref: localize('schema.json.$ref', "Reference a definition hosted on any location."),
        $comment: localize('schema.json.$comment', "Comments from schema authors to readers or maintainers of the schema."),
        readOnly: localize('schema.json.readOnly', "Indicates that the value of the instance is managed exclusively by the owning authority."),
        examples: localize('schema.json.examples', "Sample JSON values associated with a particular schema, for the purpose of illustrating usage."),
        contains: localize('schema.json.contains', "An array instance is valid against \"contains\" if at least one of its elements is valid against the given schema."),
        propertyNames: localize('schema.json.propertyNames', "If the instance is an object, this keyword validates if every property name in the instance validates against the provided schema."),
        const: localize('schema.json.const', "An instance validates successfully against this keyword if its value is equal to the value of the keyword."),
        contentMediaType: localize('schema.json.contentMediaType', "Describes the media type of a string property."),
        contentEncoding: localize('schema.json.contentEncoding', "Describes the content encoding of a string property."),
        if: localize('schema.json.if', "The validation outcome of the \"if\" subschema controls which of the \"then\" or \"else\" keywords are evaluated."),
        then: localize('schema.json.then', "The \"if\" subschema is used for validation when the \"if\" subschema succeeds."),
        else: localize('schema.json.else', "The \"else\" subschema is used for validation when the \"if\" subschema fails.")
    };
    for (var schemaName in exports.schemaContributions.schemas) {
        var schema = exports.schemaContributions.schemas[schemaName];
        for (var property in schema.properties) {
            var propertyObject = schema.properties[property];
            if (propertyObject === true) {
                propertyObject = schema.properties[property] = {};
            }
            var description = descriptions[property];
            if (description) {
                propertyObject['description'] = description;
            }
            else {
                console.log(property + ": localize('schema.json." + property + "', \"\")");
            }
        }
    }
});

/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
(function (factory) {
    if (typeof module === "object" && typeof module.exports === "object") {
        var v = factory(require, exports);
        if (v !== undefined) module.exports = v;
    }
    else if (typeof define === "function" && define.amd) {
        define('vscode-json-languageservice/services/jsonFolding',["require", "exports", "vscode-languageserver-types", "jsonc-parser", "../jsonLanguageTypes"], factory);
    }
})(function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    var vscode_languageserver_types_1 = require("vscode-languageserver-types");
    var jsonc_parser_1 = require("jsonc-parser");
    var jsonLanguageTypes_1 = require("../jsonLanguageTypes");
    function getFoldingRanges(document, context) {
        var ranges = [];
        var nestingLevels = [];
        var stack = [];
        var prevStart = -1;
        var scanner = jsonc_parser_1.createScanner(document.getText(), false);
        var token = scanner.scan();
        function addRange(range) {
            ranges.push(range);
            nestingLevels.push(stack.length);
        }
        while (token !== 17 /* EOF */) {
            switch (token) {
                case 1 /* OpenBraceToken */:
                case 3 /* OpenBracketToken */: {
                    var startLine = document.positionAt(scanner.getTokenOffset()).line;
                    var range = { startLine: startLine, endLine: startLine, kind: token === 1 /* OpenBraceToken */ ? 'object' : 'array' };
                    stack.push(range);
                    break;
                }
                case 2 /* CloseBraceToken */:
                case 4 /* CloseBracketToken */: {
                    var kind = token === 2 /* CloseBraceToken */ ? 'object' : 'array';
                    if (stack.length > 0 && stack[stack.length - 1].kind === kind) {
                        var range = stack.pop();
                        var line = document.positionAt(scanner.getTokenOffset()).line;
                        if (range && line > range.startLine + 1 && prevStart !== range.startLine) {
                            range.endLine = line - 1;
                            addRange(range);
                            prevStart = range.startLine;
                        }
                    }
                    break;
                }
                case 13 /* BlockCommentTrivia */: {
                    var startLine = document.positionAt(scanner.getTokenOffset()).line;
                    var endLine = document.positionAt(scanner.getTokenOffset() + scanner.getTokenLength()).line;
                    if (scanner.getTokenError() === 1 /* UnexpectedEndOfComment */ && startLine + 1 < document.lineCount) {
                        scanner.setPosition(document.offsetAt(vscode_languageserver_types_1.Position.create(startLine + 1, 0)));
                    }
                    else {
                        if (startLine < endLine) {
                            addRange({ startLine: startLine, endLine: endLine, kind: jsonLanguageTypes_1.FoldingRangeKind.Comment });
                            prevStart = startLine;
                        }
                    }
                    break;
                }
                case 12 /* LineCommentTrivia */: {
                    var text = document.getText().substr(scanner.getTokenOffset(), scanner.getTokenLength());
                    var m = text.match(/^\/\/\s*#(region\b)|(endregion\b)/);
                    if (m) {
                        var line = document.positionAt(scanner.getTokenOffset()).line;
                        if (m[1]) { // start pattern match
                            var range = { startLine: line, endLine: line, kind: jsonLanguageTypes_1.FoldingRangeKind.Region };
                            stack.push(range);
                        }
                        else {
                            var i = stack.length - 1;
                            while (i >= 0 && stack[i].kind !== jsonLanguageTypes_1.FoldingRangeKind.Region) {
                                i--;
                            }
                            if (i >= 0) {
                                var range = stack[i];
                                stack.length = i;
                                if (line > range.startLine && prevStart !== range.startLine) {
                                    range.endLine = line;
                                    addRange(range);
                                    prevStart = range.startLine;
                                }
                            }
                        }
                    }
                    break;
                }
            }
            token = scanner.scan();
        }
        var rangeLimit = context && context.rangeLimit;
        if (typeof rangeLimit !== 'number' || ranges.length <= rangeLimit) {
            return ranges;
        }
        var counts = [];
        for (var _i = 0, nestingLevels_1 = nestingLevels; _i < nestingLevels_1.length; _i++) {
            var level = nestingLevels_1[_i];
            if (level < 30) {
                counts[level] = (counts[level] || 0) + 1;
            }
        }
        var entries = 0;
        var maxLevel = 0;
        for (var i = 0; i < counts.length; i++) {
            var n = counts[i];
            if (n) {
                if (n + entries > rangeLimit) {
                    maxLevel = i;
                    break;
                }
                entries += n;
            }
        }
        var result = [];
        for (var i = 0; i < ranges.length; i++) {
            var level = nestingLevels[i];
            if (typeof level === 'number') {
                if (level < maxLevel || (level === maxLevel && entries++ < rangeLimit)) {
                    result.push(ranges[i]);
                }
            }
        }
        return result;
    }
    exports.getFoldingRanges = getFoldingRanges;
});

/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
(function (factory) {
    if (typeof module === "object" && typeof module.exports === "object") {
        var v = factory(require, exports);
        if (v !== undefined) module.exports = v;
    }
    else if (typeof define === "function" && define.amd) {
        define('vscode-json-languageservice/services/jsonSelectionRanges',["require", "exports", "vscode-languageserver-types", "jsonc-parser", "../jsonLanguageTypes"], factory);
    }
})(function (require, exports) {
    "use strict";
    Object.defineProperty(exports, "__esModule", { value: true });
    var vscode_languageserver_types_1 = require("vscode-languageserver-types");
    var jsonc_parser_1 = require("jsonc-parser");
    var jsonLanguageTypes_1 = require("../jsonLanguageTypes");
    function getSelectionRanges(document, positions, doc) {
        function getSelectionRange(position) {
            var offset = document.offsetAt(position);
            var node = doc.getNodeFromOffset(offset, true);
            if (!node) {
                return [];
            }
            var result = [];
            while (node) {
                switch (node.type) {
                    case 'string':
                    case 'object':
                    case 'array':
                        // range without ", [ or {
                        var cStart = node.offset + 1, cEnd = node.offset + node.length - 1;
                        if (cStart < cEnd && offset >= cStart && offset <= cEnd) {
                            result.push(newRange(cStart, cEnd));
                        }
                        result.push(newRange(node.offset, node.offset + node.length));
                        break;
                    case 'number':
                    case 'boolean':
                    case 'null':
                    case 'property':
                        result.push(newRange(node.offset, node.offset + node.length));
                        break;
                }
                if (node.type === 'property' || node.parent && node.parent.type === 'array') {
                    var afterCommaOffset = getOffsetAfterNextToken(node.offset + node.length, 5 /* CommaToken */);
                    if (afterCommaOffset !== -1) {
                        result.push(newRange(node.offset, afterCommaOffset));
                    }
                }
                node = node.parent;
            }
            return result;
        }
        function newRange(start, end) {
            return {
                range: vscode_languageserver_types_1.Range.create(document.positionAt(start), document.positionAt(end)),
                kind: jsonLanguageTypes_1.SelectionRangeKind.Declaration
            };
        }
        var scanner = jsonc_parser_1.createScanner(document.getText(), true);
        function getOffsetAfterNextToken(offset, expectedToken) {
            scanner.setPosition(offset);
            var token = scanner.scan();
            if (token === expectedToken) {
                return scanner.getTokenOffset() + scanner.getTokenLength();
            }
            return -1;
        }
        return positions.map(getSelectionRange);
    }
    exports.getSelectionRanges = getSelectionRanges;
});

(function (factory) {
    if (typeof module === "object" && typeof module.exports === "object") {
        var v = factory(require, exports);
        if (v !== undefined) module.exports = v;
    }
    else if (typeof define === "function" && define.amd) {
        define('vscode-json-languageservice/jsonLanguageService',["require", "exports", "vscode-languageserver-types", "./services/jsonCompletion", "./services/jsonHover", "./services/jsonValidation", "./services/jsonDocumentSymbols", "./parser/jsonParser", "./services/configuration", "./services/jsonSchemaService", "./services/jsonFolding", "./services/jsonSelectionRanges", "jsonc-parser", "./jsonLanguageTypes"], factory);
    }
})(function (require, exports) {
    "use strict";
    function __export(m) {
        for (var p in m) if (!exports.hasOwnProperty(p)) exports[p] = m[p];
    }
    Object.defineProperty(exports, "__esModule", { value: true });
    /*---------------------------------------------------------------------------------------------
     *  Copyright (c) Microsoft Corporation. All rights reserved.
     *  Licensed under the MIT License. See License.txt in the project root for license information.
     *--------------------------------------------------------------------------------------------*/
    var vscode_languageserver_types_1 = require("vscode-languageserver-types");
    exports.TextDocument = vscode_languageserver_types_1.TextDocument;
    exports.Position = vscode_languageserver_types_1.Position;
    exports.CompletionItem = vscode_languageserver_types_1.CompletionItem;
    exports.CompletionList = vscode_languageserver_types_1.CompletionList;
    exports.Hover = vscode_languageserver_types_1.Hover;
    exports.Range = vscode_languageserver_types_1.Range;
    exports.SymbolInformation = vscode_languageserver_types_1.SymbolInformation;
    exports.Diagnostic = vscode_languageserver_types_1.Diagnostic;
    exports.TextEdit = vscode_languageserver_types_1.TextEdit;
    exports.FormattingOptions = vscode_languageserver_types_1.FormattingOptions;
    exports.MarkedString = vscode_languageserver_types_1.MarkedString;
    var jsonCompletion_1 = require("./services/jsonCompletion");
    var jsonHover_1 = require("./services/jsonHover");
    var jsonValidation_1 = require("./services/jsonValidation");
    var jsonDocumentSymbols_1 = require("./services/jsonDocumentSymbols");
    var jsonParser_1 = require("./parser/jsonParser");
    var configuration_1 = require("./services/configuration");
    var jsonSchemaService_1 = require("./services/jsonSchemaService");
    var jsonFolding_1 = require("./services/jsonFolding");
    var jsonSelectionRanges_1 = require("./services/jsonSelectionRanges");
    var jsonc_parser_1 = require("jsonc-parser");
    __export(require("./jsonLanguageTypes"));
    function getLanguageService(params) {
        var promise = params.promiseConstructor || Promise;
        var jsonSchemaService = new jsonSchemaService_1.JSONSchemaService(params.schemaRequestService, params.workspaceContext, promise);
        jsonSchemaService.setSchemaContributions(configuration_1.schemaContributions);
        var jsonCompletion = new jsonCompletion_1.JSONCompletion(jsonSchemaService, params.contributions, promise, params.clientCapabilities);
        var jsonHover = new jsonHover_1.JSONHover(jsonSchemaService, params.contributions, promise);
        var jsonDocumentSymbols = new jsonDocumentSymbols_1.JSONDocumentSymbols(jsonSchemaService);
        var jsonValidation = new jsonValidation_1.JSONValidation(jsonSchemaService, promise);
        return {
            configure: function (settings) {
                jsonSchemaService.clearExternalSchemas();
                if (settings.schemas) {
                    settings.schemas.forEach(function (settings) {
                        jsonSchemaService.registerExternalSchema(settings.uri, settings.fileMatch, settings.schema);
                    });
                }
                jsonValidation.configure(settings);
            },
            resetSchema: function (uri) { return jsonSchemaService.onResourceChange(uri); },
            doValidation: jsonValidation.doValidation.bind(jsonValidation),
            parseJSONDocument: function (document) { return jsonParser_1.parse(document, { collectComments: true }); },
            newJSONDocument: function (root, diagnostics) { return jsonParser_1.newJSONDocument(root, diagnostics); },
            doResolve: jsonCompletion.doResolve.bind(jsonCompletion),
            doComplete: jsonCompletion.doComplete.bind(jsonCompletion),
            findDocumentSymbols: jsonDocumentSymbols.findDocumentSymbols.bind(jsonDocumentSymbols),
            findDocumentSymbols2: jsonDocumentSymbols.findDocumentSymbols2.bind(jsonDocumentSymbols),
            findColorSymbols: function (d, s) { return jsonDocumentSymbols.findDocumentColors(d, s).then(function (s) { return s.map(function (s) { return s.range; }); }); },
            findDocumentColors: jsonDocumentSymbols.findDocumentColors.bind(jsonDocumentSymbols),
            getColorPresentations: jsonDocumentSymbols.getColorPresentations.bind(jsonDocumentSymbols),
            doHover: jsonHover.doHover.bind(jsonHover),
            getFoldingRanges: jsonFolding_1.getFoldingRanges,
            getSelectionRanges: jsonSelectionRanges_1.getSelectionRanges,
            format: function (d, r, o) {
                var range = void 0;
                if (r) {
                    var offset = d.offsetAt(r.start);
                    var length = d.offsetAt(r.end) - offset;
                    range = { offset: offset, length: length };
                }
                var options = { tabSize: o ? o.tabSize : 4, insertSpaces: o ? o.insertSpaces : true, eol: '\n' };
                return jsonc_parser_1.format(d.getText(), range, options).map(function (e) {
                    return vscode_languageserver_types_1.TextEdit.replace(vscode_languageserver_types_1.Range.create(d.positionAt(e.offset), d.positionAt(e.offset + e.length)), e.content);
                });
            }
        };
    }
    exports.getLanguageService = getLanguageService;
});

define('vscode-json-languageservice', ['vscode-json-languageservice/jsonLanguageService'], function (main) { return main; });

define('vs/language/json/jsonWorker',["require", "exports", "vscode-json-languageservice", "vscode-languageserver-types"], function (require, exports, jsonService, ls) {
    /*---------------------------------------------------------------------------------------------
     *  Copyright (c) Microsoft Corporation. All rights reserved.
     *  Licensed under the MIT License. See License.txt in the project root for license information.
     *--------------------------------------------------------------------------------------------*/
    'use strict';
    Object.defineProperty(exports, "__esModule", { value: true });
    var defaultSchemaRequestService;
    if (typeof fetch !== 'undefined') {
        defaultSchemaRequestService = function (url) { return fetch(url).then(function (response) { return response.text(); }); };
    }
    var PromiseAdapter = /** @class */ (function () {
        function PromiseAdapter(executor) {
            this.wrapped = new Promise(executor);
        }
        PromiseAdapter.prototype.then = function (onfulfilled, onrejected) {
            var thenable = this.wrapped;
            return thenable.then(onfulfilled, onrejected);
        };
        PromiseAdapter.prototype.getWrapped = function () {
            return this.wrapped;
        };
        PromiseAdapter.resolve = function (v) {
            return Promise.resolve(v);
        };
        PromiseAdapter.reject = function (v) {
            return Promise.reject(v);
        };
        PromiseAdapter.all = function (values) {
            return Promise.all(values);
        };
        return PromiseAdapter;
    }());
    var JSONWorker = /** @class */ (function () {
        function JSONWorker(ctx, createData) {
            this._ctx = ctx;
            this._languageSettings = createData.languageSettings;
            this._languageId = createData.languageId;
            this._languageService = jsonService.getLanguageService({
                schemaRequestService: createData.enableSchemaRequest && defaultSchemaRequestService,
                promiseConstructor: PromiseAdapter
            });
            this._languageService.configure(this._languageSettings);
        }
        JSONWorker.prototype.doValidation = function (uri) {
            var document = this._getTextDocument(uri);
            if (document) {
                var jsonDocument = this._languageService.parseJSONDocument(document);
                return this._languageService.doValidation(document, jsonDocument);
            }
            return Promise.resolve([]);
        };
        JSONWorker.prototype.doComplete = function (uri, position) {
            var document = this._getTextDocument(uri);
            var jsonDocument = this._languageService.parseJSONDocument(document);
            return this._languageService.doComplete(document, position, jsonDocument);
        };
        JSONWorker.prototype.doResolve = function (item) {
            return this._languageService.doResolve(item);
        };
        JSONWorker.prototype.doHover = function (uri, position) {
            var document = this._getTextDocument(uri);
            var jsonDocument = this._languageService.parseJSONDocument(document);
            return this._languageService.doHover(document, position, jsonDocument);
        };
        JSONWorker.prototype.format = function (uri, range, options) {
            var document = this._getTextDocument(uri);
            var textEdits = this._languageService.format(document, range, options);
            return Promise.resolve(textEdits);
        };
        JSONWorker.prototype.resetSchema = function (uri) {
            return Promise.resolve(this._languageService.resetSchema(uri));
        };
        JSONWorker.prototype.findDocumentSymbols = function (uri) {
            var document = this._getTextDocument(uri);
            var jsonDocument = this._languageService.parseJSONDocument(document);
            var symbols = this._languageService.findDocumentSymbols(document, jsonDocument);
            return Promise.resolve(symbols);
        };
        JSONWorker.prototype.findDocumentColors = function (uri) {
            var document = this._getTextDocument(uri);
            var stylesheet = this._languageService.parseJSONDocument(document);
            var colorSymbols = this._languageService.findDocumentColors(document, stylesheet);
            return Promise.resolve(colorSymbols);
        };
        JSONWorker.prototype.getColorPresentations = function (uri, color, range) {
            var document = this._getTextDocument(uri);
            var stylesheet = this._languageService.parseJSONDocument(document);
            var colorPresentations = this._languageService.getColorPresentations(document, stylesheet, color, range);
            return Promise.resolve(colorPresentations);
        };
        JSONWorker.prototype.provideFoldingRanges = function (uri, context) {
            var document = this._getTextDocument(uri);
            var ranges = this._languageService.getFoldingRanges(document, context);
            return Promise.resolve(ranges);
        };
        JSONWorker.prototype._getTextDocument = function (uri) {
            var models = this._ctx.getMirrorModels();
            for (var _i = 0, models_1 = models; _i < models_1.length; _i++) {
                var model = models_1[_i];
                if (model.uri.toString() === uri) {
                    return ls.TextDocument.create(uri, this._languageId, model.version, model.getValue());
                }
            }
            return null;
        };
        return JSONWorker;
    }());
    exports.JSONWorker = JSONWorker;
    function create(ctx, createData) {
        return new JSONWorker(ctx, createData);
    }
    exports.create = create;
});

