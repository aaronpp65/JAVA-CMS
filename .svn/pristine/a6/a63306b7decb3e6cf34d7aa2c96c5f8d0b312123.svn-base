# Distributed Systems

## Assignment 2 

Aaron Peter Poruthoor
a1835075

### Compling

Compile files using

```
javac *.java
```

### Running

All the commands to run the project and do testing is test.sh. Give permission to the file using 

```
chmod 777 test.sh

```
To run 
```
./test.sh
```
The expected outcome is 

```

Test Succesful 

```

### Input

The input for client is given in  

```
conten1.txt
```
Each client has a unique id :
```
id:urn:uuid:61a76c80-d399-11d9-b93C-0003939e0af6
```

### DB
The content is maintained by server in db.txt. It follows a CSV like format with a delimitter ```~```
Each line of the db is of the form
```
portNumber~liveness~ATOMFeed~ClientID
```

### Heart beat
This is achieved using ```isClosed()``` function. A single thread is mainted to keep track of liveness of content server along with a List of sockets. We iterate through these sockets every 12 seconds and isClosed() returns true for content servers that are dead. We then update the corresponding entery in db.txt