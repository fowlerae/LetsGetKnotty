package edu.rosehulman.samuelma.letsgetknotty.project


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import edu.rosehulman.samuelma.letsgetknotty.R
import edu.rosehulman.samuelma.letsgetknotty.pattern.Pattern
import java.lang.RuntimeException


private const val ARG_PIC = "pic"

class ProjectFragment : Fragment() {
    private var project: Project? = null
    private var listener: OnPatternSelectedListener? = null

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

    interface OnPatternSelectedListener {
        fun onPatternSelected(pattern: Pattern)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OnPatternSelectedListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + "must implement OnPicSelected" )
        }
    }



}