package net.onthewings.touchservice

import net.onthewings.touchservice.Utils._

object AndroidEvents {
	System.loadLibrary("TouchService")
	
	@native def getDebugEnabled():Boolean
	@native def setDebugEnabled(enable:Boolean):Boolean

	@native def ScanFiles():Int // return number of devs
	@native def OpenDev(devid:Int):Int
	@native def RemoveDev(devid:Int):Int
	@native def getDevPath(devid:Int):String
	@native def getDevName(devid:Int):String
	@native def PollDev(devid:Int):Int
	@native def getType():Int
	@native def getCode():Int
	@native def getValue():Int
	// injector:
	@native def intSendEvent(devid:Int, _type:Int, code:Int, value:Int):Int
}

class AndroidEvents {
	
}