package net.onthewings.touchservice

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

class HotspotView(service:TouchService) extends View(service) {	
	var width = 40
	var height = 10
	val paint = new Paint()
	val down_position = new PointF()

	paint.setStyle(Paint.Style.FILL)
	paint.setColor(Color.WHITE)
	paint.setAlpha(100)

	def getService():TouchService = {
		return getContext().asInstanceOf[TouchService]
	}
	
    override def onDraw(canvas:Canvas) = {
        canvas.drawRect(0, 0, width, getHeight(), paint)
    }
	
	override def onTouchEvent(evt:MotionEvent):Boolean = {
		log("onTouchEvent " + evt.getRawX() + "," + evt.getRawY() + "," + evt.getAction())
		
		def get_cursor_position(x:Float, y:Float):PointF = {
			val moveScale = 3
			return new PointF((evt.getRawX() - down_position.x) * moveScale + down_position.x, (evt.getRawY() - down_position.y) * moveScale + down_position.y)
		}
		
		evt.getAction() match {
			case 0 => //down
				service.mView.cursor_position = new PointF(evt.getRawX(), evt.getRawY())
				down_position.set(evt.getRawX(), evt.getRawY())
			case 1 => //up
				service.mView.cursor_position.set(get_cursor_position(evt.getRawX(), evt.getRawY()))
				service.touchDevice.sendTapEvents(service.mView.cursor_position.x.toInt, service.mView.cursor_position.y.toInt)
				service.mView.cursor_position = null
			case 2 => //move				
				service.mView.cursor_position.set(get_cursor_position(evt.getRawX(), evt.getRawY()))
			case _ =>
		}
		service.mView.invalidate()
		return false
	}
	
	override def onDragEvent(evt:DragEvent):Boolean = {
		log("onDragEvent")
		return true
	}
}
