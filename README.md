# Reading-Notes
Some of my readings and learnings.
*  Design Pattern: learning design patterns with JDK
*  Software Architecture Principles
*  JVM Learnings
*  Leet Code Solutions

# Tags:
*  design_pattern
*  general
*  java
*  go
*  algorithm


want to write:
 go: when using gorountine: param better to be isolated
 java: auto class diagram:
  <plugin>
            <artifactId>maven-javadoc-plugin</artifactId>
            <version>2.7</version>
            <configuration>
                <aggregate>true</aggregate>
                <show>private</show>
                <doclet>org.umlgraph.doclet.UmlGraphDoc</doclet>
                <docletArtifact>
                    <groupId>org.umlgraph</groupId>
                    <artifactId>doclet</artifactId>
                    <version>5.1</version>
                </docletArtifact>
                <additionalparam>
                    -inferrel -attributes -types -visibility -inferdep -quiet -hide java.* -collpackages java.util.* -qualify -postfixpackage
                    -nodefontsize 9
                    -nodefontpackagesize 7
                </additionalparam>
            </configuration>
        </plugin>
