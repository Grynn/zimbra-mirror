package org.jivesoftware.wildfire.vcard;

/**
 * Interface to listen for vCard changes. Use the
 * {@link org.jivesoftware.wildfire.vcard.VCardManager#addListener(VCardListener)}
 * method to register for events.
 *
 * @author Remko Tron&ccedil;on
 */
public interface VCardListener {
    /**
     * A vCard was created.
     *
     * @param user the user for which the vCard was created.
     */
    public void vCardCreated(String user);

    /**
     * A vCard was updated.
     *
     * @param user the user for which the vCard was updated.
     */
    public void vCardUpdated(String user);

    /**
     * A vCard was deleted.
     *
     * @param user the user for which the vCard was deleted.
     */
    public void vCardDeleted(String user);
}
