/* File: SA_NLTK.java
 * Description: Sentiment analysis module for MixedEmotions framework using python script (NLTK)
 * Author: xorman00 <xorman00@stud.fit.vutbr.cz>
 */

package cz.vutbr.mefw.plugins;
// IMPORTS ////////////////////////////////////////////////
import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.lang.Exception;
// Config
import java.util.Properties;
// Logging
import java.util.logging.Logger;
import java.util.regex.*;
import java.util.*;
// Own custom classes
// Loaders of datasets
import cz.vutbr.mefw.Config;
import cz.vutbr.mefw.ProcessorAdapter;
import cz.vutbr.mefw.plugins.SA.loaders.*;
import cz.vutbr.mefw.plugins.SA.src.Dataset;

// MAIN CLASS ////////////////////////////////////////////////
public class SA_VADER extends ProcessorAdapter
{
	protected String processor_name = "VADER";
	protected long processor_pid = 0;
	private BufferedWriter to_python = null;
	private BufferedReader from_python = null; 
	private Process python_side = null;

	public String path_to_resources="./resources/me_sentiment/";

	public SA_VADER()
	{
		;
	}

	public SA_VADER(String new_procesor_name)
	{
		this.processor_name=new_procesor_name;
	}

	public SA_VADER(Config config)
	{
		this.path_to_resources=config.get("resoucesPath")+"/me_sentiment/";
	}

	public SA_VADER(Config config,String new_procesor_name)
	{
		this.path_to_resources=config.get("resoucesPath")+"/me_sentiment/";
		this.processor_name=new_procesor_name;
	}

	// Returns if tool is ready for usage
	public Boolean is_ready()
	{
		if (this.python_side == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}


	// Starting Python part of process
	// Adapter info: if processor need any external data in memory, they should be loaded here
	public void load()
	{
		try
		{
			// It is not desired to have more than 1 python process
			if( this.is_ready() && this.processor_pid == this.getPID() )
			{
				;
			}
			else
			{
				this.processor_pid = this.getPID();
				this.python_side = Runtime.getRuntime().exec("python3 "+this.path_to_resources+"python/me_sentiment_vader.py");

				this.from_python =  new BufferedReader(new InputStreamReader(this.python_side.getInputStream()));
				this.to_python = new BufferedWriter( new OutputStreamWriter(this.python_side.getOutputStream()));
			}			
		}
		catch(IOException e)
		{
			System.err.println("Unable to start Python part of module");
			throw new IllegalStateException("Unable to start Python part of module.");
		}	

	}

	// Unloading training datasets
	// Adapter info: drop all external data in memory
	public void unload()
	{
		// Python part is running
		try
		{
			if(this.is_ready() && this.processor_pid == this.getPID() )
			{
				this.to_python.write("<<exit>>\n");
				this.to_python.close();
				this.from_python = null;
				this.from_python.close();
				this.from_python = null;
				this.python_side = null;
			}

		}
		catch(IOException e0)
		{
			; //e.printStackTrace();
		}
		catch(NullPointerException e1)
		{
			;
		}
		finally
		{
			this.from_python = null;
			this.from_python = null;
			this.python_side = null;
		}
	}

	// Gets PID of process
	// We use it to ensure that one Python have one Java
	private long getPID()
	{
		String name = ManagementFactory.getRuntimeMXBean().getName();
		int index = name.indexOf('@');
		return Long.parseLong(name.substring(0,index));	
	}
	
	// Adapter info: call main processing method of the external class/tool
	public String process(String data)
	{
		try
		{
			if( this.is_ready() )
			{
				// This condition ensures, that python part of this tool is used only by one java process
				// This does not work if we use threads
				if (this.processor_pid != this.getPID())
				{
					this.load();
				}

				// We send sentence for processing
				this.to_python.write(data, 0, data.length());
				this.to_python.newLine();
				this.to_python.flush();

				// We wait for answer
				// TODO timeout
				String line = this.from_python.readLine();

				return line; 
			}
			else
			{
				throw new IllegalStateException("VADER has not been loaded. Use public void load() before processing.");
			}
		}
		catch(IOException e)
		{
			unload();
		}
		return "error";
	}

	// Used for testing precision
	// DANGER: tool will be retrained
	public void test()
	{
		this.test(0.2);
	}

	// DANGER: tool will be retrained
	public void test(double test_fraction)
	{
		Dataset data = new Dataset();
		// Loading for training data
		try
		{
			Loader temp = new Loader("lingpipe",path_to_resources );
			temp.load(data);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
		// splited_data[0] == data for testing
		data.shuffle();
		Dataset[] splited_data = data.split(test_fraction);
		this.load();
		this.test(splited_data[0]);
	}


	// This one will not retrain tool
	public void test(Dataset test_data)
	{
		int positive_all = 0;
		int neutral_all = 0;
		int negative_all = 0;
		int positive_right = 0;
		int neutral_right = 0;
		int negative_right = 0;

		test_data.shuffle();

		if ( this.is_ready() )
		{
			for ( int i = 0; i < test_data.size(); i++)
			{
				String result = this.process(test_data.get(i).getText() );
				if( test_data.get(i).getPolarity() == "positive" )
				{
					positive_all++;
					if (result.equals("positive") )
					{
						positive_right++;
					}
				}
				else if( test_data.get(i).getPolarity() == "neutral" )
				{
					neutral_all++;
					if (result.equals("neutral") )
					{
						neutral_right++;
					}
				}
				else if( test_data.get(i).getPolarity() == "negative" )
				{
					negative_all++;
					if (result.equals("negative") )
					{
						negative_right++;
					}
				}
				else
				{
					continue;
				}
			}
		}
		else
		{
			throw new IllegalStateException("Sometin wong");
		}
		
		System.out.println(String.format("TEST RESULTS for tool:%s",processor_name));
		System.out.println(String.format("POSITIVE: %d right from %d , what is %2f ",positive_right,positive_all,(float)positive_right/positive_all));
		System.out.println(String.format("NEUTRAL: %d right from %d , what is %2f ",neutral_right,neutral_all,(float)neutral_right/neutral_all));
		System.out.println(String.format("NEGATIVE: %d right from %d , what is %2f ",negative_right,negative_all,(float)negative_right/negative_all));
		System.out.println(String.format("ALL: %d right from %d , what is %2f ",positive_right+neutral_right+negative_right,positive_all+neutral_all+negative_all,((float)(positive_right+neutral_right+negative_right))/(positive_all+neutral_all+negative_all)));
	}

	// To be sure, that python side will be turned off
	protected void finalize()
	{
		// Python part is running
		try
		{
			if(this.is_ready() && this.processor_pid == this.getPID() )
			{
				this.to_python.write("<<exit>>\n");
				this.to_python.close();
				this.to_python = null;
				this.from_python.close();
				this.from_python = null;
				this.python_side = null;
			}

		}
		catch(IOException e)
		{
			;//e.printStackTrace();
		}
	}


	public void me_little_test() throws FileNotFoundException, IOException
	{
		int positive_all = 0;
		int neutral_all = 0;
		int negative_all = 0;
		int positive_right = 0;
		int positive_wrong = 0;
		int neutral_right = 0;
		int neutral_wrong = 0;
		int negative_right = 0;
		int negative_wrong = 0;


		if ( this.is_ready() )
		{
			try(BufferedReader br = new BufferedReader(new FileReader( "SemEval2015Task10BDevelopmentFull.tsv" ))) {
				for(String line; (line = br.readLine()) != null; ) {
					String[] parts = line.split("\t");
					if( parts.length == 4 )
					{
						if ( parts[3]!="Not Available" )
						{
							String result=this.process(parts[3]);
							if(parts[2].equals("positive"))
							{
								positive_all++;
								if ( result.equals("positive") )
									positive_right++;
								else if ( result.equals("neutral") )
									neutral_wrong++;
								else if ( result.equals("negative") )
									negative_wrong++;
							}
							else if ( parts[2].equals("neutral") || parts[2].equals("objective-OR-neutral") )
							{
								neutral_all++;
								if ( result.equals("neutral") )
									neutral_right++;
								else if ( result.equals("positive") )
									positive_wrong++;
								else if ( result.equals("negative") )
									negative_wrong++;
							}
							else if ( parts[2].equals("negative") )
							{
								negative_all++;
								if ( result.equals("negative") )
									negative_right++;
								else if ( result.equals("positive") )
									positive_wrong++;
								else if ( result.equals("neutral") )
									neutral_wrong++;
							}
						}
					}
				}
				// line is not visible here.
			}
		}
		else
		{
			throw new IllegalStateException("Sometin wong");
		}

		int all = positive_all+negative_all+neutral_all;
		int all_right_pos = positive_right+negative_right+neutral_right;
		int all_wrong_pos = positive_wrong+negative_wrong+neutral_wrong;

		float precision= positive_right/((float)positive_right+positive_wrong);
		float recall= positive_right/((float)positive_all);
		float F1= 2*((precision*recall)/(precision+recall));

		System.out.println(String.format("TEST RESULTS for tool:%s",processor_name));
		System.out.println(String.format("POSITIVE: all %d right_pos %d wrong_pos %d precision %4f recall %4f P1 %2f",positive_all,positive_right,positive_wrong,precision,recall,F1));

		precision = neutral_right/((float)neutral_right+neutral_wrong);
		recall = neutral_right/(float)neutral_all;
		F1= 2*((precision*recall)/(precision+recall));
		System.out.println(String.format("NEUTRAL: all %d right_pos %d wrong_pos %d precision %4f recall %4f P1 %2f",neutral_all,neutral_right,neutral_wrong,precision,recall,F1));

		precision = negative_right/((float)negative_right+negative_wrong);
		recall = negative_right/(float)negative_all;
		F1= 2*((precision*recall)/(precision+recall));
		System.out.println(String.format("NEGATIVE: all %d right_pos %d wrong_pos %d precision %4f recall %4f P1 %2f",negative_all,negative_right,negative_wrong,precision,recall,F1));

		System.out.println(String.format("ALL: all %d right_pos %d wrong_pos",all,all_right_pos,all_wrong_pos));
	}


	// Main for testing purposes
	public static void main(String[] args)
	{
		SA_VADER tool = new SA_VADER();
		System.out.println("LOAD");
		try
		{
			tool.load();
			tool.me_little_test();
			tool.unload();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("THANKS");
	}
}