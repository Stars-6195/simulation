package schedulesim.scheduler;

import java.util.Random;
import schedulesim.Consumer;
import schedulesim.ConsumingEntity;
import schedulesim.Scheduler;

/**
 * This work is licensed under the Creative Commons Attribution 4.0
 * International License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/ or send a letter to Creative
 * Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 *
 * @author paul moggridge (paulmogs398@gmail.com)
 */
public class WeightedRoundRobinScheduler extends Scheduler {

    private int index;
    private Random random;

    public WeightedRoundRobinScheduler() {
        super();
        index = 0;
        random = new Random();
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
            // Find fastest child
            ConsumingEntity fastestChild = null;
            for (ConsumingEntity child : super.getChildren()) {
                if ((fastestChild == null)
                        || fastestChild.getUnitsPerStep() < child.getUnitsPerStep()) {
                    fastestChild = child;
                }
            }
            while (super.getWaitingTasks().size() > 0) {
                // Use round robin to select a child
                ConsumingEntity child = super.getChildren().get(index++ % super.getChildren().size());
                // Work out the weight for this machine based on its UPS (speed)
                double wieght = ((double) child.getUnitsPerStep()) / ((double) fastestChild.getUnitsPerStep());
                // Get a random between 0 - 1 is our number lower than the wieght if so assign task
                if (random.nextDouble() < wieght) {
                    child.submitTask(super.getWaitingTasks().remove(0));                    
                    // Update gif, if we are scheduling to a Consumer
                    if(child instanceof Consumer){
                        super.updateGif();
                    }
                }
            }
        }
    }
}
