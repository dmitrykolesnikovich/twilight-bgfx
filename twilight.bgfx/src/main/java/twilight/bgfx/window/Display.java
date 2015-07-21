package twilight.bgfx.window;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import twilight.bgfx.window.events.Event;

/**
 * <p>
 * Display represents a Display device. Displays can create Windows which allow
 * application to render graphics and user interfaces.
 * </p>
 * 
 * <p>
 * Display is meant to be an opaque abstraction over Device/Window management.
 * If more detailed platform specific access is required the end user can cast
 * the Display to a more specific type (SDLDisplay, etc).
 * </p>
 * 
 * @author tmccrary
 *
 */
public abstract class Display {

    private Queue<Event> eventQueue = new LinkedList<Event>();

    public void queueEvent(Event incoming) {
        eventQueue.add(incoming);
    }

    public Queue<Event> getEvents() {
        return eventQueue;
    }

    public void clearEvents() {
        eventQueue.clear();
    }

    /**
     * 
     */
    public abstract void isValid();

    /**
     * 
     * @return
     */
    public abstract int update();

    /**
     * 
     * @return
     */
    public abstract int pumpInput();

    public abstract int getDPI();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract int getPhysicalWidth();

    public abstract int getPhysicalHeight();

    public abstract Window createWindow(Map<String, String> options);

    public abstract void setCursor(Cursor cursor);

}
