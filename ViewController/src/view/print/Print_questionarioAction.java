package view.print;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;

import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;

import com.lowagie.text.PageSize;

import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.sql.SQLException;

import java.text.DateFormat;

import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import javax.servlet.ServletContext;

import model.RefCa_AppModule;

import model.common.RefPf_AppModule;

import model.commons.DomandaQuestionario;
import model.commons.Questionario;

import model.commons.Scelta;

import model.global.A_SoAziendaViewRowImpl;

import model.referto.Ref_SoCnfSugg1livViewRowImpl;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.BlobDomain;
import oracle.jbo.domain.Date;

public class Print_questionarioAction {
    private static final Font FONT_INTESTAZIONE = new Font(Font.HELVETICA, 16, Font.BOLD);
    private static final Font FONT_TITOLO_QUESTIONARIO = new Font(Font.HELVETICA, 12, Font.BOLD);
    private static final Font FONT_TITOLO_SEZIONE = new Font(Font.HELVETICA, 12, Font.BOLD);
    private static final Font FONT_TITOLO_SCELTA = new Font(Font.HELVETICA, 10, Font.BOLD);
    private static final Font FONT_DOMANDA = new Font(Font.HELVETICA, 8, Font.NORMAL);
    private static final Font FONT_SPAZIO = new Font(Font.HELVETICA, 8, Font.NORMAL);
    private static final Font FONT_RISPOSTA = new Font(Font.HELVETICA, 10, Font.BOLD);
    private static final Font FONT_NOTE = new Font(Font.HELVETICA, 8, Font.ITALIC);
    private static final Font FONT_FOOTER = new Font(Font.HELVETICA, 8, Font.NORMAL);
    private static final float TABLE_WIDTH = 530;
    private static Image imgUnchecked = null;
    private static Image imgChecked = null;

    public Print_questionarioAction() {
    }

    public void stampaQuestionario(FacesContext facesContext, OutputStream outputStream) throws Exception {
        generatePDF(outputStream);
    }

    private void generatePDF(OutputStream pdfOut) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        File pdfFile = null;
        try {
            if (imgUnchecked == null) {
                imgUnchecked = getImage("unchecked.png");
            }
            if (imgChecked == null) {
                imgChecked = getImage("checked.png");
            }

            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfPTable table = null;
            pdfFile = File.createTempFile("questionario", ".pdf");
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            // Footer
            HeaderFooter footer = new HeaderFooter(new Phrase("Pag. ", FONT_FOOTER), true);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.setFooter(footer);

            // Ottengo il questionario
            //Questionario questionario = getQuestionario();
            Questionario questionario =
                (Questionario) BindingContext.getCurrent().findDataControl("QuestionarioDataControl").getDataProvider();

            // Ottengo il referto
            ApplicationModule am = null;
            ViewObject referti = null;
            ViewObject inviti = null;
            Map sess = ADFContext.getCurrent().getSessionScope();
            String tpscr = (String) sess.get("scr");
            if (tpscr.equals("CA")) {
                am =
                    (RefCa_AppModule) BindingContext.getCurrent().findDataControl("RefCa_AppModuleDataControl").getDataProvider();
                referti = am.findViewObject("RefCa_SoRefertocardioView1");
                inviti = am.findViewObject("Ref_SoInvitiPerRefertiCAView1");
            } else if (tpscr.equals("PF")) {
                am =
                    (RefPf_AppModule) BindingContext.getCurrent().findDataControl("RefPf_AppModuleDataControl").getDataProvider();
                referti = am.findViewObject("Ref_SoRefertopfasView1");
                inviti = am.findViewObject("Ref_SoInvitiPerRefertiPFView1");
            }
            Row refertoRow = referti.getCurrentRow();

            if (refertoRow != null) {

                // Intestazione
                PdfPCell cell;
                ViewObject aziendaView = am.findViewObject("A_SoAziendaView1");
                aziendaView.setWhereClause("CODAZ = '" + refertoRow.getAttribute("Ulss") + "'");
                aziendaView.executeQuery();
                A_SoAziendaViewRowImpl aziendaRow = (A_SoAziendaViewRowImpl) aziendaView.first();
                BlobDomain logoBlob = aziendaRow.getLogo();
                if (logoBlob != null) {
                    InputStream logoStream = logoBlob.getBinaryStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[512];
                    int count;
                    while ((count = logoStream.read(buffer)) > 0) {
                        baos.write(buffer, 0, count);
                    }
                    baos.close();
                    logoStream.close();
                    Image logo = Image.getInstance(baos.toByteArray());
                    logo.scaleToFit(100f, 100f);
                    logo.setXYRatio(1f);
                    table = new PdfPTable(2);
                    table.setTotalWidth(TABLE_WIDTH);
                    table.setLockedWidth(true);
                    table.setWidths(new float[] { 150f, TABLE_WIDTH - 150f });
                    cell = new PdfPCell(logo);
                    cell.setBorder(Rectangle.BOTTOM);
                    table.addCell(cell);
                    cell = new PdfPCell(new Phrase(new Chunk(aziendaRow.getDescrizione(), FONT_INTESTAZIONE)));
                    cell.setBorder(Rectangle.BOTTOM);
                    cell.setPaddingLeft(20f);
                    table.addCell(cell);
                    document.add(table);
                } else {
                    table = new PdfPTable(1);
                    table.setTotalWidth(TABLE_WIDTH);
                    table.setLockedWidth(true);
                    cell = new PdfPCell(new Phrase(new Chunk(aziendaRow.getDescrizione(), FONT_INTESTAZIONE)));
                    cell.setBorder(Rectangle.BOTTOM);
                    cell.setPaddingLeft(20f);
                    table.addCell(cell);
                    document.add(table);
                }

                // Titolo del questionario
                Paragraph titoloQuestionario = new Paragraph(questionario.getDescrizione(), FONT_TITOLO_QUESTIONARIO);
                titoloQuestionario.setAlignment(Element.ALIGN_CENTER);
                document.add(titoloQuestionario);

                // Dati anagrafici
                Row invitiRow = inviti.getCurrentRow();

                table = new PdfPTable(4);
                table.setSpacingBefore(20);
                table.setTotalWidth(TABLE_WIDTH);
                table.setLockedWidth(true);

                table.addCell(getCell("Cognome", (String) invitiRow.getAttribute("Cognome")));
                table.addCell(getCell("Nome", (String) invitiRow.getAttribute("Nome")));
                table.addCell(getCell("Sesso", (String) invitiRow.getAttribute("Sesso")));
                table.addCell(getCell("Data di nascita",
                                      dateFormat.format(((Date) invitiRow.getAttribute("DataNascita")).dateValue())));
                table.addCell(getCell("Tessera", (String) invitiRow.getAttribute("DocIdent")));
                table.addCell(getCell("MPI", (String) invitiRow.getAttribute("Idinterno")));
                table.addCell(getCell("C.F.", (String) invitiRow.getAttribute("Cf")));
                table.addCell(getCell("", ""));

                // Dati invito
                table.addCell(getCell("Data invito",
                                      dateFormat.format(((Date) invitiRow.getAttribute("Dtapp")).dateValue())));
                String centroPrelievo = "";
                if (tpscr.equals("CA")) {
                    centroPrelievo = (String) invitiRow.getAttribute("DescrCentroPrel");
                } else if (tpscr.equals("PF")) {
                    centroPrelievo = (String) invitiRow.getAttribute("CentroPrelievo");
                }
                PdfPCell centroCell = getCell("Centro", centroPrelievo);
                centroCell.setColspan(3);
                table.addCell(centroCell);
                //                    PdfPCell medicoCell = getCell("Medico", invitiRow.get);
                //                    medicoCell.setColspan(2);
                //                    table.addCell(medicoCell);

                // Esito referto
                if (refertoRow.getAttribute("Completo") != null &&
                    ((Integer) refertoRow.getAttribute("Completo")).intValue() == 1) {
                    Integer idSugg = (Integer) refertoRow.getAttribute("Idsugg");
                    ViewObject suggView = am.findViewObject("Ref_SoCnfSugg1livView1");
                    Row[] suggRows =
                        suggView.findByKey(new Key(new Object[] { idSugg, refertoRow.getAttribute("Ulss"), tpscr }), 1);
                    if (suggRows.length > 0) {
                        Ref_SoCnfSugg1livViewRowImpl suggRow = (Ref_SoCnfSugg1livViewRowImpl) suggRows[0];
                        String suggerimento = suggRow.getDescrizione();
                    }

                    String suggerimento = "";
                    if (tpscr.equals("CA")) {
                        suggerimento = (String) invitiRow.getAttribute("Sugg");
                    } else if (tpscr.equals("PF")) {
                        suggerimento = (String) invitiRow.getAttribute("RichiamoDescr");
                    }

                    PdfPCell suggerimentoCell = getCell("Raccomandazione", suggerimento);
                    suggerimentoCell.setColspan(4);
                    table.addCell(suggerimentoCell);
                }

                document.add(table);
                table = null;
                int numCols = 0;

                // Domande
                List domande1liv = questionario.getDomande1liv();

                for (int i1liv = 0; i1liv < domande1liv.size();) {
                    DomandaQuestionario domanda = (DomandaQuestionario) domande1liv.get(i1liv);
                    String sezionePrec = domanda.getSezione();

                    // Ciclo per sezione
                    while (i1liv < domande1liv.size() && sezionePrec.equals(domanda.getSezione())) {
                        if ("TITOLO".equals(domanda.getTipo())) {

                            // Il titolo interrompe la table
                            if (table != null) {
                                document.add(table);
                                table = null;
                            }

                            Chunk c = new Chunk(domanda.getTesto(), FONT_TITOLO_SEZIONE);
                            float w = c.getWidthPoint();
                            float r = TABLE_WIDTH - 12 - w;
                            c.setBackground(new Color(0xc0c0c0), 12, 0, r, 4);
                            Paragraph titolo = new Paragraph(c);
                            document.add(titolo);
                        } else {

                            // Creo una nuova table, se non esiste gia'
                            if (table == null) {

                                // Determino il numero di colonne
                                DomandaQuestionario domanda2 = domanda;
                                numCols = 0;
                                for (int i = i1liv; i < domande1liv.size() && sezionePrec.equals(domanda2.getSezione());
                                     i++) {
                                    int n =
                                        domanda2.getDomande1liv() != null ? domanda2.getDomande1liv().size() + 1 : 1;
                                    if (n > numCols) {
                                        numCols = n;
                                    }
                                    domanda2 = (DomandaQuestionario) domande1liv.get(i);
                                }

                                table =
                                    new PdfPTable(domanda.getDomande1liv() != null ?
                                                  domanda.getDomande1liv().size() + 1 : 1);
                                table.setSpacingAfter(12);
                                table.setTotalWidth(TABLE_WIDTH);
                                table.setLockedWidth(true);
                            }

                            // Prima cella della riga
                            cell = getNode(domanda, questionario.isReadOnly(), tpscr);
                            table.addCell(cell);
                            int numCells = 1;

                            // Altre domande sulla stessa riga
                            if (domanda.getDomande1liv() != null) {

                                // Ciclo per le domande di secondo livello (sulla stessa riga)
                                for (Iterator i2 = domanda.getDomande1liv().iterator(); i2.hasNext();) {
                                    domanda = (DomandaQuestionario) i2.next();
                                    cell = getNode(domanda, questionario.isReadOnly(), tpscr);
                                    table.addCell(cell);
                                    numCells++;
                                }
                            }

                            // Aggiungo colonne vuote fino a riempire la riga
                            for (; numCells < numCols; numCells++) {
                                PdfPCell emptyCell = new PdfPCell();
                                emptyCell.setBorder(0);
                                table.addCell(emptyCell);
                            }
                        }
                        if (++i1liv < domande1liv.size()) {
                            domanda = (DomandaQuestionario) domande1liv.get(i1liv);
                        }
                    }

                    // E' finita la sezione, quindi chiudo la table
                    if (table != null) {
                        document.add(table);
                        table = null;
                    }

                    sezionePrec = domanda.getSezione();
                }
            }
            document.close();

            FileInputStream is = new FileInputStream(pdfFile);
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                pdfOut.write(buffer, 0, bytesRead);
            }
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pdfFile != null)
                pdfFile.delete();

            try {
                pdfOut.flush();
                pdfOut.close();
            } catch (IOException e) {
            }
        }
    }

    private PdfPCell getCell(String label, String value) {
        Phrase phrase = new Phrase();
        phrase.add(new Chunk(label + "  ", FONT_DOMANDA));
        phrase.add(new Chunk(value != null ? value : "", FONT_RISPOSTA));
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBorder(0);
        return cell;
    }

    private Image getImage(String imgName) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        String relativeWebPath = "/images/";
        ServletContext servletContext =
            (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String absoluteDiskPath = servletContext.getRealPath(relativeWebPath);

        InputStream imgStream = new FileInputStream(absoluteDiskPath + "/" + imgName);
        byte[] buffer = new byte[512];
        for (int c; (c = imgStream.read(buffer)) > 0;) {
            baos.write(buffer, 0, c);
        }
        baos.close();
        imgStream.close();
        Image img = Image.getInstance(baos.toByteArray());
        img.scaleAbsolute(10, 10);
        return img;
    }

    /**
     * Restituisce una cella da aggiungere alla tabella.
     *
     * @param domanda
     * @param am
     * @param readOnly
     * @return
     */
    private PdfPCell getNode(DomandaQuestionario domanda, boolean readOnly, String tpscr) throws SQLException {
        PdfPCell cell = new PdfPCell();
        cell.setPaddingTop(12);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        cell.setBorder(0);

        List scelte = null;

        if (tpscr.equals("CA")) {
            RefCa_AppModule am =
                (RefCa_AppModule) BindingContext.getCurrent().findDataControl("RefCa_AppModuleDataControl").getDataProvider();
            if (DomandaQuestionario.SELEZIONE.equals(domanda.getTipo()) ||
                DomandaQuestionario.SELEZIONE_MULTIPLA.equals(domanda.getTipo())) {
                scelte = am.getScelte(domanda.getGruppo());
            }
        } else if (tpscr.equals("PF")) {
            //RefPf_AppModule am = (RefPf_AppModule) BindingContext.getCurrent().findDataControl("RefPf_AppModuleDataControl").getDataProvider();
            //if (DomandaQuestionario.SELEZIONE.equals(domanda.getTipo()) || DomandaQuestionario.SELEZIONE_MULTIPLA.equals(domanda.getTipo())) {
            //scelte = am.getScelte(domanda.getGruppo());
            //}
        }

        if (DomandaQuestionario.SELEZIONE.equals(domanda.getTipo())) {
            Paragraph paragraph =
                new Paragraph(new Chunk(formatPrompt(domanda.getTesto(), domanda.isObbligatorio()),
                                        FONT_TITOLO_SCELTA));
            paragraph.setLeading(12);

            for (Iterator i = scelte.iterator(); i.hasNext();) {
                paragraph.add(Chunk.NEWLINE);
                Scelta scelta = (Scelta) i.next();
                if (scelta.getValore().equals(domanda.getValore())) {
                    paragraph.add(new Chunk(imgChecked, 0, 0));
                } else {
                    paragraph.add(new Chunk(imgUnchecked, 0, 0));
                }
                paragraph.add(new Chunk(" " + scelta.getNome(), FONT_DOMANDA));
            }
            cell.addElement(paragraph);
        } else if (DomandaQuestionario.SELEZIONE_MULTIPLA.equals(domanda.getTipo())) {
            Paragraph paragraph =
                new Paragraph(new Chunk(formatPrompt(domanda.getTesto(), domanda.isObbligatorio()),
                                        FONT_TITOLO_SCELTA));
            paragraph.setLeading(12);

            for (Iterator i = scelte.iterator(); i.hasNext();) {
                paragraph.add(Chunk.NEWLINE);
                Scelta scelta = (Scelta) i.next();
                boolean valoreTrovato = false;
                for (Iterator iValore = domanda.getValori().iterator(); iValore.hasNext();) {
                    String valore = (String) iValore.next();
                    if (scelta.getValore().equals(valore)) {
                        valoreTrovato = true;
                    }
                }
                paragraph.add(new Chunk(valoreTrovato ? imgChecked : imgUnchecked, 0, 0));
                paragraph.add(new Chunk(" " + scelta.getNome(), FONT_DOMANDA));
            }
            cell.addElement(paragraph);
        } else if (DomandaQuestionario.DATA.equals(domanda.getTipo())) {
            // Testo della domanda
            Phrase phrase =
                new Phrase(new Chunk(formatPrompt(domanda.getTesto(), domanda.isObbligatorio()) + " ", FONT_DOMANDA));

            if (domanda.getValore() != null) {
                phrase.add(new Chunk(domanda.getValore()));
            } else {
                phrase.add(new Chunk("___ / ___ / ______", FONT_SPAZIO));
            }
            cell.addElement(phrase);
            //              } else if (DomandaQuestionario.TESTO.equals(domanda.getTipo())) {
        } else {
            // Testo della domanda
            Phrase phrase =
                new Phrase(new Chunk(formatPrompt(domanda.getTesto(), domanda.isObbligatorio()) + " ", FONT_DOMANDA));

            if (domanda.getValore() != null) {
                phrase.add(new Chunk(domanda.getValore(), FONT_RISPOSTA));
            } else {
                phrase.add(new Chunk("_____", FONT_SPAZIO));
            }
            cell.addElement(phrase);
        }

        // Note
        if (domanda.getNote() != null) {
            cell.addElement(new Paragraph("(" + domanda.getNote() + ")", FONT_NOTE));
        }
        return cell;
    }


    private String formatPrompt(String text, boolean required) {
        if (required) {
            return "* " + text;
        }
        return text;
    }

}
