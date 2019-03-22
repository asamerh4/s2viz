#!/bin/bash

cd src/lib/jars
cp /usr/share/java/gdal.jar .
cd ..
cd native
cp  /usr/lib/java/gdal/*.so* .
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$(pwd)/src/lib/native
