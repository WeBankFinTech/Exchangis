package com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping.match;

import com.webank.wedatasphere.exchangis.job.server.render.transform.field.FieldColumn;

/**
 * Match the column
 */
public class FieldColumnMatch {

    /**
     * Left match
     */
    private FieldColumn leftMatch;

    /**
     * Right match
     */
    private FieldColumn rightMatch;

    public FieldColumnMatch(FieldColumn leftMatch, FieldColumn rightMatch){
        this.leftMatch = leftMatch;
        this.rightMatch = rightMatch;
    }
    public FieldColumn getLeftMatch() {
        return leftMatch;
    }

    public void setLeftMatch(FieldColumn leftMatch) {
        this.leftMatch = leftMatch;
    }

    public FieldColumn getRightMatch() {
        return rightMatch;
    }

    public void setRightMatch(FieldColumn rightMatch) {
        this.rightMatch = rightMatch;
    }
}
