package twilight.bgfx.util;

/**
 * @author tmccrary
 *
 */
public class PlatformUtil {

	public static void setPlatformWindow(long ndt, long nwh, long context) {
		nsetPlatformWindow(ndt, nwh, context);
	}

	private static native void nsetPlatformWindow(long ndt, long nwh, long context);

}
