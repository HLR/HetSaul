package edu.illinois.cs.cogcomp.lfs.data_model.attribute

import edu.illinois.cs.cogcomp.lbjava.classify.{ Classifier, FeatureVector }
import edu.illinois.cs.cogcomp.lfs.data_model.DataModel
//import example.er_task.datastruct.ConllRelation

import edu.illinois.cs.cogcomp.lfs.data_model.attribute.features.DataSensitiveLBJFeature

import scala.reflect.ClassTag

/** Created by haowu on 2/9/15.
  */
class RelationalFeature[HEAD <: AnyRef, CHILD <: AnyRef](
  val dataModel: DataModel,
  val fts: List[Attribute[CHILD]]
)(implicit val tag: ClassTag[HEAD], val cTag: ClassTag[CHILD])
  extends Attribute[HEAD] // with DataModelSensitiveAttribute[HEAD]
  {

  override type S = List[_]

  override val name: String = s"RelationalFeature of ${tag.toString()} to ${cTag.toString()}"

  override val mapping: (HEAD) => S = {

    head: HEAD =>
      {
        val children = dataModel.getFromRelation[HEAD, CHILD](head)
        children.map({
          c => fts.map(f => f.mapping(c))
        })
      }
  }

  override def makeClassifierWithName(n: String): Classifier = new DataSensitiveLBJFeature {

    this.containingPackage = "LBP_Package"
    this.name = n

    override var datamodel: DataModel = dataModel

    override def getOutputType: String = {
      return "mixed%"
    }

    def classify(__example: AnyRef): FeatureVector = {
      val t: HEAD = __example.asInstanceOf[HEAD]
      val fv: FeatureVector = new FeatureVector()
      val children = dataModel.getFromRelation[HEAD, CHILD](t)

      fts.foreach(x => println(x + " Found!!"))

      children.zipWithIndex.foreach {
        case (c, idx) => {
          def getName(clsName: String) = s"RelationalFeature $clsName at position $idx of ${tag.toString()} to ${cTag.toString()}"
          fts.foreach {
            f =>
              {
                val newName = getName(f.name)
                f.addToFeatureVector(c, fv, newName)
              }
          }
        }
      }
      fv
    }
  }

  override def addToFeatureVector(t: HEAD, fv: FeatureVector): FeatureVector = {

    val children: List[CHILD] = dataModel.getFromRelation[HEAD, CHILD](t)

    //		children foreach print
    children.zipWithIndex.foreach {
      case (c, idx) => {
        def getName(clsName: String) = s" $name:RelationalFeature $clsName at position $idx of ${tag.toString()} to ${cTag.toString()}"
        fts.foreach {
          f =>
            {
              val newName = getName(f.name)
              //						println("Adding features !!")
              f.addToFeatureVector(c, fv, newName)
            }
        }
      }
    }
    fv
  }

  override def addToFeatureVector(t: HEAD, fv: FeatureVector, nameOfClassifier: String): FeatureVector = {
    val TAG_NAME = nameOfClassifier
    val children = dataModel.getFromRelation[HEAD, CHILD](t)
    children.zipWithIndex.foreach {
      case (c, idx) => {
        def getName(clsName: String) = s" $clsName $TAG_NAME:RelationalFeature at position $idx of ${tag.toString()} to ${cTag.toString()}"
        fts.foreach {
          f =>
            {
              val newName = getName(f.name)
              println("Adding features !!")
              f.addToFeatureVector(c, fv, newName)
            }
        }
      }
    }

    fv
  }
}
