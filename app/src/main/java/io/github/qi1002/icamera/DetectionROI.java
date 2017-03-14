package io.github.qi1002.icamera;

public class DetectionROI {

    public DetectionROI(int width, int height) {
        mNativeObj = nativeCreateObject(width, height);
    }

    public void detectROI(byte[] yPlane, byte[] uPlane, byte[] vPlane) {
        nativeDetectROI(mNativeObj, yPlane, uPlane, vPlane);
    }

    public void release() {
        nativeDestroyObject(mNativeObj);
        mNativeObj = 0;
    }

    static {
        System.loadLibrary("detection_roi");
    }

    private long mNativeObj = 0;

    private static native long nativeCreateObject(int width, int height);
    private static native void nativeDestroyObject(long thiz);
    private static native void nativeDetectROI(long thiz, byte[] yPlane, byte[] uPlane, byte[] vPlane);
}
