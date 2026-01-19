# Raadyret
Et regnskapsprogram for Tina og Hallvard. Tidligere kalt Blaahvalen. Utviklet i java og Vaadin.<br/> 
Prosjektet startet 25.10.2025, og foregår fortsatt. Dokumentasjon oppdatert 19.01.2026


## Installering

1. Sørg for at pom.xml bruker "war" som format for bygget, og at følgende kode er ikke kommentert ut:
c
```
<!-- kommenter ut denne ved utvikling
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-tomcat</artifactId>
        <scope>provided</scope>
    </dependency>
-->
```

2. Kjør ```mvn clean compile package -Pproduction```<br/><br/>

3. Gå til prosjektets mappe, og kjør følgende script i BASH:<br/> ```./send-til-tomcat.sh target/blaahvalen-0.9-SNAPSHOT.war```<br/><br/>

4. Appen skal starte av seg selv. Hvis det ikke skjer noe, må du sjekke i adminverktøyet laptopserver:8080/admin og se 
i loggene under /opt/tomcat10/logs, for eksempel ```tail -n100 catalina.out```. Sjekk at mariadb kjører, med 
```ps -e | grep maria```



## Datastruktur
Se følgende figur:<br/>
<img src="img/datastruktur.png" alt="diagram av datastrukturen" width="300" />

Datamodellen er sterkt forenklet:<br/>
- **Periode**: Årsoversikt, Månedsoversikt
- **Periodepost**: Årsoversiktpost, Månedsoversiktpost og Kostnadspakke (=periodeoversiktpost)
- **Post**: Normalposter, Budsjettposter



## Script for å overføre war-filen til tomcat/webapps på laptopserveren
Det ble kjempetungvinnt å kopiere war-filen til laptopserveren, sette eier til tomcat:tomcat og derfra kopiere den til 
/opt/tomcat10/webapps hele tiden. Derfor skrev jeg følgende terminalscript: 

```
#!/bin/bash

# Check if a file path is provided as an argument
if [ $# -eq 0 ]; then
    echo "<b>Error: Please provide a file path as an argument</b>"
    echo "Usage: $0 /path/to/source/file"
    exit 1
fi

# Source file path from first argument
SOURCE_FILE="$1"

# Verify the source file exists
if [ ! -f "$SOURCE_FILE" ]; then
    echo "<b>Error: Source file does not exist</b>"
    exit 1
fi

# Remote server details
REMOTE_USER="hallvard"
REMOTE_HOST="laptopserver"
REMOTE_HOME="/home/hallvard"

# Destination filename and Tomcat webapps path
DEST_FILENAME="blaahvalen.war"
TOMCAT_WEBAPPS="/opt/tomcat10/webapps"

# Step 1: Copy file to remote home directory
scp "$SOURCE_FILE" "${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_HOME}/"

# Check if SCP was successful
if [ $? -ne 0 ]; then
    echo "<b>Error: File copy failed</b>"
    exit 1
fi

# Step 2 & 3: SSH with explicit sudo commands
ssh -t "${REMOTE_USER}@${REMOTE_HOST}" "
    # Rename the file
    mv ${REMOTE_HOME}/$(basename "$SOURCE_FILE") ${REMOTE_HOME}/${DEST_FILENAME} &&
    
    # Change ownership to tomcat:tomcat
    sudo chown tomcat:tomcat ${REMOTE_HOME}/${DEST_FILENAME} &&
    
    # Copy to Tomcat webapps directory
    sudo cp ${REMOTE_HOME}/${DEST_FILENAME} ${TOMCAT_WEBAPPS}/${DEST_FILENAME} &&
    
    # Ensure correct ownership in Tomcat webapps
    sudo chown tomcat:tomcat ${TOMCAT_WEBAPPS}/${DEST_FILENAME}
"

# Check if SSH operations were successful
if [ $? -eq 0 ]; then
    echo "<b>File successfully copied, renamed, and placed in Tomcat webapps</b>"
    echo "<b>File ownership set to tomcat:tomcat</b>"
else
    echo "<b>Error: Failed to complete remote operations</b>"
    exit 1
fi

```

