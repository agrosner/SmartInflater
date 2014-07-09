package com.grosner.smartinflater.handlers;

/**
 * Created by: andrewgrosner
 * Date: 7/8/14.
 * Contributors: {}
 * Description: A simple class that holds onto the reference of the class we are inflating in for the duration of its scope.
 */
public abstract class BaseHandler implements SHandler{

    protected Object inObject;

    public BaseHandler(Object inObject){
        this.inObject = inObject;
    }
}
