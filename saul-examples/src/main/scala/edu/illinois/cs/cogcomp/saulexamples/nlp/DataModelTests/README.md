

These are small tests are for checking the edges in the data model graph, and the possibility of making contextual queries.
###Notes:

The base classes are TextAnnotation, Sentence, Constituent

###Background:

In Saul we have three ways now to establish an edge between two class of nodes:

1. Using generating sensors
2. Using matching sensors
3. Setting the primary and secondary keys explicitly when declaring nodes

###Declaring the schema of the graph

In the first two cases what is declared in the graph schema is only the type of each node:

```scala
 val document = node[TextAnnotation]
 val sentence = node[Sentence]
```
and naming the edge that connects them:

```scala
  val DocTosen = edge[TextAnnotation, Sentence]('dTos)
```

### Populating the graph with data
####Using generating sensors
Later when we want to populate the graph with data, we start from initializing the graph with a collection,
for example that comes from a reader, and then apply the sensors that can manipulate that collection and
generate a new collection and establish the edges between the items of the two collections automatically.
Populate the dataModel with a collection of TextAnnotation objects:

```scala
EdisonDataModel.populate(taList)
```
use a generator sensor sensor.f: TextAnnotation=>Sentence, apply it on the current graph to  add items of type Sentence to the graph.

```scala
EdisonDataModel.populateWith(sensor.f, 'dTos)
```
Here sensor.f is a very simple function because if you see the TextAnnotation class it already has the Sentences in it and can just return those to be added to the graph. Notice, *populate* just
  adds the nodes and *populateWith* adds the nodes and uses an edge to establish the connections.
```scala
def f(x: TextAnnotation): List[Sentence] = x.sentences().toList
```
####Using matching sensors

There is no need that to have a generating sensor but the user can use a matching sensor:(T,U)=>Boolean
that checks whether some conditions hold to connect two types node. In other words depending on
the availability of the sensors, sometimes it makes more sense to populate the graph using
these kind of boolean matching sensors. The declaration is exactly the same as the generator sensors

 First we add our first collection simply by populate:

```scala
 EdisonDataModel.populate(taList)
```
Then we add the second collection using an edge and a matching sensor:

```scala
EdisonDataModel.populateWith(sentenceList,sensors.alignment,'dTos)
```
The difference is the type of function that has been used here *alignment*
which has the following content:

```scala
  def alignment(x: TextAnnotation, y: Sentence): Boolean = x.getId == y.getSentenceConstituent.getTextAnnotation.getId
 ```

###Declaring the schema of the graph using explicit identifiers

The available collections can be added to the graph without using sensors, but the issue is to set the edges between the collections when they are added to the graph and
for this we need to define identifiers in the data model.
In this case the declarations in the data model are enriched by key declarations:
```scala
val document= node[TextAnnotation](
    PrimaryKey = {
      t: TextAnnotation => t.getId
    }
  )
```
This indicates that each object of type TextAnnotation can be identified uniquely by a primary key which is t.getId, and in the below code:
```scala
  val sentence = node[Sentence](
    PrimaryKey = {
      t: Sentence => t.hashCode().toString
    }
    ,
    SecondaryKeyMap = MutableMap(
      'dTos -> ((t: Sentence) => t.getSentenceConstituent.getTextAnnotation.getId)
    )
  )
```
Each sentence has a hash code that is used as its primary key and it contain a secondary key that connect the sentence to the
TextAnnotation object that contains it.

####Populating the graph

When using explicit identifiers then the edges are automatically used and we do not need to mention the edges explicitly.
such as follows:

```scala
modelWithKeys.populate(taList)
modelWithKeys.populate(sentenceList)
```

####Graph Queries

Examples of basic graph queries to traverse the graph.

```scala
val s = document() ~> DocToSen // all sentences
val d = sentences() ~> -DocToSen // all documents (using reverse link)
val s2 = document() ~> DocToSen filter(_.contains("Saul")) ~> -DocToSen // all sentences in documents that contain "Saul"
```
