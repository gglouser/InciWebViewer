package com.glouser.inciwebviewer.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.glouser.inciwebviewer.database.Incident
import com.glouser.inciwebviewer.databinding.ListItemIncidentBinding

/**
 * A RecyclerView.Adapter that is used to display the list of incidents.
 */
class IncidentAdapter(private val clickListener: FeedItemListener)
    : RecyclerView.Adapter<IncidentAdapter.ViewHolder>() {

    var data = listOf<Incident>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /**
     * Create the ViewHolder containing the layout for one incident in the list.
     *
     * @param parent   parent view
     * @param viewType type code (unused)
     * @return the new ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    /**
     * Load incident data into the Views contained in a ViewHolder.
     *
     * @param holder   the ViewHolder to set up
     * @param position the position of this item in the list
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, clickListener)
    }

    /**
     * Get the total number of items in the list.
     *
     * @return count of list items available
     */
    override fun getItemCount(): Int {
        return data.size
    }

    /**
     * ViewHolder used by IncidentAdapter for one incident in the list.
     */
    class ViewHolder private constructor(private val binding: ListItemIncidentBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Incident, clickListener: FeedItemListener) {
            binding.incident = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemIncidentBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class FeedItemListener(val clickListener: (incidentId: Long) -> Unit) {
    fun onClick(incident: Incident) = clickListener(incident.incidentID)
}
