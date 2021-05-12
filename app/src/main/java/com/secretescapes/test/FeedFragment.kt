package com.secretescapes.test

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FeedFragment(private val fromTest: Boolean = false) : Fragment(R.layout.feed_fragment) {

    private val feedViewModel by viewModels<FeedViewModel>(ownerProducer = { requireActivity() })

    private val feedAdapter by lazy { FeedAdapter(requireContext()) {
        findNavController().navigate(FeedFragmentDirections.actionFeedFragmentToDetailFragment(it))
    } }

    private lateinit var progressCircular: ProgressBar
    private lateinit var recyclerView: RecyclerView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recycler_view)
        progressCircular = view.findViewById(R.id.progress_circular)
        recyclerView.adapter = feedAdapter

        lifecycleScope.launch {
            feedViewModel.myState.collect { mainState ->
                when (mainState) {
                    is MainState.Idle -> {
                        progressCircular.visibility = View.GONE
                        recyclerView.visibility = View.GONE
                    }
                    is MainState.Loading -> {
                        progressCircular.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }
                    is MainState.ValidData -> {
                        progressCircular.visibility = View.GONE
                        renderResult(mainState.sales)
                    }
                    is MainState.Error -> {
                        recyclerView.visibility = View.GONE
                        progressCircular.visibility = View.GONE
                        Toast.makeText(requireContext(), mainState.error, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        if (fromTest)
            renderResult(List(20) {
                Sale("", "A Sale in position: $it", "")
            })
    }

    private fun renderResult(sales: List<Sale>) {
        recyclerView.visibility = View.VISIBLE
        feedAdapter.submitList(sales)
    }
}
