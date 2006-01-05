<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:yahoo="urn:yahoo:srch">
  <xsl:output method="html"/>
  <xsl:template match="yahoo:ResultSet">
    <html>
      <xsl:apply-templates/>
    </html>
  </xsl:template>
  <xsl:template match="yahoo:Result">
    <div/>
    <div><b>Title: </b>   <xsl:value-of select="yahoo:Title"/></div>
    <div><b>Summary: </b> <xsl:value-of select="yahoo:Summary"/></div>
    <div><b>Url: </b>     <xsl:value-of select="yahoo:Url"/></div>
  </xsl:template>
</xsl:stylesheet>
