
if [[ -z "$MS_URL" ]]
then
  echo $MS_URL
fi
curl -v POST $MS_URL/api/v1/orders    -H 'Content-Type: application/json' -d@$1
