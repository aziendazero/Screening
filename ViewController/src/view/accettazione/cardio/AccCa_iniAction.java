package view.accettazione.cardio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;

import model.AccCa_AppModule;

import model.accettazione.AccCa_RicInvitiViewRow;

import model.accettazione.AccCa_RicInvitiViewRowImpl;
import model.accettazione.AccMa_RicInvitiViewRowImpl;

import model.common.AccMa_AppModule;
import model.common.Sogg_AppModule;

import model.commons.ViewHelper;

import model.datacontrol.AccCa_RicParam;

import model.datacontrol.Sogg_RicParam;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.UIXTable;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.adf.view.rich.component.rich.data.RichTable;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.model.RowKeySetImpl;

import view.commons.AccUtils;

import view.commons.action.Parent_DataForwardAction;

import view.round.Round_invitiAction;

import view.util.Utility;

public class AccCa_iniAction extends Parent_DataForwardAction {
    protected void setAppModule() {
        this.amName = "AccCa_AppModule";
    }
    private AccCa_ricercaAction accCa_ricercaAction;

    public void setAccCa_ricercaAction(AccCa_ricercaAction accCa_ricercaAction) {
        this.accCa_ricercaAction = accCa_ricercaAction;
    }

    public AccCa_ricercaAction getAccCa_ricercaAction() {
        return accCa_ricercaAction;
    }

    public AccCa_iniAction() {
        super();
    }
}
