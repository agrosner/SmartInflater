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
import com.grosner.smartinflater.utils.MethodNames;
import com.grosner.smartinflater.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

        final HashMap<Integer, Method> onClickMap = new HashMap<>();
        final HashMap<Integer, Method> onTouchMap = new HashMap<>();
        final HashMap<Integer, Method> onLongClickMap = new HashMap<>();
        HashMap<Integer, View> localViewMap = new HashMap<>();
        final HashMap<Integer, Method> onCheckedChangedMap = new HashMap<>();
        final HashMap<AdapterView, Method> onItemSelectedMap = new HashMap<>();
        final HashMap<AdapterView, Method> onItemClickMap = new HashMap<>();


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Method method = onClickMap.get(v.getId());
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

        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Method method = onCheckedChangedMap.get(buttonView.getId());
                try{
                    method.setAccessible(true);
                    method.invoke(inObject, buttonView, isChecked);
                } catch (Throwable t){
                    throw new RuntimeException(t);
                }
            }
        };

        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Method method = onItemSelectedMap.get(parent);
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

        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Method method = onItemClickMap.get(parent);
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

        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Method method = onTouchMap.get(v.getId());
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

        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Method method = onLongClickMap.get(v.getId());
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


        List<Method> methods = ViewClassFieldMap.getMethodMap(inObject);
        for(Method method: methods){
            int methodId = getMethodId(method, inObject);
            if(methodId!=-1){
                View found = localViewMap.get(methodId);
                if(found==null) found = inLayout.findViewById(methodId);
                if(found!=null){
                    if(method.getName().startsWith(MethodNames.ONCLICK)){
                        onClickMap.put(methodId, method);
                        found.setOnClickListener(onClickListener);
                    } else if(method.getName().startsWith(MethodNames.ONCHECKEDCHANGED)) {
                        if (found instanceof CompoundButton) {
                            ((CompoundButton) found).setOnCheckedChangeListener(onCheckedChangeListener);
                            onCheckedChangedMap.put(methodId, method);
                        } else {
                            throw new MethodTypeMistmatchException(found.getClass(), method.getName());
                        }
                    } else if(method.getName().startsWith(MethodNames.ONLONGCLICK)){
                        onLongClickMap.put(methodId, method);
                        found.setOnLongClickListener(onLongClickListener);
                    } else if(method.getName().startsWith(MethodNames.ONTOUCH)){
                        onTouchMap.put(methodId, method);
                        found.setOnTouchListener(onTouchListener);
                    } else if(method.getName().startsWith(MethodNames.ONITEMSELECTED)){
                        if(found instanceof AdapterView){
                            ((AdapterView) found).setOnItemSelectedListener(onItemSelectedListener);
                            onItemSelectedMap.put((AdapterView) found, method);
                        } else{
                            throw new MethodTypeMistmatchException(found.getClass(), method.getName());
                        }
                    } else if(method.getName().startsWith(MethodNames.ONITEMCLICK)){
                        if(found instanceof Spinner){
                            throw new RuntimeException("Spinners cannot set onItemClick listeners");
                        } else if(found instanceof AdapterView){
                            ((AdapterView) found).setOnItemClickListener(onItemClickListener);
                            onItemClickMap.put((AdapterView) found, method);
                        } else{
                            throw new MethodTypeMistmatchException(found.getClass(), method.getName());
                        }
                    } else if(method.getName().startsWith(MethodNames.ONCREATE)){
                        try {
                            method.setAccessible(true);
                            method.invoke(inObject, found);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            //want to pass off crashes to the app so that the programmer sees a problem
                            throw new RuntimeException("An exception occured in an onCreate method: "
                                    + method.getName() + " for class: " + inObject.getClass(),
                                    e.getCause());
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
