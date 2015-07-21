#ifndef VERTEX_DECL
#define VERTEX_DECL

#include "include/jni_cache.h"
#include "include/twilight_bgfx_VertexDecl.h"

#include "bx/bx.h"
#include "bgfx.h"
#include "bgfxplatform.h"
#include <stdio.h>
#include <string.h>

inline bgfx::VertexDecl* GetVertexDecl(JNIEnv* env, jobject self) {
    if (!vertexDeclPtrFid) {
        return 0;
    }

    jlong declPtr = env->GetLongField(self, vertexDeclPtrFid);

    bgfx::VertexDecl* decl = reinterpret_cast<bgfx::VertexDecl*>(declPtr);
    return decl;
}

void JNICALL Java_twilight_bgfx_VertexDecl_ndestroy(JNIEnv* env, jobject self) {

    bgfx::VertexDecl* decl = GetVertexDecl(env, self);

    if (decl) {
        delete decl;
    }
}

jlong JNICALL Java_twilight_bgfx_VertexDecl_nbegin(JNIEnv* env, jobject self,
        jobject rendererType) {
    bgfx::VertexDecl* decl = new bgfx::VertexDecl();
    decl->begin();

    return (jlong) decl;
}

void JNICALL Java_twilight_bgfx_VertexDecl_nend(JNIEnv* env, jobject self) {
    bgfx::VertexDecl* decl = GetVertexDecl(env, self);
    decl->end();
}

void JNICALL Java_twilight_bgfx_VertexDecl_nAdd(JNIEnv* env, jobject self,
        jint attribute, jshort attributeSize, jint attributeFormat,
        jboolean normalized, jboolean asInt) {

    bgfx::VertexDecl* decl = GetVertexDecl(env, self);

    bgfx::Attrib::Enum attrib = static_cast<bgfx::Attrib::Enum>(attribute);
    bgfx::AttribType::Enum attribType =
            static_cast<bgfx::AttribType::Enum>(attributeFormat);

    decl->add(attrib, attributeSize, attribType);
}

void JNICALL Java_twilight_bgfx_VertexDecl_nSkip(JNIEnv* env, jobject self, jshort skip) {
    bgfx::VertexDecl* decl = GetVertexDecl(env, self);
    decl->skip(skip);
}

void JNICALL Java_twilight_bgfx_VertexDecl_nDecode(JNIEnv* env, jobject self,
        jobject, jshort, jobject, jboolean, jboolean) {

}

jboolean JNICALL Java_twilight_bgfx_VertexDecl_nHas(JNIEnv* env, jobject self,
        jobject attrib) {

}

jint JNICALL Java_twilight_bgfx_VertexDecl_ngetOffset(JNIEnv* env, jobject self,
        jobject) {

}

jint JNICALL Java_twilight_bgfx_VertexDecl_ngetStride(JNIEnv* env,
        jobject self) {

}

jlong JNICALL Java_twilight_bgfx_VertexDecl_ngetSize(JNIEnv* env, jobject self,
        jlong) {

}

#endif
