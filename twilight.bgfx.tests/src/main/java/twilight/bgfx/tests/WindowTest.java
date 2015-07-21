package twilight.bgfx.tests;

import java.util.HashMap;

import twilight.bgfx.window.Display;
import twilight.bgfx.window.DisplayManager;
import twilight.bgfx.window.Window;

/**
 * 
 * @author tmccrary
 *
 */
public class WindowTest {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Create the display manager
		DisplayManager manager = DisplayManager.getManager();
		
		Display display = manager.getDefaultDisplay();
		//int dpi = display.getDPI();

		// Create a window to draw into
		HashMap<String, String> opts = new HashMap<String, String>();
			opts.put("undecorated", "true");
			
		Window window = manager.createWindow(display, "TEST123", opts);
			window.setSize(512, 512);
			window.setVisible(true);
			
		//bgfx = window.getContext();
		
		// Render
		while(true) {

			// Kick the display to update platform events (input, etc)
			display.update();
		}
	}
}
