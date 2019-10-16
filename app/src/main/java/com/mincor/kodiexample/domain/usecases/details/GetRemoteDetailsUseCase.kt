package com.mincor.kodiexample.domain.usecases.details

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.model.local.MovieEntity
import com.mincor.kodiexample.data.repository.MoviesRepository
import com.mincor.kodiexample.domain.usecases.base.IUseCase

class GetRemoteDetailsUseCase(
        private val moviesRepository: MoviesRepository
) : IUseCase.InOut<Int, SResult<MovieEntity>> {
    override suspend fun execute(data: Int): SResult<MovieEntity> {
        return moviesRepository
                .getRemoteMovieById(data)
                .let { remoteResult ->
                    if (remoteResult is SResult.Success) {
                        moviesRepository.saveMovie(remoteResult.data.apply {
                            hasDetails = true
                        })
                    }
                    remoteResult
                }
    }
}