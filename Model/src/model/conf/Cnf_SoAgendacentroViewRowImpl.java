package model.conf;


import model.conf.common.Cnf_SoAgendacentroViewRow;

import oracle.jbo.domain.Number;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.ViewRowImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Thu Aug 24 14:49:15 CEST 2017
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class Cnf_SoAgendacentroViewRowImpl extends ViewRowImpl implements Cnf_SoAgendacentroViewRow {


    public static final int ENTITY_CNF_SOAGENDACENTRO = 0;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. DO NOT MODIFY.
     */
    public enum AttributesEnum {
        Idagenda {
            public Object get(Cnf_SoAgendacentroViewRowImpl obj) {
                return obj.getIdagenda();
            }

            public void put(Cnf_SoAgendacentroViewRowImpl obj, Object value) {
                obj.setIdagenda((Integer) value);
            }
        }
        ,
        Idcentro {
            public Object get(Cnf_SoAgendacentroViewRowImpl obj) {
                return obj.getIdcentro();
            }

            public void put(Cnf_SoAgendacentroViewRowImpl obj, Object value) {
                obj.setIdcentro((Integer) value);
            }
        }
        ,
        Ggsettimana {
            public Object get(Cnf_SoAgendacentroViewRowImpl obj) {
                return obj.getGgsettimana();
            }

            public void put(Cnf_SoAgendacentroViewRowImpl obj, Object value) {
                obj.setGgsettimana((Integer) value);
            }
        }
        ,
        Oraapp {
            public Object get(Cnf_SoAgendacentroViewRowImpl obj) {
                return obj.getOraapp();
            }

            public void put(Cnf_SoAgendacentroViewRowImpl obj, Object value) {
                obj.setOraapp((Integer) value);
            }
        }
        ,
        Minapp {
            public Object get(Cnf_SoAgendacentroViewRowImpl obj) {
                return obj.getMinapp();
            }

            public void put(Cnf_SoAgendacentroViewRowImpl obj, Object value) {
                obj.setMinapp((Integer) value);
            }
        }
        ,
        Disporaria {
            public Object get(Cnf_SoAgendacentroViewRowImpl obj) {
                return obj.getDisporaria();
            }

            public void put(Cnf_SoAgendacentroViewRowImpl obj, Object value) {
                obj.setDisporaria((Integer) value);
            }
        }
        ,
        Nascosto {
            public Object get(Cnf_SoAgendacentroViewRowImpl obj) {
                return obj.getNascosto();
            }

            public void put(Cnf_SoAgendacentroViewRowImpl obj, Object value) {
                obj.setNascosto((Integer) value);
            }
        }
        ;
        static AttributesEnum[] vals = null;
        ;
        private static final int firstIndex = 0;

        public abstract Object get(Cnf_SoAgendacentroViewRowImpl object);

        public abstract void put(Cnf_SoAgendacentroViewRowImpl object, Object value);

        public int index() {
            return AttributesEnum.firstIndex() + ordinal();
        }

        public static final int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return AttributesEnum.firstIndex() + AttributesEnum.staticValues().length;
        }

        public static final AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = AttributesEnum.values();
            }
            return vals;
        }
    }


    public static final int IDAGENDA = AttributesEnum.Idagenda.index();
    public static final int IDCENTRO = AttributesEnum.Idcentro.index();
    public static final int GGSETTIMANA = AttributesEnum.Ggsettimana.index();
    public static final int ORAAPP = AttributesEnum.Oraapp.index();
    public static final int MINAPP = AttributesEnum.Minapp.index();
    public static final int DISPORARIA = AttributesEnum.Disporaria.index();
    public static final int NASCOSTO = AttributesEnum.Nascosto.index();

    /**
     * This is the default constructor (do not remove).
     */
    public Cnf_SoAgendacentroViewRowImpl() {
    }

    /**
     * Gets Cnf_SoAgendacentro entity object.
     * @return the Cnf_SoAgendacentro
     */
    public Cnf_SoAgendacentroImpl getCnf_SoAgendacentro() {
        return (Cnf_SoAgendacentroImpl) getEntity(ENTITY_CNF_SOAGENDACENTRO);
    }

    /**
     * Gets the attribute value for IDAGENDA using the alias name Idagenda.
     * @return the IDAGENDA
     */
    public Integer getIdagenda() {
        return (Integer) getAttributeInternal(IDAGENDA);
    }

    /**
     * Sets <code>value</code> as attribute value for IDAGENDA using the alias name Idagenda.
     * @param value value to set the IDAGENDA
     */
    public void setIdagenda(Integer value) {
        setAttributeInternal(IDAGENDA, value);
    }

    /**
     * Gets the attribute value for IDCENTRO using the alias name Idcentro.
     * @return the IDCENTRO
     */
    public Integer getIdcentro() {
        return (Integer) getAttributeInternal(IDCENTRO);
    }

    /**
     * Sets <code>value</code> as attribute value for IDCENTRO using the alias name Idcentro.
     * @param value value to set the IDCENTRO
     */
    public void setIdcentro(Integer value) {
        setAttributeInternal(IDCENTRO, value);
    }

    /**
     * Gets the attribute value for GGSETTIMANA using the alias name Ggsettimana.
     * @return the GGSETTIMANA
     */
    public Integer getGgsettimana() {
        return (Integer) getAttributeInternal(GGSETTIMANA);
    }

    /**
     * Sets <code>value</code> as attribute value for GGSETTIMANA using the alias name Ggsettimana.
     * @param value value to set the GGSETTIMANA
     */
    public void setGgsettimana(Integer value) {
        setAttributeInternal(GGSETTIMANA, value);
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
     * Gets the attribute value for DISPORARIA using the alias name Disporaria.
     * @return the DISPORARIA
     */
    public Integer getDisporaria() {
        return (Integer) getAttributeInternal(DISPORARIA);
    }

    /**
     * Sets <code>value</code> as attribute value for DISPORARIA using the alias name Disporaria.
     * @param value value to set the DISPORARIA
     */
    public void setDisporaria(Integer value) {
        setAttributeInternal(DISPORARIA, value);
    }


    /**
     * Gets the attribute value for NASCOSTO using the alias name Nascosto.
     * @return the NASCOSTO
     */
    public Integer getNascosto() {
        return (Integer) getAttributeInternal(NASCOSTO);
    }

    /**
     * Sets <code>value</code> as attribute value for NASCOSTO using the alias name Nascosto.
     * @param value value to set the NASCOSTO
     */
    public void setNascosto(Integer value) {
        setAttributeInternal(NASCOSTO, value);
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
