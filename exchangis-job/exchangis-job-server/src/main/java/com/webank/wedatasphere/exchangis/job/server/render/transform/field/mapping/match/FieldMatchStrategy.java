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
     * @param sourceColumns source columns
     * @param sinkColumns sink columns
     * @return
     */
    List<FieldMappingColumn> match(List<FieldColumn> sourceColumns, List<FieldColumn> sinkColumns);

    default int getPriority(){
        return 1;
    }
}
