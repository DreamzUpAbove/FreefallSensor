/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.smartdevices.freefallsensor.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartdevices.freefallsensor.R
import com.smartdevices.freefallsensor.models.Event

class EventListAdapter : RecyclerView.Adapter<EventListAdapter.EventViewHolder>() {

    private var events = emptyList<Event>() // Cached copy of words

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventIdView: TextView = itemView.findViewById(R.id.eventIdView)
        val eventStartView: TextView = itemView.findViewById(R.id.eventStartView)
        val eventEndView: TextView = itemView.findViewById(R.id.eventEndView)
        val eventDurationView: TextView = itemView.findViewById(R.id.eventDurationView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.event_item_view, parent, false)
        return EventViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val current = events[position]
        holder.eventIdView.text = current.eventId.toString()
        holder.eventStartView.text = current.startTime
        holder.eventEndView.text = current.endTime
        holder.eventDurationView.text = current.duration
    }

    internal fun addEvents(events: List<Event>) {
        this.events = events
        notifyDataSetChanged()
    }

    override fun getItemCount() = events.size
}


