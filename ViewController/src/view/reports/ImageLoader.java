package view.reports;

import java.io.File;
import java.net.URL;

public class ImageLoader 
{
  public ImageLoader()
  {
  }

public static File getImage(String fileName)
{
  URL url = ImageLoader.class.getResource(fileName);
  String path = url.getFile();
  File f = new File(path);
  return f;
}
  
}