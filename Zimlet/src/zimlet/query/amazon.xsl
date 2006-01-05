<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:amazon="http://webservices.amazon.com/AWSECommerceService/2004-11-10">
  <xsl:output method="html"/>
  <xsl:template match="amazon:ItemSearchResponse">
    <html>
      <xsl:apply-templates select="amazon:Items/amazon:Item"/>
    </html>
  </xsl:template>
  <xsl:template match="amazon:Item">
    <div><b>ASIN: </b>    <xsl:value-of select="amazon:ASIN"/></div>
    <div><b>URL: </b>     <xsl:value-of select="amazon:DetailPageURL"/></div>
    <xsl:apply-templates select="amazon:ItemAttributes"/>
  </xsl:template>
  <xsl:template match="amazon:ItemAttributes">
    <xsl:apply-templates select="amazon:Title|amazon:Author|amazon:Artist|amazon:ISBN|amazon:Model|amazon:Publisher"/>
  </xsl:template>
  <xsl:template match="amazon:Title">
    <div><b>Title: </b><xsl:value-of select="text()"/></div>
  </xsl:template>
  <xsl:template match="amazon:Author">
    <div><b>Author: </b><xsl:value-of select="text()"/></div>
  </xsl:template>
  <xsl:template match="amazon:Artist">
    <div><b>Artist: </b><xsl:value-of select="text()"/></div>
  </xsl:template>
  <xsl:template match="amazon:ISBN">
    <div><b>ISBN: </b><xsl:value-of select="text()"/></div>
  </xsl:template>
  <xsl:template match="amazon:Model">
    <div><b>Model: </b><xsl:value-of select="text()"/></div>
  </xsl:template>
  <xsl:template match="amazon:Publisher">
    <div><b>Publisher: </b><xsl:value-of select="text()"/></div>
  </xsl:template>
</xsl:stylesheet>
