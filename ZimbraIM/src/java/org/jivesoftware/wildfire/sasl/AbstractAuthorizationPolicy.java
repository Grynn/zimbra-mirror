package org.jivesoftware.wildfire.sasl;

/**
 * Provider for authorization policies. Policy decisions are
 * not based on any storage or specific options.  They are 
 * ment to be broad sweeping policies, and are often implemented
 * with a simple pattern matching algorithm. For a large 
 * majority of sites, a policy will be all that is required.
 *
 * Users that wish to integrate with their own authorization 
 * system must extend this class and implement the 
 * AuthorizationProvider interface then register the class
 * with Wildfire in the <tt>wildfire.xml</tt> file. An entry 
 * in that file would look like the following:
 *
 * <pre>
 *   &lt;provider&gt;
 *     &lt;authorizationpolicy&gt;
 *       &lt;classlist&gt;com.foo.auth.CustomPolicyProvider&lt;/classlist&gt;
 *     &lt;/authorizationpolicy&gt;
 *   &lt;/provider&gt;</pre>
 *
 * @author Jay Kline
 */
public abstract class AbstractAuthorizationPolicy implements AuthorizationProvider {

    /**
     * Returns true if the principal is explicity authorized to the JID
     *
     * @param username The username requested.
     * @param principal The principal requesting the username.
     * @return true is the user is authorized to be principal
     */
    public abstract boolean authorize(String username, String principal);

    /**
     * Returns the short name of the Policy
     *
     * @return The short name of the Policy
     */
    public abstract String name();

    /**
     * Returns a description of the Policy
     *
     * @return The description of the Policy.
     */
    public abstract String description();

}