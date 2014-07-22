package com.grosner.smartinflater.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by: andrewgrosner
 * Date: 7/8/14.
 * Contributors: {}
 * Description: Indicates to the inflater that we inject the view corresponding to this field.
 * if an id is specified, we directly take that view, otherwise we take the exact name of the field.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SResource {

    /**
     * Allows for our fields to have a different name, while the actual id is different.
     * @return
     */
    int id() default 0;

    /**
     * Denotes that the field is optional. If the view is not optional, it will throw a
     * @return
     */
    boolean optional() default false;
}
