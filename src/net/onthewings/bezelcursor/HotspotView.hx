package net.onthewings.bezelcursor;

import android.graphics.*;
import android.view.*;
import java.util.LinkedList;
import net.onthewings.bezelcursor.Utils.*;
import android.content.Context;
import android.view.ViewGroup.ViewGroup_LayoutParams.*;
import android.view.WindowManager.WindowManager_LayoutParams.*;

@:nativeGen
class HotspotView extends View {	
	var service:BezelCursor;
	var width = 25;
	var height = 10;
	var paint = new Paint();
	var down_position = new PointF();
	var current_position = new PointF();

	public function new(service:BezelCursor):Void {
		super(service);
		
		this.service = service;

		paint.setStyle(untyped __java__("android.graphics.Paint.Style.FILL"));
		paint.setColor(Color.WHITE);
		paint.setAlpha(10);

		layoutParams = new WindowManager.WindowManager_LayoutParams(
			width,
			WRAP_CONTENT,
			TYPE_SYSTEM_ALERT,
			FLAG_NOT_FOCUSABLE | FLAG_LAYOUT_IN_SCREEN,
			PixelFormat.RGBA_8888
		);

		setLayoutParams(layoutParams);
	}
	
	private var layoutParams:WindowManager.WindowManager_LayoutParams;

	function getService():BezelCursor {
		return cast getContext();
	}
	
    @:overload function onDraw(canvas:Canvas):Void {
        canvas.drawRect(0, 0, width, getHeight(), paint);
    }
	
	@:overload function onTouchEvent(evt:MotionEvent):Bool {
		//log("onTouchEvent " + evt.getRawX() + "," + evt.getRawY() + "," + evt.getAction())
		
		function get_cursor_position(x:Float, y:Float):PointF {
			var moveScale = 3;
			return new PointF((evt.getRawX() - down_position.x) * moveScale + down_position.x, (evt.getRawY() - down_position.y) * moveScale + down_position.y);
		}
		
		switch (evt.getAction()) {
			case MotionEvent.ACTION_DOWN: //down
				setVisibility(View.INVISIBLE);
				down_position.set(evt.getRawX(), evt.getRawY());
				current_position.set(evt.getRawX(), evt.getRawY());
				service.mView.init_touch_position = down_position;
				service.mView.current_touch_position = current_position;
				service.mView.cursor_position = new PointF(evt.getRawX(), evt.getRawY());
				//service.touchDevice.sendBeginHoverEvents(service.mView.cursor_position.x / displaySize.x, service.mView.cursor_position.y / displaySize.y);
			case MotionEvent.ACTION_UP: //up
				service.mView.cursor_position.set(get_cursor_position(evt.getRawX(), evt.getRawY()));
				service.touchDevice.sendTapEvents(service.mView.cursor_position.x, service.mView.cursor_position.y);
				service.mView.init_touch_position = null;
				service.mView.current_touch_position = null;
				service.mView.cursor_position = null;
				setVisibility(View.VISIBLE);
			case MotionEvent.ACTION_MOVE: //move
				current_position.set(evt.getRawX(), evt.getRawY());
				service.mView.current_touch_position.set(evt.getRawX(), evt.getRawY());
				service.mView.cursor_position.set(get_cursor_position(evt.getRawX(), evt.getRawY()));
				//service.touchDevice.sendHoverEvents(service.mView.cursor_position.x / displaySize.x, service.mView.cursor_position.y / displaySize.y);
			case MotionEvent.ACTION_CANCEL:
				service.mView.init_touch_position = null;
				service.mView.current_touch_position = null;
				service.mView.cursor_position = null;
				setVisibility(View.VISIBLE);
			case _:
				//log("event action " + evt.getAction())
		}
		service.mView.invalidate();
		return false;
	}
}
