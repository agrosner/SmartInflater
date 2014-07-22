package com.grosner.smartinflater.exception;

/**
 * Created by: andrewgrosner
 * Date: 7/8/14.
 * Contributors: {}
 * Description:
 */
public class MethodTypeMistmatchException extends RuntimeException{
    public MethodTypeMistmatchException(Class errorClass, String methodName) {
        super("Object of class: " + errorClass.getName() + " cannot invoke " + methodName);
    }
}
