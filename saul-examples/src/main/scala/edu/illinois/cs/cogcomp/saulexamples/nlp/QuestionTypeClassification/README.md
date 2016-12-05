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
    
![](saul-examples/src/main/resources/QuestionTypeClassification/categories.png)

More details can be found in [this](http://cogcomp.cs.illinois.edu/page/publication_view/130) paper. 
The input data for training/testing the system can be 
downloaded from [here](http://cogcomp.cs.illinois.edu/page/resource_view/49).  

## Performance 


## Runnint it 

First you have to get the data, which is available at [here](https://cogcomp.cs.illinois.edu/page/publication_view/130), 
and put it under the `data/QuestionTypeClassification/` folder. 
  


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