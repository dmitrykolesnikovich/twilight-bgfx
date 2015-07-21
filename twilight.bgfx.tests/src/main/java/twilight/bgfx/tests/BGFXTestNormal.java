package twilight.bgfx.tests;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import l33tlabs.bling.math.affine.Mat4;
import l33tlabs.bling.math.util.SceneUtil;
import twilight.bgfx.Attrib;
import twilight.bgfx.AttribType;
import twilight.bgfx.BGFX;
import twilight.bgfx.TextureFormat;
import twilight.bgfx.UniformType;
import twilight.bgfx.VertexDecl;
import twilight.bgfx.buffers.DynamicVertexBuffer;
import twilight.bgfx.buffers.IndexBuffer;
import twilight.bgfx.buffers.TransientIndexBuffer;
import twilight.bgfx.buffers.TransientVertexBuffer;
import twilight.bgfx.buffers.VertexBuffer;
import twilight.bgfx.util.ResourceUtil;
import twilight.bgfx.window.Display;
import twilight.bgfx.window.Window;

/**
 * 
 * @author tmccrary
 *
 */
public class BGFXTestNormal {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		WindowUtil windowUnit = new WindowUtil();
		
		BGFX bgfx = windowUnit.getContext();
		Window window = windowUnit.getWindow();
		Display display = windowUnit.getDisplay();
		
		// Initialize it
		bgfx.init();
		bgfx.reset(window.getWidth(), window.getHeight(), BGFX.BGFX_RESET_NONE);
		bgfx.setDebug(BGFX.BGFX_DEBUG_STATS);

		// Load shader programs to render the data
		int vertexShaderHandle = ResourceUtil.loadShader(bgfx, new File("/home/tmccrary/git/twilight-bgfx/twilight.bgfx.binding/shaders/vs_test.bin"));
		int fragShaderHandle = ResourceUtil.loadShader(bgfx, new File("/home/tmccrary/git/twilight-bgfx/twilight.bgfx.binding/shaders/fs_test.bin"));
		
		// Link the shaders in a program that can be used
		int program = bgfx.createProgram(vertexShaderHandle, fragShaderHandle, true);

		System.out.println("Program ID: " + program);
		
		// Load the texture
		int texture = ResourceUtil.loadTexture(bgfx, "/home/tmccrary/bark13.dds");

		System.out.println("Texture ID: " + texture);
		
		// 
		int texUniformHandle = bgfx.createUniform("u_texColor", UniformType.Int1, 1);
		
		// Create matrices to define the scene 
		Mat4 view = new Mat4();
			view.loadIdentity();
		
		//Mat4 projMat4 = SceneUtil.perspective(60.0f, 1600f/1200f, 1f, 100.0f);
		Mat4 proj = SceneUtil.ortho(0f, 1600f, 0f, 1200f, -1f, 1f);
			proj.transposeLocal();
			
		Mat4 model = new Mat4();
			model.setTranslation(0f, 0f, 0f);
			model.transposeLocal();
			
		Mat4 model2 = new Mat4();
			model2.setTranslation(128f, 128f, 0f);
			model2.transposeLocal();
			
		// Setup the viewport
		bgfx.setViewClear(0, BGFX.BGFX_CLEAR_COLOR_BIT|BGFX.BGFX_CLEAR_DEPTH_BIT, 0xFF00FF00L, 1.0f, 0);
		bgfx.setViewRect(0, 0, 0, 1600, 1200);
		bgfx.setViewTransform(0, view.toArray(), proj.toArray());
		bgfx.setViewSeq(0, true);
		
		/*{
			int tex = bgfx.createTexture2D(256, 256, 0, TextureFormat.BGRA8, 0, null);
			int createFrameBuffer = bgfx.createFrameBuffer(1, new int[] {tex}, false);
			System.out.println("Buffer: " + createFrameBuffer);
		}
		
		{
			int tex = bgfx.createTexture2D(256, 256, 0, TextureFormat.BGRA8, 0, null);
			int createFrameBuffer = bgfx.createFrameBuffer(1, new int[] {tex}, false);
			System.out.println("Buffer: " + createFrameBuffer);
		}*/
		
		// Render
		while(true) {
			bgfx.reset(window.getWidth(), window.getHeight(), BGFX.BGFX_RESET_NONE);
			
			//updateVertexBuffer(bgfx, vertexBufferHandle, 1f);
			//updateVertexBuffer(bgfx, vertexBufferHandle2, 0f);

			// Submit a draw call
			bgfx.submit(0);
			
			// Create the data to render
			TransientVertexBuffer vertexBufferHandle = createVertexBuffer(bgfx);
			TransientIndexBuffer indexBufferHandle = createIndexBuffer(bgfx);

			// Configure render state
			bgfx.setTexture(0, texUniformHandle, texture, BGFX.BGFX_TEXTURE_NONE);
			bgfx.setTransform(model.toArray(), 16);
			bgfx.setProgram(program);
			bgfx.setVertexBuffer(vertexBufferHandle);
			bgfx.setIndexBuffer(indexBufferHandle, 0, 6);
			
			// Submit a draw call
			bgfx.submit(0);
			
			/*// Configure render state
			bgfx.setTexture(0, texUniformHandle, texture, BGFX.BGFX_TEXTURE_NONE);
			bgfx.setTransform(model2.toArray(), 16);
			bgfx.setProgram(program);
			bgfx.setVertexBuffer(vertexBufferHandle2, 6);
			bgfx.setIndexBuffer(indexBufferHandle2, 0, 6);
			
			// Submit a draw call
			bgfx.submit(0, 0);*/
			
			// Kick the render thread to process all draw calls
			bgfx.frame();
			
			// Kick the display to update platform events (input, etc)
			display.update();
		}
	}
	
	public static float[] test(float width, float height, float r, float g, float b) {
		float pos = 1f;
		
		float[] cubeVerts = new float[] {
				// tri 1
				0, 0f, pos, r, g, b, 1f, 0f, 0f,
				0f, height, pos, r, g, b, 1f, 0f, 1f,
				width, height, pos, r, g, b, 1f,  1f, 1f,
				// tri 2
				0, 0f, pos, r, g, b, 1f, 0f, 0f,
				width, height, pos, r, g, b, 1f, 1f, 1f,
				width, 0f, pos, r, g, b, 1f, 1f, 0f,
		};
		
		return cubeVerts;
	}

	static float r = 0f;
	static float width = 0f;
	
	public static void updateVertexBuffer(BGFX bgfx, DynamicVertexBuffer bufferid, float g) {
		float inc = 1f/1600f;
		r += inc;
		
		if(r > 1f) {
			r = 0f;
		}
		
		width += 1f;
		
		if(width > 1600f) {
			width = 0f;
		}
		
		float[] test = test(width, 1200f, r, 1f, 1f);
		
		bgfx.updateDynamicVertexBuffer(bufferid, 0, 54*4, test);
	}
	
	static float col = 1f;
	private static VertexDecl vertexDecl;
	
	public static TransientVertexBuffer createVertexBuffer(BGFX bgfx) {
		if(vertexDecl == null) {
			vertexDecl = VertexDecl.begin();
				vertexDecl.add(Attrib.Position, 3, AttribType.Float, false, false);
				vertexDecl.add(Attrib.Color0, 4, AttribType.Float, false, false);
				vertexDecl.add(Attrib.TexCoord0, 2, AttribType.Float, false, false);
			vertexDecl.end();
		}
		
		float[] test = test(512f, 512f, col, 1f, 1f);
		
		TransientVertexBuffer vertexBuffer = bgfx.allocTransientVertexBuffer(6, vertexDecl);
		
		ByteBuffer data = vertexBuffer.getData();
		data.rewind();
		data.asFloatBuffer().put(test);
		data.rewind();
		
		return vertexBuffer;
	}
	
	public static TransientIndexBuffer createIndexBuffer(BGFX bgfx) {
		short[] indices = new short[] {
			0, 1, 2, 3, 4, 5
		};
		
		TransientIndexBuffer indexBuffer = bgfx.allocTransientIndexBuffer(6);
		ByteBuffer data = indexBuffer.getData();
		data.rewind();
		data.asShortBuffer().put(indices);
		data.rewind();
		
		
		return indexBuffer;
	}
	
}
