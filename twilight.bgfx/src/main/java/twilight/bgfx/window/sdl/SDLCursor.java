package twilight.bgfx.window.sdl;

import twilight.bgfx.window.Cursor;

public class SDLCursor extends Cursor {

    /** */
    public enum SDLPlatformCursorStyle {
        SDL_SYSTEM_CURSOR_ARROW, 
        SDL_SYSTEM_CURSOR_IBEAM, 
        SDL_SYSTEM_CURSOR_WAIT, 
        SDL_SYSTEM_CURSOR_CROSSHAIR, 
        SDL_SYSTEM_CURSOR_WAITARROW, 
        SDL_SYSTEM_CURSOR_SIZENWSE, 
        SDL_SYSTEM_CURSOR_SIZENESW, 
        SDL_SYSTEM_CURSOR_SIZEWE, 
        SDL_SYSTEM_CURSOR_SIZENS, 
        SDL_SYSTEM_CURSOR_SIZEALL, 
        SDL_SYSTEM_CURSOR_NO, 
        SDL_SYSTEM_CURSOR_HAND
    }

    public final long cursorPtr;
    
    private boolean systemCursor;

    public SDLCursor(long cursorPtr, boolean isSystem) {
        this.cursorPtr = cursorPtr;
        this.systemCursor = isSystem;
    }

    public boolean isSystemCursor() {
        return systemCursor;
    }
    
    public static boolean isValid(SDLCursor cursor) {
        return cursor.cursorPtr > 0;
    }
    
}
