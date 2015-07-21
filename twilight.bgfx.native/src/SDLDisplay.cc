#ifndef BGFX_WINDOW
#define BGFX_WINDOW

#include "include/jni_cache.h"
#include "include/twilight_bgfx_window_SDLDisplay.h"
#include "include/twilight_bgfx_window_SDLWindow.h"

#include <SDL2/SDL.h>
#include "bgfx.h"
#include "bgfxplatform.h"
#include <stdio.h>
#include <string.h>

void JNICALL Java_twilight_bgfx_window_sdl_SDLDisplay_nSetCursor(JNIEnv* env, jobject self, jlong cursorPtr) {
    SDL_Cursor* cursor = (SDL_Cursor*) cursorPtr;
    SDL_SetCursor(cursor);
}

jlong JNICALL Java_twilight_bgfx_window_sdl_SDLDisplay_nCreatePlatformCursor(JNIEnv* env, jobject self, jint index) {
    SDL_SystemCursor sdlCursor = static_cast<SDL_SystemCursor>(index);
    SDL_Cursor* cursor = SDL_CreateSystemCursor(sdlCursor);
    return (jlong) cursor;
}

jlong JNICALL Java_twilight_bgfx_window_sdl_SDLDisplay_nIsValid(JNIEnv* env, jobject display) {
    return (jboolean) true;
}

jlong JNICALL Java_twilight_bgfx_window_sdl_SDLDisplay_nUpdate(JNIEnv* env, jobject self) {

    SDL_Event event;
    while (SDL_PollEvent(&event)) {
        switch (event.type) {

        case SDL_QUIT:
            break;

        case SDL_MOUSEWHEEL: {
            const SDL_MouseWheelEvent& mev = event.wheel;
            jobject event = env->NewObject(mouseWheelCls, mouseMove, 0, 0);

            int globalX, globalY;
            SDL_GetMouseState(&globalX, &globalY);

            int windowID = mev.windowID;

            env->CallVoidMethod(event, moveSetX, mev.x);
            env->CallVoidMethod(event, moveSetY, mev.y);
            env->CallVoidMethod(event, moveSetWindowId, windowID);

            env->CallVoidMethod(self, queueEventMethod, event);
            break;
        }

        case SDL_MOUSEMOTION: {
            const SDL_MouseMotionEvent& mev = event.motion;
            jobject event = env->NewObject(mouseMoveCls, mouseMove, 0, 0);

            int globalX, globalY;
            SDL_GetGlobalMouseState(&globalX, &globalY);

            int windowID = mev.windowID;

            env->CallVoidMethod(event, moveSetX, mev.x);
            env->CallVoidMethod(event, moveSetY, mev.y);
            env->CallVoidMethod(event, moveSetGlobalX, globalX);
            env->CallVoidMethod(event, moveSetGlobalY, globalY);
            env->CallVoidMethod(event, moveSetWindowId, windowID);

            env->CallVoidMethod(self, queueEventMethod, event);
            break;
        }

        case SDL_MOUSEBUTTONDOWN: {
            const SDL_MouseButtonEvent& mev = event.button;
            int button = 0;
            switch (mev.button) {
            default:
            case SDL_BUTTON_LEFT:
                button = 1;
                break;
            case SDL_BUTTON_MIDDLE:
                button = 2;
                break;
            case SDL_BUTTON_RIGHT:
                button = 3;
                break;
            }

            int windowID = mev.windowID;

            int globalX, globalY;
            SDL_GetGlobalMouseState(&globalX, &globalY);

            jobject javaEvent = env->NewObject(mousePressCls, mousePress, 0, 0);

            env->CallVoidMethod(javaEvent, setX, mev.x);
            env->CallVoidMethod(javaEvent, setY, mev.y);
            env->CallVoidMethod(javaEvent, setGlobalX, globalX);
            env->CallVoidMethod(javaEvent, setGlobalY, globalY);
            env->CallVoidMethod(javaEvent, setButton, button);
            env->CallVoidMethod(javaEvent, setTypeInt, 0);
            env->CallVoidMethod(javaEvent, setWindowId, windowID);

            env->CallVoidMethod(self, queueEventMethod, javaEvent);

        }
            break;
        case SDL_MOUSEBUTTONUP: {
            const SDL_MouseButtonEvent& mev = event.button;
            int button = 0;
            switch (mev.button) {
            default:
            case SDL_BUTTON_LEFT:
                button = 1;
                break;
            case SDL_BUTTON_MIDDLE:
                button = 2;
                break;
            case SDL_BUTTON_RIGHT:
                button = 3;
                break;
            }

            int windowID = mev.windowID;

            int globalX, globalY;

            SDL_GetGlobalMouseState(&globalX, &globalY);

            jobject event = env->NewObject(mousePressCls, mousePress, 0, 0);

            env->CallVoidMethod(event, setX, mev.x);
            env->CallVoidMethod(event, setY, mev.y);
            env->CallVoidMethod(event, setGlobalX, globalX);
            env->CallVoidMethod(event, setGlobalY, globalY);
            env->CallVoidMethod(event, setButton, button);
            env->CallVoidMethod(event, setTypeInt, 1);
            env->CallVoidMethod(event, setWindowId, windowID);

            env->CallVoidMethod(self, queueEventMethod, event);
        }
            break;

        case SDL_KEYDOWN: {
            const SDL_KeyboardEvent& keyEvent = event.key;

            jobject event = env->NewObject(keyPressCls, keyPress, 0, 0);

            env->CallVoidMethod(event, setKey, keyEvent.keysym);
            env->CallVoidMethod(event, setType, 1);
            env->CallVoidMethod(event, keySetWindowId, keyEvent.windowID);

            env->CallVoidMethod(self, queueEventMethod, event);
        }
        case SDL_KEYUP: {
            const SDL_KeyboardEvent& keyEvent = event.key;

            jobject event = env->NewObject(keyPressCls, keyPress, 0, 0);

            env->CallVoidMethod(event, setKey, keyEvent.keysym.scancode);
            env->CallVoidMethod(event, setType, 0);
            env->CallVoidMethod(event, keySetWindowId, keyEvent.windowID);

            env->CallVoidMethod(self, queueEventMethod, event);
        }
            break;

        case SDL_WINDOWEVENT: {

            int width1 = -1;
            int height1 = -1;
            int windowID = -1;
            const SDL_WindowEvent& wev = event.window;
            switch (wev.event) {
            case SDL_WINDOWEVENT_RESIZED:
            case SDL_WINDOWEVENT_SIZE_CHANGED:
                width1 = wev.data1;
                height1 = wev.data2;
                windowID = wev.windowID;

                env->CallVoidMethod(self, resizeConst, windowID, width1, height1);

                break;

            case SDL_WINDOWEVENT_SHOWN:
            case SDL_WINDOWEVENT_HIDDEN:
            case SDL_WINDOWEVENT_EXPOSED:
            case SDL_WINDOWEVENT_MOVED:
            case SDL_WINDOWEVENT_MINIMIZED:
            case SDL_WINDOWEVENT_MAXIMIZED:
            case SDL_WINDOWEVENT_RESTORED:
            case SDL_WINDOWEVENT_ENTER:
            case SDL_WINDOWEVENT_LEAVE:
            case SDL_WINDOWEVENT_FOCUS_GAINED:
            case SDL_WINDOWEVENT_FOCUS_LOST:
                break;

            case SDL_WINDOWEVENT_CLOSE:
                break;
            }
        }
            break;

        default: {
            break;
        }
        }
    }

    return 0;
}

JNIEXPORT jlong JNICALL Java_twilight_bgfx_window_sdl_SDLDisplay_nCreateDisplay(JNIEnv* env, jobject obj) {

    //putenv("SDL_VIDEO_X11_WMCLASS=Bling3D");
    int response = SDL_Init(SDL_INIT_VIDEO);

    return (jlong) response;
}

JNIEXPORT jint JNICALL Java_twilight_bgfx_window_sdl_SDLDisplay_nGetDPI(JNIEnv* env, jobject obj) {

    return (jint) 0;
}

JNIEXPORT jint JNICALL Java_twilight_bgfx_window_sdl_SDLDisplay_nGetWidth(JNIEnv* env, jobject display) {
    return 0;
}

JNIEXPORT jint JNICALL Java_twilight_bgfx_window_sdl_SDLDisplay_nGetHeight(JNIEnv* env, jobject display) {
    return 0;
}

JNIEXPORT jint JNICALL Java_twilight_bgfx_window_sdl_SDLDisplay_nGetPhysicalWidth(JNIEnv* env, jobject display) {
    return 0;
}

JNIEXPORT jint JNICALL Java_twilight_bgfx_window_sdl_SDLDisplay_nGetPhysicalHeight(JNIEnv* env, jobject display) {
    return 0;
}

#endif
