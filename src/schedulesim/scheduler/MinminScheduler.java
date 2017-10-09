package schedulesim.scheduler;

import java.util.Collections;
import java.util.HashMap;
import schedulesim.Consumer;
import schedulesim.ConsumingEntity;
import schedulesim.Task;
import schedulesim.TaskMinFirstComparator;
import schedulesim.Scheduler;

/**
 * This work is licensed under the Creative Commons Attribution 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * 
 * @author paul moggridge (paulmogs398@gmail.com)
 */
public class MinminScheduler extends Scheduler {

  public MinminScheduler(){
    super();
  }

  @Override
  public void step() {
    super.step();
    if(super.getChildren().size()>0){

      // This will store the delays on children below.
      HashMap<ConsumingEntity, Double> childDelay = new HashMap<>();

      // Find any exsisting delay on child from previous waves
      for (ConsumingEntity child : super.getChildren()) {
        // Create entry for child
        childDelay.put(child, child.getDelay());
      }

      // Sort the task min first, smallest tasks first
      Collections.sort(super.getWaitingTasks(), new TaskMinFirstComparator());

      while(super.getWaitingTasks().size()>0){
        Task task = super.getWaitingTasks().get(0);

        // Which ConsumingEntity can finish it first? i.e in the min. time,
        // takes into account the UnitPerStep of the ConsumingEntity and tasks
        // already scheduled to it.
        double minChildDelay = 0.0;
        ConsumingEntity minChild = null;

        // Which child can complete task first
        for(ConsumingEntity child : super.getChildren()){
          double taskMakespanWithChildDelay = ((double)task.getRemaingUnits() / (double)child.getUnitsPerStep()) + childDelay.get(child);
          // Can this child finish the task faster
          if(taskMakespanWithChildDelay < minChildDelay || minChild == null){
            minChild = child;
            minChildDelay = taskMakespanWithChildDelay;
          }
        }

        // Update child delays map
        childDelay.put(minChild, minChildDelay);

        // Submit task to child
        minChild.submitTask(super.getWaitingTasks().remove(0));
        
        // Update gif, if we are scheduling to a Consumer
        if(minChild instanceof Consumer){
            super.updateGif();
        }
      }
    }
  }
}
