package net.onthewings.bezelcursor

/**
 * Linux input event codes.
 * https://github.com/torvalds/linux/blob/master/include/uapi/linux/input.h
 */
object LinuxInput {
	/*
	 * Event types
	 */
	
	val EV_SYN = 			0x00
	val EV_KEY = 			0x01
	val EV_REL = 			0x02
	val EV_ABS = 			0x03
	val EV_MSC = 			0x04
	val EV_SW = 			0x05
	val EV_LED = 			0x11
	val EV_SND = 			0x12
	val EV_REP = 			0x14
	val EV_FF = 			0x15
	val EV_PWR = 			0x16
	val EV_FF_STATUS = 		0x17
	val EV_MAX = 			0x1f
	val EV_CNT = 			(EV_MAX+1)
	
	/*
	 * Synchronization events.
	 */
	
	val SYN_REPORT = 		0
	val SYN_CONFIG = 		1
	val SYN_MT_REPORT = 	2
	val SYN_DROPPED = 		3
	
	/*
	 * Relative axes
	 */
	
	val REL_X = 			0x00
	val REL_Y = 			0x01
	val REL_Z = 			0x02
	val REL_RX = 			0x03
	val REL_RY = 			0x04
	val REL_RZ = 			0x05
	val REL_HWHEEL = 		0x06
	val REL_DIAL = 			0x07
	val REL_WHEEL = 		0x08
	val REL_MISC = 			0x09
	val REL_MAX = 			0x0f
	val REL_CNT = 			(REL_MAX+1)
	
	/*
	 * Absolute axes
	 */
	
	val ABS_X = 			0x00
	val ABS_Y = 			0x01
	val ABS_Z = 			0x02
	val ABS_RX = 			0x03
	val ABS_RY = 			0x04
	val ABS_RZ = 			0x05
	val ABS_THROTTLE = 		0x06
	val ABS_RUDDER = 		0x07
	val ABS_WHEEL = 		0x08
	val ABS_GAS = 			0x09
	val ABS_BRAKE = 		0x0a
	val ABS_HAT0X = 		0x10
	val ABS_HAT0Y = 		0x11
	val ABS_HAT1X = 		0x12
	val ABS_HAT1Y = 		0x13
	val ABS_HAT2X = 		0x14
	val ABS_HAT2Y = 		0x15
	val ABS_HAT3X = 		0x16
	val ABS_HAT3Y = 		0x17
	val ABS_PRESSURE = 		0x18
	val ABS_DISTANCE = 		0x19
	val ABS_TILT_X = 		0x1a
	val ABS_TILT_Y = 		0x1b
	val ABS_TOOL_WIDTH = 	0x1c
	
	val ABS_VOLUME = 		0x20
	
	val ABS_MISC = 			0x28
	
	val ABS_MT_SLOT = 			0x2f	/* MT slot being modified */
	val ABS_MT_TOUCH_MAJOR = 	0x30	/* Major axis of touching ellipse */
	val ABS_MT_TOUCH_MINOR = 	0x31	/* Minor axis (omit if circular) */
	val ABS_MT_WIDTH_MAJOR = 	0x32	/* Major axis of approaching ellipse */
	val ABS_MT_WIDTH_MINOR = 	0x33	/* Minor axis (omit if circular) */
	val ABS_MT_ORIENTATION = 	0x34	/* Ellipse orientation */
	val ABS_MT_POSITION_X = 	0x35	/* Center X touch position */
	val ABS_MT_POSITION_Y = 	0x36	/* Center Y touch position */
	val ABS_MT_TOOL_TYPE = 		0x37	/* Type of touching device */
	val ABS_MT_BLOB_ID = 		0x38	/* Group a set of packets as a blob */
	val ABS_MT_TRACKING_ID = 	0x39	/* Unique ID of initiated contact */
	val ABS_MT_PRESSURE = 		0x3a	/* Pressure on contact area */
	val ABS_MT_DISTANCE = 		0x3b	/* Contact hover distance */
	val ABS_MT_TOOL_X = 		0x3c	/* Center X tool position */
	val ABS_MT_TOOL_Y = 		0x3d	/* Center Y tool position */
	
	
	val ABS_MAX = 				0x3f
	val ABS_CNT = 				(ABS_MAX+1)
}