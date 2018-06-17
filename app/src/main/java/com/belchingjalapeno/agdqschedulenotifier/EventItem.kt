package com.belchingjalapeno.agdqschedulenotifier

import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

class EventItem(val event: SpeedRunEvent, val workQueueManager: WorkQueueManager) : AbstractItem<EventItem, EventItem.ViewHolder>() {
    override fun getType(): Int {
        return R.id.event_item
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun getLayoutRes(): Int {
        return R.layout.event_item
    }

    class ViewHolder(itemView: View) : FastAdapter.ViewHolder<EventItem>(itemView) {

        private val gameNameView: TextView = itemView.findViewById(R.id.gameNameView)
        private val startTimeView: TextView = itemView.findViewById(R.id.startTimeView)
        private val runLengthView: TextView = itemView.findViewById(R.id.runLengthView)
        private val categoryView: TextView = itemView.findViewById(R.id.categoryView)
        private val castersView: TextView = itemView.findViewById(R.id.castersView)
        private val runnersView: TextView = itemView.findViewById(R.id.runnersView)
        private val expandableView: ExpandableConstraintLayout = itemView as ExpandableConstraintLayout

        private val timeCalculator = TimeCalculator()
        private val backgroundColorSetter = BackgroundColorSetter()
        private val eventItemViewSetter = EventItemViewSetter()

        override fun bindView(item: EventItem, payloads: MutableList<Any>?) {
            val currentTime = System.currentTimeMillis()
            val targetTime = timeCalculator.fromStringStartTimeToLong(item.event.startTime)
            val timeDifference = timeCalculator.getTimeDiff(currentTime, targetTime)
            //show more precise time as we get closer to the event
            val time: String = if (timeCalculator.getDays(timeDifference) <= 0) {
                if (timeCalculator.getHours(timeDifference) <= 0) {
                    timeCalculator.getFormattedTime(timeDifference, showMinutes = true, showSeconds = true)
                } else {
                    timeCalculator.getFormattedTime(timeDifference, showMinutes = true)
                }
            } else {
                timeCalculator.getFormattedTime(timeDifference, showHours = true)
            }

            startTimeView.text = "When   $time"

            val expectedLengthInMillis = timeCalculator.fromStringExpectedLengthToLong(item.event.runLength)
            val time2 = timeCalculator.getFormattedTime(expectedLengthInMillis, showHours = true, showMinutes = true, showSeconds = true)

            runLengthView.text = "Length   $time2"


            gameNameView.text = item.event.game
//            setupTimeView.text = item.event.setupTime
            categoryView.text = item.event.category
            castersView.text = item.event.runners
            runnersView.text = item.event.casters

            if(item.workQueueManager.isQueued(item.event)){
                expandableView.expand(0)
            }else{
                expandableView.collapse(0)
            }
            backgroundColorSetter.setColor(itemView, item.event, item.workQueueManager)

//            itemView.requestLayout()
//            itemView.invalidate()

//            castersTextView.visibility = View.VISIBLE
//            castersView.visibility = View.VISIBLE
//            runnersView.visibility = View.VISIBLE
//            runnersTextView.visibility = View.VISIBLE
//            EventItemViewSetter(item.workQueueManager).setViewState(itemView, item.event, false)

            itemView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    itemView.viewTreeObserver.removeOnPreDrawListener(this)
                    eventItemViewSetter.setViewState(item.workQueueManager, itemView, item.event, false)
                    return true
                }
            })
//            itemView.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
//                override fun onGlobalLayout() {
//                    itemView.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                    EventItemViewSetter(item.workQueueManager).setViewState(itemView, item.event, false)
//                }
//            })
        }

        override fun unbindView(item: EventItem) {
        }
    }
}