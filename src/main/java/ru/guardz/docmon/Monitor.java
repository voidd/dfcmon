package ru.guardz.docmon;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.*;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfLoginInfo;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.List;

public class Monitor {

    private static String OS = System.getProperty("os.name").toLowerCase();

    public static void main(String[] args) throws DfException {
        String docbaseName = args[2];
        IDfSessionManager dfSessionManager = initialConnect(args);

        IDfSession dfSession = null;
        try {
            dfSession = dfSessionManager.newSession(docbaseName);
            System.out.println("Successfully connect to the repository ".concat(docbaseName));
/*
            System.out.println("JMS Config ".concat(getJMSConfig(dfSession)));
            List<String> sessions = getActiveSessions(dfSession);
            int size = sessions.size();
            for (int i = 0; i < size; i++) {
                System.out.println(sessions.get(i));
            }*/
            System.out.println("Total open sessions in docbase: ".concat(getSessionCount(dfSession).toString()));

            if (fetchContent(dfSession)) System.out.println("Can fetch content!");

        } catch (Throwable t) {
            DfLogger.fatal(dfSessionManager, t.getMessage(), null, t);
        } finally {
            dfSession.disconnect();
        }
    }

    private static Integer getSessionCount(IDfSession dfSession) throws DfException {
        final String s = "EXECUTE show_sessions";
        IDfQuery query = new DfQuery();
        query.setDQL(s);
        int count = 0;
        IDfCollection collection = query.execute(dfSession, IDfQuery.DF_READ_QUERY);
        try {
            while (collection.next()) {
                count++;
            }
        } catch (DfException e) {
            e.printStackTrace();
        } finally {
            // ALWAYS! clean up your collections
            if (collection != null) {
                collection.close();
            }
        }
        return count;
    }

    private static Boolean fetchContent(IDfSession dfSession) throws DfException {
        final String s = "select * from dm_document where folder('/System/Sysadmin/Reports') enable (RETURN_TOP 1)";
        IDfQuery query = new DfQuery();
        query.setDQL(s);
        Boolean ret;
        String filename = null;
        if (isWindows()) {
            filename = "C:\\TEMP\\file.txt";
        } else if (isSolaris()) {
            filename = "/tmp/file.txt";
        } else if (isUnix()) {
            filename = "/tmp/file.txt";
        }
        IDfCollection collection = query.execute(dfSession, IDfQuery.DF_READ_QUERY);
        try {
            collection.next();
            IDfId id = collection.getId("r_object_id");
            IDfSysObject sysObject = (IDfSysObject) dfSession.getObject(id);
            sysObject.getFile(filename);
        } catch (DfException e) {
            e.printStackTrace();
        } finally {
            if (collection != null) {
                collection.close();
            }
        }

        File f = new File(filename);
        ret = f.exists();

        return ret;
    }

    private static String getJMSConfig(IDfSession dfSession) throws DfException {
        final String s = "SELECT * FROM DM_JMS_CONFIG";
        IDfQuery query = new DfQuery();
        query.setDQL(s);
        String jmsName = null;
        IDfCollection collection = query.execute(dfSession, IDfQuery.DF_READ_QUERY);
        try {
            while (collection.next()) {
                jmsName = collection.getString("object_name");
            }
        } catch (DfException e) {
            e.printStackTrace();
        } finally {
            if (collection != null) {
                collection.close();
            }
        }
        return jmsName;
    }

    private static List<String> getActiveSessions(IDfSession dfSession) throws DfException {
        final String s = "EXECUTE show_sessions";
        IDfQuery query = new DfQuery();
        query.setDQL(s);
        List<String> list = Lists.newArrayList();
        IDfCollection collection = query.execute(dfSession, IDfQuery.DF_READ_QUERY);
        try {
            while (collection.next()) {
                list.add(collection.getString("user_name"));
                list.add(collection.getString("db_session_id"));
                list.add(collection.getString("pid"));
                list.add(collection.getString("client_host"));
            }
        } catch (DfException e) {
            e.printStackTrace();
        } finally {
            if (collection != null) {
                collection.close();
            }
        }
        return list;
    }

    private static IDfSessionManager initialConnect(String[] args) throws DfException {
        IDfClientX clientx = new DfClientX();
        IDfClient client = clientx.getLocalClient();
        IDfLoginInfo iLogin = clientx.getLoginInfo();
        iLogin.setUser(args[0]);
        iLogin.setPassword(args[1]);
        IDfSessionManager dfSessionManager = client.newSessionManager();
        dfSessionManager.setIdentity(args[2], iLogin);
        return dfSessionManager;
    }

    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
    }

    public static boolean isSolaris() {
        return (OS.indexOf("sunos") >= 0);
    }
}
