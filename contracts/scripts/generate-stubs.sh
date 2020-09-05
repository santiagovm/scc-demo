#!/usr/bin/env bash

set -o errexit
set -o errtrace
set -o pipefail

maven_repo_path=~/.m2/repository/com/vasquez/beer-api-producer-external

function delete_old_jar_in_local_maven_repository() {
  rm -rf $maven_repo_path
}

function generate_contract_tests(){
  # todo: include this step in pom.xml (src/test/resources/contracts/com/vasquez/beer-api-producer/beer-api-consumer)
  ./gradlew generateContractTests
}

function create_jar_in_local_maven_repository() {
  pushd src/test/resources/contracts/com/vasquez/beer-api-producer/beer-api-consumer
  mvn clean install
  popd
}

function list_jar_contents() {
  echo "=============== JAR CONTENTS ==============="
  jar tf $maven_repo_path/0.0.1.BUILD-SNAPSHOT/beer-api-producer-external-0.0.1.BUILD-SNAPSHOT-stubs.jar
}

function main() {
  delete_old_jar_in_local_maven_repository
  generate_contract_tests
  create_jar_in_local_maven_repository
  list_jar_contents
}

main
