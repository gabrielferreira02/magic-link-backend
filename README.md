# Magiclink backend application

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)

# 📋 Description 

<p>This application provides a login using magic link to access an app. Below is listed features of the project<p>
  
- Sending mails
- Custom exceptions
- Docker and docker composer
- Postgres
- Logs
- Documentation
- Testing

# 🛠️ How to run

<p>First you need clone this project</p>

```bash
  git clone https://github.com/gabrielferreira02/magic-link-backend.git
```

Go to generated directory

```bash
  cd magic-link-backend
```

Now you need to generate your secret password in your google account. In your account go to security and search for password apps, now you can generate your password and set it and your email address into the docker compose file like the section below

```yaml
environment:
      SPRING_MAIL_USERNAME: your email address
      SPRING_MAIL_PASSWORD: your password
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/magiclinkdb
      SPRING_DATASOURCE_USERNAME: user
      SPRING_DATASOURCE_PASSWORD: 1234
```


Then run docker compose command to start application

```bash
  docker-compose up
```

With application running you can see the swagger documentation in following endpoint

```bash
  http://localhost:8080/swagger-ui.html
```

And, if you want to test the application use

```bash
  mvn test
```


