package ru.jilime.documentum;

import com.documentum.fc.client.*;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class DocumentumChecks {

    private static String OS = System.getProperty("os.name").toLowerCase();

    public DocumentumChecks() {

    }

    protected Integer getSessionCount(IDfSession dfSession) throws DfException {
        isConnected(dfSession);
        final String s = "EXECUTE show_sessions";
        IDfQuery query = new DfQuery();
        query.setDQL(s);
        int count = 0;
        IDfCollection collection = null;
        try {
            collection = query.execute(dfSession, IDfQuery.DF_QUERY);
            while (collection.next()) {
                String status = collection.getString("session_status");
                if (status.equals("Active")) {
                    count++;
                }
            }
        } catch (DfException e) {
            DfLogger.error(Monitor.class, e.getMessage(), null, e);
        } finally {
            if (collection != null) {
                collection.close();
            }
        }
        return count;
    }

    protected List getIndexName(IDfSession dfSession) throws DfException {
        isConnected(dfSession);
        final String s = ("select fti.index_name,iac.object_name as instance_name from dm_f" +
                "ulltext_index fti, dm_ftindex_agent_config iac where fti.index_n" +
                "ame =  iac.index_name and fti.is_standby = false and iac.force_i" +
                "nactive = false");
        List result = new ArrayList<String>();
        IDfCollection collection = null;
        IDfQuery query = new DfQuery();
        query.setDQL(s);
        DfLogger.debug(Monitor.class, query.getDQL(), null, null);
        try {
            collection = query.execute(dfSession, IDfQuery.DF_QUERY);
            while (collection.next()) {
                result.add(new IndexAgentInfo(collection.getString("index_name").trim(),
                        collection.getString("instance_name").trim()));
                DfLogger.debug(Monitor.class, result.toString(), null, null);
            }
        } catch (DfException e) {
            DfLogger.error(Monitor.class, e.getMessage(), null, e);
        } finally {
            if (collection != null) {
                collection.close();
            }
        }
        return result;
    }

    protected Integer getDeadWorkflows(IDfSession dfSession) throws DfException {
        isConnected(dfSession);
        final String s = "SELECT count(*) as cnt FROM dm_workflow w WHERE any w.r_act_state in (3,4)";
        IDfQuery query = new DfQuery();
        query.setDQL(s);
        int count = 0;
        IDfCollection collection = null;
        DfLogger.debug(Monitor.class, query.getDQL(), null, null);
        try {
            collection = query.execute(dfSession, IDfQuery.DF_QUERY);
            if (collection.next()) {
                count = collection.getInt("cnt");
            }
        } catch (DfException e) {
            DfLogger.error(Monitor.class, e.getMessage(), null, e);
        } finally {
            if (collection != null) {
                collection.close();
            }
        }
        return count;
    }

    protected Integer getBadWorkitems(IDfSession dfSession) throws DfException {
        isConnected(dfSession);
        final String s = "select count(*) as cnt from dmi_workitem w, dm_workflow" +
                " wf where  w.r_workflow_id = wf.r_object_id " +
                "and a_wq_name not in (select r_object_id from dm_server_config)";
        IDfQuery query = new DfQuery();
        query.setDQL(s);
        int count = 0;
        IDfCollection collection = null;
        DfLogger.debug(Monitor.class, query.getDQL(), null, null);
        try {
            collection = query.execute(dfSession, IDfQuery.DF_QUERY);
            if (collection.next()) {
                count = collection.getInt("cnt");
            }
        } catch (DfException e) {
            DfLogger.error(Monitor.class, e.getMessage(), null, e);
        } finally {
            if (collection != null) {
                collection.close();
            }
        }
        return count;
    }

    protected Integer getFTFailedQueueSize(IDfSession dfSession, String user) throws DfException {
        isConnected(dfSession);
        final String s = "select count(*) as cnt from dmi_queue_item where name = ''{0}''" +
                " and task_state not in (''failed'',''warning'')";
        IDfQuery query = new DfQuery();
        String dql = MessageFormat.format(s, user);
        query.setDQL(dql);
        int count = 0;
        IDfCollection collection = null;
        DfLogger.debug(Monitor.class, query.getDQL(), null, null);
        try {
            collection = query.execute(dfSession, IDfQuery.DF_QUERY);
            if (collection.next()) {
                count = collection.getInt("cnt");
            }
        } catch (DfException e) {
            DfLogger.error(Monitor.class, e.getMessage(), null, e);
        } finally {
            if (collection != null) {
                collection.close();
            }
        }
        return count;
    }

    protected Integer getQueueSize(IDfSession dfSession) throws DfException {
        isConnected(dfSession);
        final String s = "select count(*) as cnt from dmi_queue_item " +
                "where delete_flag=1 and date_send < date(today)-15";
        IDfQuery query = new DfQuery();
        query.setDQL(s);
        int count = 0;
        IDfCollection collection = null;
        DfLogger.debug(Monitor.class, query.getDQL(), null, null);
        try {
            collection = query.execute(dfSession, IDfQuery.DF_QUERY);
            if (collection.next()) {
                count = collection.getInt("cnt");
            }
        } catch (DfException e) {
            DfLogger.error(Monitor.class, e.getMessage(), null, e);
        } finally {
            if (collection != null) {
                collection.close();
            }
        }
        return count;
    }

    protected Integer getTotalQueueSize(IDfSession dfSession) throws DfException {
        isConnected(dfSession);
        final String s = "select count(*) as cnt from dmi_queue_item";
        IDfQuery query = new DfQuery();
        query.setDQL(s);
        int count = 0;
        IDfCollection collection = null;
        DfLogger.debug(Monitor.class, query.getDQL(), null, null);
        try {
            collection = query.execute(dfSession, IDfQuery.DF_QUERY);
            if (collection.next()) {
                count = collection.getInt("cnt");
            }
        } catch (DfException e) {
            DfLogger.error(Monitor.class, e.getMessage(), null, e);
        } finally {
            if (collection != null) {
                collection.close();
            }
        }
        return count;
    }

    protected Integer getFolderItemsCount(IDfSession dfSession, String folder) throws DfException {
        isConnected(dfSession);
        final String s = "select count(*) as cnt from dm_document(all)" +
                " where folder('/%s')";
        IDfQuery query = new DfQuery();
        String dql = String.format(s, folder);
        query.setDQL(dql);
        int count = 0;
        IDfCollection collection = null;
        DfLogger.debug(Monitor.class, query.getDQL(), null, null);
        try {
            collection = query.execute(dfSession, IDfQuery.DF_QUERY);
            if (collection.next()) {
                count = collection.getInt("cnt");
            }
        } catch (DfException e) {
            DfLogger.error(Monitor.class, e.getMessage(), null, e);
        } finally {
            if (collection != null) {
                collection.close();
            }
        }
        return count;
    }

    protected Integer getTodayDocsCount(IDfSession dfSession) throws DfException {
        isConnected(dfSession);
        final String s = "select count(*) as cnt from dm_document" +
                " where r_creation_date >= DATE(today)";
        IDfQuery query = new DfQuery();
        query.setDQL(s);
        int count = 0;
        IDfCollection collection = null;
        DfLogger.debug(Monitor.class, query.getDQL(), null, null);
        try {
            collection = query.execute(dfSession, IDfQuery.DF_QUERY);
            if (collection.next()) {
                count = collection.getInt("cnt");
            }
        } catch (DfException e) {
            DfLogger.error(Monitor.class, e.getMessage(), null, e);
        } finally {
            if (collection != null) {
                collection.close();
            }
        }
        return count;
    }

    protected String getSystemTime(IDfSession dfSession) throws DfException {
        isConnected(dfSession);
        final String s = "select DATE(now) as systime from dm_docbase_config enable(return_top 1)";
        IDfQuery query = new DfQuery();
        query.setDQL(s);
        String systime = null;
        IDfCollection collection = null;
        DfLogger.debug(Monitor.class, query.getDQL(), null, null);
        try {
            collection = query.execute(dfSession, IDfQuery.DF_QUERY);
            collection.next();
            systime = collection.getString("systime");
        } catch (DfException e) {
            DfLogger.error(Monitor.class, e.getMessage(), null, e);
        } finally {
            if (collection != null) {
                collection.close();
            }
        }
        return systime;
    }

    protected Boolean checkFTSearch(IDfSession dfSession) throws DfException {
        isConnected(dfSession);
        final String s = "select count(r_object_id) as cnt from dm_sysobject" +
                " SEARCH DOCUMENT CONTAINS 'test' enable(return_top 1)";
        IDfQuery query = new DfQuery();
        query.setDQL(s);
        int count = 0;
        IDfCollection collection = null;
        DfLogger.debug(Monitor.class, query.getDQL(), null, null);
        try {
            collection = query.execute(dfSession, IDfQuery.DF_QUERY);
            if (collection.next()) {
                count = collection.getInt("cnt");
            }
        } catch (DfException e) {
            DfLogger.error(Monitor.class, e.getMessage(), null, e);
        } finally {
            if (collection != null) {
                collection.close();
            }
        }
        return count >= 1;
    }

    protected Boolean fetchContent(IDfSession dfSession) throws DfException, IOException {
        isConnected(dfSession);
        final String s = "select r_object_id from dm_document" +
                " where folder('/System/Sysadmin/Reports') enable (RETURN_TOP 1)";
        IDfQuery query = new DfQuery();
        query.setDQL(s);
        Boolean ret = null;
        String filename = null;
        try {
            if (isWindows()) {
                filename = "C:\\TEMP\\file.txt";
            } else if (isUnix()) {
                filename = "/tmp/file.txt";
            }
        } catch (Exception e) {
            DfLogger.error(Monitor.class, e.getMessage(), null, e);
        }
        IDfCollection collection = null;
        DfLogger.debug(Monitor.class, query.getDQL(), null, null);
        try {
            collection = query.execute(dfSession, IDfQuery.DF_QUERY);
            collection.next();
            IDfId id = collection.getId("r_object_id");
            IDfSysObject sysObject = (IDfSysObject) dfSession.getObject(id);
            DfLogger.debug(Monitor.class, id.toString(), null, null);
            sysObject.getFile(filename);
            DfLogger.debug(Monitor.class, sysObject.getFile(filename), null, null);
        } catch (DfException e) {
            DfLogger.error(Monitor.class, e.getMessage(), null, e);
        } finally {
            if (collection != null) {
                collection.close();
            }
        }

        try {
            ret = makeFile(filename);
        } catch (IOException e) {
            DfLogger.error(Monitor.class, e.getMessage(), null, e);
        }

        return ret;
    }

    private Boolean makeFile(String filename) throws IOException {
        File file = new File(filename);
        file.deleteOnExit();
        return file.exists();
    }

    private static boolean isWindows() {
        return (OS.contains("win"));
    }

    private static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("sunos")
                || OS.contains("aix") || OS.contains("HPUX"));
    }

    private boolean isConnected(IDfSession dfSession) {
        return dfSession != null;
    }

    protected String statusOfIA(IDfSession dfSession) throws DfException {
        isConnected(dfSession);
        String ret = null;
        List list = getIndexName(dfSession);
        DfLogger.debug(Monitor.class, list.toString(), null, null);
        IndexAgentInfo agentInfo;
        for (Object aList : list) {
            agentInfo = (IndexAgentInfo) aList;

            String instanceName = agentInfo != null ? agentInfo.get_instance_name() : null;
            String indexName = agentInfo != null ? agentInfo.get_index_name() : null;
            String s = "NULL,FTINDEX_AGENT_ADMIN,NAME,S," +
                    indexName + ",AGENT_INSTANCE_NAME,S," + instanceName + ",ACTION,S,status";
            IDfQuery query = new DfQuery();
            query.setDQL(s);
            IDfCollection collection = null;
            DfLogger.debug(Monitor.class, query.getDQL(), null, null);
            try {
                collection = query.execute(dfSession, IDfQuery.DF_APPLY);
                dfSession.getMessage(1);
                collection.next();
                int count = collection.getValueCount("name");
                for (int ix = 0; ix < count; ix++) {
                    String indexAgentName = collection.getRepeatingString("name", ix);
                    String status = collection.getRepeatingString("status", ix);
                    if (Integer.parseInt(status) == 200) {
                        ret = indexAgentName.concat(" is in not responsible state");
                    } else if (Integer.parseInt(status) == 100) {
                        ret = indexAgentName.concat(" is shutdown");
                    } else if (Integer.parseInt(status) == 0) {
                        ret = indexAgentName.concat(" is running");
                    }
                    DfLogger.debug(Monitor.class, indexAgentName.concat("\n") + indexName.concat("\n")
                            + instanceName.concat("\n"), null, null);
                }
            } catch (DfException e) {
                DfLogger.error(Monitor.class, e.getMessage(), null, e);
            } finally {
                if (collection != null) {
                    collection.close();
                }
            }
        }
        return ret;
    }

    public static class IndexAgentInfo {
        private String m_index_name;
        private String m_instance_name;

        public IndexAgentInfo(String index_name, String instance_name) {
            this.m_index_name = index_name;
            this.m_instance_name = instance_name;
        }

        public String get_index_name() {
            return m_index_name;
        }

        public String get_instance_name() {
            return m_instance_name;
        }

    }
}
