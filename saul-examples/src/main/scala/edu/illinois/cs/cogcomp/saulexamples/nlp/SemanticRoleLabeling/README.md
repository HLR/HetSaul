# Semantic Role Labeling 
This task is to annotate natural language sentences with semantic roles.  

To run the main app with default properties:

```
sbt "project saulExamples" "run-main edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlApp"
```

To use a custom configuration file (containing the property keys of `ExamplesConfigurator`):
 
```
sbt "project saulExamples" "run-main edu.illinois.cs.cogcomp.saulexamples.nlp.SemanticRoleLabeling.srlApp config/saul-srl.properties"
```