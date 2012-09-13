@echo off

"%JAVA_HOME%"\bin\java -Djava.util.logging.config.file=conf\logging.properties -jar ${project.artifactId}-${project.version}.${project.packaging}
