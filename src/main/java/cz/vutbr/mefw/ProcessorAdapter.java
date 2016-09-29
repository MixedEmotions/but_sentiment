package cz.vutbr.mefw;

public abstract class ProcessorAdapter {

    //TODO:PAss config to constructor with paths to resources

    Config config;

    public ProcessorAdapter(Config config){
        this.config = config;
    }

    // xorman00: because java have to be retarded
    public ProcessorAdapter(){
        ;
    }
    public ProcessorAdapter(String new_procesor_name){
        this.config = config;
    }
    public ProcessorAdapter(Config config,String new_procesor_name){
        this.config = config;
    }


    abstract public void load();

    abstract public String process(String data);

    //abstract public String process(String data);

}
