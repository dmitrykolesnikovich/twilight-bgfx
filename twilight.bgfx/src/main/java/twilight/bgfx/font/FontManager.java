package twilight.bgfx.font;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import twilight.bgfx.BGFXException;
import twilight.bgfx.font.TextBuffer.GlyphType;
import twilight.bgfx.font.TextBuffer.UsageType;
import twilight.bgfx.util.ResourceUtil;

/**
 * 
 * @author tmccrary
 *
 */
public class FontManager {

    /** */
    private long fontManagerPtr;

    /** */
    private long textBufferManagerPtr;

    /** */
    private int count;

    /** */
    private FontFace defaultFontFace;

    /** */
    private Font defaultFont;

    /** */
    private float defaultFontSize = 21f;

    /**
     * 
     * @param textureSizeSize
     */
    public FontManager(int textureSizeSize) {
        this.count = textureSizeSize;

        nInit(textureSizeSize);

        try {
            InputStream resourceAsStream = FontManager.class.getClassLoader().getResourceAsStream("l33tlabs/twilight/bgfx/font/DroidSans.ttf");

            if (resourceAsStream == null) {
                throw new Exception("Couldn't locate default font.");
            }

            byte[] byteArray = ResourceUtil.inputStreamToByteArray(resourceAsStream);
            ByteBuffer allocateDirect = ByteBuffer.allocateDirect(byteArray.length);
            allocateDirect.put(byteArray);
            allocateDirect.flip();

            defaultFontFace = createTTF(allocateDirect);
            defaultFont = createFontByPixelSize(defaultFontFace, defaultFontSize);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param buffer
     * @return
     */
    public FontFace createTTF(ByteBuffer buffer) {
        if (buffer == null) {
            throw new BGFXException("Cannot create font face from null ByteBuffer.");
        }

        if (!buffer.isDirect()) {
            throw new BGFXException("All ByteBuffer instances pased to BFGX must be direct.");
        }

        long ttfHandle = nCreateTTF(buffer, buffer.limit());

        if (ttfHandle == -1) {
            throw new BGFXException("Error creating font face.");
        }

        return new FontFace(ttfHandle);
    }

    /**
     * 
     * @param fontDef
     * @param height
     * @return
     */
    public Font createFontByPixelSize(FontFace fontFace, float height) {
        long fontDefPtr = nCreateFontByPixelSize(fontFace.truetypeHandle, height);

        if (fontDefPtr == -1) {
            throw new BGFXException("Error creating font.");
        }

        return new Font(fontDefPtr);
    }

    /**
     * 
     * @return
     */
    public TextBuffer createTextBuffer(GlyphType glyphType, UsageType usage) {
        if (glyphType == null) {
            throw new BGFXException("GlyphType cannot be null.");
        }

        if (usage == null) {
            throw new BGFXException("UsageType cannot be null");
        }

        long handle = nCreateTextBuffer(glyphType.getValue(), usage.getValue());

        if (handle == -1) {
            throw new BGFXException("Error creating text buffer.");
        }

        return new TextBuffer(this, handle, glyphType, usage);
    }

    /**
     * 
     * @param buffer
     */
    public void destroyTextBuffer(TextBuffer buffer) {
        nDestroyTextBuffer(buffer.handle);
    }

    /**
     * 
     * @param buffer
     * @param fontInstance
     * @param text
     */
    public void updateTextBuffer(TextBuffer buffer, Font fontInstance, String text, float x, float y) {
        nUpdateTextBuffer(buffer.handle, fontInstance.handle, text, x, y);
    }

    /**
     * 
     * @param buffer
     * @param x
     * @param y
     */
    public void draw(TextBuffer buffer, float x, float y) {
        nDrawTextBuffer(buffer.handle, x, y);
    }

    /**
     * 
     * @param buffer
     * @return
     */
    public float getWidth(TextBuffer buffer) {
        return nGetTextBufferRect(buffer.handle);
    }

    /**
     * 
     * @return
     */
    public Font getDefaultFont() {
        return defaultFont;
    }

    /**
     * 
     * @return
     */
    public FontFace getDefaultFontFace() {
        return defaultFontFace;
    }

    /**
     * Native Methods
     */
    public native void nInit(int count);

    private native float nGetTextBufferRect(long handle);

    private native void nDrawTextBuffer(long textBuffer, float x, float y);

    public native long nCreateTTF(ByteBuffer buffer, int size);

    public native long nCreateFontByPixelSize(long fontHandle, float height);

    public native long nCreateTextBuffer(long glyphType, long usage);

    public native void nUpdateTextBuffer(long bufferHandle, long font, String text, float x, float y);

    private native void nDestroyTextBuffer(long ptr);

}
