#include <jni.h>
#include <stdlib.h>
#include <string.h>
#include "com_yitiaojiayu_easylist_EasyListNative.h"

JNIEXPORT jbyteArray JNICALL Java_com_yitiaojiayu_easylist_EasyListNative_get(JNIEnv *env, jclass clazz, jobject byteBuffer, jint offset, jint length)
{
    jbyte *bufferStart = (jbyte *)(*env)->GetDirectBufferAddress(env, byteBuffer);
    jbyteArray resultArray = (*env)->NewByteArray(env, length);
    jbyte *sourcePtr = bufferStart + offset;
    (*env)->SetByteArrayRegion(env, resultArray, 0, length, sourcePtr);
    return resultArray;
}

JNIEXPORT jboolean JNICALL Java_com_yitiaojiayu_easylist_EasyListNative_delete(JNIEnv *env, jclass clazz, jobject byteBuffer, jint index, jint size, jint useStart, jint useEnd)
{
    jbyte *bufferStart = (jbyte *)(*env)->GetDirectBufferAddress(env, byteBuffer);
    jint beforeSize = index - useStart;
    jint afterSize = useEnd - (index + size);
    if (beforeSize <= afterSize)
    {
        jbyte *dest = bufferStart + useStart + size;
        jbyte *src = bufferStart + useStart;
        if (beforeSize > 0)
        {
            memmove(dest, src, beforeSize);
        }
        return JNI_FALSE;
    }
    jbyte *dest = bufferStart + index;
    jbyte *src = bufferStart + index + size;
    if (afterSize > 0)
    {
        memmove(dest, src, afterSize);
    }
    return JNI_TRUE;
}