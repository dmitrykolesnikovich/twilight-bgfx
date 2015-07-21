package twilight.bgfx.font;

/**
 * 
 * @author tmccrary
 *
 */
public class TextBuffer {

    /**
     * 
     * @author tmccrary
     *
     */
    public enum GlyphType {
        ALPHA(0x00000100), DISTANCE(0x00000400), DISTANCE_SUBPIXEL(0x00000500);

        final long value;

        private GlyphType(long value) {
            this.value = value;
        }

        long getValue() {
            return value;
        }

    }

    /**
     * 
     * @author tmccrary
     *
     */
    public enum UsageType {
        STATIC(0), DYNAMIC(1), TRANSIENT(2);

        final long value;

        private UsageType(long value) {
            this.value = value;
        }

        long getValue() {
            return value;
        }

    }

    /** */
    final long handle;

    /** */
    final UsageType usageType;

    /** */
    final GlyphType glyphType;

    /** */
    final FontManager manager;

    /**
     * 
     * @param handle
     */
    public TextBuffer(FontManager manager, long handle, GlyphType glyphType, UsageType usageType) {
        this.manager = manager;
        this.handle = handle;
        this.glyphType = glyphType;
        this.usageType = usageType;
    }

    public void setBackgroundColor(long color) {

    }

    public void setForegroundColor(long color) {

    }

    public void setOverlineColor(long color) {

    }

    public void setStrikeThroughColor(long color) {

    }

    public void setStyle(long color) {

    }

    public void appendText(Font font, String text) {
        manager.updateTextBuffer(this, font, text, 0, 0);
    }

}
