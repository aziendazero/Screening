package view.soggetto;


import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;

import javax.faces.context.FacesContext;

import javax.faces.event.ActionEvent;

import model.common.RefCo_AppModule;
import model.common.Sogg_AppModule;

import model.commons.SoggUtils;

import model.commons.ViewHelper;

import model.inviti.GeneratoreInviti;
import model.inviti.InvitoUtils;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;
import oracle.adf.view.rich.component.rich.RichPopup;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;
import oracle.jbo.Transaction;

import org.apache.myfaces.trinidad.context.RequestContext;

import view.commons.action.Parent_DataForwardAction;

public class Sogg_storiaAction extends Parent_DataForwardAction {

    private RichPopup restorePop;
    
    @Override
    protected void setAppModule() {
        this.amName = "Sogg_AppModule";
    }

    protected void beforeNavigate(String dest) throws Exception {
        SoggUtils.beforeNavSogg(dest, false);
    }
    
    public void setRestorePop(RichPopup restorePop) {
        this.restorePop = restorePop;
    }

    public RichPopup getRestorePop() {
        return restorePop;
    }
    
    public void restorePopClose() {
        if (restorePop != null) {
            restorePop.hide();
        }
    }
    
    public void restore() {
        restore(false);
    }

    public void restoreAndActivate() {
        restore(true);
    }

    public void restore(boolean activate) {
        ApplicationModule am = null;
        try{
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voDeletedIter = bindings.findIteratorBinding("Sogg_StInvitiBckView1Iterator");
            ViewObject voDeleted = voDeletedIter.getViewObject();
            Row rDeleted = voDeleted.getCurrentRow();
            am = voDeleted.getApplicationModule();
            
            ADFContext context = ADFContext.getCurrent();
            Map sess = context.getSessionScope();

            String user = (String)sess.get("user");
            String tpscr = (String)sess.get("scr");
            String ulss = (String)sess.get("ulss");
            String codTs = (String)rDeleted.getAttribute("Codts");
            
            Integer idToRestore = (Integer) context.getRequestScope().get("ripristina_invito");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String nuovaNota = (String)rDeleted.getAttribute("Noteinvito");
            if (nuovaNota == null || nuovaNota.trim().isEmpty()) {
                nuovaNota = "";
            } else {
                nuovaNota += "\n\n";
            }
            nuovaNota += "Invito cancellato il " + 
                         sdf.format(Date.class.cast(rDeleted.getAttribute("Dtcanc")).getValue()) +
                         " da " + rDeleted.getAttribute("Opcanc") +
                         " e ripristinato il " + sdf.format(new java.util.Date()) +
                         " da " + user;
            
            Integer idAttivo = null;
            if (activate) {
                idAttivo = InvitoUtils.getInvitoAttivo((Sogg_AppModule)am, codTs, idToRestore, ulss, tpscr);
            }

            InvitoUtils.ripristinaInvito(am, idToRestore, codTs, ulss, tpscr, user, nuovaNota, activate, idAttivo);

            // REIMPOSTAZIONE DOPO IL RIPRISTINO DELL'INVITO
            ViewObject voAnag = am.findViewObject("Sogg_RicercaView1");
            voAnag.setWhereClauseParams(new Object[] { ulss, tpscr, ulss, tpscr, tpscr });
            String whereCodeTs = " AND CODTS = '" + codTs + "'";
            String whereAnag = voAnag.getWhereClause();
            voAnag.setWhereClause(whereAnag + whereCodeTs);
            voAnag.executeQuery();
            voAnag.first();
            // R FILTRI DI RICERCA ORIGINALI
            voAnag.setWhereClause(whereAnag);
            
            Sogg_ricercaAction.checkSelected();
            Sogg_ricercaAction.navDett();
            
            // REFRESH DELLA LISTA STORICO INVITI
            ViewObject voStInviti = am.findViewObject("Sogg_StInvitiView1");
            voStInviti.executeQuery();
            boolean hasInv = voStInviti.getEstimatedRowCount() > 0;

            // REFRESH DELLA LISTA INVITI CANCELLATI
            voDeleted.executeQuery();
            //long nInvBck = voDeleted.getEstimatedRowCount();
            sess.put("forceActive", !hasInv /*&& nInvBck == 1*/);

        } catch (Exception e) {
            if (am != null) {
                am.getTransaction().rollback();
            }
            handleException(e.getMessage());
        } finally {
            restorePopClose();
            
            RequestContext requestContext = RequestContext.getCurrentInstance();
            if (requestContext != null) {
                RichForm form = (RichForm)findComponent("f1");
                requestContext.addPartialTarget(form);
            }
        }
    }


    /**
     * Locate an UIComponent in view root with its component id. Use a recursive way to achieve this.
     * #param id UIComponent id * #return UIComponent object
     */
    public UIComponent findComponent(String id) {
        UIComponent component = null;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            UIComponent root = facesContext.getViewRoot();
            component = findComponent(root, id);
        }
        return component;
    }

    /** * Locate an UIComponent from its root component.
     * #param base root Component (parent)
     * #param id UIComponent id
     * #return UIComponent object
     */
    public UIComponent findComponent(UIComponent base, String id) {
        if (id.equals(base.getId()))
            return base;
        UIComponent child = null;
        UIComponent result = null;
        Iterator children = base.getFacetsAndChildren();
        while (children.hasNext() && (result == null)) {
            child = (UIComponent) children.next();
            if (id.equals(child.getId())) {
                result = child;
                break;
            }
            result = findComponent(child, id);
            if (result != null) {
                break;
            }
        }
        return result;
    }


    @SuppressWarnings("oracle.jdeveloper.java.unused-parameter")
    public void onSelectActive(ActionEvent actionEvent) {
        Transaction t = null;
        try{
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voInvitoIter = bindings.findIteratorBinding("Sogg_StInvitiView1Iterator");
            
            Integer[] invitoAttivo = {null};
            if(voInvitoIter!=null && voInvitoIter.getViewObject()!=null){
                RowSetIterator iter = null;
                try{
                    iter = ViewHelper.getRowSetIterator(voInvitoIter.getViewObject(), "find_iter");
                    iter.setRangeSize(-1);
                    
                    // recupero quello attivo
                    Row rattivo = Arrays.asList(iter.getAllRowsInRange()).stream().filter(rinv ->
                        (((Integer)rinv.getAttribute("Attivo"))== 1))
                    .findFirst()
                    .orElse(null);
                    
                    if(rattivo!=null)
                        invitoAttivo[0] = (Integer)rattivo.getAttribute("Idinvito");
                }finally {
                    if (iter != null)
                        iter.closeRowSetIterator();
                }
    
                // recupero quello selezionato che si vuole rendere attivo
                Row rInvitoSel = voInvitoIter.getViewObject().getCurrentRow();
                
                Integer[] invitoAttivoNew = {null};;
                if(rInvitoSel!=null)
                    invitoAttivoNew[0] = (Integer) rInvitoSel.getAttribute("Idinvito");

                // Recupero gli inviti
                ViewObject invitoView =  voInvitoIter.getViewObject().getApplicationModule().findViewObject("Sogg_SoInvitoView1");
                String _in = Arrays.asList(invitoAttivo[0],invitoAttivoNew[0]).stream().filter(i->i!=null).map(String::valueOf).collect(Collectors.joining(","));
                invitoView.setWhereClause("IDINVITO IN (" + _in + ")");
                invitoView.setRangeSize(-1);
                invitoView.executeQuery();

                // recupero quello attivo
                Iterator it = Arrays.asList(invitoView.getAllRowsInRange()).iterator();
                while(it.hasNext()){
                    Row _rinv = (Row) it.next();
                    
                    boolean isOk = false;
                    if(invitoAttivo[0]!=null && ((Integer)_rinv.getAttribute("Idinvito")).intValue() == invitoAttivo[0].intValue()){
                         _rinv.setAttribute("Attivo", 0);         
                        isOk = true;
                    }else if(invitoAttivoNew[0]!=null && ((Integer)_rinv.getAttribute("Idinvito")).intValue() == invitoAttivoNew[0].intValue()){
                        _rinv.setAttribute("Attivo", 1); 
                        isOk = true;
                    }

                    if(isOk){
                        GeneratoreInviti gen =
                            new GeneratoreInviti(voInvitoIter.getViewObject().getApplicationModule(), (String) _rinv.getAttribute("Codts"), (String) _rinv.getAttribute("Ulss"),
                                                 (String) _rinv.getAttribute("Tpscr"));
                        gen.updateRoundIndiv(_rinv);
                        gen.updateAndGetRoundInviti(_rinv);
                    }
                }

                if(invitoAttivoNew[0]!=null || invitoAttivo[0]!=null){
                    t = voInvitoIter.getViewObject().getApplicationModule().getTransaction();
                    t.commit();
                }
                
                if(invitoAttivoNew[0]!=null){
                    invitoView.setWhereClause("IDINVITO = " + invitoAttivoNew[0]);
                    invitoView.setRangeSize(1);
                    invitoView.executeQuery();
                    invitoView.setCurrentRow(invitoView.first());
                }
                
                //H00356407 devo riverificare la presenza del referto
                ApplicationModule am = voInvitoIter.getViewObject().getApplicationModule();
                Map session = ADFContext.getCurrent().getSessionScope();
                String tpscr = (String)session.get("scr");
                    
                boolean refPresente = InvitoUtils.invitoHaReferto(am, invitoAttivoNew[0], tpscr);
                session.put("refPresente", Boolean.valueOf(refPresente));
                
                voInvitoIter.getViewObject().executeQuery();
            }else
                this.handleMessages(FacesMessage.SEVERITY_ERROR, "Sogg_storiaAction method onSelectActive error!");
        }catch(Throwable th){
            this.handleException("Sogg_storiaAction method onSelectActive error: " + th.getMessage(),
                                 t);
        }
    }
}
