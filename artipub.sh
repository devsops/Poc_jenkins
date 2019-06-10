#!/bin/bash
git show > commitinfo.txt
while read line; do
  if [[ $line =~ release ]] ; then
	cd /var/lib/jenkins/workspace/Multijob_test
	build jar artifactoryPublish
  echo "push tori artifactory $line"
  exit 0;
  fi
done <commitinfo.txt
