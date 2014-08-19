#!/bin/bash

echo "Running $0 to create a new distribution for options report generation"

if test -a options-reports.zip ; then
	RENAMETO=options-reports.zip.`date +"%Y%m%d%H%M"`
	echo "Renaming existing zip file to $RENAMETO"
	mv options-reports.zip $RENAMETO
fi

rm -Rf ./target
mkdir -p target/lib
cp rendersnake-1.7.3/jars/rendersnake-1.7.3.jar target/lib
cp commons-lang3-3.3.2/commons-lang3-3.3.2.jar target/lib
jar -cmf MANIFEST.txt options-report.jar options/*.class options/reports/*.class
mv options-report.jar target
chmod 740 target/options-report.jar
tr -d '\r' < README > target/README.txt
cp test1.csv target
cp run-report.sh target
cp run-report.bat target
mv target options-reports
zip --move -r options-reports.zip options-reports
ls -l options-reports.zip*
echo "Finished"
read
exit 1

