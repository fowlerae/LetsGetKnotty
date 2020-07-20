package edu.rosehulman.samuelma.letsgetknotty.project


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import edu.rosehulman.fowlerae.letsgetknotty.project.PatternAdapter
import edu.rosehulman.samuelma.letsgetknotty.R
import edu.rosehulman.samuelma.letsgetknotty.pattern.Pattern
import edu.rosehulman.samuelma.letsgetknotty.pattern.PatternFragment
import edu.rosehulman.samuelma.letsgetknotty.projectlist.ProjectListAdapter


private const val ARG_PIC = "pic"

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
                    putParcelable(ARG_PIC, pro)
                    uid = u
                    project = pro
                }
            }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        project = arguments?.getParcelable(ARG_PIC)
        arguments?.let {
            project = it.getParcelable(ARG_PIC)
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
        val ivBasicImage =
            view.findViewById(R.id.image) as ImageView
        Picasso.get().load(project?.imageUrl).into(ivBasicImage)
        //view.image_caption.text = project?.caption ?: ""
        val recyclerView : RecyclerView = view.findViewById(R.id.pattern_recycler_view)
        adapter = PatternAdapter(
            context!!,
            uid!!,
            listener
        )
        recyclerView.layoutManager =
            GridLayoutManager(context,2)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        adapter.addSnapshotListener()
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