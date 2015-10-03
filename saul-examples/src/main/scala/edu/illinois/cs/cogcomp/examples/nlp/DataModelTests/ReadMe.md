

These are small tests are for checking the edges in the data model graph, and the possibility of making contextual queries.
###Notes:

The base classes are TextAnnotation, Sentence and Constituent

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
then later when we want to populate the graph with data, we start from initializing the graph with a collection,
for example that comes from a reader, and then apply the sensors that can manipulate that collection and
generate a new collection and establish the edges between the items of the two collections automatically.
initialize the dataModel with a collection of TextAnnotation objects:

```scala
EdisonDataModel.++(taList)
```
use a generator sensor util.f [TextAnnotation=>Sentence], apply it on the current graph to  add items of type Sentence to the graph.

```scala
EdisonDataModel.populateWith(sensor.f, 'dTos)
```
Here sensor.f is a very simple function because the TextAnnotation already has the Sentences in it and can just return those to be added to the graph.

