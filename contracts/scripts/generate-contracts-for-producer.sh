#!/usr/bin/env bash

set -o errexit
set -o errtrace
set -o pipefail

maven_repo_path=~/.m2/repository/com/vasquez/beer-contracts

function delete_old_jar_in_local_maven_repository() {
  rm -rf $maven_repo_path
}

function encode_proto_messages() {
  ./scripts/encode-proto-messages.sh
}

function create_jar_in_local_maven_repository() {
  ./mvnw install
}

function list_jar_contents() {
  echo "=============== JAR CONTENTS ==============="
  jar tf $maven_repo_path/0.0.1.BUILD-SNAPSHOT/beer-contracts-0.0.1.BUILD-SNAPSHOT.jar
}

function main() {
  delete_old_jar_in_local_maven_repository
  encode_proto_messages
  create_jar_in_local_maven_repository
  list_jar_contents
}

main
