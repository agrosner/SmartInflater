package com.grosner.smartinflater.handlers;

import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.Method;

/**
 * Created by: andrewgrosner
 * Date: 7/8/14.
 * Contributors: {}
 * Description:
 */
public class OnTouchHandler extends BaseIdToMethodHandler{

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Method method = mMap.get(v.getId());
            if(method!=null){
                method.setAccessible(true);
                try {
                    return (boolean) method.invoke(inObject, event);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return false;
        }
    };


    public OnTouchHandler(Object inObject) {
        super(inObject);
    }

    @Override
    public void handleView(Method method, View view) {
        mMap.put(view.getId(), method);
        view.setOnTouchListener(onTouchListener);
    }

    @Override
    public String getMethodPrefix() {
        return "onTouch";
    }
}
