package testips;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ReadFile
{

//    public static void main(String[] args)
//    {
//    }
    
    public static List <String[]> readFile(String fileNameAndPath)
    {
        File file = new File(fileNameAndPath); 
        List lines = new ArrayList();
        
        try {
        
            if (!file.exists())
            {
                System.err.println("### File doesn't exist");
                return new ArrayList();
            }
            
            //System.out.println("Reading file "+file.getAbsolutePath());
            BufferedReader br = new BufferedReader(new FileReader(file)); 
            String st;
            
            while ((st = br.readLine()) != null)
              lines.add(st.split(","));
        
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return lines;
    }
    
    public static List<String[]> filterWantedCountry(List<String[]> lines, String filter)
    {
        List <String[]> _lines = new ArrayList();
        int countfound = 0;
    	for(int a=0; a<lines.size(); a++)
    	{
    		String [] line = lines.get(a);
    		if(line.length > 2 && line[2].replaceAll("\"", "").toLowerCase().equals(filter.toLowerCase()))
			{
    			_lines.add(line);
    			countfound++;
			}
    	}
    	return _lines;
    }

    public static List <String[]> readFileAndFilterString(String fileNameAndPath, String stringToFilter)
    {
        File file = new File(fileNameAndPath); 
        List <String[]> lines = new ArrayList();
        System.out.println("Finding file '"+fileNameAndPath + "'");
        try {
            if (!file.exists())
            {
                System.err.println("### File doesn't exist");
                return new ArrayList();
            }
            
            BufferedReader br = new BufferedReader(new FileReader(file)); 
            String st;
            System.out.println("Reading lines ...");
            
            int index = 0;
            
            while ((st = br.readLine()) != null)
            {
                if (index == 0 && Main.consoleLog)
                {
                System.out.print("1st line: ");
                System.out.print(st);
                System.out.println();
                }
                else if (index > 0 && Main.consoleLog)
                {
                System.out.print("line "+index+": "+st);
                System.out.println();
                }
                index++;
                
                // make all strings in array to lower case
                if (st.toLowerCase().equals(stringToFilter.toLowerCase())
                        || st.toLowerCase().contains(stringToFilter.toLowerCase()))
                        {
                    
                    String [] s_lines = st.split(",");
                    for (int i=0; i<s_lines.length; i++)
                        s_lines[i] = s_lines[i].toLowerCase().replaceAll("\"", "");
                    
                    for (int i=0; i<s_lines.length; i++)
                        lines.add(s_lines);
                        }
            }
            boolean found;
            if (lines.size() > 0)
                System.out.println("Found '"+stringToFilter+"' in " + lines.size() + " lines");
            else
                System.out.println("Did NOT find '"+stringToFilter+"'");
            
            if (lines.size() > 0)
                return lines;
            
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        if (lines.size() > 0)
            return lines;
        else
            return new ArrayList();
    }
    
//    public static List <String[]> readFileAndFilterString(String fileNameAndPath, String stringToFilter)
//    {
//        File file = new File(fileNameAndPath); 
//        List <String[]> lines = new ArrayList();
//        List <String[]> new_lines = new ArrayList();
//        
//        System.out.println("Finding file '"+fileNameAndPath + "'");
//        boolean consoleLog = false;
//        
//        try {
//            
//            if (!file.exists())
//            {
//                System.err.println("### File doesn't exist");
//                return new ArrayList();
//            }
//            
//            BufferedReader br = new BufferedReader(new FileReader(file)); 
//            String st;
//            System.out.println("Reading lines ...");
//            
//            int index = 0;
//            
//            while ((st = br.readLine()) != null)
//            {
//                if (index == 0 && consoleLog)
//                {
//                    System.out.print("1st line: ");
//                    System.out.print(st);
//                    System.out.println();
//                }
//                else if (index > 0 && consoleLog)
//                {
//                    System.out.print("line "+index+": "+st);
//                    System.out.println();
//                }
//                
//                index++;
//                
//                // make all strings in array to lower case
//                String [] s_lines = st.split(",");
//                for (int i=0; i<s_lines.length; i++)
//                    s_lines[i] = s_lines[i].toLowerCase();
//                
//                for (int i=0; i<s_lines.length; i++)
//                    lines.add(s_lines);
//            }
//            System.out.println("Found "+lines.size()+" lines");
//            
//            boolean found;
//            int i_found = 0;
//            Set <String> countryName = new TreeSet();
//            
//            for (String [] temp: lines)
//            {
//                if (consoleLog)
//                {
//                    if (countryName.add(temp[3]) && consoleLog)
//                    {
//                        System.out.print("Line: ");
//                        for (int i=0; i<temp.length; i++)
//                            System.out.print(temp[i] + ", ");
//                        System.out.println();
//                    }
//                }
//                
//                for (String s_temp: temp)
//                {
//                    //System.out.println("string: "+s_temp);
//                    found = false;
//                    if (s_temp.toLowerCase().equals(stringToFilter.toLowerCase())
//                            || s_temp.toLowerCase().contains(stringToFilter.toLowerCase())
//                            )
//                    {
//                        new_lines.add(temp);
//                        found = true;
//                    }
//                    if (found) i_found++;
//                }
//            }
//            if (i_found > 0)
//                System.out.println("Found '"+stringToFilter+"' in " + i_found + " lines");
//            else
//                System.out.println("Did NOT find '"+stringToFilter+"'");
//            
//            if (new_lines.size() > 0)
//                return new_lines;
//            
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//        
//        if (lines.size() > 0)
//            return lines;
//        else
//            return new ArrayList();
//    }
    
    public static void writeFile (List <String []> lines, String fileNameAndPath)
    {
        try {
            
            System.out.print("Working (Lines)... ");
            String s_lines = "";
            for (int i=0; i<lines.size(); i+=10)
            {
              if (i % 1000 == 0 && i % 5000 != 0)
                  System.out.print(". ");
              else if (i != 0 && i % 5000 == 0)
                  System.out.print(i + " ");
              for (String s_temp: lines.get(i))
                  s_lines += s_temp + ",";
              s_lines += "\n";
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(
                    fileNameAndPath
                    ));
            writer.write(s_lines);
            writer.close();
            
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
