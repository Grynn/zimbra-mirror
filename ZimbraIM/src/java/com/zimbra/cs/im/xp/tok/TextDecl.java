package com.zimbra.cs.im.xp.tok;

/**
 * An XML TextDecl.
 * @version $Revision: 1.4 $ $Date: 1998/12/30 02:42:01 $
 */
public class TextDecl {
  private String version;
  private String encoding;
  
  /**
   * Creates a <code>TextDecl</code> from the specified byte subarray.
   * The specified encoding is used to convert bytes to characters.
   * The byte subarray should be a <code>TOK_XML_DECL</code> token
   * returned from Encoding.tokenizeProlog or Encoding.tokenizeContent,
   * starting with <code>&lt;?</code> and ending with <code>?&gt;</code>.
   * @exception InvalidTokenException if the specified byte subarray
   * is not a legal XML TextDecl.
   */
  public TextDecl(Encoding enc, byte[] buf, int off, int end)
       throws InvalidTokenException {
    init(false, enc, buf, off, end);
  }

  /**
   * Return the encoding specified in the declaration, or null
   * if no encoding was specified.
   */
  public String getEncoding() {
    return encoding;
  }

  /**
   * Return the version specified in the declaration, or null
   * if no version was specified.
   */
  public String getVersion() {
    return version;
  }

  TextDecl() { }

  boolean init(boolean isXmlDecl, Encoding enc, byte[] buf, int off, int end) throws InvalidTokenException {
    // Skip <?xml
    off += enc.getMinBytesPerChar()*5;
    // Skip ?>
    end -= enc.getMinBytesPerChar()*2;
    ContentToken ct = new ContentToken();
    int firstErrorIndex = -1;
    try {
      parsePseudoAttributes(enc, buf, off, end, ct);
    }
    catch (InvalidTokenException e) {
      firstErrorIndex = e.getOffset();
    }
    int nAtts = ct.getAttributeSpecifiedCount();
    if (nAtts == 0) {
      if (firstErrorIndex == -1)
	firstErrorIndex = end;
      throw new InvalidTokenException(firstErrorIndex);
    }
    String[] names = new String[nAtts];
    String[] values = new String[nAtts];
    char[] cbuf = new char[32];
    for (int i = 0; i < nAtts; i++) {
      int s = ct.getAttributeNameStart(i);
      int e = ct.getAttributeNameEnd(i);
      if (e - s > cbuf.length)
	cbuf = new char[e - s];
      names[i] = new String(cbuf, 0, enc.convert(buf, s, e, cbuf, 0));
      s = ct.getAttributeValueStart(i);
      e = ct.getAttributeValueEnd(i);
      if (e - s > cbuf.length)
	cbuf = new char[e - s];
      values[i] = new String(cbuf, 0, enc.convert(buf, s, e, cbuf, 0));
    }
    int att = 0;
    if (names[0].equals("version")) {
      version = values[0];
      att++;
    }
    if ((att == 1 || !isXmlDecl)
	&& att < nAtts && names[att].equals("encoding")) {
      encoding = values[att];
      if (values[att].length() == 0
	  || !Character.isLetter(values[att].charAt(0))
	  || values[att].indexOf(':') >= 0) {
	int k = ct.getAttributeValueStart(att);
	if (firstErrorIndex == -1 || k < firstErrorIndex)
	  firstErrorIndex = k;
      }
      att++;
    }
    else if (!isXmlDecl)
      firstErrorIndex = 0;	// encoding is required in a TextDecl 
    boolean standalone = false;
    if (isXmlDecl && att > 0 && att < nAtts
	&& names[att].equals("standalone")) {
      if (values[att].equals("yes"))
	standalone = true;
      else if (!values[att].equals("no")) {
	int k = ct.getAttributeValueStart(att);
	if (firstErrorIndex == -1 || k < firstErrorIndex)
	  firstErrorIndex = k;
      }
      att++;
    }
    if (att < nAtts) {
      int k = ct.getAttributeNameStart(att);
      if (firstErrorIndex == -1 || k < firstErrorIndex)
	firstErrorIndex = k;
    }
    if (firstErrorIndex != -1)
      throw new InvalidTokenException(firstErrorIndex);
    return standalone;
  }

  private final
  void parsePseudoAttributes(Encoding enc, byte[] buf, int off, int end,
			     ContentToken ct) throws InvalidTokenException {
    final int minBPC = enc.getMinBytesPerChar();			       
    for (;;) {
      off = skipWS(enc, buf, off, end);
      if (off == end)
	break;
      int nameStart = off;
      int nameEnd;
    nameLoop:
      for (;;) {
	switch (enc.byteType(buf, off)) {
	case Encoding.BT_NMSTRT:
	  break;
	case Encoding.BT_EQUALS:
	  nameEnd = off;
	  break nameLoop;
	case Encoding.BT_S:
	case Encoding.BT_LF:
	case Encoding.BT_CR:
	  nameEnd = off;
	  off += minBPC;
	  off = skipWS(enc, buf, off, end);
	  if (off == end || !enc.charMatches(buf, off, '='))
	    throw new InvalidTokenException(off);
	  break nameLoop;
	default:
	  throw new InvalidTokenException(off);
	}
	off += minBPC;
	if (off == end)
	  throw new InvalidTokenException(off);
      }
      off += minBPC;
      off = skipWS(enc, buf, off, end);
      if (off == end || !(enc.charMatches(buf, off, '\'')
			  || enc.charMatches(buf, off, '"')))
	throw new InvalidTokenException(off);
      off += minBPC;
      int valueStart = off;
    valueLoop:
      for (;;) {
	if (off == end)
	  throw new InvalidTokenException(off);
	switch (enc.byteType(buf, off)) {
	case Encoding.BT_NMSTRT:
	case Encoding.BT_NAME:
	case Encoding.BT_MINUS:
	  if ((enc.byteToAscii(buf, off) & ~0x7F) != 0)
	    throw new InvalidTokenException(off);
	  off += minBPC;
	  break;
	case Encoding.BT_QUOT:
	case Encoding.BT_APOS:
	  if (enc.byteType(buf, off) != enc.byteType(buf, valueStart - minBPC))
	    throw new InvalidTokenException(off);
	  break valueLoop;
	default:
	  throw new InvalidTokenException(off);
	}
      }
      ct.appendAttribute(nameStart, nameEnd, valueStart, off, true);
      off += minBPC;
      if (off == end)
	break;
      switch (enc.byteType(buf, off)) {
      case Encoding.BT_S:
      case Encoding.BT_LF:
      case Encoding.BT_CR:
	off += minBPC;
	break;
      default:
	throw new InvalidTokenException(off);
      }
    }
  }

  private int skipWS(Encoding enc, byte[] buf, int off, int end) {
  loop:
    while (off != end) {
      switch (enc.byteType(buf, off)) {
      case Encoding.BT_S:
      case Encoding.BT_LF:
      case Encoding.BT_CR:
	off += enc.getMinBytesPerChar();
	break;
      default:
	break loop;
      }
    }
    return off;
  }
}
