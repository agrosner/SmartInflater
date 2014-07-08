package com.grosner.smartinflater.handlers;

/**
 * Created by: andrewgrosner
 * Date: 7/8/14.
 * Contributors: {}
 * Description:
 */
public abstract class BaseHandler implements SHandler{

    protected Object inObject;

    public BaseHandler(Object inObject){
        this.inObject = inObject;
    }
}
