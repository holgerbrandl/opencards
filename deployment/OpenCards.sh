#!/bin/sh

ocjar=opencards.jar

# prefix it if not available, which will be the case on ubuntu/linux
if [ ! -f $ocjar ]
then
    ocjar=/usr/share/opencards/$ocjar
fi

# see https://stackoverflow.com/questions/43574426/how-to-resolve-java-lang-noclassdeffounderror-javax-xml-bind-jaxbexception-in-j
java -Xmx512m -XX:+IgnoreUnrecognizedVMOptions --add-modules=ALL-SYSTEM --add-exports=javafx.graphics/com.sun.javafx.application=ALL-UNNAMED -jar $ocjar