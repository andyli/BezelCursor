package net.onthewings.touchservice;

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._
import java.util.LinkedList
import Utils._
import android.graphics.PointF

class OverlayView(service:TouchService) extends View(service) {

	val current_bound = new Rect()
	val bounds = new LinkedList[Rect]()
	val current_paint = new Paint()
	val paint = new Paint()
	val cursor_paint = new Paint()
	val line_paint = new Paint()

	paint.setColor(Color.WHITE)
	paint.setStrokeWidth(2)
	paint.setStyle(Paint.Style.STROKE)
	
	current_paint.setColor(Color.GREEN)
	current_paint.setStrokeWidth(2)
	current_paint.setStyle(Paint.Style.STROKE)
	//current_paint.setAlpha(100)
	//current_paint.setStyle(Paint.Style.FILL)

	cursor_paint.setColor(Color.GREEN)
	cursor_paint.setStrokeWidth(4)
	cursor_paint.setStyle(Paint.Style.STROKE)

	line_paint.setColor(Color.GREEN)
	line_paint.setStrokeWidth(6)
	line_paint.setStyle(Paint.Style.STROKE)
	
	var touch_position:PointF = null
	var cursor_position:PointF = null

	def getService():TouchService = {
		return getContext().asInstanceOf[TouchService]
	}
	
    override def onDraw(canvas:Canvas) = {                
        if (cursor_position != null) {
	        for (bound <- bounds) {
	        	canvas.drawRect(bound, paint)
	        }
	        canvas.drawRect(current_bound, current_paint)
	        
        	canvas.drawLine(touch_position.x, touch_position.y, cursor_position.x, cursor_position.y, line_paint)
        	canvas.drawCircle(cursor_position.x, cursor_position.y, 2, cursor_paint)
        	canvas.drawCircle(cursor_position.x, cursor_position.y, 25, cursor_paint)
        }
    }
	
	override def onTouchEvent(evt:MotionEvent):Boolean = {
		log("onTouchEvent " + evt.getX() + "," + evt.getY() + "," + evt.getAction())
		return false
	}
	
	override def onDragEvent(evt:DragEvent):Boolean = {
		log("onDragEvent")
		return true
	}
}
