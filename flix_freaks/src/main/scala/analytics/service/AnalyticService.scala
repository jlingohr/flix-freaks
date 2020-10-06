package analytics.service

import domain.{ContentId, ContentAnalytics, EventLog, UserId, UserAnalytics}
import scala.concurrent.Future

trait AnalyticService {

  def getUserAnalytics(userId: UserId): Future[UserAnalytics]

  def getContentAnalytics(contentId: ContentId): Future[ContentAnalytics]

}
