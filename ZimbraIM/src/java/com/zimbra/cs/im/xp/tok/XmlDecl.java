package com.zimbra.cs.im.xp.tok;

/**
 * An XML declaration.
 * @version $Revision: 1.3 $ $Date: 1998/02/17 04:24:35 $
 */
public class XmlDecl extends TextDecl {
  private boolean standalone;

  /**
   * Returns true if the XML declaration specified
   * <code>standalone="yes"</code>.
   */
  public boolean isStandalone() {
    return standalone;
  }
  
  /**
   * Creates an <code>XMLDecl</code> from the specified byte subarray.
   * The specified encoding is used to convert bytes to characters.
   * The byte subarray should be a <code>TOK_XML_DECL</code> token
   * returned from Encoding.tokenizeProlog or Encoding.tokenizeContent,
   * starting with <code>&lt;?</code> and ending with <code>?&gt;</code>.
   * @exception InvalidTokenException if the specified byte subarray
   * is not a legal XML declaration.
   */
  public XmlDecl(Encoding enc, byte[] buf, int off, int end)
       throws InvalidTokenException {
    standalone = init(true, enc, buf, off, end);
  }
      
}
