# Déploiement de l'API

En ce moment, il y a 2 scripts GitHub actions permettant de déployer l'application de 2 façons différentes:

- **Déploiement sur Heroku**: Le script `.github/workflows/heroku-deployment.yml` permet de déployer l'application sur Heroku.
- **Déploiement sur une machine distante**: Le script `.github/workflows/dockerhub.yml` permet de déployer l'application sur une machine distante. Le fonctionnement est simple: build du conteneur docker et push sur DockerHub. Ensuite, il suffit de se connecter en SSH à la machine distante et de relancer les conteneurs.

Pour le moment, le déploiement sur une machine distante est utilisé pour sauver les coûts plus élevés d'Heroku. Pour changer le script de déploiement, il suffit de rajouter **if: false** dans le workflow GitHub Actions approprié.

## Étapes de déploiement sur une machine distante

- Se connecter en SSH à la machine distante
- Installer Docker et se login avec `docker login`
- Copier les 3 fichiers suivants dans un dossier:
  - `docker-compose.yml`
  - `.env-example`
  - `Caddyfile`
- Renommer le fichier `.env-example` en `.env` et remplir les variables d'environnement. Il est important de changer les variables **DB_USERNAME** et **DB_PASSWORD** avant de démarrer le Docker Compose pour la première fois, car il est plus difficile de changer le username et le password de la base de donnée après cela.
- Modifier le fichier `Caddyfile` pour mettre le nom de domaine à la place de **localhost**
- Démarrer les conteneurs avec la commande `docker compose --env-file .env up -d`

## Configurer les secrets GitHub Actions

- Ajouter les variables suivantes dans vos secrets GitHub Actions

  - `DOCKERHUB_USERNAME`: Le nom d'utilisateur DockerHub
  - `DOCKERHUB_PASSWORD`: Le mot de passe ou token DockerHub
  - `SSH_HOST`: L'adresse IP de la machine distante
  - `SSH_USERNAME`: Le nom d'utilisateur SSH
  - `SSH_KEY`: La clé privée SSH

- Modifier à vos besoins le script de déploiement (par exemple, mettre le bon projet DockerHub, etc.)
