package main.scala.recommender.domain

import scala.common.domain.movies.MovieId

case class AssociationRule(id: MovieId, confidence: Option[BigDecimal])