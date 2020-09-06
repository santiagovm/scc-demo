#!/usr/bin/env bash

set -o errexit
set -o errtrace
set -o pipefail

contracts_path=src/main/resources/contracts/com/vasquez/beer-api-producer-external/beer-api-consumer
maven_repo_path_for_consumer=~/.m2/repository/com/vasquez/beer-api-producer-external
maven_repo_path_for_producer=~/.m2/repository/com/vasquez/beer-contracts

function delete_old_jars_in_local_maven_repository() {
  rm -rf $maven_repo_path_for_consumer
  rm -rf $maven_repo_path_for_producer
  echo "deleted contracts jars in local maven repo"
}

function delete_contracts_temp() {
  rm -rf $contracts_path/temp
  echo "deleted contracts temp folder"
}

function delete_root_target() {
  rm -rf target
  echo "deleted root target folder"
}

function maven_clean() {
  pushd $contracts_path
  mvn clean
  popd
  echo "ran maven clean within resources/contracts "
}

function main() {
  maven_clean
  delete_old_jars_in_local_maven_repository
  delete_contracts_temp
  delete_root_target
}

main
