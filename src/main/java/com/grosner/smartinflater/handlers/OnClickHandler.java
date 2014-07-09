package com.grosner.smartinflater.handlers;

import android.view.View;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by: andrewgrosner
 * Date: 7/8/14.
 * Contributors: {}
 * Description: Provides a default implementation of {@link android.view.View.OnClickListener}'s handler
 */
public class OnClickHandler extends BaseIdToMethodHandler {

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Method method = mMap.get(v.getId());
            try {
                method.setAccessible(true);
                method.invoke(inObject,v);
            } catch (IllegalAccessException e){}
            catch (InvocationTargetException e){
                throw new RuntimeException(e);
            }
            catch (IllegalArgumentException i){
                try {
                    method.invoke(inObject);
                } catch (IllegalAccessException e) {}
                catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    };

    public OnClickHandler(Object inObject) {
        super(inObject);
    }

    @Override
    public void handleView(Method method, View view) {
        mMap.put(view.getId(), method);
        view.setOnClickListener(onClickListener);
    }

    @Override
    public String getMethodPrefix() {
        return "onClick";
    }
}
