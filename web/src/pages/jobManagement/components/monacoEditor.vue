<!--
 * @Description: 
 * @Author: sueRim
 * @Date: 2022-09-14 16:33:03
-->
<template>
  <div ref="container" class="monaco-editor" :style="`height: ${height}px`"></div>
</template>

<script>
import * as monaco from 'monaco-editor';
import { language } from 'monaco-editor/esm/vs/basic-languages/java/java';

const { keywords } = language;
export default {
  name: 'Monaco',
  props: {
    opts: {
      type: Object,
      default() {
        return {};
      },
    },
    height: {
      type: Number,
      default: 400,
    },
  },
  data() {
    return {
      monacoObj: monaco,
      // 主要配置 这里的配置只是 默认基础配置 , 一般在父组件的 data 里配置就可以了  ,子组件这里不配置都可以
      defaultOpts: {
        folding: true, // 是否启用代码折叠
        links: true, // 是否点击链接
        overviewRulerBorder: false, // 是否应围绕概览标尺绘制边框
        renderLineHighlight: 'gutter', // 当前行突出显示方式
        roundedSelection: false, // 选区是否有圆角
        scrollBeyondLastLine: false, // 设置编辑器是否可以滚动到最后一行之后
        readOnly: false, // 是否为只读模式
        theme: 'vs', // vs, hc-black, or vs-dark
      },
    };
  },
  watch: {
    opts: {
      handler(n) {
        this.init();
      },
      deep: true,
    },
  },
  mounted() {
    this.init();
  },
  methods: {
    // 编辑器初始化
    init() {
      this.setSuggestions();
      // 初始化container的内容，销毁之前生成的编辑器
      this.$refs.container.innerHTML = '';
      this.editorOptions = Object.assign(this.defaultOpts, this.opts);
      // 生成编辑器对象 this.$refs.container
      this.monacoEditor = this.monacoObj.editor.create(
        this.$refs.container,
        this.editorOptions
      );
      // 编辑器内容发生改变时触发
      this.monacoEditor.onDidChangeModelContent(() => {
        this.$emit('change', this.monacoEditor.getValue());
      });
    },

    // 设置自定义提示
    setSuggestions() {
      const _that = this;
      _that.monacoObj.languages.registerCompletionItemProvider('java', {
        provideCompletionItems: (model, position) => {
          let suggestions = [..._that.getJavaSuggest()];
          console.log(9999)
          return {
            suggestions,
          };
        },
      });
    },

    // 获取 Java语法提示
    getJavaSuggest() {
      return keywords.map((key) => ({
        label: key,
        kind: monaco.languages.CompletionItemKind.Enum,
        insertText: key,
      }));
    },

    // 供父组件调用手动获取值
    getVal() {
      return this.monacoEditor.getValue();
    },
  },
};
</script>