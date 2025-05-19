# SpyQuest Backend &middot; [![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=sopra-fs25-group-20-server&metric=coverage)](https://sonarcloud.io/summary/new_code?id=sopra-fs25-group-20-server)

Welcome to SpyQuest, a web-based multiplayer deduction game that combines strategic thinking with real-time communication. Players can create or enter game rooms using unique room codes and participate in image-based deduction rounds supported by a live chat and voting system. The game features customisable settings, permanent stat tracking and a seamless cross-platform experience.
## How to Run
You can either play the [live version](https://spyquest.whtvr.ch/) of SpyQuest or deploy the backend using Docker and your own [Google API key](https://developers.google.com/maps/documentation/javascript/get-api-key):
```bash
docker run -e GOOGLE_MAPS_API_KEY=[INSERT] -p 8080:8080 ghcr.io/sopra-fs25-group-20/sopra-fs25-group-20-server:latest
```

## High Level Components

### [GameService](https://github.com/sopra-fs25-group-20/sopra-fs25-group-20-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs25/service/GameService.java)

Handles core game logic through real-time WebSocket interactions. It manages the [Game](https://github.com/sopra-fs25-group-20/sopra-fs25-group-20-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs25/entity/Game.java)  object and is invoked by [GameController](https://github.com/sopra-fs25-group-20/sopra-fs25-group-20-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs25/controller/GameController.java). Responsibilities include:

- Starting the round
- Handling the spy guess
- Advancing the game phase
- Updating the stats of logged-in users

### [GameReadService](https://github.com/sopra-fs25-group-20/sopra-fs25-group-20-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs25/service/GameReadService.java)

Provides current game state details to client, invoked by [GameRestController](https://github.com/sopra-fs25-group-20/sopra-fs25-group-20-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs25/controller/GameRestController.java). Information it provides include:

- The active game phase
- The list of players currently in the room
- The Current game settings
- Each player's assigned role

### [GameBroadcastService](https://github.com/sopra-fs25-group-20/sopra-fs25-group-20-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs25/service/GameBroadcastService.java)

Provides real-time updates to subscribed clients via WebSocket broadcasts. Delivers both general and personalized game updates, including:

- Current list of players in the room
- Each player’s role (personalized)
- Highlighted image index (hidden from the spy)
- Chat messages
- Current voting session status

### [VotingService](https://github.com/sopra-fs25-group-20/sopra-fs25-group-20-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs25/service/VotingService.java)

Manages voting functionality within the game, invoked by [VotingController](https://github.com/sopra-fs25-group-20/sopra-fs25-group-20-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs25/controller/VotingController.java) and handles the [VotingSession](https://github.com/sopra-fs25-group-20/sopra-fs25-group-20-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs25/entity/VotingSession.java) object. Responsibilities include:

- Creating a voting session against a player
- Casting a vote
- Handling the vote results

### [UserService](https://github.com/sopra-fs25-group-20/sopra-fs25-group-20-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs25/service/UserService.java)

Manages user accounts and profiles, allowing players to save and view their game stats. It works with the [User](https://github.com/sopra-fs25-group-20/sopra-fs25-group-20-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs25/entity/User.java) object and is invoked by [UserController](https://github.com/sopra-fs25-group-20/sopra-fs25-group-20-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs25/controller/UserController.java). It handles User interactions including:

- Registering a user
- Logging in through token or credentials
- Updating user information
- Update and get user's stats