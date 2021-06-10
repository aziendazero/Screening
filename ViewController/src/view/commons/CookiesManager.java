package view.commons;
import java.io.File;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookiesManager 
{
  public CookiesManager()
  {
  }
  
  /**
   * Crea ed aggiunge il cookie desiderato. Se tale cookie esiste gia' ne 
   * restituisce il vecchio valore
   * @return null se ho creato un nuovo cookie, una stringa con il vecchio 
   * valore se il cookie esisteva gia'
   * @param req request della servlet
   * @param resp response della servlet
   * @param value nuovo valore del cookie
   * @param name nome del cookie
   */
  public static String createOrUpdate(String name, String value, HttpServletResponse resp, HttpServletRequest req)
  {
    Cookie c=getCookie(name,req);
    String old=null;
    if(c!=null)
      old=c.getValue();
      
      //creo il nuovo cookie e lo invio alla response
      Cookie logoC=new Cookie(name,value);
      //durata 100 giorni
      logoC.setMaxAge(60*60*24*100);
      resp.addCookie(logoC);
      return old;
  }
  
  /**
   * Aggiorna il valore di un cokie che contiene il percorso di un file. Se tale 
   * cookie esisteva gia', cancella il vecchio file
   * @param req request della servlet
   * @param resp response della servlet
   * @param value nuovo valore del cookie
   * @param name nome del cookie
   */
  public static void updateFileCookie(String name, String value, HttpServletResponse resp, HttpServletRequest req)
  {
    //aggiorno il cookie
    String old=createOrUpdate(name,value,resp,req);
      //se il cookie esisteva gia' e il file e' diverso
      if(old!=null && !old.equals(value)){
           //cancello il vecchio file
           File oldFile=new File(old);
           if(oldFile!=null)
             oldFile.delete();
      }
  }
  
  /**
   * Restituisce il cookie cercato o null, se non esiste
   * @return 
   * @param req
   * @param name
   */
  public static Cookie getCookie(String name,HttpServletRequest req)
  {
    Cookie[] cookies=req.getCookies();

      if(cookies!=null){
        for(int i=0;i<cookies.length;i++)
        {
          if(name.equals(cookies[i].getName()))
          {//il cookie esiste gia':
           return cookies[i];
          }
        }
      }
    return null;
  }
}