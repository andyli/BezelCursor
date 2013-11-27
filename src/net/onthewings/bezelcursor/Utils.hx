package net.onthewings.bezelcursor;

import android.util.Log;

class Utils {
	static public function log(msg:String):Void {
		Log.d("Testtesttest", msg);
	}
	
	static public function map(value:Float, min1:Float, max1:Float, min2:Float, max2:Float):Float {
		return min2 + (max2 - min2) * ((value - min1) / (max1 - min1));
	}

	static public function lastIndexOf<T>(inArray:Array<T>, match:T->Bool, ?fromIndex:Int):Int {
		var i = fromIndex == null ? inArray.length : Std.int(Math.min(inArray.length, fromIndex+1));
		
		while (--i > 0) {
			if (match(inArray[i])) return i;
		}
		return -1;
	}

	@:functionCode('return android.graphics.Paint.Style.FILL;')
	static public function FILL():android.graphics.Paint.Paint_Style return null;

	@:functionCode('return android.graphics.Paint.Style.STROKE;')
	static public function STROKE():android.graphics.Paint.Paint_Style return null;

	@:functionCode('return android.graphics.Shader.TileMode.CLAMP;')
	static public function CLAMP():android.graphics.Shader.Shader_TileMode return null;
}