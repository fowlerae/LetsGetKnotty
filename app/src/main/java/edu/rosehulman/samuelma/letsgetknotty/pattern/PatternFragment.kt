package edu.rosehulman.samuelma.letsgetknotty.pattern

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.samuelma.letsgetknotty.R
import edu.rosehulman.samuelma.letsgetknotty.calculateYardage.CalculateYardageFragment
import edu.rosehulman.samuelma.letsgetknotty.createPattern.PatternDisplayAdapter
import edu.rosehulman.samuelma.letsgetknotty.project.Project
import edu.rosehulman.samuelma.letsgetknotty.project.ProjectFragment


const val ARG_PATTERN = "pattern"
private const val ARG_UID = "uid"
private const val ARG_PROJECT = "project"

class PatternFragment : Fragment() {
    private lateinit var pattern: Pattern
    private lateinit var uid: String
    private lateinit var project : Project
    private lateinit var adapter: PatternDisplayAdapter
    companion object {
        @JvmStatic
        fun newInstance(uid :String, pattern: Pattern, project: Project) =
            PatternFragment().apply {
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
        val view : View = inflater.inflate(R.layout.fragment_pattern, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.pattern_grid_view)
        adapter = context?.let { PatternDisplayAdapter(it,uid,project,pattern) }!!
        recyclerView.layoutManager = GridLayoutManager(context,pattern.stitchesInRepeat)
        recyclerView.adapter = adapter
        adapter.addSnapshotListener()
        val name : TextView = view.findViewById(R.id.pattern_fragment_name_text_view)
        name.text = pattern.name

        val button : Button = view.findViewById(R.id.calculate_yardage_button)
        button.setOnClickListener {
            switchToYardageFragment()
        }

        return view
    }

    private fun switchToYardageFragment() {
        val fragment = CalculateYardageFragment.newInstance(uid, pattern, project)
        val fm = fragmentManager
        val ft = fm?.beginTransaction()
        if (ft != null) {
            ft.replace(R.id.fragment_container, fragment)
            ft.addToBackStack("yardage")
            ft.commit()
        }
    }



}