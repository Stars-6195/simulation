package schedulesim;

import java.util.ArrayList;

/**
 * This work is licensed under the Creative Commons Attribution 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * 
 * @author paul moggridge (paulmogs398@gmail.com)
 */
public class Architecture {

  private String name;
  private Producer producer;
  private RenderGif gif;
  private final ArrayList<ConsumingEntity> consumingEntities;

  public Architecture(String name){
    this.name = name;
    this.consumingEntities = new ArrayList<>();
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void setGif(RenderGif gif){
    this.gif = gif;
  }

  public void updateGif(){
    if(gif!=null){
      gif.setArchitecture(this);
      gif.renderFrame(ScheduleSim.getSimulationStep());
    }
  }
  
  public void addEntity(SimEntity parent , ConsumingEntity child) throws BadParentException {
    if(parent instanceof Producer){
      // Root of the tree
      producer = (Producer)parent;
    } else {
      // Before adding, confirm parent exists
      if(!consumingEntities.contains((ConsumingEntity)parent)){
        throw new BadParentException("Tried to add child with a parent that had not yet been added! " +
                                      "You have to build the tree starting with the Producer.",parent,child);
      }
    }
    // Add child to archictecture
    consumingEntities.add(child);
    // Add child to parent.
    parent.addChild(child);
  }
  
  public void removeEntity(SimEntity parent, ConsumingEntity child) throws BadChildException, BadParentException {
    // Removes child from parent
    
    if(!consumingEntities.contains((ConsumingEntity)parent)){
        throw new BadParentException("Tried to remove a child from a parent that had not yet been added! ",parent,child);
    }
    
    if(!consumingEntities.contains((ConsumingEntity)child)){
      throw new BadParentException("Tried to remove child from a parent that did have that child added! ",parent,child);
    }
    
    // Remove child from list of entities
    consumingEntities.remove(child);
    
    // Remove reference from parent
    parent.removeChild(child);
  }

  public Producer getProducer(){
    return producer;
  }

  public void resetAllEntities(){
    producer.reset();
    for(ConsumingEntity consumingEntity : consumingEntities){
      consumingEntity.reset();
    }
  }
  
  public ArrayList<ConsumingEntity> getConsumingEntities(){
    return consumingEntities;
  }

  public ArrayList<Scheduler> getSchedulers() {
    ArrayList<Scheduler> schedulers = new ArrayList<>();
    for(ConsumingEntity entity : consumingEntities){
      if(entity instanceof Scheduler){
        schedulers.add((Scheduler)entity);
      }
    }
    return schedulers;
  }

  public ArrayList<Consumer> getConsumers() {
    ArrayList<Consumer> consumers = new ArrayList<>();
    for(ConsumingEntity entity : consumingEntities){
      if(entity instanceof Consumer){
        consumers.add((Consumer)entity);
      }
    }
    return consumers;
  }
  
  public int getMakespan() {
    
    // Get Consumers
    ArrayList<Consumer> consumers = getConsumers();
    
    // Gather completed tasks
    ArrayList<Task> completedTasks = new ArrayList<>();
    for (Consumer consumer : consumers) {
      completedTasks.addAll(consumer.getCompletedTasks());
    }
    
    // Get first task submission time, this will count as our start time
    int firstSubmissionStep = -1;
    for (Task task : completedTasks ) {
      if (task.getStartUnits() < firstSubmissionStep || (firstSubmissionStep == -1)) {
        firstSubmissionStep = task.getStepSubmitted();
      }
    }

    // Get time of last task to finish
    int lastFinishStep = -1;
    for (Task task : completedTasks ) {
      if ((task.getStepFinished() > lastFinishStep)
        || (lastFinishStep == -1)) {
        lastFinishStep = task.getStepFinished();
      }
    }

    // This shouldn't happen
    if (lastFinishStep < firstSubmissionStep) {
      return 0;
    }

    return lastFinishStep - firstSubmissionStep;
  }

  public double getUtilisation() {
    // How much did these tasks utilise these consumers?

    // Get Consumers
    ArrayList<Consumer> consumers = getConsumers();
    
    // Having sense of UPS is important so that it is represented that it is worse
    // having a big resource ilde than a small one.
    // Get total UPS
    int totalUPS = 0;
    for (Consumer consumer : consumers) {
      totalUPS += consumer.getUnitsPerStep();
    }

    // Get utilisation of each consumer
    double consumerUtilisation = 0.0;
    for (Consumer consumer : consumers) {
      consumerUtilisation += (consumer.getTotalUtilisation() * consumer.getUnitsPerStep()) / totalUPS;
    }

    return consumerUtilisation;
  }

  public double[] getTaskSizeBinsAverageMakespans(int binCount) {

    // Get Consumers
    ArrayList<Consumer> consumers = getConsumers();
    
    // Gather completed tasks
    ArrayList<Task> completedTasks = new ArrayList<>();
    for (Consumer consumer : consumers) {
      completedTasks.addAll(consumer.getCompletedTasks());
    }

    double[] avgTaskMakespanForBin = new double[binCount];
    int[] taskCountInBins = new int[binCount];
    int[] totalTaskMakespan = new int[binCount];

    // Find biggest and smallest task
    int smallestTaskUnits = -1;
    int largestTaskUnits = -1;
    for (Task task : completedTasks) {
      if (task.getStartUnits() < smallestTaskUnits || smallestTaskUnits == -1) {
        smallestTaskUnits = task.getStartUnits();
      }
      if (task.getStartUnits() > largestTaskUnits) {
        largestTaskUnits = task.getStartUnits();
      }
    }

    // Create group (bins) based on task size (equi-width)
    double binWidth = ((double)(largestTaskUnits - smallestTaskUnits) / (double)binCount);

    // Place each task in the group based on it's length
    for (Task task : completedTasks) {
      int binIndex = 0;
      while ((((binIndex + 1) * binWidth) + smallestTaskUnits) < task.getStartUnits()) {
        binIndex++;
      }
      totalTaskMakespan[binIndex] += task.getStepFinished() - task.getStepSubmitted();
      taskCountInBins[binIndex]++;
    }

    // Calculate average task group finish time
    for (int binIndex = 0; binIndex < binCount; binIndex++) {
      avgTaskMakespanForBin[binIndex] = ((double) totalTaskMakespan[binIndex] / (double) taskCountInBins[binIndex]);
    }

    return avgTaskMakespanForBin;
  }
  
}
