package edu.rosehulman.samuelma.letsgetknotty.pattern

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.samuelma.letsgetknotty.R
import edu.rosehulman.samuelma.letsgetknotty.createPattern.PatternDisplayAdapter
import edu.rosehulman.samuelma.letsgetknotty.project.Project


private const val ARG_PATTERN = "pattern"
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
        recyclerView.layoutManager =
            GridLayoutManager(context,pattern.stitchesInRepeat)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        adapter.addSnapshotListener()

        view.findViewById<TextView>(R.id.pattern_title_view).text = pattern.name

        return view
    }

//    fun loadGrid(recyclerView: RecyclerView){
//        val grids = adapter.getAllGrids()
//        val size = adapter.getItemCount()
//        for (grid in 0 until size){
//            recyclerView.addView()
//        }
//    }


}