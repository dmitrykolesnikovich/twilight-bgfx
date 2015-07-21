package twilight.bgfx.window.swt;

import twilight.bgfx.BGFX;

/**
 * 
 * @author tmccrary
 *
 */
public class SWTHelper {

    /**
     * 
     * @param renderCtx 
     * @param widget
     * @param window
     * @param context
     */
    public static void swtSetWindow(BGFX renderCtx, long widget, long window, long context) {
        nswtSetWindow(widget, window, context);
    }
    
    /**
     * 
     * @param widget
     * @param window
     * @param context
     */
    public static native void nswtSetWindow(long widget, long window, long context);
    
}
