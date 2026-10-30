## Introduktion
   Dokumentation af Take a chance REST API. Det er udviklet i Java med Javalin og Hibernate/JPA og bruges til at håndtere brugere,
   spil, spilleplader, spillerstatus, terningkast, møntkast, skæbnekort og spørgsmålskort.

```
+------------------------+---------------------------+
| Information            | Beskrivelse               |
+------------------------+---------------------------+
| Base URL               | http://localhost:7070     |
| Programmeringssprog    | Java                      |
| Framework              | Javalin                   |
| Databasehåndtering     | Hibernate / JPA           |
| Dataformat             | JSON                      |
+------------------------+---------------------------+
````

### Endpointoversigt

```
+--------+--------------------------------------------------+--------------------------+--------------------------+------------------+
| Metode | URL                                              | Request Body             | Response                 | Fejl             |
+--------+--------------------------------------------------+--------------------------+--------------------------+------------------+
| POST   | /users/register                                  | username, password,      | UserDTO                  | E1, E5, E8       |
|        |                                                  | email                    |                          |                  |
| POST   | /users/login                                     | username, password       | UserDTO                  | E1, E2, E8       |
| POST   | /users/logout                                    | Ingen                    | Ingen (204)              | E2, E8           |
| GET    | /users/me                                        | Ingen                    | UserDTO                  | E2, E4, E8       |
+--------+--------------------------------------------------+--------------------------+--------------------------+------------------+
| GET    | /game-selection/names                            | Ingen                    | [GameNameDTO]            | E2, E8           |
| GET    | /game-selection/boards?gameNameId={id}           | Ingen                    | [GameBoardDTO]           | E1, E2, E4, E8   |
+--------+--------------------------------------------------+--------------------------+--------------------------+------------------+
| POST   | /games/                                          | CreateGameRequest        | Spil-objekt              | E1, E2, E4,      |
|        |                                                  |                          |                          | E5, E7, E8       |
| GET    | /games/{gameId}                                  | Ingen                    | Game-objekt              | E1, E2, E3,      |
|        |                                                  |                          |                          | E4, E8           |
| POST   | /games/{gameId}/turn                             | Ingen                    | TurnResult               | E1, E2, E3,      |
|        |                                                  |                          |                          | E5, E8           |
| POST   | /games/{gameId}/monkey-challenge/complete        | Ingen                    | Ingen (204)              | E1, E2, E3,      |
|        |                                                  |                          |                          | E5, E8           |
| POST   | /games/{gameId}/card-action/complete             | Ingen                    | Ingen (204)              | E1, E2, E3,      |
|        |                                                  |                          |                          | E5, E8           |
| POST   | /games/{gameId}/users/{userId}/played            | Ingen                    | Ingen (204)              | E1, E2, E3,      |
|        |                                                  |                          |                          | E4, E5, E8       |
| GET    | /games/{gameId}/users/{userId}/played            | Ingen                    | hasPlayedBefore: boolean | E1, E2, E3,      |
|        |                                                  |                          |                          | E4, E8           |
+--------+--------------------------------------------------+--------------------------+--------------------------+------------------+
| POST   | /dice/roll                                       | Ingen                    | value, message           | E2, E8           |
| POST   | /coin/flip                                       | Ingen                    | result, message          | E2, E8           |
+--------+--------------------------------------------------+--------------------------+--------------------------+------------------+
| GET    | /question-cards/                                 | Ingen                    | [QuestionCardDTO]        | E2, E8           |
| GET    | /question-cards/{id}                             | Ingen                    | QuestionCardDTO          | E1, E2, E4, E8   |
| POST   | /question-cards/generate                         | subject, ageGroup,       | [QuestionCardDTO]        | E1, E2, E4,      |
|        |                                                  | cardDeckId               |                          | E5, E6, E8       |
+--------+--------------------------------------------------+--------------------------+--------------------------+------------------+
```

### Brugere - Registrering og login/out
Håndtere brugeroprettelse, login, logout og oplysninger om den aktuelle bruger.


#### Opret bruger:
* Metode: POST
* URL: http://localhost:7070/users/register

Bruges ved registrering og ved returnering af brugeroplysninger

Eksempel på request body ved registrering:

````json
 {
"username": "Barry Allen",
"password": "password!",
"email": "burger@example.com"
}
````

#### Log ind:
* Metode: POST
* URL: http://localhost:7070/users/login

Eksempel på request body ved registrering:
````json
{
"username": "Barry Allen",
"password": "TestPassword123!"
}
````

#### Log ud:
* Metode: POST
* URL: http://localhost:7070/users/logout


#### Hent aktuel bruger:
* Metode: GET
* URL: http://localhost:7070/users/me

____________________________________________
### Spilvalg

#### Hent spil navne:
* Metode: GET
* URL: http://localhost:7070/game-selection/names

Eksempel:
```json
[
  {
    "id": 1,
    "name": "take a chance"
  }
]
```

#### Hent spillet for valgt spil:
* Metode: GET
* URL: http://localhost:7070/game-selection/boards?gameNameId=1

Eksempel:
```json
[
  {
    "id": 1,
    "boardName": "Take a chance",
    "subject": "Sandsynlighedsberegning",
    "boardLength": "MEDIUM",
    "gameNameId": 1,
    "gameName": "Take a chance"
  }
]
```
_______________________________________________


### Spil oprettelse og tur

#### Opret et spil:
* Metode: POST
* URL: http://localhost:7070/games/

Eksempel:
```json
[
  {
    "id": 1,
    "name": "Take a chance"
  }
]
````

#### Hent spilleplade for et spil:
* Metode: GET
* URL: http://localhost:7070/game-selection/boards?gameNameId=1

Eksempel request body:
```json
[
  {
    "id": 1,
    "boardName": "Take a chance",
    "subject": "Sandsynlighedsberegning",
    "boardLength": "MEDIUM",
    "gameNameId": 1,
    "gameName": "Take a chance"
  }
]
```
Forventet resonce eksempel:
```json
{
  "id": 1,
  "gameBoardId": 1,
  "boardName": "Take a chance",
  "currentPlayerIndex": 0,
  "players": [
    {
      "id": 1,
      "name": "Spiller 1",
      "age": 12,
      "position": 0,
      "points": 0
    }
  ]
}
````
Eksemplet illustrerer responsens struktur. De faktiske værdier afhænger af spillet og dets spillere.

Krav:

* Brugeren skal være logget ind.
* Den valgte spilleplade skal eksistere i databasen.
* Request body skal indeholde de nødvendige oplysninger om spillepladen og spillerne.


#### Hent spil ud fra spilID:
* Metode: GET
* URL: http://localhost:7070/games/{gameId}


#### Udfør en spilrunde:
* Metode: POST
* URL: http://localhost:7070/games/{gameId}/turn


#### Afslut Abeudfordring:
* Metode: POST
* URL: http://localhost:7070/games/{gameId}/monkey-challenge/complete


#### Afslut korthandling:
* Metode: POST
* URL: http://localhost:7070/games/{gameId}/card-action/complete

___________________________________________________

### Spillerstatus — Registrering af tidligere spil

#### Registrer en bruger har spillet et bestemt spil før:
* Metode: POST
* URL: http://localhost:7070/games/{gameId}/users/{userId}/played

#### Hent spillerstatus
* Metode: GET
* URL: http://localhost:7070/games/{gameId}/users/{userId}/played

____________________________________________________________

### Terning
#### Terningkast:
* Metode: POST
* URL: http://localhost:7070/dice/roll

___________________________________________________________

### Møntkast
#### Plat eller Krone:
* Metode: POST
* URL: http://localhost:7070/coin/flip

_______________________________________________________

### Spørgsmålskort

#### Hent alle spørgsmålskort:
* Metode: GET
* URL: http://localhost:7070/question-cards/

#### Hent ét spørgsmålskort:
* Metode: GET
* URL: http://localhost:7070/question-cards/{id}

#### Generer spørgsmålskort:
* Metode: POST
* URL: http://localhost:7070/question-cards/generate

Eksempel request body:
``` json
{
  "subject": "Sandsynlighedsberegning",
  "ageGroup": "10-12 år",
  "cardDeckId": 1
}
```
Eksempel response:
``` json
{
  "question": "Du trækker lod med 5 af dine venner hvad er sandsynligheden for du ikke trækker det korteste strå?",
  "answer": "20 %",
  "difficulty": "EASY"
}
```

#### Krav:
* Emne skal angives.
* Aldersgruppe skal angives.
* ID'et på kortbunken skal angives.
* Kortbunken skal eksistere i databasen.


### Fejl-koder
```
+------+-------------+--------------------------------------------------------+
| Kode | HTTP-status | Forklaring                                             |
+------+-------------+--------------------------------------------------------+
| E1   | 400         | Ugyldig forespørgsel, JSON, parameter eller ID.        |
| E2   | 401         | Brugeren er ikke logget ind, eller session er ugyldig. |
| E3   | 403         | Brugeren har ikke adgang til det pågældende spil.      |
| E4   | 404         | Den ønskede ressource findes ikke.                     |
| E5   | 409         | Konflikt med spillets tilstand eller eksisterende data.|
| E6   | 502         | Fejl ved kommunikation med OpenAI.                     |
| E7   | 503         | Der kan ikke skaffes tilstrækkeligt med spørgsmål.     |
| E8   | 500         | Intern serverfejl.                                     |
+------+-------------+--------------------------------------------------------+
```