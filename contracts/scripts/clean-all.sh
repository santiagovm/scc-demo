#!/usr/bin/env bash

set -o errexit
set -o errtrace
set -o pipefail

contracts_path=src/test/resources/contracts/com/vasquez/beer-api-producer/beer-api-consumer
maven_repo_path=~/.m2/repository/com/vasquez/beer-api-producer-external

function delete_old_jar_in_local_maven_repository() {
  rm -rf $maven_repo_path
  echo "deleted contracts jar in local maven repo"
}

function delete_contracts_temp() {
  rm -rf $contracts_path/temp
  echo "deleted contracts temp folder"
}

function delete_root_temp() {
  rm -rf temp
  echo "deleted root temp folder"
}

function maven_clean() {
  pushd $contracts_path
  mvn clean
  popd
  echo "ran maven clean within resources/contracts "
}

function gradle_clean() {
  ./gradlew clean
  echo "ran gradle clean in contracts project root"
}

function main() {
  maven_clean
  gradle_clean
  delete_old_jar_in_local_maven_repository
  delete_contracts_temp
  delete_root_temp
}

main
