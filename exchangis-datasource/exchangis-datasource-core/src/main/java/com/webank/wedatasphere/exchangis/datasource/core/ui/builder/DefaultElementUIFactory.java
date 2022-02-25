package com.webank.wedatasphere.exchangis.datasource.core.ui.builder;

import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.InputElementUI;

import java.lang.annotation.ElementType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * Default element factory
 */
public class DefaultElementUIFactory  implements ElementUIFactory{
    /**
     * Element builders holder
     */
    private Map<Identify, Function<Object, ? extends ElementUI<?>>> builders = new HashMap<>();


    @Override
    @SuppressWarnings("unchecked")
    public <T, R> void register(String type, Function<T, ? extends ElementUI<R>> builder, Class<?> inputType) {
        builders.putIfAbsent(new Identify(type, inputType), (Function<Object, ? extends ElementUI<?>>) builder);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> ElementUI<R> createElement(String type, Object input, Class<?> inputType) {
        Identify identify = new Identify(type, inputType);
        AtomicReference<ElementUI<R>> elementUI = new AtomicReference<>();
        Optional.ofNullable(builders.get(identify)).ifPresent(builder -> {
            elementUI.set((ElementUI<R>) builder.apply(input));
        });
        return elementUI.get();
    }

    /**
     * Identify for element builder
     */
    private static class Identify{

        /**
         * Type
         */
        private final String type;

        /**
         * Input class
         */
        private final Class<?> inputClass;

        public Identify(String type, Class<?> inputClass){
            this.type = type;
            this.inputClass = inputClass;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Identify identify = (Identify) o;
            return Objects.equals(type, identify.type) && Objects.equals(inputClass, identify.inputClass);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, inputClass);
        }

        @Override
        public String toString() {
            return "Identify{" +
                    "type='" + type + '\'' +
                    ", inputClass=" + inputClass.getCanonicalName() +
                    '}';
        }
    }

}
