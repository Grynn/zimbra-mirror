<%@ tag body-content="empty" %>
<%@ attribute name="post_url" required="true" %>

<form method="post" action="${post_url}">

<input type="text" name="foo" value="${myBean.foo}">
<input type="text" name="gender" value="${myBean.gender}">
<input type="submit" value="Submit">

</form>