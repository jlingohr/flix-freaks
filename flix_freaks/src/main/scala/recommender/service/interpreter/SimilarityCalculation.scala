package main.scala.recommender.service.interpreter

import common.domain.auth.UserId
import main.scala.recommender.service.SimilarityCalculation

import scala.common.domain.movies.MovieId

object SimilarityCalculation extends SimilarityCalculation[UserId, BigDecimal] {

  override def pearson(users: Map[UserId, Map[MovieId, BigDecimal]], thisUser: UserId, thatUser: UserId): BigDecimal = {
    if (users.contains(thisUser) && users.contains(thatUser)) {
      val thisUserRatings = users.get(thisUser)
      val thatUserRatings = users.get(thatUser)
      val score = for {
        thisur <- thisUserRatings
        thatur <- thatUserRatings
      } yield computePearson(thisur, thatur)

      score.getOrElse(BigDecimal(0))
    } else {
      BigDecimal(0)
    }
  }

  override def jaccard(users: Map[UserId, Map[MovieId, BigDecimal]], thisUser: UserId, thatUser: UserId): BigDecimal = {
    if (users.contains(thisUser) && users.contains(thatUser)) {
      val thisUserRatings = users.get(thisUser).map(_.keys.toSet).getOrElse(Set.empty)
      val thatUserRatings = users.get(thatUser).map(_.keys.toSet).getOrElse(Set.empty)
      val intersect = thisUserRatings.intersect(thatUserRatings)
      val union = thisUserRatings.union(thatUserRatings)
      val iou = intersect.size / union.size.toDouble
      BigDecimal(iou)
    } else {
      BigDecimal(0)
    }
  }

  def computePearson(thisUserRatings: Map[MovieId, BigDecimal], thatUserRatings: Map[MovieId, BigDecimal]): BigDecimal = {
    val thisUserAvg = thisUserRatings.values.sum / thisUserRatings.values.size
    val thatUserAvg = thatUserRatings.values.sum / thatUserRatings.values.size
    val allMovies = thisUserRatings.keySet.intersect(thatUserRatings.keySet)

    val (div, aDiv, bDiv) =
      allMovies
        .foldLeft((BigDecimal(0), BigDecimal(0), BigDecimal(0))) {
          case ((dividend, adiv, bdiv), movie) =>
            val thisMovieRating = thisUserRatings.get(movie)
            val thatMovieRating = thatUserRatings.get(movie)
            val updates = for {
              thisRating <- thisMovieRating
              thatRating <- thatMovieRating
              anr = thisRating - thisUserAvg
              bnr = thatRating - thatUserAvg
              divNew = anr * bnr
              anrNew = anr.pow(2)
              bnrNew = bnr.pow(2)
            } yield (divNew, anrNew, bnrNew)
            updates.map {
              case (d,a,b) => (dividend + d, adiv + a, bdiv + b)
            }.getOrElse((dividend, adiv, bdiv))
        }

    val divisor = BigDecimal(Math.sqrt(aDiv.doubleValue) * Math.sqrt(bDiv.doubleValue))

    if (divisor.compare(BigDecimal(0)) != 0) {
      div / divisor
    } else {
      BigDecimal(0)
    }
  }
}
