/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.jetstream.data.repositories

import com.google.jetstream.data.entities.MovieCategoryDetails
import com.google.jetstream.data.entities.MovieDetails
import com.google.jetstream.data.entities.MovieList
import com.google.jetstream.data.entities.ThumbnailType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val movieDataSource: MovieDataSource,
    private val movieCastDataSource: MovieCastDataSource,
    private val movieCategoryDataSource: MovieCategoryDataSource,
) : MovieRepository {

    override fun getFeaturedMovies() = flow {
        val list = movieDataSource.getFeaturedMovieList()
        emit(list)
    }

    override fun getTrendingMovies(): Flow<MovieList> = flow {
        val list = movieDataSource.getTrendingMovieList()
        emit(list)
    }

    override fun getTop10Movies(): Flow<MovieList> = flow {
        val list = movieDataSource.getTop10MovieList()
        emit(list)
    }

    override fun getNowPlayingMovies(): Flow<MovieList> = flow {
        val list = movieDataSource.getNowPlayingMovieList()
        emit(list)
    }

    override fun getMovieCategories() = flow {
        val list = movieCategoryDataSource.getMovieCategoryList()
        emit(list)
    }

    override suspend fun getMovieCategoryDetails(categoryId: String): MovieCategoryDetails {
        val categoryList = movieCategoryDataSource.getMovieCategoryList()
        val category = categoryList.find { categoryId == it.id } ?: categoryList.first()

        val movieList = movieDataSource.getMovieList().shuffled().subList(0, 20)

        return MovieCategoryDetails(
            id = category.id,
            name = category.name,
            movies = movieList
        )
    }

    override suspend fun getMovieDetails(movieId: String): MovieDetails {
        val movieList = movieDataSource.getMovieList()
        val movie = movieList.find { it.id == movieId } ?: movieList.first()

        return MovieDetails(
            id = movie.id,
            videoUri = movie.videoUri,
            subtitleUri = movie.subtitleUri,
            posterUri = movie.posterUri,
            title = movie.title,
            fullTitle = movie.fullTitle,
            year = movie.year,
            releaseDate = movie.releaseDate,
            runtimeMins = movie.runtimeMins,
            runtimeStr = movie.runtimeStr,
            genres = movie.genres,
            plot = movie.plot,
            directors = movie.directors,
            contentRating = movie.contentRating,
            stars = movie.stars
        )
    }

    override suspend fun searchMovies(query: String): MovieList {
        return movieDataSource.getMovieList().filter {
            it.fullTitle.contains(other = query, ignoreCase = true)
        }
    }

    override fun getMoviesWithLongThumbnail() = flow {
        val list = movieDataSource.getMovieList(ThumbnailType.Long)
        emit(list)
    }

    override fun getMovies(): Flow<MovieList> = flow {
        val list = movieDataSource.getMovieList()
        emit(list)
    }
    override fun getPopularFilmsThisWeek(): Flow<MovieList> = flow {
        val list = movieDataSource.getPopularFilmThisWeek()
        emit(list)
    }

    override fun getFavouriteMovies(): Flow<MovieList> = flow {
        val list = movieDataSource.getFavoriteMovieList()
        emit(list)
    }
}
