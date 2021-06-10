package view.conf;

import java.util.Arrays;
import java.util.Map;

import javax.faces.event.ActionEvent;

import javax.faces.event.ValueChangeEvent;

import model.accettazione.AccMa_RicInvitiViewRowImpl;

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

public class Cnf_sugg1livAction extends Parent_DataForwardAction {
    private RichForm suggForm;
    private RichTable tabSugg1;

    private RichSelectOneChoice selectedUlss;

    @Override
    protected void setAppModule() {
        this.amName = "Cnf_AppModule";
    }

    public Cnf_sugg1livAction() {
    }

    public void setSuggForm(RichForm suggForm) {
        this.suggForm = suggForm;
    }

    public RichForm getSuggForm() {
        if (suggForm == null) {
            findForward();
        }
        return suggForm;
    }

    public void setTabSugg1(RichTable tabSugg1) {
        this.tabSugg1 = tabSugg1;
    }

    public RichTable getTabSugg1() {
        return tabSugg1;
    }

    protected void findForward() {
        Map session = ADFContext.getCurrent().getSessionScope();
        Key navigDaDett = (Key) session.get("navSugg1Dett");

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfSugg1livView1Iterator");
        if (navigDaDett != null) {
            if (!navigDaDett.isAnyNull()) {
                ViewObject vo = voIter.getViewObject();
                Row[] r = vo.findByKey(navigDaDett, 1);
                if (r != null && r.length>0)
                    vo.setCurrentRow(r[0]);

                ViewHelper.queryAndRestoreCurrentRow(vo);

                if (this.tabSugg1 == null)
                    this.tabSugg1 = new RichTable();

                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabSugg1.setSelectedRowKeys(selected);
                }

                Utility.gotoTablePageOfSelectedRow(voIter, this.tabSugg1);
            }
        } else
            ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedSugg1", "Tutte");
    }

    public void onDett(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfSugg1livView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();

        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("navSugg1Dett", rowKey);
    }

    public void onChange(ValueChangeEvent valueChangeEvent) {
        RichSelectOneChoice selected = this.getSelectedUlss();
        String ulss = "";

        if (selected != null && selected.getValue() != null)
            ulss = selected.getValue().toString();

        ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedSugg1", selected.getValue());

        if (ulss != "") {
            Map session = ADFContext.getCurrent().getSessionScope();
            String tpscr = (String) session.get("scr");
            
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfSugg1livView1Iterator");
            ViewObject vo = voIter.getViewObject();

            if ("Tutte".equals(ulss))
                vo.setWhereClause("Cnf_SoCnfSugg1liv.TPSCR='" + tpscr + "'");
            else
                vo.setWhereClause("Cnf_SoCnfSugg1liv.TPSCR='" + tpscr + "' AND Cnf_SoCnfSugg1liv.ULSS = '" + ulss + "'");

            vo.executeQuery();
        }
    }

    public void setSelectedUlss(RichSelectOneChoice selectedUlss) {
        this.selectedUlss = selectedUlss;
    }

    public RichSelectOneChoice getSelectedUlss() {
        return selectedUlss;
    }
}
