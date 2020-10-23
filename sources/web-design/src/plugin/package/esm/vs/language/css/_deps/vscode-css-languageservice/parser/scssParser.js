/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
'use strict';
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
import * as scssScanner from './scssScanner.js';
import { TokenType } from './cssScanner.js';
import * as cssParser from './cssParser.js';
import * as nodes from './cssNodes.js';
import { SCSSParseError } from './scssErrors.js';
import { ParseError } from './cssErrors.js';
/// <summary>
/// A parser for scss
/// http://sass-lang.com/documentation/file.SASS_REFERENCE.html
/// </summary>
var SCSSParser = /** @class */ (function (_super) {
    __extends(SCSSParser, _super);
    function SCSSParser() {
        return _super.call(this, new scssScanner.SCSSScanner()) || this;
    }
    SCSSParser.prototype._parseStylesheetStatement = function () {
        if (this.peek(TokenType.AtKeyword)) {
            return this._parseWarnAndDebug()
                || this._parseControlStatement()
                || this._parseMixinDeclaration()
                || this._parseMixinContent()
                || this._parseMixinReference() // @include
                || this._parseFunctionDeclaration()
                || _super.prototype._parseStylesheetAtStatement.call(this);
        }
        return this._parseRuleset(true) || this._parseVariableDeclaration();
    };
    SCSSParser.prototype._parseImport = function () {
        if (!this.peekKeyword('@import')) {
            return null;
        }
        var node = this.create(nodes.Import);
        this.consumeToken();
        if (!node.addChild(this._parseURILiteral()) && !node.addChild(this._parseStringLiteral())) {
            return this.finish(node, ParseError.URIOrStringExpected);
        }
        while (this.accept(TokenType.Comma)) {
            if (!node.addChild(this._parseURILiteral()) && !node.addChild(this._parseStringLiteral())) {
                return this.finish(node, ParseError.URIOrStringExpected);
            }
        }
        if (!this.peek(TokenType.SemiColon) && !this.peek(TokenType.EOF)) {
            node.setMedialist(this._parseMediaQueryList());
        }
        return this.finish(node);
    };
    // scss variables: $font-size: 12px;
    SCSSParser.prototype._parseVariableDeclaration = function (panic) {
        if (panic === void 0) { panic = []; }
        if (!this.peek(scssScanner.VariableName)) {
            return null;
        }
        var node = this.create(nodes.VariableDeclaration);
        if (!node.setVariable(this._parseVariable())) {
            return null;
        }
        if (!this.accept(TokenType.Colon)) {
            return this.finish(node, ParseError.ColonExpected);
        }
        node.colonPosition = this.prevToken.offset;
        if (!node.setValue(this._parseExpr())) {
            return this.finish(node, ParseError.VariableValueExpected, [], panic);
        }
        while (this.accept(TokenType.Exclamation)) {
            if (!this.peekRegExp(TokenType.Ident, /^(default|global)$/)) {
                return this.finish(node, ParseError.UnknownKeyword);
            }
            this.consumeToken();
        }
        if (this.peek(TokenType.SemiColon)) {
            node.semicolonPosition = this.token.offset; // not part of the declaration, but useful information for code assist
        }
        return this.finish(node);
    };
    SCSSParser.prototype._parseMediaFeatureName = function () {
        return this._parseFunction() || this._parseIdent() || this._parseVariable(); // first function, the indent
    };
    SCSSParser.prototype._parseKeyframeSelector = function () {
        return this._tryParseKeyframeSelector()
            || this._parseControlStatement(this._parseKeyframeSelector.bind(this))
            || this._parseVariableDeclaration()
            || this._parseMixinContent();
    };
    SCSSParser.prototype._parseVariable = function () {
        if (!this.peek(scssScanner.VariableName)) {
            return null;
        }
        var node = this.create(nodes.Variable);
        this.consumeToken();
        return node;
    };
    SCSSParser.prototype._parseIdent = function (referenceTypes) {
        var _this = this;
        if (!this.peek(TokenType.Ident) && !this.peek(scssScanner.InterpolationFunction) && !this.peekDelim('-')) {
            return null;
        }
        var node = this.create(nodes.Identifier);
        node.referenceTypes = referenceTypes;
        node.isCustomProperty = this.peekRegExp(TokenType.Ident, /^--/);
        var hasContent = false;
        var indentInterpolation = function () {
            var pos = _this.mark();
            if (_this.acceptDelim('-')) {
                if (!_this.hasWhitespace()) {
                    _this.acceptDelim('-');
                }
                if (_this.hasWhitespace()) {
                    _this.restoreAtMark(pos);
                    return null;
                }
            }
            return _this._parseInterpolation();
        };
        while (this.accept(TokenType.Ident) || node.addChild(indentInterpolation()) || (hasContent && (this.acceptDelim('-') || this.accept(TokenType.Num)))) {
            hasContent = true;
            if (this.hasWhitespace()) {
                break;
            }
        }
        return hasContent ? this.finish(node) : null;
    };
    SCSSParser.prototype._parseTerm = function () {
        var term = _super.prototype._parseTerm.call(this);
        if (term) {
            return term;
        }
        term = this.create(nodes.Term);
        if (term.setExpression(this._parseVariable())
            || term.setExpression(this._parseSelectorCombinator())
            || term.setExpression(this._tryParsePrio())) {
            return this.finish(term);
        }
        return null;
    };
    SCSSParser.prototype._parseInterpolation = function () {
        if (this.peek(scssScanner.InterpolationFunction)) {
            var node = this.create(nodes.Interpolation);
            this.consumeToken();
            if (!node.addChild(this._parseExpr()) && !this._parseSelectorCombinator()) {
                if (this.accept(TokenType.CurlyR)) {
                    return this.finish(node);
                }
                return this.finish(node, ParseError.ExpressionExpected);
            }
            if (!this.accept(TokenType.CurlyR)) {
                return this.finish(node, ParseError.RightCurlyExpected);
            }
            return this.finish(node);
        }
        return null;
    };
    SCSSParser.prototype._parseOperator = function () {
        if (this.peek(scssScanner.EqualsOperator) || this.peek(scssScanner.NotEqualsOperator)
            || this.peek(scssScanner.GreaterEqualsOperator) || this.peek(scssScanner.SmallerEqualsOperator)
            || this.peekDelim('>') || this.peekDelim('<')
            || this.peekIdent('and') || this.peekIdent('or')
            || this.peekDelim('%')) {
            var node = this.createNode(nodes.NodeType.Operator);
            this.consumeToken();
            return this.finish(node);
        }
        return _super.prototype._parseOperator.call(this);
    };
    SCSSParser.prototype._parseUnaryOperator = function () {
        if (this.peekIdent('not')) {
            var node = this.create(nodes.Node);
            this.consumeToken();
            return this.finish(node);
        }
        return _super.prototype._parseUnaryOperator.call(this);
    };
    SCSSParser.prototype._parseRuleSetDeclaration = function () {
        if (this.peek(TokenType.AtKeyword)) {
            return this._parseKeyframe() // nested @keyframe
                || this._parseImport() // nested @import
                || this._parseMedia(true) // nested @media
                || this._parseFontFace() // nested @font-face
                || this._parseWarnAndDebug() // @warn, @debug and @error statements
                || this._parseControlStatement() // @if, @while, @for, @each
                || this._parseFunctionDeclaration() // @function
                || this._parseExtends() // @extends
                || this._parseMixinReference() // @include
                || this._parseMixinContent() // @content
                || this._parseMixinDeclaration() // nested @mixin
                || this._parseRuleset(true) // @at-rule
                || this._parseSupports(true); // @supports
        }
        return this._parseVariableDeclaration() // variable declaration
            || this._tryParseRuleset(true) // nested ruleset
            || _super.prototype._parseRuleSetDeclaration.call(this); // try css ruleset declaration as last so in the error case, the ast will contain a declaration
    };
    SCSSParser.prototype._parseDeclaration = function (resyncStopTokens) {
        var node = this.create(nodes.Declaration);
        if (!node.setProperty(this._parseProperty())) {
            return null;
        }
        if (!this.accept(TokenType.Colon)) {
            return this.finish(node, ParseError.ColonExpected, [TokenType.Colon], resyncStopTokens);
        }
        node.colonPosition = this.prevToken.offset;
        var hasContent = false;
        if (node.setValue(this._parseExpr())) {
            hasContent = true;
            node.addChild(this._parsePrio());
        }
        if (this.peek(TokenType.CurlyL)) {
            node.setNestedProperties(this._parseNestedProperties());
        }
        else {
            if (!hasContent) {
                return this.finish(node, ParseError.PropertyValueExpected);
            }
        }
        if (this.peek(TokenType.SemiColon)) {
            node.semicolonPosition = this.token.offset; // not part of the declaration, but useful information for code assist
        }
        return this.finish(node);
    };
    SCSSParser.prototype._parseNestedProperties = function () {
        var node = this.create(nodes.NestedProperties);
        return this._parseBody(node, this._parseDeclaration.bind(this));
    };
    SCSSParser.prototype._parseExtends = function () {
        if (this.peekKeyword('@extend')) {
            var node = this.create(nodes.ExtendsReference);
            this.consumeToken();
            if (!node.getSelectors().addChild(this._parseSimpleSelector())) {
                return this.finish(node, ParseError.SelectorExpected);
            }
            while (this.accept(TokenType.Comma)) {
                node.getSelectors().addChild(this._parseSimpleSelector());
            }
            if (this.accept(TokenType.Exclamation)) {
                if (!this.acceptIdent('optional')) {
                    return this.finish(node, ParseError.UnknownKeyword);
                }
            }
            return this.finish(node);
        }
        return null;
    };
    SCSSParser.prototype._parseSimpleSelectorBody = function () {
        return this._parseSelectorCombinator() || this._parseSelectorPlaceholder() || _super.prototype._parseSimpleSelectorBody.call(this);
    };
    SCSSParser.prototype._parseSelectorCombinator = function () {
        if (this.peekDelim('&')) {
            var node = this.createNode(nodes.NodeType.SelectorCombinator);
            this.consumeToken();
            while (!this.hasWhitespace() && (this.acceptDelim('-') || this.accept(TokenType.Num) || this.accept(TokenType.Dimension) || node.addChild(this._parseIdent()) || this.acceptDelim('&'))) {
                //  support &-foo-1
            }
            return this.finish(node);
        }
        return null;
    };
    SCSSParser.prototype._parseSelectorPlaceholder = function () {
        if (this.peekDelim('%')) {
            var node = this.createNode(nodes.NodeType.SelectorPlaceholder);
            this.consumeToken();
            this._parseIdent();
            return this.finish(node);
        }
        else if (this.peekKeyword('@at-root')) {
            var node = this.createNode(nodes.NodeType.SelectorPlaceholder);
            this.consumeToken();
            return this.finish(node);
        }
        return null;
    };
    SCSSParser.prototype._parseElementName = function () {
        var pos = this.mark();
        var node = _super.prototype._parseElementName.call(this);
        if (node && !this.hasWhitespace() && this.peek(TokenType.ParenthesisL)) { // for #49589
            this.restoreAtMark(pos);
            return null;
        }
        return node;
    };
    SCSSParser.prototype._tryParsePseudoIdentifier = function () {
        return this._parseInterpolation() || _super.prototype._tryParsePseudoIdentifier.call(this); // for #49589
    };
    SCSSParser.prototype._parseWarnAndDebug = function () {
        if (!this.peekKeyword('@debug')
            && !this.peekKeyword('@warn')
            && !this.peekKeyword('@error')) {
            return null;
        }
        var node = this.createNode(nodes.NodeType.Debug);
        this.consumeToken(); // @debug, @warn or @error
        node.addChild(this._parseExpr()); // optional
        return this.finish(node);
    };
    SCSSParser.prototype._parseControlStatement = function (parseStatement) {
        if (parseStatement === void 0) { parseStatement = this._parseRuleSetDeclaration.bind(this); }
        if (!this.peek(TokenType.AtKeyword)) {
            return null;
        }
        return this._parseIfStatement(parseStatement) || this._parseForStatement(parseStatement)
            || this._parseEachStatement(parseStatement) || this._parseWhileStatement(parseStatement);
    };
    SCSSParser.prototype._parseIfStatement = function (parseStatement) {
        if (!this.peekKeyword('@if')) {
            return null;
        }
        return this._internalParseIfStatement(parseStatement);
    };
    SCSSParser.prototype._internalParseIfStatement = function (parseStatement) {
        var node = this.create(nodes.IfStatement);
        this.consumeToken(); // @if or if
        if (!node.setExpression(this._parseExpr(true))) {
            return this.finish(node, ParseError.ExpressionExpected);
        }
        this._parseBody(node, parseStatement);
        if (this.acceptKeyword('@else')) {
            if (this.peekIdent('if')) {
                node.setElseClause(this._internalParseIfStatement(parseStatement));
            }
            else if (this.peek(TokenType.CurlyL)) {
                var elseNode = this.create(nodes.ElseStatement);
                this._parseBody(elseNode, parseStatement);
                node.setElseClause(elseNode);
            }
        }
        return this.finish(node);
    };
    SCSSParser.prototype._parseForStatement = function (parseStatement) {
        if (!this.peekKeyword('@for')) {
            return null;
        }
        var node = this.create(nodes.ForStatement);
        this.consumeToken(); // @for
        if (!node.setVariable(this._parseVariable())) {
            return this.finish(node, ParseError.VariableNameExpected, [TokenType.CurlyR]);
        }
        if (!this.acceptIdent('from')) {
            return this.finish(node, SCSSParseError.FromExpected, [TokenType.CurlyR]);
        }
        if (!node.addChild(this._parseBinaryExpr())) {
            return this.finish(node, ParseError.ExpressionExpected, [TokenType.CurlyR]);
        }
        if (!this.acceptIdent('to') && !this.acceptIdent('through')) {
            return this.finish(node, SCSSParseError.ThroughOrToExpected, [TokenType.CurlyR]);
        }
        if (!node.addChild(this._parseBinaryExpr())) {
            return this.finish(node, ParseError.ExpressionExpected, [TokenType.CurlyR]);
        }
        return this._parseBody(node, parseStatement);
    };
    SCSSParser.prototype._parseEachStatement = function (parseStatement) {
        if (!this.peekKeyword('@each')) {
            return null;
        }
        var node = this.create(nodes.EachStatement);
        this.consumeToken(); // @each
        var variables = node.getVariables();
        if (!variables.addChild(this._parseVariable())) {
            return this.finish(node, ParseError.VariableNameExpected, [TokenType.CurlyR]);
        }
        while (this.accept(TokenType.Comma)) {
            if (!variables.addChild(this._parseVariable())) {
                return this.finish(node, ParseError.VariableNameExpected, [TokenType.CurlyR]);
            }
        }
        this.finish(variables);
        if (!this.acceptIdent('in')) {
            return this.finish(node, SCSSParseError.InExpected, [TokenType.CurlyR]);
        }
        if (!node.addChild(this._parseExpr())) {
            return this.finish(node, ParseError.ExpressionExpected, [TokenType.CurlyR]);
        }
        return this._parseBody(node, parseStatement);
    };
    SCSSParser.prototype._parseWhileStatement = function (parseStatement) {
        if (!this.peekKeyword('@while')) {
            return null;
        }
        var node = this.create(nodes.WhileStatement);
        this.consumeToken(); // @while
        if (!node.addChild(this._parseBinaryExpr())) {
            return this.finish(node, ParseError.ExpressionExpected, [TokenType.CurlyR]);
        }
        return this._parseBody(node, parseStatement);
    };
    SCSSParser.prototype._parseFunctionBodyDeclaration = function () {
        return this._parseVariableDeclaration() || this._parseReturnStatement() || this._parseWarnAndDebug()
            || this._parseControlStatement(this._parseFunctionBodyDeclaration.bind(this));
    };
    SCSSParser.prototype._parseFunctionDeclaration = function () {
        if (!this.peekKeyword('@function')) {
            return null;
        }
        var node = this.create(nodes.FunctionDeclaration);
        this.consumeToken(); // @function
        if (!node.setIdentifier(this._parseIdent([nodes.ReferenceType.Function]))) {
            return this.finish(node, ParseError.IdentifierExpected, [TokenType.CurlyR]);
        }
        if (!this.accept(TokenType.ParenthesisL)) {
            return this.finish(node, ParseError.LeftParenthesisExpected, [TokenType.CurlyR]);
        }
        if (node.getParameters().addChild(this._parseParameterDeclaration())) {
            while (this.accept(TokenType.Comma)) {
                if (this.peek(TokenType.ParenthesisR)) {
                    break;
                }
                if (!node.getParameters().addChild(this._parseParameterDeclaration())) {
                    return this.finish(node, ParseError.VariableNameExpected);
                }
            }
        }
        if (!this.accept(TokenType.ParenthesisR)) {
            return this.finish(node, ParseError.RightParenthesisExpected, [TokenType.CurlyR]);
        }
        return this._parseBody(node, this._parseFunctionBodyDeclaration.bind(this));
    };
    SCSSParser.prototype._parseReturnStatement = function () {
        if (!this.peekKeyword('@return')) {
            return null;
        }
        var node = this.createNode(nodes.NodeType.ReturnStatement);
        this.consumeToken(); // @function
        if (!node.addChild(this._parseExpr())) {
            return this.finish(node, ParseError.ExpressionExpected);
        }
        return this.finish(node);
    };
    SCSSParser.prototype._parseMixinDeclaration = function () {
        if (!this.peekKeyword('@mixin')) {
            return null;
        }
        var node = this.create(nodes.MixinDeclaration);
        this.consumeToken();
        if (!node.setIdentifier(this._parseIdent([nodes.ReferenceType.Mixin]))) {
            return this.finish(node, ParseError.IdentifierExpected, [TokenType.CurlyR]);
        }
        if (this.accept(TokenType.ParenthesisL)) {
            if (node.getParameters().addChild(this._parseParameterDeclaration())) {
                while (this.accept(TokenType.Comma)) {
                    if (this.peek(TokenType.ParenthesisR)) {
                        break;
                    }
                    if (!node.getParameters().addChild(this._parseParameterDeclaration())) {
                        return this.finish(node, ParseError.VariableNameExpected);
                    }
                }
            }
            if (!this.accept(TokenType.ParenthesisR)) {
                return this.finish(node, ParseError.RightParenthesisExpected, [TokenType.CurlyR]);
            }
        }
        return this._parseBody(node, this._parseRuleSetDeclaration.bind(this));
    };
    SCSSParser.prototype._parseParameterDeclaration = function () {
        var node = this.create(nodes.FunctionParameter);
        if (!node.setIdentifier(this._parseVariable())) {
            return null;
        }
        if (this.accept(scssScanner.Ellipsis)) {
            // ok
        }
        if (this.accept(TokenType.Colon)) {
            if (!node.setDefaultValue(this._parseExpr(true))) {
                return this.finish(node, ParseError.VariableValueExpected, [], [TokenType.Comma, TokenType.ParenthesisR]);
            }
        }
        return this.finish(node);
    };
    SCSSParser.prototype._parseMixinContent = function () {
        if (!this.peekKeyword('@content')) {
            return null;
        }
        var node = this.createNode(nodes.NodeType.MixinContent);
        this.consumeToken();
        return this.finish(node);
    };
    SCSSParser.prototype._parseMixinReference = function () {
        if (!this.peekKeyword('@include')) {
            return null;
        }
        var node = this.create(nodes.MixinReference);
        this.consumeToken();
        if (!node.setIdentifier(this._parseIdent([nodes.ReferenceType.Mixin]))) {
            return this.finish(node, ParseError.IdentifierExpected, [TokenType.CurlyR]);
        }
        if (this.accept(TokenType.ParenthesisL)) {
            if (node.getArguments().addChild(this._parseFunctionArgument())) {
                while (this.accept(TokenType.Comma)) {
                    if (this.peek(TokenType.ParenthesisR)) {
                        break;
                    }
                    if (!node.getArguments().addChild(this._parseFunctionArgument())) {
                        return this.finish(node, ParseError.ExpressionExpected);
                    }
                }
            }
            if (!this.accept(TokenType.ParenthesisR)) {
                return this.finish(node, ParseError.RightParenthesisExpected);
            }
        }
        if (this.peek(TokenType.CurlyL)) {
            var content = this.create(nodes.BodyDeclaration);
            this._parseBody(content, this._parseMixinReferenceBodyStatement.bind(this));
            node.setContent(content);
        }
        return this.finish(node);
    };
    SCSSParser.prototype._parseMixinReferenceBodyStatement = function () {
        return this._tryParseKeyframeSelector() || this._parseRuleSetDeclaration();
    };
    SCSSParser.prototype._parseFunctionArgument = function () {
        // [variableName ':'] expression | variableName '...'
        var node = this.create(nodes.FunctionArgument);
        var pos = this.mark();
        var argument = this._parseVariable();
        if (argument) {
            if (!this.accept(TokenType.Colon)) {
                if (this.accept(scssScanner.Ellipsis)) { // optional
                    node.setValue(argument);
                    return this.finish(node);
                }
                else {
                    this.restoreAtMark(pos);
                }
            }
            else {
                node.setIdentifier(argument);
            }
        }
        if (node.setValue(this._parseExpr(true))) {
            this.accept(scssScanner.Ellipsis); // #43746
            node.addChild(this._parsePrio()); // #9859
            return this.finish(node);
        }
        return null;
    };
    SCSSParser.prototype._parseURLArgument = function () {
        var pos = this.mark();
        var node = _super.prototype._parseURLArgument.call(this);
        if (!node || !this.peek(TokenType.ParenthesisR)) {
            this.restoreAtMark(pos);
            var node_1 = this.create(nodes.Node);
            node_1.addChild(this._parseBinaryExpr());
            return this.finish(node_1);
        }
        return node;
    };
    SCSSParser.prototype._parseOperation = function () {
        if (!this.peek(TokenType.ParenthesisL)) {
            return null;
        }
        var node = this.create(nodes.Node);
        this.consumeToken();
        while (node.addChild(this._parseListElement())) {
            this.accept(TokenType.Comma); // optional
        }
        if (!this.accept(TokenType.ParenthesisR)) {
            return this.finish(node, ParseError.RightParenthesisExpected);
        }
        return this.finish(node);
    };
    SCSSParser.prototype._parseListElement = function () {
        var node = this.create(nodes.ListEntry);
        var child = this._parseBinaryExpr();
        if (!child) {
            return null;
        }
        if (this.accept(TokenType.Colon)) {
            node.setKey(child);
            if (!node.setValue(this._parseBinaryExpr())) {
                return this.finish(node, ParseError.ExpressionExpected);
            }
        }
        else {
            node.setValue(child);
        }
        return this.finish(node);
    };
    return SCSSParser;
}(cssParser.Parser));
export { SCSSParser };
