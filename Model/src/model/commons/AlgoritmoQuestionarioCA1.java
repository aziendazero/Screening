package model.commons;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import oracle.adf.share.logging.ADFLogger;

public class AlgoritmoQuestionarioCA1 implements AlgoritmoQuestionario {
    private List messaggi = new ArrayList(); // List<String>
    private boolean campiObbligatoriMancanti;

    private static final int INTENSITA_BASSA = 1;
    private static final int INTENSITA_MODERATA = 2;
    private static final int INTENSITA_VIGOROSA = 3;

    private static final int CATEGORIA_BASSA = 1;
    private static final int CATEGORIA_MODERATA = 2;
    private static final int CATEGORIA_ALTA = 3;

    private static final int FREQUENZA_GIORNALIERA = 1;
    private static final int FREQUENZA_SETTIMANALE = 2;
    private static final int FREQUENZA_MENSILE = 3;

    private static final String TIPOLOGIA_POST_PRANDIALE = "2";

    private static final ADFLogger logger = ADFLogger.createADFLogger("model.commons.AlgoritmoQuestionarioCA1");
    private static final NumberFormat metFormat =
        new DecimalFormat("######0.##", new DecimalFormatSymbols(Locale.ITALY));
    private static final NumberFormat bmiFormat = metFormat;
    private static final NumberFormat numberFormat =
        new DecimalFormat("####.###", new DecimalFormatSymbols(Locale.ITALY));

    private class Attivita {
        public float frequenza;
        public float minuti;
        public int intensita;
        public float met;
        public DomandaQuestionario domandaMet;
    }

    /**
     * Calcola i campi non valorizzabili dall'utente.
     * @return La classe di appartenenza del soggetto, sotto forma di id suggerimento. null se non calcolabile.
     * @param questionario
     */
    public Object calcola(Questionario questionario) {

        campiObbligatoriMancanti = false;

        // Campi di input
        Map mapAttivita = new HashMap(); // List<Attivita>
        float sistolica1 = -1;
        float sistolica2 = -1;
        float diastolica1 = -1;
        float diastolica2 = -1;
        float peso = -1;
        float altezza = -1;
        boolean terapiaInAtto = false;
        int frequenzaFrutta = -1;
        float quantitaFrutta = -1;
        int frequenzaAlcol = -1;
        float quantitaAlcol = -1; // ml
        boolean fumo = false;
        float circonferenzaVita = -1; // cm
        String tipologiaGlicemia = null;
        float glicemia = -1;
        float colesterolo = -1;

        // Campi di output
        DomandaQuestionario domandaMetTotale = null;
        DomandaQuestionario domandaCategoriaTotale = null;
        DomandaQuestionario domandaSistolicaUsata = null;
        DomandaQuestionario domandaDiastolicaUsata = null;
        DomandaQuestionario domandaBMI = null;

        // Estrapolo i dati dal questionario
        // Raggruppo i dati delle attività per capostipite in un Map la cui chiave è il capostipite
        List domande = questionario.getDomandeTuttiLiv();
        for (Iterator i = domande.iterator(); i.hasNext();) {
            DomandaQuestionario domanda = (DomandaQuestionario) i.next();

            // Controlli formali
            controlla(domanda);

            String codice = domanda.getCodice();

            // Campi di INPUT

            // Attività fisica - frequenza
            if ("AF_FR".equals(codice)) {
                Attivita attivita = (Attivita) mapAttivita.get(new Long(domanda.getCapostipite()));
                if (attivita == null) {
                    attivita = new Attivita();
                    mapAttivita.put(new Long(domanda.getCapostipite()), attivita);
                }
                try {
                    attivita.frequenza = numberFormat.parse(domanda.getValore()).floatValue();
                } catch (Exception e) {
                    attivita.frequenza = -1;
                }
                continue;
            }
            // Attività fisica - minuti al giorno
            if ("AF_MIN".equals(codice)) {
                Attivita attivita = (Attivita) mapAttivita.get(new Long(domanda.getCapostipite()));
                if (attivita == null) {
                    attivita = new Attivita();
                    mapAttivita.put(new Long(domanda.getCapostipite()), attivita);
                }
                try {
                    attivita.minuti = numberFormat.parse(domanda.getValore()).floatValue();
                } catch (Exception e) {
                    attivita.minuti = -1;
                }
                continue;
            }
            // Attività fisica - intensità
            if ("AF_INTEN".equals(codice)) {
                Attivita attivita = (Attivita) mapAttivita.get(new Long(domanda.getCapostipite()));
                if (attivita == null) {
                    attivita = new Attivita();
                    mapAttivita.put(new Long(domanda.getCapostipite()), attivita);
                }
                try {
                    attivita.intensita = numberFormat.parse(domanda.getValore()).intValue();
                } catch (Exception e) {
                    attivita.intensita = -1;
                }
                continue;
            }
            // Pressione diastolica 1a misurazione
            if ("DIA1".equals(codice)) {
                if (domanda.getValore() != null) {
                    try {
                        diastolica1 = numberFormat.parse(domanda.getValore()).floatValue();
                    } catch (ParseException e) {
                        diastolica1 = -1;
                    }
                }
            }
            // Pressione diastolica 2a misurazione
            if ("DIA2".equals(codice)) {
                if (domanda.getValore() != null) {
                    try {
                        diastolica2 = numberFormat.parse(domanda.getValore()).floatValue();
                    } catch (ParseException e) {
                        diastolica2 = -1;
                    }
                }
            }
            // Pressione sistolica 1a misurazione
            if ("SYS1".equals(codice)) {
                if (domanda.getValore() != null) {
                    try {
                        sistolica1 = numberFormat.parse(domanda.getValore()).floatValue();
                    } catch (ParseException e) {
                        sistolica1 = -1;
                    }
                }
            }
            // Pressione sistolica 2a misurazione
            if ("SYS2".equals(codice)) {
                if (domanda.getValore() != null) {
                    try {
                        sistolica2 = numberFormat.parse(domanda.getValore()).floatValue();
                    } catch (ParseException e) {
                        sistolica2 = -1;
                    }
                }
            }
            // Peso in Kg
            if ("PESO".equals(codice)) {
                if (domanda.getValore() != null) {
                    try {
                        peso = numberFormat.parse(domanda.getValore()).floatValue();
                    } catch (ParseException e) {
                        peso = -1;
                    }
                }
            }
            // Altezza in cm
            if ("ALTEZZA".equals(codice)) {
                if (domanda.getValore() != null) {
                    try {
                        altezza = numberFormat.parse(domanda.getValore()).floatValue();
                    } catch (ParseException e) {
                        altezza = -1;
                    }
                }
            }
            if ("TERAPIA".equals(codice)) {
                if ("2".equals(domanda.getValore())) {
                    terapiaInAtto = true;
                }
            }
            // Frequenza frutta e verdura
            if ("FRUTTA_FR".equals(codice)) {
                if (domanda.getValore() != null) {
                    try {
                        frequenzaFrutta = numberFormat.parse(domanda.getValore()).intValue();
                    } catch (ParseException e) {
                        frequenzaFrutta = -1;
                    }
                }
            }
            // Quantità frutta e verdura
            if ("FRUTTA_NUM".equals(codice)) {
                if (domanda.getValore() != null) {
                    try {
                        quantitaFrutta = numberFormat.parse(domanda.getValore()).floatValue();
                    } catch (ParseException e) {
                        quantitaFrutta = -1;
                    }
                }
            }
            // Frequenza alcol
            if ("ALCOL_FR".equals(codice)) {
                if (domanda.getValore() != null) {
                    try {
                        frequenzaAlcol = numberFormat.parse(domanda.getValore()).intValue();
                    } catch (ParseException e) {
                        frequenzaAlcol = -1;
                    }
                }
            }
            // Quantità alcol (ml)
            if ("ALCOL_NUM".equals(codice)) {
                if (domanda.getValore() != null) {
                    try {
                        quantitaAlcol = numberFormat.parse(domanda.getValore()).floatValue();
                    } catch (ParseException e) {
                        quantitaAlcol = -1;
                    }
                }
            }
            // Fumo
            if ("FUMO".equals(codice)) {
                fumo = "2".equals(domanda.getValore());
            }
            // Circonferenza vita
            if ("CIRCONF".equals(codice)) {
                if (domanda.getValore() != null) {
                    try {
                        circonferenzaVita = numberFormat.parse(domanda.getValore()).floatValue();
                    } catch (ParseException e) {
                        circonferenzaVita = -1;
                    }
                }
            }
            // Tipologia glicemia
            if ("GLICEM_TIP".equals(codice)) {
                tipologiaGlicemia = domanda.getValore();
            }
            // Glicemia
            if ("GLICEM_NUM".equals(codice)) {
                if (domanda.getValore() != null) {
                    try {
                        glicemia = numberFormat.parse(domanda.getValore()).floatValue();
                    } catch (ParseException e) {
                        glicemia = -1;
                    }
                }
            }
            // Colesterolo
            if ("COLESTEROL".equals(codice)) {
                if (domanda.getValore() != null) {
                    try {
                        colesterolo = numberFormat.parse(domanda.getValore()).floatValue();
                    } catch (ParseException e) {
                        colesterolo = -1;
                    }
                }
            }

            // Campi di OUTPUT

            // Attività fisica - MET
            if ("MET".equals(codice)) {
                Attivita attivita = (Attivita) mapAttivita.get(new Long(domanda.getCapostipite()));
                if (attivita == null) {
                    attivita = new Attivita();
                    mapAttivita.put(new Long(domanda.getCapostipite()), attivita);
                }
                attivita.domandaMet = domanda;
                continue;
            }
            // Attività fisica totale
            if ("AF_TOT".equals(codice)) {
                domandaCategoriaTotale = domanda;
            }
            if ("MET_TOT".equals(codice)) {
                domandaMetTotale = domanda;
            }
            // Pressione sistolica
            if ("SYS_USED".equals(codice)) {
                domandaSistolicaUsata = domanda;
            }
            // Pressione diastolica
            if ("DIA_USED".equals(codice)) {
                domandaDiastolicaUsata = domanda;
            }
            if ("BMI".equals(codice)) {
                domandaBMI = domanda;
            }
        }

        // Se mancano campi obbligatori, non faccio il calcolo dei campi da calcolare
        if (campiObbligatoriMancanti) {
            return null;
        }

        // Valorizzo i valori MET di ciascuna attività
        float metTotale = 0;
        for (Iterator i = mapAttivita.values().iterator(); i.hasNext();) {
            Attivita attivita = (Attivita) i.next();

            // Se manca un'informazione necessaria al calcolo, ignoro l'attività e la elimino dalla lista
            if (attivita.minuti < 0 || attivita.frequenza < 0 || attivita.intensita < 0) {
                i.remove();
            } else {
                attivita.met = attivita.minuti * attivita.frequenza * getMet(attivita.intensita);
                attivita.domandaMet.setValore(metFormat.format(attivita.met));
                metTotale += attivita.met;
            }
        }

        // Valorizzo MET totale
        if (domandaMetTotale != null) {
            domandaMetTotale.setValore(metFormat.format(metTotale));
        }

        // Calcolo categoria attività fisica
        int categoria = CATEGORIA_BASSA;

        // Sommatorie delle attività vigorose
        float metVigorosa = 0;
        int frequenzaVigorosa = 0;
        int minutiVigorosa = 0;

        // Sommatorie delle attività moderate
        float metModerata = 0;
        int frequenzaModerata = 0;
        int minutiModerata = 0;

        // Sommatorie delle attività basse
        float metBassa = 0;
        int frequenzaBassa = 0;
        int minutiBassa = 0;

        // Sommatorie di tutte le intensità
        float met = 0;
        int frequenza = 0;

        for (Iterator i = mapAttivita.values().iterator(); i.hasNext();) {
            Attivita attivita = (Attivita) i.next();
            if (attivita.intensita == INTENSITA_VIGOROSA) {
                metVigorosa += attivita.met;
                frequenzaVigorosa += attivita.frequenza;
                minutiVigorosa += attivita.minuti;
            }
            if (attivita.intensita == INTENSITA_MODERATA) {
                metModerata += attivita.met;
                frequenzaModerata += attivita.frequenza;
                minutiModerata += attivita.minuti;
            }
            if (attivita.intensita == INTENSITA_BASSA) {
                metBassa += attivita.met;
                frequenzaBassa += attivita.frequenza;
                minutiBassa += attivita.minuti;
            }
            met += attivita.met;
            frequenza += attivita.frequenza;
        }

        // Verifico se categoria alta
        // Attività vigorosa per almeno 3 giorni, almeno 1500 MET
        if (frequenzaVigorosa >= 3 && metVigorosa >= 1500) {
            categoria = CATEGORIA_ALTA;
        } else {
            // Almeno 7 giorni, almeno 3000 MET
            if (frequenza >= 7 && met >= 3000) {
                categoria = CATEGORIA_ALTA;
            }
        }

        // Verifico se categoria moderata
        if (categoria < CATEGORIA_MODERATA) {
            // Attività vigorosa per almeno 3 giorni, per almeno 20 minuti al giorno
            if (frequenzaVigorosa >= 3 && minutiVigorosa >= 20) {
                categoria = CATEGORIA_MODERATA;
            } else {
                // Attività moderata per almeno 5 giorni e 30 minuti al giorno
                if (frequenzaModerata >= 5 && minutiModerata >= 30) {
                    categoria = CATEGORIA_MODERATA;
                } else {
                    // Attività qualsiasi per almeno 5 giorni e MET >=600
                    if ((frequenzaModerata + frequenzaVigorosa) >= 5 && (minutiModerata + minutiVigorosa) >= 30 &&
                        met >= 600) {
                        categoria = CATEGORIA_MODERATA;
                    }
                }
            }
        }

        // Codifico la categoria
        String categoriaCodificata;
        switch (categoria) {
        case CATEGORIA_BASSA:
            categoriaCodificata = "Lieve";
            break;
        case CATEGORIA_MODERATA:
            categoriaCodificata = "Moderata";
            break;
        case CATEGORIA_ALTA:
            categoriaCodificata = "Alta";
            break;
        default:
            categoriaCodificata = "";
        }

        if (domandaCategoriaTotale != null) {
            domandaCategoriaTotale.setValore(categoriaCodificata);
        }

        // Scelgo pressione diastolica e sistolica tra le due letture
        // valore <= 0 significa dato non presente
        float diastolica = 0;
        float sistolica = 0;

        // Se manca la seconda misurazione, considero valida la prima
        if (diastolica2 <= 0 || sistolica2 <= 0) {
            diastolica = diastolica1;
            sistolica = sistolica1;
        }

        // E' presente la seconda misurazione
        else {

            // Considero la seconda misurazione, se non è fuori norma
            if (diastolica2 < 90 && sistolica2 < 140) {
                diastolica = diastolica2;
                sistolica = sistolica2;
            }

            // La seconda misurazione è fuori norma
            else {

                // Considero quella che ha diastolica maggiore
                if (diastolica1 > diastolica2) {
                    diastolica = diastolica1;
                    sistolica = sistolica1;
                } else if (diastolica2 > diastolica1) {
                    diastolica = diastolica2;
                    sistolica = sistolica2;
                }

                // Le diastoliche sono uguali
                else {
                    // Considero la sistolica maggiore
                    if (sistolica1 > sistolica2) {
                        diastolica = diastolica1;
                        sistolica = sistolica1;
                    } else {
                        diastolica = diastolica2;
                        sistolica = sistolica2;
                    }
                }
            }
        }
        if (domandaDiastolicaUsata != null) {
            domandaDiastolicaUsata.setValore(numberFormat.format(diastolica));
        }
        if (domandaSistolicaUsata != null) {
            domandaSistolicaUsata.setValore(numberFormat.format(sistolica));
        }

        // Calcolo BMI peso(Kg) / (altezza(m) ^ 2)
        float altezzaM = ((float) altezza) / 100;
        float bmi = peso / (altezzaM * altezzaM);
        if (domandaBMI != null) {
            domandaBMI.setValore(bmiFormat.format(bmi));
        }

        // Calcolo classe di appartenenza (id suggerimento)
        // 1 = classe A
        // 2 = classe B
        // 3 = classe C
        // 4 = classe C1
        // 5 = classe D
        long classe = 0;
        calcolaClasse:
        {
            if (terapiaInAtto) {
                classe = 5;
                break calcolaClasse;
            }

            // Calcolo lo stile di vita
            boolean stileDiVitaAdeguato = true;
            calcolaStileDiVita:
            {

                // Attività fisica
                if (categoria == CATEGORIA_BASSA) {
                    stileDiVitaAdeguato = false;
                    break calcolaStileDiVita;
                }

                // Frutta e verdura
                if (frequenzaFrutta == FREQUENZA_SETTIMANALE || frequenzaFrutta == FREQUENZA_MENSILE) {
                    stileDiVitaAdeguato = false;
                    break calcolaStileDiVita;
                }
                if (frequenzaFrutta == FREQUENZA_GIORNALIERA && quantitaFrutta < 2) {
                    stileDiVitaAdeguato = false;
                    break calcolaStileDiVita;
                }

                // Alcol (più di 3 bicchieri di vino al giorno)
                if (frequenzaAlcol == FREQUENZA_GIORNALIERA && quantitaAlcol > 3) {
                    stileDiVitaAdeguato = false;
                    break calcolaStileDiVita;
                }

                // Fumo
                if (fumo) {
                    stileDiVitaAdeguato = false;
                    break calcolaStileDiVita;
                }

                // BMI
                if (bmi >= 25) {
                    stileDiVitaAdeguato = false;
                    break calcolaStileDiVita;
                }

                // Circonferenza vita
                if (("M".equals(questionario.getSesso()) && circonferenzaVita >= 102) ||
                    ("F".equals(questionario.getSesso()) && circonferenzaVita >= 88)) {
                    stileDiVitaAdeguato = false;
                    break calcolaStileDiVita;
                }
            }

            // Calcolo se parametri nella norma
            boolean parametriNellaNorma = true;
            calcolaParametri:
            {
                // Pressione
                if (sistolica >= 140 || diastolica >= 90) {
                    parametriNellaNorma = false;
                    break calcolaParametri;
                }

                // Glicemia
                if (TIPOLOGIA_POST_PRANDIALE.equals(tipologiaGlicemia)) {
                    if (glicemia >= 140) {
                        parametriNellaNorma = false;
                        break calcolaParametri;
                    }
                } else {
                    if (glicemia >= 110) {
                        parametriNellaNorma = false;
                        break calcolaParametri;
                    }
                }

                // Colesterolo
                if (colesterolo >= 240) {
                    parametriNellaNorma = false;
                    break calcolaParametri;
                }
            }

            if (stileDiVitaAdeguato && parametriNellaNorma) {
                classe = 1;
                break calcolaClasse;
            }
            if (!stileDiVitaAdeguato && parametriNellaNorma) {
                classe = 2;
                break calcolaClasse;
            }
            if (stileDiVitaAdeguato && !parametriNellaNorma) {
                classe = 4;
                break calcolaClasse;
            }
            if (!stileDiVitaAdeguato && !parametriNellaNorma) {
                classe = 3;
                break calcolaClasse;
            }
        }

        return new Long(classe);
    }

    private float getMet(int intensita) {
        float result = 0;
        switch (intensita) {
        case INTENSITA_BASSA:
            result = 3.3f;
            break;
        case INTENSITA_MODERATA:
            result = 4;
            break;
        case INTENSITA_VIGOROSA:
            result = 8;
            break;
        }
        return result;
    }

    private void aggiungiMessaggio(String messaggio) {
        logger.fine(messaggio);
        messaggi.add(messaggio);
    }

    private String getNomePerCodice(String codice) {
        if ("FRUTTA_NUM".equals(codice)) {
            return "Quantità frutta/verdura";
        }
        if ("ALCOL_NUM".equals(codice)) {
            return "Quantità alcol";
        }
        return codice;
    }

    private void controlla(DomandaQuestionario domanda) {
        String messaggio = null;

        // Obbligatorietà
        if (domanda.getValore() == null) {
            if (domanda.isObbligatorio()) {
                campiObbligatoriMancanti = true;
                messaggio = "Manca " + getNomeCampo(domanda);
            }
        } else {
            if (DomandaQuestionario.NUMERO.equals(domanda.getTipo()) ||
                DomandaQuestionario.INTERO.equals(domanda.getTipo())) {
                String valore = domanda.getValore();
                if (valore.indexOf(".") > 0) {
                    domanda.setValore(valore.replace('.', ','));
                    if (DomandaQuestionario.INTERO.equals(domanda.getTipo())) {
                        messaggio = "Formato di " + getNomeCampo(domanda) + " non valido: deve essere un numero intero";
                    }
                }
                try {
                    numberFormat.parse(domanda.getValore());
                } catch (ParseException e) {
                    messaggio = "Formato di " + getNomeCampo(domanda) + " non valido";
                }
            }
        }

        if (messaggio != null) {
            logger.fine(messaggio);
            messaggi.add(messaggio);
        }
    }

    private String getNomeCampo(DomandaQuestionario domanda) {
        String nomeCampo;
        if (domanda.getTesto() != null && domanda.getTesto().trim().length() > 0) {
            nomeCampo = domanda.getTesto();
        } else if (domanda.getCodice() != null) {
            nomeCampo = getNomePerCodice(domanda.getCodice());
        } else {
            if (domanda.isObbligatorio()) {
                nomeCampo = "campo obbligatorio";
            } else {
                nomeCampo = "campo";
            }
        }
        return nomeCampo;
    }

    public List getMessaggi() {
        return messaggi;
    }
}
