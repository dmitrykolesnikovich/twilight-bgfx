#ifndef BGFX_WINDOW
#define BGFX_WINDOW

#include "include/jni_cache.h"
#include "include/twilight_bgfx_window_SDLWindow.h"

#include <SDL2/SDL.h>
#include <SDL2/SDL_syswm.h>
#include "bgfx.h"
#include "bgfxplatform.h"
#include <stdio.h>
#include <string.h>
#include <iostream>

using std::cout;

typedef struct {
    unsigned long flags;
    unsigned long functions;
    unsigned long decorations;
    long inputMode;
    unsigned long status;
} Hints;

JNIEXPORT void JNICALL Java_twilight_bgfx_window_sdl_SDLWindow_nSetMaximized(JNIEnv* env, jobject self,
        jboolean maximize) {

    SDL_Window* m_window = (SDL_Window*) env->GetLongField(self, windowPtrField);

    if (maximize) {
        SDL_MaximizeWindow(m_window);
    } else {
        SDL_RestoreWindow(m_window);
    }
}

JNIEXPORT jint JNICALL Java_twilight_bgfx_window_sdl_SDLWindow_nGetAbsoluteX(JNIEnv* env, jobject self) {

    /*
     SDL_SysWMinfo info;

     SDL_Window* m_window = (SDL_Window*)env->GetLongField(self, windowPtrField);

     SDL_GetWindowWMInfo(m_window, &info);
     Display* x11Display = info.info.x11.display;
     Window root, child;

     int absX;
     int absY;
     int relX;
     int relY;
     unsigned int mask;

     Display *dsp = XOpenDisplay( NULL );
     XQueryPointer(dsp, DefaultRootWindow(dsp), &root, &child, &absX, &absY, &relX, &relY, &mask);
     XCloseDisplay(dsp);

     return (jint)absX;
     */

    return 0;
}

JNIEXPORT jint JNICALL Java_twilight_bgfx_window_sdl_SDLWindow_nGetAbsoluteY(JNIEnv* env, jobject self) {
    /*
     SDL_SysWMinfo info;

     SDL_Window* m_window = (SDL_Window*)env->GetLongField(self, windowPtrField);

     SDL_GetWindowWMInfo(m_window, &info);
     Display* x11Display = info.info.x11.display;
     Window root, child;

     int absX;
     int absY;
     int relX;
     int relY;
     unsigned int mask;

     Display *dsp = XOpenDisplay( NULL );
     XQueryPointer(dsp, DefaultRootWindow(dsp), &root, &child, &absX, &absY, &relX, &relY, &mask);
     XCloseDisplay(dsp);

     return (jint)absY;
     */

    return 0;
}

jlong JNICALL Java_twilight_bgfx_window_sdl_SDLWindow_createWindow(JNIEnv* env, jobject self, jobject display,
        jboolean undecorated) {

    jstring name = (jstring) env->GetObjectField(self, windowNameField);

    const char* windowName = env->GetStringUTFChars(name, NULL);

    int windowFlags = 0;

    if (undecorated) {
        windowFlags = SDL_WINDOW_RESIZABLE | SDL_WINDOW_BORDERLESS | SDL_WINDOW_HIDDEN;
    } else {
        windowFlags = SDL_WINDOW_RESIZABLE | SDL_WINDOW_HIDDEN;
    }

    SDL_Window* m_window = SDL_CreateWindow(windowName, SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED, 2, 2,
            windowFlags);

    env->ReleaseStringUTFChars(name, NULL);

    return (jlong) m_window;
}

jlong JNICALL Java_twilight_bgfx_window_sdl_SDLWindow_ngetNativeHandle(JNIEnv* env, jobject self, jlong windowPointer) {

    SDL_Window* window = SDL_CreateWindow("", 0, 0, 0, 0, SDL_WINDOW_HIDDEN);

    SDL_SysWMinfo wmi;
    SDL_VERSION(&wmi.version);
    if (!SDL_GetWindowWMInfo((SDL_Window*) windowPointer, &wmi)) {
        return 0;
    }

    SDL_DestroyWindow(window);

#ifdef defined TWILIGHT_BGFX_macosx
    return (jlong)wmi.info.cocoa.window;
#elif defined TWILIGHT_BGFX_linux
    return (jlong)wmi.info.x11.window;
#elif defined TWILIGHT_BGFX_win
    return (jlong)wmi.info.win.window;
#else
    return (jlong) 0;
#endif

}

jint JNICALL Java_twilight_bgfx_window_sdl_SDLWindow_ngetSDLWindowId(JNIEnv* env, jobject self, jlong windowPointer) {

    SDL_Window* m_window = (SDL_Window*) windowPointer;

    return (jint) SDL_GetWindowID(m_window);
}

jlong JNICALL Java_twilight_bgfx_window_sdl_SDLWindow_destroyWindow(JNIEnv* env, jobject self, jobject window) {

    SDL_Window* m_window = (SDL_Window*) env->GetLongField(self, windowPtrField);

    SDL_DestroyWindow(m_window);

    return (jlong) 0;
}

void JNICALL Java_twilight_bgfx_window_sdl_SDLWindow_initBGFX(JNIEnv* env, jobject self) {

    SDL_Window* m_window = (SDL_Window*) env->GetLongField(self, windowPtrField);

    bgfx::sdlSetWindow(m_window);
}

jlong JNICALL Java_twilight_bgfx_window_sdl_SDLWindow_nGetId(JNIEnv* env, jobject self) {

    SDL_Window* m_window = (SDL_Window*) env->GetLongField(self, windowPtrField);
    return (jlong) SDL_GetWindowID(m_window);
}

void JNICALL Java_twilight_bgfx_window_sdl_SDLWindow_nSetPosition(JNIEnv* env, jobject self, jint x, jint y) {

    SDL_Window* m_window = (SDL_Window*) env->GetLongField(self, windowPtrField);

    SDL_SetWindowPosition(m_window, x, y);
}

jobject JNICALL Java_twilight_bgfx_window_sdl_SDLWindow_nGetWindowRect(JNIEnv* env, jobject self) {

    SDL_Window* m_window = (SDL_Window*) env->GetLongField(self, windowPtrField);

    int x, y, width, height;

    SDL_GetWindowPosition(m_window, &x, &y);
    SDL_GetWindowSize(m_window, &width, &height);

    jobject rect = env->NewObject(rectClass, rectConst, x, y, width, height);

    return rect;
}

void JNICALL Java_twilight_bgfx_window_sdl_SDLWindow_nSetSize(JNIEnv* env, jobject self, jint width, jint height) {

    SDL_Window* m_window = (SDL_Window*) env->GetLongField(self, windowPtrField);

    SDL_SetWindowSize(m_window, width, height);
}

void JNICALL Java_twilight_bgfx_window_sdl_SDLWindow_nSetVisible(JNIEnv* env, jobject self, jboolean visible) {

    SDL_Window* m_window = (SDL_Window*) env->GetLongField(self, windowPtrField);

    if (visible) {
        SDL_ShowWindow(m_window);
    } else {
        SDL_HideWindow(m_window);
    }
}

void JNICALL Java_twilight_bgfx_window_sdl_SDLWindow_nRaise(JNIEnv* env, jobject self) {

    SDL_Window* m_window = (SDL_Window*) env->GetLongField(self, windowPtrField);

    SDL_RaiseWindow(m_window);
}

#endif
