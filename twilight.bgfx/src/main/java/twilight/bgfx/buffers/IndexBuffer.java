package twilight.bgfx.buffers;

public class IndexBuffer {

    final long handle;

    public IndexBuffer(long handle) {
        this.handle = handle;
    }

    public long getHandle() {
        return handle;
    }

}
