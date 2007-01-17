package org.jivesoftware.wildfire.net;

import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;
import org.jivesoftware.wildfire.ConnectionManager;
import org.jivesoftware.wildfire.ServerPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.Set;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.DefaultIoFilterChainBuilder;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.TransportType;
import org.apache.mina.filter.LoggingFilter;
import org.apache.mina.handler.StreamIoHandler;
import org.apache.mina.handler.support.IoSessionInputStream;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketSessionConfig;

public class NewNonBlockingAcceptingMode extends SocketAcceptingMode {

    private static final String KEY_IN = StreamIoHandler.class.getName() + ".in";
    private static final String KEY_OUT = StreamIoHandler.class.getName() + ".out";

    private static final String SOCKET = NewNonBlockingAcceptingMode.class.getName() + ".s";
    private static final String READER = NewNonBlockingAcceptingMode.class.getName() + ".r";

    class XMPPIoHandlerAdapter extends IoHandlerAdapter  {
        
        public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
            Log.info("Exception caught for session: " + session.toString() + " Caused by: " +cause.toString());
            cause.printStackTrace();
            super.exceptionCaught(session, cause);
        }
        
        public void messageSent(IoSession session, Object message) throws Exception {
            Log.info("Message send for session: "+session.toString());
            super.messageSent(session, message);
        }
        
        public void sessionClosed(IoSession session) throws Exception {
            Log.info("Session closed: "+session.toString());
            
            SocketReader reader = (SocketReader)(session.getAttribute(READER));
            reader.connection.close();
            super.sessionClosed(session);
        }
        
        public void sessionCreated(IoSession session) throws Exception {
            Log.info("Session created: " + session.toString());
            
            try {
                if( session.getTransportType() == TransportType.SOCKET )
                {
                    ( ( SocketSessionConfig ) session.getConfig() ).setReceiveBufferSize( 128 );
                }

                FakeSocket.MinaFakeSocket socket = new FakeSocket.MinaFakeSocket(session);
                session.setAttribute(SOCKET, socket);
                SocketReader reader = connManager.createSocketReader(socket, false, serverPort, false);
                session.setAttribute(READER, reader);
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
            super.sessionCreated(session);
        }
        
        public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
            Log.info("Session idle: "+session.toString() + " status "+status.toString());
            super.sessionIdle(session, status);
        }

        public void sessionOpened(IoSession session) throws Exception {
            Log.info("Session opened: " + session.toString());
            
            super.sessionOpened(session);
        }

        public void messageReceived( IoSession session, Object buf ) {
            //Log.info("Received a message!");

            if( !( buf instanceof ByteBuffer ) ) // check your imports: should be org.apache.mina.common.ByteBuffer, not java.nio!
            {
                return;
            }

            FakeSocket.MinaFakeSocket socket = (FakeSocket.MinaFakeSocket)(session.getAttribute(SOCKET));
            socket.readReady((ByteBuffer)buf);
            //socket.getIoInputStream().write((ByteBuffer)buf);
            //socket.readReady();
        }



//      protected void processStreamIo(IoSession session, InputStream in, OutputStream out) {
//      Log.info("New NIO Session: "+session);

//      if( session.getTransportType() == TransportType.SOCKET )
//      {
//      ( ( SocketSessionConfig ) session.getConfig() ).setReceiveBufferSize( 128 );
//      }

//      session.setIdleTime( IdleStatus.BOTH_IDLE,  60 * 30); // 30 minute idle

//      try {
//      reader = connManager.createSocketReader(new InSession(session, in, out), false, serverPort);
//      } catch(IOException ex) {
//      ex.printStackTrace();
//      }
//      }

//      /**
//      * Forwards read data to input stream.
//      */
//      public void messageReceived( IoSession session, Object buf )
//      {
//      Log.info("Received a message!");

//      if( !( buf instanceof ByteBuffer ) ) // check your imports: should be org.apache.mina.common.ByteBuffer, not java.nio!
//      {
//      return;
//      }

//      ByteBuffer rb = ( ByteBuffer ) buf;
//      // Write the received data back to remote peer
//      ByteBuffer wb = ByteBuffer.allocate( rb.remaining() );
//      wb.put( rb );
//      wb.flip();
//      session.write( wb );

//      final IoSessionInputStream in = ( IoSessionInputStream ) session.getAttribute( StreamIoHandler.class.getName() + ".in");
//      in.write( ( ByteBuffer ) buf );
//      }


//      SocketReader reader;

//      XMPPIoHandlerAdapter() {
//      Log.info("XMPPoHandlerAdapter");
//      }



//      public void sessionCreated( IoSession session ) throws IOException
//      {
//      Log.info("New NIO Session: "+session);

//      if( session.getTransportType() == TransportType.SOCKET )
//      {
//      ( ( SocketSessionConfig ) session.getConfig() ).setReceiveBufferSize( 2048 );
//      }

//      session.setIdleTime( IdleStatus.BOTH_IDLE,  60 * 30); // 30 minute idle

////    reader = connManager.createSocketReader(session, false, serverPort, false); 
//      }

//      public void sessionIdle( IoSession session, IdleStatus status )
//      {
//      Log.info("*** IDLE #" + session.getIdleCount( IdleStatus.BOTH_IDLE ) + " ***" );
//      }

//      public void exceptionCaught( IoSession session, Throwable cause )
//      {
//      cause.printStackTrace();
//      session.close();
    }

//  public void messageReceived( IoSession session, Object message ) throws Exception
//  {
//  Log.info("Received a message!");

//  if( !( message instanceof ByteBuffer ) ) // check your imports: should be org.apache.mina.common.ByteBuffer, not java.nio!
//  {
//  return;
//  }

//  ByteBuffer rb = ( ByteBuffer ) message;
//  // Write the received data back to remote peer
//  ByteBuffer wb = ByteBuffer.allocate( rb.remaining() );
//  wb.put( rb );
//  wb.flip();
//  session.write( wb );
//  }

    //}

    NewNonBlockingAcceptingMode(ConnectionManager connManager, ServerPort serverPort, InetAddress bindInterface) throws IOException {
        super(connManager, serverPort);

    }

    public void shutdown() {
        super.shutdown();
    }


    public void run() {
        IoAcceptor acceptor = new SocketAcceptor();
        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
        System.out.println(chain);

        InetSocketAddress addr = new InetSocketAddress( serverPort.getPort() ); 

        try {
            // Bind
            acceptor.bind(addr, new XMPPIoHandlerAdapter() );

        } catch (IOException ie) {
            if (notTerminated) {
                Log.error(LocaleUtils.getLocalizedString("admin.error.accept"),
                            ie);
            }
        }


        System.out.println( "Listening on port " + addr );
    }        



}
