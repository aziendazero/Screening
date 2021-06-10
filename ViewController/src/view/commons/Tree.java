package view.commons;

import java.util.HashMap;

public class Tree 
{
  private HashMap root=new HashMap();
  
  
  
  public Tree()
  {
  }
  
  /**
   * Restituisce il figlo del ramo indicato, se c'e',
   * o null se non c'e' (se il ramo indicato comprende anche una foglia viene 
   * restituita una hashmap vuota)
   * @return 
   * @param nodes elenco dei nodi dalla radice al padre del nodo desiderato
   */
  public Object getNode(Object[] nodes)
  {
    Object out=root;
    for(int i=0;i<nodes.length;i++)
    {
      try{
        out=((HashMap)out).get(nodes[i]);
      }
      catch(ClassCastException cast)
      { //ho raggiunto la foglia
        out=new HashMap();
      }
      if(out==null) return null;
    }
    return out;
  }
  
  
  public boolean insertBranch(Object[] nodes) 
  {
     HashMap parent=root;
    // boolean out=true;
     for(int i=0;i<nodes.length-1;i++)
     {
        //sto inserendo un nodo intermedio
       if(i<nodes.length-2)
          parent=(HashMap)this.insertNode((HashMap)parent,nodes[i]);
      else //sto inserendo una foglia
          this.insertNode(parent, nodes[i],nodes[i+1]);
      if(parent==null){
       
        return false;
      }
     }
     return true;
  }
  
  public boolean deleteBranch(Object[] nodes)
  {
    if(nodes==null || nodes.length==0)
       return true;
    
    
      for(int i=nodes.length-2;i>=0;i--)
      {
        Object[] o=new Object[i+1];
        System.arraycopy(nodes,0,o,0,i+1);
        Object parent=this.getNode(o);
        if(!this.deleteNode(parent,nodes[i+1]))
              return false;
           
        try
        {
         //se il padre ha anch altri figli, non concello da li in su
          if(!((HashMap)parent).isEmpty())
            break;
          }   
        catch(ClassCastException cast)
        {
          
        }
        catch(NullPointerException npex)
        {
          
        }
      }
    return true;
    
  }
  
  private boolean deleteNode(Object parent,Object key)
  {
    if(parent==null || key==null)
      return true;
    
    //se e' un nodo intremedio
    try
    {
      
      ((HashMap)parent).put(key,null);
      ((HashMap)parent).remove(key);
      return true;
    }
     catch(ClassCastException cast)
    {//se e' una foglia non si puo' cancellare
     
      return true;
    }
    
  }
  
  
  /**
   *  
   * @throws java.lang.Exception
   * @return 
   * @param value
   * @param key
   * @param parent
   */
  private Object insertNode(HashMap parent,Object key,Object value) 
  {
    if(parent==null || key==null)
      return null;
    if(value==null)
    //sto inserendo un nodo intermedio e non una foglia
      value=new HashMap();
 
      
    //se la chiave non c'e', bisogna crearla  
    if(parent.get(key)==null)
    {
      parent.put(key,value);
    }
    
    return parent.get(key);
  }
  
   private Object insertNode(HashMap parent,Object key) 
  {
    return this.insertNode(parent,key,null);
  }
  
  
  public void printTree()
  {
    this.printSubTree(this.root,"+---");
  }
  
  public void printSubTree(Object parent,String prefix)
  {
    
    try{
        if(parent==null){
          System.out.println("Node not found");
          return;
        }
        if(((HashMap)parent).isEmpty()){
          System.out.println("Leaf node");
          return;
        }
       
       Object[] iter= ((HashMap)parent).keySet().toArray();
       for(int i=0;i<iter.length;i++)
       {
         System.out.print(prefix);
         
         System.out.println(iter[i].toString());
        
         this.printSubTree(((HashMap)parent).get(iter[i]),"   "+prefix);
       }
       
       
    
    }
    catch(ClassCastException cast)
    {
      //l'oggetto parent e' una foglia
      System.out.print("   "+prefix);
      System.out.println(parent.toString());
    }
    
    
      
  }
  

  public int getLeaf_count()
  {
    return getSubTreeLeafCount(this.root,0);
  }
  
   public int getSubTreeLeafCount(Object parent,int leaf_count)
  {
    
    try{
        if(parent==null || ((HashMap)parent).isEmpty()){
          return leaf_count;
        }

       Object[] iter= ((HashMap)parent).keySet().toArray();
       for(int i=0;i<iter.length;i++)
       {
         leaf_count=this.getSubTreeLeafCount(((HashMap)parent).get(iter[i]),leaf_count);
       }

    }
    catch(ClassCastException cast)
    {
      //l'oggetto parent e' una foglia
      return leaf_count+1;
    }
    
    return leaf_count;
      
  }

 
 
/*  public static void main(String[] args)
  {
    Tree t=new Tree();
    try{
      t.insertBranch(new Object[]{"A","A1","b"});
       System.out.println(t.getLeaf_count());
      t.insertBranch(new Object[]{"A","A2","A3","c"});
       System.out.println(t.getLeaf_count());
      t.insertBranch(new Object[]{"B","B1","B2","d"});
       System.out.println(t.getLeaf_count());
      t.insertBranch(new Object[]{"B","B1","B3","e"});
       System.out.println(t.getLeaf_count());


      
     t.printTree();
      System.out.println(t.getLeaf_count());
      t.deleteBranch(new Object[]{"B","B1"});
       System.out.println(t.getLeaf_count());
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }*/
 
}