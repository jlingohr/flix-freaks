package main.scala.recommender.service

import common.domain.auth.UserId

import scala.common.domain.movies.MovieId

trait SimilarityCalculation[User, SimilarityScore] {

  def pearson(users: Map[UserId, Map[MovieId, SimilarityScore]], thisUser: User, thatUser: User): SimilarityScore

  def jaccard(users: Map[UserId, Map[MovieId, SimilarityScore]], thisUser: User, thatUser: User): SimilarityScore

}
