package tools.vitruv.change.propagation.examples;

import tools.vitruv.change.propagation.AlwaysPropagateTriggerStrategy;
import tools.vitruv.change.propagation.examples.domain.MechanicalRequirements;
import tools.vitruv.change.propagation.examples.domain.SoftwareRequirements;

/**
 * Demonstrates {@link AlwaysPropagateTriggerStrategy}: any change to the braking distance
 * in the mechanical requirements is immediately reflected in the software requirements
 * without any delay or human approval.
 *
 * <p>Scenario: a safety engineer tightens the stopping-distance requirement from 50 m to 40 m.
 * The ABS/ECU software requirement for target deceleration must be updated accordingly.
 *
 * <p>Physical relationship: targetDeceleration = initialSpeed² / (2 * breakingDistance),
 * at a reference initial speed of 100 km/h (27.78 m/s).
 */
public class AlwaysPropagateExample {

  private static final double INITIAL_SPEED_MS = 100.0 / 3.6; // 100 km/h in m/s

  public static void main(String[] args) {
    var mechanical = new MechanicalRequirements(50.0);
    var software = new SoftwareRequirements(computeDeceleration(mechanical.getBreakingDistance()));

    System.out.println("=== Always Propagate Example ===");
    System.out.println("Strategy: AlwaysPropagateTriggerStrategy");
    System.out.println("Changes propagate immediately, no approval required.");
    System.out.println();
    printState("Initial state", mechanical, software);

    // Engineer changes the mechanical requirement
    double newBreakingDistance = 40.0;
    mechanical.setBreakingDistance(newBreakingDistance);
    System.out.printf("Mechanical requirement changed: breakingDistance -> %.1f m%n%n", newBreakingDistance);

    // The trigger strategy decides whether to propagate
    var strategy = new AlwaysPropagateTriggerStrategy();
    boolean shouldPropagate = strategy.shouldPropagate(null, null);

    if (shouldPropagate) {
      software.setTargetDeceleration(computeDeceleration(mechanical.getBreakingDistance()));
      printState("After propagation", mechanical, software);
    } else {
      System.out.println("Change not propagated.");
    }
  }

  private static double computeDeceleration(double breakingDistance) {
    return (INITIAL_SPEED_MS * INITIAL_SPEED_MS) / (2.0 * breakingDistance);
  }

  private static void printState(String label, MechanicalRequirements m, SoftwareRequirements s) {
    System.out.printf("%s:%n  %s%n  %s%n%n", label, m, s);
  }
}
