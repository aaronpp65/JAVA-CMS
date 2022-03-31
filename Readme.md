# Content Managment System


A client/server system that aggregates and distributes ATOM feeds.

The main elements are:

*An ATOM server (or aggregation server) that responds to requests for feeds and also accepts feed updates from clients. The aggregation server will store feed information persistently, only removing it when the content server who provided it is no longer in contact, or when the feed item is not one of the most recent 20.
*A client that makes an HTTP GET request to the server and then displays the feed data, stripped of its XML information.
*A CONTENT SERVER that makes an HTTP PUT request to the server and then uploads a new version of the feed to the server, replacing the old one. This feed information is assembled into ATOM XML after being read from a file on the content server's local filesystem.

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