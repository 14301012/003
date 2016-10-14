import java.util.*;

//����dom4j����web.xml
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
/**
 * ����web.xml,������Ϣ���浽map
 * @author 18401606107
 *
 */
public class xmlParse {
  
	private Map<String, String>servlets=new HashMap<String, String>();
	private Map<String, String>serletMapping=new HashMap<String, String>();
	
	public xmlParse(){
		
		SAXReader reader=new SAXReader();
		try {
			Document doc=reader.read("web.xml");

			List<Element> list=doc.selectNodes("/web-app/servlet");
			for (Element servlet:list) {//��ȡservlet����
				Element sname = servlet.element("servlet-name");
                Element sclass = servlet.element("servlet-class");
                servlets.put(sname.getText(), sclass.getText());
			}
			
			List<Element> list1=doc.selectNodes("/web-app/servlet-mapping");
			for (Element servlet:list1) {//��ȡservlet��Ӧ��url
				Element sname = servlet.element("servlet-name");
                Element url = servlet.element("url-pattern");
                serletMapping.put(url.getText(), sname.getText());
			}
			
			
		} catch (DocumentException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	/**
	 * ����url���ض�Ӧ��servlet
	 * @param url
	 * @return servletClass
	 */
	public String getClassByURL(String url){
		String servletClass=null;
		
		String sname=serletMapping.get(url);
		servletClass=servlets.get(sname);
		
		return servletClass;
	}

	/*public static void main(String[] args) {
		xmlParse xml=new xmlParse();
		String str=xml.getClassByURL("/Primitive");

				System.out.println(str);
				
		
	}*/
	
}
