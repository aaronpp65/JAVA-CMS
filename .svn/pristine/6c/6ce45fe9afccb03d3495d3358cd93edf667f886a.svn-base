import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;


public class Client {

    public static void main(String[] args) throws Exception {

        Parser parser = new Parser();
        int lamportsClock =0;
        String[] data = new String[2];

        if (args.length != 1) {

            System.err.println("Pass IP:PORT as thecommand line argument");
            return;
        }
        
            try {
                var socket = new Socket(args[0], 59898);
                System.out.println("Reading lines. Ctrl+D or Ctrl+C to quit");

                var in = new Scanner(socket.getInputStream());
                var out = new PrintWriter(socket.getOutputStream(), true);
                
                // sending GET request with lamports clock
                out.println("GET"+"~"+lamportsClock);

                // update lamports clock after GET request sent
                lamportsClock++;
                System.out.println("lamportsClock : "+lamportsClock);

                while (in.hasNextLine()) {

                    data = in.nextLine().split("~");
                    //parse the xml recived
                    parser.parseXml(data[0]);

                }

                // update lamports clcok after getting requested data
                lamportsClock = Math.max(lamportsClock,Integer.parseInt(data[1]))+1;
                System.out.println("lamportsClock : "+lamportsClock);

            }
            catch (Exception e) {
                System.out.println(e);
            }

           
        }

    }


