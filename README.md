## Local Run

1. Start MySQL (local default is `localhost:3306/user_management`).
2. Set required environment variables in your shell:

```bash
export COLLEGE_SCORECARD_API_KEY=your_key_here
export GITHUB_CLIENT_ID=your_client_id
export GITHUB_CLIENT_SECRET=your_client_secret
```

3. Run backend:

```bash
./mvnw spring-boot:run
```

The app reads all sensitive values from environment variables. Do not hardcode secrets in source files.

## Data Source Mode

Default is API mode:

```bash
export COLLEGE_DATA_SOURCE=api
```

Optional modes:

```bash
export COLLEGE_DATA_SOURCE=csv
export COLLEGE_DATA_SOURCE=auto
```

CSV path override (only when CSV mode is used):

```bash
export COLLEGE_CSV_PATH=/absolute/path/to/colleges.csv
```

## Render Deployment

This repo includes a ready blueprint file: `render.yaml`.

### Option A: Blueprint deploy
1. In Render, choose **New +** -> **Blueprint**.
2. Select this repository.
3. Render will load `render.yaml`.
4. Fill secret env vars before first deploy:
   - `COLLEGE_SCORECARD_API_KEY`
   - `GITHUB_CLIENT_ID`
   - `GITHUB_CLIENT_SECRET`
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`

### Option B: Manual web service
Use:
- Build command: `./mvnw clean package -DskipTests`
- Start command: `java -Dserver.port=$PORT -jar target/*.jar`

Set the same env vars listed above.

### Frontend integration
For GitHub Pages frontend, point the Jekyll setting `college_explorer_api_base` to your Render backend URL:

```text
https://<your-service>.onrender.com/api/colleges
```

Also set CORS in backend env:

```bash
FRONTEND_URL=https://pages.opencodingsociety.com
CORS_ALLOWED_ORIGINS=https://pages.opencodingsociety.com
```