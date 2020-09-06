#!/usr/bin/env bash

set -o errexit
set -o errtrace
set -o pipefail

function publish_to_maven_local(){
  ./mvnw clean install
}

function main() {
  publish_to_maven_local
}

main
