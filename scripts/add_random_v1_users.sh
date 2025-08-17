#!/bin/bash

# A simple list of names to choose from randomly
names=(Liam Olivia Noah Emma Oliver Ava James Isabella Sophia Mia)

echo "Adding 100 random users..."

for i in {1..100}
do
  # Pick a random name from the list
  RANDOM_NAME=${names[$RANDOM % ${#names[@]}]}

  # Generate a random age between 18 and 70
  RANDOM_AGE=$((18 + RANDOM % 53))

  # Construct the JSON payload
  JSON_PAYLOAD="{\"name\":\"$RANDOM_NAME-$i\",\"age\":$RANDOM_AGE}"

  echo "Adding user: $JSON_PAYLOAD"

  # Send the POST request using curl
  curl -X POST http://0.0.0.0:8080/users \
       -H "Content-Type: application/json" \
       -d "$JSON_PAYLOAD"

done

echo "Finished adding 100 users."