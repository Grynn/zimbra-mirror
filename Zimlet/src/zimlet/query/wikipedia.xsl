<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml">
  <xsl:output method="html"/>
  <xsl:template match='xhtml:h1[@class="firstHeading"]'>
    <div><b>Title: </b><xsl:value-of select="text()"/></div>
  </xsl:template>
  <xsl:template match='id("bodyContent")'>
    <div><b>Content: </b></div>
    <xsl:for-each select="xhtml:p">
      <xsl:copy>
        <xsl:apply-templates select="@*|node()" mode="doit"/>
      </xsl:copy>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="*|/" mode="doit">
    <xsl:if test='local-name() != "script"'>
      <xsl:apply-templates mode="doit"/>
    </xsl:if>
  </xsl:template>
  <xsl:template match="text()|@*"/>
</xsl:stylesheet>
