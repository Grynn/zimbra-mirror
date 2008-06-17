<%@ page language="java"
         import="java.util.*,javax.sip.*,javax.sip.address.*,javax.sip.header.*,javax.sip.message.*,java.security.MessageDigest,java.security.NoSuchAlgorithmException" %>
<%@ taglib prefix="z" uri="/WEB-INF/zimbra.tld" %>
<z:zimletconfig var="config" action="list" zimlet="com_zimbra_asterisk"/>
<%

	final class Invite implements SipListener {

		private SipProvider udpProvider;
		private AddressFactory addressFactory;
		private MessageFactory messageFactory;
		private HeaderFactory headerFactory;
		private SipStack sipStack;
		private ContactHeader contactHeader;
		private ListeningPoint udpListeningPoint;
		private Request request;
		private String sipHost;
		private String from;
		private String to;
		private String toList;
		private String user;
		private String pass;
		private SipURI requestURI;
		private String[] toArray;
		Random generator = new Random();
		private int numCallees;

		private char[] hxArray = {
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
		};

		// To run on two machines change these to suit.
		public String myAddress;
		private Boolean debug;
		private int myPort;

		protected ClientTransaction inviteTid;

		protected final String usageString =
				"Invite <sipHost> <from> <to> <user> <pass>";

		private void usage() {
			System.out.println(usageString);
			this.shutDown();
		}

		private void shutDown() {
			try {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}
				//System.out.println("nulling reference");
				sipStack.deleteListeningPoint(udpListeningPoint);
				// This will close down the stack and exit all threads
				udpProvider.removeSipListener(this);
				while (true) {
					try {
						sipStack.deleteSipProvider(udpProvider);
						break;
					} catch (ObjectInUseException ex) {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							continue;
						}
					}
				}
				sipStack = null;
				udpProvider = null;
				this.inviteTid = null;
				this.contactHeader = null;
				addressFactory = null;
				headerFactory = null;
				messageFactory = null;
				this.udpListeningPoint = null;
				System.gc();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}


		public void processRequest(RequestEvent requestReceivedEvent) {
			Request request = requestReceivedEvent.getRequest();
			ServerTransaction serverTransactionId =
					requestReceivedEvent.getServerTransaction();

			/*System.out.println(
					"\n\nRequest "
							+ request.getMethod()
							+ " received at "
							+ sipStack.getStackName()
							+ " with server transaction id "
							+ serverTransactionId);
			*/

			if (request.getMethod().equals(Request.BYE))
				processBye(request, serverTransactionId);
			else if (request.getMethod().equals(Request.INVITE))
				processInvite(request, serverTransactionId);
			else if (request.getMethod().equals(Request.ACK))
				processAck(request, serverTransactionId);

		}

		public void processInvite(Request request, ServerTransaction st) {
			try {
				Response response = messageFactory.createResponse(Response.OK, request);
				((ToHeader) response.getHeader(ToHeader.NAME)).setTag(((ToHeader) request.getHeader(ToHeader.NAME)).getTag());

				javax.sip.address.Address address =
						addressFactory.createAddress("Shootme <sip:" + myAddress + ":" + myPort + ">");
				ContactHeader contactHeader =
						headerFactory.createContactHeader(address);
				response.addHeader(contactHeader);
				st.sendResponse(response);
			} catch (Exception ex) {
				ex.printStackTrace();
				this.shutDown();
				//System.exit(0);
			}
		}


		public void processAck(Request request, ServerTransaction tid) {
			try {
				if (debug) {
					System.out.println("Got an ACK! sending bye : " + tid);
				}
				if (tid != null) {
					Dialog dialog = tid.getDialog();
					Request bye = dialog.createRequest(Request.BYE);
					SipProvider provider = udpProvider;
					ClientTransaction ct = provider.getNewClientTransaction(bye);
					dialog.sendRequest(ct);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				this.shutDown();
				//System.exit(0);
			}
		}

		public void processBye(
				Request request,
				ServerTransaction serverTransactionId) {
			try {
				if (debug) {
					System.out.println("got a bye .");
				}
				if (serverTransactionId == null) {
					if (debug) {
						System.out.println("null TID.");
					}
					return;
				}
				Dialog dialog = serverTransactionId.getDialog();
				if (debug) {
					System.out.println("Dialog State = " + dialog.getState());
				}
				Response response = messageFactory.createResponse
						(200, request);
				serverTransactionId.sendResponse(response);
				if (debug) {
					System.out.println("Sending OK.");
					System.out.println("Dialog State = " + dialog.getState());
				}

				this.shutDown();

			} catch (Exception ex) {
				ex.printStackTrace();
				this.shutDown();
				//System.exit(0);

			}
		}

		public void processResponse(ResponseEvent responseReceivedEvent) {
			Response response = responseReceivedEvent.getResponse();
			Transaction tid = responseReceivedEvent.getClientTransaction();

			
			if (debug) {
				System.out.println(
					"Response received with client transaction id "
							+ tid
							+ ": "
							+ response.getStatusCode());
			}
			if (tid == null) {
				if (debug) {
					System.out.println("Stray response -- dropping ");
				}
				return;
			}

			try {
				if (response.getStatusCode() == Response.OK
						&& ((CSeqHeader) response.getHeader(CSeqHeader.NAME))
						.getMethod()
						.equals(
								Request.INVITE)) {
					// Request cancel = inviteTid.createCancel();
					// ClientTransaction ct =
					//	sipProvider.getNewClientTransaction(cancel);
					Dialog dialog = tid.getDialog();
					Request ackRequest = dialog.createRequest(Request.ACK);
					if (debug) {
					  System.out.println("Sending ACK");
					}
					dialog.sendAck(ackRequest);

					if (debug) {
						System.out.println("Invite accepted:");
						System.out.println(response.toString());
					}

					Request referRequest = dialog.createRequest(Request.REFER);

					// I shouldn't have to override this, right?
					SipURI referURI = (SipURI) ((ToHeader) response.getHeader(ToHeader.NAME)).getAddress().getURI();
					referRequest.setRequestURI(referURI);

					to = from;

					SipURI toAddress =
							addressFactory.createSipURI(to, sipHost + ":5060");

					javax.sip.address.Address toNameAddress = addressFactory.createAddress(toAddress);
					ReferToHeader referToHeader =
							headerFactory.createReferToHeader(toNameAddress);

					referRequest.setHeader(referToHeader);

					if (debug) {
						System.out.println("REFER: \n" + referRequest.toString());
					}

					//try {Thread.sleep(4000); } catch (Exception ex) {}

					if (debug) {
						System.out.println("Sending REFER to " + toAddress.toString());
					}

					ClientTransaction ct =
							udpProvider.getNewClientTransaction(referRequest);
					dialog.sendRequest(ct);

					// Now we want to REFER to the other phone to the to.

				} else if (response.getStatusCode() == Response.OK
						&& ((CSeqHeader) response.getHeader(CSeqHeader.NAME))
						.getMethod()
						.equals(
								Request.BYE)) {
					this.shutDown();
				} else if (response.getStatusCode() == Response.PROXY_AUTHENTICATION_REQUIRED) {
					if (debug) {
						System.out.println("Proxy auth required:");
						System.out.println (response.toString());
					}

					//Dialog dialog = tid.getDialog();
					//Request inviteRequest = dialog.createRequest(Request.INVITE);
					Request inviteRequest = (Request) request.clone();
					ProxyAuthenticateHeader authReqHeader =
							(ProxyAuthenticateHeader) response.getHeader("Proxy-Authenticate");

					ProxyAuthorizationHeader authHeader =
							headerFactory.createProxyAuthorizationHeader(authReqHeader.getScheme());
					authHeader.setRealm(authReqHeader.getRealm());
					authHeader.setNonce(authReqHeader.getNonce());
					authHeader.setURI(requestURI);
					authHeader.setUsername(user);

					String A1 = user + ":" + authReqHeader.getRealm() + ":" + pass;
					String A2 = request.getMethod() + ":" + request.getRequestURI().toString();
					String A1dig = this.md5Digest(A1);
					String A2dig = this.md5Digest(A2);

					authHeader.setResponse(md5Digest(A1dig + ":" + authReqHeader.getNonce() + ":" + A2dig));

					inviteRequest.addHeader(authHeader);

					CSeqHeader cSeqHeader =
							headerFactory.createCSeqHeader(2, Request.INVITE);

					inviteRequest.setHeader(cSeqHeader);
					Invite listener = this;

					inviteRequest.setHeader(response.getHeader("Via"));
					inviteRequest.setHeader(response.getHeader("To"));
					inviteRequest.setHeader(response.getHeader("Call-ID"));
					inviteRequest.setHeader(response.getHeader("From"));

					listener.inviteTid = udpProvider.getNewClientTransaction(inviteRequest);
					if (debug) {
						System.out.println("Sending auth response");
						System.out.println (inviteRequest.toString());
					}
					listener.inviteTid.sendRequest();

				} else if (response.getStatusCode() == Response.ACCEPTED
						&& ((CSeqHeader) response.getHeader(CSeqHeader.NAME))
						.getMethod()
						.equals(
								Request.REFER)) {
					numCallees--;
					if (debug) {
						System.out.println("Referral accepted (" + numCallees + "/" + toArray.length + " remaining)");
						System.out.println (response.toString());
					}
					if (numCallees == 0) {
						this.shutDown();
					}
				/*
				# TRYING - 100
				# RINGING - 180
				# CALL_IS_BEING_FORWARDED - 181
				# QUEUED - 182
				# SESSION_PROGRESS - 183
				*/
				} else if (response.getStatusCode() == Response.TRYING) {
					if (debug) {
						System.out.println("Trying number...");
					}
				} else if (response.getStatusCode() == Response.RINGING) {
					if (debug) {
						System.out.println("Ringing number...");
					}
				} else if (response.getStatusCode() == Response.CALL_IS_BEING_FORWARDED) {
					if (debug) {
						System.out.println("Forwarding response...");
					}
				} else if (response.getStatusCode() == Response.QUEUED) {
					if (debug) {
						System.out.println("Queued response...");
					}
				} else if (response.getStatusCode() == Response.SESSION_PROGRESS) {
					if (debug) {
						System.out.println("Session progress response...");
					}
				} else {
					if (debug) {
						System.out.println("OTHER RESPONSE:\n" + response.toString());
					}
					this.shutDown();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				this.shutDown();
				//System.exit(0);
			}

		}

		private String getRandomTag() {
			//System.out.println ("Getting random bind port");
			int r = this.generator.nextInt(60000);
			String foo = "Zimbra" + r;
			//System.out.println (r);
			return (foo);
		}

		private int getRandomPort() {
			//System.out.println ("Getting random bind port");
			int r = this.generator.nextInt(60000);
			//System.out.println (r);
			return (r + 5070);
		}

		public String md5Digest(String s) {
			MessageDigest digest;
			try {
				digest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				return null;
			}
			for (int i = 0; i < s.length(); i++) {
				byte b = (byte) (s.charAt(i) & 0xff);
				digest.update(b);
			}
			byte[] bin = digest.digest();
			char[] result = new char[bin.length * 2];

			int j = 0;
			for (byte aBin : bin) {
				result[j++] = hxArray[(aBin >> 4) & 0xf];
				result[j++] = hxArray[aBin & 0xf];
			}
			return new String(result);
		}


		public void processTimeout(javax.sip.TimeoutEvent timeoutEvent) {

			if (debug) {
				System.out.println("Transaction Time out");
				System.out.println("TimeoutEvent " + timeoutEvent.getTimeout());
			}
		}

		public String init(String args[]) {
			SipFactory sipFactory;
			sipStack = null;
			sipFactory = SipFactory.getInstance();
			sipFactory.setPathName("gov.nist");
			Properties properties = new Properties();

			//myAddress = java.net.InetAddress.getLocalHost().getHostAddress();
			myAddress = args[0];
			sipHost = args[1];
			from = args[2];
			toList = args[3];
			user = args[4];
			pass = args[5];
			String dbg = args[6];
			if (dbg.equals("true")) {
				System.out.println ("Asterisk debug mode on");
				debug = true;
			} else {
				debug = false;
			}

			toArray = toList.split(";");

			if (debug)  {
				System.out.println("Calling from " + from + " to " + toList);
			}

			// If you want to try TCP transport change the following to
			String transport = "udp";
			String peerHostPort = sipHost + ":5060";

			// Mandatory properties
			properties.setProperty("javax.sip.IP_ADDRESS", myAddress);
			properties.setProperty("javax.sip.STACK_NAME", "invite");

			// The following properties are specific to nist-sip
			// and are not necessarily part of any other jain-sip
			// implementation.
			// You can set a max message size for tcp transport to
			// guard against denial of service attack.
			properties.setProperty("gov.nist.javax.sip.MAX_MESSAGE_SIZE",
					"1048576");

			// Drop the client connection after we are done with the transaction.
			properties.setProperty("gov.nist.javax.sip.CACHE_CLIENT_CONNECTIONS", "false");
			// Set to 0 in your production code for max speed.
			// You need  16 for logging traces. 32 for debug + traces.
			// Your code will limp at 32 but it is best for debugging.
			properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "0");

			try {
				// Create SipStack object
				sipStack = sipFactory.createSipStack(properties);
				if (debug) {
					System.out.println("createSipStack " + sipStack);
				}
			} catch (PeerUnavailableException e) {
				// could not find
				// gov.nist.jain.protocol.ip.sip.SipStackImpl
				// in the classpath
				e.printStackTrace();
				//System.err.println(e.getMessage());
				this.shutDown();
				//System.exit(0);
			}

			try {
				headerFactory = sipFactory.createHeaderFactory();
				addressFactory = sipFactory.createAddressFactory();
				messageFactory = sipFactory.createMessageFactory();

				udpListeningPoint = null;
				udpProvider = null;
				Invite listener;

				if (udpListeningPoint == null) {
					for (int count = 0; count < 5; count++) {
						myPort = this.getRandomPort();
						if (debug) {
							System.out.println("Binding to " + myAddress + ":" + myPort);
						}
						try {
							udpListeningPoint = sipStack.createListeningPoint
									(myPort, "udp");
							break;
						} catch (InvalidArgumentException ex) {
						}
					}
				}

				if (udpListeningPoint == null) {
					System.out.println ("Failed to bind to "+myAddress);
					return "Failed to bind to "+myAddress;
				}

				if (udpProvider == null) {
					udpProvider = sipStack.createSipProvider
							(udpListeningPoint);
				}

				listener = this;
				udpProvider.addSipListener(listener);

				SipProvider sipProvider = udpProvider;

				// create >From Header
				SipURI fromAddress =
						addressFactory.createSipURI(user, sipHost);

				javax.sip.address.Address fromNameAddress = addressFactory.createAddress(fromAddress);
				// fromNameAddress.setDisplayName(fromDisplayName);

				// Create ContentTypeHeader
				ContentTypeHeader contentTypeHeader =
						headerFactory.createContentTypeHeader("application", "sdp");

				// Create a new MaxForwardsHeader
				MaxForwardsHeader maxForwards =
						headerFactory.createMaxForwardsHeader(70);

				// Create contact headers
				String host = sipStack.getIPAddress();

				String sdpData =
						"v=0\r\n"
								+ "c=IN IP4 0.0.0.0\r\n"
								+ "m=audio " + myPort + " RTP/AVP 0 8 4 18\r\n"
								+ "a=rtpmap:0 PCMU/8000\r\n"
								+ "a=rtpmap:8 PCMA/8000\r\n"
								+ "a=rtpmap:4 G723/8000\r\n"
								+ "a=rtpmap:18 G729A/8000\r\n";

				javax.sip.header.Header callInfoHeader =
						headerFactory.createHeader(
								"Call-Info",
								"<http://www.antd.nist.gov>");

				// to Loop

				numCallees = toArray.length;
				for (String aToArray : toArray) {
					String tag = getRandomTag();
					FromHeader fromHeader =
							headerFactory.createFromHeader(fromNameAddress, tag);

					// create Request URI
					requestURI =
							addressFactory.createSipURI(aToArray, peerHostPort);

					// create To Header
					SipURI toAddress =
							addressFactory.createSipURI(aToArray, peerHostPort);
					Address toNameAddress = addressFactory.createAddress(toAddress);
					//toNameAddress.setDisplayName(toDisplayName);
					ToHeader toHeader =
							headerFactory.createToHeader(toNameAddress, null);

					// Create a new CallId header
					CallIdHeader callIdHeader = sipProvider.getNewCallId();

					// Create a new Cseq header
					CSeqHeader cSeqHeader =
							headerFactory.createCSeqHeader(1, Request.INVITE);

					// Create ViaHeaders

					ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
					ViaHeader viaHeader =
							headerFactory.createViaHeader(
									sipStack.getIPAddress(),
									sipProvider.getListeningPoint().getPort(),
									transport,
									null);

					// add via headers
					viaHeaders.add(viaHeader);

					// Create the request.
					request =
							messageFactory.createRequest(
									requestURI,
									Request.INVITE,
									callIdHeader,
									cSeqHeader,
									fromHeader,
									toHeader,
									viaHeaders,
									maxForwards);

					// Create the contact name address.
					SipURI contactURI = addressFactory.createSipURI(user, host);
					//contactURI.setPort(sipProvider.getListeningPoint().getPort());

					Address contactAddress = addressFactory.createAddress(contactURI);

					// Add the contact address.
					//contactAddress.setDisplayName(toArray[forIndex]);

					contactHeader =
							headerFactory.createContactHeader(contactAddress);

					request.addHeader(contactHeader);
					request.setContent(sdpData, contentTypeHeader);
					request.addHeader(callInfoHeader);

					if (debug) {
						System.out.println("REQUEST \n" + request.toString());
					}
					// Create the client transaction.
					listener.inviteTid = sipProvider.getNewClientTransaction(request);

					// send the request out.
					listener.inviteTid.sendRequest();
					if (debug) {
						System.out.println("REQUEST Sent to " + toAddress.toString());
					}
					try {
						//Thread.sleep(4000);
					} catch (Exception ex) {
					}
				}
				// to Loop

			} catch (Exception ex) {
				System.out.println(ex.getMessage());
				ex.printStackTrace();
				this.usage();
				return "Call failed "+ex.getMessage();
			}
			return "Call successfully established from " + from + " to " + toList;
		}
	}

	Map zConfig = (Map) request.getAttribute("config");
	String myAddress = (String) ((Map) zConfig.get("global")).get("myAddress");
	String debug = (String) ((Map) zConfig.get("global")).get("debug");

	String to = request.getParameter("to");
	String from = request.getParameter("from");
	String user = request.getParameter("uname");
	String pass = request.getParameter("pass");
    String sipHost = request.getParameter("sipHost");
    if(sipHost == null)
         sipHost = (String) ((Map) zConfig.get("global")).get("sipHost");

    to = java.net.URLDecoder.decode(to, "UTF8");
	from = java.net.URLDecoder.decode(from, "UTF8");
	user = java.net.URLDecoder.decode(user, "UTF8");
	pass = java.net.URLDecoder.decode(pass, "UTF8");

	to = to.replace("(", "");
	to = to.replace(")", "");
	to = to.replace("-", "");
	to = to.replace(".", "");
	to = to.trim();

	from = from.replace("(", "");
	from = from.replace(")", "");
	from = from.replace("-", "");
	from = from.replace(".", "");
	from = from.trim();

	String args[] = {myAddress, sipHost, from, to, user, pass, debug};
	String foo = new Invite().init(args);
	out.println(foo);
%>
