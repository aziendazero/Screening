package model.commons;

import java.util.List;

public interface AlgoritmoQuestionario {
    /**
     * Valorizza i campi da calcolare nel questionario.
     * @param questionario Questonario da completare.
     * @return Eventuale risultato.
     */
    public Object calcola(Questionario questionario);

    /**
     * Restituisce messaggi relativi all'ultima invocazione di calcola().
     * @return Lista di String. Non è mai null.
     */
    public List getMessaggi();
}
