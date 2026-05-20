#!/bin/bash

# test_hello.sh - Script to test the HelloController API versioning.
# Author: Edoardo Sabatini
# Date: 2026-05-20
# Description: This script uses curl to send HTTP requests to the HelloController endpoint
# with different API version headers to verify that the correct responses are returned based on
# the API versioning logic implemented in the controller.

# ANSI Color codes for clean output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "=================================================="
echo " Running Spring Boot 4 / Spring 7 API Version Tests"
echo "=================================================="
echo ""

# 1. Test API Version 1.0 (Expected: 200 OK)
echo -e "${GREEN}[TEST 1] Testing API v1.0 (Should return 200 OK)...${NC}"
curl -i -H "X-API-Version: 1.0" http://localhost:8080/api/hello
echo -e "\n"

echo "--------------------------------------------------"
echo ""

# 2. Test API Version 2.0 (Expected: 400 Bad Request)
echo -e "${RED}[TEST 2] Testing API v2.0 (Should return 400 Bad Request)...${NC}"
curl -i -H "X-API-Version: 2.0" http://localhost:8080/api/hello
echo -e "\n"

echo "=================================================="
echo " Tests Completed!"
echo "=================================================="
