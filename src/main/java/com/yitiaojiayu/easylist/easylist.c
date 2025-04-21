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

JNIEXPORT jboolean JNICALL Java_com_yitiaojiayu_easylist_EasyListNative_delete
  (JNIEnv *env, jclass clazz, jobject byteBuffer, jint index, jint size, jint useStart, jint useEnd)
{
    // 获取指向直接缓冲区的指针
    jbyte *bufferStart = (jbyte *)(*env)->GetDirectBufferAddress(env, byteBuffer);
    // (如果严格遵循不检查，可以去掉这个null判断。但实际项目中建议保留)
    // if (bufferStart == NULL) {
    //     // 处理错误，例如抛出异常或返回一个特定的值
    //     // 这里我们假设 ByteBuffer 总是有效的 DirectByteBuffer
    //     return JNI_FALSE; // 或者根据错误处理策略返回
    // }

    // 计算要删除数据块之前和之后的数据大小
    jint beforeSize = index - useStart;       // 删除点之前的数据量 (左边)
    jint afterSize = useEnd - (index + size); // 删除点之后的数据量 (右边)

    // 判断哪部分数据更小，移动较小的那部分
    if (beforeSize <= afterSize) {
        // 前面的数据 (左边) 更少或相等，将 [useStart, index) 的数据向后移动 'size' 位 (从左移向右)
        jbyte *dest = bufferStart + useStart + size;
        jbyte *src = bufferStart + useStart;
        if (beforeSize > 0) {
           memmove(dest, src, beforeSize);
        }
        // 返回 false，表示数据从左边移向了右边
        return JNI_FALSE;
    } else {
        // 后面的数据 (右边) 更少，将 [index + size, useEnd) 的数据向前移动 'size' 位 (从右移向左)
        jbyte *dest = bufferStart + index;
        jbyte *src = bufferStart + index + size;
        if (afterSize > 0) {
           memmove(dest, src, afterSize);
        }
        // 返回 true，表示数据从右边移向了左边
        return JNI_TRUE;
    }
    // 注意：上面的 if/else 结构保证了总会有返回值，这里理论上不可达
}