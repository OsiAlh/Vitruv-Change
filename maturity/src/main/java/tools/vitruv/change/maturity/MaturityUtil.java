package tools.vitruv.change.maturity;

import maturity.MaturityLevelEnum;
import org.eclipse.emf.ecore.EObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for managing maturity states across multiple EObjects.
 * Provides static methods for easy access to maturity tracking.
 */
public class MaturityUtil {
    private static final Map<EObject, MaturityManager> maturityManagers = new HashMap<>();
    
    /**
     * Gets or creates a MaturityManager for the given EObject.
     * @param target the EObject to track maturity for
     * @return the MaturityManager for this object
     */
    public static MaturityManager getOrCreateManager(EObject target) {
        return maturityManagers.computeIfAbsent(target, MaturityManager::new);
    }
    
    /**
     * Gets the current maturity level of an EObject.
     * @param target the EObject to query
     * @return the current maturity level, or null if not tracked
     */
    public static MaturityLevelEnum getMaturityLevel(EObject target) {
        MaturityManager manager = maturityManagers.get(target);
        return manager != null ? manager.getCurrentLevel() : null;
    }
    
    /**
     * Sets the maturity level of an EObject.
     * @param target the EObject to update
     * @param level the new maturity level
     */
    public static void setMaturityLevel(EObject target, MaturityLevelEnum level) {
        getOrCreateManager(target).updateMaturityLevel(level);
    }
    
    /**
     * Checks if an EObject has reached FINAL maturity.
     * @param target the EObject to check
     * @return true if the object is at FINAL maturity
     */
    public static boolean isFinal(EObject target) {
        MaturityManager manager = maturityManagers.get(target);
        return manager != null && manager.isFinal();
    }
    
    /**
     * Checks if an EObject has been reviewed (REVIEWED or FINAL).
     * @param target the EObject to check
     * @return true if the object has been reviewed
     */
    public static boolean isReviewed(EObject target) {
        MaturityManager manager = maturityManagers.get(target);
        return manager != null && manager.isReviewed();
    }

    /**
     * Removes all maturity tracking for the given EObject.
     * @param target the EObject whose tracking should be removed
     */
    public static void removeMaturityTracking(EObject target) {
        maturityManagers.remove(target);
    }

    /**
     * Removes all tracked maturity state.
     */
    public static void clearAll() {
        maturityManagers.clear();
    }
    
}
