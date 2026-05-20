#!/bin/bash

# test_activities_v2.sh - A script to test the ActivityRegistry API version 2.0 with dynamic column parsing.
# Author: Edoardo Sabatini
# Date: 2026-05-20
# Description: This script tests the ActivityRegistry API endpoint for version 2.0. 
# It checks if the server is running, then sends requests to the /api/activities endpoint
# with different groupBy parameters. The script dynamically parses the JSON response to print
# headers and values in a formatted table, allowing for flexible testing of various response structures.

BASE_URL="http://localhost:8080/api/activities"

# Verify server availability
if ! curl -s --connect-timeout 2 "$BASE_URL" &>/dev/null; then
    echo "ERROR: Server unreachable at $BASE_URL."
    exit 1
fi

# Function to execute test cases with dynamic column parsing
run_test() {
    local label=$1
    local query=$2
    echo -e "\n--- [TEST CASE] $label ---"
    
    # Python dynamically finds columns and prints them
    curl -s -H "X-API-Version: 2.0" "${BASE_URL}${query}" | python3 -c "
import sys, json
data = json.load(sys.stdin)
if not data: 
    print('No data available')
    sys.exit(0)

# Helper to flatten nested objects (like project/employee)
def format_value(v):
    if isinstance(v, dict):
        return v.get('name', str(v))
    return str(v)

# Get headers dynamically from the first object
headers = list(data[0].keys())

# Print headers
print(' | '.join([f'{h:<20}' for h in headers]))
print('-' * 60)

# Print rows
for row in data:
    vals = [format_value(row.get(h, '')) for h in headers]
    print(' | '.join([f'{v:<20}' for v in vals]))
"
}

echo "=================================================="
echo " Running ActivityRegistry API Version 2.0 Tests"
echo "=================================================="

run_test "Default (NONE)" ""
run_test "Group by PROJECT" "?groupBy=PROJECT"
run_test "Group by PROJECT_EMPLOYEE" "?groupBy=PROJECT_EMPLOYEE"
run_test "Group by EMPLOYEE_PROJECT" "?groupBy=EMPLOYEE_PROJECT"

echo -e "\n=================================================="
echo " All Tests Completed Successfully!"
