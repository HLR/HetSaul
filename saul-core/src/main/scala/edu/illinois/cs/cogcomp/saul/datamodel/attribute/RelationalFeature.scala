package edu.illinois.cs.cogcomp.saul.datamodel.attribute

import edu.illinois.cs.cogcomp.lbjava.classify.{ Classifier, FeatureVector }
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

import edu.illinois.cs.cogcomp.saul.datamodel.attribute.features.DataSensitiveLBJFeature

import scala.reflect.ClassTag

class RelationalFeature[HEAD <: AnyRef, CHILD <: AnyRef](
  val dataModel: DataModel,
  val fts: List[Attribute[CHILD]]
)(implicit val tag: ClassTag[HEAD], val cTag: ClassTag[CHILD])
  extends Attribute[HEAD] {

  override type S = List[_]

  override val name: String = s"RelationalFeature of ${tag.toString()} to ${cTag.toString()}"

  override val sensor: (HEAD) => S = {

    head: HEAD =>
      val children = dataModel.getFromRelation[HEAD, CHILD](head).toList
      children.map(
        c => fts.map(f => f.sensor(c))
      )
  }

  override def makeClassifierWithName(name: String): Classifier = new DataSensitiveLBJFeature {

    this.containingPackage = "LBP_Package"
    this.name = name

    override var datamodel = dataModel

    override def getOutputType: String = {
      "mixed%"
    }

    def classify(instance: AnyRef): FeatureVector = {
      val typedInstance = instance.asInstanceOf[HEAD]
      val featureVector = new FeatureVector()
      val children = dataModel.getFromRelation[HEAD, CHILD](typedInstance)

      fts.foreach(x => println(x + " Found!!"))

      children.zipWithIndex.foreach {
        case (c, idx) =>
          def getName(className: String) = s"RelationalFeature $className at position $idx of ${tag.toString()} to ${cTag.toString()}"
          fts.foreach { f =>
            val newName = getName(f.name)
            f.addToFeatureVector(c, featureVector, newName)
          }
      }
      featureVector
    }
  }

  override def addToFeatureVector(t: HEAD, featureVector: FeatureVector): FeatureVector = {

    val children: List[CHILD] = dataModel.getFromRelation[HEAD, CHILD](t).toList

    children.zipWithIndex.foreach {
      case (c, idx) =>
        def getName(clsName: String) = s" $name:RelationalFeature $clsName at position $idx of ${tag.toString()} to ${cTag.toString()}"
        fts.foreach { f =>
          val newName = getName(f.name)
          f.addToFeatureVector(c, featureVector, newName)
        }
    }
    featureVector
  }

  override def addToFeatureVector(t: HEAD, featureVector: FeatureVector, nameOfClassifier: String): FeatureVector = {
    val TAG_NAME = nameOfClassifier
    val children = dataModel.getFromRelation[HEAD, CHILD](t)
    children.zipWithIndex.foreach {
      case (c, idx) =>
        def getName(clsName: String) = s" $clsName $TAG_NAME:RelationalFeature at position $idx of ${tag.toString()} to ${cTag.toString()}"
        fts.foreach {
          f =>
            val newName = getName(f.name)
            println("Adding features !!")
            f.addToFeatureVector(c, featureVector, newName)
        }
    }
    featureVector
  }
}
