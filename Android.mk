LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, app/src/main)

LOCAL_MANIFEST_FILE := app/src/main/AndroidManifest.xml
LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/app/src/main/res
LOCAL_RESOURCE_DIR += frameworks/support/v7/appcompat/res
LOCAL_RESOURCE_DIR += frameworks/support/v7/recyclerview/res
LOCAL_RESOURCE_DIR += frameworks/support/design/res

LOCAL_JAVA_LIBRARIES += telephony-common

LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4
LOCAL_STATIC_JAVA_LIBRARIES += android-support-v7-appcompat
LOCAL_STATIC_JAVA_LIBRARIES += android-support-v7-recyclerview
LOCAL_STATIC_JAVA_LIBRARIES += android-support-design
LOCAL_STATIC_JAVA_LIBRARIES += picasso
LOCAL_STATIC_JAVA_LIBRARIES += jodatime
LOCAL_STATIC_JAVA_AAR_LIBRARIES += roundedimageview
LOCAL_STATIC_JAVA_AAR_LIBRARIES += emojiconlibrary

LOCAL_AAPT_INCLUDE_ALL_RESOURCES := true
LOCAL_AAPT_FLAGS := --auto-add-overlay
LOCAL_AAPT_FLAGS += --extra-packages android.support.v7.appcompat
LOCAL_AAPT_FLAGS += --extra-packages android.support.v7.recyclerview
LOCAL_AAPT_FLAGS += --extra-packages android.support.design
LOCAL_AAPT_FLAGS += --extra-packages picasso
LOCAL_AAPT_FLAGS += --extra-packages jodatime
LOCAL_AAPT_FLAGS += --extra-packages roundedimageview
LOCAL_AAPT_FLAGS += --extra-packages emojiconlibrary

LOCAL_PACKAGE_NAME := Messages
LOCAL_CERTIFICATE := platform
#LOCAL_PROGUARD_FLAG_FILES := app/proguard-rules.pro

LOCAL_PROGUARD_ENABLED := disabled

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := picasso:app/libs/picasso-2.5.2.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += jodatime:app/libs/jodatime-2.9.2.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += roundedimageview:app/libs/roundedimageview-2.2.1.aar 
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += emojiconlibrary:app/libs/emojiconlibrary-1.3.3.aar 

include $(BUILD_MULTI_PREBUILT)

include $(call all-makefiles-under,$(LOCAL_PATH))

