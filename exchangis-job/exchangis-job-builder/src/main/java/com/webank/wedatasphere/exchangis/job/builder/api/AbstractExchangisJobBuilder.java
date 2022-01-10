package com.webank.wedatasphere.exchangis.job.builder.api;

import com.webank.wedatasphere.exchangis.job.domain.ExchangisJob;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

public abstract class AbstractExchangisJobBuilder<T extends ExchangisJob, E extends ExchangisJob> implements ExchangisJobBuilder<T, E> {
    @Override
    @SuppressWarnings("unchecked")
    public Class<T> inputJob() {
        return (Class<T>) getGenericType(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<E> outputJob() {
        return (Class<E>)getGenericType(1);
    }

    @Override
    public int priority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canBuild(T inputJob) {
        return true;
    }

    public Class<?> getGenericType(int position){
        Map<String, Type> typeVariableReflect = new HashMap<>();
        Map<Class<?>, Type[]> classTypeVariableMap = new HashMap<>();
        Queue<Class<?>> traverseQueue = new LinkedList<>();
        Type[] classTypes = null;
        Class<?> currentClass = null;
        traverseQueue.offer(this.getClass());
        while (!traverseQueue.isEmpty()) {
            currentClass = traverseQueue.poll();
            Type[] typeParameters = currentClass.getTypeParameters();
            if (typeParameters.length > 0) {
                classTypes = classTypeVariableMap.get(currentClass);
                //Ignore the builder which has the parameterType (not resolved)
                if (null == classTypes) {
                    return null;
                }
                for (int i = 0; i < classTypes.length; i++) {
                    typeVariableReflect.put(typeParameters[i].getTypeName(), classTypes[i]);
                }
            }
            if (Objects.equals(currentClass, AbstractExchangisJobBuilder.class)) {
                break;
            }
            //Just traverse the superclass ignore interfaces
            Type superclassType = currentClass.getGenericSuperclass();
            if (Objects.nonNull(superclassType) && superclassType instanceof ParameterizedType) {
                Type[] actualTypes = ((ParameterizedType) superclassType).getActualTypeArguments();
                for (int i = 0 ; i < actualTypes.length; i++){
                    Type actualType =  actualTypes[i];
                    if (actualType instanceof  TypeVariable){
                        actualTypes[i] = typeVariableReflect.getOrDefault(actualType.getTypeName(), actualType);
                    }
                }
                classTypeVariableMap.put(currentClass.getSuperclass(), actualTypes);
            }
            traverseQueue.offer(currentClass.getSuperclass());
        };
        if (Objects.nonNull(classTypes) && classTypes.length > position){
            return (Class<?>)classTypes[position];
        }
        return null;
    }
}
