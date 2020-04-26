package com.mincor.kodiexample.domain.usecases.details

import com.mincor.kodiexample.data.dto.SResult
import com.mincor.kodiexample.data.model.local.MovieEntity
import com.mincor.kodiexample.data.repository.MoviesRepository
import com.mincor.kodiexample.domain.usecases.base.IUseCase

class GetLocalDetailsUseCase(
        private val moviesRepository: MoviesRepository
) : IGetLocalDetailsUseCase {
    override suspend fun invoke(data: Int): SResult<MovieEntity> = moviesRepository.getLocalMovieById(data)
}

interface IGetLocalDetailsUseCase : IUseCase.InOut<Int, SResult<MovieEntity>>