package tools.vitruv.change.propagation;


import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.interaction.InternalUserInteractor;

/**
 * Strategy that delays propagation for a configured duration.
 */
public class DelayedTriggerStrategy implements ChangePropagationTriggerStrategy {
  private static final Logger LOGGER = LogManager.getLogger(DelayedTriggerStrategy.class);
  private final Duration delay;
  private final Sleeper sleeper;

  /**
   * A default one-day delayed strategy.
   */
  public DelayedTriggerStrategy() {
    this(Duration.ofDays(1));
  }

  /**
   * Creates a strategy with a custom delay.
   *
   * @param delay delay before propagation starts
   */
  public DelayedTriggerStrategy(Duration delay) {
    this(delay, duration -> Thread.sleep(duration.toMillis()));
  }

  DelayedTriggerStrategy(Duration delay, Sleeper sleeper) {
    this.delay = delay;
    this.sleeper = sleeper;
  }

  @Override
  public boolean shouldPropagate(
      VitruviusChange<Uuid> change, InternalUserInteractor userInteractor) {
    if (delay.isZero()) {
      return true;
    }
    LOGGER.info("Waiting {} before propagating change", delay);
    try {
      sleeper.sleep(delay);
      return true;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Propagation delay interrupted", e);
    }
  }

  @FunctionalInterface
  interface Sleeper {
    void sleep(Duration delay) throws InterruptedException;
  }
}
