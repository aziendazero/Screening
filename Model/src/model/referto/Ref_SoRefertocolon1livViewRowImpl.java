package model.referto;

import java.math.BigDecimal;

import oracle.jbo.RowIterator;
import oracle.jbo.domain.Date;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.ViewRowImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Wed Mar 30 12:30:43 CEST 2016
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class Ref_SoRefertocolon1livViewRowImpl extends ViewRowImpl {

    public static final int ENTITY_SOREFERTOCOLON1LIV = 0;
    public static final int ENTITY_REF_SOINVITO = 1;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Idreferto {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getIdreferto();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setIdreferto((Integer) value);
            }
        }
        ,
        Idinvito {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getIdinvito();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setIdinvito((Integer) value);
            }
        }
        ,
        Codts {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getCodts();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setCodts((String) value);
            }
        }
        ,
        Ulss {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getUlss();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setUlss((String) value);
            }
        }
        ,
        Tpscr {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getTpscr();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setTpscr((String) value);
            }
        }
        ,
        CodProvetta {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getCodProvetta();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setCodProvetta((BigDecimal) value);
            }
        }
        ,
        NProvetta {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getNProvetta();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setNProvetta((Integer) value);
            }
        }
        ,
        Dtreferto {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getDtreferto();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setDtreferto((Date) value);
            }
        }
        ,
        Idlettore {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getIdlettore();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setIdlettore((Integer) value);
            }
        }
        ,
        Idcentroref {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getIdcentroref();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setIdcentroref((Integer) value);
            }
        }
        ,
        Idsugg {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getIdsugg();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setIdsugg((Integer) value);
            }
        }
        ,
        Quantita {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getQuantita();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setQuantita((Integer) value);
            }
        }
        ,
        Completo {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getCompleto();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setCompleto((Integer) value);
            }
        }
        ,
        Note {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getNote();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setNote((String) value);
            }
        }
        ,
        Dtinserimento {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getDtinserimento();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setDtinserimento((Date) value);
            }
        }
        ,
        Opinserimento {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getOpinserimento();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setOpinserimento((String) value);
            }
        }
        ,
        Dtultmodifica {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getDtultmodifica();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setDtultmodifica((Date) value);
            }
        }
        ,
        Opultmodifica {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getOpultmodifica();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setOpultmodifica((String) value);
            }
        }
        ,
        Tpreferto {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getTpreferto();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setTpreferto((Integer) value);
            }
        }
        ,
        Idallegato {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getIdallegato();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setIdallegato((Integer) value);
            }
        }
        ,
        Dtcreazione {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getDtcreazione();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setDtcreazione((Date) value);
            }
        }
        ,
        Dtspedizione {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getDtspedizione();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setDtspedizione((Date) value);
            }
        }
        ,
        CodReferto {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getCodReferto();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setCodReferto((String) value);
            }
        }
        ,
        Idcentroprelievo {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getIdcentroprelievo();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setIdcentroprelievo((Integer) value);
            }
        }
        ,
        Idinvito1 {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getIdinvito1();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setIdinvito1((Integer) value);
            }
        }
        ,
        Esito {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getEsito();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setEsito((Integer) value);
            }
        }
        ,
        Ref_SoCnfSugg1livView {
            public Object get(Ref_SoRefertocolon1livViewRowImpl obj) {
                return obj.getRef_SoCnfSugg1livView();
            }

            public void put(Ref_SoRefertocolon1livViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ;
        static AttributesEnum[] vals = null;
        ;
        private static int firstIndex = 0;

        public abstract Object get(Ref_SoRefertocolon1livViewRowImpl object);

        public abstract void put(Ref_SoRefertocolon1livViewRowImpl object, Object value);

        public int index() {
            return AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return AttributesEnum.firstIndex() + AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = AttributesEnum.values();
            }
            return vals;
        }
    }


    public static final int IDREFERTO = AttributesEnum.Idreferto.index();
    public static final int IDINVITO = AttributesEnum.Idinvito.index();
    public static final int CODTS = AttributesEnum.Codts.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int CODPROVETTA = AttributesEnum.CodProvetta.index();
    public static final int NPROVETTA = AttributesEnum.NProvetta.index();
    public static final int DTREFERTO = AttributesEnum.Dtreferto.index();
    public static final int IDLETTORE = AttributesEnum.Idlettore.index();
    public static final int IDCENTROREF = AttributesEnum.Idcentroref.index();
    public static final int IDSUGG = AttributesEnum.Idsugg.index();
    public static final int QUANTITA = AttributesEnum.Quantita.index();
    public static final int COMPLETO = AttributesEnum.Completo.index();
    public static final int NOTE = AttributesEnum.Note.index();
    public static final int DTINSERIMENTO = AttributesEnum.Dtinserimento.index();
    public static final int OPINSERIMENTO = AttributesEnum.Opinserimento.index();
    public static final int DTULTMODIFICA = AttributesEnum.Dtultmodifica.index();
    public static final int OPULTMODIFICA = AttributesEnum.Opultmodifica.index();
    public static final int TPREFERTO = AttributesEnum.Tpreferto.index();
    public static final int IDALLEGATO = AttributesEnum.Idallegato.index();
    public static final int DTCREAZIONE = AttributesEnum.Dtcreazione.index();
    public static final int DTSPEDIZIONE = AttributesEnum.Dtspedizione.index();
    public static final int CODREFERTO = AttributesEnum.CodReferto.index();
    public static final int IDCENTROPRELIEVO = AttributesEnum.Idcentroprelievo.index();
    public static final int IDINVITO1 = AttributesEnum.Idinvito1.index();
    public static final int ESITO = AttributesEnum.Esito.index();
    public static final int REF_SOCNFSUGG1LIVVIEW = AttributesEnum.Ref_SoCnfSugg1livView.index();

    /**
     * This is the default constructor (do not remove).
     */
    public Ref_SoRefertocolon1livViewRowImpl() {
    }

    /**
     * Gets SoRefertocolon1liv entity object.
     * @return the SoRefertocolon1liv
     */
    public Ref_SoRefertocolon1livImpl getSoRefertocolon1liv() {
        return (Ref_SoRefertocolon1livImpl) getEntity(ENTITY_SOREFERTOCOLON1LIV);
    }

    /**
     * Gets Ref_SoInvito entity object.
     * @return the Ref_SoInvito
     */
    public Ref_SoInvitoImpl getRef_SoInvito() {
        return (Ref_SoInvitoImpl) getEntity(ENTITY_REF_SOINVITO);
    }

    /**
     * Gets the attribute value for IDREFERTO using the alias name Idreferto.
     * @return the IDREFERTO
     */
    public Integer getIdreferto() {
        return (Integer) getAttributeInternal(IDREFERTO);
    }

    /**
     * Sets <code>value</code> as attribute value for IDREFERTO using the alias name Idreferto.
     * @param value value to set the IDREFERTO
     */
    public void setIdreferto(Integer value) {
        setAttributeInternal(IDREFERTO, value);
    }

    /**
     * Gets the attribute value for IDINVITO using the alias name Idinvito.
     * @return the IDINVITO
     */
    public Integer getIdinvito() {
        return (Integer) getAttributeInternal(IDINVITO);
    }

    /**
     * Sets <code>value</code> as attribute value for IDINVITO using the alias name Idinvito.
     * @param value value to set the IDINVITO
     */
    public void setIdinvito(Integer value) {
        setAttributeInternal(IDINVITO, value);
    }

    /**
     * Gets the attribute value for CODTS using the alias name Codts.
     * @return the CODTS
     */
    public String getCodts() {
        return (String) getAttributeInternal(CODTS);
    }

    /**
     * Sets <code>value</code> as attribute value for CODTS using the alias name Codts.
     * @param value value to set the CODTS
     */
    public void setCodts(String value) {
        setAttributeInternal(CODTS, value);
    }

    /**
     * Gets the attribute value for ULSS using the alias name Ulss.
     * @return the ULSS
     */
    public String getUlss() {
        return (String) getAttributeInternal(ULSS);
    }

    /**
     * Sets <code>value</code> as attribute value for ULSS using the alias name Ulss.
     * @param value value to set the ULSS
     */
    public void setUlss(String value) {
        setAttributeInternal(ULSS, value);
    }

    /**
     * Gets the attribute value for TPSCR using the alias name Tpscr.
     * @return the TPSCR
     */
    public String getTpscr() {
        return (String) getAttributeInternal(TPSCR);
    }

    /**
     * Sets <code>value</code> as attribute value for TPSCR using the alias name Tpscr.
     * @param value value to set the TPSCR
     */
    public void setTpscr(String value) {
        setAttributeInternal(TPSCR, value);
    }

    /**
     * Gets the attribute value for COD_PROVETTA using the alias name CodProvetta.
     * @return the COD_PROVETTA
     */
    public BigDecimal getCodProvetta() {
        return (BigDecimal) getAttributeInternal(CODPROVETTA);
    }

    /**
     * Sets <code>value</code> as attribute value for COD_PROVETTA using the alias name CodProvetta.
     * @param value value to set the COD_PROVETTA
     */
    public void setCodProvetta(BigDecimal value) {
        setAttributeInternal(CODPROVETTA, value);
    }

    /**
     * Gets the attribute value for N_PROVETTA using the alias name NProvetta.
     * @return the N_PROVETTA
     */
    public Integer getNProvetta() {
        return (Integer) getAttributeInternal(NPROVETTA);
    }

    /**
     * Sets <code>value</code> as attribute value for N_PROVETTA using the alias name NProvetta.
     * @param value value to set the N_PROVETTA
     */
    public void setNProvetta(Integer value) {
        setAttributeInternal(NPROVETTA, value);
    }

    /**
     * Gets the attribute value for DTREFERTO using the alias name Dtreferto.
     * @return the DTREFERTO
     */
    public Date getDtreferto() {
        return (Date) getAttributeInternal(DTREFERTO);
    }

    /**
     * Sets <code>value</code> as attribute value for DTREFERTO using the alias name Dtreferto.
     * @param value value to set the DTREFERTO
     */
    public void setDtreferto(Date value) {
        setAttributeInternal(DTREFERTO, value);
    }

    /**
     * Gets the attribute value for IDLETTORE using the alias name Idlettore.
     * @return the IDLETTORE
     */
    public Integer getIdlettore() {
        return (Integer) getAttributeInternal(IDLETTORE);
    }

    /**
     * Sets <code>value</code> as attribute value for IDLETTORE using the alias name Idlettore.
     * @param value value to set the IDLETTORE
     */
    public void setIdlettore(Integer value) {
        setAttributeInternal(IDLETTORE, value);
    }

    /**
     * Gets the attribute value for IDCENTROREF using the alias name Idcentroref.
     * @return the IDCENTROREF
     */
    public Integer getIdcentroref() {
        return (Integer) getAttributeInternal(IDCENTROREF);
    }

    /**
     * Sets <code>value</code> as attribute value for IDCENTROREF using the alias name Idcentroref.
     * @param value value to set the IDCENTROREF
     */
    public void setIdcentroref(Integer value) {
        setAttributeInternal(IDCENTROREF, value);
    }

    /**
     * Gets the attribute value for IDSUGG using the alias name Idsugg.
     * @return the IDSUGG
     */
    public Integer getIdsugg() {
        return (Integer) getAttributeInternal(IDSUGG);
    }

    /**
     * Sets <code>value</code> as attribute value for IDSUGG using the alias name Idsugg.
     * @param value value to set the IDSUGG
     */
    public void setIdsugg(Integer value) {
        setAttributeInternal(IDSUGG, value);
    }

    /**
     * Gets the attribute value for QUANTITA using the alias name Quantita.
     * @return the QUANTITA
     */
    public Integer getQuantita() {
        return (Integer) getAttributeInternal(QUANTITA);
    }

    /**
     * Sets <code>value</code> as attribute value for QUANTITA using the alias name Quantita.
     * @param value value to set the QUANTITA
     */
    public void setQuantita(Integer value) {
        setAttributeInternal(QUANTITA, value);
    }

    /**
     * Gets the attribute value for COMPLETO using the alias name Completo.
     * @return the COMPLETO
     */
    public Integer getCompleto() {
        return (Integer) getAttributeInternal(COMPLETO);
    }

    /**
     * Sets <code>value</code> as attribute value for COMPLETO using the alias name Completo.
     * @param value value to set the COMPLETO
     */
    public void setCompleto(Integer value) {
        setAttributeInternal(COMPLETO, value);
    }

    /**
     * Gets the attribute value for NOTE using the alias name Note.
     * @return the NOTE
     */
    public String getNote() {
        return (String) getAttributeInternal(NOTE);
    }

    /**
     * Sets <code>value</code> as attribute value for NOTE using the alias name Note.
     * @param value value to set the NOTE
     */
    public void setNote(String value) {
        setAttributeInternal(NOTE, value);
    }

    /**
     * Gets the attribute value for DTINSERIMENTO using the alias name Dtinserimento.
     * @return the DTINSERIMENTO
     */
    public Date getDtinserimento() {
        return (Date) getAttributeInternal(DTINSERIMENTO);
    }

    /**
     * Sets <code>value</code> as attribute value for DTINSERIMENTO using the alias name Dtinserimento.
     * @param value value to set the DTINSERIMENTO
     */
    public void setDtinserimento(Date value) {
        setAttributeInternal(DTINSERIMENTO, value);
    }

    /**
     * Gets the attribute value for OPINSERIMENTO using the alias name Opinserimento.
     * @return the OPINSERIMENTO
     */
    public String getOpinserimento() {
        return (String) getAttributeInternal(OPINSERIMENTO);
    }

    /**
     * Sets <code>value</code> as attribute value for OPINSERIMENTO using the alias name Opinserimento.
     * @param value value to set the OPINSERIMENTO
     */
    public void setOpinserimento(String value) {
        setAttributeInternal(OPINSERIMENTO, value);
    }

    /**
     * Gets the attribute value for DTULTMODIFICA using the alias name Dtultmodifica.
     * @return the DTULTMODIFICA
     */
    public Date getDtultmodifica() {
        return (Date) getAttributeInternal(DTULTMODIFICA);
    }

    /**
     * Sets <code>value</code> as attribute value for DTULTMODIFICA using the alias name Dtultmodifica.
     * @param value value to set the DTULTMODIFICA
     */
    public void setDtultmodifica(Date value) {
        setAttributeInternal(DTULTMODIFICA, value);
    }

    /**
     * Gets the attribute value for OPULTMODIFICA using the alias name Opultmodifica.
     * @return the OPULTMODIFICA
     */
    public String getOpultmodifica() {
        return (String) getAttributeInternal(OPULTMODIFICA);
    }

    /**
     * Sets <code>value</code> as attribute value for OPULTMODIFICA using the alias name Opultmodifica.
     * @param value value to set the OPULTMODIFICA
     */
    public void setOpultmodifica(String value) {
        setAttributeInternal(OPULTMODIFICA, value);
    }

    /**
     * Gets the attribute value for TPREFERTO using the alias name Tpreferto.
     * @return the TPREFERTO
     */
    public Integer getTpreferto() {
        return (Integer) getAttributeInternal(TPREFERTO);
    }

    /**
     * Sets <code>value</code> as attribute value for TPREFERTO using the alias name Tpreferto.
     * @param value value to set the TPREFERTO
     */
    public void setTpreferto(Integer value) {
        setAttributeInternal(TPREFERTO, value);
    }

    /**
     * Gets the attribute value for IDALLEGATO using the alias name Idallegato.
     * @return the IDALLEGATO
     */
    public Integer getIdallegato() {
        return (Integer) getAttributeInternal(IDALLEGATO);
    }

    /**
     * Sets <code>value</code> as attribute value for IDALLEGATO using the alias name Idallegato.
     * @param value value to set the IDALLEGATO
     */
    public void setIdallegato(Integer value) {
        setAttributeInternal(IDALLEGATO, value);
    }

    /**
     * Gets the attribute value for DTCREAZIONE using the alias name Dtcreazione.
     * @return the DTCREAZIONE
     */
    public Date getDtcreazione() {
        return (Date) getAttributeInternal(DTCREAZIONE);
    }

    /**
     * Sets <code>value</code> as attribute value for DTCREAZIONE using the alias name Dtcreazione.
     * @param value value to set the DTCREAZIONE
     */
    public void setDtcreazione(Date value) {
        setAttributeInternal(DTCREAZIONE, value);
    }

    /**
     * Gets the attribute value for DTSPEDIZIONE using the alias name Dtspedizione.
     * @return the DTSPEDIZIONE
     */
    public Date getDtspedizione() {
        return (Date) getAttributeInternal(DTSPEDIZIONE);
    }

    /**
     * Sets <code>value</code> as attribute value for DTSPEDIZIONE using the alias name Dtspedizione.
     * @param value value to set the DTSPEDIZIONE
     */
    public void setDtspedizione(Date value) {
        setAttributeInternal(DTSPEDIZIONE, value);
    }

    /**
     * Gets the attribute value for COD_REFERTO using the alias name CodReferto.
     * @return the COD_REFERTO
     */
    public String getCodReferto() {
        return (String) getAttributeInternal(CODREFERTO);
    }

    /**
     * Sets <code>value</code> as attribute value for COD_REFERTO using the alias name CodReferto.
     * @param value value to set the COD_REFERTO
     */
    public void setCodReferto(String value) {
        setAttributeInternal(CODREFERTO, value);
    }

    /**
     * Gets the attribute value for IDCENTROPRELIEVO using the alias name Idcentroprelievo.
     * @return the IDCENTROPRELIEVO
     */
    public Integer getIdcentroprelievo() {
        return (Integer) getAttributeInternal(IDCENTROPRELIEVO);
    }

    /**
     * Sets <code>value</code> as attribute value for IDCENTROPRELIEVO using the alias name Idcentroprelievo.
     * @param value value to set the IDCENTROPRELIEVO
     */
    public void setIdcentroprelievo(Integer value) {
        setAttributeInternal(IDCENTROPRELIEVO, value);
    }

    /**
     * Gets the attribute value for IDINVITO using the alias name Idinvito1.
     * @return the IDINVITO
     */
    public Integer getIdinvito1() {
        return (Integer) getAttributeInternal(IDINVITO1);
    }

    /**
     * Sets <code>value</code> as attribute value for IDINVITO using the alias name Idinvito1.
     * @param value value to set the IDINVITO
     */
    public void setIdinvito1(Integer value) {
        setAttributeInternal(IDINVITO1, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Esito.
     * @return the Esito
     */
    public Integer getEsito() {
        return (Integer) getAttributeInternal(ESITO);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Esito.
     * @param value value to set the  Esito
     */
    public void setEsito(Integer value) {
        setAttributeInternal(ESITO, value);
    }

    /**
     * Gets the associated <code>RowIterator</code> using master-detail link Ref_SoCnfSugg1livView.
     */
    public RowIterator getRef_SoCnfSugg1livView() {
        return (RowIterator) getAttributeInternal(REF_SOCNFSUGG1LIVVIEW);
    }

    /**
     * getAttrInvokeAccessor: generated method. Do not modify.
     * @param index the index identifying the attribute
     * @param attrDef the attribute

     * @return the attribute value
     * @throws Exception
     */
    protected Object getAttrInvokeAccessor(int index, AttributeDefImpl attrDef) throws Exception {
        if ((index >= AttributesEnum.firstIndex()) && (index < AttributesEnum.count())) {
            return AttributesEnum.staticValues()[index - AttributesEnum.firstIndex()].get(this);
        }
        return super.getAttrInvokeAccessor(index, attrDef);
    }

    /**
     * setAttrInvokeAccessor: generated method. Do not modify.
     * @param index the index identifying the attribute
     * @param value the value to assign to the attribute
     * @param attrDef the attribute

     * @throws Exception
     */
    protected void setAttrInvokeAccessor(int index, Object value, AttributeDefImpl attrDef) throws Exception {
        if ((index >= AttributesEnum.firstIndex()) && (index < AttributesEnum.count())) {
            AttributesEnum.staticValues()[index - AttributesEnum.firstIndex()].put(this, value);
            return;
        }
        super.setAttrInvokeAccessor(index, value, attrDef);
    }


}
