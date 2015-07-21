package twilight.bgfx.buffers;

public class DynamicVertexBuffer {

    public final long handle;

    public DynamicVertexBuffer(long handle) {
        this.handle = handle;
    }

    public long getHandle() {
        return handle;
    }

}
