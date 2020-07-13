package edu.rosehulman.samuelma.letsgetknotty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso

private const val ARG_PIC = "pic"

class PictureFragment : Fragment() {
    private var project: Project? = null

    companion object {
        @JvmStatic
        fun newInstance(pro: Project) =
            PictureFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PIC, pro)
                }
            }

        fun newInstance2(pro: Project): PictureFragment {
            val fragment = PictureFragment()
            fragment.arguments = Bundle()
            fragment.requireArguments().putParcelable(ARG_PIC, pro)
            return fragment
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
        val view = inflater.inflate(R.layout.fragment_picture, container, false)
        val ivBasicImage =
            view.findViewById(R.id.image) as ImageView
        Picasso.get().load(project?.imageUrl).into(ivBasicImage)
        //view.image_caption.text = project?.caption ?: ""
        return view
    }



}