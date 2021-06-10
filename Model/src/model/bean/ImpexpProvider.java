package model.bean;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import model.commons.ViewHelper;

import model.util.ImpexpUtils;

import oracle.adf.share.ADFContext;

public class ImpexpProvider {
    
    public ImpexpProvider(){
        
        System.out.println("---->>>> inizializzo ImpexpProvider");
        
    }
    
    public void init(){
        System.out.println("---->>>> metodo init ImpexpProvider");
    }

    /****************************************************************/
    /**************        IMPORT REFERTI DA AP     *****************/

    /****************************************************************/
    public List<ImpexpFile> findFileList() {
        Map session = ADFContext.getCurrent().getSessionScope();

        String dirpath = (String) session.get("impexp_localdir");
        String filename = (String) session.get("impexp_localfile");
        if (dirpath == null || filename == null) {
            return null;
        }
        String dateFilter = (String) session.get("impexp_dateFilter");
        String centerFilter = (String) session.get("impexp_centerFilter");
        Integer current = (Integer) session.get("impexp_newCurrent");
        int new_current = current == null ? -1 : current.intValue();
        //se ho impostato il filtro sulla data lo includo nella prima parte del nome
        if (dateFilter != null && dateFilter.length() > 0)
            filename += dateFilter;


        File dir = new File(dirpath);
        /* new_files=changeVectorIntoDOL(ImpexpUtils.getFileList(dir,filename,centerFilter),
                                    new_current);
      return new_files;*/
        return changeVectorIntoDOL(ImpexpUtils.getFileList(dir, filename, centerFilter));
    }

    public List<ImpexpFile> findOLDFileList() {
        Map session = ADFContext.getCurrent().getSessionScope();

        String dirpath = (String) session.get("impexp_localdir");
        String filename = (String) session.get("impexp_localfile");
        if (dirpath == null || filename == null) {
            return null;
        }

        String dateFilter = (String) session.get("impexp_dateFilter");
        String centerFilter = (String) session.get("impexp_centerFilter");
        Integer current = (Integer) session.get("impexp_oldCurrent");
        int old_current = current == null ? -1 : current.intValue();

        //se ho impostato il filtro sulla dta lo includo nell aprima parte del nome
        if (dateFilter != null && dateFilter.length() > 0)
            filename += dateFilter;
        File dir = new File(dirpath);
        List<ImpexpFile> old_files =
            changeVectorIntoDOL(ImpexpUtils.getOLDFileList(dir, filename, centerFilter));

        //imposto i nomi da visualizzare privi di _OLD
        for (int i = 0; i < old_files.size(); i++) {
            ImpexpFile fdo = (ImpexpFile) old_files.get(i);
            String s = fdo.getName();
            if (s != null && s.endsWith("_OLD")) {
                s = s.substring(0, s.indexOf("_OLD"));
                fdo.setViewName(s);
            }

            // 16112011 gaion: aggiunto lo stato del file
            if (s != null && s.endsWith("_IMPORTED")) {
                s = s.substring(0, s.indexOf("_IMPORTED"));
                fdo.setViewName(s);
                fdo.setStatus("IMPORTATO");
            }
            //fine 16112011
        }

        return old_files;
    }

    static protected List<ImpexpFile> changeVectorIntoDOL(Vector v) {
        if (v == null)
            return null;

        ImpexpFile[] dol = new ImpexpFile[v.size()];
        for (int i = 0; i < dol.length; i++) {
            File f = (File) v.elementAt(i);
            dol[i] = new ImpexpFile(f);
            dol[i].setName(f.getName());
        }

        return new ArrayList<ImpexpFile>(Arrays.asList(dol));
    }


    /****************************************************************/
    /**************        TRACCIATO UNICO         *****************/

    /****************************************************************/


    public List<ImpexpFile> findZipFileList( String namespace, String name) {
       Map session = ADFContext.getCurrent().getSessionScope();

        String dirpath = (String) session.get("impexp_localdir");
        String filename = (String) session.get("impexp_localfile");
        if (dirpath == null || filename == null) {
            return null;
        }
        /*  String dateFilter=(String)session.get("impexp_dateFilter");
      String centerFilter=(String)session.get("impexp_centerFilter");*/
        Integer current = (Integer) session.get("impexp_zipCurrent");
        int zip_current = current == null ? -1 : current.intValue();
        String tpscr = (String) session.get("scr");
        File dir = new File(dirpath);
        List<ImpexpFile> zip_files =
            changeVectorIntoDOL(ImpexpUtils.getFileList(dir, filename, tpscr + ".zip"));


        //imposto i nomi da visualizzare: solo peridodo e anno
        for (int i = 0; i < zip_files.size(); i++) {
            ImpexpFile fdo = (ImpexpFile) zip_files.get(i);
            String s = fdo.getName();
            if (s != null) {
                //tolgo il suffisso CI.zip (o MA.zip o CO.zip)
                s = s.substring(0, s.length() - 6);
                //prenso solo i 4 caratteri centrali, 2 per il periodo e 2 per l'anno
                //s=s.substring(s.length()-4);
                fdo.setViewName(s);
            }
        }
        return zip_files;
    }

    static public List<ImpexpFile> findDwhFileList( String namespace, String name) {
       Map session = ADFContext.getCurrent().getSessionScope();

        String dirpath = namespace;
        String filename = name;
        if (dirpath == null || filename == null) {
            return null;
        }

        File dir = new File(dirpath);
        String tpscr = (String) session.get("scr");
        String suffisso = (String) ViewHelper.decodeByTpscr(tpscr, "A", "C", "B", null, null) + ".zip";
        List<ImpexpFile> zip_files = changeVectorIntoDOL(ImpexpUtils.getFileList(dir, filename, suffisso));


        //imposto i nomi da visualizzare: solo peridodo e anno e tpscr
        for (int i = 0; i < zip_files.size(); i++) {
            ImpexpFile fdo = (ImpexpFile) zip_files.get(i);
            String s = fdo.getName();
            if (s != null) {
                //tolgo il suffisso .zip (o MA.zip o CO.zip)
                s = s.substring(0, s.length() - 4);
                fdo.setViewName(s);
            }
        }
        return zip_files;
    }

    public List<ImpexpFile> findSPSFileList( String namespace, String name) {
       Map session = ADFContext.getCurrent().getSessionScope();

        String dirpath = (String) session.get("impexp_localdir");
        String filename = (String) session.get("impexp_localfile");
        if (dirpath == null || filename == null) {
            return null;
        }
        Integer current = (Integer) session.get("impexp_zipCurrent");
        int zip_current = current == null ? -1 : current.intValue();
        File dir = new File(dirpath);
        String tpscr = (String) session.get("scr");
        String suffisso = ".zip";
        List<ImpexpFile> zip_files =
            changeVectorIntoDOL(ImpexpUtils.getFileList(dir, filename + tpscr, suffisso));


        //imposto i nomi da visualizzare: solo peridodo e anno e tpscr
        for (int i = 0; i < zip_files.size(); i++) {
            ImpexpFile fdo = (ImpexpFile) zip_files.get(i);
            String s = fdo.getName();
            if (s != null) {
                //tolgo il suffisso .zip
                s = s.substring(0, s.length() - 4);
                fdo.setViewName(s);
            }
        }
        return zip_files;
    }

    public List<ImpexpFile> findSospesiFileList( String namespace, String name) {
       Map session = ADFContext.getCurrent().getSessionScope();

        String dirpath = (String) session.get("impexp_virtualdir");
        String filename = (String) session.get("impexp_localfile");
        if (dirpath == null || filename == null) {
            return null;
        }

        Integer current = (Integer) session.get("impexp_sospesiCurrent");
        int sospesi_current = current == null ? -1 : current.intValue();
        String tpscr = (String) session.get("scr");
        File dir = new File(dirpath);
        List<ImpexpFile> sospesi_files =
            changeVectorIntoDOL(ImpexpUtils.getFileList(dir, filename, "S" + tpscr + ".txt"));

        //imposto i nomi da visualizzare: solo periodo e anno
        for (int i = 0; i < sospesi_files.size(); i++) {
            ImpexpFile fdo = (ImpexpFile) sospesi_files.get(i);
            String s = fdo.getName();
            if (s != null) { //tolgo l'estensione
                if (s.indexOf(".") > 0)
                    s = s.substring(0, s.indexOf("."));
                //tolgo il suffisso SCI.txt (o SMA.txt o SCO.txt)
                s = s.substring(0, s.length() - 3);
                //prenso solo i 4 caratteri centrali, 2 per il periodo e 2 per l'anno
                s = s.substring(s.length() - 4);
                fdo.setViewName(s);
            }
        }
        return sospesi_files;
    }

   


}



