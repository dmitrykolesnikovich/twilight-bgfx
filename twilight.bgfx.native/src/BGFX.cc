#ifndef BGFX_JAVA_BINDING
#define BGFX_JAVA_BINDING

#define __STDC_LIMIT_MACROS true
#include "bgfx.h"
#include "bgfxplatform.h"
#include "bx/fpumath.h"
#include "include/jni_cache.h"

#include <bx/readerwriter.h>
#include <bx/fpumath.h>

#include "include/twilight_bgfx_BGFX.h"

void JNICALL Java_twilight_bgfx_BGFX_cleanJVMMetadata(JNIEnv* env, jobject self) {
    env->DeleteGlobalRef(transientVertexBufferClazz);
    env->DeleteGlobalRef(transientIndexBufferClazz);
}

jint JNICALL Java_twilight_bgfx_BGFX_ncreateFrameBuffer__JIILtwilight_bgfx_TextureFormat_2(JNIEnv* env, jobject self,
        jlong nativeWindowPtr, jint width, jint height, jobject textureFormat) {
    jint formatValue = env->CallIntMethod(textureFormat, formatOrdinal);

    const bgfx::TextureFormat::Enum& nativeFormat = static_cast<bgfx::TextureFormat::Enum>(formatValue);
    bgfx::FrameBufferHandle handle = bgfx::createFrameBuffer((void*) nativeWindowPtr, width, height, nativeFormat);
    return handle.idx;
}

jint JNICALL Java_twilight_bgfx_BGFX_ncreateFrameBuffer__IILtwilight_bgfx_TextureFormat_2J(JNIEnv* env, jobject self,
        jint width, jint height, jobject textureFormat, jlong flags) {
    jint formatValue = env->CallIntMethod(textureFormat, formatOrdinal);

    const bgfx::TextureFormat::Enum& nativeFormat = static_cast<bgfx::TextureFormat::Enum>(formatValue);

    bgfx::FrameBufferHandle handle = bgfx::createFrameBuffer(width, height, nativeFormat, flags);
    return handle.idx;
}

jint JNICALL Java_twilight_bgfx_BGFX_ncreateFrameBuffer__I_3IZ(JNIEnv* env, jobject self, jint handleCount,
        jintArray textureHandles, jboolean destroyTextures) {

    bgfx::TextureHandle handles[handleCount];
    jint* textureHandleArray = env->GetIntArrayElements(textureHandles, 0);

    for (int i = 0; i < handleCount; ++i) {
        bgfx::TextureHandle handle;
        handle.idx = textureHandleArray[i];
        handles[i] = handle;
    }

    bgfx::FrameBufferHandle handle = bgfx::createFrameBuffer(handleCount, handles, destroyTextures);

    env->ReleaseIntArrayElements(textureHandles, textureHandleArray, 0);

    return handle.idx;
}

void JNICALL Java_twilight_bgfx_BGFX_ndestroyFrameBuffer(JNIEnv* env, jobject self, jint handleId) {
    bgfx::FrameBufferHandle fbHandle;
    fbHandle.idx = handleId;

    bgfx::destroyFrameBuffer(fbHandle);
}

jlong JNICALL Java_twilight_bgfx_BGFX_nsetTransform(JNIEnv* env, jobject self, jfloatArray matrix, jint unknown) {
    jsize matrixSize = env->GetArrayLength(matrix);
    jfloat* matrixFloat = env->GetFloatArrayElements(matrix, 0);

    bgfx::setTransform(matrixFloat);

    env->ReleaseFloatArrayElements(matrix, matrixFloat, 0);
}

void JNICALL Java_twilight_bgfx_BGFX_nsetViewTransform(JNIEnv* env, jobject self, jshort id, jfloatArray view,
        jfloatArray proj) {

    jsize viewSize = env->GetArrayLength(view);
    jfloat* viewMatrix = env->GetFloatArrayElements(view, 0);

    jsize projSize = env->GetArrayLength(proj);
    jfloat* projMatrix = env->GetFloatArrayElements(proj, 0);

    bgfx::setViewTransform(id, (void*) viewMatrix, (void*) projMatrix);

    env->ReleaseFloatArrayElements(view, viewMatrix, 0);
    env->ReleaseFloatArrayElements(proj, projMatrix, 0);
}

void JNICALL Java_twilight_bgfx_BGFX_nsetProgram(JNIEnv* env, jobject self, jint handleIndex) {
    bgfx::ProgramHandle handle;
    handle.idx = handleIndex;

    bgfx::setProgram(handle);
}

void JNICALL Java_twilight_bgfx_BGFX_ndestroyShader(JNIEnv* env, jobject self, jint shaderHandle) {
    bgfx::ShaderHandle handle;
    handle.idx = shaderHandle;

    bgfx::destroyShader(handle);
}

jint JNICALL Java_twilight_bgfx_BGFX_ncreateProgram(JNIEnv* env, jobject self, jint vertexShader, jint fragmentShader,
        jboolean destroyShaders) {
    bgfx::ShaderHandle vertexShaderHandle;
    vertexShaderHandle.idx = vertexShader;

    bgfx::ShaderHandle fragmentShaderHandle;
    fragmentShaderHandle.idx = fragmentShader;

    bgfx::ProgramHandle handle = bgfx::createProgram(vertexShaderHandle, fragmentShaderHandle, destroyShaders);

    return handle.idx;
}

void JNICALL Java_twilight_bgfx_BGFX_ndestroyProgram(JNIEnv* env, jobject self, jint programHandle) {
    bgfx::ProgramHandle handle;
    handle.idx = programHandle;

    bgfx::destroyProgram(handle);
}

jint JNICALL Java_twilight_bgfx_BGFX_ncreateShader(JNIEnv* env, jobject self, jobject buffer) {
    jint capacity = env->CallIntMethod(buffer, capacityMethod);
    void* shaderData = env->GetDirectBufferAddress(buffer);
    const bgfx::Memory* mem = bgfx::copy((uint8_t*) shaderData, capacity);

    bgfx::ShaderHandle handle = bgfx::createShader(mem);
    return handle.idx;
}

void JNICALL Java_twilight_bgfx_BGFX_nsetIndexBuffer__Ltwilight_bgfx_buffers_IndexBuffer_2JJ(JNIEnv* env, jobject self,
        jobject buffer, jlong start, jlong count) {
    jlong handleIndex = env->GetLongField(buffer, indexBufferHandle);

    bgfx::IndexBufferHandle handle;
    handle.idx = handleIndex;

    bgfx::setIndexBuffer(handle, start, count);
}

void JNICALL Java_twilight_bgfx_BGFX_nsetVertexBuffer__Ltwilight_bgfx_buffers_DynamicVertexBuffer_2J(JNIEnv* env,
        jobject self, jobject buffer, jlong vertexCount) {

    jlong handleIndex = env->GetLongField(buffer, dynamicVertexBufferHandle);

    bgfx::DynamicVertexBufferHandle handle;
    handle.idx = handleIndex;

    bgfx::setVertexBuffer(handle, vertexCount);
}

void JNICALL Java_twilight_bgfx_BGFX_nsetVertexBuffer__Ltwilight_bgfx_buffers_VertexBuffer_2JJ(JNIEnv* env,
        jobject self, jobject buffer, jlong startVertex, jlong vertexCount) {

    jlong handleIndex = env->GetLongField(buffer, vertexBufferHandle);

    bgfx::VertexBufferHandle handle;
    handle.idx = handleIndex;

    bgfx::setVertexBuffer(handle, 0, vertexCount);
}

void JNICALL Java_twilight_bgfx_BGFX_nsetVertexBuffer__Ltwilight_bgfx_buffers_TransientVertexBuffer_2(JNIEnv* env,
        jobject self, jobject transientBuffer) {

    bgfx::TransientVertexBuffer* tvp = (bgfx::TransientVertexBuffer*) env->GetLongField(transientBuffer,
            transientVertexBufferPointer);
    bgfx::setVertexBuffer(tvp);
}

void JNICALL Java_twilight_bgfx_BGFX_nsetVertexBuffer__Ltwilight_bgfx_buffers_TransientVertexBuffer_2JJ(JNIEnv* env,
        jobject self, jobject transientBuffer, jlong offset, jlong size) {
    bgfx::TransientVertexBuffer* tvp = (bgfx::TransientVertexBuffer*) env->GetLongField(transientBuffer,
            transientVertexBufferPointer);
    bgfx::setVertexBuffer(tvp, offset, size);
}

jobject JNICALL Java_twilight_bgfx_BGFX_ngetCaps(JNIEnv* env, jobject self) {

    const bgfx::Caps* caps = bgfx::getCaps();

    jobject javaCaps = env->NewObject(capsClazz, capsConstr);

    env->SetLongField(javaCaps, supportedFid, (jlong) caps->supported);
    //env->SetLongField(capsClazz, maxTextureSizeFid, caps->maxTextureSize);
    //env->SetLongField(capsClazz, maxDrawCallsFid, caps->maxDrawCalls);
    //env->SetLongField(capsClazz, maxFBAttachmentsFid, caps->maxFBAttachments);

    return javaCaps;
}

jint JNICALL Java_twilight_bgfx_BGFX_ncreateIndexBuffer(JNIEnv* env, jobject self, jobject buffer) {
    jint capacity = env->CallIntMethod(buffer, capacityMethod);
    jshort* indexData = (jshort*) env->GetDirectBufferAddress(buffer);

    const bgfx::Memory* memory = bgfx::copy((uint8_t*) indexData, capacity);

    bgfx::IndexBufferHandle handle = bgfx::createIndexBuffer(memory);
    return handle.idx;
}

void JNICALL Java_twilight_bgfx_BGFX_ndestroyIndexBuffer(JNIEnv* env, jobject self, jint handle) {

    bgfx::IndexBufferHandle indexHandle;
    indexHandle.idx = handle;

    bgfx::destroyIndexBuffer(indexHandle);
}

void JNICALL Java_twilight_bgfx_BGFX_ndestroyDynamicIndexBuffer(JNIEnv* env, jobject self, jint bufferHandle) {

    bgfx::DynamicIndexBufferHandle handle;
    handle.idx = bufferHandle;

    bgfx::destroyDynamicIndexBuffer(handle);
}

jint JNICALL Java_twilight_bgfx_BGFX_ncreateVertexBuffer(JNIEnv* env, jobject self, jobject buffer,
        jobject vertexDecl) {

    jint capacity = env->CallIntMethod(buffer, capacityMethod);

    void* vertexData = env->GetDirectBufferAddress(buffer);

    const bgfx::Memory* memory = bgfx::copy((uint8_t*) vertexData, capacity);

    // Dereference the pointer to comply with bgfx API
    const bgfx::VertexDecl& vertexDeclInst = *(bgfx::VertexDecl*) env->GetLongField(vertexDecl, vertexDeclPtrFid);

    // Create the buffer
    bgfx::VertexBufferHandle handle = bgfx::createVertexBuffer(memory, vertexDeclInst);

    return handle.idx;
}

void JNICALL Java_twilight_bgfx_BGFX_ndestroyVertexBuffer(JNIEnv* env, jobject self, jint bufferHandle) {
    bgfx::VertexBufferHandle handle;
    handle.idx = bufferHandle;

    bgfx::destroyVertexBuffer(handle);
}

jint JNICALL Java_twilight_bgfx_BGFX_ncreateDynamicVertexBuffer(JNIEnv* env, jobject self, jobject buffer,
        jobject vertexDecl) {

    jint capacity = env->CallIntMethod(buffer, capacityMethod);

    void* vertexData = env->GetDirectBufferAddress(buffer);

    const bgfx::Memory* memory = bgfx::copy((uint8_t*) vertexData, capacity);

    // Dereference the pointer to comply with bgfx API
    const bgfx::VertexDecl& vertexDeclInst = *(bgfx::VertexDecl*) env->GetLongField(vertexDecl, vertexDeclPtrFid);

    // Create the buffer
    bgfx::DynamicVertexBufferHandle handle = bgfx::createDynamicVertexBuffer(memory, vertexDeclInst);

    return handle.idx;
}

void JNICALL Java_twilight_bgfx_BGFX_nupdateDynamicVertexBuffer(JNIEnv* env, jobject self, jint handleIndex,
        jint offset, jint size, jobject buffer) {

    jint capacity = env->CallIntMethod(buffer, capacityMethod);

    void* vertexData = env->GetDirectBufferAddress(buffer);

    const bgfx::Memory* memory = bgfx::copy((uint8_t*) vertexData, capacity);

    bgfx::DynamicVertexBufferHandle handle;
    handle.idx = handleIndex;

    bgfx::updateDynamicVertexBuffer(handle, memory);
}

void JNICALL Java_twilight_bgfx_BGFX_ndestroyDynamicVertexBuffer(JNIEnv* env, jobject self, jint handleIndex) {

    bgfx::DynamicVertexBufferHandle handle;
    handle.idx = handleIndex;

    bgfx::destroyDynamicVertexBuffer(handle);
}

jint JNICALL Java_twilight_bgfx_BGFX_ncreateDynamicIndexBuffer__J(JNIEnv* env, jobject self, jlong count) {

    bgfx::DynamicIndexBufferHandle handle = bgfx::createDynamicIndexBuffer(count);

    return handle.idx;
}

jint JNICALL Java_twilight_bgfx_BGFX_ncreateDynamicIndexBuffer__Ljava_nio_ByteBuffer_2(JNIEnv* env, jobject self,
        jobject buffer) {
    jint capacity = env->CallIntMethod(buffer, capacityMethod);

    const void* indexData = env->GetDirectBufferAddress(buffer);

    const bgfx::Memory* mem = bgfx::copy((uint8_t*) indexData, capacity);
    bgfx::DynamicIndexBufferHandle handle = bgfx::createDynamicIndexBuffer(mem);
    return handle.idx;
}

void JNICALL Java_twilight_bgfx_BGFX_nupdateDynamicIndexBuffer(JNIEnv* env, jobject self, jint handleId,
        jobject buffer) {
    jint capacity = env->CallIntMethod(buffer, capacityMethod);

    const void* indexData = env->GetDirectBufferAddress(buffer);

    const bgfx::Memory* mem = bgfx::copy((uint8_t*) indexData, capacity);

    bgfx::DynamicIndexBufferHandle handle;
    handle.idx = handleId;

    bgfx::updateDynamicIndexBuffer(handle, mem);
}

jboolean JNICALL Java_twilight_bgfx_BGFX_ncheckAvailTransientIndexBuffer(JNIEnv* env, jobject self, jlong size) {
    return bgfx::checkAvailTransientIndexBuffer(size);
}

jboolean JNICALL Java_twilight_bgfx_BGFX_ncheckAvailTransientVertexBuffer(JNIEnv* env, jobject self, jlong size,
        jobject vertexDecl) {
    const bgfx::VertexDecl& vertexDeclInst = *(bgfx::VertexDecl*) env->GetLongField(vertexDecl, vertexDeclPtrFid);
    return bgfx::checkAvailTransientVertexBuffer(size, vertexDeclInst);
}

jboolean JNICALL Java_twilight_bgfx_BGFX_ncheckAvailTransientBuffers(JNIEnv* env, jobject self, jlong numVertices,
        jobject vertexDecl, jlong numIndices) {
    const bgfx::VertexDecl& vertexDeclInst = *reinterpret_cast<bgfx::VertexDecl*>(env->GetLongField(vertexDecl, vertexDeclPtrFid));
    return bgfx::checkAvailTransientBuffers(numVertices, vertexDeclInst, numIndices);
}

jobject JNICALL Java_twilight_bgfx_BGFX_nallocTransientIndexBuffer(JNIEnv* env, jobject self, jlong size) {

    // These are freed in the cleanup call
    bgfx::TransientIndexBuffer* nativeBuffer = new bgfx::TransientIndexBuffer();

    bgfx::allocTransientIndexBuffer(nativeBuffer, size);

    jobject bufferObj = env->NewObject(transientIndexBufferClazz, transientIndexBufferConstr);
    env->SetLongField(bufferObj, transientIndexBufferPointer, (jlong) nativeBuffer);

    return bufferObj;
}

jobject JNICALL Java_twilight_bgfx_BGFX_nallocTransientVertexBuffer(JNIEnv* env, jobject self, jlong size,
        jobject vertexDecl) {

    // These are freed in the cleanup call
    bgfx::TransientVertexBuffer* nativeBuffer = new bgfx::TransientVertexBuffer();

    const bgfx::VertexDecl& vertexDeclInst = *reinterpret_cast<bgfx::VertexDecl*>(env->GetLongField(vertexDecl, vertexDeclPtrFid));

    bgfx::allocTransientVertexBuffer(nativeBuffer, size, vertexDeclInst);

    jobject bufferObj = env->NewObject(transientVertexBufferClazz, transientVertexBufferConstr);
    env->SetLongField(bufferObj, transientVertexBufferPointer, (jlong) nativeBuffer);

    return bufferObj;
}

void JNICALL Java_twilight_bgfx_BGFX_nfreeTransientIndexBuffers(JNIEnv* env, jobject self, jlongArray bufferPointers) {

    int numElements = env->GetArrayLength(bufferPointers);

    jlong* pointers = env->GetLongArrayElements(bufferPointers, NULL);

    for (int i = 0; i < numElements; ++i) {
        bgfx::TransientIndexBuffer* buffer = reinterpret_cast<bgfx::TransientIndexBuffer*>(pointers[i]);
        delete buffer;
    }

    env->ReleaseLongArrayElements(bufferPointers, pointers, 0);
}

void JNICALL Java_twilight_bgfx_BGFX_nfreeTransientVertexBuffers(JNIEnv* env, jobject self, jlongArray bufferPointers) {

    int numElements = env->GetArrayLength(bufferPointers);

    jlong* pointers = env->GetLongArrayElements(bufferPointers, NULL);

    for (int i = 0; i < numElements; ++i) {
        bgfx::TransientVertexBuffer* buffer = reinterpret_cast<bgfx::TransientVertexBuffer*>(pointers[i]);
        delete buffer;
    }

    env->ReleaseLongArrayElements(bufferPointers, pointers, 0);
}

void JNICALL Java_twilight_bgfx_BGFX_nsetTexture__SIIJ(JNIEnv* env, jobject self, jshort stage, jint uniformId,
        jint textureId, jlong flags) {

    bgfx::UniformHandle uniformHandle;
    uniformHandle.idx = uniformId;

    bgfx::TextureHandle textureHandle;
    textureHandle.idx = textureId;

    bgfx::setTexture(stage, uniformHandle, textureHandle, flags);
}

void JNICALL Java_twilight_bgfx_BGFX_nsetTexture__SIISJ(JNIEnv* env, jobject self, jshort stage, jint uniformId,
        jint framebufferId, jshort attachmentIndex, jlong flags) {

    bgfx::UniformHandle uniformHandle;
    uniformHandle.idx = uniformId;

    bgfx::FrameBufferHandle framebufferHandle;
    framebufferHandle.idx = framebufferId;

    bgfx::setTexture(stage, uniformHandle, framebufferHandle, attachmentIndex, flags);
}

bgfx::TextureInfo createTextureInfo(JNIEnv* env, jobject javaTexInfo) {
    jlong storageSize = env->GetLongField(javaTexInfo, storageSizeFid);
    jint width = env->GetLongField(javaTexInfo, widthFid);
    jint height = env->GetLongField(javaTexInfo, heightFid);
    jint depth = env->GetLongField(javaTexInfo, depthFid);
    jint numMips = env->GetLongField(javaTexInfo, numMipsFid);
    jint bitsPerPixel = env->GetLongField(javaTexInfo, bitsPerPixelFid);

    bgfx::TextureInfo info;
    info.storageSize = storageSize;
    info.width = width;
    info.height = height;
    info.depth = depth;
    info.numMips = numMips;
    info.bitsPerPixel = bitsPerPixel;

    return info;
}

void JNICALL Java_twilight_bgfx_BGFX_ncalcTextureSize(JNIEnv* env, jobject self, jobject textureInfo, jint width,
        jint height, jint depth, jshort mipCount, jobject textureFormat) {

    jint formatValue = env->CallIntMethod(textureFormat, formatOrdinal);

    const bgfx::TextureFormat::Enum& nativeFormat = static_cast<bgfx::TextureFormat::Enum>(formatValue);

    bgfx::TextureInfo info = createTextureInfo(env, textureInfo);

    bgfx::calcTextureSize(info, width, height, depth, false, mipCount, nativeFormat);
}

jint JNICALL Java_twilight_bgfx_BGFX_ncreateTexture(JNIEnv* env, jobject self, jobject buffer, jlong flags, jshort skip,
        jobject outputTextureInfo) {

    jint capacity = env->CallIntMethod(buffer, capacityMethod);

    void* vertexData = env->GetDirectBufferAddress(buffer);

    const bgfx::Memory* memory = bgfx::copy((uint8_t*) vertexData, capacity);

    bgfx::TextureInfo info;
    bgfx::TextureHandle handle = bgfx::createTexture(memory, flags, skip, &info);

    return handle.idx;
}

jint JNICALL Java_twilight_bgfx_BGFX_ncreateTexture2D(JNIEnv* env, jobject self, jint width, jint height,
        jshort numMips, jobject textureFormat, jlong flags, jobject buffer) {
    if (!textureFormat) {
        return bgfx::TextureFormat::BGRA8;
    }

    jint formatValue = env->CallIntMethod(textureFormat, formatOrdinal);

    const bgfx::TextureFormat::Enum& nativeFormat = static_cast<bgfx::TextureFormat::Enum>(formatValue);

    if (!buffer) {
        bgfx::TextureHandle handle = bgfx::createTexture2D(width, height, numMips, nativeFormat, flags);
        return handle.idx;
    } else {
        jint capacity = env->CallIntMethod(buffer, capacityMethod);

        void* vertexData = env->GetDirectBufferAddress(buffer);

        const bgfx::Memory* memory = bgfx::copy((uint8_t*) vertexData, capacity);
        bgfx::TextureHandle handle = bgfx::createTexture2D(width, height, numMips, nativeFormat, flags, memory);
        return handle.idx;
    }
}

jint JNICALL Java_twilight_bgfx_BGFX_ncreateTexture3D(JNIEnv* env, jobject self, jint width, jint height, jint depth,
        jshort numMips, jobject textureFormat, jlong flags, jobject buffer) {

    jint formatValue = env->CallIntMethod(textureFormat, formatOrdinal);

    const bgfx::TextureFormat::Enum& nativeFormat = static_cast<bgfx::TextureFormat::Enum>(formatValue);

    if (buffer != 0) {
        bgfx::TextureHandle handle = bgfx::createTexture3D(width, height, depth, numMips, nativeFormat, flags);
        return handle.idx;
    } else {
        jint capacity = env->CallIntMethod(buffer, capacityMethod);

        void* vertexData = env->GetDirectBufferAddress(buffer);

        const bgfx::Memory* memory = bgfx::copy((uint8_t*) vertexData, capacity);
        bgfx::TextureHandle handle = bgfx::createTexture3D(width, height, depth, numMips, nativeFormat, flags, memory);
        return handle.idx;
    }
}

void JNICALL Java_twilight_bgfx_BGFX_nupdateTexture2D(JNIEnv* env, jobject self, jint handle, jshort mip, jint x,
        jint y, jint width, jint height, jobject buffer, jint pitch) {
    jint capacity = env->CallIntMethod(buffer, capacityMethod);

    void* vertexData = env->GetDirectBufferAddress(buffer);

    const bgfx::Memory* memory = bgfx::copy((uint8_t*) vertexData, capacity);

    bgfx::TextureHandle textureHandle;
    textureHandle.idx = handle;

    bgfx::updateTexture2D(textureHandle, mip, x, y, width, height, memory);
}

void JNICALL Java_twilight_bgfx_BGFX_ndestroyTexture(JNIEnv* env, jobject self, jint handle) {
    bgfx::TextureHandle textureHandle;
    textureHandle.idx = handle;

    bgfx::destroyTexture(textureHandle);
}

jint JNICALL Java_twilight_bgfx_BGFX_ncreateUniform(JNIEnv* env, jobject self, jstring uniformName, jint uniformType,
        jint count) {
    const bgfx::UniformType::Enum& nativeFormat = static_cast<bgfx::UniformType::Enum>(uniformType);
    const char* uniformChars = env->GetStringUTFChars(uniformName, NULL);
    bgfx::UniformHandle handle = bgfx::createUniform(uniformChars, nativeFormat, count);

    env->ReleaseStringUTFChars(uniformName, uniformChars);

    return handle.idx;
}

void JNICALL Java_twilight_bgfx_BGFX_ndestroyUniform(JNIEnv* env, jobject self, jint handleId) {

    bgfx::UniformHandle uniformHandle;
    uniformHandle.idx = handleId;

    bgfx::destroyUniform(uniformHandle);
}

void JNICALL Java_twilight_bgfx_BGFX_nsetViewName(JNIEnv* env, jobject self, jshort viewId, jstring name) {
    const char* nameChars = env->GetStringUTFChars(name, NULL);
    bgfx::setViewName(viewId, nameChars);
    env->ReleaseStringUTFChars(name, nameChars);
}

void JNICALL Java_twilight_bgfx_BGFX_nsetViewScissor(JNIEnv* env, jobject self, jshort viewId, jint x, jint y,
        jint width, jint height) {
    bgfx::setViewScissor(viewId, x, y, width, height);
}

void JNICALL Java_twilight_bgfx_BGFX_nsetViewSeq(JNIEnv* env, jobject self, jshort viewId, jboolean enabled) {
    bgfx::setViewSeq(viewId, enabled);
}

void JNICALL Java_twilight_bgfx_BGFX_nsetViewFrameBuffer(JNIEnv* env, jobject self, jshort viewId, jint framebufferId) {
    bgfx::FrameBufferHandle handle;
    handle.idx = framebufferId;

    bgfx::setViewFrameBuffer(viewId, handle);
}

void JNICALL Java_twilight_bgfx_BGFX_nsetMarker(JNIEnv* env, jobject self, jstring marker) {
    const char* markerChars = env->GetStringUTFChars(marker, NULL);
    bgfx::setMarker(markerChars);
    env->ReleaseStringUTFChars(marker, markerChars);
}

static uint64_t bgfx_blend_func(uint64_t _srcRGB, uint64_t _dstRGB, uint64_t _srcA, uint64_t _dstA) {
    return (0 | ((uint64_t(_srcRGB) | (uint64_t(_dstRGB) << 4))) | ((uint64_t(_srcA) | (uint64_t(_dstA) << 4)) << 8));
}

jlong JNICALL Java_twilight_bgfx_BGFX_ndoBlendFunc(JNIEnv* env, jobject self, jlong srcRGB, jlong dstRGB, jlong srcA,
        jlong dstA) {
    return bgfx_blend_func(srcRGB, dstRGB, srcA, dstA);
}

void JNICALL Java_twilight_bgfx_BGFX_nsetState(JNIEnv* env, jobject self, jlong flag, jlong rgba) {
    bgfx::setState(0 | flag, rgba);
}

void JNICALL Java_twilight_bgfx_BGFX_nsetStencil(JNIEnv* env, jobject self, jlong fstencil, jlong bstencil) {
    bgfx::setStencil(fstencil, bstencil);
}

jint JNICALL Java_twilight_bgfx_BGFX_nsetScissor__IIII(JNIEnv* env, jobject self, jint x, jint y, jint width,
        jint height) {
    bgfx::setScissor(x, y, width, height);
}

void JNICALL Java_twilight_bgfx_BGFX_nsetScissor__I(JNIEnv* env, jobject self, jint cache) {
    bgfx::setScissor(cache);
}

void JNICALL Java_twilight_bgfx_BGFX_nsetUniform__IFI(JNIEnv* env, jobject self, jint id, jfloat uniformValue,
        jint num) {
    bgfx::UniformHandle handle;
    handle.idx = id;

    bgfx::setUniform(handle, (void*) &uniformValue);
}

void JNICALL Java_twilight_bgfx_BGFX_nsetUniform__I_3FI(JNIEnv* env, jobject self, jint id, jfloatArray uniformArray,
        jint num) {

    bgfx::UniformHandle handle;
    handle.idx = id;

    jsize matrixSize = env->GetArrayLength(uniformArray);
    jfloat* floatData = env->GetFloatArrayElements(uniformArray, 0);
    bgfx::setUniform(handle, floatData, num);
    env->ReleaseFloatArrayElements(uniformArray, floatData, 0);
}

void JNICALL Java_twilight_bgfx_BGFX_nsetIndexBuffer__Ltwilight_bgfx_buffers_TransientIndexBuffer_2(JNIEnv* env,
        jobject self, jobject transientBuffer) {
    bgfx::TransientIndexBuffer* tip = (bgfx::TransientIndexBuffer*) env->GetLongField(transientBuffer,
            transientIndexBufferPointer);
    bgfx::setIndexBuffer(tip);
}

void JNICALL Java_twilight_bgfx_BGFX_nsetIndexBuffer__Ltwilight_bgfx_buffers_TransientIndexBuffer_2JJ(JNIEnv* env,
        jobject self, jobject transientBuffer, jlong offset, jlong size) {
    bgfx::TransientIndexBuffer* tip = (bgfx::TransientIndexBuffer*) env->GetLongField(transientBuffer,
            transientIndexBufferPointer);
    bgfx::setIndexBuffer(tip, offset, size);
}

void JNICALL Java_twilight_bgfx_BGFX_nsaveScreenShot(JNIEnv* env, jobject self, jstring outputPath) {
    const char* pathChars = env->GetStringUTFChars(outputPath, NULL);
    bgfx::saveScreenShot(pathChars);
    env->ReleaseStringUTFChars(outputPath, pathChars);
}

void JNICALL Java_twilight_bgfx_BGFX_nreset(JNIEnv* env, jobject self, jlong width, jlong height, jlong flags) {
    bgfx::reset(width, height, flags);
}

jlong JNICALL Java_twilight_bgfx_BGFX_nsubmit(JNIEnv* env, jobject self, jshort id, jshort depth) {
    return (jlong) bgfx::submit(id, depth);
}

void JNICALL Java_twilight_bgfx_BGFX_nsetViewRect(JNIEnv* env, jobject self, jshort id, jint x, jint y, jint width,
        jint height) {
    bgfx::setViewRect(id, x, y, width, height);
}

jlong JNICALL Java_twilight_bgfx_BGFX_nframe(JNIEnv* env, jobject self) {
    bgfx::frame();
}

void JNICALL Java_twilight_bgfx_BGFX_ninit(JNIEnv* env, jobject self, int rendererType, int vendorId, int deviceId) {
    bgfx::init(static_cast<bgfx::RendererType::Enum>(rendererType), vendorId, deviceId);
}

void JNICALL Java_twilight_bgfx_BGFX_nshutdown(JNIEnv* env, jobject self) {
    bgfx::shutdown();
}

void JNICALL Java_twilight_bgfx_BGFX_nsetViewClear(JNIEnv* env, jobject self, jshort id, jshort flags, jlong rgba,
        jfloat depth, jshort stencil) {
    bgfx::setViewClear(id, flags, rgba, depth, stencil);
}

void JNICALL Java_twilight_bgfx_BGFX_ndiscard(JNIEnv* env, jobject self) {
    bgfx::discard();
}

void JNICALL Java_twilight_bgfx_BGFX_nsetDebug(JNIEnv* env, jobject self, jlong flags) {
    bgfx::setDebug(flags);
}

void JNICALL Java_twilight_bgfx_BGFX_nsetUniform__ILjava_nio_ByteBuffer_2I(JNIEnv* env, jobject self, jint handleId,
        jobject buffer, jint num) {
    jint capacity = env->CallIntMethod(buffer, capacityMethod);

    const void* vertexData = env->GetDirectBufferAddress(buffer);

    bgfx::UniformHandle uniformHandle;
    uniformHandle.idx = handleId;

    bgfx::setUniform(uniformHandle, vertexData, num);
}

jint JNICALL Java_twilight_bgfx_BGFX_ngetRendererType(JNIEnv* env, jobject self) {

    bgfx::RendererType::Enum type = bgfx::getRendererType();
    return (jint) type;
}

void JNICALL Java_twilight_bgfx_BGFX_ndbgTextClear(JNIEnv* env, jobject self, jshort attr, jboolean small) {
    bgfx::dbgTextClear(attr, small);
}

void JNICALL Java_twilight_bgfx_BGFX_ndbgTextPrintf(JNIEnv* env, jobject self, jint x, jint y, jshort attr,
        jstring msg) {
    const char* msgChar = env->GetStringUTFChars(msg, 0);

    bgfx::dbgTextPrintf(x, y, attr, msgChar);

    env->ReleaseStringUTFChars(msg, msgChar);
}

jobject JNICALL Java_twilight_bgfx_BGFX_nalloc(JNIEnv* env, jobject self, jlong size) {

    const bgfx::Memory* mem = bgfx::alloc(size);

    jobject buffer = env->NewDirectByteBuffer(mem->data, size);
    return buffer;
}

jobject JNICALL Java_twilight_bgfx_BGFX_ncopy(JNIEnv* env, jobject self, jobject buffer) {
    jint capacity = env->CallIntMethod(buffer, capacityMethod);

    const void* data = env->GetDirectBufferAddress(buffer);

    const bgfx::Memory* mem = bgfx::copy(data, capacity);

    jobject newBuffer = env->NewDirectByteBuffer(mem->data, capacity);
    return newBuffer;
}

jobject JNICALL Java_twilight_bgfx_BGFX_nmakeRef(JNIEnv* env, jobject self, jobject buffer) {
    jint capacity = env->CallIntMethod(buffer, capacityMethod);

    const void* data = env->GetDirectBufferAddress(buffer);

    const bgfx::Memory* mem = bgfx::makeRef(data, capacity);

    jobject newBuffer = env->NewDirectByteBuffer(mem->data, capacity);
    return newBuffer;
}

void JNICALL Java_twilight_bgfx_BGFX_nupdateTexture3D(JNIEnv* env, jobject self, jint handleId, jshort mip, jint x,
        jint y, jint z, jint width, jint height, jint depth, jobject buffer) {
    bgfx::TextureHandle handle;
    handle.idx = handleId;

    jint capacity = env->CallIntMethod(buffer, capacityMethod);

    const void* texData = env->GetDirectBufferAddress(buffer);

    const bgfx::Memory* mem = bgfx::copy((uint8_t*) texData, capacity);

    bgfx::updateTexture3D(handle, mip, x, y, z, width, height, depth, mem);
}

void JNICALL Java_twilight_bgfx_BGFX_nupdateTextureCube(JNIEnv* env, jobject self, jint handleId, jshort side,
        jshort mip, jint x, jint y, jint width, jint height, jobject buffer, jint pitch) {
    bgfx::TextureHandle handle;
    handle.idx = handleId;

    jint capacity = env->CallIntMethod(buffer, capacityMethod);

    const void* texData = env->GetDirectBufferAddress(buffer);

    const bgfx::Memory* mem = bgfx::copy((uint8_t*) texData, capacity);

    bgfx::updateTextureCube(handle, side, mip, x, y, width, height, mem, pitch);
}

jint JNICALL Java_twilight_bgfx_BGFX_ncreateTextureCube(JNIEnv* env, jobject self, jint size, jshort numMips,
        jint format, jlong flags, jobject buffer) {
    bgfx::TextureHandle handle = BGFX_INVALID_HANDLE;

    if(buffer) {
        jint capacity = env->CallIntMethod(buffer, capacityMethod);
        const void* texData = env->GetDirectBufferAddress(buffer);
        const bgfx::Memory* mem = bgfx::copy((uint8_t*)texData, capacity);
        handle = bgfx::createTextureCube(size, numMips, bgfx::TextureFormat::Enum(format), flags, mem);

    } else {
        handle = bgfx::createTextureCube(size, numMips, bgfx::TextureFormat::Enum(format), flags);
    }

    return handle.idx;
}

jint JNICALL Java_twilight_bgfx_BGFX_ngetShaderUniforms(JNIEnv* env, jobject self, jint handleId) {
    bgfx::ShaderHandle handle;
    handle.idx = handleId;

    return bgfx::getShaderUniforms(handle);
}

void JNICALL Java_twilight_bgfx_BGFX_nsetInstanceDataBuffer(JNIEnv* env, jobject self, jobject buffer, jint count) {

    const bgfx::InstanceDataBuffer* bufferInst = (bgfx::InstanceDataBuffer*) env->GetLongField(buffer,
            instanceDataBufferPointer);

    bgfx::setInstanceDataBuffer(bufferInst, count);
}

jobject JNICALL Java_twilight_bgfx_BGFX_nallocInstanceDataBuffer(JNIEnv* env, jobject self, jlong num, jint stride) {

    // These are freed in the cleanup call
    const bgfx::InstanceDataBuffer* dataBuffer = bgfx::allocInstanceDataBuffer(num, stride);

    jobject bufferObj = env->NewObject(instanceDataBufferClazz, instanceDataBufferConstr);
    env->SetLongField(bufferObj, instanceDataBufferPointer, (jlong) dataBuffer);

    return bufferObj;
}

jboolean JNICALL Java_twilight_bgfx_BGFX_ncheckAvailInstanceDataBuffer(JNIEnv* env, jobject self, jlong num,
        jint stride) {
    return bgfx::checkAvailInstanceDataBuffer(num, stride);
}

/**
 * TODO
 */

void JNICALL Java_twilight_bgfx_BGFX_nvertexPack(JNIEnv*, jobject, jfloatArray, jboolean, jobject, jobject, jlong,
        jlong) {

}

void JNICALL Java_twilight_bgfx_BGFX_nvertexUnpack(JNIEnv*, jobject, jfloat, jobject, jobject, jlong, jlong) {

}

void JNICALL Java_twilight_bgfx_BGFX_nvertexConvert(JNIEnv*, jobject, jobject, jlong, jobject, jlong, jlong) {

}

jint JNICALL Java_twilight_bgfx_BGFX_nweldVertices(JNIEnv*, jobject, jlong, jobject, jlong, jint, jfloat) {

}

void JNICALL Java_twilight_bgfx_BGFX_nimageSwizzleBgra8(JNIEnv*, jobject, jlong, jlong, jlong, jlong, jlong) {

}

void JNICALL Java_twilight_bgfx_BGFX_nimageRgba8Downsample2x2(JNIEnv* env, jobject self, jlong, jlong, jlong, jlong,
        jlong) {

}

#endif
