package edu.rosehulman.samuelma.letsgetknotty

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import java.lang.RuntimeException

private const val ARG_UID = "UID"

class ProjectFragment : Fragment() {
    private var listener: OnProjectSelectedListener? = null
    private var uid: String? = null
    private lateinit var adapter: ProjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uid = it.getString(ARG_UID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val recyclerView = inflater.inflate(R.layout.fragment_projects, container, false) as RecyclerView
        adapter = ProjectAdapter(context!!, uid!!,listener)
        recyclerView.layoutManager =
            GridLayoutManager(context,2)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        adapter.addSnapshotListener()
        (context as MainActivity).getFab().setOnClickListener {
            adapter.showAddEditDialog()
        }
        return recyclerView
    }

    companion object {
        @JvmStatic
        fun newInstance(uid: String) =
            ProjectFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_UID, uid)
                }
            }
    }

    interface OnProjectSelectedListener {
        fun onProjectSelected(pic: Project)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OnProjectSelectedListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + "must implement OnPicSelected" )
        }
    }

}
