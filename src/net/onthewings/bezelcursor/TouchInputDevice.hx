package net.onthewings.bezelcursor;

import net.onthewings.bezelcursor.Utils.*;
import net.onthewings.bezelcursor.LinuxInput.*;
import android.view.*;
import android.graphics.Point;
import android.os.Build_VERSION;

using Std;
using Lambda;
using StringTools;

@:nativeGen
class TouchInputDevice extends net.onthewings.bezelcursor.InputDevice {
	var display:Display;

	public function new(path:String, display:Display):Void {
		super(path);
		this.display = display;

		isProtocolB = getevent_p.exists(function(line) return line.indexOf(ABS_MT_SLOT.hex(4).toLowerCase()) >= 0);
		log(if (isProtocolB) path + " is B" else path + " is A");

		var detail = getevent_p.filter(function(line) return line.indexOf(ABS_MT_POSITION_X.hex(4).toLowerCase()) >= 0)[0];
		if (detail_re.match(detail)) {
			x_min = detail_re.matched(3).parseInt();
			x_max = detail_re.matched(4).parseInt();
		} else throw "parsing error: " + detail;

		var detail = getevent_p.filter(function(line) return line.indexOf(ABS_MT_POSITION_Y.hex(4).toLowerCase()) >= 0)[0];
		if (detail_re.match(detail)) {
			y_min = detail_re.matched(3).parseInt();
			y_max = detail_re.matched(4).parseInt();
		} else throw "parsing error: " + detail;
	}

	public var isProtocolB(default, null):Bool;
	public var x_min(default, null):Int;
	public var x_max(default, null):Int;
	public var y_min(default, null):Int;
	public var y_max(default, null):Int;
	
	public function displayToDevice(x:Float, y:Float):Point {
		var displaySize = new Point();
		if (Build_VERSION.SDK_INT >= 13) {
			display.getSize(displaySize);
		} else {
			displaySize.set(display.getWidth(), display.getHeight());
		}
		
		log("display " + displaySize.x + " " + displaySize.y + " " + display.getRotation());
		return switch(display.getRotation()) {
			case Surface.ROTATION_0:
				new Point(
					map(x, 0, displaySize.x, x_min, x_max).int(),
					map(y, 0, displaySize.y, y_min, y_max).int()
				);
			case Surface.ROTATION_90:
				new Point(
					map(y, 0, displaySize.y, x_max, x_min).int(),
					map(x, 0, displaySize.x, y_min, y_max).int()
				);
			case Surface.ROTATION_180:
				new Point(
					map(x, 0, displaySize.x, x_max, x_min).int(),
					map(y, 0, displaySize.y, y_max, y_min).int()
				);
			case Surface.ROTATION_270:
				new Point(
					map(y, 0, displaySize.y, x_min, x_max).int(), 
					map(x, 0, displaySize.x, y_max, y_min).int()
				);
			case rotation:
				throw "unknown rotation: " + rotation;
		}
	}
	
	public function sendTapEvents(x:Float, y:Float):Void {
		//https://www.kernel.org/doc/Documentation/input/multi-touch-protocol.txt
		var devicePoint = displayToDevice(x, y);
		if (isProtocolB) {
			sendEvent(EV_ABS, ABS_MT_SLOT, 0x00000000);
			sendEvent(EV_ABS, ABS_MT_TRACKING_ID, 0x00000100);
			sendEvent(EV_ABS, ABS_MT_POSITION_X, devicePoint.x);
			sendEvent(EV_ABS, ABS_MT_POSITION_Y, devicePoint.y);
			//sendEvent(EV_ABS, ABS_MT_PRESSURE, 0x00000001);
			sendEvent(EV_SYN, SYN_REPORT, 0);
			sendEvent(EV_ABS, ABS_MT_TRACKING_ID, 0xffffffff);
			sendEvent(EV_SYN, SYN_REPORT, 0);
		} else {
			sendEvent(EV_ABS, ABS_MT_POSITION_X, devicePoint.x);
			sendEvent(EV_ABS, ABS_MT_POSITION_Y, devicePoint.y);
			//sendEvent(EV_ABS, ABS_MT_PRESSURE, 0x00000001);
			sendEvent(EV_SYN, SYN_MT_REPORT, 0);
			sendEvent(EV_SYN, SYN_REPORT, 0);
			sendEvent(EV_SYN, SYN_MT_REPORT, 0);
			sendEvent(EV_SYN, SYN_REPORT, 0);
		}
	}
}