package net.onthewings.touchservice;

import net.pocketmagic.android.eventinjector.Events;
import net.pocketmagic.android.eventinjector.Events.InputDevice;
import android.accessibilityservice.AccessibilityService;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

public class TouchService extends AccessibilityService {

    private OverlayView mView;
    private Events events = new Events();
    
    void log(String msg) {
    	Log.d("Testtesttest", msg);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    	log("AccessibilityEvent " + AccessibilityEvent.eventTypeToString(event.getEventType()));
    	mView.invalidate();
    }

    @Override
    public void onInterrupt() {
    	
    }
    

    @Override
    public void onServiceConnected() {
    	log("onServiceConnected");
    	
    	for (InputDevice idev:events.m_Devs) {
	    	String path = idev.getPath();
	    	if (path.charAt(path.length() - 1) != '2')
	    		continue;
	    	
	    	log("dev: " + idev.getId() + " " + idev.getName());
	    	Events.intSendEvent(idev.m_nId, 0003, 0x0039, 0x00000058);
	    	Events.intSendEvent(idev.m_nId, 0003, 0x0035, 0x00000254);
	    	Events.intSendEvent(idev.m_nId, 0003, 0x0036, 0x0000020b);
	    	Events.intSendEvent(idev.m_nId, 0003, 0x003a, 0x0000004f);
	    	Events.intSendEvent(idev.m_nId, 0003, 0x0031, 0x00000004);
	    	Events.intSendEvent(idev.m_nId, 0000, 0x0000, 0x00000000);
	    	Events.intSendEvent(idev.m_nId, 0003, 0x0035, 0x00000252);
	    	Events.intSendEvent(idev.m_nId, 0003, 0x003a, 0x00000050);
	    	Events.intSendEvent(idev.m_nId, 0003, 0x0030, 0x00000005);
	    	Events.intSendEvent(idev.m_nId, 0003, 0x0034, 0x00000001);
	    	Events.intSendEvent(idev.m_nId, 0000, 0x0000, 0x00000000);
	    	Events.intSendEvent(idev.m_nId, 0003, 0x0039, 0xffffffff);
	    	Events.intSendEvent(idev.m_nId, 0000, 0x0000, 0x00000000);
	    	log("dev: " + idev.getId() + " " + idev.getName() + " sent!!!");
    	}
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        Log.d("Testtesttest", "onCreate"); 

        
        mView = new OverlayView(this);
        
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
			
			WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
			|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
			|WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
			|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
			|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
			,
			PixelFormat.RGBA_8888
        );
        params.gravity = Gravity.FILL;
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        
        wm.addView(mView, params);
        Log.d("Testtesttest", "addView");
        
        events.intEnableDebug(1);
        int eInit = events.Init();
        log("event.Init() " + eInit);
        

    	
    	for (InputDevice idev:events.m_Devs) {
	    	String path = idev.getPath();
	    	if (path.charAt(path.length() - 1) != '2')
	    		continue;
	    	
	    	idev.Open(true);
	    	log("dev: " + idev.getId() + " " + idev.getName() + " " + idev.getOpen());
    	}
    }
    
    @Override
    public void onDestroy() {
    	WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.removeView(mView);
        
        for (InputDevice idev:events.m_Devs) {
	    	String path = idev.getPath();
	    	if (path.charAt(path.length() - 1) != '2')
	    		continue;
	    	
	    	idev.Close();
    	}
        
        events.Release();
    }
}
