package com.grosner.smartinflater.exception;

/**
 * Created by: andrewgrosner
 * Date: 7/8/14.
 * Contributors: {}
 * Description:
 */
public class SResourceNotFoundException extends RuntimeException{
    public SResourceNotFoundException(String fieldName, Class inClass) {
        super("Field: " + fieldName + " for class " + inClass.getName() + " was not found when inflating.");
    }
}
