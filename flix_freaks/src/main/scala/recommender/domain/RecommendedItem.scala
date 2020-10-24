package main.scala.recommender.domain

import scala.common.domain.movies.MovieId

case class RecommendedItem(movieId: MovieId, numRatings: Int, avgRating: BigDecimal)
