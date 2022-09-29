<template>
  <div class="field-map-wrap">
    <div class="fm-r">
      <div class="main-header" @click="showInfo">
        <span style="margin-right: 8px; color: rgba(0, 0, 0, 0.45)">
          <RightOutlined v-if="!isFold" />
          <DownOutlined v-else />
        </span>
        <span style="display: inline-block;width: 140px;"></span>
      </div>
      <div class="main-content" v-show="isFold">
        <monaco ref="monaco" :opts="opts" :jobId="jobId" :monacoVal="originVal"
          @change="changeValue"></monaco>
      </div>
    </div>
  </div>
</template>
<script>
import monaco from './monacoEditor.vue';
import { message } from 'ant-design-vue';
import { RightOutlined, DownOutlined } from '@ant-design/icons-vue';
import {
  getProcessor,
  getTemplate,
  saveProcessor,
  updateProcessor,
} from '@/common/service';

export default {
  components: {
    monaco,
    RightOutlined,
    DownOutlined,
  },
  props: {
    procCodeId: {
      type: String,
      default: '',
    },
    jobId: {
      type: String,
      default: '',
    },
    copyCodeId: {
      type: String,
      default: '',
    }
  },
  data() {
    return {
      isFold: false,
      proId: this.procCodeId,
      opts: {
        value: '',
        readOnly: false, // 是否可编辑 // 是否为只读模式
        language: 'java', // 语言类型
        theme: 'vs-dark', // 编辑器主题
        acceptSuggestionOnCommitCharacter: true, // 接受关于提交字符的建议
        acceptSuggestionOnEnter: 'on', // 接受输入建议 "on" | "off" | "smart" //-如果设置off 编辑器上的代码补全显示了,但却不补上
        accessibilityPageSize: 10, // 辅助功能页面大小 Number 说明：控制编辑器中可由屏幕阅读器读出的行数。警告：这对大于默认值的数字具有性能含义。
        accessibilitySupport: 'off', // 辅助功能支持 控制编辑器是否应在为屏幕阅读器优化的模式下运行。
        autoClosingBrackets: 'always', // 是否自动添加结束括号(包括中括号) "always" | "languageDefined" | "beforeWhitespace" | "never"
        autoClosingDelete: 'always', // 是否自动删除结束括号(包括中括号) "always" | "never" | "auto"
        autoClosingOvertype: 'always', // 是否关闭改写 即使用insert模式时是覆盖后面的文字还是不覆盖后面的文字 "always" | "never" | "auto"
        autoClosingQuotes: 'always', // 是否自动添加结束的单引号 双引号 "always" | "languageDefined" | "beforeWhitespace" | "never"
        autoIndent: 'None', // 控制编辑器在用户键入、粘贴、移动或缩进行时是否应自动调整缩进
        automaticLayout: true, // 自动布局
        foldingStrategy: 'indentation', // 折叠方式  auto | indentation
        codeLens: false, // 是否显示codeLens 通过 CodeLens，你可以在专注于工作的同时了解代码所发生的情况 – 而无需离开编辑器。 可以查找代码引用、代码更改、关联的 Bug、工作项、代码评审和单元测试。
        codeLensFontFamily: '', // codeLens的字体样式
        codeLensFontSize: 14, // codeLens的字体大小
        colorDecorators: false, // 呈现内联色彩装饰器和颜色选择器
        comments: {
          ignoreEmptyLines: true, // 插入行注释时忽略空行。默认为真。
          insertSpace: true, // 在行注释标记之后和块注释标记内插入一个空格。默认为真。
        }, // 注释配置
        contextmenu: true, // 启用上下文菜单
        columnSelection: false, // 启用列编辑 按下shift键位然后按↑↓键位可以实现列选择 然后实现列编辑
        autoSurround: 'never', // 是否应自动环绕选择
        copyWithSyntaxHighlighting: true, // 是否应将语法突出显示复制到剪贴板中 即 当你复制到word中是否保持文字高亮颜色
        cursorBlinking: 'Solid', // 光标动画样式
        cursorSmoothCaretAnimation: false, // 是否启用光标平滑插入动画  当你在快速输入文字的时候 光标是直接平滑的移动还是直接"闪现"到当前文字所处位置
        cursorStyle: 'UnderlineThin', // "Block"|"BlockOutline"|"Line"|"LineThin"|"Underline"|"UnderlineThin" 光标样式
        cursorSurroundingLines: 0, // 光标环绕行数 当文字输入超过屏幕时 可以看见右侧滚动条中光标所处位置是在滚动条中间还是顶部还是底部 即光标环绕行数 环绕行数越大 光标在滚动条中位置越居中
        cursorSurroundingLinesStyle: 'all', // "default" | "all" 光标环绕样式
        cursorWidth: 2, // <=25 光标宽度
        minimap: {
          enabled: false, // 是否启用预览图
        }, // 预览图设置
        folding: true, // 是否启用代码折叠
        links: true, // 是否点击链接
        overviewRulerBorder: false, // 是否应围绕概览标尺绘制边框
        renderLineHighlight: 'gutter', // 当前行突出显示方式
        roundedSelection: false, // 选区是否有圆角
        scrollBeyondLastLine: false, // 设置编辑器是否可以滚动到最后一行之后
        fontSize: 14,
      },
      monacoVal: '',
      backupVal: '', // 备份值
      originVal: '', // 原始值
      timeOut: '',
      isSaving: false,
    };
  },
  mounted() {
    this.isFold = true;
    if (this.proId) { // 编辑任务
      this.getProcessor(this.proId); 
    } else if (this.copyCodeId) { // 复制任务才有
      this.getProcessor(this.copyCodeId); 
    } else {
      this.getTemplate(); // 否则获取模板
    }
    this.intervalSave();
  },
  methods: {
    // 内容改变自动获取值
    changeValue(val) {
      console.log('获取编辑内容', val)
      this.monacoVal = val;
    },

    // 折叠内容
    showInfo() {
      this.isFold = !this.isFold;
    },

    // 获取模板
    async getTemplate() {
      const res = await getTemplate();
      this.originVal = res.code;
      this.backupVal = res.code;
      this.monacoVal = res.code;
    },

    // 获取processor内容
    async getProcessor(proId) {
      const res = await getProcessor(proId);
      this.originVal = res.code;
      this.backupVal = res.code;
      this.monacoVal = res.code;
    },

    // 任务保存前执行
    beforeSave() {
      return new Promise(async (resolve, reject) => {
        try {
          this.isSaving = true;
          if (!this.proId) {
            let param = {
              code: this.monacoVal,
              jobId: this.jobId,
            };
            const res = await saveProcessor(param);
            this.proId = res.proc_code_id;
          } else {
            let param = {
              proc_code_id: this.proId,
              code: this.monacoVal,
            };
            await updateProcessor(param);
          }
          const updateProMap = {
            type: 'PROCESSOR',
            code_id: this.proId,
          };
          this.$emit('updateProMap', updateProMap);
          this.isSaving = false;
          resolve(true);
        } catch (err) {
          this.isSaving = false;
          console.log(err);
          resolve(false);
        }
      });
    },

    // 定时更新processor
    intervalSave() {
      console.log('定时任务', this.timeOut);
      clearTimeout(this.timeOut);
      if (
        this.monacoVal &&
        !this.isSaving &&
        this.backupVal !== this.monacoVal
      ) {
        this.isSaving = true;
        this.beforeSave()
          .then((res) => {
            this.backupVal = this.monacoVal;
          })
          .finally(() => {
            this.isSaving = false;
            this.timeOut = setTimeout(() => {
              this.intervalSave();
            }, 10000);
          });
      } else {
        this.timeOut = setTimeout(() => {
          this.intervalSave();
        }, 10000);
      }
    },
  },
  beforeUnmount() {
    clearTimeout(this.timeOut);
  },
};
</script>
<style lang="less" scoped>
@import '../../../common/content.less';
.field-map-wrap {
  display: flex;
  border-bottom: 1px solid #dee4ec;
  border-top: 1px solid #dee4ec;
  min-height: 68px;
  padding: 24px;
  box-sizing: border-box;
}
.fm-l {
  width: 122px;
  .main-header {
    height: 20px;
    font-family: PingFangSC-Medium;
    font-size: 14px;
    color: rgba(0, 0, 0, 0.85);
    font-weight: 500;
  }
}
.fm-r {
  flex: 1;
  display: flex;
  background: inherit;
  background-color: rgba(255, 255, 255, 1);
  box-sizing: border-box;
  border-top: none;
  flex-direction: column;
  .main-header {
    height: 20px;
    font-family: PingFangSC-Medium;
    font-size: 14px;
    color: rgba(0, 0, 0, 0.85);
    font-weight: 500;
  }
  .main-content {
    padding: 24px;
    min-width: 950px;
    max-width: 950px;
    display: flex;
    flex-direction: column;
  }
}
</style>