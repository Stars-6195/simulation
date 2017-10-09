package schedulesim;

/**
 * This work is licensed under the Creative Commons Attribution 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * 
 * @author paul moggridge (paulmogs398@gmail.com)
 */
public abstract class Scheduler extends ConsumingEntity {

  private Architecture architecture;
  
  public Scheduler(){
    super();
  }
  
  public void setArchitecture(Architecture architecture){
    this.architecture = architecture;
  }
  
  public void updateGif(){
    architecture.updateGif();
  }
  
  @Override
  public int getUnitsPerStep() {
    // Find the fastest UPS
    int totalUPS = 0;
    for(ConsumingEntity entity : this.getChildren()){
        totalUPS += entity.getUnitsPerStep();
    }
    return totalUPS;
  }

  @Override
  public double getUtilisation() {
    // Find a consumer with utilisation 0.0 if there is one
    double totalUtil = 0.0;
    for(ConsumingEntity entity : this.getChildren()){
        totalUtil += entity.getUtilisation();
    }
    if(this.getChildren().size() > 0){
      totalUtil /= this.getChildren().size();
    }
    return totalUtil;
  }

  @Override
  public double getDelay() {
    // Find the consumer with the shortest delay
    double shortestDelay = 0;
    for(ConsumingEntity entity : this.getChildren()){
      if(shortestDelay > entity.getDelay()){
        shortestDelay = entity.getDelay();
      }
    }
    return shortestDelay;
  }
}
