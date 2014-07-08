package com.grosner.smartinflater.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * Created by: andrewgrosner
 * Date: 7/8/14.
 * Contributors: {}
 * Description:
 */
public class SGlobalHandlerList {

    private static ArrayList<Class<? extends SHandler>> sHandlers = new ArrayList<>();
    static {
        sHandlers.add(OnCheckedChangedHandler.class);
        sHandlers.add(OnClickHandler.class);
        sHandlers.add(OnCreateHandler.class);
        sHandlers.add(OnItemClickHandler.class);
        sHandlers.add(OnItemSelectedHandler.class);
        sHandlers.add(OnLongClickHandler.class);
        sHandlers.add(OnTouchHandler.class);
    }

    /**
     * Add a custom handler that will respond to custom method types, allowing you to set custom view methods within classes.
     * @param clazz
     */
    public static void addHandler(Class<? extends SHandler> clazz){
        if(!sHandlers.contains(clazz)){
            sHandlers.add(clazz);
        }
    }

    /**
     * Returns a list of handler instances that we use while inflating
     * @param inObject
     * @return
     */
    public static ArrayList<? extends SHandler> getHandlerInstances(Object inObject){
        ArrayList<SHandler> handlers = new ArrayList<>();
        for(Class<? extends SHandler> handler: sHandlers){
            try {
                SHandler sHandler = handler.getConstructor(Object.class).newInstance(inObject);
                handlers.add(sHandler);
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return handlers;
    }
}
