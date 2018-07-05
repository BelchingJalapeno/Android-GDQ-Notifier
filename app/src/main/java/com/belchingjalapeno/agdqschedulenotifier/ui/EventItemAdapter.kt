package com.belchingjalapeno.agdqschedulenotifier.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.belchingjalapeno.agdqschedulenotifier.EventFilter
import com.belchingjalapeno.agdqschedulenotifier.R
import com.belchingjalapeno.agdqschedulenotifier.SpeedRunEvent
import com.belchingjalapeno.agdqschedulenotifier.TimeFormatter
import com.belchingjalapeno.agdqschedulenotifier.notifications.NotificationQueue
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class EventItemAdapter(private val events: Array<SpeedRunEvent>,
                       private val notificationQueue: NotificationQueue,
                       private val eventFilter: EventFilter,
                       private val fragment: LifecycleOwner) : RecyclerView.Adapter<EventItemAdapter.ViewHolder>() {

    private val timeFormatter = TimeFormatter()
    private val notificationUiStateSetter = NotificationUiStateSetter()

    //all of the events that are shown in the RecyclerView that arnt filtered out
    private val visibleEventList = events.toMutableList()
    //list map of events to how high the itemview is when it is displayed, used for animating collapse
    private val heightMap = mutableMapOf<SpeedRunEvent, Int>()

    private val oldEventBackgroundColor = Color.LTGRAY
    private val futureEventBackgroundColor = Color.WHITE

    companion object {
        private val backgroundExecutor = ThreadPoolExecutor(0, 8, 5L, TimeUnit.SECONDS, SynchronousQueue())
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val expandableView = LayoutInflater.from(p0.context)
                .inflate(R.layout.event_item, p0, false) as ExpandableConstraintLayout
        return ViewHolder(expandableView)
    }

    override fun getItemCount(): Int {
        return visibleEventList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        bind(p0, visibleEventList[p1])
    }

    private fun bind(viewHolder: ViewHolder, item: SpeedRunEvent) {
        viewHolder.apply {
            notificationQueuedLiveData?.removeObservers(fragment)
            notificationQueuedLiveData = notificationQueue.isQueued(item)
            val currentTime = System.currentTimeMillis()
            val targetTime = item.startTime
            val timeDifference = timeFormatter.getTimeDiff(currentTime, targetTime)
            //show more precise time as we get closer to the event
            val time: String = if (Math.abs(timeFormatter.getDays(timeDifference)) <= 0) {
                if (Math.abs(timeFormatter.getHours(timeDifference)) <= 0) {
                    if (Math.abs(timeFormatter.getMinutes(timeDifference)) <= 0) {
                        timeFormatter.getFormattedTime(timeDifference, showMinutes = false, showSeconds = true)
                    } else {
                        timeFormatter.getFormattedTime(timeDifference, showMinutes = true, showSeconds = false)
                    }
                } else {
                    timeFormatter.getFormattedTime(timeDifference, showHours = true, showMinutes = true)
                }
            } else {
                timeFormatter.getFormattedTime(timeDifference, showHours = true)
            }

            startTimeView.text = "When   $time"

            val expectedLengthInMillis = timeFormatter.fromStringExpectedLengthToLong(item.estimatedTime)
            val time2 = timeFormatter.getFormattedTime(expectedLengthInMillis, showHours = true, showMinutes = true, showSeconds = false)

            runLengthView.text = "Length   $time2"

            gameNameView.text = item.game
            categoryView.text = item.category
            castersView.text = item.runners
            runnersView.text = item.casters

            if (!heightMap.contains(item)) {
                expandableView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        castersView.visibility = View.GONE
                        runnersView.visibility = View.GONE
                        castersTextView.visibility = View.GONE
                        runnersTextView.visibility = View.GONE
                        expandableView.measure(View.MeasureSpec.makeMeasureSpec(expandableView.measuredWidth, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))

                        heightMap[item] = expandableView.measuredHeight
                        expandableView.collapseNoAnimation(heightMap[item]!!)

                        expandableView.viewTreeObserver.removeOnPreDrawListener(this)

                        return true
                    }
                })
            } else {
                expandableView.collapseNoAnimation(heightMap[item]!!)
            }

            setBackgroundColorState(itemView, item)
            notificationUiStateSetter.setViewState(false, notificationIcon, 0)

            notificationQueuedLiveData?.observe(fragment, object : Observer<Boolean?> {
                override fun onChanged(value: Boolean?) {
                    val isQueued = value ?: false
                    notificationUiStateSetter.setViewState(isQueued, notificationIcon, 0)
                    notificationQueuedLiveData?.removeObserver(this)
                }
            })

            notificationIcon.setOnClickListener {
                //don't allow subscribing / unsubscribing if the filter is enabled
                if (eventFilter.notificationOnly) {
                    return@setOnClickListener
                }

                notificationQueue.toggleNotification(item)

                setBackgroundColorState(notificationIcon, item)

                notificationQueuedLiveData?.observe(fragment, object : Observer<Boolean?> {
                    override fun onChanged(value: Boolean?) {
                        val isQueued = value ?: false
                        notificationUiStateSetter.setViewState(isQueued, notificationIcon)
                        notificationQueuedLiveData?.removeObserver(this)
                    }
                })
            }
        }
    }

    fun filter(notificationEnabled: Boolean, query: String, runOnUiThread: (Runnable) -> Unit) {
        backgroundExecutor.submit {
            val backingListCopy = visibleEventList.toList()

            val filteredEvents = events.filter {
                if (notificationEnabled) {
                    notificationQueue.isQueuedNow(it)
                } else {
                    true
                }
            }.filter {
                it.game.contains(query, ignoreCase = true)
            }
            val calculatedDiff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(p0: Int, p1: Int): Boolean {
                    return backingListCopy[p0] === filteredEvents[p1]
                }

                override fun getOldListSize(): Int {
                    return backingListCopy.size
                }

                override fun getNewListSize(): Int {
                    return filteredEvents.size
                }

                override fun areContentsTheSame(p0: Int, p1: Int): Boolean {
                    return backingListCopy[p0] == filteredEvents[p1]
                }
            })
            runOnUiThread(Runnable {
                calculatedDiff
                        .dispatchUpdatesTo(this)
                visibleEventList.clear()
                visibleEventList.addAll(filteredEvents)
            })
        }
    }

    private fun setBackgroundColorState(v: View?, event: SpeedRunEvent) {
        if (timeFormatter.getTimeDiff(System.currentTimeMillis(), event.startTime) <= 0) {
            v?.setBackgroundColor(oldEventBackgroundColor)
        } else {
            v?.setBackgroundColor(futureEventBackgroundColor)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val gameNameView: TextView = itemView.findViewById(R.id.gameNameView)
        val startTimeView: TextView = itemView.findViewById(R.id.startTimeView)
        val runLengthView: TextView = itemView.findViewById(R.id.runLengthView)
        val categoryView: TextView = itemView.findViewById(R.id.categoryView)
        val castersView: TextView = itemView.findViewById(R.id.castersView)
        val runnersView: TextView = itemView.findViewById(R.id.runnersView)
        val runnersTextView: TextView = itemView.findViewById(R.id.runnersTextView)
        val castersTextView: TextView = itemView.findViewById(R.id.castersTextView)
        val expandableView: ExpandableConstraintLayout = itemView as ExpandableConstraintLayout
        val notificationIcon: ImageView = itemView.findViewById(R.id.notification_toggle_button)

        var notificationQueuedLiveData: LiveData<Boolean>? = null
    }
}