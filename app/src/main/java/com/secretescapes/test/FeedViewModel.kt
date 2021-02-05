package com.secretescapes.test

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import okhttp3.OkHttpClient

class FeedViewModel : ViewModel() {

    private val okHttpClient = OkHttpClient()

    val feedFlow: Flow<Unit> = emptyFlow()
}
