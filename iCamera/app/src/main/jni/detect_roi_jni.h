#include <jni.h>
#ifndef ICAMERA_DETECT_ROI_H
#define ICAMERA_DETECT_ROI_H
#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     org_opencv_samples_fd_DetectionBasedTracker
 * Method:    nativeCreateObject
 * Signature: (Ljava/lang/String;F)J
 */
JNIEXPORT jlong JNICALL Java_io_github_qi1002_icamera_DetectionROI_nativeCreateObject
        (JNIEnv *, jclass, jint, jint, jstring path);

/*
 * Class:     org_opencv_samples_fd_DetectionBasedTracker
 * Method:    nativeDestroyObject
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_io_github_qi1002_icamera_DetectionROI_nativeDestroyObject
(JNIEnv *, jclass, jlong);

/*
 * Class:     org_opencv_samples_fd_DetectionBasedTracker
 * Method:    nativeSetFaceSize
 * Signature: (JI)V
 */
JNIEXPORT jintArray JNICALL Java_io_github_qi1002_icamera_DetectionROI_nativeDetectROI
(JNIEnv *, jclass, jlong, jbyteArray, jbyteArray, jbyteArray);

#ifdef __cplusplus
}
#endif

#endif //ICAMERA_DETECT_ROI_H


