RESOURCE_URL="https://athena.example-api.com/v1"

for i in {1..25}
do
    echo "Iteration: ${i}"
    curl -s "${RESOURCE_URL}/venues/5" \
    & curl -s "${RESOURCE_URL}/users/3" \
    & curl -s "${RESOURCE_URL}/users?limit=10&offset=10" \
    & curl -s "${RESOURCE_URL}/users?limit=25&offset=8" \
    & curl -s "${RESOURCE_URL}/salesbyseller/3" \
    & curl -s "${RESOURCE_URL}/salesbycategory?date=2020-1-1&limit=&offset=" \
    & curl -s "${RESOURCE_URL}/sales/3" \
    & curl -s "${RESOURCE_URL}/sales?limit=15&offset=25" \
    & curl -s "${RESOURCE_URL}/listings/2" \
    & curl -s "${RESOURCE_URL}/listings?limit=25&offset=2" \
    & curl -s "${RESOURCE_URL}/events/3" \
    & curl -s "${RESOURCE_URL}/events?limit=25&offset=4" \
    & curl -s "${RESOURCE_URL}/dates/1827" \
    & curl -s "${RESOURCE_URL}/dates?limit=25&offset=29" \
    & curl -s "${RESOURCE_URL}/categories/3" \
    & curl -s "${RESOURCE_URL}/categories?limit=25&offset=1" \
    & curl -s "${RESOURCE_URL}/buyerlikes"
done