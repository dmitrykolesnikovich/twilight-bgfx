package twilight.bgfx.window.events;

public class Keyboard extends Event {

    private int key;

    private ButtonEventType type;

    protected Keyboard(long id, long timestamp) {
        super(id, timestamp);

    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public ButtonEventType getType() {
        return type;
    }

    public void setType(ButtonEventType type) {
        this.type = type;
    }

    public void setTypeInt(int type) {
        this.type = ButtonEventType.values()[type];
    }

}
