# 📦 Inventory Management System

Ein einfaches Backend Inventarverwaltungssystem basierend auf **Spring Boot**, **MySQL**, **Docker** und **JWT**.

🔗 [Projekt auf GitHub ansehen](https://github.com/Bashir-Janbalat/inventory_app)

## 🛠️ Technologien

- Java 17 & Spring Boot
- Spring Security & JWT & Redis
- Spring Data JPA (Hibernate)
- MySQL (Docker)
- Docker & Docker Compose
- GitHub Actions

---

## ▶️ Schnellstart

```bash
# 1. Jar-Datei erstellen
mvn clean package

# 2. Docker-Image bauen
docker build -t inventory_app .

# 3. Anwendung starten
docker-compose up --build
````
⚙️ Konfiguration

.env.example:
````
DATABASE_URL=jdbc:mysql://db:3306/inventory_app
DATABASE_USER=root
DATABASE_PASSWORD=root
JWT_SECRET_KEY=...
JWT.EXPIRATION.TIME=3600000
SPRING_PROFILES_ACTIVE=prod
````
📄 GitHub Actions (CI)


Automatischer Build & Deployment bei jedem Push auf main branch.

````
name: CI Pipeline
on:
    push:
      branches:
        - main
    pull_request:
      branches:
        - main
    jobs:
        build:
            runs-on: ubuntu-latest

    steps:
        - name: Checkout code
          uses: actions/checkout@v2

        - name: Set up JDK 17
          uses: actions/setup-java@v2
          with:
            distribution: 'temurin'
            java-version: '17'

        - name: Build with Maven
          run: mvn clean install

        - name: Build Docker image
          run: |
            docker build -t inventory_app .
            docker tag inventory_app:latest ${{ secrets.DOCKER_USERNAME }}/inventory_app:latest

        - name: Log in to Docker Hub
          uses: docker/login-action@v2
          with:
            username: ${{ secrets.DOCKER_USERNAME }}
            password: ${{ secrets.DOCKER_PASSWORD }}

        - name: Push Docker image to Docker Hub
          run: |
            docker push ${{ secrets.DOCKER_USERNAME }}/inventory_app:latest
````
🧪 Tests 

Die Tests wurden mit einer In-Memory H2-Datenbank durchgeführt, um die Funktionalität in einer isolierten Umgebung zu validieren.

````bash

mvn test
````

# 📚 Workflow Dokumentation

Für Details zu Produkt-, Lager- und Purchase-Workflows siehe [WORKFLOW.md](docs/WORKFLOW.md).


🧠 Autor

Bashir Janbalat

📬[ LinkedIn Profil](https://www.linkedin.com/in/bashir-janbalat/)



