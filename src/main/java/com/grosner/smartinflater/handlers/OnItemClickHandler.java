package com.grosner.smartinflater.handlers;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

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
public class OnItemClickHandler extends BaseAdapterToMethodHandler {

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
    };

    public OnItemClickHandler(Object inObject) {
        super(inObject);
    }

    @Override
    public void handleView(Method method, int methodId, View view) {
        if(view instanceof Spinner){
            throw new RuntimeException("Spinners cannot set onItemClick listeners");
        } else if(view instanceof AdapterView){
            ((AdapterView) view).setOnItemClickListener(onItemClickListener);
            mMap.put((AdapterView) view, method);
        } else{
            throw new MethodTypeMistmatchException(view.getClass(), method.getName());
        }
    }

    @Override
    public String getMethodPrefix() {
        return MethodNames.ONITEMCLICK;
    }
}
