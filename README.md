
# Project Title

TAMAPI is a simple User/Customer API based on Springboot, Admin users can manage other users and normal users can manage customers

# Requisites

- An instance of Postgresql 11.14
- Java 11
- A json key from gcp (To push files to a gcp bucket)




# Installation

```bash
  git clone https://github.com/joseluiselp/TAMAPI.git
  cd TAMAPI
```

Then, Go to `\tam-java-api\src\main\resources` and paste the GCP json key there, also in that same folder configure your database connection params

```bash
server.port=YOUR_PORT
spring.datasource.url=jdbc:postgresql://localhost:5434/YOUR_DB_NAME
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASSWORD
```

After that you can use maven to download all dependencies needed.
    
# Usage/Examples

Inside `AuthController.java` are the only 2 open endpoints:

- `localhost:YOUR_PORT/signup` to create a sample admin user with password `password`, would return 
```json
{
    "id": 1,
    "email": "admin@email.com",
    "isAdmin": true
}
```

- `localhost:YOUR_PORT/signin` to retreive a jwt token used for all other endpoints, would return
```json
{
    "jwt": "eyJhbGciOi...."
}
```

You can take a look at the rest of the enpoints with [this](https://www.getpostman.com/collections/1e6f29ba102353d8ef7c) postman collection, remember to create the environment!
