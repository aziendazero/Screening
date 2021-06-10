package model.commons;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class DomandaQuestionario implements Serializable, Cloneable {
    @SuppressWarnings("compatibility:-8427344200444358572")
    private static final long serialVersionUID = 2934066161127551058L;
    
	public static final String DATA = "DATA";
	public static final String NUMERO = "NUM";
	public static final String SELEZIONE = "SEL";
	public static final String SELEZIONE_MULTIPLA = "MUL";
	public static final String TESTO = "TESTO";
	public static final String TITOLO = "TITOLO";
        public static final String INTERO = "INT";
        


    private String sezione;
	private long id;
	private long capostipite;
	private String testo;
	private String valore;
	private Set valori; // lista valori per la selezione multipla
	private boolean obbligatorio;
	private String tipo;
	private int dimensione;
	private boolean modificabile;
	private String gruppo;
	private List domande; // List<DomandaQuestionario>
	private String note;
	private String codice;
	private boolean dirty;
  private String maxValue;
  private String minValue;
	
	public void setSezione(String sezione) {
		this.sezione = sezione;
	}
	
	public String getSezione() {
		return sezione;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	
	public void setTesto(String testo) {
		this.testo = testo;
	}
	
	public String getTesto() {
		return testo;
	}
	
	public void setValore(String valore) {
		if ((valore != null && !valore.equals(this.valore)) || (this.valore != null && !this.valore.equals(valore))) {
			setDirty(true);
			this.valore = valore;
			this.valori = null;
		}
	}
	
	public String getValore() {
		return valore;
	}
	
	public Set getValori() {
		if (this.valori == null) {
			this.valori = new TreeSet();
			if (this.valore != null) {
				this.valori.add(valore);
			}
		}
		return this.valori;
	}
	
	public void addValore(String valore) {
		if (valore != null) {
			getValori().add(valore);
		
			// Aggiorno valore
			setValori(this.valori);
		}
	}
	
	public void setValori(Set valori) {
		if (valori != this.valori) {
			this.valori = valori;
			setDirty(true);
		}
		
		// Costruisco valore come concatenazione dei valori
		this.valore = null;
		for (Iterator i = valori.iterator(); i.hasNext();) {
			String valore = (String)i.next();
			if (this.valore == null) {
				this.valore = valore;
			} else {
				this.valore += "|" + valore;
			}
		}
	}
	
	public void setObbligatorio(boolean obbligatorio) {
		this.obbligatorio = obbligatorio;
	}
	
	public boolean isObbligatorio() {
		return obbligatorio;
	}
	
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	public String getTipo() {
		return tipo;
	}
	
	public void setDimensione(int dimensione) {
		this.dimensione = dimensione;
	}
	
	public int getDimensione() {
		return dimensione;
	}
	
	public void setModificabile(boolean modificabile) {
		this.modificabile = modificabile;
	}
	
	public boolean isModificabile() {
		return modificabile;
	}
	
	public void setGruppo(String gruppo) {
		this.gruppo = gruppo;
	}
	
	public String getGruppo() {
		return gruppo;
	}
	
	public void setNote(String note) {
		this.note = note;
	}
	
	public String getNote() {
		return note;
	}
	
	public List getDomande1liv() {
		return domande;
	}
	
	public List getDomandeTuttiLiv() {
		List result = null;
		if (domande != null) {
			result = new ArrayList();
			for (Iterator i = domande.iterator(); i.hasNext();) {
				DomandaQuestionario domanda = (DomandaQuestionario)i.next();
				result.add(domanda);
				List domande2 = domanda.getDomandeTuttiLiv();
				if (domande2 != null) {
					result.addAll(domande2);
				}
			}
		}
		return result;
	}
	
	public void setDomande1liv(List domande) {
		this.domande = domande;
	}
	
	public void setDirty(boolean value) {
		this.dirty = value;
		if (!this.dirty) {
			if (domande != null) {
				for (Iterator i = domande.iterator(); i.hasNext();) {
					DomandaQuestionario domanda = (DomandaQuestionario)i.next();
					domanda.setDirty(false);
				}
			}
		}
	}
	
	public boolean isDirty() {
		if (!dirty) {
			if (domande != null) {
				for (Iterator i = domande.iterator(); i.hasNext();) {
					DomandaQuestionario domanda = (DomandaQuestionario)i.next();
					if (domanda.isDirty()) {
						dirty = true;
						break;
					}
				}
			}
		}
		return dirty;
	}


	public void setCodice(String codice) {
		this.codice = codice;
	}


	public String getCodice() {
		return codice;
	}


	public void setCapostipite(long capostipite) {
		this.capostipite = capostipite;
	}


	public long getCapostipite() {
		return capostipite;
	}
	
	// Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}


  public void setMaxValue(String maxValue)
  {
    this.maxValue = maxValue;
  }


  public String getMaxValue()
  {
    return maxValue;
  }


  public void setMinValue(String minValue)
  {
    this.minValue = minValue;
  }


  public String getMinValue()
  {
    return minValue;
  }
}
