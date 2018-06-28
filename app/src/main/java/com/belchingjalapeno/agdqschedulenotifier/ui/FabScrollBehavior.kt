package com.belchingjalapeno.agdqschedulenotifier.ui

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet
import android.view.MotionEvent

class FabScrollBehavior(context: Context?, attrs: AttributeSet?) : CoordinatorLayout.Behavior<FloatingActionButton>(context, attrs) {

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: FloatingActionButton, ev: MotionEvent): Boolean {
        if (ev.historySize > 0) {
            val dy = ev.y - ev.getHistoricalY(0)
            val threshHold = 10
            if (dy > threshHold) {
                child.show()
            }
            if (dy < -threshHold) {
                child.hide()
            }
        }

        return super.onInterceptTouchEvent(parent, child, ev)
    }
}