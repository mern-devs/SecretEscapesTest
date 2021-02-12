package com.secretescapes.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FeedViewModel : ViewModel() {

    private val okHttpClient = OkHttpClient()

    private val intentions = MutableSharedFlow<Intention>()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val state = intentions
        .onSubscription {
            emit(Intention.Initial)
        }.flatMapMerge { intention ->
            when (intention) {
                Intention.Initial -> flow { emit(requestSales()) }
                    .map<List<Sale>, MviResult<FeedState>> { sales ->
                        FeedLoadedResult(sales)
                    }
            }
        }.scan(FeedState()) { accumulator, value ->
            value.reduce(accumulator)
        }.stateIn(viewModelScope, SharingStarted.Lazily, FeedState())

    private suspend fun requestSales() = suspendCoroutine<List<Sale>> { cont ->
        val gqlBody = JSONObject()
        gqlBody.put("query", GQL_QUERY)
        val gqlCall = okHttpClient.newCall(
            Request.Builder()
                .url(GQL_ENDPOINT)
                .addHeader(API_HEADER_NAME, API_HEADER_VALUE)
                .post(gqlBody.toString().toRequestBody("application/json".toMediaType()))
                .build()
        )

        gqlCall.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                cont.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseJsonString = response.body?.string()!!
                val responseJsonObject = JSONObject(responseJsonString)
                val dataJsonObject = responseJsonObject.getJSONObject("data")
                val salesArray = dataJsonObject.getJSONArray("sales")

                val salesList = mutableListOf<Sale>()
                for (i in 0 until salesArray.length()) {
                    val salesObject = salesArray.getJSONObject(i)
                    salesList.add(
                        Sale(
                            id = salesObject.getString("id"),
                            title = salesObject.getString("title")
                        )
                    )
                }
                cont.resume(salesList)
            }
        })
    }

    sealed class Intention {
        object Initial : Intention()
    }

    private companion object {
        private const val GQL_QUERY = """
            {
                sales(
                    affiliate: "es",
                    limit: 300
                ) {
                    id
                    title
                }
            }
        """

        private const val API_HEADER_NAME = "x-api-key"
        private const val API_HEADER_VALUE = "1lVkC4YOx1acSOia7dUH093PFCeaKk0a6zEUYR3x"

        private const val GQL_ENDPOINT =
            "https://w7szo4xfvg.execute-api.eu-central-1.amazonaws.com/staging/graphql"
    }
}
