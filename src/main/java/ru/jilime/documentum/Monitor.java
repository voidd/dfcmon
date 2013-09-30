package ru.jilime.documentum;

import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import org.apache.commons.cli.*;

import java.io.OutputStream;
import java.io.PrintWriter;

public class Monitor {

    private static DocumentumChecks checks = new DocumentumChecks();

    public static void main(String[] args) throws DfException {
        Options options = construct();
        DocbaseConFactory connection = initial(args, options);

        CommandLineParser parser = new BasicParser();
        try {
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("S")) {
                System.out.println(checks.getSessionCount(connection.getSession()).toString());
            }
            if (line.hasOption("i")) {
                System.out.println(checks.statusOfIA(connection.getSession()));
            }
            if (line.hasOption("W")) {
                System.out.println(checks.getDeadWorkflows(connection.getSession()).toString());
            }
            if (line.hasOption("b")) {
                System.out.println(checks.getBadWorkitems(connection.getSession()).toString());
            }
            if (line.hasOption("F")) {
                if (checks.checkFTSearch(connection.getSession())) System.out.print(0);
            }
            if (line.hasOption("C")) {
                if (checks.fetchContent(connection.getSession())) System.out.print(0);
            }
            if (line.hasOption("q")) {
                System.out.println(checks.getFTFailedQueueSize(connection.getSession(), line.getOptionValue("q")).toString());
            }
            if (line.hasOption("Q")) {
                System.out.println(checks.getQueueSize(connection.getSession()).toString());
            }
            if (line.hasOption("Qt")) {
                System.out.println(checks.getTotalQueueSize(connection.getSession()).toString());
            }
            if (line.hasOption("f")) {
                System.out.println(checks.getFolderItemsCount(connection.getSession(), line.getOptionValue("f")).toString());
            }
            if (line.hasOption("t")) {
                System.out.println(checks.getSystemTime(connection.getSession()));
            }
            if (line.hasOption("td")) {
                System.out.println(checks.getTodayDocsCount(connection.getSession()).toString());
            }

        } catch (Throwable t) {
            DfLogger.fatal(connection.getSession(), t.getMessage(), null, t);
            DfLogger.error(Monitor.class, "Exception while parsing ", null, t);
        } finally {
            connection.releaseConnection();
        }
    }

    private static Options construct() {
        Options options = new Options();
        options.addOption("u", "username", true, "user name in docbase");
        options.addOption("p", "password", true, "password in docbase");
        options.addOption("d", "docbase", true, "docbase name");
        options.addOption("S", "sessions", false, "list sessions count");
        options.addOption("i", "indexagent", false, "show indexagents statuses");
        options.addOption("W", "workflows", false, "show bad workflows count");
        options.addOption("b", "workitems", false, "show bad workitems count");
        options.addOption("C", "content", false, "fetching content from docbase");
        options.addOption("F", "search", false, "search in Fulltext");
        options.addOption("q", "queue", true, "show total number of failed queued items (for user)");
        options.addOption("Q", "queueitem", false, "show total number of queued items marked for deletion");
        options.addOption("Qt", "totalqueueitem", false, "show total number of queue items");
        options.addOption("f", "folder", true, "get items count in particular folder");
        options.addOption("t", "systime", false, "show current docbase time");
        options.addOption("td", "todaydocs", false, "show all documents created today");

        return options;
    }

    private static DocbaseConFactory initial(String[] args, Options options) throws DfException {
        CommandLineParser parser = new BasicParser();
        DocbaseConFactory conFactory = null;
        String username = null;
        String password = null;
        String docbase = null;
        if (args.length < 1) {
            System.out.println("Class usage info:");
            printUsage(options, System.out);
        }

        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("u")) {
                username = line.getOptionValue("u");
            }
            if (line.hasOption("p")) {
                password = line.getOptionValue("p");
            }
            if (line.hasOption("d")) {
                docbase = line.getOptionValue("d");
            }
            conFactory = new DocbaseConFactory(username, password, docbase);
        } catch (ParseException e) {
            DfLogger.error(Monitor.class, "Exception while parsing ", null, e);
            printUsage(options, System.out);
        }
        return conFactory;
    }

    public static void printUsage(final Options options, final OutputStream out) {
        final PrintWriter writer = new PrintWriter(out);
        final HelpFormatter usageFormatter = new HelpFormatter();
        usageFormatter.printUsage(writer, 80, Monitor.class.getName(), options);
        writer.close();
    }
}
