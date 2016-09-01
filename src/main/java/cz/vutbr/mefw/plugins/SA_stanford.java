/* File: SA_stanford.java
 * Description: Sentiment analysis module for MixedEmotions framework using StanfordCoreNLP
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
import java.util.regex.*;
import java.util.*;
// Own custom classes
// Loaders of datasets
import cz.vutbr.mefw.*;
import cz.vutbr.mefw.plugins.SA.src.*;
import cz.vutbr.mefw.plugins.SA.loaders.*;
// Stanford
import edu.stanford.nlp.sentiment.*;
import edu.stanford.nlp.pipeline.*;
import org.apache.log4j.*;
import static sun.misc.Version.println;

// MAIN CLASS ////////////////////////////////////////////////
public class SA_stanford extends ProcessorAdapter
{
	private Properties stanfordProperties = null;
	private StanfordCoreNLP stanfordPipeline = null;
	private SentimentTraining stanfordST = null;
	private String processor_name = "stanford";

	public String path_to_resources = "./resources/but_sentiment/";
	//public String path_to_resources = "./";

	public static double polarity_threshold_down = -0.4; 
	public static double polarity_threshold_up = 0.4;
	public static HashMap<String, Double> polarityNumber = new HashMap<String, Double>();

	static
	{
		polarityNumber.put("very negative", -5.0);
		polarityNumber.put("negative", -1.0);
		polarityNumber.put("neutral", 0.0);
		polarityNumber.put("positive", 1.0);
		polarityNumber.put("very positive", 5.0);		
	}

	public SA_stanford()
	{
		;
	}

	public SA_stanford(String new_procesor_name)
	{
		this.processor_name=new_procesor_name;
	}

	public SA_stanford(Config config)
	{
		this.path_to_resources=config.get("resoucesPath")+"/but_sentiment/";
	}

	public SA_stanford(Config config,String new_procesor_name)
	{
		this.path_to_resources=config.get("resoucesPath")+"/but_sentiment/";
		this.processor_name=new_procesor_name;
	}

	// Returns if tool is ready for usage
	public Boolean is_ready()
	{
		if (this.stanfordPipeline == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}


	// Adapter info: if processor need any external data in memory, they should be loaded here
	public void load()
	{
		BasicConfigurator.configure();
		this.stanfordST = new SentimentTraining();
		this.stanfordProperties = new Properties();
		this.stanfordProperties.put("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz" );
		this.stanfordProperties.setProperty("annotators", "tokenize, ssplit, parse, pos, sentiment");
		this.stanfordPipeline = new StanfordCoreNLP(stanfordProperties);
	}

	// Unloading training datasets
	// Adapter info: drop all external data in memory
	public void unload()
	{
		stanfordProperties = null;
		stanfordPipeline = null;
		stanfordST = null;
	}
	
	// Adapter info: call main processing method of the external class/tool
	public String process(String data)
	{
		if ( this.is_ready() )
		{
			List<String> polarities = this.stanfordAnalyze(data.toLowerCase());

			double sum = 0;
			int count = 0;
			for(String p : polarities)
			{
				double num = SA_stanford.polarityNumber.get(p.toLowerCase());
				if(num != 0 || true)
				{
					count++;
					sum += num;
				}
			}
				
			if ( sum/count < SA_stanford.polarity_threshold_down )
			{
				return "negative";
			}
			else if ( sum/count > SA_stanford.polarity_threshold_up ) 
			{
				return "positive";
			}
			else
			{
				return "neutral";
			}			
		}
		else
		{
			throw new IllegalStateException("StanfordNLP needs to load tree before using, use public void load() before using this tool.");
		}
	}

	private List<String> stanfordAnalyze(String text)
	{
		List<String> polarities = new ArrayList<String>();

		Annotation document = new Annotation(text);
		this.stanfordPipeline.annotate(document);

		StringWriter writer = new StringWriter();
		this.stanfordPipeline.prettyPrint(document, new PrintWriter(writer));
		String output = writer.toString();

		Pattern p = Pattern.compile("Sentence #[0-9]+ \\([^,]+, sentiment: ([^\\)]+)\\)");

		Matcher m = p.matcher(output);

		while(m.find())
		{
			String polarity = m.group(1);
			polarities.add(polarity);
		}

		return polarities;
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
			Loader temp = new Loader("stanford",path_to_resources+"config.ini" );
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

	public static void main(String[] args)
	{
		SA_stanford tool = new SA_stanford();
		System.out.println("LOAD");
		try
		{
			tool.test();
			tool.unload();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("THANKS");
	}
}