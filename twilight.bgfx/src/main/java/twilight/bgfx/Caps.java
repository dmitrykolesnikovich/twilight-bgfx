package twilight.bgfx;

/**
 * <p>
 * Renderer capabilities are used to determine what
 * features are available for use with the BGFX rendering
 * API.
 * </p>
 * 
 * @author tmccrary
 * 
 */
public class Caps {
    
    /** Renderer backend type. */
    public RendererType rendererType;

    /**
     * Supported functionality, it includes emulated functionality. Checking
     * supported and not emulated will give functionality natively supported by
     * renderer.
     */
    long supported;

    /**
     * Emulated functionality. For example some texture compression modes are
     * not natively supported by all renderers. The library internally
     * decompresses texture into supported format.
     */
    long emulated;

    /** Maximum texture size. */
    int maxTextureSize;

    /** Maximum draw calls. */
    int maxDrawCalls;

    /** Maximum frame buffer attachments. */
    short maxFBAttachments;

    /**
     * <p>
     * Checks whether the supplied capability is supported by the current
     * renderer.
     * </p>
     * 
     * @param cap
     *            the capability to check
     * @return true if the capability is supported, false otherwise
     */
    public boolean isSupported(Capability cap) {
        return 0 != (supported & cap.id);
    }
}