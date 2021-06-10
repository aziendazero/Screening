package model.datacontrol;

public class Sogg_ConsensoParam {
    public Sogg_ConsensoParam() {
        super();
    }

    boolean propagaCI = true;
    boolean propagaMA = true;
    boolean propagaCO = true;


    public void setPropagaCI(boolean propagaCI)
    {
      this.propagaCI = propagaCI;
    }


    public boolean getPropagaCI()
    {
      return propagaCI;
    }


    public void setPropagaMA(boolean propagaMA)
    {
      this.propagaMA = propagaMA;
    }


    public boolean getPropagaMA()
    {
      return propagaMA;
    }


    public void setPropagaCO(boolean propagaCO)
    {
      this.propagaCO = propagaCO;
    }


    public boolean getPropagaCO()
    {
      return propagaCO;
    }
    
    public void resetCampi()
    {
      propagaCI = true;
      propagaMA = true;
      propagaCO = true;
    }
    }
