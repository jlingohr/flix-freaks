package domain

case class GenreStatistics(genres: List[(Int, String, BigDecimal)], userAvg: BigDecimal)
