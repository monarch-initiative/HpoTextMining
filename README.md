# Hpo text mining plugin
Perform text mining for identification  of HPO terms in free text from scientific publication.

## What is this for?
TODO

## How to use
### Installation to local Maven repository
The plugin is designed to be used as a part of other application that is created using JavaFX platform. It is also a Maven project, but it is not available from Maven Central Repository yet. Therefore, to be able to use the plugin as Maven dependency, it is neccessary to perform installation into local repository.

```bash
git clone https://github.com/ielis/HpoTextMining.git
cd HpoTextMining
mvn install
```

### Adding as Maven dependency
After installation, add following into `pom.xml` file of the project where you want to use the plugin.
```
<dependency>
	<groupId>org.monarchinitiative</groupId>
	<artifactId>hpotextmining</artifactId>
	<version>${project.version}</version>
</dependency>
```
### Plug into a JavaFX application
Finally, usage of the plugin could look like this (see `Main` class):

```java
// omitted imports
public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		Optional<TextMiningResult> textMiningResult = TextMiningAnalysis.run();
		if (textMiningResult.isPresent()) {
			// data container with results
			TextMiningResult result = textMiningResult.get();
			
			Set<Term> yesTerms = result.getYesTerms();   // set of YES terms approved by the curator
			Set<Term> notTerms = result.getNotTerms();   // set of NOT terms approved by the curator
			String pmid = result.getPMID();              // PMID of the publication
			
			// do something with results
		}
	}
	
	public static void main(String[] args) {
		launch(args);
}
```
