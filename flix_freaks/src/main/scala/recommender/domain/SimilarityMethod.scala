package main.scala.recommender.domain

sealed trait SimilarityMethod

case object Jaccard extends SimilarityMethod
case object Pearson extends SimilarityMethod
