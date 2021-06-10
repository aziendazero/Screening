package view;

import java.io.IOException;  
   
 import javax.servlet.Filter;  
 import javax.servlet.FilterChain;  
 import javax.servlet.FilterConfig;  
 import javax.servlet.ServletException;  
 import javax.servlet.ServletRequest;  
 import javax.servlet.ServletResponse;  
 import javax.servlet.http.HttpServletRequest;  
 import javax.servlet.http.HttpServletRequestWrapper;  
   
   
 public class IECompatibleFilter implements Filter {  
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {  
   
     String userAgentStr = ((HttpServletRequest)request).getHeader("user-agent");  
     // Check to see if we have to do with a IE request. If so, we return a wrapped request.  
     if (userAgentStr != null && (userAgentStr.contains("MSIE 1") || 
                                     userAgentStr.contains("Trident"))) {  
       ServletRequest wrappedRequest = new WrapperRequest((HttpServletRequest)request);  
       chain.doFilter(wrappedRequest, response);  
     } else {  
       chain.doFilter(request, response);  
     }  
   }  
   
   public void destroy() {  
   }  
   
   public void init(FilterConfig arg0) throws ServletException {  
   }  
   
   private class WrapperRequest extends HttpServletRequestWrapper {  
     public WrapperRequest(HttpServletRequest request) {  
       super(request);  
     }  
   
     public String getHeader(String name) {  
       // IE 10: replace 'MSIE 10.x' into 'MSIE 9.x' for ADF 11.1.1.6 and below  
       HttpServletRequest request = (HttpServletRequest)getRequest();  
       if ("user-agent".equalsIgnoreCase(name) && request.getHeader("user-agent").contains("MSIE 10")) {  
         return request.getHeader("user-agent").replaceAll("MSIE [^;]*;", "MSIE 9.0;");  
       }  
       // IE 11: replace the whole agent-string into an MSIE 9.0 string for ADF 11.1.1.6 and below  
       // or MSIE 10.0 for ADF 11.1.1.7 or higher  
       if ("user-agent".equalsIgnoreCase(name) && request.getHeader("user-agent").contains("Trident")) {  
         //Choose your preferred version  
         String newAgentStr = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.2; Trident/6.0)";  
         return newAgentStr;  
       }  
       return request.getHeader(name);  
     }  
   }  
 }  
