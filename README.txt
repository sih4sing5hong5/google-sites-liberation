README

INTRODUCTION
  Note:  This project is built using Maven (http://maven.apache.org/).

  The latest, most stable version of the code will always be located in the 'default' branch.  See
  the revision history for informaiton about the code in any other branch.

REQUIREMENTS
  This project is built using Java 1.5 or later.  All other dependencies are managed by Maven.

COMPILE

  To compile this project, execute 'mvn compile'.

TEST
  To run the tests, execute 'mvn test'.

JAR
  To package this project into a jar, execute 'mvn package'.  The jar will be located in the
  'target' directory.

EXECUTABLE JAR
  To create a double-clickable, executable jar, execute 'mvn assembly:assembly'.  The jar will be
  located in the 'target' directory with a '-jar-with-dependencies.jar' suffix.