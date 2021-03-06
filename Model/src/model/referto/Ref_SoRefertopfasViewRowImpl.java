package model.referto;

import oracle.jbo.RowIterator;
import oracle.jbo.domain.Date;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.EntityImpl;
import oracle.jbo.server.ViewRowImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Fri Jan 20 15:51:23 CET 2017
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class Ref_SoRefertopfasViewRowImpl extends ViewRowImpl {

    public static final int ENTITY_REF_SOREFERTOPFAS = 0;
    public static final int ENTITY_REF_SOINVITO = 1;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Idreferto {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getIdreferto();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setIdreferto((Integer)value);
            }
        }
        ,
        Codts {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getCodts();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setCodts((String)value);
            }
        }
        ,
        Ulss {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getUlss();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setUlss((String)value);
            }
        }
        ,
        Tpscr {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getTpscr();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setTpscr((String)value);
            }
        }
        ,
        Idinvito {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getIdinvito();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setIdinvito((Integer)value);
            }
        }
        ,
        Idcnfquest {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getIdcnfquest();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setIdcnfquest((Integer)value);
            }
        }
        ,
        StileVita {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getStileVita();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setStileVita((Integer)value);
            }
        }
        ,
        Idsugg {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getIdsugg();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setIdsugg((Integer)value);
            }
        }
        ,
        Dtcreazione {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getDtcreazione();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setDtcreazione((Date)value);
            }
        }
        ,
        Dtspedizione {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getDtspedizione();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setDtspedizione((Date)value);
            }
        }
        ,
        Idallegato {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getIdallegato();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setIdallegato((Integer)value);
            }
        }
        ,
        Dtinserimento {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getDtinserimento();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setDtinserimento((Date)value);
            }
        }
        ,
        Opinserimento {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getOpinserimento();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setOpinserimento((String)value);
            }
        }
        ,
        Dtultmodifica {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getDtultmodifica();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setDtultmodifica((Date)value);
            }
        }
        ,
        Opultmodifica {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getOpultmodifica();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setOpultmodifica((String)value);
            }
        }
        ,
        Note {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getNote();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setNote((String)value);
            }
        }
        ,
        Completo {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getCompleto();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setCompleto((Integer)value);
            }
        }
        ,
        Idcentroref {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getIdcentroref();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setIdcentroref((Integer)value);
            }
        }
        ,
        DataRilevazione {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getDataRilevazione();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setDataRilevazione((Date)value);
            }
        }
        ,
        Oprilevazione {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getOprilevazione();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setOprilevazione((Integer)value);
            }
        }
        ,
        Dtreferto {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getDtreferto();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setDtreferto((Date)value);
            }
        }
        ,
        ClasseEsami {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getClasseEsami();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setClasseEsami((Integer)value);
            }
        }
        ,
        GrStile {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getGrStile();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setGrStile((String)value);
            }
        }
        ,
        GrClesami {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getGrClesami();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setGrClesami((String)value);
            }
        }
        ,
        Alimentazione {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getAlimentazione();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setAlimentazione((Integer)value);
            }
        }
        ,
        GrAliment {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getGrAliment();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setGrAliment((String)value);
            }
        }
        ,
        Pressione {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getPressione();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setPressione((Integer)value);
            }
        }
        ,
        GrPressione {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getGrPressione();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setGrPressione((String)value);
            }
        }
        ,
        Bmi {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getBmi();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setBmi((Integer)value);
            }
        }
        ,
        GrBmi {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getGrBmi();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setGrBmi((String)value);
            }
        }
        ,
        ClassePfas {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getClassePfas();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setClassePfas((Integer)value);
            }
        }
        ,
        GrPfas {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getGrPfas();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setGrPfas((String)value);
            }
        }
        ,
        DtRefpfas {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getDtRefpfas();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setDtRefpfas((Date)value);
            }
        }
        ,
        Idcentroprelievo {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getIdcentroprelievo();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setIdcentroprelievo((Integer) value);
            }
        }
        ,
        Idinvito1 {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getIdinvito1();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setIdinvito1((Integer) value);
            }
        }
        ,
        Dtapp {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getDtapp();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setDtapp((Date)value);
            }
        }
        ,
        Oraapp {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getOraapp();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setOraapp((Integer) value);
            }
        }
        ,
        Minapp {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getMinapp();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setMinapp((Integer) value);
            }
        }
        ,
        Ref_SoRefertopfasDatiView {
            public Object get(Ref_SoRefertopfasViewRowImpl obj) {
                return obj.getRef_SoRefertopfasDatiView();
            }

            public void put(Ref_SoRefertopfasViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Ref_SoRefertopfasViewRowImpl object);

        public abstract void put(Ref_SoRefertopfasViewRowImpl object, Object value);

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
    public static final int CODTS = AttributesEnum.Codts.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int IDINVITO = AttributesEnum.Idinvito.index();
    public static final int IDCNFQUEST = AttributesEnum.Idcnfquest.index();
    public static final int STILEVITA = AttributesEnum.StileVita.index();
    public static final int IDSUGG = AttributesEnum.Idsugg.index();
    public static final int DTCREAZIONE = AttributesEnum.Dtcreazione.index();
    public static final int DTSPEDIZIONE = AttributesEnum.Dtspedizione.index();
    public static final int IDALLEGATO = AttributesEnum.Idallegato.index();
    public static final int DTINSERIMENTO = AttributesEnum.Dtinserimento.index();
    public static final int OPINSERIMENTO = AttributesEnum.Opinserimento.index();
    public static final int DTULTMODIFICA = AttributesEnum.Dtultmodifica.index();
    public static final int OPULTMODIFICA = AttributesEnum.Opultmodifica.index();
    public static final int NOTE = AttributesEnum.Note.index();
    public static final int COMPLETO = AttributesEnum.Completo.index();
    public static final int IDCENTROREF = AttributesEnum.Idcentroref.index();
    public static final int DATARILEVAZIONE = AttributesEnum.DataRilevazione.index();
    public static final int OPRILEVAZIONE = AttributesEnum.Oprilevazione.index();
    public static final int DTREFERTO = AttributesEnum.Dtreferto.index();
    public static final int CLASSEESAMI = AttributesEnum.ClasseEsami.index();
    public static final int GRSTILE = AttributesEnum.GrStile.index();
    public static final int GRCLESAMI = AttributesEnum.GrClesami.index();
    public static final int ALIMENTAZIONE = AttributesEnum.Alimentazione.index();
    public static final int GRALIMENT = AttributesEnum.GrAliment.index();
    public static final int PRESSIONE = AttributesEnum.Pressione.index();
    public static final int GRPRESSIONE = AttributesEnum.GrPressione.index();
    public static final int BMI = AttributesEnum.Bmi.index();
    public static final int GRBMI = AttributesEnum.GrBmi.index();
    public static final int CLASSEPFAS = AttributesEnum.ClassePfas.index();
    public static final int GRPFAS = AttributesEnum.GrPfas.index();
    public static final int DTREFPFAS = AttributesEnum.DtRefpfas.index();
    public static final int IDCENTROPRELIEVO = AttributesEnum.Idcentroprelievo.index();
    public static final int IDINVITO1 = AttributesEnum.Idinvito1.index();
    public static final int DTAPP = AttributesEnum.Dtapp.index();
    public static final int ORAAPP = AttributesEnum.Oraapp.index();
    public static final int MINAPP = AttributesEnum.Minapp.index();
    public static final int REF_SOREFERTOPFASDATIVIEW = AttributesEnum.Ref_SoRefertopfasDatiView.index();

    /**
     * This is the default constructor (do not remove).
     */
    public Ref_SoRefertopfasViewRowImpl() {
    }

    /**
     * Gets Ref_SoRefertopfas entity object.
     * @return the Ref_SoRefertopfas
     */
    public EntityImpl getRef_SoRefertopfas() {
        return (EntityImpl)getEntity(ENTITY_REF_SOREFERTOPFAS);
    }

    /**
     * Gets Ref_SoInvito entity object.
     * @return the Ref_SoInvito
     */
    public Ref_SoInvitoImpl getRef_SoInvito() {
        return (Ref_SoInvitoImpl)getEntity(ENTITY_REF_SOINVITO);
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
     * Gets the attribute value for IDCNFQUEST using the alias name Idcnfquest.
     * @return the IDCNFQUEST
     */
    public Integer getIdcnfquest() {
        return (Integer) getAttributeInternal(IDCNFQUEST);
    }

    /**
     * Sets <code>value</code> as attribute value for IDCNFQUEST using the alias name Idcnfquest.
     * @param value value to set the IDCNFQUEST
     */
    public void setIdcnfquest(Integer value) {
        setAttributeInternal(IDCNFQUEST, value);
    }

    /**
     * Gets the attribute value for STILE_VITA using the alias name StileVita.
     * @return the STILE_VITA
     */
    public Integer getStileVita() {
        return (Integer) getAttributeInternal(STILEVITA);
    }

    /**
     * Sets <code>value</code> as attribute value for STILE_VITA using the alias name StileVita.
     * @param value value to set the STILE_VITA
     */
    public void setStileVita(Integer value) {
        setAttributeInternal(STILEVITA, value);
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
     * Gets the attribute value for DATA_RILEVAZIONE using the alias name DataRilevazione.
     * @return the DATA_RILEVAZIONE
     */
    public Date getDataRilevazione() {
        return (Date) getAttributeInternal(DATARILEVAZIONE);
    }

    /**
     * Sets <code>value</code> as attribute value for DATA_RILEVAZIONE using the alias name DataRilevazione.
     * @param value value to set the DATA_RILEVAZIONE
     */
    public void setDataRilevazione(Date value) {
        setAttributeInternal(DATARILEVAZIONE, value);
    }

    /**
     * Gets the attribute value for OPRILEVAZIONE using the alias name Oprilevazione.
     * @return the OPRILEVAZIONE
     */
    public Integer getOprilevazione() {
        return (Integer) getAttributeInternal(OPRILEVAZIONE);
    }

    /**
     * Sets <code>value</code> as attribute value for OPRILEVAZIONE using the alias name Oprilevazione.
     * @param value value to set the OPRILEVAZIONE
     */
    public void setOprilevazione(Integer value) {
        setAttributeInternal(OPRILEVAZIONE, value);
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
     * Gets the attribute value for CLASSE_ESAMI using the alias name ClasseEsami.
     * @return the CLASSE_ESAMI
     */
    public Integer getClasseEsami() {
        return (Integer) getAttributeInternal(CLASSEESAMI);
    }

    /**
     * Sets <code>value</code> as attribute value for CLASSE_ESAMI using the alias name ClasseEsami.
     * @param value value to set the CLASSE_ESAMI
     */
    public void setClasseEsami(Integer value) {
        setAttributeInternal(CLASSEESAMI, value);
    }

    /**
     * Gets the attribute value for GR_STILE using the alias name GrStile.
     * @return the GR_STILE
     */
    public String getGrStile() {
        return (String) getAttributeInternal(GRSTILE);
    }

    /**
     * Sets <code>value</code> as attribute value for GR_STILE using the alias name GrStile.
     * @param value value to set the GR_STILE
     */
    public void setGrStile(String value) {
        setAttributeInternal(GRSTILE, value);
    }

    /**
     * Gets the attribute value for GR_CLESAMI using the alias name GrClesami.
     * @return the GR_CLESAMI
     */
    public String getGrClesami() {
        return (String) getAttributeInternal(GRCLESAMI);
    }

    /**
     * Sets <code>value</code> as attribute value for GR_CLESAMI using the alias name GrClesami.
     * @param value value to set the GR_CLESAMI
     */
    public void setGrClesami(String value) {
        setAttributeInternal(GRCLESAMI, value);
    }

    /**
     * Gets the attribute value for ALIMENTAZIONE using the alias name Alimentazione.
     * @return the ALIMENTAZIONE
     */
    public Integer getAlimentazione() {
        return (Integer) getAttributeInternal(ALIMENTAZIONE);
    }

    /**
     * Sets <code>value</code> as attribute value for ALIMENTAZIONE using the alias name Alimentazione.
     * @param value value to set the ALIMENTAZIONE
     */
    public void setAlimentazione(Integer value) {
        setAttributeInternal(ALIMENTAZIONE, value);
    }

    /**
     * Gets the attribute value for GR_ALIMENT using the alias name GrAliment.
     * @return the GR_ALIMENT
     */
    public String getGrAliment() {
        return (String) getAttributeInternal(GRALIMENT);
    }

    /**
     * Sets <code>value</code> as attribute value for GR_ALIMENT using the alias name GrAliment.
     * @param value value to set the GR_ALIMENT
     */
    public void setGrAliment(String value) {
        setAttributeInternal(GRALIMENT, value);
    }

    /**
     * Gets the attribute value for PRESSIONE using the alias name Pressione.
     * @return the PRESSIONE
     */
    public Integer getPressione() {
        return (Integer) getAttributeInternal(PRESSIONE);
    }

    /**
     * Sets <code>value</code> as attribute value for PRESSIONE using the alias name Pressione.
     * @param value value to set the PRESSIONE
     */
    public void setPressione(Integer value) {
        setAttributeInternal(PRESSIONE, value);
    }

    /**
     * Gets the attribute value for GR_PRESSIONE using the alias name GrPressione.
     * @return the GR_PRESSIONE
     */
    public String getGrPressione() {
        return (String) getAttributeInternal(GRPRESSIONE);
    }

    /**
     * Sets <code>value</code> as attribute value for GR_PRESSIONE using the alias name GrPressione.
     * @param value value to set the GR_PRESSIONE
     */
    public void setGrPressione(String value) {
        setAttributeInternal(GRPRESSIONE, value);
    }

    /**
     * Gets the attribute value for BMI using the alias name Bmi.
     * @return the BMI
     */
    public Integer getBmi() {
        return (Integer) getAttributeInternal(BMI);
    }

    /**
     * Sets <code>value</code> as attribute value for BMI using the alias name Bmi.
     * @param value value to set the BMI
     */
    public void setBmi(Integer value) {
        setAttributeInternal(BMI, value);
    }

    /**
     * Gets the attribute value for GR_BMI using the alias name GrBmi.
     * @return the GR_BMI
     */
    public String getGrBmi() {
        return (String) getAttributeInternal(GRBMI);
    }

    /**
     * Sets <code>value</code> as attribute value for GR_BMI using the alias name GrBmi.
     * @param value value to set the GR_BMI
     */
    public void setGrBmi(String value) {
        setAttributeInternal(GRBMI, value);
    }

    /**
     * Gets the attribute value for CLASSE_PFAS using the alias name ClassePfas.
     * @return the CLASSE_PFAS
     */
    public Integer getClassePfas() {
        return (Integer) getAttributeInternal(CLASSEPFAS);
    }

    /**
     * Sets <code>value</code> as attribute value for CLASSE_PFAS using the alias name ClassePfas.
     * @param value value to set the CLASSE_PFAS
     */
    public void setClassePfas(Integer value) {
        setAttributeInternal(CLASSEPFAS, value);
    }

    /**
     * Gets the attribute value for GR_PFAS using the alias name GrPfas.
     * @return the GR_PFAS
     */
    public String getGrPfas() {
        return (String) getAttributeInternal(GRPFAS);
    }

    /**
     * Sets <code>value</code> as attribute value for GR_PFAS using the alias name GrPfas.
     * @param value value to set the GR_PFAS
     */
    public void setGrPfas(String value) {
        setAttributeInternal(GRPFAS, value);
    }

    /**
     * Gets the attribute value for DT_REFPFAS using the alias name DtRefpfas.
     * @return the DT_REFPFAS
     */
    public Date getDtRefpfas() {
        return (Date) getAttributeInternal(DTREFPFAS);
    }

    /**
     * Sets <code>value</code> as attribute value for DT_REFPFAS using the alias name DtRefpfas.
     * @param value value to set the DT_REFPFAS
     */
    public void setDtRefpfas(Date value) {
        setAttributeInternal(DTREFPFAS, value);
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
     * Gets the attribute value for DTAPP using the alias name Dtapp.
     * @return the DTAPP
     */
    public Date getDtapp() {
        return (Date) getAttributeInternal(DTAPP);
    }

    /**
     * Sets <code>value</code> as attribute value for DTAPP using the alias name Dtapp.
     * @param value value to set the DTAPP
     */
    public void setDtapp(Date value) {
        setAttributeInternal(DTAPP, value);
    }

    /**
     * Gets the attribute value for ORAAPP using the alias name Oraapp.
     * @return the ORAAPP
     */
    public Integer getOraapp() {
        return (Integer) getAttributeInternal(ORAAPP);
    }

    /**
     * Sets <code>value</code> as attribute value for ORAAPP using the alias name Oraapp.
     * @param value value to set the ORAAPP
     */
    public void setOraapp(Integer value) {
        setAttributeInternal(ORAAPP, value);
    }

    /**
     * Gets the attribute value for MINAPP using the alias name Minapp.
     * @return the MINAPP
     */
    public Integer getMinapp() {
        return (Integer) getAttributeInternal(MINAPP);
    }

    /**
     * Sets <code>value</code> as attribute value for MINAPP using the alias name Minapp.
     * @param value value to set the MINAPP
     */
    public void setMinapp(Integer value) {
        setAttributeInternal(MINAPP, value);
    }

    /**
     * Gets the associated <code>RowIterator</code> using master-detail link Ref_SoRefertopfasDatiView.
     */
    public RowIterator getRef_SoRefertopfasDatiView() {
        return (RowIterator)getAttributeInternal(REF_SOREFERTOPFASDATIVIEW);
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
