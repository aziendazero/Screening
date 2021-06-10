package view.invito;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import oracle.jbo.Row;

public class DistributoreInviti 
{

  
  /**
   * Crea, per ogni tipo di invito, un oggetto che memorizza i dati per la distribuzione
   * @return PercenatgeObject con il tipo di invito ed il numero di inviti da effettuare
   * sul blocco
   * @param blocksize dimensione del blocco
   * @param hash tabella con l'array dei soggetti da invitare per ogni tipo di invito
   */
  private static Vector getDistribuzioneInviti(Hashtable hash,int blocksize)
  {
    Vector v=new Vector();
    //1.memorizzo il numero di ogni tipo di inviti e sommo
    Enumeration en=hash.keys();
    int tot=0;
    while(en.hasMoreElements())
    {
      String tipo_invito=(String)en.nextElement();
      //ora gli passo direttamente il nuemro...
     // int n=((Integer)hash.get(tipo_invito)).intValue();
      
      //...poi dovro' contare gli elementi
      int n=((Row[])hash.get(tipo_invito)).length;
      tot+=n;
      PercentageObject p=new PercentageObject(tipo_invito);
      p.setHowmany(n);
      v.addElement(p);
    }
    
 

    //2. calcolo la percentuale ed il numero di inviti ogni 10 per ogni tipo di invito
    for(int i=0;i<v.size();i++)
    {
      PercentageObject p=(PercentageObject)v.elementAt(i);
      double percentage=(double)p.getHowmany()*100/tot;
      p.setPercentage(percentage);
      //sarebbe percentage/100 e poi *10
     // p.setOverblocksize((double)percentage/10);
      p.setOverblocksize((double)percentage/100*blocksize);
    //  System.out.println(p.toString());
    }
    return v;
  }
  
  /**
   * Metodo di supporto per generare finti array di inviti in base ad una has di numeri
   * @return 
   * @param hash
   */
  public static Hashtable getInviti(Hashtable hash)
  {
     Hashtable h2=new Hashtable();
     Enumeration en=hash.keys();
     // int tot=0;
      while(en.hasMoreElements())
    {
      String tipo_invito=(String)en.nextElement();
      //ora gli passo direttamente il nuemro...
      int n=((Integer)hash.get(tipo_invito)).intValue();
      String[] inviti=new String[n];
      for(int i=0;i<n;i++)
        inviti[i]=tipo_invito+i;
      
      h2.put(tipo_invito,inviti);
    }
  
     return h2; 
  }
  
  /**
   * VERSIONE di TEST
   * Distribuisce gli inviti a blocchi della dimensione fissata e restituisce 
   * il vettore dei soggetti da invitare nell'ordine di distribuzione
   * @param hash per ogni tipo di invito ho il numero di soggetti da invitare
   * @param blocksize dimensione del blocco da usare per la distribuzione
   */
/*  public static Vector distribuisciInviti0(Hashtable hash,int blocksize)
  {
    Vector dist=getDistribuzioneInviti(hash,blocksize);
    Hashtable inviti=getInviti(hash);
    Vector lista_inviti=new Vector();
    //se floor e' true uso i troncamenti, altrimenti gli arrotondamenti
    boolean floor=true;
    //manca un ciclo piu' esterno
  while(remaining(dist)){
    //per ogni tipo di invito
    for(int i=0;i<dist.size();i++)
    {
      PercentageObject p=(PercentageObject)dist.elementAt(i);
      String[] inv=(String[])inviti.get(p.getName());
      //quanti inviti devo generare? dipende da floor
      int n;
      if(floor)
        n=(int)Math.floor(p.getOverblocksize());
      else
        n=(int)Math.ceil(p.getOverblocksize());
      for(int j=0;j<n;j++)
      { //se ci sono ancora inviti
        if(p.getIndex()<inv.length){
          //aggiungo l'invito ed incremento il relativo contatore
          //System.out.print(inv[p.getIndex()]+",");
          lista_inviti.addElement(inv[p.getIndex()]);
          
        }
        //incremento comunque l'indice
       p.incIndex();
      }
    }
   // System.out.println();
   //cambio tra arrotondamenti e trocamenti
   floor=!floor; 
  }
 
    return lista_inviti;
  }*/
  
  /**
   * Distribuisce gli inviti a blocchi della dimensione fissata e restituisce 
   * il vettore dei soggetti da invitare nell'ordine di distribuzione
   * @param hash per ogni tipo di invito ho il relativo array di sogetti da invitare
   * @param blocksize dimensione del blocco da usare per la distribuzione
   */
  public static Vector distribuisciInviti(Hashtable hash,int blocksize)
  {
    Vector dist=getDistribuzioneInviti(hash,blocksize);
    Hashtable inviti=hash;
    Vector lista_inviti=new Vector();
    //se floor e' true uso i troncamenti, altrimenti gli arrotondamenti
    boolean floor=true;
    //manca un ciclo piu' esterno
  while(remaining(dist)){
    //per ogni tipo di invito
    for(int i=0;i<dist.size();i++)
    {
      PercentageObject p=(PercentageObject)dist.elementAt(i);
      Row[] inv=(Row[])inviti.get(p.getName());
      //quanti inviti devo generare? dipende da floor
      int n;
      if(floor)
        n=(int)Math.floor(p.getOverblocksize());
      else
        n=(int)Math.ceil(p.getOverblocksize());
      for(int j=0;j<n;j++)
      { //se ci sono ancora inviti
        if(p.getIndex()<inv.length){
          //aggiungo l'invito ed incremento il relativo contatore
          //System.out.print(inv[p.getIndex()]+",");
          lista_inviti.addElement(inv[p.getIndex()]);
          
        }
        //incremento comunque l'indice
       p.incIndex();
      }
    }
   // System.out.println();
   //cambio tra arrotondamenti e trocamenti
   floor=!floor; 
  }
    return lista_inviti;
  }
  
  private static boolean remaining(Vector v)
  {
    for(int i=0;i<v.size();i++)
    {
      PercentageObject p=(PercentageObject)v.elementAt(i);
      //se alemno un tipo di appuntamenti ne ha ancora vando avanti
      if(p.getIndex()<p.getHowmany())
        return true;
    }
    //altrimentio mi devo fermare
    return false;
  }
  
/*  public static void main(String[] args)
  {
    DistributoreInviti di=new DistributoreInviti();
    Hashtable h=new Hashtable();
    h.put("Primo invito",new Integer(20));
    h.put("Sollecito",new Integer(5));
    h.put("Ripetizione per inadeguato",new Integer(2));
    h.put("Follow-up",new Integer(3));

    di.distribuisciInviti0(h,5);
    
  }*/
  
  
}

 class PercentageObject
{
  private String name;
  private int howmany;
  private double percentage;
  private double overblocksize;
  private int index=0;
  
  public PercentageObject(String _name)
  {
    name=_name;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setHowmany(int i)
  {
    howmany=i;
  }
  public int getHowmany()
  {
    return howmany;
  }
  
  public void setPercentage(double i)
  {
    percentage=i;
  }
  public double getPercentage()
  {
    return percentage;
  }
  
  public void setOverblocksize(double i)
  {
    overblocksize=i;
  }
  public double getOverblocksize()
  {
    return overblocksize;
  }
  
  public String toString()
  {
    return name+" (numero="+howmany+"; percentuale="+percentage+"; numero inviti in un blocco="+overblocksize+")";
  }
  
  public void incIndex()
  {
    index++;
  }
  
  public int getIndex()
  {
    return index;
  }
  
}