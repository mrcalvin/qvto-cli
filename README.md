# On "qvto-cli"
A command line interface (CLI) application to execute Eclipse Query/View/Transformation Operational (QVTo) model transformations without having Eclipse IDE installed. The build procedure is fully automated using Maven, including resolution of Eclipse dependencies. The resulting artifact is a standalone jar which can be deployed and executed as-is. The aim is to provide a CLI component which can be glued to form composite applications and, thereby, make QVTo model transformations available to a broader range of (non-Java) applications.

qvto-cli consists of two sub-components:

* qvt-bundle: This is a Maven artifact which contains all QVTo standalone Eclipse plug-in (jar) dependencies. This is achieved by using [Eclipse Tycho](https://eclipse.org/tycho/).
* qvto-app: This is a slim CLI app build to access Eclipse QVTo from the command line. 

# Prerequisites

* Maven 3.2.5
* Ant 1.9+
* Java 1.7

# Build procedure

1. Clone this GIT repository and `cd` into it.
2. `(cd qvto-bundle && mvn clean install)`
3. `(cd qvto-app && mvn clean compile package)`

Note that Step 2 might require a considerable amount of time on the first run, because the corresponding Eclipse release (P2) repository is cloned and mirrored on your machine.

# Usage

```
Usage: java -jar qvto-app-0.1-SNAPSHOT.jar [options] 
  Options:
  * -xform, --transformation
       QVTo transformation to be executed
  * -src
       Pairs of source metamodel and source model
       Default: []
    -tgt
       Pair of target metamodel and target model
       Default: []
```
