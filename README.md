## Initialization

1. Make sure you have docker installed!

2. Run ./scripts/init_db.sh -> This will create your MySQL datebase running on port 3306

3. Make sure the container is running before starting the app

4. Also make sure `application.properties` is correctly configured (client-id, client-secret).

5. For live College Explorer data, set your College Scorecard API key before running:

```bash
export COLLEGE_SCORECARD_API_KEY=your_key_here
```

You can request a key from api.data.gov and use the same shell session to run `./mvnw spring-boot:run`.

## CSV mode (no signup required)

If you do not want an API key, College Explorer can read from a local CSV dataset.

CSV mode is the default source now.

1. Put your CSV file somewhere on disk (or use the bundled starter file at `src/main/resources/data/colleges.csv`).
2. Export the path before starting backend:

```bash
export COLLEGE_CSV_PATH=/absolute/path/to/colleges.csv
```

3. Run the backend normally:

```bash
./mvnw spring-boot:run
```

Optional: switch source modes when needed:

```bash
export COLLEGE_DATA_SOURCE=csv   # default
export COLLEGE_DATA_SOURCE=api   # API only
export COLLEGE_DATA_SOURCE=auto  # try API, then CSV
```

Expected CSV headers:

```text
id,name,state,type,acceptanceRate,averageNetPrice,retentionRate,satMidpoint,actMidpoint,averageGpa,undergradEnrollment,dataLastRefreshed
```

## FAQ

### **IF THE DATABASE IS CONFIGURED WRONG OR NOT RUNNING ON THE CORRECT PORT, IT WILL FAIL**

### How do I send a request from the frontend?

Make sure you include the following:

```js
{
    method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Access-Control-Allow-Origin": "true"
        },
        credentials: "include",
        body: JSON.stringify({...})
}
```