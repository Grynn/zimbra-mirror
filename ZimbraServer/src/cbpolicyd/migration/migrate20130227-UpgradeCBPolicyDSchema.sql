BEGIN TRANSACTION;
DROP TABLE tmp_session_tracking;
ALTER TABLE session_tracking RENAME TO tmp_session_tracking;
CREATE TABLE session_tracking(
    Instance        VARCHAR(255),
    QueueID         VARCHAR(255),

    UnixTimestamp       BIGINT NOT NULL,

    ClientAddress       VARCHAR(64),
    ClientName      VARCHAR(255),
    ClientReverseName   VARCHAR(255),

    Protocol        VARCHAR(255),

    EncryptionProtocol  VARCHAR(255),
    EncryptionCipher    VARCHAR(255),
    EncryptionKeySize   VARCHAR(255),

    SASLMethod      VARCHAR(255),
    SASLSender      VARCHAR(255),
    SASLUsername        VARCHAR(255),

    Helo            VARCHAR(255),

    Sender          VARCHAR(255),

    Size            INT8,

    RecipientData       TEXT,  /* Policy state information */

    UNIQUE (Instance)
);
INSERT INTO session_tracking(Instance, QueueID, UnixTimestamp, ClientAddress,
ClientName, ClientReverseName, Protocol, EncryptionProtocol, EncryptionCipher,
EncryptionKeySize, SASLMethod, SASLSender, SASLUsername, Helo, Sender, Size,
RecipientData)
SELECT Instance, QueueID, Timestamp, ClientAddress, ClientName,
ClientReverseName, Protocol, EncryptionProtocol, EncryptionCipher,
EncryptionKeySize, SASLMethod, SASLSender, SASLUsername, Helo, Sender, Size,
RecipientData
FROM tmp_session_tracking;
DROP TABLE tmp_session_tracking;
COMMIT;
