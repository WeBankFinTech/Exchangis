package com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping.match;

import com.webank.wedatasphere.exchangis.job.server.render.transform.field.FieldColumn;
import com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping.FieldMappingColumn;

import java.util.List;

/**
 * Field match strategy
 */
public interface FieldMatchStrategy {
    /**
     * Strategy name
     * @return string
     */
    String name();
    /**
     * Match entrance
     * @param dependColumns depend columns
     * @param searchColumns search columns
     * @param ignoreUnMatch ignore the unMatch element
     * @return match column list
     */
    List<FieldColumnMatch> match(List<FieldColumn> dependColumns, List<FieldColumn> searchColumns, boolean ignoreUnMatch);

    default int getPriority(){
        return 1;
    }
}
