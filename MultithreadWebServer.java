import java.io.BufferedReader;  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileNotFoundException;  
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.io.PrintStream;  
import java.net.ServerSocket;  
import java.net.Socket;  
import java.net.URLDecoder;  
import java.util.StringTokenizer;  
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;  
  
public class server{

    ServerSocket serverSocket;// server Socket

    public static int PORT = 8004;//setting port numbers

    public sever() {
        
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (Exception e) {
            System.out.println("couldn't start HTTP server:" + e.getMessage());
        }
        
        if (serverSocket == null) System.exit(1);// can not start server

        System.out.println("HTTP server is running, port:" + PORT);
        
        
        for (int i=0;i<=100;i++) {

            myThread task = new myThread(serverSocket);
            Thread thread = new Thread(task);
            thread.start();

            }

        }

    public static void main(String[] args) {
        try {
            if(args.length != 1) {
                usage();
            } else if(args.length == 1) {
                PORT = Integer.parseInt(args[0]);
            }
        } catch (Exception ex) {
            System.err.println("Invalid port arguments. It must be a integer that greater than 0");
        }
        
        new sever();
    }


    private static void usage() {
        System.out.println("Usage: java HTTPServer <port>\nDefault port is 80.");
    }

}


class myThread implements Runnable
{
    private ServerSocket serverSocket;

    myThread(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }




public void run() {
        while(true) {
            try {

                //System.out.println("### Thread ### "+Thread.currentThread().getName());//this line is for test

                Socket client=null;//client Socket
                client=serverSocket.accept();//client connect to server
                if(client!=null) {
                    System.out.println("user :"+client);
                    
                  
                            	
                    try {
                        // open read buffer
                        BufferedReader in=new BufferedReader(new InputStreamReader(
                                client.getInputStream()));
                        
                        System.out.println("requirements from client:\n***************");
                        // read 1st row, requested address
                        String line=in.readLine();
                        System.out.println(line);
                        
                       
                            
                        String resource=line.substring(line.indexOf('/'),line.lastIndexOf('/')-5);
                        //get the address of resource requested
                        resource=URLDecoder.decode(resource, "UTF-8");//decode URL
                        System.out.println("---");
                        System.out.println(resource);
                        System.out.println("---");
                        String method = new StringTokenizer(line).nextElement().toString();// Method of get request GET or POST
												
                        // read head info
                        while( (line = in.readLine()) != null) {
                            System.out.println(line);
                          
                            if(line.equals("")) break;
                        }
                        //System.out.println("flag: " + flag);
                        // display post form
                        if("POST".equalsIgnoreCase(method)) {
                            System.out.println(in.readLine());
                        }
                        
                        System.out.println("request end\n***************");
                        System.out.println("request source:"+resource);
                        System.out.println("request type: " + method);

                        String params = null;  
                        if (resource.indexOf("?") > -1) {  
                        params = resource.substring(resource.indexOf("?") + 1);  
                        resource = resource.substring(0, resource.indexOf("?"));  
                        }  
                        
                        fileReaderAndReturn(resource, client);
                      
                            
                            //  writer output HTML code to socket
                            PrintWriter out=new PrintWriter(client.getOutputStream(),true);
                        

                            out.close();
                            closeSocket(client);
                       // }
                    } catch(Exception e) {
                        System.out.println("HTTP server :"+e.getLocalizedMessage());
                    }
                }
            } catch(Exception e) {
                System.out.println("HTTP server :"+e.getLocalizedMessage());
            }
        }
    }
    
   
    void closeSocket(Socket socket) {
        try {
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
                            System.out.println(socket + "leave the HTTP server");        
    }
    
    
    private static void usage() {
        System.out.println("Usage: java HTTPServer <port>\nDefault port is 80.");
    }

    
    
    void fileReaderAndReturn(String fileName, Socket socket) throws IOException {  
        if ("/".equals(fileName)) {//
            fileName = "/index.html";
                                    }
        fileName = fileName.substring(1);  
         System.out.println("filename:" + fileName); 
          
        PrintStream out = new PrintStream(socket.getOutputStream(), true);  
        File fileToSend = new File(fileName);  
          
          
        String fileEx = fileName.substring(fileName.indexOf(".") + 1);
        String contentType = null;
         System.out.println("fileex:" + fileEx); 
        
        // set return content and type
        // type is same to mime-mapping in tomcat/conf/web.xml
        if ("htmlhtmxml".indexOf(fileEx) > -1) {  
                contentType = "text/html;charset=GBK";
            } else if ("jpegjpggifbpmpng".indexOf(fileEx) > -1) {
                contentType = "application/binary";
            }
           
           
       if ("stkwwy".indexOf(fileEx) > -1) {    
        	out.println("<h1>400error！</h1>" + "invalid request");
            out.close();
        }
          
       if (fileToSend.exists() && !fileToSend.isDirectory()) {  
        // http protocol head
        out.println("HTTP/1.0 200 OK");// return and end
        out.println("Content-Type:" + contentType);  
        out.println("Content-Length:" + fileToSend.length());// return content length
        out.println();// according to HTTP protocol, empty row end info
          
          
        FileInputStream fis = null;  
        try {  
            fis = new FileInputStream(fileToSend);
        } catch (FileNotFoundException e) {  
        out.println("<h1>404error！</h1>" + e.getMessage());  
            }
            byte data[];
            try {
                data = new byte[fis.available()];
          
                
                fis.read(data);
                out.write(data);
            } catch (IOException e) {
                out.println("<h1>500error!</h1>" + e.getMessage());
                e.printStackTrace();
            } finally {
                out.close();
                try {
                    fis.close();
            } catch (IOException e) {
          
          
        e.printStackTrace();  
                }
            }
        } else {  
            out.println("<h1>404error！</h1>" + "file not exits");
            out.close();
        }  
          
          
    }
}