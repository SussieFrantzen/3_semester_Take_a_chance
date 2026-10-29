# Take a Chance
af Sussie Frantzen

Studerende på EK-Lyngby

3 semester portfolio projekt

### Vision
Take a Chance er det første af en række læringsorienteret brætspilsapplikationer.
Take a chance gør det interaktivt og tilgængeligt at lære om sandsynlighedsberegning.

Applikationen er primært rettet mod undervisningsbrug og henvender sig til spillere fra 6 år og opefter.
Spillet er designet med let humor, og let frustration så det ikke føles som en undervisnings quiz. 

Den understøtter både spil for én person og multiplayer-sessioner med op til fire spillere.

Applikationen er designet, så der løbende kan tilføjes flere spil.
Den første version indeholder spillet Take a Chance,
som fokuserer på sandsynlighedsberegning og hjælper 
spillerne med at udforske de matematiske begreber gennem spil.

### Links
Portfolio-hjemmeside:
https://sussiefrantzen.github.io/portfolio/


Deployet applikation (valgfrit):
https://take-a-chance.xn--fnixdesign-0cb.com/


Kildekode (repository):
https://github.com/SussieFrantzen/3_semester_Take_a_chance.git

### Arkitektur
Systemoversigt
Take a Chance er en Java-baseret applikation bygget op omkring en REST API-arkitektur. 
Backenden håndterer:
* forespørgsler
* spillets logik
* dataadgang 
* kommunikationen med databasen.

Applikationen er deployeret på DigitalOcean og benytter en ekstern API 
til at hente yderligere spørgsmål til spillets kortbunke. 

Arkitekturen er designet med henblik på fremtidig udvidelse, så der kan tilføjes flere spil.

### Anvendte teknologier
* Java 25 
<br> Applikationens primære programmeringssprog.
* Javalin 6.5
<br> Webframework til håndtering af HTTP-forespørgsler og REST API-ruter.

* Maven  
<br> Byggeværktøj til håndtering af afhængigheder og projektkonfiguration.
* Hibernate ORM 7.2
  <br> Mapper Java-objekter til relationelle databaser og håndterer persistens.
* PostgreSQL 
  <br> Relationel database til lagring af applikationens data.
* pgAdmin 
  <br>  Værktøj til administration og inspektion af PostgreSQL-databasen.
* HikariCP
  <br> Håndterer databaseforbindelser gennem connection pooling.
* Thymeleaf
  <br> Skabelonmotor til generering af HTML på serversiden.
* Jackson 
* <br> Håndterer serialisering og deserialisering af JSON-data.
* Lombok 
* <br> Reducerer gentagende standardkode i Java-klasser.
* jBCrypt
  <br>  Bruges til hashing af adgangskoder.
* JUnit 6
  <br> Framework til automatiserede tests.

* Testcontainers
  <br>  Understøtter tests med containeriserede afhængigheder, herunder PostgreSQL.
* REST Assured
  <br> Bruges til test af REST API-endpoints.
* Hamcrest
  <br> Understøtter læsbare og fleksible assertions i tests.
* DigitalOcean
 <br> Cloudplatform til hosting og deployment af applikationen.


### Applikationens struktur
Applikationen er opdelt i komponenter med forskellige ansvarsområder:

* Main
<br>
Her initieres applikationens opstart.

* Routes
<br>
Definerer API-endpoints og forbinder indgående forespørgsler med de relevante controllere.

* Controllers
<br>
Modtager og håndterer HTTP-forespørgsler samt koordinerer svar.

* Services
<br>
Indeholder applikationens forretningslogik og koordinerer operationer mellem komponenterne.

* DAO (Data Access Objects)
<br> 
Håndterer adgang til databasen og operationerne på data'en.

* Entities
<br>
Repræsenterer dataobjekter, der lagres permanent i databasen.

* DTOs (Data Transfer Objects) 
<br>
Definerer de data, der overføres mellem API-klienter og applikationen.

* Enums
<br>
Repræsenterer faste sæt af værdier.

* Utils
<br> 
Indeholder fælles hjælpefunktioner.

* Exceptions
<br> 
Understøtter håndtering af fejl og undtagelser.

* Security
<br>
håndtere adgangen til endpoint om hvem der har tilgang til applikationens funktionaliteter.


### Database og ekstern API
Applikationen anvender PostgreSQL som relationel database og Hibernate ORM til at mappe Java-entiteter til databasens tabeller.
DAO-komponenterne adskiller databaseoperationer fra applikationens forretningslogik,
mens HikariCP håndterer databaseforbindelser.

En ekstern API anvendes til at hente yderligere spørgsmål til spillets kortbunke. Dette giver mulighed for at udvide 
mængden af spørgsmål uden at skulle hardcode alle spørgsmål direkte i applikationen.


### Test
Projektet indeholder et testmiljø, der bruges til at kontrollere applikationens funktionalitet og bidrage til at opretholde en høj kodekvalitet.

Følgende værktøjer indgår i testmiljøet:

* JUnit 6
<br>
Til udvikling og afvikling af automatiserede tests.
<br>

* Testcontainers
<br>
Til test med containeriserede afhængigheder, herunder PostgreSQL.
<br>

* REST Assured
<br> 
Til test af HTTP-endpoints og REST API-funktionalitet.
<br>

* Hamcrest
<br> 
Til at formulere tydelige og fleksible testbetingelser.
<br>

* Http-client
<br>
Til at kunne Teste Endpoints og ekstern APi.


Disse værktøjer gør det muligt at teste applikationens logik, databaseintegration og API-funktionalitet.

### Deployment
Applikationen er deployeret på DigitalOcean, som leverer hostingmiljøet til at køre backend-applikationen og gøre den tilgængelig uden for det lokale udviklingsmiljø.


### Udvidelsesmuligheder
Take a Chance er designet til at kunne udvides med flere spil over tid. Den første version fokuserer på sandsynlighedsberegning med vægt på alderssvarende læring og anvendelse i undervisningen.

Opdelingen i routes, controllers, services og dataadgangskomponenterne skaber et fundament for at udvide funktionaliteten.