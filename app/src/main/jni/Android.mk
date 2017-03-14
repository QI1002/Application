LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)


LOCAL_SRC_FILES  := detect_roi_jni.cpp
LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_LDLIBS     += -llog -ldl

LOCAL_MODULE     := detection_roi

include $(BUILD_SHARED_LIBRARY)
