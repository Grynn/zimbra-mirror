$global:zreq.ApiRequest = new-object Zimbra.Client.Mail.GetFolderRequest
$global:zres = $global:zdisp.SendRequest( $global:zreq )

echo $global:zres