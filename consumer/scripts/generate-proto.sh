#!/usr/bin/env bash

set -o errexit
set -o errtrace
set -o pipefail

function download_protoc() {
  export PROTOC_VERSION="3.9.1"
  export PROTOC_TAG="v${PROTOC_VERSION}"

  if [ ! -f temp/protoc/bin/protoc ]; then
    rm -rf temp/protoc
    mkdir -p temp/protoc
    echo "fetching protoc..."
    wget https://github.com/protocolbuffers/protobuf/releases/download/"${PROTOC_TAG}"/protoc-"${PROTOC_VERSION}"-osx-x86_64.zip -O temp/protoc.zip
    unzip temp/protoc.zip -d temp/protoc
    echo "protoc fetched!"
  else
    echo "protoc already downloaded"
  fi
}

function generate_proto() {
  rm -rf temp/generated-sources/java
  mkdir -p temp/generated-sources/java
  temp/protoc/bin/protoc --java_out=temp/generated-sources/java src/main/resources/proto/beer.proto
  echo "proto files generated"
}

function main() {
  download_protoc
  generate_proto
}

main
