package analytics.service

import domain.{ContentAnalytics, ContentId, UserAnalytics, UserId}

import scala.concurrent.Future

trait AnalyticService[F[_]] {

  def getUserAnalytics(userId: UserId): F[UserAnalytics]

  def getContentAnalytics(contentId: ContentId): F[ContentAnalytics]

}
