package twilight.bgfx.swt;

import org.eclipse.swt.widgets.Composite;

import twilight.bgfx.BGFX;
import twilight.bgfx.util.PlatformUtil;

/**
 * 
 * @author tmccrary
 *
 */
public class BGFXHelper {

    /**
     * 
     * @param composite
     */
    public static void setupBGFX(BGFX renderCtx, Composite composite) {
        long handle = composite.handle;
        PlatformUtil.setPlatformWindow(0, handle, 0);
    }
    
}
