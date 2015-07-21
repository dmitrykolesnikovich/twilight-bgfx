package twilight.bgfx.window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import twilight.bgfx.BGFX;
import twilight.bgfx.BGFXException;
import twilight.bgfx.Messages;
import twilight.bgfx.window.sdl.SDLDisplay;

/**
 * 
 * @author tmccrary
 *
 */
public class DisplayManager {

    /** */
    public static final String LIB_NAME = "twilight-bgfx";

    /** */
    private static DisplayManager instance;

    /** */
    private List<Display> displays;

    /** */
    private Display defaultDisplay;

    /** */
    private Map<String, Window> windows;

    static {
        try {
            System.loadLibrary(LIB_NAME);
        } catch (Exception e) {
            throw new BGFXException(Messages.getString("BGFX.CantInitNatives") + e.getMessage(), e); //$NON-NLS-1$
        }
    }

    /**
     * 
     */
    public static DisplayManager getManager() {
        if (instance == null) {
            instance = new DisplayManager();
        }

        return instance;
    }

    /**
     * 
     */
    DisplayManager() {
        displays = new ArrayList<>();

        windows = new HashMap<String, Window>();

        defaultDisplay = createDisplay();
    }

    /**
     * 
     * @param name
     * @return
     */
    public Window createWindow(Display display, String name, Map<String, String> options) {
        Window window = display.createWindow(null);
        windows.put(name, window);

        if (options == null) {
            options = new HashMap<String, String>();
        }

        options.put("name", name);

        window.init(display, options);

        return window;
    }

    /**
     * 
     * @param name
     * @return
     */
    public Window getWindow(String name) {
        return windows.get(name);
    }

    /**
     * 
     * @param name
     */
    public void destroyWindow(String name) {
        Window window = windows.get(name);

        if (window == null) {
            return;
        }

        window.destroy();
    }

    /**
     * 
     */
    public void shutdown() {
        for (Entry<String, Window> entry : windows.entrySet()) {
            Window value = entry.getValue();
            value.destroy();
        }
    }

    /**
     * 
     */
    private Display createDisplay() {
        // String osString = System.getProperty("os.name");

        SDLDisplay sdlDisplay = new SDLDisplay();
        displays.add(sdlDisplay);

        return sdlDisplay;
    }

    /**
     * 
     * @return
     */
    public Display getDefaultDisplay() {
        return defaultDisplay;
    }

}
