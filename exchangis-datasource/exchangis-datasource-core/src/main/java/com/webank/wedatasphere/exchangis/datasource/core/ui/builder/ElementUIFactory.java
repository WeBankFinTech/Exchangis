package com.webank.wedatasphere.exchangis.datasource.core.ui.builder;

import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;

import java.util.function.Function;

/**
 * Element factory
 */
public interface ElementUIFactory {

    /**
     * Register the element builder
     * @param type type
     * @param builder builder
     * @param inputType input type
     * @param <T> T
     * @param <R> R
     */
    <T, R> void register(String type, Function<T, ? extends ElementUI<R>> builder, Class<?> inputType);


    /**
     * Create element
     * @param type type
     * @param input input object
     * @param <R> element value type
     * @return element
     */
    <R>ElementUI<R> createElement(String type, Object input, Class<?> inputType);
}
