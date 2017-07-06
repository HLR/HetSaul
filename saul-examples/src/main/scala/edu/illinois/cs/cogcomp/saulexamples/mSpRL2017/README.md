# Spatial Role Labeling using Images

In this example, we have used CLEF image dataset Available at (http://www.imageclef.org/SIAPRdata) for the task of spatial role labeling using images.

The CLEF dataset contains segmentation masks, visual features, labels for the regions and the hierarchical annotation for regions. 

For further information about these attributes please refer to CLEF dataset README.txt 

## mSpRL using Saul
In this example, we implemented and compared the performance of SVM and Naive Bayes for classifying the image segments using the feature set provided by CLEF image dataset using Saul.

### Data representation and preparation

The CLEF image data is made available in different files, we have used the following files in this project 1) wlist.txt, 2) labels.txt, 3) features.txt, 4) spatial_rels mat (Matlab) files 5) training.mat and 6) testing.mat.

`wlist.txt` file contains the vocabulary used for annotation, the format of the file is Id followed by label.

29	branch

30	bridge

These Id are used in other files for referring the associated label.

`label.txt` file contains the codes of segments / objects found in the image, the format of the file is ImageId followed by segment sequence number followed by segment code.
  
25	1	29

25	2	60

25	3	31

means that segments / objects "1", "2" and "3" in image "25" (.jpg) have associated the labels 29 ("branch"), 60 ("cloud") and 31 ("building") respectively.

`feature.txt` file contains visual features extracted for each segment / object of the image. The visual features were extracted using code from Peter Carbonetto [3] and are the following: "region area, width and height of the region, mean and standard deviation in the x and y axis, boundary/area, convexity, average, standard deviation and skewness in RGB and CIE-Lab color spaces, for a total of 27 attributes". the format of the file is as follow:

112	1	0.4417593         0.775     0.8583333     0.5560012     0.6382412      0.656913     0.7427811    0.01397768     0.3359084      119.3438      111.6017      105.8651      67.57657      64.81628      66.64162     0.3635511     0.5000695     0.7093432      68.94983      1.267164      3.321504      17.48109      8.993122      8.798025    -0.0448975     0.1654533      1.945854	120
112	2	0.0189062     0.127083     0.219444     0.932729      0.59748      0.11265     0.170704    0.0624426     0.322059      69.2314      76.0888      70.3459      32.4674      33.4975       32.245      1.57548      1.40818      1.56848      59.4284     -3.34258      1.99991      10.3577      2.65751      1.48654     0.785716    -0.310416      0.87871	204

The file contains 30 columns, columns 1 and 2 are as in the `labels.txt` file, columns 3-29 are the values of the extracted visual features, the last column is as the third column in `labels.txt`. 

`spatial_rels` is a directory containing information of spatial relationships in the images. These relations are stored in a matlab file, with the same name of the image from which they were extracted.  Each file contains three matrices namely topo, x_rels and y_rels. The matrice topo stores the topological relations (i.e. 1: adjacent and 2: disjoint), x_rels matice stores the direction relations with reference to the x axis (i.e. 3: x-aligned and 4: beside) and y_rels matrice stores the direction relations with reference to the y axis (i.e. 5: y-aligned, 6: above and 7: below). 


`training.mat` file contains the list of images (14000 in total) to be used in the training.
  
`testing.mat` file contains the list of images (4000 in total) to be used in the testing.

We need basic data structures to load these data and feed them to the Saul application, therefore, we developed [`Image`](../../../../../../../../java/edu/illinois/cs/cogcomp/nlp/BaseTypes/Image.java), [`Segment`](../../../../../../../../java/edu/illinois/cs/cogcomp/saulexamples/nlp/BaseTypes/Segment.java), and [`SegmentRelation`](../../../../../../../../java/edu/illinois/cs/cogcomp/saulexamples/nlp/BaseTypes/SegmentRelation.java) classes for this purpose.

In `Image` class, all revelant information about image will be stored, in `Segment` class all information about segments such as concept, features etc will be stored and in `SegmentRelation` class information about segment to segment relationship is stored.     

As discussed above image dataset is a collection of different files, therefore, we developed CLEFImageReader for populating appropiate files and also converting image / segment / relation codes to corresponding concepts.

So, the next step is to populate image / segment / relations.

```scala
val CLEFDataset = new CLEFImageReader("/data/mSprl/saiapr_tc-12")

  val imageListTrain = CLEFDataset.trainingImages
  val segementListTrain = CLEFDataset.trainingSegments
  val relationListTrain = CLEFDataset.trainingRelations

  images.populate(imageListTrain)
  segments.populate(segementListTrain)
  relation.populate(relationListTrain)


  val imageListTest = CLEFDataset.testImages
  val segementListTest = CLEFDataset.testSegments
  val relationListTest = CLEFDataset.testRelations

  images.populate(imageListTest, false)
  segments.populate(segementListTest, false)
  relation.populate(relationListTest, false)
 ```
`CLEFImageReader` is the data reader which loads data from the dataset. `CLEFDataset.trainingImages` and `CLEFDataset.trainingSegments` returns all the training images and segments available in the dataset respectively. `CLEFDataset.trainingRelations` returns the relations amoung different segments. 
 
 `CLEFDataset.testImages` and `CLEFDataset.testSegments` returns all the training images and segments available in the dataset respectively. `CLEFDataset.testRelations` returns the relations amoung different segments.


### Defining the `DataModel`
In order to identify spatial relations, images and segments are populated using image reader, the relation `imageSegmentLink` is used to generate relationship between image and its associated segments.
The relation `rel_segment` is used to generate relationship amoung different segments of the image, like above, below, adjanct. 
See [`MultiModalSpRLDataModel`](MultiModalSpRLDataModel.scala)

```scala
  // data model
  val images = node[Image]
  val segments = node[Segment]
  val relation = node[SegmentRelation]

  val image_segment = edge(images, segments)

  val relationsToSegments = edge(relation, segments)
  
```

### Sensors
Next step is to determine sensors:
```scala
  // sensors
    image_segment.addSensor(imageSegmentLink _)
    
    relationsToSegments.addSensor(rel_segment _)
```
The [`imageSegmentLink`](MultiModalSpRLSensors.scala) sensor, generates relationship between image and its associated segements. The [`rel_segment`](MultiModalSpRLSensors.scala) sensor, generates relationship between different segments. 

### Features
Now we can specify the features, all features are constructed using `property` method of `DataModel`. The classifier tries to predict segmentLable, segmentLable is a string like Building, Tree, Group of People etc:
```scala
  // classifier labels
  val imageId = property(images) {
    x: Image => x.getId
  }

  val segmentLable = property(segments) {
    x: Segment => x.getSegmentConcept
  }

  val segmentId = property(segments) {
    x: Segment => x.getSegmentCode
  }
```

### Classification
We used SVM and Naive Bayes classifiers for classifying image segments using the CLEF features. Each feature in CLEF feature set consists of 27 double values, these values are used for classifying the segment. Defining the classifier is straightforward using Saul:

```scala
object ImageClassifiers {
  object ImageSVMClassifier extends Learnable(segments) {
    def label = segmentLable
    override lazy val classifier = new SupportVectorMachine()
    override def feature = using(segmentFeatures)
  }

  object ImageClassifierWeka extends Learnable(segments) {
    def label = segmentLable
    override lazy val classifier = new SaulWekaWrapper(new NaiveBayes())
    override def feature = using(segmentFeatures)
  }
}
```
We extend `Learnable` class of Saul and specify the type of classifier we want. Next the target label for classification is determined by implementing `label` property and finally the set of features needed for classification is provided.
You can find this implementation in [`ImageClassifiers`](MultiModalSpRLClassifiers.scala)

## Test results for mSpRL-2017 data
The IAPR TC-12 Benchmark dataset is used in this example.

Total Training Images: 14000

Total Segments in Training Images: 70086

Total Relations Created Using Training Segments: 1274046

Total Test Images: 4000

Total Segments in Test Images: 19612

Total Relations Created Using Test Segments: 356538

Note: The training or test relation created in this example are not playing any role in classification, however, they will play very important role in Multi-Model Spatial Role Labeling

<pre>
 Results using SVM Classifier
   
       Label         Precision Recall   F1   LCount PCount
----------------------------------------------------------
bed                      0.000  0.000  0.000    143      0
bicycle                  0.000  0.000  0.000     77      0
bird                     0.000  0.000  0.000     59      0
boat                     0.000  0.000  0.000     71      0
bottle                   0.000  0.000  0.000     87      0
branch                   0.000  0.000  0.000     82      1
building                 0.000  0.000  0.000    297      0
bush                     0.000  0.000  0.000    139      0
cactus                   0.000  0.000  0.000     78      0
car                      0.000  0.000  0.000    216      0
castle                   0.000  0.000  0.000     44      0
chair                    0.000  0.000  0.000    122      0
child-boy                0.000  0.000  0.000    117      2
child-girl               0.000  0.000  0.000    116      0
church                   0.000  0.000  0.000     84      0
city                     0.000  0.000  0.000    147      0
cloth                    0.000  0.000  0.000    104      0
cloud                   26.608 40.067 31.979    599    902
column                   0.000  0.000  0.000     74      0
construction-other       0.000  0.000  0.000     40      0
couple-of-persons        0.000  0.000  0.000    248      0
curtain                  0.000  0.000  0.000    101      0
dish                     0.000  0.000  0.000     78      2
door                     0.000  0.000  0.000    128      0
edifice                  0.000  0.000  0.000     73      0
fabric                   0.000  0.000  0.000    211      0
face-of-person          23.502 29.310 26.087    174    217
fence                    0.000  0.000  0.000    115      0
flag                    24.528 20.635 22.414     63     53
floor                    0.000  0.000  0.000    228      1
floor-other              0.000  0.000  0.000    152      0
floor-tennis-court       0.000  0.000  0.000     34      0
floor-wood               0.000  0.000  0.000     65      3
flower                   0.000  0.000  0.000     41     10
flowerbed                0.000  0.000  0.000     45      1
furniture-other          0.000  0.000  0.000     35      0
generic-objects          0.000  0.000  0.000     55      0
glass                    0.000  0.000  0.000     72      0
grass                   23.434 78.150 36.056    627   2091
ground                  15.533 50.477 23.756    733   2382
group-of-persons        15.483 70.366 25.381    793   3604
hand-of-person           0.000  0.000  0.000     95      0
hat                      0.000  0.000  0.000    155      0
highway                100.000  0.980  1.942    102      1
hill                     0.000  0.000  0.000    168      0
horse                    0.000  0.000  0.000     69      0
house                    0.000  0.000  0.000    264      0
hut                      0.000  0.000  0.000     47      0
kitchen-pot              0.000  0.000  0.000     56      0
lake                     0.000  0.000  0.000    116      3
lamp                    33.333  0.662  1.299    151      3
leaf                     0.000  0.000  0.000     32      0
llama                    0.000  0.000  0.000     39      0
man                      9.157 29.912 14.022    799   2610
monument                 0.000  0.000  0.000     32      0
mountain                12.941 38.398 19.358    487   1445
non-wooden-furniture     0.000  0.000  0.000     59      0
ocean                   10.920 10.133 10.512    375    348
painting                 0.000  0.000  0.000    112      0
palm                     0.000  0.000  0.000    129      0
paper                    0.000  0.000  0.000     46      1
person                   0.000  0.000  0.000    258      0
plant                    0.000  0.000  0.000    201      0
plant-pot                0.000  0.000  0.000     54      0
public-sign              0.000  0.000  0.000    134      0
river                    0.000  0.000  0.000    161      0
road                     0.000  0.000  0.000     63      0
rock                    16.327  1.262  2.343    634     49
roof                     0.000  0.000  0.000     90      0
ruin-archeological       0.000  0.000  0.000     68      0
sand-beach               0.000  0.000  0.000    157      0
sand-dessert             0.000  0.000  0.000    102      0
seal                     0.000  0.000  0.000     39      0
ship                     0.000  0.000  0.000     43      0
sidewalk                 0.000  0.000  0.000    180      0
sky                     37.681 23.551 28.986    552    345
sky-blue                43.961 91.637 59.418   1136   2368
sky-light               47.300 68.649 56.009    370    537
sky-night               66.667 20.000 30.769     60     18
sky-red-sunset-dusk      0.000  0.000  0.000     60      2
snow                     0.000  0.000  0.000    101      0
stairs                   0.000  0.000  0.000     57      0
statue                   0.000  0.000  0.000     49      0
street                   0.000  0.000  0.000    205      0
table                    0.000  0.000  0.000    105      0
tower                    0.000  0.000  0.000     35      0
tree                     0.000  0.000  0.000    263      0
trees                   16.726  8.656 11.408    543    281
trunk                    0.000  0.000  0.000     98      0
umbrella                 0.000  0.000  0.000     32      0
vegetation              18.212 28.901 22.344    564    895
wall                     7.576  1.969  3.125    508    132
water                    0.000  0.000  0.000    139      0
water-reflection         0.000  0.000  0.000     60      0
waterfall               33.333  1.724  3.279     58      3
window                   0.000  0.000  0.000    333      0
woman                    0.000  0.000  0.000    507      6
wood                     0.000  0.000  0.000     85      1
wooden-furniture         0.000  0.000  0.000    113      0
----------------------------------------------------------
Accuracy                21.041   -      -      -     18317

</pre>

<pre>

 Results using Naive Bayes Classifier

       Label         Precision Recall   F1   LCount PCount
----------------------------------------------------------
bed                     31.183 20.280 24.576    143     93
bicycle                 12.698 10.390 11.429     77     63
bird                     7.097 18.644 10.280     59    155
boat                     0.000  0.000  0.000     71      9
bottle                   3.670  4.598  4.082     87    109
branch                   0.000  0.000  0.000     82      8
building                23.077  4.040  6.877    297     52
bush                     7.143 29.496 11.501    139    574
cactus                 100.000  1.282  2.532     78      1
car                     14.134 18.519 16.032    216    283
castle                  11.268 18.182 13.913     44     71
chair                    7.143  0.820  1.471    122     14
child-boy               26.667  3.419  6.061    117     15
child-girl              13.000 22.414 16.456    116    200
church                   0.000  0.000  0.000     84      9
city                     9.483 29.932 14.403    147    464
cloth                   12.821  4.808  6.993    104     39
cloud                   26.280 25.710 25.992    599    586
column                   0.000  0.000  0.000     74      0
construction-other       0.000  0.000  0.000     40      1
couple-of-persons        0.000  0.000  0.000    248      1
curtain                 28.571  5.941  9.836    101     21
dish                    17.857 19.231 18.519     78     84
door                     5.060 13.281  7.328    128    336
edifice                  0.000  0.000  0.000     73      0
fabric                  11.111  0.948  1.747    211     18
face-of-person          17.214 63.218 27.060    174    639
fence                   14.286  0.870  1.639    115      7
flag                    13.825 47.619 21.429     63    217
floor                   18.182  0.877  1.674    228     11
floor-other              0.000  0.000  0.000    152     11
floor-tennis-court      28.571 58.824 38.462     34     70
floor-wood              10.465 13.846 11.921     65     86
flower                   9.756 19.512 13.008     41     82
flowerbed               11.364 22.222 15.038     45     88
furniture-other          0.000  0.000  0.000     35      0
generic-objects          0.000  0.000  0.000     55      0
glass                   13.043  4.167  6.316     72     23
grass                   48.854 44.179 46.399    627    567
ground                  27.695 22.783 25.000    733    603
group-of-persons        37.640 55.107 44.729    793   1161
hand-of-person           3.273 21.053  5.666     95    611
hat                      9.924  8.387  9.091    155    131
highway                 13.784 50.000 21.610    102    370
hill                    14.286  4.167  6.452    168     49
horse                    7.767 11.594  9.302     69    103
house                   15.789  1.136  2.120    264     19
hut                      0.000  0.000  0.000     47      0
kitchen-pot              0.000  0.000  0.000     56      0
lake                     5.882  2.586  3.593    116     51
lamp                    14.141  9.272 11.200    151     99
leaf                    18.182 43.750 25.688     32     77
llama                    4.306 23.077  7.258     39    209
man                     26.339 29.537 27.847    799    896
monument                 0.000  0.000  0.000     32      1
mountain                33.698 31.622 32.627    487    457
non-wooden-furniture     0.000  0.000  0.000     59      2
ocean                   30.417 38.933 34.152    375    480
painting                14.583  6.250  8.750    112     48
palm                    20.000  6.202  9.467    129     40
paper                    3.521 10.870  5.319     46    142
person                  11.783 14.341 12.937    258    314
plant                    0.000  0.000  0.000    201      3
plant-pot                0.000  0.000  0.000     54      4
public-sign            100.000  2.239  4.380    134      3
river                    9.091  0.621  1.163    161     11
road                     0.000  0.000  0.000     63      0
rock                    27.778  2.366  4.360    634     54
roof                    25.000  2.222  4.082     90      8
ruin-archeological       4.671 32.353  8.163     68    471
sand-beach               8.225 12.102  9.794    157    231
sand-dessert            10.164 30.392 15.233    102    305
seal                     3.253 48.718  6.100     39    584
ship                     4.762  2.326  3.125     43     21
sidewalk                15.152  5.556  8.130    180     66
sky                     37.864 56.522 45.349    552    824
sky-blue                68.773 65.141 66.908   1136   1076
sky-light               61.323 65.135 63.172    370    393
sky-night               30.435 81.667 44.344     60    161
sky-red-sunset-dusk     26.271 51.667 34.831     60    118
snow                     7.639 21.782 11.311    101    288
stairs                   0.000  0.000  0.000     57      6
statue                   0.000  0.000  0.000     49      2
street                  19.388  9.268 12.541    205     98
table                    0.000  0.000  0.000    105      4
tower                    3.448  2.857  3.125     35     29
tree                    13.539 21.673 16.667    263    421
trees                   23.952 22.099 22.989    543    501
trunk                   18.182 18.367 18.274     98     99
umbrella                 8.333  3.125  4.545     32     12
vegetation              33.153 32.624 32.887    564    555
wall                    41.509  4.331  7.843    508     53
water                   33.333  0.719  1.408    139      3
water-reflection        17.500 11.667 14.000     60     40
waterfall                6.759 67.241 12.283     58    577
window                  18.182  6.006  9.029    333    110
woman                   21.212  1.381  2.593    507     33
wood                     0.000  0.000  0.000     85     16
wooden-furniture         7.491 17.699 10.526    113    267
        ----------------------------------------------------------
Accuracy                23.317   -      -      -     18317


</pre>
## Running
Please download the full dataset from ImageCLEF (http://www.imageclef.org/SIAPRdata).  
Make sure you download all the three parts saiaprtc12ok.part1.rar, saiaprtc12ok.part2.rar and saiaprtc12ok.part3.rar.
Once you download, please extract saiaprtc12ok.part1.rar, it will automatically extract the other two parts and make folders namely matlab and benchmark.
Inside benchmark folder you will find saiapr_tc-12 folder, move the saiapr_tc-12 folder to saul -> data/mSprl folder.
From matlab folder move training.mat, testing.mat and wlist100.txt files inside saul -> data/mSprl/saiapr_tc-12 folder.

After completing the above task, run the main app with default properties:

```
sbt "project saulExamples" "runMain edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.mSpRL2017App"
```

## References
[1] Kordjamshidi, Parisa, Steven Bethard, and Marie-Francine Moens. "SemEval-2012 task 3: Spatial role labeling." Proceedings of the First Joint Conference on Lexical and Computational Semantics. Association for Computational Linguistics, 2012.

[2] Roberts, Kirk, and Sanda M. Harabagiu. "UTD-SpRL: A joint approach to spatial role labeling." Proceedings of the First Joint Conference on Lexical and Computational Semantics. Association for Computational Linguistics, 2012.

[3] http://www.cs.ubc.ca/~pcarbo/