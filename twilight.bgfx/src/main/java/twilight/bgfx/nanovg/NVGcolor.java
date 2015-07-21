package twilight.bgfx.nanovg;

/**
 * 
 * @author tmccrary
 *
 */
public class NVGcolor {

    /** */
    public float r;

    /** */
    public float g;

    /** */
    public float b;

    /** */
    public float a;

    /**
     * 
     */
    public NVGcolor() {
        r = 1f;
        g = 1f;
        b = 1f;
        a = 1f;
    }

    /**
     * 
     * @param r
     * @param g
     * @param b
     * @param a
     */
    public NVGcolor(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

}
