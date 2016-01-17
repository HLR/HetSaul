package controllers

sealed trait Event

object Event {

  final case class DisplayModel() extends Event

  final case class RunMain() extends Event

  final case class PopulateData() extends Event

}
