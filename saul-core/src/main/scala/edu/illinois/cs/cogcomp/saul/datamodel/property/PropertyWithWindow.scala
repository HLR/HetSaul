/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.datamodel.property

import edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node
import edu.illinois.cs.cogcomp.saul.datamodel.property.features.discrete.DiscreteProperty
import edu.illinois.cs.cogcomp.saul.datamodel.property.features.discrete.DiscreteGenProperty
import edu.illinois.cs.cogcomp.saul.datamodel.property.features.real.RealProperty
import edu.illinois.cs.cogcomp.saul.datamodel.property.features.real.RealGenProperty

import scala.reflect.ClassTag

class PropertyWithWindow[T <: AnyRef](
  var node: Node[T],
  val before: Int,
  val after: Int,
  val filters: Iterable[T => Any],
  val properties: List[Property[T]]
)(implicit val tag: ClassTag[T]) extends TypedProperty[T, List[_]] {

  // TODO: need to work on the mapping such that.
  override val sensor: (T) => List[_] = {
    t: T =>
      val winds = node.getWithWindow(t, before, after)
      // Now we have a windows of option items.

      properties.flatMap(property =>
        winds.map {
          case Some(x) => Some(property.sensor(x))
          case _ => None
        })
  }

  var hiddenProperties: List[Property[T]] = rebuildHiddenProperties()

  def rebuildHiddenProperties(): List[Property[T]] = {
    {
      val ent = node

      properties.toList.flatMap {
        knowProperty: Property[T] =>
          {
            {
              type OUTPUT_TYPE = knowProperty.S

              (before to after).map {
                idx =>
                  {
                    {

                      val newName = s"WindowsClassifierAtPosition${idx}<=${knowProperty.name}"
                      //									println(newName)
                      knowProperty match {

                        case da: DiscreteProperty[T] => {
                          {
                            val newMappingFunction: T => String = {
                              t: T =>
                                {
                                  {
                                    ent.getWithRelativePosition(t, idx, filters) match {
                                      case Some(target) => {
                                        {
                                          da.sensor(target)
                                        }
                                      }
                                      case _ => {
                                        {
                                          "***BLANK***"
                                        }
                                      }
                                    }
                                  }
                                }
                            }
                            new DiscreteProperty[T](newName, newMappingFunction, da.range)
                          }
                        }
                        case dga: DiscreteGenProperty[T] => {
                          {
                            val newMappingFunction: T => List[String] = {
                              t: T =>
                                {

                                  ent.getWithRelativePosition(t, idx, filters) match {
                                    case Some(target) =>
                                      dga.sensor(target)
                                    case _ => Nil
                                  }
                                }
                            }
                            new DiscreteGenProperty[T](newName, newMappingFunction)
                          }
                        }
                        case ra: RealProperty[T] => {
                          {
                            val newMappingFunction: T => Double = {
                              t: T =>
                                {
                                  {
                                    //								ent.Nil
                                    ent.getWithRelativePosition(t, idx, filters) match {
                                      case Some(target) => {
                                        {
                                          ra.sensor(target)
                                        }
                                      }
                                      case _ => {
                                        {
                                          0
                                        }
                                      }
                                    }
                                  }
                                }
                            }
                            new RealProperty[T](newName, newMappingFunction)
                          }
                        }
                        case rga: RealGenProperty[T] => {
                          val newMappingFunction: T => List[Double] = {
                            t: T =>
                              {
                                ent.getWithRelativePosition(t, idx, filters) match {
                                  case Some(target) => {
                                    rga.sensor(target)
                                  }
                                  case _ => {
                                    Nil
                                  }
                                }
                              }
                          }
                          new RealGenProperty[T](newName, newMappingFunction)
                        }
                        case _ => {
                          throw new Exception("Can't combine classifier with ranges")
                        }
                      }

                    }
                  }
              }.toList
            }
          }
      }
    }
  }

  override val name: String = {
    s"WindowProperty($before,$after}_Of${this.properties.map(_.name).mkString("|")}})"
  }

  override def featureVector(instance: T): FeatureVector = {
    val result: FeatureVector = new FeatureVector()
    hiddenProperties.foreach(property => result.addFeatures(property.featureVector(instance)))
    result
  }
}
