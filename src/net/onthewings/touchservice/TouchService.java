package net.onthewings.touchservice;

import android.accessibilityservice.AccessibilityService;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

public class TouchService extends AccessibilityService {

    private OverlayView mView;
    
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
			//|WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
			|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
			|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
			,
			PixelFormat.RGBA_8888
        );
        params.gravity = Gravity.FILL;
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        
        wm.addView(mView, params);
        Log.d("Testtesttest", "addView");
    }
    
    @Override
    public void onDestroy() {
    	WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.removeView(mView);
    }
}
