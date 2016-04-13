#!/bin/bash
WORKINGDIR=`pwd -P`

# get version from SimpleActivity.java and create MANIFEST.MF
MANIFESTFILE=META-INF/MANIFEST.MF
# VERSION=`grep "LIBRARY_VERSION =" $WORKINGDIR/app/src/main/java/stanford/androidlib/SimpleActivity.java | sed -e 's/[^"]*"//' | sed -e 's/".*//'`
echo "Manifest-Version: 1.0" > $MANIFESTFILE
echo "Implementation-Version: $VERSION" >> $MANIFESTFILE
echo "Main-Class: stanford.androidlib.Version" >> $MANIFESTFILE

# make the JAR file with all the .class files in it
rm -f stanford-android-lib.jar
cd app/build/intermediates/classes/debug/
jar cvmf $WORKINGDIR/META-INF/MANIFEST.MF $WORKINGDIR/stanford-android-lib.jar stanford/*/*.class stanford/*/*/*.class

# put the source code .java files in the JAR, too
# (so the Android Studio user can step into the declarations)
cd $WORKINGDIR/app/src/main/java/
jar uvf $WORKINGDIR/stanford-android-lib.jar stanford/*/*.java stanford/*/*/*.java

echo ""
echo "*****************************************************"
echo "* HEY, MARTY! Don't forget to Clean/Rebuild Project *"
echo "* So the changes will be seen in the JAR.           *"
echo "*****************************************************"

echo ""
echo "The JAR reports that its version is now:"
cd $WORKINGDIR
#java -classpath "stanford-android-lib.jar" stanford.androidlib.Version
java -jar "stanford-android-lib.jar" --version
