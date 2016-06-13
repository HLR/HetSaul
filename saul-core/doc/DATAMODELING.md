Here you see the syntax of the declaration of Saul graph. Most of these features are unit tested in [GraphQueriesTest](https://github.com/IllinoisCogComp/saul/blob/master/saul-examples/src/test/scala/edu/illinois/cs/cogcomp/saulexamples/datamodel/GraphQueriesTest.scala).

## Node collections

Definition:
```scala
val n = node[A]
```

Get all the instances of a node:
```scala
n()
```

To start querying using a custom collection:
```scala
val coll: Iterable[A] = ...
n(coll)
```

## Edge Queries

Definition:
```scala
val n2 = node[B]
val e = edge(n,n2)
```

Get all neighbors of all instances of `n`:
```scala
n() ~> e
```

Get all neighbors of some of the instances of `n`:
```scala
n(coll) ~> e
```

Reverse Query, get all neighbors of all instances of `n2`:
```scala
n2() ~> -e
```

## Property Queries

Definition:
```scala
val p=property[A]{
   x : A => property_sensor(x)
   }
```
Get the property of a specific instance `x`:
```scala
p(x)
```
It would be nicer if we can have:
`x.p`

and

```scala
n(x) ~> edge1 ~>edge2 p
```
which provides a collection of all `p` property values of all nodes that we get after `edge2`.
Properties have a core which is a basic data type `b`, such as `String`, `Int`, etc or an `Iterable[b]`.
So property functions should be able to be applied on single  instances or a set of instances.

## Filtering Queries
Filter the instances of a node:
```scala
def f: A => Boolean = ...
n() filter f
```
Filter a collection of instances:
```scala
n(coll) filter f
```
Note that this is different from `n.filter(f)` or `coll.filter(f)` since you can continue the graph query:
```scala
n(coll) filter f ~> e
```

## Aggregation
- Applying a property `p` on a set of instances `X`:
```scala
node(X).p.aggregation1
```
- Aggregation functions:

 -- Features with nominal values

    String aggregations, mainly concatenation

 -- Integer and Real Features

    Numerical aggregations such as sum, multiplication, max, min, ...

## Filtering based on properties

`node(n) filter (a logical expression over p)`

This implies we need to implement the following functions for properties:
equality of one property with a value, equality of two properties, p.contains(x), etc. This seems to be trivial to do or even simply granted by Scala for the most cases because the properties return basic types or collections of them.

## Example Combinations
```scala
val n1 = node[A]
val n2 = node[B]
val n3 = node[C]

val e1 = edge(n1, n2)
val e2 = edge(n2, n3)


n1()
n1() ~> e1 // = n2
n1() ~> e1 ~> e2 // = n3

n3() ~> -e2 // = n2
n3() ~> -e2 ~> -e1 // = n1
```
##More contextual queries

 getting neighbors of a node
   ```scala
     n1(a)~>*
   ```
getting neighbors of a node within a specific distance:

```scala
   n1(a)~>e(2)
   n1(a)~>e(-2,2)

```
 getting the properties of the neighbors:

```scala
n(a)~>e(2).p
n(a)~>e(-2,2).p.aggregate

```

finding path between two nodes

```scala
n1(a).path(n2(b))
```