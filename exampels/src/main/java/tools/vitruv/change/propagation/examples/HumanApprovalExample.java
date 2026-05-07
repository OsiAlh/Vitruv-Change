package tools.vitruv.change.propagation.examples;

import java.time.Duration;
import tools.vitruv.change.interaction.UserInteractionFactory;
import tools.vitruv.change.propagation.HumanApprovalTriggerStrategy;
import tools.vitruv.change.propagation.examples.domain.MechanicalRequirements;
import tools.vitruv.change.propagation.examples.domain.SoftwareRequirements;
import tools.vitruv.change.propagation.examples.support.TerminalInteractionResultProvider;

/**
 * Demonstrates {@link HumanApprovalTriggerStrategy}: before a change to the braking distance
 * is propagated to the software requirements, an engineer must explicitly approve it via a
 * terminal prompt.
 *
 * <p>Scenario: a safety engineer reduces the stopping-distance requirement from 50 m to 40 m.
 * The propagation framework asks a human reviewer whether to accept, deny, or delay the
 * propagation. Only an explicit acceptance triggers the update in the software requirements.
 *
 * <p>Run this class and type {@code 0} to accept, {@code 1} to deny, or {@code 2} to delay
 * (delays by 5 seconds then propagates automatically).
 */
public class HumanApprovalExample {

  private static final double INITIAL_SPEED_MS = 100.0 / 3.6; // 100 km/h in m/s

  public static void main(String[] args) {
    var mechanical = new MechanicalRequirements(50.0);
    var software = new SoftwareRequirements(computeDeceleration(mechanical.getBreakingDistance()));

    System.out.println("=== Human Approval Example ===");
    System.out.println("Strategy: HumanApprovalTriggerStrategy");
    System.out.println("A human must explicitly approve propagation via the terminal.");
    System.out.println();
    printState("Initial state", mechanical, software);

    // Engineer changes the mechanical requirement
    double newBreakingDistance = 40.0;
    mechanical.setBreakingDistance(newBreakingDistance);
    System.out.printf(
        "Mechanical requirement changed: breakingDistance -> %.1f m%n%n", newBreakingDistance);

    // Build a terminal-backed interactor for the approval dialog
    var interactor = UserInteractionFactory.instance.createUserInteractor(
        new TerminalInteractionResultProvider());

    var strategy = new HumanApprovalTriggerStrategy(
        "The braking distance was changed from 50 m to 40 m. Propagate to software requirements?",
        Duration.ofSeconds(5));
    boolean shouldPropagate = strategy.shouldPropagate(null, interactor);

    System.out.println();
    if (shouldPropagate) {
      software.setTargetDeceleration(computeDeceleration(mechanical.getBreakingDistance()));
      printState("After propagation", mechanical, software);
    } else {
      System.out.println("Propagation denied. Software requirements remain unchanged.");
      printState("Current state", mechanical, software);
    }
  }

  private static double computeDeceleration(double breakingDistance) {
    return (INITIAL_SPEED_MS * INITIAL_SPEED_MS) / (2.0 * breakingDistance);
  }

  private static void printState(String label, MechanicalRequirements m, SoftwareRequirements s) {
    System.out.printf("%s:%n  %s%n  %s%n%n", label, m, s);
  }
}
