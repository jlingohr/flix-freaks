package domain

case class UserAnalytics(userId: UserId,
                         genreAnalytics: GenreStatistics,
                         filmCount: Int,
                         movies: Seq[MovieDTO],
                         logs: Seq[EventLog])
