import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import java.io.*;
import java.nio.file.Files;

public class ContentServer {

    public static void main(String[] args) throws Exception {

        Parser parser = new Parser();
        int lamportsClock =0;

        if (args.length != 2) {

            System.err.println("Pass the IP:PORT and content file as command line arguments");
            return;

        }
        // make client retry on server connection lost
        for(int i=0;i<3;i++) {

            try {
                var socket = new Socket(args[0], 59898);
                System.out.println("Reading lines. Ctrl+D or Ctrl+C to quit");

                var in = new Scanner(socket.getInputStream());
                var out = new PrintWriter(socket.getOutputStream(), true);

                // sending PUT request with lamports clock
                out.println("PUT"+"~"+lamportsClock);

                // update lamports clock after sending PUT request
                lamportsClock++;
                System.out.println("lamportsClock : "+lamportsClock);
                
                // sending content by appending lamports clock
                out.println(parser.parse(args[1])+"~"+lamportsClock);

                // update clcok after sending content
                lamportsClock++;
                System.out.println("lamportsClock : "+lamportsClock);
                
                // recieving response
                while (in.hasNextLine()) {

                    System.out.println(in.nextLine());
                }

            }
            catch (Exception e) {

                System.out.println(e);

            }

            // retry only after 10 seconds
            Thread.sleep(10000);
        }

        System.out.println("The server is not responding!!");
    }
}