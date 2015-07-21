package twilight.bgfx.buffers;

public class VertexBuffer {

    final long handle;

    public VertexBuffer(long handle) {
        this.handle = handle;
    }

    public long getHandle() {
        return handle;
    }

}
