package view.util;

import insiel.dataHandling.DateUtils;

import java.util.Date;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.PopupCanceledEvent;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.context.RequestContext;
import org.apache.myfaces.trinidad.event.RangeChangeEvent;
import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.render.ExtendedRenderKitService;
import org.apache.myfaces.trinidad.util.Service;

public class Utility {
    private RichInputText globalMessageField;

    public Utility() {
        super();
    }

    public static Object invokeEL(String el, Class[] paramTypes, Object[] params) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext elContext = facesContext.getELContext();
        ExpressionFactory expressionFactory = facesContext.getApplication().getExpressionFactory();
        MethodExpression exp = expressionFactory.createMethodExpression(elContext, el, Object.class, paramTypes);

        return exp.invoke(elContext, params);
    }

    public static Object resolveExpression(String expression) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Application app = facesContext.getApplication();
        ExpressionFactory elFactory = app.getExpressionFactory();
        ELContext elContext = facesContext.getELContext();
        ValueExpression valueExp = elFactory.createValueExpression(elContext, expression, Object.class);
        return valueExp.getValue(elContext);
    }

    public static void addScriptOnPartialRequest(String script) {
        FacesContext context = FacesContext.getCurrentInstance();
        ExtendedRenderKitService erks = Service.getRenderKitService(context, ExtendedRenderKitService.class);
        erks.addScript(context, script);
    }

    public void cancelGlobalMessage(PopupCanceledEvent popupCanceledEvent) {
        Map session = ADFContext.getCurrent().getSessionScope();
        session.remove("Globals_MESSAGE_KEY");
        RequestContext.getCurrentInstance().addPartialTarget(globalMessageField);
    }

    public void setGlobalMessageField(RichInputText globalMessageField) {
        this.globalMessageField = globalMessageField;
    }

    public RichInputText getGlobalMessageField() {
        return globalMessageField;
    }

    public String selectAllCheckboxValueChange(String voIteratorName, String rowSelectFlagAttribute, boolean selected) {
        DCBindingContainer bindingContainer =
            (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding dciter = bindingContainer.findIteratorBinding(voIteratorName);
        ViewObject vo = dciter.getViewObject();
        Row row = null;
        vo.reset();
        RowSetIterator rs = vo.createRowSetIterator(null);
        rs.reset();
        while (rs.hasNext()) {
            row = rs.next();
            if (selected)
                row.setAttribute(rowSelectFlagAttribute, true);
            else
                row.setAttribute(rowSelectFlagAttribute, false);
        }
        rs.closeRowSetIterator();
        return null;
    }

    public static void gotoTablePageOfSelectedRow(DCIteratorBinding voIter, RichTable table) {
        if(voIter!=null && table!=null){
            RowKeySet oldKeySet = table.getSelectedRowKeys();
            int range = voIter.getRangeSize();
            int oldStart = voIter.getRangeStart();
            int oldEnd = oldStart + range;
            int newPage = (voIter.getRowSetIterator().getCurrentRowIndex() / range);
            int newStart = newPage * range;
            int newEnd = newStart + range;
            
            table.broadcast(new RangeChangeEvent(table, oldStart, oldEnd, newStart, newEnd));
            table.setSelectedRowKeys(oldKeySet);
            AdfFacesContext.getCurrentInstance().addPartialTarget(table);
        }
    }

    public static String format(Date data) {
        if(data!=null)
            return DateUtils.dateToString(data);
        else
        return "";
    }
}
