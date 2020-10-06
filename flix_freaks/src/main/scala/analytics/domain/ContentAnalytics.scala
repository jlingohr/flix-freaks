package domain

case class ContentAnalytics(title: String,
                            avgRatings: Float,
                            genres: Seq[String],
                            contentId: ContentId,
                            ratedBy: Seq[UserId],
                            logs: Seq[EventLog],
                            numberUsers: Int)
