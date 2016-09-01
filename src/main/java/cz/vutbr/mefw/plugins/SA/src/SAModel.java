package cz.vutbr.mefw.plugins.SA.src;

import java.util.*;
import java.io.*;
import java.util.regex.*;
import edu.stanford.nlp.sentiment.*;
import edu.stanford.nlp.pipeline.*;
import com.aliasi.classify.*;
import com.aliasi.lm.*;

/**
 * The class models sentiment analysis.
 */
public class SAModel
{
  private static final Set<String> validTools;
  private static final Map<String, Double> polarityNumber;
  private String tool;
  private DynamicLMClassifier<NGramBoundaryLM> lingPipeClassifier = null;
  private int lingPipeNgrams = 10;
  private Properties stanfordProperties;
  private StanfordCoreNLP stanfordPipeline = null;
  private SentimentTraining stanfordST = null;

  static
  {
    validTools = new HashSet<String>();
    validTools.add("stanford");
    validTools.add("lingpipe");

    polarityNumber = new HashMap<String, Double>();
    polarityNumber.put("very negative", -5.0);
    polarityNumber.put("negative", -1.0);
    polarityNumber.put("neutral", 0.0);
    polarityNumber.put("positive", 1.0);
    polarityNumber.put("very positive", 5.0);
  }

  /**
   * @param tool Name of a tool to use to perform sentiment analysis. Posible values: 'lingpipe' (default) and 'stanford'.
   */
  public SAModel(String tool)
  {
    System.out.println("SE");
    if(!validTools.contains(tool))
      throw new Error("Invalid tool name");
    this.tool = tool;
  }

  /**
   * Sets n of ngrams feature regarding bag-of-words model that LingPipe uses. If not set, n=10 is used as default.
   *
   * @param ngrams The n value
   */
  public void setNgrams(int ngrams)
  {
    this.lingPipeNgrams = ngrams;
  }

  /**
   * Trains the model using given data set.
   *
   * @param set The set to train
   */
  public void train(Dataset set)
  {
    if(!tool.equals("lingpipe"))
      throw new IllegalStateException("Cannot train using "+tool+" tool");

    Set<String> polarities = set.getPolarities();
    String[] cats = polarities.toArray(new String[polarities.size()]);

    lingPipeClassifier = DynamicLMClassifier.createNGramBoundary(cats, lingPipeNgrams);

    Map<String, Classification> pol2class = new HashMap<String, Classification>();
    for(String pol : cats)
      pol2class.put(pol, new Classification(pol));

    for(int i = 0; i < set.size(); i++)
    {
      Classified<CharSequence> classified = new Classified<CharSequence>(cleanText(set.get(i).getText()), pol2class.get(set.get(i).getPolarity()));
      lingPipeClassifier.handle(classified);
    }
  }

  private static final String from = "áäčďéěíľĺňóôřšśťúůýž";
  private static final String  to  = "aacdeeillnoorsstuuyz";

  /**
   * Cleans a text by removing diacritics.
   *
   * @param text The text to be cleaned
   * @return Cleaned text
   */
  private String cleanText(String text)
  {
    text = text.toLowerCase();
    for(int i = 0; i < text.length(); i++)
    {
      char c = text.charAt(i);
      if(c < 'a' || c > 'z')
      {
        int index = from.indexOf(c);
        if(index != -1)
          text = text.replace(c, to.charAt(index));
        else if(text.codePointAt(i) > 153)
          System.err.println("Unknown character ("+text.codePointAt(i)+"): "+c);
      }
    }
    return text.trim();
  }

  /**
   * Returns polarity predictions of all sentiments from given data set. Only 'positive' and 'negative' values will be considered.
   *
   * @param set The set whose sentiments are about to be predicted
   * @return The predictions as a list respecting the order of the input sentiments
   */
  public List<String> predict(Dataset set)
  {
    return predict(set, false);
  }

  /**
   * Returns polarity predictions of all sentiments from given data set.
   *
   * @param set The set whose sentiments are about to be predicted
   * @param considerNeutral When true, the 'neutral' will be considered as a valid value in addition to 'positive' and 'negative' values.
   * @return The predictions as a list respecting the order of the input sentiments
   */
  public List<String> predict(Dataset set, boolean considerNeutral)
  {
    List<String> predictions = new ArrayList<String>();
    for(int i = 0; i < set.size(); i++)
      predictions.add(predict(set.get(i), considerNeutral));
    return predictions;
  }

  /**
   * Returns polarity prediction of given sentiment. Only 'positive' and 'negative' values will be considered.
   *
   * @param sentiment The sentiment
   * @return The predicted polarity
   */
  public String predict(Sentiment sentiment)
  {
    return predict(sentiment, false);
  }

  /**
   * Returns polarity prediction of given sentiment.
   *
   * @param sentiment The sentiment
   * @param considerNeutral When true, the 'neutral' will be considered as a valid value in addition to 'positive' and 'negative' values.
   * @return The predicted polarity
   */
  public String predict(Sentiment sentiment, boolean considerNeutral)
  {
    if(tool.equals("lingpipe"))
    {
      if(lingPipeClassifier == null)
        throw new IllegalStateException("LingPipe model needs to be trained first");

      Classification cl = lingPipeClassifier.classify(cleanText(sentiment.getText()));
      return cl.bestCategory();
    }
    else if(tool.equals("stanford"))
    {
      // See http://stackoverflow.com/questions/23493343/how-to-get-the-sentimental-statement-like-positive-or-negative-using-stanford-nl
      double neutralThreshold = considerNeutral ? 0.4 : 0.0;

      List<String> polarities = this.stanfordAnalyze(cleanText(sentiment.getText()));

      String polarity;

      Double sum = new Double(0);
      int count = 0;
      for(String p : polarities)
      {
        Double num = SAModel.polarityNumber.get(p.toLowerCase());
        if(num == null)
          throw new RuntimeException("Unknown polarity: "+p);
        else if(num != 0 || true)
        {
          count++;
          sum += num;
        }
      }

      sum /= count;
      if(sum < -neutralThreshold)
        polarity = "negative";
      else if(sum <= neutralThreshold)
        polarity = "neutral";
      else
        polarity = "positive";
      return polarity;
    }
    else
      throw new IllegalStateException("Unsupported tool");
  }

  /**
   * Measures accuracy of the model using given test set. In case of LingPipe model, training the model prior testing is necessary.
   *
   * @param set The test data set
   * @return Accuracy of the model
   */
  public double test(Dataset set)
  {
    double accuracy;
    if(tool.equals("lingpipe"))
    {
      if(lingPipeClassifier == null)
        throw new IllegalStateException("LingPipe model needs to be trained first");
      int hits = 0;
      int misses = 0;

      for(int i = 0; i < set.size(); i++)
      {
        Classification cl = lingPipeClassifier.classify(cleanText(set.get(i).getText()));
        String polarity = cl.bestCategory();
        if(polarity.equals(set.get(i).getPolarity()))
          hits++;
        else
          misses++;
      }

      accuracy = ((double)(100*hits))/(hits+misses);
    }

    else if(tool.equals("stanford"))
    {
      boolean considerNeutral = set.getPolarities().contains("neutral");

      int hits = 0;
      int misses = 0;

      for(int i = 0; i < set.size(); i++)
      {
        Sentiment sentiment = set.get(i);

        String polarityMined = predict(sentiment, considerNeutral);

        if(polarityMined.equals(sentiment.getPolarity()))
          hits++;
        else
          misses++;
      }

      accuracy = ((float)(100*hits))/(hits+misses);
    }
    else
      throw new IllegalStateException("Unsupported tool");
    return accuracy;
  }

  /**
   * Returns Stanford polarities of each sentence from given text.
   *
   * @param text The text
   * @return A list of polarities
   */
  private List<String> stanfordAnalyze(String text)
  {
    if(stanfordST == null)
    {
      stanfordST = new SentimentTraining();
      stanfordProperties = new Properties();
      stanfordProperties.put("sentiment.model", "libs/stanford-corenlp-full-2015-04-20/edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
      stanfordProperties.setProperty("annotators", "tokenize, ssplit, parse, pos, sentiment");
      stanfordPipeline = new StanfordCoreNLP(stanfordProperties);
    }

    List<String> polarities = new ArrayList<String>();

    Annotation document = new Annotation(text);
    stanfordPipeline.annotate(document);

    StringWriter writer = new StringWriter();
    stanfordPipeline.prettyPrint(document, new PrintWriter(writer));
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

  /**
   * Performs k-fold cross validation test using given data set. The test may be repeated multiple times and mean precision is returned.
   *
   * @param set The set
   * @param k The k value, the set will be partitioned to k pieces
   * @param interations Number of times the test will be repeated.
   * @return Precision of the model using given attributes
   */
  public double kfold(Dataset set, int k, int iterations)
  {
    if(!this.tool.equals("lingpipe"))
      throw new IllegalStateException("Cannot run k-fold cross validation test using "+this.tool+" tool");

    if(k == 0)
      k = set.size();

    set = set.copy();
    double scoreSum = 0.0;

    for(int i = 0; i < iterations; i++)
    {
      set.shuffle();
      Dataset[] sets = set.splitn(k);
      for(int s = 0; s < k; s++)
      {
        Dataset trainSet = new Dataset();
        for(int j = 0; j < k; j++)
          if(j != s)
            trainSet.add(sets[j]);
        SAModel e = new SAModel(this.tool);
        e.setNgrams(lingPipeNgrams);
        e.train(trainSet);
        scoreSum += e.test(sets[s]);
      }
    }

    return scoreSum/(k*iterations);
  }

  /**
   * Performs a precision test. Given data set will be randomly divided into train and test subsets. The test may be repeated multiple times and mean precision is returned.
   *
   * @param set The data set
   * @param trainProportion A value between 0.0 and 1.0. The given set will be divided respecting this proportion.
   * @param interations Number of times the test will be repeated.
   * @return Precision of the model using given attributes
   */
  public double randomTest(Dataset set, double trainProportion, int iterations)
  {
    double scoreSum = 0.0;
    set = set.copy();

    for(int i = 0; i < iterations; i++)
    {
      set.shuffle();
      if(this.tool.equals("lingpipe"))
      {
        Dataset[] sets = set.split(trainProportion);
        SAModel e = new SAModel(this.tool);
        e.setNgrams(lingPipeNgrams);
        e.train(sets[0]);
        scoreSum += e.test(sets[1]);
      }
      else
      {
        SAModel e = new SAModel(this.tool);
        scoreSum += e.test(set);
      }
    }

    return scoreSum/iterations;
  }
}
