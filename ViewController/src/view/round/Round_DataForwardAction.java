package view.round;

import insiel.dataHandling.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import model.common.ElencoSoggetti;
import model.common.Round_AppModule;

import model.commons.ConfigurationConstants;

import model.datacontrol.Cnf_selectionBean;
import model.datacontrol.Round_invitiBean;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import view.commons.action.Parent_DataForwardAction;

public abstract class Round_DataForwardAction extends Parent_DataForwardAction {


    protected void setAppModule() {
        this.amName = "Round_AppModule";
    }

    protected abstract String getElenco_voName();

    public void setGenerationDate() {
        /*
         * mauro 07/10/2010, per richiesta utente il metodo non deve fare niente
         * creato metodo setGenerationDateOld per salvare vecchia implementazione
         */
    }

    public void setGenerationDateOld() {
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        Round_invitiBean bean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();

        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        String scr = (String) sess.get("scr");
        ViewObject vv =
            am.createViewObjectFromQueryStmt("max_app",
                                             "select max(dtapp) from so_appuntamento " + "where idcentro=" +
                                             bean.getCentro() + " and SLOTOCCUP >0 and dtapp>sysdate");
        try {
            if (bean.getCentro() == null)
                return;

            /*  ViewObject vo=am.findViewObject("Round_SoAppuntamentoView1");

          // mauro 8-5-06, revisione per colon


          String wh = "FUN_OCCUPATI(IDAPP,'" + scr +
            "')>0 AND IDCENTRO="+bean.getCentro();

          vo.setWhereClause(wh);

          // mauro 8-5-06, fine revisione
          vo.setOrderByClause("DTAPP desc");
          vo.executeQuery();
          Row r=vo.first();
          */

            //  vv.executeQuery();
            Row r = vv.first();

            Calendar c = DateUtils.getCalendar(new Date());
            c.add(Calendar.DAY_OF_MONTH, ConfigurationConstants.AGENDA_INVITI_INIZIO_POSTICIPATO);
            if (r == null) { //nessuno slot generato, posso generare a partire da oggi+i giorni di margine fissati
                bean.setGenerate_dal(c.getTime());

            } else {

                //  Date d=DateUtils.getJavaDate((oracle.jbo.domain.Date)r.getAttribute("Dtapp"));
                Date d = DateUtils.getJavaDate((oracle.jbo.domain.Date) r.getAttribute(0));
                if (d.before(c.getTime())) { //quello che ho generato e' relativo al passato
                    bean.setGenerate_dal(c.getTime());
                } else { //se e' sabato o domenica mi porto sul lunedì successivo
                    Calendar c1 = DateUtils.getCalendar(d);
                    int inc = 0;
                    if (c1.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
                        inc = 2;
                    if (c1.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                        inc = 1;
                    c1.add(Calendar.DAY_OF_MONTH, inc);
                    bean.setGenerate_dal(c1.getTime());
                }
            }
            try {
                //a questo punto predispongo la data di fine generazione
                //come due mesi piu' avanti
                Calendar c2 = DateUtils.getCalendar(bean.getGenerate_dal());
                c2.add(Calendar.DAY_OF_MONTH, ConfigurationConstants.AGENDA_FISICA_N_GIORNI + 1);
                bean.setGenerate_al(c2.getTime());

            } catch (Throwable ex) {

            }

        } catch (Exception e) {
             e.printStackTrace();

            this.handleException(e.getMessage(), null);
        } finally {
            vv.remove();
        }
    }


    public void onSetInClause() {
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();

        ViewObject vo = am.findViewObject(this.getElenco_voName());
        //imposto la inclause per l'eventuale stampa
        String s = ((ElencoSoggetti) vo).getInClause();
        Cnf_selectionBean c_bean =
            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
        c_bean.setInClause(s);
        c_bean.setNote(null);
    }

        protected void beforeNavigate(String dest) throws Exception {
        Round_invitiBean bean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();
        bean.setCentro_2(bean.getCentro());
        super.beforeNavigate(dest);
    }
}
