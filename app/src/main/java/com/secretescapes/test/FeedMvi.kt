package com.secretescapes.test

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

interface MviResult<StateT> {
    fun reduce(oldState: StateT): StateT
}

@Parcelize
data class Sale(val id: String, val title: String, val summaryContent: String) : Parcelable

sealed class MainState {
    object Idle: MainState()
    object Loading: MainState()
    data class ValidData(val sales: List<Sale>): MainState()
    data class Error(val error: String?): MainState()
}

data class FeedState(
    val sales: List<Sale> = emptyList()
)

data class FeedLoadedResult(private val loadedSales: List<Sale>) : MviResult<FeedState> {
    override fun reduce(oldState: FeedState): FeedState = oldState.copy(
        sales = loadedSales
    )
}
