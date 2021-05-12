package com.secretescapes.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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

    private val _state = MutableStateFlow<MainState>(MainState.Idle)
    val myState: StateFlow<MainState>
        get() = _state
    val userIntent = Channel<Intention>(Channel.UNLIMITED)

    init {
        handleIntent()
    }
    private fun handleIntent() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect{
                when (it) {
                    is Intention.Initial -> {
                        viewModelScope.launch {
                            _state.value = MainState.Loading
                            _state.value = try {
                                MainState.ValidData(requestSales())
                            } catch (e: Exception) {
                                MainState.Error(e.localizedMessage)
                            }
                        }
                    }
                    is Intention.Refresh -> {
                        //TODO
                    }
                    is Intention.Third -> {
                        //TODO
                    }
                }
            }
        }
    }

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
                            title = salesObject.getString("title"),
                            summaryContent = salesObject.getString("summaryContent")
                        )
                    )
                }
                cont.resume(salesList)
            }
        })
    }

    sealed class Intention {
        object Initial : Intention()
        object Refresh : Intention()
        object Third : Intention()
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
                    summaryContent
                }
            }
        """

        private const val API_HEADER_NAME = "x-api-key"
        private const val API_HEADER_VALUE = "1lVkC4YOx1acSOia7dUH093PFCeaKk0a6zEUYR3x"

        private const val GQL_ENDPOINT =
            "https://w7szo4xfvg.execute-api.eu-central-1.amazonaws.com/staging/graphql"
    }
}
