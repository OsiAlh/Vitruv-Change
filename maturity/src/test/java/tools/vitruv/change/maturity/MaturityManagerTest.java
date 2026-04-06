package tools.vitruv.change.maturity;

import maturity.MaturityLevelEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MaturityManager
 */
class MaturityManagerTest {
    
    private EObject testObject;
    private MaturityManager manager;
    
    @BeforeEach
    void setUp() {
        testObject = EcoreFactory.eINSTANCE.createEObject();
        manager = new MaturityManager(testObject);
    }
    
    @Test
    void testInitialMaturityIsDraft() {
        assertEquals(MaturityLevelEnum.DRAFT, manager.getCurrentLevel());
    }
    
    @Test
    void testUpdateMaturityLevel() {
        manager.updateMaturityLevel(MaturityLevelEnum.DRAFT);
        assertEquals(MaturityLevelEnum.DRAFT, manager.getCurrentLevel());
    }
    
    @Test
    void testMaturityLevelProgression() {
        manager.updateMaturityLevel(MaturityLevelEnum.DRAFT);
        assertEquals(MaturityLevelEnum.DRAFT, manager.getCurrentLevel());
        
        manager.updateMaturityLevel(MaturityLevelEnum.REVIEWED);
        assertEquals(MaturityLevelEnum.REVIEWED, manager.getCurrentLevel());
        
        manager.updateMaturityLevel(MaturityLevelEnum.FINAL);
        assertEquals(MaturityLevelEnum.FINAL, manager.getCurrentLevel());
    }
    
    @Test
    void testHistoryIsTracked() {
        manager.updateMaturityLevel(MaturityLevelEnum.DRAFT);
        manager.updateMaturityLevel(MaturityLevelEnum.REVIEWED);
        manager.updateMaturityLevel(MaturityLevelEnum.FINAL);

        // Initial DRAFT entry + REVIEWED + FINAL transitions
        assertEquals(3, manager.getState().getHistory().size());
    }
    
    @Test
    void testIsFinal() {
        assertFalse(manager.isFinal());
        
        manager.updateMaturityLevel(MaturityLevelEnum.FINAL);
        assertTrue(manager.isFinal());
    }
    
    @Test
    void testIsReviewed() {
        assertFalse(manager.isReviewed());
        
        manager.updateMaturityLevel(MaturityLevelEnum.DRAFT);
        assertFalse(manager.isReviewed());
        
        manager.updateMaturityLevel(MaturityLevelEnum.REVIEWED);
        assertTrue(manager.isReviewed());
        
        manager.updateMaturityLevel(MaturityLevelEnum.FINAL);
        assertTrue(manager.isReviewed());
    }
    
    @Test
    void testNoHistoryEntryForSameLevel() {
        assertEquals(1, manager.getState().getHistory().size());

        manager.updateMaturityLevel(MaturityLevelEnum.DRAFT);
        assertEquals(1, manager.getState().getHistory().size());
    }

    @Test
    void testInitialHistoryContainsDraftEntry() {
        assertEquals(1, manager.getState().getHistory().size());
        assertEquals(MaturityLevelEnum.DRAFT, manager.getState().getHistory().get(0).getOldLevel());
        assertEquals(MaturityLevelEnum.DRAFT, manager.getState().getHistory().get(0).getNewLevel());
    }
}
