<template>
  <div class="table-warp" style="padding-bottom: 32px;">
    <form-create :rule="rule" v-model:api="fApi" :option="options" v-model="formData"/>
    <a-button type="primary" @click="submit" style="float: right;margin: 0 0 0 10px;">确定</a-button>
    <a-button @click="$emit('cancel')" style="float: right">取消</a-button>
  </div>
</template>
<script>
import _, { merge, mergeWith} from 'lodash-es';
import {getKeyDefine, getDataSourceById} from "@/common/service";
import { request } from "@fesjs/fes";

const type = {
  TEXT: {type: 'input'},
  NUMBER: {type: 'input', props: {type: 'number'}},
  PASSWORD: {type: 'input', props: {type: 'password'}},
  EMAIL: {type: 'input', props: {type: 'email'}},
  DATE: {type: 'input', props: {type: 'date'}},
  TEL: {type: 'input', props: {type: 'tel'}},
  TEXTAREA: {type: 'input', props: {type: 'textarea'}},
  URL: {type: 'input', props: {type: 'url'}},
  FILE: (self, source)=>{
    return {type: 'upload', props: {uploadType: 'file', action: '',allowRemove: true, "maxLength": 1,
      beforeUpload: (file)=>{
        self.file = file;
        source.props.value = file.name;
        return false;
      }}}
  },
  SELECT: {type: 'select', props: {placement: "bottom"}}
}
const typesMap = {
  valueType: (data, source, self)=>{
    if(type[data.valueType]){
      if(typeof type[data.valueType] === 'function'){
        return type[data.valueType](self, source);
      }
      return type[data.valueType]
    }else{
      return {type: data.valueType}
    }
  },
  name: 'title',
  
  defaultValue: 'value',
  // dataSource: 'options',
  dataSource: (data, source, self)=>{
    const fApi = self.fApi;
    if(/^https?:/.test(data.dataSource)){
      request(data.dataSource, {}, {
        method: "GET",
      }).then(result=>{
        delete source.options;
        source.options = result.env_list.map(item=>{
          return {label: item.envName, value: ''+item.id}
        });
        // console.log('self.rule',self.rule)
        fApi.refreshOptions();
      })
      return {options: []}
    }else {
      try {
        return {options: JSON.parse(data.dataSource)}
      } catch (error) {
        return {options: []}
      }
    }
  },
  key: 'field',
  description: (data)=>{
    return {props: {placeholder: data.description}}
  },
  require: (data)=>{
    if(data.require) //&& !data.valueRegex
      return {validate: [{ required: true, message: `请输入${data.name}`, trigger: 'blur' }]}
    else return null
  },
  valueRegex: (data)=>{
    if(data.valueRegex){
      return {validate: [{pattern: new RegExp(data.valueRegex), message: '不符合规则', trigger: 'blur'}]}
    }
    else return null;
  },
  // valueRegex: (data)=>{
  //   if(data.valueRegex)
  //     return {validate: { pattern: new RegExp(data.valueRegex), message: '不符合规则', trigger: 'blur' }}
  //   else return null
  // },
  
  refId: 'refId',
  refValue: 'refValue',
  id: 'id',
}
export default {
  props: {
    data: Object
  },
  data () {
    return {
      sourceConnectData: {},
      fApi: {},
      loading: false,
      formData: {file: 'adn'},
      options: {
        submitBtn: false,
      },
      rule: [],
      defaultRule: [
        {
          type: "input",
          title: this.$t('message.linkis.datasource.sourceName'),
          field: "dataSourceName",
          value: "",
          props: {
            "placeholder": this.$t('message.linkis.datasource.sourceName'),
          },
          validate: [{
            required: true,
            message: `${this.$t('message.linkis.datasource.pleaseInput')}${this.$t('message.linkis.datasource.sourceName')}`,
            trigger: 'blur'
          }],
        },
        {
          type: "input",
          title: this.$t('message.linkis.datasource.sourceDec'),
          field: "dataSourceDesc",
          value: "",
          props: {
            "placeholder": this.$t('message.linkis.datasource.sourceDec'),
          }
        },
        {
          type: "input",
          title: this.$t('message.linkis.datasource.label'),
          field: "labels",
          value: "",
          props: {
            "placeholder": this.$t('message.linkis.datasource.label'),
          }
        }
      ],

    }
  },
  created(){
    this.loading = true;
    
    this.getDataSource(this.data);
    
    getKeyDefine(this.data.type).then((data)=>{
      this.loading = false;
      this.transformData(data.list);
    })
  },
  watch: {
    data: {
      handler (newV) {
        getKeyDefine(this.data.type).then((data)=>{
          this.transformData(data.list);
          this.getDataSource(newV);
        })
      },
      deep: true
    }
  },
  methods: {
    getDataSource(newV){
      if(this.data.id){
        getDataSourceById(newV.id).then(result=>{
          const mConnect = result.info.connectParams;
          this.sourceConnectData = mConnect;
          delete result.info.connectParams;
          //this.dataSrc = { ...result.info,  ...mConnect};
          this.formData = { ...result.info,  ...mConnect};
        })
      }else{
        const connectParams = newV.connectParams;
        delete newV.connectParams;
        this.formData = {
          ...newV,
          dataSourceDesc: '',
          dataSourceName: '',
          labels: '',
          ...connectParams
        };
        //this.dataSrc = {...newV, ...connectParams};
      }
    },
    transformData(keyDefinitions){
      const tempData = [];
      
      keyDefinitions.forEach((obj)=>{
        let item = {};
        Object.keys(obj).forEach((keyName) =>{
          
          switch (typeof typesMap[keyName]) {
            case 'object':
              item = merge({}, item, typesMap[keyName])
              break;
            
            case 'function':
              
              item = mergeWith(item, typesMap[keyName](obj, item, this), function(objValue, srcValue){
                if(_.isArray(objValue)) {
                  return objValue.concat(srcValue);
                }
              });
              break;
            
            case 'string':
              item[typesMap[keyName]] = obj[keyName];
              break;
          }
        });
        tempData.push(item);
      });

      const insertParent = (id, child)=>{
        
        let parent = tempData.find(item=> id==item.id);
        if(parent && child){
          
          if(!parent.control || parent.control.length===0) {
            parent.control = [  //不存在新建
              {
                value: child.refValue,
                rule: [{...child} ]   
              }                                              
            ]
          }else {
            let index = parent.control.findIndex(item=>item.value+'' === child.refValue+'');
            if(index > -1){
              parent.control[index].rule.push({...child})
            }else {
              parent.control.push(
                {
                  value: child.refValue,
                  rule: [{...child} ]   
                }  
              )
            }
            
          }
          
        }
      }
      for(var i =0; i<tempData.length; i++){
        let item = tempData[i];
        if(item.refId && item.refId > 0){
          let children = tempData.splice(i, 1);
          i--;
          insertParent(item.refId, children[0]);
        }
      }
      this.rule =  this.defaultRule.concat(tempData);
      return tempData;
      
    },
    submit(){
      this.fApi.submit((formData, fApi)=>{
        console.log(JSON.stringify(formData))
        this.$emit("submit", JSON.stringify(formData));
      })
    }
  }
}
</script>

<style lang="less" scoped>
  .search-bar {
    .search-item {
      display: flex;
      justify-content: flex-start;
      align-items: center;
      font-size: 14px;
      .lable {
        min-width: 90px;
      // flex-basis: 130px;
        text-align: right;
      }
    }

    .ivu-col {
      display: flex;
      justify-content: center;
    }
  }

  .table-content {
    margin-top: 25px;
  }

  .datasource-type-wrap {
    .project-header {
      height: 32px;

      .header-title {
        font-size: 16px;
        font-weight: bold;
        padding-left: 12px;
        border-left: 3px solid #2d8cf0;
        color: rgba(0, 0, 0, 0.85);
      }

      .header-tool {
        float: right;
        margin-right: 6%;
        color: #2d8cf0;
        cursor: pointer;

        .sort-icon {
          margin-right: 20px;

          .icon {
            margin-left: 5px;
            color: #2d8cf0;
          }
        }

        .search-input {
          width: 200px;
          margin-right: 30px;
        }
      }
    }
  }
  :deep(.ant-form-item-label){
    flex: 0 0 20%;
    max-width: 20%;
  }
</style>