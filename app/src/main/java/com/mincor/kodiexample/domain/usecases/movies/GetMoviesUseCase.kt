package com.mincor.kodiexample.domain.usecases.movies

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.domain.usecases.base.IUseCase
import com.mincor.kodiexample.presentation.movies.MovieUI
import com.rasalexman.coroutinesmanager.AsyncTasksManager
import com.rasalexman.coroutinesmanager.doAsyncAwait

class GetMoviesUseCase(
        private val getCachedMoviesUseCase: GetCachedMoviesUseCase,
        private val getRemoteMoviesUseCase: GetRemoteMoviesUseCase
) : AsyncTasksManager(), IGetMoviesInOutUseCase {

    override suspend fun execute(data: Int): SResult<List<MovieUI>> = doAsyncAwait {
        when (val localResult = getCachedMoviesUseCase.execute(data)) {
            is SResult.Empty -> getRemoteMoviesUseCase.execute(data)
            is SResult.Success -> localResult
            is SResult.Error -> localResult
        }
    }
}

typealias IGetMoviesInOutUseCase = IUseCase.InOut<Int, SResult<List<MovieUI>>>