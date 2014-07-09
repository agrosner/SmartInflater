package com.grosner.smartinflater.handlers;

import android.view.View;

import com.grosner.smartinflater.utils.MethodNames;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by: andrewgrosner
 * Date: 7/8/14.
 * Contributors: {}
 * Description:
 */
public class OnCreateHandler extends BaseHandler {

    public OnCreateHandler(Object inObject) {
        super(inObject);
    }

    @Override
    public void handleView(Method method, int methodId, View view) {
        try {
            method.setAccessible(true);
            method.invoke(inObject, view);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            //want to pass off crashes to the app so that the programmer sees a problem
            throw new RuntimeException("An exception occured in an onCreate method: "
                    + method.getName() + " for class: " + inObject.getClass(),
                    e.getCause());
        }
    }

    @Override
    public String getMethodPrefix() {
        return "onCreate";
    }
}
