<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:google="urn:GoogleSearch">
  <xsl:output method="html"/>
  <xsl:template match="SOAP-ENV:Envelope/SOAP-ENV:Body/google:doGoogleSearchResponse/return">
    <html>
      <xsl:apply-templates select="resultElements"/>
    </html>
  </xsl:template>
  <xsl:template match="resultElements">
    <xsl:apply-templates select="item"/>
  </xsl:template>
  <xsl:template match="item">
    <div/>
    <div><b>Title: </b>   <xsl:value-of select="title"/></div>
    <div><b>Summary: </b> <xsl:value-of select="snippet"/></div>
    <div><b>Url: </b>     <xsl:value-of select="URL"/></div>
  </xsl:template>
</xsl:stylesheet>
