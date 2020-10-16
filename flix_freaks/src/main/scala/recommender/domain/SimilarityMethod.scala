package main.scala.recommender.domain

sealed trait SimilarityMethod

case object Jaccard extends SimilarityMethod
case object Pearson extends SimilarityMethod

object SimilarityMethod extends SimilarityMethod {
  def apply(method: String): SimilarityMethod = method match {
    case "jaccard" => Jaccard
    case "pearson" => Pearson
  }
}