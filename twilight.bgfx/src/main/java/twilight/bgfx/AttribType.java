package twilight.bgfx;

public enum AttribType {
    Uint8, Int16, Half, // Availability depends on:
                        // BGFX_CAPS_VERTEX_ATTRIB_HALF.
    Float,

    Count
}