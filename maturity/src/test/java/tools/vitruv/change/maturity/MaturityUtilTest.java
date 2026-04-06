package tools.vitruv.change.maturity;

import maturity.MaturityLevelEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MaturityUtil
 */
class MaturityUtilTest {
    
    private EObject testObject1;
    private EObject testObject2;
    
    @BeforeEach
    void setUp() {
        MaturityUtil.clearAll();
        testObject1 = EcoreFactory.eINSTANCE.createEObject();
        testObject2 = EcoreFactory.eINSTANCE.createEObject();
    }
    
    @Test
    void testGetOrCreateManager() {
        MaturityManager manager1 = MaturityUtil.getOrCreateManager(testObject1);
        MaturityManager manager2 = MaturityUtil.getOrCreateManager(testObject1);
        
        assertSame(manager1, manager2);
    }
    
    @Test
    void testSetAndGetMaturityLevel() {
        MaturityUtil.setMaturityLevel(testObject1, MaturityLevelEnum.DRAFT);
        assertEquals(MaturityLevelEnum.DRAFT, MaturityUtil.getMaturityLevel(testObject1));
    }
    
    @Test
    void testMultipleObjects() {
        MaturityUtil.setMaturityLevel(testObject1, MaturityLevelEnum.DRAFT);
        MaturityUtil.setMaturityLevel(testObject2, MaturityLevelEnum.REVIEWED);
        
        assertEquals(MaturityLevelEnum.DRAFT, MaturityUtil.getMaturityLevel(testObject1));
        assertEquals(MaturityLevelEnum.REVIEWED, MaturityUtil.getMaturityLevel(testObject2));
    }
    
    @Test
    void testIsFinal() {
        assertFalse(MaturityUtil.isFinal(testObject1));
        
        MaturityUtil.setMaturityLevel(testObject1, MaturityLevelEnum.FINAL);
        assertTrue(MaturityUtil.isFinal(testObject1));
    }
    
    @Test
    void testIsReviewed() {
        assertFalse(MaturityUtil.isReviewed(testObject1));
        
        MaturityUtil.setMaturityLevel(testObject1, MaturityLevelEnum.REVIEWED);
        assertTrue(MaturityUtil.isReviewed(testObject1));
    }
    
    @Test
    void testRemoveMaturityTracking() {
        MaturityUtil.setMaturityLevel(testObject1, MaturityLevelEnum.DRAFT);
        assertNotNull(MaturityUtil.getMaturityLevel(testObject1));
        
        MaturityUtil.removeMaturityTracking(testObject1);
        assertNull(MaturityUtil.getMaturityLevel(testObject1));
    }
    
    @Test
    void testClearAll() {
        MaturityUtil.setMaturityLevel(testObject1, MaturityLevelEnum.DRAFT);
        MaturityUtil.setMaturityLevel(testObject2, MaturityLevelEnum.REVIEWED);
        
        MaturityUtil.clearAll();
        
        assertNull(MaturityUtil.getMaturityLevel(testObject1));
        assertNull(MaturityUtil.getMaturityLevel(testObject2));
    }
}
