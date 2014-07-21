package com.grosner.smartinflater.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grosner.smartinflater.annotation.SMethod;
import com.grosner.smartinflater.annotation.SResource;
import com.grosner.smartinflater.exception.SResourceNotFoundException;
import com.grosner.smartinflater.handlers.SGlobalHandlerList;
import com.grosner.smartinflater.handlers.SHandler;
import com.grosner.smartinflater.utils.ReflectionUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
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

    /**
     * Hold it weakly so we prevent serious memory leaks
     */
    private static WeakReference<Context> mContext;

    /**
     * Call this at some point within your app (preferably in the Application class), so you don't have to use a context.
     * If you require a specific context (for theming) its preferred you pass in the correct context in the onCreate Method
     * @param context
     */
    public static void initialize(Context context){
        mContext = new WeakReference<>(context);
    }

    public static Context getContext(){
        if(mContext==null || mContext.get()==null){
            throw new IllegalStateException("Context not initialized");
        } else {
            return mContext.get();
        }
    }

    /**
     * Inflates the layout with the passed layoutResId, fills the inObject {@link com.grosner.smartinflater.annotation.SResource}'s, and
     * invokes the {@link com.grosner.smartinflater.handlers.SHandler} methods.
     * @param inObject - the class we want to connect methods to, that have declared {@link com.grosner.smartinflater.annotation.SResource}
     * @param layoutResId - the resource id of the layout
     * @return
     */
    public static View inflate(Object inObject, int layoutResId){
        ViewGroup root = inObject instanceof ViewGroup ? (ViewGroup) inObject : null;
        View layout = LayoutInflater.from(getContext()).inflate(layoutResId, root);

        injectViews(inObject, layout);
        connectMethods(inObject, layout);

        return layout;
    }

    /**
     * Injects a View layout's children into the corresponding {@link com.grosner.smartinflater.annotation.SResource}'s of the inObject.
     * By default, this method will throw an exception when the {@link com.grosner.smartinflater.annotation.SResource} could not be found
     * in the inLayout. This is intentional so that we can eliminate errors and prevent you from not noticing right away.
     * @param inObject - the class we want to fill with the declared {@link com.grosner.smartinflater.annotation.SResource}
     * @param inLayout - the layout we have previously inflated
     */
    public static void injectViews(Object inObject, View inLayout){
        List<Field> fieldList = SResourcesMap.getFieldMap(inObject);

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

    /**
     * Connects methods from the {@link com.grosner.smartinflater.handlers.SGlobalHandlerList} into the inObject using the inLayout
     * @param inObject - the class we want to connect methods to, that have declared {@link com.grosner.smartinflater.annotation.SResource}
     * @param inLayout - the layout we have previously inflated
     */
    public static void connectMethods(final Object inObject, final View inLayout){

        ArrayList<? extends SHandler> handlers = SGlobalHandlerList.getHandlerInstances(inObject);

        HashMap<Integer, View> localViewMap = new HashMap<>();

        List<Method> methods = SResourcesMap.getMethodMap(inObject);
        for(Method method: methods){
            int methodId = getMethodId(method, inObject);
            if(methodId!=-1){
                View found = localViewMap.get(methodId);
                if(found==null) found = inLayout.findViewById(methodId);
                if(found!=null){
                    for(SHandler sHandler: handlers){
                        if(method.getName().startsWith(sHandler.getMethodPrefix())){
                            sHandler.handleView(method, found);
                            break;
                        }
                    }
                    localViewMap.put(methodId, found);
                }
            }
        }
    }

    /**
     * Will return the view that the method corresponds to. By default, we take out the method prefix and
     * lowercase the first letter to see if it corresponds to the name of a field. If the field has an
     * id declared in the {@link com.grosner.smartinflater.annotation.SMethod} annotation, we search for that one.
     * @param method
     * @param inObject
     * @return
     */
    private static int getMethodId(Method method, Object inObject){
        int methodId = -1;

        try{
            SMethod s = method.getAnnotation(SMethod.class);
            if(s!=null && s.id()!=0){
                methodId = s.id();
            } else{
                methodId = getContext().getResources().getIdentifier(ReflectionUtils.getFieldFromMethod(inObject.getClass(), method.getName(), false,
                                SGlobalHandlerList.getMethodPrefixes()), "id",
                        getContext().getPackageName());
            }
        } catch (Exception ignored){}
        return methodId;
    }

    /**
     * Destroys the views contained in the {@link SResourcesMap}
     * to release grip on the view objects to prevent memory leaks.
     * @param inObject
     */
    public static void destroyViews(Object inObject){
        List<Field> views = SResourcesMap.getFieldMap(inObject);
        for(Field field: views){
            field.setAccessible(true);
            try {
                field.set(inObject, null);
            } catch (IllegalAccessException e) {
            }
        }
    }
}
