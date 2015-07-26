## README

### INTRODUCTION
  Note:  This project is built using Maven (http://maven.apache.org/).

  The latest, most stable version of the code will always be located in the `master` branch.  See
  the revision history for informaiton about the code in any other branch.

### REQUIREMENTS
  This project is built using Java 1.5 or later.  All other dependencies are managed by Maven.

#### Preparation with Ubuntu 14.04
```
sudo apt-get install -y openjdk-7-jdk maven2
```

### COMPILE
  To compile this project, execute `mvn compile`.

### TEST
  To run the tests, execute `LANG=en_us mvn test -Djava.util.logging.config.file=SEVERE`.

### JAR
  To package this project into a jar, execute `LANG=en_us mvn package`.  The jar will be located in the
  `target` directory.

### EXECUTABLE JAR
  To create a double-clickable, executable jar, execute `LANG=en_us mvn assembly:assembly`.  The jar will be
  located in the `target` directory with a `-jar-with-dependencies.jar` suffix.

### Reference
* [Google Developers Console](https://console.developers.google.com/project)
* [OAuth2 example](http://stackoverflow.com/questions/10242751/oauth-invalid-token-request-token-used-when-not-allowed)
* [Google Data APIs Client Library](https://developers.google.com/gdata/javadoc/)
* [Entry  Class Hierarchy ](https://developers.google.com/gdata/javadoc/com/google/gdata/data/sites/package-tree)
