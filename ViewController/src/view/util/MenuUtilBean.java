package view.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

import java.util.Objects;

import javax.el.ELContext;
import javax.el.ELResolver;

import javax.faces.context.FacesContext;

import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.nav.RichNavigationPane;
import oracle.adf.view.rich.model.MDSMenuModel;

import org.apache.myfaces.trinidad.model.TreeModel;

import view.commons.action.Parent_DataForwardAction;

public class MenuUtilBean {
    public MenuUtilBean() {
    }

    @SuppressWarnings("unchecked")
    private List findNodeByPropertyValue(TreeModel tree, Object myVal, String myProp) {
        FacesContext context = FacesContext.getCurrentInstance();
        ELResolver resolver = context.getApplication().getELResolver();
        ELContext initialELContext = context.getELContext();

        List nodeList = new ArrayList<Object>();

        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.setRowIndex(i);

            Object node = tree.getRowData();
            Object propVal = resolver.getValue(initialELContext, node, myProp);

            if (propVal.equals(myVal)) {
                nodeList.add(node);
            }

            if (tree.isContainer() && !tree.isContainerEmpty()) {
                tree.enterContainer();
                nodeList.addAll(findNodeByPropertyValue(tree, myVal, myProp));
                tree.exitContainer();
            }
        }
        return nodeList;
    }

    public List getMenuItems(MDSMenuModel menu) {
        List _ret = new ArrayList();

        MDSMenuModel m = menu != null ? menu : null;

        Integer focusId =
            (m != null && m.getFocusRowKey() != null && !((ArrayList) m.getFocusRowKey()).isEmpty()) ?
            (Integer) ((ArrayList) m.getFocusRowKey()).get(0) : null;

        if (focusId != null) {
            TreeModel treeModel = (TreeModel) m.getWrappedData();
            treeModel.setRowIndex(focusId);

            if (treeModel.isContainer() && !treeModel.isContainerEmpty()) {
                treeModel.enterContainer();
                _ret = findNodeByPropertyValue(treeModel, true, "rendered");
                treeModel.exitContainer();
            }
        }

        return _ret;
    }
    
    public List<Object> getMenuItemsRenderedSize(MDSMenuModel menu) {
        List _ret = new ArrayList();

        MDSMenuModel m = menu != null ? menu : null;
        if(m!=null){
            RichNavigationPane rp = new RichNavigationPane();
            rp.setValue(m);
            rp.setLevel(0);
            
            FacesContext context = FacesContext.getCurrentInstance();
            ELResolver resolver = context.getApplication().getELResolver();
            ELContext initialELContext = context.getELContext();

            for(int i=0; i<rp.getRowCount(); i++){
                Object node = rp.getRowData(i);
                Object propVal = resolver.getValue(initialELContext, node, "rendered");
    
                if (propVal.equals(true)) 
                    _ret.add(node);
            }
        }
        return _ret;
    }    

    public int getMenuItemsSize(MDSMenuModel menu) {
        try {
            return getMenuItems(menu).size();

        } catch (Exception s){
            return 0;
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Parent_DataForwardAction> String doAction(String baseDestination, T parent_DataForwardAction,
                                                                String params) throws Exception {
        String ret = baseDestination;
        HashMap param = paramsInReq(params);
        if (parent_DataForwardAction != null) {
            ret = parent_DataForwardAction.onRichnav(baseDestination, param);
        } else {
            Map req = ADFContext.getCurrent().getRequestScope();
            req.putAll(param);
        }

        System.out.println("----------------------------------> doAction: " + ret);

        return ret;
    }

    @SuppressWarnings("unchecked")
    private HashMap  paramsInReq(String params){
        HashMap param = new HashMap();
        if (params != null && !params.isEmpty())
            param.putAll(splitQuery(params));
        
        return param;
    }

    public static Map<String, String> splitQuery(String param) {
        @SuppressWarnings("unchecked")
        Map<String, String> kvs =
            Arrays.asList(param.split("&")).stream().map(elem -> elem.split("=")).filter(elem -> elem.length >
                                                                                         0).collect(HashMap::new,
                                                                                                    (m, e) -> m.put(e[0],
                                                                                                                    e.length >
                                                                                                                    1 ?
                                                                                                                    e[1] :
                                                                                                                    null),
                                                                                                    HashMap::putAll);

        kvs.entrySet().removeIf(entry -> Objects.isNull(entry.getKey()) || entry.getKey().isEmpty());
        return kvs;
    }

    public String doActionEvent(String baseDestination, String eventEvaluation) throws Exception {
        String ret = baseDestination;
        if (eventEvaluation != null && !eventEvaluation.isEmpty()) {
            Object _o = Utility.resolveExpression(eventEvaluation);
            ret = _o == null ? null : _o.toString();
        }
        return ret;
    }
}
