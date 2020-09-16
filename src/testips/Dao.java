package testips;

import java.io.File;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.maxmind.geoip2.DatabaseReader;

public class Dao {
	protected static boolean test = false;
    //static DBUtilsNew dbutils;
	//protected static List list_allIps;
	protected static List <String> allIps;
	//protected static int countOfAllIps = 0;
	protected static boolean consoleLog = false;
	protected static boolean consoleLogEvery5Secs = true;
	protected static boolean showNow = true;
	protected static SQLiteConnect sqlite;
	protected static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//protected static Date date;
	protected static DecimalFormat format = new DecimalFormat("0.00");
	protected static DatabaseReader dbReadercity;
	protected static DatabaseReader dbReadercountry;
	protected static String workspace;
    protected static String fileNameAllData;
    protected static String dbTabelName;
    //protected static String dbFileName;
    protected static boolean createNewDB = false;
    protected static boolean createNewDBIfNotExists = true;
    protected static boolean createNewDBIfFileDoesNotExist = true;
    //protected static boolean limit = true;
    //protected static int int_limit = 5;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected static List <String> ipsKnown_ips = new ArrayList();
    protected static String pathToResultFile;
    protected static String dbPath;
    //protected static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    protected static boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
    protected static InetAddress inetAddress = null;
    protected static int limitOfLastCheckToAvoidDBUpdate_inHours = 24;
    protected static File file;
    protected static int globalIndex;
    protected static List <String> allIpsEverFound = null;
    protected static int testLimit = 0;
    protected static boolean onlyStoreConnectionsWithOpenPorts = true;
    protected static boolean waitForNextIPCheck = false;
    protected static String locationFilter;
}
