package model.referto;
import oracle.jbo.server.ViewRowImpl;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.domain.Date;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class RefCa_SoRefertocardioBckViewRowImpl extends ViewRowImpl {


    public static final int ENTITY_REFCA_SOREFERTOCARDIOBCK = 0;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. DO NOT MODIFY.
     */
    protected enum AttributesEnum {
        Idreferto,
        Dtinserimento,
        Opinserimento,
        Dtultimamodifica,
        Opultmodifica,
        Ulss,
        Tpscr,
        Idinvito,
        Codts,
        DataRilevazione,
        Oprilevazione,
        Idsugg,
        Idcnfquest,
        Completo,
        Idallegato,
        Dtcreazione,
        Dtspedizione,
        Note,
        Dtcanc,
        Opcanc;
        static AttributesEnum[] vals = null;
        ;
        private static final int        firstIndex = 0;

        protected int index() {
            return AttributesEnum.firstIndex() + ordinal();
        }

        protected static final int firstIndex() {
            return firstIndex;
        }

        protected static int count() {
            return AttributesEnum.firstIndex() + AttributesEnum.staticValues().length;
        }

        protected static final AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = AttributesEnum.values();
            }
            return vals;
        }
    }


    public static final int IDREFERTO = AttributesEnum.Idreferto.index();
    public static final int DTINSERIMENTO = AttributesEnum.Dtinserimento.index();
    public static final int OPINSERIMENTO = AttributesEnum.Opinserimento.index();
    public static final int DTULTIMAMODIFICA = AttributesEnum.Dtultimamodifica.index();
    public static final int OPULTMODIFICA = AttributesEnum.Opultmodifica.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int IDINVITO = AttributesEnum.Idinvito.index();
    public static final int CODTS = AttributesEnum.Codts.index();
    public static final int DATARILEVAZIONE = AttributesEnum.DataRilevazione.index();
    public static final int OPRILEVAZIONE = AttributesEnum.Oprilevazione.index();
    public static final int IDSUGG = AttributesEnum.Idsugg.index();
    public static final int IDCNFQUEST = AttributesEnum.Idcnfquest.index();
    public static final int COMPLETO = AttributesEnum.Completo.index();
    public static final int IDALLEGATO = AttributesEnum.Idallegato.index();
    public static final int DTCREAZIONE = AttributesEnum.Dtcreazione.index();
    public static final int DTSPEDIZIONE = AttributesEnum.Dtspedizione.index();
    public static final int NOTE = AttributesEnum.Note.index();
    public static final int DTCANC = AttributesEnum.Dtcanc.index();
    public static final int OPCANC = AttributesEnum.Opcanc.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
	public RefCa_SoRefertocardioBckViewRowImpl() {
	}

    /**
     * Gets RefCa_SoRefertocardioBck entity object.
     * @return the RefCa_SoRefertocardioBck
     */
    public RefCa_SoRefertocardioBckImpl getRefCa_SoRefertocardioBck() {
        return (RefCa_SoRefertocardioBckImpl) getEntity(ENTITY_REFCA_SOREFERTOCARDIOBCK);
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
     * Gets the attribute value for DTULTIMAMODIFICA using the alias name Dtultimamodifica.
     * @return the DTULTIMAMODIFICA
     */
    public Date getDtultimamodifica() {
        return (Date) getAttributeInternal(DTULTIMAMODIFICA);
    }

    /**
     * Sets <code>value</code> as attribute value for DTULTIMAMODIFICA using the alias name Dtultimamodifica.
     * @param value value to set the DTULTIMAMODIFICA
     */
    public void setDtultimamodifica(Date value) {
        setAttributeInternal(DTULTIMAMODIFICA, value);
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
     * Gets the attribute value for DTCANC using the alias name Dtcanc.
     * @return the DTCANC
     */
    public Date getDtcanc() {
        return (Date) getAttributeInternal(DTCANC);
    }

    /**
     * Sets <code>value</code> as attribute value for DTCANC using the alias name Dtcanc.
     * @param value value to set the DTCANC
     */
    public void setDtcanc(Date value) {
        setAttributeInternal(DTCANC, value);
    }

    /**
     * Gets the attribute value for OPCANC using the alias name Opcanc.
     * @return the OPCANC
     */
    public String getOpcanc() {
        return (String) getAttributeInternal(OPCANC);
    }

    /**
     * Sets <code>value</code> as attribute value for OPCANC using the alias name Opcanc.
     * @param value value to set the OPCANC
     */
    public void setOpcanc(String value) {
        setAttributeInternal(OPCANC, value);
    }


}
