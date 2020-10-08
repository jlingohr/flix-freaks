package main.scala.recommender.repository

import domain.EventType

import scala.concurrent.Future

trait EventRepository {

  def filterContentByEvent(event: EventType, take: Int): Future[Seq[(String, Int)]]

}
