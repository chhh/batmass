#!/bin/bash

dir_top="batmass"
dir_src="website"
dir_pub="docs"
dir_cur=${PWD##*/}


if [ $dir_cur != $dir_src ]; then
	 printf"\n\nYou must be in '$dir_src' direcotry to execute this script.\n"
	exit 1
fi


cd ..
dir_cur=${PWD##*/}
printf "\nUpper level dir: %s\n" $dir_cur
if [ $dir_cur != "$dir_top" ]; then
    printf "\n\nYou must be in $dir_top/$dir_src to execute this script\n"
    exit 1
fi

printf "\n\nRemoving old content:\n"
rm -rf $dir_pub
cd $dir_src
printf "\n\nBuilding new content...\n"
rm -rf public
hugo
printf "\n\nCopying new content:\n"
ls public/*
mkdir ../$dir_pub
cp -r public/* ../$dir_pub
printf "\n\nRemoving temporary files.\n"
rm -rf public
