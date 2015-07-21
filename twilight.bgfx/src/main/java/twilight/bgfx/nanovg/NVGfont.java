package twilight.bgfx.nanovg;

public class NVGfont {

    final int handle;

    NVGfont(int handle) {
        if (handle == -1) {
            throw new RuntimeException("Invalid font handle!");
        }

        this.handle = handle;
    }

}
