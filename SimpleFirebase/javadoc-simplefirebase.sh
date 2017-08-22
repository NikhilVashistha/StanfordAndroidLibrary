#!/bin/bash
javadoc \
    -sourcepath "/home/stepp/Downloads/jdk1.8.0_73/src.zip" \
    -classpath ".:/home/stepp/Android/Sdk/platforms/android-23/android.jar:/home/stepp/Android/Sdk/tools/support/annotations.jar:/home/stepp/Android/Sdk/extras/android/m2repository/com/android/support/support-annotations/23.1.1/support-annotations-23.1.1.jar:./jar/firebase-database.jar:./jar/firebase-auth.jar" \
    -d javadoc/ \
    -linkoffline https://firebase.google.com/docs/reference/ ~/Android/Sdk/extras/google/google_play_services/docs/reference/ \
    -linkoffline http://developer.android.com/reference/ ~/Android/Sdk/docs/reference/ \
    -link https://docs.oracle.com/javase/8/docs/api \
    -tag usage \
    -tag inherited \
    -tag noshow \
    ~/AndroidStudioProjects/SimpleFirebase/app/src/main/java/stanford/androidlib/data/firebase/*.java
