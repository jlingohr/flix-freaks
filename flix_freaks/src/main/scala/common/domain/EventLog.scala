package domain

import java.time.Instant


sealed trait EventType
case object genreView extends EventType
case object details extends EventType
case object moreDetails extends EventType
case object addToList extends EventType
case object play extends EventType

case class EventLog(id: Int = 0,
                    created: Instant,
                    userId: String,
                    contentId: String,
                    event: EventType,
                    sessionId: String)


