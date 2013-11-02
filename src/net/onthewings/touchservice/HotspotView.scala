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
import android.view.ViewGroup
import android.graphics.PixelFormat
import android.content.Context
import android.graphics.Point

class HotspotView(service:TouchService) extends View(service) {	
	var width = 25
	var height = 10
	val paint = new Paint()
	val down_position = new PointF()
	val current_position = new PointF()

	paint.setStyle(Paint.Style.FILL)
	paint.setColor(Color.WHITE)
	paint.setAlpha(10)
	
	private val layoutParams = new WindowManager.LayoutParams(
		width,
		ViewGroup.LayoutParams.WRAP_CONTENT,
		WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
		
		WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
		,
		PixelFormat.RGBA_8888
	)
	setLayoutParams(layoutParams)

	def getService():TouchService = {
		return getContext().asInstanceOf[TouchService]
	}
	
    override def onDraw(canvas:Canvas) = {
        canvas.drawRect(0, 0, width, getHeight(), paint)
    }
	
	override def onTouchEvent(evt:MotionEvent):Boolean = {
		//log("onTouchEvent " + evt.getRawX() + "," + evt.getRawY() + "," + evt.getAction())
		
		def get_cursor_position(x:Float, y:Float):PointF = {
			val moveScale = 3
			return new PointF((evt.getRawX() - down_position.x) * moveScale + down_position.x, (evt.getRawY() - down_position.y) * moveScale + down_position.y)
		}
		
		evt.getAction() match {
			case MotionEvent.ACTION_DOWN => //down
				setVisibility(View.INVISIBLE)
				down_position.set(evt.getRawX(), evt.getRawY())
				current_position.set(evt.getRawX(), evt.getRawY())
				service.mView.init_touch_position = down_position
				service.mView.current_touch_position = current_position
				service.mView.cursor_position = new PointF(evt.getRawX(), evt.getRawY())
				//service.touchDevice.sendBeginHoverEvents(service.mView.cursor_position.x / displaySize.x, service.mView.cursor_position.y / displaySize.y)
			case MotionEvent.ACTION_UP => //up
				service.mView.cursor_position.set(get_cursor_position(evt.getRawX(), evt.getRawY()))
				service.touchDevice.sendTapEvents(service.mView.cursor_position.x, service.mView.cursor_position.y)
				service.mView.init_touch_position = null
				service.mView.current_touch_position = null
				service.mView.cursor_position = null
				setVisibility(View.VISIBLE)
			case MotionEvent.ACTION_MOVE => //move
				current_position.set(evt.getRawX(), evt.getRawY())
				service.mView.current_touch_position.set(evt.getRawX(), evt.getRawY())
				service.mView.cursor_position.set(get_cursor_position(evt.getRawX(), evt.getRawY()))
				//service.touchDevice.sendHoverEvents(service.mView.cursor_position.x / displaySize.x, service.mView.cursor_position.y / displaySize.y)
			case MotionEvent.ACTION_CANCEL =>
				service.mView.init_touch_position = null
				service.mView.current_touch_position = null
				service.mView.cursor_position = null
				setVisibility(View.VISIBLE)
			case _ =>
				//log("event action " + evt.getAction())
		}
		service.mView.invalidate()
		return false
	}
}
