export default {
    data() {
        return {
            options: [
                {label: ',', value: ','},
                {label: ';', value: ','},
                {label: '\\u0001', value: '\\u0001'}
            ]
        }
    },
    created() {
        if (this.subData.fieldDelimiter) {
            if (this.options.some((item) => item.value === this.subData.fieldDelimiter)) return;
            this.options.push({
                label: this.subData.fieldDelimiter,
                value: this.subData.fieldDelimiter
            })
        }
    },
    methods: {
        search(val) {
            if (val) {
                this.options = [
                    {
                        label: val,
                        value: val
                    }
                ];
                this.subData.fieldDelimiter = val;
            } else {
                this.options = [
                    {label: ',', value: ','},
                    {label: ';', value: ','},
                    {label: '\\u0001', value: '\\u0001'}
                ];
                this.subData.fieldDelimiter = '';
            }
        },
        deUnicode(str) {
            var strArray = str.split('\\u');
            if (str.startsWith('\\u')) { strArray = strArray.slice(1, strArray.length); }
            if (str.endsWith('\\u')) { strArray = strArray.slice(0, strArray.length - 1); }
            var rs = '';
            for (var i = 0; i < strArray.length; i++) {
                rs += String.fromCharCode(parseInt(strArray[i], 16));
            }
            return rs;
        }
    }
}