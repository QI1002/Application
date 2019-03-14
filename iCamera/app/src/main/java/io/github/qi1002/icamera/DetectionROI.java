package io.github.qi1002.icamera;

public class DetectionROI {

    public DetectionROI(int width, int height, String path) {
        mNativeObj = nativeCreateObject(width, height, path);
    }

    public int[] detectROI(byte[] yPlane, byte[] uPlane, byte[] vPlane) {
        return nativeDetectROI(mNativeObj, yPlane, uPlane, vPlane);
    }

    public void release() {
        nativeDestroyObject(mNativeObj);
        mNativeObj = 0;
    }

    static {
        System.loadLibrary("detection_roi");
    }

    private long mNativeObj = 0;

    private static native long nativeCreateObject(int width, int height, String path);
    private static native void nativeDestroyObject(long thiz);
    private static native int[] nativeDetectROI(long thiz, byte[] yPlane, byte[] uPlane, byte[] vPlane);
}