package ru.jilime.documentum;

import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;

public class DocbaseConFactory {

    private final String m_username;
    private final String m_password;
    private final String m_docbase;

    private static IDfSession dfSession;

    public DocbaseConFactory(String username, String password, String docbase) {
        this.m_username = username;
        this.m_password = password;
        this.m_docbase = docbase;
    }

    public IDfSession newSession() throws DfException {
        IDfClient client = new DfClient();
        IDfLoginInfo iLogin = new DfLoginInfo(m_username,m_password);

        try {
            dfSession = client.newSession(m_docbase, iLogin);
            DfLogger.debug(DocbaseConFactory.class, dfSession.toString(), null, null);
        } catch (DfException e) {
            DfLogger.error(DocbaseConFactory.class, e.getMessage(), null, null);
        } finally {
            DfLogger.info(DocbaseConFactory.class, "Success owning session in docbase " + m_docbase, null, null);
        }
        return dfSession;
    }

    public void releaseConnection() throws DfException {
        try {
            dfSession.getSessionManager().release(dfSession);
        } catch (Exception e) {
            DfLogger.error(DocbaseConFactory.class, e.getMessage(), null, null);
        }
        DfLogger.info(DocbaseConFactory.class, "Session is succseed released from docbase " + m_docbase, null, null);
    }

    public IDfSession getSession() throws DfException {
        return dfSession != null ? dfSession : newSession();
    }
}
