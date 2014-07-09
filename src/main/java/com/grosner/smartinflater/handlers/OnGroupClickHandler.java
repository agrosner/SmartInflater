package com.grosner.smartinflater.handlers;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by: andrewgrosner
 * Date: 7/9/14.
 * Contributors: {}
 * Description:
 */
public class OnGroupClickHandler extends BaseIdToMethodHandler {

    private ExpandableListView.OnGroupClickListener onGroupClickListener = new ExpandableListView.OnGroupClickListener() {
        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            Method method = mMap.get(v.getId());
            method.setAccessible(true);
            boolean onGroupClick = false;
            try {
                onGroupClick = (boolean) method.invoke(inObject, parent, v, groupPosition);
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
                try {
                    onGroupClick = (boolean) method.invoke(inObject, v, groupPosition);
                } catch (IllegalAccessException e1) {
                } catch (InvocationTargetException e1) {
                    try {
                        onGroupClick = (boolean) method.invoke(inObject, groupPosition);
                    } catch (IllegalAccessException | InvocationTargetException e2) {
                        throw new RuntimeException(e2);
                    }
                }
            }
            return onGroupClick;
        }
    };

    public OnGroupClickHandler(Object inObject) {
        super(inObject);
    }

    @Override
    public void handleView(Method method, View view) {
        if(view instanceof ExpandableListView){
            mMap.put(view.getId(), method);
            ((ExpandableListView) view).setOnGroupClickListener(onGroupClickListener);
        }
    }

    @Override
    public String getMethodPrefix() {
        return "onGroupClick";
    }
}
