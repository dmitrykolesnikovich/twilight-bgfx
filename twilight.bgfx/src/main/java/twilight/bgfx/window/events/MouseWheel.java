package twilight.bgfx.window.events;

/**
 * 
 * @author tmccrary
 *
 */
public class MouseWheel extends Event {

    private int x;

    private int y;

    protected MouseWheel(long id, long timestamp) {
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

}
