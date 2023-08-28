package com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping.match;

/**
 * Field all match ignore case strategy
 */
public class FieldAllMatchIgnoreCaseStrategy extends FieldAllMatchStrategy{
    public static final String ALL_MATCH_IGNORE_CASE = "ALL_MATCH_IGNORE_CASE";
    @Override
    public String name() {
        return ALL_MATCH_IGNORE_CASE;
    }

    @Override
    protected boolean ignoreCase() {
        return true;
    }
}
