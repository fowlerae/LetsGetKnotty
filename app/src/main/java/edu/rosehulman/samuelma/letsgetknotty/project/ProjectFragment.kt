package edu.rosehulman.samuelma.letsgetknotty.project

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.samuelma.letsgetknotty.R
import edu.rosehulman.samuelma.letsgetknotty.RowCounter
import edu.rosehulman.samuelma.letsgetknotty.note.NoteAdapter
import edu.rosehulman.samuelma.letsgetknotty.pattern.Pattern
import edu.rosehulman.samuelma.letsgetknotty.pattern.PatternFragment
import kotlinx.android.synthetic.main.dialog_add_gauge.view.*
import kotlinx.android.synthetic.main.dialog_add_row_counter.view.*


private const val ARG_PROJECT = "project"

class ProjectFragment : Fragment(), PatternAdapter.OnPatternSelectedListener{
    private var project: Project? = null
    lateinit var patternAdapter : PatternAdapter
    lateinit var noteAdapter : NoteAdapter
    lateinit var listener: PatternAdapter.OnPatternSelectedListener
    private var uid : String? = null
    companion object {
        @JvmStatic
        fun newInstance(pro: Project, u: String?) =
            ProjectFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PROJECT, pro)
                    uid = u
                    project = pro
                }
            }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        project = arguments?.getParcelable(ARG_PROJECT)
        arguments?.let {
            project = it.getParcelable(ARG_PROJECT)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_project_view, container, false)

        val textView : TextView = view.findViewById(R.id.project_title_text_view)
        textView.text = project?.name

        // pattern recycler
        val patternRecyclerView : RecyclerView = view.findViewById(R.id.pattern_recycler_view)
        patternAdapter = project?.id?.let { PatternAdapter(context!!, uid!!, it, listener) }!!
        patternRecyclerView.adapter = patternAdapter
        patternAdapter.addSnapshotListener()
        patternRecyclerView.layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL ,false)

        // note recycler

        val noteRecyclerView : RecyclerView = view.findViewById(R.id.pattern_recycler_view)
        noteAdapter = project?.id?.let { NoteAdapter(context!!, uid!!, it) }!!
        noteRecyclerView.adapter = noteAdapter
        noteAdapter.addSnapshotListener()
        noteRecyclerView.layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL ,false)

        val addPattern = view.findViewById<LinearLayout>(R.id.add_pattern_button)
        addPattern.setOnClickListener {
//            adapter.add(Pattern("front","https://cdn.shopify.com/s/files/1/0032/0025/4021/products/ilia_01_182d4112-7a3f-4057-807e-7f9cc68bfe79_480x480.jpg?v=1571710489",false))
//            adapter.notifyDataSetChanged()
            patternAdapter.showAddEditDialog(-1)
        }
        val addCounter = view.findViewById<LinearLayout>(R.id.add_row_counter_button)
        addCounter.setOnClickListener {
            showAddCounter()
        }
        val addGauge : LinearLayout = view.findViewById(R.id.add_gauge_button)
        addGauge.setOnClickListener {
            showAddGauge()
            addGauge.visibility = View.INVISIBLE
            addGauge.setOnClickListener {

            }
        }

        return view
    }

    @SuppressLint("InflateParams")
    fun showAddCounter() {
        val builder = context?.let { AlertDialog.Builder(it) }
        if (builder != null) {
            builder.setTitle("Add Row Counter")
            val view = LayoutInflater.from(context).inflate(
                R.layout.dialog_add_row_counter, null, false
            )
            builder.setView(view)
            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                val name = view.row_counter_name_edit_text.text.toString()
                val startingValue = view.starting_value_edit_text.text.toString()
                var num = 0
                if(startingValue != "") {
                    num = startingValue.toInt()
                }
                val rowCounter = RowCounter(name,num,0)
            }
            builder.setNegativeButton(android.R.string.cancel, null)
            builder.show()
        }

    }

    @SuppressLint("InflateParams")
    fun showAddGauge() {
        val builder = context?.let { AlertDialog.Builder(it) }
        if (builder != null) {
            builder.setTitle("Add Row Counter")
            val view = LayoutInflater.from(context).inflate(
                R.layout.dialog_add_edit_image, null, false
            )
            builder.setView(view)
            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                val rowsCountString = view.rows_completed_edit_text.text.toString()
                val widthString = view.project_width_edit_text.text.toString()
                val heightString : String = view.project_height_edit_text.text.toString()
                var rowCount = 0
                if(rowsCountString != "") {
                     rowCount = rowsCountString.toInt()
                }
                var width = 0
                if(widthString != "") {
                    width = widthString.toInt()
                }
                var height = 0
                if(heightString != "") {
                    height = heightString.toInt()
                }
             //   adapter.addGuage(patternName,rowCount,width,height)
            }
            builder.setNegativeButton(android.R.string.cancel, null)
            builder.show()
        }

    }



    override fun onPatternSelected(pattern: Pattern) {
        val fragment = PatternFragment.newInstance(pattern)
        val fm = fragmentManager
        val ft = fm?.beginTransaction()
        if (ft != null) {
            ft.replace(R.id.fragment_container, fragment)
            ft.addToBackStack("pattern")
            ft.commit()
        }
    }


    fun editDialog(position : Int) {
        patternAdapter?.showAddEditDialog(position)
    }

    fun addRowCounter(rowCounter: RowCounter) {
        //rowCounterRef.add(rowCounter)
    }

}