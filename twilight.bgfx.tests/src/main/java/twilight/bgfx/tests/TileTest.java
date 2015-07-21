package twilight.bgfx.tests;

import java.io.File;

import l33tlabs.bling.math.affine.Mat4;
import l33tlabs.bling.math.affine.Vec3;
import l33tlabs.bling.math.util.SceneUtil;
import twilight.bgfx.Attrib;
import twilight.bgfx.AttribType;
import twilight.bgfx.BGFX;
import twilight.bgfx.UniformType;
import twilight.bgfx.VertexDecl;
import twilight.bgfx.buffers.DynamicVertexBuffer;
import twilight.bgfx.buffers.IndexBuffer;
import twilight.bgfx.buffers.VertexBuffer;
import twilight.bgfx.util.ResourceUtil;
import twilight.bgfx.window.Display;
import twilight.bgfx.window.Window;

/**
 * 
 * @author tmccrary
 *
 */
public class TileTest {

	private static final float PRIM_W = 450f;
	private static final float PRIM_H = 450f;
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		WindowUtil windowUnit = new WindowUtil();
		
		BGFX bgfx = windowUnit.getContext();
		Window window = windowUnit.getWindow();
		Display display = windowUnit.getDisplay();
		
		window.setSize(1024, 768);
		window.setVisible(true);
		
		// Initialize it
		bgfx.init();
		bgfx.reset(window.getWidth(), window.getHeight(), BGFX.BGFX_RESET_NONE | BGFX.BGFX_RESET_VSYNC);
		bgfx.setDebug(BGFX.BGFX_DEBUG_STATS);

		// Create matrices to define the scene 
		Mat4 view = new Mat4();
		
		//Mat4 projMat4 = SceneUtil.perspective(60.0f, 1600f/1200f, 1f, 100.0f);
		Mat4 proj = SceneUtil.ortho(0f, 1024, 768, 0f, -1f, 1f);
		
			
		Mat4 model2 = new Mat4();
			model2.setTranslation(55f, 55f, 0f);
			//model2.transposeLocal();
				
		// Setup the viewport
		bgfx.setViewClear(0, BGFX.BGFX_CLEAR_COLOR_BIT|BGFX.BGFX_CLEAR_DEPTH_BIT, 1024, 1.0f, 0);
		bgfx.setViewRect(0, 0, 0, 1024, 768);
		bgfx.setViewTransform(0, view.toArray(), proj.toArray());

		bgfx.setViewSeq(0, true);
		bgfx.setViewRect(0, 0, 0, 1024, 768);
		
		VertexBuffer vertexBufferHandle = createVertexBuffer(bgfx);
		IndexBuffer indexBufferHandle = createIndexBuffer(bgfx);
		
		// Load shader programs to render the data
		int vertexShaderHandle = ResourceUtil.loadShader(bgfx,  new File("/home/tmccrary/git/twilight-bgfx/twilight.bgfx.binding/src/main/java/shaders/vs_tile.bin"));
		int fragShaderHandle = ResourceUtil.loadShader(bgfx,  new File("/home/tmccrary/git/twilight-bgfx/twilight.bgfx.binding/src/main/java/shaders/fs_tile.bin"));

		// Link the shaders in a program that can be used
		int program = bgfx.createProgram(vertexShaderHandle, fragShaderHandle, true);

		System.out.println("Program created! " + vertexShaderHandle + " " + fragShaderHandle + " " + program);
		
		// Load the texture
		int texture = ResourceUtil.loadTexture(bgfx, "/home/tmccrary/tiler.dds");

		// u_texColor
		int texUniformHandle = bgfx.createUniform("u_texColor", UniformType.Int1, 1);
		
		int tilePosition = bgfx.createUniform("tilePosition", UniformType.Vec4, 2);
		int tileSize = bgfx.createUniform("tileSize", UniformType.Vec4, 2);
		int tileScale = bgfx.createUniform("tileScale", UniformType.Vec4, 2);
		int primCoefficient = bgfx.createUniform("primCoefficient", UniformType.Vec4, 2);
		
		System.out.println("Tile Position: " + tilePosition);
		
		int lastWidth = -1;
		int lastHeight = -1;

		Vec3 trans = new Vec3(50f, 50f, 0f);
		
		display.update();
		bgfx.reset(window.getWidth(), window.getHeight(), BGFX.BGFX_RESET_NONE | BGFX.BGFX_RESET_VSYNC);
		proj = SceneUtil.ortho(0f, window.getWidth(), window.getHeight(), 0f, -1f, 1f);
		
		// Submit a draw call
		bgfx.submit(0, 0);
		
		
		while(true) {
			int width = window.getWidth();
			int height = window.getHeight();
			
			if(width != lastWidth || height != lastHeight) {
				bgfx.reset(width, height, BGFX.BGFX_RESET_NONE | BGFX.BGFX_RESET_VSYNC);
				lastWidth = width;
				lastHeight = height;
				proj = SceneUtil.ortho(0f, width, height, 0f, -1f, 1f);
			}

			// Submit a draw call
			bgfx.submit(0, 0);
			
			Mat4 model = new Mat4();
			model.setTranslation(trans);
			model.transposeLocal();
			
			bgfx.setViewRect(0, 0, 0, width, height);
			bgfx.setViewTransform(0, view.toArray(), proj.toArray());
			
			// Configure render state
			bgfx.setTexture(0, texUniformHandle, texture, BGFX.BGFX_TEXTURE_NONE);
			bgfx.setUniform(primCoefficient, new float[] {PRIM_W/150f, PRIM_H/150f}, 1);
			bgfx.setUniform(tilePosition, new float[] {0.1f, 0.1f}, 1);
			bgfx.setUniform(tileSize, new float[] {0.15f, 0.15f}, 1);
			bgfx.setUniform(tileScale, new float[] {2.25f, 2.25f}, 1);
			bgfx.setTransform(model.toArray(), 16);
			bgfx.setProgram(program);
			bgfx.setVertexBuffer(vertexBufferHandle, 0, 6);
			bgfx.setIndexBuffer(indexBufferHandle, 0, 6);
			bgfx.setState(BGFX.BGFX_STATE_DEFAULT, 0);
			
			// Submit a draw call
			bgfx.submit(0, 0);
			
			// Kick the render thread to process all draw calls
			bgfx.frame();
			
			// Kick the display to update platform events (input, etc)
			display.update();
			
			/*try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
	}
	
	public static float[] createTestQuad(float width, float height, float r, float g, float b) {
		float pos = 0f;
		
		float[] cubeVerts = new float[] {
				// tri 1
				0, 0f, pos, r, g, b, 1f, 0.0f, 0.0f,
				0f, height, pos, r, g, b, 1f, 0.0f, 1f,
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
	static float col = 1f;
	
	public static VertexBuffer createVertexBuffer(BGFX bgfx) {
		VertexDecl vertexDecl = VertexDecl.begin();
			vertexDecl.add(Attrib.Position, 3, AttribType.Float, false, false);
			vertexDecl.add(Attrib.Color0, 4, AttribType.Float, false, false);
			vertexDecl.add(Attrib.TexCoord0, 2, AttribType.Float, false, false);
		vertexDecl.end();
		
		VertexBuffer vertexBuf = bgfx.createVertexBuffer(createTestQuad(PRIM_W, PRIM_H, col, 1f, 1f), vertexDecl);
		
		System.out.println("Created vertex buffer: " + vertexBuf);
		
		if(col == 0f) {
			col = 1f;
		}
		
		return vertexBuf;
	}
	
	public static IndexBuffer createIndexBuffer(BGFX bgfx) {
		short[] indices = new short[] {
			0, 1, 2, 3, 4, 5
		};
		
		IndexBuffer indexBuf = bgfx.createIndexBuffer(indices);
		return indexBuf;
	}
	
}
