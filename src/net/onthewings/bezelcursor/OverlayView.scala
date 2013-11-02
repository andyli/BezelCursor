package net.onthewings.bezelcursor;

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.view.View
import aurelienribon.tweenengine.Tween
import aurelienribon.tweenengine.TweenManager
import net.onthewings.bezelcursor.tween.PaintAccessor
import net.onthewings.bezelcursor.tween.PaintAccessor.PaintProperty
import Utils.map
import android.graphics.RadialGradient

class OverlayView(service:BezelCursor) extends View(service) {
	val current_bound = new Rect()
	
	val cursor_path = new Path()
	
	val current_bound_paint = new Paint()
	val bounds_paint = new Paint()
	val clickable_bounds_paint = new Paint()
	val cursor_paint = new Paint()
	val cursor_point_paint = new Paint()
	val line_paint = new Paint()

	bounds_paint.setColor(Color.WHITE)
	bounds_paint.setAlpha(150)
	bounds_paint.setStrokeWidth(1)
	bounds_paint.setStyle(Paint.Style.STROKE)

	clickable_bounds_paint.setColor(Color.YELLOW)
	clickable_bounds_paint.setStrokeWidth(2)
	clickable_bounds_paint.setStyle(Paint.Style.STROKE)
	
	current_bound_paint.setColor(Color.GREEN)
	current_bound_paint.setStrokeWidth(2)
	current_bound_paint.setStyle(Paint.Style.STROKE)

	cursor_paint.setColor(Color.GREEN)
	cursor_paint.setAntiAlias(true)
	cursor_paint.setStrokeWidth(4)
	cursor_paint.setStyle(Paint.Style.STROKE)

	cursor_point_paint.setColor(Color.GREEN)
	cursor_point_paint.setAntiAlias(true)
	cursor_point_paint.setStrokeWidth(8)
	cursor_point_paint.setStyle(Paint.Style.STROKE)

	line_paint.setColor(Color.GREEN)
	line_paint.setAlpha(150)
	line_paint.setAntiAlias(true)
	line_paint.setStyle(Paint.Style.FILL)
	
	var init_touch_position:PointF = null
	var current_touch_position:PointF = null
	var cursor_position:PointF = null
	
	val tweenManager = new TweenManager()
	PaintAccessor.register()
	import PaintAccessor.PaintProperty
	
	val flash_tween = Tween
		.to(cursor_paint, PaintProperty.alpha.id, 0.8f)
		.target(100)
		.repeatYoyo(-1, 0)
		.start(tweenManager)
	
	val flash_point_tween = Tween
		.to(cursor_point_paint, PaintProperty.alpha.id, 0.8f)
		.target(100)
		.delay(0.25f)
		.repeatYoyo(-1, 0)
		.start(tweenManager)

	def getService():BezelCursor = {
		return getContext().asInstanceOf[BezelCursor]
	}
	
	var lastMillis:Long = -1
	override def onDraw(canvas:Canvas) = {
		val currentMillis = System.currentTimeMillis()
		if (lastMillis < 0) {
			lastMillis = currentMillis
		}
		tweenManager.update((currentMillis - lastMillis) / 1000f)
		
		if (cursor_position != null) {
			val bounds = getService().getBounds()
			
			for (bound <- bounds) {
				if (bound._2) {
					canvas.drawRect(bound._1, clickable_bounds_paint)
				} else {
					canvas.drawRect(bound._1, bounds_paint)
				}
			}
			canvas.drawRect(current_bound, current_bound_paint)
			
			val drag_vec = new PointF(cursor_position.x - init_touch_position.x, cursor_position.y - init_touch_position.y)
			val drag_len = drag_vec.length()
			val path_start_radius = map(drag_len, 0, 4000, 5, 150).toInt
			val path_end_radius = 5
			val control_ratio = map(drag_len, 0, 4000, 0, 1)
			val control_pt = new PointF((init_touch_position.x + drag_vec.x * control_ratio).toFloat, (init_touch_position.y + drag_vec.y * control_ratio).toFloat)
			
			cursor_path.reset()
			cursor_path.moveTo(init_touch_position.x, init_touch_position.y - path_start_radius)
			cursor_path.quadTo(control_pt.x, control_pt.y, cursor_position.x, cursor_position.y - path_end_radius)
			cursor_path.lineTo(cursor_position.x, cursor_position.y + path_end_radius)
			cursor_path.quadTo(control_pt.x, control_pt.y, init_touch_position.x, init_touch_position.y + path_start_radius)
			cursor_path.close()
			canvas.drawPath(cursor_path, line_paint)
			
			val cursor_RadialGradient = new RadialGradient(cursor_position.x, cursor_position.y, 50, 0x00FFFFFF, 0x3333FF33, android.graphics.Shader.TileMode.CLAMP);
			val cursor_inner_paint = new Paint()
			cursor_inner_paint.setDither(true)
			cursor_inner_paint.setShader(cursor_RadialGradient)
			canvas.drawCircle(cursor_position.x, cursor_position.y, 40, cursor_inner_paint)
			canvas.drawCircle(cursor_position.x, cursor_position.y, 40, cursor_paint)
			canvas.drawCircle(cursor_position.x, cursor_position.y, 4, cursor_point_paint)
		}
		
		invalidate()
		lastMillis = currentMillis
	}
}
