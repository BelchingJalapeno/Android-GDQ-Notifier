package com.belchingjalapeno.agdqschedulenotifier

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.TextView

class ExpandableConstraintLayout(context: Context?, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    //https://stackoverflow.com/questions/41464629/expand-collapse-animation-in-cardview

    private var expanded = false

    override fun performClick(): Boolean {
        if (!expanded) expand()
        else collapse()
        return super.performClick()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        collapse(0)
    }

    fun expand(animationTime: Long = 200) {
        expanded = true
        val initialHeight = height
        Log.i("Expandable", "InitialHeight = $initialHeight")

        measure(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT)
        val targetHeight = measuredHeight

        val distanceToExpand = targetHeight - initialHeight

        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    // Do this after expanded
                }

                layoutParams.height = (initialHeight + distanceToExpand * interpolatedTime).toInt()

                findViewById<TextView>(R.id.castersView).visibility = View.VISIBLE
                findViewById<TextView>(R.id.runnersView).visibility = View.VISIBLE
                findViewById<TextView>(R.id.castersTextView).visibility = View.VISIBLE
                findViewById<TextView>(R.id.runnersTextView).visibility = View.VISIBLE

                requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        a.duration = animationTime
        startAnimation(a)
    }

    fun collapse(animationTime: Long = 200) {
        expanded = false
        measure(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT)
        val initialHeight = measuredHeight
        val collapsedHeight = initialHeight - (findViewById<TextView>(R.id.castersTextView).measuredHeight + findViewById<TextView>(R.id.runnersTextView).measuredHeight)

        val distanceToCollapse = initialHeight - collapsedHeight

        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    // Do this after collapsed
                    findViewById<TextView>(R.id.castersView).visibility = View.INVISIBLE
                    findViewById<TextView>(R.id.runnersView).visibility = View.INVISIBLE
                    findViewById<TextView>(R.id.castersTextView).visibility = View.INVISIBLE
                    findViewById<TextView>(R.id.runnersTextView).visibility = View.INVISIBLE
                }

                Log.i("Expandable", "Collapse | InterpolatedTime = $interpolatedTime")

                layoutParams.height = (initialHeight - distanceToCollapse * interpolatedTime).toInt()
                requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        a.duration = animationTime
        startAnimation(a)
    }
}