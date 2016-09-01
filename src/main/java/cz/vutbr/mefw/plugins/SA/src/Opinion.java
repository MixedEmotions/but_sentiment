package cz.vutbr.mefw.plugins.SA.src;

/**
 * The class represents opinion of a sentiment.
 */
public class Opinion
{
  public String category = null;
  public String polarity = null;
  public int targetFrom = -1;
  public int targetTo = -1;

  /**
   * @param category Aspect ategory of the opinion
   * @param polarity Polarity of the opinion
   * @param targetFrom Start index of the opinion's target
   * @param targetTo End index of the opinion's target
   */
  public Opinion(String category, String polarity, int targetFrom, int targetTo)
  {
    this.category = category;
    this.polarity = polarity;
    this.targetFrom = targetFrom;
    this.targetTo = targetTo;
  }

  /**
   * @param category Aspect ategory of the opinion
   * @param polarity Polarity of the opinion
   */
  public Opinion(String category, String polarity)
  {
    this.category = category;
    this.polarity = polarity;
  }
}
