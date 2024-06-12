#!/bin/bash
set -e

# Create the database
psql -v ON_ERROR_STOP=1 --username hs <<-EOSQL
    CREATE DATABASE hsds OWNER hs;
EOSQL
