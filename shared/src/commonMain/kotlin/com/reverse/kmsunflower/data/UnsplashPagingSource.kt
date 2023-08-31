package com.reverse.kmsunflower.data
import app.cash.paging.PagingSource
import app.cash.paging.PagingSourceLoadParams
import app.cash.paging.PagingSourceLoadResult
import app.cash.paging.PagingSourceLoadResultError
import app.cash.paging.PagingSourceLoadResultPage
import app.cash.paging.PagingState
import com.reverse.kmsunflower.api.UnsplashService

private const val UNSPLASH_STARTING_PAGE_INDEX = 1
@Suppress("INCOMPATIBLE_OVERRIDE")
   class UnsplashPagingSource(
    private val service: UnsplashService,
    private val query: String
) : PagingSource<Int, UnsplashPhoto>() {

    override val jumpingSupported: Boolean = true
    override val keyReuseSupported: Boolean = true

    @Suppress("NOTHING_TO_OVERRIDE","CAST_NEVER_SUCCEEDS","TYPE_MISMATCH" )
     override suspend fun load(params: PagingSourceLoadParams<Int>): PagingSourceLoadResult<Int, UnsplashPhoto> {
        val page = params.key ?: UNSPLASH_STARTING_PAGE_INDEX
        return try {
            val response = service.searchPhotos(query, page, params.loadSize)
            val photos = response.results
            PagingSourceLoadResultPage(
                data = photos,
                prevKey = if (page == UNSPLASH_STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (page == response.totalPages) null else page + 1
            ) as PagingSourceLoadResult<Int, UnsplashPhoto>
        } catch (exception: Exception) {
            PagingSourceLoadResultError<Int, UnsplashPhoto>(throwable = exception)  as PagingSourceLoadResult<Int, UnsplashPhoto>
        }
    }
     @Suppress("NOTHING_TO_OVERRIDE","CAST_NEVER_SUCCEEDS","TYPE_MISMATCH" )
     override  fun getRefreshKey(state: PagingState<Int, UnsplashPhoto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
          val result=  state.closestPageToPosition(anchorPosition) as PagingSourceLoadResultPage<Int, UnsplashPhoto>
            return result.prevKey
        }
    }
}
