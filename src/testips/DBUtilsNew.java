//package testips.dbutil;
//
//import java.sql.Connection;
//import java.sql.DatabaseMetaData;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.Hashtable;
//import java.util.List;
//import java.util.Set;
//import java.util.Vector;
//
////import org.apache.cxf.binding.corba.wsdl.Array;
////import org.json.JSONArray;
//
////import iafis.soap.communication.Web_to_excel_table;
////import iafis.util.IafisAppletSkeleton;
////import iafis.util.Misc;
////import iafis.util.Props;
//
//public class DBUtilsNew
//{
//    private static String serverName;
//    private static int portNumber;
//    private static String dbName;
//    private static String url;
//    private static String username;
//    private static String pw;
//    private static String tableName;
//    public static boolean consoleLog = true;
//    public static boolean consoleLogOnlyAlterTable = false;
//    public static boolean execute = false;
//    public static boolean showOldSYSKEYNumeric = false;
//    private static Connection connection = null;
//    private static Statement stmt = null;
//    private static DatabaseMetaData dbmd;
//    private static ResultSet rs;
//    
//    public DBUtilsNew () {}
//    
//    public DBUtilsNew (String serverName, int portNumber, String dbName, String userName, String pw, boolean consoleLog)
//    {
//        try
//        {
//            this.serverName = serverName;
//            this.portNumber = portNumber;
//            this.dbName = dbName;
//            this.url = "jdbc:postgresql://"+ this.serverName +":"+ this.portNumber +"/"+ this.dbName;;
//            this.username = userName;
//            this.pw = pw;
//            System.out.println("Trying connection with "+url+", user:"+userName);
//            Class.forName("org.postgresql.Driver");
//            this.connection = DriverManager.getConnection(
//                            url,
//                            username,
//                            pw
//                            );
//            if (consoleLog)
//                System.out.println("Opened database successfully with "+url+", user:"+userName);
//            
//            //stmt = connection.createStatement();
//        }
//        catch (Exception e) {
//            System.err.println(e.toString());
//        }
//    }
//    
//    private void closeConnection ()
//    {
//        try
//        {
//            if (! rs.isClosed()
//                    || rs != null)
//            {
//                rs.close();
//                rs = null;
//            }
//            if (stmt != null)
//                if (stmt.isClosed())
//            {
//                stmt.close();
//                stmt = null;
//            }
//            if (dbmd != null)
//                dbmd = null;
//        }        
//        catch (Exception e) {
//            System.err.println(e.toString());
//        }
//    }
//    
//    public static Vector getDBTableNamesMaxDB (DBconnect dbc)
//    {
//        ResultSet rs = dbc.getResultSet("select tablename from tables where TYPE = 'TABLE'");
//        ResultSetMetaData rsmd = dbc.getMetaData(rs);
//        int countOfColumns = dbc.getColumnCount(rsmd);
//        
//        Vector array = new Vector();
//        
//        while(dbc.next(rs))
//            array.add(dbc.getString(rs, 1));
//        
//        dbc.closeSTMT();
//        
//        if (array.size() > 0)
//        {
//            System.err.println("Found "+array.size()+" tables.");
//            return array;
//        }
//        else
//            return new Vector();
//    }
//
//    public static List vectorToList (Vector vector)
//    {
//        return Collections.list(vector.elements());
//    }
//    
//    public List getDBTableNamesMaxDBWithFKs (DBconnect dbc)
//    {
//        Vector allTables = getDBTableNamesMaxDB(dbc);
//        List LallTables = vectorToList(allTables);
//        List allTablesWithFKs = new ArrayList();
//        
//        for (Object temp: LallTables)
//        {
//            if (hasTableForeignKeys(dbc, temp.toString()))
//            allTablesWithFKs.add(temp.toString());
//        }
//        return allTablesWithFKs;
//    }
//
//    public List getDBTableNamesMaxDBWithoutFKs (DBconnect dbc)
//    {
//        Vector allTables = getDBTableNamesMaxDB(dbc);
//        List LallTables = vectorToList(allTables);
//        List allTablesWithoutFKs = new ArrayList();
//        
//        for (Object temp: LallTables)
//        {
//            if (! hasTableForeignKeys(dbc, temp.toString()))
//                allTablesWithoutFKs.add(temp.toString());
//        }
//        return allTablesWithoutFKs;
//    }
//    
//    private void restartConnection ()
//    {
//        try
//        {
//            if (connection == null || connection.isClosed())
//            {
//                Class.forName("org.postgresql.Driver");
//                connection = DriverManager.getConnection(
//                                url,
//                                username,
//                                pw
//                                );
//            }
//        }
//        catch (Exception e) {
//            System.err.println(e.toString());
//        }
//    }
//    
//    public Vector getDBTableNamesPsql ()
//    {
//        try
//        {
//            restartConnection();
//            dbmd = connection.getMetaData();
//            rs = dbmd.getTables(null, null, null, new String [] {"TABLE"});
//            Vector tableNames = new Vector();
//            
//            while(rs.next())
//                tableNames.add(
//                        rs.getString("TABLE_NAME")
//                        );
//            
//            closeConnection();
//            return tableNames;
//        }
//        catch (Exception e) {
//            System.err.println(e.toString());
//            return null;
//        }
//    }
//    
////    private void closeConnection()
////    {
////        try {
////            
////            if (connection != null)
////            {
////                if (! connection.isClosed())
////                {
////                    connection.close();
////                }
////                connection = null;
////            }
////            
////        }catch (Exception e) {
////            System.err.println(e.toString());
////        }
////    }
//    
//    public boolean execute (String sql)
//    {
//        try {
//            if (connection == null || connection.isClosed())
//            {
//                Class.forName("org.postgresql.Driver");
//                connection = DriverManager.getConnection(
//                        url,
//                        username,
//                        pw
//                        );
//            }
//
//            stmt = connection.createStatement();
//            stmt.executeUpdate(sql);
//            return true;
//        }
//        catch (SQLException e) {
//            System.err.println(e.toString());
//            return false;
//        }
//        catch (Exception e) {
//            System.err.println(e.toString());
//            return false;
//        }
//    }
//    
//    public ResultSet getResultSet (String sql)
//    {
//        try {
//            stmt = connection.createStatement();
//            return stmt.executeQuery(sql);
//        }
//        catch (Exception e) {
//            System.err.println(e.toString());
//            return null;
//        }
//    }
//    
//    public String getString (ResultSet rs, int col)
//    {
//        Object s = null;
//        ResultSetMetaData rsmd;
//        
//        try
//        {
//            rsmd = rs.getMetaData();
//            
//            if(rs != null)
//            {
//                s = rs.getObject(col);
//                if(s==null)
//                    s = "";
//            }
//            
//            String cl = getColumnLabel(rsmd, col).toUpperCase();
////            String colContentWithSYSKEY = new String(
////                    hexToBytes(s.toString())
////                    );
//            
////            if(s instanceof byte[])
////                return(bytesToHex((byte [])s));
//            if(s instanceof Timestamp)
//                return(""+adjustTimestamp((Timestamp)s));
////            else if (! showOldSYSKEYNumeric
////                    && (cl.equals("SYSKEY") || (colContentWithSYSKEY).startsWith("fffe")) // found syskey-column or some content with syskey-like content
////                    )
////                return(colContentWithSYSKEY);
//            else
//                return(grabUnicodeString(""+s));
//        
//        }
//        catch (Exception e) {
//            System.err.println(e.toString());
//            return "";
//        }
//    }
//
////    private String bytesToHex(byte []b)
////    {
////        return(Misc.bytesToHex(b));
////    }
////
////    private byte [] hexToBytes(String hex)
////    {
////        return(Misc.hexToBytes(hex));
////    }
//    
//    public Timestamp adjustTimestamp(Timestamp ts)
//    {
//        if(ts != null)
//        {
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(ts);
//            int year = cal.get(Calendar.YEAR);
//            if(year < 1900)
//            {
//                cal.add(Calendar.YEAR, 1900);
//                ts = new Timestamp(cal.getTime().getTime());
//            }
//        }
//        return(ts);
//    }
//    
//    public static String grabUnicodeString(String str)
//    {
//      StringBuffer ostr = new StringBuffer();
//      String hex;
//      char c;
//
//      for(int i=0; i < str.length(); i++)
//      {
//          char ch = str.charAt(i);
//          if(ch == '\\' && i+5 < str.length())
//          { // \u2345
//              hex = str.substring(i+2,i+6);
//              i += 5;
//              ch = fromHexString(hex);
//          }
//          ostr.append(ch);
//      }
//      return(new String(ostr));
//    }
//    
//    public static char fromHexString(String s)
//    {
//        if(s.length() < 4) return('*');
//        int stringLength = 4;
//        byte[] b = new byte [stringLength / 2];
//
//        for(int i=0, j=0; i < stringLength; i += 2, j++)
//        {
//            int high = charToNibble(s.charAt(i));
//            int low = charToNibble(s.charAt(i+1));
//            b[j] = (byte)(((high << 4)&0xf0) | (low&0x0f));
//        }
//        char c = (char)(((b[0]<<8)&0xff00) | (b[1]&0x00ff));
//        return(c);
//    }
//    
//    private static int charToNibble(char c)
//    {
//        if     ('0' <= c && c <= '9')   return(c - '0');
//        else if('a' <= c && c <= 'f')   return(c - 'a' + 0xa);
//        else if('A' <= c && c <= 'F')   return(c - 'A' + 0xa);
//        else return(0);
//    }
//
//    public ResultSetMetaData getMetaData (ResultSet rs)
//    {
//        try {
//            return rs.getMetaData();
//        }
//        catch (Exception e) {
//            System.err.println(e.toString());
//            return null;
//        }
//    }
//
//    public int getColumnCount (ResultSetMetaData rsmd)
//    {
//        try {
//            return rsmd.getColumnCount();
//        }
//        catch (Exception e) {
//            System.err.println(e.toString());
//            return -1;
//        }
//    }
//
//    public int getCountOfData (String table)
//    {
//        try {
//            ResultSet rs = getResultSet("SELECT * FROM \""+table+"\"");
//            
//            Vector data = new Vector();
//            while (rs.next())
//                data.add(rs.getString(0));
//            
//            if (data.size() > 0)
//                return data.size();
//            else
//                return -1;
//        }
//        catch (Exception e) {
//            System.err.println(e.toString());
//            return -1;
//        }
//    }
//
//    public <List> getData (String table)
//    {
//        try {
//            ResultSet rs = getResultSet("SELECT * FROM \""+table+"\"");
//            int colcount = getColumnCount(getMetaData(rs));
//            
//            List data = new ArrayList();
//            List <String> temp = new ArrayList();
//            while (rs.next())
//            {
//                for (int i=0; i<colcount; i++)
//                {
//                    temp.add(rs.getString(i));
//                }
//                data.ad
//            }
//                
//            if (data.size() > 0)
//                return data.size();
//            else
//                return null;
//        }
//        catch (Exception e) {
//            System.err.println(e.toString());
//            return null;
//        }
//    }
//    
//    public boolean hasTableForeignKeys (DBconnect dbc, String tablename)
//    {
//        int i;
//        String name;
//        String querystr;
//        
//        if((i = tablename.indexOf(".")) >= 0)
//            name = tablename.substring(i+1);
//        else
//            name = tablename;
//        
//            querystr = "select TABLENAME,COLUMNNAME,REFTABLENAME,REFCOLUMNNAME";
//            querystr += " from DOMAIN.FOREIGNKEYCOLUMNS";
//            querystr += " where TABLENAME = '"+name+"'";
//
//        ResultSet rs = dbc.getResultSet(querystr);
//        if(rs != null)
//        {
//            while(dbc.next(rs))
//            {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * This function looks for all table column names with "_SYSKEY" in its names
//     */
//    private List getForeignKeysForUpdatePSQL (String table)
//    {
//        ResultSetMetaData rsmd = getMetaData(getResultSet("select * from \""+table.toUpperCase()+ "\""));
//        int columnCount = getColumnCount(rsmd);
//        List label = new ArrayList();
//        
//        for (int i=1; i<columnCount; i++)
//        {
//            String slabel = getColumnLabel(rsmd, i);
//            
//            if (slabel.contains("_SYSKEY"))
//                label.add(slabel);
//        }
//        
//        if (label.size() > 0)
//            return label;
//        else
//            return new ArrayList();
//    }
//    
//    public boolean hasTableForeignKeysForUpdatePSQL (String tablename)
//    {
//        ResultSetMetaData rsmd = getMetaData(getResultSet("select * from \""+tablename.toUpperCase()+ "\""));
//        int columnCount = getColumnCount(rsmd);
//        
//        for (int i=1; i<columnCount; i++)
//        {
//            String slabel = getColumnLabel(rsmd, i);
//            
//            if (slabel.contains("_SYSKEY"))
//                return true;
//        }
//        return false;
//    }
//
//    public int getCountOfForeignKeysPSQL (String tablename)
//    {
//        List fks = getForeignKeysForUpdatePSQL(tablename);
//        return fks.size();
//    }
//
//    public List <String> getTableForeignKeysForUpdatePSQL (String tablename)
//    {
//        return getForeignKeysForUpdatePSQL(tablename);
//    }
//    
////    public int getCountOfForeignKeysMaxdb (DBconnect dbc, String table)
////    {
////        Vector vector = getForeignKeysOfTableMaxdb(dbc, table);
////        
////        return vector.size();
////    }
////
////    /**
////     * get foreign refcolumnnames as vector(s)
////     */
////    public Vector getForeignKeysOfTableMaxdb (DBconnect dbc, String tablename)
////    {
////        int i;
////        String name;
////        String querystr;
////        
////        if((i = tablename.indexOf(".")) >= 0)
////            name = tablename.substring(i+1);
////        else
////            name = tablename;
////        
////        querystr = "select TABLENAME,COLUMNNAME,REFTABLENAME,REFCOLUMNNAME";
////        querystr += " from DOMAIN.FOREIGNKEYCOLUMNS";
////        querystr += " where TABLENAME = '"+name+"'";
////        
////        Vector tables = new Vector();
////        
////        ResultSet rs = dbc.getResultSet(querystr);
////        if(rs != null)
////        {
////            while(dbc.next(rs))
////            {
////                tables.add(dbc.getString(rs, 2));
////            }
////            
////            if (tables.size() > 0)
////                return tables;
////        }
////        return new Vector();
////    }
//
//    /**
//     * get foreign reftablenames and refcolumnnames as vector(s)
//     */
////    public Vector getForeignReferencesMaxdb (DBconnect dbc, String tablename)
////    {
////        int i;
////        String name;
////        String querystr;
////        
////        if((i = tablename.indexOf(".")) >= 0)
////            name = tablename.substring(i+1);
////        else
////            name = tablename;
////        
////        querystr = "select TABLENAME,COLUMNNAME,REFTABLENAME,REFCOLUMNNAME";
////        querystr += " from DOMAIN.FOREIGNKEYCOLUMNS";
////        querystr += " where TABLENAME = '"+name+"'";
////        
////        Vector tables = new Vector();
////        
////        ResultSet rs = dbc.getResultSet(querystr);
////        if(rs != null)
////        {
////            while(dbc.next(rs))
////            {
////                Vector temp = new Vector();
////                temp.add(dbc.getString(rs, 3));
////                temp.add(dbc.getString(rs, 4));
////                
////                tables.add(temp);
////            }
////            
////            if (tables.size() > 0)
////                return tables;
////        }
////        return new Vector();
////    }
//
//    /**
//     * get foreign reftablenames as vector(s)
//     */
////    public Vector getForeignRefTable (DBconnect dbc, String tablename)
////    {
////        int i;
////        String name;
////        String querystr;
////        
////        if((i = tablename.indexOf(".")) >= 0)
////            name = tablename.substring(i+1);
////        else
////            name = tablename;
////        
////        querystr = "select REFTABLENAME";
////        querystr += " from DOMAIN.FOREIGNKEYCOLUMNS";
////        querystr += " where TABLENAME = '"+name+"'";
////        
////        ResultSet rs = dbc.getResultSet(querystr);
////        if(rs != null)
////        {
////            Vector tables = new Vector();
////            
////            while(dbc.next(rs))
////                tables.add(dbc.getString(rs, 1));
////            
////            if (tables.size() > 0)
////                return tables;
////        }
////        return new Vector();
////    }
//
//    public boolean hasTableData (String table)
//    {
//        try {
//            ResultSet rs = getResultSet("SELECT * FROM \""+table+"\"");
//            
//            Vector data = new Vector();
//            if (rs.next())
//                return true;
//            else
//                return false;
//        }
//        catch (Exception e) {
//            System.err.println(e.toString());
//            return false;
//        }
//    }
//
//    public String getColumnLabel (ResultSetMetaData rsmd, int column)
//    {
//        try {
//            return rsmd.getColumnLabel(column);
//        }
//        catch (Exception e) {
//            System.err.println(e.toString());
//            return null;
//        }
//    }
//
//    public String getColumnTypeName (ResultSetMetaData rsmd, int column)
//    {
//        try {
//            return rsmd.getColumnTypeName(column);
//        }
//        catch (Exception e) {
//            System.err.println(e.toString());
//            return null;
//        }
//    }
//    
//    public void createIndex (String table)
//    {
//        String queryString = "CREATE UNIQUE INDEX CONCURRENTLY \"KEYID_INDEX\" ON \"" + table.toUpperCase() + "\" (\"KEYID\")";
//    }
//    
//    /**
//     * this only works with maxdb
//     */
////    public static void findAndDeleteMultipleForeignKeysMaxdb (DBconnect dbc, String tableName)
////    {
////        Set set;
////        //List <String> doubleEntries = new ArrayList();
////        
////        try {
////            ResultSet rs = dbc.getResultSet("select COLUMNNAME, FKEYNAME from DOMAIN.FOREIGNKEYCOLUMNS where TABLENAME = '"+tableName + "'");
////            ResultSetMetaData rsmd = dbc.getMetaData(rs);
////            int columnCount = dbc.getColumnCount(rsmd);
////            
////            //Vector temp = new Vector();
////            Vector data = new Vector();
////            
////            boolean founddata = false;
////            while (rs.next())
////            {
////                founddata = true;
////                Vector temp = new Vector();
////                for (int col=1; col<=columnCount; col++)
////                    temp.add(rs.getString(col));
////                
////                data.add(temp);
////            }
////            
////            if (! founddata)
////                System.err.println("No foreign keys found in table '"+tableName+"'");
////            else
////                System.err.println("Found foreign keys in table \t'"+tableName+"': <COLUMNNAME, FKEYNAME> " + data);
////            
////            if (data.size() > 0)
////            {
////                set = new HashSet();
////                
////                // Find all columnames
////                if (consoleLog || consoleLogOnlyAlterTable)
////                    if (! consoleLogOnlyAlterTable)
////                        System.out.print("All foreign columnnames in table '"+tableName+"': ");
////                for (int i=0; i<data.size(); i++)
////                {
////                    Vector temp = (Vector) data.get(i);
////                    if (consoleLog || consoleLogOnlyAlterTable)
////                        if (! consoleLogOnlyAlterTable)
////                            System.out.print("'"+temp.get(0) + "' ");
////                }
////                if (consoleLog || consoleLogOnlyAlterTable)
////                    if (! consoleLogOnlyAlterTable)
////                        System.out.println();
////                    
////                boolean foundBadKeys = false;
////                
////                // Find multiple elements in "array". Set doesn't allow multiple entries and will return an error on try. Nice: error will occur on minimum 2nd double-value, so start deleting directly then :)
////                for (int i=0; i<data.size(); i++)
////                {
////                    Vector temp = (Vector) data.get(i);
////                    String columnname = temp.get(0).toString();
////                    String fkeyname = temp.get(1).toString();
////                    
////                    if (set.add(columnname) == false)
////                    {
////                        foundBadKeys = true;
////                        if (consoleLog || consoleLogOnlyAlterTable)
////                            if (! consoleLogOnlyAlterTable)
////                                System.out.println("Elementname_key '"+columnname+ "' in table '"+tableName+"' is double.");
////                        String queryString = "alter table "+tableName+" DROP CONSTRAINT " + fkeyname;
////                        if (consoleLog || consoleLogOnlyAlterTable)
////                            System.out.println(queryString);
////                        if (execute)
////                            dbc.exUpdate(queryString);
////                    }
////                }
////                
////                if (! foundBadKeys)
////                    System.err.println("No multiple fks found in table '"+tableName+"'");
////            }
////            
//////            for (int i=0; i<doubleEntries.size(); i++)
//////            {
//////                for (int fk=1; fk<fknames.size(); fk++)
//////                {
//////                    String queryString = "alter table "+tableName+" drop foreign key '" + fknames.get(fk) +"'";
//////                    System.out.println(queryString);
//////                }
//////            }
////        }
////        catch (Exception e) {
////            System.err.println(e.toString());
////        }
////    }
//    
////    public static class FindDoubleKeys
////    {
////        public static String columnnames;
////        public static String fkeynames;
////        
////        public FindDoubleKeys(){}
////        
////        public FindDoubleKeys(String columnnames, String fkeynames)
////        {
////            this.columnnames = columnnames;
////            this.fkeynames = fkeynames;
////        }
////        
//////        public void setColName(String columnName)
//////        {
//////            this.columnnames = columnName;
//////        }
//////
//////        public void setfkeyName(String fkeyName)
//////        {
//////            this.fkeynames = fkeyName;
//////        }
//////        
//////        public String toString()
//////        {
//////            return (columnnames + " " + fkeynames);
//////        }
////    }
//    
//    public static void main (String [] args)
//    {
////        Object [] params = IafisAppletSkeleton.preMain(new String [1], "");
////        Props props = (Props)params[IafisAppletSkeleton.SAF_PROPS];
////        DBconnect dbc = new DBconnect(props, "");
////        dbc.initLocalHostInfo();
//        
//        String [] tableName = {};
//        boolean checkAllTableNames = false;
//
//        /////////////////////////////////////////////////////
//        // just to fix all infos to run ootb
//        
//        List<String> args_temp = new ArrayList();
//        args_temp.add("-clear");
//        args_temp.add("-execute");
////        args_temp.add("-table");
////        args_temp.add("ALARMPARAMS,BDGROUP");
//        args_temp.add("-allTables");
//        
//        int f=0;
//        args = new String [args_temp.size()];
//        for (String temp: args_temp)
//            args[f++] = temp;
//        
//        /////////////////////////////////////////////////////
//        // end: just to fix all infos to run ootb
//        
//        for (int i=0; i<args.length; i++)
//        {
//            if (args[i].equals("-clear"))
//                consoleLogOnlyAlterTable = true;
//            if (args[i].equals("-table"))
//                tableName = args[i+1].split(",");
//            if (args[i].equals("-execute"))
//                execute = true;
//            if (args[i].equals("-allTables"))
//                checkAllTableNames = true;
//        }
//        
////        List argsList = new ArrayList();
////        for (int i=0; i<args.length; i++)
////            argsList.add(args[i]);
//         
//        // Check parameter, tablename whitelist has priority
//        if ((tableName == null || tableName.length == 0) && ! checkAllTableNames)
//        {
//            System.out.println("I did not find parameter for 'table'. Exit");
//            System.exit(0);
//        }
////        else
////        {
////            if (tableName.length > 0)
////            {
////                for (String temp: tableName)
////                    findAndDeleteMultipleForeignKeysMaxdb(dbc, temp);
////                System.exit(0);
////            }
////            else
////            {
////                Vector allTablesFromMaxDB = getDBTableNamesMaxDB(dbc);
////                
////                for (int i=0; i<allTablesFromMaxDB.size(); i++)
////                    findAndDeleteMultipleForeignKeysMaxdb(dbc, allTablesFromMaxDB.get(i).toString());
////                
////                System.exit(0);
////            }
////        }
//        
//    }
//}
