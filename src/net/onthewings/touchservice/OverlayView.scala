package net.onthewings.touchservice;

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import scala.collection.mutable.ListBuffer
import Utils._
import aurelienribon.tweenengine.Tween
import aurelienribon.tweenengine.TweenManager
import net.onthewings.touchservice.tween.PaintAccessor

class OverlayView(service:TouchService) extends View(service) {
	val current_bound = new Rect()
	val current_paint = new Paint()
	val paint = new Paint()
	val cursor_paint = new Paint()
	val line_paint = new Paint()
	val clickable_paint = new Paint()

	paint.setColor(Color.WHITE)
	paint.setAlpha(50)
	paint.setStrokeWidth(1)
	paint.setStyle(Paint.Style.STROKE)

	clickable_paint.setColor(Color.YELLOW)
	clickable_paint.setStrokeWidth(2)
	clickable_paint.setStyle(Paint.Style.STROKE)
	
	current_paint.setColor(Color.GREEN)
	current_paint.setStrokeWidth(2)
	current_paint.setStyle(Paint.Style.STROKE)
	//current_paint.setAlpha(100)
	//current_paint.setStyle(Paint.Style.FILL)

	cursor_paint.setColor(Color.GREEN)
	cursor_paint.setAntiAlias(true)
	cursor_paint.setStrokeWidth(4)
	cursor_paint.setStyle(Paint.Style.STROKE)

	line_paint.setColor(Color.GREEN)
	line_paint.setAntiAlias(true)
	line_paint.setStrokeWidth(6)
	line_paint.setStyle(Paint.Style.STROKE)
	
	var touch_position:PointF = null
	var cursor_position:PointF = null
	
	val tweenManager = new TweenManager()
	PaintAccessor.register()
	import PaintAccessor.PaintProperty
	
	val flashTween = Tween
		.to(cursor_paint, PaintProperty.alpha.id, 0.5f)
		.target(100)
		.repeatYoyo(-1, 0)
		.start(tweenManager)

	def getService():TouchService = {
		return getContext().asInstanceOf[TouchService]
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
	        		canvas.drawRect(bound._1, clickable_paint)
	        	} else {
	        		canvas.drawRect(bound._1, paint)
	        	}
	        }
	        canvas.drawRect(current_bound, current_paint)
	        
        	canvas.drawLine(touch_position.x, touch_position.y, cursor_position.x, cursor_position.y, line_paint)
        	canvas.drawCircle(cursor_position.x, cursor_position.y, 2, cursor_paint)
        	canvas.drawCircle(cursor_position.x, cursor_position.y, 25, cursor_paint)
        }
    	
    	invalidate()
    	lastMillis = currentMillis
    }
}
