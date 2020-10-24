package domain

import common.domain.auth.UserId
import common.domain.events.EventLog

case class UserAnalytics(userId: UserId,
                         genreAnalytics: GenreStatistics,
                         filmCount: Int,
                         movies: Seq[MovieDTO],
                         logs: Seq[EventLog])
