package twilight.bgfx.tests;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import sun.misc.IOUtils;
import twilight.bgfx.BGFX;
import twilight.bgfx.font.Font;
import twilight.bgfx.font.FontFace;
import twilight.bgfx.font.FontManager;
import twilight.bgfx.font.TextBuffer;
import twilight.bgfx.font.TextBuffer.GlyphType;
import twilight.bgfx.font.TextBuffer.UsageType;
import twilight.bgfx.window.Display;
import twilight.bgfx.window.Window;
import l33tlabs.bling.math.affine.Mat4;
import l33tlabs.bling.math.affine.Vec3;
import l33tlabs.bling.math.util.SceneUtil;

/**
 * 
 * @author tmccrary
 *
 */
public class FontTest {
	
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
		//bgfx.setDebug(BGFX.BGFX_DEBUG_STATS);

		// Create matrices to define the scene 
		Mat4 view = new Mat4();
			view.loadIdentity();
		
		//Mat4 projMat4 = SceneUtil.perspective(60.0f, 1600f/1200f, 1f, 100.0f);
		Mat4 proj = SceneUtil.ortho(0f, 1024, 768, 0f, -1f, 1f);
			
		Mat4 model2 = new Mat4();
			model2.loadIdentity();
			//model2.setTranslation(55f, 55f, 0f);
			//model2.transposeLocal();
				
		// Setup the viewport
		bgfx.setViewClear(0, BGFX.BGFX_CLEAR_COLOR_BIT|BGFX.BGFX_CLEAR_DEPTH_BIT, 1024, 1.0f, 0);
		bgfx.setViewRect(0, 0, 0, 1024, 768);
		bgfx.setViewTransform(0, view.toArray(), proj.toArray());

		bgfx.setViewSeq(0, true);
		bgfx.setViewRect(0, 0, 0, 1024, 768);
		
		int lastWidth = -1;
		int lastHeight = -1;

		Vec3 trans = new Vec3(50f, 50f, 0f);
		
		display.update();
		bgfx.reset(window.getWidth(), window.getHeight(), BGFX.BGFX_RESET_NONE | BGFX.BGFX_RESET_VSYNC);
		proj = SceneUtil.ortho(0f, window.getWidth(), window.getHeight(), 0f, -1f, 1f);
		
		FontManager fontMan = new FontManager(512);
		
		InputStream resourceAsStream = FontTest.class.getResourceAsStream("DroidSans.ttf");
		
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		try {
			org.apache.commons.io.IOUtils.copy(resourceAsStream, byteOutput);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ByteBuffer allocateDirect = ByteBuffer.allocateDirect(byteOutput.size());
			allocateDirect.put(byteOutput.toByteArray());
			allocateDirect.rewind();
		
		FontFace fontFace = fontMan.createTTF(allocateDirect);
		
		Font font = fontMan.createFontByPixelSize(fontFace, 19f);
		
		TextBuffer textBuffer = fontMan.createTextBuffer(GlyphType.DISTANCE, UsageType.TRANSIENT);
		
		for(int i=0; i<25; i++) {
			textBuffer.appendText(font, "This is cool text!\nASDASD\n");
			textBuffer.appendText(font, "\n123456788\nNERP\n\n");
		}

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
			
			bgfx.setViewRect(0, 0, 0, width, height);
			bgfx.setViewTransform(0, view.toArray(), proj.toArray());
			
			fontMan.draw(textBuffer, 5, 5);
			
			// Submit a draw call
			bgfx.submit(0, 0);
			
			// Kick the render thread to process all draw calls
			bgfx.frame();
			
			// Kick the display to update platform events (input, etc)
			display.update();
		}
	}
	
}
