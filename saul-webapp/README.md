# Saul Webapp 

## Running
- Enter the sbt environment: `sbt`
- Choose the webapp project: `project saulWebapp`
- Spin up the webapp: `run`; to specify a port, include it after `run`: `run PORT_NUM`. 

To stop it, use `Ctrl+D`. 

## Usage
After spinning up the server, go to `localhost:9000` through any web browser.

There will be a new user guidance for showing how to use saulWebapp.
Following the conversations on the webpage will lead you through every step of using the webapp.
Click `X` or cancel to exit the tutorial.

The left panel is the code editor.
The right panel is the graph visualization and result display window.

1. All files in the editor's filenames should be the same as their class name.
For example,
```scala
class testModel extends DataModel {
   ...
}
```
This file's filename should be `testModel.scala` or `testModel.java`

2. All files should have the same package name. If there is only one file, the package name is not required.

3. All import statements must be included as usual.
***
There are three running options: Compile, Populate and Run.

**Compile:**
-requires a subclass of `DataModel` presents in editor

Will generate the schema graph of the datamodel.

**Populate**
-requires a subclass of `DataModel` presents in editor
-requires main method (`main(args : Array[String]): Unit = { }`) presence in editor

Will generate a populated graph of the trained datamodel.

**Run**
-requires main method (`main(args : Array[String]): Unit = {  }`) presence in editor

Will print the output of the program.

Example toy code:
```scala
package test

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel

object testModel extends DataModel {

    val firstNames = node[String]
    val lastNames = node[String]
    val name = edge(firstNames,lastNames)
    val prefix = property(firstNames,"prefix")((s: String) => s.charAt(1).toString)
    val prefix2 = property(firstNames,"prefix")((s: String) => s.charAt(0).toString)

    def main(args : Array[String]): Unit ={
        firstNames.populate(Seq("Dave","John","Mark","Michael"))
        lastNames.populate(Seq("Dell","Jacobs","Maron","Mario"))
        name.populateWith(_.charAt(0) == _.charAt(0))
    }
}
```

Result for above example: [http://imgur.com/WjsYfYT](http://imgur.com/WjsYfYT)

***

Graph Query methods
