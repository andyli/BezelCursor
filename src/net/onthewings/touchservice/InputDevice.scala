package net.onthewings.touchservice

//import scala.collection.JavaConversions._
import Utils._
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
    
    /*
     * Get the input device path (eg. /dev/input/event2) that gives touch events
     */
    def getTouchDevicePath():String = {
    	if (Shell.isSuAvailable()) {
    		val getEvent_lp = Shell.getProcessOutput("getevent -p").split("\n")
    		val touchEventLine = getEvent_lp.indexWhere(line => line.indexOf("0035"/*"ABS_MT_POSITION_X"*/) > -1)
    		val deviceLine = getEvent_lp.lastIndexWhere(line => line.indexOf("device") > -1, touchEventLine)
    		return getEvent_lp(deviceLine).substring(getEvent_lp(deviceLine).indexOf(":")+1).trim()
    	}
    	
    	return null
    }
}

class AbsInputEvent(id:Int, name:String, value:Int, min:Int, max:Int, fuzz:Int, flat:Int, resolution:Int) {
	
}

class InputDevice(path:String) {
	private val getevent_p = if (Shell.isSuAvailable())
		Shell.getProcessOutput("getevent -p " + path).split("\n")
	else
		null
	private val name_re = """\s*name:\s*"(.*)"\s*""".r
	val name_re(name) = getevent_p(1)
	
	/*
	private val getevent_lp = Shell.getProcessOutput("getevent -lp " + path).split("\n")
	
	private val events_lp = getevent_lp.slice(
		getevent_lp.indexWhere(line => line.indexOf("events:") > -1) + 1,
		getevent_lp.indexWhere(line => line.indexOf("input props:") > -1)
	)
	private val events_p = getevent_p.slice(
		getevent_p.indexWhere(line => line.indexOf("events:") > -1) + 1,
		getevent_p.indexWhere(line => line.indexOf("input props:") > -1)
	)
	
	val absEvents = new HashMap[String,AbsInputEvent]()
	private val eventType_re = """\s*([A-Z]{3}) \(([0-9]{4})\): """.r
	private val eventDetail_re = """\s*([^\s]+)\s*:? value (-?[0-9]+), min (-?[0-9]+), max (-?[0-9]+), fuzz (-?[0-9]+), flat (-?[0-9]+), resolution (-?[0-9]+)\s*""".r
	private var _type:Int = -1
	for ((line_lp, line_p) <- events_lp.zip(events_p)) {
		eventType_re.findFirstIn(line_lp) match {
			case Some(eventType_re(t, n)) =>
				_type = n.toInt				
				_type match {
					case 3 =>
						val eventDetail_re(name, value, min, max, fuzz, flat, resolution) = eventType_re.replaceFirstIn(line_lp, "")
						val eventDetail_re(id, _, _, _, _, _, _) = eventType_re.replaceFirstIn(line_p, "")
						absEvents(name) = new AbsInputEvent(Integer.parseInt(id, 16), name, value.toInt, min.toInt, max.toInt, fuzz.toInt, flat.toInt, resolution.toInt)
					case _ =>
				}
				
			case None =>
				_type match {
					case 3 =>
						val eventDetail_re(name, value, min, max, fuzz, flat, resolution) = line_lp
						val eventDetail_re(id, _, _, _, _, _, _) = line_p
						absEvents(name) = new AbsInputEvent(Integer.parseInt(id, 16), name, value.toInt, min.toInt, max.toInt, fuzz.toInt, flat.toInt, resolution.toInt)
					case _ =>
				}
		}
	}
	//log("eventTypes(3).keySet" + absEvents.keySet)
	 */
	def open():Boolean = {
		return InputDevice.Open(path)
	}
	
	def sendEvent(eventType:Int, event:Int, value:Int):Unit = {
		val sendEventSuccess = InputDevice.SendEvent(path, eventType, event, value)
		log("sendEventSuccess " + sendEventSuccess)
		//Shell.runCommand("sendevent " + path + " " + eventType + " " + event + " " + value)
	}
	
	def sendTapEvents(x:Int, y:Int):Unit = {
		val EV_ABS = 0x0003
		val EV_SYN = 0x0000
		val ABS_MT_POSITION_X = 0x0035
		val ABS_MT_POSITION_Y = 0x0036
		val ABS_MT_TRACKING_ID = 0x0039
		val SYN_REPORT = 0x0000
		sendEvent(EV_ABS, ABS_MT_TRACKING_ID, 0x00000001)
		sendEvent(EV_ABS, ABS_MT_POSITION_X, x)
		sendEvent(EV_ABS, ABS_MT_POSITION_Y, y)
		sendEvent(EV_SYN, SYN_REPORT, 0)
		sendEvent(EV_ABS, ABS_MT_TRACKING_ID, 0xffffffff)
		sendEvent(EV_SYN, SYN_REPORT, 0)
		sendEvent(EV_SYN, SYN_REPORT, 0xcccccccc)
	}
}