package com.webank.wedatasphere.exchangis.datasource.core.ui.builder;

import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.InputElementUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.MapElementUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.OptionElementUI;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Default element factory in spring
 */
@Component
public class SpringElementUIFactory extends DefaultElementUIFactory{

    @PostConstruct
    public void init(){
        super.register(ElementUI.Type.MAP.name(), (Function<Map<String, Object>, MapElementUI>) params ->
                setElementValue(new MapElementUI(), params), Map.class);
        super.register(ElementUI.Type.INPUT.name(), (Function<Map<String, Object>, InputElementUI>) params ->
                setElementValue(new InputElementUI(), params), Map.class);
        super.register(ElementUI.Type.OPTION.name(), (Function<Map<String, Object>, OptionElementUI>) params ->
                setElementValue(new OptionElementUI(), params), Map.class);
    }

    /**
     * Set the params into element and return
     * @param element element
     * @param params params Map
     * @param <R> R
     * @return
     */
    private <R extends ElementUI<?>>R setElementValue(R element, Map<String, Object> params){
        element.setValue(params);
        return element;
    }

    public static void main(String[] args){
        SpringElementUIFactory elementUIFactory = new SpringElementUIFactory();
        elementUIFactory.init();
        Map<String, Object> map = new HashMap<>();
        map.putIfAbsent("hello", "world");
        System.out.println(elementUIFactory.createElement(ElementUI.Type.MAP.name(), map, Map.class).getValue());
    }
}
