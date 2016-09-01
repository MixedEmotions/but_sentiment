package cz.vutbr.mefw.plugins.SA.src;

import java.util.*;

/**
 * The class represents a sentiment segment. Besides text, also polarity, topic and a list of opinions can be assigned to a sentiment segment.
 */
public class Segment
{
  public String text = null;
  public String polarity = null;
  public String topic = null;
  public List<Opinion> opinions = new ArrayList<Opinion>();

  /**
   * @param text Text of the segment
   * @param polarity Polarity of the segment
   * @param topic Topic of the segment
   */
  public Segment(String text, String polarity, String topic)
  {
    this.text = text;
    this.polarity = polarity;
    this.topic = topic;
  }

  /**
   * @param text Text of the segment
   * @param polarity Polarity of the segment
   */
  public Segment(String text, String polarity)
  {
    this.text = text;
    this.polarity = polarity;
  }

  /**
   * @param text Text of the segment
   * @param opinions Opinions of the segment
   */
  public Segment(String text, List<Opinion> opinions)
  {
    this.text = text;
    this.opinions = opinions;
  }

  /**
   * @param text Text of the segment
   */
  public Segment(String text)
  {
    this.text = text;
  }
}
