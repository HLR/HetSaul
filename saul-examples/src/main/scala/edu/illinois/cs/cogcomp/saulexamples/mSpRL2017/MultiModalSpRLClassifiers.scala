/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.lbjava.learn.{SparseAveragedPerceptron, SparseNetworkLearner, SupportVectorMachine}
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.datamodel.property.Property
import edu.illinois.cs.cogcomp.saul.learn.SaulWekaWrapper
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers.FeatureSets
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers.FeatureSets.FeatureSets
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes._
import weka.classifiers.`lazy`.IBk
import weka.classifiers.bayes.NaiveBayes

object MultiModalSpRLClassifiers {
  var featureSet = FeatureSets.WordEmbeddingPlusImage

  def phraseFeatures: List[Property[Phrase]] = phraseFeatures(featureSet)

  def phraseFeatures(featureSet: FeatureSets): List[Property[Phrase]] =
    List(wordForm, headWordFrom, pos, headWordPos, phrasePos, semanticRole, dependencyRelation, subCategorization,
      spatialContext, headSpatialContext, headDependencyRelation, headSubCategorization) ++
      (featureSet match {
        case FeatureSets.BaseLineWithImage => List(isImageConcept)
        case FeatureSets.WordEmbedding => List(headVector)
        case FeatureSets.WordEmbeddingPlusImage => List(headVector, nearestSegmentConceptToHeadVector)
        case _ => List[Property[Phrase]]()
      })

  def pairFeatures: List[Property[Relation]] = pairFeatures(featureSet)

  def pairFeatures(featureSet: FeatureSets): List[Property[Relation]] =
    List(pairWordForm, pairHeadWordForm, pairPos, pairHeadWordPos, pairPhrasePos,
      pairSemanticRole, pairDependencyRelation, pairSubCategorization, pairHeadSpatialContext,
      distance, before, isTrajectorCandidate, isLandmarkCandidate, isIndicatorCandidate) ++
      (featureSet match {
        case FeatureSets.BaseLineWithImage => List(pairIsImageConcept)
        case FeatureSets.WordEmbedding => List(pairTokensVector)
        case FeatureSets.WordEmbeddingPlusImage => List(pairTokensVector, pairNearestSegmentConceptToHeadVector,
          pairNearestSegmentConceptToPhraseVector, pairIsImageConcept)
        case _ => List[Property[Relation]]()
      })

  def tripletFeatures: List[Property[Relation]] = tripletFeatures(featureSet)

  def tripletFeatures(featureSet: FeatureSets): List[Property[Relation]] =
    List(tripletWordForm, tripletHeadWordForm, tripletPos, tripletHeadWordPos, tripletPhrasePos,
      tripletSemanticRole, tripletDependencyRelation, tripletSubCategorization, tripletSpatialContext, tripletHeadSpatialContext) ++
      (featureSet match {
        case FeatureSets.BaseLineWithImage => List()
        case FeatureSets.WordEmbedding => List(tripletTokensVector)
        case FeatureSets.WordEmbeddingPlusImage => List(tripletTokensVector)
        case _ => List[Property[Relation]]()
      })

  object ImageSVMClassifier extends Learnable(segments) {
    def label = segmentLabel

    override lazy val classifier = new SupportVectorMachine()

    override def feature = using(segmentFeatures)
  }

  object ImageClassifierWeka extends Learnable(segments) {
    def label = segmentLabel

    override lazy val classifier = new SaulWekaWrapper(new NaiveBayes())

    override def feature = using(segmentFeatures)
  }

  object ImageClassifierWekaIBK extends Learnable(segments) {
    def label = segmentLabel

    override lazy val classifier = new SaulWekaWrapper(new IBk())

    override def feature = using(segmentFeatures)
  }

  object SpatialRoleClassifier extends Learnable(phrases) {
    def label = spatialRole

    override lazy val classifier = new SparseNetworkLearner()

    override def feature = phraseFeatures
  }

  object TrajectorRoleClassifier extends Learnable(phrases) {
    def label = trajectorRole

    override lazy val classifier = new SparseNetworkLearner {
      val p = new SparseAveragedPerceptron.Parameters()
      p.learningRate = .1
      p.thickness = 2
      baseLTU = new SparseAveragedPerceptron(p)
    }

    override def feature = phraseFeatures
  }

  object LandmarkRoleClassifier extends Learnable(phrases) {
    def label = landmarkRole

    override lazy val classifier = new SparseNetworkLearner {
      val p = new SparseAveragedPerceptron.Parameters()
      p.learningRate = .1
      p.thickness = 2
      baseLTU = new SparseAveragedPerceptron(p)

    }

    override def feature = (phraseFeatures ++ List(lemma, headWordLemma))
      .diff(List(isImageConcept))
  }

  object IndicatorRoleClassifier extends Learnable(phrases) {
    def label = indicatorRole

    override lazy val classifier = new SparseNetworkLearner {
      val p = new SparseAveragedPerceptron.Parameters()
      p.learningRate = .1
      p.thickness = 2
      baseLTU = new SparseAveragedPerceptron(p)
    }

    override def feature = (phraseFeatures(FeatureSets.BaseLine) ++ List(headSubCategorization))
      .diff(List(headWordPos, headWordFrom, headDependencyRelation, isImageConcept))
  }

  object TrajectorPairClassifier extends Learnable(pairs) {
    def label = isTrajectorRelation

    override lazy val classifier = new SparseNetworkLearner {
      val p = new SparseAveragedPerceptron.Parameters()
      p.learningRate = .1
      p.positiveThickness = 2
      p.negativeThickness = 1
      //p.thickness = 4
      baseLTU = new SparseAveragedPerceptron(p)
    }

    override def feature = (pairFeatures ++ List(relationHeadDependencyRelation, relationHeadSubCategorization))
      .diff(List(pairNearestSegmentConceptToHeadVector))
  }

  object LandmarkPairClassifier extends Learnable(pairs) {
    def label = isLandmarkRelation

    override lazy val classifier = new SparseNetworkLearner {
      val p = new SparseAveragedPerceptron.Parameters()
      p.learningRate = .1
      p.positiveThickness = 4
      p.negativeThickness = 1
      baseLTU = new SparseAveragedPerceptron(p)
    }

    override def feature = (pairFeatures ++ List(relationSpatialContext))
      .diff(List(pairIsImageConcept, pairNearestSegmentConceptToPhraseVector))
  }


  object TripletGeneralTypeClassifier extends Learnable(triplets) {
    def label = tripletGeneralType

    override lazy val classifier = new SparseNetworkLearner {
      val p = new SparseAveragedPerceptron.Parameters()
      p.learningRate = .1
      p.positiveThickness = 4
      p.negativeThickness = 1
      baseLTU = new SparseAveragedPerceptron(p)
    }

    override def feature = tripletFeatures
  }

  object TripletSpecificTypeClassifier extends Learnable(triplets) {
    def label = tripletSpecificType

    override lazy val classifier = new SparseNetworkLearner {
      val p = new SparseAveragedPerceptron.Parameters()
      p.learningRate = .1
      p.positiveThickness = 4
      p.negativeThickness = 1
      baseLTU = new SparseAveragedPerceptron(p)
    }

    override def feature = tripletFeatures
  }

  object TripletRCC8Classifier extends Learnable(triplets) {
    def label = tripletRCC8

    override lazy val classifier = new SparseNetworkLearner {
      val p = new SparseAveragedPerceptron.Parameters()
      p.learningRate = .1
      p.positiveThickness = 4
      p.negativeThickness = 1
      baseLTU = new SparseAveragedPerceptron(p)
    }

    override def feature = tripletFeatures
  }


  object TripletFoRClassifier extends Learnable(triplets) {
    def label = tripletFoR

    override lazy val classifier = new SparseNetworkLearner {
      val p = new SparseAveragedPerceptron.Parameters()
      p.learningRate = .1
      p.positiveThickness = 4
      p.negativeThickness = 1
      baseLTU = new SparseAveragedPerceptron(p)
    }

    override def feature = tripletFeatures
  }

  object TripletClassifier extends Learnable(triplets) {
    def label = tripletIsRelation

    override lazy val classifier = new SparseNetworkLearner {
      val p = new SparseAveragedPerceptron.Parameters()
      p.learningRate = .1
      p.positiveThickness = 4
      p.negativeThickness = 1
      baseLTU = new SparseAveragedPerceptron(p)
    }

    override def feature = tripletFeatures
  }


}
