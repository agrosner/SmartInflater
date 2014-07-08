package com.grosner.smartinflater.handlers;

import android.widget.AdapterView;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by: andrewgrosner
 * Date: 7/8/14.
 * Contributors: {}
 * Description:
 */
public abstract class BaseAdapterToMethodHandler extends BaseHandler{

    protected HashMap<AdapterView, Method> mMap = new HashMap<>();

    public BaseAdapterToMethodHandler(Object inObject) {
        super(inObject);
    }
}
