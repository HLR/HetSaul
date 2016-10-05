/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package controllers

sealed trait Event

// Events the application can received from frontend
object Event {

  final case class DisplayModel() extends Event

  final case class RunMain() extends Event

  final case class PopulateData() extends Event

  final case class Query() extends Event

  final case class GetExample() extends Event
}
