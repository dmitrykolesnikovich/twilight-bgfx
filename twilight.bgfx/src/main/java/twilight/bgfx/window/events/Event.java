package twilight.bgfx.window.events;

/**
 * 
 * @author tmccrary
 *
 */
public abstract class Event {

    protected long windowId;

    private long id;

    private long timestamp;

    protected Event(long id, long timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getWindowId() {
        return windowId;
    }

    public void setWindowId(long windowId) {
        this.windowId = windowId;
    }

}
