#include <jni.h>
#include <stdlib.h>
#include <string.h>
#include "com_yitiaojiayu_easylist_EasyListNative.h"

static void throwJavaException(JNIEnv *env, const char *className, const char *message) {
    jclass exceptionClass = (*env)->FindClass(env, className);
    if (exceptionClass != NULL) {
        (*env)->ThrowNew(env, exceptionClass, message);
        (*env)->DeleteLocalRef(env, exceptionClass);
    }
}

/*
 * Class:     com_yitiaojiayu_easylist_EasyListNative
 * Method:    get
 * Signature: (Ljava/nio/ByteBuffer;II)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_yitiaojiayu_easylist_EasyListNative_get
  (JNIEnv *env, jobject thiz, jobject byteBuffer, jint offset, jint length) {

    if (byteBuffer == NULL) {
        throwJavaException(env, "java/lang/NullPointerException", "Input ByteBuffer is null");
        return NULL;
    }
    if (offset < 0) {
         throwJavaException(env, "java/lang/IllegalArgumentException", "Offset cannot be negative");
         return NULL;
    }
     if (length < 0) {
         throwJavaException(env, "java/lang/IllegalArgumentException", "Length cannot be negative");
         return NULL;
    }
    if (length == 0) {
        jbyteArray emptyArray = (*env)->NewByteArray(env, 0);
        if (emptyArray == NULL) {
             throwJavaException(env, "java/lang/OutOfMemoryError", "Failed to allocate empty byte array");
        }
        return emptyArray;
    }

    jbyte *bufferStart = (jbyte *)(*env)->GetDirectBufferAddress(env, byteBuffer);

    if (bufferStart == NULL) {
        throwJavaException(env, "java/lang/UnsupportedOperationException",
                           "ByteBuffer is not direct; only direct ByteBuffers are supported");
        return NULL;
    }

    jclass byteBufferClass = (*env)->GetObjectClass(env, byteBuffer);
    if (byteBufferClass == NULL) {
        return NULL;
    }
    jmethodID capacityMethodId = (*env)->GetMethodID(env, byteBufferClass, "capacity", "()I");
    (*env)->DeleteLocalRef(env, byteBufferClass);
    if (capacityMethodId == NULL) {
        throwJavaException(env, "java/lang/NoSuchMethodError", "Cannot find ByteBuffer.capacity() method");
        return NULL;
    }

    jint capacity = (*env)->CallIntMethod(env, byteBuffer, capacityMethodId);
    if ((*env)->ExceptionCheck(env)) {
        return NULL;
    }

    if ((jlong)offset + (jlong)length > (jlong)capacity) {
        char errorMsg[100];
        snprintf(errorMsg, sizeof(errorMsg), "Requested region (offset %d, length %d) exceeds buffer capacity (%d)",
                 (int)offset, (int)length, (int)capacity);
        throwJavaException(env, "java/lang/IndexOutOfBoundsException", errorMsg);
        return NULL;
    }

    jbyteArray resultArray = (*env)->NewByteArray(env, length);
    if (resultArray == NULL) {
        throwJavaException(env, "java/lang/OutOfMemoryError", "Failed to allocate result byte array");
        return NULL;
    }

    jbyte *sourcePtr = bufferStart + offset;

    (*env)->SetByteArrayRegion(env, resultArray, 0, length, sourcePtr);

    if ((*env)->ExceptionCheck(env)) {
        return NULL;
    }

    return resultArray;
}