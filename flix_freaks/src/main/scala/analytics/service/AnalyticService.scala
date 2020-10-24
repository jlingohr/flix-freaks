package analytics.service

import common.domain.auth.UserId
import domain.{ContentAnalytics, UserAnalytics}

import scala.common.domain.movies.MovieId

trait AnalyticService[F[_]] {

  def getUserAnalytics(userId: UserId): F[UserAnalytics]

  def getContentAnalytics(contentId: MovieId): F[ContentAnalytics]

}
