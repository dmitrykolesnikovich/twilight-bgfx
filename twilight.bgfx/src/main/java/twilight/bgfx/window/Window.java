package twilight.bgfx.window;

import java.util.Map;

import twilight.bgfx.BGFX;

/**
 * 
 * @author tmccrary
 *
 */
public abstract class Window {

    /**
     * 
     */
    protected Window() {

    }

    /**
     * 
     * @param display
     */
    protected abstract void init(Display display, Map<String, String> options);

    /**
     * 
     */
    public abstract void destroy();

    public abstract void setPosition(int x, int y);

    public abstract int getX();

    public abstract int getY();

    public abstract int getAbsoluteX();

    public abstract int getAbsoluteY();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract void setSize(int width, int height);

    public abstract void setVisible(boolean visible);

    public abstract BGFX getContext();

    public abstract boolean isVisible();

    public abstract long getNativeHandle();

    public abstract long getLocalId();

    public abstract void raise();

    public abstract void setMaximized(boolean max);

    public abstract boolean isMaximized();

}
