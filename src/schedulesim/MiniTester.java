package schedulesim;

import java.util.ArrayList;
import java.util.Collections;
import schedulesim.scheduler.MaxminFastTrackScheduler;
import schedulesim.scheduler.MaxminScheduler;
import schedulesim.scheduler.MinminMaxminFastTrackScheduler;
import schedulesim.scheduler.MinminScheduler;
import schedulesim.scheduler.RandomScheduler;
import schedulesim.scheduler.RoundRobinScheduler;
import schedulesim.scheduler.ShoppingScheduler;
import schedulesim.scheduler.WeightedRoundRobinScheduler;

/**
 * This work is licensed under the Creative Commons Attribution 4.0
 * International License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/ or send a letter to Creative
 * Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 *
 * @author paul moggridge (paulmogs398@gmail.com)
 */
public class MiniTester {

    public static void runFunctionalTests() {
        boolean result;

        Log.println("Testing Task...");
        result = testTask();
        Log.println((result ? "SUCCESS\n" : "FAILURE\n"));

        Log.println("Testing Task Min Sort...");
        result = testTaskMinSort();
        Log.println((result ? "SUCCESS\n" : "FAILURE\n"));

        Log.println("Testing Task Max Sort...");
        result = testTaskMaxSort();
        Log.println((result ? "SUCCESS\n" : "FAILURE\n"));

        Log.println("Testing ConsumingEntity Sort");
        result = testConsumingEntitySort();
        Log.println((result ? "SUCCESS\n" : "FAILURE\n"));

        Log.println("Testing FlatTaskPattern...");
        result = testFlatTaskPattern();
        Log.println((result ? "SUCCESS\n" : "FAILURE\n"));

        Log.println("Testing GaussianTaskPattern...");
        result = testGaussianTaskPattern();
        Log.println((result ? "SUCCESS\n" : "FAILURE\n"));

        Log.println("Testing IncrementingTaskPattern...");
        result = testIncrementingTaskPattern();
        Log.println((result ? "SUCCESS\n" : "FAILURE\n"));

        Log.println("Testing RandomTaskPattern...");
        result = testRandomTaskPattern();
        Log.println((result ? "SUCCESS\n" : "FAILURE\n"));

        Log.println("Testing Producer...");
        result = testProducer();
        Log.println((result ? "SUCCESS\n" : "FAILURE\n"));
    }

    public static void runSchedulingTests() {

        Log.println("Testing Random Scheduler...");
        testScheduler(new RandomScheduler(), "Random");

        Log.println("\nTesting Shopping Scheduler...");
        testScheduler(new ShoppingScheduler(), "Shopping");

        Log.println("\nTesting Round Robin Scheduler...");
        testScheduler(new RoundRobinScheduler(), "Robin");

        Log.println("\nTesting Weighted Round Robin Scheduler...");
        testScheduler(new WeightedRoundRobinScheduler(), "Weighted Robin");

        Log.println("\nTesting Minmin Scheduler...");
        testScheduler(new MinminScheduler(), "MinMin");

        Log.println("\nTesting Maxmin Scheduler...");
        testScheduler(new MaxminScheduler(), "MaxMin");

        Log.println("\nTesting MXFT Scheduler...");
        testScheduler(new MaxminFastTrackScheduler(), "MXFT");
        
        Log.println("\nTesting MMMXFT Scheduler...");
        testScheduler(new MinminMaxminFastTrackScheduler(), "MMMXFT");

        Log.println("\nTesting Random Scheduler with multiple waves...");
        testSchedulerMulti(new RandomScheduler(), "Random");

        Log.println("\nTesting Shopping Scheduler with multiple waves...");
        testSchedulerMulti(new ShoppingScheduler(), "Shopping");

        Log.println("\nTesting Round Robin Scheduler with multiple waves...");
        testSchedulerMulti(new RoundRobinScheduler(), "Robin");

        Log.println("\nTesting Weighted Round Robin Scheduler with multiple waves...");
        testSchedulerMulti(new WeightedRoundRobinScheduler(), "Weighted Robin");

        Log.println("\nTesting Minmin Scheduler with multiple waves...");
        testSchedulerMulti(new MinminScheduler(), "MinMin");

        Log.println("\nTesting Maxmin Scheduler with multiple waves...");
        testSchedulerMulti(new MaxminScheduler(), "MaxMin");

        Log.println("\nTesting MXFT Scheduler with multiple waves...");
        testSchedulerMulti(new MaxminFastTrackScheduler(), "MXFT");
        
        Log.println("\nTesting MMMXFT Scheduler with multiple waves ...");
        testSchedulerMulti(new MinminMaxminFastTrackScheduler(), "MMMXFT");

        Log.println("\nTesting Heirarhical Scheduling...");
        testHierarichalScheduling(new RandomScheduler(),new ShoppingScheduler(), new WeightedRoundRobinScheduler(), "Heirarhical");
    }

    private static boolean testTask() {
        int unitSizeOne = 10;
        int unitSizeTwo = 20;
        Task taskOne = new Task(unitSizeOne);
        Task taskTwo = new Task(unitSizeTwo);
        if (taskOne.getTid() == taskTwo.getTid()
                && taskOne.getStartUnits() != unitSizeOne
                && taskOne.getRemaingUnits() != unitSizeOne
                && taskTwo.getStartUnits() != unitSizeTwo
                && taskTwo.getRemaingUnits() != unitSizeTwo) {
            return false;
        }
        taskOne.decrementUnits(unitSizeOne);
        taskTwo.decrementUnits(unitSizeTwo);
        return taskOne.isFinished() && taskTwo.isFinished()
                && taskOne.getRemaingUnits() == 0
                && taskTwo.getRemaingUnits() == 0
                && taskOne.getStartUnits() == unitSizeOne
                && taskTwo.getStartUnits() == unitSizeTwo;
    }

    private static boolean testTaskMinSort() {
        ArrayList<Task> tasks = new ArrayList<>();

        tasks.add(new Task(12));
        tasks.add(new Task(22));
        tasks.add(new Task(4));
        tasks.add(new Task(11));
        tasks.add(new Task(13));
        tasks.add(new Task(8));
        tasks.add(new Task(1));

        Log.println("Sorting Smallest (Min.) First");
        Collections.sort(tasks, new TaskMinFirstComparator());

        for (Task task : tasks) {
            Log.println(task.getTid() + "," + task.getRemaingUnits() + "u");
        }

        if (tasks.get(6).getRemaingUnits() < tasks.get(0).getRemaingUnits()) {
            return false;
        }

        return true;
    }

    private static boolean testTaskMaxSort() {
        ArrayList<Task> tasks = new ArrayList<>();

        tasks.add(new Task(12));
        tasks.add(new Task(22));
        tasks.add(new Task(4));
        tasks.add(new Task(11));
        tasks.add(new Task(13));
        tasks.add(new Task(8));
        tasks.add(new Task(1));

        Log.println("Sorting Biggest (Max.) First");
        Collections.sort(tasks, new TaskMaxFirstComparator());

        for (Task task : tasks) {
            Log.println(task.getTid() + "," + task.getRemaingUnits() + "u");
        }

        if (tasks.get(6).getRemaingUnits() > tasks.get(0).getRemaingUnits()) {
            return false;
        }

        return true;
    }

    private static boolean testConsumingEntitySort() {
        ArrayList<ConsumingEntity> consumingEntities = new ArrayList<>();

        consumingEntities.add(new Consumer(15));
        consumingEntities.add(new Consumer(10));
        consumingEntities.add(new Consumer(15));
        consumingEntities.add(new Consumer(12));
        consumingEntities.add(new Consumer(5));
        consumingEntities.add(new Consumer(1));
        consumingEntities.add(new Consumer(14));

        Log.println("Sorting Biggest UPS First ");
        Collections.sort(consumingEntities, new ConsumingEntityMinFirstComparator());

        for (ConsumingEntity entity : consumingEntities) {
            Log.println(entity.getId() + "," + entity.getUnitsPerStep() + "ups");
        }

        if (consumingEntities.get(6).getUnitsPerStep() > consumingEntities.get(0).getUnitsPerStep()) {
            return false;
        }
        return true;
    }

    private static boolean testFlatTaskPattern() {
        int taskCount = 8;
        int taskSize = 16;
        FlatPattern flat = new FlatPattern(taskCount, taskSize);
        ArrayList<Task> tasks = flat.generateMetatask();
        Log.println("Flat Tasks");
        for (Task task : tasks) {
            Log.println(task.getTid() + "," + task.getStartUnits());
        }
        return tasks.size() == taskCount
                && tasks.get(0).getStartUnits() == taskSize;
    }

    private static boolean testGaussianTaskPattern() {
        int startSize = 2;
        int endSize = 20;
        double mu = 11;
        double sigma = 4;
        int combinedTargetSize = 5000;
        GaussianPattern gaussian = new GaussianPattern(startSize, endSize, mu, sigma, combinedTargetSize);
        ArrayList<Task> tasks = gaussian.generateMetatask();

        // So we can see the gaussian, count the number of type of task
        int[] counts = new int[endSize - startSize];
        for (int i = 0; i < counts.length; i++) {
            counts[i] = 0;
        }
        for (Task task : tasks) {
            counts[(task.getRemaingUnits() - startSize)]++;
        }
        Log.println("Gaussian Tasks as Distro");
        for (int i = 0; i < counts.length; i++) {
            Log.print("Count of tasks with size " + (i + startSize) + " :");
            String bar = "";
            for (int b = 0; b < counts[i]; b++) {
                bar += "#";
            }
            Log.println(bar);
        }

        return !tasks.isEmpty();
    }

    private static boolean testIncrementingTaskPattern() {
        int start = 2;
        int stop = 10;
        IncrementingPattern increment = new IncrementingPattern(start, stop);
        ArrayList<Task> tasks = increment.generateMetatask();
        Log.println("Incrementing Tasks");
        for (Task task : tasks) {
            Log.println(task.getTid() + "," + task.getStartUnits());
        }
        return tasks.size() == (stop - start);
    }

    private static boolean testRandomTaskPattern() {
        int start = 2;
        int end = 50;
        int count = 10;
        RandomPattern random = new RandomPattern(start, end, count);
        ArrayList<Task> tasks = random.generateMetatask();
        Log.println("Random Tasks");
        for (Task task : tasks) {
            Log.println(task.getTid() + "," + task.getStartUnits());
        }
        return tasks.size() == (count);
    }

    /**
     * Tests the Producer. This test doesn't use a scheduler.
     */
    private static boolean testProducer() {
        // Create simulator
        ScheduleSim sim = new ScheduleSim();

        // Create Producer
        Producer producer = new Producer("ProducerTest");

        // Create Task Pattern
        int taskCount = 1;
        int taskSize = 1;
        FlatPattern flat = new FlatPattern(taskCount, taskSize);
        producer.addMetatask(1, flat); // Consumer in the first then will have have idle
        producer.addMetatask(4, flat);

        // Create architecture
        Architecture architecture = new Architecture("SingleConsumer");

        // Create Scheduler
        // Not going to bother, going to straight from producer to consumer
        // Create Consumer
        Consumer consumer = new Consumer(1);

        // Build architecture Tree
        try {
            architecture.addEntity(producer, consumer);
        } catch (BadParentException bpe) {
            System.out.println(bpe.getMessage());
            System.out.println("Parent Id: " + bpe.getParent().getId());
            System.out.println("Child Id: " + bpe.getChild().getId());
            return false;
        }

        // Give experiment to ScheduleSim
        sim.setArchitecture(architecture);

        // Chose output options
        OutputOptions outputOptions = new OutputOptions();

        // Run the experiment
        try {
            sim.runArchitecture(outputOptions);
        } catch (BadStepsException bse) {
            Log.println(bse.getMessage() + " SimEntity id:" + bse.getSimEntity().getId());
        } catch (BadTaskCompletionException bjce) {
            Log.println(bjce.getMessage() + " Sent:" + bjce.getTasksSent() + " Completed:" + bjce.getTasksCompleted());
        }

        // Check the consumer in use half the steps. 0 and 3 but, not 1 and 2
        if (consumer.getTotalUtilisation() != 0.5) {
            return false;
        }

        // Check the simulator finsihed at the right step
        if (ScheduleSim.getSimulationStep() != 4) {
            return false;
        }

        return true;
    }

    /**
     * Tests a scheduler.
     */
    private static void testScheduler(Scheduler scheduler, String schedulerName) {
        // Create simulator
        ScheduleSim sim = new ScheduleSim();

        // Create Producer
        Producer producer = new Producer("Random");

        // Create Task Pattern
        int start = 20;
        int stop = 270;
        int count = 200;
        RandomPattern pattern = new RandomPattern(start, stop, count);
        producer.addMetatask(1, pattern); // One wave of tasks

        // Create Architecture
        Architecture architecture = new Architecture(schedulerName);

        // Create Consumers
        ArrayList<Consumer> consumers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            consumers.add(new Consumer(20));
        }
        for (int i = 0; i < 4; i++) {
            consumers.add(new Consumer(18));
        }
        for (int i = 0; i < 12; i++) {
            consumers.add(new Consumer(16));
        }
        for (int i = 0; i < 16; i++) {
            consumers.add(new Consumer(6));
        }

        // Build Architecture Tree
        try {
            architecture.addEntity(producer, scheduler);
            for (Consumer consumer : consumers) {
                architecture.addEntity(scheduler, consumer);
            }
        } catch (BadParentException bpe) {
            System.out.println(bpe.getMessage());
            System.out.println("Parent Id: " + bpe.getParent().getId());
            System.out.println("Child Id: " + bpe.getChild().getId());
            return;
        }

        // Give experiment to ScheduleSim
        sim.setArchitecture(architecture);

        // Chose output options
        OutputOptions outputOptions = new OutputOptions();
        outputOptions.setRenderSchedule(true); // Render schedule images
        outputOptions.setRenderGif(true); // Render GIF

        // Run the experiment
        try {
            sim.runArchitecture(outputOptions);
        } catch (BadStepsException bse) {
            Log.println(bse.getMessage() + " SimEntity id:" + bse.getSimEntity().getId());
        } catch (BadTaskCompletionException bjce) {
            Log.println(bjce.getMessage() + " Sent:" + bjce.getTasksSent() + " Completed:" + bjce.getTasksCompleted());
        }
    }

    /**
     * Tests a scheduler.
     */
    private static void testSchedulerMulti(Scheduler scheduler, String schedulerName) {
        // Create simulator
        ScheduleSim sim = new ScheduleSim();

        // Create Producer
        Producer producer = new Producer("RandomMulti");

        // Create Task Pattern
        int start = 10;
        int stop = 100;
        int count = 200;
        RandomPattern pattern = new RandomPattern(start, stop, count);
        producer.addMetatask(1, pattern); // One wave of tasks
        producer.addMetatask(30, pattern); // One wave of tasks

        // Create Architecture
        Architecture architecture = new Architecture(schedulerName);

        // Create Consumers
        ArrayList<Consumer> consumers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            consumers.add(new Consumer(20));
        }
        for (int i = 0; i < 4; i++) {
            consumers.add(new Consumer(18));
        }
        for (int i = 0; i < 12; i++) {
            consumers.add(new Consumer(16));
        }
        for (int i = 0; i < 16; i++) {
            consumers.add(new Consumer(14));
        }

        // Build Architecture Tree
        try {
            architecture.addEntity(producer, scheduler);
            for (Consumer consumer : consumers) {
                architecture.addEntity(scheduler, consumer);
            }
        } catch (BadParentException bpe) {
            System.out.println(bpe.getMessage());
            System.out.println("Parent Id: " + bpe.getParent().getId());
            System.out.println("Child Id: " + bpe.getChild().getId());
            return;
        }

        // Give experiment to ScheduleSim
        sim.setArchitecture(architecture);

        // Chose output options
        OutputOptions outputOptions = new OutputOptions();
        outputOptions.setRenderSchedule(true); // Render schedule images
        outputOptions.setRenderGif(true); // Render GIF

        // Run the experiment
        try {
            sim.runArchitecture(outputOptions);
        } catch (BadStepsException bse) {
            Log.println(bse.getMessage() + " SimEntity id:" + bse.getSimEntity().getId());
        } catch (BadTaskCompletionException bjce) {
            Log.println(bjce.getMessage() + " Sent:" + bjce.getTasksSent() + " Completed:" + bjce.getTasksCompleted());
        }
    }

    private static void testHierarichalScheduling(Scheduler topScheduler,
            Scheduler subSchedulerOne,
            Scheduler subSchedulerTwo,
            String schedulingName) {
        // Create simulation environment
        ScheduleSim simulator = new ScheduleSim();

        // Create Producer, this will dispatch waves of tasks
        Producer producer = new Producer("RandomLight");

        // Create Task Pattern
        int start = 30;
        int stop = 200;
        int count = 150;
        RandomPattern pattern = new RandomPattern(start, stop, count);
        producer.addMetatask(1, pattern); // One wave of tasks

        // Create Architecture
        Architecture architecture = new Architecture(schedulingName);

        // Create Consumer(s)
        Consumer[] consumers = new Consumer[10];
        consumers[0] = new Consumer(30); // A consumer with a speed of 30 units per step
        consumers[1] = new Consumer(30);
        consumers[2] = new Consumer(20);
        consumers[3] = new Consumer(20);
        consumers[4] = new Consumer(16);
        consumers[5] = new Consumer(16);
        consumers[6] = new Consumer(10);
        consumers[7] = new Consumer(10);
        consumers[8] = new Consumer(8);
        consumers[9] = new Consumer(8);

        // Build Archictecture Tree
        try {
            // Build up tree
            // producer > TopScheduler |> SubSchedulerOne |> consumers[0]
            //                         |                  |> consumers[1]
            //                         |
            //                         |> SubSchedulerTwo |> consumers[2]
            //                                            |> consumers[3]
            //                                            |> consumers[4]
            //                                            |> consumers[5]
            //                                            |> consumers[6]
            //                                            |> consumers[7]
            //                                            |> consumers[8]
            //                                            |> consumers[9]
            architecture.addEntity(producer, topScheduler);
            architecture.addEntity(topScheduler, subSchedulerOne);
            architecture.addEntity(topScheduler, subSchedulerTwo);
            architecture.addEntity(subSchedulerOne, consumers[0]);
            architecture.addEntity(subSchedulerOne, consumers[1]);
            architecture.addEntity(subSchedulerTwo, consumers[2]);
            architecture.addEntity(subSchedulerTwo, consumers[3]);
            architecture.addEntity(subSchedulerTwo, consumers[4]);
            architecture.addEntity(subSchedulerTwo, consumers[5]);
            architecture.addEntity(subSchedulerTwo, consumers[6]);
            architecture.addEntity(subSchedulerTwo, consumers[7]);
            architecture.addEntity(subSchedulerTwo, consumers[8]);
            architecture.addEntity(subSchedulerTwo, consumers[9]);
        } catch (BadParentException bpe) {
            Log.println(bpe.getMessage());
            Log.println("Parent Id: " + bpe.getParent().getId());
            Log.println("Child Id: " + bpe.getChild().getId());
        }

        simulator.setArchitecture(architecture);

        // Define what to output
        OutputOptions outputOptions = new OutputOptions(); // default is makespan and utilisation
        outputOptions.setCountBins(10); // Display task makespans group on 10 bins of task size
        outputOptions.setRenderSchedule(true);
        outputOptions.setRenderGif(true);
        outputOptions.setVisualiseArchitecture(true);

        // Run the experiment
        try {
            simulator.runArchitecture(outputOptions);
        } catch (BadStepsException bse) {
            Log.println(bse.getMessage() + " SimEntity id:" + bse.getSimEntity().getId());
        } catch (BadTaskCompletionException bjce) {
            Log.println(bjce.getMessage() + " Sent:" + bjce.getTasksSent() + " Completed:" + bjce.getTasksCompleted());
        }
    }
}
