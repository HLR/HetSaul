# NLP Xml Reader
An [Xml reader](NlpXmlReader.java) that facilitates reading data from xml files into Saul's NLP 
[BaseTypes](../BaseTypes/BaseTyps.md).
The current `` BaseTypes `` include ``"DOCUMENT"`, `"SENTENCE"`, `"PHRASE"`, and `"TOKEN"``.

Assuming that the above mentioned tagnames have been used in the annotated input XML file, the reader can be called via 

``new NlpXmlReader("xml_file.xml")``.

##Tag Names

The defaul input XML tags that is `"DOCUMENT"`, `"SENTENCE"`, `"PHRASE"`, and `"TOKEN"` can have different tag names in the input file, however, in this case 
 we will need to indicate the actual tagnames for the corresponding BaseTypes.
 
  We can change them either by the constructor:
```java
  NlpXmlReader reader = new NlpXmlReader("xml_file.xml", "Doc_Tag", "Sentence_Tag", "Phrase_Tag", "TokenTag")
```
or by the setters provided in the reader's class. For example we can change the phrase's tag name by:
```java
  reader.setPhraseTagName("another_phrase_tag")
```

Note that you can read a type from multiple tags by changing the tag name just before reading the data:
```java
  reader.setPhraseTagName("first_phrase_tag")
  List[Phrase] firstPhraseList = reader.getPhrases() 
  reader.setPhraseTagName("second_phrase_tag")
  List[Phrase] secondPhraseList = reader.getPhrases()
```

The properties or tags that determine `start`, `end`, `id` and `text` of each linguistic unit
are predefined in the reader and can be changed using the corresponding setters as well.

##Read a list of linguistic units
Using `getDocuments`, `getSentences`, `getPhrases` and `getTokens` we can retrieve any of 
the base types in the xml file. For each type except the document there is another function
`getXXXByParentId` which retrieves list of a linguistic unit which contained in another (Parent)linguistic unit.

For example, to retrieve all sentences we should have:
```java
  List[Sentence] sentnces = reader.getSentences()
```
and to get sentences within a document with `id="doc1"` :
```java
  List[Sentence] sentnces = reader.getSentencesByParentId("doc1")
```

`getRelations` and `getRelationsByParentId` work similar to aforementioned functions, but need
to determine the property name of argument ids of the relation as well. For example consider
a relation with `R1` tag name that connects two phrases, we can obtain it's instances from the xml file by:
```java
  List[Relation] relations = reader.getRelations("R1", "first_phrase_id", "second_phrase_id")
  List[Relation] doc1Relations = reader.getRelations("R1", "doc1", "first_phrase_id", "second_phrase_id")
```

## Adding properties to the linguistic units from other tags
In many scenarios we need to add properties from various tags. We can do that by providing
the tag names when using `getXXXs` or `getXXXByParentId` when we have the linguistic units in 
the xml file. But when we have generated the linguistic units from another source and want to 
add properties from a tag, we can use `addPropertiesFromTag`. This function uses the parent Id
of the linguistic units(`documentId` for sentence, and `sentenceId` for `Token` and `Phrase`) to find
the context for each linguistic unit to be retrieved.

### How to access added properties from other tags?
To access those properties that added to a linguistic unit from other tags, simply use 
the tag name followed by an underscore and then the property name. Let's say we've added
properties of `"MyTag"` tag to a linguistic unit `x` from the following xml file:
```xml
...
    <MyTag firstProp="value1" ..../>
    <MyTag firstProp="value2" ..../>
...
```
We can access `MyTag` properties like this:
```java
String firstProp = x.getPropertyFisrtValue("MyTag_firstProp");       
```

### Matching
adding properties from a tag requires matching between linguistic units and tags. The default
matching strategy is [`ExactMatching`](XmlExachMatching.java). 

Built in matching strategies:
- [`ExactMatching`](../BaseTypes/ExachMatching.java): adds tag's properties if the tag's span
 exactly matches with the linguistic unit's span 
- [`InclusionMatching`](../BaseTypes/InclusionMatching.java): adds tag's properties if the tag's span
 includes the linguistic unit's span
- [`PartOfMatching`](../BaseTypes/PartOfMatching.java): adds tag's properties if the tag's span is
a part of linguistic unit span, in other word if the linguistic unit's span includes the tag's span
- [`OverlapMatching`](../BaseTypes/OverlapMatching.java): adds tag's properties if the tag's span
and the linguistic unit's span are overlapping
- [`phraseHeadwordMatching`](../../../../../../../../scala/edu/illinois/cs/cogcomp/saulexamples/nlp/SpatialRoleLabeling/XmlMatchings.scala):
 adds tag's properties if the tag's span contains the headwords span of the phrase
- [`xmlHeadwordMatching`](../../../../../../../../scala/edu/illinois/cs/cogcomp/saulexamples/nlp/SpatialRoleLabeling/XmlMatchings.scala):
 adds tag's properties if the head word of the tag text contains the span of the linguistic unit

You can create your own matching strategy by implementing [`IXmlSpanMatching`](IXmlSpanMatching.java) interface.

We can use these strategies when calling `addPropertiesFromTag` function. 

For example consider the following xml file:
```xml
  <PHRASE id="T3" start="95" end="96" text=","/>
  <PHRASE id="T4" start="98" end="102" text="a TV"/>
  <PHRASE id="T5" start="70" end="81" text="wooden desk"/>
  <PHRASE id="T6" start="86" end="91" text="chair"/>
  <PHRASE id="T7" start="144" end="156" text="a glass door"/>
  <MATCH id="inc1" start="70" end="82"/>
  <MATCH id="inc2" start="143" end="157"/>
  <MATCH id="p1" start="100" end="101"/>
  <MATCH id="p2" start="98" end="100"/>
  <MATCH id="p3" start="100" end="102"/>
  <MATCH id="o1" start="93" end="99"/>
  <MATCH id="e1" start="144" end="156"/>
```
after reading phrases:
```java
  List[Phrase] phrases = reader.getPhrases()
```
we can use exact matching to add properties to them:
```java
  reader.addPropertiesFromTag("MATCH", phrases, new ExactMatching())
```
By doing so, the reader finds the "MATCH" tags that their span matches exactly with a phrase 
instance and adds it's properties to that instance. For this example, the `MATCH` tag with 
`id="e1"` matches with `PHRASE` with `id="T7"` and therefore it's properties will be added to 
that phrase. The reader renames properties of the tag, by prepending the tag name to the name
of each properties(`{MATCH_id="e1", MATCH_start="144", MATCH_end="156"}` will be added to `T7` phrase).

For other strategies:
- `InclusionMatching`: adds `Match` with `id="inc1"` properties to `T5` phrase and `inc2` to `T7`  
- `PartOfMatching`: adds `p1`, `p2` and `p3` to `T4`
- `OverlapMatching`: adds `inc1` to `T5`, `inc2` to `T7`, `p1` to `T4`, `p2` to `T4`, 
`p3` to `T4`, `o1` to `T3` and `T4`, and `e1` to `T7`

## An example in scala
Suppose we have this xml file: 
```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<SpRL>
    <SCENE id="sc2" test="test">
        <DOCNO>annotations/01/1069.eng</DOCNO>
        <IMAGE>images/01/1069.jpg</IMAGE>
        <SENTENCE id="s603" start="130" end="344">
            <TEXT>Interior view of a room with a large bed with red bedcovers , a white 
            wooden desk and chair below a TV fixed in the corner , a white fridge and a 
            glass door with a wooden frame leading onto a veranda and garden .
            </TEXT>
            <SPATIALINDICATOR id="S3" start="109" end="111" text="in"/>
            <SPATIALINDICATOR id="S4" start="92" end="97" text="below"/>
            <SPATIALINDICATOR id="S5" start="177" end="189" text="leading onto"/>
            <LANDMARK id="L3" start="112" end="122" text="the corner"/>
            <LANDMARK id="L4" start="98" end="102" text="a TV"/>
            <LANDMARK id="L5" start="192" end="199" text="veranda"/>
            <LANDMARK id="L6" start="204" end="210" text="garden"/>
            <TRAJECTOR id="T4" start="98" end="102" text="a TV"/>
            <TRAJECTOR id="T5" start="70" end="81" text="wooden desk"/>
            <TRAJECTOR id="T6" start="86" end="91" text="chair"/>
            <TRAJECTOR id="T7" start="144" end="156" text="a glass door"/>
            <TESTPROP id="TP3" first_value="1" second_value="T1" start="62" end="72"/>
            <MATCH id="inc1" start="70" end="82"/>
            <MATCH id="inc2" start="143" end="157"/>
            <MATCH id="p1" start="100" end="101"/>
            <MATCH id="p2" start="98" end="100"/>
            <MATCH id="p3" start="100" end="102"/>
            <MATCH id="o1" start="93" end="99"/>
            <MATCH id="e1" start="144" end="156"/>
            <RELATION id="SR4" trajector_id="T4" landmark_id="L3" spatial_indicator_id="S3" general_type="region"
                      specific_type="RCC8" RCC8_value="EC" FoR="intrinsic"/>
            <RELATION id="SR5" trajector_id="T5" landmark_id="L4" spatial_indicator_id="S4" general_type="direction"
                      specific_type="relative" RCC8_value="below" FoR="intrinsic"/>
            <RELATION id="SR6" trajector_id="T6" landmark_id="L4" spatial_indicator_id="S4" general_type="direction"
                      specific_type="relative" RCC8_value="below" FoR="intrinsic"/>
            <RELATION id="SR7" trajector_id="T7" landmark_id="L5" spatial_indicator_id="S5" general_type="region"
                      specific_type="relative" RCC8_value="DC" FoR="intrinsic"/>
            <RELATION id="SR8" trajector_id="T7" landmark_id="L6" spatial_indicator_id="S5" general_type="region"
                      specific_type="relative" RCC8_value="DC" FoR="intrinsic"/>
        </SENTENCE>
    </SCENE>
</SpRL>
```
we can read the hierarchy and the relation list by:

```scala
  val reader = new NlpXmlReader("path_to_the_xml_file.xml", "SCENE", "SENTENCE", null, null)
  val documentList = reader.getDocuments()
  val sentencesList = reader.getSentences()

  reader.setPhraseTagName("TRAJECTOR")// set the phrase tag name before reading as phrase list
  val trajectorList = reader.getPhrases() // reading trajectors as a list of phrases
  reader.setPhraseTagName("LANDMARK")
  val landmarkList = reader.getPhrases()
  reader.setPhraseTagName("SPATIALINDICATOR")
  val spIndicatorList = reader.getPhrases()

  //the relation contains three arguments which their ids are determined by the specified strings
  val relationList = reader.getRelations("RELATION", "trajector_id", "spatial_indicator_id", "landmark_id")

  val phraseList = getPhrasesFromSomewhere()// generating the phrases from other sources
  reader.addPropertiesFromTag("TRAJECTOR", phraseList, XmlMatchings.headwordMatching)
  reader.addPropertiesFromTag("LANDMARK", phraseList, new XmlPartOfMatching)
  reader.addPropertiesFromTag("SPATIALINDICATOR", phraseList, new XmlPartOfMatching)
```