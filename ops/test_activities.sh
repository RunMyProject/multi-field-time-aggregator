#!/bin/bash

# test_activities.sh
# Author: Edoardo Sabatini
# Date: 2026-05-20
# Description: This script tests the ActivityRegistry API endpoint for version 1.0. 
# It checks if the server is running, then sends a request to the /api/activities endpoint 
# with the appropriate API version header. The script prints the HTTP status, headers
# and formatted JSON response for verification.


URL="http://localhost:8080/api/activities"
HEADER="X-API-Version: 1.0"

echo "=================================================="
echo " Running ActivityRegistry API Version Tests"
echo "=================================================="
echo ""
echo "[TEST 1] Testing Activities API v1.0"
echo "------------------------------------------------"

# This check ensures that the script does not proceed with the tests 
# if the server is not running, preventing unnecessary errors and providing clear feedback to the user.
if ! curl -s --connect-timeout 2 "$URL" &>/dev/null; then
    echo "Please start the server"
    exit 1
fi

echo "--- HTTP STATUS & HEADERS ---"
curl -s -I -H "$HEADER" "$URL" | grep -E "HTTP/|Content-Type:|Date:"
echo ""

echo "--- JSON DATA PRINT ---"
curl -s -H "$HEADER" "$URL" | python3 -m json.tool

echo ""
echo "=================================================="
echo " Tests Completed!"
echo "=================================================="
