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




/* Debug tools
 */
 int g_debug = 0;
 
   
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



jint Java_net_onthewings_touchservice_AndroidEvents_intEnableDebug(JNIEnv* env, jobject thiz, jint enable) {
	g_debug = enable;
	return g_debug;
}
 
jint JNI_OnLoad(JavaVM *vm, void *reserved)
{
	debug("TouchService native lib loaded.");
	return JNI_VERSION_1_2; //1_2 1_4
}

void JNI_OnUnload(JavaVM *vm, void *reserved)
{
	debug("TouchService native lib unloaded.");
}

std::vector<struct pollfd> ufds;

const char *device_path = "/dev/input";

int g_Polling = 0;
struct input_event event;
int c;
int i;
int pollres;
int get_time = 0;
const char *newline = "\n";
uint16_t get_switch = 0;
int version;

int dont_block = -1;
int event_count = 0;
int sync_rate = 0;
int64_t last_sync_time = 0;
const char *device = NULL; 


static int open_device(int index)
{
	if (index >= ioDevices.size()) return -1;

	debug("open_device prep to open");
	std::string device = ioDevices[index].device_path;
	
	debug("open_device call %s", device.c_str());
    int version;
    int fd;
    
    char name[80];
    char location[80];
    char idstr[80];
    struct input_id id;
	
    fd = open(device.c_str(), O_RDWR);
    if(fd < 0) {
    	ioDevices[index].ufds.fd = -1;
		
    	ioDevices[index].device_name = "";
		debug("could not open %s, %s", device.c_str(), strerror(errno));
        return -1;
    }
    
    ioDevices[index].ufds.fd = fd;
	ufds[index].fd = fd;
	
    name[sizeof(name) - 1] = '\0';
    if(ioctl(fd, EVIOCGNAME(sizeof(name) - 1), &name) < 1) {
        debug("could not get device name for %s, %s", device.c_str(), strerror(errno));
        name[0] = '\0';
    }
	debug("Device %d: %s: %s", ioDevices.size(), device.c_str(), name);
	
	ioDevices[index].device_name = strdup(name);
    
    
    return 0;
}


static int scan_dir(const char *dirname)
{
	ioDevices.clear();
    char devname[PATH_MAX];
    char *filename;
    DIR *dir;
    struct dirent *de;
    dir = opendir(dirname);
    if(dir == NULL)
        return -1;
    strcpy(devname, dirname);
    filename = devname + strlen(devname);
    *filename++ = '/';
    while((de = readdir(dir))) {
        if (
        	de->d_name[0] == '.' &&
        	(de->d_name[1] == '\0' ||
            (de->d_name[1] == '.' && de->d_name[2] == '\0'))
        )
            continue;

        strcpy(filename, de->d_name);
		debug("scan_dir:prepare to open:%s", devname);
		// add new filename to our structure: devname
		pollfd ufd;
		ufd.events = POLLIN;
		ufds.push_back(ufd);
		
		IoDevice dev;
		dev.ufds.events = POLLIN;
		dev.device_path = strdup(devname);
		ioDevices.push_back(dev);

    }
    closedir(dir);
    return 0;
} 

jint Java_net_onthewings_touchservice_AndroidEvents_intSendEvent(JNIEnv* env,jobject thiz, jint index, uint16_t type, uint16_t code, int32_t value) {
	if (index >= ioDevices.size() || ioDevices[index].ufds.fd == -1) return -1;
	int fd = ioDevices[index].ufds.fd;
	debug("SendEvent call (%d,%d,%d,%d)", fd, type, code, value);
	struct uinput_event event;
	int len;

	if (fd <= fileno(stderr)) return -1;

	memset(&event, 0, sizeof(event));
	event.type = type;
	event.code = code;
	event.value = value;

	len = write(fd, &event, sizeof(event));
	debug("SendEvent done:%d",len);
} 



jint Java_net_onthewings_touchservice_AndroidEvents_ScanFiles( JNIEnv* env,jobject thiz ) {
	int res = scan_dir(device_path);
	if(res != 0) {
		debug("scan dir failed for %s:", device_path);
		return -1;
	}
	
	return ioDevices.size();
}

jstring Java_net_onthewings_touchservice_AndroidEvents_getDevPath( JNIEnv* env,jobject thiz, jint index) {
	return env->NewStringUTF(ioDevices[index].device_path.c_str());
}

jstring Java_net_onthewings_touchservice_AndroidEvents_getDevName( JNIEnv* env,jobject thiz, jint index) {
	std::string dName = ioDevices[index].device_name;
	if (dName == "") return NULL;
	else return env->NewStringUTF(ioDevices[index].device_name.c_str());
}

jint Java_net_onthewings_touchservice_AndroidEvents_OpenDev( JNIEnv* env,jobject thiz, jint index ) {
	return open_device(index);
}

jint Java_net_onthewings_touchservice_AndroidEvents_PollDev( JNIEnv* env,jobject thiz, jint index ) {
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

#ifdef __cplusplus
}
#endif
