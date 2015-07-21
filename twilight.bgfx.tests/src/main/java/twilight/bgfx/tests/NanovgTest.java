package twilight.bgfx.tests;

import l33tlabs.bling.math.affine.Mat4;
import l33tlabs.bling.math.util.SceneUtil;
import twilight.bgfx.Attrib;
import twilight.bgfx.AttribType;
import twilight.bgfx.BGFX;
import twilight.bgfx.VertexDecl;
import twilight.bgfx.buffers.DynamicVertexBuffer;
import twilight.bgfx.buffers.IndexBuffer;
import twilight.bgfx.buffers.VertexBuffer;
import twilight.bgfx.nanovg.NVGcolor;
import twilight.bgfx.nanovg.NanoVG;
import twilight.bgfx.window.Display;
import twilight.bgfx.window.Window;

/**
 * 
 * @author tmccrary
 *
 */
public class NanovgTest {

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
		bgfx.reset(window.getWidth(), window.getHeight(), BGFX.BGFX_RESET_NONE | BGFX.BGFX_RESET_VSYNC);
		bgfx.setDebug(BGFX.BGFX_DEBUG_STATS);

		// Create matrices to define the scene 
		Mat4 view = new Mat4();
		
		//Mat4 projMat4 = SceneUtil.perspective(60.0f, 1600f/1200f, 1f, 100.0f);
		Mat4 proj = SceneUtil.ortho(0f, 1600f, 1200f, 0f, -1f, 1f);
		
		Mat4 model = new Mat4();
			model.setTranslation(0f, 0f, 0f);
			model.transposeLocal();
			
		Mat4 model2 = new Mat4();
			model2.setTranslation(128f, 128f, 0f);
			model2.transposeLocal();
			
		// Setup the viewport
		bgfx.setViewClear(0, BGFX.BGFX_CLEAR_COLOR_BIT, 0, 1.0f, 0);
		bgfx.setViewRect(0, 0, 0, 1600, 1200);
		bgfx.setViewTransform(0, view.toArray(), proj.toArray());

		NanoVG nvg = NanoVG.createNVGContext(1600, 1200, 1, 0);
		bgfx.setViewSeq(0, true);
		
		NVGcolor color = new NVGcolor(1f, 0f, 0f, 1f);
		NVGcolor color2 = new NVGcolor(0f, 0f, 1f, 1f);
		
		float inp = 0;
		
		bgfx.setViewRect(0, 0, 0, 1600, 1200);
		
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		
		int lastWidth = -1;
		int lastHeight = -1;
		
		// Render
		while(true) {
			int width = window.getWidth();
			int height = window.getHeight();
			
			if(width != lastWidth || height != lastHeight) {
				bgfx.reset(width, height, BGFX.BGFX_RESET_NONE | BGFX.BGFX_RESET_VSYNC);
				lastWidth = width;
				lastHeight = height;
			}
			
			proj = SceneUtil.ortho(0f, 1600f, 1200f, 0f, -1f, 1f);
			bgfx.setViewRect(0, 0, 0, window.getWidth(), window.getHeight());
			bgfx.setViewTransform(0, view.toArray(), proj.toArray());
			
			NVGcolor lerped = nvg.LerpRGBA(color, color2, inp);
			
			inp += 0.01;
			
			if(inp >= 1f) {
				inp = 0;
			}
			
			float currentX = 0;
			float currentY = 0;
			
			for(int i=0; i<5; i++) {
				nvg.beginFrame(1600, 1200, 1, 0);
				
				nvg.save();
				nvg.strokeWidth(5f);
				nvg.fillColor(lerped);
				nvg.strokeColor(color2);
				nvg.beginPath();
				nvg.roundedRect(currentX, currentY, 200f, 200f, 15f);
				nvg.fill();
				nvg.stroke();

				nvg.beginPath();
				nvg.circle(currentX, currentY, 100f);
				nvg.fill();
				nvg.stroke();
				
				nvg.restore();
				
				currentX += 100f;
				
				if(currentX > 1600f) {
					currentX = 0;
					currentY += 100f;
				}
				nvg.endFrame();
			}
			
			/*
			    nvg.save();
				nvg.beginPath();
				nvg.moveTo(250, 250);
				nvg.stroke();
				nvg.fill();
				nvg.restore();
			*/
			
			// Kick the render thread to process all draw calls
			bgfx.frame();
			
			// Kick the display to update platform events (input, etc)
			display.update();
			
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static float[] test(float width, float height, float r, float g, float b) {
		float pos = 0f;
		
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
	
	static float col = 0f;
	
	public static DynamicVertexBuffer createVertexBuffer(BGFX bgfx) {
		VertexDecl vertexDecl = VertexDecl.begin();
			vertexDecl.add(Attrib.Position, 3, AttribType.Float, false, false);
			vertexDecl.add(Attrib.Color0, 4, AttribType.Float, false, false);
			vertexDecl.add(Attrib.TexCoord0, 2, AttribType.Float, false, false);
		vertexDecl.end();
		
		DynamicVertexBuffer vertexBuf = bgfx.createDynamicVertexBuffer(test(250f, 250f, col, 1f, 1f), vertexDecl);
		
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
