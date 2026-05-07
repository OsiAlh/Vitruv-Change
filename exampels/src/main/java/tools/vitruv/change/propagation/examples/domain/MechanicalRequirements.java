package tools.vitruv.change.propagation.examples.domain;

/**
 * Mechanical requirements for a braking system.
 * Defined by safety engineers specifying how far a vehicle must stop.
 */
public class MechanicalRequirements {

  private double breakingDistance;

  public MechanicalRequirements(double breakingDistance) {
    this.breakingDistance = breakingDistance;
  }

  public double getBreakingDistance() {
    return breakingDistance;
  }

  public void setBreakingDistance(double breakingDistance) {
    this.breakingDistance = breakingDistance;
  }

  @Override
  public String toString() {
    return String.format("MechanicalRequirements{breakingDistance=%.1f m}", breakingDistance);
  }
}
