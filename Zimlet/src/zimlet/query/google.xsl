<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:google="urn:GoogleSearch">
  <xsl:output method="html"/>
  <xsl:template match="SOAP-ENV:Envelope/SOAP-ENV:Body/google:doGoogleSearchResponse/return/resultElements/item">
    <p/>
    <div><b>Title: </b>   <a href="{URL}" target="_blank"><xsl:value-of select="title"/></a></div>
    <div><b>Summary: </b> <xsl:value-of select="snippet"/></div>
    <div><b>URL: </b>     <xsl:value-of select="URL"/></div>
  </xsl:template>
  <xsl:template match="text()|@*"/>
</xsl:stylesheet>
