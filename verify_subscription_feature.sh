#!/bin/bash

# Base URL
BASE_URL="http://localhost:8080"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}Starting Subscription Manager Feature Verification...${NC}"

# 1. Register a generic User
USERNAME="testuser_$(date +%s)"
echo -e "\n${GREEN}1. Registering user: $USERNAME${NC}"
curl -s -X POST "$BASE_URL/register" \
     -H "Content-Type: application/json" \
     -d "{\"username\": \"$USERNAME\", \"password\": \"password\"}"

# 2. Get User ID
echo -e "\n${GREEN}2. Getting User ID for: $USERNAME${NC}"
USER_RESPONSE=$(curl -s -X GET "$BASE_URL/getUser?username=$USERNAME")
echo "Response: $USER_RESPONSE"
USER_ID=$(echo $USER_RESPONSE | grep -o '"id":[0-9]*' | awk -F: '{print $2}')
echo "User ID: $USER_ID"

if [ -z "$USER_ID" ]; then
    echo -e "${RED}Failed to get User ID. Exiting.${NC}"
    exit 1
fi

# 3. Create Subscription
echo -e "\n${GREEN}3. Creating Subscription for User ID: $USER_ID${NC}"
SUBSCRIPTION_DATA='{
  "serviceName": "Netflix",
  "planType": "Premium",
  "nextRenewalDate": "2023-12-25",
  "amount": 15.99,
  "currency": "USD"
}'
CREATE_RESPONSE=$(curl -s -X POST "$BASE_URL/api/subscriptions/createSubscription/$USER_ID" \
     -H "Content-Type: application/json" \
     -d "$SUBSCRIPTION_DATA")
echo "Response: $CREATE_RESPONSE"
SUBSCRIPTION_ID=$(echo $CREATE_RESPONSE | grep -o '"id":[0-9]*' | head -1 | awk -F: '{print $2}')
echo "Subscription ID: $SUBSCRIPTION_ID"

if [ -z "$SUBSCRIPTION_ID" ]; then
    echo -e "${RED}Failed to create Subscription. Exiting.${NC}"
    exit 1
fi

# 4. Get All Subscriptions for User
echo -e "\n${GREEN}4. Getting All Subscriptions for User ID: $USER_ID${NC}"
curl -s -X GET "$BASE_URL/api/subscriptions/user/$USER_ID" | grep "$SUBSCRIPTION_ID"
if [ $? -eq 0 ]; then
    echo -e "${GREEN}Subscription found in list.${NC}"
else
    echo -e "${RED}Subscription NOT found in list.${NC}"
fi

# 5. Get Subscription by ID
echo -e "\n${GREEN}5. Getting Subscription by ID: $SUBSCRIPTION_ID${NC}"
curl -s -X GET "$BASE_URL/api/subscriptions/subscription/$SUBSCRIPTION_ID"

# 6. Update Subscription
echo -e "\n${GREEN}6. Updating Subscription ID: $SUBSCRIPTION_ID${NC}"
UPDATE_DATA='{
  "serviceName": "Netflix",
  "planType": "Standard",
  "nextRenewalDate": "2024-01-25",
  "amount": 12.99,
  "currency": "USD"
}'
curl -s -X PUT "$BASE_URL/api/subscriptions/updateSubscription/$SUBSCRIPTION_ID" \
     -H "Content-Type: application/json" \
     -d "$UPDATE_DATA"

# 7. Delete Subscription
echo -e "\n${GREEN}7. Deleting Subscription ID: $SUBSCRIPTION_ID${NC}"
curl -s -X DELETE "$BASE_URL/api/subscriptions/deleteSubscription/$SUBSCRIPTION_ID"

echo -e "\n${GREEN}Verification Complete!${NC}"
