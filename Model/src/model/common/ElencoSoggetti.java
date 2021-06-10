package model.common;


public interface ElencoSoggetti 
{

  /**
   * Per la stampa di un elenco dei soggetti: questo metodo deve restituire 
   * la stringa s, che va usata nella where caluse della selezione dei soggetta
   * da stampare nel seguente modo:
   * CODTS IN(s).
   * La stringa s può essre, a sua volta, una query oppure un elenco di numeri 
   * di tessera, a seconda dell'implementazione del ViewObject
   * @return 
   */
  public abstract String getInClause();
}