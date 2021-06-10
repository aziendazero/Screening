package view.conf;

import javax.faces.application.FacesMessage;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import javax.faces.event.ValueChangeEvent;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.view.rich.event.DialogEvent;

import oracle.binding.OperationBinding;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Number;
import view.backing.ParentBackingBean;

public class Cnf_questionarioBean extends ParentBackingBean {
    public Cnf_questionarioBean() {
    }

    public void deleteQuestionario(DialogEvent dialogEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();

        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfQuestionarioView1Iterator");
        ViewObject vo = voIter.getViewObject();
        Row questionario = vo.getCurrentRow();

        //L’operazione di cancellazione non deve essere possibile se esiste almeno un referto cardiologico relativo a tale questionario
        String qryReferti =
            "select count(*) from SO_REFERTOCARDIO where TPSCR = 'CA' AND IDCNFQUEST = " + questionario.getAttribute("Idcnfquest");
        ApplicationModule am = vo.getApplicationModule();
        ViewObject voReferti = am.createViewObjectFromQueryStmt("", qryReferti);
        voReferti.setRangeSize(-1);
        voReferti.executeQuery();
        Row cntReferti = voReferti.first();

        Number countReferti = (Number) cntReferti.getAttribute(0);
        if (countReferti.intValue() > 0) {
            String msg = "Non e' possibile cancellare questo questionario perche' esistono referti associati.";
            this.handleMessage(FacesMessage.SEVERITY_ERROR, msg);
            return;
        }

        //la cancellazione deve partire dall’associazione tra il questionario e le domande, per cancellare solo alla fine il questionario stesso
        DCIteratorBinding domandeIter = bindings.findIteratorBinding("Cnf_SoCnfQuestDomandeView1Iterator");
        ViewObject domAssociate = domandeIter.getViewObject();
        Row r = domAssociate.last();
        while (r != null) {
            domAssociate.setCurrentRow(r);
            r.remove();
            r = domAssociate.last();
        }

        OperationBinding deleteBinding = bindings.getOperationBinding("Delete");
        OperationBinding commitBinding = bindings.getOperationBinding("Commit");
        Object result = deleteBinding.execute();
        if (!deleteBinding.getErrors().isEmpty()) {
            //add error handling here
        }
        Object commitResult = commitBinding.execute();
        if (!commitBinding.getErrors().isEmpty()) {
            OperationBinding rollbackBinding = bindings.getOperationBinding("Rollback");
            rollbackBinding.execute();
            this.handleMessage(FacesMessage.SEVERITY_ERROR,
                               "Impossibile cancellare il record: restrizione di integrita' violata, chiave figlia trovata.");
        }
    }

    public void deleteQuestDom(DialogEvent dialogEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();

        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfQuestionarioView1Iterator");
        ViewObject vo = voIter.getViewObject();
        Row questionario = vo.getCurrentRow();

        //L’operazione di cancellazione non deve essere possibile se esiste almeno un referto cardiologico relativo a tale questionario
        String qryReferti =
            "select count(*) from SO_REFERTOCARDIO where TPSCR = 'CA' AND IDCNFQUEST = " + questionario.getAttribute("Idcnfquest");
        ApplicationModule am = vo.getApplicationModule();
        ViewObject voReferti = am.createViewObjectFromQueryStmt("", qryReferti);
        voReferti.setRangeSize(-1);
        voReferti.executeQuery();
        Row cntReferti = voReferti.first();

        int countReferti = ((Integer) cntReferti.getAttribute(0)).intValue();
        if (countReferti > 0) {
            String msg = "Non e' possibile cancellare questa domanda perche' esistono referti associati.";
            this.handleMessage(FacesMessage.SEVERITY_ERROR, msg);
            return;
        }
        
        OperationBinding deleteBinding = bindings.getOperationBinding("Delete1");
        OperationBinding commitBinding = bindings.getOperationBinding("Commit");
        Object result = deleteBinding.execute();
        if (!deleteBinding.getErrors().isEmpty()) {
            //add error handling here
        }
        Object commitResult = commitBinding.execute();
        if (!commitBinding.getErrors().isEmpty()) {
            OperationBinding rollbackBinding = bindings.getOperationBinding("Rollback");
            rollbackBinding.execute();
            this.handleMessage(FacesMessage.SEVERITY_ERROR,
                               "Impossibile cancellare il record: restrizione di integrita' violata, chiave figlia trovata.");
        }
    }

    public void beforeSave(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfQuestionarioView1Iterator");
        ViewObject vo = voIter.getViewObject();
        Row r = vo.getCurrentRow();

        //solo un questionario attivo
        Row[] attivi = vo.getFilteredRows("DtFineVal", null);
        if (r.getAttribute("DtFineVal") == null && attivi.length > 0) {
            if ((r.getAttribute("Idcnfquest") != null &&
                !r.getAttribute("Idcnfquest").equals(attivi[0].getAttribute("Idcnfquest"))) || r.getAttribute("Idcnfquest") == null) {
                String msg = "Non e' puo' esserci piu' di un questionario attivo contemporaneamente.";
                this.handleMessage(FacesMessage.SEVERITY_ERROR, msg);
                throw new AbortProcessingException();
            }
        }
    }

    public void editDomandaListener(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfQuestDomandeView1Iterator");
        ViewObject vo = voIter.getViewObject();
        Row domanda = vo.getCurrentRow();
        Integer idQuestionario = (Integer) domanda.getAttribute("Idcnfquest");

        DCIteratorBinding capostipitiIter = bindings.findIteratorBinding("Cnf_SoCnfQuestDomandeView2Iterator");
        ViewObject capostipiti = capostipitiIter.getViewObject();
        capostipiti.setWhereClause("Cnf_SoCnfQuestDomande.IDCNFQUEST = " + idQuestionario +
                                   " AND Cnf_SoCnfQuestDomande.LIVELLO = 1");
        capostipiti.executeQuery();

        DCIteratorBinding domandeIter = bindings.findIteratorBinding("Cnf_SoCnfDomandeView2Iterator");
        ViewObject domande = domandeIter.getViewObject();
        domande.setWhereClause("1=1");
        domande.executeQuery();
    }

    public void createDomListener(ActionEvent actionEvent) {
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();

        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfQuestionarioView1Iterator");
        ViewObject vo = voIter.getViewObject();
        Row questionario = vo.getCurrentRow();
        
        Integer idQuestionario = (Integer)questionario.getAttribute("Idcnfquest");
        
        //In fase di inserimento di un nuovo record, vanno filtrate le sole domande ancora valide (senza fine validita' o con fine validita' futura)
        DCIteratorBinding capostipitiIter = bindings.findIteratorBinding("Cnf_SoCnfQuestDomandeView2Iterator");
        ViewObject capostipiti = capostipitiIter.getViewObject();
        capostipiti.setWhereClause("Cnf_SoCnfQuestDomande.IDCNFQUEST = " + idQuestionario +
                                   " AND Cnf_SoCnfQuestDomande.LIVELLO = 1" +
                                   " AND (Cnf_SoCnfDomande.DT_FINE_VAL IS NULL OR Cnf_SoCnfDomande.DT_FINE_VAL > SYSDATE) ");
        capostipiti.executeQuery();

        DCIteratorBinding domandeIter = bindings.findIteratorBinding("Cnf_SoCnfDomandeView2Iterator");
        ViewObject domande = domandeIter.getViewObject();
        domande.setWhereClause("DT_FINE_VAL IS NULL OR DT_FINE_VAL > SYSDATE ");
        domande.executeQuery();
    }

    public void beforeSaveDom(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();

        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfQuestDomandeView1Iterator");
        ViewObject vo = voIter.getViewObject();
        Row r = vo.getCurrentRow();

        //controllo il capostipite
        if (r.getAttribute("Livello").equals("2") && r.getAttribute("Capostipite") == null) {
            String msg = "Una domanda di livello 2 deve avere una domanda capostipite.";
            this.handleMessage(FacesMessage.SEVERITY_ERROR, msg);
            throw new AbortProcessingException();
        }
    }

    public void deleteDomanda(DialogEvent dialogEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();

        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfDomandeView1Iterator");
        ViewObject vo = voIter.getViewObject();
        Row domanda = vo.getCurrentRow();

        //L’operazione di cancellazione non deve essere possibile se esiste almeno un questionario (ovvero un record di SO_CNF_QUEST_DOMANDE) che utilizza la domanda.
        DCIteratorBinding questIter = bindings.findIteratorBinding("Cnf_SoCnfQuestDomandeView2Iterator");
        ViewObject questionari = questIter.getViewObject();
        questionari.setWhereClause("Cnf_SoCnfQuestDomande.IDCNFDOM = " + domanda.getAttribute("Idcnfdom"));
        questionari.executeQuery();
        if (questionari.getEstimatedRowCount() > 0) {
            String msg = "Non e' possibile cancellare questa domanda perche' esistono questionari associati.";
            this.handleMessage(FacesMessage.SEVERITY_ERROR, msg);
            return;
        }

        OperationBinding deleteBinding = bindings.getOperationBinding("Delete");
        OperationBinding commitBinding = bindings.getOperationBinding("Commit");
        Object result = deleteBinding.execute();
        if (!deleteBinding.getErrors().isEmpty()) {
            //add error handling here
        }
        Object commitResult = commitBinding.execute();
        if (!commitBinding.getErrors().isEmpty()) {
            OperationBinding rollbackBinding = bindings.getOperationBinding("Rollback");
            rollbackBinding.execute();
            this.handleMessage(FacesMessage.SEVERITY_ERROR,
                               "Impossibile cancellare il record: restrizione di integrita' violata, chiave figlia trovata.");
        }
    }

    public void changeTipoListener(ValueChangeEvent valueChangeEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();

        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfDomandeView1Iterator");
        ViewObject vo = voIter.getViewObject();
        Row domanda = vo.getCurrentRow();
        
        String tipo = (String) valueChangeEvent.getNewValue();
        if (!"SEL".equals(tipo)&&!"MUL".equals(tipo))
            {
              domanda.setAttribute("Gruppo",null);
            }
    }
}
