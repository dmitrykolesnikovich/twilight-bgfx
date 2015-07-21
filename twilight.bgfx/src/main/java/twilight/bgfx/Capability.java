package twilight.bgfx;

/**
 * <p>
 * BGFX Capability enum. Used with {@link BGFX#getCaps()} to determine what
 * rendering features the client system is capable of.
 * </p>
 * 
 * @author tmccrary
 *
 */
public enum Capability {
    BGFX_CAPS_TEXTURE_COMPARE_LEQUAL(0x0000000000000001), 
    BGFX_CAPS_TEXTURE_COMPARE_ALL(0x0000000000000003), 
    BGFX_CAPS_TEXTURE_3D(0x0000000000000004), 
    BGFX_CAPS_VERTEX_ATTRIB_HALF(0x0000000000000008), 
    BGFX_CAPS_INSTANCING(0x0000000000000010), 
    BGFX_CAPS_RENDERER_MULTITHREADED(0x0000000000000020), 
    BGFX_CAPS_FRAGMENT_DEPTH(0x0000000000000040), 
    BGFX_CAPS_BLEND_INDEPENDENT(0x0000000000000080), 
    BGFX_CAPS_COMPUTE(0x0000000000000100), 
    BGFX_CAPS_FRAGMENT_ORDERING(0x0000000000000200), 
    BGFX_CAPS_SWAP_CHAIN(0x0000000000000400), 
    BGFX_CAPS_HMD(0x0000000000000800);

    // The ID of the capability
    long id;

    public long id() {
        return id;
    }

    private Capability(long id) {
        this.id = id;
    }
}