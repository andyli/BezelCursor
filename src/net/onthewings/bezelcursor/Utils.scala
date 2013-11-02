package net.onthewings.bezelcursor

import android.util.Log

object Utils {
	def log(msg:String) = {
		Log.d("Testtesttest", msg)
	}
	
	def map(value:Double, min1:Double, max1:Double, min2:Double, max2:Double):Double =  {
		return min2 + (max2 - min2) * ((value - min1) / (max1 - min1));
	}
}