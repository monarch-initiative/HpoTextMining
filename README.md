# HpoTextMining
***HpoTextMining*** allows convenient curation of phenotype information

## Aim
The aim of ***HpoTextMining*** is to allow easy & convenient curation of phenotypic information using Human Phenotype Ontology (HPO).
The software is designed to be a plugin in more complex curation software built using *JavaFX* framework.

## How to use
The ***HpoTextMining*** presents a dialog window to the curator/user. The dialog window consists of following subparts:

- **approved terms table** - table (at the bottom) that contains the approved phenotype terms. The terms which are inside the table will be returned as results after closing or hitting OK button of the dialog
- **ontology tree pane** - tree hierarchy of the ontology is displayed on the left side of the dialog. User can expand individual levels and add selected terms into the approved terms table
- **text-mining pane** - place where user submits a query text that is mined for HPO terms. Then, results of the analysis are presented in the same pane


## How to use in *JavaFX* app
In order to use *HpoTextMining* in your project, add the following dependency into your `pom.xml` file.
```
<dependency>
	<groupId>com.github.monarch-initiative</groupId>
	<artifactId>hpotextmining-core</artifactId>
	<version>0.2.1</version>
</dependency>
```
## Example usage
See `Main` and `ApplicationConfig` classes of the *hpotextmining-demo* module to see an example usage.
