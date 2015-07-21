package twilight.bgfx.nanovg;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import twilight.bgfx.BGFXException;
import twilight.bgfx.Messages;

/**
 * <p>
 * Provides access to the NanoVG graphics API. NanoVG is an antialiased 2D
 * vector drawing library.
 * </p>
 * 
 * NanoVG home page: https://github.com/memononen/nanovg
 * 
 * @author Tony McCrary (tmccrary@l33tlabs.com)
 *
 */
public class NanoVG {

    /** List of active NanoVG contexts. */
    private static List<NanoVG> contexts = new ArrayList<>();

    /** The native NanoVG context backing this object. */
    public NVGcontext context;

    private int windowWidth;

    private int windowHeight;

    private float devicePixelRatio;

    private int alphaBlend;

    public boolean deleteMe;

    /**
     * <p>
     * Creates a new NanoVG context.
     * </p>
     * 
     * @param atlasw
     * @param atlash
     * @param edgeaa
     * @param viewid
     * 
     * @return a new NanoVG context
     */
    public static NanoVG createNVGContext(int atlasw, int atlash, int edgeaa, int viewid) {
        NanoVG nanoVG = new NanoVG(atlasw, atlash, edgeaa, viewid);
        contexts.add(nanoVG);
        return nanoVG;
    }

    /**
     * <p>
     * Frees all contexts.
     * </p>
     */
    public static void freeAllContexts() {
        List<NanoVG> queuedRemove = new ArrayList<>(contexts.size());
        for (NanoVG nanovg : contexts) {
            if (!nanovg.isValid()) {
                continue;
            }

            nanovg.delete();
            queuedRemove.add(nanovg);
        }

        for (NanoVG nanovg : queuedRemove) {
            contexts.remove(nanovg);
        }
    }

    /**
     * 
     */
    public void queueDelete() {
        this.deleteMe = true;
    }

    /**
     * <p>
     * Deletes the context and frees memory and other resources associated with
     * the context.
     * </p>
     */
    public void delete() {
        checkContext();

        nnvgDelete(context.ctxPtr);

        context = null;
    }

    /**
     * 
     */
    public boolean isValid() {
        return context != null && context.ctxPtr != 0;
    }

    /**
     * 
     * @param atlasw
     * @param atlash
     * @param edgeaa
     * @param viewid
     */
    public NanoVG(int atlasw, int atlash, int edgeaa, int viewid) {
        this.context = nvgCreate(atlasw, atlash, edgeaa, viewid);
    }

    /**
     * 
     * @param atlasw
     * @param atlash
     * @param edgeaa
     * @param viewid
     * @return
     */
    private NVGcontext nvgCreate(int atlasw, int atlash, int edgeaa, int viewid) {
        return nnvgCreate(atlasw, atlash, edgeaa, viewid);
    }

    /**
     * 
     * @param windowWidth
     * @param windowHeight
     * @param devicePixelRatio
     * @param alphaBlend
     */
    public void beginFrame(int windowWidth, int windowHeight, float devicePixelRatio, int alphaBlend) {
        checkContext();

        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.devicePixelRatio = devicePixelRatio;
        this.alphaBlend = alphaBlend;

        nnvgBeginFrame(context.ctxPtr, windowWidth, windowHeight, devicePixelRatio, alphaBlend);
    }

    /**
     * 
     */
    public void endFrame() {
        checkContext();
        nnvgEndFrame(context.ctxPtr);
    }

    /**
     * 
     * @param r
     * @param g
     * @param b
     * @return
     */
    public NVGcolor RGB(char r, char g, char b) {
        checkContext();
        return nnvgRGB(r, g, b);
    }

    /**
     * 
     * @param r
     * @param g
     * @param b
     * @return
     */
    public NVGcolor RGBf(float r, float g, float b) {
        checkContext();
        return nnvgRGBf(r, g, b);
    }

    /**
     * 
     * @param r
     * @param g
     * @param b
     * @param a
     * @return
     */
    public NVGcolor RGBA(char r, char g, char b, char a) {
        checkContext();
        return nnvgRGBA(r, g, b, a);
    }

    /**
     * 
     * @param abgr
     * @return
     */
    public NVGcolor RGBAu(int abgr) {
        checkContext();
        return nnvgRGBAu(abgr);
    }

    /**
     * 
     * @param r
     * @param g
     * @param b
     * @param a
     * @return
     */
    public NVGcolor RGBAf(float r, float g, float b, float a) {
        checkContext();
        return nnvgRGBAf(r, g, b, a);
    }

    /**
     * 
     * @param c0
     * @param c1
     * @param u
     * @return
     */
    public NVGcolor LerpRGBA(NVGcolor c0, NVGcolor c1, float u) {
        checkContext();
        return nnvgLerpRGBA(c0, c1, u);
    }

    /**
     * 
     * @param c0
     * @param a
     * @return
     */
    public NVGcolor TransRGBA(NVGcolor c0, char a) {
        checkContext();
        return nnvgTransRGBA(c0, a);
    }

    /**
     * 
     * @param c0
     * @param a
     * @return
     */
    public NVGcolor TransRGBAf(NVGcolor c0, float a) {
        checkContext();
        return nnvgTransRGBAf(c0, a);
    }

    /**
     * 
     * @param h
     * @param s
     * @param l
     * @return
     */
    public NVGcolor HSL(float h, float s, float l) {
        checkContext();
        return nnvgHSL(h, s, l);
    }

    /**
     * 
     * @param h
     * @param s
     * @param l
     * @param a
     * @return
     */
    public NVGcolor HSLA(float h, float s, float l, char a) {
        checkContext();
        return nnvgHSLA(h, s, l, a);
    }

    /**
     * 
     * @param color
     */
    public void strokeColor(NVGcolor color) {
        checkContext();
        nnvgStrokeColor(context.ctxPtr, color);
    }

    /**
     * 
     * @param paint
     */
    public void strokePaint(NVGpaint paint) {
        checkContext();
        nnvgStrokePaint(context.ctxPtr, paint);
    }

    /**
     * 
     * @param color
     */
    public void fillColor(NVGcolor color) {
        checkContext();
        nnvgFillColor(context.ctxPtr, color);
    }

    /**
     * 
     * @param paint
     */
    public void fillPaint(NVGpaint paint) {
        checkContext();
        nnvgFillPaint(context.ctxPtr, paint);
    }

    /**
     * 
     * @param sx
     * @param sy
     * @param ex
     * @param ey
     * @param icol
     * @param ocol
     * @return
     */
    public NVGpaint linearGradient(float sx, float sy, float ex, float ey, NVGcolor icol, NVGcolor ocol) {
        checkContext();
        return nnvgLinearGradient(context.ctxPtr, sx, sy, ex, ey, icol, ocol);
    }

    /**
     * 
     * @param x
     * @param y
     * @param w
     * @param h
     * @param r
     * @param f
     * @param icol
     * @param ocol
     * @return
     */
    public NVGpaint boxGradient(float x, float y, float w, float h, float r, float f, NVGcolor icol, NVGcolor ocol) {
        checkContext();
        return nnvgBoxGradient(context.ctxPtr, x, y, w, h, r, f, icol, ocol);
    }

    /**
     * 
     * @param cx
     * @param cy
     * @param inr
     * @param outr
     * @param icol
     * @param ocol
     * @return
     */
    public NVGpaint radialGradient(float cx, float cy, float inr, float outr, NVGcolor icol, NVGcolor ocol) {
        checkContext();
        return nnvgRadialGradient(context.ctxPtr, cx, cy, inr, outr, icol, ocol);
    }

    /**
     * 
     * @param ox
     * @param oy
     * @param ex
     * @param ey
     * @param angle
     * @param image
     * @param repeat
     * @return
     */
    public NVGpaint imagePattern(float ox, float oy, float ex, float ey, float angle, int image, int repeat) {
        checkContext();
        return nnvgImagePattern(context.ctxPtr, ox, oy, ex, ey, angle, image, repeat);
    }

    /**
     * 
     */
    public void save() {
        checkContext();
        nnvgSave(context.ctxPtr);
    }

    /**
     * 
     */
    public void restore() {
        checkContext();
        nnvgRestore(context.ctxPtr);
    }

    /**
     * 
     */
    public void reset() {
        checkContext();
        nnvgReset(context.ctxPtr);
    }

    /**
     * 
     * @param limit
     */
    public void miterLimit(float limit) {
        checkContext();
        nnvgMiterLimit(context.ctxPtr, limit);
    }

    /**
     * 
     * @param size
     */
    public void strokeWidth(float size) {
        checkContext();
        nnvgStrokeWidth(context.ctxPtr, size);
    }

    /**
     * 
     * @param cap
     */
    public void lineCap(int cap) {
        checkContext();
        nnvgLineCap(context.ctxPtr, cap);
    }

    /**
     * 
     * @param join
     */
    public void lineJoin(int join) {
        checkContext();
        nnvgLineJoin(context.ctxPtr, join);
    }

    /**
     * 
     */
    public void resetTransform() {
        checkContext();
        nnvgResetTransform(context.ctxPtr);
    }

    /**
     * 
     * @param a
     * @param b
     * @param c
     * @param d
     * @param e
     * @param f
     */
    public void transform(float a, float b, float c, float d, float e, float f) {
        checkContext();
        nnvgTransform(context.ctxPtr, a, b, c, d, e, f);
    }

    /**
     * 
     * @param x
     * @param y
     */
    public void translate(float x, float y) {
        checkContext();
        nnvgTranslate(context.ctxPtr, x, y);
    }

    /**
     * 
     * @param angle
     */
    public void rotate(float angle) {
        checkContext();
        nnvgRotate(context.ctxPtr, angle);
    }

    /**
     * 
     * @param x
     * @param y
     */
    public void scale(float x, float y) {
        checkContext();
        nnvgScale(context.ctxPtr, x, y);
    }

    /**
     * 
     * @param filename
     * @return
     */
    public int createImage(String filename) {
        checkContext();
        return nnvgCreateImage(context.ctxPtr, filename);
    }

    /**
     * 
     * @param data
     * @param ndata
     * @return
     */
    public int createImageMem(String data, int ndata) {
        checkContext();
        return nnvgCreateImageMem(context.ctxPtr, data, ndata);
    }

    /**
     * 
     * @param w
     * @param h
     * @param data
     * @return
     */
    public int createImageRGBA(int w, int h, String data) {
        checkContext();
        return nnvgCreateImageRGBA(context.ctxPtr, w, h, data);
    }

    /**
     * 
     * @param image
     * @param data
     */
    public void updateImage(int image, String data) {
        checkContext();
        nnvgUpdateImage(context.ctxPtr, image, data);
    }

    /**
     * 
     * @param image
     * @param w
     * @param h
     */
    public void imageSize(int image, int w, int h) {
        checkContext();
        nnvgImageSize(context.ctxPtr, image, w, h);
    }

    /**
     * 
     * @param image
     */
    public void deleteImage(int image) {
        checkContext();
        nnvgDeleteImage(context.ctxPtr, image);
    }

    /**
     * 
     * @param x
     * @param y
     * @param w
     * @param h
     */
    public void scissor(float x, float y, float w, float h) {
        checkContext();
        nnvgScissor(context.ctxPtr, x, y, w, h);
    }

    /**
     * 
     */
    public void resetScissor() {
        checkContext();
        nnvgResetScissor(context.ctxPtr);
    }

    /**
     * 
     */
    public void beginPath() {
        checkContext();
        nnvgBeginPath(context.ctxPtr);
    }

    /**
     * 
     * @param x
     * @param y
     */
    public void moveTo(float x, float y) {
        checkContext();
        nnvgMoveTo(context.ctxPtr, x, y);
    }

    /**
     * 
     * @param x
     * @param y
     */
    public void lineTo(float x, float y) {
        checkContext();
        nnvgLineTo(context.ctxPtr, x, y);
    }

    /**
     * 
     * @param c1x
     * @param c1y
     * @param c2x
     * @param c2y
     * @param x
     * @param y
     */
    public void bezierTo(float c1x, float c1y, float c2x, float c2y, float x, float y) {
        checkContext();
        nnvgBezierTo(context.ctxPtr, c1x, c1y, c2x, c2y, x, y);
    }

    /**
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param radius
     */
    public void arcTo(float x1, float y1, float x2, float y2, float radius) {
        checkContext();
        nnvgArcTo(context.ctxPtr, x1, y1, x2, y2, radius);
    }

    /**
     * 
     */
    public void closePath() {
        checkContext();
        nnvgClosePath(context.ctxPtr);
    }

    /**
     * 
     * @param dir
     */
    public void pathWinding(int dir) {
        checkContext();
        nnvgClosePath(context.ctxPtr);
    }

    /**
     * 
     * @param cx
     * @param cy
     * @param r
     * @param a0
     * @param a1
     * @param dir
     */
    public void arc(float cx, float cy, float r, float a0, float a1, int dir) {
        checkContext();
        nnvgArc(context.ctxPtr, cx, cy, r, a0, a1, dir);
    }

    /**
     * 
     * @param x
     * @param y
     * @param w
     * @param h
     */
    public void rect(float x, float y, float w, float h) {
        checkContext();
        nnvgRect(context.ctxPtr, x, y, w, h);
    }

    /**
     * 
     * @param x
     * @param y
     * @param w
     * @param h
     * @param r
     */
    public void roundedRect(float x, float y, float w, float h, float r) {
        checkContext();
        nnvgRoundedRect(context.ctxPtr, x, y, w, h, r);
    }

    /**
     * 
     * @param cx
     * @param cy
     * @param rx
     * @param ry
     */
    public void ellipse(float cx, float cy, float rx, float ry) {
        checkContext();
        nnvgEllipse(context.ctxPtr, cx, cy, rx, ry);
    }

    /**
     * 
     * @param cx
     * @param cy
     * @param r
     */
    public void circle(float cx, float cy, float r) {
        checkContext();
        nnvgCircle(context.ctxPtr, cx, cy, r);
    }

    /**
     * 
     */
    public void fill() {
        checkContext();
        nnvgFill(context.ctxPtr);
    }

    /**
     * 
     */
    public void stroke() {
        checkContext();
        nnvgStroke(context.ctxPtr);
    }

    /**
     * 
     * @param name
     * @param filename
     * @return
     */
    public int createFont(String name, String filename) {
        checkContext();
        // return nnvgCreateFont(context.ctxPtr, name, filename);
        return -1;
    }

    /**
     * 
     * @param name
     * @param data
     * @param ndata
     * @param freeData
     * @return
     */
    public NVGfont createFontMem(String name, ByteBuffer data, int ndata, int freeData) {
        checkContext();
        int fontHandle = nnvgCreateFontMem(context.ctxPtr, name, data, ndata, freeData);
        return new NVGfont(fontHandle);
    }

    /**
     * 
     * @param name
     * @return
     */
    public int findFont(String name) {
        checkContext();
        // return nnvgFindFont(context.ctxPtr, name);
        return -1;
    }

    /**
     * 
     * @param size
     */
    public void fontSize(float size) {
        checkContext();
        // nnvgFontSize(context.ctxPtr, size);
    }

    /**
     * 
     * @param blur
     */
    public void fontBlur(float blur) {
        checkContext();
        // nnvgFontBlur(context.ctxPtr, blur);
    }

    /**
     * 
     * @param spacing
     */
    public void textLetterSpacing(float spacing) {
        checkContext();
        // nnvgTextLetterSpacing(context.ctxPtr, spacing);
    }

    /**
     * 
     * @param lineHeight
     */
    public void textLineHeight(float lineHeight) {
        checkContext();
        // nnvgTextLineHeight(context.ctxPtr, lineHeight);
    }

    /**
     * 
     * @param align
     */
    public void textAlign(int align) {
        checkContext();
        // nnvgTextAlign(context.ctxPtr, align);
    }

    /**
     * 
     * @param font
     */
    public void fontFaceId(int font) {
        checkContext();
        // nnvgFontFaceId(context.ctxPtr, font);
    }

    /**
     * 
     * @param font
     */
    public void fontFace(String font) {
        checkContext();
        // nnvgFontFace(context.ctxPtr, font);
    }

    /**
     * 
     * @param x
     * @param y
     * @param string
     * @param end
     * @return
     */
    public float text(float x, float y, String string, String end) {
        checkContext();
        // return nnvgText(context.ctxPtr, x, y, string, end);
        return 0f;
    }

    /**
     * 
     * @param x
     * @param y
     * @param breakRowWidth
     * @param string
     * @param end
     */
    public void textBox(float x, float y, float breakRowWidth, String string, String end) {
        checkContext();
        // nnvgTextBox(context.ctxPtr, x, y, breakRowWidth, string, end);
    }

    /**
     * 
     * @param x
     * @param y
     * @param string
     * @param end
     * @param bounds
     * @return
     */
    public float textBounds(float x, float y, String string, String end, float bounds) {
        checkContext();
        return nnvgTextBounds(context.ctxPtr, x, y, string, end, bounds);

    }

    /**
     * 
     * @param x
     * @param y
     * @param breakRowWidth
     * @param string
     * @param end
     * @param bounds
     */
    public void textBoxBounds(float x, float y, float breakRowWidth, String string, String end, float bounds) {
        checkContext();
        nnvgTextBoxBounds(context.ctxPtr, x, y, breakRowWidth, string, end, bounds);
    }

    /**
     * 
     * @param x
     * @param y
     * @param string
     * @param end
     * @param positions
     * @param maxPositions
     * @return
     */
    public int textGlyphPositions(float x, float y, String string, String end, NVGglyphPosition positions, int maxPositions) {
        checkContext();
        return -1;
    }

    /**
     * 
     * @param ascender
     * @param descender
     * @param lineh
     */
    public void textMetrics(float ascender, float descender, float lineh) {
        checkContext();
        nnvgTextMetrics(context.ctxPtr, ascender, descender, lineh);
    }

    /**
     * 
     * @param string
     * @param end
     * @param breakRowWidth
     * @param rows
     * @param maxRows
     * @return
     */
    public int textBreakLines(String string, String end, float breakRowWidth, NVGtextRow rows, int maxRows) {
        checkContext();
        return maxRows;
    }

    /**
     * <p>
     * Checks if the current context is valid (not null, not freed, etc).
     * </p>
     */
    private void checkContext() {
        if (context == null) {
            throw new BGFXException(Messages.getString("NanoVG.NullContext")); //$NON-NLS-1$
        }

        if (context.ctxPtr == 0) {
            throw new BGFXException(Messages.getString("NanoVG.NullContextPointer")); //$NON-NLS-1$
        }
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public float getDevicePixelRatio() {
        return devicePixelRatio;
    }

    public int getAlphaBlend() {
        return alphaBlend;
    }

    private native NVGcontext nnvgCreate(int atlasw, int atlash, int edgeaa, int viewid);

    private native void nnvgDelete(long ctx);

    private native void nnvgBeginFrame(long ctx, int windowWidth, int windowHeight, float devicePixelRatio, int alphaBlend);

    private native void nnvgEndFrame(long ctx);

    private native NVGcolor nnvgRGB(char r, char g, char b);

    private native NVGcolor nnvgRGBf(float r, float g, float b);

    private native NVGcolor nnvgRGBA(char r, char g, char b, char a);

    private native NVGcolor nnvgRGBAu(int abgr);

    private native NVGcolor nnvgRGBAf(float r, float g, float b, float a);

    private native NVGcolor nnvgLerpRGBA(NVGcolor c0, NVGcolor c1, float u);

    private native NVGcolor nnvgTransRGBA(NVGcolor c0, char a);

    private native NVGcolor nnvgTransRGBAf(NVGcolor c0, float a);

    private native NVGcolor nnvgHSL(float h, float s, float l);

    private native NVGcolor nnvgHSLA(float h, float s, float l, char a);

    private native void nnvgSave(long ctx);

    private native void nnvgRestore(long ctx);

    private native void nnvgReset(long ctx);

    private native void nnvgStrokeColor(long ctx, NVGcolor color);

    private native void nnvgStrokePaint(long ctx, NVGpaint paint);

    private native void nnvgFillColor(long ctx, NVGcolor color);

    private native void nnvgFillPaint(long ctx, NVGpaint paint);

    private native void nnvgMiterLimit(long ctx, float limit);

    private native void nnvgStrokeWidth(long ctx, float size);

    private native void nnvgLineCap(long ctx, int cap);

    private native void nnvgLineJoin(long ctx, int join);

    private native void nnvgResetTransform(long ctx);

    private native void nnvgTransform(long ctx, float a, float b, float c, float d, float e, float f);

    private native void nnvgTranslate(long ctx, float x, float y);

    private native void nnvgRotate(long ctx, float angle);

    private native void nnvgScale(long ctx, float x, float y);

    private native int nnvgCreateImage(long ctx, String filename);

    private native int nnvgCreateImageMem(long ctx, Object data, int ndata);

    private native int nnvgCreateImageRGBA(long ctx, int w, int h, String data);

    private native void nnvgUpdateImage(long ctx, int image, String data);

    private native void nnvgImageSize(long ctx, int image, int w, int h);

    private native void nnvgDeleteImage(long ctx, int image);

    private native NVGpaint nnvgLinearGradient(long ctx, float sx, float sy, float ex, float ey, NVGcolor icol, NVGcolor ocol);

    private native NVGpaint nnvgBoxGradient(long ctx, float x, float y, float w, float h, float r, float f, NVGcolor icol, NVGcolor ocol);

    private native NVGpaint nnvgRadialGradient(long ctx, float cx, float cy, float inr, float outr, NVGcolor icol, NVGcolor ocol);

    private native NVGpaint nnvgImagePattern(long ctx, float ox, float oy, float ex, float ey, float angle, int image, int repeat);

    private native void nnvgScissor(long ctx, float x, float y, float w, float h);

    private native void nnvgResetScissor(long ctx);

    private native void nnvgBeginPath(long ctx);

    private native void nnvgMoveTo(long ctx, float x, float y);

    private native void nnvgLineTo(long ctx, float x, float y);

    private native void nnvgBezierTo(long ctx, float c1x, float c1y, float c2x, float c2y, float x, float y);

    private native void nnvgArcTo(long ctx, float x1, float y1, float x2, float y2, float radius);

    private native void nnvgClosePath(long ctx);

    private native void nnvgPathWinding(long ctx, int dir);

    private native void nnvgArc(long ctx, float cx, float cy, float r, float a0, float a1, int dir);

    private native void nnvgRect(long ctx, float x, float y, float w, float h);

    private native void nnvgRoundedRect(long ctx, float x, float y, float w, float h, float r);

    private native void nnvgEllipse(long ctx, float cx, float cy, float rx, float ry);

    private native void nnvgCircle(long ctx, float cx, float cy, float r);

    private native void nnvgFill(long ctx);

    private native void nnvgStroke(long ctx);

    private native int nnvgCreateFont(long ctx, String name, String filename);

    private native int nnvgCreateFontMem(long ctx, String name, ByteBuffer data, int ndata, int freeData);

    private native int nnvgFindFont(long ctx, String name);

    private native void nnvgFontSize(long ctx, float size);

    private native void nnvgFontBlur(long ctx, float blur);

    private native void nnvgTextLetterSpacing(long ctx, float spacing);

    private native void nnvgTextLineHeight(long ctx, float lineHeight);

    private native void nnvgTextAlign(long ctx, int align);

    private native void nnvgFontFaceId(long ctx, int font);

    private native void nnvgFontFace(long ctx, String font);

    private native float nnvgText(long ctx, float x, float y, String string, String end);

    private native void nnvgTextBox(long ctx, float x, float y, float breakRowWidth, String string, String end);

    private native float nnvgTextBounds(long ctx, float x, float y, String string, String end, float bounds);

    private native void nnvgTextBoxBounds(long ctx, float x, float y, float breakRowWidth, String string, String end, float bounds);

    private native int nnvgTextGlyphPositions(long ctx, float x, float y, String string, String end, NVGglyphPosition positions, int maxPositions);

    private native void nnvgTextMetrics(long ctx, float ascender, float descender, float lineh);

    private native int nnvgTextBreakLines(long ctx, String string, String end, float breakRowWidth, NVGtextRow rows, int maxRows);

}
