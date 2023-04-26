
package com.example.pilltracker
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LogAdapter(
    private var logs: List<Logs>,
    private val listener: OnItemClickListener,
) : RecyclerView.Adapter<LogAdapter.ViewHolder>() {

    private val idList = ArrayList<Int>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val medicineName: TextView = view.findViewById(R.id.medicineName)
        val status: TextView = view.findViewById(R.id.status)
        val dateTaken: TextView = view.findViewById(R.id.dateTaken)
        val additionalNotes: TextView = view.findViewById(R.id.additionalNotes)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val log = logs[position]
        holder.medicineName.text = log.medicineName
        holder.status.text = log.status
        holder.dateTaken.text = log.dateTaken
        holder.additionalNotes.text = log.additionalNotes

        holder.checkBox.isChecked = log.isSelected

        holder.itemView.setOnClickListener {
            listener.onItemClick(log)
        }

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            log.isSelected = isChecked
            if (isChecked) {
                idList.add(log.id)
            } else {
                idList.remove(log.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return logs.size
    }

    // Update the list of medicines and notify the adapter
    fun updatelogs(logs: List<Logs>) {
        this.logs = logs
        notifyDataSetChanged()
    }


    interface OnItemClickListener {
        fun onItemClick(log: Logs)
    }

    // Get the IDs of the selected items
    fun getSelectedLogIds(): List<Int> {
        return idList
    }

    fun selectAll(isSelected: Boolean) {
        idList.clear()
        logs.forEach { log ->
            log.isSelected = isSelected
            if (isSelected) {
                idList.add(log.id)
            }
        }
        notifyDataSetChanged()
    }
}
