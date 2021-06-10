package model.commons;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class ConfigurationReader 
{
 // private File f;
 // private String localUrl;
  
  protected static String LOCAL_ADDRESS="localAddress";
  
  public ConfigurationReader()
  {
 
  }
  
  public static String readProperty(String propName)
  {
    try {
        InputStream input=ConfigurationReader.class.getResourceAsStream("cnf.properties");
        if (input==null)
            return null;
        
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        String str;
        while ((str = in.readLine()) != null) {
            if(str.startsWith("#"))
              continue;
            
            if(str.trim().startsWith(propName))
            {
              str=str.substring(str.indexOf("=")+1);
              in.close();
              return str;
            }
        }
        in.close();
        return null;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

  }

  /**
   * 
   * @param args
   */
 /* public static void main(String[] args)
  {
    ConfigurationReader x = new ConfigurationReader();
    System.out.println(x.readProperty("localAddress"));
  }*/
}
