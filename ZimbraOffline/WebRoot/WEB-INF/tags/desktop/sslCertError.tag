<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>

<a href="javascript:zd.toggle('certInfo')"><fmt:message key='CertDetails'/></a>

<div id="certInfo" style="display:none">
<p>
<table cellpadding="0" cellspacing="0" width="100%">
    <tr><td class="ZFieldLabel"><fmt:message key='CertIssuedTo'/></td><td></td></tr>
    <tr><td class="ZFieldSubLabel"><fmt:message key='CertCommonName'/></td><td class="ZFieldSubLabelLeft">${bean.sslCertInfo.commonName}</td></tr>
    <tr><td class="ZFieldSubLabel"><fmt:message key='CertOrganizationUnit'/></td><td class="ZFieldSubLabelLeft">${bean.sslCertInfo.organizationUnit}</td></tr>
    <tr><td class="ZFieldSubLabel"><fmt:message key='CertOrganization'/></td><td class="ZFieldSubLabelLeft">${bean.sslCertInfo.organization}</td></tr>
    <tr><td class="ZFieldSubLabel"><fmt:message key='CertSerialNumber'/></td><td class="ZFieldSubLabelLeft">${bean.sslCertInfo.serialNumber}</td></tr>

    <tr><td class="ZFieldLabel"><fmt:message key='CertIssuedBy'/></td><td></td></tr>
    <tr><td class="ZFieldSubLabel"><fmt:message key='CertIssuerCommonName'/></td><td class="ZFieldSubLabelLeft">${bean.sslCertInfo.issuerCommonName}</td></tr>
    <tr><td class="ZFieldSubLabel"><fmt:message key='CertIssuerOrganizationUnit'/></td><td class="ZFieldSubLabelLeft">${bean.sslCertInfo.issuerOrganizationUnit}</td></tr>
    <tr><td class="ZFieldSubLabel"><fmt:message key='CertIssuerOrganization'/></td><td class="ZFieldSubLabelLeft">${bean.sslCertInfo.issuerOrganization}</td></tr>
    
    <tr><td class="ZFieldLabel"><fmt:message key='CertValidity'/></td><td></td></tr>
    <tr><td class="ZFieldSubLabel"><fmt:message key='CertIssuedOn'/></td><td class="ZFieldSubLabelLeft">${bean.sslCertInfo.issuedOn}</td></tr>
    <tr><td class="ZFieldSubLabel"><fmt:message key='CertExpiresOn'/></td><td class="ZFieldSubLabelLeft">${bean.sslCertInfo.expiresOn}</td></tr>
    
    <tr><td class="ZFieldLabel"><fmt:message key='CertFingerPrints'/></td><td></td></tr>
    <tr><td class="ZFieldSubLabel"><fmt:message key='CertSHA1'/></td><td class="ZFieldSubLabelLeft">${bean.sslCertInfo.sha1}</td></tr>
    <tr><td class="ZFieldSubLabel"><fmt:message key='CertMD5'/></td><td class="ZFieldSubLabelLeft">${bean.sslCertInfo.md5}</td></tr>
</table>
</p>
</div>