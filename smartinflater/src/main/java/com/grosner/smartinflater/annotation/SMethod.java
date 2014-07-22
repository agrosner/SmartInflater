package com.grosner.smartinflater.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by: andrewgrosner
 * Date: 7/8/14.
 * Contributors: {}
 * Description: Indicates that this method will be called at some point during inflation and/or when an
 * action occurs (see {@link com.grosner.smartinflater.utils.MethodNames})
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SMethod {

    /**
     * Allow for a different named method than the view it references.
     * @return
     */
    int id() default 0;
}
