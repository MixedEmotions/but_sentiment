package cz.vutbr.mefw;

import org.ini4j.Ini;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;
import java.io.IOException;

/**
 * Config crate class. It loads, holds and provide access to ini configuration file.
 */
public class Config {

    Ini ini;
    String resourcesFolder = "resources";

    /**
     * @param
     * @throww
     * @return
     */
    public Config()  {

    }

    public void load(String filepath) throws IOException{
        File f = new File(filepath);
        if(filepath != null && f.exists() && !f.isDirectory()){
            this.ini = new Ini();
            this.ini.load(f);
            //System.out.println(this.ini);
            //System.out.println(this.get("resoucesPath"));
            //System.out.println(this.get("pluginsPath"));
            //System.out.println(this.get("projectPath"));
        }else{
            System.out.println("generating default");
            this.loadDefault();
        }

    }

    public void load(File file) throws IOException{
        if(file.exists() && !file.isDirectory()){
            this.ini.load(file);
        }else{
            this.loadDefault();
        }

    }

    private void loadDefault(){
        this.ini.put("default","resoucesPath","./resources");
        this.ini.put("default","pluginsPath","./plugins");
        this.ini.put("default","projectPath",Paths.get("").toAbsolutePath().toString());
    }

    /** Getter for ini key from default section
     * @param key is the key string from the ini file
     * @return the value of the key
     */
    public String get(String key){
        return this.get("default", key);
    }

    /**
     * @param sectionKey specify section name in the ini file
     * @param key is the key string in the selected section
     * @return
     */
    // xorman00: exception
    // TODO do it better
    public String get(String sectionKey, String key){
        Ini.Section section = ini.get(sectionKey);
        return section.get(key);
    }

}
