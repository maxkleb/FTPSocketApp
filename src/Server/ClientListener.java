/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MaxKleb
 */
class ClientListener extends Thread {
    private Socket cl;
    private String fileName;
    
    public ClientListener(Socket client) {
        super();
        cl = client;
    }

  public void run(){
       BufferedReader in;
       PrintWriter out;
        try {
            in = new BufferedReader(new InputStreamReader(cl.getInputStream()));
            String msg,fileName = "";
            while ((msg = in.readLine()) != null){
                    
                 if(msg.contains("<dist>")){
                   fileName = msg.substring(6);
                   System.out.println(fileName);
                    // in.close();
                     break;
                }
            }
            
            File myFile = new File (fileName);
            byte [] mybytearray = new byte [(int)myFile.length()];
            System.out.println("File size is "+(int)myFile.length());
            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);

            bis.read(mybytearray, 0, mybytearray.length);

            OutputStream os = cl.getOutputStream();
            System.out.println("Sending file");

            os.write(mybytearray, 0, mybytearray.length);
            os.flush();
            cl.close();
              
             
              } catch (IOException ex) {
            Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    
}
