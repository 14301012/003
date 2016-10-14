import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.Map;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;



public class HttpServer {

	 //private static Map servletCache=new HashMap<>();//���servletʵ���Ļ���	
    // �رշ�������
    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        //�ȴ���������
        server.await();
    }

    public void await() {
        ServerSocket serverSocket = null;
        int port = 8080;
        try {
            //�������׽��ֶ���
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // ѭ���ȴ�����
        while (true) {
            Socket socket = null;
            InputStream input = null;
            OutputStream output = null;
            try {
                //�ȴ����ӣ����ӳɹ��󣬷���һ��Socket����
                socket = serverSocket.accept();
                input = socket.getInputStream();
                output = socket.getOutputStream();

                // ����Request���󲢽���
                Request request = new Request(input);
                request.parse();
                // ����Ƿ��ǹرշ�������
                if (request.getUri().equals(SHUTDOWN_COMMAND)) {
                    break;
                }
                
                // ���� Response ����
                Response response = new Response(output);
                response.setRequest(request);
                
               // xmlParse xml=new xmlParse();
               // String u=request.getUri();
                if (request.getUri().startsWith("/servlet/")) {
                    //����uri��/servlet/��ͷ����ʾservlet����
                    ServletProcessor1 processor = new ServletProcessor1();
                    processor.process(request, response);
                   
                    

                    
                } else {
                	
                	
                	
                
                    //��̬��Դ����
                  
                	
                  xmlParse xml=new xmlParse();
                  
                  String ok=xml.getClassByURL(request.getUri());
                  if(ok==null){
                	  StaticResourceProcessor process1=new StaticResourceProcessor();
                	  process1.process(request, response);
                  }
                  else{
                  String p=ok.substring(8);
                  
                    //������������ڴ�ָ��JAR�ļ���Ŀ¼������
                    URLClassLoader loader = null;
                    try {
                        URLStreamHandler streamHandler = null;
                        //�����������
                        loader = new URLClassLoader(new URL[]{new URL(null, "file:" + Constants.WEB_SERVLET_ROOT, streamHandler)});
                    } catch (IOException e) {
                        System.out.println(e.toString());
                    }
                    
                    Class<?> myClass = null;
                    try {
                        //���ض�Ӧ��servlet��
                        myClass = loader.loadClass(p);
                    } catch (ClassNotFoundException e) {
                        System.out.println(e.toString());
                    }

                    Servlet servlet = null;

                    try {
                        //����servletʵ��
                        servlet = (Servlet) myClass.newInstance();
                        //ִ��ervlet��service����
                        servlet.service((ServletRequest) request,(ServletResponse) response);
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    } catch (Throwable e) {
                        System.out.println(e.toString());
                    }
                  }
                    
                   }  
                

                // �ر� socket
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}