package model.soggetto;

import oracle.jbo.Key;
import oracle.jbo.server.EntityDefImpl;
import oracle.jbo.server.EntityImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Fri Feb 28 12:52:08 CET 2020
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class Sogg_SoChiaviImpl extends EntityImpl {
    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. DO NOT MODIFY.
     */
    public enum AttributesEnum {
        Codts {
            public Object get(Sogg_SoChiaviImpl obj) {
                return obj.getAttributeInternal(index());
            }

            public void put(Sogg_SoChiaviImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ulss {
            public Object get(Sogg_SoChiaviImpl obj) {
                return obj.getAttributeInternal(index());
            }

            public void put(Sogg_SoChiaviImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        DataCreazione {
            public Object get(Sogg_SoChiaviImpl obj) {
                return obj.getAttributeInternal(index());
            }

            public void put(Sogg_SoChiaviImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        OpCreazione {
            public Object get(Sogg_SoChiaviImpl obj) {
                return obj.getAttributeInternal(index());
            }

            public void put(Sogg_SoChiaviImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Chiave {
            public Object get(Sogg_SoChiaviImpl obj) {
                return obj.getAttributeInternal(index());
            }

            public void put(Sogg_SoChiaviImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Sogg_SoSoggetto {
            public Object get(Sogg_SoChiaviImpl obj) {
                return obj.getAttributeInternal(index());
            }

            public void put(Sogg_SoChiaviImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static final int firstIndex = 0;

        public abstract Object get(Sogg_SoChiaviImpl object);

        public abstract void put(Sogg_SoChiaviImpl object, Object value);

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


    public static final int CODTS = AttributesEnum.Codts.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int DATACREAZIONE = AttributesEnum.DataCreazione.index();
    public static final int OPCREAZIONE = AttributesEnum.OpCreazione.index();
    public static final int CHIAVE = AttributesEnum.Chiave.index();
    public static final int SOGG_SOSOGGETTO = AttributesEnum.Sogg_SoSoggetto.index();

    /**
     * This is the default constructor (do not remove).
     */
    public Sogg_SoChiaviImpl() {
    }

    /**
     * @param chiave key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(String chiave) {
        return new Key(new Object[] { chiave });
    }

    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        return EntityDefImpl.findDefObject("model.soggetto.Sogg_SoChiavi");
    }


}

