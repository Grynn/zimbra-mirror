package com.zimbra.cs.im.xp.parse;

import java.util.ListResourceBundle;

/**
 * A <code>ResourceBundle</code> for the text of messages used
 * in <code>NotWellFormedException</code>.
 *
 * @see NotWellFormedException
 * @see Parser
 * @version $Revision: 1.10 $ $Date: 1998/05/14 02:04:05 $
 */
public class Messages extends ListResourceBundle {
  protected Object[][] getContents() {
    return contents;
  }
  private static final Object[][] contents = {
    // Arguments: description, location, line number, column number, byte index
    // Use ,0 in the number format to avoid getting , as thousands separator.
    { MessageId.MESSAGE_FORMAT,
      "{1}:{2,number,0}:{3,number,0}: {0}" },
    { MessageId.PUBID_CHAR,
      "character not allowed in public identifier" },
    { MessageId.ELEMENT_AFTER_DOCUMENT_ELEMENT,
      "element after document element" },
    { MessageId.BAD_INITIAL_BYTES,
      "impossible initial byte sequence" },
    { MessageId.BAD_DECL_ENCODING,
      "incorrect encoding specified in XML declaration" },
    { MessageId.INVALID_XML_DECLARATION,
      "invalid XML declaration" },
    { MessageId.IGNORE_SECT_CHAR,
      "invalid character in ignored conditional section" },
    { MessageId.INVALID_END_TAG,
      "invalid end-tag" },
    { MessageId.EPILOG_JUNK,
      "junk after document element" },
    { MessageId.MISMATCHED_END_TAG,
      "mismatched end tag: expected \"{1}\" but got \"{0}\"" },
    { MessageId.MISPLACED_XML_DECL,
      "misplaced XML decl" },
    { MessageId.MISSING_END_TAG,
      "missing end-tag" },
    { MessageId.NO_DOCUMENT_ELEMENT,
      "no document element" },
    { MessageId.NOT_WELL_FORMED,
      "not well-formed" },
    { MessageId.PE_GROUP_NESTING,
      "parameter entities not properly nested with parentheses" },
    { MessageId.PE_DECL_NESTING,
      "parameter entity not properly nested with declarations" },
    { MessageId.INTERNAL_PEREF_ENTVAL,
      "parameter entity reference in entity value in internal subset" },
    { MessageId.RECURSION,
      "recursive entity reference" },
    { MessageId.EXTERN_REF_ATTVAL,
      "reference to external entity in attribute value" },
    { MessageId.UNDEF_REF,
      "reference to undefined entity \"{0}\"" },
    { MessageId.UNDEF_PEREF,
      "reference to undefined parameter entity \"{0}\"" },
    { MessageId.UNPARSED_REF,
      "reference to unparsed entity" },
    { MessageId.SYNTAX_ERROR,
      "syntax error" },
    { MessageId.UNCLOSED_CDATA_SECTION,
      "unclosed CDATA section" },
    { MessageId.UNCLOSED_CONDITIONAL_SECTION,
      "unclosed conditional section" },
    { MessageId.UNCLOSED_TOKEN,
      "unclosed token" },
    { MessageId.UNSUPPORTED_ENCODING,
      "unsupported encoding" },
    { MessageId.DUPLICATE_ATTRIBUTE,
      "duplicate attribute" },
    { MessageId.XML_TARGET,
      "target of a processing instruction must not be [Xx][Mm][Ll]" },
    { MessageId.ILLEGAL_CHAR,
      "character not allowed" }
  };
}
