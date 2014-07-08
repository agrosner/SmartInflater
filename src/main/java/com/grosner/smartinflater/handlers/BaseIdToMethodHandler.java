package com.grosner.smartinflater.handlers;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by: andrewgrosner
 * Date: 7/8/14.
 * Contributors: {}
 * Description:
 */
public abstract class BaseIdToMethodHandler extends BaseHandler implements SHandler{

    protected HashMap<Integer, Method> mMap = new HashMap<>();


    public BaseIdToMethodHandler(Object inObject) {
        super(inObject);
    }
}
