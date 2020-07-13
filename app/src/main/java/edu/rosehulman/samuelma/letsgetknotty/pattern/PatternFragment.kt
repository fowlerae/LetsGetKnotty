package edu.rosehulman.samuelma.letsgetknotty.pattern

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.fowlerae.letsgetknotty.project.PatternAdapter
import edu.rosehulman.samuelma.letsgetknotty.project.Project
import edu.rosehulman.samuelma.letsgetknotty.R
import java.lang.RuntimeException


private const val ARG_PIC = "pic"

class PatternFragment : Fragment() {
    private var project: Project? = null
    private var listener: PatternFragment.OnPatternSelectedListener? = null
    private var uid: String? = null
    private lateinit var adapter: PatternAdapter
    companion object {
        @JvmStatic
        fun newInstance(pattern: Pattern) =
            PatternFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PIC, pattern)
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
        val recyclerView = inflater.inflate(R.layout.fragment_pattern_list, container, false) as RecyclerView
        adapter = PatternAdapter(context!!, uid!!, listener)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        adapter.addSnapshotListener()
        return recyclerView
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