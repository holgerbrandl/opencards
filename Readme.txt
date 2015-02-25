Description
-----------

OpenCards is a free award-winning flashcard learning software. The basic idea of OpenCards
is to use PowerPoint presentations (*.ppt) as flashcard sets. Thereby, slide-titles are
treated questions and the slide contents as their answers. Based on state-of-the-art
memorization and scheduling algorithms OpenCards will help you to learn any set of flashcards.


Web-Resources
-----------

Website: opencards.info
Devlopers:  http://code.google.com/p/opencards
Discussion: http://groups.google.com/group/opencards


Installation
------------

Grab a binary package for your platform from http://code.google.com/p/opencards/downloads/list


How to build it from the sources
--------------------------------

If you're NOT running MacOS you have to uncomment the exclude tag in the compile-target in the build.xml which
will exclude some macos specific classes.

# build it
ant create-jar

# run it
java -Xmx512m -jar opencards.jar


Contributors & Guidelines
-------------------------

http://www.opencards.info/about-us/
http://www.opencards.info/how-to-contribute/

