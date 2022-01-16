package com.webank.wedatasphere.exchangis.job.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

/**
 * Utils of type generic
 */
public class TypeGenericUtils {

    public static Class<?> getActualTypeFormGenericClass(Class<?> genericClass, Class<?> abstractClass,
                                                         int position){
        Map<String, Type> typeVariableReflect = new HashMap<>();
        Map<Class<?>, Type[]> classTypeVariableMap = new HashMap<>();
        Queue<Class<?>> traverseQueue = new LinkedList<>();
        Type[] classTypes = null;
        Class<?> currentClass;
        traverseQueue.offer(genericClass);
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
            if (Objects.equals(currentClass, abstractClass)){
                break;
            }
            //Just traverse the superclass ignore interfaces
            Type superclassType = currentClass.getGenericSuperclass();
            if (Objects.nonNull(superclassType) && superclassType instanceof ParameterizedType) {
                Type[] actualTypes = ((ParameterizedType) superclassType).getActualTypeArguments();
                for (int i = 0 ; i < actualTypes.length; i++){
                    Type actualType =  actualTypes[i];
                    if (actualType instanceof TypeVariable){
                        actualTypes[i] = typeVariableReflect.getOrDefault(actualType.getTypeName(), actualType);
                    }
                }
                classTypeVariableMap.put(currentClass.getSuperclass(), actualTypes);
            }
            Optional.ofNullable(currentClass.getSuperclass()).ifPresent(traverseQueue::offer);
        }
        if (Objects.nonNull(classTypes) && classTypes.length > position){
            return (Class<?>)classTypes[position];
        }
        return null;
    }
}
