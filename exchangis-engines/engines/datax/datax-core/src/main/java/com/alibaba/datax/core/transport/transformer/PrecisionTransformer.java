package com.alibaba.datax.core.transport.transformer;

import com.alibaba.datax.common.element.Column;
import com.alibaba.datax.common.element.Record;
import com.alibaba.datax.common.element.StringColumn;
import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.transformer.Transformer;

import java.math.BigDecimal;
import java.util.Arrays;

public class PrecisionTransformer extends Transformer {
    public PrecisionTransformer() {
        setTransformerName("dx_precision");
    }

    @Override
    public Record evaluate(Record record, Object... paras) {

        int columnIndex;
        int precision;
        try {
            if (paras.length != 2) {
                throw new RuntimeException("dx_precision paras must be 2");
            }

            columnIndex = (Integer) paras[0];
            precision = Integer.valueOf((String) paras[1]);
        } catch (Exception e) {
            throw DataXException.asDataXException(TransformerErrorCode.TRANSFORMER_ILLEGAL_PARAMETER, "paras:" + Arrays.asList(paras).toString() + " => " + e.getMessage());
        }

        Column column = record.getColumn(columnIndex);

        try {
            String oriValue = column.asString();

            //如果字段为空，跳过replace处理
            if (oriValue == null) {
                return record;
            }
            BigDecimal oriNum = new BigDecimal(oriValue);
            BigDecimal zeroNum = new BigDecimal("0");
            if(oriNum.doubleValue() == zeroNum.doubleValue()){
                record.setColumn(columnIndex, new StringColumn("0"));
            }
            else {
                BigDecimal newValue = new BigDecimal(oriValue).setScale(precision, BigDecimal.ROUND_DOWN);
                record.setColumn(columnIndex, new StringColumn(newValue.toString()));
            }
        } catch (Exception e) {
            throw DataXException.asDataXException(TransformerErrorCode.TRANSFORMER_RUN_EXCEPTION, e.getMessage(), e);
        }
        return record;
    }
}
