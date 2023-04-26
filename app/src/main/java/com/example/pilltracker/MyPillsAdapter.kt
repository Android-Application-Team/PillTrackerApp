package com.example.pilltracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyPillsAdapter(
    private var myPills: List<MyPills>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<MyPillsAdapter.ViewHolder>() {

    private val selectedIds = ArrayList<Int>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(medicine: MyPills) {

        }

        val nameOfMedicine: TextView = view.findViewById(R.id.tv_medicine_name)
        val dose: TextView = view.findViewById(R.id.tv_dose)
        val timeToTakeMed: TextView = view.findViewById(R.id.tv_time_to_take_med)
        val remainingMedicine: TextView = view.findViewById(R.id.tv_remaining_medicine)
        val checkBox: CheckBox = view.findViewById(R.id.checkBoxMypills)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mypill, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val medicine = myPills[position]
        holder.bind(medicine)
        holder.nameOfMedicine.text = medicine.nameOfMedicine
        holder.dose.text = medicine.dose
        holder.timeToTakeMed.text = medicine.timeToTakeMed
        holder.remainingMedicine.text = medicine.remainingMedicine
        // Set the checked state of the checkbox based on the selectedLogs list
        holder.checkBox.isChecked = selectedIds.contains(medicine.id)
        // Set up the click listener
        holder.itemView.setOnClickListener {
            listener.onItemClick(medicine)
        }

        // Set up the check listener
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedIds.add(medicine.id)
            } else {
                selectedIds.remove(medicine.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return myPills.size
    }

    // Update the list of medicines and notify the adapter
    fun updateMyPills(newMedicines: List<MyPills>) {
        myPills = newMedicines
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(medicine: MyPills)
    }

    // Get the IDs of the selected items
    fun getSelectedIds(): ArrayList<Int> {
        return selectedIds
    }
}
