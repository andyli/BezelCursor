package net.onthewings.bezelcursor.tween;

import aurelienribon.tweenengine.TweenAccessor;
import android.graphics.Paint;
import aurelienribon.tweenengine.Tween;
import java.*;

using Std;

@:enum abstract PaintProperty(Int) to Int {
    var alpha = 0;
}

class PaintAccessor implements TweenAccessor<Paint> {
    public function new():Void {

    }

    public function getValues(target:Paint, tweenType:Int, returnValues:NativeArray<Single>):Int {
        switch (tweenType) {
            case PaintProperty.alpha:
            	returnValues[0] = target.getAlpha();
            	return 1;
        }
        
        return -1;
    }
    
    public function setValues(target:Paint, tweenType:Int, newValues:NativeArray<Single>):Void {
        switch(tweenType) {
            case PaintProperty.alpha:
            	target.setAlpha(newValues[0].int());
        }
    }
    
    static public function register():Void {
        if (Tween.getRegisteredAccessor(untyped Paint) == null) {
            Tween.registerAccessor(untyped Paint, new PaintAccessor());
        }
    }
}