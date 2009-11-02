#!/bin/bash

data=./data
extr=./extracted

mkdir -p $extr

[ -d $data ] || exit 1;

for name in $data/*.zip; do
	echo Processing file: $name
	unzip -x $name -d $extr 
done 
