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

    response_proto=$(cat $filename | target/protoc/bin/protoc --encode=com.vasquez.beer.Response src/protos/beer.proto)
    # show_bytes_as_octal "$response_proto"

    response_proto_octal=$(convert_octal_bytes_to_escaped_string_sequence "$response_proto")
    # printf "response_proto_octal = [%s]\n" "$response_proto_octal"

    message_envelope_plain_text="message_type: \"foo-message-type\"
event_data: \"$response_proto_octal\""

    # echo "message_envelope_plain_text"
    # echo "$message_envelope_plain_text"

    message_envelope_proto=$(printf "%s" "$message_envelope_plain_text" | \
      target/protoc/bin/protoc --encode=com.vasquez.beer.SomeCustomEnvelope src/protos/beer.proto)

    # show_bytes_as_chars_and_decimal "$message_envelope_proto"

    binFilename="$tempFolder/$(basename "$filename" .txt).bin"
    printf "%s" "$message_envelope_proto" > "$binFilename"

  done

  echo "proto messages encoded [$tempFolder]"
}

function show_bytes_as_octal() {
  echo "octal bytes"
  echo "$1" | od -v -An -to1
}

function show_bytes_as_chars_and_decimal {
  echo "chars/decimal bytes"
  printf "%s" "$1" | od -v -An -tcd1
}

function convert_octal_bytes_to_escaped_string_sequence() {

    response_proto_octal=$(printf "%s" "$1" | \

      # get octal bytes
      od -v -An -to1 | \

      # squeeze spaces to single spaces
      tr -s ' ' | \

      # remove new lines
      tr -d '\n' | \

      # replace leading spaces to \ so " 001" becomes "\001"
      tr ' ' '\' | \

      # remove trailing \
      sed 's/.$//'
    )

    printf "%s" "$response_proto_octal"
}

function main() {
  go_to_root_directory
  download_protoc
  encode_messages
}

main
