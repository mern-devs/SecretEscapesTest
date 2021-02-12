package com.secretescapes.test


interface MviResult<StateT> {
    fun reduce(oldState: StateT): StateT
}

data class Sale(val id: String, val title: String)

data class FeedState(
    val sales: List<Sale> = emptyList()
)

data class FeedLoadedResult(private val loadedSales: List<Sale>) : MviResult<FeedState> {
    override fun reduce(oldState: FeedState): FeedState = oldState.copy(
        sales = loadedSales
    )
}
