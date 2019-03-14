package io.github.qi1002.icamera;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Camera2BasicFragment extends Fragment implements View.OnClickListener {

    /**
     * Conversion from screen rotation to JPEG orientation.
     */
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /**
     * Tag for the {@link Log}.
     */
    private static final String TAG = "Camera2BasicFragment";

    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }

    };

    /**
     * ID of the current {@link CameraDevice}.
     */
    private String mCameraId;

    /**
     * An {@link AutoFitTextureView} for camera preview.
     */
    private AutoFitTextureView mTextureView;
    private boolean detectSwitch = true;
    private int frameCount = 0;
    private int captureCount = 0;
    private long frameTime = 0;
    private long captureTime = 0;
    public TextView textinfo1;
    public TextView textinfo2;
    public TextView textinfo3;
    public TextView textinfo4;
    public TextView textinfo5;
    public TextView textinfo6;
    public TextView textinfo7;
    public TextView textinfo8;
    public TextView textinfo9;
    public TextView textinfo10;
    public TextView textinfo11;
    public VideoView videoView;

    /**
     * A {@link CameraCaptureSession } for camera preview.
     */

    private CameraCaptureSession mCaptureSession;
    /**
     * A reference to the opened {@link CameraDevice}.
     */

    private CameraDevice mCameraDevice;
    /**
     * The {@link android.util.Size} of camera preview.
     */

    private Size mSurfaceSize;
    /**
     * The {@link android.util.Size} of camera preview.
     */

    private Size mPreviewSize;
    /**
     * The {@link android.util.Size} of camera capture.
     */

    private Size mCaptureSize;

    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
     */
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            Activity activity = getActivity();
            if (null != activity) {
                activity.finish();
            }
        }
    };

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private HandlerThread mBackgroundThread;

    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler mBackgroundHandler;

    /**
     * An {@link ImageReader} that handles still image capture.
     */
    private ImageReader mImageReader;

    /**
     * This is the output directory for our capture picture.
     */
    private File mFile;

    private final static int MESSAGE_SHOW_TOAST = 0;
    private final static int MESSAGE_SHOW_FRAME_COUNT = 1;
    private final static int MESSAGE_SHOW_CAPTURE_COUNT = 2;
    private final static int MESSAGE_SHOW_ROI = 3;

    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            captureCount++;
            showCaptureCount(captureCount);
            Image img = reader.acquireNextImage();
            //Log.d("camerademo", "capture c = " + captureCount + " w = " + img.getWidth() + " h = " + img.getHeight());
            if (detectSwitch)
                mBackgroundHandler.post(new ImageSaver(img, mFile, mMessageHandler, captureCount));
            else
                img.close();
        }

    };

    /**
     * {@link CaptureRequest.Builder} for the camera preview
     */
    private CaptureRequest.Builder mPreviewRequestBuilder;

    /**
     * {@link CaptureRequest} generated by {@link #mPreviewRequestBuilder}
     */
    private CaptureRequest mPreviewRequest;

    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    /**
     * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
     */
    private CameraCaptureSession.CaptureCallback mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            frameCount++;
            showFrameCount(frameCount);
            captureStillPicture();
        }

        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request,
                                        CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
                                       TotalCaptureResult result) {
            process(result);
        }

    };

    /**
     * A {@link Handler} for showing {@link Toast}s.
     */
    private Handler mMessageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Activity activity = getActivity();
            if (activity != null) {
                switch (msg.arg1)
                {
                    case MESSAGE_SHOW_TOAST:
                        if (msg.obj instanceof String)
                            Toast.makeText(activity, (String) msg.obj, Toast.LENGTH_SHORT).show();
                        break;
                    case MESSAGE_SHOW_FRAME_COUNT:
                        if (msg.obj instanceof Float) {
                            textinfo7.setText(((Integer) msg.arg2).toString());
                            textinfo8.setText(((Float) msg.obj).toString());
                        }
                        break;
                    case MESSAGE_SHOW_CAPTURE_COUNT:
                        if (msg.obj instanceof Float) {
                            textinfo9.setText(((Integer) msg.arg2).toString());
                            textinfo10.setText(((Float) msg.obj).toString());
                        }
                        break;
                    case MESSAGE_SHOW_ROI:
                        if (msg.obj instanceof int[])
                        {
                            int fdiff, cdiff, count = msg.arg2;
                            int[] roi = (int[])msg.obj;
                            float xRatio = (float)mSurfaceSize.getWidth()/mCaptureSize.getHeight();
                            float yRatio = (float)mSurfaceSize.getHeight()/mCaptureSize.getWidth();
                            if (roi[0] != -1 && roi[1] != -1 && (xRatio == yRatio)) {
                                float x = (float)mSurfaceSize.getWidth() - roi[3] * xRatio;
                                float y = roi[0] * yRatio;
                                videoView.setX((float) x);
                                videoView.setY((float) y);
                                textinfo5.setText(((Float) x).toString());
                                textinfo6.setText(((Float) y).toString());
                                fdiff = frameCount - count;
                                cdiff = captureCount - count;
                                textinfo11.setText("" + fdiff + "," + cdiff);
                            }
                        }
                        break;
                }
            }
        }
    };

    /**
     * Shows a {@link Toast} on the UI thread.
     *
     * @param text The message to show
     */
    private void showToast(String text) {
        // We show a Toast by sending request message to mMessageHandler. This makes sure that the
        // Toast is shown on the UI thread.
        Message message = Message.obtain();
        message.arg1 = MESSAGE_SHOW_TOAST;
        message.obj = text;
        mMessageHandler.sendMessage(message);
    }

    private void showFrameCount(Integer count) {
        float fps;
        if (count == 1) {
            frameTime = (new Date()).getTime();
            fps = 0.0f;
        }
        else {
            long diff = (new Date()).getTime() - frameTime;
            fps = diff / ((frameCount - 1) * 10);
        }

        Message message = Message.obtain();
        message.arg1 = MESSAGE_SHOW_FRAME_COUNT;
        message.arg2 = count;
        message.obj = new Float(fps);
        mMessageHandler.sendMessage(message);
    }

    private void showCaptureCount(Integer count) {
        float fps;
        if (count == 1) {
            frameTime = (new Date()).getTime();
            fps = 0.0f;
        }
        else {
            long diff = (new Date()).getTime() - frameTime;
            fps = diff / ((frameCount - 1) * 10);
        }

        Message message = Message.obtain();
        message.arg1 = MESSAGE_SHOW_CAPTURE_COUNT;
        message.arg2 = count;
        message.obj = fps;
        mMessageHandler.sendMessage(message);
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, chooses the smallest one whose
     * width and height are at least as large as the respective requested values, and whose aspect
     * ratio matches with the specified value.
     *
     * @param choices     The list of sizes that the camera supports for the intended output class
     * @param width       The minimum desired width
     * @param height      The minimum desired height
     * @param aspectRatio The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private static Size chooseOptimalSize(Fragment fragment, Size[] choices, int width, int height, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<Size>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        int orientation = fragment.getResources().getConfiguration().orientation;
        for (Size option : choices) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (option.getHeight() <= option.getWidth() * height / width &&
                        option.getWidth() >= width && option.getHeight() >= height) {
                    bigEnough.add(option);
                }
            } else {
                if (option.getHeight() >= option.getWidth() * width / height &&
                        option.getHeight() >= width && option.getWidth() >= height) {
                    bigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    private static Size chooseCaptureSize(Fragment fragment, Size[] choices, int width, int height) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        Size lessEnough = new Size(width, height);
        int orientation = fragment.getResources().getConfiguration().orientation;
        for (Size option : choices) {
            if (option.getHeight() <= option.getWidth() * height / width &&
                    option.getWidth() < lessEnough.getWidth() && option.getHeight() < lessEnough.getHeight()) {
                lessEnough = new Size(option.getWidth() , option.getHeight());
            }
        }

        return lessEnough;
    }

    public static Camera2BasicFragment newInstance() {
        Camera2BasicFragment fragment = new Camera2BasicFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera2_basic, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        view.findViewById(R.id.detect_switch).setOnClickListener(this);
        textinfo1 = (TextView)view.findViewById(R.id.info1);
        textinfo2 = (TextView)view.findViewById(R.id.info2);
        textinfo3 = (TextView)view.findViewById(R.id.info3);
        textinfo4 = (TextView)view.findViewById(R.id.info4);
        textinfo5 = (TextView)view.findViewById(R.id.info5);
        textinfo6 = (TextView)view.findViewById(R.id.info6);
        textinfo7 = (TextView)view.findViewById(R.id.info7);
        textinfo8 = (TextView)view.findViewById(R.id.info8);
        textinfo9 = (TextView)view.findViewById(R.id.info9);
        textinfo10 = (TextView)view.findViewById(R.id.info10);
        textinfo11 = (TextView)view.findViewById(R.id.info11);
        videoView = (VideoView)view.findViewById(R.id.videoview);
        videoView.setX(0); videoView.setY(0);
        mTextureView = (AutoFitTextureView) view.findViewById(R.id.texture);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFile = getActivity().getExternalFilesDir(null);
        for(File file: mFile.listFiles())
            if (!file.isDirectory())
                file.delete();
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();

        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    /**
     * Sets up member variables related to camera.
     *
     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     */
    private void setUpCameraOutputs(int width, int height) {
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics
                        = manager.getCameraCharacteristics(cameraId);

                // We don't use a front facing camera in this sample.
                if (characteristics.get(CameraCharacteristics.LENS_FACING)
                        == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                // For still image captures, we use the largest available size.
                Size largest = Collections.max(
                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                        new CompareSizesByArea());

                // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                mPreviewSize = chooseOptimalSize(this, map.getOutputSizes(SurfaceTexture.class),
                        width, height, largest);

                // get less enough one as capture size
                mCaptureSize = chooseCaptureSize(this, map.getOutputSizes(SurfaceTexture.class), mPreviewSize.getWidth(), mPreviewSize.getHeight());

                // use small resolution to preview also
                mPreviewSize = mCaptureSize;

                // We fit the aspect ratio of TextureView to the size of preview we picked.
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mTextureView.setAspectRatio(
                            mPreviewSize.getWidth(), mPreviewSize.getHeight());
                    mImageReader = ImageReader.newInstance(mCaptureSize.getWidth(), mCaptureSize.getHeight(),
                            ImageFormat.YUV_420_888, /*maxImages*/3);
                    mSurfaceSize = new Size(height*mPreviewSize.getWidth()/mPreviewSize.getHeight(), height);
                } else {
                    mTextureView.setAspectRatio(
                            mPreviewSize.getHeight(), mPreviewSize.getWidth());
                    mImageReader = ImageReader.newInstance(mCaptureSize.getHeight(), mCaptureSize.getWidth(),
                            ImageFormat.YUV_420_888, /*maxImages*/3);
                    mSurfaceSize = new Size(width, width*mPreviewSize.getWidth()/mPreviewSize.getHeight());
                }

                mImageReader.setOnImageAvailableListener(
                        mOnImageAvailableListener, mBackgroundHandler);

                mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            new ErrorDialog().show(getFragmentManager(), "dialog");
        }
    }

    /**
     * Opens the camera specified by {@link Camera2BasicFragment#mCameraId}.
     */
    private void openCamera(int width, int height) {
        setUpCameraOutputs(width, height);
        configureTransform(width, height);
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }

    /**
     * Closes the current {@link CameraDevice}.
     */
    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new {@link CameraCaptureSession} for camera preview.
     */
    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder
                    = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (null == mCameraDevice) {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            mCaptureSession = cameraCaptureSession;
                            try {
                                // Finally, we start displaying the camera preview.
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        mCaptureCallback, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                            showToast("Failed");
                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = getActivity();
        if (null == mTextureView || null == mPreviewSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    /**
     * Capture a still picture. This method should be called when we get a response in
     */
    private void captureStillPicture() {
        try {
            final Activity activity = getActivity();
            if (null == activity || null == mCameraDevice) {
                return;
            }
            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());

            // Orientation
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            mCaptureSession.capture(captureBuilder.build(), null, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.detect_switch: {
                Button btn = (Button) view.findViewById(R.id.detect_switch);
                detectSwitch = !detectSwitch;
                if (detectSwitch)
                    btn.setText(getString(R.string.detect_on));
                else
                    btn.setText(getString(R.string.detect_off));
                break;
            }
        }
    }

    /**
     * Saves a JPEG {@link Image} into the specified {@link File}.
     */
    private static class ImageSaver implements Runnable {

        private final Image mImage;
        private final File mFile;
        private final Handler mHandler;
        private final int mCount;

        private void moveROI(int[] roi) {
            // We show a Toast by sending request message to mMessageHandler. This makes sure that the
            // Toast is shown on the UI thread.
            Message message = Message.obtain();
            message.arg1 = Camera2BasicFragment.MESSAGE_SHOW_ROI;
            message.arg2 = mCount;
            message.obj = roi;
            mHandler.sendMessage(message);
        }

        public ImageSaver(Image image, File file, Handler handler, int count) {
            mImage = image;
            mFile = file;
            mHandler = handler;
            mCount = count;
        }

        @Override
        public void run() {

            ByteBuffer buffer;
            buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes0 = new byte[buffer.remaining()];
            buffer.get(bytes0);
            buffer = mImage.getPlanes()[1].getBuffer();
            byte[] bytes1 = new byte[buffer.remaining()];
            buffer.get(bytes1);
            buffer = mImage.getPlanes()[2].getBuffer();
            byte[] bytes2 = new byte[buffer.remaining()];
            buffer.get(bytes2);

            DetectionROI roi = new DetectionROI(mImage.getWidth(), mImage.getHeight(), mFile.getPath());
            int[] rectROI = roi.detectROI(bytes0, bytes1, bytes2);
            //Log.d("camerademo", "rect = " + rectROI[0] + " " + rectROI[1] + " " + rectROI[2] + " " + rectROI[3]);
            roi.release();
            moveROI(rectROI);
            mImage.close();
        }
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    public static class ErrorDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage("This device doesn't support Camera2 API.")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    })
                    .create();
        }

    }

}