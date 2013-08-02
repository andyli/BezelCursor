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
	
	final def getBounds(src:AccessibilityNodeInfo, results:List[Rect]):Unit = {
    	val bound = new Rect()
    	
    	src.getBoundsInScreen(bound)
    	results.add(new Rect(bound))
    	
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

    var mView:OverlayView = null
    var hotspotView_l:HotspotView = null
    var hotspotView_r:HotspotView = null
    //private Events events = new Events();

    override def onAccessibilityEvent(event:AccessibilityEvent) = {
    	log("AccessibilityEvent " + AccessibilityEvent.eventTypeToString(event.getEventType()))
    	
    	val src = event.getSource()
    	if (src != null){
        	src.getBoundsInScreen(mView.current_bound)
        	
        	mView.bounds.clear()
    		var _root = src
    		var _temp:AccessibilityNodeInfo = _root.getParent()
    		while (_temp != null) {
    			if (_root != src) _root.recycle()
    			_root = _temp
    			_temp = _root.getParent()
    		}
        	getBounds(_root, mView.bounds)
    		
        	mView.invalidate()
    	}
    }

    override def onInterrupt() = {
    	
    }
    
    val touchDevice = new InputDevice(InputDevice.getTouchDevicePath())

    override def onServiceConnected() = {
    	log("onServiceConnected")
    }
    
    override def onCreate() = {
        super.onCreate()
        
        log("onCreate")

        val wm = getSystemService(Context.WINDOW_SERVICE).asInstanceOf[WindowManager]
        
        mView = new OverlayView(this);
        
        val params = new WindowManager.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
			
			WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
			|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
			|WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
			|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
			|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
			,
			PixelFormat.RGBA_8888
        )
        params.gravity = Gravity.FILL
        
        wm.addView(mView, params)
        
        
        
        val params_hotspot_l = new WindowManager.LayoutParams(
			10,
			ViewGroup.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
			
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
			|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
			,
			PixelFormat.RGBA_8888
        )
        params_hotspot_l.gravity = Gravity.LEFT
        
        hotspotView_l = new HotspotView(this)
        wm.addView(hotspotView_l, params_hotspot_l)
        
        val params_hotspot_r = new WindowManager.LayoutParams(
			10,
			ViewGroup.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
			
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
			|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
			,
			PixelFormat.RGBA_8888
        )
        params_hotspot_r.gravity = Gravity.RIGHT
        
        hotspotView_r = new HotspotView(this)
        wm.addView(hotspotView_r, params_hotspot_r)
        
        
        log("addView")
    }
    
    override def onDestroy() = {
    	val wm = getSystemService(Context.WINDOW_SERVICE).asInstanceOf[WindowManager]
        wm.removeView(mView)
    }
}
