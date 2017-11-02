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
### Installation to local Maven repository
*HpoTextMining* is a Maven project, but it is not available from Maven Central Repository yet. Therefore, in order to use the plugin as a Maven dependency, it is neccessary to install specific version into the local Maven repository first.

```bash
git clone https://github.com/monarch-initiative/HpoTextMining; cd HpoTextMining  # clone the repo into local filesystem & enter the directory
git checkout v${project.version}  # checkout tag for installation of a specific release (e.g. not a SNAPSHOT version)
mvn clean    # clean the repo before installation
mvn install  # run the installation
```

### Adding as Maven dependency
After installation, add following into `pom.xml` file of the project where you want to use the plugin.
```
<dependency>
	<groupId>com.github.monarch-initiative</groupId>
	<artifactId>hpotextmining-core</artifactId>
	<version>${project.version}</version>
</dependency>
```
### Plug into the app
Read **Main** and **ApplicationConfig** classes of the *hpotextmining-demo* module to see how a usage of the plugin could look like.