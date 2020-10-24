package main.scala.recommender.repository

import scala.common.domain.movies.MovieId


trait RecommendationRepository[F[_], SeededRec] {
  type RecommendationWithAvgConf = (MovieId, Option[BigDecimal])

  def getBySourceId(source: MovieId, take: Int=6): F[Seq[SeededRec]]
  def getBySourceIn(seeds: Set[MovieId]): F[Seq[RecommendationWithAvgConf]]

}
