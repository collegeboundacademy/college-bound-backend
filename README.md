## Initialization

1. Make sure you have docker installed!

2. Run ./scripts/init_db.sh -> This will create your MySQL datebase running on port 3306

3. Make sure the container is running before starting the app

4. Also make sure `application.properties` is correctly configured (client-id, client-secret).

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