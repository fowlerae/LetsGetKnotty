package edu.rosehulman.samuelma.letsgetknotty.calculateYardage

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import edu.rosehulman.samuelma.letsgetknotty.R
import edu.rosehulman.samuelma.letsgetknotty.createPattern.PatternDisplayAdapter
import edu.rosehulman.samuelma.letsgetknotty.pattern.*
import edu.rosehulman.samuelma.letsgetknotty.project.Project
import edu.rosehulman.samuelma.letsgetknotty.project.ProjectFragment

private const val ARG_PATTERN = "pattern"
private const val ARG_UID = "uid"
private const val ARG_PROJECT = "project"

class CalculateYardageFragment : Fragment() {


    private lateinit var pattern: Pattern
    private lateinit var uid: String
    private lateinit var project : Project
    companion object {
        @JvmStatic
        fun newInstance(uid :String, pattern: Pattern, project: Project) =
            CalculateYardageFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PATTERN, pattern)
                    putString(ARG_UID,uid)
                    putParcelable(ARG_PROJECT,project)
                }
            }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pattern = arguments!!.getParcelable(ARG_PATTERN)!!
        project = arguments!!.getParcelable(ARG_PROJECT)!!
        uid = arguments!!.getString(ARG_UID).toString()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.fragment_calculate_yardage, container, false)
        val button : Button = view.findViewById(R.id.calculate_button)
        button.setOnClickListener {
            val message = calculate(view)
            calculateDialog(message)
        }

        return view
    }


    private fun calculateDialog(message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.setNegativeButton("Enter Information Again",null)
        builder.setPositiveButton("Done") { _, _ ->
            switchToProjectFragment()
        }
        builder.show()
    }

    fun calculate(view: View): String {
        val desiredHeightString = view.findViewById<EditText>(R.id.calc_desired_height).text.toString()
        val desiredWidthString : String = view.findViewById<EditText>(R.id.calc_desired_width).text.toString()

        val projectHeightString : String = view.findViewById<EditText>(R.id.calc_project_height).text.toString()
        val projectWidthString : String = view.findViewById<EditText>(R.id.calc_project_width).text.toString()

        val yardsUsedString : String = view.findViewById<EditText>(R.id.calc_yards_used).text.toString()
        val yardsPerSkeinString : String = view.findViewById<EditText>(R.id.calc_yarns_per_skein).text.toString()

        if(desiredHeightString != "" || desiredWidthString != "" || projectHeightString != ""
            || projectWidthString != "" || yardsUsedString != "" || yardsPerSkeinString != "") {

            val dHeight : Double = desiredHeightString.toInt().toDouble()
            val dWidth : Double = desiredWidthString.toInt().toDouble()
            val pHeight : Double = projectHeightString.toInt().toDouble()
            val pWidth : Double = projectWidthString.toInt().toDouble()
            val yUsed : Double = yardsUsedString.toInt().toDouble()
            val ySkein : Double = yardsPerSkeinString.toInt().toDouble()

            val yardageRatio : Double = yUsed / ySkein
            val desiredArea : Double = dHeight * dWidth
            val actualArea : Double = pHeight * pWidth
            val areaRatio : Double = desiredArea/actualArea
            val skeinsNeeded : Double = areaRatio * yardageRatio

            return "Estimated Skeins Needed For Pattern $skeinsNeeded "
        }
        return "Please fill out all fields"
    }

    fun switchToProjectFragment() {
        val fragment = PatternFragment.newInstance(uid, pattern, project)
        val fm = fragmentManager
        val ft = fm?.beginTransaction()
        if (ft != null) {
            ft.replace(R.id.fragment_container, fragment)
            ft.addToBackStack("pattern")
            ft.commit()
        }
    }
}