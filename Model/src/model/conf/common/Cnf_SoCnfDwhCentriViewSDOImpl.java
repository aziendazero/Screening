package model.conf.common;

import org.eclipse.persistence.sdo.SDODataObject;

@SuppressWarnings("serial")
public class Cnf_SoCnfDwhCentriViewSDOImpl extends SDODataObject implements Cnf_SoCnfDwhCentriViewSDO {

   public static final int START_PROPERTY_INDEX = 0;

   public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + 6;

   public Cnf_SoCnfDwhCentriViewSDOImpl() {}

   public java.lang.Integer getIdcentro() {
      return getInt(START_PROPERTY_INDEX + 0);
   }

   public void setIdcentro(java.lang.Integer value) {
      set(START_PROPERTY_INDEX + 0 , value);
   }

   public java.lang.String getUlss() {
      return getString(START_PROPERTY_INDEX + 1);
   }

   public void setUlss(java.lang.String value) {
      set(START_PROPERTY_INDEX + 1 , value);
   }

   public java.lang.String getTpscr() {
      return getString(START_PROPERTY_INDEX + 2);
   }

   public void setTpscr(java.lang.String value) {
      set(START_PROPERTY_INDEX + 2 , value);
   }

   public java.lang.String getCodice() {
      return getString(START_PROPERTY_INDEX + 3);
   }

   public void setCodice(java.lang.String value) {
      set(START_PROPERTY_INDEX + 3 , value);
   }

   public java.lang.String getDescrizione() {
      return getString(START_PROPERTY_INDEX + 4);
   }

   public void setDescrizione(java.lang.String value) {
      set(START_PROPERTY_INDEX + 4 , value);
   }

   public java.lang.String getTipo() {
      return getString(START_PROPERTY_INDEX + 5);
   }

   public void setTipo(java.lang.String value) {
      set(START_PROPERTY_INDEX + 5 , value);
   }

   public java.lang.String getDescrbreve() {
      return getString(START_PROPERTY_INDEX + 6);
   }

   public void setDescrbreve(java.lang.String value) {
      set(START_PROPERTY_INDEX + 6 , value);
   }


}

