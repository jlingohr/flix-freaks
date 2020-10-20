package main.scala.recommender.repository


trait RecommendationRepository[F[_], SeededRec] {
  type RecommendationWithAvgConf = (String, Option[BigDecimal])

  def getBySourceId(source: String, take: Int=6): F[Seq[SeededRec]]
  def getBySourceIn(seeds: Set[String]): F[Seq[RecommendationWithAvgConf]]

}
