package twilight.bgfx.buffers;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import twilight.bgfx.VertexDecl;

/**
 * 
 * @author tmccrary
 *
 */
public class TransientVertexBuffer {
    long size;
    long startVertex;
    int stride;
    int handleId;
    VertexDecl decl;

    public long pointer;

    /**
     * 
     * @return
     */
    public ByteBuffer getData() {
        ByteBuffer buffer = nGetData();
        buffer = buffer.order(ByteOrder.nativeOrder());
        return buffer;
    }

    /**
     * 
     * @return
     */
    public native ByteBuffer nGetData();

}