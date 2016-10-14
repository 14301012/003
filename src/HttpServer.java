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

	 //private static Map servletCache=new HashMap<>();//存放servlet实例的缓存	
    // 关闭服务命令
    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        //等待连接请求
        server.await();
    }

    public void await() {
        ServerSocket serverSocket = null;
        int port = 8080;
        try {
            //服务器套接字对象
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // 循环等待请求
        while (true) {
            Socket socket = null;
            InputStream input = null;
            OutputStream output = null;
            try {
                //等待连接，连接成功后，返回一个Socket对象
                socket = serverSocket.accept();
                input = socket.getInputStream();
                output = socket.getOutputStream();

                // 创建Request对象并解析
                Request request = new Request(input);
                request.parse();
                // 检查是否是关闭服务命令
                if (request.getUri().equals(SHUTDOWN_COMMAND)) {
                    break;
                }
                
                // 创建 Response 对象
                Response response = new Response(output);
                response.setRequest(request);
                
               // xmlParse xml=new xmlParse();
               // String u=request.getUri();
                if (request.getUri().startsWith("/servlet/")) {
                    //请求uri以/servlet/开头，表示servlet请求
                    ServletProcessor1 processor = new ServletProcessor1();
                    processor.process(request, response);
                   
                    

                    
                } else {
                	
                	
                	
                
                    //静态资源请求
                  
                	
                  xmlParse xml=new xmlParse();
                  
                  String ok=xml.getClassByURL(request.getUri());
                  if(ok==null){
                	  StaticResourceProcessor process1=new StaticResourceProcessor();
                	  process1.process(request, response);
                  }
                  else{
                  String p=ok.substring(8);
                  
                    //类加载器，用于从指定JAR文件或目录加载类
                    URLClassLoader loader = null;
                    try {
                        URLStreamHandler streamHandler = null;
                        //创建类加载器
                        loader = new URLClassLoader(new URL[]{new URL(null, "file:" + Constants.WEB_SERVLET_ROOT, streamHandler)});
                    } catch (IOException e) {
                        System.out.println(e.toString());
                    }
                    
                    Class<?> myClass = null;
                    try {
                        //加载对应的servlet类
                        myClass = loader.loadClass(p);
                    } catch (ClassNotFoundException e) {
                        System.out.println(e.toString());
                    }

                    Servlet servlet = null;

                    try {
                        //生产servlet实例
                        servlet = (Servlet) myClass.newInstance();
                        //执行ervlet的service方法
                        servlet.service((ServletRequest) request,(ServletResponse) response);
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    } catch (Throwable e) {
                        System.out.println(e.toString());
                    }
                  }
                    
                   }  
                

                // 关闭 socket
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}