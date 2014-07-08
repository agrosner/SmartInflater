package com.grosner.smartinflater.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.grosner.smartinflater.annotation.SMethod;
import com.grosner.smartinflater.annotation.SResource;
import com.grosner.smartinflater.exception.MethodTypeMistmatchException;
import com.grosner.smartinflater.exception.SResourceNotFoundException;
import com.grosner.smartinflater.handlers.SGlobalHandlerList;
import com.grosner.smartinflater.handlers.SHandler;
import com.grosner.smartinflater.utils.MethodNames;
import com.grosner.smartinflater.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by: andrewgrosner
 * Date: 7/8/14.
 * Contributors: {}
 * Description: Using reflection, we can provide significant boilerplate code reduction for interactions with view classes.
 * <br />
 * This class enables adding methods without the need for implementing interfaces, switching by view id, or calling {@link android.view.View#findViewById(int)}.
 *
 * <br />
 *
 *
 */
public class SmartInflater {

    private static Context mContext;

    /**
     * Call this at some point within your app before using this library. This should ONLY be the Application Context.
     * @param context
     */
    public static void initialize(Context context){
        mContext = context;
    }

    public static Context getContext(){
        if(mContext==null){
            throw new IllegalStateException("Context not initialized");
        }
        return mContext;
    }

    public static void inflate(Object inObject, int layoutResId){
        ViewGroup root = inObject instanceof ViewGroup ? (ViewGroup) inObject : null;
        View layout = LayoutInflater.from(mContext).inflate(layoutResId, root);

        injectViews(inObject, layout);
        connectMethods(inObject, layout);
    }

    static void injectViews(Object inObject, View inLayout){
        List<Field> fieldList = ViewClassFieldMap.getFieldMap(inObject);

        for(Field field: fieldList){
            int fieldId;
            SResource resource = field.getAnnotation(SResource.class);
            if(resource.id()!=0){
                fieldId = resource.id();
            } else {
                fieldId = getContext().getResources().getIdentifier(field.getName(), "id", getContext().getPackageName());
            }

            if(fieldId!=-1){
                View found = inLayout.findViewById(fieldId);
                if(found!=null){
                    try {
                        field.setAccessible(true);
                        field.set(inObject, found);
                    } catch (IllegalAccessException | IllegalArgumentException e) {}
                } else if(!resource.optional()){
                    throw new SResourceNotFoundException(field.getName(), inObject.getClass());
                }
            }
        }
    }

    static void connectMethods(final Object inObject, final View inLayout){

        ArrayList<? extends SHandler> handlers = SGlobalHandlerList.getHandlerInstances(inObject);

        HashMap<Integer, View> localViewMap = new HashMap<>();

        List<Method> methods = ViewClassFieldMap.getMethodMap(inObject);
        for(Method method: methods){
            int methodId = getMethodId(method, inObject);
            if(methodId!=-1){
                View found = localViewMap.get(methodId);
                if(found==null) found = inLayout.findViewById(methodId);
                if(found!=null){
                    for(SHandler sHandler: handlers){
                        if(method.getName().startsWith(sHandler.getMethodPrefix())){
                            sHandler.handleView(method, methodId, found);
                            break;
                        }
                    }
                    localViewMap.put(methodId, found);
                }
            }
        }
    }

    private static int getMethodId(Method method, Object inObject){
        int methodId = -1;

        try{
            SMethod s = method.getAnnotation(SMethod.class);
            if(s!=null && s.id()!=0){
                methodId = s.id();
            } else{
                methodId = getContext().getResources().getIdentifier(ReflectionUtils.getFieldFromMethod(inObject.getClass(), method.getName(), false,
                                MethodNames.ONCLICK, MethodNames.ONCHECKEDCHANGED, MethodNames.ONCREATE, MethodNames.ONITEMCLICK, MethodNames.ONITEMSELECTED), "id",
                        getContext().getPackageName());
            }
        } catch (Exception ignored){}
        return methodId;
    }

    /**
     * Destroys the views contained in the {@link com.grosner.smartinflater.view.ViewClassFieldMap}
     * to release grip on the view objects to prevent memory leaks.
     * @param inObject
     */
    public static void destroyViews(Object inObject){
        List<Field> views = ViewClassFieldMap.getFieldMap(inObject);
        for(Field field: views){
            field.setAccessible(true);
            try {
                field.set(inObject, null);
            } catch (IllegalAccessException e) {
            }
        }
    }
}
