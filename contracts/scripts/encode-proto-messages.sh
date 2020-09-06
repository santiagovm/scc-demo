#!/usr/bin/env bash

set -o errexit
set -o errtrace
set -o pipefail

function go_to_root_directory() {
    root_directory=$(git rev-parse --show-toplevel)
    cd "$root_directory/contracts"
}

function download_protoc() {
  export PROTOC_VERSION="3.9.1"
  export PROTOC_TAG="v${PROTOC_VERSION}"

  if [ ! -f target/protoc/bin/protoc ]; then
    rm -rf target/protoc
    mkdir -p target/protoc
    echo "fetching protoc..."
    wget https://github.com/protocolbuffers/protobuf/releases/download/"${PROTOC_TAG}"/protoc-"${PROTOC_VERSION}"-osx-x86_64.zip -O target/protoc.zip
    unzip target/protoc.zip -d target/protoc
    echo "protoc fetched!"
  else
    echo "protoc already downloaded"
  fi
}

function encode_messages() {
  local contractsFolder="src/main/resources/contracts/com/vasquez/beer-api-producer-external/beer-api-consumer"
  local tempFolder="$contractsFolder/temp/encoded-messages"

  rm -rf $tempFolder
  mkdir -p $tempFolder

  for filename in $(ls $contractsFolder/messages/*.txt); do
    encodedFilename="$tempFolder/$(basename "$filename" .txt).bin.base64"

    cat $filename | \
      target/protoc/bin/protoc --encode=com.vasquez.beer.Response src/protos/beer.proto | \
      base64 > "$encodedFilename"
  done

  echo "proto messages encoded [$tempFolder]"
}

function main() {
  go_to_root_directory
  download_protoc
  encode_messages
}

main
