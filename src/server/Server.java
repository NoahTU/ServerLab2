/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

/**
 *
 * @author Noah
 */
import java.io.*;
import java.net.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
 
public class Server{
    
    public static void main(String argv[]) throws Exception{
   
        Mac sha512_HMAC = null;
        String r = null;
        String key =  "Welcome1";
   	System.out.println(" Server is Running  " );
        ServerSocket mysocket = new ServerSocket(5555);
        SecretKeySpec keySpec = null;
        byte [] mac_data= null;
        byte [] byteKey = null;
 
        while(true){
             
            
            
            
            
            Socket connectionSocket = mysocket.accept();
 
            BufferedReader reader =
            		new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            BufferedWriter writer= 
            		new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));
 
            writer.write("*** Welcome to the Calculation Server (Addition Only) ***\r\n");            
            writer.write("*** Please type in key and press Enter : \n");
            //writer.write(null);
            writer.flush();
            String data1 = reader.readLine().trim();
            key=data1;
            //key="test";
            
            try{
                byteKey = key.getBytes("UTF-8");
                final String HMAC_SHA256 = "HmacSHA512";
                sha512_HMAC = Mac.getInstance(HMAC_SHA256);      
                keySpec = new SecretKeySpec(byteKey, HMAC_SHA256);
                sha512_HMAC.init(keySpec);
                mac_data = sha512_HMAC.
                doFinal("My message".getBytes("UTF-8"));
                //result = Base64.encode(mac_data);
                r = bytesToHex(mac_data);
                System.out.println(r);
            } catch (UnsupportedEncodingException e) {
        // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }finally{
                System.out.println("Done");
            }
            data1 = reader.readLine().trim();
            
            if (!data1.equals(r)){
                writer.write("*** Nope : \n");
                writer.flush();
                connectionSocket.close();
            }
 
            else{
                writer.write("*** Key Verified. Enter Message : \n");
                writer.flush();
                System.out.println("waiting");
                String data2 = reader.readLine().trim();
                String check = reader.readLine().trim();

                System.out.println("not waiting");    
                System.out.println(data2);



                sha512_HMAC.init(keySpec);
                mac_data = sha512_HMAC.
                doFinal(data2.getBytes("UTF-8"));
                r = bytesToHex(mac_data);
                
                if (r.equals(check)){
                    System.out.println("check true");
                    System.out.println("Say something? ");
                    Scanner scanner = new Scanner(System.in);
                    String fmsg = scanner.nextLine();
                    //String fmsg="Hi A, this is B!\n";
                    mac_data = sha512_HMAC.
                    doFinal(fmsg.getBytes("UTF-8"));
                    r = bytesToHex(mac_data);
                    
                    writer.write("ECHO: "+data2+"\n");
                    writer.flush();
                    writer.write(fmsg+"\n");
                    writer.flush();
                    writer.write(r);
                    writer.flush();
                    System.out.println("Our end done.");
                    connectionSocket.close();}
                else{
                        System.out.println("check false");
                        writer.write("ERROR /n");
                    writer.write("Keys do not match \n");
                    writer.flush();
                    connectionSocket.close();
            }
            }
         }
      }
   
    
    
   public static String bytesToHex(byte[] bytes) {
    final  char[] hexArray = "0123456789ABCDEF".toCharArray();
    char[] hexChars = new char[bytes.length * 2];
    for ( int j = 0; j < bytes.length; j++ ) {
        int v = bytes[j] & 0xFF;
        hexChars[j * 2] = hexArray[v >>> 4];
        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
}
   
}
