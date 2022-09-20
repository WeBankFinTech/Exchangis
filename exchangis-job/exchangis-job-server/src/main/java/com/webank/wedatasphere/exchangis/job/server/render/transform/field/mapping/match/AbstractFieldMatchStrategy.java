package com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping.match;

import com.webank.wedatasphere.exchangis.job.server.render.transform.field.FieldColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Abstract field match strategy
 */
public abstract class AbstractFieldMatchStrategy implements FieldMatchStrategy{
    @Override
    public List<FieldColumnMatch> match(List<FieldColumn> dependColumns, List<FieldColumn> searchColumns, boolean ignoreUnMatch) {
        List<FieldColumnMatch> fieldColumnMatches = new ArrayList<>();
        if (Objects.nonNull(dependColumns) && Objects.nonNull(searchColumns)){
            // Convert the search columns list to map
            Map<String, FieldColumn> searchColumnMap = searchColumns.stream()
                    .collect(Collectors.toMap(FieldColumn::getName, fieldColumn -> fieldColumn, (left, right) -> left));
            for(int i = 0; i < dependColumns.size(); i ++){
                FieldColumn dependColumn = dependColumns.get(i);
                FieldColumn matchColumn = match(dependColumn, searchColumnMap);
                if (Objects.nonNull(matchColumn)){
                    fieldColumnMatches.add(new FieldColumnMatch(dependColumn, matchColumn));
                } else if (!ignoreUnMatch){
                    fieldColumnMatches.add(new FieldColumnMatch(dependColumn, searchColumns.get(i % searchColumns.size())));
                }
            }
        }
        return fieldColumnMatches;
    }

    /**
     * Convert search column list to map
     * @param searchColumns search columns
     * @return map
     */
    protected Map<String, FieldColumn> searchColumnListToMap(List<FieldColumn> searchColumns){
        return searchColumns.stream()
                .collect(Collectors.toMap(FieldColumn::getName, fieldColumn -> fieldColumn, (left, right) -> left));
    }
    /**
     * Depend column
     * @param dependColumn depend column
     * @param searchColumns search column map
     * @return match field column
     */
    protected abstract FieldColumn match(FieldColumn dependColumn, Map<String, FieldColumn> searchColumns);
}
