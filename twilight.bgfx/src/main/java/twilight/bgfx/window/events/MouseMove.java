package twilight.bgfx.window.events;

/**
 * 
 * @author tmccrary
 *
 */
public class MouseMove extends Event {

    private int x;

    private int y;

    private int globalX;

    private int globalY;

    protected MouseMove(long id, long timestamp) {
        super(id, timestamp);

        x = -1;
        y = -1;
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

    public void setGlobalX(int globalX) {
        this.globalX = globalX;
    }

    public int getGlobalY() {
        return globalY;
    }

    public void setGlobalY(int globalY) {
        this.globalY = globalY;
    }
}
