#include <jni.h>
#include "com_yitiaojiayu_easylist_EasyListNative.h"

#include <stdlib.h>

typedef struct
{
    int capacity;
    int count;
    char **data;
} clazz_data;

static int clazz_max = 16;
static int clazz_id = 0;
static char **data;

JNIEXPORT void JNICALL Java_com_yitiaojiayu_easylist_EasyListNative_init(JNIEnv *env, jclass clazz)
{
    data = malloc(clazz_max * sizeof(clazz_data *));
}

JNIEXPORT jint JNICALL Java_com_yitiaojiayu_easylist_EasyListNative_new_1object(JNIEnv *env, jclass clazz)
{
    clazz_data *new_obj = malloc(sizeof(clazz_data));
    new_obj->capacity = 16;
    new_obj->count = 0;
    new_obj->data = malloc(16 * sizeof(char *));
    data[clazz_id] = new_obj;
    return clazz_id++;
}

JNIEXPORT jint JNICALL Java_com_yitiaojiayu_easylist_EasyListNative_size(JNIEnv *env, jclass clazz, jint id)
{
    return ((clazz_data *)data[id])->count;
}

JNIEXPORT jboolean JNICALL Java_com_yitiaojiayu_easylist_EasyListNative_is_1empty(JNIEnv *env, jclass clazz, jint id)
{
    return ((clazz_data *)data[id])->count == 0;
}