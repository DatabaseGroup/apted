cmake_minimum_required(VERSION 3.0)

find_package(Java)
include(UseJava)

project(apted)

set(SRC
    src/distance/APTED.java
    src/distance/InfoTree_PLUS.java
    src/util/FormatUtilities.java 
    src/util/LblTree.java
    src/util/LabelDictionary.java
    src/util/RTEDCommandLine.java
)
add_jar(APTED ${SRC} ENTRY_POINT util/RTEDCommandLine)