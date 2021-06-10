package view.print;

import insiel.dataHandling.DateUtils;

import java.io.File;

import java.util.Date;
import java.util.Vector;

import model.common.Print_AppModule;

import model.commons.ConfigurationConstants;
import model.commons.ViewHelper;

import model.datacontrol.PrintBean;

import model.inviti.GeneratoreInviti;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

import view.reports.Documento;

public class Print_refStampaAction extends Print_DataAction {
    public Print_refStampaAction() {
    }

    @Override
    protected Vector getLettere() throws Exception {
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");

        //nome dei viewobject
        String ref1l_name =
            (String) ViewHelper.decodeByTpscr(tpscr, "Print_SoRefertocito1livView2", "Print_SoRefertocolon1livView2",
                                              "Print_SoRefertomammo1livView2", "Print_SoRefertocardioView2",
                                              "Print_SoRefertopfasView2");
        String ref2l_name =
            (String) ViewHelper.decodeByTpscr(tpscr, "Print_SoRefertocito2livView2", "Print_SoRefertocolon2livView2",
                                              "Print_SoRefertomammo2livView2", "Print_SoRefertocardioView2",
                                              "Print_SoRefertopfas2livView1");
        String allegati =
            (String) ViewHelper.decodeByTpscr(tpscr, "Print_SoAllegatoView2", "Print_SoAllegatoView3",
                                              "Print_SoAllegatoView4", "Print_SoAllegatoView5",
                                              "Print_SoAllegatoView6");
        String att =
            (String) ViewHelper.decodeByTpscr(tpscr, "Dtultimamodifica", "Dtultmodifica", "Dtultmodifica",
                                              "Dtultimamodifica", "Dtultmodifica");

        Print_AppModule am =
            (Print_AppModule) BindingContext.getCurrent().findDataControl("Print_AppModuleDataControl").getDataProvider();
        String voName = (String) ADFContext.getCurrent().getRequestScope().get("vo");
        ViewObject vo = am.findViewObject(voName);
        ViewObject ref1l = am.findViewObject(ref1l_name);
        ViewObject ref2l = am.findViewObject(ref2l_name);
        ViewObject all = am.findViewObject(allegati);

        RowSetIterator iter = ViewHelper.getRowSetIterator(vo, "stampa");

        PrintBean bean = (PrintBean) BindingContext.getCurrent().findDataControl("PrintBeanDataControl").getDataProvider();

        File f = null;
        Vector printList = new Vector();

        if (vo == null)
            throw new Exception("ViewObject " + voName + " not found");
        //HttpServletResponse resp=ctx.getHttpServletResponse();

        //se ho selezionato solo un invito da stampare
        if ("selection".equals(ADFContext.getCurrent().getRequestScope().get("quanti"))) {

            Row r = vo.getCurrentRow();
            f = File.createTempFile("SingolaLettera", ".pdf");
            if (r == null)
                throw new Exception("Nessuna lettera selezionata per la stampa");
            r = all.first();
            if (r == null)
                throw new Exception("Nessuna lettera selezionata per la stampa");
            /*  BlobDomain blob=(BlobDomain)r.getAttribute("Lettera");
                f=BlobUtils.getFileFromBlob(blob,f.getAbsolutePath());*/
            f = Documento.getDocumento(r, f);
            printList.addElement(f);

            //aggiorno i dati solo se si tratta della prima stampa
            if (r.getAttribute("Dtstampa") == null) {
                r.setAttribute("Dtstampa", DateUtils.getOracleDateNow());
                if ("locale".equals(bean.getTpStampa()))
                    r.setAttribute("Stampapostel", ConfigurationConstants.DB_FALSE);
                else
                    r.setAttribute("Stampapostel", ConfigurationConstants.DB_TRUE);

                //vado ad aggiornre anche il referto
                Row ref;
                //se e' di primo livello
                if (ref1l.getEstimatedRowCount() > 0) {
                    ref = ref1l.first();
                } else //se e' di secondo livello
                {
                    ref = ref2l.first();
                }
                ref.setAttribute("Dtspedizione", r.getAttribute("Dtstampa"));
                ref.setAttribute(att, DateUtils.getOracleDateNow());
                ref.setAttribute("Opultmodifica", ADFContext.getCurrent().getRequestScope().get("user"));
            }


        } else { //..se invece stampo tutti gli inviti di quel tipo


            try {
                //aggiungo tutte le lettere
                System.out.println("stampe massive - ingresso loop");
                int i = 0; // mauro modifica 09/09/2010 rilascio cursori

                while (iter.hasNext()) {

                    // mauro modifica 09/09/2010 rilascio cursori
                    if (i == 100) {
                        am.getTransaction().commit();
                        i = 1;
                    } else {
                        i++;
                    }
                    // mauro modifica 09/09/2010 - fine

                    Row r = iter.next();

                    vo.setCurrentRow(r);

                    r = all.first();
                    if (r == null)
                        throw new Exception("Nessuna lettera selezionata per la stampa");

                    /*  BlobDomain blob=(BlobDomain)r.getAttribute("Lettera");
                 f=BlobUtils.getFileFromBlob(blob,"SingolaLettera.pdf"); */
                    f = File.createTempFile("SingolaLettera", ".pdf");
                    f = Documento.getDocumento(r, f);
                    printList.addElement(f);

                    if (r.getAttribute("Dtstampa") == null) {
                        r.setAttribute("Dtstampa", DateUtils.getOracleDate(new Date()));
                        if ("locale".equals(bean.getTpStampa()))
                            r.setAttribute("Stampapostel", ConfigurationConstants.DB_FALSE);
                        else
                            r.setAttribute("Stampapostel", ConfigurationConstants.DB_TRUE);

                        //vado ad aggiornre anche il referto
                        Row ref;
                        //se e' di primo livello
                        if (ref1l.getEstimatedRowCount() > 0) {
                            ref = ref1l.first();
                        } else //se e' di secondo livello
                        {
                            ref = ref2l.first();
                        }
                        ref.setAttribute("Dtspedizione", DateUtils.getOracleDateNow());
                        ref.setAttribute(att, DateUtils.getOracleDateNow());
                        ref.setAttribute("Opultmodifica",
                                         ADFContext.getCurrent().getRequestScope().get("user"));

                    }

                }
                System.out.println("stampe massive - uscita loop");
                iter.closeRowSetIterator();

            } catch (Exception ex) {
                iter.closeRowSetIterator();
                throw ex;
            }


        } //end if

        if (iter != null)
            iter.closeRowSetIterator();

        return printList;
    }
}
