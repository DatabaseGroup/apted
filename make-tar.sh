#!/bin/bash
gnutar vcfa APTED-0.1.1.tar.xz \
README.txt \
CMakeLists.txt \
src \
--exclude='*.class' \
--exclude='2-node-spf.txt' \
--exclude='testing-steps.txt'