package testips;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.net.util.SubnetUtils;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;

public class Main extends Dao
{
    private void findLocationByIp(String ip)
    {
        String dbLocationCountry = workspace + File.separator + "files/GeoLite2-Country.mmdb";
        String dbLocationCity = workspace + File.separator +  "files/GeoLite2-City.mmdb";
        CityResponse cityResponse = null;
        CountryResponse countryResponse = null;
     try {
        
        File databasecity = new File(dbLocationCity);
        File databasecountry = new File(dbLocationCountry);
        if (dbReadercity == null)
            dbReadercity = new DatabaseReader.Builder(databasecity).build();
        if (dbReadercountry == null)
            dbReadercountry = new DatabaseReader.Builder(databasecountry).build();
        
        InetAddress ipAddress = InetAddress.getByName(ip);
        countryResponse = dbReadercountry.country(ipAddress);
        cityResponse = dbReadercity.city(ipAddress);
        
     }catch (Exception e) {
         e.printStackTrace();
    }
        
     if (cityResponse.getCountry().getName() != null)                   System.out.print("countryname: "+cityResponse.getCountry().getName()+", \t");
     if (cityResponse.getCity().getName() != null)                      System.out.print("cityName: " + cityResponse.getCity().getName()+", \t");
     if (cityResponse.getPostal().getCode() != null)                    System.out.print("postal: " + cityResponse.getPostal().getCode()+", \t");
     if (cityResponse.getLeastSpecificSubdivision().getName() != null)  System.out.print("state: "+ cityResponse.getLeastSpecificSubdivision().getName()+", \t");
     if (countryResponse.getCountry().getName() != null)                System.out.print("countryname: "+countryResponse.getCountry().getName()+", \t");
     if (countryResponse.getCountry().getGeoNameId() != null)           System.out.println("geonameid: "+countryResponse.getCountry().getGeoNameId()+", \t");
    }
    
    @SuppressWarnings("rawtypes")
    private boolean isHostOnline (String ip, List ipsKnown)
    {
//        if (ipsKnown.contains(ip))
//        {
//            List temp = sqlite.getValues("select teston from ips where ip = '"+ip+"';");
//            //List <List<Ips>> temp = sqlite.getValuesObj("select teston from ips where ip = '"+ip+"';");
//            String dateLastTest = temp.get(0).toString();
//            System.out.println("Reachable: Host with ip '"+ip+ "' was tested on "+ dateLastTest + " (= "+
//            		Tools.getDifferenceTimeinHoursFromElementDateToNow(dateLastTest)+" hours ago)");
//            // yes, online, but false will end in not updating db and will not change status. -> done :)
//            if(Tools.getDifferenceTimeinHoursFromElementDateToNow(dateLastTest) < limitOfLastCheckToAvoidDBUpdate_inHours)
//            	return false;
//        }
        
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("ping", isWindows? "-c" : "-n", "1", ip);
        //processBuilder.command("path/to/hello.sh");
        //processBuilder.command("cmd.exe", "/c", "dir C:\\Users\\mkyong");

        try {
            //Process process = processBuilder.start();
            inetAddress = InetAddress.getByName(ip);
            //if(process.waitFor(1000, TimeUnit.MILLISECONDS))
            if(inetAddress.isReachable(1000))
            {
            	if (consoleLog)
                    System.out.println("--- "+ip + " \tis reachable");
            	return true;
            }
            else
            	return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private String updateSQLInfoAndGetDate(boolean reachable, String ip)
    {
    	String sql,sqlGetDate,sqlUpdate;
            sql = ""
                    + "insert into "+dbTabelName
                    + "(ip, teston, reachable)"
                    + "values('"
                    + ip+"',"
                    + "datetime('now','localtime'),"
                    + "'true'"
                    + ")"
                    + ";";
            sqlGetDate = "select teston from "+dbTabelName+" where ip = '"+ip+"' and reachable = '0'";
            // sqlite.bool: true=0, false=1
            sqlUpdate = "update "+dbTabelName+" set reachable = 'true' and teston = datetime('now','localtime') where ip = '"+ip+"'";
            if(sqlite.isIpknown(ip)){
            	String s_date = sqlite.getValues(sqlGetDate).get(0).toString();
//            	if(Tools.getDifferenceTimeinHoursFromElementDateToNow(s_date) > 24){
            		sqlite.execute(sqlUpdate);
            		if(consoleLog)
            			System.err.println(sqlUpdate);
            		List<ArrayList<String>> dates = sqlite.getValues(sqlGetDate);
            		ArrayList date = dates.get(0);
            		//String date = dates.get(0);
            		return date.toString();
//            	}else{
//            		System.err.println(sqlGetDate);
//            		String date = sqlite.getValues(sqlGetDate).get(0).toString();
//            		return date;
//            	}
            }else{
            	sqlite.execute(sql);
            	if(consoleLog)
        			System.err.println(sql);
            	return sqlite.getValues(sqlGetDate).get(0).toString();
            }
    }
//    private String insertInfoAndGetDate(boolean reachable, String ip)
//    {
//    	String sql,sqlGetDate,sqlUpdate;
//    	sql = ""
//    			+ "insert into "+dbTabelName
//    			+ "(ip, teston, reachable)"
//    			+ "values('"
//    			+ ip+"',"
//    			+ "datetime('now','localtime'),"
//    			+ "'true'"
//    			+ ")"
//    			+ ";";
//    	sqlGetDate = "select teston from "+dbTabelName+" where ip = '"+ip+"' and reachable = 'true'";
//    	if(sqlite.isIpknown(ip)){
//    		String s_date = sqlite.getValues(sqlGetDate).get(0).toString();
////            	if(Tools.getDifferenceTimeinHoursFromElementDateToNow(s_date) > 24){
//    		sqlite.execute(sql);
//    		System.err.println(sql);
//    		String date = sqlite.getValues(sqlGetDate).get(0).toString();
//    		return date;
////            	}else{
////            		System.err.println(sqlGetDate);
////            		String date = sqlite.getValues(sqlGetDate).get(0).toString();
////            		return date;
////            	}
//    	}else{
//    		sqlite.execute(sql);
//    		System.err.println(sql);
//    		return sqlite.getValues(sqlGetDate).get(0).toString();
//    	}
//    }
    private void updateSQLInfoAndDate(String ip, List listPorts)
    {
    	String sqlUpdate;
    	String ports = String.join(",", listPorts);
    	sqlUpdate = "update "+dbTabelName+" set ports = '"+ports+"' where ip = '"+ip+"'";
    	if (consoleLog)
    		System.err.println(sqlUpdate);
		sqlite.execute(sqlUpdate);
    }
    
    @SuppressWarnings("rawtypes")
	private void testAllAddressesOnLocalNetwork ()
    {
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            while(nis.hasMoreElements())
            {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration ias = ni.getInetAddresses();
                while (ias.hasMoreElements())
                {
                    InetAddress ia = (InetAddress) ias.nextElement();
                    System.out.println(ia.getHostAddress());
                }

            }
        } catch (SocketException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    private void showAllIps (String hostAndMask)
    {
        try {
            InetAddress[] myHost = InetAddress.getAllByName(hostAndMask);
            for(InetAddress host:myHost){
               System.out.println(host.getHostAddress());
            }
         } catch (UnknownHostException ex) {
            ex.printStackTrace();
         }
    }
    
    private void getCountOfIps(String hostAndMask)
    {
        SubnetUtils utils = new SubnetUtils(hostAndMask);
        String [] ips = utils.getInfo().getAllAddresses();
        System.out.println("Count of ips: "+ips.length);
    }
    
//    public Main getObject()
//    {
//    	return this;
//    }
    
//    private boolean areParameterInValid()
//    {
//    	 return fileNameAllData.trim() == null || fileNameAllData.trim().isEmpty()
//    			|| dbPath.trim() == null || dbPath.trim().isEmpty()
//    			|| dbTabelName.trim() == null || dbTabelName.trim().isEmpty()
//    			|| pathToResultFile.trim() == null || pathToResultFile.trim().isEmpty()
//    			|| locationFilter.trim() == null || locationFilter.trim().isEmpty();
//    }

//	private boolean areParametersInValidTest(Object[] parameter)
//	{
//		String textOneParameter = "Parameter is not set or wrong:";
//		String textMoreParameters = "One of parameters is not set or wrong:";
//		for(int i=0; i<parameter.length; i++)
//    	{
//			if(parameter[i] instanceof String)
//			{
//				String temp = parameter[i].toString();
//	    		if(temp == null || temp.trim() == null || temp.trim().isEmpty())
//	    		{
//					if (parameter.length > 1)
//						System.err.println(textMoreParameters+": '"+temp+"'");
//					else
//						System.err.println(textOneParameter+": '"+temp+"'");
//	    			return true;
//	    		}
//			}
//			else if(parameter[i] instanceof Integer || parameter[i] instanceof Short)
//			{
//				int temp = Integer.valueOf(String.valueOf(i));
//				if(temp < 0)
//				{
//					if (parameter.length > 1)
//						System.err.println(textMoreParameters+": '"+temp+"'");
//					else
//	    				System.err.println(textOneParameter+": '"+temp+"'");
//	    			return true;
//				}
//			}
//			else if(parameter[i] instanceof Double || parameter[i] instanceof Float)
//			{
//				double temp = Double.valueOf(String.valueOf(i));
//				if(temp < 0)
//				{
//					if (parameter.length > 1)
//						System.err.println(textMoreParameters+": '"+temp+"'");
//					else
//						System.err.println(textOneParameter+": '"+temp+"'");
//					return true;
//				}
//			}
//			else
//				continue;
//    	}
//    	for(String temp: parameter)
//    	{
//    		if(temp.trim() == null || temp.trim().isEmpty())
//    		{
//    			//System.err.println("parameter @ index "+parameter.);
//    			return true;
//    		}
//    	}
//		return false;
//	}
	private boolean areParametersInValidTest(String[] parameterName, Object[] parameter)
	{
		String checkparameterName;
		String textOneParameter = "Parameter is not set or wrong:";
		String textMoreParameters = "One of parameters is not set or wrong:";
		int length = parameter.length;
		for(int i=0; i<length; i++)
		{
			checkparameterName = parameterName.length >= i ? parameterName[i] : "";
			if(parameter[i] == null) {
				if (parameter.length > 1)
					System.err.println(textMoreParameters+" '"+checkparameterName+"' = '"+parameter[i]+"'");
				else
					System.err.println(textOneParameter+" '"+checkparameterName+"' = '"+parameter[i]+"'");
				return true;
			}
			if(parameter[i] == null || parameter[i] instanceof String)
			{
				String temp = parameter[i].toString();
				if(temp == null || temp.trim() == null || temp.trim().isEmpty())
				{
					if (parameter.length > 1)
						System.err.println(textMoreParameters+" '"+checkparameterName+"' = '"+temp+"'");
					else
						System.err.println(textOneParameter+" '"+checkparameterName+"' = '"+temp+"'");
					return true;
				}
			}
			else if(parameter[i] == null || parameter[i] instanceof Integer || parameter[i] instanceof Short)
			{
				int temp = Integer.valueOf(String.valueOf(i));
				if(temp < 0)
				{
					if (parameter.length > 1)
						System.err.println(textMoreParameters+": '"+checkparameterName+"' = '"+temp+"'");
					else
						System.err.println(textOneParameter+" '"+checkparameterName+"' = '"+temp+"'");
					return true;
				}
			}
			else if(parameter[i] == null || parameter[i] instanceof Double || parameter[i] instanceof Float)
			{
				double temp = Double.valueOf(String.valueOf(i));
				if(temp < 0)
				{
					if (parameter.length > 1)
						System.err.println(textMoreParameters+" '"+checkparameterName+"' = '"+temp+"'");
					else
						System.err.println(textOneParameter+" '"+checkparameterName+"' = '"+temp+"'");
					return true;
				}
			}
			else
				continue;
		}
//    	for(String temp: parameter)
//    	{
//    		if(temp.trim() == null || temp.trim().isEmpty())
//    		{
//    			//System.err.println("parameter @ index "+parameter.);
//    			return true;
//    		}
//    	}
		return false;
	}
    
//	private void updateResultFileWithIps()
//	{
//	    if(!file.exists())
//	        return;
//	    System.out.println("### Reading file with known ips, updateing...");
//	    List <String[]> oldLines = ReadFile.readFile(file.getAbsolutePath());
//	    for(String[] temp: oldLines)
//	    {
//	        // catch header of csv file
//            if(!temp[0].equals("ip"))
//            {
//        	    String dateLastTest = temp[0].toString();
//        	    String ip = temp[1].toString();
//                System.out.println("Host with ip '"+ip+ "' was tested on "+ dateLastTest + " (= "+
//                        Tools.getDifferenceTimeinHoursFromElementDateToNow(dateLastTest)+" hours ago)");
//                if(Tools.getDifferenceTimeinHoursFromElementDateToNow(dateLastTest) < limitOfLastCheckToAvoidDBUpdate_inHours)
//                    continue;
//                else
//                {}
//            }
//	    }
//	}
	
	private List <String> getAllCountryCodesFromFile(List <String[]> lines)
	{
		HashSet<String> set = new HashSet<String>();
		for(String [] temp: lines)
		{
			if(temp != null && temp.length >= 3 && temp[2].replaceAll("\"", "").trim() != null)
				set.add(temp[2]);
			else
				;
		}
		List temp = new ArrayList<String>(set);
		java.util.Collections.sort(temp);
		return temp;
	}
	
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private void run ()
    {
        sqlite = new SQLiteConnect();
        workspace = System.getProperty("user.dir");
        //fileNameAllData = workspace + File.separator + fileNameAllData;
        //pathToResultFile = workspace + File.separator + pathToResultFile;
        file = new File(fileNameAllData);
        //dbPath = workspace+"/ips.sql";
        //dbPath = "";
    	showInfo();
//    	if(areParameterInValid())
//    	if(areParametersInValidTest(new Object []{6, -32.98, fileNameAllData, pathToResultFile, dbPath, dbTabelName, dbTabelName}))
    	if(areParametersInValidTest(
    			new String[]{
    			"fileNameAllData",
    			"dbPath",
    			"dbTabelName",
    			"locationFilter"},
    			new Object []{fileNameAllData, dbPath, dbTabelName, locationFilter})
    			)
    	{
    		System.err.println("Exit");
    		System.exit(1);
    	}
    	if(consoleLog)
    		System.out.println("Reading file "+fileNameAllData);
        List <String[]> lines = ReadFile.readFile(fileNameAllData);
        if(consoleLog)
        	System.out.println("Country codes: "+getAllCountryCodesFromFile(lines));
        lines = ReadFile.filterWantedCountry(lines, locationFilter);
        if(consoleLog)
        	System.out.println("Data with wanted code: "+lines.size());
        ///////////////////////////////////////////////////////////////
        ThreadConsole thread = null;
        ///////////////////////////////////////////////////////////////
        // db
        sqlite.initDBConnection(dbPath);
        if(createNewDBIfNotExists || createNewDB)
        {
	        if(createNewDB)
	        {
	        	sqlite.dropTable();
	        	if(file.exists())
	        		file.delete();
	        }
	        if(!file.exists())
	        	sqlite.createNewTable();
        }
        ///////////////////////////////////////////////////////////////
        // db
        if(consoleLog)
        {
        	System.out.println("### Columns of database: "+sqlite.getAllColumnsNames());
        	if(sqlite.getAllColumnsNames().size() > 0)
        		System.out.println("### ips known: "+sqlite.getAllIpsKnown().size());
        	System.out.println("########################################################");
        }
        try {
        	List ipsKnown = sqlite.getAllValues();
            // add info as ip to list
            for(Object temp: ipsKnown)
        		ipsKnown_ips.add(((ArrayList)temp).get(0).toString());
            allIps = new ArrayList();
            if(consoleLogEvery5Secs)
            {
            	thread = new ThreadConsole(this);
            	thread.start();
            }
            findAllIpsAndSaveToDB(lines, 0);
            ///////////////////////////////////////////////////////////////
            // thread
            if(thread != null && thread.isAlive())
            {
	            try {
					thread.interrupt();
				} catch (Exception e) {
					//e.printStackTrace();
					System.err.println("console.log thread stopped.");
				}
	            thread = null;
            }
//            List <Ips> allIpsKnown = sqlite.getValuesKnownObj();
//            List <Ips> allIpsInRandomSequence = Tools.getElementsInRandomSequenceObj(allIpsKnown);
//            for (int i=0; i<allIpsInRandomSequence.size(); i++)
//            {
//            	Ips ip = allIpsInRandomSequence.get(i);
//            	List ports = findOpenWKPorts(ip);
//            	if(ports.size() > 0)
//            		Tools.exportLineToCSVFile(ip.ip, ip.teston, Tools.listToString(ports));
//            }
            System.out.println("Done, Exit.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

	private void showInfo()
	{
		if(consoleLog)
		{
			System.out.println("########################################################");
			System.out.println("Info:");
			System.out.println("\tLimit of ip-last-check in hours to avoid db update: "+limitOfLastCheckToAvoidDBUpdate_inHours);
	    	System.out.println("\tthis folder: \t\t'"+workspace+"'");
	    	System.out.println("\tfilenamealldata: \t'"+fileNameAllData.substring(fileNameAllData.lastIndexOf(File.separator)+1)+"'");
	    	System.out.println("\tdatabase table name: \t'"+dbTabelName+"'");
	    	System.out.println("\tdatabase file name: \t'"+dbPath+"'");
	    	//System.out.println("\tresult file ["+pathToResultFile.substring(pathToResultFile.lastIndexOf(".")+1)+"]: \t'"+pathToResultFile.substring(pathToResultFile.lastIndexOf(File.separator)+1)+"'");
	    	System.out.println("\tlocation filter: \t'"+locationFilter+"'");
	    	System.out.println("##################################");
		}
	}
    
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void findAllIpsAndSaveToDB(List<String[]> lines, int index)
	{
		boolean updated = true;
		for (int i=index; i<lines.size(); i++)
		{
			index = i;
			if(index >= lines.size()-1)
				System.out.println("ouch");
			if(testLimit > 0)
				for(int g=allIps.size()-1; g>testLimit;g--)
					allIps.remove(g);
			double usedMemory = Tools.getUsedMemory();
			double maxMemory = Tools.getMaxMemory() * 0.8;
			// start run if memory is 'full' and all ips are found (=iterateOverIPRange)
			if (usedMemory >= maxMemory || !updated)
			{
				if(allIps.size() == 0)
					continue;
				allIps = Tools.getElementsInRandomSequence(allIps);
				operate(i);
				updated = true;
			} else {
				String [] line = lines.get(i);
				index = i;
			    String ip = longToIp2(Long.parseLong(line[0].replaceAll("\"", "")));
			    String lastip = longToIp2(Long.parseLong(line[1].replaceAll("\"", "")));
			    
			    if (consoleLog && !consoleLogEvery5Secs)
			    	consoleLog();
			    iterateOverIPRange(
			            ip,
			            lastip
			            );
			    updated = false;
			}
		}
	}

	/**
	 * @param i
	 * @return
	 */
	private void operate(int i)
	{
		if(allIpsEverFound == null)
			allIpsEverFound = new ArrayList<String>();
		for(String ip: allIps)
			allIpsEverFound.add(ip);
		List <String> ipsKnown = sqlite.getAllIpsKnown();
		for (String ip : allIps)
		{
			globalIndex = allIps.indexOf(ip);
			Ips ip_ = new Ips();
			ip_.ip = ip;
			
			// check date of last successful test to avoid testing in small intervals
			if (ipsKnown.contains(ip))
		    {
		        List temp = sqlite.getValues("select teston from ips where ip = '"+ip+"';");
		        String dateLastTest = temp.get(0).toString();
		        ip_.teston = dateLastTest;
		        System.out.println("Reachable: Host with ip '"+ip+ "' was tested on "+ dateLastTest + " (= "+
		        		Tools.getDifferenceTimeinHoursFromElementDateToNow(dateLastTest)+" hours ago)");
		        if(Tools.getDifferenceTimeinHoursFromElementDateToNow(dateLastTest) < limitOfLastCheckToAvoidDBUpdate_inHours)
		        	continue;
		        else
		        {
					if(waitForNextIPCheck)
					{
			        	int random = (int) (Math.random() * 5) + 0;
			        	try {
			        		Thread.sleep(1000 * random);
			        	} catch (InterruptedException e) {
			        		e.printStackTrace();
			        	}
					}
		        	if(isHostOnline(ip, ipsKnown_ips))
		            {
		            	updateSQLInfoAndGetDate(true, allIps.get(i).toString());
		            	List ports = findOpenWKPorts(ip_);
		            	if(!onlyStoreConnectionsWithOpenPorts && ports.size() > 0)
		            	{
		            		updateSQLInfoAndDate(ip, ports);
		            	}
		            }
		        }
		    }
			else
			{
				if(waitForNextIPCheck)
				{
					int random = (int) (Math.random() * 5) + 0;
					try {
						Thread.sleep(1000 * random);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(isHostOnline(ip, ipsKnown_ips))
				{
					updateSQLInfoAndGetDate(true, allIps.get(i).toString());
					List ports = findOpenWKPorts(ip_);
					if(!onlyStoreConnectionsWithOpenPorts && ports.size() > 0)
					{
						updateSQLInfoAndDate(ip, ports);
					}
				}
				
			}
//					Ips ip_ = new Ips();
//					ip_.ip = ip;
//					ip_.teston = 
//					List ports = findOpenWKPorts(ip_);
//	            	if(!onlyStoreConnectionsWithOpenPorts && ports.size() > 0)
//	            		exportedFile = Tools.exportLineToCSVFile(ip_.ip, ip_.teston, Tools.listToString(ports));
		}
		consoleLog();
		allIps.clear();
		// ask data from file for connections already tested
//	            // export data to file
//				List <Ips> obj_IpsKnown = sqlite.getValuesKnownObj();
//	            List <Ips> allIpsInRandomSequence = Tools.getElementsInRandomSequenceObj(obj_IpsKnown);
//	            boolean exportedFile = false;
//	            List <String[]> oldLines = ReadFile.readFile(Main.file.getAbsolutePath());
//	    		List <String> ipsKnownFromFile = new ArrayList();
//	    		for(String [] tempip: oldLines)
//	    		{
//	    			// catch header of csv file
//	    			if(!tempip[0].equals("ip"))
//	    			{
//	    				for(Ips tempIpip: allIpsInRandomSequence)
//	    				{
//	    					if(tempIpip.ip.equals(tempip[0]))
//	    						ipsKnownFromFile.add(tempip[0]);
//	    				}
//	    			}
//	    		}
//	    		System.out.println("##################################");
//	    		System.out.println("\tKnown ips:");
//	    		for(String currentIp: ipsKnownFromFile)
//	    		{
//	    			System.out.println("\t"+currentIp);
//	    		}
//	    		System.out.println("##################################");
//	            for (int g=0; g<allIpsInRandomSequence.size(); g++)
//	            {
//	            	Ips ip = allIpsInRandomSequence.get(g);
//	            	if(ipsKnownFromFile.contains(ip.ip))
//	            		continue;
//	            	List ports = findOpenWKPorts(ip);
//	            	if(!onlyStoreConnectionsWithOpenPorts && ports.size() > 0)
//	            		exportedFile = Tools.exportLineToCSVFile(ip.ip, ip.teston, Tools.listToString(ports));
//	            }
//	            if(exportedFile) System.out.println("exported data to file: "+file.getAbsolutePath());
//	            // end: export data to file
	}
    private String longToIp2(long ip)
    {
        StringBuilder sb = new StringBuilder(15);

        for (int i = 0; i < 4; i++) {

            // 1. 2
            // 2. 1
            // 3. 168
            // 4. 192
            sb.insert(0, Long.toString(ip & 0xff));

            if (i < 3) {
                sb.insert(0, '.');
            }

            // 1. 192.168.1.2
            // 2. 192.168.1
            // 3. 192.168
            // 4. 192
            ip = ip >> 8;

        }

        return sb.toString();
    }
    public void iterateOverIPRange(List <String> ips, String ip, int from, int to)
    {
        for (int i=0; i<to; i++)
        {
        	ips.add(ip + "." + from);
            from++;
        }
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void iterateOverIPRange(String fromip, String toip)
    {
        try {
        	List ips = new ArrayList();
            String fromip_temp = fromip.substring(0, fromip.lastIndexOf("."));
            String first2digitsofip = fromip_temp.substring(0, fromip_temp.lastIndexOf("."));
            String first2digitsofEndip = toip.substring(0, toip.lastIndexOf("."));
            first2digitsofEndip = first2digitsofEndip.substring(0, first2digitsofEndip.lastIndexOf("."));
            int secondPartofip = Integer.parseInt(first2digitsofip.substring(first2digitsofip.indexOf(".")+1));
            int secondPartofEndip = Integer.parseInt(first2digitsofEndip.substring(first2digitsofEndip.indexOf(".")+1));
        int first_ip = Integer.parseInt(fromip_temp.substring(fromip_temp.lastIndexOf(".") + 1));
        
        while (!ips.contains(toip))
        {
            if (secondPartofip != secondPartofEndip)
                return;
            iterateOverIPRange(
            		ips,
                    fromip_temp,
                    0,
                    256
                    );
        fromip_temp = first2digitsofip + "." + first_ip++;
        }
        if (ips.size() > 0
	            && ips.get(0).equals(fromip)
	            && ips.get(ips.size()-1).equals(toip))
	        {
        	for(Object ip: ips)
        		allIps.add(ip.toString());
	        if (consoleLog && !consoleLogEvery5Secs)
	        {
	        	System.out.print(allIps.size());
		        System.out.println();
	        }
	        else if(consoleLog && showNow)
	        {
	        	consoleLog();
	        }
        }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void consoleLog()
    {
    	//System.out.println("-------------------------------");
    	if(allIpsEverFound != null)
    		System.out.print("+++ Count ips: "+allIpsEverFound.size());
    	else
    		System.out.print("Count ips: 0");
    	System.out.print("\t ### used RAM [MB]: ");
    	System.out.print( format.format(Tools.getUsedMemory() / Tools.getMaxMemory()*0.8) + " % \t" );
    	System.out.print(format.format(Tools.getUsedMemory()));
    	System.out.print(" of ");
    	System.out.print(format.format(Tools.getMaxMemory()));
        System.out.print(" (limit: ");
        System.out.print(format.format(Tools.getMaxMemory()*0.8));
        if(allIps != null)
        	System.out.print(") index: "+globalIndex+" / "+allIps.size());
        else
        	System.out.print(") index: "+globalIndex+" / 0");
        System.out.println();
        showNow = false;
        //System.out.println("-------------------------------");
    }
    private boolean isSocketReachable(String ip, int port)
    {
        Socket socket = null;
        try {
        
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), 100);
            socketClose(socket);
            return true;
        
        } catch (Exception e) {
            e.printStackTrace();
            socketClose(socket);
            return false;
        }
    }
    @SuppressWarnings("rawtypes")
	private void findOpenPorts(String ip)
    {
        List ports = getOpenPorts(ip);
        if (ports.size() > 0)
            System.out.println("### Ports open: "+ports);
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private List<String> findOpenWKPorts(Ips ip)
    {
        List ports = getOpenWKPorts(ip);
        if (ports.size() > 0)
        {
            System.out.println("### Ports open: "+ports);
            return ports;
        }
        else
        {
        	System.out.println();
        	return new ArrayList();
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private List getOpenPorts(String ip)
    {
        List ports = new ArrayList();
        Socket socket = null;
        System.out.println("Scanning ip "+ip+" for open ports");
        try {
            
            for (int i=0; i<65536; i++)
            {
                if (i % 100 == 0)
                    System.out.println(i);
                try {
                    socket = new Socket();
                    socket.setSoTimeout(50);
                    socket.connect(new InetSocketAddress(ip, i), 50);
                }catch (Exception e) {
                    socketClose(socket);
                    continue;
                }
                socketClose(socket);
                ports.add(i);
            }
            return ports;
            
        } catch (Exception e) {
            e.printStackTrace();
            socketClose(socket);
            return new ArrayList();
        }
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private List getOpenWKPorts(Ips ip)
    {
        List <Integer> ports = new ArrayList();
        int [] tryPorts = {21, 22, 80, 443};
        Socket socket = null;
        System.out.print("Scanning ip "+ip.ip+" for open ports ");
        for (int i=0; i<tryPorts.length; i++)
        {
            System.out.print(tryPorts[i]);
            if(i<tryPorts.length-1)
                System.out.print(",");
        }
        System.out.println();
        try {
            
            for (int i=0; i<tryPorts.length; i++)
            {
                try {
                    socket = new Socket();
                    socket.setSoTimeout(50);
                    socket.connect(new InetSocketAddress(ip.ip, i), 50);
                }catch (Exception e) {
                    socketClose(socket);
                    continue;
                }
                socketClose(socket);
                ports.add(i);
            }
            return ports;
            
        } catch (Exception e) {
            e.printStackTrace();
            socketClose(socket);
            return new ArrayList();
        }
    }
    
    private void socketClose(Socket socket)
    {
        try {
            if (socket != null)
            {
                socket.close();
                socket = null;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static String showHelp()
    {
    	String help;
    	help = ""
    			+ "This program will read available ips from an cvs-table-file, filter all wanted ips by given location-code and test them to be connectable or not"
    			+ "\nSystax: <-filenamealldata> <-db-tablename> <-db-filename> <-location> <-filenamealldata>"
    			+ "\n-?	\t\t-	show this help and exit"
    			+ "\n-filenamealldata	-	filename of the csv-table-file with all ips"
    			+ "\n-db-tablename>	\t-	tablename of the database to store information. type=sqlite"
    			+ "\n-db-filename>	\t-	filename of the database to store information"
    			+ "\n-location>	\t-	code for country in the csv-table-file"
    			+ "\n";
    	return help;
    }
    
    private static String getLatestFileNameFromDirWithExtension(String dirPath, String extension)
    {
    	File dir = new File(dirPath);
    	File [] files;
    	long lastModifiedTime = Long.MIN_VALUE;
    	File chosenFile = null;
    	if(dir.isDirectory())
    	{
    		files = dir.listFiles();
    		for(File temp: files)
    		{
    			if(temp.getAbsolutePath().toLowerCase().endsWith(extension.toLowerCase()) && temp.lastModified() > lastModifiedTime)
    			{
    				chosenFile = temp;
                    lastModifiedTime = temp.lastModified();
    			}
    		}
    	}
    	if(chosenFile != null)
    		return chosenFile.getAbsolutePath();
    	else
    		return null;
    }
    
    private static boolean isNullOrEmpty(String string)
    {
    	if(string == null || string.trim().isEmpty())
    		return true;
    	else
    		return false;
    }
    
    public static void main (String [] args)
    {
    	for(int i=0; i<args.length; i++)
    	{
    		if(args[i].equals("-test"))
    			test = true;
    		else if(args[i].equals("-no-test"))
    			test = false;
    	}
    	if(test) {
	    	// Fix values for quick start
	    	List <String> list = new ArrayList<String>();
	    	list.add("-filenamealldata");
	    	list.add("IP2LOCATION-LITE-DB1.CSV");
	    	list.add("-db-tablename");
	    	list.add("ips");
	    	list.add("-db-filename");
	    	list.add(System.getProperty("user.home")+"/Nextcloud/privat/ips.sql");
	//    	list.add("-resultfile");
	//    	list.add("ips.csv");
	    	list.add("-location");
	    	list.add("AU");
	    	list.add("-v");
	    	//list.add("-?");
	    	args = new String[list.size()];
	    	for(int i=0; i<list.size(); i++)
	    		args[i] = list.get(i);
	    	list.clear();
	    	// end: Fix values for quick start
    	}
    	dbPath = System.getProperty("user.dir")+"/ips.sql";
    	//fileNameAllData = System.getProperty("user.dir")+"/IP2LOCATION-LITE-DB1.CSV";
    	fileNameAllData = getLatestFileNameFromDirWithExtension(System.getProperty("user.dir"), "csv");
    	dbTabelName = "ip";
    	locationFilter = "au";
//    	if(args.length == 0)
//    	{
//			System.out.println(showHelp());
//			System.exit(0);
//		}
    	for(int i=0; i<args.length; i++)
    	{
    		if(args[i].equals("-?") || args[i].equals("-help"))
    		{
    			System.out.println(showHelp());
    			System.exit(0);
    		}
    		if(args[i].equals("-v"))
    			consoleLog = true;
    		if(args[i].toLowerCase().equals("-filenamealldata")) {
    			if(new File(args[i+1]).exists()) {
					fileNameAllData = args[i+1];
    			} else {
    				fileNameAllData = System.getProperty("user.dir")+"/"+args[i+1];
    			}
    		}
    		if(args[i].toLowerCase().equals("-db-tablename"))
    			dbTabelName = args[i+1];
    		if(args[i].toLowerCase().equals("-db-filename")) {
    			if(new File(args[i+1]).exists()) {
    				dbPath = args[i+1];
    			} else {
    				dbPath = System.getProperty("user.dir")+"/"+args[i+1];
    			}
    		}
//    		if(args[i].toLowerCase().equals("-resultfile"))
//    			pathToResultFile = args[i+1];
    		if(args[i].toLowerCase().equals("-location"))
    			locationFilter = args[i+1];
    		if(args[i].toLowerCase().equals("-create-new-database"))
    			createNewDB = true;
    	}
    	// show parameter
    	if(consoleLog)
    	{
    		System.out.print("### Parameter: ");
    		for(int i=0; i<args.length; i++)
    		{
    			System.out.print(args[i]);
    			if(i<args.length-1)
    				System.out.print(", ");
    			else
    				System.out.println();
    		}
    	}
//    	if(isNullOrEmpty(fileNameAllData)){
//    		System.err.println("Value of fileNameAllData is empty");
//    		System.exit(1);
//    	}
//    	else if(isNullOrEmpty(locationFilter)){
//    		System.err.println("Value of locationFilter is empty");
//    		System.exit(1);
//    	}
//		else if(isNullOrEmpty(dbTabelName)){
//			System.err.println("Value of dbTabelName is empty");
//			System.exit(1);
//		}
//		else if(isNullOrEmpty(dbPath)){
//			System.err.println("Value of dbPath is empty");
//			System.exit(1);
//		}
        new Main().run();
    }
    
    public static class Ips
    {
    	public String ip;
    	public String teston;
    	public boolean reachable;
    	public Ips(){};
    }
}
