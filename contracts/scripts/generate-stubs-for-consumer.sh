#!/usr/bin/env bash

set -o errexit
set -o errtrace
set -o pipefail

maven_repo_path=~/.m2/repository/com/vasquez/beer-api-producer-external

function delete_old_jar_in_local_maven_repository() {
  rm -rf $maven_repo_path
}

function encode_proto_messages() {
  ./scripts/encode-proto-messages.sh
}

function create_jar_in_local_maven_repository() {
  pushd src/main/resources/contracts/com/vasquez/beer-api-producer-external/beer-api-consumer
  mvn install
  popd
}

function list_jar_contents() {
  echo "=============== STUBS JAR CONTENTS ==============="
  jar tf $maven_repo_path/0.0.1.BUILD-SNAPSHOT/beer-api-producer-external-0.0.1.BUILD-SNAPSHOT-stubs.jar

  echo "===============    JAR CONTENTS    ==============="
  jar tf $maven_repo_path/0.0.1.BUILD-SNAPSHOT/beer-api-producer-external-0.0.1.BUILD-SNAPSHOT.jar
}

function main() {
  delete_old_jar_in_local_maven_repository
  encode_proto_messages
  create_jar_in_local_maven_repository
  list_jar_contents
}

main
