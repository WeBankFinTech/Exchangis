<script>
import { defineComponent, h, toRaw } from "vue";

export default defineComponent({
  props: {
    param: Object,
  },
  emits: ["updateInfo"],
  render() {
    const { type, field } = this.param;
    let tmlName = field.split(".").pop();
    if (type === "OPTION") {
      // 下拉框
      const { values, value } = this.param;
      return h(
        "select",
        {
          value: value,
          onChange: ($event) => {
            let res = toRaw(this.param);
            res.value = $event.target.value;
            this.$emit("updateInfo", res);
          },
        },
        values.map((item) => {
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
      return h(
        "input",
        {
          value: this.param.value,
          onChange: ($event) => {
            let res = toRaw(this.param);
            res.value = $event.target.value;
            this.$emit("updateInfo", res);
          },
        },
        ""
      );
    }
  },
});
</script>
