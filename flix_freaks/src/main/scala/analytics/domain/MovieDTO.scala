package domain

case class MovieDTO(movieId: String, movieTitle: String, rating: Option[Rating])