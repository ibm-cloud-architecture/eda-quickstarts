curl -X 'POST' \
  'http://localhost:8080/api/v1/orders' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "customerID": "Customer01",
  "destinationAddress": {
    "city": "SF",
    "country": "USA",
    "state": "CA",
    "street": "lombart st",
    "zipcode": "90000"
  },
  "productID": "P04",
  "quantity": 10
}'