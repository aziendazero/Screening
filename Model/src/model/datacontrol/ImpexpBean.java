package model.datacontrol;

import java.util.Date;

public class ImpexpBean {
    private Date data;
    private String centro;
    private Date data_max;
    private Integer idcentro;
    private Integer centro_prel;
    private Integer op_type;
    private String livello;
    private String tpdip;
    private Integer inviti_hpv;
    private Integer id_invito;
    private String nome;
    private String cognome;
    private String codts;

    public ImpexpBean() {
        reset();
    }

    public void reset() {
        data = null;
        centro = null;
        data_max = null;
        idcentro = null;
        centro_prel = null;
        op_type = 0;
        livello = "1";
        tpdip = null;
        inviti_hpv = -1;
        id_invito = null;
        nome = null;
        cognome = null;
        codts = null;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getCentro() {
        return centro;
    }

    public void setCentro(String centro) {
        this.centro = centro;
    }

    public Date getData_max() {
        return data_max;
    }

    public void setData_max(Date data_max) {
        this.data_max = data_max;
    }

    public Integer getIdcentro() {
        return idcentro;
    }

    public void setIdcentro(Integer idcentro) {
        this.idcentro = idcentro;
    }

    public Integer getCentro_prel() {
        return centro_prel;
    }

    public void setCentro_prel(Integer centro_prel) {
        this.centro_prel = centro_prel;
    }

    public int getOp_type() {
        return op_type;
    }

    public void setOp_type(int op_type) {
        this.op_type = op_type;
    }

    public String getLivello() {
        return livello;
    }

    public void setLivello(String livello) {
        this.livello = livello;
    }

    public String getTpdip() {
        return tpdip;
    }

    public void setTpdip(String tpdip) {
        this.tpdip = tpdip;
    }

    public Integer getInviti_hpv() {
        return inviti_hpv;
    }

    public void setInviti_hpv(Integer inviti_hpv) {
        this.inviti_hpv = inviti_hpv;
    }

    public Integer getId_invito() {
        return id_invito;
    }

    public void setId_invito(Integer id_invito) {
        this.id_invito = id_invito;
    }


    public void setNome(String nome) {
        this.nome = nome;
    }


    public String getNome() {
        return nome;
    }


    public void setCognome(String cognome) {
        this.cognome = cognome;
    }


    public String getCognome() {
        return cognome;
    }


    public void setCodts(String codts) {
        this.codts = codts;
    }


    public String getCodts() {
        return codts;
    }
}
