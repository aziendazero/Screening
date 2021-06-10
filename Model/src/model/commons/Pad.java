package model.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Pad 

{

  public Pad()

  {

  }

 

/*  public static void main(String[] args)

  {

    Pad pad = new Pad();

    

    pad.doPad("c:/temp/riceviPapTest.txt", 1505);

  }*/

  public static void doPad(String file, int padsize)

  {

    InputStream is = null;

    FileOutputStream os = null;

    

    boolean bOK = true;

    

    File fTemp = null;

    

    try

    {

    

      is = new FileInputStream(file);

      

      //creo un file temporaneo in cui caricare il file

      fTemp = File.createTempFile("screening", null, null);

 

      os = new FileOutputStream(fTemp);

            

      byte[] bo = new byte[padsize];

      

      for (int k = 0; k < padsize ; k++ ) 

        bo[k] = 0x20;

      

      int count = 0;

 

      byte[] bi = new byte[(int)1];

      

      while(is.read(bi) != -1)

      {

        if(bi[0] == (byte)0x0d)

        {

          continue;          

        }

        

        if((bi[0] == (byte)0x0a) || (count >= padsize))

        {

          os.write(bo);

                        

          for (int k = 0; k < padsize ; k++ ) 

            bo[k] = 0x20;                         

          

          count = 0;

            

          continue;

        }

                  

        bo[count++] = bi[0];

      }

      

      is.close();

      is = null;      

      os.close();

      os = null;

    }

    catch (FileNotFoundException ex)

    {

      ex.printStackTrace();

      

      bOK = false;

    }

    catch (IOException ex)

    {

      ex.printStackTrace();

      

      bOK = false;

    }

    finally

    {

      try

      {

        if (is != null)

          is.close();

        if (os != null)

          os.close();

          

        if(bOK) // se tutto OK cancella il file di partenza e rimpiazzalo con quello "paddato" 

        {

          boolean success = (new File(file)).delete();

          if (!success)

          {

            System.out.println("Impossibile cancellare il file " + file);

          }

          else

          {

    

            File fNew = new File(file);

    

            boolean bRen = fTemp.renameTo(fNew);

            if (!bRen)

            {

              System.out.println("Impossibile rinominare il file " + fTemp.getAbsolutePath() + " in " + file);

            }            

          }

        }

      }

      catch (IOException ex)

      {

        ex.printStackTrace();

      }

    }

  }  



/*  public static void doPad(String file, int padsize,boolean newline) throws Exception
  {

    BufferedReader input = null;
    BufferedWriter output = null;

    boolean bOK = true;

    File fTemp = null;
    File fNew =null;
    
    try
    {
      input = new BufferedReader( new FileReader(file));
      String line = null;

      fNew = new File(file);
      //creo un file temporaneo in cui caricare il file
      
      fTemp = File.createTempFile("screening", null, fNew.getParentFile());

    //  System.out.println(fTemp.getAbsolutePath());

      output = new BufferedWriter( new FileWriter(fTemp) );

        

      while((line = input.readLine()) != null)
      {
        StringBuffer b = new StringBuffer(padsize);      
        b.append(line);

      for (int k=0;k < padsize - line.length(); k++ ) 
          b.append(" ");

        output.write(b.toString());
        if(newline)
          output.newLine();
      }

    }
    catch (Exception ex)
    {
      ex.printStackTrace();

      bOK = false;
      throw ex;
    }
    finally

    {
        if (input!= null)
          input.close();

        if (output != null)
          output.close();

        if(bOK) // se tutto OK cancella il file di partenza e rimpiazzalo con quello "paddato" 
        {
          boolean success = (new File(file)).delete();
          if (!success)
            throw new Exception("Impossibile cancellare il file " + file);
          else
          {
            
            

            boolean bRen = fTemp.renameTo(fNew);
            if (!bRen)
              throw new Exception("Impossibile rinominare il file " + fTemp.getAbsolutePath() + " in " + file);

          }
        } //end if(bOK)

      }//finally

    }

  */

}


 

