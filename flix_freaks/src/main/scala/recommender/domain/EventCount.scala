package main.scala.recommender.domain

import scala.common.domain.movies.MovieId

case class EventCount(contentId: MovieId, count: Int)
