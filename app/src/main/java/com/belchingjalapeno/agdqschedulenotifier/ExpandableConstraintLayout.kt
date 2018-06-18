package com.belchingjalapeno.agdqschedulenotifier

import android.content.Context
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ImageView
import android.widget.TextView

class ExpandableConstraintLayout(context: Context?, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    //https://stackoverflow.com/questions/41464629/expand-collapse-animation-in-cardview

    var expanded = true
        private set

    override fun performClick(): Boolean {
        if (!expanded) {
            expand()
        } else {
            collapse()
        }
        return super.performClick()
    }

    /**
     * animationTime must be > 0 to prevent color not getting set
     */
    fun expand(animationTime: Long = 200) {
        doOnPreDraw {
            expanded = true
            val initialHeight = height
            Log.i("Expandable", "InitialHeight = $initialHeight")

            measure(MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
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
        val notificationIconView = findViewById<ImageView>(R.id.expandImageView)
        val startingColor = Color.argb((0.54f * 255).toInt(), 255, 255, 255)
        val endingColor = ContextCompat.getColor(context, R.color.colorAccent)
        addColorAnimation(notificationIconView, startingColor, endingColor)
        notificationIconView.animate().rotation(-180.0f)
        notificationIconView.animate().setDuration(animationTime).start()
    }

    /**
     * animationTime must be > 0 to prevent color not getting set
     */
    fun collapse(animationTime: Long = 200) {
        doOnPreDraw {
            expanded = false
            measure(MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
            val initialHeight = measuredHeight
            val castersView = findViewById<TextView>(R.id.castersView)
            val runnersView = findViewById<TextView>(R.id.runnersView)
            val collapsedHeight = initialHeight - (castersView.measuredHeight + runnersView.measuredHeight)

            val distanceToCollapse = initialHeight - collapsedHeight

            val a = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    if (interpolatedTime == 1f) {
                        // Do this after collapsed
                        castersView.visibility = View.INVISIBLE
                        runnersView.visibility = View.INVISIBLE
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
        val notificationIconView = findViewById<ImageView>(R.id.expandImageView)
        val startingColor = ContextCompat.getColor(context, R.color.colorAccent)
        val endingColor = Color.argb((0.54f * 255).toInt(), 255, 255, 255)
        addColorAnimation(notificationIconView, startingColor, endingColor)
        notificationIconView.animate().rotation(0.0f)
        notificationIconView.animate().setDuration(animationTime).start()
    }

    private fun doOnPreDraw(lambda: () -> Unit) {
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                lambda()
                viewTreeObserver.removeOnPreDrawListener(this)
                return true
            }
        })
    }
}