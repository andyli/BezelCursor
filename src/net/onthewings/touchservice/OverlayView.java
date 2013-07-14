package net.onthewings.touchservice;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class OverlayView extends View {
	Rect current_bound = new Rect();
	public List<Rect> bounds = new LinkedList<Rect>();
	Paint current_paint = new Paint();
	Paint paint = new Paint();

	protected WindowManager.LayoutParams layoutParams;

	public OverlayView(TouchService service) {
		super(service);
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(2);
		paint.setStyle(Paint.Style.STROKE);
		
		current_paint.setColor(Color.GREEN);
		current_paint.setStrokeWidth(2);
		current_paint.setStyle(Paint.Style.STROKE);
		//current_paint.setAlpha(100);
		//current_paint.setStyle(Paint.Style.FILL);
	}

	public TouchService getService() {
		return (TouchService) getContext();
	}
	
	@Override
    public void onDraw(Canvas canvas) {
        //canvas.drawLine(0, 0, getWidth(), getHeight(), paint);
        
        for (Rect bound:bounds) {
        	canvas.drawRect(bound, paint);
        }
        canvas.drawRect(current_bound, current_paint);
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent evt) {
		Log.d("Testtesttest", "onTouchEvent " + evt.getX() + "," + evt.getY() + "," + evt.getAction());
		return false;
	}
	
	@Override
	public boolean onDragEvent(DragEvent evt) {
		Log.d("Testtesttest", "onDragEvent");
		return true;
	}
}
