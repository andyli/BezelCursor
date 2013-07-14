LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := TouchService
LOCAL_SRC_FILES := TouchService.cpp
LOCAL_LDLIBS    := -llog

include $(BUILD_SHARED_LIBRARY)
