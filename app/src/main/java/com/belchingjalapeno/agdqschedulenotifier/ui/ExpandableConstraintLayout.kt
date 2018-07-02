package com.belchingjalapeno.agdqschedulenotifier.ui

import android.content.Context
import android.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import com.belchingjalapeno.agdqschedulenotifier.R
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

        expanded = false
        castersView.visibility = View.GONE
        runnersView.visibility = View.GONE
        castersTextView.visibility = View.GONE
        runnersTextView.visibility = View.GONE

        castersView.alpha = 0.0f
        runnersView.alpha = 0.0f
        castersTextView.alpha = 0.0f
        runnersTextView.alpha = 0.0f

        val color = Color.argb((0.54f * 255).toInt(), 255, 255, 255)
        expandImageView.rotation = 0.0f
        expandImageView.setColorFilter(color)
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

                        requestLayout()
                    }
                    .setDuration(animationTime)
                    .start()
        }
        animate().cancel()
        castersView.visibility = View.VISIBLE
        runnersView.visibility = View.VISIBLE
        castersTextView.visibility = View.VISIBLE
        runnersTextView.visibility = View.VISIBLE

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
            //subtract 16dp extra because extra is added for some reason
            val collapsedHeight = measuredHeight - (castersView.measuredHeight + runnersView.measuredHeight) - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16.0f, resources.displayMetrics)

            val distanceToCollapse = initialHeight - collapsedHeight

            animate()
                    .setUpdateListener {
                        val clamp = { input: Float -> max(min(input, 1.0f), 0.0f) }

                        val percent = clamp((it.currentPlayTime.toDouble() / it.duration.toDouble()).toFloat())

                        layoutParams.height = (initialHeight - (distanceToCollapse * percent)).toInt()
                        requestLayout()
                    }
                    .withEndAction {
                        castersView.visibility = View.GONE
                        runnersView.visibility = View.GONE
                        castersTextView.visibility = View.GONE
                        runnersTextView.visibility = View.GONE
                    }
                    .setDuration(animationTime)
                    .start()
        }
        animate().cancel()
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

    fun collapseNoAnimation(collapsedSize: Int) {
        expanded = false
        castersView.visibility = View.GONE
        runnersView.visibility = View.GONE
        castersTextView.visibility = View.GONE
        runnersTextView.visibility = View.GONE
        layoutParams.height = collapsedSize

        castersView.alpha = 0.0f
        runnersView.alpha = 0.0f
        castersTextView.alpha = 0.0f
        runnersTextView.alpha = 0.0f

        val color = Color.argb((0.54f * 255).toInt(), 255, 255, 255)
        expandImageView.rotation = 0.0f
        expandImageView.setColorFilter(color)

        requestLayout()
        invalidate()
    }
}