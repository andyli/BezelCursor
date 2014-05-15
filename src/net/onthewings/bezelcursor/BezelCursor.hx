package net.onthewings.bezelcursor;

import net.onthewings.bezelcursor.Utils.*;
import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.view.*;
import android.view.accessibility.*;
import android.widget.Toast;
import android.view.ViewGroup.ViewGroup_LayoutParams.*;
import android.view.WindowManager.WindowManager_LayoutParams.*;
import com.crittercism.app.Crittercism;

class BezelCursor extends AccessibilityService {
	var inited:Bool = false;
    public var mView(get, null):OverlayView;
    function get_mView():OverlayView {
        return mView != null ? mView : mView = new OverlayView(this);
    }

    public var hotspotView_l:HotspotView;
    public var hotspotView_r:HotspotView;

    public var touchDevice(get, null):TouchInputDevice;
    function get_touchDevice():TouchInputDevice {
        return touchDevice != null ? touchDevice : touchDevice = new TouchInputDevice(
    		net.onthewings.bezelcursor.InputDevice.getTouchDevicePath(),
    		cast(getSystemService(Context.WINDOW_SERVICE), WindowManager).getDefaultDisplay()
        );
    }
    
    @:overload function onAccessibilityEvent(event:AccessibilityEvent):Void {
//    	log("AccessibilityEvent " + AccessibilityEvent.eventTypeToString(event.getEventType()))
//    	
//    	event.getEventType() match {
//    		case AccessibilityEvent.TYPE_VIEW_FOCUSED
//    		|    AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
//    		|    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
//    		|    AccessibilityEvent.TYPE_VIEW_SCROLLED
//    		=>
//    			val src = event.getSource()
//		    	if (src != null){
//		        	src.getBoundsInScreen(mView.current_bound)
//		        	
//			    	if (task != null) {
//			    		task.cancel(true)
//			    	}
//		        	task = new FutureTask(new OnAccessibilityEvent(src))
//		        	task.run()
//		        	//mView.invalidate()
//		    	}
//    		case _ =>
//    	}
    	
    }

    @:overload function onInterrupt():Void {
    	
    }

    @:overload function onServiceConnected():Void {
    	log("onServiceConnected");
    }
    
    @:overload function onCreate():Void {
        super.onCreate();
        
        init();
    }
    
    function init():Void {
        Crittercism.initialize(this, "5065d991067e7c109c00000b");

    	if (!Shell.isSuAvailable()) {
        	log("su not available");
        	
        	Toast.makeText(getApplicationContext(), "Failed to get root access...\nPlease restart BezelCursor...", Toast.LENGTH_LONG).show();
        	
        	return;
        }

        var wm:WindowManager = getSystemService(Context.WINDOW_SERVICE);
        
        var params = new WindowManager.WindowManager_LayoutParams(
			WRAP_CONTENT, 
			WRAP_CONTENT, 
			TYPE_SYSTEM_OVERLAY, 
			
			FLAG_NOT_TOUCH_MODAL
			|FLAG_NOT_FOCUSABLE
			|FLAG_NOT_TOUCHABLE
			|FLAG_LAYOUT_IN_SCREEN 
			//|FLAG_WATCH_OUTSIDE_TOUCH
			,
			PixelFormat.RGBA_8888
        );
        params.gravity = Gravity.FILL;
        wm.addView(mView, params);
        hotspotView_l = new HotspotView(this, Left);
        hotspotView_r = new HotspotView(this, Right);
        
        log("addView");
        
        var touchDeviceOpenSuccess = touchDevice.open();
        log("touchDeviceOpenSuccess " + touchDeviceOpenSuccess);
        
        inited = true;
    }
    
    @:overload function onDestroy():Void {
    	if (inited) {
    		var wm:WindowManager = getSystemService(Context.WINDOW_SERVICE);
	    	wm.removeView(mView);
	    	wm.removeView(hotspotView_l);
	    	wm.removeView(hotspotView_r);
    	}
    }
}
