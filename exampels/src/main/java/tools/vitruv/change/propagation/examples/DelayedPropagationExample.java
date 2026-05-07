package tools.vitruv.change.propagation.examples;

import java.time.Duration;
import tools.vitruv.change.propagation.DelayedTriggerStrategy;
import tools.vitruv.change.propagation.examples.domain.MechanicalRequirements;
import tools.vitruv.change.propagation.examples.domain.SoftwareRequirements;

/**
 * Demonstrates {@link DelayedTriggerStrategy}: a change to the braking distance is recorded
 * immediately, but the propagation to the software requirements is held back for a configured
 * duration. This models a review period before a mechanical change is allowed to affect downstream
 * artifacts.
 *
 * <p>Scenario: a safety engineer reduces the stopping-distance requirement from 50 m to 40 m.
 * The team policy requires a 5-second cool-down before the change is propagated to the ABS/ECU
 * software requirements (shortened to 5 seconds here for the demo; production use would be hours
 * or days).
 */
public class DelayedPropagationExample {

  private static final double INITIAL_SPEED_MS = 100.0 / 3.6; // 100 km/h in m/s
  private static final Duration DEMO_DELAY = Duration.ofSeconds(5);

  public static void main(String[] args) {
    var mechanical = new MechanicalRequirements(50.0);
    var software = new SoftwareRequirements(computeDeceleration(mechanical.getBreakingDistance()));

    System.out.println("=== Delayed Propagation Example ===");
    System.out.println("Strategy: DelayedTriggerStrategy (delay: " + DEMO_DELAY.getSeconds() + " s)");
    System.out.println("Propagation is held back for the configured duration before taking effect.");
    System.out.println();
    printState("Initial state", mechanical, software);

    // Engineer changes the mechanical requirement
    double newBreakingDistance = 40.0;
    mechanical.setBreakingDistance(newBreakingDistance);
    System.out.printf(
        "Mechanical requirement changed: breakingDistance -> %.1f m%n", newBreakingDistance);
    System.out.printf(
        "Waiting %d seconds before propagating (simulating a review period)...%n%n",
        DEMO_DELAY.getSeconds());

    var strategy = new DelayedTriggerStrategy(DEMO_DELAY);
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
