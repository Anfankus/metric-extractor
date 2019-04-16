package com.github.mauricioaniche.ck;

public class MetricValue {

  private String sourceFilePath;
  private String fullClassName;
  private String type;

  private int dit;
  private int noc;
  private int wmc;
  private int cbo;
  private int lcom;
  private int rfc;
  private int nom;
  private int nopm;
  private int nosm;

  private int nof;
  private int nopf;
  private int nosf;

  private int nosi;
  private int loc;

  public MetricValue(String sourceFilePath, String fullClassName, String type) {
    this.sourceFilePath = sourceFilePath;
    this.fullClassName = fullClassName;
    this.type = type;

  }

  public String getSourceFilePath() {
    return sourceFilePath;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fullClassName == null) ? 0 : fullClassName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    MetricValue other = (MetricValue) obj;
    if (fullClassName == null) {
      if (other.fullClassName != null) {
        return false;
      }
    } else if (!fullClassName.equals(other.fullClassName)) {
      return false;
    }
    return true;
  }

  public int getDit() {
    return dit;
  }

  public void setDit(int dit) {
    this.dit = dit;
  }

  /**
   * @return the name of top-level class/interface/enum in a compilation unit
   */
  public String getFullyQualifiedClassName() {
    return fullClassName;
  }

  public void increaseNoc() {
    increaseNoc(1);
  }

  public void increaseNoc(int value) {
    this.noc += value;
  }

  public int getNoc() {
    return noc;
  }

  public void setWmc(int cc) {
    this.wmc = cc;
  }

  public int getWmc() {
    return wmc;
  }


  public int getCbo() {
    return cbo;
  }

  public void setCbo(int cbo) {
    this.cbo = cbo;
  }

  public void setLcom(int lcom) {
    this.lcom = lcom;
  }

  public int getLcom() {
    return lcom;
  }

  public void setRfc(int rfc) {
    this.rfc = rfc;
  }

  public int getRfc() {
    return rfc;
  }

  public void setNom(int nom) {
    this.nom = nom;
  }

  public int getNom() {
    return nom;
  }

  public String getType() {
    return type;
  }

  public int getNopm() {
    return nopm;
  }

  public void setNopm(int nopm) {
    this.nopm = nopm;
  }

  public int getNosm() {
    return nosm;
  }

  public void setNosm(int nosm) {
    this.nosm = nosm;
  }

  public int getNof() {
    return nof;
  }

  public void setNof(int nof) {
    this.nof = nof;
  }

  public int getNopf() {
    return nopf;
  }

  public void setNopf(int nopf) {
    this.nopf = nopf;
  }

  public int getNosf() {
    return nosf;
  }

  public void setNosf(int nosf) {
    this.nosf = nosf;
  }

  public int getNosi() {
    return nosi;
  }

  public void setNosi(int nosi) {
    this.nosi = nosi;
  }

  public int getLoc() {
    return loc;
  }

  public void setLoc(int loc) {
    this.loc = loc;
  }

  /**
   * @return "WMC,DIT,NOC,CBO,RFC,LCOM,NOM,NOPM,NOSM,NOF,NOPF,NOSF,NOSI"
   */
  static public String getResultHeader() {
    //return "filePath,className,classType,WMC,DIT,NOC,CBO,RFC,LCOM,NOM,NOPM,NOSM,NOF,NOPF,NOSF,NOSI,LOC";
    return "WMC,DIT,NOC,CBO,RFC,LCOM,NOM,NOPM,NOSM,NOF,NOPF,NOSF,NOSI";
  }

  /**
   * @return "WMC,DIT,NOC,CBO,RFC,LCOM,NOM,NOPM,NOSM,NOF,NOPF,NOSF,NOSI"
   */
  @Override
  public String toString() {
		/*return this.getSourceFilePath() + "," + this.getClassName() + "," + this.getClassType() + ","
				+ this.getWmc() + "," + this.getDit() + "," + this.getNoc() + "," + this.getCbo() + "," + this.getRfc() + "," + this.getLcom()
				+ "," + this.getNom() + "," + this.getNopm() + "," + this.getNosm() + "," + this.getNof() + ","
				+ this.getNopf() + "," + this.getNosf() + "," + this.getNosi() + "," + this.getLoc();*/

    return this.getWmc() + "," + this.getDit() + "," + this.getNoc() + "," + this.getCbo() + ","
        + this.getRfc() + ","
        + this.getLcom() + "," + this.getNom() + "," + this.getNopm() + "," + this.getNosm() + ","
        + this.getNof()
        + "," + this.getNopf() + "," + this.getNosf() + "," + this.getNosi();
  }
}
