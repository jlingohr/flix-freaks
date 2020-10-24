package main.scala.recommender.domain

import common.domain.auth.UserId


case class SimilarUsersCalculation(userId: UserId,
                                   numberMovesRated: Int,
                                   method: SimilarityMethod,
                                   topN: Seq[(UserId, BigDecimal)],
                                   similarity: Seq[(UserId, BigDecimal)])
