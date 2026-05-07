package tools.vitruv.change.propagation.examples.domain;

/**
 * Software requirements for a braking system.
 * Derived from mechanical requirements: the ABS/ECU must achieve a certain deceleration
 * to satisfy the stopping-distance constraint.
 */
public class SoftwareRequirements {

  private double targetDeceleration;

  public SoftwareRequirements(double targetDeceleration) {
    this.targetDeceleration = targetDeceleration;
  }

  public double getTargetDeceleration() {
    return targetDeceleration;
  }

  public void setTargetDeceleration(double targetDeceleration) {
    this.targetDeceleration = targetDeceleration;
  }

  @Override
  public String toString() {
    return String.format("SoftwareRequirements{targetDeceleration=%.2f m/s²}", targetDeceleration);
  }
}
