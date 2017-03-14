//
// Created by MTK00544 on 2017/3/14.
//
#include <stdio.h>
#include "detect_roi_jni.h"

#include <android/log.h>

#define LOG_TAG "camerademo"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

JNIEXPORT jlong JNICALL Java_io_github_qi1002_icamera_DetectionROI_nativeCreateObject
        (JNIEnv * jenv, jclass, jint width, jint height) {
    LOGD("Java_io_github_qi1002_icamera_DetectionROI_nativeCreateObject enter (%d,%d)", width, height);
    return 1234;
}

JNIEXPORT void JNICALL Java_io_github_qi1002_icamera_DetectionROI_nativeDestroyObject
        (JNIEnv * jenv, jclass, jlong thiz) {
    LOGD("Java_io_github_qi1002_icamera_DetectionROIr_nativeDestroyObject enter");
}

JNIEXPORT void JNICALL Java_io_github_qi1002_icamera_DetectionROI_nativeDetectROI
        (JNIEnv * jenv, jclass, jlong thiz, jbyteArray yPlane, jbyteArray uPlane, jbyteArray vPlane) {

    LOGD("Java_io_github_qi1002_icamera_DetectionROI_nativeDetectROI enter");
}
