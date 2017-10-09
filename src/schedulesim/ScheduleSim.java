package schedulesim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This work is licensed under the Creative Commons Attribution 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * 
 * @author paul moggridge (paulmogs398@gmail.com)
 */
public class ScheduleSim {

  // Version
  private static final String version = "1.7.0"; // Major . Minor . BugFix

  // Current simulator time
  private static int step = 0;

  // Experiment to run
  private Architecture architecture = null;

  // Gif output, this records the action for the gif
  private static RenderGif gif;
  
  public static void main(String[] args) {
    Log.println("ScheduleSim " + version);
    Log.println("Running self test...");
    MiniTester.runFunctionalTests();
    MiniTester.runSchedulingTests();
  }

  public ScheduleSim() {
    step = 0;
  }

  public static int getSimulationStep() {
    return step;
  }

  public void setArchitecture(Architecture architecture) {
    this.architecture = architecture;
  }

  public void runArchitecture(OutputOptions outputOptions) throws BadStepsException, BadTaskCompletionException {
    // Print results header
    printResultsHeader(outputOptions);

    // Prepare to record a gif if required
    gif = null;
    if (outputOptions.isRenderGif()){
      gif = new RenderGif(architecture);
      architecture.setGif(gif);
    }

    // Run experiment
    while (!isExperimentFinsihed(architecture)) {
      stepSimulator(architecture);
    }

    // Check simulation step integrity
    checkStepIntegrity(architecture);

    // Check task completion integrity
    checkTaskCompletedIntegrity(architecture);

    // Output results
    printResult(outputOptions);

    // Draw schedule image
    if (outputOptions.isRenderSchedule()) {
      renderScheduleDiagram(architecture);
    }

    // Draw gif 
    if (outputOptions.isRenderGif()){
      renderGifDiagram();
    }

    // Draw heirarchcy
    if(outputOptions.isVisualiseArchitecture()){
        visualiseArchitecture(architecture);
    }
  }

  private boolean isExperimentFinsihed(Architecture architecture) {
    // Check whether the consumer and schedulers are finished
    for (ConsumingEntity consumingEntity : architecture.getConsumingEntities()) {
      if (!consumingEntity.isFinished()) {
        return false;
      }
    }
    // Lastly check for tasks that haven't been sent yet
    return architecture.getProducer().isFinished();
  }

  private void stepSimulator(Architecture architecture) {
    step++;
    stepProducer(architecture);
    stepConsumingEntities(architecture/*,gif*/);
    if(gif!=null){
      gif.setArchitecture(architecture);
      gif.renderFrame(step);
    }
  }

  private void stepProducer(Architecture architecture) {
    architecture.getProducer().step();
  }

  private void stepConsumingEntities(Architecture architecture /*, RenderGif gif*/) {
    // The schedulers and consumers are stepped in random order. Why is this?
    // Imagine consumer 1 was always stepped before consumer 2. Then
    // from consumer 2's point of view consumer 1 is always be slgihtly ahead
    // in time. This behaviour could skew data, so address this, schedulers and
    // consumers (ConsumingEntities) are stepping in radnom order.

    // ScheduleSim is not a network simulator and network propagation is not
    // simulated. Thus, in ScheduleSim tasks can transverse
    // from the producer to a consumer through any number of schedulers in a
    // single time step. To avoid the randomness preventing consumer transvering
    // fully each tier of network is stepped randomly.
    // In short like Breadth First crossed with Random in term of tree search
    ArrayList<ConsumingEntity> firstLayer = architecture.getProducer().getChildren(); // only one
    stepBreadthFirstRandnom(firstLayer, architecture);
  }

  private void stepBreadthFirstRandnom(ArrayList<ConsumingEntity> entities, Architecture architecture) {
    Collections.shuffle(entities);
    ArrayList<ConsumingEntity> nextLayer = new ArrayList<>();
    for (ConsumingEntity entity : entities) {
      if (entity instanceof Scheduler) {
        nextLayer.addAll(((Scheduler) entity).getChildren());
        ((Scheduler) entity).setArchitecture(architecture);
      }
      entity.step();
    }

    if (!nextLayer.isEmpty()) {
      stepBreadthFirstRandnom(nextLayer, architecture);
    }
  }

  private void checkStepIntegrity(Architecture architecture) throws BadStepsException {
    // Check integrity of SimEntities
    // make sure no one has stepped too much
    if (!architecture.getProducer().checkStep()) {
      throw new BadStepsException("Producer took incorrect number of steps.", architecture.getProducer());
    }
    for (ConsumingEntity consumingEntity : architecture.getConsumingEntities()) {
      if (!consumingEntity.checkStep()) {
        throw new BadStepsException("Consuming entity took incorrect number of steps.", consumingEntity);
      }
    }
  }

  private void checkTaskCompletedIntegrity(Architecture architecture) throws BadTaskCompletionException {
    // Does the number of the tasks the producer sent match the
    // the number of tasks ompleted by the consumers
    int consumerCompletedTaskCount = 0;
    for (Consumer consumer : architecture.getConsumers()) {
      consumerCompletedTaskCount += consumer.getCompletedTasks().size();
    }
    if (consumerCompletedTaskCount != architecture.getProducer().getTasksSubmittedCount()) {
      throw new BadTaskCompletionException("Task completed did not match the number of task sent!",
        architecture.getProducer().getTasksSubmittedCount(), consumerCompletedTaskCount);
    }
  }

  private static void printResultsHeader(OutputOptions outputOptions) {
    Log.println("ScheduleSim Experiment Results");
    String header = "Architecture, Producer,";
    if (outputOptions.isMakespan()) {
      header += "Makespan(step),";
    }
    if (outputOptions.isUtilisation()) {
      header += "Utilisation(%),";
    }
    if (outputOptions.getCountBins() > 0) {
      for (int i = 0; i < outputOptions.getCountBins(); i++) {
        header += "Task Size Group " + i + " Avg. Makespan,";
      }
    }
  }

  private void printResult(OutputOptions outputOptions) {
    
    // Has anything been requested to be print
    if(!outputOptions.isPrintingRequested()){
      return; // Nothing to print skip
    }
    
    String resultStr = architecture.getName() + "," + architecture.getProducer().getName() + ",";
    if (outputOptions.isMakespan()) {
      resultStr += architecture.getMakespan() + ",";
    }
    if (outputOptions.isUtilisation()) {
      resultStr += architecture.getUtilisation() + ",";
    }
    if (outputOptions.getCountBins() > 0) {
      double[] binsResult = architecture.getTaskSizeBinsAverageMakespans(outputOptions.getCountBins());
      for (int i = 0; i < outputOptions.getCountBins(); i++) {
        resultStr += binsResult[i] + ",";
      }
    }
    Log.println(resultStr);
  }
  
  private void renderGifDiagram(){
    try {
      gif.writeToFile();
    } catch (IOException ioe) {
      Log.println("Failed to write image: " + ioe.getMessage());
    }
  }
  
  private void renderScheduleDiagram(Architecture architecture) {
    try {
      RenderSchedule render = new RenderSchedule(architecture);
      render.writeToFile(render.renderSchedule());
    } catch (IOException ioe) {
      Log.println("Failed to write image: " + ioe.getMessage());
    }
  }
  
  private void visualiseArchitecture(Architecture architecture) {
    try {
      ArchitectureVisualiser visualiser = new ArchitectureVisualiser(architecture);
      visualiser.writeToFile(visualiser.visualise());
    } catch (IOException ioe) {
      Log.println("Failed to write image: " + ioe.getMessage());
    }
  }
}