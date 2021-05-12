package com.secretescapes.test

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment

class DetailFragment: Fragment(R.layout.detail_fragment) {
    private lateinit var title: TextView
    private lateinit var content: TextView
    private val sale by lazy {
        arguments?.let { DetailFragmentArgs.fromBundle(it).sale }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title = view.findViewById(R.id.textView)
        content = view.findViewById(R.id.content)
        sale?.let {
            title.text = it.title
            content.text = it.summaryContent
        }
    }
}