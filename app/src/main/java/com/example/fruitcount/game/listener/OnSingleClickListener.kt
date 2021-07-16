package kr.rowan.superbrain_5gxr.game.listener

import android.os.SystemClock
import android.util.Log
import android.view.View

abstract class OnSingleClickListener: View.OnClickListener {

    // 중복 클릭 방지 시간 설정
    private var mLastClickTime: Long = 0L

    abstract fun onSingleClick(view: View)

    override fun onClick(view: View) {
        val currentClickTime = SystemClock.uptimeMillis()
        val elapsedTime = currentClickTime - mLastClickTime
        mLastClickTime = currentClickTime

        if (elapsedTime <= MIN_CLICK_INTERVAL) {
            Log.d(TAG, "중복클릭!")
            return
        }
        onSingleClick(view)
    }

    companion object {
        const val MIN_CLICK_INTERVAL = 400L
        private val TAG = OnSingleClickListener::class.java.simpleName
    }
}