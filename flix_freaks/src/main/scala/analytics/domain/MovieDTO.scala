package domain

import common.domain.ratings.UserRating

import scala.common.domain.movies.{MovieId, Title}

case class MovieDTO(movieId: MovieId, movieTitle: Title, rating: Option[UserRating])