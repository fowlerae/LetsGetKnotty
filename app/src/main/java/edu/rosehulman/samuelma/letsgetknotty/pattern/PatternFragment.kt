package edu.rosehulman.samuelma.letsgetknotty.pattern

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.rosehulman.samuelma.letsgetknotty.project.PatternAdapter
import edu.rosehulman.samuelma.letsgetknotty.R


private const val ARG_PATTERN = "pattern"

class PatternFragment : Fragment() {
    private var pattern: Pattern? = null
    private var uid: String? = null
    private lateinit var adapter: PatternAdapter
    companion object {
        @JvmStatic
        fun newInstance(pattern: Pattern) =
            PatternFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PATTERN, pattern)
                }
            }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pattern = arguments?.getParcelable(ARG_PATTERN)
        arguments?.let {
            pattern = it.getParcelable(ARG_PATTERN)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pattern, container, false)
    }




}