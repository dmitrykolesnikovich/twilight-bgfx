package twilight.bgfx.buffers;

public class DynamicIndexBuffer {

    final long handle;

    public DynamicIndexBuffer(long handle) {
        this.handle = handle;
    }

    public long getHandle() {
        return handle;
    }

}
