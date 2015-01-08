package net.onthewings.bezelcursor;

import android.content.*;
import android.content.res.Configuration.*;
import android.graphics.*;
import android.view.*;
import android.content.Context;
import android.view.ViewGroup.ViewGroup_LayoutParams.*;
import android.view.WindowManager.WindowManager_LayoutParams.*;
import net.onthewings.bezelcursor.Utils.*;

using Std;

enum HotspotViewSide {
	Left;
	Right;
}

class HotspotViewBroadcastReceiver extends BroadcastReceiver {
	var view:HotspotView;

	public function new(view:HotspotView):Void {
		super();
		this.view = view;
	}

	@:overload override function onReceive(context:Context, myIntent:Intent):Void {
		if (myIntent.getAction() == Intent.ACTION_CONFIGURATION_CHANGED) {
			var wm:WindowManager = view.service.getSystemService(Context.WINDOW_SERVICE);
			wm.removeView(view);
			view.addToWindow();
		}
	}
}

@:nativeGen
class HotspotView extends View {
	public var side(default, null):HotspotViewSide;
	public var service(default, null):BezelCursor;
	var width = 25;
	var height = 10;
	var paint = new Paint();
	var down_position = new PointF();
	var current_position = new PointF();
	var broadcastReceiver:HotspotViewBroadcastReceiver;
	var filter:IntentFilter;

	public function new(service:BezelCursor, side:HotspotViewSide):Void {
		super(service);
		
		this.service = service;

		paint.setStyle(FILL);
		paint.setColor(Color.WHITE);
		paint.setAlpha(10);

		this.side = side;

		filter = new IntentFilter();
		filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
		broadcastReceiver = new HotspotViewBroadcastReceiver(this);

		addToWindow();
	}

	@:overload override public function onAttachedToWindow():Void {
		super.onAttachedToWindow();
		service.registerReceiver(broadcastReceiver, filter);
	}

	@:overload override public function onDetachedFromWindow():Void {
		cancel();
		service.unregisterReceiver(broadcastReceiver);
		super.onDetachedFromWindow();
	}

	public function addToWindow():Void {
		var wm:WindowManager = service.getSystemService(Context.WINDOW_SERVICE);
		var layoutParams = new WindowManager.WindowManager_LayoutParams(
			width,
			(wm.getDefaultDisplay().getHeight() * 0.5).int(),
			TYPE_SYSTEM_ALERT,
			FLAG_NOT_FOCUSABLE | FLAG_LAYOUT_IN_SCREEN,
			PixelFormat.RGBA_8888
		);

		switch (side) {
			case Left:
				layoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
			case Right:
				layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
		}

		wm.addView(this, layoutParams);
	}
	
	@:overload override function onDraw(canvas:Canvas):Void {
		canvas.drawRect(0, 0, width, getHeight(), paint);
	}
	
	@:overload override function onTouchEvent(evt:MotionEvent):Bool {
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
				cancel();
			case _:
				//log("event action " + evt.getAction())
		}
		service.mView.invalidate();
		return false;
	}

	public function cancel():Void {
		service.mView.init_touch_position = null;
		service.mView.current_touch_position = null;
		service.mView.cursor_position = null;
		setVisibility(View.VISIBLE);
	}
}
