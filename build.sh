#!/bin/bash

echo "Running $0 to create a new distribution for options report generation"
rm -Rf ./target
mkdir -p target/lib
cp rendersnake-1.7.3/jars/rendersnake-1.7.3.jar target/lib
cp commons-lang3-3.3.2/commons-lang3-3.3.2.jar target/lib
jar -cmf MANIFEST.txt options-report.jar options/*.class options/reports/*.class
mv options-report.jar target
chmod 740 target/options-report.jar
cp README.txt target
cp test1.csv target
cp run-report.sh target
mv target options-reports
zip --move -r options-report.zip options-reports
ls -l options-reports
echo "Finished"
exit 1

