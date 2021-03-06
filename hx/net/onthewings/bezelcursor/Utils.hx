package net.onthewings.bezelcursor;

import android.util.Log;

class Utils {
	static public function log(msg:String, ?pos:haxe.PosInfos):Void {
		Log.d("BezelCursor", '${pos.fileName} ${pos.lineNumber}: $msg');
	}
	
	static public function map(value:Float, min1:Float, max1:Float, min2:Float, max2:Float):Float {
		return min2 + (max2 - min2) * ((value - min1) / (max1 - min1));
	}
}