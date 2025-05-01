#include <jni.h>
#include "com_yitiaojiayu_easylist_EasyListNative.h"

#include <stdlib.h>
#include <string.h>

static jclass ArrayIndexOutOfBoundsException;
typedef struct
{
    int size;
    char *data;
} element_data;
typedef struct
{
    int capacity;
    int count;
    int begin;
    int end;
    element_data **data;
} clazz_data;

static int clazz_max = 16;
static int clazz_id = 0;
static clazz_data **data;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved)
{
    JNIEnv *env;
    (*vm)->GetEnv(vm, (void **)&env, JNI_VERSION_1_8);
    jclass tempException = (*env)->FindClass(env, "java/lang/ArrayIndexOutOfBoundsException");
    ArrayIndexOutOfBoundsException = (*env)->NewGlobalRef(env, tempException);
    data = malloc(clazz_max * sizeof(clazz_data *));
    return JNI_VERSION_1_8;
}

JNIEXPORT jint JNICALL Java_com_yitiaojiayu_easylist_EasyListNative_new_1object(JNIEnv *env, jclass clazz)
{
    if (clazz_id >= clazz_max)
    {
        clazz_max *= 2;
        clazz_data **temp = realloc(data, clazz_max * sizeof(clazz_data *));
        data = temp;
    }
    clazz_data *new_obj = malloc(sizeof(clazz_data));
    new_obj->capacity = 16;
    new_obj->count = 0;
    new_obj->begin = 4;
    new_obj->end = 4;
    new_obj->data = malloc(16 * sizeof(element_data *));
    data[clazz_id] = new_obj;
    return clazz_id++;
}

JNIEXPORT jint JNICALL Java_com_yitiaojiayu_easylist_EasyListNative_size(JNIEnv *env, jclass clazz, jint id)
{
    return data[id]->count;
}

JNIEXPORT jboolean JNICALL Java_com_yitiaojiayu_easylist_EasyListNative_is_1empty(JNIEnv *env, jclass clazz, jint id)
{
    return data[id]->count == 0;
}

JNIEXPORT jboolean JNICALL Java_com_yitiaojiayu_easylist_EasyListNative_add(JNIEnv *env, jclass clazz, jint id, jint index, jbyteArray obj)
{
    if (index < 0 || index > data[id]->count)
    {
        char msg[64];
        snprintf(msg, sizeof(msg), "EasyList -> add -> index: %d, range: 0 ~ %d", index, data[id]->count);
        (*env)->ThrowNew(env, ArrayIndexOutOfBoundsException, msg);
        return JNI_FALSE;
    }
    jbyte *bytes = (*env)->GetByteArrayElements(env, obj, NULL);
    int len = (*env)->GetArrayLength(env, obj);
    if (index > data[id]->count - index - 1)
    {
        if (data[id]->end >= data[id]->capacity)
        {
            data[id]->capacity *= 2;
            element_data **temp = realloc(data[id]->data, data[id]->capacity * sizeof(element_data *));
            data[id]->data = temp;
        }
        if (index != data[id]->count)
        {
            index += data[id]->begin;
            memmove(&(data[id]->data[index + 1]), &(data[id]->data[index]), (data[id]->end - index) * sizeof(element_data *));
        }
        else
        {
            index += data[id]->begin;
        }
        data[id]->end++;
        data[id]->data[index] = malloc(sizeof(element_data));
        data[id]->data[index]->data = malloc(len);
    }
    else
    {
        if (data[id]->begin <= 0)
        {
            data[id]->capacity *= 2;
            element_data **temp = realloc(data[id]->data, data[id]->capacity * sizeof(element_data *));
            memcpy(&(temp[data[id]->capacity / 2]), &(temp[0]), data[id]->end * sizeof(element_data *));
            data[id]->data = temp;
            data[id]->begin += data[id]->capacity / 2;
            data[id]->end += data[id]->capacity / 2;
        }
        if (index != 0)
        {
            index += data[id]->begin;
            memmove(&(data[id]->data[data[id]->begin - 1]), &(data[id]->data[data[id]->begin]), (index - data[id]->begin) * sizeof(element_data *));
        }
        else
        {
            index += data[id]->begin;
        }
        data[id]->begin--;
        data[id]->data[--index] = malloc(sizeof(element_data));
        data[id]->data[index]->data = malloc(len);
    }
    data[id]->data[index]->size = len;
    memcpy(data[id]->data[index]->data, bytes, len);
    (*env)->ReleaseByteArrayElements(env, obj, bytes, JNI_ABORT);
    data[id]->count++;
    return JNI_TRUE;
}

JNIEXPORT jbyteArray JNICALL Java_com_yitiaojiayu_easylist_EasyListNative_get(JNIEnv *env, jclass clazz, jint id, jint index)
{
    if (index < 0 || index >= data[id]->count)
    {
        char msg[64];
        snprintf(msg, sizeof(msg), "EasyList -> get -> index: %d, range: 0 ~ %d", index, data[id]->count - 1);
        (*env)->ThrowNew(env, ArrayIndexOutOfBoundsException, msg);
        return NULL;
    }
    index += data[id]->begin;
    jbyteArray javaByteArray = (*env)->NewByteArray(env, data[id]->data[index]->size);
    (*env)->SetByteArrayRegion(env, javaByteArray, 0, data[id]->data[index]->size, data[id]->data[index]->data);
    return javaByteArray;
}

JNIEXPORT void JNICALL Java_com_yitiaojiayu_easylist_EasyListNative_remove(JNIEnv *env, jclass clazz, jint id, jint index)
{
    if (index < 0 || index >= data[id]->count)
    {
        char msg[64];
        snprintf(msg, sizeof(msg), "EasyList -> remove -> index: %d, range: 0 ~ %d", index, data[id]->count - 1);
        (*env)->ThrowNew(env, ArrayIndexOutOfBoundsException, msg);
        return;
    }
    free(data[id]->data[index + data[id]->begin]->data);
    free(data[id]->data[index + data[id]->begin]);
    if (index > data[id]->count - index - 1)
    {
        if (index != data[id]->count - 1)
        {
            index += data[id]->begin;
            memmove(&(data[id]->data[index]), &(data[id]->data[index + 1]), (data[id]->end - index - 1) * sizeof(element_data *));
        }
        data[id]->end--;
    }
    else
    {
        if (index != 0)
        {
            index += data[id]->begin;
            memmove(&(data[id]->data[data[id]->begin + 1]), &(data[id]->data[data[id]->begin]), (index - data[id]->begin) * sizeof(element_data *));
        }
        data[id]->begin++;
    }
    data[id]->count--;
}

JNIEXPORT void JNICALL Java_com_yitiaojiayu_easylist_EasyListNative_set(JNIEnv *env, jclass clazz, jint id, jint index, jbyteArray obj)
{
    if (index < 0 || index >= data[id]->count)
    {
        char msg[64];
        snprintf(msg, sizeof(msg), "EasyList -> set -> index: %d, range: 0 ~ %d", index, data[id]->count - 1);
        (*env)->ThrowNew(env, ArrayIndexOutOfBoundsException, msg);
        return;
    }
    jbyte *bytes = (*env)->GetByteArrayElements(env, obj, NULL);
    int len = (*env)->GetArrayLength(env, obj);
    index += data[id]->begin;
    free(data[id]->data[index]->data);
    data[id]->data[index]->data = malloc(len);
    data[id]->data[index]->size = len;
    memcpy(data[id]->data[index]->data, bytes, len);
    (*env)->ReleaseByteArrayElements(env, obj, bytes, JNI_ABORT);
}