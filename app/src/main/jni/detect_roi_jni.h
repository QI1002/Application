#include <jni.h>
#ifndef ICAMERA_DETECT_ROI_H
#define ICAMERA_DETECT_ROI_H
#ifdef __cplusplus
extern "C" {
#endif

/*
 * Method:    nativeCreateObject
 */
JNIEXPORT jlong JNICALL Java_io_github_qi1002_icamera_DetectionROI_nativeCreateObject
        (JNIEnv *, jclass, jint, jint);

/*
 * Method:    nativeDestroyObject
  */
JNIEXPORT void JNICALL Java_io_github_qi1002_icamera_DetectionROI_nativeDestroyObject
(JNIEnv *, jclass, jlong);

/*
 * Method:    nativeDetectROI
 */
JNIEXPORT void JNICALL Java_io_github_qi1002_icamera_DetectionROI_nativeDetectROI
(JNIEnv *, jclass, jlong, jbyteArray, jbyteArray, jbyteArray);

#ifdef __cplusplus
}
#endif

#endif //ICAMERA_DETECT_ROI_H


