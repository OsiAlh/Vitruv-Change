package tools.vitruv.change.propagation;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.function.Function;
import maturity.MaturityLevelEnum;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.interaction.InternalUserInteractor;

/**
 * Strategy that compares change and source-model maturity levels.
 */
public class MaturityBasedTriggerStrategy implements ChangePropagationTriggerStrategy {
	/**
	 * Decision resulting from maturity comparison.
	 */
	public enum Decision {
		PROPAGATE,
		REQUEST_APPROVAL
	}

	private static final String DEFAULT_PROMPT =
			"Change maturity is lower than the source model. Propagate anyway?";

	private final Function<VitruviusChange<Uuid>, MaturityLevelEnum> changeMaturityProvider;
	private final Function<VitruviusChange<Uuid>, MaturityLevelEnum> sourceModelMaturityProvider;
	private final HumanApprovalTriggerStrategy approvalStrategy;

	/**
	 * Creates the strategy with the default approval prompt.
	 *
	 * @param changeMaturityProvider maps the incoming change to its maturity
	 * @param sourceModelMaturityProvider maps the incoming change to the source model maturity
	 */
	public MaturityBasedTriggerStrategy(
			Function<VitruviusChange<Uuid>, MaturityLevelEnum> changeMaturityProvider,
			Function<VitruviusChange<Uuid>, MaturityLevelEnum> sourceModelMaturityProvider) {
		this(changeMaturityProvider, sourceModelMaturityProvider, DEFAULT_PROMPT);
	}

	/**
	 * Creates the strategy with a custom approval prompt.
	 *
	 * @param changeMaturityProvider maps the incoming change to its maturity
	 * @param sourceModelMaturityProvider maps the incoming change to the source model maturity
	 * @param approvalPrompt message shown when manual approval is needed
	 */
	public MaturityBasedTriggerStrategy(
			Function<VitruviusChange<Uuid>, MaturityLevelEnum> changeMaturityProvider,
			Function<VitruviusChange<Uuid>, MaturityLevelEnum> sourceModelMaturityProvider,
			String approvalPrompt) {
		checkArgument(changeMaturityProvider != null, "changeMaturityProvider must not be null");
		checkArgument(sourceModelMaturityProvider != null, "sourceModelMaturityProvider must not be null");
		checkArgument(approvalPrompt != null && !approvalPrompt.isBlank(), "approvalPrompt must not be blank");
		this.changeMaturityProvider = changeMaturityProvider;
		this.sourceModelMaturityProvider = sourceModelMaturityProvider;
		this.approvalStrategy = new HumanApprovalTriggerStrategy(approvalPrompt);
	}

	/**
	 * Determines whether the change can be propagated without manual approval.
	 *
	 * @param change the incoming change
	 * @return the derived decision
	 */
	public Decision determineDecision(VitruviusChange<Uuid> change) {
		checkArgument(change != null, "change must not be null");
		MaturityLevelEnum changeMaturity = changeMaturityProvider.apply(change);
		MaturityLevelEnum sourceModelMaturity = sourceModelMaturityProvider.apply(change);
		if (changeMaturity == null || sourceModelMaturity == null) {
			return Decision.REQUEST_APPROVAL;
		}
		if (maturityRank(changeMaturity) >= maturityRank(sourceModelMaturity)) {
			return Decision.PROPAGATE;
		}
		return Decision.REQUEST_APPROVAL;
	}

	@Override
	public boolean shouldPropagate(
			VitruviusChange<Uuid> change, InternalUserInteractor userInteractor) {
		Decision decision = determineDecision(change);
		if (decision == Decision.PROPAGATE) {
			return true;
		}
		return approvalStrategy.shouldPropagate(change, userInteractor);
	}

	private static int maturityRank(MaturityLevelEnum level) {
		return switch (level) {
			case DRAFT -> 0;
			case REVIEWED -> 1;
			case FINAL -> 2;
		};
	}
}
