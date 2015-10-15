package edu.illinois.cs.cogcomp.saul.datamodel.attribute

import java.util

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier
import edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.{ DataSensitiveLBJFeature, ClassifierContainsInLBP }
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.discrete.DiscreteCollectionAttribute
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.discrete.DiscreteAttribute
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.discrete.DiscreteGenAttribute
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.real.RealAttribute
import edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.real.RealGenAttribute

import scala.reflect.ClassTag

/** Created by haowu on 2/8/15.
  */
class AttributeWithWindow[T <: AnyRef](
  var dataModel: DataModel,
  val before: Int,
  val after: Int,
  val filters: Iterable[T => Any],
  val atts: List[Attribute[T]]
)(implicit val tag: ClassTag[T]) extends TypedAttribute[T, List[_]] with DataModelSensitiveAttribute[T] {

  // TODO: need to work on the mapping such that.
  override val sensor: (T) => List[_] = {
    t: T =>
      {
        {

          val ent = dataModel.getNodeWithType[T]

          val winds = ent.getWithWindow(t, before, after)
          // Now we have a windows of option items.

          atts.flatMap(att => {
            winds map {
              case Some(x) => Some(att.sensor(x))
              case _ => None
            }
          })

        }
      }
  }

  override def setDM(dm: DataModel): Unit = {
    super.setDM(dm)
    this.hiddenAttributes = this.rebuildHiddenAttribute(dataModel: DataModel)
  }

  var hiddenAttributes: List[Attribute[T]] = rebuildHiddenAttribute(this.dataModel)

  def rebuildHiddenAttribute(dm: DataModel): List[Attribute[T]] = {
    {
      val ent = dm.getNodeWithType[T]

      atts.toList.flatMap {
        knowAtt: Attribute[T] =>
          {
            {
              type OUTPUT_TYPE = knowAtt.S

              (before to after).map {
                idx =>
                  {
                    {

                      val newName = s"WindowsClassifierAtPosition${idx}<=${knowAtt.name}"
                      //									println(newName)
                      knowAtt match {

                        case da: DiscreteAttribute[T] => {
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
                            new DiscreteAttribute[T](newName, newMappingFunction, da.range)
                          }
                        }
                        case dga: DiscreteGenAttribute[T] => {
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
                            new DiscreteGenAttribute[T](newName, newMappingFunction)
                          }
                        }
                        case ra: RealAttribute[T] => {
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
                            new RealAttribute[T](newName, newMappingFunction)
                          }
                        }
                        case rga: RealGenAttribute[T] => {
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
                          new RealGenAttribute[T](newName, newMappingFunction)
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

  override def addToFeatureVector(t: T, fv: FeatureVector): FeatureVector = {
    // All it need to do is calling the curated classifiers.
    hiddenAttributes.foreach(_.addToFeatureVector(t, fv))
    fv
  }

  override def addToFeatureVector(t: T, fv: FeatureVector, name: String): FeatureVector = {
    // All it need to do is calling the curated classifiers.
    hiddenAttributes.foreach(_.addToFeatureVector(t, fv))
    fv
  }

  override val name: String = {
    s"WindowAtt($before,$after}_Of${this.atts.map(_.name).mkString("|")}})"
  }

  val o = this

  // TODO: use the real classifiers
  override def makeClassifierWithName(n: String): Classifier = new DataSensitiveLBJFeature() {

    val parent = o

    def rebuidWithDM(dm: DataModel) = this.parent.rebuildHiddenAttribute(dm)

    this.containingPackage = "LBP_Package"
    this.name = n

    override def getOutputType: String = {
      "mixed%"
    }

    def classify(__example: AnyRef): FeatureVector = {

      val t: T = __example.asInstanceOf[T]
      val __result: FeatureVector = new FeatureVector()

      parent.hiddenAttributes.foreach(_.addToFeatureVector(t, __result))

      __result
    }

    override def classify(examples: Array[AnyRef]): Array[FeatureVector] = {
      super.classify(examples)
    }

    override def getCompositeChildren: util.LinkedList[_] = {
      val result: util.LinkedList[Classifier] = new util.LinkedList[Classifier]()
      parent.atts.foreach(x => {
        result.add(x.classifier)
      })
      result
    }

    override var datamodel: DataModel = dataModel
  }

}
