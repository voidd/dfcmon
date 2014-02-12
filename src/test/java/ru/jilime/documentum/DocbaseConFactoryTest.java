package ru.jilime.documentum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;

public class DocbaseConFactoryTest {

    private static DocbaseConFactory docbaseConFactory;

    @Before
    public void setUp() throws Exception {
        String m_username = "dmadmin";
        String m_password = "dmadmin";
        String m_docbase = "";
        docbaseConFactory = new DocbaseConFactory(m_username, m_password, m_docbase);
    }

    @After
    public void tearDown() throws Exception {
        docbaseConFactory.releaseConnection();
    }

    @Test
    public void testNewSession() throws Exception {
        assertNotNull(docbaseConFactory.newSession());
    }

    @Test
    public void testGetSession() throws Exception {
        assertNotNull(docbaseConFactory.getSession());
    }
}
