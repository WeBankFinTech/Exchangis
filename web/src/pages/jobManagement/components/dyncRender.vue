<script>
import { defineComponent, h, toRaw, watch, computed } from "vue";

export default defineComponent({
  props: {
    param: Object,
  },
  emits: ["updateInfo"],
  setup(props, context){
    let { type, field , value } = props.param;
    let tmlName = field.split(".").pop();
    const newProps = computed(() => JSON.parse(JSON.stringify(props.param)))
    watch(newProps, (val, oldVal) => {
      value = val.value
      console.log(val, 123123)
    })

    if (type === "OPTION") {
      // 下拉框
      return () => h(
        "select",
        {
          value: value,
          onChange: ($event) => {
            let res = toRaw(props.param);
            res.value = $event.target.value;
            context.emit("updateInfo", res);
          },
        },
        props.param.values.map((item) => {
          return h(
            "option",
            {
              value: item,
            },
            item
          );
        })
      );
    } else {
      // 输入框
      return () => h("div", {}, [
        h(
          "input",
          {
            value: value,
            onChange: ($event) => {
              let res = toRaw(props.param);
              res.value = $event.target.value;
              context.emit("updateInfo", res);
            },
          },
          ""
        ),
        h("span", {}, props.param.unit ? props.param.unit : ""),
      ]);
    }
  }
});
</script>
