package com.example.pilltracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

class MyPillsDetailsFragment : Fragment() {

    private lateinit var tvNameOfMedicine: TextView
    private lateinit var tvDose: TextView
    private lateinit var tvTimeToTakeMed: TextView
    private lateinit var tvAdditionalNotes: TextView
    private lateinit var tvRemainingMedicine: TextView
    private lateinit var pushNotificationBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_my_pills_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvNameOfMedicine = view.findViewById(R.id.tv_name_of_medicine)
        tvDose = view.findViewById(R.id.tv_dose)
        tvTimeToTakeMed = view.findViewById(R.id.tv_time_to_take_med)
        tvAdditionalNotes = view.findViewById(R.id.tv_additional_notes)
        tvRemainingMedicine = view.findViewById(R.id.tv_remaining_medicine)
        pushNotificationBtn = view.findViewById<Button>(R.id.send_notification_btn)


        // Retrieve medicine data from arguments and populate UI elements
        val myPill = arguments?.getParcelable<MyPills>(ARG_MEDICINE)
        if (myPill != null) {
            tvNameOfMedicine.text = myPill.nameOfMedicine
            tvDose.text = myPill.dose
            tvTimeToTakeMed.text = myPill.timeToTakeMed
            tvAdditionalNotes.text = myPill.additionalNotes
            tvRemainingMedicine.text = myPill.remainingMedicine
            pushNotificationBtn.setOnClickListener {
                AddMedication()
            }
        }


    }

    companion object {
        private const val ARG_MEDICINE = "myPill"

        fun newInstance(myPill: MyPills): MyPillsDetailsFragment {
            val fragment = MyPillsDetailsFragment()
            val args = Bundle()
            args.putParcelable(ARG_MEDICINE, myPill)
            fragment.arguments = args
            return fragment
        }
    }


}
