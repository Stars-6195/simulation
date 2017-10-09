package schedulesim;

/**
 * This work is licensed under the Creative Commons Attribution 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * 
 * @author paul moggridge (paulmogs398@gmail.com)
 */
public class OutputOptions {

  private boolean makespan;
  private boolean utilisation;
  private int countBins;

  private boolean renderSchedule;
  private boolean renderGif;
  private boolean visualiseArchitecture;

  public OutputOptions(){
    this.makespan= true;
    this.utilisation = true;
    this.countBins = 0;
    this.renderSchedule = false;
    this.renderGif = false;
    this.visualiseArchitecture = false;
  }

  public boolean isMakespan() {
    return makespan;
  }

  public void setMakespan(boolean makespan) {
    this.makespan = makespan;
  }

  public boolean isUtilisation() {
    return utilisation;
  }

  public void setUtilisation(boolean utilisation) {
    this.utilisation = utilisation;
  }

  public int getCountBins() {
    return countBins;
  }

  public void setCountBins(int countBins) {
    this.countBins = countBins;
  }

  public boolean isRenderSchedule() {
    return renderSchedule;
  }

  public void setRenderSchedule(boolean renderSchedule) {
    this.renderSchedule = renderSchedule;
  }

  public boolean isRenderGif(){
    return renderGif;
  }
  
  public void setRenderGif(boolean renderGif){
    this.renderGif = renderGif;
  }

  public boolean isVisualiseArchitecture() {
      return visualiseArchitecture;
  }

  public void setVisualiseArchitecture(boolean visualiseArchitecture) {
      this.visualiseArchitecture = visualiseArchitecture;
  }
  
  public void setNoPrinting(){
    // Clear all variables that involve printing
    makespan = false;
    utilisation = false;
    countBins = 0;
  }
  
  public boolean isPrintingRequested(){
    return (makespan || utilisation || (countBins > 0) );
  }
}
