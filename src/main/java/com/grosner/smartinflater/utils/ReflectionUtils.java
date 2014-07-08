package com.grosner.smartinflater.utils;

import com.grosner.smartinflater.annotation.SMethod;
import com.grosner.smartinflater.annotation.SResource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by: andrewgrosner
 * Date: 7/8/14.
 * Contributors: {}
 * Description:
 */
public class ReflectionUtils {


    public static List<Field> getFieldsWithAnnotation(Class inDataObject, Class<?> annotationClass) {
        ArrayList<Field> fields = new ArrayList<>();
        return getFieldsWithAnnotation(fields, inDataObject, annotationClass);
    }

    public static List<Field> getFieldsWithAnnotation(ArrayList<Field> fieldsWithAnnotation, Class type, Class annotationClass) {
        if (type != null) {
            Field[] fields = type.getDeclaredFields();
            Annotation annos;
            for (Field field : fields) {
                annos = field.getAnnotation(annotationClass);
                if (annos != null) {
                    fieldsWithAnnotation.add(field);
                }
            }

            type = type.getSuperclass();
            if (type != null) {
                getFieldsWithAnnotation(fieldsWithAnnotation, type, annotationClass);
            }
        }
        return fieldsWithAnnotation;
    }

    /**
     * Gets all methods that start with any of the inIntendedMethodNames from the specific class
     *
     * @param inIntendedMethodNames
     * @param methods
     * @param type
     * @return methods starting with intendedMethodName
     */
    public static List<Method> getAllMethods(List<Method> methods, Class<?> type, String... inIntendedMethodNames) {
        for (Method method : type.getDeclaredMethods()) {
            if (methodStartsWith(method, inIntendedMethodNames) && method.isAnnotationPresent(SMethod.class)) {
                methods.add(method);
            }
        }
        if (type.getSuperclass() != null && !type.getSuperclass().getName().startsWith("android")) {
            methods = getAllMethods(methods, type.getSuperclass(), inIntendedMethodNames);
        }
        return methods;
    }

    private static boolean methodStartsWith(Method method, String... inMethodNames) {
        for (String methodName : inMethodNames) {
            if (method.getName().startsWith(methodName))
                return true;
        }
        return false;
    }

    public static String getFieldFromMethod(Class inClass, String inMethodName, boolean unused, String... inMethodPrefixToReplace){
        String field = inMethodName;
        for(String methodPrefix: inMethodPrefixToReplace) {
            if (field.startsWith(methodPrefix)) {
                field = field.replaceFirst(methodPrefix, "");
            }
        }
        String firstChar = field.substring(0,1);
        field = field.replaceFirst(firstChar, firstChar.toLowerCase());
        return field;
    }
}
