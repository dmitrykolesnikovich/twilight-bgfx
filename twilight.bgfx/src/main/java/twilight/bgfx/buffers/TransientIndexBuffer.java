package twilight.bgfx.buffers;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * 
 * @author tmccrary
 *
 */
public class TransientIndexBuffer {
    long data;
    long size;
    int handleId;
    long startIndex;

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

    public ShortBuffer getShortData() {
        return getData().asShortBuffer();
    }
    
    public native ByteBuffer nGetData();

}