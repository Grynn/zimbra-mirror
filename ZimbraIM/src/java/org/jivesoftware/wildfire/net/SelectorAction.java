package org.jivesoftware.wildfire.net;

import java.io.IOException;
import java.nio.channels.SelectionKey;

/**
 * @author Daniele Piras
 */
interface SelectorAction
{
  public abstract void read( SelectionKey key ) throws IOException;
  public abstract void connect( SelectionKey key ) throws IOException;
}
