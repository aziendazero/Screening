package view.reports;

import insiel.dataHandling.BlobUtils;
import insiel.dataHandling.DateUtils;

import java.io.File;

import java.util.HashMap;
import java.util.NoSuchElementException;

import model.common.A_SoAllegatoViewRow;
import model.common.A_SoOldDocsViewRow;

import model.commons.ConfigurationConstants;
import model.commons.Lettera;
import model.commons.LetteraRefertoCitoBean;
import model.commons.ParametriSistema;
import model.commons.SoggUtils;



import model.inviti.GeneratoreInviti;
import model.inviti.LetteraInvitoBean;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.client.Configuration;
import oracle.jbo.domain.BlobDomain;

@SuppressWarnings({ "deprecation", "unchecked", "oracle.jdeveloper.java.insufficient-catch-block" })
public class Documento {


    public static void aggiornaInfoStampa(ApplicationModule am, Integer idInvito, int stampaPostel) {
        String sql =
            "update so_allegato " + "set dtstampa = sysdate, stampapostel = " + stampaPostel + " where idinvito = " +
            idInvito.toString() + " and dtstampa is null";
        am.getTransaction().executeCommand(sql);
        am.getTransaction().commit();
    }

    private static File generateDoc(boolean checkLettera, Integer _idallegato, Row allegato, File f, boolean invito,
                                    String idTpinvito, Integer idApp, GeneratoreInviti gen, ApplicationModule passedAM,
                                    String testProposto) throws Exception {

        ApplicationModule am = null;

        if (passedAM == null)
            am = Configuration.createRootApplicationModule("model.A_AppModule", "A_AppModuleLocal");
        else
            am = passedAM;

        ViewObject vo = null;
        Row all = allegato;
        HashMap h = null;
        //se devo controllare se la lettera c'e' gia', allora controllo
        try {
            if (checkLettera) {
                vo = am.findViewObject("A_SoAllegatoView1");
                vo.setWhereClause("IDALLEGATO=" + _idallegato);
                vo.executeQuery();
                A_SoAllegatoViewRow all2 = (A_SoAllegatoViewRow) vo.first();
                if (all == null)
                    throw new NoSuchElementException("Non e' stato trovato nessun allegato con codice " + _idallegato);
                BlobDomain blob = all2.getLettera();
                //se la lettera c'e' gia', la estraggo in un file
                if (blob != null)
                    return BlobUtils.getFileFromBlob(blob, f.getAbsolutePath());

                //imposto l'allegato con quello trovato
                all = all2;
            }


            //altrimenti vado a rigenerarla
            vo = am.findViewObject("A_SoOldDocsView1");
            //cerco il vecchio documento corrisponedente (imposto anche i criteri di
            //ulss e tpscr per sicurezza)
            vo.setWhereClause("IDALLEGATO=" + all.getAttribute("Idallegato") + " AND ULSS='" +
                              all.getAttribute("Ulss") + "' AND TPSCR='" + all.getAttribute("Tpscr") + "'");
            vo.executeQuery();
            A_SoOldDocsViewRow doc = (A_SoOldDocsViewRow) vo.first();
            if (doc == null) {
                if (!invito)
                    throw new NoSuchElementException("Non e' stato trovato nessun vecchio documento corrispondente all'allegato con codice " +
                                                     _idallegato);

                // mauro 26/11/2010, modifica per gestione postel, vado a generare la lettera
                // utilizzando i dati dell'invito
                String codts = (String) all.getAttribute("Codts");
                String ulss = (String) all.getAttribute("Ulss");
                String tpscr = (String) all.getAttribute("Tpscr");
                Integer idInvito = (Integer) all.getAttribute("Idinvito");

                // se generatore inviti e' nullo lo devo istanziare, altrimenti solo filtrare
                boolean releaseLogo = false;

                if (gen == null) {
                    gen = new GeneratoreInviti(am, codts, ulss, tpscr);
                    releaseLogo = true;
                } else {
                    gen.filter(codts, ulss, tpscr);
                }

                //trovo il docuemnto tessera sanitaria
                String tesseraSanitaria = SoggUtils.trovaTessera(am, codts, ulss);

                gen.creaLetteraPDF(all, ulss, tpscr, idTpinvito, tesseraSanitaria, idApp.intValue(), idInvito, testProposto);

                if (releaseLogo)
                    gen.releaseLogo();

                am.getTransaction().commit();

                BlobDomain blob = (BlobDomain) all.getAttribute("Lettera");

                //se trovo la lettera la estraggo
                if (blob != null)
                    return BlobUtils.getFileFromBlob(blob, f.getAbsolutePath());

                return null;

            }


            if (doc.getCodtempl() == null)
                throw new NullPointerException("Nessun template associato all'allegato con codice " + _idallegato);
            //cerco la riga col template
            vo = am.findViewObject("A_SoTemplateView1");
            vo.setWhereClause("CODTEMPL=" + doc.getCodtempl() + " AND ULSS='" + doc.getUlss() + "' AND TPSCR='" +
                              doc.getTpscr() + "'");
            vo.executeQuery();
            Row template = vo.first();
            if (template == null)
                throw new NoSuchElementException("Non e' stato trovato nessun template corrispondente a codice " +
                                                 doc.getCodtempl() + ", ulss " + doc.getUlss() +
                                                 ", tipo di screening " + doc.getTpscr());

            //creo l'ggetto lettera col template
            Lettera l =
                new Lettera(template, "A_SoTemplateView1", "Filexml", "Compiled", f.getAbsolutePath(),
                            ConfigurationConstants.FORMATO_PDF);
            //imposto la hashmap con quella disponibile, modificando alcuni parametri
            h = ParametriSistema.getParamTemplate(doc.getUlss(), doc.getTpscr(), am.findViewObject("A_SoAziendaView1"),
                                                  am.findViewObject("A_SoCnfPartemplateView1"));
            //sovrascrivo i parametri con quelli storicizzati
            h.put("giorni_orari", doc.getGiorniOrari());
            h.put("telefono", doc.getTelefono());
            h.put("int_firma_inviti", doc.getIntFirmaInviti());
            h.put("firma_inviti", doc.getFirmaInviti());
            h.put("firma_ref1", doc.getFirmaRef1());
            h.put("firma_ref2", doc.getFirmaRef2());
            h.put("luogo", doc.getLuogo());

            l.setParametersMap(h);

            //trovo la tessera sanitaria
            String tesseraSanitaria = SoggUtils.trovaTessera(am, doc.getTesseraSanitaria(), doc.getUlss());

            //creo il bean in caso si tratti di un invito
            File pdf = null;
            try {
                LetteraInvitoBean li =
                    new LetteraInvitoBean(doc.getCognome(), doc.getNome(), doc.getIndirizzo(), doc.getCap(),
                                          doc.getComune(), doc.getProvincia(),
                                          doc.getDataStampa() == null ? null :
                                          DateUtils.dateToString(doc.getDataStampa().dateValue()), doc.getCentro(),
                                          doc.getIndirizzoCentro(), doc.getDataApp(), doc.getOraApp(), doc.getMedico(),
                                          doc.getDataEsame(), doc.getCodiceCampione(), doc.getAltro(),
                                          doc.getDataNascita(), tesseraSanitaria, doc.getCodiceFiscale(), //30092011 Gaion cf negli inviti
                                          /*20080718 MOD: dati centro nelle stampe*/
                                          null, //sede
                                          null, //orari
                                          null, //tel centro
                                          /*20080718 FINE MOD*/
                                          null, //braccio mx45
                                          null, //codice mx45
                                          null, //email
                                          null, //cellulare
                                          null, //otp
                                          null); //codice richiesta
                pdf = l.createLetter(li);

            } catch (Exception ex) {
                //probabilmente non e' un alettera di invito ma di referto
                LetteraRefertoCitoBean lr =
                    new LetteraRefertoCitoBean(doc.getCognome(), doc.getNome(), doc.getIndirizzo(), doc.getCap(),
                                               doc.getComune(), doc.getProvincia(),
                                               doc.getDataStampa() == null ? null :
                                               DateUtils.dateToString(doc.getDataStampa()), tesseraSanitaria,
                                               doc.getCodiceFiscale(), null, doc.getDataNascita(),
                                               doc.getCognomeMarito(), doc.getTelefono1(), doc.getTelefono2(),
                                               doc.getDataApp(), doc.getCentro(), doc.getCentroReferto(), null, null,
                                               null, null, null, null, null, null, doc.getDataEsame(), null,
                                               doc.getMedico(),
                                               /*20080718 MOD: dati centro nelle stampe*/
                                               doc.getIndirizzoCentro(), null, //sede
                                               null, //orari
                                               null, //tel centro
                                               /*20080718 FINE MOD*/
                                               /*20110207 fine mod Serra*/
                                               doc.getDataApp()
                                               /*20110207 fine mod Serra*/, null, null, null, null, null, null, null,
                                               null, null, null);


                pdf = l.createLetter(lr);
            }
            //segnalo che e' gia' stato importato
            doc.setImportato(ConfigurationConstants.DB_TRUE);
            //lo salvo
            all.setAttribute("Lettera", BlobUtils.getBlobFromFile(pdf));
            //la commit salva anche il pdf se ho usato l'id dell'allegato, altrimenti
            //il slavataggio e' a carico dell'application module del chiamante
            am.getTransaction().commit();

            return pdf;

        } finally {
            if (passedAM == null)
                Configuration.releaseRootApplicationModule(am, true);
            //per evitare sovraccarico di connessioni al db (7/12/2006)
            //Configuration.releaseRootApplicationModule(am,false);
            ParametriSistema.releaseLogo(h);

        }
    }

    private static File getDocumento(Row allegato, File f, boolean invito, String idTpinvito, Integer idApp,
                                     GeneratoreInviti gen, ApplicationModule passedAM, String testProposto)
    throws Exception {
        return getDocumento(allegato, f, invito, idTpinvito, idApp, gen, passedAM, testProposto, false);
    }
    
    private static File getDocumento(Row allegato, File f, boolean invito, String idTpinvito, Integer idApp,
                                     GeneratoreInviti gen, ApplicationModule passedAM, String testProposto,
                                     boolean deleted)

        throws Exception {
        //System.out.println(f.getName());
        if (allegato == null)
            throw new NullPointerException("Impossibile reperire un allegato nullo");

        BlobDomain blob = (BlobDomain) allegato.getAttribute("Lettera");
        //se trovo la lettera la estraggo
        if (blob != null) {
            return BlobUtils.getFileFromBlob(blob, f.getAbsolutePath());
        } else if (deleted) {
            // altrimenti, se sto aprendo un documento cancellato restituisco null
            return null;
        } else {
            //altrimenti vado a rigenerarla, ma senza il controllo sulla lettera
            //percio' non serve l'id dell'allegato ma solo il record
            return generateDoc(false, null, allegato, f, invito, idTpinvito, idApp, gen, passedAM, testProposto);
        }
    }

    public static File getDocumento(Row allegato, File f) throws Exception {
        //System.out.println(f.getName());
        return getDocumento(allegato, f, false, null, null, null, null, null);

    }

    public static File getDocumento(Row allegato, File f, String idTpinvito, Integer idApp, GeneratoreInviti gen,
                                    ApplicationModule passedAM, String testProposto)

        throws Exception {
        return getDocumento(allegato, f, true, idTpinvito, idApp, gen, passedAM, testProposto);

    }

    public static File getDocumento(Row allegato, File f, String idTpinvito, Integer idApp)

        throws Exception {
        return getDocumento(allegato, f, true, idTpinvito, idApp, null, null, null);

    }

    public static File getDocumento(Row allegato, File f, String idTpinvito, Integer idApp, String testProposto)
        throws Exception {
        return getDocumento(allegato, f, true, idTpinvito, idApp, null, null, testProposto);
    }

    public static File getDocumentoDeleted(Row allegato, File f, String idTpinvito, Integer idApp, String testProposto)
        throws Exception {
        return getDocumento(allegato, f, true, idTpinvito, idApp, null, null, testProposto, true);
    }

}
