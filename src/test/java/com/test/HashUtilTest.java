package com.test;

import com.util.HashUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author pc
 */
public class HashUtilTest {
    private static HashUtil hashUtil;
    
    public HashUtilTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        hashUtil = new HashUtil();
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testHashNegativeCost() {
        HashUtil hutil = new HashUtil(-1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testHashGreaterThan30Cost() {
        HashUtil hutil = new HashUtil(31);
    }
    
    @Test()
    public void testHashCustomCost() {
        int customCost = 3;
        HashUtil hutil = new HashUtil(customCost);
        String plainPassword = "verysecurepassword123";
        String hash = hutil.hash(plainPassword);
        
        String[] expectedCostInHashStr = hash.split("\\$");
        int usedCost = Integer.parseInt(expectedCostInHashStr[2]);
        
        assertEquals(usedCost, customCost);
    }
    
    @Test(expected = NullPointerException.class)
    public void testHashNullStringInput() {
        String plainPassword = null;
        String hash = hashUtil.hash(plainPassword);
    }
    
    @Test
    public void testHashAuthenticate() {
        String hash = "$31$16$qAR5ChtrU7a1JxYrV9C9m4J6EpO9nu7_HdCazQ80Bz0";
        String plainPassword = "oneofthemostsecurepasswordintheworld";
        assertTrue(hashUtil.authenticate(plainPassword, hash));
    }
    
    @Test
    public void testHashWrongAuthenticate() {
        String hash = "$31$16$qAR5ChtrU7a1JxYrV9C9m4J6EpO9nu7_HdCazQ80Bz0";
        String plainPassword = "definitelytheincorrectpassword";
        assertFalse(hashUtil.authenticate(plainPassword, hash));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testHashAuthenticateInvalidHash() {
        String hash = "";
        String plainPassword = "oneofthemostsecurepasswordintheworld";
        hashUtil.authenticate(plainPassword, hash);
    }
    
    @Test 
    public void testHashUnique() {
        String plainPassword = "oneofthemostsecurepasswordintheworld";
        String hash1 = hashUtil.hash(plainPassword);
        String hash2 = hashUtil.hash(plainPassword);
        
        assertNotEquals(hash1, hash2);
    }
}
