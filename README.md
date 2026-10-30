# Calculator DevOps

A Spring Boot Calculator REST API with a CI/CD pipeline: GitHub -> Jenkins -> Docker Hub -> Docker Compose (Nginx + API).

## API

| Operation | Endpoint |
|---|---|
| Add | `GET /api/v1/add?a=2&b=3` |
| Subtract | `GET /api/v1/subtract?a=5&b=3` |
| Multiply | `GET /api/v1/multiply?a=2&b=3` |
| Divide | `GET /api/v1/divide?a=6&b=3` (b=0 returns HTTP 400) |
| Health | `GET /actuator/health` |

Example response: `{"operation":"add","a":2.0,"b":3.0,"result":5.0}`

Errors (missing/non-numeric parameter, division by zero) return HTTP 400 with a JSON body containing `timestamp`, `status`, `error` and `message`.

## Build, test, run

Requires JDK 17 and Maven.

```
mvn clean package      # build target/calculator-api.jar
mvn test               # unit + MockMvc tests (reports in target/surefire-reports)
java -jar target/calculator-api.jar
curl "http://localhost:8080/api/v1/add?a=2&b=3"
```

## Docker

```
docker build -t calculator-api .
docker run -p 8080:8080 calculator-api
```

## Docker Compose (API behind Nginx)

```
docker compose up -d --build
curl "http://localhost:8081/api/v1/add?a=2&b=3"
curl http://localhost:8081/actuator/health
docker compose down
```

The Nginx proxy is published on host port 8081; the API itself is only reachable inside the `calc-net` network.

## Jenkins pipeline

The `Jenkinsfile` defines these stages: Checkout, Build, Test, Docker Build, Push, Deploy. It is triggered by a GitHub webhook.

Jenkins setup:
- Credentials: a "Username with password" credential with ID `dockerhub-creds` (Docker Hub user + access token).
- Set `DOCKERHUB_USER` and `NOTIFY_EMAIL` at the top of the `Jenkinsfile`.
- Configure SMTP under Manage Jenkins -> System for email notifications.
- GitHub webhook payload URL: `<public-url>/github-webhook/` (content type `application/json`).
