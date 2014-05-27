#!/bin/sh

# javac -target 1.6 -source 1.6 spacetime/SpacetimeApp.java  # If compiling with higher version.  Will need to add -bootclasspath info as well to avoid warning
javac spacetime/SpacetimeApp.java
jar cvfe Spacetime.jar spacetime.SpacetimeApp LICENSE.md README.md resources spacetime/*.class
