package model.conf;
import oracle.jbo.server.ViewObjectImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Cnf_SoCnfFunzViewImpl extends ViewObjectImpl {
  /**
   * 
   *  This is the default constructor (do not remove)
   */
  public Cnf_SoCnfFunzViewImpl()
  {
  }

	/**
	 * Returns the variable value for UlssVar.
	 * @return variable value for UlssVar
	 */
	public String getUlssVar() {
		return (String)ensureVariableManager().getVariableValue("UlssVar");
	}

	/**
	 * Sets <code>value</code> for variable UlssVar.
	 * @param value value to bind as UlssVar
	 */
	public void setUlssVar(String value) {
		ensureVariableManager().setVariableValue("UlssVar", value);
	}


}
