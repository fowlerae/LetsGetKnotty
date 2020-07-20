package edu.rosehulman.samuelma.letsgetknotty.project

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.fowlerae.letsgetknotty.project.PatternAdapter
import edu.rosehulman.samuelma.letsgetknotty.MainActivity
import edu.rosehulman.samuelma.letsgetknotty.R
import edu.rosehulman.samuelma.letsgetknotty.pattern.Pattern
import edu.rosehulman.samuelma.letsgetknotty.pattern.PatternFragment


private const val ARG_PROJECT = "project"

class ProjectFragment : Fragment(), PatternAdapter.OnPatternSelectedListener{
    private var project: Project? = null
    lateinit var adapter : PatternAdapter
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
//        val ivBasicImage =
//            view.findViewById(R.id.image) as ImageView
//        Picasso.get().load(project?.imageUrl).into(ivBasicImage)
        val textView : TextView = view.findViewById(R.id.project_title_text_view)
        textView.text = project?.name
        val recyclerView : RecyclerView = view.findViewById(R.id.pattern_recycler_view)
        adapter = project?.id?.let { PatternAdapter(context!!, uid!!, it, listener) }!!
        recyclerView.adapter = adapter
        adapter.addSnapshotListener()
        recyclerView.layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL ,false)
        (context as MainActivity).getFab().setOnClickListener {
            adapter.add(Pattern("front","https://cdn.shopify.com/s/files/1/0032/0025/4021/products/ilia_01_182d4112-7a3f-4057-807e-7f9cc68bfe79_480x480.jpg?v=1571710489",false))
            adapter.notifyDataSetChanged()
        }

        val rowCounterView: View = inflater.inflate(R.layout.row_counter, container, false)
        val array : ArrayList<View> = ArrayList<View>()
        array.add(rowCounterView)
        view.addTouchables(array)
        return view
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


}