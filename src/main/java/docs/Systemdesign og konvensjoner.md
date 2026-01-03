# Systemdesign og konvensjoner 
Havaara og Blaahvalen

## Introduksjon
Blaahvalen er en java-applikasjon for personlig regnskap og budsjett, og Havaara er et javabibliotek som skal gjøre 
det enklest mulig å produsere applilkasjoner i rammeverket Vaadin med god funksjonalitet, et pent utseende og
tilpasset norske forhold. I dette dokumentet oppsummeres konvensjoner for koden, slik at den blir lettere å vedlikeholde.

## Generelt systemdesign
Blaahvalen bruker Spring Boot (som Vaadin) og har en Model - Component- View (MCP) - orientering. Det innebærer
at koden inneholder Views, Repositories og Services.

### Repositories
Alle spørringer legges til interfacer hvor navnet slutter på "Repository".  Spørringene kan være håndlagde eller 
automatiske (ved hjelp av JPA). Hvert repo skal *bare* ha metoder som returnerer entiteten den er satt til, 
eller behandlede egenskaper ved denne entiteten.

### Services
Alle kommunikasjon med databasen skal skje gjennom en klasse hvor navnet slutter på "Service". Det er viktig at ikke de 
andre klassene jukser og går rett på Repository. Årsaken er at vi vil unngå at utvikleren (meg, altså) mister oversikten 
over hvilke spørringer som faktisk er implementert, og hvordan metodenavnene er bygget opp. Er metodene spredd på flere 
steder, må utvikler lete flere steder også når metodene skal brukes. Jeg hadde allerede laget flere redundante metoder 
før jeg innså dette.

I Service ligger også alle metodene for CRUD, altså oppretting, redigering og sletting av entiteter. Som med Repositories 
skal hver Service organiseres etter hva den 

### Views og Components
Alt brukeren ser ligger i klasser som ekstenderer en layoutklasse i Vaadin, og er annotert med @View eller @Component.
Views skal mest mulig bygge på moduler som utvikler kan sette sammen etter behov. Den viktigste av disse er så langt 
"Redigeringsomraade", som håndterer redigering av alle feltene til en Entitet. 

### Navngiving på klasser generelt
Jeg bruker følgende norske navn:

| Endelse     | Forklaring |
|-------------|------------|
| - Kyklop    | Singleton-klasse, dvs. en enkelt instans som er globalt tilgjengelig via den statiske metoden "hent()". <br/>Der klassen bruker et interface hentes den med metoden "bruk()" |
| - Mester    | Klasse med statiske eller dynamiske metoder som kan gjenbrukes av andre. |
| - Assistent | Klasse som spesifiserer funksjonaliteten til en annen, typisk med endelsen "Mester" | 

### Allvitekyklop og OmView
Singletonklassen "Allvitekyklip" gjør sentrale  objekter i applikasjonen tilgjengelig for hverandre via "getters" og "setters". Sørger for at initiering av 
hver klasse skjer i riktig rekkefølge, og håndterer dependency injection mer eksplisitt. Jeg liker det, fremfor å stole 
på at Spring Boot gjør alt slik jeg tror den gjør det. 

Initieringen skjer i OmView, som også viser en oversikt over applikasjonens hensikt, historie og omfang. I tillegg håndteres 
backup og restore her, via rene CSV tekstfiler (;-separert) som er pakket inn i en zip-fil. Årsaken er at jeg vil være 
sikker på at dataene er tilgjenglig også om 20 år. Backup og restore er mer effektivt direkte på databasen, men der har 
jeg tidligere gjort feil, og ikke fått det til med gamle data. 

### Ekstensjon  av maler
De fleste klassene som skal gjenbrukes av andre med ekstensjon har endelsen "-Mal", og en generalisert forstavelse
(f.eks. PeriodeServiceMal, som brukes av MaanedsoversiktService og AarsoversiktService). Bruk av maler gir konsistens og 
rask koding, men gir også rigiditet. Jeg begynte med å styre variasjoner i koden i malen, men har sett at det gir lang 
og uoversiktlig kode. I stedet vil jeg legge mest mulig i subklassen, og bruke @Override for å sette inn egen kode, 
men likevel utnytte standardfunksjonalitet i malen. Det betyr at alle felter og komponenter i malen må være tilgjengelig 
for subklassen på en effektiv måte, f.eks. via "access modifier protected". De mest brukte komponentene og metodene skal 
imidlertid være "public" med "override i interface", og felter som har en "public getter" skal fortsatt være "private".





