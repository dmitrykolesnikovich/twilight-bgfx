package twilight.bgfx;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import twilight.bgfx.buffers.DynamicIndexBuffer;
import twilight.bgfx.buffers.DynamicVertexBuffer;
import twilight.bgfx.buffers.IndexBuffer;
import twilight.bgfx.buffers.InstanceDataBuffer;
import twilight.bgfx.buffers.TransientIndexBuffer;
import twilight.bgfx.buffers.TransientVertexBuffer;
import twilight.bgfx.buffers.VertexBuffer;
import twilight.bgfx.window.Window;

/**
 * <p>
 * Main context class for BGFX, a cross-platform, graphics API agnostic,
 * "Bring Your Own Engine/Framework" style rendering library.
 * </p>
 * 
 * <p>
 * Twilight BGFX is a java binding for the original library.
 * </p>
 * 
 * <p>
 * BGFX is final and should not be extended.
 * </p>
 * 
 * <p>
 * <ul>
 * <li>Twilight BGFX: https://github.com/enleeten/twilight-bgfx</li>
 * <li>BGFX home page: https://github.com/bkaradzic/bgfx</li>
 * </ul>
 * </p>
 * 
 * @author Tony McCrary (tmccrary@l33tlabs.com)
 *
 */
final public class BGFX {

    /** The native library name. */
    private static final String LIB_NAME = "twilight-bgfx";

    /** Invalid BFGX handle (buffers, shaders, etc.) */
    public final static int INVALID_HANDLE = 65535;

    /** Used with setViewFramebuffer(view, framebuffer) */
    public final static int DEFAULT_FRAMEBUFFER = 65535;

    /** The BGFX submission thread. */
    private Thread submitThread;

    /**
     * A list of allocated transient vertex buffers. These are freed after every
     * frame.
     */
    private List<Long> vertexPointerList = new ArrayList<Long>();

    /**
     * A list of allocated transient index buffers. These are freed after every
     * frame.
     */
    private List<Long> indexPointerList = new ArrayList<Long>();

    /** Whether the init call has been successfully made.  */
    private boolean inititalized = false;
    
    /** Whether the natives have been loaded. */
    private static boolean libraryLoaded = false;

    static {
        if(!libraryLoaded) {
            try {
                System.loadLibrary(LIB_NAME);
                libraryLoaded = true;
            } catch(Exception e) {
                throw new BGFXException("Could not load BGFX native library: " + e.getMessage());
            }
        }
    }
    
    public BGFX() {
    }
    
    /**
     * <pVerifies that the supplied BGFX context is active and valid for the
     * current thread.
     * </p>
     * 
     * @param context
     *            the BGFX context to validate
     */
    public static void checkValidContext(BGFX context) {
        if(context == null) {
            throw new BGFXException("Null BGFX context");
        }

        if(!context.isValidThread()) {
            throw new BGFXException("Invalid BGFX thread.");
        }
    }    
    
    /**
     * 
     * @param context
     * @return
     */
    public static boolean isValidContext(BGFX context) {
        if(context == null) {
            return false;
        }

        if(!context.isValidThread()) {
            return false;
        }
        
        return context.inititalized;
    }

    /**
     * <p>
     * Returns true if the supplied BGFX shader/texture/etc handle is valid,
     * false otherwise.
     * </p>
     * 
     * @param handleId
     *            a BGFX resource handle to test
     * 
     * @return true if the supplied BGFX handle is valid, false otherwise
     */
    public static boolean isValidHandle(int handleId) {
        return handleId != BGFX.INVALID_HANDLE;
    }

    /**
     * <p>
     * Returns true if the current calling thread is the BGFX submission thread,
     * false otherwise.
     * </p>
     * 
     * @return true if the current thread is the BGFX submission thread, false
     *         otherwise
     */
    private boolean isValidThread() {
        if (!Thread.currentThread().equals(submitThread)) {
            return false;
        }

        return true;
    }

    /**
     * <p>
     * Checks the input ByteBuffer for use with BGFX. The input object cannot be
     * null, must be a direct ByteBuffer and cannot be empty.
     * </p>
     * 
     * <p>
     * If the input buffer is invalid, a runtime exception will be thrown.
     * </p>
     * 
     * @param memory
     *            the ByteBuffer to test
     */
    private void checkValidByteBuffer(ByteBuffer memory) {
        if (memory == null) {
            throw new NullPointerException(Messages.getString("BGFX.NullByteBuffer")); //$NON-NLS-1$
        }

        if (!memory.isDirect()) {
            throw new BGFXException(Messages.getString("BGFX.NonDirectBufferError")); //$NON-NLS-1$
        }

        if (memory.capacity() == 0) {
            throw new BGFXException(Messages.getString("BGFX.EmptyDirectBufferError")); //$NON-NLS-1$
        }
    }

    /**
     * <p>
     * Returns the type of renderer BGFX is using to draw graphics.
     * </p>
     * 
     * @return the RendererType that is being used to draw graphics
     */
    public RendererType getRendererType() {
        int index = ngetRendererType();
        return RendererType.values()[index];
    }

    /**
     * 
     * <p>
     * Pack vec4 into vertex stream format.
     * </p>
     * 
     * @param input
     * @param inputNormalized
     * @param attr
     * @param decl
     * @param data
     * @param index
     */
    public void vertexPack(float input[], boolean inputNormalized, Attrib attr, VertexDecl decl, long data, long index) {
        nvertexPack(input, inputNormalized, attr, decl, data, index);
    }

    /**
     * <p>
     * Unpack vec4 from vertex stream format.
     * </p>
     * 
     * @param output
     * @param attr
     * @param decl
     * @param data
     * @param index
     */
    public void vertexUnpack(float output, Attrib attr, VertexDecl decl, long data, long index) {
        nvertexUnpack(output, attr, decl, data, index);
    }

    /**
     * <p>
     * Converts vertex stream data from one vertex stream format to another.
     * </p>
     * 
     * @param destDecl
     *            Destination vertex stream declaration.
     * @param destData
     *            Destination vertex stream.
     * @param srcDecl
     *            Source vertex stream declaration.
     * @param srcData
     *            Source vertex stream data.
     * @param num
     *            Number of vertices to convert from source to destination.
     */
    public void vertexConvert(VertexDecl destDecl, long destData, VertexDecl srcDecl, long srcData, long num) {
        nvertexConvert(destDecl, destData, srcDecl, srcData, num);
    }

    /**
     * <p>
     * Weld vertices.
     * </p>
     * 
     * @param output
     *            Welded vertices remapping table. The size of buffer must be
     *            the same as number of vertices.
     * @param decl
     *            Vertex stream declaration.
     * @param data
     *            Vertex stream.
     * @param num
     *            Number of vertices in vertex stream.
     * @param epsilon
     *            Error tolerance for vertex position comparison.
     * @return Number of unique vertices after vertex welding.
     */
    public int weldVertices(long output, VertexDecl decl, long data, int num, float epsilon) {
        return nweldVertices(output, decl, data, num, epsilon);
    }

    /**
     * <p>
     * Swizzle RGBA8 image to BGRA8.
     * </p>
     * 
     * @param width
     *            Width of input image (pixels).
     * @param height
     *            Width of input image (pixels).
     * @param pitch
     *            Pitch of input image (bytes).
     * @param src
     *            Source image.
     * @param dst
     *            Destination image. Must be the same size as input image. dst
     *            might be pointer to the same memory as src.
     */
    public void imageSwizzleBgra8(long width, long height, long pitch, long src, long dst) {
        nimageSwizzleBgra8(width, height, pitch, src, dst);
    }

    /**
     * <p>
     * Downsample RGBA8 image with 2x2 pixel average filter.
     * </p>
     * 
     * @param width
     *            Width of input image (pixels).
     * @param height
     *            Height of input image (pixels).
     * @param pitch
     *            Pitch of input image (bytes).
     * @param src
     *            Source image.
     * @param dst
     *            Destination image. Must be at least quarter size of
     */
    public void imageRgba8Downsample2x2(long width, long height, long pitch, long src, long dst) {
        nimageRgba8Downsample2x2(width, height, pitch, src, dst);
    }

    /**
     * <p>
     * Initialize bgfx library.
     * </p>
     * 
     * @param rendererType
     *            Select rendering backend. When set to RendererType::Count
     * @param deviceVendor
     *            Vendor PCI id. If set to BGFX_PCI_ID_NONE it will select the
     *            first device.
     * @param deviceId
     *            Device id. If set to 0 it will select first device, or device
     *            with matching id.
     */
    public void init(RendererType rendererType, long deviceVendor, int deviceId) {
        submitThread = Thread.currentThread();

        ninit(rendererType.ordinal(), (int) deviceVendor, deviceId);
    }

    /**
     * <p>
     * Overloaded convenience method for init(RendererType, long, int)
     * </p>
     * <p>
     * Uses default/autodetect settings.
     * </p>
     */
    public void init() {
        submitThread = Thread.currentThread();

        ninit(RendererType.Count.ordinal(), (int) BGFX_PCI_ID_NONE, 0);
        
        inititalized = true;
    }

    /**
     * <p>
     * Shutdown bgfx library.
     * </p>
     */
    public void shutdown() {
        nshutdown();
    }

    /**
     * <p>
     * Reset graphic settings and back-buffer size.
     * </p>
     * 
     * @param width
     *            Back-buffer width.
     * @param height
     *            Back-buffer height.
     * @param flags
     *            Allows you to specify flags with BGFX_RESET_* enums
     *            <ul>
     *            <li>BGFX_RESET_NONE - No reset flags.</li>
     *            <li>BGFX_RESET_FULLSCREEN - Not supported yet.</li>
     *            <li>BGFX_RESET_MSAA_X[2/4/8/16] - Enable 2, 4, 8 or 16 x MSAA.
     *            </li>
     *            <li>BGFX_RESET_VSYNC - Enable V-Sync.</li>
     *            <li>BGFX_RESET_MAXANISOTROPY - Turn on/off max anisotropy.
     *            </li>
     *            <li>BGFX_RESET_CAPTURE - Begin screen capture.</li>
     *            <li>BGFX_RESET_HMD - HMD stereo rendering.</li>
     *            <li>BGFX_RESET_HMD_DEBUG - HMD stereo rendering debug mode.
     *            </li>
     *            <li>BGFX_RESET_HMD_RECENTER - HMD calibration.</li>
     *            <li>BGFX_RESET_FLIP_AFTER_RENDER - This flag specifies where
     *            flip occurs. Default behavior is that flip occurs before
     *            rendering new frame. This flag only has effect when compiled
     *            with <i>BGFX_CONFIG_MULTITHREADED=0</i>.</li>
     *            </ul>
     * 
     *  <b>Note:</b> This call doesn't actually change window size, it
     *  just resizes back-buffer. Windowing code has to change window
     *  size.
     * 
     */
    public void reset(long width, long height, long flags) {
        nreset(width, height, flags);
    }

    /**
     * <p>
     * Advance to next frame. When using multithreaded renderer, this call just
     * swaps internal buffers, kicks render thread, and returns. In
     * single threaded renderer this call does frame rendering.
     * </p>
     * 
     * @return Current frame number. This might be used in conjunction with
     *         double/multi buffering data outside the library and passing it to
     *         library via `bgfx::makeRef` calls.
     */
    public long frame() {
        long result = nframe();

        freeTransientVertexPointers();
        freeTransientIndexPointers();

        return result;
    }

    /**
     * <p>
     * Returns renderer capabilities.
     * </p>
     * 
     * @return capabilities instance.
     */
    public Caps getCaps() {
        return ngetCaps();
    }

    /**
     * <p>
     * Allocate buffer to pass to bgfx calls. Data will be freed inside bgfx.
     * </p>
     * 
     * @param size
     *            the number of bytes to allocate.
     * 
     * @return a direct ByteBuffer of the specified size
     */
    public ByteBuffer alloc(long size) {
        return alloc(size);
    }

    /**
     * <p>
     * Allocate buffer and copy data into it. Data will be freed inside bgfx.
     * </p>
     * 
     * @param dataPointer
     *            pointer to memory to allocate
     * @param size
     *            the byte size of the memory at the pointer
     * 
     * @return a direct ByteBuffer of the specified size
     */
    public ByteBuffer copy(long dataPointer, long size) {
        return ncopy(dataPointer, size);
    }

    /**
     * <p>
     * Make reference to data to pass to bgfx. Unlike `bgfx::alloc` this call
     * doesn't allocate memory for data. It just copies pointer to data. You can
     * pass `ReleaseFn` function pointer to release this memory after it's
     * consumed, or you must make sure data is available for at least 2
     * `bgfx::frame` calls. `ReleaseFn` function must be able to be called
     * called from any thread.
     * </p>
     * 
     * <b>This method needs some refactoring.</b>
     * 
     * @param data
     *            pointer to create a reference for
     * @param size
     *            the byte size of the memory at the pointer
     * @return a direct ByteBuffer of the specified size
     */
    public ByteBuffer makeRef(long data, long size) {
        return nmakeRef(data, size);
    }

    /**
     * <p>
     * Set debug flags.
     * </p>
     * 
     * <b>Note: these flags need to be OR'd together</b>
     * 
     * @param debug
     *            integer containing OR'd flags to set
     * 
     *            <b>Available flags:</b>
     *            <ul>
     *            <li><b>BGFX_DEBUG_IFH</b> - Infinitely fast hardware. When
     *            this flag is set all rendering calls will be skipped. It's
     *            useful when profiling to quickly assess bottleneck between CPU
     *            and GPU.</li>
     *            <li><b>BGFX_DEBUG_STATS</b> - Display internal statistics.
     *            </li>
     *            <li><b>BGFX_DEBUG_TEXT</b> - Display debug text.</li>
     *            <li><b>BGFX_DEBUG_WIREFRAME</b> - Wireframe rendering. All
     *            rendering primitives will be rendered as lines.</li>
     *            </ul>
     */
    public void setDebug(long debug) {
        nsetDebug(debug);
    }

    /**
     * <p>
     * Clear internal debug text buffer.
     * </p>
     * 
     * @param attr
     *            the attribute to clear
     * @param small
     *            small text
     */
    public void dbgTextClear(short attr, boolean small) {
        ndbgTextClear(attr, small);
    }

    /**
     * <p>
     * Clear internal debug text buffer.
     * </p>
     * <p>
     * Uses defaults (0, false)
     * </p>
     */
    public void dbgTextClear() {
        ndbgTextClear((short) 0, false);
    }

    /**
     * <p>
     * Print into internal debug text buffer
     * </p>
     * 
     * @param x
     *            the x coordinate to print to
     * @param y
     *            the y coordinate to print to
     * @param attr
     *            the attributes (color, etc) to print
     * @param format
     *            the string message
     */
    public void dbgTextPrintf(int x, int y, short attr, String format) {
        ndbgTextPrintf(x, y, attr, format);
    }

    /**
     * <p>
     * Draw image into internal debug text buffer.
     * </p>
     * 
     * @param x
     *            X position from top-left.
     * @param y
     *            Y position from top-left.
     * @param width
     *            Image width.
     * @param height
     *            Image height.
     * @param imageData
     *            Raw image data (character/attribute raw encoding).
     * @param pitch
     *            Image pitch in bytes
     */
    public void dbgTextImage(int x, int y, int width, int height, ByteBuffer imageData, int pitch) {
        throw new RuntimeException("Not Implemented!");
    }

    /**
     * <p>
     * Create static index buffer. Convenience method, prefer the ByteBuffer
     * implementation if possible.
     * </p>
     * 
     * <p>
     * NOTE: This method needs to be updated to support BGFX flags.
     * </p>
     * 
     * @param indices
     *            a short array containing index values
     * 
     * @return a GPU BGFX IndexBuffer handle containing indices from the
     *         supplied array
     */
    public IndexBuffer createIndexBuffer(short[] indices) {
        ByteBuffer indexData = ByteBuffer.allocateDirect(indices.length * 2).order(ByteOrder.nativeOrder());
        indexData.asShortBuffer().put(indices);
        indexData.rewind();

        return createIndexBuffer(indexData);
    }

    /**
     * <p>
     * Create static index buffer.
     * </p>
     * 
     * <p>
     * Note: This method needs to be updated to support BGFX flags.
     * </p>
     * 
     * @param mem
     *            a direct ByteBuffer containing index values
     * 
     * @return a GPU BGFX IndexBuffer handle containing indices from the
     *         supplied buffer
     */
    public IndexBuffer createIndexBuffer(ByteBuffer mem) {
        checkValidByteBuffer(mem);

        return new IndexBuffer(ncreateIndexBuffer(mem));
    }

    /**
     * <p>
     * Destroy static index buffer.
     * </p>
     * 
     * @param handle
     *            the buffer handle to destroy
     */
    public void destroyIndexBuffer(int handle) {
        ndestroyIndexBuffer(handle);
    }

    /**
     * <p>
     * Create static vertex buffer. Convenience method, prefer the ByteBuffer
     * version if possible.
     * </p>
     * 
     * <p>
     * Note: This method needs to be updated to support BGFX flags.
     * </p>
     * <p>
     * Note: this currently assumes 32-bit floats.
     * </p>
     * 
     * @param mem
     *            Vertex buffer data in a float array.
     * @param decl
     *            Vertex declaration.
     * @return Static vertex buffer handle.
     */
    public VertexBuffer createVertexBuffer(float[] mem, VertexDecl decl) {
        ByteBuffer vertexData = ByteBuffer.allocateDirect(mem.length * 4).order(ByteOrder.nativeOrder());
        vertexData.asFloatBuffer().put(mem);
        vertexData.rewind();

        return createVertexBuffer(vertexData, decl);
    }

    /**
     * <p>
     * Create static vertex buffer.
     * </p>
     * 
     * <p>
     * Note: This method needs to be updated to support BGFX flags.
     * </p>
     * 
     * @param mem
     *            Vertex buffer data in direct ByteBuffer
     * @param decl
     *            Vertex declaration.
     * @return Static vertex buffer handle.
     */
    public VertexBuffer createVertexBuffer(ByteBuffer mem, VertexDecl decl) {
        checkValidByteBuffer(mem);

        if (decl == null) {
            throw new NullPointerException(Messages.getString("BGFX.NullVertexDeclaration")); //$NON-NLS-1$
        }

        if (!decl.isValid()) {
            throw new BGFXException(Messages.getString("BGFX.InvalidVertexDeclaration")); //$NON-NLS-1$
        }

        int handle = ncreateVertexBuffer(mem, decl);

        return new VertexBuffer(handle);
    }

    /**
     * <p>
     * Destroy static vertex buffer.
     * </p>
     * 
     * @param handle
     *            the buffer handle to destroy
     */
    public void destroyVertexBuffer(VertexBuffer handle) {
        ndestroyVertexBuffer((int) handle.getHandle());
    }

    /**
     * <p>
     * Create dynamic index buffer.
     * </p>
     * 
     * <p>
     * Note: This method needs to be updated to support BGFX flags.
     * </p>
     * 
     * @param mem
     *            a direct ByteBuffer containing index values
     * 
     * @return a GPU BGFX IndexBuffer handle containing indices from the
     *         supplied buffer
     */
    public DynamicIndexBuffer createDynamicIndexBuffer(ByteBuffer mem) {
        checkValidByteBuffer(mem);

        int handle = ncreateDynamicIndexBuffer(mem);

        return new DynamicIndexBuffer(handle);
    }

    /**
     * <p>
     * Update dynamic index buffer.
     * </p>
     * 
     * @param handle
     *            Dynamic index buffer handle
     * @param mem
     *            Index buffer data.
     */
    public void updateDynamicIndexBuffer(DynamicIndexBuffer handle, ByteBuffer mem) {
        checkValidByteBuffer(mem);

        nupdateDynamicIndexBuffer((int) handle.getHandle(), mem);
    }

    /**
     * <p>
     * Destroy dynamic index buffer.
     * </p>
     * 
     * @param handle
     *            the buffer handle to destroy
     */
    public void destroyDynamicIndexBuffer(DynamicIndexBuffer handle) {
        ndestroyDynamicIndexBuffer((int) handle.getHandle());
    }

    /**
     * <p>
     * Create empty dynamic vertex buffer.
     * </p>
     * 
     * <p>
     * Note: This method needs to be updated to support BGFX flags.
     * </p>
     * 
     * @param num
     *            Number of vertices.
     * @param decl
     *            Vertex declaration.
     * @return Dynamic vertex buffer handle.
     */
    public DynamicVertexBuffer createDynamicVertexBuffer(int num, VertexDecl decl) {
        int bufferHandle = ncreateDynamicVertexBuffer(num, decl);

        if (bufferHandle == BGFX.INVALID_HANDLE) {
            throw new BGFXException(Messages.getString("BGFX.ErrorCreatingVertexBuffer") + bufferHandle); //$NON-NLS-1$
        }

        return new DynamicVertexBuffer(bufferHandle);
    }

    /**
     * <p>
     * Create dynamic vertex buffer. Convenience method, prefer the ByteBuffer
     * version if possible.
     * </p>
     * 
     * <p>
     * Note: This method needs to be updated to support BGFX flags.
     * </p>
     * 
     * @param mem
     *            Vertex buffer data in a float array
     * @param decl
     *            Vertex declaration.
     * @return Static vertex buffer handle.
     */
    public DynamicVertexBuffer createDynamicVertexBuffer(float[] mem, VertexDecl decl) {
        ByteBuffer vertexData = ByteBuffer.allocateDirect(mem.length * 4).order(ByteOrder.nativeOrder());
        vertexData.asFloatBuffer().put(mem);
        vertexData.rewind();

        return createDynamicVertexBuffer(vertexData, decl);
    }

    /**
     * <p>
     * Create dynamic vertex buffer.
     * </p>
     * 
     * <p>
     * Note: This method needs to be updated to support BGFX flags.
     * </p>
     * 
     * @param mem
     *            Vertex buffer data in direct ByteBuffer
     * @param decl
     *            Vertex declaration.
     * @return Static vertex buffer handle.
     */
    public DynamicVertexBuffer createDynamicVertexBuffer(ByteBuffer mem, VertexDecl decl) {
        int bufferHandle = ncreateDynamicVertexBuffer(mem, decl);

        if (bufferHandle == BGFX.INVALID_HANDLE) {
            throw new BGFXException(Messages.getString("BGFX.ErrorCreatingVertexBuffer") + bufferHandle); //$NON-NLS-1$
        }

        return new DynamicVertexBuffer(bufferHandle);
    }

    /**
     * <p>
     * Update dynamic vertex buffer.
     * </p>
     * 
     * @param handle
     *            Dynamic vertex buffer handle
     * @param mem
     *            vertex buffer data.
     */
    public void updateDynamicVertexBuffer(int handle, ByteBuffer mem) {
        checkValidByteBuffer(mem);

        nupdateDynamicVertexBuffer(handle, mem);
    }

    /**
     * <p>
     * Destroy dynamic vertex buffer.
     * </p>
     * 
     * @param handle
     *            the buffer handle to destroy
     */
    public void destroyDynamicVertexBuffer(int handle) {
        ndestroyDynamicVertexBuffer(handle);
    }

    /**
     * <p>
     * Returns true if internal transient index buffer has enough space.
     * </p>
     * 
     * @param num
     *            Number of indices.
     * @return true if internal transient index buffer has enough space.
     */
    public boolean checkAvailTransientIndexBuffer(long num) {
        return ncheckAvailTransientIndexBuffer(num);
    }

    /**
     * <p>
     * Returns true if internal transient vertex buffer has enough space.
     * </p>
     * 
     * @param num
     *            Number of vertices.
     * @param decl
     *            the Vertex Declaration
     * @return true if internal transient vertex buffer has enough space.
     */
    public boolean checkAvailTransientVertexBuffer(long num, VertexDecl decl) {
        return ncheckAvailTransientVertexBuffer(num, decl);
    }

    /**
     * <p>
     * Returns true if internal instance data buffer has enough space.
     * </p>
     * 
     * @param num
     *            Number of instances.
     * @param decl
     *            Stride per instance.
     * @return true if internal transient vertex buffer has enough space.
     */
    public boolean checkAvailInstanceDataBuffer(long num, int stride) {
        return ncheckAvailInstanceDataBuffer(num, stride);
    }

    /**
     * <p>
     * Returns true if both internal transient index and vertex buffer have
     * enough space.
     * </p>
     * 
     * @param numVertices
     *            Number of vertices.
     * @param decl
     *            the Vertex Declaration
     * @param numIndices
     *            Number of indices.
     * @return true if internal transient vertex buffer has enough space.
     */
    public boolean checkAvailTransientBuffers(long numVertices, VertexDecl decl, long numIndices) {
        return ncheckAvailTransientBuffers(numVertices, decl, numIndices);
    }

    /**
     * <p>
     * Allocate transient index buffer.
     * </p>
     * 
     * <ol>
     * <li>You must call setIndexBuffer after alloc in order to avoid memory
     * leak.</li>
     * <li>Only 16-bit index buffer is supported.</li>
     * </ol>
     * 
     * @param num
     *            Number of indices to allocate.
     * @return TransientIndexBuffer instance
     */
    public TransientIndexBuffer allocTransientIndexBuffer(long num) {
        TransientIndexBuffer buffer = nallocTransientIndexBuffer(num);

        long pointer = buffer.pointer;

        storeTransientIndexPointer(pointer);

        return buffer;
    }

    /**
     * <p>
     * Allocate transient vertex buffer.
     * </p>
     * 
     * <ol>
     * <li>You must call setVertexBuffer after alloc in order to avoid memory
     * leak.</li>
     * </ol>
     * 
     * @param num
     *            Number of vertices to allocate.
     * @return TransientVertexBuffer instance
     */
    public TransientVertexBuffer allocTransientVertexBuffer(long num, VertexDecl decl) {
        TransientVertexBuffer buffer = nallocTransientVertexBuffer(num, decl);

        long pointer = buffer.pointer;

        storeTransientVertexPointer(pointer);

        return buffer;
    }

    private void storeTransientVertexPointer(long pointer) {
        vertexPointerList.add(pointer);
    }

    private void storeTransientIndexPointer(long pointer) {
        indexPointerList.add(pointer);
    }

    /**
     * <p>
     * Allocate instance data buffer.
     * </p>
     * 
     * <p>
     * Note: You must call setInstanceDataBuffer after alloc in order to avoid
     * memory leak.
     * </p>
     * 
     * @param num
     *            the number of instances
     * @param stride
     *            the stride between instances
     * @return InstanceDataBuffer instance
     */
    public InstanceDataBuffer allocInstanceDataBuffer(long num, int stride) {
        return nallocInstanceDataBuffer(num, stride);
    }

    /**
     * <p>
     * Create shader from memory buffer.
     * </p>
     * 
     * @param mem
     *            ByteBuffer containing BGFX shader data
     * @return a ShaderHandle id
     */
    public int createShader(ByteBuffer mem) {
        checkValidByteBuffer(mem);

        return ncreateShader(mem);
    }

    /**
     * <p>
     * Note: This method needs more work to match up with the native use case.
     * </p>
     * 
     * @return Returns num of uniforms
     */
    public int getShaderUniforms(int handle, int uniforms, int max) {
        return ngetShaderUniforms(handle, uniforms, max);
    }

    /**
     * <p>
     * Destroy shader. Once program is created with shader it is safe to destroy
     * shader.
     * </p>
     * 
     * @param handle
     *            ShaderHandle id
     */
    public void destroyShader(int handle) {
        ndestroyShader(handle);
    }

    /**
     * <p>
     * Create program with vertex and fragment shaders.
     * </p>
     * 
     * @param vsh
     *            Vertex shader.
     * @param fsh
     *            Fragment shader.
     * @param destroyShaders
     *            If true, shaders will be destroyed when program is destroyed.
     * @return Program handle if vertex shader output and fragment shader input
     *         are matching, otherwise returns invalid program handle.
     */
    public int createProgram(int vsh, int fsh, boolean destroyShaders) {
        return ncreateProgram(vsh, fsh, destroyShaders);
    }

    /**
     * <p>
     * Destroy program.
     * </p>
     * 
     * @param handle
     */
    public void destroyProgram(int handle) {
        ndestroyProgram(handle);
    }

    /***
     * <p>
     * Note: This method needs more work to match up with the native use case.
     * </p>
     * 
     * @param info
     * @param width
     * @param height
     * @param depth
     * @param numMips
     * @param format
     */
    public void calcTextureSize(TextureInfo info, int width, int height, int depth, short numMips, TextureFormat format) {
        ncalcTextureSize(info, width, height, depth, numMips, format);
    }

    /**
     * <p>
     * Create texture from memory buffer.
     * </p>
     * 
     * @param mem
     *            DDS, KTX or PVR texture data.
     * @param flags
     *            Default texture sampling mode is linear, and wrap mode
     * @param skip
     *            Skip top level mips when parsing texture.
     * @param info
     *            When non-`NULL` is specified it returns parsed texture
     *            information.
     * @return Texture handle.
     */
    public int createTexture(ByteBuffer mem, long flags, int skip, TextureInfo info) {
        checkValidByteBuffer(mem);

        return ncreateTexture(mem, flags, (short) skip, info);
    }

    /**
     * <p>
     * Create 2D texture.
     * </p>
     * 
     * @param width
     *            Width.
     * @param height
     *            Height.
     * @param numMips
     *            Number of mip-maps.
     * @param format
     *            Texture format. See: `TextureFormat::Enum`.
     * @param flags
     *            Default texture sampling mode is linear, and wrap mode is
     *            repeat
     * @param mem
     *            ByteBuffer containing texture data
     * 
     *            <ul>
     *            <li>BGFX_TEXTURE_[U/V/W]_[MIRROR/CLAMP] - Mirror or clamp to
     *            edge wrap mode.</li>
     *            <li>BGFX_TEXTURE_[MIN/MAG/MIP]_[POINT/ANISOTROPIC] - Point or
     *            anisotropic sampling.</li>
     *            </ul>
     *
     * @return TextureHandle id for the new texture
     */
    public int createTexture2D(int width, int height, int numMips, TextureFormat format, long flags, ByteBuffer mem) {
        if (format == null) {
            throw new BGFXException(Messages.getString("BGFX.InvalidTextureFormat"));
        }

        if (mem != null && !mem.isDirect()) {
            throw new BGFXException(Messages.getString("BGFX.NonDirectBufferError")); //$NON-NLS-1$
        }

        return ncreateTexture2D(width, height, (short) numMips, format, flags, mem);
    }

    /**
     * <p>
     * Update 2D texture.
     * </p>
     * 
     * @param handle
     *            Texture handle.
     * @param mip
     *            Mip level.
     * @param x
     *            X offset in texture.
     * @param y
     *            Y offset in texture.
     * @param width
     *            Width of texture block.
     * @param height
     *            Height of texture block.
     * @param mem
     *            Texture update data.
     * @param pitch
     *            Pitch of input image (bytes). When pitch is set to
     *            UINT16_MAX, it will be calculated internally based on width.
     */
    public void updateTexture2D(int handle, short mip, int x, int y, int width, int height, ByteBuffer mem, int pitch) {
        nupdateTexture2D(handle, mip, x, y, width, height, mem, pitch);
    }

    /**
     * <p>
     * Create 3D texture.
     * </p>
     * 
     * @param width
     *            Width.
     * @param height
     *            Height.
     * @param depth
     *            Depth.
     * @param numMips
     *            Number of mip-maps.
     * @param format
     *            Texture format. See: `TextureFormat::Enum`.
     * @param flags
     *            Default texture sampling mode is linear, and wrap mode is
     *            repeat.
     * @param mem
     *            Texture data.
     * 
     * @return TextureHandle id
     */
    public int createTexture3D(int width, int height, int depth, short numMips, TextureFormat format, long flags, ByteBuffer mem) {
        return ncreateTexture3D(width, height, depth, numMips, format, flags, mem);
    }

    /**
     * <p>
     * Update 3D texture.
     * </p>
     * 
     * @param handle
     *            Texture handle.
     * @param mip
     *            Mip level.
     * @param x
     *            X offset in texture.
     * @param y
     *            Y offset in texture.
     * @param z
     *            Z offset in texture.
     * @param width
     *            Width of texture block.
     * @param height
     *            Height of texture block.
     * @param depth
     *            Depth of texture block.
     * @param mem
     *            Texture update data.
     */
    public void updateTexture3D(int handle, short mip, int x, int y, int z, int width, int height, int depth, ByteBuffer mem) {
        nupdateTexture3D(handle, mip, x, y, z, width, height, depth, mem);
    }

    /**
     * <p>
     * Create Cube texture.
     * </p>
     * 
     * @param size
     *            Cube side size.
     * @param numMips
     *            Number of mip-maps.
     * @param format
     *            Texture format. See: `TextureFormat::Enum`.
     * @param flags
     *            Default texture sampling mode is linear, and wrap mode is
     *            repeat.
     * @param mem
     *            Texture data.
     * 
     *            <ol>
     *            <il>BGFX_TEXTURE_[U/V/W]_[MIRROR/CLAMP] - Mirror or clamp to
     *            edge wrap mode.</il>
     *            <il>BGFX_TEXTURE_[MIN/MAG/MIP]_[POINT/ANISOTROPIC] - Point or
     *            anisotropic sampling.</il>
     *            </ol>
     * 
     * @return TextureHandle id
     */
    public int createTextureCube(int size, short numMips, TextureFormat format, long flags, ByteBuffer mem) {
        return ncreateTextureCube(size, numMips, format, flags, mem);
    }

    /**
     * <p>
     * Update Cube texture.
     * </p>
     * 
     * @param handle
     *            Texture handle.
     * @param side
     *            Cubemap side, where 0 is +X, 1 is -X, 2 is +Y, 3 is -Y, 4 is
     *            +Z, and 5 is -Z.
     * @param mip
     *            Mip level.
     * @param x
     *            X offset in texture.
     * @param y
     *            Y offset in texture.
     * @param width
     *            Width of texture block.
     * @param height
     *            Height of texture block.
     * @param mem
     *            Texture update data.
     * @param pitch
     *            Pitch of input image (bytes). When pitch is set to
     *            UINT16_MAX, it will be calculated internally based on width.
     * 
     *            <pre>
     *              +----------+
     *              |-z       2|
     *              | ^  +y    |
     *              | |        |
     *              | +---->+x |
     *   +----------+----------+----------+----------+
     *   |+y       1|+y       4|+y       0|+y       5|
     *   | ^  -x    | ^  +z    | ^  +x    | ^  -z    |
     *   | |        | |        | |        | |        |
     *   | +---->+z | +---->+x | +---->-z | +---->-x |
     *   +----------+----------+----------+----------+
     *              |+z       3|
     *              | ^  -y    |
     *              | |        |
     *              | +---->+x |
     *              +----------+
     *            </pre>
     */
    public void updateTextureCube(int handle, short side, short mip, int x, int y, int width, int height, ByteBuffer mem, int pitch) {
        nupdateTextureCube(handle, side, mip, x, y, width, height, mem, pitch);
    }

    /**
     * <p>
     * Destroy texture.
     * </p>
     * 
     * @param handle
     *            TextureHandle id of the texture to destroy
     */
    public void destroyTexture(int handle) {
        ndestroyTexture(handle);
    }

    /**
     * <p>
     * Create frame buffer (simple).
     * </p>
     * 
     * @param width
     *            Texture width.
     * @param height
     *            Texture height.
     * @param format
     *            Texture format. See: `TextureFormat::Enum`.
     * @param textureFlags
     *            Default texture sampling mode is linear, and wrap mode
     * @return FramebufferHandle id
     */
    public int createFrameBuffer(int width, int height, TextureFormat format, long textureFlags) {
        return ncreateFrameBuffer(width, height, format, textureFlags);
    }

    /**
     * <p>
     * Create a framebuffer with attachmennts.
     * </p>
     * 
     * @param num
     *            the number of texture attachments
     * @param handles
     *            array of TextureHandle ids
     * @param destroyTextures
     *            If true, textures will be destroyed when frame buffer is
     *            destroyed.
     * @return FramebufferHandle id
     */
    public int createFrameBuffer(int num, int[] handles, boolean destroyTextures) {
        return ncreateFrameBuffer(num, handles, destroyTextures);
    }

    /**
     * <p>
     * Create frame buffer for multiple window rendering.
     * </p>
     * 
     * @param window
     *            OS' target native window handle.
     * @param width
     *            Window back buffer width.
     * @param height
     *            Window back buffer height.
     * @param format
     *            Window back buffer depth format.
     * 
     * @return FramebufferHandle id
     */
    public int createFrameBuffer(Window window, int width, int height, TextureFormat format) {
        return ncreateFrameBuffer(window.getNativeHandle(), width, height, format);
    }

    /**
     * <p>
     * Destroy frame buffer.
     * </p>
     * 
     * @param handle
     */
    public void destroyFrameBuffer(int handle) {
        ndestroyFrameBuffer(handle);
    }

    /**
     * <p>
     * Create shader uniform parameter.
     * </p>
     * 
     * @param name
     *            Uniform name in shader.
     * @param type
     *            Type of uniform (See: `bgfx::UniformType`).
     * @param num
     *            Number of elements in array.
     * @return Handle to uniform object.
     * 
     *         Predefined uniforms (declared in `bgfx_shader.sh`):
     * 
     *         <ul>
     *         <li>u_viewRect vec4(x, y, width, height) - view rectangle for
     *         current view.</li>
     *         <li>u_viewTexel vec4(1.0/width, 1.0/height, undef, undef) -
     *         inverse width and height</li>
     *         <li>u_view mat4 - view matrix</li>
     *         <li>u_invView mat4 - inverted view matrix</li>
     *         <li>u_proj mat4 - projection matrix</li>
     *         <li>u_invProj mat4 - inverted projection matrix</li>
     *         <li>u_viewProj mat4 - concatenated view projection matrix</li>
     *         <li>u_invViewProj mat4 - concatenated inverted view projection
     *         matrix</li>
     *         <li>u_model mat4[BGFX_CONFIG_MAX_BONES] - array of model
     *         matrices.</li>
     *         <li>u_modelView mat4 - concatenated model view matrix, only first
     *         model matrix from array is used.</li>
     *         <li>u_modelViewProj mat4 - concatenated model view projection
     *         matrix.</li>
     *         <li>u_alphaRef float` - alpha reference value for alpha test.
     *         </li>
     *         </ul>
     */
    public int createUniform(String name, UniformType type, int num) {
        return ncreateUniform(name, type.ordinal(), num);
    }

    /**
     * <p>
     * Destroys the uniform.
     * </p>
     * 
     * @param handle
     *            UniformHandle id
     */
    public void destroyUniform(int handle) {
        ndestroyUniform(handle);
    }

    /**
     * <p>
     * Set view name
     * </p>
     * 
     * @param id
     *            View id.
     * @param name
     *            View name.
     * 
     *            <pre>
     * This is debug only feature.
     *
     *   In graphics debugger view name will appear as:
     *
     *     "nnnce <view name>"
     *      ^  ^^ ^
     *      |  |+-- eye (L/R)
     *      |  +-- compute (C)
     *      +-- view id
     *            </pre>
     */
    public void setViewName(int id, String name) {
        nsetViewName((short) id, name);
    }

    /**
     * <p>
     * Set view rectangle. Draw primitive outside view will be clipped.
     * </p>
     * 
     * @param id
     *            View id.
     * @param x
     *            Position x from the left corner of the window.
     * @param y
     *            Position y from the top corner of the window.
     * @param width
     *            Width of view port region.
     * @param height
     *            Height of view port region.
     */
    public void setViewRect(int id, int x, int y, int width, int height) {
        nsetViewRect((short) id, x, y, width, height);
    }

    /**
     * <p>
     * Set view scissor. Draw primitive outside view will be clipped. When x,
     * y, width and height are set to 0, scissor will be disabled.
     * </p>
     * 
     * @param id
     *            View id
     * @param x
     *            Position x from the left corner of the window.
     * @param y
     *            Position y from the top corner of the window.
     * @param width
     *            Width of scissor region.
     * @param height
     *            Height of scissor region.
     */
    public void setViewScissor(int id, int x, int y, int width, int height) {
        nsetViewScissor((short) id, x, y, width, height);
    }

    /**
     * <p>
     * Set view clear flags.
     * </p>
     * 
     * @param id
     *            View id.
     * @param flags
     *            Clear flags. Use `BGFX_CLEAR_NONE` to remove any clear
     *            operation. See: `BGFX_CLEAR_*`.
     * @param rgba
     *            Color clear value.
     * @param depth
     *            Depth clear value.
     * @param stencil
     *            Stencil clear value.
     */
    public void setViewClear(int id, long flags, long rgba, float depth, int stencil) {
        nsetViewClear((short) id, (short) flags, rgba, depth, (short) stencil);
    }

    /**
     * <p>
     * Set view into sequential mode. Draw calls will be sorted in the same
     * order in which submit calls were called.
     * </p>
     * 
     * <p>
     * This is particularly useful for rendering GUI systems.
     * </p>
     * 
     * @param id
     *            view id
     * @param enabled
     *            if true, all render calls will retain their sequential order
     *            and will not be sorted for better performance.
     */
    public void setViewSeq(int id, boolean enabled) {
        nsetViewSeq((short) id, enabled);
    }

    /**
     * <p>
     * Set view frame buffer.
     * </p>
     * 
     * @param id
     *            View id.
     * @param handle
     *            Frame buffer handle. Passing `BGFX_INVALID_HANDLE` as frame
     *            buffer handle will draw primitives from this view into
     */
    public void setViewFrameBuffer(int id, int handle) {
        nsetViewFrameBuffer((short) id, handle);
    }

    /**
     * <p>
     * Set view view and projection matrices, all draw primitives in this view
     * will use these matrices.
     * </p>
     * 
     * @param id
     *            View id.
     * @param view
     *            View matrix.
     * @param proj
     *            Projection matrix. When using stereo rendering this projection
     *            matrix represent projection matrix for left eye.
     */
    public void setViewTransform(int id, float[] view, float[] proj) {
        nsetViewTransform((short) id, view, proj);
    }

    /**
     * <p>
     * Sets debug marker.
     * </p>
     * 
     * @param marker
     *            marker text
     */
    public void setMarker(String marker) {
        nsetMarker(marker);
    }

    /**
     * <p>
     * Set render states for draw primitive.
     * </p>
     * 
     * @param state
     *            State flags. Default state for primitive type is triangles.
     * @param rgba
     *            Sets blend factor used by `BGFX_STATE_BLEND_FACTOR`
     *            and`BGFX_STATE_BLEND_INV_FACTOR` blend modes.
     * 
     *            See: `BGFX_STATE_DEFAULT`.
     * 
     *            <ul>
     *            <li>BGFX_STATE_ALPHA_WRITE` - Enable alpha write.</li>
     *            <li>BGFX_STATE_DEPTH_WRITE` - Enable depth write.</li>
     *            <li>BGFX_STATE_DEPTH_TEST_*` - Depth test function.</li>
     *            <li>BGFX_STATE_BLEND_*` - See remark 1 about
     *            BGFX_STATE_BLEND_FUNC.</li>
     *            <li>BGFX_STATE_BLEND_EQUATION_*` - See remark 2.</li>
     *            <li>BGFX_STATE_CULL_*` - Backface culling mode.</li>
     *            <li>BGFX_STATE_RGB_WRITE` - Enable RGB write.</li>
     *            <li>BGFX_STATE_MSAA` - Enable MSAA.</li>
     *            <li>BGFX_STATE_PT_[TRISTRIP/LINES/POINTS]` - Primitive type.
     *            </li>
     *            </ul>
     * 
     *            <ol>
     *            <li>Use `BGFX_STATE_ALPHA_REF`, `BGFX_STATE_POINT_SIZE` and
     *            `BGFX_STATE_BLEND_FUNC` macros to setup more complex states.
     *            </li>
     *            <li>`BGFX_STATE_BLEND_EQUATION_ADD` is set when no other blend
     *            equation is specified.</li>
     *            </ol>
     * 
     */
    public void setState(long state, long rgba) {
        nsetState(state, rgba);
    }

    /**
     * <p>
     * Set stencil test state.
     * </p>
     * 
     * @param fstencil
     *            Front stencil state.
     * @param bstencil
     *            Back stencil state. If back is set to `BGFX_STENCIL_NONE`
     *            fstencil is applied to both front and back facing primitives.
     */
    public void setStencil(long fstencil, long bstencil) {
        nsetStencil(fstencil, bstencil);
    }

    /**
     * <p>
     * Set scissor for draw primitive. For scissor for all primitives in view
     * see setViewScissor.
     * </p>
     * 
     * @param x
     *            Position x from the left corner of the window.
     * @param y
     *            Position y from the top corner of the window.
     * @param width
     *            Width of scissor region.
     * @param height
     *            Height of scissor region.
     * 
     * @return Scissor cache index.
     */
    public int setScissor(int x, int y, int width, int height) {
        return nsetScissor(x, y, width, height);
    }

    /**
     * <p>
     * Set scissor from cache for draw primitive.
     * </p>
     * 
     * @param cache
     *            Index in scissor cache. Passing UINT16_MAX unset primitive
     *            scissor and primitive will use view scissor instead.
     */
    public void setScissor(int cache) {
        nsetScissor(cache);
    }

    /**
     * <p>
     * Set model matrix for draw primitive. If it is not called model will be
     * rendered with identity model matrix.
     * </p>
     * 
     * @param mtx
     *            Pointer to first matrix in array.
     * @param num
     *            Number of matrices in array.
     * @return index into matrix cache in case the same model matrix has to be
     *         used for other draw primitive call.
     */
    public long setTransform(float[] mtx, int num) {
        return nsetTransform(mtx, num);
    }

    /**
     * <p>
     * Set shader uniform parameter for draw primitive.
     * </p>
     * 
     * @param handle
     *            handle Uniform.
     * @param buffer
     *            direct ByteBuffer containing uniform data.
     * @param num
     *            Number of elements.
     */
    public void setUniform(int handle, ByteBuffer buffer, int num) {
        nsetUniform(handle, buffer, num);
    }

    /**
     * <p>
     * Set shader uniform parameter for draw primitive. Convenience method.
     * </p>
     * 
     * @param handle
     *            handle Uniform.
     * @param value
     *            array containing containing uniform data.
     * @param num
     *            Number of elements.
     */
    public void setUniform(int handle, float[] value, int num) {
        nsetUniform(handle, value, num);
    }

    /**
     * <p>
     * Set shader uniform parameter for draw primitive.
     * </p>
     * 
     * @param handle
     *            handle Uniform.
     * @param value
     *            float value
     * @param num
     *            Number of elements.
     */
    public void setUniform(int handle, float value, int num) {
        nsetUniform(handle, value, num);
    }

    /**
     * <p>
     * Set index buffer for draw primitive.
     * </p>
     * 
     * @param handle
     *            Index buffer.
     * @param firstIndex
     *            First index to render.
     * @param numIndices
     *            Number of indices to render.
     */
    public void setIndexBuffer(IndexBuffer handle, long firstIndex, long numIndices) {
        nsetIndexBuffer(handle, firstIndex, numIndices);
    }

    /**
     * <p>
     * Set index buffer for draw primitive.
     * </p>
     * 
     * @param handle
     *            Index buffer.
     * @param firstIndex
     *            First index to render.
     * @param numIndices
     *            Number of indices to render.
     */
    public void setIndexBuffer(DynamicIndexBuffer handle, long firstIndex, long numIndices) {
        nsetIndexBuffer(handle, firstIndex, numIndices);
    }

    /**
     * <p>
     * Set index buffer for draw primitive.
     * </p>
     * 
     * @param handle
     *            Index buffer.
     */
    public void setIndexBuffer(TransientIndexBuffer tib) {
        nsetIndexBuffer(tib);
    }

    /**
     * <p>
     * Set index buffer for draw primitive.
     * </p>
     * 
     * @param handle
     *            Index buffer.
     * @param firstIndex
     *            First index to render.
     * @param numIndices
     *            Number of indices to render.
     */
    public void setIndexBuffer(TransientIndexBuffer tib, long firstIndex, long numIndices) {
        nsetIndexBuffer(tib, firstIndex, numIndices);
    }

    /**
     * <p>
     * Set vertex buffer for draw primitive.
     * </p>
     * 
     * @param buffer
     *            Vertex buffer.
     */
    public void setVertexBuffer(VertexBuffer buffer) {
        nsetVertexBuffer(buffer);
    }

    /**
     * <p>
     * Set vertex buffer for draw primitive.
     * </p>
     * 
     * @param handle
     *            Vertex buffer.
     * @param startVertex
     *            First vertex to render.
     * @param numVertices
     *            Number of vertices to render.
     */
    public void setVertexBuffer(VertexBuffer handle, long startVertex, long numVertices) {
        nsetVertexBuffer(handle, startVertex, numVertices);
    }

    /**
     * <p>
     * Set vertex buffer for draw primitive.
     * </p>
     * 
     * @param handle
     *            Vertex buffer.
     * @param numVertices
     *            Number of vertices to render.
     */
    public void setVertexBuffer(DynamicVertexBuffer handle, long numVertices) {
        nsetVertexBuffer(handle, numVertices);
    }

    /**
     * <p>
     * Set vertex buffer for draw primitive.
     * </p>
     * 
     * @param handle
     *            Vertex buffer.
     */
    public void setVertexBuffer(TransientVertexBuffer tvb) {
        nsetVertexBuffer(tvb);
    }

    /**
     * <p>
     * Set vertex buffer for draw primitive.
     * </p>
     * 
     * @param handle
     *            Vertex buffer.
     * @param startVertex
     *            First vertex to render.
     * @param numVertices
     *            Number of vertices to render.
     */
    public void setVertexBuffer(TransientVertexBuffer tvb, long startVertex, long numVertices) {
        nsetVertexBuffer(tvb, startVertex, numVertices);
    }

    /**
     * <p>
     * Set instance data buffer for draw primitive.
     * </p>
     * 
     * @param idb
     *            instance buffer
     * @param num
     *            number of instances
     */
    public void setInstanceDataBuffer(InstanceDataBuffer idb, int num) {
        nsetInstanceDataBuffer(idb, num);
    }

    /**
     * <p>
     * Set program for draw primitive.
     * </p>
     * 
     * @param handle
     *            ProgramHandle ind
     */
    public void setProgram(int handle) {
        nsetProgram(handle);
    }

    /**
     * <p>
     * Set texture stage for draw primitive.
     * </p>
     * 
     * @param stage
     *            Texture unit.
     * @param sampler
     *            Program sampler.
     * @param handle
     *            Texture handle.
     * @param flags
     *            Texture sampling mode. Default value UINT32_MAX uses texture
     *            sampling settings from the texture.
     * 
     *            <ul>
     *            <li>BGFX_TEXTURE_[U/V/W]_[MIRROR/CLAMP] - Mirror or clamp to
     *            edge wrap mode.</li>
     *            <li>BGFX_TEXTURE_[MIN/MAG/MIP]_[POINT/ANISOTROPIC] - Point or
     *            anisotropic sampling.</li>
     *            </ul>
     */
    public void setTexture(int stage, int sampler, int handle, long flags) {
        nsetTexture((short) stage, sampler, handle, flags);
    }

    /**
     * <p>
     * Set texture stage for draw primitive.
     * </p>
     * 
     * @param stage
     *            Texture unit.
     * @param sampler
     *            Program sampler.
     * @param handle
     *            Frame buffer handle.
     * @param attachment
     *            Attachment index.
     * @param flags
     *            Texture sampling mode. Default value UINT32_MAX uses texture
     *            sampling settings from the texture.
     * 
     *            <ul>
     *            <li>BGFX_TEXTURE_[U/V/W]_[MIRROR/CLAMP] - Mirror or clamp to
     *            edge wrap mode.</li>
     *            <li>BGFX_TEXTURE_[MIN/MAG/MIP]_[POINT/ANISOTROPIC] - Point or
     *            anisotropic sampling.</li>
     *            </ul>
     */
    public void setTexture(short stage, int sampler, int handle, short attachment, long flags) {
        nsetTexture(stage, sampler, handle, attachment, flags);
    }

    /**
     * <p>
     * Submit primitive for rendering.
     * </p>
     * 
     * @param id
     *            View id.
     * @return Number of draw calls.
     */
    public long submit(int id) {
        return submit(id, 0);
    }

    /**
     * <p>
     * Submit primitive for rendering.
     * </p>
     * 
     * @param id
     *            View id.
     * @param depth
     *            Depth for sorting.
     * 
     * @return Number of draw calls.
     */
    public long submit(int id, int depth) {
        return nsubmit((short) id, (short) depth);
    }

    /**
     * <p>
     * Discard all previously set state for draw or compute call.
     * </p>
     */
    public void discard() {
        ndiscard();
    }

    /**
     * <p>
     * Request screen shot.
     * </p>
     * 
     * @param filePath
     *            Will be passed to `bgfx::CallbackI::screenShot` callback.
     * 
     *            <p>
     *            <b>Note:</b> `bgfx::CallbackI::screenShot` must be
     *            implemented.
     *            </p>
     */
    public void saveScreenShot(File filePath) {
        nsaveScreenShot(filePath.getAbsolutePath());
    }

    private void freeTransientIndexPointers() {
        int indexPointerSize = indexPointerList.size();

        if (indexPointerSize == 0) {
            return;
        }

        long[] pointers = new long[indexPointerSize];

        for (int i = 0; i < indexPointerSize; i++) {
            pointers[i] = indexPointerList.get(i);
        }

        indexPointerList.clear();

        nfreeTransientIndexBuffers(pointers);
    }

    private void freeTransientVertexPointers() {
        int vertexPointerSize = vertexPointerList.size();

        if (vertexPointerSize == 0) {
            return;
        }

        long[] pointers = new long[vertexPointerSize];

        for (int i = 0; i < vertexPointerSize; i++) {
            pointers[i] = vertexPointerList.get(i);
        }

        vertexPointerList.clear();

        nfreeTransientVertexBuffers(pointers);
    }

    protected native void nfreeTransientIndexBuffers(long[] pointers);

    protected native void nfreeTransientVertexBuffers(long[] pointers);

    protected native void nvertexPack(float _input[], boolean _inputNormalized, Attrib _attr, VertexDecl _decl, long _data, long _index);

    protected native void nvertexUnpack(float _output, Attrib _attr, VertexDecl _decl, long _data, long _index);

    protected native void nvertexConvert(VertexDecl _destDecl, long _destData, VertexDecl _srcDecl, long _srcData, long _num);

    protected native int nweldVertices(long _output, VertexDecl _decl, long _data, int _num, float _epsilon);

    protected native void nimageSwizzleBgra8(long _width, long _height, long _pitch, long _src, long _dst);

    protected native void nimageRgba8Downsample2x2(long _width, long _height, long _pitch, long _src, long _dst);

    protected native int ngetRendererType();

    protected native void ninit(int rendererType, int vendorId, int deviceId);

    protected native void nshutdown();

    protected native void nreset(long _width, long _height, long _flags);

    protected native long nframe();

    protected native Caps ngetCaps();

    protected native ByteBuffer nalloc(long _size);

    protected native ByteBuffer ncopy(long _data, long _size);

    protected native ByteBuffer nmakeRef(long _data, long _size);

    protected native void nsetDebug(long _debug);

    protected native void ndbgTextClear(short _attr, boolean _small);

    protected native void ndbgTextPrintf(int _x, int _y, short _attr, String _format);

    protected native int ncreateIndexBuffer(ByteBuffer _mem);

    protected native void ndestroyIndexBuffer(int _handle);

    protected native int ncreateVertexBuffer(ByteBuffer _mem, VertexDecl _decl);

    protected native void ndestroyVertexBuffer(int _handle);

    protected native int ncreateDynamicIndexBuffer(long _num);

    protected native int ncreateDynamicIndexBuffer(ByteBuffer _mem);

    protected native void nupdateDynamicIndexBuffer(int _handle, ByteBuffer _mem);

    protected native void ndestroyDynamicIndexBuffer(int _handle);

    protected native int ncreateDynamicVertexBuffer(int _handle, VertexDecl _decl);

    protected native int ncreateDynamicVertexBuffer(ByteBuffer _mem, VertexDecl _decl);

    protected native void nupdateDynamicVertexBuffer(int _handle, ByteBuffer _mem);

    protected native void ndestroyDynamicVertexBuffer(int _handle);

    protected native boolean ncheckAvailTransientIndexBuffer(long _num);

    protected native boolean ncheckAvailTransientVertexBuffer(long _num, VertexDecl _decl);

    protected native boolean ncheckAvailInstanceDataBuffer(long _num, int _stride);

    protected native boolean ncheckAvailTransientBuffers(long _numVertices, VertexDecl _decl, long _numIndices);

    protected native TransientIndexBuffer nallocTransientIndexBuffer(long _num);

    protected native TransientVertexBuffer nallocTransientVertexBuffer(long _num, VertexDecl _decl);

    protected native InstanceDataBuffer nallocInstanceDataBuffer(long _num, int _stride);

    protected native int ncreateShader(ByteBuffer _mem);

    protected native int ngetShaderUniforms(int _handle, int _uniforms, int _max);

    protected native void ndestroyShader(int _handle);

    protected native int ncreateProgram(int _vsh, int _fsh, boolean _destroyShaders);

    protected native void ndestroyProgram(int _handle);

    protected native void ncalcTextureSize(TextureInfo _info, int _width, int _height, int _depth, short _numMips, TextureFormat _format);

    protected native int ncreateTexture(ByteBuffer _mem, long _flags, short _skip, TextureInfo _info);

    protected native int ncreateTexture2D(int _width, int _height, short _numMips, TextureFormat _format, long _flags, ByteBuffer _mem);

    protected native int ncreateTexture3D(int _width, int _height, int _depth, short _numMips, TextureFormat _format, long _flags, ByteBuffer _mem);

    protected native int ncreateTextureCube(int _size, short _numMips, TextureFormat _format, long _flags, ByteBuffer _mem);

    protected native void nupdateTexture2D(int _handle, short _mip, int _x, int _y, int _width, int _height, ByteBuffer _mem, int _pitch);

    protected native void nupdateTexture3D(int _handle, short _mip, int _x, int _y, int _z, int _width, int _height, int _depth, ByteBuffer _mem);

    protected native void nupdateTextureCube(int _handle, short _side, short _mip, int _x, int _y, int _width, int _height, ByteBuffer _mem, int _pitch);

    protected native void ndestroyTexture(int _handle);

    protected native int ncreateFrameBuffer(long nativeWindowHandle, int _width, int _height, TextureFormat _format);

    protected native int ncreateFrameBuffer(int _width, int _height, TextureFormat _format, long _textureFlags);

    protected native int ncreateFrameBuffer(int _num, int[] _handles, boolean _destroyTextures);

    protected native void ndestroyFrameBuffer(int _handle);

    protected native int ncreateUniform(String _name, int _type, int _num);

    protected native void ndestroyUniform(int _handle);

    protected native void nsetViewName(short _id, String _name);

    protected native void nsetViewRect(short _id, int _x, int _y, int _width, int _height);

    protected native void nsetViewRectMask(long _viewMask, int _x, int _y, int _width, int _height);

    protected native void nsetViewScissor(short _id, int _x, int _y, int _width, int _height);

    protected native void nsetViewScissorMask(long _viewMask, int _x, int _y, int _width, int _height);

    protected native void nsetViewClear(short _id, short _flags, long _rgba, float _depth, short _stencil);

    protected native void nsetViewClearMask(long _viewMask, short _flags, long _rgba, float _depth, short _stencil);

    protected native void nsetViewSeq(short _id, boolean _enabled);

    protected native void nsetViewSeqMask(long _viewMask, boolean _enabled);

    protected native void nsetViewFrameBuffer(short _id, int _handle);

    protected native void nsetViewFrameBufferMask(long _viewMask, int _handle);

    protected native void nsetViewTransform(short _id, float[] _view, float[] _proj);

    protected native void nsetViewTransformMask(long _viewMask, long _view, long _proj, short _otherxff);

    protected native void nsetMarker(String _marker);

    protected native void nsetState(long _state, long _rgba);

    protected native void nsetStencil(long _fstencil, long _bstencil);

    protected native int nsetScissor(int _x, int _y, int _width, int _height);

    protected native void nsetScissor(int _cache);

    protected native long nsetTransform(float[] _mtx, int _num);

    protected native void nsetUniform(int _handle, ByteBuffer _value, int _num);

    protected native void nsetUniform(int _handle, float _value, int _num);

    protected native void nsetUniform(int _handle, float[] _value, int _num);

    protected native void nsetIndexBuffer(IndexBuffer _handle, long _firstIndex, long _numIndices);

    protected native void nsetIndexBuffer(DynamicIndexBuffer _handle, long _firstIndex, long _numIndices);

    protected native void nsetIndexBuffer(TransientIndexBuffer _tib);

    protected native void nsetIndexBuffer(TransientIndexBuffer _tib, long _firstIndex, long _numIndices);

    protected native void nsetDynamicVertexBuffer(int _handle);

    protected native void nsetVertexBuffer(VertexBuffer _handle);

    protected native void nsetVertexBuffer(VertexBuffer _handle, long _startVertex, long _numVertices);

    protected native void nsetVertexBuffer(DynamicVertexBuffer _handle, long _numVertices);

    protected native void nsetVertexBuffer(TransientVertexBuffer _tvb);

    protected native void nsetVertexBuffer(TransientVertexBuffer _tvb, long _startVertex, long _numVertices);

    protected native void nsetInstanceDataBuffer(InstanceDataBuffer _idb, int _num);

    protected native void nsetProgram(int _handle);

    protected native void nsetTexture(short _stage, int _sampler, int _handle, long _flags);

    protected native void nsetTexture(short _stage, int _sampler, int _handle, short _attachment, long _flags);

    protected native long nsubmit(short _id, short depth);

    protected native long nsubmitMask(long _viewMask, int _depth);

    protected native void ndiscard();

    protected native void nsaveScreenShot(String _filePath);

    protected static long blendFunc(long src, long dst) {
        return ndoBlendFunc(src, dst, src, dst);
    }

    protected static long blendFuncSeparate(long srcRGB, long dstRGB, long srcA, long dstA) {
        return ndoBlendFunc(srcRGB, dstRGB, srcA, dstA);
    }

    protected native static long ndoBlendFunc(long srcRGB, long dstRGB, long srcA, long dstA);

    /// BGFX States
    public static final long BGFX_PCI_ID_NONE = 0x0000000000000000L;
    public static final long BGFX_PCI_ID_SOFTWARE_RASTERIZER = 0x0000000000000001L;
    public static final long BGFX_PCI_ID_AMD = 0x0000000000001002L;
    public static final long BGFX_PCI_ID_INTEL = 0x0000000000008086L;
    public static final long BGFX_PCI_ID_NVIDIA = 0x00000000000010deL;

    public static final long BGFX_STATE_RGB_WRITE = 0x0000000000000001L;
    public static final long BGFX_STATE_ALPHA_WRITE = 0x0000000000000002L;
    public static final long BGFX_STATE_DEPTH_WRITE = 0x0000000000000004L;

    public static final long BGFX_STATE_DEPTH_TEST_LESS = 0x0000000000000010L;
    public static final long BGFX_STATE_DEPTH_TEST_LEQUAL = 0x0000000000000020L;
    public static final long BGFX_STATE_DEPTH_TEST_EQUAL = 0x0000000000000030L;
    public static final long BGFX_STATE_DEPTH_TEST_GEQUAL = 0x0000000000000040L;
    public static final long BGFX_STATE_DEPTH_TEST_GREATER = 0x0000000000000050L;
    public static final long BGFX_STATE_DEPTH_TEST_NOTEQUAL = 0x0000000000000060L;
    public static final long BGFX_STATE_DEPTH_TEST_NEVER = 0x0000000000000070L;
    public static final long BGFX_STATE_DEPTH_TEST_ALWAYS = 0x0000000000000080L;
    public static final long BGFX_STATE_DEPTH_TEST_SHIFT = 4L;
    public static final long BGFX_STATE_DEPTH_TEST_MASK = 0x00000000000000f0L;

    public static final long BGFX_STATE_BLEND_ZERO = 0x0000000000001000L;
    public static final long BGFX_STATE_BLEND_ONE = 0x0000000000002000L;
    public static final long BGFX_STATE_BLEND_SRC_COLOR = 0x0000000000003000L;
    public static final long BGFX_STATE_BLEND_INV_SRC_COLOR = 0x0000000000004000L;
    public static final long BGFX_STATE_BLEND_SRC_ALPHA = 0x0000000000005000L;
    public static final long BGFX_STATE_BLEND_INV_SRC_ALPHA = 0x0000000000006000L;
    public static final long BGFX_STATE_BLEND_DST_ALPHA = 0x0000000000007000L;
    public static final long BGFX_STATE_BLEND_INV_DST_ALPHA = 0x0000000000008000L;
    public static final long BGFX_STATE_BLEND_DST_COLOR = 0x0000000000009000L;
    public static final long BGFX_STATE_BLEND_INV_DST_COLOR = 0x000000000000a000L;
    public static final long BGFX_STATE_BLEND_SRC_ALPHA_SAT = 0x000000000000b000L;
    public static final long BGFX_STATE_BLEND_FACTOR = 0x000000000000c000L;
    public static final long BGFX_STATE_BLEND_INV_FACTOR = 0x000000000000d000L;
    public static final long BGFX_STATE_BLEND_SHIFT = 12L;
    public static final long BGFX_STATE_BLEND_MASK = 0x000000000ffff000L;

    public static final long BGFX_STATE_BLEND_EQUATIOnSUB = 0x0000000010000000L;
    public static final long BGFX_STATE_BLEND_EQUATIOnREVSUB = 0x0000000020000000L;
    public static final long BGFX_STATE_BLEND_EQUATIOnMIN = 0x0000000030000000L;
    public static final long BGFX_STATE_BLEND_EQUATIOnMAX = 0x0000000040000000L;
    public static final long BGFX_STATE_BLEND_EQUATIOnSHIFT = 28L;
    public static final long BGFX_STATE_BLEND_EQUATIOnMASK = 0x00000003f0000000L;

    public static final long BGFX_STATE_BLEND_INDEPENDENT = 0x0000000400000000L;

    public static final long BGFX_STATE_CULL_CW = 0x0000001000000000L;
    public static final long BGFX_STATE_CULL_CCW = 0x0000002000000000L;
    public static final long BGFX_STATE_CULL_SHIFT = 36L;
    public static final long BGFX_STATE_CULL_MASK = 0x0000003000000000L;

    public static final long BGFX_STATE_ALPHA_REF_SHIFT = 40L;
    public static final long BGFX_STATE_ALPHA_REF_MASK = 0x0000ff0000000000L;

    public static final long BGFX_STATE_PT_TRISTRIP = 0x0001000000000000L;
    public static final long BGFX_STATE_PT_LINES = 0x0002000000000000L;
    public static final long BGFX_STATE_PT_POINTS = 0x0003000000000000L;
    public static final long BGFX_STATE_PT_SHIFT = 48L;
    public static final long BGFX_STATE_PT_MASK = 0x0003000000000000L;

    public static final long BGFX_STATE_POINT_SIZE_SHIFT = 52L;
    public static final long BGFX_STATE_POINT_SIZE_MASK = 0x0ff0000000000000L;

    public static final long BGFX_STATE_MSAA = 0x1000000000000000L;

    public static final long BGFX_STATE_RESERVED_MASK = 0xe000000000000000L;

    public static final long BGFX_STATE_NONE = 0x0000000000000000L;
    public static final long BGFX_STATE_MASK = 0xffffffffffffffffL;

    public static final long BGFX_STATE_DEFAULT = 0 | BGFX_STATE_RGB_WRITE | BGFX_STATE_ALPHA_WRITE | BGFX_STATE_DEPTH_TEST_LESS | BGFX_STATE_DEPTH_WRITE | BGFX_STATE_MSAA;

    // public static final long BGFX_STATE_ALPHA_REF(_ref; (
    // (uint64_t(_ref;<<BGFX_STATE_ALPHA_REF_SHIFT;&BGFX_STATE_ALPHA_REF_MASK;
    // public static final long BGFX_STATE_POINT_SIZE(_size; (
    // (uint64_t(_size;<<BGFX_STATE_POINT_SIZE_SHIFT;&BGFX_STATE_POINT_SIZE_MASK;

    ///
    // public static final long BGFX_STATE_BLEND_FUNC_SEPARATE(_srcRGB, _dstRGB,
    /// _srcA, _dstA; (0 \
    // | ( (uint64_t(_srcRGB;|(uint64_t(_dstRGB;<<4; ; ; \
    // | ( (uint64_t(_srcA ;|(uint64_t(_dstA ;<<4; ;<<8; \
    // ;

    // public static final long BGFX_STATE_BLEND_EQUATIOnSEPARATE(_rgb, _a;
    // (uint64_t(_rgb;|(uint64_t(_a;<<3; ;

    ///
    // public static final long BGFX_STATE_BLEND_FUNC(_src, _dst;
    /// BGFX_STATE_BLEND_FUNC_SEPARATE(_src, _dst, _src, _dst;
    // public static final long BGFX_STATE_BLEND_EQUATION(_equation;
    /// BGFX_STATE_BLEND_EQUATIOnSEPARATE(_equation, _equation;

    public static final long BGFX_STATE_BLEND_ADD = blendFunc(BGFX_STATE_BLEND_ONE, BGFX_STATE_BLEND_ONE);
    public static final long BGFX_STATE_BLEND_ALPHA = blendFunc(BGFX_STATE_BLEND_SRC_ALPHA, BGFX_STATE_BLEND_INV_SRC_ALPHA);
    public static final long BGFX_STATE_BLEND_DARKEN = blendFunc(BGFX_STATE_BLEND_ONE, BGFX_STATE_BLEND_ONE);
    public static final long BGFX_STATE_BLEND_LIGHTEN = blendFunc(BGFX_STATE_BLEND_ONE, BGFX_STATE_BLEND_ONE);
    public static final long BGFX_STATE_BLEND_MULTIPLY = blendFunc(BGFX_STATE_BLEND_DST_COLOR, BGFX_STATE_BLEND_ZERO);
    public static final long BGFX_STATE_BLEND_NORMAL = blendFunc(BGFX_STATE_BLEND_ONE, BGFX_STATE_BLEND_INV_SRC_ALPHA);
    public static final long BGFX_STATE_BLEND_SCREEN = blendFunc(BGFX_STATE_BLEND_ONE, BGFX_STATE_BLEND_INV_SRC_COLOR);
    public static final long BGFX_STATE_BLEND_LINEAR_BURN = blendFunc(BGFX_STATE_BLEND_DST_COLOR, BGFX_STATE_BLEND_INV_DST_COLOR);

    ///
    // public static final long BGFX_STATE_BLEND_FUNC_RT_x(_src, _dst; (0 \
    // | ( uint32_t( (_src;>>BGFX_STATE_BLEND_SHIFT; \
    // | ( uint32_t( (_dst;>>BGFX_STATE_BLEND_SHIFT;<<4; ; \
    // ;

    // public static final long BGFX_STATE_BLEND_FUNC_RT_xE(_src, _dst,
    // _equation; (0 \
    // | BGFX_STATE_BLEND_FUNC_RT_x(_src, _dst; \
    // | ( uint32_t( (_equation;>>BGFX_STATE_BLEND_EQUATIOnSHIFT;<<8; \
    // ;

    // public static final long BGFX_STATE_BLEND_FUNC_RT_1(_src, _dst;
    // (BGFX_STATE_BLEND_FUNC_RT_x(_src, _dst;<< 0;
    // public static final long BGFX_STATE_BLEND_FUNC_RT_2(_src, _dst;
    // (BGFX_STATE_BLEND_FUNC_RT_x(_src, _dst;<<11;
    // public static final long BGFX_STATE_BLEND_FUNC_RT_3(_src, _dst;
    // (BGFX_STATE_BLEND_FUNC_RT_x(_src, _dst;<<22;

    // public static final long BGFX_STATE_BLEND_FUNC_RT_1E(_src, _dst,
    // _equation; (BGFX_STATE_BLEND_FUNC_RT_xE(_src, _dst, _equation;<< 0;
    // public static final long BGFX_STATE_BLEND_FUNC_RT_2E(_src, _dst,
    // _equation; (BGFX_STATE_BLEND_FUNC_RT_xE(_src, _dst, _equation;<<11;
    // public static final long BGFX_STATE_BLEND_FUNC_RT_3E(_src, _dst,
    // _equation; (BGFX_STATE_BLEND_FUNC_RT_xE(_src, _dst, _equation;<<22;

    ///
    public static final long BGFX_STENCIL_FUNC_REF_SHIFT = 0L;
    public static final long BGFX_STENCIL_FUNC_REF_MASK = 0x000000ffL;
    public static final long BGFX_STENCIL_FUNC_RMASK_SHIFT = 8L;
    public static final long BGFX_STENCIL_FUNC_RMASK_MASK = 0x0000ff00L;

    public static final long BGFX_STENCIL_TEST_LESS = 0x00010000L;
    public static final long BGFX_STENCIL_TEST_LEQUAL = 0x00020000L;
    public static final long BGFX_STENCIL_TEST_EQUAL = 0x00030000L;
    public static final long BGFX_STENCIL_TEST_GEQUAL = 0x00040000L;
    public static final long BGFX_STENCIL_TEST_GREATER = 0x00050000L;
    public static final long BGFX_STENCIL_TEST_NOTEQUAL = 0x00060000L;
    public static final long BGFX_STENCIL_TEST_NEVER = 0x00070000L;
    public static final long BGFX_STENCIL_TEST_ALWAYS = 0x00080000L;
    public static final long BGFX_STENCIL_TEST_SHIFT = 16L;
    public static final long BGFX_STENCIL_TEST_MASK = 0x000f0000L;

    public static final long BGFX_STENCIL_OP_FAIL_S_ZERO = 0x00000000L;
    public static final long BGFX_STENCIL_OP_FAIL_S_KEEP = 0x00100000L;
    public static final long BGFX_STENCIL_OP_FAIL_S_REPLACE = 0x00200000L;
    public static final long BGFX_STENCIL_OP_FAIL_S_INCR = 0x00300000L;
    public static final long BGFX_STENCIL_OP_FAIL_S_INCRSAT = 0x00400000L;
    public static final long BGFX_STENCIL_OP_FAIL_S_DECR = 0x00500000L;
    public static final long BGFX_STENCIL_OP_FAIL_S_DECRSAT = 0x00600000L;
    public static final long BGFX_STENCIL_OP_FAIL_S_INVERT = 0x00700000L;
    public static final long BGFX_STENCIL_OP_FAIL_S_SHIFT = 20L;
    public static final long BGFX_STENCIL_OP_FAIL_S_MASK = 0x00f00000L;

    public static final long BGFX_STENCIL_OP_FAIL_Z_ZERO = 0x00000000L;
    public static final long BGFX_STENCIL_OP_FAIL_Z_KEEP = 0x01000000L;
    public static final long BGFX_STENCIL_OP_FAIL_Z_REPLACE = 0x02000000L;
    public static final long BGFX_STENCIL_OP_FAIL_Z_INCR = 0x03000000L;
    public static final long BGFX_STENCIL_OP_FAIL_Z_INCRSAT = 0x04000000L;
    public static final long BGFX_STENCIL_OP_FAIL_Z_DECR = 0x05000000L;
    public static final long BGFX_STENCIL_OP_FAIL_Z_DECRSAT = 0x06000000L;
    public static final long BGFX_STENCIL_OP_FAIL_Z_INVERT = 0x07000000L;
    public static final long BGFX_STENCIL_OP_FAIL_Z_SHIFT = 24L;
    public static final long BGFX_STENCIL_OP_FAIL_Z_MASK = 0x0f000000L;

    public static final long BGFX_STENCIL_OP_PASS_Z_ZERO = 0x00000000L;
    public static final long BGFX_STENCIL_OP_PASS_Z_KEEP = 0x10000000L;
    public static final long BGFX_STENCIL_OP_PASS_Z_REPLACE = 0x20000000L;
    public static final long BGFX_STENCIL_OP_PASS_Z_INCR = 0x30000000L;
    public static final long BGFX_STENCIL_OP_PASS_Z_INCRSAT = 0x40000000L;
    public static final long BGFX_STENCIL_OP_PASS_Z_DECR = 0x50000000L;
    public static final long BGFX_STENCIL_OP_PASS_Z_DECRSAT = 0x60000000L;
    public static final long BGFX_STENCIL_OP_PASS_Z_INVERT = 0x70000000L;
    public static final long BGFX_STENCIL_OP_PASS_Z_SHIFT = 28L;
    public static final long BGFX_STENCIL_OP_PASS_Z_MASK = 0xf0000000L;

    public static final long BGFX_STENCIL_NONE = 0x00000000L;
    public static final long BGFX_STENCIL_MASK = 0xffffffffL;
    public static final long BGFX_STENCIL_DEFAULT = 0x00000000L;

    // public static final long BGFX_STENCIL_FUNC_REF(_ref; (
    // (uint32_t(_ref;<<BGFX_STENCIL_FUNC_REF_SHIFT;&BGFX_STENCIL_FUNC_REF_MASK;
    // public static final long BGFX_STENCIL_FUNC_RMASK(_mask; (
    // (uint32_t(_mask;<<BGFX_STENCIL_FUNC_RMASK_SHIFT;&BGFX_STENCIL_FUNC_RMASK_MASK;

    ///
    public static final long BGFX_CLEAR_NONE = 0x00L;
    public static final long BGFX_CLEAR_COLOR_BIT = 0x01L;
    public static final long BGFX_CLEAR_DEPTH_BIT = 0x02L;
    public static final long BGFX_CLEAR_STENCIL_BIT = 0x04L;

    ///
    public static final long BGFX_DEBUG_NONE = 0x00000000L;
    public static final long BGFX_DEBUG_WIREFRAME = 0x00000001L;
    public static final long BGFX_DEBUG_IFH = 0x00000002L;
    public static final long BGFX_DEBUG_STATS = 0x00000004L;
    public static final long BGFX_DEBUG_TEXT = 0x00000008L;

    ///
    public static final long BGFX_TEXTURE_NONE = 0x00000000L;
    public static final long BGFX_TEXTURE_U_MIRROR = 0x00000001L;
    public static final long BGFX_TEXTURE_U_CLAMP = 0x00000002L;
    public static final long BGFX_TEXTURE_U_SHIFT = 0L;
    public static final long BGFX_TEXTURE_U_MASK = 0x00000003L;
    public static final long BGFX_TEXTURE_V_MIRROR = 0x00000004L;
    public static final long BGFX_TEXTURE_V_CLAMP = 0x00000008L;
    public static final long BGFX_TEXTURE_V_SHIFT = 2L;
    public static final long BGFX_TEXTURE_V_MASK = 0x0000000cL;
    public static final long BGFX_TEXTURE_W_MIRROR = 0x00000010L;
    public static final long BGFX_TEXTURE_W_CLAMP = 0x00000020L;
    public static final long BGFX_TEXTURE_W_SHIFT = 4L;
    public static final long BGFX_TEXTURE_W_MASK = 0x00000030L;
    public static final long BGFX_TEXTURE_MInPOINT = 0x00000040L;
    public static final long BGFX_TEXTURE_MInANISOTROPIC = 0x00000080L;
    public static final long BGFX_TEXTURE_MInSHIFT = 6L;
    public static final long BGFX_TEXTURE_MInMASK = 0x000000c0L;
    public static final long BGFX_TEXTURE_MAG_POINT = 0x00000100L;
    public static final long BGFX_TEXTURE_MAG_ANISOTROPIC = 0x00000200L;
    public static final long BGFX_TEXTURE_MAG_SHIFT = 8L;
    public static final long BGFX_TEXTURE_MAG_MASK = 0x00000300L;
    public static final long BGFX_TEXTURE_MIP_POINT = 0x00000400L;
    public static final long BGFX_TEXTURE_MIP_SHIFT = 10L;
    public static final long BGFX_TEXTURE_MIP_MASK = 0x00000400L;
    public static final long BGFX_TEXTURE_RT = 0x00001000L;
    public static final long BGFX_TEXTURE_RT_MSAA_X2 = 0x00002000L;
    public static final long BGFX_TEXTURE_RT_MSAA_X4 = 0x00003000L;
    public static final long BGFX_TEXTURE_RT_MSAA_X8 = 0x00004000L;
    public static final long BGFX_TEXTURE_RT_MSAA_X16 = 0x00005000L;
    public static final long BGFX_TEXTURE_RT_MSAA_SHIFT = 12L;
    public static final long BGFX_TEXTURE_RT_MSAA_MASK = 0x00007000L;
    public static final long BGFX_TEXTURE_RT_BUFFER_ONLY = 0x00008000L;
    public static final long BGFX_TEXTURE_RT_MASK = 0x0000f000L;
    public static final long BGFX_TEXTURE_COMPARE_LESS = 0x00010000L;
    public static final long BGFX_TEXTURE_COMPARE_LEQUAL = 0x00020000L;
    public static final long BGFX_TEXTURE_COMPARE_EQUAL = 0x00030000L;
    public static final long BGFX_TEXTURE_COMPARE_GEQUAL = 0x00040000L;
    public static final long BGFX_TEXTURE_COMPARE_GREATER = 0x00050000L;
    public static final long BGFX_TEXTURE_COMPARE_NOTEQUAL = 0x00060000L;
    public static final long BGFX_TEXTURE_COMPARE_NEVER = 0x00070000L;
    public static final long BGFX_TEXTURE_COMPARE_ALWAYS = 0x00080000L;
    public static final long BGFX_TEXTURE_COMPARE_SHIFT = 16L;
    public static final long BGFX_TEXTURE_COMPARE_MASK = 0x000f0000L;
    public static final long BGFX_TEXTURE_RESERVED_SHIFT = 24L;
    public static final long BGFX_TEXTURE_RESERVED_MASK = 0xff000000L;

    public static final long BGFX_TEXTURE_SAMPLER_BITS_MASK = 0 | BGFX_TEXTURE_U_MASK | BGFX_TEXTURE_V_MASK | BGFX_TEXTURE_W_MASK | BGFX_TEXTURE_MInMASK | BGFX_TEXTURE_MAG_MASK | BGFX_TEXTURE_MIP_MASK | BGFX_TEXTURE_COMPARE_MASK;

    ///
    public static final long BGFX_RESET_NONE = 0x00000000L;
    public static final long BGFX_RESET_FULLSCREEN = 0x00000001L;
    public static final long BGFX_RESET_FULLSCREEnSHIFT = 0L;
    public static final long BGFX_RESET_FULLSCREEnMASK = 0x00000001L;
    public static final long BGFX_RESET_MSAA_X2 = 0x00000010L;
    public static final long BGFX_RESET_MSAA_X4 = 0x00000020L;
    public static final long BGFX_RESET_MSAA_X8 = 0x00000030L;
    public static final long BGFX_RESET_MSAA_X16 = 0x00000040L;
    public static final long BGFX_RESET_MSAA_SHIFT = 4L;
    public static final long BGFX_RESET_MSAA_MASK = 0x00000070L;
    public static final long BGFX_RESET_VSYNC = 0x00000080L;
    public static final long BGFX_RESET_CAPTURE = 0x00000100L;

    ///
    public static final long BGFX_CAPS_TEXTURE_FORMAT_BC1 = 0x0000000000000001L;
    public static final long BGFX_CAPS_TEXTURE_FORMAT_BC2 = 0x0000000000000002L;
    public static final long BGFX_CAPS_TEXTURE_FORMAT_BC3 = 0x0000000000000004L;
    public static final long BGFX_CAPS_TEXTURE_FORMAT_BC4 = 0x0000000000000008L;
    public static final long BGFX_CAPS_TEXTURE_FORMAT_BC5 = 0x0000000000000010L;
    public static final long BGFX_CAPS_TEXTURE_FORMAT_ETC1 = 0x0000000000000020L;
    public static final long BGFX_CAPS_TEXTURE_FORMAT_ETC2 = 0x0000000000000040L;
    public static final long BGFX_CAPS_TEXTURE_FORMAT_ETC2A = 0x0000000000000080L;
    public static final long BGFX_CAPS_TEXTURE_FORMAT_ETC2A1 = 0x0000000000000100L;
    public static final long BGFX_CAPS_TEXTURE_FORMAT_PTC12 = 0x0000000000000200L;
    public static final long BGFX_CAPS_TEXTURE_FORMAT_PTC14 = 0x0000000000000400L;
    public static final long BGFX_CAPS_TEXTURE_FORMAT_PTC14A = 0x0000000000000800L;
    public static final long BGFX_CAPS_TEXTURE_FORMAT_PTC12A = 0x0000000000001000L;
    public static final long BGFX_CAPS_TEXTURE_FORMAT_PTC22 = 0x0000000000002000L;
    public static final long BGFX_CAPS_TEXTURE_FORMAT_PTC24 = 0x0000000000004000L;
    public static final long BGFX_CAPS_TEXTURE_FORMAT_D16 = 0x0000000000008000L;
    public static final long BGFX_CAPS_TEXTURE_FORMAT_D24 = 0x0000000000010000L;
    public static final long BGFX_CAPS_TEXTURE_FORMAT_D24S8 = 0x0000000000020000L;
    public static final long BGFX_CAPS_TEXTURE_FORMAT_D32 = 0x0000000000040000L;
    public static final long BGFX_CAPS_TEXTURE_FORMAT_D16F = 0x0000000000080000L;
    public static final long BGFX_CAPS_TEXTURE_FORMAT_D24F = 0x0000000000100000L;
    public static final long BGFX_CAPS_TEXTURE_FORMAT_D32F = 0x0000000000200000L;
    public static final long BGFX_CAPS_TEXTURE_FORMAT_D0S8 = 0x0000000000400000L;
    public static final long BGFX_CAPS_TEXTURE_COMPARE_LEQUAL = 0x0000000001000000L;
    public static final long BGFX_CAPS_TEXTURE_COMPARE_ALL = 0x0000000003000000L;
    public static final long BGFX_CAPS_TEXTURE_3D = 0x0000000004000000L;
    public static final long BGFX_CAPS_VERTEX_ATTRIB_HALF = 0x0000000008000000L;
    public static final long BGFX_CAPS_INSTANCING = 0x0000000010000000L;
    public static final long BGFX_CAPS_RENDERER_MULTITHREADED = 0x0000000020000000L;
    public static final long BGFX_CAPS_FRAGMENT_DEPTH = 0x0000000040000000L;
    public static final long BGFX_CAPS_BLEND_INDEPENDENT = 0x0000000080000000L;

    public static final long BGFX_CAPS_TEXTURE_DEPTH_MASK = 0 | BGFX_CAPS_TEXTURE_FORMAT_D16 | BGFX_CAPS_TEXTURE_FORMAT_D24 | BGFX_CAPS_TEXTURE_FORMAT_D24S8 | BGFX_CAPS_TEXTURE_FORMAT_D32 | BGFX_CAPS_TEXTURE_FORMAT_D16F | BGFX_CAPS_TEXTURE_FORMAT_D24F | BGFX_CAPS_TEXTURE_FORMAT_D32F | BGFX_CAPS_TEXTURE_FORMAT_D0S8;
}
