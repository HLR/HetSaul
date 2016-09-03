# Semantic Role Labeling 
This task is to annotate natural language sentences with semantic roles.  

## Running
To run the main app with default properties:

```
sbt "project saulExamples" "run-main edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlApp"
```

To use a custom configuration file (containing the property keys of `ExamplesConfigurator`):
 
```
 sbt "project saulExamples" "run-main edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlApp config/saul-srl.properties"
```

## Example
Input sentence:  Washington covers Seattle for associated press.
Output with srl labels:
 (covers) [Predicate]
 (covers, Washington) [A0]
 (covers, Associated-Press) [A1]
 (covers, Seattle) [AM-PNC]

## Application Structure
Similar to other applications in Saul, here also we have a datamodel in file `SRLMultiGraphDataModel`, a bunch of single classifier definitions in file `SRLClassifires`, a bunch of constraints to be used by global models during either training or test in file `SRLConstraints`, a bunch of constraied classifiers in file SRLConstrainedClassifiers and the running configurations that are all placed in one file called `SRLApp`.
For using the reader and populating data there is a program in file `PopulateSRLDataModel`.
In contrast to other Saul applications this data model has been defined as a class instead of as an object. The reason is the effciency of the population of the data model, we skip the details of this implementation choice.
However, when making this choice we should be aware that the populated object by the data should be the same as the object that is imported to the Classifiers declaration file.
Since the data model class has been parametrized, to avoid creating new objects when using different parameters, the data model can be rewritten in a way that the properties are parametrized rather than the data model itself, in this case the properties which use framenet frames can recieve it as a parameter individually.
We reffer the reader to see an example of defining such parametrized properties alongwith Learnable classes rather than Learnable objects in DrugResponse example of KnowEng data model in SaulExamples package.
   
There are various machine learning models to solve this including pipelines, learning only models (LO), learning plus inference models (L+I)
 and Joint Learning models (IBT).
 The test units and SRLApp are runnable on a sample `SRLToy` folder in `resources` due to the licencing issues of the full dataset. 
 If you have access to propbank data, you could use the following setting in the `SRLConfigurator`.

```scala
public static final Property TREEBANK_HOME = new Property("treebankHome", "./data/treebank");
public static final Property PROPBANK_HOME = new Property("propbankHome","./data/propbank");
public static final Property TEST_SECTION = new Property("testSection","23");
```
For the results reported in this document, the training is done on folders 2-21 of probbank data and test on the folder 23.
We have designed a number of configurations and the trained models which are packaged and can be tested.

Here, we describe the configurations accompanied in this package and the results that you should get but using those models.
Pred.: Predicate Cand.: Candidate

<pre>

  | Predicate   |      Argument        |  Model                   | Name |
  |-------------|----------------------|--------------------------|------|
  | Gold Pred.  |  Gold Boundaries     | Argument Type Classifier |aTr   |
  | Gold Pred.  |  XuPalmer Candidates | Argument identifier      |bTr   |
  | Gold Pred.  |  XuPalmer Candidates | Argument Type Classifier |cTr   |
  | Pred. Cand. |    N A               | Predicate Classifier     |dTr   |
  | Pred. Cand. |  XuPalmer Candidates | Argument identifier      |eTr   |
  | Pred. Cand  |  XuPalmer Candidates | Argument Type Classifier |fTr   |
  | Gold Pred.  |  Gold Boundries      | Argument Type Classifier |jTr   |
  | Gold Pred.  | Argument Identifier  | Argument Type Classifier |pTr   |

</pre>

#### Training independent models
  * Given gold predicates:
  - [x] **[aTr]** Train `Argument Type Classifier` given gold boundaries

<pre>
    Label   Precision Recall   F1   LCount PCount
   ----------------------------------------------
   A0          94.239 95.556 94.893   3578   3628
   A1          92.057 92.168 92.113   4967   4973
   A2          78.820 73.556 76.097   1108   1034
   A3          72.794 57.558 64.286    172    136
   A4          84.783 76.471 80.412    102     92
   A5         100.000 60.000 75.000      5      3
   AM-ADV      77.723 62.055 69.011    506    404
   AM-CAU      76.119 67.105 71.329     76     67
   AM-DIR      68.966 47.059 55.944     85     58
   AM-DIS      88.927 80.313 84.401    320    289
   AM-EXT      76.190 50.000 60.377     32     21
   AM-LOC      61.732 60.383 61.050    366    358
   AM-MNR      61.724 51.437 56.113    348    290
   AM-MOD      99.458 99.819 99.638    551    553
   AM-NEG      99.127 98.696 98.911    230    229
   AM-PNC      71.591 54.783 62.069    115     88
   AM-PRD       0.000  0.000  0.000      5      0
   AM-REC       0.000  0.000  0.000      2      0
   AM-TMP      83.861 75.828 79.643   1117   1010
   AM-at        0.000  0.000  0.000      0      1
   AM-in        0.000  0.000  0.000      0      1
   C-A0        66.667 33.333 44.444     18      9
   C-A1        87.429 66.812 75.743    229    175
   C-A2        66.667 40.000 50.000      5      3
   C-A3         0.000  0.000  0.000      3      0
   C-A5         0.000  0.000  0.000      0      2
   C-AM-DIR     0.000  0.000  0.000      0     25
   C-AM-LOC     0.000  0.000  0.000      0      2
   C-AM-MNR     0.000  0.000  0.000      0      1
   C-AM-NEG     0.000  0.000  0.000      0    326
   C-AM-PNC     0.000  0.000  0.000      0      1
   C-V         83.544 93.617 88.294    141    158
   R-A0        92.929 86.792 89.756    212    198
   R-A1        76.087 80.153 78.067    131    138
   R-A2        83.333 38.462 52.632     13      6
   R-A3         0.000  0.000  0.000      1      0
   R-A4         0.000  0.000  0.000      1      0
   R-AM-ADV     0.000  0.000  0.000      2     20
   R-AM-CAU     0.000  0.000  0.000      1      0
   R-AM-EXT     0.000  0.000  0.000      1     27
   R-AM-LOC    64.286 56.250 60.000     16     14
   R-AM-MNR     0.000  0.000  0.000      2      4
   R-AM-PNC     0.000  0.000  0.000      0    127
   R-AM-TMP    62.500 27.778 38.462     18      8
   ----------------------------------------------
   Accuracy    85.351   -      -      -     14479
</pre>

  - [x] **[bTr]** Train `Argument identifier` given XuPalmerCandidates

<pre>

   false       97.342 97.935 97.637  27746  27915
   true        95.996 94.875 95.432  14479  14310
   ----------------------------------------------
   Accuracy    96.886   -      -      -     42225

</pre>

  - [x] **[cTr]** Train  `Argument Type Classifier` given XuPalmerCandidates

 argument classifier test results:

<pre>
  Label   Precision Recall   F1   LCount PCount
  -----------------------------------------------
  A0           91.738 89.380 90.544   3578   3486
  A1           90.736 89.128 89.925   4967   4879
  A2           78.449 70.307 74.155   1108    993
  A3           74.627 58.140 65.359    172    134
  A4           87.500 75.490 81.053    102     88
  A5           11.538 60.000 19.355      5     26
  AA            0.000  0.000  0.000      0      4
  AM-ADV       74.545 56.719 64.422    506    385
  AM-CAU       77.586 59.211 67.164     76     58
  AM-DIR       67.797 47.059 55.556     85     59
  AM-DIS       76.812 66.250 71.141    320    276
  AM-EXT       76.190 50.000 60.377     32     21
  AM-LOC       57.955 55.738 56.825    366    352
  AM-MNR       60.714 48.851 54.140    348    280
  AM-MOD       99.088 98.548 98.817    551    548
  AM-NEG       96.552 97.391 96.970    230    232
  AM-PNC       68.132 53.913 60.194    115     91
  AM-PRD        0.000  0.000  0.000      5      0
  AM-REC        0.000  0.000  0.000      2      0
  AM-TMP       81.866 69.919 75.423   1117    954
  AM-in         0.000  0.000  0.000      0     39
  AM-with       0.000  0.000  0.000      0      2
  C-A0         66.667 11.111 19.048     18      3
  C-A1         81.871 61.135 70.000    229    171
  C-A2          0.000  0.000  0.000      5      0
  C-A3          0.000  0.000  0.000      3      0
  C-AM-ADV      0.000  0.000  0.000      0      1
  C-AM-CAU      0.000  0.000  0.000      0    183
  C-AM-LOC      0.000  0.000  0.000      0     19
  C-AM-NEG      0.000  0.000  0.000      0     14
  C-AM-PNC      0.000  0.000  0.000      0      2
  C-V          84.314 91.489 87.755    141    153
  R-A0         88.660 81.132 84.729    212    194
  R-A1         77.165 74.809 75.969    131    127
  R-A2         55.556 38.462 45.455     13      9
  R-A3          0.000  0.000  0.000      1      0
  R-A4          0.000  0.000  0.000      1      0
  R-AM-ADV      0.000  0.000  0.000      2      3
  R-AM-CAU      0.000  0.000  0.000      1      1
  R-AM-EXT      0.000  0.000  0.000      1      7
  R-AM-LOC     77.778 43.750 56.000     16      9
  R-AM-MNR      0.000  0.000  0.000      2      1
  R-AM-PNC      0.000  0.000  0.000      0    428
  R-AM-TMP     33.333 16.667 22.222     18      9
  -----------------------------------------------
  candidate    97.331 98.166 97.746  27746  27984
  -----------------------------------------------
  Overall      82.326 80.972 81.643  14479  14241
  Accuracy     92.270   -      -      -     42225
</pre>

  * Given predicate candidates:
      - [x] **[dTr]** Train `Predicate Classifier`

<pre>
  Label   Precision Recall   F1   LCount PCount
   ----------------------------------------------
   false       99.378 97.907 98.637   1959   1930
   true        99.223 99.771 99.497   5249   5278
   ----------------------------------------------
   Accuracy    99.265   -      -      -      7208
</pre>

   - [x] **[eTr]** Train `Argument identifier` given XuPalmerCandidates
   
<pre>
  Label   Precision Recall   F1   LCount PCount
   ----------------------------------------------
    false       97.947 98.426 98.186  40714  40913
    true        95.511 94.198 94.850  14479  14280
    ----------------------------------------------
    Accuracy    97.317   -      -      -     55193
</pre>
      - [x] **[fTr]**  Train `Argument Type Classifier` given XuPalmerCandidates
<pre>
  argument classifier test results:  Label   Precision Recall   F1   LCount PCount
  -----------------------------------------------
  A0           91.484 88.876 90.162   3578   3476
  A1           90.722 88.987 89.847   4967   4872
  A2           78.278 70.578 74.229   1108    999
  A3           75.362 60.465 67.097    172    138
  A4           88.506 75.490 81.481    102     87
  A5           66.667 80.000 72.727      5      6
  AA            0.000  0.000  0.000      0      4
  AM-ADV       73.590 56.719 64.063    506    390
  AM-CAU       76.667 60.526 67.647     76     60
  AM-DIR       70.175 47.059 56.338     85     57
  AM-DIS       77.695 65.313 70.968    320    269
  AM-EXT       76.190 50.000 60.377     32     21
  AM-LOC       58.310 56.557 57.420    366    355
  AM-MNR       59.857 47.989 53.270    348    279
  AM-MOD       98.165 97.096 97.628    551    545
  AM-NEG       97.778 95.652 96.703    230    225
  AM-PNC       66.667 53.913 59.615    115     93
  AM-PRD        0.000  0.000  0.000      5      0
  AM-REC        0.000  0.000  0.000      2      0
  AM-TMP       81.752 70.188 75.530   1117    959
  AM-in         0.000  0.000  0.000      0     36
  AM-with       0.000  0.000  0.000      0      3
  C-A0         66.667 11.111 19.048     18      3
  C-A1         81.250 62.445 70.617    229    176
  C-A2          0.000  0.000  0.000      5      0
  C-A3          0.000  0.000  0.000      3      0
  C-AM-DIS      0.000  0.000  0.000      0     10
  C-AM-LOC      0.000  0.000  0.000      0      1
  C-AM-NEG      0.000  0.000  0.000      0     26
  C-AM-PNC      0.000  0.000  0.000      0      2
  C-V          84.314 91.489 87.755    141    153
  R-A0         89.583 81.132 85.149    212    192
  R-A1         76.613 72.519 74.510    131    124
  R-A2         60.000 46.154 52.174     13     10
  R-A3          0.000  0.000  0.000      1      0
  R-A4          0.000  0.000  0.000      1      0
  R-AM-ADV      0.000  0.000  0.000      2      2
  R-AM-CAU      0.000  0.000  0.000      1      7
  R-AM-EXT      0.000  0.000  0.000      1      1
  R-AM-LOC     77.778 43.750 56.000     16      9
  R-AM-MNR      0.000  0.000  0.000      2      0
  R-AM-PNC      0.000  0.000  0.000      0    585
  R-AM-TMP     50.000 22.222 30.769     18      8
  -----------------------------------------------
  candidate    97.879 98.590 98.233  40714  41010
  -----------------------------------------------
  Overall      82.479 80.793 81.627  14479  14183
  Accuracy     93.921   -      -      -     55193
</pre>
     - [x] **[gTr]** Train `Argument Type Classifier` separately for main roles and adjuncts (if needed)
<pre>
  argument classifier test results:  Label   Precision Recall   F1   LCount PCount
  -----------------------------------------------
  A0           90.940 89.212 90.068   3578   3510
  A1           90.280 89.007 89.639   4967   4897
  A2           79.131 67.419 72.807   1108    944
  A3           78.512 55.233 64.846    172    121
  A4           86.517 75.490 80.628    102     89
  A5            4.412 60.000  8.219      5     68
  -----------------------------------------------
  candidate    98.113 98.769 98.440  45261  45564
  -----------------------------------------------
  Overall      88.638 85.934 87.265   9932   9629
  Accuracy     96.460   -      -      -     55193
</pre>

<pre>
  argument classifier test results:  Label   Precision Recall   F1   LCount PCount
  -----------------------------------------------
  AM-ADV       70.694 54.348 61.453    506    389
  AM-CAU       75.862 57.895 65.672     76     58
  AM-DIR       77.083 43.529 55.639     85     48
  AM-DIS       75.735 64.375 69.595    320    272
  AM-EXT       69.565 50.000 58.182     32     23
  AM-LOC       60.299 55.191 57.632    366    335
  AM-MNR       60.920 45.690 52.217    348    261
  AM-MOD       97.810 97.278 97.543    551    548
  AM-NEG       97.333 95.217 96.264    230    225
  AM-PNC       64.773 49.565 56.158    115     88
  AM-PRD        0.000  0.000  0.000      5      0
  AM-REC        0.000  0.000  0.000      2      0
  AM-TMP       81.571 69.740 75.193   1117    955
  AM-in         0.000  0.000  0.000      0     22
  AM-with       0.000  0.000  0.000      0      2
  C-A0         75.000 16.667 27.273     18      4
  C-A1         78.698 58.079 66.834    229    169
  C-A2          0.000  0.000  0.000      5      1
  C-A3          0.000  0.000  0.000      3      0
  C-AM-DIS      0.000  0.000  0.000      0      9
  C-AM-NEG      0.000  0.000  0.000      0     16
  C-AM-PNC      0.000  0.000  0.000      0      1
  C-V          80.892 90.071 85.235    141    157
  R-A0         89.305 78.774 83.709    212    187
  R-A1         74.265 77.099 75.655    131    136
  R-A2         57.143 30.769 40.000     13      7
  R-A3          0.000  0.000  0.000      1      1
  R-A4          0.000  0.000  0.000      1      0
  R-AM-ADV      0.000  0.000  0.000      2      9
  R-AM-CAU      0.000  0.000  0.000      1      1
  R-AM-EXT      0.000  0.000  0.000      1      0
  R-AM-LOC     62.500 31.250 41.667     16      8
  R-AM-MNR      0.000  0.000  0.000      2      0
  R-AM-PNC      0.000  0.000  0.000      0    193
  R-AM-TMP     33.333 11.111 16.667     18      6
  -----------------------------------------------
  candidate    98.380 99.188 98.783  50646  51062
  -----------------------------------------------
  Overall      74.365 67.561 70.800   4547   4131
  Accuracy     96.583   -      -      -     55193

</pre>

  - [x] **[hTr]** Use pipeline of identification for training examples.

#### Second phase: test independent models

 -[x] **[aTs]** Test **aTr, bTr, cTr, dTr, eTr, fTr, gTr** independently.
 -aTr with constraints.

<pre>

  Label   Precision Recall    F1   LCount PCount
  -----------------------------------------------
  A0          95.468  96.562 96.012   3578   3619
  A1          94.723  91.786 93.231   4967   4813
  A2          82.353  74.549 78.257   1108   1003
  A3          73.381  59.302 65.595    172    139
  A4          80.198  79.412 79.803    102    101
  A5          55.556 100.000 71.429      5      9
  AA           0.000   0.000  0.000      0      1
  AM-ADV      76.977  65.415 70.726    506    430
  AM-CAU      73.239  68.421 70.748     76     71
  AM-DIR      61.250  57.647 59.394     85     80
  AM-DIS      86.957  81.250 84.006    320    299
  AM-EXT      73.913  53.125 61.818     32     23
  AM-LOC      60.256  64.208 62.169    366    390
  AM-MNR      60.123  56.322 58.160    348    326
  AM-MOD      93.537  99.819 96.576    551    588
  AM-NEG      50.000  99.130 66.472    230    456
  AM-PNC      64.948  54.783 59.434    115     97
  AM-PRD     100.000  20.000 33.333      5      1
  AM-REC       0.000   0.000  0.000      2     24
  AM-TMP      83.189  77.529 80.259   1117   1041
  C-A0        71.429  55.556 62.500     18     14
  C-A1        87.845  69.432 77.561    229    181
  C-A2        66.667  40.000 50.000      5      3
  C-A3         0.000   0.000  0.000      3      0
  C-A5         0.000   0.000  0.000      0      1
  C-AM-CAU     0.000   0.000  0.000      0      1
  C-AM-DIR     0.000   0.000  0.000      0     29
  C-AM-DIS     0.000   0.000  0.000      0      6
  C-AM-EXT     0.000   0.000  0.000      0      4
  C-AM-LOC     0.000   0.000  0.000      0      5
  C-AM-NEG     0.000   0.000  0.000      0    284
  C-V          0.000   0.000  0.000    141      0
  R-A0        89.151  89.151 89.151    212    212
  R-A1        77.206  80.153 78.652    131    136
  R-A2        55.556  38.462 45.455     13      9
  R-A3        33.333 100.000 50.000      1      3
  R-A4         0.000   0.000  0.000      1      1
  R-AM-ADV     0.000   0.000  0.000      2     33
  R-AM-CAU     0.000   0.000  0.000      1      8
  R-AM-EXT     0.000   0.000  0.000      1      1
  R-AM-LOC   100.000  31.250 47.619     16      5
  R-AM-MNR     0.000   0.000  0.000      2      2
  R-AM-PNC     0.000   0.000  0.000      0     16
  -----------------------------------------------
  R-AM-TMP    50.000  38.889 43.750     18     14
  -----------------------------------------------
  Overall     85.358  85.358 85.358  14479  14479
  Accuracy    85.358    -      -      -     14479
</pre>

  -[aTr] test with pipeline of identification without constraints.

<pre>
  Label   Precision Recall   F1   LCount PCount
  -----------------------------------------------
  A0           90.719 89.603 90.157   3578   3534
  A1           89.630 90.135 89.882   4967   4995
  A2           77.110 71.751 74.334   1108   1031
  A3           70.290 56.395 62.581    172    138
  A4           79.787 73.529 76.531    102     94
  A5          100.000 60.000 75.000      5      3
  AM-ADV       73.067 57.905 64.609    506    401
  AM-CAU       74.627 65.789 69.930     76     67
  AM-DIR       67.857 44.706 53.901     85     56
  AM-DIS       76.384 64.688 70.051    320    271
  AM-EXT       69.565 50.000 58.182     32     23
  AM-LOC       60.000 58.197 59.085    366    355
  AM-MNR       60.690 50.575 55.172    348    290
  AM-MOD       97.645 97.822 97.733    551    552
  AM-NEG       97.357 96.087 96.718    230    227
  AM-PNC       70.115 53.043 60.396    115     87
  AM-PRD        0.000  0.000  0.000      5      0
  AM-REC        0.000  0.000  0.000      2      0
  AM-TMP       81.078 71.352 75.905   1117    983
  AM-at         0.000  0.000  0.000      0      1
  AM-in         0.000  0.000  0.000      0      1
  C-A0         50.000 11.111 18.182     18      4
  C-A1         81.609 62.009 70.471    229    174
  C-A2          0.000  0.000  0.000      5      1
  C-A3          0.000  0.000  0.000      3      0
  C-A5          0.000  0.000  0.000      0      2
  C-AM-DIR      0.000  0.000  0.000      0     25
  C-AM-LOC      0.000  0.000  0.000      0      2
  C-AM-MNR      0.000  0.000  0.000      0      1
  C-AM-NEG      0.000  0.000  0.000      0    325
  C-AM-PNC      0.000  0.000  0.000      0      1
  C-V          83.226 91.489 87.162    141    155
  R-A0         89.840 79.245 84.211    212    187
  R-A1         72.727 73.282 73.004    131    132
  R-A2         83.333 38.462 52.632     13      6
  R-A3          0.000  0.000  0.000      1      0
  R-A4          0.000  0.000  0.000      1      0
  R-AM-ADV      0.000  0.000  0.000      2     19
  R-AM-CAU      0.000  0.000  0.000      1      0
  R-AM-EXT      0.000  0.000  0.000      1     26
  R-AM-LOC     60.000 37.500 46.154     16     10
  R-AM-MNR      0.000  0.000  0.000      2      1
  R-AM-PNC      0.000  0.000  0.000      0    122
  R-AM-TMP     25.000 11.111 15.385     18      8
  -----------------------------------------------
  candidate    97.342 97.935 97.637  27746  27915
  -----------------------------------------------
  Overall      82.558 81.594 82.073  14479  14310
  Accuracy     92.332   -      -      -     42225
</pre>

  Same experiment if we use [cTr] instead of [aTr]

<pre>
  -----------------------------------------------
  candidate    96.948 98.331 97.635  27746  28142
  -----------------------------------------------
  Overall      82.490 80.233 81.346  14479  14083
  Accuracy     92.126   -      -      -     42225
</pre>

   - [x] **[cTr]** Test with constraints

<pre>

   Label   Precision Recall    F1   LCount PCount
   ------------------------------------------------
   A0           93.099  90.497 91.780   3578   3478
   A1           93.839  88.927 91.317   4967   4707
   A2           81.631  71.390 76.168   1108    969
   A3           73.188  58.721 65.161    172    138
   A4           86.170  79.412 82.653    102     94
   A5           45.455 100.000 62.500      5     11
   AM-ADV       74.177  57.905 65.039    506    395
   AM-CAU       77.966  60.526 68.148     76     59
   AM-DIR       63.158  56.471 59.627     85     76
   AM-DIS       71.761  67.500 69.565    320    301
   AM-EXT       69.565  50.000 58.182     32     23
   AM-LOC       56.300  57.377 56.834    366    373
   AM-MNR       58.361  51.149 54.518    348    305
   AM-MOD       96.106  98.548 97.312    551    565
   AM-NEG       87.843  97.391 92.371    230    255
   AM-PNC       63.265  53.913 58.216    115     98
   AM-PRD        0.000   0.000  0.000      5      0
   AM-REC        0.000   0.000  0.000      2     68
   AM-TMP       81.402  71.710 76.249   1117    984
   C-A0         57.143  22.222 32.000     18      7
   C-A1         84.756  60.699 70.738    229    164
   C-A2          0.000   0.000  0.000      5      0
   C-A3          0.000   0.000  0.000      3      0
   C-A5          0.000   0.000  0.000      0      1
   C-AM-ADV      0.000   0.000  0.000      0      1
   C-AM-CAU      0.000   0.000  0.000      0    390
   C-AM-DIR      0.000   0.000  0.000      0      1
   C-AM-DIS      0.000   0.000  0.000      0    130
   C-AM-LOC      0.000   0.000  0.000      0     23
   C-AM-NEG      0.000   0.000  0.000      0     52
   C-V           0.000   0.000  0.000    141      0
   R-A0         82.464  82.075 82.270    212    211
   R-A1         71.186  64.122 67.470    131    118
   R-A2         26.667  30.769 28.571     13     15
   R-A3          0.000   0.000  0.000      1      1
   R-A4          0.000   0.000  0.000      1      0
   R-AM-ADV      0.000   0.000  0.000      2     14
   R-AM-CAU      0.000   0.000  0.000      1      3
   R-AM-EXT      0.000   0.000  0.000      1      1
   R-AM-LOC    100.000  31.250 47.619     16      5
   R-AM-MNR      0.000   0.000  0.000      2      3
   R-AM-PNC      0.000   0.000  0.000      0     24
   R-AM-TMP     16.129  27.778 20.408     18     31
   ------------------------------------------------
   candidate    97.131  98.479 97.801  27746  28131
   ------------------------------------------------
   Overall      82.908  80.703 81.791  14479  14094
   Accuracy     92.384    -      -      -     42225

</pre>

   - [x] **[fTr]** Test with constraints

<pre>
   Label   Precision Recall    F1   LCount PCount
   ------------------------------------------------
   A0           93.316  89.743 91.495   3578   3441
   A1           93.827  88.746 91.216   4967   4698
   A2           81.474  71.841 76.355   1108    977
   A3           73.759  60.465 66.454    172    141
   A4           86.022  78.431 82.051    102     93
   A5           55.556 100.000 71.429      5      9
   AM-ADV       73.762  58.893 65.495    506    404
   AM-CAU       75.806  61.842 68.116     76     62
   AM-DIR       64.789  54.118 58.974     85     71
   AM-DIS       73.702  66.563 69.951    320    289
   AM-EXT       72.727  50.000 59.259     32     22
   AM-LOC       57.180  59.836 58.478    366    383
   AM-MNR       58.387  52.011 55.015    348    310
   AM-MOD       97.806  97.096 97.450    551    547
   AM-NEG       97.345  95.652 96.491    230    226
   AM-PNC       61.386  53.913 57.407    115    101
   AM-PRD        0.000   0.000  0.000      5      0
   AM-REC        0.000   0.000  0.000      2      1
   AM-TMP       80.827  71.710 75.996   1117    991
   C-A0         66.667  22.222 33.333     18      6
   C-A1         83.140  62.445 71.322    229    172
   C-A2          0.000   0.000  0.000      5      0
   C-A3          0.000   0.000  0.000      3      0
   C-AM-DIS      0.000   0.000  0.000      0    550
   C-AM-EXT      0.000   0.000  0.000      0      3
   C-AM-NEG      0.000   0.000  0.000      0     45
   C-AM-TMP      0.000   0.000  0.000      0      3
   C-V           0.000   0.000  0.000    141      0
   R-A0         83.732  82.547 83.135    212    209
   R-A1         70.339  63.359 66.667    131    118
   R-A2         25.000  30.769 27.586     13     16
   R-A3          0.000   0.000  0.000      1      3
   R-A4          0.000   0.000  0.000      1      0
   R-AM-ADV      0.000   0.000  0.000      2      3
   R-AM-CAU      0.000   0.000  0.000      1      4
   R-AM-EXT      0.000   0.000  0.000      1      0
   R-AM-LOC    100.000  37.500 54.545     16      6
   R-AM-MNR      0.000   0.000  0.000      2      1
   R-AM-PNC      0.000   0.000  0.000      0     27
   R-AM-TMP     35.714  27.778 31.250     18     14
   ------------------------------------------------
   candidate    96.644  98.501 97.564  27746  28279
   ------------------------------------------------
   Overall      83.623  80.544 82.055  14479  13946
   Accuracy     92.343    -      -      -     42225
</pre>

   - [ ] **[bTs]** Test `pipe1Model` : **dTr** => **eTr** (given identified predicates) => **fTr** (given identified arguments)
    - [ ] **[cTs]** Test **dTr, eTr, fTr** jointly given various number of constraints
      * Add constraints gradually and test.

#### Third phase: training joint models

   - [ ] **[aTrJ]** Train **dTr, eTr, fTr** jointly
      * Add constraints gradually and train various models considering subsets of constraints
    - [ ] **[aTr]**

#### Fourth phase: testing joint models

   - [ ] **[aTsJ]** Test the **cTs** of the second phase for joint models.

The defaul configuration when running the sprlApp will run only the test for pretrained cTr model while it uses srl global constraints during prediction.
You can run it from command line by:

```scala

sbt -mem 4000 "project saulExamples" "run-main edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.SRLApps"

```
