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
import net.onthewings.touchservice.Utils._

class OverlayView(service:TouchService) extends View(service) {
	val current_bound = new Rect()
	val bounds = new LinkedList[Rect]()
	val current_paint = new Paint()
	val paint = new Paint()

	paint.setColor(Color.WHITE)
	paint.setStrokeWidth(2)
	paint.setStyle(Paint.Style.STROKE)
	
	current_paint.setColor(Color.GREEN)
	current_paint.setStrokeWidth(2)
	current_paint.setStyle(Paint.Style.STROKE)
	//current_paint.setAlpha(100)
	//current_paint.setStyle(Paint.Style.FILL)

	def getService():TouchService = {
		return getContext().asInstanceOf[TouchService]
	}
	
    override def onDraw(canvas:Canvas) = {
        //canvas.drawLine(0, 0, getWidth(), getHeight(), paint);
        
        for (bound <- bounds) {
        	canvas.drawRect(bound, paint)
        }
        canvas.drawRect(current_bound, current_paint)
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
