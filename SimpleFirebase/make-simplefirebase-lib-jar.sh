#!/bin/bash
WORKINGDIR=`pwd -P`
VERSION="1.0"

# get version from SimpleActivity.java and create MANIFEST.MF
MANIFESTFILE=META-INF/MANIFEST.MF
# VERSION=`grep "LIBRARY_VERSION =" $WORKINGDIR/app/src/main/java/stanford/androidlib/SimpleActivity.java | sed -e 's/[^"]*"//' | sed -e 's/".*//'`
echo "Manifest-Version: 1.0" > $MANIFESTFILE
echo "Implementation-Version: $VERSION" >> $MANIFESTFILE
echo "Main-Class: stanford.androidlib.data.firebase.Version" >> $MANIFESTFILE
echo "" >> $MANIFESTFILE

# make the JAR file with all the .class files in it
rm -f stanford-simplefirebase-lib.jar
cd bin/
cp ~/AndroidStudioProjects/SimpleFirebase/app/build/intermediates/classes/debug/stanford/androidlib/data/firebase/*.class stanford/androidlib/data/firebase/
cp ~/AndroidStudioProjects/SimpleFirebase/app/src/main/java/stanford/androidlib/data/firebase/*.java stanford/androidlib/data/firebase/
cp ~/AndroidStudioProjects/SimpleFirebase/app/src/main/java/stanford/androidlib/data/firebase/*.java ../src/stanford/androidlib/data/firebase/
jar cvmf $WORKINGDIR/META-INF/MANIFEST.MF $WORKINGDIR/stanford-simplefirebase-lib.jar stanford/androidlib/data/firebase/*.class stanford/androidlib/data/firebase/*.java
