package twilight.bgfx.window.sdl;

import java.util.Map;

import twilight.bgfx.BGFX;
import twilight.bgfx.BGFXException;
import twilight.bgfx.Messages;
import twilight.bgfx.TextureFormat;
import twilight.bgfx.window.Display;
import twilight.bgfx.window.Window;
import twilight.bgfx.window.WindowRect;

/**
 * 
 * @author tmccrary
 *
 */
public class SDLWindow extends Window {

    /** The display that owns this window. */
    private SDLDisplay display;

    /** The SDL_Window native pointer. */
    private long SDLWindowPtr;

    /**
     * The native windowing system handle wrapped by SDL (i.e. Xlib, win32,
     * cocoa, etc)
     */
    private long nativeHandle;

    /** The id of the window. */
    private long id;

    /** The SDL WindowID uint32. */
    private long sdlId;

    /** Options passed into the window (for future use). */
    private Map<String, String> options;

    /** The window's name and title. */
    private String name;

    /** */
    private static int COUNTER;

    /** The BGFX context used by the window. */
    private BGFX bgfx;

    /** The window size/pos rectangle. */
    private WindowRect rect = new WindowRect(0, 0, 1, 1);

    /** */
    private WindowRect dragPos;

    /** Whether the window is visible. */
    private boolean visible = false;

    /** */
    private boolean maximized;

    /** */
    private int currentWindowFrameBufferId = BGFX.INVALID_HANDLE;

    /** */
    private int lastWidth;

    /** */
    private int lastHeight;

    /**
     * 
     */
    @Override
    protected void init(Display parentDisplay, Map<String, String> options) {
        if (!(parentDisplay instanceof SDLDisplay)) {
            throw new RuntimeException("Invalid Display supplied for SDL Window.");
        }

        this.display = (SDLDisplay) parentDisplay;
        this.options = options;

        COUNTER++;

        this.name = "UNNAMED_WINDOW_" + COUNTER;

        if (options != null && options.get("name") != null) {
            name = options.get("name");
        }

        boolean undecorated = false;

        if (options != null && options.get("undecorated") != null) {
            String undec = options.get("undecorated");

            undecorated = Boolean.parseBoolean(undec);
        }

        SDLWindowPtr = createWindow(this.display, undecorated);

        // Get the native window (i.e. Xlib, Win32, Cocoa, etc)
        // This is required for multiple window support
        nativeHandle = ngetNativeHandle(SDLWindowPtr);

        sdlId = ngetSDLWindowId(SDLWindowPtr);

        this.display.registerWindow(getId(), this);

        rect = new WindowRect(0, 0, 2, 2);
        
        lastWidth = -1;
        lastHeight = -1;
        
        updateContextFramebuffer();
        
        setVisible(true);
    }

    /**
     * <p>Recreates the window's framebuffer if the size has
     * changed.</p>
     */
    private void updateContextFramebuffer() {
        BGFX bgfx = getContext();
        
        if(!BGFX.isValidContext(bgfx)) {
            return;
        }
        
        if(rect.width != lastWidth || rect.height != lastHeight) {
            if(BGFX.isValidHandle(currentWindowFrameBufferId)) {
               bgfx.destroyFrameBuffer(currentWindowFrameBufferId);
            }
            
            currentWindowFrameBufferId = bgfx.createFrameBuffer(this, rect.width, rect.height, TextureFormat.BGRA8);
            
            lastWidth = rect.width;
            lastHeight = rect.height;
        }
    }

    /**
     * 
     */
    @Override
    public void destroy() {
        if (!(display instanceof SDLDisplay)) {
            throw new RuntimeException("Invalid Display created for SDL Window.");
        }

        this.destroyWindow((SDLDisplay) display);
    }

    /**
     * 
     * @param display
     * @return
     */
    private native long destroyWindow(SDLDisplay display);

    /**
     * 
     */
    @Override
    public BGFX getContext() {
        if (bgfx == null) {
            if (display.getContext() == null) {
                bgfx = new BGFX();
                initBGFX();

                display.setContext(bgfx);
            } else {
                bgfx = display.getContext();
            }
        }

        return bgfx;
    }

    /**
     * 
     * @return
     */
    public long getId() {
        return nGetId();
    }

    /**
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void setRectInternal(int x, int y, int width, int height) {
        if (rect.width == width && rect.height == height) {
            return;
        }

        this.rect.width = width;
        this.rect.height = height;
        
        updateContextFramebuffer();
    }

    /**
     * 
     */
    void setPositionInternal(int x, int y) {
        if (this.rect.x == x && this.rect.y == y) {
            return;
        }

        // this.rect.x = x;
        // this.rect.y = y;
    }

    /**
     * 
     */
    public void setPosition(int x, int y) {
        this.rect.x = x;
        this.rect.y = y;
        this.nSetPosition(x, y);
    }

    /**
     * 
     */
    public int getAbsoluteX() {
        return nGetAbsoluteX();
    }

    /**
     * 
     */
    public int getAbsoluteY() {
        return nGetAbsoluteY();
    }

    /**
     * 
     */
    public int getX() {
        return rect.x;
    }

    /**
     * 
     */
    public int getY() {
        return rect.y;
    }

    /**
     * 
     */
    public int getWidth() {
        return rect.width;
    }

    /**
     * 
     */
    public int getHeight() {
        return rect.height;
    }

    /**
     * 
     */
    public void setSize(int width, int height) {
        this.nSetSize(width, height);
        updateContextFramebuffer();
    }

    /**
     * 
     */
    public void setVisible(boolean visible) {
        this.nSetVisible(visible);
        this.visible = visible;
    }

    /**
     * 
     * @return
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * 
     */
    @Override
    public long getNativeHandle() {
        return nativeHandle;
    }

    public void raise() {
        nRaise();
    }

    /**
     * 
     */
    public long getLocalId() {
        return sdlId;
    }

    /**
     * 
     * @param display
     * @return
     */
    private native long createWindow(SDLDisplay display, boolean undecorated);

    /**
     * 
     */
    private native void initBGFX();

    /**
     * 
     * @return
     */
    public native WindowRect nGetWindowRect();

    /**
     * 
     * @param width
     * @param height
     */
    public native void nSetSize(int width, int height);

    /**
     * 
     * @param visible
     */
    private native void nSetVisible(boolean visible);

    private native void nRaise();

    /**
     * 
     */
    public native int nGetAbsoluteX();

    /**
     * 
     */
    public native int nGetAbsoluteY();

    /**
     * 
     */
    private native long ngetNativeHandle(long pointer);

    /**
     * 
     */
    private native long ngetSDLWindowId(long pointer);

    /**
     * 
     * @param x
     * @param y
     */
    public native void nSetPosition(int x, int y);

    /**
     * 
     * @param max
     */
    private native void nSetMaximized(boolean max);

    /**
     * 
     */
    @Override
    public void setMaximized(boolean max) {
        this.maximized = max;
        nSetMaximized(max);
    }

    /**
     * 
     */
    public boolean isMaximized() {
        return this.maximized;
    }

    /**
     * 
     */
    private native long nGetId();

    public int getCurrentWindowFrameBufferId() {
        return currentWindowFrameBufferId;
    }


}
