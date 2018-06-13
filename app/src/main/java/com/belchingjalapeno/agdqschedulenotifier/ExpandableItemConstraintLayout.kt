package com.belchingjalapeno.agdqschedulenotifier

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet

class ExpandableItemConstraintLayout(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : ConstraintLayout(context, attrs, defStyleAttr) {

    var expand = false
        private set

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val superVal = super.onLayout(changed, left, top, right, bottom)
        if(!isDoneExpanding()){

        }
    }

    private fun isDoneExpanding():Boolean = true
}