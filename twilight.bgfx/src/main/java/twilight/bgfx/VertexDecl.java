package twilight.bgfx;

/**
 * 
 * @author tmccrary
 *
 */
public class VertexDecl {

    /** */
    private long vertexDeclPtr;

    /** */
    private boolean buildActive;

    /** */
    private boolean valid;

    private VertexDecl() {
        valid = false;
        buildActive = false;
    }

    /**
     * 
     */
    private void checkDeclBegin() {
        if (vertexDeclPtr != 0) {
            throw new BGFXException("VertexDecl#begin already called on vertex declaration, did you call it twice?");
        }
    }

    /**
     * 
     */
    private void checkDecl() {
        if (vertexDeclPtr == 0) {
            throw new BGFXException("Vertex declaration has not been initialized. VertexDecl#begin must be called before other methods can be used.");
        }
    }

    /**
     * 
     */
    public void checkBuildActive() {
        if (!buildActive) {
            throw new BGFXException("Cannot call methods on vertex declaration before it has been initialized with VertexDecl#begin.");
        }
    }

    /**
     * 
     * @return
     */
    public boolean isValid() {
        return valid && !buildActive;
    }

    public static VertexDecl begin() {
        VertexDecl decl = new VertexDecl();
        decl.intBegin(RendererType.Count);

        return decl;
    }

    public static VertexDecl begin(RendererType _renderer) {
        VertexDecl decl = new VertexDecl();
        decl.intBegin(_renderer);

        return decl;
    }

    private void intBegin(RendererType _renderer) {
        checkDeclBegin();

        vertexDeclPtr = this.nbegin(_renderer);
        buildActive = true;
    }

    public void end() {
        checkDecl();

        this.nend();
        buildActive = false;
        valid = true;
    }

    public void destroy() {
        ndestroy();
    }

    private native void ndestroy();

    private native long nbegin(RendererType type);

    private native void nend();

    public void add(Attrib _attrib, int elements, AttribType _type, boolean _normalized, boolean _asInt) {
        checkDecl();

        this.nAdd(_attrib.ordinal(), (short) elements, _type.ordinal(), _normalized, _asInt);
    }

    private native void nAdd(int _attrib, short _num, int _type, boolean _normalized, boolean _asInt);

    void skip(short _num) {
        checkDecl();

        this.nSkip(_num);
    }

    private native void nSkip(short _num);

    public void decode(Attrib _attrib, int _num, AttribType _type, boolean _normalized, boolean _asInt) {
        checkDecl();

        this.nDecode(_attrib, (short) _num, _type, _normalized, _asInt);
    }

    private native void nDecode(Attrib _attrib, short _num, AttribType _type, boolean _normalized, boolean _asInt);

    public boolean has(Attrib _attrib) {
        checkDecl();

        return nHas(_attrib);
    }

    private native boolean nHas(Attrib attrib);

    public int getOffset(Attrib _attrib) {
        checkDecl();

        return ngetOffset(_attrib);
    }

    private native int ngetOffset(Attrib attrib);

    int getStride() {
        checkDecl();

        return ngetStride();
    }

    private native int ngetStride();

    long getSize(long _num) {
        checkDecl();

        return ngetSize(_num);
    }

    private native long ngetSize(long num);

}
