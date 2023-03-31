## HorairÉTS

HorairÉTS est un générateur de combinaisons d'horaire pour les étudiants de [l'École de technologie supérieure](https://www.etsmtl.ca/). L'application officielle est hébergé sur [https://horairets.emmanuelcoulombe.dev/](https://horairets.emmanuelcoulombe.dev/). Cette application est séparée en deux projets différents, soit l'interface utilisateur et le backend. Pour en savoir plus sur l'interface utilisateur de HorairÉTS, vous pouvez la retrouver [ici](https://github.com/imrashb/horairets-ui).

![horairets](https://github.com/imrashb/horairets-ui/raw/main/public/logo.png)

### Pour commencer

#### Cloner le code

```
git clone https://github.com/imrashb/horaire-ets.git
```


#### Installation

- Installer [Java 11](https://www.oracle.com/ca-fr/java/technologies/javase/jdk11-archive-downloads.html)
- Installer [Maven](https://maven.apache.org/)
- Installer [Docker](https://www.docker.com/)
- Installer [Postgres](https://www.postgresql.org/download/)

> **_NOTE:_** L'IDE [IntelliJ IDEA](https://www.jetbrains.com/idea/) permet de facilement installer Java 11 et Maven est déjà intégré

- Installer les dépendances avec Maven. IntelliJ IDEA est recommandé, tout se fait presque automatiquement

#### Variables d'environnement
Avant de démarrer l'application, vous devez remplir les variables d'environnements. IntelliJ IDEA est recommandé, il supporte bien l'injection de variables d'environnement avec une configuration d'application
| Variable d'environnement        | Description                        |
| ------------- | ---------------------------------- |
| DISCORD_TOKEN | Le token du bot Discord |
| SPRING_DATASOURCE_URL | L'URL de la base de donnée PostgreSQL. (ex. jdbc:postgresql://localhost:5432/postgres) |
| SPRING_DATASOURCE_USERNAME | L'identifiant de l'utilisateur de la base de donnée (ex. postgres) |
| SPRING_DATASOURCE_PASSWORD | Le mot de passe de l'utilisateur de la base de donnée (ex. password) |

#### Démarrer l'application

IntelliJ IDEA est recommandé pour démarrer l'application. Il y a plusieurs classes principales
- me.imrashb.Main: Démarre l'API et le bot Discord
- me.imrashb.TestImageHoraire: Démarre un interface utilisateur rudimentaire pour créer des thèmes d'horaire

#### Conteneurisation avec Docker

Ajouter les variables d'environnement dans [Dockerfile](Dockerfile) ou dans [docker-compose.yml](docker-compose.yml).

Créer le conteneur Docker avec Compose
```
docker-compose build
```

Démarrer le conteneur
```
docker-compose up
```

Arrêter le conteneur
```
docker-compose down
```

### Technologies utilisées
| Technologie de développement        | Description                        |
| ------------- | ---------------------------------- |
| [Spring Boot](https://spring.io/) | Pour facilement créer des endpoints de l'API |
| [Spring Data](https://spring.io/)    | ORM pour accéder à la base de donnée|
| [Lombok](https://projectlombok.org/)    | Accélérer le développement et simplifier le code en enlevant le code boilerplate |
| [JDA](https://github.com/DV8FromTheWorld/JDA)  | Java Discord API pour le bot Discord|
| [jsoup](https://jsoup.org/)     | Pour scrape des données provenant du site de l'ÉTS |
| [Apache PDFBox](https://pdfbox.apache.org/) | Pour extraire les informations des PDF de l'ÉTS|

| Outils de développement        | Description                        |
| ------------- | ---------------------------------- |
| [Postman](https://www.postman.com/)    | Pour tester les endpoints de l'API|
| [Docker](https://www.docker.com/)    | Pour conteneuriser l'application|
| [Heroku](https://www.heroku.com/)    | Pour héberger l'application|
