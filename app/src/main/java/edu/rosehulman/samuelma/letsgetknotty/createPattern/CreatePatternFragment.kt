package edu.rosehulman.samuelma.letsgetknotty.createPattern

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import edu.rosehulman.samuelma.letsgetknotty.Constants
import edu.rosehulman.samuelma.letsgetknotty.R
import edu.rosehulman.samuelma.letsgetknotty.pattern.Pattern
import edu.rosehulman.samuelma.letsgetknotty.project.Project
import edu.rosehulman.samuelma.letsgetknotty.project.ProjectFragment


private const val ARG_PATTERN = "pattern"
private const val ARG_PROJECT = "project"
private const val ARG_UID = "uid"

class CreatePatternFragment: Fragment() {
    private lateinit var pattern : Pattern
    private lateinit var project: Project
    private var uid: String = ""
    private lateinit var adapter: CreatePatternAdapter
    private var color : Int = Color.BLACK
    private var stitch : Int? = null

    companion object {
        @JvmStatic
        fun newInstance(uid :String, pattern: Pattern, project : Project) =
            CreatePatternFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PATTERN, pattern)
                    putParcelable(ARG_PROJECT, project)
                    putString(ARG_UID, uid)
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
        val view = inflater.inflate(R.layout.fragment_create_pattern, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.create_pattern_grid_view)
        adapter = context?.let { CreatePatternAdapter(it,uid,project,pattern) }!!
        recyclerView.layoutManager =
            GridLayoutManager(context,pattern.stitchesInRepeat)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        adapter.addSnapshotListener()
        val button : Button = view.findViewById(R.id.choose_color_button)
        button.setOnClickListener {
            adapter.color  = color
            adapter.stitch = null
        }
        button.setOnLongClickListener {
            showColorDialog(button)
            true
        }

        val addButton : Button = view.findViewById(R.id.add_created_pattern_button)
        addButton.setOnClickListener {
            switchToProjectFragment()
        }
        val cancelButton : Button = view.findViewById(R.id.cancel_created_pattern_button)
        cancelButton.setOnClickListener {
            adapter.removePattern()
            switchToProjectFragment()
        }
        val stitchButton : ImageButton = view.findViewById(R.id.choose_stitch_button)
        stitchButton.setOnClickListener {
            adapter.color  = Color.WHITE
            adapter.stitch = stitch
        }
        stitchButton.setOnLongClickListener {
            showStitchDialog(stitchButton)
            true
        }
        val handler = Handler()
        handler.postDelayed({
            if(adapter.rectangles.size == 0) {
                createGrid()
            } },1000)

        return view
    }

    private fun createGrid() {
        for(x in 0 until pattern.stitchesInRepeat*pattern.rowsInRepeat) {
            adapter.add(Grid(Color.WHITE,x))
        }
    }

    // From https://android-arsenal.com/details/1/1693
    private fun showColorDialog(colorButton : Button) {
        val builder = ColorPickerDialogBuilder.with(context)
        builder.setTitle("Choose HSV color")
        builder.wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
        builder.density(12)
        builder.setOnColorSelectedListener { selectedColor ->
            Toast.makeText(
                context,
                "onColorSelected: 0x" + Integer.toHexString(selectedColor),
                Toast.LENGTH_SHORT
            ).show()
        }
        builder.setPositiveButton(android.R.string.ok) { dialog, selectedColor, allColors ->
            adapter.stitch = null
            colorButton.setBackgroundColor(selectedColor)
            color = selectedColor
            adapter.color = selectedColor
        }
        builder.setNegativeButton(android.R.string.cancel) { _, _ ->

        }
        builder.build().show()
    }

    private fun showStitchDialog(stitchButton: ImageButton) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(context!!)
        builder.setTitle("Choose a stitch")
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_choose_stitch, null, false)
        builder.setView(view)
        val purlButton = view.findViewById<ImageButton>(R.id.black_purl_stitch_button)
        var choosenStitch : Int? = null
        purlButton.setOnClickListener {
            choosenStitch = R.drawable.ic_stitch_purl_black
        }
        val yarnOverButton = view.findViewById<ImageButton>(R.id.black_yarn_over_stitch_button)
        yarnOverButton.setOnClickListener {
            choosenStitch = R.drawable.ic_stitch_yarnover_black
        }
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            adapter.stitch = choosenStitch
            stitch = choosenStitch
            adapter.color = Color.WHITE
            adapter.stitch?.let { stitchButton.setImageResource(it) }
        }
        builder.setNegativeButton(android.R.string.cancel,null)
        builder.create().show()
    }

    private fun switchToProjectFragment() {
        val fragment = ProjectFragment.newInstance(project,uid)
        val fm = fragmentManager
        val ft = fm?.beginTransaction()
        if (ft != null) {
            ft.replace(R.id.fragment_container, fragment)
            ft.addToBackStack("project")
            ft.commit()
        }
    }

}