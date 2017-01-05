OpenCards
-----------

OpenCards is a free flashcard learning software for ppt-files

OpenCards is a free award-winning flashcard learning software. The basic idea of OpenCards
is to use PowerPoint presentations (*.ppt) as flashcard sets. Thereby, slide-titles are
treated questions and the slide contents as their answers. Based on state-of-the-art
memorization and scheduling algorithms OpenCards will help you to learn any set of flashcards.

Website: http://opencards.info


Installation
------------

Just grab a binary package for your platform from http://opencards.info


## Linux 

To use OpenCards with Ubuntu/Debian, you need to install `openjdk-8-jre` and `openjfx`. You can do so with

```bash
sudo apt-get install openjdk-8-jreopenjfx 
```
When using the debian-installer those dependencies will be installed automatically.

How to build and run it from the sources
--------------------------------

Make sure to jave [Kotlin](https://kotlinlang.org/) installed. Install [sdkman](http://sdkman.io/) to install it with `sdk install kotlin` if necessary.

Build OpenCards with
```{bash}
 kotlin_lib_dir=$(dirname $(which kotlin))/../lib/

# build it
ant -Dkotlin_lib_dir="${kotlin_lib_dir}" create-jar

# run it
java -Xmx512m -jar opencards.jar
```

How to contribute?
-------------------------

Feel welcome to suggest changes or pull requests.

There's a discussion forum to get in touch with us https://groups.google.com/forum/#!forum/opencards

http://opencards.info/contribute.html


About
------------------------

http://opencards.info/about.html


