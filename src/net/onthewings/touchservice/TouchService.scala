package net.onthewings.touchservice;

import android.accessibilityservice.AccessibilityService
import android.graphics.PixelFormat
import android.graphics.Rect
import android.view.Gravity
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.ViewGroup
import android.content.Context
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._
import java.util.List
import java.util.LinkedList
import java.util.ArrayList
import Utils._

class TouchService extends AccessibilityService {

    lazy val mView = new OverlayView(this)
    lazy val hotspotView_l:HotspotView = new HotspotView(this)
    lazy val hotspotView_r:HotspotView = new HotspotView(this)    
    lazy val touchDevice = new TouchInputDevice(
		InputDevice.getTouchDevicePath(),
		getSystemService(Context.WINDOW_SERVICE).asInstanceOf[WindowManager].getDefaultDisplay()
    )
	
	final def getBounds(src:AccessibilityNodeInfo, results:List[Rect]):Unit = {
    	val bound = new Rect()
    	
    	src.getBoundsInScreen(bound)
    	results.add(bound)
    	
    	val childCount = src.getChildCount()
    	var c = 0
    	while (c < childCount) {
    		val child = src.getChild(c)
    		if (child != null) {
    			getBounds(child, results)
    		} /*else {
    			log("get child is null!!!")
    		}*/
    		c += 1
    	}
    	
    	src.recycle()
    }

    override def onAccessibilityEvent(event:AccessibilityEvent) = {
    	//log("AccessibilityEvent " + AccessibilityEvent.eventTypeToString(event.getEventType()))
    	
//    	val src = event.getSource()
//    	if (src != null){
//        	src.getBoundsInScreen(mView.current_bound)
//        	
//        	mView.bounds.clear()
//    		var _root = src
//    		var _temp:AccessibilityNodeInfo = _root.getParent()
//    		while (_temp != null) {
//    			if (_root != src) _root.recycle()
//    			_root = _temp
//    			_temp = _root.getParent()
//    		}
//        	getBounds(_root, mView.bounds)
//    		
//        	mView.invalidate()
//    	}
    }

    override def onInterrupt() = {
    	
    }

    override def onServiceConnected() = {
    	log("onServiceConnected")
    }
    
    override def onCreate() = {
        super.onCreate()
        
        log("onCreate")

        val wm = getSystemService(Context.WINDOW_SERVICE).asInstanceOf[WindowManager]
        
        val params = new WindowManager.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
			
			WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
			|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
			|WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
			|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
			//|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
			,
			PixelFormat.RGBA_8888
        )
        params.gravity = Gravity.FILL
        wm.addView(mView, params)
        
        val params_hotspot_l = hotspotView_l.getLayoutParams().asInstanceOf[WindowManager.LayoutParams]
        params_hotspot_l.gravity = Gravity.LEFT
        wm.addView(hotspotView_l, params_hotspot_l)
        
        val params_hotspot_r = hotspotView_r.getLayoutParams().asInstanceOf[WindowManager.LayoutParams]
        params_hotspot_r.gravity = Gravity.RIGHT
        wm.addView(hotspotView_r, params_hotspot_r)
        
        
        log("addView")
        
        val touchDeviceOpenSuccess = touchDevice.open()
        log("touchDeviceOpenSuccess " + touchDeviceOpenSuccess)
    }
    
    override def onDestroy() = {
    	val wm = getSystemService(Context.WINDOW_SERVICE).asInstanceOf[WindowManager]
        wm.removeView(mView)
        wm.removeView(hotspotView_l)
        wm.removeView(hotspotView_r)
    }
}
