<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:yahoo="urn:yahoo:srch">
  <xsl:output method="html"/>
  <xsl:template match="yahoo:ResultSet/yahoo:Result">
    <p/>
    <div><b>Title: </b>   <a href="{yahoo:ClickUrl}" target="_blank"><xsl:value-of select="yahoo:Title"/></a></div>
    <div><b>Summary: </b> <xsl:value-of select="yahoo:Summary"/></div>
  </xsl:template>
</xsl:stylesheet>
