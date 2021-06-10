package view.providers;

import java.text.DateFormat;
import java.text.ParseException;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;

import javax.faces.convert.DateTimeConverter;
import javax.faces.convert.NumberConverter;
import javax.faces.validator.DoubleRangeValidator;

import model.RefCa_AppModule;

import model.commons.DomandaQuestionario;
import model.commons.Questionario;
import model.commons.Scelta;
import model.commons.ViewHelper;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCDataControl;
import oracle.adf.model.servlet.HttpBindingContext;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.input.RichInputDate;
import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.component.rich.input.RichSelectItem;
import oracle.adf.view.rich.component.rich.input.RichSelectManyCheckbox;
import oracle.adf.view.rich.component.rich.input.RichSelectOneChoice;
import oracle.adf.view.rich.component.rich.layout.RichPanelGroupLayout;

import oracle.adf.view.rich.component.rich.output.RichOutputText;
import oracle.adf.view.rich.component.rich.output.RichSeparator;

import oracle.jbo.ApplicationModule;

import org.apache.myfaces.trinidad.context.RequestContext;

public class QuestionarioProvider {
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private final static DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    public QuestionarioProvider() {
    }

    public RichPanelGroupLayout createNode(RichPanelGroupLayout node) {
        ADFContext ctx = ADFContext.getCurrent();
        BindingContext bc = HttpBindingContext.getCurrent();
        String tpscr = (String) ctx.getSessionScope().get("scr");

        String amName =
            (String) ViewHelper.decodeByTpscr(tpscr, "Ref_AppModuleDataControl", "RefCo_AppModuleDataControl",
                                              "RefMa_AppModuleDataControl", "RefCa_AppModuleDataControl",
                                              "RefPf_AppModuleDataControl");
        DCDataControl dc = bc.findDataControl(amName);
        ApplicationModule am = (ApplicationModule) dc.getDataProvider();
        //Questionario questionario = am.getQuestionario();
        Questionario questionario = (Questionario) bc.findDataControl("QuestionarioDataControl").getDataProvider();

        String leftStyle = "text-align: left;";

        RichPanelGroupLayout containerNode = node;
        containerNode.setInlineStyle(leftStyle);

        String sezionePrec = "";
        List domande1liv = questionario.getDomande1liv();
        RichPanelGroupLayout tableNode = null;


        for (int i1liv = 0; i1liv < domande1liv.size(); i1liv++) {
            DomandaQuestionario domanda = (DomandaQuestionario) domande1liv.get(i1liv);
            if ("TITOLO".equals(domanda.getTipo())) {

                // Metto un separatore orizzontale prima del testo
                containerNode.getChildren().add(new RichSeparator());

                RichOutputText bean = new RichOutputText();
                bean.setValue(domanda.getTesto());
                bean.setStyleClass("Titolo2");
                containerNode.getChildren().add(bean);
            } else {

                // Nuova sezione
                if (!sezionePrec.equals(domanda.getSezione())) {
                    String sezione = domanda.getSezione() == null ? "" : domanda.getSezione();

                    tableNode = new RichPanelGroupLayout();
                    tableNode.setLayout(RichPanelGroupLayout.LAYOUT_VERTICAL);
                    containerNode.getChildren().add(tableNode);

                    sezionePrec = sezione;
                }

                RichPanelGroupLayout rowNode = new RichPanelGroupLayout();
                rowNode.setLayout(RichPanelGroupLayout.LAYOUT_HORIZONTAL);
                rowNode.setValign(RichPanelGroupLayout.VALIGN_TOP);
                tableNode.getChildren().add(rowNode);
                tableNode.setStyleClass("padding8");
                rowNode.getChildren().add(getNode(domanda, tpscr, am, questionario.isReadOnly()));
                if (domanda.getDomande1liv() != null) {
                    for (Iterator i2 = domanda.getDomande1liv().iterator(); i2.hasNext();) {
                        domanda = (DomandaQuestionario) i2.next();
                        rowNode.getChildren().add(getNode(domanda, tpscr, am, questionario.isReadOnly()));
                    }
                }
            }
        }
        containerNode.setLayout(RichPanelGroupLayout.LAYOUT_SCROLL);
        return containerNode;
    }

    /*
	 * I campi obbligatori lo sono solo al fine della chiusura del referto,
	 * quindi non imposto l'attributo "required" altrimenti non si riesce
	 * a salvare il referto incompleto.
	 * Di conseguenza devo impostare manualmente l'asterisco che indica
	 * l'obbilgatorieta' del campo.
	 */
    static private UIComponent getNode(DomandaQuestionario domanda, String tpscr, ApplicationModule am,
                                       boolean readOnly) {
        UIComponent bean = null;
        String name = domanda.getSezione() + "_" + domanda.getId();

        String cellStyle = "margin-right: 12px;";

        if (DomandaQuestionario.NUMERO.equals(domanda.getTipo()) ||
            DomandaQuestionario.INTERO.equals(domanda.getTipo())) {
            RichInputText numBean = new RichInputText();
            numBean.setId(name);
            numBean.setValue(domanda.getValore());

            numBean.setLabel(formatPrompt(domanda.getTesto(), domanda.isObbligatorio()));
            numBean.setColumns(domanda.getDimensione());


            numBean.setDisabled(!domanda.isModificabile() || readOnly);

            // Aggiungo il validatore solo se il campo e' modificabile
            if (domanda.isModificabile() && !readOnly) {
                NumberConverter c = new NumberConverter();  
                c.setGroupingUsed(false);
                c.setMaxIntegerDigits(domanda.getDimensione()); //setMaxPrecision
                if (DomandaQuestionario.NUMERO.equals(domanda.getTipo())){
                    c.setMaxFractionDigits(3); // setMaxScale
                } else {
                    c.setMaxFractionDigits(0); // setMaxScale                  
                }
                numBean.setConverter(c);
                
                DoubleRangeValidator validater = new DoubleRangeValidator();
                numBean.setAutoSubmit(true);

                if (domanda.getMaxValue() != null) {
                    double max = Double.parseDouble(domanda.getMaxValue());
                    validater.setMaximum(max);
                }
                if (domanda.getMinValue() != null) {
                    double min = Double.parseDouble(domanda.getMinValue());
                    validater.setMinimum(min);
                }
                numBean.addValidator(validater);
            }

            numBean.setShortDesc(domanda.getNote());
            numBean.setInlineStyle(cellStyle);
            bean = numBean;
        } else if (DomandaQuestionario.SELEZIONE.equals(domanda.getTipo())) {
            RichSelectOneChoice choiceBean = new RichSelectOneChoice();
            choiceBean.setId(name);
            choiceBean.setLabel(formatPrompt(domanda.getTesto(), domanda.isObbligatorio()));
            // Aggiungo sempre il valore vuoto
            RichSelectItem _si = new RichSelectItem();
            _si.setValue("");
            _si.setLabel("");
            choiceBean.getChildren().add(_si);

            List scelte = null;
            if ("CA".equals(tpscr))
                scelte = ((RefCa_AppModule) am).getScelte(domanda.getGruppo());

            for (Iterator i = scelte.iterator(); i.hasNext();) {
                RichSelectItem si = new RichSelectItem();
                Scelta scelta = (Scelta) i.next();
                si.setValue(scelta.getValore());
                si.setLabel(scelta.getNome());

                choiceBean.getChildren().add(si);
            }
            choiceBean.setDisabled(!domanda.isModificabile() || readOnly);
            choiceBean.setValue(domanda.getValore());

            choiceBean.setShortDesc(domanda.getNote());
            choiceBean.setInlineStyle(cellStyle);
            bean = choiceBean;
        } else if (DomandaQuestionario.SELEZIONE_MULTIPLA.equals(domanda.getTipo())) {
            RichPanelGroupLayout selBean = new RichPanelGroupLayout();

            // Titolo della selezione
            /*String boldStyle = "font-weight: bold";
            RichInputText textBean = new RichInputText();
            textBean.setReadOnly(true);
            textBean.setLabel(domanda.getTesto());
            textBean.setInlineStyle(boldStyle);
            selBean.getChildren().add(textBean);
            */
            RichSelectManyCheckbox checkBean = new RichSelectManyCheckbox();
            checkBean.setId(name);
            checkBean.setLabel(formatPrompt(domanda.getTesto(), domanda.isObbligatorio()));

            List scelte = null;
            List<Object> l = new ArrayList<Object>();
            if ("CA".equals(tpscr))
                scelte = ((RefCa_AppModule) am).getScelte(domanda.getGruppo());

            for (Iterator i = scelte.iterator(); i.hasNext();) {
                Scelta scelta = (Scelta) i.next();

                RichSelectItem si = new RichSelectItem();
                si.setValue(scelta.getValore());
                si.setLabel(scelta.getNome());

                checkBean.getChildren().add(si);

                checkBean.setDisabled(!domanda.isModificabile() || readOnly);

                // Cerco tra i valori della domanda se c'e' quello corrispondente a questo elemento
                for (Iterator i2 = domanda.getValori().iterator(); i2.hasNext();) {
                    String valore = (String) i2.next();
                    if (scelta.getValore().equals(valore)) {
                        l.add(valore);
                        break;
                    }
                }

            }
            checkBean.setValue(l);
            checkBean.setAutoSubmit(true);
            selBean.getChildren().add(checkBean);

            selBean.setShortDesc(domanda.getNote());
            selBean.setInlineStyle(cellStyle);
            bean = selBean;
        } else if (DomandaQuestionario.DATA.equals(domanda.getTipo())) {
            RichInputDate dateBean = new RichInputDate();
            dateBean.setId(name);
            dateBean.setLabel(domanda.getTesto());
            if (domanda.getValore() != null) {
                try {
                    dateBean.setValue(dateFormat.parse(domanda.getValore()));
                } catch (ParseException e) {
                    // Se data non valida, non imposto il valore
                    e.printStackTrace();
                }
            }

            dateBean.setDisabled(!domanda.isModificabile() || readOnly);
            dateBean.setRequired(domanda.isObbligatorio());

            DateTimeConverter c = new DateTimeConverter();
            c.setPattern(DATE_FORMAT);

            dateBean.setConverter(c);
            dateBean.setAutoSubmit(true);
            dateBean.setShortDesc(domanda.getNote());
            dateBean.setInlineStyle(cellStyle);
            bean = dateBean;
        } else {
            RichInputText textBean = new RichInputText();
            textBean.setId(name);
            textBean.setValue(domanda.getValore());
            textBean.setLabel(formatPrompt(domanda.getTesto(), domanda.isObbligatorio()));
            if (domanda.getDimensione() > 0) {
                int colonne = domanda.getDimensione();
                int righe = 1;
                if (colonne > 120) {
                    righe = (colonne / 120 + 1);
                    colonne = 120;
                }
                textBean.setColumns(colonne);
                textBean.setRows(righe);
            }
            textBean.setDisabled(!domanda.isModificabile() || readOnly);
            textBean.setShortDesc(domanda.getNote());
            textBean.setInlineStyle(cellStyle);
            bean = textBean;
        }

        return bean;
    }

    /**
     * Formatta l'etichetta aggiungendo in testa l'asterisco se il campo e' obbligatorio.
     * @return
     * @param text Etichetta.
     * @param required Indica se il campo e' obbligatorio.
     */
    static private String formatPrompt(String text, boolean required) {
        if (required) {
            return "* " + text;
        }
        return text;
    }
}
