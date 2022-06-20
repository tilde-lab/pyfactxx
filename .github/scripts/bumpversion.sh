#!/bin/bash

args="$*"

while [ $# -gt 0 ] ; do
  if [[ "$1" == "--dry-run" ]]; then
    echo "Dry run, no change intended"
    dryrun=1
  fi
  shift
done
if [[ -z "$dryrun" ]]; then
  today=`date +"%d %B %Y"`
  sed -i "s/ReleaseDate = \"\(.*\)\"/ReleaseDate = \"${today}\"/" Kernel/Kernel.cpp 
  echo "Release date set"
fi

bumpversion $args

