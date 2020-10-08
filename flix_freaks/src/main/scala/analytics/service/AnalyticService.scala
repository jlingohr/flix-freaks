package analytics.service

import domain.{ContentAnalytics, ContentId, UserAnalytics, UserId}

import scala.concurrent.Future

trait AnalyticService {

  def getUserAnalytics(userId: UserId): Future[UserAnalytics]

  def getContentAnalytics(contentId: ContentId): Future[ContentAnalytics]

}
