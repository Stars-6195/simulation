package schedulesim;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
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
 * @author paul moggridge (paulmogs398@gmail.com)
 */
public class ArchitectureVisualiser {

    private static final int MARGIN = 4;
    private static final int ENTITY_SIZE = 4;
    private static final int V_SPACING = 32;
    private static final int H_SPACING = 24;

    // Entity colors
    private static final Color PRODUCER_COLOR = Color.BLACK;

    private static final Color RANDOM_COLOUR = Color.RED;
    private static final Color SHOPPING_COLOUR = Color.PINK;
    private static final Color ROUND_ROBIN_COLOUR = Color.ORANGE;
    private static final Color WEIGHTED_ROUND_ROBIN_COLOUR = Color.MAGENTA;
    private static final Color MINMIN_COLOUR = Color.YELLOW;
    private static final Color MAXMIN_COLOUR = Color.GREEN;
    private static final Color MXFT_COLOUR = Color.BLUE;
    private static final Color MMMXFT_COLOUR = Color.CYAN;

    private static final Color CONSUMER_COLOUR = Color.DARK_GRAY;
    private static final Color UNKNOWN_COLOUR = Color.LIGHT_GRAY;
    private static final Color LINE_COLOUR = Color.LIGHT_GRAY;

    private final Architecture architecture;

    public ArchitectureVisualiser(Architecture architecture) {
        this.architecture = architecture;
    }

    public void writeToFile(BufferedImage visualArchitecture) throws IOException {
        //Write image
        File outputfile = new File("VisArch_" + architecture.getName() + ".png");
        ImageIO.write(visualArchitecture, "png", outputfile);
    }

    public BufferedImage visualise() {

        // Get width and height
        int width = getWidth();
        int height = getHeight();

        // Create image to draw on
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Prepare to draw on image
        Graphics2D g = image.createGraphics();

        // White background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // Specify current column position
        int xPos = MARGIN;
        int yPos = MARGIN;

        // Draw producer
        g.setColor(Color.BLACK);
        g.fillOval(xPos, yPos, ENTITY_SIZE, ENTITY_SIZE);

        // Advanced x pos position (i.e. the tree starts on the left and flows to the right)
        xPos = xPos + ENTITY_SIZE + H_SPACING;

        // Set to last draw ply as just the producer
        ArrayList<SimEntity> lastPly = new ArrayList<>();
        lastPly.add(architecture.getProducer());

        // Set current ply to the producers children
        ArrayList<SimEntity> currentPly = new ArrayList<>();
        for (SimEntity entity : architecture.getProducer().getChildren()) {
            currentPly.add(entity);
        }

        // Loop drawing the structure
        while (currentPly.size() > 0) {
            for (SimEntity entity : currentPly) {
                if (entity instanceof Scheduler) {
                    if (entity.getClass() == RandomScheduler.class) {
                        g.setColor(RANDOM_COLOUR);
                    } else if (entity.getClass() == ShoppingScheduler.class) {
                        g.setColor(ROUND_ROBIN_COLOUR);
                    } else if (entity.getClass() == RoundRobinScheduler.class) {
                        g.setColor(ROUND_ROBIN_COLOUR);
                    } else if (entity.getClass() == WeightedRoundRobinScheduler.class) {
                        g.setColor(WEIGHTED_ROUND_ROBIN_COLOUR);
                    } else if (entity.getClass() == MinminScheduler.class) {
                        g.setColor(MINMIN_COLOUR);
                    } else if (entity.getClass() == MaxminScheduler.class) {
                        g.setColor(MAXMIN_COLOUR);
                    } else if (entity.getClass() == MaxminFastTrackScheduler.class) {
                        g.setColor(MXFT_COLOUR);
                    } else if (entity.getClass() == MinminMaxminFastTrackScheduler.class) {
                        g.setColor(MMMXFT_COLOUR);
                    }

                    // Draw box to represent scheduler
                    g.fillRect(xPos, yPos, ENTITY_SIZE, ENTITY_SIZE);

                } else if (entity.getClass() == Consumer.class) {
                    // It is consumer
                    g.setColor(CONSUMER_COLOUR);
                    g.fillOval(xPos, yPos, ENTITY_SIZE, ENTITY_SIZE);
                } else {
                    // It is an unknown sim entity, maybe a new scheduler has
                    // been added without this section beeing updated.
                    g.setColor(UNKNOWN_COLOUR);
                    g.fillRect(xPos, yPos, ENTITY_SIZE, ENTITY_SIZE);
                }

                // Draw line from parent to this entity
                for (int i = 0; i < lastPly.size(); i++) {
                    if (entity.getParent() == lastPly.get(i)) {
                        g.setColor(LINE_COLOUR);
                        g.drawLine(
                                xPos - H_SPACING + (ENTITY_SIZE / 2),
                                MARGIN + (ENTITY_SIZE * i) + (V_SPACING * i) + (ENTITY_SIZE / 2),
                                xPos + (ENTITY_SIZE / 2),
                                yPos + (ENTITY_SIZE / 2));
                        break;
                    }
                }

                // Increment y positon (i.e. each ply of the tree goes down the image)
                yPos = yPos + ENTITY_SIZE + V_SPACING;
            }

            ArrayList<SimEntity> newPly = new ArrayList<>();
            for (SimEntity entityToExpand : currentPly) {
                for (SimEntity newEntity : entityToExpand.getChildren()) {
                    newPly.add(newEntity);
                }
            }

            // Current becomes last ply
            lastPly = new ArrayList<>(currentPly);

            // New becomes current ply
            currentPly = newPly;

            // Increment column position
            xPos = xPos + ENTITY_SIZE + V_SPACING;
            
            // Reset row position
            yPos = MARGIN;
        }

        return image;
    }

    private int getWidth() {
        // Get max tree depth
        int maxDepth = 0;

        ArrayList<SimEntity> currentPly = new ArrayList<>();
        currentPly.add(architecture.getProducer());

        while (currentPly.size() > 0) {
            maxDepth++;

            // Get next ply
            ArrayList<SimEntity> newPly = new ArrayList<>();
            for (SimEntity entity : currentPly) {
                for (SimEntity newEntity : entity.getChildren()) {
                    newPly.add(newEntity);
                }
            }

            currentPly = new ArrayList<>(newPly);
        }

        return MARGIN + (maxDepth * ENTITY_SIZE) + (maxDepth * H_SPACING) + MARGIN;
    }

    private int getHeight() {
        // Get max tree breadth
        int maxBreadth = 1;

        ArrayList<SimEntity> currentPly = new ArrayList<>();
        currentPly.add(architecture.getProducer());

        while (currentPly.size() > 0) {

            // Get next ply
            ArrayList<SimEntity> newPly = new ArrayList<>();
            for (SimEntity entity : currentPly) {
                for (SimEntity newEntity : entity.getChildren()) {
                    newPly.add(newEntity);
                }
            }

            // Is this the biggest ply we have seen?
            if (maxBreadth < newPly.size()) {
                maxBreadth = newPly.size();
            }

            currentPly = new ArrayList<>(newPly);
        }

        return MARGIN + (maxBreadth * ENTITY_SIZE) + (maxBreadth * V_SPACING) + MARGIN;
    }
}
