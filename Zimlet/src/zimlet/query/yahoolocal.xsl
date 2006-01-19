<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:yahoo="urn:yahoo:lcl">
  <xsl:output method="html"/>
  <xsl:template match="yahoo:ResultSet/yahoo:ResultSetMapUrl">
    <div><a href="{text()}" target="_blank"><b>Click here for the map of the results</b></a></div>
  </xsl:template>
  <xsl:template match="yahoo:ResultSet/yahoo:Result">
    <p/>
    <div><b>Title: </b>   <a href="{yahoo:ClickUrl}" target="_blank"><xsl:value-of select="yahoo:Title"/></a></div>
    <div><b>Address: </b> 
      <xsl:value-of select="yahoo:Address"/>, <xsl:value-of select="yahoo:City"/>, <xsl:value-of select="yahoo:State"/>
    </div>
    <div><b>Phone: </b>    <xsl:value-of select="yahoo:Phone"/></div>
    <div><b>Rating: </b>   <xsl:value-of select="yahoo:Rating"/></div>
    <div><b>Distance: </b> <xsl:value-of select="yahoo:Distance"/></div>
  </xsl:template>
  <xsl:template match="text()|@*"/>
</xsl:stylesheet>
