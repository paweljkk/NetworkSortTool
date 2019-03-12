//Version 1.03 -Pawel Klimek
//Network Sorting tool

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import static java.nio.file.StandardCopyOption.*;


public class rSorting {

public static int bootconfigCounter =0;
public static int overrideCounter =0;
public static int aclCounter =0;

    public static void main(String[] args) {
       	Scanner reader = new Scanner(System.in);
		
		String sortpath = args[0];
        String createdir = args[1];
        Path inputCheckPath= Paths.get(sortpath, "");     
        Path outputCheckPath= Paths.get(createdir, "");    
        //if(createdir.contains(sortpath))
        if(outputCheckPath.startsWith(inputCheckPath))
        {
            System.out.println("\nPlease pick a location outside of the sort location");
            Scanner in = new Scanner(System.in);
            in.nextLine();
            System.exit(0);
        }
               

        new File(createdir).mkdir();
        File workdir = new File(createdir);


        new File(createdir +"\\bootCFGs").mkdir();
        new File(createdir +"\\overrides").mkdir();
        new File(createdir +"\\acls").mkdir();

        String newFile = createdir;
        File root = new File(sortpath); 
 
        find_files(root, newFile, root.toString());
        System.out.println("!!! Number of boot.cfgs: " + bootconfigCounter);
        System.out.println("!!! Number of primary overrides: " + overrideCounter);
        System.out.println("!!! Number of primary acls: " + aclCounter);
        reader.close();
    }

    public static void find_files(File root, String newFile, String basicPath)
    {   
        File[] files = root.listFiles();
        for(File file : files)
        {
            if (file.isFile())
            {
                //System.out.println(file.getName());
                if(!(file.getName().contains("secondary")))
                {
                    
                    try{
                        Scanner scanner = new Scanner(file);
                        
                        while (scanner.hasNextLine()){
                            String lineFromFile = scanner.nextLine();
                            if(lineFromFile.contains("SETDefault -SYS NMPrompt =")){

                               
                                if( new File(newFile + "\\bootCFGs\\" + lineFromFile.substring(28, lineFromFile.length()-3) + ".cfg").isFile()) {
                                    System.out.println("!!! Warning: Duplicate Config !!!");                    
                                } 

                                System.out.println(lineFromFile.substring(28, lineFromFile.length()-3));        
                                Path dest = Paths.get(newFile + "\\bootCFGs\\" + lineFromFile.substring(28, lineFromFile.length()-3) + ".cfg", "");
                                Path src = Paths.get(root + "\\" + file.getName(), "");
                                
                                Files.copy(src, dest, REPLACE_EXISTING);
                                bootconfigCounter ++;
                                
                            }

                        }

                    }catch (Exception ex) {}
                }
                if(file.getName().contains("primary#override"))
                {
                    try{
                        //System.out.println(root.toString().replace(basicPath,"").substring(1));
                        int index = root.toString().lastIndexOf("\\")+1;
                        Path dest = Paths.get(newFile + "\\overrides\\" + root.toString().substring(index, root.toString().length()) +"-override.cfg", "");
                        //System.out.println(dest.toString());
                        Path src = Paths.get(root + "\\" + file.getName(), "");
                        
                    
                        /*System.out.println(root.toString() );
                        
                        System.out.println(root.toString().substring(index, root.toString().length()));*/

                        Files.copy(src, dest, REPLACE_EXISTING);
                        
                        overrideCounter ++;
                    }catch (Exception ex) {/*System.out.println(ex);*/}

                }
                if(file.getName().contains("primary#acl"))
                {
                    try{
                        /*//System.out.println(root.toString().replace(basicPath,"").substring(1));
                        int index = root.toString().lastIndexOf("\\")+1;
                        Path dest = Paths.get(newFile + "\\acls\\" + root.toString().substring(index, root.toString().length()) +"-acl.cfg", "");
                        //System.out.println(dest.toString());
                        Path src = Paths.get(root + "\\" + file.getName(), "");
                        
                    
                        //System.out.println(root.toString() );
                        
                        //System.out.println(root.toString().substring(index, root.toString().length()));

                        Files.copy(src, dest, REPLACE_EXISTING);
                        
                        aclCounter ++;
                        */

                        //!!!--------------------above is working
                        Scanner scanner1 = new Scanner(file);
                        String aclRouter ="";
                        String aclIP="";
                        while (scanner1.hasNextLine()){
                            String lineFromFile = scanner1.nextLine();
                            if(lineFromFile.contains("Router Sysname:")){
                                aclRouter = lineFromFile.substring(18);                                    
                            }
                            if(lineFromFile.contains("Router IP:")){
                                aclIP = lineFromFile.substring(13);
                                break;                                    
                            }
                        }
                        //System.out.println("acl: "+ aclRouter + "_" + aclIP +"\n");

                        Path dest = Paths.get(newFile + "\\acls\\" + aclRouter + "_" + aclIP + "_acl.cfg", "");
                        Path src = Paths.get(root + "\\" + file.getName(), "");
                        

                        Files.copy(src, dest, REPLACE_EXISTING);
                        
                        aclCounter ++;
                        
                    }catch (Exception ex) {/*System.out.println(ex);*/}

                }
            }
            else if(file.isDirectory())
            {
                find_files(file, newFile, basicPath);
            }
        }
    }

}

