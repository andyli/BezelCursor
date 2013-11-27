#include <string>
#include <vector>
#include <stdint.h>
#include <jni.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <stdio.h>
#include <dirent.h>
#include <time.h>
#include <errno.h>

#include <sys/ioctl.h>
#include <sys/mman.h>
#include <sys/types.h>
#include <sys/inotify.h>
#include <sys/limits.h>
#include <sys/poll.h>

#include <linux/fb.h>
#include <linux/kd.h>
#include <linux/input.h>

#include <android/log.h>
#define TAG "TouchService::JNI"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , TAG, "%s", __VA_ARGS__)

#ifdef __cplusplus
extern "C" {
#endif

struct IoDevice {
	struct pollfd ufds;
	std::string device_path;
	std::string device_name;
};

std::vector<IoDevice> ioDevices;

struct uinput_event {
	struct timeval time;
	uint16_t type;
	uint16_t code;
	int32_t value;
};

struct label {
    const char *name;
    int value;
};

#define LABEL(constant) { #constant, constant }
#define LABEL_END { NULL, -1 }




/*
 * Debug tools
 */
bool g_debug = false;

void debug(const char *szFormat, ...)
{
	if (g_debug == 0) return;
	//if (strlen(szDbgfile) == 0) return;

	char szBuffer[4096]; //in this buffer we form the message
	const size_t NUMCHARS = sizeof(szBuffer) / sizeof(szBuffer[0]);
	const int LASTCHAR = NUMCHARS - 1;
	//format the input string
	va_list pArgs;
	va_start(pArgs, szFormat);
	// use a bounded buffer size to prevent buffer overruns.  Limit count to
	// character size minus one to allow for a NULL terminating character.
	vsnprintf(szBuffer, NUMCHARS - 1, szFormat, pArgs);
	va_end(pArgs);
	//ensure that the formatted string is NULL-terminated
	szBuffer[LASTCHAR] = '\0';

	LOGD(szBuffer);
	//TextCallback(szBuffer);
}



JNIEXPORT jboolean JNICALL getDebugEnabled(JNIEnv* env, jobject thiz) {
	return g_debug;
}

JNIEXPORT jboolean JNICALL setDebugEnabled(JNIEnv* env, jobject thiz, jboolean enable) {
	g_debug = enable;
	return g_debug;
}

std::vector<struct pollfd> ufds;

int g_Polling = 0;
struct input_event event;
int c;
int i;
int pollres;
int get_time = 0;
const char *newline = "\n";
uint16_t get_switch = 0;

int dont_block = -1;
int event_count = 0;
int sync_rate = 0;
int64_t last_sync_time = 0;
const char *device = NULL;

IoDevice* getIoDevice(std::string devicePath, bool createIfNeeded) {
	for (int i = 0; i < ioDevices.size(); ++i) {
		if (ioDevices[i].device_path == devicePath){
			debug("getIoDevice found");
			return &ioDevices[i];
		}
	}

	IoDevice* dev = NULL;
	if (createIfNeeded) {
		debug("getIoDevice not found");
		ioDevices.resize(ioDevices.size()+1);
		dev = &ioDevices[ioDevices.size()-1];
		dev->device_path = devicePath;
	}
	return dev;
}

jint SendEvent(JNIEnv* env, jobject thiz, jstring devicePath, uint16_t type, uint16_t code, int32_t value) {
	const char *devicePathStr = env->GetStringUTFChars(devicePath, 0);
	IoDevice* ioDevice = getIoDevice(devicePathStr, false);
	env->ReleaseStringUTFChars(devicePath, devicePathStr);

	if (!ioDevice)
		return -1;

	int fd = ioDevice->ufds.fd;

	debug("SendEvent call (%d,%d,%d,%d)", fd, type, code, value);

	if (fd <= fileno(stderr))
		return -1;

	struct uinput_event event;
	int len;

	memset(&event, 0, sizeof(event));
	event.type = type;
	event.code = code;
	event.value = value;

	len = write(fd, &event, sizeof(event));
	debug("SendEvent done:%d",len);

	if (len != sizeof(event))
		return -1;

	return 0;
}

jint OpenDev(JNIEnv* env, jobject thiz, jstring devicePath) {
	const char *devicePathStr = env->GetStringUTFChars(devicePath, 0);

    int fd;

    fd = open(devicePathStr, O_RDWR);
    if (fd < 0) {
		debug("could not open %s, %s", devicePathStr, strerror(errno));
		env->ReleaseStringUTFChars(devicePath, devicePathStr);
        return -1;
    }

	debug("open_device %s %i", devicePathStr, fd);

    IoDevice* ioDevice = getIoDevice(devicePathStr, true);
    if (!ioDevice) {
    	debug("getIoDevice failed");
    	return -1;
    }
    ioDevice->ufds.events = POLLIN;
    ioDevice->ufds.fd = fd;

    env->ReleaseStringUTFChars(devicePath, devicePathStr);

    return 0;
}

jint Java_net_onthewings_bezelcursor_AndroidEvents_PollDev( JNIEnv* env,jobject thiz, jint index ) {
	if (index >= ioDevices.size() || ioDevices[index].ufds.fd == -1) return -1;
	int pollres = poll(&ufds[0], ioDevices.size(), -1);
	if(ufds[index].revents) {
		if(ufds[index].revents & POLLIN) {
			int res = read(ufds[index].fd, &event, sizeof(event));
			if(res < (int)sizeof(event)) {
				return 1;
			} 
			else return 0;
		}
	}
	return -1;
}

jint Java_net_pocketmagic_android_eventinjector_Events_getType( JNIEnv* env,jobject thiz ) {
	return event.type;
}

jint Java_net_pocketmagic_android_eventinjector_Events_getCode( JNIEnv* env,jobject thiz ) {
	return event.code;
}

jint Java_net_pocketmagic_android_eventinjector_Events_getValue( JNIEnv* env,jobject thiz ) {
	return event.value;
}

/*
 * Table of methods associated with the class.
 */
static JNINativeMethod InputDeviceMethods[] = {
	/* name, signature, funcPtr */
	{"getDebugEnabled", "()Z", (void*)getDebugEnabled},
	{"setDebugEnabled", "(Z)Z", (void*)setDebugEnabled},
	{"OpenDev", "(Ljava/lang/String;)I", (void*)OpenDev},
	{"SendEvent", "(Ljava/lang/String;III)I", (void*)SendEvent},
};

int jniRegisterNativeMethods(JNIEnv* env, const char* className,
    const JNINativeMethod* gMethods, int numMethods)
{
    jclass clazz;

    debug("Registering %s natives\n", className);
    clazz = env->FindClass(className);
    if (clazz == NULL) {
    	debug("Native registration unable to find class '%s'\n", className);
        return -1;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
    	debug("RegisterNatives failed for '%s'\n", className);
        return -1;
    }
    return 0;
}

/*
 * Register native methods for all classes we know about.
 */
static int registerNatives(JNIEnv* env)
{
	if (jniRegisterNativeMethods(
		env,
		"net/onthewings/bezelcursor/InputDevice",
		InputDeviceMethods,
		sizeof(InputDeviceMethods) / sizeof(InputDeviceMethods[0])
	) != 0){
		debug("registerNatives failed");
		return JNI_FALSE;
	}

	debug("registerNatives ok");
	return JNI_TRUE;
}

jint JNI_OnLoad(JavaVM *vm, void *reserved)
{
	debug("TouchService native lib loaded.");

	JNIEnv* env = NULL;
	jint result = -1;

	if (vm->GetEnv((void **)&env, JNI_VERSION_1_2) != JNI_OK)
		return JNI_ERR;

	if (!registerNatives(env))
		return JNI_ERR;

	return JNI_VERSION_1_2; //1_2 1_4
}

void JNI_OnUnload(JavaVM *vm, void *reserved)
{
	debug("TouchService native lib unloaded.");
}

#ifdef __cplusplus
}
#endif
