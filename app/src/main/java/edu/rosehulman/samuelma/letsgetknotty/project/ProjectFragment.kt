package edu.rosehulman.samuelma.letsgetknotty.project


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import edu.rosehulman.fowlerae.letsgetknotty.project.PatternAdapter
import edu.rosehulman.samuelma.letsgetknotty.R
import edu.rosehulman.samuelma.letsgetknotty.pattern.Pattern
import edu.rosehulman.samuelma.letsgetknotty.pattern.PatternFragment
import edu.rosehulman.samuelma.letsgetknotty.projectlist.ProjectListAdapter
import edu.rosehulman.samuelma.letsgetknotty.projectlist.ProjectListFragment
import java.lang.RuntimeException


private const val ARG_PIC = "pic"

class ProjectFragment : Fragment(), PatternAdapter.OnPatternSelectedListener {
    private var project: Project? = null
   private var listener: PatternAdapter.OnPatternSelectedListener? = null

    companion object {
        @JvmStatic
        fun newInstance(pro: Project) =
            ProjectFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PIC, pro)
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_project_view, container, false)
        val ivBasicImage =
            view.findViewById(R.id.image) as ImageView
        Picasso.get().load(project?.imageUrl).into(ivBasicImage)
        //view.image_caption.text = project?.caption ?: ""
        return view
    }




    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = this
    }

    override fun onPatternSelected(pattern: Pattern) {
        val fragment = PatternFragment.newInstance(pattern)
        val ft = fragmentManager?.beginTransaction()
        if (ft != null) {
            ft.replace(R.id.fragment_container, fragment)
            ft.addToBackStack("picture")
            ft.commit()
        }

    }



}