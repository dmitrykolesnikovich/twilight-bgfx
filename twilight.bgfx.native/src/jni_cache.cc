#include "include/jni_cache.h"

// Declare the jni metadata in the source file to ensure
// they get linked in.

//------ BGFX

// ByteBuffer metadata
jclass byteBufferClazz;
jmethodID capacityMethod;

// BGFX metadata
jclass vertexDeclClazz;
jfieldID vertexDeclPtrFid;

// Caps
jclass capsClazz;
jmethodID capsConstr;
jfieldID supportedFid;
jfieldID maxTextureSizeFid;
jfieldID maxDrawCallsFid;
jfieldID maxFBAttachmentsFid;

// TextureInfo
jclass texInfoCls;
jfieldID storageSizeFid;
jfieldID widthFid;
jfieldID heightFid;
jfieldID depthFid;
jfieldID numMipsFid;
jfieldID bitsPerPixelFid;
jmethodID infoFormatOrdinal;

// TextureFormat
jclass texFormatCls;
jmethodID formatOrdinal;

// Buffers
jclass dynamicIndexBufferClazz;
jfieldID dynamicIndexBufferHandle;

jclass dynamicVertexBufferClazz;
jfieldID dynamicVertexBufferHandle;

jclass vertexBufferClazz;
jfieldID vertexBufferHandle;

jclass indexBufferClazz;
jfieldID indexBufferHandle;

// Transient buffers (only live for one frame)
jclass transientVertexBufferClazz;
jfieldID transientVertexBufferPointer;
jmethodID transientVertexBufferConstr;

jclass transientIndexBufferClazz;
jfieldID transientIndexBufferPointer;
jmethodID transientIndexBufferConstr;

jclass instanceDataBufferClazz;
jfieldID instanceDataBufferPointer;
jmethodID instanceDataBufferConstr;
jfieldID instanceDataBufferHandle;

//---- NanoVG

// NVGcolor jvm metadata
jclass cachedColorClass;
jmethodID cachedColorConstID;
jfieldID cachedrFid;
jfieldID cachedgFid;
jfieldID cachedbFid;
jfieldID cachedaFid;

// NVGpaint jvm metadata
jclass cachedPaintClass;
jmethodID cachedPaintConstID;
jfieldID cachedradiusFid;
jfieldID cachedfeatherFid;
jfieldID cachedimageFid;
jfieldID cachedrepeatFid;
jfieldID cachedinnerColorFid;
jfieldID cachedouterColorFid;
jfieldID cachedxformFid;
jfieldID cachedextentFid;

// NVGcontext jvm metadata
jclass cachedctxClass;
jmethodID cachedctxConst;

//-------- SDLDisplay
jmethodID resizeConst;
jmethodID moveConst;

jmethodID queueEventMethod;

jclass javaDisplay;

jclass mouseMoveCls;
jclass mouseWheelCls;

jmethodID mouseMove;

jmethodID moveSetX;
jmethodID moveSetY;
jmethodID moveSetGlobalX;
jmethodID moveSetGlobalY;
jmethodID moveSetWindowId;

jclass mousePressCls;
jmethodID mousePress;

jmethodID setX;
jmethodID setY;
jmethodID setGlobalX;
jmethodID setGlobalY;
jmethodID setButton;
jmethodID setTypeInt;
jmethodID setWindowId;

jclass keyPressCls;
jmethodID keyPress;

jmethodID setKey;
jmethodID setType;
jmethodID keySetWindowId;

//---- SDLWindow
jclass javaWindow;
jfieldID windowPtrField;
jfieldID windowNameField;

jclass rectClass;
jmethodID rectConst;


/**
 * Globally cache the various JVM variables so we don't
 * have to look them up at runtime.
 *
 * Global state like this is not ideal, but will work for now.
 */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved) {
    JNIEnv* env;
    int envStat = jvm->GetEnv((void **) &env, JNI_VERSION_1_6);

    // BGFX
    byteBufferClazz = (jclass) env->NewGlobalRef(env->FindClass("java/nio/ByteBuffer"));
    capacityMethod = env->GetMethodID(byteBufferClazz, "capacity", "()I");

    // Need to globalize the class reference
    jclass localCapsClazz = env->FindClass("twilight/bgfx/Caps");
    capsClazz = (jclass) env->NewGlobalRef(localCapsClazz);

    capsConstr = env->GetMethodID(capsClazz, "<init>", "()V");
    supportedFid = env->GetFieldID(capsClazz, "supported", "J");

    //maxTextureSizeFid = env->GetFieldID(capsClazz, "maxTextureSize", "J");
    //maxDrawCallsFid = env->GetFieldID(capsClazz, "maxDrawCalls", "J");
    //maxFBAttachmentsFid = env->GetFieldID(capsClazz, "maxFBAttachments", "J");

    vertexDeclClazz = (jclass) env->NewGlobalRef(env->FindClass("twilight/bgfx/VertexDecl"));
    vertexDeclPtrFid = env->GetFieldID(vertexDeclClazz, "vertexDeclPtr", "J");


    texInfoCls = (jclass) env->NewGlobalRef(env->FindClass("twilight/bgfx/TextureInfo"));
    storageSizeFid = env->GetFieldID(texInfoCls, "storageSize", "J");
    widthFid = env->GetFieldID(texInfoCls, "width", "I");
    heightFid = env->GetFieldID(texInfoCls, "height", "I");
    depthFid = env->GetFieldID(texInfoCls, "depth", "I");
    numMipsFid = env->GetFieldID(texInfoCls, "numMips", "S");
    bitsPerPixelFid = env->GetFieldID(texInfoCls, "bitsPerPixel", "S");
    infoFormatOrdinal = env->GetMethodID(texInfoCls, "getFormatOrdinal", "()I");

    texFormatCls = (jclass) env->NewGlobalRef(env->FindClass("twilight/bgfx/TextureFormat"));
    formatOrdinal = env->GetMethodID(texFormatCls, "ordinal", "()I");


    // Buffers
    dynamicIndexBufferClazz = (jclass) env->NewGlobalRef(env->FindClass("twilight/bgfx/buffers/DynamicIndexBuffer"));
    dynamicVertexBufferClazz = (jclass) env->NewGlobalRef(env->FindClass("twilight/bgfx/buffers/DynamicVertexBuffer"));

    vertexBufferClazz = (jclass) env->NewGlobalRef(env->FindClass("twilight/bgfx/buffers/VertexBuffer"));
    indexBufferClazz = (jclass) env->NewGlobalRef(env->FindClass("twilight/bgfx/buffers/IndexBuffer"));

    dynamicIndexBufferHandle = env->GetFieldID(dynamicIndexBufferClazz, "handle", "J");
    dynamicVertexBufferHandle = env->GetFieldID(dynamicVertexBufferClazz, "handle", "J");

    vertexBufferHandle = env->GetFieldID(vertexBufferClazz, "handle", "J");
    indexBufferHandle = env->GetFieldID(indexBufferClazz, "handle", "J");

    // Transient buffers
    // Need global refs because the class is used for NewObject in some spots
    transientVertexBufferClazz = (jclass) env->NewGlobalRef(
            env->FindClass("twilight/bgfx/buffers/TransientVertexBuffer"));
    transientIndexBufferClazz = (jclass) env->NewGlobalRef(
            env->FindClass("twilight/bgfx/buffers/TransientIndexBuffer"));
    transientVertexBufferConstr = env->GetMethodID(transientVertexBufferClazz, "<init>", "()V");
    transientIndexBufferConstr = env->GetMethodID(transientIndexBufferClazz, "<init>", "()V");
    transientVertexBufferPointer = env->GetFieldID(transientVertexBufferClazz, "pointer", "J");
    transientIndexBufferPointer = env->GetFieldID(transientIndexBufferClazz, "pointer", "J");

    instanceDataBufferClazz = (jclass) env->NewGlobalRef(env->FindClass("twilight/bgfx/buffers/InstanceDataBuffer"));
    instanceDataBufferConstr = env->GetMethodID(instanceDataBufferClazz, "<init>", "()V");
    instanceDataBufferPointer = env->GetFieldID(instanceDataBufferClazz, "pointer", "J");

    //---- NanoVG

    // Color
    jclass colorClass = env->FindClass("twilight/bgfx/nanovg/NVGcolor");
    cachedColorClass = (jclass) env->NewGlobalRef(colorClass);

    cachedColorConstID = env->GetMethodID(cachedColorClass, "<init>", "(FFFF)V");

    cachedrFid = env->GetFieldID(cachedColorClass, "r", "F");
    cachedgFid = env->GetFieldID(cachedColorClass, "g", "F");
    cachedbFid = env->GetFieldID(cachedColorClass, "b", "F");
    cachedaFid = env->GetFieldID(cachedColorClass, "a", "F");

    // Paint
    jclass paintClass = env->FindClass("twilight/bgfx/nanovg/NVGpaint");
    cachedPaintClass = (jclass) env->NewGlobalRef(paintClass);

    cachedPaintConstID = env->GetMethodID(cachedPaintClass, "<init>",
            "([F[FFFLtwilight/bgfx/nanovg/NVGcolor;Ltwilight/bgfx/nanovg/NVGcolor;II)V");
    cachedradiusFid = env->GetFieldID(cachedPaintClass, "radius", "F");
    cachedfeatherFid = env->GetFieldID(cachedPaintClass, "feather", "F");
    cachedimageFid = env->GetFieldID(cachedPaintClass, "image", "I");
    cachedrepeatFid = env->GetFieldID(cachedPaintClass, "repeat", "I");
    cachedinnerColorFid = env->GetFieldID(cachedPaintClass, "innerColor", "Ltwilight/bgfx/nanovg/NVGcolor;");
    cachedouterColorFid = env->GetFieldID(cachedPaintClass, "outerColor", "Ltwilight/bgfx/nanovg/NVGcolor;");
    cachedxformFid = env->GetFieldID(cachedPaintClass, "xform", "[F");
    cachedextentFid = env->GetFieldID(cachedPaintClass, "extent", "[F");

    // Context
    jclass ctxClass = env->FindClass("twilight/bgfx/nanovg/NVGcontext");
    cachedctxClass = (jclass) env->NewGlobalRef(ctxClass);

    cachedctxConst = env->GetMethodID(cachedctxClass, "<init>", "(J)V");

    //----- SDLDisplay
    javaDisplay = (jclass) env->NewGlobalRef(env->FindClass("twilight/bgfx/window/sdl/SDLDisplay"));

    mouseMoveCls = (jclass) env->NewGlobalRef(env->FindClass("twilight/bgfx/window/events/MouseMove"));
    mouseWheelCls = (jclass) env->NewGlobalRef(env->FindClass("twilight/bgfx/window/events/MouseWheel"));
    mousePressCls = (jclass) env->NewGlobalRef(env->FindClass("twilight/bgfx/window/events/Mouse"));
    keyPressCls = (jclass) env->NewGlobalRef(env->FindClass("twilight/bgfx/window/events/Keyboard"));

    resizeConst = env->GetMethodID(javaDisplay, "windowResize", "(JII)V");
    moveConst = env->GetMethodID(javaDisplay, "windowMove", "(JII)V");

    queueEventMethod = env->GetMethodID(javaDisplay, "queueEvent", "(Ltwilight/bgfx/window/events/Event;)V");

    mouseMove = env->GetMethodID(mouseMoveCls, "<init>", "(JJ)V");

    moveSetX = env->GetMethodID(mouseMoveCls, "setX", "(I)V");
    moveSetY = env->GetMethodID(mouseMoveCls, "setY", "(I)V");
    moveSetGlobalX = env->GetMethodID(mouseMoveCls, "setGlobalX", "(I)V");
    moveSetGlobalY = env->GetMethodID(mouseMoveCls, "setGlobalY", "(I)V");
    moveSetWindowId = env->GetMethodID(mouseMoveCls, "setWindowId", "(J)V");

    mousePress = env->GetMethodID(mousePressCls, "<init>", "(JJ)V");

    setX = env->GetMethodID(mousePressCls, "setX", "(I)V");
    setY = env->GetMethodID(mousePressCls, "setY", "(I)V");
    setGlobalX = env->GetMethodID(mousePressCls, "setGlobalX", "(I)V");
    setGlobalY = env->GetMethodID(mousePressCls, "setGlobalY", "(I)V");
    setButton = env->GetMethodID(mousePressCls, "setButton", "(I)V");
    setTypeInt = env->GetMethodID(mousePressCls, "setTypeInt", "(I)V");
    setWindowId = env->GetMethodID(mousePressCls, "setWindowId", "(J)V");

    keyPress = env->GetMethodID(keyPressCls, "<init>", "(JJ)V");

    setKey = env->GetMethodID(keyPressCls, "setKey", "(I)V");
    setType = env->GetMethodID(keyPressCls, "setTypeInt", "(I)V");
    keySetWindowId = env->GetMethodID(keyPressCls, "setWindowId", "(J)V");

    //---- SDLWindow
    rectClass = (jclass) env->NewGlobalRef(env->FindClass("twilight/bgfx/window/WindowRect"));
    rectConst = env->GetMethodID(rectClass, "<init>", "(IIII)V");

    javaWindow = (jclass) env->NewGlobalRef(env->FindClass("twilight/bgfx/window/sdl/SDLWindow"));
    windowPtrField = env->GetFieldID(javaWindow, "SDLWindowPtr", "J");
    windowNameField = env->GetFieldID(javaWindow, "name", "Ljava/lang/String;");


    return JNI_VERSION_1_6;
}
