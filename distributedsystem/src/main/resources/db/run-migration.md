## Run migrations 

To run migration manually execute these command in your favorite command line, note that you need to have installed postgres and configured in the environment correctly  

```sh
psql -U postgres -d hsds_1 -W -c "CREATE TABLE IF NOT EXISTS transaction ( transaction_id VARCHAR(255) PRIMARY KEY, status VARCHAR(50), amount DECIMAL(19, 2), currency VARCHAR(10), description TEXT, user_id VARCHAR(255))"
```

```sh
psql -U postgres -d hsds_2 -W -c "CREATE TABLE IF NOT EXISTS transaction ( transaction_id VARCHAR(255) PRIMARY KEY, status VARCHAR(50), amount DECIMAL(19, 2), currency VARCHAR(10), description TEXT, user_id VARCHAR(255))"
```

```sh
psql -U postgres -d hsds_3 -W -c "CREATE TABLE IF NOT EXISTS transaction ( transaction_id VARCHAR(255) PRIMARY KEY, status VARCHAR(50), amount DECIMAL(19, 2), currency VARCHAR(10), description TEXT, user_id VARCHAR(255))"
```
