# Question Type Classification 

The goal is to categorize questions into different semantic classes based on the possible semantic types of the answers. 
We develop a hierarchical classifier guided by a layered semantic hierarchy of answer
types that makes use of a sequential model for multi-class classification
Question classification would benefit question answering process further if it has the capacity to distinguish 
between a large and complex set of finer classes. 

We define a two-layered taxonomy, which represents a natural semantic classification
for typical answers. The hierarchy contains 6 coarse classes: 
(`ABBREVIATION`, `DESCRIPTION`, `ENTITY`, `HUMAN`, `LOCATION` and `NUMERIC VALUE`) and
50 fine classes.
    
![](../../../../../../../../resources/QuestionTypeClassification/categories.png)


More details can be found in [this](http://cogcomp.cs.illinois.edu/page/publication_view/130) paper. 
The input data for training/testing the system can be 
downloaded from [here](http://cogcomp.cs.illinois.edu/page/resource_view/49).  

## Performance 
Some ablation study is included [here](https://docs.google.com/spreadsheets/d/1Amb-tphGHg0OSbjlFi5zQvPX72sR4axCLDW6JBSHHS0/edit?usp=sharing).

Here is the performance of the coarse-label classifier: 

```
  Label   Precision Recall   F1   LCount PCount
 ----------------------------------------------
 ABBR       100.000 71.429 83.333      7      5
 DESC        84.416 97.744 90.592    133    154
 ENTY        85.526 69.149 76.471     94     76
 HUM         85.915 93.846 89.706     65     71
 LOC         86.747 90.000 88.344     80     83
 NUM         96.040 87.387 91.509    111    101
 ----------------------------------------------
 Accuracy    87.755   -      -      -       490
```

And here is the performance of the fine-labels classifier: 
```
     Label      Precision Recall    F1    LCount PCount
 ------------------------------------------------------
 ABBR:abb         100.000 100.000 100.000      1      1
 ABBR:exp         100.000  66.667  80.000      6      4
 DESC:def          87.023  96.610  91.566    118    131
 DESC:desc         60.000  85.714  70.588      7     10
 DESC:manner       50.000 100.000  66.667      2      4
 DESC:reason      100.000  83.333  90.909      6      5
 ENTY:animal       92.308  75.000  82.759     16     13
 ENTY:body        100.000  50.000  66.667      2      1
 ENTY:color       100.000 100.000 100.000     10     10
 ENTY:cremat        0.000   0.000   0.000      0      2
 ENTY:currency    100.000  83.333  90.909      6      5
 ENTY:dismed       25.000  50.000  33.333      2      4
 ENTY:event         0.000   0.000   0.000      2      0
 ENTY:food        100.000 100.000 100.000      4      4
 ENTY:instru      100.000 100.000 100.000      1      1
 ENTY:lang        100.000 100.000 100.000      2      2
 ENTY:other        40.000  33.333  36.364     12     10
 ENTY:plant       100.000  40.000  57.143      5      2
 ENTY:product     100.000  25.000  40.000      4      1
 ENTY:sport       100.000 100.000 100.000      1      1
 ENTY:substance    87.500  46.667  60.870     15      8
 ENTY:techmeth     33.333 100.000  50.000      1      3
 ENTY:termeq       77.778 100.000  87.500      7      9
 ENTY:veh          50.000  75.000  60.000      4      6
 HUM:desc         100.000 100.000 100.000      3      3
 HUM:gr           100.000  66.667  80.000      6      4
 HUM:ind           90.164 100.000  94.828     55     61
 HUM:title          0.000   0.000   0.000      1      1
 LOC:city          93.333  82.353  87.500     17     15
 LOC:country       75.000 100.000  85.714      3      4
 LOC:mount        100.000  66.667  80.000      3      2
 LOC:other         84.615  88.000  86.275     50     52
 LOC:state         58.333 100.000  73.684      7     12
 NUM:count         88.889 100.000  94.118      8      9
 NUM:date          97.872  97.872  97.872     47     47
 NUM:dist         100.000  46.667  63.636     15      7
 NUM:money         33.333  33.333  33.333      3      3
 NUM:other         83.333  41.667  55.556     12      6
 NUM:perc          66.667  66.667  66.667      3      3
 NUM:period        72.727 100.000  84.211      8     11
 NUM:speed        100.000  66.667  80.000      6      4
 NUM:temp         100.000  80.000  88.889      5      4
 NUM:weight        80.000 100.000  88.889      4      5
 ------------------------------------------------------
 Accuracy          84.694    -       -      -       490
```

This is not the most-recent system on this task. For more sophisticated systems, please have a look at recent works (e.g. [this](http://www.inesc-id.pt/pt/indicadores/Ficheiros/6678.pdf)). 

## Running it 

First you have to get the data, which is available at [here](https://cogcomp.cs.illinois.edu/page/publication_view/130), 
and put it under the `data/QuestionTypeClassification/` folder. 
 
Now you can run `sbt` and `run` the corresponding setting: 
```
>  run -t 1  
```
to run the coarse classifier, and 

```
>  run -t 2 
```
to run the fine  classifier. 

And then select the corresponding number to `QuestionTypeClassificationApp`. 

## References 

To get more details on the original idea and the data, here is the paper: 

```
@article{LiRo05a,
    author = {X. Li and D. Roth},
    title = {Learning Question Classifiers: The Role of Semantic Information},
    year = {2005},
    journal = {Journal of Natural Language Engineering},
    volume = {11},
    number = {4},
    url = " http://cogcomp.cs.illinois.edu/papers/LiRo05a.pdf",
    funding = {MURI,ITR-MIT},
    projects = {QA,MCR,TE},
    comment = {Question Classification; Hierarchical classifiers; Sequential Model; Expressive Feature generation with semantic information.},
}
```