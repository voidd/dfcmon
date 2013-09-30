package ru.jilime.documentum;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfLoginInfo;

public class DocbaseConFactory {

    private static String m_username = null;
    private static String m_password = null;
    private static String m_docbase = null;

    private static IDfSession dfSession = null;

    public DocbaseConFactory(String username, String password, String docbase) {
        m_username=username;
        m_password=password;
        m_docbase=docbase;
    }

    public IDfSession newSession() throws DfException {
        IDfClientX clientx = new DfClientX();
        IDfClient client = clientx.getLocalClient();
        IDfLoginInfo iLogin = clientx.getLoginInfo();

        iLogin.setUser(m_username);
        iLogin.setPassword(m_password);

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
        DfLogger.info(DocbaseConFactory.class, "Success release session in docbase " + m_docbase, null, null);
    }

    public IDfSession getSession() throws DfException {
        return dfSession != null ? dfSession : newSession();
    }
}
