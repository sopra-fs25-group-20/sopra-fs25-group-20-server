## SpyQuest Backend &middot; [![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=sopra-fs25-group-20-server&metric=coverage)](https://sonarcloud.io/summary/new_code?id=sopra-fs25-group-20-server)

SpyQuest is a social deduction game where players identify a hidden spy. Innocents know the correct street-view photo, but the spy doesn't. Through questioning, innocents try to expose the spy, while the spy gathers clues to guess the photo. Vote out the spy or outsmart the innocents to win!

## How to Run
You can either play the [live version](https://spyquest.whtvr.ch/) of SpyQuest or deploy the backend using Docker:
```bash
docker run -p 8080:8080 ghcr.io/sopra-fs25-group-20/sopra-fs25-group-20-server:latest
```