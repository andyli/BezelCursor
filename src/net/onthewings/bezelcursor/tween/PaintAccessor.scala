package net.onthewings.bezelcursor.tween

import aurelienribon.tweenengine.TweenAccessor
import android.graphics.Paint
import aurelienribon.tweenengine.Tween

import PaintAccessor.PaintProperty

class PaintAccessor extends TweenAccessor[Paint] {
    def getValues(target:Paint, tweenType:Int, returnValues:Array[Float]):Int =  {
        PaintProperty(tweenType) match {
            case alpha =>
            	returnValues(0) = target.getAlpha()
            	return 1
        }
        
        return -1
    }
    
    def setValues(target:Paint, tweenType:Int, newValues:Array[Float]):Unit = {
        PaintProperty(tweenType) match {
            case alpha =>
            	target.setAlpha(newValues(0).toInt)
        }
    }
}

object PaintAccessor {
	object PaintProperty extends Enumeration {
		val alpha = Value
	}
	
	def register() = {
		if (Tween.getRegisteredAccessor(classOf[Paint]) == null) {
			Tween.registerAccessor(classOf[Paint], new PaintAccessor())
		}
	}
}