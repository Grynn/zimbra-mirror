package com.zimbra.cs.im.xp.tok;

/**
 * Thrown to indicate that the byte subarray being tokenized does not start
 * with a legal XML token but might be one if the subarray were extended.
 * @version $Revision: 1.2 $ $Date: 1998/02/17 04:24:13 $
 */
public class PartialTokenException extends TokenException {
}
