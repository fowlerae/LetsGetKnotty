package edu.rosehulman.samuelma.letsgetknotty.projectlist


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import edu.rosehulman.samuelma.letsgetknotty.MainActivity
import edu.rosehulman.samuelma.letsgetknotty.project.Project
import edu.rosehulman.samuelma.letsgetknotty.R
import edu.rosehulman.samuelma.letsgetknotty.project.ProjectFragment

private const val ARG_UID = "UID"

class ProjectListFragment : Fragment(), ProjectListAdapter.OnProjectSelectedListener {
    private var listener: ProjectListAdapter.OnProjectSelectedListener? = null
    private var uid: String? = null
    private lateinit var listAdapter: ProjectListAdapter

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
        val recyclerView = inflater.inflate(R.layout.fragment_project_list, container, false) as RecyclerView
        listAdapter = ProjectListAdapter(
            context!!,
            uid!!,
            listener
        )
        recyclerView.layoutManager =
            GridLayoutManager(context,2)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = listAdapter
        listAdapter.addSnapshotListener()
        (context as MainActivity).getFab().setOnClickListener {
            listAdapter.showAddEditDialog(-1)
        }
        return recyclerView
    }

    companion object {
        @JvmStatic
        fun newInstance(uid: String) =
            ProjectListFragment()
                .apply {
                arguments = Bundle().apply {
                    putString(ARG_UID, uid)
                }
            }
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = this
    }

    override fun onProjectSelected(pro: Project) {
        val fragment = ProjectFragment.newInstance(pro,uid)
        val fm = fragmentManager
        val ft = fm?.beginTransaction()
        if (ft != null) {
            ft.replace(R.id.fragment_container, fragment)
            ft.addToBackStack("project")
            ft.commit()
        }
    }

}
