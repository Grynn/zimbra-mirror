<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:google="urn:GoogleSearch">
  <xsl:output method="html"/>
  <xsl:template match="SOAP-ENV:Envelope/SOAP-ENV:Body/google:doGoogleSearchResponse/return/resultElements/item">
    <div/>
    <div><b>Title: </b>   <xsl:value-of select="title"/></div>
    <div><b>Summary: </b> <xsl:value-of select="snippet"/></div>
    <div><b>Url: </b>     <xsl:value-of select="URL"/></div>
  </xsl:template>
</xsl:stylesheet>
