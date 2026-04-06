package tools.vitruv.change.maturity;

import maturity.MaturityChange;
import maturity.MaturityFactory;
import maturity.MaturityLevelEnum;
import maturity.MaturityState;
import org.eclipse.emf.ecore.EObject;

import java.util.Date;

/**
 * Manager for tracking and updating the maturity state of an EObject.
 * This class provides methods to track maturity levels and their changes over time.
 */
public class MaturityManager {
    private final MaturityState state;
    
    /**
     * Creates a new MaturityManager for the given target object.
     * Sets the initial maturity level to DRAFT and records this in the history.
     * @param target the EObject to track maturity for
     */
    public MaturityManager(EObject target) {
        this.state = createMaturityState(target);
        // Setting initial maturity level to DRAFT
        MaturityChange initialChange = MaturityFactory.eINSTANCE.createMaturityChange();
        initialChange.setDate(new Date());
        initialChange.setOldLevel(MaturityLevelEnum.DRAFT);
        initialChange.setNewLevel(MaturityLevelEnum.DRAFT);
        this.state.getHistory().add(initialChange);
    }
    
    /**
     * Updates the maturity level and records the change in history.
     * @param newLevel the new maturity level to set
     */
    public void updateMaturityLevel(MaturityLevelEnum newLevel) {
        MaturityLevelEnum oldLevel = getCurrentLevel();
        if (oldLevel != newLevel) {
            MaturityChange change = MaturityFactory.eINSTANCE.createMaturityChange();
            change.setDate(new Date());
            change.setOldLevel(oldLevel);
            change.setNewLevel(newLevel);
            state.getHistory().add(change);
        }
    }
    
    /**
     * Gets the current maturity level.
     * @return the current maturity level, or DRAFT if no changes have been recorded
     */
    public MaturityLevelEnum getCurrentLevel() {
        return state.getHistory().get(state.getHistory().size() - 1).getNewLevel();
    }
    
    /**
     * Gets the complete maturity state including all history.
     * Returns a defensive copy to prevent external mutation of internal state.
     * @return a copy of the MaturityState object
     */
    public MaturityState getState() {
        return copyState(state);
    }
    
    /**
     * Checks if the object has reached the FINAL maturity level.
     * @return true if the current level is FINAL
     */
    public boolean isFinal() {
        return MaturityLevelEnum.FINAL.equals(getCurrentLevel());
    }
    
    /**
     * Checks if the object has been reviewed (REVIEWED or FINAL).
     * @return true if the current level is REVIEWED or FINAL
     */
    public boolean isReviewed() {
        MaturityLevelEnum current = getCurrentLevel();
        return MaturityLevelEnum.REVIEWED.equals(current) || MaturityLevelEnum.FINAL.equals(current);
    }
    
    private MaturityState createMaturityState(EObject target) {
        MaturityState newState = MaturityFactory.eINSTANCE.createMaturityState();
        newState.setTarget(target);
        return newState;
    }

    private MaturityState copyState(MaturityState source) {
        MaturityState copy = MaturityFactory.eINSTANCE.createMaturityState();
        copy.setTarget(source.getTarget());
        for (MaturityChange change : source.getHistory()) {
            copy.getHistory().add(copyChange(change));
        }
        return copy;
    }

    private MaturityChange copyChange(MaturityChange source) {
        MaturityChange copy = MaturityFactory.eINSTANCE.createMaturityChange();
        copy.setDate(source.getDate() == null ? null : new Date(source.getDate().getTime()));
        copy.setOldLevel(source.getOldLevel());
        copy.setNewLevel(source.getNewLevel());
        return copy;
    }
}
