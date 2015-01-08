package net.onthewings.bezelcursor;

import android.graphics.*;
import android.view.View;
import net.onthewings.bezelcursor.Utils.*;
import java.lang.System;
import tweenx909.*;

using Std;
using haxe.Int64;

@:nativeGen
class OverlayView extends View {
	var service:BezelCursor;
	var current_bound = new Rect();
	
	var cursor_path = new Path();
	
	var current_bound_paint = new Paint();
	var bounds_paint = new Paint();
	var clickable_bounds_paint = new Paint();
	var cursor_paint = new Paint();
	var cursor_point_paint = new Paint();
	var line_paint = new Paint();

	var cursor_paint_tween:TweenX;
	var cursor_point_paint_tween:TweenX;

	public function new(service:BezelCursor):Void {
		super(service);

		this.service = service;

		bounds_paint.setColor(Color.WHITE);
		bounds_paint.setAlpha(150);
		bounds_paint.setStrokeWidth(1);
		bounds_paint.setStyle(FILL);

		clickable_bounds_paint.setColor(Color.YELLOW);
		clickable_bounds_paint.setStrokeWidth(2);
		clickable_bounds_paint.setStyle(STROKE);
		
		current_bound_paint.setColor(Color.GREEN);
		current_bound_paint.setStrokeWidth(2);
		current_bound_paint.setStyle(STROKE);

		cursor_paint.setColor(Color.GREEN);
		cursor_paint.setAntiAlias(true);
		cursor_paint.setStrokeWidth(4);
		cursor_paint.setStyle(STROKE);

		cursor_point_paint.setColor(Color.GREEN);
		cursor_point_paint.setAntiAlias(true);
		cursor_point_paint.setStrokeWidth(8);
		cursor_point_paint.setStyle(STROKE);

		line_paint.setColor(Color.GREEN);
		line_paint.setAlpha(150);
		line_paint.setAntiAlias(true);
		line_paint.setStyle(FILL);

		TweenX.updateMode = MANUAL;

		var prop:{alpha:Float} = {
			alpha: cursor_paint.getAlpha()
		};
		cursor_paint_tween = TweenX
			.to(prop, {
				alpha: 100.0
			}, 0.6)
			.onUpdate(setCursorPaintAlpha.bind(cursor_paint, prop))
			.repeat(0)
			.yoyo();

		var prop:{alpha:Float} = {
			alpha: cursor_point_paint.getAlpha()
		};
		cursor_point_paint_tween = TweenX
			.to(prop, {
				alpha:100.0
			}, 0.6)
			.onUpdate(setCursorPaintAlpha.bind(cursor_point_paint, prop))
			.delay(0.1)
			.repeat(0)
			.yoyo();
	}

	function setCursorPaintAlpha(p:Paint, _prop:{alpha:Float}):Void {
		p.setAlpha(Std.int(_prop.alpha));
	}

	
	
	public var init_touch_position:PointF = null;
	public var current_touch_position:PointF = null;
	public var cursor_position(default, set):PointF = null;
	function set_cursor_position(v) {
		return cursor_position = v;
	}
	
	var lastMillis:Int64 = Int64.ofInt(-1);
	@:overload override function onDraw(canvas:Canvas):Void {
		var currentMillis = System.currentTimeMillis();
		if (lastMillis.isNeg()) {
			lastMillis = currentMillis;
		}
		
		if (cursor_position != null) {
			TweenX.manualUpdate(currentMillis.sub(lastMillis).toInt() / 1000);

			var bounds = [];//TODO getService().getBounds();
			
			for (bound in bounds) {
				if (bound._2) {
					canvas.drawRect(bound._1, clickable_bounds_paint);
				} else {
					canvas.drawRect(bound._1, bounds_paint);
				}
			}
			canvas.drawRect(current_bound, current_bound_paint);
			
			var drag_vec = new PointF(cursor_position.x - init_touch_position.x, cursor_position.y - init_touch_position.y);
			var drag_len = drag_vec.length();
			var path_start_radius = map(drag_len, 0, 4000, 5, 150).int();
			var path_end_radius = 5;
			var control_ratio = map(drag_len, 0, 4000, 0, 1);
			var control_pt = new PointF((init_touch_position.x + drag_vec.x * control_ratio), (init_touch_position.y + drag_vec.y * control_ratio));
			
			cursor_path.reset();
			cursor_path.moveTo(init_touch_position.x, init_touch_position.y - path_start_radius);
			cursor_path.quadTo(control_pt.x, control_pt.y, cursor_position.x, cursor_position.y - path_end_radius);
			cursor_path.lineTo(cursor_position.x, cursor_position.y + path_end_radius);
			cursor_path.quadTo(control_pt.x, control_pt.y, init_touch_position.x, init_touch_position.y + path_start_radius);
			cursor_path.close();
			canvas.drawPath(cursor_path, line_paint);
			
			var cursor_RadialGradient = new RadialGradient(cursor_position.x, cursor_position.y, 50, 0x00FFFFFF, 0x3333FF33, CLAMP);
			var cursor_inner_paint = new Paint();
			cursor_inner_paint.setDither(true);
			cursor_inner_paint.setShader(cursor_RadialGradient);
			canvas.drawCircle(cursor_position.x, cursor_position.y, 40, cursor_inner_paint);
			canvas.drawCircle(cursor_position.x, cursor_position.y, 40, cursor_paint);
			canvas.drawCircle(cursor_position.x, cursor_position.y, 4, cursor_point_paint);
		}
		
		invalidate();
		lastMillis = currentMillis;
	}
}
