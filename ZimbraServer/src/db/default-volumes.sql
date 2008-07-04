-- 
-- ***** BEGIN LICENSE BLOCK *****
-- 
-- Zimbra Collaboration Suite Server
-- Copyright (C) 2004, 2005, 2006, 2007 Zimbra, Inc.
-- 
-- The contents of this file are subject to the Yahoo! Public License
-- Version 1.0 ("License"); you may not use this file except in
-- compliance with the License.  You may obtain a copy of the License at
-- http://www.zimbra.com/license.
-- 
-- Software distributed under the License is distributed on an "AS IS"
-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
-- 
-- ***** END LICENSE BLOCK *****
-- 

INSERT INTO volume (id, type, name, path, file_bits, file_group_bits,
    mailbox_bits, mailbox_group_bits, compress_blobs, compression_threshold)
  VALUES (1, 1, 'message1', '/opt/zimbra/store', 12, 8, 12, 8, 0, 4096);
INSERT INTO volume (id, type, name, path, file_bits, file_group_bits,
    mailbox_bits, mailbox_group_bits, compress_blobs, compression_threshold)
  VALUES (2, 10, 'index1', '/opt/zimbra/index', 12, 8, 12, 8, 0, 4096);

INSERT INTO current_volumes (message_volume_id, index_volume_id, next_mailbox_id) VALUES (1, 2, 1);
