package com.secretescapes.test

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

class FeedFragment : Fragment(R.layout.feed_fragment) {

    private val feedViewModel by viewModels<FeedViewModel>()

    private val feedAdapter by lazy { FeedAdapter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.recycler_view).adapter = feedAdapter

        lifecycleScope.launchWhenStarted {
            feedViewModel.state.onEach { feedState ->
                feedAdapter.submitList(feedState.sales)
            }.collect()
        }
    }
}
