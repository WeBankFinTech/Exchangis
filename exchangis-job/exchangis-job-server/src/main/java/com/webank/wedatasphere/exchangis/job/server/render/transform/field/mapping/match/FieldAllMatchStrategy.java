package com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping.match;

import com.webank.wedatasphere.exchangis.job.server.render.transform.field.FieldColumn;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Field all match strategy
 */
public class FieldAllMatchStrategy extends AbstractFieldMatchStrategy{

    public static final String ALL_MATCH = "ALL_MATCH";

    @Override
    protected Map<String, FieldColumn> searchColumnListToMap(List<FieldColumn> searchColumns) {
        if (ignoreCase()){
            // Convert the column name to lower case
            return searchColumns.stream()
                    .collect(Collectors.toMap(column -> column.getName().toLowerCase(Locale.ROOT), fieldColumn -> fieldColumn, (left, right) -> left));
        } else {
            return super.searchColumnListToMap(searchColumns);
        }
    }

    @Override
    protected FieldColumn match(FieldColumn dependColumn, Map<String, FieldColumn> searchColumns) {
        String columnName = dependColumn.getName();
        return ignoreCase()? searchColumns.get(columnName.toLowerCase(Locale.ROOT))
                : searchColumns.get(columnName);
    }

    @Override
    public String name() {
        return ALL_MATCH;
    }

    protected boolean ignoreCase(){
        return false;
    }
}
