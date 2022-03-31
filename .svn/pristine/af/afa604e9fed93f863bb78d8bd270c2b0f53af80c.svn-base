!/bin/bash

clear
echo '###############################################' >> outputClient.txt
echo '#Running test for multiple PUT followed by GET#' >> outputClient.txt
echo '###############################################' >> outputClient.txt


# running server
java Server &
sleep 3

#running content servers
java ContentServer 127.0.0.1 content1.txt &
sleep 1
java ContentServer 127.0.0.1 content2.txt &
sleep 1

java Client 127.0.0.1 >> outputClient.txt &
sleep 3

pkill -9 java

clear
echo '#####################################################################' >> outputClient.txt
echo '#Running test for GET only displaying data from live content servers#' >> outputClient.txt
echo '#####################################################################' >> outputClient.txt

# running server
java Server &
sleep 3

#running content servers
java ContentServer 127.0.0.1 content1.txt &
P1=$!
sleep 1
java ContentServer 127.0.0.1 content2.txt &
sleep 1

# kills the content server 
kill ${P1}

# wait 12 seconds to make sure heartbeat mechanism reflects in db.txt
sleep 12

java Client 127.0.0.1 >> outputClient.txt &
sleep 3

pkill -9 java

clear
echo '##############################################' >> outputClient.txt
echo '#Running test for sequence PUT, GET, PUT, GET#' >> outputClient.txt
echo '##############################################' >> outputClient.txt

# running server
java Server &
sleep 3

#running content servers
java ContentServer 127.0.0.1 content1.txt &
sleep 1

java Client 127.0.0.1 >> outputClient.txt &
sleep 1

java ContentServer 127.0.0.1 content2.txt &
sleep 1

java Client 127.0.0.1 >> outputClient.txt &
sleep 1

pkill -9 java

clear

echo '####################################################' >> outputClient.txt
echo '#Running test for fault tolerance of content server#' >> outputClient.txt
echo '####################################################' >> outputClient.txt


# running server
java Server &
sleep 3

#running content servers
java ContentServer 127.0.0.1 content1.txt &
P2=$!
sleep 1
# kills the content server 
kill ${P2}

# wait 12 seconds to make sure heartbeat mechanism reflects in db.txt
sleep 12
# starting the content server again (servers the same content)
java ContentServer 127.0.0.1 content1.txt &
sleep 1

# GET will display content1
java Client 127.0.0.1 >> outputClient.txt &
sleep 3

pkill -9 java


# compare o/p with expected o/p
cmp --silent outputClient.txt expectedClientOutput.txt && o1= true || o1=false

if $o1 ; then
    echo 'Test Succesful'
else
	echo 'Test failed'
fi
