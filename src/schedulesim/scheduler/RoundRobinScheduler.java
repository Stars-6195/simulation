package schedulesim.scheduler;

import schedulesim.Consumer;
import schedulesim.ConsumingEntity;
import schedulesim.Scheduler;

/**
 * This work is licensed under the Creative Commons Attribution 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * 
 * 
 * @author paul moggridge (paulmogs398@gmail.com)
 */
public class RoundRobinScheduler extends Scheduler {

  private int index;

  public RoundRobinScheduler() {
    super();
    index = 0;
  }

  @Override
  public void reset(){
    super.reset();
    index = 0;
  }
  
  @Override
  public void step() {
    super.step();
    if (super.getChildren().size() > 0) {
      while(super.getWaitingTasks().size() > 0){
        ConsumingEntity child = super.getChildren().get(index++ % super.getChildren().size());
        child.submitTask(super.getWaitingTasks().remove(0));
        
        // Update gif, if we are scheduling to a Consumer
        if(child instanceof Consumer){
            super.updateGif();
        }
      }
    }
  }
}
