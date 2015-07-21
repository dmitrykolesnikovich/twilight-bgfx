package twilight.bgfx;

/**
 * <p>Defines the rendering interfaces that are 
 * supported by BGFX.</p>
 * 
 * <p><b>NOTE:</b> when updating renderer types, it needs to be updated
 * everywhere (java, jni code, etc).</p>
 */
public enum RendererType {
    Null, // !< No rendering.
    Direct3D9, // !< Direct3D 9.0
    Direct3D11, // !< Direct3D 11.0
    Direct3D12, // !< Direct3D 12.0
    OpenGLES, // !< OpenGL ES 2.0+
    OpenGL, // !< OpenGL 2.1+
    Vulkan, // !< Vulkan
    Count
}