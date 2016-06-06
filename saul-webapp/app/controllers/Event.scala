package controllers

sealed trait Event

// Events the application can received from frontend
object Event {

  final case class DisplayModel() extends Event

  final case class RunMain() extends Event

  final case class PopulateData() extends Event

  final case class Query() extends Event
}
