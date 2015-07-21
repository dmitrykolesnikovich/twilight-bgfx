#include "include/jni_cache.h"
#include "include/twilight_bgfx_nanovg_NanoVG.h"
#include "nanovg.h"

#define STB_TRUETYPE_IMPLEMENTATION
#include "stb/stb_truetype.h"

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_ndisposeJVMMetadata(JNIEnv* env, jobject self) {
    env->DeleteGlobalRef(cachedctxClass);
    env->DeleteGlobalRef(cachedPaintClass);
    env->DeleteGlobalRef(cachedColorClass);
}

inline static jobject nvgColorToJava(JNIEnv* env, NVGcolor color) {
    jobject javaColor = env->NewObject(cachedColorClass, cachedColorConstID, color.r, color.g, color.b, color.a);
    return javaColor;
}

inline static NVGcolor javaToNVGColor(JNIEnv* env, jobject object) {
    jfloat r = env->GetFloatField(object, cachedrFid);
    jfloat g = env->GetFloatField(object, cachedgFid);
    jfloat b = env->GetFloatField(object, cachedbFid);
    jfloat a = env->GetFloatField(object, cachedaFid);

    struct NVGcolor color;
    color.r = r;
    color.g = g;
    color.b = b;
    color.a = a;

    return color;
}

inline static jobject nvgPaintToJava(JNIEnv* env, NVGpaint paint) {
    jfloatArray xformJavaArray = env->NewFloatArray(6);
    jfloat* xformData = env->GetFloatArrayElements(xformJavaArray, 0);
    memcpy(xformData, &(paint.xform), 6);
    env->ReleaseFloatArrayElements(xformJavaArray, xformData, 0);

    jfloatArray extentJavaArray = env->NewFloatArray(2);
    jfloat* extentData = env->GetFloatArrayElements(extentJavaArray, 0);
    memcpy(extentData, &(paint.extent), 2);
    env->ReleaseFloatArrayElements(extentJavaArray, extentData, 0);

    jobject javaInnerColor = nvgColorToJava(env, paint.innerColor);
    jobject javaOuterColor = nvgColorToJava(env, paint.outerColor);

    jobject javaPaint = env->NewObject(cachedPaintClass, cachedPaintConstID, xformJavaArray, extentJavaArray,
            paint.radius, paint.feather, javaInnerColor, javaOuterColor, paint.image, 0);

    return javaPaint;
}

inline static NVGpaint javaToNVGPaint(JNIEnv* env, jobject object) {
    jfloat radius = env->GetFloatField(object, cachedradiusFid);
    jfloat feather = env->GetFloatField(object, cachedfeatherFid);
    jint image = env->GetFloatField(object, cachedimageFid);
    jint repeat = env->GetFloatField(object, cachedrepeatFid);

    jobject javaInnerColor = env->GetObjectField(object, cachedinnerColorFid);
    NVGcolor innerColor = javaToNVGColor(env, javaInnerColor);

    jobject javaOuterColor = env->GetObjectField(object, cachedouterColorFid);
    NVGcolor outerColor = javaToNVGColor(env, javaOuterColor);

    NVGpaint paint;
    paint.radius = radius;
    paint.feather = feather;
    paint.image = image;
    //paint.repeat = 0;
    paint.innerColor = innerColor;
    paint.outerColor = outerColor;

    jfloatArray xformJavaArray = static_cast<jfloatArray>(env->GetObjectField(object, cachedxformFid));

    jfloat* xformData = env->GetFloatArrayElements(xformJavaArray, 0);
    memcpy(&(paint.xform), xformData, 6);
    env->ReleaseFloatArrayElements(xformJavaArray, xformData, JNI_ABORT);

    jfloatArray extentJavaArray = static_cast<jfloatArray>(env->GetObjectField(object, cachedextentFid));
    jfloat* extentData = env->GetFloatArrayElements(extentJavaArray, 0);

    memcpy(&(paint.extent), xformData, 2);
    env->ReleaseFloatArrayElements(extentJavaArray, extentData, JNI_ABORT);

    return paint;
}

jobject JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgCreate(JNIEnv* env, jobject, jint atlasw, jint atlash, jint edgeaa,
        jint viewid) {
    NVGcontext* context = nvgCreate(edgeaa, static_cast<unsigned char>(viewid));
    jobject ctx = env->NewObject(cachedctxClass, cachedctxConst, reinterpret_cast<jlong>(context));
    return ctx;
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgDelete(JNIEnv* env, jobject self, jlong ctx) {
    nvgDelete(reinterpret_cast<NVGcontext*>(ctx));
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgBeginFrame(JNIEnv* env, jobject self, jlong ctx, jint windowWidth,
        jint windowHeight, jfloat devicePixelRatio, jint alphaBlend) {
    nvgBeginFrame(reinterpret_cast<NVGcontext*>(ctx), windowWidth, windowHeight, devicePixelRatio);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgEndFrame(JNIEnv* env, jobject self, jlong ctx) {
    nvgEndFrame(reinterpret_cast<NVGcontext*>(ctx));
}

jobject JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgRGB(JNIEnv* env, jobject self, jchar r, jchar g, jchar b) {
    NVGcolor color = nvgRGB(r, g, b);
    jobject javaColor = nvgColorToJava(env, color);
    return javaColor;
}

jobject JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgRGBf(JNIEnv* env, jobject self, jfloat r, jfloat g, jfloat b) {
    NVGcolor color = nvgRGBf(r, g, b);
    jobject javaColor = nvgColorToJava(env, color);
    return javaColor;
}

jobject JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgRGBA(JNIEnv* env, jobject self, jchar r, jchar g, jchar b,
        jchar a) {
    NVGcolor color = nvgRGBA(r, g, b, a);
    jobject javaColor = nvgColorToJava(env, color);
    return javaColor;
}

jobject JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgRGBAf(JNIEnv* env, jobject self, jfloat r, jfloat g, jfloat b,
        jfloat a) {
    NVGcolor color = nvgRGBAf(r, g, b, a);
    jobject javaColor = nvgColorToJava(env, color);
    return javaColor;
}

jobject JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgLerpRGBA(JNIEnv* env, jobject self, jobject javac0, jobject javac1,
        jfloat u) {
    NVGcolor c0 = javaToNVGColor(env, javac0);
    NVGcolor c1 = javaToNVGColor(env, javac1);
    NVGcolor lerpedColor = nvgLerpRGBA(c0, c1, u);
    jobject javaColor = nvgColorToJava(env, lerpedColor);
    return javaColor;
}

jobject JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgTransRGBA(JNIEnv* env, jobject self, jobject javaColor, jchar r) {
    NVGcolor c1 = javaToNVGColor(env, javaColor);
    NVGcolor color = nvgTransRGBA(c1, r);
    return nvgColorToJava(env, color);
}

jobject JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgTransRGBAf(JNIEnv* env, jobject self, jobject javaColor,
        jfloat c) {
    NVGcolor c1 = javaToNVGColor(env, javaColor);
    NVGcolor color = nvgTransRGBAf(c1, c);
    return nvgColorToJava(env, color);
}

jobject JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgHSL(JNIEnv* env, jobject self, jfloat h, jfloat s, jfloat l) {
    NVGcolor color = nvgHSL(h, s, l);
    return nvgColorToJava(env, color);
}

jobject JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgHSLA(JNIEnv* env, jobject self, jfloat h, jfloat s, jfloat l,
        jchar a) {
    NVGcolor color = nvgHSLA(h, s, l, a);
    return nvgColorToJava(env, color);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgSave(JNIEnv* env, jobject self, jlong ctx) {
    nvgSave(reinterpret_cast<NVGcontext*>(ctx));
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgRestore(JNIEnv* env, jobject self, jlong ctx) {
    nvgRestore(reinterpret_cast<NVGcontext*>(ctx));
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgReset(JNIEnv* env, jobject self, jlong ctx) {
    nvgReset(reinterpret_cast<NVGcontext*>(ctx));
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgStrokeColor(JNIEnv* env, jobject self, jlong ctx, jobject javaColor) {
    nvgStrokeColor(reinterpret_cast<NVGcontext*>(ctx), javaToNVGColor(env, javaColor));
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgStrokePaint(JNIEnv* env, jobject self, jlong ctx, jobject javaPaint) {
    nvgStrokePaint(reinterpret_cast<NVGcontext*>(ctx), javaToNVGPaint(env, javaPaint));
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgFillColor(JNIEnv* env, jobject self, jlong ctx, jobject javaColor) {
    nvgFillColor(reinterpret_cast<NVGcontext*>(ctx), javaToNVGColor(env, javaColor));
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgFillPaint(JNIEnv* env, jobject self, jlong ctx, jobject javaPaint) {
    nvgFillPaint(reinterpret_cast<NVGcontext*>(ctx), javaToNVGPaint(env, javaPaint));
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgMiterLimit(JNIEnv* env, jobject self, jlong ctx, jfloat limit) {
    nvgMiterLimit(reinterpret_cast<NVGcontext*>(ctx), limit);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgStrokeWidth(JNIEnv* env, jobject self, jlong ctx, jfloat size) {
    nvgStrokeWidth(reinterpret_cast<NVGcontext*>(ctx), size);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgLineCap(JNIEnv* env, jobject self, jlong ctx, jint cap) {
    nvgLineCap(reinterpret_cast<NVGcontext*>(ctx), cap);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgLineJoin(JNIEnv* env, jobject self, jlong ctx, jint join) {
    nvgLineJoin(reinterpret_cast<NVGcontext*>(ctx), join);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgResetTransform(JNIEnv* env, jobject self, jlong ctx) {
    nvgResetTransform(reinterpret_cast<NVGcontext*>(ctx));
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgTransform(JNIEnv* env, jobject self, jlong ctx, jfloat a, jfloat b,
        jfloat c, jfloat d, jfloat e, jfloat f) {
    nvgTransform(reinterpret_cast<NVGcontext*>(ctx), a, b, c, d, e, f);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgTranslate(JNIEnv* env, jobject self, jlong ctx, jfloat x, jfloat y) {
    nvgTranslate(reinterpret_cast<NVGcontext*>(ctx), x, y);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgRotate(JNIEnv* env, jobject self, jlong ctx, jfloat angle) {
    nvgRotate(reinterpret_cast<NVGcontext*>(ctx), angle);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgScale(JNIEnv* env, jobject self, jlong ctx, jfloat x, jfloat y) {
    nvgScale(reinterpret_cast<NVGcontext*>(ctx), x, y);
}

jint JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgCreateImage(JNIEnv* env, jobject self, jlong ctx, jstring path) {
    const char* utfPath = env->GetStringUTFChars(path, NULL);
    int imagePath = nvgCreateImage(reinterpret_cast<NVGcontext*>(ctx), utfPath, 0);
    env->ReleaseStringUTFChars(path, utfPath);
    return imagePath;
}

jint JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgCreateImageMem(JNIEnv* env, jobject self, jlong ctx, jobject buffer,
        jint size) {
    unsigned char* bufferData = reinterpret_cast<unsigned char*>(env->GetDirectBufferAddress(buffer));
    int handle = nvgCreateImageMem(reinterpret_cast<NVGcontext*>(ctx), 0, bufferData, size);
    return handle;
}

jint JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgCreateImageRGBA(JNIEnv* env, jobject self, jlong ctx, jint w, jint h,
        jobject buffer) {
    unsigned char* bufferData = reinterpret_cast<unsigned char*>(env->GetDirectBufferAddress(buffer));
    int handle = nvgCreateImageRGBA(reinterpret_cast<NVGcontext*>(ctx), w, h, 0, bufferData);
    return handle;
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgUpdateImage(JNIEnv* env, jobject self, jlong ctx, jint imageHandle,
        jobject buffer) {
    unsigned char* bufferData = (unsigned char*) env->GetDirectBufferAddress(buffer);
    nvgUpdateImage(reinterpret_cast<NVGcontext*>(ctx), imageHandle, bufferData);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgDeleteImage(JNIEnv* env, jobject self, jlong ctx, jint image) {
    nvgDeleteImage(reinterpret_cast<NVGcontext*>(ctx), image);
}

jobject JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgLinearGradient(JNIEnv* env, jobject self, jlong ctx, jfloat sx,
        jfloat sy, jfloat ex, jfloat ey, jobject icol, jobject ocol) {
    NVGpaint paint = nvgLinearGradient(reinterpret_cast<NVGcontext*>(ctx), sx, sy, ex, ey, javaToNVGColor(env, icol),
            javaToNVGColor(env, ocol));
    return nvgPaintToJava(env, paint);
}

jobject JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgBoxGradient(JNIEnv* env, jobject self, jlong ctx, jfloat x,
        jfloat y, jfloat w, jfloat h, jfloat r, jfloat f, jobject icol, jobject ocol) {
    NVGpaint paint = nvgBoxGradient(reinterpret_cast<NVGcontext*>(ctx), x, y, w, h, r, f, javaToNVGColor(env, icol),
            javaToNVGColor(env, ocol));
    return nvgPaintToJava(env, paint);
}

jobject JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgRadialGradient(JNIEnv* env, jobject self, jlong ctx, jfloat cx,
        jfloat cy, jfloat inr, jfloat outr, jobject icol, jobject ocol) {
    NVGpaint paint = nvgRadialGradient(reinterpret_cast<NVGcontext*>(ctx), cx, cy, inr, outr, javaToNVGColor(env, icol),
            javaToNVGColor(env, ocol));
    return nvgPaintToJava(env, paint);
}

jobject JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgImagePattern(JNIEnv* env, jobject self, jlong ctx, jfloat ox,
        jfloat oy, jfloat ex, jfloat ey, jfloat angle, jint image, jint repeat) {
    NVGpaint paint = nvgImagePattern(reinterpret_cast<NVGcontext*>(ctx), ox, oy, ex, ey, angle, image, repeat);
    return nvgPaintToJava(env, paint);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgScissor(JNIEnv* env, jobject self, jlong ctx, jfloat x, jfloat y,
        jfloat w, jfloat h) {
    nvgScissor(reinterpret_cast<NVGcontext*>(ctx), x, y, w, h);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgResetScissor(JNIEnv* env, jobject self, jlong ctx) {
    nvgResetScissor(reinterpret_cast<NVGcontext*>(ctx));
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgBeginPath(JNIEnv* env, jobject self, jlong ctx) {
    nvgBeginPath(reinterpret_cast<NVGcontext*>(ctx));
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgMoveTo(JNIEnv* env, jobject self, jlong ctx, jfloat x, jfloat y) {
    nvgMoveTo(reinterpret_cast<NVGcontext*>(ctx), x, y);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgLineTo(JNIEnv* env, jobject self, jlong ctx, jfloat x, jfloat y) {
    nvgLineTo(reinterpret_cast<NVGcontext*>(ctx), x, y);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgBezierTo(JNIEnv* env, jobject self, jlong ctx, jfloat c1x, jfloat c1y,
        jfloat c2x, jfloat c2y, jfloat x, jfloat y) {
    nvgBezierTo(reinterpret_cast<NVGcontext*>(ctx), c1x, c1y, c2x, c2y, x, y);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgArcTo(JNIEnv* env, jobject self, jlong ctx, jfloat x1, jfloat y1,
        jfloat x2, jfloat y2, jfloat radius) {
    nvgArcTo(reinterpret_cast<NVGcontext*>(ctx), x1, y1, x2, y2, radius);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgClosePath(JNIEnv* env, jobject self, jlong ctx) {
    nvgClosePath(reinterpret_cast<NVGcontext*>(ctx));
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgPathWinding(JNIEnv* env, jobject self, jlong ctx, jint dir) {
    nvgPathWinding(reinterpret_cast<NVGcontext*>(ctx), dir);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgArc(JNIEnv* env, jobject self, jlong ctx, jfloat cx, jfloat cy,
        jfloat r, jfloat a0, jfloat a1, jint dir) {
    nvgArc(reinterpret_cast<NVGcontext*>(ctx), cx, cy, r, a0, a1, dir);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgRect(JNIEnv* env, jobject self, jlong ctx, jfloat x, jfloat y,
        jfloat w, jfloat h) {
    nvgRect(reinterpret_cast<NVGcontext*>(ctx), x, y, w, h);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgRoundedRect(JNIEnv* env, jobject self, jlong ctx, jfloat x, jfloat y,
        jfloat w, jfloat h, jfloat r) {
    nvgRoundedRect(reinterpret_cast<NVGcontext*>(ctx), x, y, w, h, r);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgEllipse(JNIEnv* env, jobject self, jlong ctx, jfloat cx, jfloat cy,
        jfloat rx, jfloat ry) {
    nvgEllipse(reinterpret_cast<NVGcontext*>(ctx), cx, cy, rx, ry);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgCircle(JNIEnv* env, jobject self, jlong ctx, jfloat cx, jfloat cy,
        jfloat r) {
    nvgCircle(reinterpret_cast<NVGcontext*>(ctx), cx, cy, r);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgFill(JNIEnv* env, jobject self, jlong ctx) {
    nvgFill(reinterpret_cast<NVGcontext*>(ctx));
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgStroke(JNIEnv* env, jobject self, jlong ctx) {
    nvgStroke(reinterpret_cast<NVGcontext*>(ctx));
}

jint JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgCreateFont(JNIEnv* env, jobject self, jlong ctx, jstring name,
        jstring fileName) {
    const char* utfname = env->GetStringUTFChars(name, NULL);
    const char* utffileName = env->GetStringUTFChars(fileName, NULL);
    int handle = nvgCreateFont(reinterpret_cast<NVGcontext*>(ctx), utfname, utffileName);
    env->ReleaseStringUTFChars(name, utfname);
    env->ReleaseStringUTFChars(fileName, utffileName);
    return handle;
}

jint JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgCreateFontMem(JNIEnv* env, jobject self, jlong ctx, jstring name,
        jobject buffer, jint ndata, jint freeData) {
    const char* utfname = env->GetStringUTFChars(name, NULL);
    unsigned char* bufferData = reinterpret_cast<unsigned char*>(env->GetDirectBufferAddress(buffer));
    int handle = nvgCreateFontMem(reinterpret_cast<NVGcontext*>(ctx), utfname, bufferData, ndata, freeData);
    env->ReleaseStringUTFChars(name, utfname);
    return handle;
}

jint JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgFindFont(JNIEnv* env, jobject self, jlong ctx, jstring name) {
    const char* utfname = env->GetStringUTFChars(name, NULL);
    int handle = nvgFindFont(reinterpret_cast<NVGcontext*>(ctx), utfname);
    env->ReleaseStringUTFChars(name, utfname);
    return handle;
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgFontSize(JNIEnv* env, jobject self, jlong ctx, jfloat size) {
    nvgFontSize(reinterpret_cast<NVGcontext*>(ctx), size);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgFontBlur(JNIEnv* env, jobject self, jlong ctx, jfloat blur) {
    nvgFontBlur(reinterpret_cast<NVGcontext*>(ctx), blur);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgTextLetterSpacing(JNIEnv* env, jobject self, jlong ctx,
        jfloat spacing) {
    nvgTextLetterSpacing(reinterpret_cast<NVGcontext*>(ctx), spacing);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgTextLineHeight(JNIEnv* env, jobject self, jlong ctx,
        jfloat lineHeight) {
    nvgTextLineHeight(reinterpret_cast<NVGcontext*>(ctx), lineHeight);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgTextAlign(JNIEnv* env, jobject self, jlong ctx, jint align) {
    nvgTextAlign(reinterpret_cast<NVGcontext*>(ctx), align);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgFontFaceId(JNIEnv* env, jobject self, jlong ctx, jint font) {
    nvgFontFaceId(reinterpret_cast<NVGcontext*>(ctx), font);
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgFontFace(JNIEnv* env, jobject self, jlong ctx, jstring font) {
    const char* utfFont = env->GetStringUTFChars(font, NULL);
    nvgFontFace(reinterpret_cast<NVGcontext*>(ctx), utfFont);
    env->ReleaseStringUTFChars(font, utfFont);
}

jfloat JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgText(JNIEnv* env, jobject self, jlong ctx, jfloat x, jfloat y,
        jstring string, jstring pointer) {
    const char* utfString = env->GetStringUTFChars(string, NULL);
    jfloat size = nvgText(reinterpret_cast<NVGcontext*>(ctx), x, y, utfString, NULL);
    env->ReleaseStringUTFChars(string, utfString);
    return size;
}

/**
 * Unimplemented.
 */

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgImageSize(JNIEnv* env, jobject self, jlong ctx, jint, jint, jint) {

}

jobject JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgRGBAu(JNIEnv* env, jobject self, jint rgba) {
    //NVGcolor color = nvgRGBAu(rgba);
    //jobject javaColor = nvgColorToJava(env, color);
    //return javaColor;
    return 0;
}

jint JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgCreateImageFromHandle(JNIEnv* env, jobject self, jlong ctx,
        jint textureHandle) {
    return 1;
}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgTextBox(JNIEnv* env, jobject self, jlong ctx, jfloat x, jfloat y,
        jfloat breakRowWidth, jstring string, jstring end) {

}

jfloat JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgTextBounds(JNIEnv* env, jobject self, jlong ctx, jfloat x, jfloat y,
        jstring string, jstring end, jfloat boundsOUTPUT) {

}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgTextBoxBounds(JNIEnv* env, jobject self, jlong ctx, jfloat x,
        jfloat y, jfloat breakRowWidth, jstring string, jstring end, jfloat boundsOUTPUT) {

}

jint JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgTextGlyphPositions(JNIEnv* env, jobject self, jlong ctx, jfloat x,
        jfloat y, jstring string, jlong pointer, jlong glyphPosition, jint maxPositions) {

}

void JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgTextMetrics(JNIEnv* env, jobject self, jlong ctx, jfloat, jfloat,
        jfloat) {

}

jint JNICALL Java_twilight_bgfx_nanovg_NanoVG_nnvgTextBreakLines(JNIEnv* env, jobject self, jlong ctx, jstring string,
        jlong endPointer, jfloat breakRowWidth, jlong pointer, jint maxRows) {

}
