package model.commons;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Questionario implements Serializable {
    @SuppressWarnings("compatibility:6189615731468768824")
    private static final long serialVersionUID = 8814159248089722651L;
    private String nome;
	private String descrizione;
	private long idReferto;
	private List domande; // List<DomandaQuestionario>
	private boolean dirty;
	private boolean readOnly;
	private String sesso;
        private Map range;

	public void setNome(String nome) {
		this.nome = nome;
	}


	public String getNome() {
		return nome;
	}


	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}


	public String getDescrizione() {
		return descrizione;
	}


	public List getDomande1liv() {
		return domande;
	}
	
	public void setDomande1liv(List domande) {
		this.domande = domande;
	}
	
	public List getDomandeTuttiLiv() {
		List result = new ArrayList();
		if (domande != null) {
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
	
	public void setDirty(boolean value) {
		this.dirty = value;
		if (!this.dirty) {
			if (domande != null) {
				for (Iterator i = getDomandeTuttiLiv().iterator(); i.hasNext();) {
					DomandaQuestionario domanda = (DomandaQuestionario)i.next();
					domanda.setDirty(false);
				}
			}
		}
	}
	
	public boolean isDirty() {
		if (!dirty) {
			if (domande != null) {
				for (Iterator i = getDomandeTuttiLiv().iterator(); i.hasNext();) {
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


	public void setIdReferto(long idReferto) {
		this.idReferto = idReferto;
	}


	public long getIdReferto() {
		return idReferto;
	}


	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}


	public boolean isReadOnly() {
		return readOnly;
	}


	public void setSesso(String sesso) {
		this.sesso = sesso;
	}


	public String getSesso() {
		return sesso;
	}


  public void setRange(Map range)
  {
    this.range = range;
  }


  public Map getRange()
  {
    return range;
  }
}
