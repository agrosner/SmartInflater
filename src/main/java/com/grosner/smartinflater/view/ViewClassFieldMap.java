package com.grosner.smartinflater.view;

import com.grosner.smartinflater.annotation.SResource;
import com.grosner.smartinflater.handlers.SGlobalHandlerList;
import com.grosner.smartinflater.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by: andrewgrosner
 * Date: 7/8/14.
 * Contributors: {}
 * Description:
 */
public class ViewClassFieldMap {
    private static HashMap<Class, List<Field>> sClassFieldMap;
    private static HashMap<Class, List<Method>> sClassMethodMap;

    static HashMap<Class, List<Field>> getSharedFieldInstance(){
        if(sClassFieldMap==null){
            sClassFieldMap = new HashMap<>();
        }
        return sClassFieldMap;
    }

    static HashMap<Class, List<Method>> getSharedMethodInstance(){
        if(sClassMethodMap ==null){
            sClassMethodMap = new HashMap<>();
        }
        return sClassMethodMap;
    }

    static List<Field> getFieldMap(Class inDataObject){
        List<Field> fieldList = getSharedFieldInstance().get(inDataObject);
        if( fieldList == null ){
            fieldList =  ReflectionUtils.getFieldsWithAnnotation(inDataObject, SResource.class);
            getSharedFieldInstance().put(inDataObject, fieldList);
        }
        return fieldList;

    }

    static List<Method> getMethodMap(Class inDataObject){
        List<Method> methodList = getSharedMethodInstance().get(inDataObject);
        if(methodList == null){
            methodList = ReflectionUtils.getAllMethods(new ArrayList<Method>(), inDataObject, SGlobalHandlerList.getMethodPrefixes());
            getSharedMethodInstance().put(inDataObject, methodList);
        }
        return methodList;

    }

    static List<Field> getFieldMap(Object object) {
        return getFieldMap(object.getClass());
    }

    static List<Method> getMethodMap(Object object){
        return getMethodMap(object.getClass());
    }
}
