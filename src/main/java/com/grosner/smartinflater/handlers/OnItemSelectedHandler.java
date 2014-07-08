package com.grosner.smartinflater.handlers;

import android.view.View;
import android.widget.AdapterView;

import com.grosner.smartinflater.exception.MethodTypeMistmatchException;
import com.grosner.smartinflater.utils.MethodNames;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by: andrewgrosner
 * Date: 7/8/14.
 * Contributors: {}
 * Description:
 */
public class OnItemSelectedHandler extends BaseAdapterToMethodHandler implements SHandler {

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Method method = mMap.get(parent);
            if(method!=null){
                method.setAccessible(true);
                try{
                    method.invoke(inObject, parent, position);
                } catch (IllegalAccessException e){}
                catch (InvocationTargetException e){
                    throw new RuntimeException(e);
                } catch (IllegalArgumentException i){
                    try {
                        method.invoke(inObject, position);
                    } catch (IllegalAccessException e) {}
                    catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            /**
             * We will pass -1 as being nothing has been selected
             */
            onItemSelected(parent, null, -1, 0);
        }
    };

    public OnItemSelectedHandler(Object inObject) {
        super(inObject);
    }

    @Override
    public void handleView(Method method, int methodId, View view) {
        if(view instanceof AdapterView){
            ((AdapterView) view).setOnItemSelectedListener(onItemSelectedListener);
            mMap.put((AdapterView) view, method);
        } else{
            throw new MethodTypeMistmatchException(view.getClass(), method.getName());
        }
    }

    @Override
    public String getMethodPrefix() {
        return MethodNames.ONITEMSELECTED;
    }
}
