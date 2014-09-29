package net.onthewings.bezelcursor;

import net.onthewings.bezelcursor.Utils.*;
import net.onthewings.bezelcursor.LinuxInput.*;

using Std;
using Lambda;
using StringTools;
using net.onthewings.bezelcursor.Utils;
using hxLINQ.LINQ;
import net.onthewings.bezelcursor.InputDevice.*;

class InputDevice {

	static function __init__():Void {
		java.lang.System.loadLibrary("TouchService");
	}

	@:native static public function getDebugEnabled():Bool return throw "jni";
	@:native static public function setDebugEnabled(enable:Bool):Bool return throw "jni";
	@:native static public function OpenDev(devicePath:String):Int return throw "jni";
	@:native static public function SendEvent(devicePath:String, _type:Int, code:Int, value:Int):Int return throw "jni";
	
	
	/**
	 * function Open : opens an input event node
	 * @param forceOpen will try to set permissions and then reopen if first open attempt fails
	 * @return true if input event node has been opened
	 */
	static public function Open(devicePath:String, forceOpen:Bool = true):Bool {
		var res = OpenDev(devicePath);
		// if opening fails, we might not have the correct permissions, try changing 660 to 666
		if (res != 0) {
			// possible only if we have root
			if(forceOpen && Shell.isSuAvailable()) { 
				// set new permissions
				Shell.runCommand("chmod 666 "+ devicePath);
				// reopen
				res = OpenDev(devicePath);
			}
		}
		var opened = (res == 0);
		log("Open:" + devicePath + " Result:" + opened);
		return opened;
	}
	
	/**
	 * Get the input device path (eg. /dev/input/event2) that gives touch events.
	 */
	static public function getTouchDevicePath():String {
		if (Shell.isSuAvailable()) {
			var getEvent_lp = Shell.getProcessOutput("getevent -p").split("\n");
			var touchEventLine = getEvent_lp.filter(function(line) return line.indexOf(ABS_MT_POSITION_X.hex(4).toLowerCase()) >= 0 && line.indexOf("value") >= 0);
			if (touchEventLine.length <= 0) {
				return null;
			}
			var deviceLine = getEvent_lp.linq()
				.select(function(line,i) return {line:line, i:i})
				.last(function(l) return l.i < getEvent_lp.indexOf(touchEventLine[0]) && l.line.indexOf("device") >= 0)
				.line;
			var path = deviceLine.substring(deviceLine.indexOf(":")+1).trim();
			return path;
		}
		
		return null;
	}


	public var path(default, null):String;
	public function new(path:String):Void {
		this.path = path;
		getevent_p = if (Shell.isSuAvailable())
			Shell.getProcessOutput("getevent -p " + path).split("\n");
		else
			null;

		name = name_re.match(getevent_p[1]) ? name_re.matched(1) : "";
	}

	var getevent_p:Array<String>; 
	var name_re = ~/\s*name:\s*"(.*)"\s*/;
	function detail(evt:Int) {
		var re = new EReg('\\s*(${evt.hex(4).toLowerCase()})\\s+:?\\s*value\\s+([0-9]+),?\\s+min\\s+([0-9]+),?\\s+max\\s+([0-9]+),.+', "i");
		for (line in getevent_p) {
			if (re.match(line)) {
				return {
					value: re.matched(2).parseInt(),
					min: re.matched(3).parseInt(),
					max: re.matched(4).parseInt(),
				}
			}
		}
		return throw 'failed to find the line of $evt in:\n' + getevent_p;
	}
	
	var name:String;
	
	public function open():Bool {
		return InputDevice.Open(path);
	}
	
	public function sendEvent(eventType:Int, event:Int, value:Int):Void {
		var sendEventSuccess = untyped InputDevice.SendEvent(path, eventType, event, value);
		log("sendEventSuccess " + sendEventSuccess);
		//Shell.runCommand("sendevent " + path + " " + eventType + " " + event + " " + value)
	}
}

class AbsInputEvent {
	public var id:Int;
	public var name:String;
	public var value:Int;
	public var min:Int;
	public var max:Int;
	public var fuzz:Int;
	public var flat:Int;
	public var resolution:Int;

	public function new(id:Int, name:String, value:Int, min:Int, max:Int, fuzz:Int, flat:Int, resolution:Int):Void {
		this.id = id;
		this.name = name;
		this.value = value;
		this.min = min;
		this.max = max;
		this.fuzz = fuzz;
		this.flat = flat;
		this.resolution = resolution;
	}
}