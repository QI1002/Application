//
// Created by MTK00544 on 2017/3/14.
//
#include "detect_roi_jni.h"

#include <stdio.h>
#include <string.h>
#include <android/log.h>

#define LOG_TAG "camerademo"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

class ROIDetection {

private:
    int mWidth;
    int mHeight;
    char mPath[128];

    char* getTestFile(int count)
    {
        char temp[128];
        char savePath[128];
        strcpy(savePath, mPath);
        sprintf(temp, "/test%d.yuv420", count);
        strcat(savePath, temp);
        return strdup(savePath);
    }

public:

    ROIDetection(int width, int height, const char* path)
    {
        mWidth = width;
        mHeight = height;
        strcpy(mPath, path);
    }

    int getWidth() { return mWidth; }
    int getHeight() { return mHeight; }
    char* getPath() { return mPath; }

    void saveFile(int count, unsigned char* yData, unsigned char* uData, unsigned char* vData)
    {
        char* savePath = getTestFile(count);
        FILE* wf = fopen(savePath, "w");
        free(savePath);
        if (wf != NULL)
        {
            fwrite(yData, 1, mWidth * mHeight, wf);
            fwrite(uData, 1, mWidth * mHeight/4, wf);
            fwrite(vData, 1, mWidth * mHeight/4, wf);
            fclose(wf);
        }else
        {
            LOGD("Java_io_github_qi1002_icamera_DetectionROI_nativeDetectROI enter fail %s", savePath);
        }
    }
};

JNIEXPORT jlong JNICALL Java_io_github_qi1002_icamera_DetectionROI_nativeCreateObject
        (JNIEnv * jenv, jclass, jint width, jint height, jstring path) {
    const char* jpathstr = jenv->GetStringUTFChars(path, NULL);
    LOGD("Java_io_github_qi1002_icamera_DetectionROI_nativeCreateObject enter (%d,%d)", width, height);
    jlong result = (jlong)(new ROIDetection(width, height, jpathstr));
    return result;
}

JNIEXPORT void JNICALL Java_io_github_qi1002_icamera_DetectionROI_nativeDestroyObject
        (JNIEnv * jenv, jclass, jlong thiz) {
    LOGD("Java_io_github_qi1002_icamera_DetectionROIr_nativeDestroyObject enter");
    if (thiz != 0)
    {
        delete (ROIDetection*)thiz;
    }
}

JNIEXPORT jintArray JNICALL Java_io_github_qi1002_icamera_DetectionROI_nativeDetectROI
        (JNIEnv * jenv, jclass, jlong thiz, jbyteArray yPlane, jbyteArray uPlane, jbyteArray vPlane) {

    static int detectCount = 0;
    unsigned char* yData = (unsigned char*)jenv->GetByteArrayElements(yPlane, (jboolean*)0);
    unsigned char* uData = (unsigned char*)jenv->GetByteArrayElements(uPlane, (jboolean*)0);
    unsigned char* vData = (unsigned char*)jenv->GetByteArrayElements(vPlane, (jboolean*)0);

    jintArray rectROI = jenv->NewIntArray(4);
    jint* roiData = jenv->GetIntArrayElements(rectROI, (jboolean*)0);
    roiData[0] = 100;
    roiData[1] = 200;
    roiData[2] = 300;
    roiData[3] = 400;
    jenv->SetIntArrayRegion(rectROI, 0, 4, roiData);

    detectCount++;
    LOGD("Java_io_github_qi1002_icamera_DetectionROI_nativeDetectROI enter %d", detectCount);

    ROIDetection* detection = (ROIDetection*)thiz;

    if (detectCount == 33)
        detection->saveFile(detectCount, yData, uData, vData);

    jenv->ReleaseByteArrayElements(yPlane, (jbyte*)yData, JNI_ABORT);
    jenv->ReleaseByteArrayElements(uPlane, (jbyte*)uData, JNI_ABORT);
    jenv->ReleaseByteArrayElements(vPlane, (jbyte*)vData, JNI_ABORT);
    jenv->ReleaseIntArrayElements(rectROI, roiData, JNI_ABORT);

    return rectROI;
}
