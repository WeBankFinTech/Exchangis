import * as monaco from './package/esm/vs/editor/editor.main';


monaco.languages.registerCompletionItemProvider('java', {
    provideCompletionItems: () => {
        var suggestions = [{
            label: 'ifelse',
            kind: monaco.languages.CompletionItemKind.Snippet,
            insertText: [
                'if (${1:condition}) {',
                '\t$0',
                '} else {',
                '\t',
                '}'
            ].join('\n'),
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            documentation: 'If-Else Statement'
        } ];
        return { suggestions: suggestions };
    }
});

export default monaco;
