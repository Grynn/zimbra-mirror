<%@ page language="java" import="org.apache.commons.httpclient.*, org.apache.commons.httpclient.methods.*, javax.servlet.*, com.zimbra.common.util.*"%>
<%@ page language="java" import="java.net.*, java.util.*, com.zimbra.common.util.*, com.zimbra.cs.util.NetUtil, com.zimbra.cs.servlet.ZimbraServlet"%>
<%@ page language="java" import="java.io.*, org.apache.commons.httpclient.methods.multipart.*"%>
<%@ page language="java" import="com.zimbra.cs.service.*" %>
<%@ page language="java" import="com.zimbra.cs.zclient.*" %>

<%@ page import="org.apache.commons.fileupload.*,org.apache.commons.fileupload.disk.*, org.apache.commons.io.*, java.util.*,
java.io.File, java.lang.Exception" %>
<%
        //Read Mode of the Upload
        String mode = request.getParameter("mode");
        //Image Caption if available
        String imageCaption = request.getParameter("caption");
		//Snapfish:Src
		String src = request.getParameter("Src");
        //Snapfish: Target URL
        String url = request.getParameter("url");
        //Snapfish: Upload SessionId
        String sessionId = request.getParameter("SessionId");
        //Snapfish: AuthCode
        String authcode = request.getParameter("authcode");
        //Snapfish: AlbumID
        String albumId = request.getParameter("AlbumID");
        //Snapfish: Sequence No.
        String seqNo= request.getParameter("SequenceNumber");
		//Image Path for the uploaded Image
		String imagePath = request.getParameter("imagePath");

        //Constructing URL with the params required for the upload action
        String reqParams = 	"authcode="+authcode+"&"
						+	"AlbumId="+albumId+"&"
						+	"SessionId="+sessionId+"&"
						+	"Src="+src+"&"
						+	"SequenceNumber="+seqNo;
		
		url = url + "?" + reqParams;
		System.out.println("URL:"+url);


        //MultipartPostMethod postMethod = null;
		MultipartPostMethod postMethod =  new MultipartPostMethod(url);


        //Tmp. File to upload image in the temporary session                                                                                                              
        String dirPath = System.getProperty("java.io.tmpdir", "/tmp");
        String resourceFilePath = dirPath+"/snapfish_"+System.currentTimeMillis()+".jpg";
        File resourceFile = new File(resourceFilePath);
        FileOutputStream outFileStream = new FileOutputStream(resourceFile.getPath());
        System.out.println("File Absolute Path:"+ resourceFile.getPath());

        try{
			System.out.println("Inside Try");
            //Copying the Cookies into the HTTPClient to maintain the state
            javax.servlet.http.Cookie reqCookie[] = request.getCookies();
			org.apache.commons.httpclient.Cookie[] clientCookie= new org.apache.commons.httpclient.Cookie[reqCookie.length];
			String hostName = request.getRemoteHost();
            //Fix: to test in the local file system
            if("127.0.0.1".equals(hostName)){
				hostName = "localhost";
			}
			for(int i=0;i<reqCookie.length;i++){
				javax.servlet.http.Cookie cookie = reqCookie[i];
				clientCookie[i] = new org.apache.commons.httpclient.Cookie(hostName,cookie.getName(),cookie.getValue(),"/",null,false);
			}
			
			HttpState state = new HttpState();
	        state.addCookies(clientCookie);
	        
	        //Setting the state to HTTPClient			
			HttpClient client = new HttpClient();
	        client.setState(state);
	       
	       // ByteArrayPartSource byteArrayPartSrc=null;
	        
	        System.out.println("Mode:"+mode);
            
            
            //Upload URL (external image) into Snpafish
            if("uploadURL".equals(mode)){

	       	try{ 			
                GetMethod get = new GetMethod(URLDecoder.decode(imagePath, "UTF-8"));

                get.setFollowRedirects(true);

                try {
                    client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
                    client.executeMethod(get);
                	} catch (HttpException ex) {
                   	 	ex.printStackTrace();
                    	response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    	return;
                	}

                	ByteUtil.copy(get.getResponseBodyAsStream(), false, outFileStream, false);

            	}catch(Exception e){
	        		e.printStackTrace();
	        		throw new Exception("Failed to Upload Zimbra Image");
	        	}finally{
            		if (outFileStream != null) {
                		outFileStream.close();
            		}
	        	}
	        
	        
	        }else{ //Upload Local File from the File System.

                System.out.println("uploadLocal");
	        	//Hey, we need to change this to handle real file upload
	        	//resourceFile = new File(URLDecoder.decode(imagePath, "UTF-8"));
	        	
	        		//resourceFile = new File("C:\\tmp.jpg");
	        		//outFileStream = new FileOutputStream(resourceFile.getPath());
	        	
	        		if(!FileUpload.isMultipartContent(request)){
                        throw new Exception("Failed to Upload Image to Snapfish(not multipart/form-data)");
                        //System.out.println("Illegeal Request");
	        			//throw new Exception("Illegal Request");
	        		}
	        	
	        		DiskFileUpload upload = new DiskFileUpload();
	        		List fileItemsList = upload.parseRequest(request);
  					
  					String optionalFileName = "";
  					FileItem fileItem = null;
	
  					Iterator it = fileItemsList.iterator();
 					while (it.hasNext()){
    						FileItem fileItemTemp = (FileItem)it.next();
    						if (fileItemTemp.isFormField()){
    							if (fileItemTemp.getFieldName().equals("filename"))
							        optionalFileName = fileItemTemp.getString();
    						}else{
    							fileItem = fileItemTemp;
    						}	
    						
    				}
    				if(fileItem == null){
                        throw new Exception("Failed to Upload Image to Snapfish(fileItem null)");
                        //System.out.println("File not uploaded properly");
    				}


					System.out.println("File Uploaded successfully:"+fileItem.getSize());
					    
					String fileName = fileItem.getName();

					if (fileItem.getSize() > 0){
                        
                        fileName = optionalFileName.trim().equals("")?fileName:optionalFileName;

                        try {

                            ByteUtil.copy(fileItem.getInputStream(), false, outFileStream, false);

                            imageCaption = fileName;
                            //filePart = new FilePart("foo", fileName, resourceFile);

                        }catch (Exception e){
                            e.printStackTrace();
                            throw new Exception("Failed to Upload to Snapfish(Unknown)");
                        }finally{
                            if(outFileStream != null){
                                outFileStream.close();
                            }
                        }
					}
	        }
	        //Setting up the proxy
	        NetUtil.configureProxy(client);
            
            //Adding File to the PostMethod
            FilePart filePart = new FilePart("foo", imageCaption, resourceFile);
            filePart.setTransferEncoding(null);
	        postMethod.addPart(filePart);


            //Uploading File to Snapfish
            try {
	        	client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
				client.executeMethod(postMethod);
			} catch (HttpException ex) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}




                try {
            		response.setStatus(postMethod.getStatusCode());
            	} catch (Exception ex) {
            		throw new Exception("exception while proxying in getting status code "+url);
            	}





            //Becoz of UploadManager in client side, special case becoz its form based file upload
			if("uploadLocal".equals(mode)){
			    System.out.println("Upload Local Response");
				out.println("<html><body onload=\"window.parent._snapfish(document.body.innerHTML,null);\">");
				out.println(postMethod.getResponseBodyAsString());
				out.println("</body></html>");
				System.out.println("Response Given back");
				return;
			}
			
			try{
				response.setContentType(postMethod.getResponseHeader("Content-Type").getValue());
			}catch(Exception e){
				response.setContentType("text/xml");
			}
			
			ByteUtil.copy(postMethod.getResponseBodyAsStream(), false, response.getOutputStream(), false);
			
		} finally {
			if (postMethod != null)
				postMethod.releaseConnection();
            if(resourceFile != null)
                resourceFile.delete();
        }
%>
