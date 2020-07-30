package edu.rosehulman.samuelma.letsgetknotty.createPattern

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.samuelma.letsgetknotty.R
import edu.rosehulman.samuelma.letsgetknotty.pattern.Pattern
import edu.rosehulman.samuelma.letsgetknotty.project.Project


private const val ARG_PATTERN = "pattern"
private const val ARG_PROJECT = "project"
private const val ARG_WIDTH = "width"
private const val ARG_HEIGHT = "height"
private const val ARG_UID = "uid"

class CreatePatternFragment: Fragment() {
    private lateinit var pattern : Pattern
    private lateinit var project: Project
    private var width: Int = 0
    private var height: Int = 0
    private var uid: String? = null
    private lateinit var adapter: CreatePatternAdapter?
    companion object {
        @JvmStatic
        fun newInstance(uid :String, pattern: Pattern, project : Project, width : Int, height : Int) =
            CreatePatternFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PATTERN, pattern)
                    putParcelable(ARG_PROJECT, project)
                    putInt(ARG_WIDTH,width)
                    putInt(ARG_HEIGHT, height)
                    putString(ARG_UID, uid)
                }
            }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pattern = arguments!!.getParcelable(ARG_PATTERN)!!
        project = arguments!!.getParcelable(ARG_PROJECT)!!
        width = arguments!!.getInt(ARG_WIDTH)
        height = arguments!!.getInt(ARG_HEIGHT)
        uid = arguments!!.getString(ARG_UID)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_pattern, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.create_pattern_grid_view)
        adapter = context?.let { uid?.let { it1 -> CreatePatternAdapter(it, it1, project,pattern) } }
        recyclerView.layoutManager = width?.let { GridLayoutManager(context, it) }
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        adapter?.addSnapshotListener()
        return view
    }


}