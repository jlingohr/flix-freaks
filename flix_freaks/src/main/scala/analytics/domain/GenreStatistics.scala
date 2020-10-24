package domain

import common.domain.genres.GenreId

case class GenreStatistics(genres: List[(GenreId, String, BigDecimal)], userAvg: BigDecimal)
