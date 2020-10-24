package domain

import common.domain.auth.UserId
import common.domain.events.EventLog

import scala.common.domain.movies.MovieId

case class ContentAnalytics(title: MovieId,
                            avgRatings: Float,
                            genres: Seq[String],
                            contentId: MovieId,
                            ratedBy: Seq[UserId],
                            logs: Seq[EventLog],
                            numberUsers: Int)
