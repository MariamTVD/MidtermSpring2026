#!/usr/bin/env bash
set -e

rm -rf out/classes
mkdir -p out/classes

javac -d out/classes src/*.java

java -cp out/classes Main "$@"
