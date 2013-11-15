package net.onthewings.bezelcursor

//import scala.collection.JavaConversions._
import Utils._
import LinuxInput._
import scala.collection.mutable.HashMap


object InputDevice {
	System.loadLibrary("TouchService")
	
	@native protected def getDebugEnabled():Boolean
	@native protected def setDebugEnabled(enable:Boolean):Boolean
	@native protected def OpenDev(devicePath:String):Int
	@native protected def SendEvent(devicePath:String, _type:Int, code:Int, value:Int):Int
	
	
//	@native def RemoveDev(devid:Int):Int
//	@native def getDevPath(devid:Int):String
//	@native def getDevName(devid:Int):String
//	@native def PollDev(devid:Int):Int
//	@native def getType():Int
//	@native def getCode():Int
//	@native def getValue():Int
	
	
	/**
	 * function Open : opens an input event node
	 * @param forceOpen will try to set permissions and then reopen if first open attempt fails
	 * @return true if input event node has been opened
	 */
	def Open(devicePath:String, forceOpen:Boolean = true):Boolean = {
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
   		val opened = (res == 0);
   		log("Open:" + devicePath + " Result:" + opened);
   		return opened;
   	}
    
    /**
     * Get the input device path (eg. /dev/input/event2) that gives touch events.
     */
    def getTouchDevicePath():String = {
    	if (Shell.isSuAvailable()) {
    		val getEvent_lp = Shell.getProcessOutput("getevent -p").split("\n")
    		val touchEventLine = getEvent_lp.indexWhere(line => line.indexOf("%04x".format(ABS_MT_POSITION_X)) >= 0 && line.indexOf("value") >= 0)
    		if (touchEventLine < 0) {
    			return null;
    		}
    		val deviceLine = getEvent_lp.lastIndexWhere(line => line.indexOf("device") >= 0, touchEventLine)
    		val path = getEvent_lp(deviceLine).substring(getEvent_lp(deviceLine).indexOf(":")+1).trim()
    		return path
    	}
    	
    	return null
    }
}

class AbsInputEvent(id:Int, name:String, value:Int, min:Int, max:Int, fuzz:Int, flat:Int, resolution:Int) {
	
}

class InputDevice(path:String) {
	protected val getevent_p = if (Shell.isSuAvailable())
		Shell.getProcessOutput("getevent -p " + path).split("\n")
	else
		null
	protected val name_re = """\s*name:\s*"(.*)"\s*""".r
	protected val detail_re = """\s*([0-9a-f]+)\s+:?\s*value\s+([0-9]+),?\s+min\s+([0-9]+),?\s+max\s+([0-9]+),.+""".r
	
	val name_re(name) = getevent_p(1)
	
	def open():Boolean = {
		return InputDevice.Open(path)
	}
	
	def sendEvent(eventType:Int, event:Int, value:Int):Unit = {
		val sendEventSuccess = InputDevice.SendEvent(path, eventType, event, value)
		log("sendEventSuccess " + sendEventSuccess)
		//Shell.runCommand("sendevent " + path + " " + eventType + " " + event + " " + value)
	}
}