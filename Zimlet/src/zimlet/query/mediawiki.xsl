<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml">
  <xsl:output method="html"/>
  <xsl:template match='xhtml:h1[@class="firstHeading"]'>
    <div><b>Title: </b><xsl:value-of select="text()"/></div>
  </xsl:template>
  <xsl:template match='id("bodyContent")'>
    <xsl:for-each select="*">
      <xsl:if test='local-name() != "div" and local-name() != "table"'>
        <p>
          <xsl:apply-templates mode="doit"/>
        </p>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="*|/" mode="doit">
    <xsl:if test='local-name() != "script"'>
      <xsl:copy>
        <xsl:apply-templates mode="doit"/>
      </xsl:copy>
    </xsl:if>
  </xsl:template>
  <xsl:template match="text()|@*"/>
</xsl:stylesheet>
