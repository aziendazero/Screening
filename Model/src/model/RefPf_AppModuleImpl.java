package model;


import model.common.RefPf_AppModule;

import model.commons.Parent_AppModuleImpl;

import model.global.A_SoAziendaViewImpl;
import model.global.A_SoCentroPrelRefViewImpl;
import model.global.A_SoCnfParametriSistemaViewImpl;
import model.global.A_SoCnfPartemplateViewImpl;
import model.global.A_SoSoggettoViewImpl;

import model.referto.Ref_SoAllegatoViewImpl;
import model.referto.Ref_SoCnfLetteracentroViewImpl;
import model.referto.Ref_SoCnfSugg1livViewImpl;
import model.referto.Ref_SoRefertopfasViewImpl;
import model.referto.Ref_SoTemplateViewImpl;

import oracle.jbo.server.ViewObjectImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Fri Jan 20 16:19:21 CET 2017
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class RefPf_AppModuleImpl extends Parent_AppModuleImpl implements RefPf_AppModule {
    private String newField;

    /**
     * This is the default constructor (do not remove).
     */
    public RefPf_AppModuleImpl() {
    }

    public void setNewField(String newField) {
        this.newField = newField;
    }

    public String getNewField() {
        return newField;
    }

    /**
     * Container's getter for Ref_SoRefertopfasView1.
     * @return Ref_SoRefertopfasView1
     */
    public Ref_SoRefertopfasViewImpl getRef_SoRefertopfasView1() {
        return (Ref_SoRefertopfasViewImpl)findViewObject("Ref_SoRefertopfasView1");
    }

    /**
     * Container's getter for Ref_SoOpmedicoRilevatoreView1.
     * @return Ref_SoOpmedicoRilevatoreView1
     */
    public ViewObjectImpl getRef_SoOpmedicoRilevatoreView1() {
        return (ViewObjectImpl)findViewObject("Ref_SoOpmedicoRilevatoreView1");
    }

    /**
     * Container's getter for Ref_SoAllegatoView1.
     * @return Ref_SoAllegatoView1
     */
    public Ref_SoAllegatoViewImpl getRef_SoAllegatoView1() {
        return (Ref_SoAllegatoViewImpl)findViewObject("Ref_SoAllegatoView1");
    }

    /**
     * Container's getter for A_SoSoggettoView1.
     * @return A_SoSoggettoView1
     */
    public A_SoSoggettoViewImpl getA_SoSoggettoView1() {
        return (A_SoSoggettoViewImpl)findViewObject("A_SoSoggettoView1");
    }

    /**
     * Container's getter for Ref_SoCnfLetteracentroView1.
     * @return Ref_SoCnfLetteracentroView1
     */
    public Ref_SoCnfLetteracentroViewImpl getRef_SoCnfLetteracentroView1() {
        return (Ref_SoCnfLetteracentroViewImpl)findViewObject("Ref_SoCnfLetteracentroView1");
    }

    /**
     * Container's getter for Ref_SoTemplateView1.
     * @return Ref_SoTemplateView1
     */
    public Ref_SoTemplateViewImpl getRef_SoTemplateView1() {
        return (Ref_SoTemplateViewImpl)findViewObject("Ref_SoTemplateView1");
    }

    /**
     * Container's getter for A_SoCnfParametriSistemaView1.
     * @return A_SoCnfParametriSistemaView1
     */
    public A_SoCnfParametriSistemaViewImpl getA_SoCnfParametriSistemaView1() {
        return (A_SoCnfParametriSistemaViewImpl)findViewObject("A_SoCnfParametriSistemaView1");
    }

    /**
     * Container's getter for Ref_SoCnfSugg1livView3.
     * @return Ref_SoCnfSugg1livView3
     */
    public Ref_SoCnfSugg1livViewImpl getRef_SoCnfSugg1livView3() {
        return (Ref_SoCnfSugg1livViewImpl)findViewObject("Ref_SoCnfSugg1livView3");
    }

    /**
     * Container's getter for A_SoCentroPrelRefView1.
     * @return A_SoCentroPrelRefView1
     */
    public A_SoCentroPrelRefViewImpl getA_SoCentroPrelRefView1() {
        return (A_SoCentroPrelRefViewImpl)findViewObject("A_SoCentroPrelRefView1");
    }

    /**
     * Container's getter for A_SoAziendaView1.
     * @return A_SoAziendaView1
     */
    public A_SoAziendaViewImpl getA_SoAziendaView1() {
        return (A_SoAziendaViewImpl)findViewObject("A_SoAziendaView1");
    }

    /**
     * Container's getter for A_SoCnfPartemplateView1.
     * @return A_SoCnfPartemplateView1
     */
    public A_SoCnfPartemplateViewImpl getA_SoCnfPartemplateView1() {
        return (A_SoCnfPartemplateViewImpl)findViewObject("A_SoCnfPartemplateView1");
    }
}
