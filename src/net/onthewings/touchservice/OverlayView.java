package net.onthewings.touchservice;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class OverlayView extends View {
	Paint paint = new Paint();

	protected WindowManager.LayoutParams layoutParams;

	public OverlayView(TouchService service) {
		super(service);
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(10);
	}

	public TouchService getService() {
		return (TouchService) getContext();
	}
	
	@Override
    public void onDraw(Canvas canvas) {
        canvas.drawLine(0, 0, getWidth(), getHeight(), paint);
        canvas.drawRect(0, 0, 200, 200, paint);
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent evt) {
		Log.d("Testtesttest", "onTouchEvent " + evt.getX() + "," + evt.getY());
		return false;
	}
	
	@Override
	public boolean onDragEvent(DragEvent evt) {
		Log.d("Testtesttest", "onDragEvent");
		return true;
	}
}
