# Pac-Man API <img width="48" height="48" src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original-wordmark.svg" />

This repository is the Java backend server for my solo Pac-Man project. This API connects to both MongoDB and Redis databases to store and retrieve user account and score data respectively.

The client application that pairs with this API can be found at https://github.com/jmcnally17/pacman-client

The JS Express version of this API (deployed version) can be found at https://github.com/jmcnally17/pacman-api-js

The original project monolith (with a full comprehensive commit history) can be found at https://github.com/jmcnally17/pacman-old

[<img src="./images/pacman-play-button.png">](https://projectpacman.netlify.app/)

### Technologies Used

- [Spring Boot](https://spring.io/projects/spring-boot) as a web framework
- [Gradle](https://gradle.org/) as a build tool
- [MongoDB Atlas](https://www.mongodb.com/atlas/database) and [Redis Labs](https://redis.com/) for hosting the databases
- [JUnit](https://junit.org/junit5/) as a testing framework
- [GitHub](https://github.com/) for version control
- [Intellij IDEA](https://www.jetbrains.com/idea/) as an IDE

## Running Locally

This API can be run on your localhost. However, a number of frameworks need to be installed which requires some setup to do.

### Getting Started

This webapp can be built and run using Gradle, a build automation tool for Java. However, you will need a JDK, which, like Gradle, can installed via Homebrew (which can be installed using the instructions [here](https://brew.sh/)):

```
brew install openjdk
brew install gradle
```

MongoDB and Redis will need to be installed as they are the databases that store user data and scores:

```
brew tap mongodb/brew
brew install mongodb-community@5.0
brew install redis
```

Then, start MongoDB by using:

```
brew services start mongodb-community@5.0
```

### How To Use

Now, you can clone this repository:

```
git clone https://github.com/jmcnally17/pacman-api-java.git
```

You can then build the app by entering `gradle build` while in the [main](https://github.com/jmcnally17/pacman-api-java) directory.

Your local Redis server may need to be running for the backend to connect to it. Enter

```
redis-server
```

into a separate terminal to do this.

Now you can run the server by entering `gradle bootRun` while in the [main](https://github.com/jmcnally17/pacman-api-java) directory and the API will be ready to receive requests.

In order to play the game, you must also be running the client application alongside this server (link to that repo found at the top of this README).

You can also run this API using Intellij IDEA by simply running the [App.java](https://github.com/jmcnally17/pacman-api-java/blob/main/src/main/java/pacmanapi/App.java) file (although you may need to take care with compatible JDK versions).

## Testing

Tests can be run while in the [main](https://github.com/jmcnally17/pacman-api-java) directory by running `gradle test`. Code coverage statistics are generated in a `build/coverage` folder using the [Jacoco](https://www.jacoco.org/jacoco/) plugin. Tests were written first in order to adhere to the test-driven development (TDD) process by following the red-green-refactor cycle.
