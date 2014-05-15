package net.onthewings.bezelcursor;

/**
 * Linux input event codes.
 * https://github.com/torvalds/linux/blob/master/include/uapi/linux/input.h
 */
class LinuxInput {
	/*
	 * Event types
	 */
	
	static public var EV_SYN = 			0x00;
	static public var EV_KEY = 			0x01;
	static public var EV_REL = 			0x02;
	static public var EV_ABS = 			0x03;
	static public var EV_MSC = 			0x04;
	static public var EV_SW = 			0x05;
	static public var EV_LED = 			0x11;
	static public var EV_SND = 			0x12;
	static public var EV_REP = 			0x14;
	static public var EV_FF = 			0x15;
	static public var EV_PWR = 			0x16;
	static public var EV_FF_STATUS = 	0x17;
	static public var EV_MAX = 			0x1f;
	static public var EV_CNT = 			(EV_MAX+1);
	
	/*
	 * Synchronization events.
	 */
	
	static public var SYN_REPORT = 		0;
	static public var SYN_CONFIG = 		1;
	static public var SYN_MT_REPORT = 	2;
	static public var SYN_DROPPED = 	3;
	
	/*
	 * Relative axes
	 */
	
	static public var REL_X = 			0x00;
	static public var REL_Y = 			0x01;
	static public var REL_Z = 			0x02;
	static public var REL_RX = 			0x03;
	static public var REL_RY = 			0x04;
	static public var REL_RZ = 			0x05;
	static public var REL_HWHEEL = 		0x06;
	static public var REL_DIAL = 		0x07;
	static public var REL_WHEEL = 		0x08;
	static public var REL_MISC = 		0x09;
	static public var REL_MAX = 		0x0f;
	static public var REL_CNT = 		(REL_MAX+1);
	
	/*
	 * Absolute axes
	 */
	
	static public var ABS_X = 			0x00;
	static public var ABS_Y = 			0x01;
	static public var ABS_Z = 			0x02;
	static public var ABS_RX = 			0x03;
	static public var ABS_RY = 			0x04;
	static public var ABS_RZ = 			0x05;
	static public var ABS_THROTTLE = 	0x06;
	static public var ABS_RUDDER = 		0x07;
	static public var ABS_WHEEL = 		0x08;
	static public var ABS_GAS = 		0x09;
	static public var ABS_BRAKE = 		0x0a;
	static public var ABS_HAT0X = 		0x10;
	static public var ABS_HAT0Y = 		0x11;
	static public var ABS_HAT1X = 		0x12;
	static public var ABS_HAT1Y = 		0x13;
	static public var ABS_HAT2X = 		0x14;
	static public var ABS_HAT2Y = 		0x15;
	static public var ABS_HAT3X = 		0x16;
	static public var ABS_HAT3Y = 		0x17;
	static public var ABS_PRESSURE = 	0x18;
	static public var ABS_DISTANCE = 	0x19;
	static public var ABS_TILT_X = 		0x1a;
	static public var ABS_TILT_Y = 		0x1b;
	static public var ABS_TOOL_WIDTH = 	0x1c;
	
	static public var ABS_VOLUME = 		0x20;
	
	static public var ABS_MISC = 		0x28;
	
	static public var ABS_MT_SLOT = 		0x2f;	/* MT slot being modified */
	static public var ABS_MT_TOUCH_MAJOR = 	0x30;	/* Major axis of touching ellipse */
	static public var ABS_MT_TOUCH_MINOR = 	0x31;	/* Minor axis (omit if circular) */
	static public var ABS_MT_WIDTH_MAJOR = 	0x32;	/* Major axis of approaching ellipse */
	static public var ABS_MT_WIDTH_MINOR = 	0x33;	/* Minor axis (omit if circular) */
	static public var ABS_MT_ORIENTATION = 	0x34;	/* Ellipse orientation */
	static public var ABS_MT_POSITION_X = 	0x35;	/* Center X touch position */
	static public var ABS_MT_POSITION_Y = 	0x36;	/* Center Y touch position */
	static public var ABS_MT_TOOL_TYPE = 	0x37;	/* Type of touching device */
	static public var ABS_MT_BLOB_ID = 		0x38;	/* Group a set of packets as a blob */
	static public var ABS_MT_TRACKING_ID = 	0x39;	/* Unique ID of initiated contact */
	static public var ABS_MT_PRESSURE = 	0x3a;	/* Pressure on contact area */
	static public var ABS_MT_DISTANCE = 	0x3b;	/* Contact hover distance */
	static public var ABS_MT_TOOL_X = 		0x3c;	/* Center X tool position */
	static public var ABS_MT_TOOL_Y = 		0x3d;	/* Center Y tool position */
	
	
	static public var ABS_MAX = 			0x3f;
	static public var ABS_CNT = 			(ABS_MAX+1);
}