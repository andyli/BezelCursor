package net.onthewings.touchservice

import Utils._
import LinuxInput._
import android.view.Display
import android.view.Surface
import android.graphics.Point

class TouchInputDevice(path:String, display:Display) extends InputDevice(path) {
	val isProtocolB = getevent_p.indexWhere(line => line.indexOf("%04x".format(ABS_MT_SLOT)) >= 0) >= 0
	
	log(if (isProtocolB) path + " is B" else path + " is A")
	protected val detail_re(_, _, x_min_str, x_max_str) = getevent_p(
		getevent_p.indexWhere(line => line.indexOf("%04x".format(ABS_MT_POSITION_X)) >= 0)
	)
	protected val detail_re(_, _, y_min_str, y_max_str) = getevent_p(
		getevent_p.indexWhere(line => line.indexOf("%04x".format(ABS_MT_POSITION_Y)) >= 0)
	)
	val x_min = x_min_str.toInt
	val x_max = x_max_str.toInt
	val y_min = y_min_str.toInt
	val y_max = y_max_str.toInt
	
	def displayToDevice(x:Double, y:Double):Point = {
		val displaySize = new Point()
		display.getSize(displaySize)
		log("display " + displaySize.x + " " + displaySize.y + " " + display.getRotation())
		display.getRotation() match {
			case Surface.ROTATION_0 =>
				return new Point(
					map(x, 0, displaySize.x, x_min, x_max).toInt,
					map(y, 0, displaySize.y, y_min, y_max).toInt
				)
			case Surface.ROTATION_90 =>
				return new Point(
					map(y, 0, displaySize.y, x_max, x_min).toInt,
					map(x, 0, displaySize.x, y_min, y_max).toInt
				)
			case Surface.ROTATION_180 =>
				return new Point(
					map(x, 0, displaySize.x, x_max, x_min).toInt,
					map(y, 0, displaySize.y, y_max, y_min).toInt
				)
			case Surface.ROTATION_270 =>
				return new Point(
					map(y, 0, displaySize.y, x_min, x_max).toInt, 
					map(x, 0, displaySize.x, y_max, y_min).toInt
				)
		}
	}
	
	def sendTapEvents(x:Double, y:Double):Unit = {
		//https://www.kernel.org/doc/Documentation/input/multi-touch-protocol.txt
		val devicePoint = displayToDevice(x, y)
		if (isProtocolB) {
			sendEvent(EV_ABS, ABS_MT_SLOT, 0x00000000)
			sendEvent(EV_ABS, ABS_MT_TRACKING_ID, 0x00000100)
			sendEvent(EV_ABS, ABS_MT_POSITION_X, devicePoint.x)
			sendEvent(EV_ABS, ABS_MT_POSITION_Y, devicePoint.y)
			//sendEvent(EV_ABS, ABS_MT_PRESSURE, 0x00000001)
			sendEvent(EV_SYN, SYN_REPORT, 0)
			sendEvent(EV_ABS, ABS_MT_TRACKING_ID, 0xffffffff)
			sendEvent(EV_SYN, SYN_REPORT, 0)
		} else {
			sendEvent(EV_ABS, ABS_MT_POSITION_X, devicePoint.x)
			sendEvent(EV_ABS, ABS_MT_POSITION_Y, devicePoint.y)
			//sendEvent(EV_ABS, ABS_MT_PRESSURE, 0x00000001)
			sendEvent(EV_SYN, SYN_MT_REPORT, 0)
			sendEvent(EV_SYN, SYN_REPORT, 0)
			sendEvent(EV_SYN, SYN_MT_REPORT, 0)
			sendEvent(EV_SYN, SYN_REPORT, 0)
		}
	}
}