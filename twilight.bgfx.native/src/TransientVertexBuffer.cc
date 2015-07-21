#ifndef TRANSIENT_VERTEX_BUFFER
#define TRANSIENT_VERTEX_BUFFER

#include "include/jni_cache.h"
#include "include/twilight_bgfx_buffers_TransientVertexBuffer.h"

#include "bgfx.h"

jobject JNICALL Java_twilight_bgfx_buffers_TransientVertexBuffer_nGetData(JNIEnv* env, jobject self) {
    jlong vertexBufferPtr = env->GetLongField(self, transientVertexBufferPointer);
    bgfx::TransientVertexBuffer* tvp = reinterpret_cast<bgfx::TransientVertexBuffer*>(vertexBufferPtr);

    return env->NewDirectByteBuffer(tvp->data, tvp->size);
}

#endif
