#include <jni.h>
#include <stdlib.h>
#include <string.h>
#include "com_yitiaojiayu_easylist_EasyListNative.h"

/*
 * Class:      com_yitiaojiayu_easylist_EasyListNative
 * Method:     get
 * Signature: (Ljava/nio/ByteBuffer;II)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_yitiaojiayu_easylist_EasyListNative_get(JNIEnv *env, jclass clazz, jobject byteBuffer, jint offset, jint length)
{
    jbyte *bufferStart = (jbyte *)(*env)->GetDirectBufferAddress(env, byteBuffer);
    jbyteArray resultArray = (*env)->NewByteArray(env, length);
    jbyte *sourcePtr = bufferStart + offset;
    (*env)->SetByteArrayRegion(env, resultArray, 0, length, sourcePtr);
    return resultArray;
}