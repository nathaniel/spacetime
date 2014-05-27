package spacetime;

public /**
 * This is the "data point" for the world-line.
 * Contains x and t coordinates of each beta change event, 
 *  and prior and new beta values.  All values are stored as measured from
 *  original reference frame.
 * Each STObject contains a linked list of such data points
 *  indicating its history.
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
    // Ensure -1 <= betapNew <= 1
    if(betapNew>1) betapNew=1;
    else if(betapNew<-1) betapNew=-1;
    // Transform new beta value to original reference frame
    this.betaNew = (betapNew+betaRel)/(1+betapNew*betaRel);
    
    // Ensure -1 <= betapOld <= 1
    if(betapOld>1) betapOld=1;
    else if(betapOld<-1) betapOld=-1;
    // Transform old beta value to original reference frame
    this.betaOld = (betapOld+betaRel)/(1+betapOld*betaRel);
    
    // Transform x and t coordinates to original reference frame
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
