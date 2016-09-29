/* File: SA_lingpipe.java
 * Description: Sentiment analysis module for MixedEmotions framework using lingpipe
 * Author: xorman00 <xorman00@stud.fit.vutbr.cz>
 */

package cz.vutbr.mefw.plugins;
// IMPORTS ////////////////////////////////////////////////
import java.io.*;
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
import cz.vutbr.mefw.*;
import cz.vutbr.mefw.plugins.SA.src.*;
import cz.vutbr.mefw.plugins.SA.loaders.*;
// Lingpipe
import com.aliasi.classify.*;
import com.aliasi.lm.*;

// MAIN CLASS ////////////////////////////////////////////////
public class SA_lingpipe extends ProcessorAdapter
{
	// Sentiment analysis is based on n-grams
	// https://en.wikipedia.org/wiki/N-gram
	private static final int ngram = 10;
	// Lingpipe tool
	private DynamicLMClassifier<NGramBoundaryLM> lingPipe = null;
	// Name of this processor, for Loader
	protected String processor_name = "lingpipe";

	public String path_to_resources="./resources/me_sentiment/";
	//public String path_to_resources = "./";

	public SA_lingpipe()
	{
		;
	}

	public SA_lingpipe(String new_procesor_name)
	{
		this.processor_name=new_procesor_name;
	}

	public SA_lingpipe(Config config)
	{
		this.path_to_resources=config.get("resoucesPath")+"/me_sentiment/";
	}

	public SA_lingpipe(Config config,String new_procesor_name)
	{
		this.path_to_resources=config.get("resoucesPath")+"/me_sentiment/";
		this.processor_name=new_procesor_name;
	}

	// Returns if tool is ready for usage
	public Boolean is_ready()
	{
		if (lingPipe == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	// Training of lingpipe tool
	// Adapter info: if processor need any external data in memory, they should be loaded here
	public void load()
	{
		Dataset training_data = new Dataset();
		// Loading for training data
		try
		{
			Loader temp = new Loader(processor_name,this.path_to_resources);
			temp.load(training_data);
		}
		catch ( IllegalStateException e0)
		{
			System.err.println("No training data was obtained. Bad path to module resources could be cause of this.");
            throw new IllegalStateException("No training data.");
		}
        catch ( IOException e1)
        {
            ;
        }
		catch(Exception e)
		{
			e.printStackTrace();
		}

		
		// Training
		if ( training_data.size() != 0 )
		{
			this.train(training_data);
		}
		else
		{
            System.err.println("No training data was obtained. Bad path to module resources could be cause of this.");
			throw new IllegalStateException("No training data.");
		}	
	}

	// Training of lingpipe tool
	// load used by test tools
	public void load(Dataset training_data)
	{	
		// Training
		if ( training_data.size() != 0 )
		{
			this.train(training_data);
		}
		else
		{
            System.err.println("No training data was obtained. Bad path to module resources could be cause of this.");
			throw new IllegalStateException("No training data.");
		}	
	}

	// Unloading training datasets
	// Adapter info: drop all external data in memory
	public void unload()
	{
		this.lingPipe = null;
	}
	
	// Adapter info: call main processing method of the external class/tool
	public String process(String data)
	{
		if(this.lingPipe == null)
		{
			throw new IllegalStateException("Mudule needs to be trained first. Call load() before using.");
		}
		else
		{
			return (lingPipe.classify(data.toLowerCase())).bestCategory();
		}
	}

	// Training
	private void train(Dataset training_data)
	{
		Set<String> polarities = training_data.getPolarities();
		String[] cats = polarities.toArray(new String[polarities.size()]);

		this.lingPipe = DynamicLMClassifier.createNGramBoundary(cats, ngram);

		Map<String, Classification> pol2class = new HashMap<String, Classification>();
		for(String pol : cats)
			pol2class.put(pol, new Classification(pol));

		for(int i = 0; i < training_data.size(); i++)
		{
			Classified<CharSequence> classified = new Classified<CharSequence>(training_data.get(i).getText(), pol2class.get(training_data.get(i).getPolarity()));
			this.lingPipe.handle(classified);
		}		
	}

	// Used for testing precision
	// DANGER: tool will be retrained
	public void test()
	{
		this.test(0.2);
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
					if (result == "positive" )
					{
						positive_right++;
					}
				}
				else if( test_data.get(i).getPolarity() == "neutral" )
				{
					neutral_all++;
					if (result == "neutral" )
					{
						neutral_right++;
					}
				}
				else if( test_data.get(i).getPolarity() == "negative" )
				{
					negative_all++;
					if (result == "negative" )
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
			throw new IllegalStateException("LingPipe model needs to be trained first");
		}
		
		System.out.println(String.format("TEST RESULTS for tool:%s",processor_name));
		System.out.println(String.format("POSITIVE: %d right from %d , what is %2f ",positive_right,positive_all,(float)positive_right/positive_all));
		System.out.println(String.format("NEUTRAL: %d right from %d , what is %2f ",neutral_right,neutral_all,(float)neutral_right/neutral_all));
		System.out.println(String.format("NEGATIVE: %d right from %d , what is %2f ",negative_right,negative_all,(float)negative_right/negative_all));
		System.out.println(String.format("ALL: %d right from %d , what is %2f ",positive_right+neutral_right+negative_right,positive_all+neutral_all+negative_all,((float)(positive_right+neutral_right+negative_right))/(positive_all+neutral_all+negative_all)));
	}

	// DANGER: tool will be retrained
	public void test(double test_fraction)
	{
		Dataset data = new Dataset();
		// Loading for training data
		try
		{
			Loader temp = new Loader(processor_name,path_to_resources+"config.ini" );
			temp.load(data);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
		this.test(test_fraction,data);
	}

	// DANGER: tool will be retrained
	public void test(double test_fraction,Dataset data)
	{
		data.shuffle();
		Dataset[] splited_data=data.split(test_fraction);
		// [0] = test_data [1] = training data
		this.test(splited_data[0],splited_data[1]);
	}

	// DANGER: tool will be retrained
	public void test(Dataset test_data,Dataset training_data)
	{
		// We train out tool
		this.train(training_data);
		// Than we use it for testing
		this.test(test_data);
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
		SA_lingpipe tool = new SA_lingpipe();
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