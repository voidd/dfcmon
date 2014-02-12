package ru.jilime.documentum;

import com.documentum.fc.client.IDfSession;
import org.junit.Before;
import org.junit.Test;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import static org.junit.Assert.*;

public class DocumentumChecksTest {

    private static DocumentumChecks checks = new DocumentumChecks();
    private IDfSession dfSession;

    @Before
    public void setUp() throws Exception {
        String m_username = "dmadmin";
        String m_password = "dmadmin";
        String m_docbase = "";
        DocbaseConFactory docbaseConFactory = new DocbaseConFactory(m_username, m_password, m_docbase);
        dfSession = docbaseConFactory.newSession();
    }


    @Test
    public void testGetSessionCount() throws Exception {
        assertNotNull(checks.getSessionCount(dfSession));
    }

    @Test
    public void testGetIndexName() throws Exception {
        List indexes = checks.getIndexName(dfSession);
        assertFalse(indexes.isEmpty());
    }

    @Test
    public void testGetDeadWorkflows() throws Exception {
        assertNotNull(checks.getDeadWorkflows(dfSession));
    }

    @Test
    public void testGetBadWorkitems() throws Exception {
        assertNotNull(checks.getBadWorkitems(dfSession));
    }

    @Test
    public void testGetFTFailedQueueSize() throws Exception {
        assertNotNull(checks.getFTFailedQueueSize(dfSession, "dm_fulltext_index_user"));
    }

    @Test
    public void testGetQueueSize() throws Exception {
        assertNotNull(checks.getQueueSize(dfSession));
    }

    @Test
    public void testGetTotalQueueSize() throws Exception {
        assertNotNull(checks.getTotalQueueSize(dfSession));
    }

    @Test
    public void testGetFolderItemsCount() throws Exception {
        assertNotNull(checks.getFolderItemsCount(dfSession, "/System"));
    }

    @Test
    public void testGetTodayDocsCount() throws Exception {
        assertNotNull(checks.getTodayDocsCount(dfSession));
    }

    @Test
    public void testGetSystemTime() throws Exception {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        assertTrue(checkTime(dateFormat.format(date), checks.getSystemTime(dfSession)));
    } 
    private boolean checkTime(String act, String exp) {
        return act.equals(exp);
    }

    @Test
    public void testCheckFTSearch() throws Exception {
        assertTrue(checks.checkFTSearch(dfSession));
    }

    @Test
    public void testFetchContent() throws Exception {
        assertTrue(checks.fetchContent(dfSession));
    }

    @Test
    public void testStatusOfIA() throws Exception {
        assertNotNull(checks.statusOfIA(dfSession));
    }
}
