package view.providers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import model.common.Parent_AppModule;

import model.commons.ViewHelper;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

public class Ref_supportForProviders {
    
    /**
     * 
     */
    public static void resetRefSelection(ViewObject cnfSource){
        RowSetIterator iter = cnfSource.createRowSetIterator(null);
        while (iter.hasNext()) {

            Row row = iter.next();
            
            //controllo se e' una riga selezionata
            row.setAttribute("Selected", false);
        }
    }
    
    
    /**
     * Metodo che crea il vettore con i bean per i provider dei referti
     * leggendo i dati presenti sul database
     * @return vettore di bean
     * @param gruppo gruppo di dati di conf da utilizzare (POSOTO, MODREA...)
     * @param cnfDataAttName nome dell'attributo da impostare nella tabella dati
     * @param cnfData tabella dati
     * @param cnfSourceAttName nome dell'attributo da leggere dalla tabella di configurazione
     * @param cnfSource tabella di configurazione
     */
    public static void getDBRefSelection(ViewObject cnfSource, String cnfSourceAttName, ViewObject cnfData,
                                         String cnfDataAttName, String gruppo) {

        System.out.println("coref: " + cnfData.getEstimatedRowCount());
        //leggo i dati dei referti effettivamnete inseriti
        Row[] dati = cnfData.getFilteredRows("Gruppo", gruppo);
        System.out.println("filtrate: " + dati.length);
        HashMap hash = ViewHelper.createHashMapFromRows(dati, cnfDataAttName);
        System.out.println("hasmap: " + hash.size());
        //mi baso sul fatto che mostro sempre tutti i valori,
        //quindi la posizione e' uguale all'indice
        //itero i valori di configurazione
        RowSetIterator iter = cnfSource.createRowSetIterator(null);
        while (iter.hasNext()) {
            Row row = iter.next();
            Boolean selected = Boolean.FALSE;
            System.out.println("keys :" + hash.keySet().toString());
            System.out.println("values :" + hash.values().toString());
            //controllo se tale configurazione e' presente e quindi va selezionata
            Row r1 = (Row) hash.get(row.getAttribute(cnfSourceAttName));
            if (r1 != null)
                selected = Boolean.TRUE;

            row.setAttribute("Selected", selected);
        }
        iter.closeRowSetIterator();
    }


    /**
     * Metodo che, a partire dai dati selezionati nell'interfacci,
     * fa un update del database aggiungendo e rimuovendo le configuraizoni necessarie
     * @param gruppo gruppo di dati di conf da utilizzare (POSOTO, MODREA...)
     * @param cnfDataAttName nome dell'attributo da impostare nella tabella dati
     * @param cnfData tabella dati
     * @param cnfSourceAttName nome dell'attributo da leggere dalla tabella di configurazione
     * @param cnfSource tabella di configurazione
     * @param indices elenco di indici selezionati nella selezione multipla
     * @param referto
     * @param am
     */
    public static void updateRefSelection(ViewObject cnfSource, String cnfSourceAttName, ViewObject cnfData,
                                          String cnfDataAttName, String gruppo, Parent_AppModule am, ViewObject referto,
                                          int livello) {


        Row r = referto.getCurrentRow();
        if (r == null)
            return;
        //configurazione dipendente dai referti su cui sto lavorando

        Row[] dati = cnfData.getFilteredRows("Gruppo", gruppo);
        HashMap hash = ViewHelper.createHashMapFromRows(dati, cnfDataAttName);

        RowSetIterator iter = cnfSource.createRowSetIterator(null);
        while (iter.hasNext()) {

            Row row = iter.next();
            //controllo se e' una riga selezionata
            Boolean selected = (Boolean) row.getAttribute("Selected");

            Row r1 = (Row) hash.get(row.getAttribute(cnfSourceAttName));
            //se la riga e' selezionata devo controllare che esista sul db
            if (selected != null && selected) {
                //se non esiste la inserisco
                if (r1 == null) {
                    r1 = cnfData.createRow();
                    cnfData.insertRow(r1);
                    if (livello == 2)
                        r1.setAttribute("Id", am.getNextIdConfReferto2liv());
                    else
                        r1.setAttribute("Id", am.getNextIdConfReferto1liv());
                    r1.setAttribute("Idreferto", r.getAttribute("Idreferto"));
                    r1.setAttribute("Gruppo", gruppo);
                    r1.setAttribute(cnfDataAttName, row.getAttribute(cnfSourceAttName));
                    r1.setAttribute("Ulss", r.getAttribute("Ulss"));
                    r1.setAttribute("Tpscr", r.getAttribute("Tpscr"));


                }
            } else //se la riga NON e' selezionata allora devo controllare che NON esista sul DB
            {
                //se esiste la cancello
                if (r1 != null) {
                    r1.remove();
                }
            }

        }
        iter.closeRowSetIterator();
    }

    /**
     * Legge se esiste una riga (corrispondente ad una selezione singola) per un
     * dato di configurazione sul Database
     * @return il valore da usra eper l'interfaccia o null se il record non e' stato trovato
     * @param gruppo gruppo di riferimento della configurzione
     * @param cnfDataAttName nome dell'attributo da restituire
     * @param cnfData view object con i dati di configurzione del referto in uso
     */
    public static Object getDBSingleRefSelection(ViewObject cnfData, String cnfDataAttName, String gruppo) {

        //leggo i dati dei referti effettivamnete inseriti
        Row[] dati = cnfData.getFilteredRows("Gruppo", gruppo);
        if (dati.length > 0)
            return dati[0].getAttribute(cnfDataAttName);
        else
            return null;
    }

          /**
           * Riporta sul DB il risultato della sleezione singola dell'utente
           * @param referto referto in uso
           * @param am application module
           * @param beanProperty velore impostato dall'utente
           * @param gruppo gruppo della configurazione
           * @param cnfDataAttName nome dell'attributo da impostare
           * @param cnfData viewobject dei datidi configurazione gia' scremati
           * per il referto in uso
           */
           public static void updateSingleRefSelection(ViewObject cnfData,
                                                      String cnfDataAttName,
                                                      String gruppo,
                                                      Object beanProperty,
                                                      Parent_AppModule am,
                                                      ViewObject referto,
                                                      int livello)
          {
    
    
            Row r=referto.getCurrentRow();
              if(r==null)
                return;
            //configurazione dipendente dai referti su cui sto lavorando
    
            Row[] dati=cnfData.getFilteredRows("Gruppo",gruppo);
            //se l'utente non ha selezionato nulla, bisogna evetualmente cancellare
            if(beanProperty==null)
            {
              for(int i=0;i<dati.length;i++)
                dati[i].remove();
            }
            else
            {  //l'utente ha fatto una selezione che sul DB non c'e', dunque la creo
              if(dati.length==0)
              {
                Row r1=cnfData.createRow();
                   cnfData.insertRow(r1);
                   if(livello==2)
                     r1.setAttribute("Id",am.getNextIdConfReferto2liv());
                   else
                    r1.setAttribute("Id",am.getNextIdConfReferto1liv());
    
                   r1.setAttribute("Idreferto",r.getAttribute("Idreferto"));
                   r1.setAttribute("Gruppo",gruppo);
                   r1.setAttribute(cnfDataAttName,beanProperty);
                   r1.setAttribute("Ulss",r.getAttribute("Ulss"));
                   r1.setAttribute("Tpscr",r.getAttribute("Tpscr"));
              }
              else
              {
    
              //la selezione c'e' gia', basta aggiornarla, solo se serve
               if(beanProperty==null && dati[0].getAttribute(cnfDataAttName)!=null)
                  dati[0].setAttribute(cnfDataAttName,null);
               else if(!beanProperty.equals(dati[0].getAttribute(cnfDataAttName)))
                  dati[0].setAttribute(cnfDataAttName,beanProperty);
    
              }
            }
          }
    
    
    /*MAMMOGRAFICO*/

    /**
     * Metodo che, a partire dai dati selezionati nell'interfacci,
     * fa un update del database aggiungendo e rimuovendo le configuraizoni necessarie
     * @param gruppo gruppo di dati di conf da utilizzare (POSOTO, MODREA...)
     * @param cnfDataAttName nome dell'attributo da impostare nella tabella dati
     * @param cnfData tabella dati
     * @param cnfSourceAttName nome dell'attributo da leggere dalla tabella di configurazione
     * @param cnfSource tabella di configurazione
     * @param indices elenco di indici selezionati nella selezione multipla
     * @param referto
     * @param am
     */
    public static void updateRefSelectionMA(ViewObject cnfSource, String cnfSourceAttName, ViewObject cnfData,
                                            String cnfDataAttName, String gruppo, Parent_AppModule am,
                                            ViewObject referto) {


        Row r = referto.getCurrentRow();
        if (r == null)
            return;
        //configurazione dipendente dai referti su cui sto lavorando
        Row[] dati = cnfData.getFilteredRows("Gruppo", gruppo);
        HashMap hash = ViewHelper.createHashMapFromRows(dati, cnfDataAttName);

        //mi baso sul fatto che mostro sempre tutti i valori,
        RowSetIterator iter = cnfSource.createRowSetIterator(null);
        while (iter.hasNext()) {

            Row row = iter.next();
            //controllo se e' una riga selezionata
            Boolean selected = (Boolean) row.getAttribute("Selected");

            Row r1 = (Row) hash.get(row.getAttribute(cnfSourceAttName));
            //se la riga e' selezionata devo controllare che esista sul db
            if (selected != null && selected) {
                //se non esiste la inserisco
                if (r1 == null) {
                    r1 = cnfData.createRow();
                    cnfData.insertRow(r1);

                    r1.setAttribute("Id", am.getNextIdCnfRef3livMA());
                    r1.setAttribute("Idint", r.getAttribute("Idint"));
                    r1.setAttribute("Gruppo", gruppo);
                    r1.setAttribute(cnfDataAttName, row.getAttribute(cnfSourceAttName));
                    r1.setAttribute("Ulss", r.getAttribute("Ulss"));
                    r1.setAttribute("Tpscr", r.getAttribute("Tpscr"));
                }
            } else //se la riga NON e' selezionata allora devo controllare che NON esista sul DB
            {
                //se esiste la cancello
                if (r1 != null) {
                    r1.remove();
                }
            }
        }

        iter.closeRowSetIterator();

    }
}
