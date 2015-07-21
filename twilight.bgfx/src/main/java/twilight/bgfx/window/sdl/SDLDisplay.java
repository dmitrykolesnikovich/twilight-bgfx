package twilight.bgfx.window.sdl;

import java.util.HashMap;
import java.util.Map;

import twilight.bgfx.BGFX;
import twilight.bgfx.BGFXException;
import twilight.bgfx.Messages;
import twilight.bgfx.window.Cursor;
import twilight.bgfx.window.Display;
import twilight.bgfx.window.Window;
import twilight.bgfx.window.sdl.SDLCursor.SDLPlatformCursorStyle;

/**
 * 
 * @author tmccrary
 *
 */
public class SDLDisplay extends Display {

    /** Map of windows to their ids */
    private Map<Long, SDLWindow> SDLWindowMap;

    /** global context */
    private BGFX context;

    /** */
    private static Map<SDLPlatformCursorStyle, SDLCursor> platformCursors;

    /**
     * 
     * @param style
     * @return
     */
    public static SDLCursor getPlatformCursor(SDLPlatformCursorStyle style) {
        return platformCursors.get(style);
    }

    /**
     * 
     */
    public void initSystemCursors() {
        int cursorSize = SDLPlatformCursorStyle.values().length;
        platformCursors = new HashMap<SDLPlatformCursorStyle, SDLCursor>(cursorSize);

        for (SDLPlatformCursorStyle style : SDLPlatformCursorStyle.values()) {
            long cursorPtr = nCreatePlatformCursor(style.ordinal());
            platformCursors.put(style, new SDLCursor(cursorPtr, true));
        }
    }

    /**
     * 
     * @param cursorStyle
     * @return
     */
    private static native long nCreatePlatformCursor(int cursorStyle);

    /**
     * 
     */
    public SDLDisplay() {
        long response = nCreateDisplay();

        if (response != 0) {
            throw new BGFXException("Could not initialize display, exiting.");
        }

        SDLWindowMap = new HashMap<Long, SDLWindow>();

        if (platformCursors == null) {
            initSystemCursors();
        }
    }

    public void windowMove(long id, int x, int y) {
        /*
         * SDLWindow windowByXID = getWindowByXID(xid); if(windowByXID == null)
         * { return; }
         * 
         * windowByXID.setPositionInternal(x, y);
         */
    }

    public void windowResize(long id, int newWidth, int newHeight) {
        SDLWindow windowByXID = getWindowByHandle(id);
        if (windowByXID == null) {
            return;
        }

        windowByXID.setRectInternal(0, 0, newWidth, newHeight);
    }

    /**
     * 
     */
    public Window createWindow(Map<String, String> options) {
        SDLWindow newWindow = new SDLWindow();
        // newWindow.init(this, options);

        return newWindow;
    }

    /**
     * 
     * @param id
     * @return
     */
    public SDLWindow getWindowByHandle(long id) {
        return SDLWindowMap.get(id);
    }

    /**
     * 
     * @param handle
     * @param window
     */
    public void registerWindow(long handle, SDLWindow window) {
        SDLWindowMap.put(handle, window);
    }

    /**
     * 
     */
    public void isValid() {
        this.nIsValid();
    }

    /**
     * 
     */
    public int update() {
        return (int) this.nUpdate();
    }

    /**
     * 
     */
    public int getDPI() {
        return nGetDPI();
    }

    public int getWidth() {
        return nGetWidth();
    }

    public int getHeight() {
        return nGetHeight();
    }

    public int getPhysicalWidth() {
        return nGetPhysicalWidth();
    }

    public int getPhysicalHeight() {
        return nGetPhysicalHeight();
    }

    @Override
    public int pumpInput() {
        return 0;
    }

    public BGFX getContext() {
        return context;
    }

    protected void setContext(BGFX context) {
        this.context = context;
    }

    @Override
    public void setCursor(Cursor cursor) {
        SDLCursor sdlCursor = (SDLCursor) cursor;

        nSetCursor(sdlCursor.cursorPtr);
    }

    private native void nSetCursor(long cursorPtr);

    private native int nGetHeight();

    protected native long nIsValid();

    protected native long nUpdate();

    protected native long nCreateDisplay();

    private native int nGetDPI();

    private native int nGetWidth();

    private native int nGetPhysicalWidth();

    private native int nGetPhysicalHeight();

}
