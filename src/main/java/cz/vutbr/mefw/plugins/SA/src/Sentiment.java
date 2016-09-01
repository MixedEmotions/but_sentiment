package cz.vutbr.mefw.plugins.SA.src;


import java.util.*;

/**
 * The class represents a sentiment. A sentiment may consist of multiple segments. Besides text, also polarity, topic and a list of opinions can be assigned to a sentiment.
 */
public class Sentiment
{
  private String topic = null;
  private String polarity = null;
  private String text = null;
  private List<Opinion> opinions = new ArrayList<Opinion>();
  private List<Segment> segments = new ArrayList<Segment>();
  public String rid;
  public boolean outOfScope;

  /**
   * Creates single segment sentiment.
   *
   * @param text Text of the sentiment
   * @param polarity Polarity of the sentiment
   * @param topic Topic of the sentiment
   */
  public Sentiment(String text, String polarity, String topic)
  {
    this.text = text;
    this.polarity = polarity;
    this.topic = topic;
    segments.add(new Segment(text, polarity, topic));
  }

  /**
   * Creates single segment sentiment.
   *
   * @param text Text of the sentiment
   * @param polarity Polarity of the sentiment
   */
  public Sentiment(String text, String polarity)
  {
    this.text = text;
    this.polarity = polarity;
    segments.add(new Segment(text, polarity));
  }

  /**
   * Creates single segment sentiment.
   *
   * @param text Text of the sentiment
   * @param opinions Opinions of the sentiment
   */
  public Sentiment(String text, List<Opinion> opinions)
  {
    this.text = text;
    this.opinions = opinions;
    segments.add(new Segment(text));
  }

  /**
   * Creates single segment sentiment.
   *
   * @param text Text of the sentiment
   */
  public Sentiment(String text)
  {
    this.text = text;
    segments.add(new Segment(text));
  }

  /**
   * Returns polarity of the sentiment.
   *
   * @return The polarity
   */
  public String getPolarity()
  {
    return polarity;
  }

  /**
   * Sets polarity of the sentiment.
   *
   * @param polarity The polarity
   */
  public void setPolarity(String polarity)
  {
    this.polarity = polarity;
  }

  /**
   * Returns text of the sentiment.
   *
   * @return The text
   */
  public String getText()
  {
    return text;
  }

  /**
   * Returns aspect categories of the sentiment.
   *
   * @return The categories
   */
  public Set<String> getCategories()
  {
    Set<String> categories = new HashSet<String>();
    for(Opinion op : opinions)
      categories.add(op.category);
    return categories;
  }

  /**
   * Returns segments of the sentiment.
   *
   * @return The segments
   */
  public List<Segment> getSegments()
  {
    return segments;
  }

  /**
   * Returns opinions of the sentiment.
   *
   * @return The opinions
   */
  public List<Opinion> getOpinions()
  {
    return opinions;
  }

  /**
   * Adds an opinion to the sentiment.
   *
   * @param opinion The opinion to add
   */
  public void addOpinion(Opinion opinion)
  {
    opinions.add(opinion);
  }
}
