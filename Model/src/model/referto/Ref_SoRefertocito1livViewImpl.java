package model.referto;
import oracle.jbo.server.ViewObjectImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Ref_SoRefertocito1livViewImpl extends ViewObjectImpl {
  /**
   * 
   *  This is the default constructor (do not remove)
   */
  public Ref_SoRefertocito1livViewImpl()
  {
  }

    /**
     * Returns the variable value for IdrefertoVar.
     * @return variable value for IdrefertoVar
     */
    public Long getIdrefertoVar() {
        return (Long)ensureVariableManager().getVariableValue("IdrefertoVar");
    }

    /**
     * Sets <code>value</code> for variable IdrefertoVar.
     * @param value value to bind as IdrefertoVar
     */
    public void setIdrefertoVar(Long value) {
        ensureVariableManager().setVariableValue("IdrefertoVar", value);
    }
}
