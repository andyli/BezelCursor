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
import net.onthewings.touchservice.Utils._

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
    

    override def onServiceConnected() = {
    	log("onServiceConnected")
    	
//    	for (InputDevice idev:events.m_Devs) {
//	    	String path = idev.getPath();
//	    	if (path.charAt(path.length() - 1) != '2')
//	    		continue;
//	    	
//	    	log("dev: " + idev.getId() + " " + idev.getName());
//	    	/*
//	    	Events.intSendEvent(idev.m_nId, 0003, 0x0039, 0x00000058);
//	    	Events.intSendEvent(idev.m_nId, 0003, 0x0035, 0x00000254);
//	    	Events.intSendEvent(idev.m_nId, 0003, 0x0036, 0x0000020b);
//	    	Events.intSendEvent(idev.m_nId, 0003, 0x003a, 0x0000004f);
//	    	Events.intSendEvent(idev.m_nId, 0003, 0x0031, 0x00000004);
//	    	Events.intSendEvent(idev.m_nId, 0000, 0x0000, 0x00000000);
//	    	Events.intSendEvent(idev.m_nId, 0003, 0x0035, 0x00000252);
//	    	Events.intSendEvent(idev.m_nId, 0003, 0x003a, 0x00000050);
//	    	Events.intSendEvent(idev.m_nId, 0003, 0x0030, 0x00000005);
//	    	Events.intSendEvent(idev.m_nId, 0003, 0x0034, 0x00000001);
//	    	Events.intSendEvent(idev.m_nId, 0000, 0x0000, 0x00000000);
//	    	Events.intSendEvent(idev.m_nId, 0003, 0x0039, 0xffffffff);
//	    	Events.intSendEvent(idev.m_nId, 0000, 0x0000, 0x00000000);
//	    	*/
//	    	log("dev: " + idev.getId() + " " + idev.getName() + " sent!!!");
//    	}
    }
    
    override def onCreate() = {
        super.onCreate()
        
        log("onCreate")

        
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
        val wm = getSystemService(Context.WINDOW_SERVICE).asInstanceOf[WindowManager]
        
        wm.addView(mView, params)
        log("addView")
        
//        events.intEnableDebug(1);
//        int eInit = events.Init();
//        log("event.Init() " + eInit);
//        
//
//    	
//    	for (InputDevice idev:events.m_Devs) {
//	    	String path = idev.getPath();
//	    	if (path.charAt(path.length() - 1) != '2')
//	    		continue;
//	    	
//	    	idev.Open(true);
//	    	log("dev: " + idev.getId() + " " + idev.getName() + " " + idev.getOpen());
//    	}
    }
    
    override def onDestroy() = {
    	val wm = getSystemService(Context.WINDOW_SERVICE).asInstanceOf[WindowManager]
        wm.removeView(mView)
        
        
//        for (InputDevice idev:events.m_Devs) {
//	    	String path = idev.getPath();
//	    	if (path.charAt(path.length() - 1) != '2')
//	    		continue;
//	    	
//	    	idev.Close();
//    	}
//        
//        events.Release();
    }
}
