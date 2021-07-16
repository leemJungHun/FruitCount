package kr.rowan.superbrain_5gxr.game.listener

import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

abstract class OnSingleItemTouchListener : RecyclerView.OnItemTouchListener {

    private var mLastClickTime = 0

    //abstract fun onChildClick(child: View): Boolean
    abstract fun onSingleInterceptTouch(parent: RecyclerView, evt: MotionEvent): Boolean

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        if(e.action == MotionEvent.ACTION_UP) {
            val currentClickTime = SystemClock.uptimeMillis().toInt()
            val elapsedTime = currentClickTime - mLastClickTime
            mLastClickTime = currentClickTime


            if (elapsedTime <= MIN_CLICK_INTERVAL) {
                Log.d(TAG, "중복클릭!")
                return false
            }
            return onSingleInterceptTouch(rv, e)
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    }

    companion object {
        const val MIN_CLICK_INTERVAL = 200
        private val TAG = OnSingleItemTouchListener::class.java.simpleName
    }
}