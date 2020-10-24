package main.scala.recommender.domain

import scala.common.domain.movies.{MovieId, Title}

case class ChartRecommendation(movieId: MovieId, title: Title)
