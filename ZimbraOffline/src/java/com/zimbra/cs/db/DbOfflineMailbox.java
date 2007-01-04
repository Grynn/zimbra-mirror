/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * The Original Code is: Zimbra Network
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.db.DbPool.Connection;
import com.zimbra.cs.localconfig.DebugConfig;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OfflineMailbox;
import com.zimbra.cs.mailbox.Tag;

public class DbOfflineMailbox {

    public static void renumberItem(MailItem item, int newId, int mod_content) throws ServiceException {
        if (Db.Capability.ON_UPDATE_CASCADE)
            renumberItemCascade(item, newId, mod_content);
        else
            renumberItemManual(item, newId, mod_content);
    }

    public static void renumberItemManual(MailItem item, int newId, int mod_content) throws ServiceException {
        Mailbox mbox = item.getMailbox();
        Connection conn = mbox.getOperationConnection();
        byte type = item.getType();

        PreparedStatement stmt = null;
        try {
            // first, duplicate the original row with the new ID
            String table = DbMailItem.getMailItemTableName(mbox);
            stmt = conn.prepareStatement("INSERT INTO " + table +
                    "(" + (!DebugConfig.disableMailboxGroup ? "mailbox_id, " : "") +
                    " id, type, parent_id, folder_id, index_id, imap_id, date, size, volume_id, blob_digest," +
                    " unread, flags, tags, sender, subject, name, metadata, mod_metadata, change_date, mod_content, change_mask) " +
                    "(SELECT " + (!DebugConfig.disableMailboxGroup ? "mailbox_id, " : "") +
                    " ?, type, parent_id, folder_id, index_id, imap_id, date, size, volume_id, blob_digest," +
                    " unread, flags, tags, sender, subject, name, metadata, mod_metadata, change_date, ?, change_mask" +
                    " FROM " + table + " WHERE " + DbMailItem.IN_THIS_MAILBOX_AND + "id = ?)");
            int pos = 1;
            stmt.setInt(pos++, newId);
            stmt.setInt(pos++, mod_content);
            if (!DebugConfig.disableMailboxGroup)
                stmt.setInt(pos++, mbox.getId());
            stmt.setInt(pos++, item.getId());
            stmt.executeUpdate();
            stmt.close();

            // then update all the dependent rows (foreign keys)
            if (type == MailItem.TYPE_MESSAGE || type == MailItem.TYPE_CHAT || type == MailItem.TYPE_CONVERSATION) {
                // update OPEN_CONVERSATION.CONV_ID
                stmt = conn.prepareStatement("UPDATE " + DbMailItem.getConversationTableName(mbox) +
                        " SET conv_id = ?" +
                        " WHERE " + DbMailItem.IN_THIS_MAILBOX_AND + "conv_id = ?");
                pos = 1;
                stmt.setInt(pos++, newId);
                if (!DebugConfig.disableMailboxGroup)
                    stmt.setInt(pos++, mbox.getId());
                stmt.setInt(pos++, item.getId());
                stmt.executeUpdate();
                stmt.close();
            }

            if (type == MailItem.TYPE_APPOINTMENT) {
                // update APPOINTMENT.ITEM_ID
                stmt = conn.prepareStatement("UPDATE " + DbMailItem.getCalendarItemTableName(mbox) +
                        " SET item_id = ?" +
                        " WHERE " + DbMailItem.IN_THIS_MAILBOX_AND + "item_id = ?");
                pos = 1;
                stmt.setInt(pos++, newId);
                if (!DebugConfig.disableMailboxGroup)
                    stmt.setInt(pos++, mbox.getId());
                stmt.setInt(pos++, item.getId());
                stmt.executeUpdate();
                stmt.close();
            }

            if (type == MailItem.TYPE_FOLDER || type == MailItem.TYPE_SEARCHFOLDER) {
                // update MAIL_ITEM.FOLDER_ID
                stmt = conn.prepareStatement("UPDATE " + DbMailItem.getMailItemTableName(mbox) +
                        " SET folder_id = ?" +
                        " WHERE " + DbMailItem.IN_THIS_MAILBOX_AND + "folder_id = ?");
                pos = 1;
                stmt.setInt(pos++, newId);
                if (!DebugConfig.disableMailboxGroup)
                    stmt.setInt(pos++, mbox.getId());
                stmt.setInt(pos++, item.getId());
                stmt.executeUpdate();
                stmt.close();
            }

            if (type == MailItem.TYPE_FOLDER || type == MailItem.TYPE_SEARCHFOLDER || type == MailItem.TYPE_CONVERSATION) {
                // update MAIL_ITEM.PARENT_ID
                stmt = conn.prepareStatement("UPDATE " + DbMailItem.getMailItemTableName(mbox) +
                        " SET parent_id = ?" +
                        " WHERE " + DbMailItem.IN_THIS_MAILBOX_AND + "parent_id = ?");
                pos = 1;
                stmt.setInt(pos++, newId);
                if (!DebugConfig.disableMailboxGroup)
                    stmt.setInt(pos++, mbox.getId());
                stmt.setInt(pos++, item.getId());
                stmt.executeUpdate();
                stmt.close();
            }

            // now we can delete the original row with no foreign key conflicts
            stmt = conn.prepareStatement("DELETE FROM " + DbMailItem.getMailItemTableName(mbox) +
                    " WHERE " + DbMailItem.IN_THIS_MAILBOX_AND + "id = ?");
            pos = 1;
            if (!DebugConfig.disableMailboxGroup)
                stmt.setInt(pos++, mbox.getId());
            stmt.setInt(pos++, item.getId());
            stmt.executeUpdate();
            stmt.close();

            if (type == MailItem.TYPE_TAG)
                updateTagBitmask(conn, (Tag) item, newId);

        } catch (SQLException e) {
            throw ServiceException.FAILURE("renumbering " + MailItem.getNameForType(type) + " (" + item.getId() + " => " + newId + ")", e);
        } finally {
            DbPool.closeStatement(stmt);
        }
    }

    public static void renumberItemCascade(MailItem item, int newId, int mod_content) throws ServiceException {
        Mailbox mbox = item.getMailbox();
        Connection conn = mbox.getOperationConnection();
        byte type = item.getType();

        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE " + DbMailItem.getMailItemTableName(item) +
                    " SET id = ?, mod_content = ?" +
                    " WHERE " + DbMailItem.IN_THIS_MAILBOX_AND + "id = ?");
            int pos = 1;
            stmt.setInt(pos++, newId);
            stmt.setInt(pos++, mod_content);
            if (!DebugConfig.disableMailboxGroup)
                stmt.setInt(pos++, mbox.getId());
            stmt.setInt(pos++, item.getId());
            stmt.executeUpdate();
            stmt.close();

            if (type == MailItem.TYPE_FOLDER || type == MailItem.TYPE_SEARCHFOLDER) {
                // update MAIL_ITEM.FOLDER_ID
                stmt = conn.prepareStatement("UPDATE " + DbMailItem.getMailItemTableName(mbox) +
                        " SET folder_id = ?" +
                        " WHERE " + DbMailItem.IN_THIS_MAILBOX_AND + "folder_id = ?");
                pos = 1;
                stmt.setInt(pos++, newId);
                if (!DebugConfig.disableMailboxGroup)
                    stmt.setInt(pos++, mbox.getId());
                stmt.setInt(pos++, item.getId());
                stmt.executeUpdate();
                stmt.close();
            }

            if (type == MailItem.TYPE_FOLDER || type == MailItem.TYPE_SEARCHFOLDER || type == MailItem.TYPE_CONVERSATION) {
                // update MAIL_ITEM.PARENT_ID
                stmt = conn.prepareStatement("UPDATE " + DbMailItem.getMailItemTableName(mbox) +
                        " SET parent_id = ?" +
                        " WHERE " + DbMailItem.IN_THIS_MAILBOX_AND + "parent_id = ?");
                pos = 1;
                stmt.setInt(pos++, newId);
                if (!DebugConfig.disableMailboxGroup)
                    stmt.setInt(pos++, mbox.getId());
                stmt.setInt(pos++, item.getId());
                stmt.executeUpdate();
                stmt.close();
            }

            if (type == MailItem.TYPE_TAG)
                updateTagBitmask(conn, (Tag) item, newId);

        } catch (SQLException e) {
            throw ServiceException.FAILURE("renumbering " + MailItem.getNameForType(type) + " (" + item.getId() + " => " + newId + ")", e);
        } finally {
            DbPool.closeStatement(stmt);
        }
    }

    // handle reworking tag bitmasks for other mail items
    private static void updateTagBitmask(Connection conn, Tag tag, int newId) throws SQLException, ServiceException {
        Mailbox mbox = tag.getMailbox();
        long newMask = 1L << Tag.getIndex(newId);

        PreparedStatement stmt = null;
        try {
            if (Db.Capability.BITWISE_OPERATIONS) {
                stmt = conn.prepareStatement("UPDATE " + DbMailItem.getMailItemTableName(tag) +
                        " SET tags = (tags & ?) | ?" +
                        " WHERE " + DbMailItem.IN_THIS_MAILBOX_AND + "tags & ?");
                int pos = 1;
                stmt.setLong(pos++, ~tag.getBitmask());
                stmt.setLong(pos++, newMask);
                if (!DebugConfig.disableMailboxGroup)
                    stmt.setInt(pos++, mbox.getId());
                stmt.setLong(pos++, tag.getBitmask());
                stmt.executeUpdate();
            } else {
                // first, add the new mask
                stmt = conn.prepareStatement("UPDATE " + DbMailItem.getMailItemTableName(tag) +
                        " SET tags = tags + ?" +
                        " WHERE " + DbMailItem.IN_THIS_MAILBOX_AND + Db.bitmaskAND("tags") + " AND NOT " + Db.bitmaskAND("tags"));
                int pos = 1;
                stmt.setLong(pos++, newMask);
                if (!DebugConfig.disableMailboxGroup)
                    stmt.setInt(pos++, mbox.getId());
                stmt.setLong(pos++, tag.getBitmask());
                stmt.setLong(pos++, newMask);
                stmt.executeUpdate();
                stmt.close();

                // then, remove the old mask
                stmt = conn.prepareStatement("UPDATE " + DbMailItem.getMailItemTableName(tag) +
                        " SET tags = tags - ?" +
                        " WHERE " + DbMailItem.IN_THIS_MAILBOX_AND + Db.bitmaskAND("tags"));
                pos = 1;
                stmt.setLong(pos++, tag.getBitmask());
                if (!DebugConfig.disableMailboxGroup)
                    stmt.setInt(pos++, mbox.getId());
                stmt.setLong(pos++, tag.getBitmask());
                stmt.executeUpdate();
            }
        } finally {
            DbPool.closeStatement(stmt);
        }
    }

    public static void setChangeIds(MailItem item, int date, int mod_content, int change_date, int mod_metadata) throws ServiceException {
        Mailbox mbox = item.getMailbox();
        Connection conn = mbox.getOperationConnection();

        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE " + DbMailItem.getMailItemTableName(item) +
                    " SET date = ?, mod_content = ?, change_date = ?, mod_metadata = ?" +
                    " WHERE " + DbMailItem.IN_THIS_MAILBOX_AND + "id = ?");
            int pos = 1;
            stmt.setInt(pos++, date);
            stmt.setInt(pos++, mod_content);
            stmt.setInt(pos++, change_date);
            stmt.setInt(pos++, mod_metadata);
            if (!DebugConfig.disableMailboxGroup)
                stmt.setInt(pos++, mbox.getId());
            stmt.setInt(pos++, item.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw ServiceException.FAILURE("setting content change IDs for item " + item.getId(), e);
        } finally {
            DbPool.closeStatement(stmt);
        }
    }

    public static MailItem.TypedIdList getChangedItems(OfflineMailbox ombx) throws ServiceException {
        Connection conn = ombx.getOperationConnection();

        MailItem.TypedIdList result = new MailItem.TypedIdList();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement("SELECT id, type" +
                    " FROM " + DbMailItem.getMailItemTableName(ombx) +
                    " WHERE " + DbMailItem.IN_THIS_MAILBOX_AND + "change_mask IS NOT NULL");
            int pos = 1;
            if (!DebugConfig.disableMailboxGroup)
                stmt.setInt(pos++, ombx.getId());

            rs = stmt.executeQuery();
            while (rs.next())
                result.add(rs.getByte(2), rs.getInt(1));
            return result;
        } catch (SQLException e) {
            throw ServiceException.FAILURE("getting changed item ids for ombx " + ombx.getId(), e);
        } finally {
            DbPool.closeResults(rs);
            DbPool.closeStatement(stmt);
        }
    }

    public static int getChangeMask(MailItem item) throws ServiceException {
        Mailbox mbox = item.getMailbox();
        Connection conn = mbox.getOperationConnection();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement("SELECT change_mask" +
                    " FROM " + DbMailItem.getMailItemTableName(mbox) +
                    " WHERE " + DbMailItem.IN_THIS_MAILBOX_AND + "id = ?");
            int pos = 1;
            if (!DebugConfig.disableMailboxGroup)
                stmt.setInt(pos++, mbox.getId());
            stmt.setInt(pos++, item.getId());

            rs = stmt.executeQuery();
            if (!rs.next())
                throw MailItem.noSuchItem(item.getId(), item.getType());
            return rs.getInt(1);
        } catch (SQLException e) {
            throw ServiceException.FAILURE("getting change record for item " + item.getId(), e);
        } finally {
            DbPool.closeResults(rs);
            DbPool.closeStatement(stmt);
        }
    }

    public static void setChangeMask(MailItem item, int mask) throws ServiceException {
        Mailbox mbox = item.getMailbox();
        Connection conn = mbox.getOperationConnection();

        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE " + DbMailItem.getMailItemTableName(mbox) +
                    " SET change_mask = ?" +
                    " WHERE " + DbMailItem.IN_THIS_MAILBOX_AND + "id = ?");
            int pos = 1;
            if (mask == 0)
                stmt.setNull(pos++, Types.INTEGER);
            else
                stmt.setInt(pos++, mask);
            if (!DebugConfig.disableMailboxGroup)
                stmt.setInt(pos++, mbox.getId());
            stmt.setInt(pos++, item.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw ServiceException.FAILURE("setting change bitmask for item " + item.getId(), e);
        } finally {
            DbPool.closeStatement(stmt);
        }
    }

    public static void updateChangeRecord(MailItem item, int mask) throws ServiceException {
        Mailbox mbox = item.getMailbox();
        Connection conn = mbox.getOperationConnection();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            if (!Db.Capability.BITWISE_OPERATIONS) {
                stmt = conn.prepareStatement("SELECT change_mask FROM " + DbMailItem.getMailItemTableName(item) +
                        " WHERE " + DbMailItem.IN_THIS_MAILBOX_AND + "id = ?");
                int pos = 1;
                if (!DebugConfig.disableMailboxGroup)
                    stmt.setInt(pos++, mbox.getId());
                stmt.setInt(pos++, item.getId());
                rs = stmt.executeQuery();
                if (rs.next())
                    mask |= rs.getInt(1);
                rs.close();
                stmt.close();
            }

            String newMask = (Db.Capability.BITWISE_OPERATIONS ? "CASE WHEN change_mask IS NULL THEN ? ELSE change_mask | ? END" : "?");
            stmt = conn.prepareStatement("UPDATE " + DbMailItem.getMailItemTableName(item) +
                    " SET change_mask = " + newMask +
                    " WHERE " + DbMailItem.IN_THIS_MAILBOX_AND + "id = ?");
            int pos = 1;
            stmt.setInt(pos++, mask);
            if (Db.Capability.BITWISE_OPERATIONS)
                stmt.setInt(pos++, mask);
            if (!DebugConfig.disableMailboxGroup)
                stmt.setInt(pos++, mbox.getId());
            stmt.setInt(pos++, item.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw ServiceException.FAILURE("setting change record for item " + item.getId(), e);
        } finally {
            DbPool.closeStatement(stmt);
        }
    }

    public static void clearChangeRecords(OfflineMailbox ombx, List<Integer> ids) throws ServiceException {
        Connection conn = ombx.getOperationConnection();

        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE " + DbMailItem.getMailItemTableName(ombx) +
                    " SET change_mask = NULL" +
                    " WHERE " + DbMailItem.IN_THIS_MAILBOX_AND + "change IS NOT NULL");
            int pos = 1;
            if (!DebugConfig.disableMailboxGroup)
                stmt.setInt(pos++, ombx.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw ServiceException.FAILURE("clearing change records for items " + ids, e);
        } finally {
            DbPool.closeStatement(stmt);
        }
    }

    public static boolean isTombstone(OfflineMailbox ombx, int id, byte type) throws ServiceException {
        Connection conn = ombx.getOperationConnection();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            // FIXME: oh, this is not pretty
            stmt = conn.prepareStatement("SELECT sequence FROM " + DbMailItem.getTombstoneTableName(ombx) +
                    " WHERE " + DbMailItem.IN_THIS_MAILBOX_AND + "type = ? AND (ids = ? OR ids LIKE ? OR ids LIKE ? OR ids LIKE ?)");
            int pos = 1;
            if (!DebugConfig.disableMailboxGroup)
                stmt.setInt(pos++, ombx.getId());
            stmt.setByte(pos++, type);
            stmt.setString(pos++, "" + id);
            stmt.setString(pos++, "%," + id);
            stmt.setString(pos++, id + ",%");
            stmt.setString(pos++, "%," + id + ",%");
            rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw ServiceException.FAILURE("checking TOMBSTONE table for " + MailItem.getNameForType(type) + " " + id, e);
        } finally {
            DbPool.closeResults(rs);
            DbPool.closeStatement(stmt);
        }
    }

    public static void clearTombstones(OfflineMailbox ombx, int token) throws ServiceException {
        Connection conn = ombx.getOperationConnection();

        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("DELETE FROM " + DbMailItem.getTombstoneTableName(ombx) +
                    " WHERE " + DbMailItem.IN_THIS_MAILBOX_AND + "sequence <= ?");
            int pos = 1;
            if (!DebugConfig.disableMailboxGroup)
                stmt.setInt(pos++, ombx.getId());
            stmt.setInt(pos++, token);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw ServiceException.FAILURE("clearing tombstones up to change " + token, e);
        } finally {
            DbPool.closeStatement(stmt);
        }
    }
}
