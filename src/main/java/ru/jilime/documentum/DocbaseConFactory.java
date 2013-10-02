package ru.jilime.documentum;

/*
Copyright 2013 Jilime.ru

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

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
