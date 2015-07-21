package twilight.bgfx.tests;

import java.util.HashMap;

import twilight.bgfx.BGFX;
import twilight.bgfx.window.Display;
import twilight.bgfx.window.DisplayManager;
import twilight.bgfx.window.Window;

public class WindowUtil {

	private BGFX bgfx;
	private Window window;
	private Display display;

	public WindowUtil() {
		// Create the display manager
		DisplayManager manager = DisplayManager.getManager();
		
		display = manager.getDefaultDisplay();
		//int dpi = display.getDPI();

		// Create a window to draw into
		HashMap<String, String> opts = new HashMap<String, String>();
			opts.put("undecorated", "false");
			
		window = manager.createWindow(display, "test", opts);
			//window.setPosition(512, 512);
			//window.setSize(1600, 1200);
			window.setVisible(true);
			
		bgfx = window.getContext();
	}
	
	public BGFX getContext() {
		return bgfx;
	}
	
	public Window getWindow() {
		return window;
	}
	
	public Display getDisplay() {
		return display;
	}
	
}
