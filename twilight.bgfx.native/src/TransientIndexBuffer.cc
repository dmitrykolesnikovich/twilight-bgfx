#ifndef TRANSIENT_INDEX_BUFFER
#define TRANSIENT_INDEX_BUFFER

#include "include/jni_cache.h"
#include "include/twilight_bgfx_buffers_TransientIndexBuffer.h"

#include "bgfx.h"

jobject JNICALL Java_twilight_bgfx_buffers_TransientIndexBuffer_nGetData(JNIEnv* env, jobject self) {
    jlong indexBufferPtr = env->GetLongField(self, transientIndexBufferPointer);
    bgfx::TransientIndexBuffer* tvp = reinterpret_cast<bgfx::TransientIndexBuffer*>(indexBufferPtr);
    return env->NewDirectByteBuffer(tvp->data, tvp->size);
}

#endif
