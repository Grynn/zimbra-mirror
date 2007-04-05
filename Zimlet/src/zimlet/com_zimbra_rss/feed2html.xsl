<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0'>
    <xsl:param name="limit" />

    <xsl:template match='/'>
        <div class='Feed'><xsl:apply-templates /></div>
    </xsl:template>

    <!-- RSS -->
    <xsl:template match='rss/channel[1]'>
        <div class='FeedTitle'>
            <a target='_new' href='{link}'><xsl:value-of select="title" /></a>
        </div>
        <div class='FeedDate'>
            <xsl:value-of select="lastBuildDate" />
        </div>
        <div class='FeedItems'>
            <xsl:choose>
                <xsl:when test="not($limit='')">
                    <xsl:apply-templates select="item[position() &lt;= number($limit)]" />
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select='item' />
                </xsl:otherwise>
            </xsl:choose>
        </div>
    </xsl:template>

    <xsl:template match='item'>
        <xsl:variable name="num" select="(count(preceding-sibling::item) mod 2) + 1" />
        <div class='FeedItem Line{$num}'>
            <div class='FeedTitle'>
                <a target='_new' href='{link}'><xsl:value-of select="title" /></a>
            </div>
            <div class='FeedDesc'>
                <xsl:value-of select="description" />
            </div>
        </div>
    </xsl:template>

    <!-- Atom -->
    <xsl:template match='feed'>
        <div class='FeedTitle'>
            <a target='_new' href='{link/@href}'><xsl:value-of select="title" /></a>
        </div>
        <div class='FeedDate'>
            <xsl:call-template name='formatDate'>
                <xsl:with-param name="s" select='modified' />
            </xsl:call-template>
        </div>
        <div class='FeedItems'>
            <xsl:choose>
                <xsl:when test="not($limit='')">
                    <xsl:apply-templates select="entry[position() &lt;= number($limit)]" />
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select='entry' />
                </xsl:otherwise>
            </xsl:choose>
        </div>
    </xsl:template>

    <xsl:template match='entry'>
        <xsl:variable name="num" select="(count(preceding-sibling::entry) mod 2) + 1" />
        <div class='FeedItem FeedLine{$num}'>
            <div class='FeedTitle'>
                <a target='_new' href='{link/@href}'><xsl:value-of select="title" /></a>
            </div>
            <div class='FeedDesc'>
                <xsl:value-of select="content" />
            </div>
        </div>
    </xsl:template>

    <xsl:template name='formatDate'>
        <xsl:param name="s" />
    </xsl:template>

</xsl:stylesheet>