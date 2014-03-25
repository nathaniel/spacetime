package spacetime;

public /**
 * This is the "data point" for the world-line.
 */
class WorldlineRecord{
  double t, x, betaNew, betaOld;
  Scenario sc;
  
  public WorldlineRecord(Scenario sc, double xp, double tp, double betapOld, double betapNew){
    this.sc=sc;
    setXpTpBetapOldNew(xp, tp, betapOld, betapNew);
  }
  
  public void setXpTpBetapOldNew(double xp, double tp, double betapOld, double betapNew){
    double betaRel = sc.getBetaRel();
    if(betapNew>1) betapNew=1;
    else if(betapNew<-1) betapNew=-1;
    this.betaNew = (betapNew+betaRel)/(1+betapNew*betaRel);
    
    if(betapOld>1) betapOld=1;
    else if(betapOld<-1) betapOld=-1;
    this.betaOld = (betapOld+betaRel)/(1+betapOld*betaRel);
    
    
    double gammaRel=1/Math.sqrt(1-betaRel*betaRel);
    this.x = gammaRel*(xp + betaRel*tp);
    this.t = gammaRel*(tp + betaRel*xp);
  }
  
  public double getXp(){
    double betaRel = sc.getBetaRel();
    double gammaRel = 1/Math.sqrt(1-betaRel*betaRel);
    return gammaRel*(x-betaRel*t);
  }
  
  public double getTp(){
    double betaRel = sc.getBetaRel();
    double gammaRel = 1/Math.sqrt(1-betaRel*betaRel);
    return gammaRel*(t-betaRel*x);
  }
  
  public double getBetaPOld(){
    double betaRel = sc.getBetaRel();
    return (betaOld-betaRel)/(1-betaOld*betaRel);
  }
  
  public double getBetaPNew(){
    double betaRel = sc.getBetaRel();
    return (betaNew-betaRel)/(1-betaNew*betaRel);
  }
  
  
}