package com.grosner.smartinflater.handlers;

import android.view.View;

import java.lang.reflect.Method;

/**
 * Created by: andrewgrosner
 * Date: 7/8/14.
 * Contributors: {}
 * Description: Provides a standard callback for handling view operations.
 */
public interface SHandler {

    public void handleView(Method method, int methodId, View view);

    public String getMethodPrefix();
}
