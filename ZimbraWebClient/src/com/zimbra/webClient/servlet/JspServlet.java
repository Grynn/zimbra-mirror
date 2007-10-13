package com.zimbra.webClient.servlet;

import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.taglib.ZJspSession;
import com.zimbra.cs.zclient.ZMailbox;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

/**
 * This class sub-classes the Jasper JspServlet in order to override
 * the context class loader. This is done so that we can transparently
 * merge skin message files into the default ones allowing skins to
 * independently override messages; JSP authors continue to use the
 * same mechanisms to load and format messages in JSP pages without
 * having to care about how the skin messages are overloaded.
 *
 * @author Andy Clark
 */
public class JspServlet extends org.apache.jasper.servlet.JspServlet {

	//
	// Servlet methods
	//

	public void service(ServletRequest request, ServletResponse response)
	throws IOException, ServletException {
		// set custom class loader
		Thread thread = Thread.currentThread();
		ClassLoader oLoader = thread.getContextClassLoader();
		ClassLoader nLoader = new ResourceLoader(oLoader, this, request, response);
		thread.setContextClassLoader(nLoader);

		// default processing
		try {
			super.service(request, response);
		}

		// restore previous class loader
		finally {
			thread.setContextClassLoader(oLoader);
		}
	}

	//
	// Classes
	//

	static class ResourceLoader extends ClassLoader {

		//
		// Data
		//

		private JspServlet servlet;
		private ServletRequest request;
		private ServletResponse response;

		private boolean isInitialized = false;
		private File skinDir;

		//
		// Constructors
		//

		public ResourceLoader(ClassLoader parent, JspServlet servlet,
							  ServletRequest request, ServletResponse response) {
			super(parent);
			this.servlet = servlet;
			this.request = request;
			this.response = response;
		}

		//
		// ClassLoader methods
		//

		public InputStream getResourceAsStream(String basename) {
			if (ZimbraLog.webclient.isDebugEnabled()) {
				ZimbraLog.webclient.debug("getResourceAsStream: basename="+basename);
			}

			// find resources
			InputStream stream = super.getResourceAsStream(basename);
			boolean isMsgOrKey = basename.startsWith("/messages/") || basename.startsWith("/keys/");
			if (isMsgOrKey) {
				// get skin dir
				if (!this.isInitialized) {
					this.isInitialized = true;
					try {
						String skin = (String)this.request.getAttribute("skin");
						if (ZimbraLog.webclient.isDebugEnabled()) {
							ZimbraLog.webclient.debug("ResourceLoader: attribute.skin="+skin);
						}
						if (skin == null) {
							skin = this.request.getParameter("skin");
							if (ZimbraLog.webclient.isDebugEnabled()) {
								ZimbraLog.webclient.debug("ResourceLoader: parameter.skin="+skin);
							}
						}
						if (skin == null && this.request instanceof HttpServletRequest) {
							HttpSession session = ((HttpServletRequest)this.request).getSession(false);
							if (session != null) {
								skin = (String)session.getAttribute("skin");
								if (ZimbraLog.webclient.isDebugEnabled()) {
									ZimbraLog.webclient.debug("ResourceLoader: session.attribute.skin="+skin);
								}
								if (skin == null) {
									JspFactory factory  = JspFactory.getDefaultFactory();
									PageContext context = factory.getPageContext(this.servlet, this.request, this.response, null, true, 0, true);
									ZJspSession zsession = ZJspSession.getSession(context);
									if (zsession != null) {
										ZMailbox mailbox = ZJspSession.getZMailbox(context);
										skin = mailbox.getPrefs().getSkin();
										if (ZimbraLog.webclient.isDebugEnabled()) {
											ZimbraLog.webclient.debug("ResourceLoader: mailbox.pref.skin="+skin);
										}
									}
									factory.releasePageContext(context);
								}
							}
						}
						if (skin == null) {
							skin = this.servlet.getServletContext().getInitParameter("zimbraDefaultSkin");
							if (ZimbraLog.webclient.isDebugEnabled()) {
								ZimbraLog.webclient.debug("ResourceLoader: context.init-parameter.zimbraDefaultSkin="+skin);
							}
						}
						File dir = new File(this.servlet.getServletContext().getRealPath("/skins/"+skin));
						if (dir.exists() && dir.isDirectory()) {
							this.skinDir = dir;
						}
					}
					catch (Exception e) {
						if (ZimbraLog.webclient.isDebugEnabled()) {
							ZimbraLog.webclient.debug(e);
						}
					}
				}
				// return resources
				if (this.skinDir != null) {
					File file = new File(this.skinDir, basename);
					if (file.exists()) {
						if (ZimbraLog.webclient.isDebugEnabled()) {
							ZimbraLog.webclient.debug("  found message overrides for skin="+this.skinDir.getName());
						}
						try {
							InputStream skinStream = new FileInputStream(file);
							if (stream != null) {
								// NOTE: We have to add a newline in case the original
								//       stream doesn't end with one. Otherwise, the
								//       first line from the skin stream will appear
								//       as part of the value of the last line in the
								//       original stream.
								InputStream newlineStream = new ByteArrayInputStream("\n".getBytes());
								stream = new ConcatInputStream(stream, newlineStream, skinStream);
							}
							else {
								stream = skinStream;
							}
						}
						catch (FileNotFoundException e) {
							// ignore
						}
					}
				}
			}
			return stream;
		}
		
	} // class ResourceLoader

	static class ConcatInputStream extends InputStream {

		//
		// Data
		//

		private InputStream[] streams;
		private InputStream current;
		private int count = 0;
		private int index = 0;

		//
		// Constructors
		//

		public ConcatInputStream(InputStream... streams) {
			this.streams = streams;
			if (this.streams != null) {
				this.count = this.streams.length;
			}
		}

		//
		// InputStream methods
		//
		
		public int read() throws IOException {
			if (this.current == null) {
				if (this.index == this.count) {
					return -1;
				}
				this.current = this.streams[this.index++];
			}
			int c = this.current.read();
			if (c == -1) {
				try {
					this.current.close();
				}
				catch (IOException e) {
					this.close();
					throw e;
				}
				this.current = null;
			}
			return c;
		}

		public void close() throws IOException {
			IOException ex = null;
			for (int i = this.index; i < this.count; i++) {
				try {
					this.streams[i].close();
				}
				catch (IOException e) {
					ex = e;
				}
			}
			if (ex != null) {
				throw ex;
			}
		}

	} // class ConcatInputStream

} // class JspServlet
