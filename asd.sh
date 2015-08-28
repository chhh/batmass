#!/bin/sh

git for-each-ref --count 1 --sort=-committerdate --shell --format="ref=%(refname)" | \
while read entry
do
	eval "$entry"
	echo $ref
done
