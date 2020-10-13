package main.scala.builder.service.interpreter

import domain.{EventLog, play}
import main.scala.builder.service.AssociationRuleBuilder
import main.scala.common.domain.SeededRecommendation

import scala.concurrent.Future
import main.scala.builder.repository.interpreter.LogQuery._

import java.time.Instant

import scala.concurrent.ExecutionContext.Implicits.global

import scalaz._
import Scalaz._


case class Transaction()

class AssociationRuleBuilderInterpreter extends AssociationRuleBuilder[EventLog, Map[String, List[String]], SeededRecommendation] {
  override def retrievePlayEvents: Future[Seq[EventLog]] = queryEvent(play)

  override def generateTransactions(data: Seq[EventLog]): Future[Map[String, List[String]]] = Future {
    val transactions =
      data
        .foldLeft(Map[String, List[String]]()) {
          case (acc, event) =>
            val transactionId = event.sessionId
            val updatedValue = event.contentId :: acc.getOrElse(transactionId, List.empty)
            acc + (transactionId -> updatedValue)
        }
    transactions
  }

  override def calculateSupportConfidence(transactions: Map[String, List[String]], confidence: Double): Future[Seq[SeededRecommendation]] = Future {
    val n = transactions.size
    val oneItemSets = calculateItemSetsOne(transactions, confidence)
    val twoItemSets = calculateItemSetsTwo(transactions, oneItemSets)
    val rules = calculateAssociationRules(oneItemSets, twoItemSets, n)
    rules
  }

  def calculateItemSetsOne(transactions: Map[String, List[String]], minSupport: Double): Map[Set[String], Int] = {
    val n = transactions.size
    val temp =
      transactions.foldLeft(Map[Set[String], Int]()) {
        case (acc, (k, v)) =>
          val inner = v.foldLeft(Map[Set[String], Int]()) {
            case (inner, item) =>
              val keySet = Set(item)
              val updated = inner.getOrElse(keySet, 0) + 1
              inner + (keySet -> updated)
          }
          acc |+| inner
      }

    // Remove all items that is not supported
    val oneItemSet = {
      temp.foldLeft(Map[Set[String], Int]()) {
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

  def calculateItemSetsTwo(transactions: Map[String, List[String]], oneItemSets: Map[Set[String], Int]): Map[Set[String], Int] = {
    val twoItemSets =
      transactions.foldLeft(Map[Set[String], Int]()) {
        case (acc, (k, v)) =>
          val items = v.toSet.toList //Set(v).toList
          if (items.length > 2) {
            val combinations = items.combinations(2)
            acc |+| combinations.foldLeft(Map[Set[String], Int]()) {
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

  def calculateAssociationRules(oneItemSets: Map[Set[String], Int], twoItemSets: Map[Set[String], Int], n: Int): Seq[SeededRecommendation] = {
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

  def hasSupport(perm: List[String], oneItemSets: Map[Set[String], Int]): Boolean = {
    oneItemSets.contains(Set(perm(0))) && oneItemSets.contains(Set(perm(1)))
  }

}

object AssociationRuleBuilder extends AssociationRuleBuilderInterpreter