package twilight.bgfx.nanovg;

/**
 * 
 * @author tmccrary
 *
 */
public class NVGpaint {

    float[] xform;
    float[] extent;
    float radius;
    float feather;
    NVGcolor innerColor;
    NVGcolor outerColor;
    int image;
    int repeat;

    public NVGpaint(float[] xform, float[] extent, float radius, float feather, NVGcolor innerColor, NVGcolor outerColor, int image, int repeat) {
        this.xform = xform;
        this.extent = extent;
        this.feather = feather;
        this.innerColor = innerColor;
        this.outerColor = outerColor;
        this.image = image;
        this.repeat = repeat;
    }
}
