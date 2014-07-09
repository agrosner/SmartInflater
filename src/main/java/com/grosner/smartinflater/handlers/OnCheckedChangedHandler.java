package com.grosner.smartinflater.handlers;

import android.view.View;
import android.widget.CompoundButton;

import com.grosner.smartinflater.exception.MethodTypeMistmatchException;
import com.grosner.smartinflater.utils.MethodNames;

import java.lang.reflect.Method;

/**
 * Created by: andrewgrosner
 * Date: 7/8/14.
 * Contributors: {}
 * Description: Provides a default implementation of {@link android.widget.CompoundButton.OnCheckedChangeListener}'s handler
 */
public class OnCheckedChangedHandler extends BaseIdToMethodHandler {

    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Method method = mMap.get(buttonView.getId());
            try{
                method.setAccessible(true);
                method.invoke(inObject, buttonView, isChecked);
            } catch (Throwable t){
                throw new RuntimeException(t);
            }
        }
    };

    public OnCheckedChangedHandler(Object inObject) {
        super(inObject);
    }

    @Override
    public void handleView(Method method, int methodId, View view) {
        if (view instanceof CompoundButton) {
            ((CompoundButton) view).setOnCheckedChangeListener(onCheckedChangeListener);
            mMap.put(methodId, method);
        } else {
            throw new MethodTypeMistmatchException(view.getClass(), method.getName());
        }
    }

    @Override
    public String getMethodPrefix() {
        return "onCheckedChanged";
    }
}
