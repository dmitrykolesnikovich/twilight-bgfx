#ifndef BGFX_JNI_CACHEH
#define BGFX_JNI_CACHEH

#include "jni.h"

//------ BGFX

// ByteBuffer metadata
extern jclass byteBufferClazz;
extern jmethodID capacityMethod;

// BGFX metadata
extern jclass vertexDeclClazz;
extern jfieldID vertexDeclPtrFid;

// Caps
extern jclass capsClazz;
extern jmethodID capsConstr;
extern jfieldID supportedFid;
extern jfieldID maxTextureSizeFid;
extern jfieldID maxDrawCallsFid;
extern jfieldID maxFBAttachmentsFid;

// TextureInfo
extern jclass texInfoCls;
extern jfieldID storageSizeFid;
extern jfieldID widthFid;
extern jfieldID heightFid;
extern jfieldID depthFid;
extern jfieldID numMipsFid;
extern jfieldID bitsPerPixelFid;
extern jmethodID infoFormatOrdinal;

// TextureFormat
extern jclass texFormatCls;
extern jmethodID formatOrdinal;

// Buffers
extern jclass dynamicIndexBufferClazz;
extern jfieldID dynamicIndexBufferHandle;

extern jclass dynamicVertexBufferClazz;
extern jfieldID dynamicVertexBufferHandle;

extern jclass vertexBufferClazz;
extern jfieldID vertexBufferHandle;

extern jclass indexBufferClazz;
extern jfieldID indexBufferHandle;

// Transient buffers (only live for one frame)
extern jclass transientVertexBufferClazz;
extern jfieldID transientVertexBufferPointer;
extern jmethodID transientVertexBufferConstr;

extern jclass transientIndexBufferClazz;
extern jfieldID transientIndexBufferPointer;
extern jmethodID transientIndexBufferConstr;

extern jclass instanceDataBufferClazz;
extern jfieldID instanceDataBufferPointer;
extern jmethodID instanceDataBufferConstr;
extern jfieldID instanceDataBufferHandle;

//---- NanoVG

// NVGcolor jvm metadata
extern jclass cachedColorClass;
extern jmethodID cachedColorConstID;
extern jfieldID cachedrFid;
extern jfieldID cachedgFid;
extern jfieldID cachedbFid;
extern jfieldID cachedaFid;

// NVGpaint jvm metadata
extern jclass cachedPaintClass;
extern jmethodID cachedPaintConstID;
extern jfieldID cachedradiusFid;
extern jfieldID cachedfeatherFid;
extern jfieldID cachedimageFid;
extern jfieldID cachedrepeatFid;
extern jfieldID cachedinnerColorFid;
extern jfieldID cachedouterColorFid;
extern jfieldID cachedxformFid;
extern jfieldID cachedextentFid;

// NVGcontext jvm metadata
extern jclass cachedctxClass;
extern jmethodID cachedctxConst;

//-------- SDLDisplay


extern jmethodID resizeConst;
extern jmethodID moveConst;

extern jmethodID queueEventMethod;

extern jclass javaDisplay;

extern jclass mouseMoveCls;
extern jclass mouseWheelCls;

extern jmethodID mouseMove;

extern jmethodID moveSetX;
extern jmethodID moveSetY;
extern jmethodID moveSetGlobalX;
extern jmethodID moveSetGlobalY;
extern jmethodID moveSetWindowId;

extern jclass mousePressCls;
extern jmethodID mousePress;

extern jmethodID setX;
extern jmethodID setY;
extern jmethodID setGlobalX;
extern jmethodID setGlobalY;
extern jmethodID setButton;
extern jmethodID setTypeInt;
extern jmethodID setWindowId;

extern jclass keyPressCls;
extern jmethodID keyPress;

extern jmethodID setKey;
extern jmethodID setType;
extern jmethodID keySetWindowId;

//---- SDLWindow
extern jclass javaDisplay;
extern jclass javaWindow;
extern jfieldID windowPtrField;
extern jfieldID windowNameField;

extern jclass rectClass;
extern jmethodID rectConst;

//---- TransientIndexBuffer
extern jclass transientIndexBufferClazz;
extern jfieldID transientIndexBufferPointer;

//---- TransientVertexBuffer
extern jclass transientVertexBufferClazz;
extern jfieldID transientVertexBufferPointer;

//---- VertexDecl
extern jclass vertexDeclClazz;
extern jfieldID vertexDeclPtrFid;

#endif
