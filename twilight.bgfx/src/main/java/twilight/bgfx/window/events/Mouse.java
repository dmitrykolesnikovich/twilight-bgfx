package twilight.bgfx.window.events;

public class Mouse extends Event {

    private ButtonEventType type;

    private int button;

    private int x;

    private int y;

    private int globalX;

    private int globalY;

    protected Mouse(long id, long timestamp) {
        super(id, timestamp);
        // TODO Auto-generated constructor stub
    }

    public ButtonEventType getType() {
        return type;
    }

    public void setType(ButtonEventType type) {
        this.type = type;
    }

    public int getButton() {
        return button;
    }

    public void setButton(int button) {
        this.button = button;
    }

    public void setTypeInt(int type) {
        this.type = ButtonEventType.values()[type];
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getGlobalX() {
        return globalX;
    }

    public int getGlobalY() {
        return globalY;
    }

    public void setGlobalX(int globalX) {
        this.globalX = globalX;
    }

    public void setGlobalY(int globalY) {
        this.globalY = globalY;
    }

}
