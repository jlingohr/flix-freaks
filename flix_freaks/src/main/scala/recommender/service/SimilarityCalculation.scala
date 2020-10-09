package main.scala.recommender.service

trait SimilarityCalculation[User, SimilarityScore] {

  def pearson(users: Map[String, Map[String, SimilarityScore]], thisUser: User, thatUser: User): SimilarityScore

  def jaccard(users: Map[String, Map[String, SimilarityScore]], thisUser: User, thatUser: User): SimilarityScore

}
