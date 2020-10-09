package main.scala.recommender.domain

import domain.UserId

case class SimilarUsersCalculation(userId: UserId,
                                   numberMovesRated: Int,
                                   method: SimilarityMethod,
                                   topN: Seq[(String, BigDecimal)],
                                   similarity: Seq[(String, BigDecimal)])
