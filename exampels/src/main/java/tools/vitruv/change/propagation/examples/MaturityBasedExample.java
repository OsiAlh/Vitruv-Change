package tools.vitruv.change.propagation.examples;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import maturity.MaturityLevelEnum;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.interaction.UserInteractionFactory;
import tools.vitruv.change.propagation.HumanApprovalTriggerStrategy;
import tools.vitruv.change.propagation.MaturityBasedTriggerStrategy;
import tools.vitruv.change.propagation.examples.domain.MechanicalRequirements;
import tools.vitruv.change.propagation.examples.domain.SoftwareRequirements;
import tools.vitruv.change.propagation.examples.support.TerminalInteractionResultProvider;

/**
 * Demonstrates {@link MaturityBasedTriggerStrategy}: the decision to propagate depends on the
 * maturity levels of the incoming change and the source model.
 *
 * <ul>
 *   <li><b>Scenario A – automatic propagation:</b> the change maturity (FINAL) is at least as
 *       high as the source-model maturity (REVIEWED), so propagation proceeds immediately.
 *   <li><b>Scenario B – human approval required:</b> the change maturity (DRAFT) is lower than
 *       the source-model maturity (FINAL), so the strategy asks a human via the terminal before
 *       propagating. Choosing Delay waits 5 seconds then propagates automatically.
 * </ul>
 *
 * <p>Scenario: a safety engineer reduces the stopping-distance requirement from 50 m to 40 m.
 * Whether the downstream software requirement is updated automatically or requires approval
 * depends on whether the change is sufficiently mature relative to the established model.
 */
public class MaturityBasedExample {

  private static final double INITIAL_SPEED_MS = 100.0 / 3.6; // 100 km/h in m/s

  public static void main(String[] args) {
    System.out.println("=== Maturity-Based Propagation Example ===");
    System.out.println("Strategy: MaturityBasedTriggerStrategy");
    System.out.println();

    runScenarioA();
    runScenarioB();
  }

  // Change maturity (FINAL) >= source model maturity (REVIEWED) → propagate automatically
  private static void runScenarioA() {
    System.out.println("--- Scenario A: Change maturity FINAL, source model maturity REVIEWED ---");
    System.out.println("Expected: automatic propagation without human approval.");
    System.out.println();

    var mechanical = new MechanicalRequirements(50.0);
    var software = new SoftwareRequirements(computeDeceleration(mechanical.getBreakingDistance()));
    printState("Initial state", mechanical, software);

    double newBreakingDistance = 40.0;
    mechanical.setBreakingDistance(newBreakingDistance);
    System.out.printf(
        "Mechanical requirement changed: breakingDistance -> %.1f m%n%n", newBreakingDistance);

    var strategy = new MaturityBasedTriggerStrategy(
        change -> MaturityLevelEnum.FINAL,
        change -> MaturityLevelEnum.REVIEWED);

    VitruviusChange<Uuid> change = stubChange();
    boolean shouldPropagate = strategy.shouldPropagate(change, null);

    if (shouldPropagate) {
      software.setTargetDeceleration(computeDeceleration(mechanical.getBreakingDistance()));
      printState("After propagation", mechanical, software);
    } else {
      System.out.println("Change not propagated.");
    }
  }

  // Change maturity (DRAFT) < source model maturity (FINAL) → ask human
  private static void runScenarioB() {
    System.out.println("--- Scenario B: Change maturity DRAFT, source model maturity FINAL ---");
    System.out.println("Expected: human approval required via terminal.");
    System.out.println();

    var mechanical = new MechanicalRequirements(50.0);
    var software = new SoftwareRequirements(computeDeceleration(mechanical.getBreakingDistance()));
    printState("Initial state", mechanical, software);

    double newBreakingDistance = 40.0;
    mechanical.setBreakingDistance(newBreakingDistance);
    System.out.printf(
        "Mechanical requirement changed: breakingDistance -> %.1f m%n%n", newBreakingDistance);

    var interactor = UserInteractionFactory.instance.createUserInteractor(
        new TerminalInteractionResultProvider());

    // MaturityBasedTriggerStrategy.shouldPropagate() delegates to an internal
    // HumanApprovalTriggerStrategy whose delay cannot be configured from outside.
    // We therefore use determineDecision() directly and supply our own
    // HumanApprovalTriggerStrategy so we can set a short 5-second demo delay.
    var maturityStrategy = new MaturityBasedTriggerStrategy(
        change -> MaturityLevelEnum.DRAFT,
        change -> MaturityLevelEnum.FINAL);

    VitruviusChange<Uuid> change = stubChange();
    boolean shouldPropagate;
    if (maturityStrategy.determineDecision(change) == MaturityBasedTriggerStrategy.Decision.PROPAGATE) {
      shouldPropagate = true;
    } else {
      var approvalStrategy = new HumanApprovalTriggerStrategy(
          "Change is still a DRAFT but the source model is FINAL. Propagate to software requirements?",
          Duration.ofSeconds(5));
      shouldPropagate = approvalStrategy.shouldPropagate(change, interactor);
    }

    System.out.println();
    if (shouldPropagate) {
      software.setTargetDeceleration(computeDeceleration(mechanical.getBreakingDistance()));
      printState("After propagation", mechanical, software);
    } else {
      System.out.println("Propagation denied. Software requirements remain unchanged.");
      printState("Current state", mechanical, software);
    }
  }

  /**
   * Creates a minimal stub {@link VitruviusChange} whose content is not inspected by the
   * maturity providers — they only receive it as context and return a fixed maturity level.
   */
  @SuppressWarnings("unchecked")
  private static VitruviusChange<Uuid> stubChange() {
    return (VitruviusChange<Uuid>) Proxy.newProxyInstance(
        MaturityBasedExample.class.getClassLoader(),
        new Class<?>[] {VitruviusChange.class},
        (proxy, method, methodArgs) -> switch (method.getName()) {
          case "containsConcreteChange" -> false;
          case "getEChanges", "getUserInteractions" -> List.of();
          case "getAffectedEObjects", "getAffectedAndReferencedEObjects",
              "getChangedURIs", "getAffectedEObjectsMetamodelDescriptors" -> Set.of();
          case "copy" -> proxy;
          default -> null;
        });
  }

  private static double computeDeceleration(double breakingDistance) {
    return (INITIAL_SPEED_MS * INITIAL_SPEED_MS) / (2.0 * breakingDistance);
  }

  private static void printState(String label, MechanicalRequirements m, SoftwareRequirements s) {
    System.out.printf("%s:%n  %s%n  %s%n%n", label, m, s);
  }
}
