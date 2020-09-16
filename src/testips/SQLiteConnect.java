package testips;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

//import com.itextpdf.text.List;
import java.util.List;
import java.util.Vector;

public class SQLiteConnect
{
    private static final SQLiteConnect dbcontroller = new SQLiteConnect();
    public static Connection connection;
    //private static String DB_PATH = System.getProperty("user.home") + "/" + "testips/ips";
    private static String DB_PATH;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Fehler beim Laden des JDBC-Treibers");
            e.printStackTrace();
        }
    }
    
    public ArrayList<String> getAllColumnsNames()
    {
    	String sql = "select * from "+Main.dbTabelName+";";
        try {
            
            if (connection != null && !connection.isClosed())
            {
                PreparedStatement pstmt = connection.prepareStatement(sql);
                //pstmt = connection.prepareStatement("select * from ips;");
                connection.setAutoCommit(true);
                ResultSet rs = pstmt.executeQuery();
                ResultSetMetaData rsmd = rs.getMetaData();
                ArrayList temp;
                int count = rsmd.getColumnCount();
               
//               if(rs.next())
//               {
                   temp = new ArrayList();
                   
                   for (int i=1; i<=count; i++)
                       temp.add(rsmd.getColumnLabel(i));
                   rs.close();
           			pstmt.close();
                   return temp;
//               }
            }
            
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList();
            }
        return new ArrayList();
    }
    
    public void createNewTable()
    {
    	try {
    		if(Main.consoleLog)
        		System.out.println("### creating new db file");
			new File(Main.dbPath).createNewFile();
			String sql = "create table '"+Main.dbTabelName+"' (ip varchar type unique, teston date, reachable boolean);";
			if(Main.consoleLog)
				System.out.println("--- "+sql);
			execute(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
        System.out.println("Created table '"+Main.dbTabelName+"'");
    }

    public void dropDB()
    {
        execute("drop database ips;");
    }

    public void dropTable()
    {
        execute("drop table "+Main.dbTabelName);
        System.out.println("Dropped table '"+Main.dbTabelName+"'");
    }
    
    public SQLiteConnect()
    {
        //initDBConnection();
    }
    
    public static SQLiteConnect getInstance(){
        return dbcontroller;
    }
    
    public void initDBConnection(String dbpath) {
        try {
            if (connection != null)
                return;
            if (DB_PATH == null)
                DB_PATH = Main.dbPath;
                
                if(Dao.consoleLog)
                	System.out.println("Connecting to database..." + "jdbc:sqlite:" + DB_PATH);
                connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            if (!connection.isClosed() && Dao.consoleLog)
                System.out.println(" ...connected");
/*
            File dbFile = new File(DB_PATH);
            if (!dbFile.exists())
            {
            	try {
            		dbFile.createNewFile();
            	}catch (Exception e) {
            		e.printStackTrace();
				}
            	dbFile = null;
            }
 */
        } catch (SQLException e)
        {
            System.out.println(" ...not connected");
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run() {
                try {
                    if (!connection.isClosed() && connection != null) {
                        connection.close();
                        if (connection.isClosed())
                            System.out.println("Connection to Database closed");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        );
    }
    public void executeAndLog (String sqlcmd)
    {
        try {
            
        if (connection != null && !connection.isClosed())
        {
            PreparedStatement pstmt = connection.prepareStatement(sqlcmd);
            long t1=System.nanoTime();
            pstmt.executeUpdate();
            long t2=System.nanoTime();
            
            System.err.println(sqlcmd);
            System.out.println("Time [milliseconds]: "+((t2-t1)/Math.pow(10, 6)));
        }
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean execute (String sqlcmd)
    {
        try {
            
        	if(connection == null || connection.isClosed())
        		connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
			PreparedStatement pstmt = connection.prepareStatement(sqlcmd);
			int value = pstmt.executeUpdate();
			if (value != 0)
				return true;
			else
				return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List getAllIpsKnown()
    {
    	List allValues = getAllValues();
    	List <String> allIps = new ArrayList();
        for(Object temp: allValues)
        	allIps.add(((ArrayList)temp).get(0).toString());
        return allIps;
    }

//    public List <Main.Ips> getAllIpsKnown()
//    {
//    	List allValues = getAllValues();
//    	List <String> allIps = new ArrayList();
//    	for(Object temp: allValues)
//    		allIps.add(((ArrayList)temp).get(0).toString());
//    	return allIps;
//    }
    
    public String getDateOfElement(String ip)
    {
    	String sql = "select teston from "+Main.dbTabelName+" where ip = '"+ip+"';";
        try {
            
            if (connection == null || connection.isClosed())
            	initDBConnection(Main.dbPath);
			PreparedStatement pstmt = connection.prepareStatement(sql);
			// pstmt = connection.prepareStatement("select * from ips;");
			connection.setAutoCommit(true);
			ResultSet rs = pstmt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			String result = null;
			if (rs.next())
				result = rs.getString(1);
			rs.close();
    		pstmt.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
    List getAllValues ()
    {
        String sql = "select * from "+Main.dbTabelName+";";
        try {
            
            if(connection == null || connection.isClosed())
        		initDBConnection(Main.dbPath);
			PreparedStatement pstmt = connection.prepareStatement(sql);
			// pstmt = connection.prepareStatement("select * from ips;");
			connection.setAutoCommit(true);
			ResultSet rs = pstmt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			List temp, values;
			values = new ArrayList();
			int count = rsmd.getColumnCount();
			
			while (rs.next()) {
				temp = new ArrayList();

				for (int i = 1; i <= count; i++)
					temp.add(rs.getString(i));
				values.add(temp);
			}
			rs.close();
    		pstmt.close();
			if (values.size() > 0)
				return values;
            
            return new ArrayList();
            
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList();
            }
    }

//    public boolean isElementKnown(String ip)
//    {
//    	List obj = getValues("select * from "+Main.dbTabelName);
//    	List <String> ips = new ArrayList();
//    	for(Object temp: obj)
//    		ips.add(temp.toString());
//    	return ips.contains(ips);
//    }
    
    public List<ArrayList <String>> getValues (String sql)
    {
        try {
        	if (connection == null || connection.isClosed())
            	initDBConnection(Main.dbPath);
            PreparedStatement pstmt = connection.prepareStatement(sql);
            //pstmt = connection.prepareStatement("select * from ips;");
            connection.setAutoCommit(true);
            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            List<ArrayList<String>> values = new ArrayList<ArrayList<String>>();
            ArrayList<String> temp;
            //values = new ArrayList<ArrayList>();
            int count = rsmd.getColumnCount();
            
            while (rs.next())
            {
                temp = new ArrayList<String>();
                for (int i=1; i<=count; i++)
                    temp.add(rs.getString(i));
                values.add(temp);
            }
            rs.close();
    		pstmt.close();
            if (values.size() > 0)
                return values;
            
            return new ArrayList();
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList();
        }
    }
    
    public List <Main.Ips> getValuesKnownObj ()
    {
    	return getValuesObj("select * from "+Main.dbTabelName+" where reachable = 'true'");
    }
    
    public List <Main.Ips> getValuesObj (String sql)
    {
    	try {
    		if (connection == null || connection.isClosed())
    			initDBConnection(Main.dbPath);
    		PreparedStatement pstmt = connection.prepareStatement(sql);
    		//pstmt = connection.prepareStatement("select * from ips;");
    		connection.setAutoCommit(true);
    		ResultSet rs = pstmt.executeQuery();
    		//ResultSetMetaData rsmd = rs.getMetaData();
    		List <Main.Ips> values = new ArrayList();
    		Main.Ips obj;
    		//int count = rsmd.getColumnCount();
    		
    		while (rs.next())
    		{
//    			for (int i=1; i<=count; i++)
//    			{
    			obj = new Main.Ips();
//    			Vector test = new Vector();
//    			test.add(rs.getString(i));
//    			test.add(rs.getString(i));
//    			test.add(rs.getString(i));
    			obj.ip = rs.getString("ip");
    			obj.teston = rs.getString("teston");
    			obj.reachable = rs.getString("reachable").equals("true")? true: false;
    			values.add(obj);
    			
    			//}
    		}
    		rs.close();
    		pstmt.close();
    		return values;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return new ArrayList<Main.Ips>();
    	}
    }
    
    public boolean isIpknown(String ip)
    {
    	try {
    		if (connection == null || connection.isClosed())
            	initDBConnection(Main.dbPath);
                PreparedStatement pstmt = connection.prepareStatement("select * from "+Main.dbTabelName+" where ip = '"+ip+"' and reachable = 'true'");
                //pstmt = connection.prepareStatement("select * from ips;");
                connection.setAutoCommit(true);
                ResultSet rs = pstmt.executeQuery();
                if(rs.next())
                {
                	rs.close();
            		pstmt.close();
                    return true;
                }
                else
                {
                	rs.close();
            		pstmt.close();
                    return false;
                }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
