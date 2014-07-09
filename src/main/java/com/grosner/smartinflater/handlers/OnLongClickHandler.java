package com.grosner.smartinflater.handlers;

import android.view.View;

import com.grosner.smartinflater.utils.MethodNames;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by: andrewgrosner
 * Date: 7/8/14.
 * Contributors: {}
 * Description:
 */
public class OnLongClickHandler extends BaseIdToMethodHandler {

    View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Method method = mMap.get(v.getId());
            if(method!=null){
                method.setAccessible(true);
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
            return false;
        }
    };

    public OnLongClickHandler(Object inObject) {
        super(inObject);
    }

    @Override
    public void handleView(Method method, int methodId, View view) {
        mMap.put(methodId, method);
        view.setOnLongClickListener(onLongClickListener);
    }

    @Override
    public String getMethodPrefix() {
        return "onLongClick";
    }
}
