package twilight.bgfx.nanovg.svg;

import java.nio.ByteBuffer;

/**
 * <p>
 * Utility class to rasterize SVG graphics for icons, images and other purposes.
 * </p>
 * 
 * @author tmccrary
 *
 */
public class SVG {

    /**
     * <p>
     * Parse the supplied ByteBuffer containing SVG markup into an image object
     * that can be rasterized.
     * </p>
     * 
     * @param svgSource
     * @return
     */
    public static SVGImage parse(ByteBuffer svgSource) {
        long svgImagePtr = n_Parse(svgSource);

        return new SVGImage(svgImagePtr);
    }

    /**
     * <p>
     * Rasterizes an image into a BGRA image supplied as a ByteBuffer.
     * </p>
     * 
     * @param image
     * @param width
     * @param height
     * @return
     */
    public static ByteBuffer rasterize(SVGImage image, int tx, int ty, float scale, int width, int height, int stride) {
        return n_Rasterize(image.imagePtr, tx, ty, scale, width, height, stride);
    }

    /** */
    private static native long n_Parse(ByteBuffer svgSource);

    /** */
    private static native ByteBuffer n_Rasterize(long imagePtr, int tx, int ty, float scale, int width, int height, int stride);

}
