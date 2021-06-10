package view.conf;

import java.util.Arrays;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import model.commons.ViewHelper;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.input.RichSelectOneChoice;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.model.RowKeySetImpl;

import view.commons.action.Parent_DataForwardAction;

import view.util.Utility;

public class Cnf_docsAction extends Parent_DataForwardAction {

    public Cnf_docsAction() {
    }

    @Override
    protected void setAppModule() {
        this.amName = "Cnf_AppModule";
    }

    private RichForm templateForm;
    private RichForm letteraForm;
    private RichForm tpLetteraForm;
    private RichForm insertoForm;
    private RichForm filtroForm;

    private RichTable tabTemplate;
    private RichTable tabLettera;
    private RichTable tabTpLettera;
    private RichTable tabInserto;
    private RichTable tabFiltro;

    private RichSelectOneChoice selectedLettera;
    private RichSelectOneChoice selectedInserti;
    private RichSelectOneChoice selectedTemplates;

    public void setLetteraForm(RichForm letteraForm) {
        this.letteraForm = letteraForm;
    }

    public RichForm getLetteraForm() {
        if (letteraForm == null) {
            findForward("lettera");
        }
        return letteraForm;
    }

    public void setTpLetteraForm(RichForm tpLetteraForm) {
        this.tpLetteraForm = tpLetteraForm;
    }

    public RichForm getTpLetteraForm() {
        if (tpLetteraForm == null) {
            findForward("tp_lettera");
        }
        return tpLetteraForm;
    }

    public void setInsertoForm(RichForm insertoForm) {
        this.insertoForm = insertoForm;
    }

    public RichForm getInsertoForm() {
        if (insertoForm == null) {
            findForward("inserto");
        }
        return insertoForm;
    }
    
    public void setFiltroForm(RichForm filtroForm) {
        this.filtroForm = filtroForm;
    }

    public RichForm getFiltroForm() {
        if (filtroForm == null) {
            findForward("filtro");
        }
        return filtroForm;
    }

    public void setTemplateForm(RichForm template) {
        this.templateForm = template;
    }

    public RichForm getTemplateForm() {
        if (templateForm == null) {
            findForward("template");
        }
        return templateForm;
    }

    protected void findForward(String type) {

        Map session = ADFContext.getCurrent().getSessionScope();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        Key navigDaDett = null;
        DCIteratorBinding voIter = null;
        if ("template".equals(type)) {
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoTemplateView1Iterator");
        } else if ("lettera".equals(type)) {
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoCnfLetteracentroExtendedView1Iterator");
        } else if ("tp_lettera".equals(type)) {
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoCnfTpletteraView1Iterator");
        } else if ("inserto".equals(type)) {
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoInsertoView1Iterator");
        } else if ("filtro".equals(type)) {
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoFiltriTemplateView1Iterator");
        } else
            return;

        if (navigDaDett != null) {
            ViewObject vo = voIter.getViewObject();
            Row[] r = vo.findByKey(navigDaDett, 1);
            if (r != null)
                vo.setCurrentRow(r[0]);

            ViewHelper.queryAndRestoreCurrentRow(vo);
            if ("template".equals(type)) {
                if (this.tabTemplate == null)
                    this.tabTemplate = new RichTable();

                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabTemplate.setSelectedRowKeys(selected);
                }

                Utility.gotoTablePageOfSelectedRow(voIter, this.tabTemplate);
            } else if ("lettera".equals(type)) {
                if (this.tabLettera == null)
                    this.tabLettera = new RichTable();

                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabLettera.setSelectedRowKeys(selected);
                }

                Utility.gotoTablePageOfSelectedRow(voIter, this.tabLettera);
            } else if ("tp_lettera".equals(type)) {
                if (this.tabTpLettera == null)
                    this.tabTpLettera = new RichTable();

                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabTpLettera.setSelectedRowKeys(selected);
                }

                Utility.gotoTablePageOfSelectedRow(voIter, this.tabTpLettera);
            } else if ("inserto".equals(type)) {
                if (this.tabInserto == null)
                    this.tabInserto = new RichTable();

                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabInserto.setSelectedRowKeys(selected);
                }

                Utility.gotoTablePageOfSelectedRow(voIter, this.tabInserto);
            } else if ("filtro".equals(type)) {
                if (this.tabFiltro == null)
                    this.tabFiltro = new RichTable();

                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabFiltro.setSelectedRowKeys(selected);
                }

                Utility.gotoTablePageOfSelectedRow(voIter, this.tabFiltro);
            }
        } else {
            if ("template".equals(type))
                ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedTemplates", "Tutte");

            else if ("lettera".equals(type))
                ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedLettere", "Tutte");

            else if ("inserto".equals(type))
                ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedInserti", "Tutte");
            
            else if ("filtro".equals(type)){
                ViewObject vo = voIter.getViewObject();
                vo.executeQuery();
            }
        }
    }

    public void setTabTemplate(RichTable tabTemplate) {
        this.tabTemplate = tabTemplate;
    }

    public RichTable getTabTemplate() {
        return tabTemplate;
    }

    public void setTabLettera(RichTable tabLettera) {
        this.tabLettera = tabLettera;
    }

    public RichTable getTabLettera() {
        return tabLettera;
    }

    public void setTabTpLettera(RichTable tabTpLettera) {
        this.tabTpLettera = tabTpLettera;
    }

    public RichTable getTabTpLettera() {
        return tabTpLettera;
    }

    public void setTabInserto(RichTable tabInserto) {
        this.tabInserto = tabInserto;
    }

    public RichTable getTabInserto() {
        return tabInserto;
    }

    //template lettera tp_lettera inserto
    public void onDettTempalte(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoTemplateView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();

        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("template", rowKey);
    }

    public void onDettLettera(ActionEvent actionEvent) {
        this.templatesFilter(actionEvent);

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfLetteracentroExtendedView1Iterator");
        ViewObject voLett = voIter.getViewObject();
        Row cLet = voLett.getCurrentRow();

        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cLet.getKey();
        session.put("lettera", rowKey);

        //Recuper l'object selezionato
        Object id = cLet.getAttribute("Idrow");
        if (id != null && (id instanceof Integer)) {
            int idrow = (Integer) id;

            DCIteratorBinding lett = bindings.findIteratorBinding("Cnf_SoCnfLetteracentroView2Iterator");
            ViewObject lettDet = lett.getViewObject();

            String where = "idrow = '" + idrow + "'";

            lettDet.setWhereClause(where);
            lettDet.executeQuery();
        }
    }

    public void onDettTpLettera(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfTpletteraView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();

        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("tp_lettera", rowKey);
    }

    public void onDettInserto(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoInsertoView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();

        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("inserto", rowKey);
    }
    
    public void onDettFiltro(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoFiltriTemplateView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();

        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("filtro", rowKey);
    }

    public void onChangeTemplates(ValueChangeEvent valueChangeEvent) {
        RichSelectOneChoice selected = this.getSelectedTemplates();
        String ulss = "";

        if (selected != null && selected.getValue() != null)
            ulss = selected.getValue().toString();

        ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedTemplates", selected.getValue());

        if (ulss != "") {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoTemplateView1Iterator");
            ViewObject vo = voIter.getViewObject();

            Map session = ADFContext.getCurrent().getSessionScope();
            String tpscr = (String) session.get("scr");

            if ("Tutte".equals(ulss))
                vo.setWhereClause("TPSCR='" + tpscr + "'");
            else
                vo.setWhereClause("TPSCR='" + tpscr + "' AND ULSS = '" + ulss + "'");

            vo.executeQuery();
        }
    }

    public void onChangeLettera(ValueChangeEvent valueChangeEvent) {
        RichSelectOneChoice selected = this.getSelectedLettera();
        String ulss = "";

        if (selected != null && selected.getValue() != null)
            ulss = selected.getValue().toString();

        ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedLettere", selected.getValue());

        if (ulss != "") {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfLetteracentroExtendedView1Iterator");
            ViewObject vo = voIter.getViewObject();

            Map session = ADFContext.getCurrent().getSessionScope();
            String tpscr = (String) session.get("scr");

            if ("Tutte".equals(ulss))
                vo.setWhereClause("Cnf_SoCnfLetteracentro.TPSCR='" + tpscr + "'");
            else
                vo.setWhereClause("Cnf_SoCnfLetteracentro.TPSCR='" + tpscr + "' AND Cnf_SoCnfLetteracentro.ULSS = '" +
                                  ulss + "'");

            vo.executeQuery();
        }
    }

    public void onChangeInserti(ValueChangeEvent valueChangeEvent) {
        RichSelectOneChoice selected = this.getSelectedInserti();
        String ulss = "";

        if (selected != null && selected.getValue() != null)
            ulss = selected.getValue().toString();

        ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedInserti", selected.getValue());

        if (ulss != "") {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoInsertoView1Iterator");
            ViewObject vo = voIter.getViewObject();

            Map session = ADFContext.getCurrent().getSessionScope();
            String tpscr = (String) session.get("scr");

            if ("Tutte".equals(ulss))
                vo.setWhereClause("TPSCR='" + tpscr + "'");
            else
                vo.setWhereClause("TPSCR='" + tpscr + "' AND ULSS = '" + ulss + "'");

            vo.executeQuery();
        }
    }

    public void setSelectedTemplates(RichSelectOneChoice selectedTemplates) {
        this.selectedTemplates = selectedTemplates;
    }

    public RichSelectOneChoice getSelectedTemplates() {
        return selectedTemplates;
    }

    public void setSelectedLettera(RichSelectOneChoice selectedLettera) {
        this.selectedLettera = selectedLettera;
    }

    public RichSelectOneChoice getSelectedLettera() {
        return selectedLettera;
    }

    public void setSelectedInserti(RichSelectOneChoice selectedInserti) {
        this.selectedInserti = selectedInserti;
    }

    public RichSelectOneChoice getSelectedInserti() {
        return selectedInserti;
    }

    public void templatesFilter(ActionEvent actionEvent) {
        Map session = ADFContext.getCurrent().getSessionScope();

        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");
        Boolean regionale = (Boolean) session.get("regionale");

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        if (regionale != null && regionale.booleanValue()) {
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfLetteracentroExtendedView1Iterator");
            Row _r = voIter.getViewObject().getCurrentRow();

            if (_r != null && _r.getAttribute("Ulss") != null)
                ulss = (String) _r.getAttribute("Ulss");

        }

        if (ulss != "" && tpscr != "") {
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoTemplateView2Iterator");
            ViewObject vo = voIter.getViewObject();

            String where = vo.getWhereClause();
            //if (where == null || "".equals(where)) {
                vo.setWhereClause("Cnf_SoTemplate.TPSCR = '" + tpscr + "' AND Cnf_SoTemplate.ULSS = '" + ulss + "'");
                //vo.setWhereClause("Cnf_SoTemplate.TPSCR = '" + tpscr + "'");
                vo.executeQuery();
            //}
        }
    }

    public void onChangeUlss(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null) {
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

            Map session = ADFContext.getCurrent().getSessionScope();

            String tpscr = (String) session.get("scr");

            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoTemplateView2Iterator");
            ViewObject vo = voIter.getViewObject();

            String where = vo.getWhereClause();
            //if (where == null || "".equals(where)) {
                vo.setWhereClause("Cnf_SoTemplate.TPSCR = '" + tpscr + "' AND Cnf_SoTemplate.ULSS = '" + valueChangeEvent.getNewValue() + "'");
                vo.executeQuery();
            //}
        }
    }

    public void setTabFiltro(RichTable tabFiltro) {
        this.tabFiltro = tabFiltro;
    }

    public RichTable getTabFiltro() {
        return tabFiltro;
    }
}
