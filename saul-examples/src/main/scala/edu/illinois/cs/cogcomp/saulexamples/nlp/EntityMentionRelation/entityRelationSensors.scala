package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation

import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.reader.GazeteerReader

object entityRelationSensors {
  def cityGazetSensor: GazeteerReader = {
    new GazeteerReader("./data/EntityMentionRelation/known_city.lst", "Gaz:City", true)
  }

  def personGazetSensor: GazeteerReader = {
    val persongazet = new GazeteerReader("./data/EntityMentionRelation/known_maleFirst.lst", "Gaz:Person", true)
    persongazet.addFile("./data/EntityMentionRelation/known_femaleFirst.lst", true)
    persongazet
  }
}
