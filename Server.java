import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.List;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.util.concurrent.atomic.AtomicInteger;
public class Server {

    public static void main(String[] args) throws Exception {

        // list of sockets
        List<Socket> sockets =  new ArrayList<Socket>();

        // to keep track of sockets liveness
        List<Integer> socketsFlag =  new ArrayList<Integer>();

        // to make lamportsClock sharable among threads
        AtomicInteger lamportsClock = new AtomicInteger();  

        try (var listener = new ServerSocket(59898)) {

            System.out.println("The server is running...");
            // thread pool of max 200 threads
            var pool = Executors.newFixedThreadPool(200);

            pool.execute(new Heartbeat(sockets, socketsFlag));

            // updating files to makes all clients "closed" initally
            File originalFile = new File("db.txt");
            BufferedReader br = new BufferedReader(new FileReader(originalFile));
            File tempFile = new File("tempDb.txt");
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile)); 
            String line = null;

            while ((line = br.readLine()) != null) {
                String[] words = line.split("~");
                line = words[0]+"~false~"+words[2]+"~"+words[3];              
                pw.println(line);
                pw.flush();
            }
            pw.close();
            br.close();

            // Delete and rename temp file
            if (!originalFile.delete()) {
                System.out.println("Could not delete file");
                return;
            }
            if (!tempFile.renameTo(originalFile))
                System.out.println("Could not rename file");
            

            while (true) {

                //adding socket and flag to Lists
                sockets.add(listener.accept());
                socketsFlag.add(1);

                // incrementing lamports clock
                System.out.println("lamportsClock : "+lamportsClock.incrementAndGet()); 

                // executing thread
                pool.execute(new ClientThread(sockets.get(sockets.size()-1), lamportsClock));

            }
        }
    }

    private static class ClientThread implements Runnable {

        private Socket socket;
        // private int flag;
        private boolean existsFlag = true, statusFlag = true;
        private File originalFileUpdator = new File("db.txt");
        AtomicInteger lamportsClock;
        Parser parser = new Parser();

       

        ClientThread(Socket socket, AtomicInteger lamportsClock) {
            this.socket = socket;
            this.lamportsClock=lamportsClock;
            
        }

        @Override
        public void run() {

            System.out.println("Connected: " + socket);

            try {

                var in = new Scanner(socket.getInputStream());
                var out = new PrintWriter(socket.getOutputStream(), true);

               
                String temp = "";
                String requestIn =in.nextLine();
                String request = requestIn.split("~")[0];
                String requestLamportsClock = requestIn.split("~")[1];

                System.out.println("Request : "+request);
                // updating lamports clock when a particular request is recieved
                lamportsClock.set(Math.max(lamportsClock.intValue(), Integer.parseInt(requestLamportsClock))+1);
                System.out.println("lamportsClock : "+lamportsClock);


                if(request.equals("PUT")){
                    
                     //initally status will be "201" and every other requests will be "200"
                    if(statusFlag){
                        out.println("Status : 201");
                        statusFlag = false;
                    }
                    else if(!statusFlag){
                        out.println("Status : 200");

                    }
        
                    //updating db file
                    BufferedReader br = new BufferedReader(new FileReader(originalFileUpdator));
                    File tempFile = new File("tempDb.txt");
                    PrintWriter pw = new PrintWriter(new FileWriter(tempFile)); 
                    String line = null;

                    System.out.println(socket.getPort()+request);
                    while (in.hasNextLine()) {

                        temp= in.nextLine();

                        // updating lamports clock when content is recieved from content server
                        lamportsClock.set(Math.max(lamportsClock.intValue(), Integer.parseInt(temp.split("~")[2]))+1);
                        System.out.println("lamportsClock : "+lamportsClock);


                        if(temp.equals("feedError")){

                            out.println("Status : 204");
                        }
                        else{

                            out.println(temp.split("~")[1]);

                        }
                        //if PUT is from same content server then replace else add as a new entry to db.txt
                        while ((line = br.readLine()) != null) {

                            if (line.contains(temp.split("~")[0])) {
                               line = socket.getPort()+"~"+!socket.isClosed()+"~"+temp.split("~")[1]+"~"+temp.split("~")[0];
                               existsFlag=false;
                            }
                            pw.println(line);
                            pw.flush();
                        }
                        if(existsFlag){
                            pw.println(socket.getPort()+"~"+!socket.isClosed()+"~"+temp.split("~")[1]+"~"+temp.split("~")[0]);
                            pw.flush();
                        }
                        pw.close();
                        br.close();

                        // updating lamports clock when file is written
                        System.out.println("lamportsClock : "+lamportsClock.incrementAndGet());

                        // Delete and rename temp file
                        if (!originalFileUpdator.delete()) {
                            System.out.println("Could not delete file");
                            return;
                        }
                        if (!tempFile.renameTo(originalFileUpdator))
                            System.out.println("Could not rename file");

                    } 

                }

                else if(request.equals("GET")){

                    // update lamports clock when sending data to GET Client
                    System.out.println("lamportsClock : "+lamportsClock.incrementAndGet());       
                    System.out.println(lamportsClock);

                    BufferedReader brUpdator = new BufferedReader(new FileReader(originalFileUpdator));
                    String line = null;

                    while ((line = brUpdator.readLine()) != null) {
                        String[] words = line.split("~");

                        //sending only content from content servers that are alive
                        if(words[1].equals("true")){
                            
                                out.println(words[2]+"~"+lamportsClock.intValue());
                            
                        }
                    }

                    brUpdator.close();

                }

                else{

                    out.println("Status : 400");
                }


            } catch (Exception e) {

                System.out.println("Error:" + socket);

            } finally {

                try {
                    socket.close();

                } catch (IOException e) {

                }
                System.out.println("Closed: " + socket);
            }
        }
    }

    private static class Heartbeat implements Runnable {
        private List<Socket> sockets;
        private List<Integer> socketsFlag;

        Heartbeat(List<Socket> sockets, List<Integer> socketsFlag) {
            this.sockets = sockets;
            this.socketsFlag = socketsFlag;
            
        }

        @Override
        public void run() {
        try{  
            
            while(true){

                // iterate through sockets List to see if closed
                for(int i=0;i<sockets.size();i++){

                    if(sockets.get(i).isClosed() && socketsFlag.get(i)==1){

                        // changing flag value to 0 for the corresponding socket to avoid checking in next iteration
                        socketsFlag.set(i, 0);

                        File originalFile = new File("db.txt");
                        BufferedReader br = new BufferedReader(new FileReader(originalFile));
                        File tempFile = new File("tempDb.txt");
                        PrintWriter pw = new PrintWriter(new FileWriter(tempFile)); 
                        String line = null;

                        // update status for the corresponding content server which is closed
                        while ((line = br.readLine()) != null) {

                            if (line.contains(String.valueOf(sockets.get(i).getPort()))) {
                               String[] words = line.split("~");
                               line = words[0]+"~false~"+words[2]+"~"+words[3];

                            }
                            pw.println(line);
                            pw.flush();
                        }
                        pw.close();
                        br.close();

                        // Delete and rename file
                        if (!originalFile.delete()) {
                            System.out.println("Could not delete file");
                            return;
                        }
                        if (!tempFile.renameTo(originalFile))
                            System.out.println("Could not rename file");
                    }

                }

                // to check every 12 seconds only
                Thread.sleep(3000);

            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
         
        }
    }

}