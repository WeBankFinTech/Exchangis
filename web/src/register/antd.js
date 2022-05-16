import {
  Button,
  Row,
  Col,
  Tag,
  Form,
  Input,
  ConfigProvider,
  Select,
  Checkbox,
  DatePicker,
  TimePicker,
  Dropdown,
  Divider,
  Modal,
  Popconfirm,
  Upload,
  InputNumber,
  Table,
  Spin,
  Breadcrumb,
  Tabs,
  Card,
  Menu,
  Pagination,
  Typography,
  Space,
  message,
  Radio,
  Tree,
  Tooltip,
  Progress,
  Drawer,
  Empty,
  Icon
} from "ant-design-vue";
import formCreate from '@form-create/ant-design-vue'

/**
 * @description 手动注册 antd-vue 组件,达到按需加载目的
 * @description Automatically register components under Button, such as Button.Group
 * @param {ReturnType<typeof createApp>} app 整个应用的实例
 * @returns void
 */
export default function loadComponent(app) {
  app.use(Button);
  app.use(Table);
  app.use(TimePicker);
  app.use(Row);
  app.use(Col);
  app.use(Tag);
  app.use(Form);
  app.use(Input);
  app.use(Dropdown);
  app.use(Divider);
  app.use(ConfigProvider);
  app.use(Select);
  app.use(DatePicker);
  app.use(Checkbox);
  app.use(Modal);
  app.use(Popconfirm);
  app.use(Upload);
  app.use(InputNumber);
  app.use(Spin);
  app.use(Breadcrumb);
  app.use(Tabs);
  app.use(Card);
  app.use(Menu);
  app.use(Pagination);
  app.use(Space);
  app.use(Typography);
  app.use(Radio);
  app.use(Tree);
  app.use(Tooltip);
  app.use(Progress);
  app.use(Drawer);
  app.use(Empty);
  app.use(Icon)
  app.use(formCreate)
}
