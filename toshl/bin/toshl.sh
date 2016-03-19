#!/bin/sh
bindir="$(dirname $0)"
cd "$bindir"
cd ../target
$JAVA_HOME8/bin/java -cp toshl-1.0.0-jar-with-dependencies.jar qiushuo.treasurebox.toshl.Toshl

mysql -uroot -Dtoshl

