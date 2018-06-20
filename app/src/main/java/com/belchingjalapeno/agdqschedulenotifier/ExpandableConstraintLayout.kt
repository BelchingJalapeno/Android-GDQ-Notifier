package com.belchingjalapeno.agdqschedulenotifier

import android.content.Context
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import kotlin.math.max
import kotlin.math.min

class ExpandableConstraintLayout(context: Context?, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    //https://stackoverflow.com/questions/41464629/expand-collapse-animation-in-cardview

    var expanded = true
        private set

    private lateinit var castersView: TextView
    private lateinit var runnersView: TextView
    private lateinit var castersTextView: TextView
    private lateinit var runnersTextView: TextView
    private lateinit var expandImageView: ImageView

    override fun performClick(): Boolean {
        if (!expanded) {
            expand()
        } else {
            collapse()
        }
        return super.performClick()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        castersView = findViewById(R.id.castersView)
        runnersView = findViewById(R.id.runnersView)
        castersTextView = findViewById(R.id.castersTextView)
        runnersTextView = findViewById(R.id.runnersTextView)
        expandImageView = findViewById(R.id.expandImageView)
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

            animate()
                    .setUpdateListener {

                        val clamp = { input: Float -> max(min(input, 1.0f), 0.0f) }

                        val percent = clamp((it.currentPlayTime.toDouble() / it.duration.toDouble()).toFloat())
                        layoutParams.height = (initialHeight + (distanceToExpand * percent)).toInt()

                        castersView.visibility = View.VISIBLE
                        runnersView.visibility = View.VISIBLE
                        castersTextView.visibility = View.VISIBLE
                        runnersTextView.visibility = View.VISIBLE

                        requestLayout()
                    }
                    .setDuration(animationTime)
                    .start()
        }
        val startingColor = Color.argb((0.54f * 255).toInt(), 255, 255, 255)
        val endingColor = ContextCompat.getColor(context, R.color.colorAccent)
        addColorAnimation(expandImageView, startingColor, endingColor)
        expandImageView.animate().rotation(-180.0f)
        expandImageView.animate().setDuration(animationTime).start()

        addAlphaAnimation(castersTextView, 1.0f)
        addAlphaAnimation(runnersTextView, 1.0f)
        addAlphaAnimation(castersView, 1.0f)
        addAlphaAnimation(runnersView, 1.0f)

        castersTextView.animate().setDuration(animationTime).start()
        runnersTextView.animate().setDuration(animationTime).start()
        castersView.animate().setDuration(animationTime).start()
        runnersView.animate().setDuration(animationTime).start()
    }

    /**
     * animationTime must be > 0 to prevent color not getting set
     */
    fun collapse(animationTime: Long = 200) {
        doOnPreDraw {
            expanded = false
            measure(MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
            val initialHeight = height
            val collapsedHeight = measuredHeight - (castersView.measuredHeight + runnersView.measuredHeight)

            val distanceToCollapse = initialHeight - collapsedHeight

            animate()
                    .setUpdateListener {
                        val clamp = { input: Float -> max(min(input, 1.0f), 0.0f) }

                        val percent = clamp((it.currentPlayTime.toDouble() / it.duration.toDouble()).toFloat())

                        layoutParams.height = (initialHeight - (distanceToCollapse * percent)).toInt()
                        requestLayout()
                    }
                    .withEndAction {
                        castersView.visibility = View.INVISIBLE
                        runnersView.visibility = View.INVISIBLE
                        castersTextView.visibility = View.INVISIBLE
                        runnersTextView.visibility = View.INVISIBLE
                    }
                    .setDuration(animationTime)
                    .start()
        }
        val startingColor = ContextCompat.getColor(context, R.color.colorAccent)
        val endingColor = Color.argb((0.54f * 255).toInt(), 255, 255, 255)
        addColorAnimation(expandImageView, startingColor, endingColor)
        expandImageView.animate().rotation(0.0f)
        expandImageView.animate().setDuration(animationTime).start()

        addAlphaAnimation(castersTextView, 0.0f)
        addAlphaAnimation(runnersTextView, 0.0f)
        addAlphaAnimation(castersView, 0.0f)
        addAlphaAnimation(runnersView, 0.0f)

        castersTextView.animate().setDuration(animationTime).start()
        runnersTextView.animate().setDuration(animationTime).start()
        castersView.animate().setDuration(animationTime).start()
        runnersView.animate().setDuration(animationTime).start()
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

    fun collapseNoAnimation() {
        doOnPreDraw {
            expanded = false
            measure(MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
            val initialHeight = height
            val collapsedHeight = measuredHeight - (castersView.measuredHeight + runnersView.measuredHeight)

            val distanceToCollapse = initialHeight - collapsedHeight

            castersView.visibility = View.INVISIBLE
            runnersView.visibility = View.INVISIBLE
            castersTextView.visibility = View.INVISIBLE
            runnersTextView.visibility = View.INVISIBLE
            layoutParams.height = (initialHeight - distanceToCollapse)

            castersView.alpha = 0.0f
            runnersView.alpha = 0.0f
            castersTextView.alpha = 0.0f
            runnersTextView.alpha = 0.0f

            val color = Color.argb((0.54f * 255).toInt(), 255, 255, 255)
            expandImageView.rotation = 0.0f
            expandImageView.setColorFilter(color)

            requestLayout()
        }
    }
}