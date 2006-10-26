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

import com.zimbra.cs.db.DbPool.Connection;
import com.zimbra.cs.localconfig.DebugConfig;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OfflineMailbox;
import com.zimbra.cs.mailbox.Tag;
import com.zimbra.cs.service.ServiceException;

public class DbOfflineMailbox {

    public static void renumberItem(MailItem item, int newId, int mod_content) throws ServiceException {
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

            // leaf nodes are easy cases -- that's all we need to do
            if (type == MailItem.TYPE_MESSAGE || type == MailItem.TYPE_CONTACT)
                return;

            if (type == MailItem.TYPE_TAG) {
                // handle reworking tag bitmasks for other mail items
                Tag tag = (Tag) item;
                stmt = conn.prepareStatement("UPDATE " + DbMailItem.getMailItemTableName(item) +
                        " SET tags = (tags & ?) | ?" +
                        " WHERE " + DbMailItem.IN_THIS_MAILBOX_AND + "folder_id = ?");
                pos = 1;
                stmt.setLong(pos++, ~tag.getBitmask());
                stmt.setLong(pos++, 1L << Tag.getIndex(newId));
                if (!DebugConfig.disableMailboxGroup)
                    stmt.setInt(pos++, mbox.getId());
                stmt.setInt(pos++, item.getId());
                stmt.executeUpdate();
                stmt.close();

                return;
            }

            // if we're here, it's a folder
            stmt = conn.prepareStatement("UPDATE " + DbMailItem.getMailItemTableName(item) +
                    " SET folder_id = ?" +
                    " WHERE " + DbMailItem.IN_THIS_MAILBOX_AND + "folder_id = ?");
            pos = 1;
            stmt.setInt(pos++, newId);
            if (!DebugConfig.disableMailboxGroup)
                stmt.setInt(pos++, mbox.getId());
            stmt.setInt(pos++, item.getId());
            stmt.executeUpdate();
            stmt.close();

            stmt = conn.prepareStatement("UPDATE " + DbMailItem.getMailItemTableName(item) +
                    " SET parent_id = ?" +
                    " WHERE " + DbMailItem.IN_THIS_MAILBOX_AND + "parent_id = ?");
            pos = 1;
            stmt.setInt(pos++, newId);
            if (!DebugConfig.disableMailboxGroup)
                stmt.setInt(pos++, mbox.getId());
            stmt.setInt(pos++, item.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw ServiceException.FAILURE("renumbering " + MailItem.getNameForType(type) + " (" + item.getId() + " => " + newId + ")", e);
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
        try {
            stmt = conn.prepareStatement("UPDATE " + DbMailItem.getMailItemTableName(item) +
                    " SET change_mask = IF(change_mask IS NULL, ?, change_mask | ?)" +
                    " WHERE " + DbMailItem.IN_THIS_MAILBOX_AND + "id = ?");
            int pos = 1;
            stmt.setInt(pos++, mask);
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
