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

    /**
     * Class list of handlers we will use to find methods to interpret.
     */
    private static ArrayList<Class<? extends SHandler>> sHandlers = new ArrayList<>();

    /**
     * Holds a running list of methods we care about
     */
    private static ArrayList<String> sMethodNames;
    private static boolean needMethodNames = false;
    static {
        sHandlers.add(OnCreateHandler.class);
        sHandlers.add(OnCheckedChangedHandler.class);
        sHandlers.add(OnClickHandler.class);
        sHandlers.add(OnItemClickHandler.class);
        sHandlers.add(OnItemSelectedHandler.class);
        sHandlers.add(OnLongClickHandler.class);
        sHandlers.add(OnTouchHandler.class);
        sHandlers.add(OnChildClickHandler.class);
        sHandlers.add(OnGroupClickHandler.class);
        needMethodNames = true;
    }

    /**
     * Add a custom handler that will respond to custom method types, allowing you to set custom view methods within classes.
     * @param clazz
     */
    public static void addHandler(Class<? extends SHandler> clazz){
        if(!sHandlers.contains(clazz)){
            sHandlers.add(clazz);
            needMethodNames = true;
        }
    }

    /**
     * Returns a list of handler instances that we use while inflating
     * @param inObject
     * @return
     */
    public static ArrayList<? extends SHandler> getHandlerInstances(Object inObject){
        ArrayList<SHandler> handlers = new ArrayList<>();

        if(needMethodNames) {
            sMethodNames = new ArrayList<>();
        }
        for(Class<? extends SHandler> handler: sHandlers){
            try {
                SHandler sHandler = handler.getConstructor(Object.class).newInstance(inObject);
                handlers.add(sHandler);
                if(needMethodNames) {
                    sMethodNames.add(sHandler.getMethodPrefix());
                }
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        needMethodNames = false;
        return handlers;
    }

    /**
     * Returns the method prefixes from our handler instances
     * @return
     */
    public static String[] getMethodPrefixes(){
        return sMethodNames.toArray(new String[sMethodNames.size()]);
    }
}
