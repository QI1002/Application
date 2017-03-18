//
// Created by MTK00544 on 2017/3/14.
//
#include "detect_roi_jni.h"

#include <stdio.h>
#include <string.h>
#include <malloc.h>
#include <memory.h>
#include <assert.h>
#include <android/log.h>

#define ISROI(y, u, v) (y > 100 && u < 120 && v < 120)
#define MAX(x,y)       ((x>y) ? x : y)
#define MIN(x,y)       ((x>y) ? y : x)
#define YPOS(y, x)     ((y)*width + (x))
#define UVPOS(y,x)     ((y)*width/2 + (x))

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

    char* getLogFile()
    {
        char savePath[128];
        strcpy(savePath, mPath);
        strcat(savePath, "/detect.log");
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

    void detect(int detectcount, unsigned char* yplane, unsigned char* uplane, unsigned char* vplane, jint* roi)
    {
        const int MAX_GROUP = 256;
        const int MAX_EQUAL = 256;
        int width = mWidth;
        int height = mHeight;
        unsigned char* gplane = (unsigned char*)malloc(width*height);
        unsigned char  equal[MAX_EQUAL][2];
        unsigned int   region[MAX_GROUP][5];
        unsigned char* final_equal;
        unsigned char  group = 0;
        unsigned char  count = 0;
        unsigned char  y, u, v, gh, gv, gs[4];

        memset(gplane, 0, width*height);
        memset(region, 0, MAX_GROUP*5*sizeof(int));
        memset(equal, 0, MAX_GROUP*2);
        gh = gv = 0;

        for (int i = 0; i < MAX_GROUP; i++)
        {
            region[i][1] = width;
            region[i][2] = height;
        }

        for (int i = 0; i < height/2; i++) {
            for (int j = 0; j < width / 2; j++) {
                u = uplane[UVPOS(i, j)];
                v = vplane[UVPOS(i, j)];

                for (int k = 0; k < 4; k++) {
                    char xx = (k & 1);
                    char yy = (k & 2) >> 1;
                    unsigned int jy = 2 * j + xx;
                    unsigned int iy = 2 * i + yy;

                    y = yplane[YPOS(iy, jy)];
                    if (!ISROI(y, u, v))
                        continue;

                    gh = (j == 0 && xx == 0) ? 0 : gplane[YPOS(iy, jy - 1)];
                    gv = (i == 0 && yy == 0) ? 0 : gplane[YPOS(iy - 1, jy)];

                    if (gh > 0 || gv > 0) {
                        if (gh > 0 && gv > 0) {
                            gs[k] = MIN(gh, gv);

                            if (gh != gv) {

                                int same = -1;
                                for (int s = count - 1; s >= 0; s--)
                                {
                                    if (equal[s][0] == MIN(gh, gv) ||
                                        equal[s][1] == MAX(gh, gv))
                                    {
                                        same = s;
                                        break;
                                    }
                                }

                                if (same == -1)
                                {
                                    assert(count < MAX_EQUAL);
                                    equal[count][0] = MIN(gh, gv);
                                    equal[count][1] = MAX(gh, gv);
                                    count++;
                                }
                            }
                        } else
                            gs[k] = MAX(gh, gv);

                    } else {
                        group++;
                        assert(group < MAX_GROUP);
                        gs[k] = group;
                    }

                    gplane[YPOS(iy, jy)] = gs[k];

                    region[gs[k]][0]++;
                    if (region[gs[k]][1] > jy)
                        region[gs[k]][1] = jy;
                    if (region[gs[k]][2] > iy)
                        region[gs[k]][2] = iy;
                    if (region[gs[k]][3] < jy)
                        region[gs[k]][3] = jy;
                    if (region[gs[k]][4] < iy)
                        region[gs[k]][4] = iy;
                }
            }
        }

        // group equal to group final_equal
        final_equal = (unsigned char*)malloc((group+1) * sizeof(unsigned char));
        for (int g = 0; g <= group; g++) final_equal[g] = g;
        for (int c = 0; c < count; c++)
        {
            int min = equal[c][0];
            int max = equal[c][1];
            int temp = min;
            while(final_equal[temp] != temp) temp = final_equal[temp];
            final_equal[max] = temp;
        }

        // compact group final_equal again
        bool isCompact = true;
        for (int g = 1; g <= group; g++)
        {
            int temp = final_equal[g];
            while(final_equal[temp] != temp)
            {
                temp = final_equal[temp];
                isCompact = false;
            }

            final_equal[g] = temp;
            if (temp != g)
               region[temp][0] += region[g][0];
        }

        // find the group with max points
        int max_group = 0;
        for (int g = 1; g <= group; g++)
        {
            if (region[g][0] > region[max_group][0])
                max_group = g;
        }

        // find the bound of max group
        bool update = false;
        unsigned int left = width;
        unsigned int right = 0;
        unsigned int top = height;
        unsigned int bottom = 0;
        for (int g = 1; g <= group; g++)
        {
            if (final_equal[g] == max_group)
            {
                update = true;
                if (left > region[g][1])
                    left = region[g][1];
                if (top > region[g][2])
                    top = region[g][2];
                if (right < region[g][3])
                    right = region[g][3];
                if (bottom < region[g][4])
                    bottom = region[g][4];
            }
        }

        free(final_equal);
        free(gplane);

        if (update) {
            roi[0] = (jint) left;
            roi[1] = (jint) top;
            roi[2] = (jint) right;
            roi[3] = (jint) bottom;
        }else
        {
            roi[0] = roi[1] = roi[2] = roi[3] = -1;
        }

        if (group != 0) {
            char *logPath = getLogFile();
            FILE *wf = fopen(logPath, "a");
            free(logPath);
            if (wf != NULL) {
                fprintf(wf,
                        "detectcount = %d count=%d group = %d left = %d, top = %d, right = %d, bottom = %d (compact = %s)\n",
                        detectcount, count, group, left, top, right, bottom,
                        isCompact ? "true" : "false");
                fclose(wf);
            }

            if (group > 50 || count > 200 || left > right || top > bottom)
                saveFile(detectcount, yplane, uplane, vplane);
            //if (detectcount == 300)
            //    saveFile(detectcount, yplane, uplane, vplane);
        }

        LOGD("detectcount = %d count=%d group = %d left = %d, top = %d, right = %d, bottom = %d (compact = %s)",
             detectcount, count, group, left, top, right, bottom, isCompact ? "true" : "false");
    }
};

JNIEXPORT jlong JNICALL Java_io_github_qi1002_icamera_DetectionROI_nativeCreateObject
        (JNIEnv * jenv, jclass, jint width, jint height, jstring path) {
    const char* jpathstr = jenv->GetStringUTFChars(path, NULL);
    //LOGD("Java_io_github_qi1002_icamera_DetectionROI_nativeCreateObject enter (%d,%d,%s)", width, height, jpathstr);
    jlong result = (jlong)(new ROIDetection(width, height, jpathstr));
    return result;
}

JNIEXPORT void JNICALL Java_io_github_qi1002_icamera_DetectionROI_nativeDestroyObject
        (JNIEnv * jenv, jclass, jlong thiz) {
    //LOGD("Java_io_github_qi1002_icamera_DetectionROIr_nativeDestroyObject enter");
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
    roiData[0] = -1;
    roiData[1] = -1;
    roiData[2] = -1;
    roiData[3] = -1;
    
    ROIDetection* detection = (ROIDetection*)thiz;
    char* path = detection->getPath();

    detectCount++;
    //LOGD("Java_io_github_qi1002_icamera_DetectionROI_nativeDetectROI enter %d", detectCount);

    detection->detect(detectCount, yData, uData, vData, roiData);
    jenv->SetIntArrayRegion(rectROI, 0, 4, roiData);

    jenv->ReleaseByteArrayElements(yPlane, (jbyte*)yData, JNI_ABORT);
    jenv->ReleaseByteArrayElements(uPlane, (jbyte*)uData, JNI_ABORT);
    jenv->ReleaseByteArrayElements(vPlane, (jbyte*)vData, JNI_ABORT);
    jenv->ReleaseIntArrayElements(rectROI, roiData, JNI_ABORT);

    return rectROI;
}
