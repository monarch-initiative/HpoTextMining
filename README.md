# HpoTextMining
*HpoTextMining* allows convenient curation of phenotype information from free-text in English by using [Monarch BioLark](http://phenotyper.monarchinitiative.org:5678/cr/annotate) instance.

## Aim
The aim of *HpoTextMining* is to allow easy & convenient curation of phenotypic information using Human Phenotype Ontology (HPO).
It is possible to use the code as a plugin in more complex curation software built using *JavaFX* framework (*hpotextmining-demo*), or as an independent application (*hpotextmining-gui*).

## How to use
The *HpoTextMining* presents a dialog window to the curator/user. The dialog window consists of following subparts:

- **approved terms table** - table (at the bottom) that contains the approved phenotype terms. The terms which are inside the table will be returned as results after closing or hitting OK button of the dialog
- **ontology tree pane** - tree hierarchy of the ontology is displayed on the left side of the dialog. User can expand individual levels and add selected terms into the approved terms table
- **text-mining pane** - an area, where user submits a query text that is mined for HPO terms. After the text mining, results are presented in the same area. The parts of a query text, where a HPO terms were identified, are highlighted with red. It is possible to see context of the highlighted HPO term in the ontology graph by clicking on the highlighted text. 
A sorted list of the HPO terms is displayed on the right side, where user is expected to review the results and approve the correctly identified terms. After marking all correct terms and clicking on the *Add selected terms* button, the terms will be transfered into the *approved terms table* described above.

### How to use as an independent application
The pre-built `exe` file is not available at the moment. Therefore, it is necessary to build the app from sources. Since *HpoTextMining* is a *maven* project, you can build the app by running:
```bash
cd HpoTextMining
mvn clean package
```
If everything goes well, *maven* will create a distribution ZIP archive `hpotextmining-gui-0.2.1-distribution.zip` in the `hpotextmining-gui/target` directory. Copy and extract the archive into a directory in your system, where you usually put other software. After extracting, run the app either from shell, or by double-click on the JAR file.

### How to use as a plugin in *JavaFX* application
In order to use *HpoTextMining* in your project, add the following dependency into your `pom.xml` file and see the `Play` class of the *hpotextmining-demo* module to see an example usage.

```
<dependency>
	<groupId>com.github.monarch-initiative</groupId>
	<artifactId>hpotextmining-demo</artifactId>
	<version>0.2.1</version>
</dependency>
```
 * 
