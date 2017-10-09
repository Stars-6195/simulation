package schedulesim.scheduler;

import java.util.Random;
import schedulesim.Consumer;
import schedulesim.Scheduler;
import schedulesim.ConsumingEntity;

/**
 * This work is licensed under the Creative Commons Attribution 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * 
 * @author paul moggridge (paulmogs398@gmail.com)
 */
public class ShoppingScheduler extends Scheduler {

  private int optionCount;
  private Random random;

  public ShoppingScheduler(){
    this(16);
  }
  
  public ShoppingScheduler(int optionCount){
    super();
    this.optionCount = optionCount;
    random = new Random();
  }

  @Override
  public void step() {
    super.step();
    if (super.getChildren().size() > 0) {
      while(super.getWaitingTasks().size() > 0){

        // Make sure there is enough consuming entities
        if(optionCount > super.getChildren().size()){
          optionCount = super.getChildren().size();
        }

        // Randomly choose X consuming entities
        // Pick the fastest one (highest UPS) thus (min. execution time).
        ConsumingEntity minEntity = null;
        for(int i = 0; i < optionCount; i++){
          ConsumingEntity temp = super.getChildren().get(random.nextInt(super.getChildren().size()));
          if( minEntity == null ||
              temp.getUnitsPerStep() > minEntity.getUnitsPerStep()){
            minEntity = temp;
          }
        }

        // Assign task to min of randomly selected entities
        minEntity.submitTask(super.getWaitingTasks().remove(0));
        
        // Update gif, if we are scheduling to a Consumer
        if(minEntity instanceof Consumer){
            super.updateGif();
        }
      }
    }
  }

}
