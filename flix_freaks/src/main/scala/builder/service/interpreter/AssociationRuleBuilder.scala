package main.scala.builder.service.interpreter

import java.time.Instant

import cats.implicits._
import common.domain.events.{EventLog, SessionId}
import common.domain.recommendations.SeededRecommendation
import main.scala.builder.service.AssociationRuleBuilder

import scala.common.domain.movies.MovieId
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


case class Transaction()


class AssociationRuleBuilderInterpreter
  extends AssociationRuleBuilder[Future, EventLog, Map[SessionId, List[MovieId]], SeededRecommendation] {


  //TODO should be a better way to abstract over transaction generation without using Future explicitly
  override def generateTransactions(data: Seq[EventLog]): Future[Map[SessionId, List[MovieId]]] = Future {
    val transactions =
      data
        .foldLeft(Map[SessionId, List[MovieId]]()) {
          case (acc, event) =>
            val transactionId = event.sessionId
            val updatedValue = event.contentId :: acc.getOrElse(transactionId, List.empty)
            acc + (transactionId -> updatedValue)
        }
    transactions
  }

  override def calculateSupportConfidence(transactions: Map[SessionId, List[MovieId]], confidence: Double): Future[Seq[SeededRecommendation]] = Future {
    val n = transactions.size
    val oneItemSets = calculateItemSetsOne(transactions, confidence)
    val twoItemSets = calculateItemSetsTwo(transactions, oneItemSets)
    val rules = calculateAssociationRules(oneItemSets, twoItemSets, n)
    rules
  }

  def calculateItemSetsOne(transactions: Map[SessionId, List[MovieId]], minSupport: Double): Map[Set[MovieId], Int] = {
    val n = transactions.size
    val temp =
      transactions.foldLeft(Map[Set[MovieId], Int]()) {
        case (acc, (k, v)) =>
          val inner = v.foldLeft(Map[Set[MovieId], Int]()) {
            case (inner, item) =>
              val keySet = Set(item)
              val updated = inner.getOrElse(keySet, 0) + 1
              inner + (keySet -> updated)
          }
          acc |+| inner
      }

    // Remove all items that is not supported
    val oneItemSet = {
      temp.foldLeft(Map[Set[MovieId], Int]()) {
        case (acc, (k, v)) =>
          if (v > minSupport * n) {
            acc + (k -> v)
          } else {
            acc
          }
      }
    }

    oneItemSet
  }

  def calculateItemSetsTwo(transactions: Map[SessionId, List[MovieId]], oneItemSets: Map[Set[MovieId], Int]): Map[Set[MovieId], Int] = {
    val twoItemSets =
      transactions.foldLeft(Map[Set[MovieId], Int]()) {
        case (acc, (k, v)) =>
          val items = v.toSet.toList //Set(v).toList
          if (items.length > 2) {
            val combinations = items.combinations(2)
            acc |+| combinations.foldLeft(Map[Set[MovieId], Int]()) {
              case (acc, perm) =>
                if (hasSupport(perm, oneItemSets)) {
                  val updated = acc.getOrElse(perm.toSet, 0) + 1
                  acc + (perm.toSet -> updated)
                } else {
                  acc
                }
            }
          } else if (items.length == 2) {
            if (hasSupport(items, oneItemSets)) {
              val updated = acc.getOrElse(items.toSet, 0) + 1
              acc + (items.toSet -> updated)
            } else {
              acc
            }
          } else {
            acc
          }
      }
    twoItemSets
  }

  def calculateAssociationRules(oneItemSets: Map[Set[MovieId], Int], twoItemSets: Map[Set[MovieId], Int], n: Int): Seq[SeededRecommendation] = {
    val timestamp = Instant.now()
    val rules =
      oneItemSets.flatMap {
        case (source, sourceFreq) =>
          twoItemSets
            .foldLeft(List.empty[SeededRecommendation]) {
              case (inner, (key, groupFreq)) =>
                if (source.subsetOf(key)) {
                  val target = key.diff(source)
                  val support = BigDecimal(groupFreq.toDouble / n)
                  val confidence = BigDecimal(groupFreq.toDouble / sourceFreq)
                  val associationRule = SeededRecommendation(timestamp, source.iterator.next(), target.iterator.next(), confidence, support, "association_rules")
                  associationRule :: inner
                } else {
                  inner
                }
            }
      }

    rules.toSeq
  }

  def hasSupport(perm: List[MovieId], oneItemSets: Map[Set[MovieId], Int]): Boolean = {
    oneItemSets.contains(Set(perm.head)) && oneItemSets.contains(Set(perm(1)))
  }

}

object AssociationRuleBuilder extends AssociationRuleBuilderInterpreter