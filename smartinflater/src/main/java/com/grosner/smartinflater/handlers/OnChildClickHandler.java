package com.grosner.smartinflater.handlers;

import android.view.View;
import android.widget.ExpandableListView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by: andrewgrosner
 * Date: 7/9/14.
 * Contributors: {}
 * Description:
 */
public class OnChildClickHandler extends BaseIdToMethodHandler {

    private ExpandableListView.OnChildClickListener onChildClickListener = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            Method method = mMap.get(parent.getId());
            method.setAccessible(true);
            boolean onGroupClick = false;
            try {
                onGroupClick = (boolean) method.invoke(inObject, parent, v, groupPosition, childPosition);
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
                try {
                    onGroupClick = (boolean) method.invoke(inObject, v, groupPosition, childPosition);
                } catch (IllegalAccessException e1) {
                } catch (InvocationTargetException e1) {
                    try {
                        onGroupClick = (boolean) method.invoke(inObject, groupPosition, childPosition);
                    } catch (IllegalAccessException | InvocationTargetException e2) {
                        throw new RuntimeException(e2);
                    }
                }
            }
            return onGroupClick;
        }
    };

    public OnChildClickHandler(Object inObject) {
        super(inObject);
    }

    @Override
    public void handleView(Method method, View view) {
        if(view instanceof ExpandableListView){
            mMap.put(view.getId(), method);
            ((ExpandableListView) view).setOnChildClickListener(onChildClickListener);
        }
    }

    @Override
    public String getMethodPrefix() {
        return "onChildClick";
    }
}
