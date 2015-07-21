package twilight.bgfx.nanovg.svg;

/**
 * 
 * @author tmccrary
 *
 */
public class SVGImage {

    /** */
    public final long imagePtr;

    /**
     * 
     * @param svgImagePtr
     */
    public SVGImage(long svgImagePtr) {
        this.imagePtr = svgImagePtr;
    }

    public void dispose() {
        n_Dispose();
    }

    private native void n_Dispose();

}
