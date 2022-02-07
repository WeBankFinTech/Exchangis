package com.webank.wedatasphere.exchangis.datasource.core.ui;

import java.util.Collections;
import java.util.List;

public class ExchangisDataSourceParamsUI {

    private List<ElementUI<?>> sources = Collections.emptyList();

    private List<ElementUI<?>> sinks = Collections.emptyList();

    public List<ElementUI<?>> getSources() {
        return sources;
    }

    public void setSources(List<ElementUI<?>> sources) {
        this.sources = sources;
    }

    public List<ElementUI<?>> getSinks() {
        return sinks;
    }

    public void setSinks(List<ElementUI<?>> sinks) {
        this.sinks = sinks;
    }

    public void addSourceUI(ElementUI<?> ui) {
        this.sources.add(ui);
    }

    public void addSinkUI(ElementUI<?> ui) {
        this.sinks.add(ui);
    }

}
