/*
    This fine was generated from {src}
    Target database: MS SQL Server

    DO NOT EDIT - all edits will be lost when this file is regenerated.
*/

/*=============================================================================
 *                                  Tables
 *===========================================================================*/
<tpl for="tables">
/*-----------------------------------------------------------------------------
 * <tpl if="entity">Entity: {name}{[values['extends'] ? ' extends ' + values['extends'] : '']}<tpl else>Table: {name}</tpl>
 *---------------------------------------------------------------------------*/

CREATE TABLE {name}
(<tpl for="columns" between=",">
    {name} <tpl for="typeDef">{sqlType}{sizeAndScale}<tpl
    if="nullable"><tpl else> NOT NULL</tpl><tpl if="defaultValue"> DEFAULT {[
        values.textual ? ("('" + values.defaultValue + "')") : values.defaultValue
    ]}</tpl></tpl></tpl><tpl if="primaryKey">,
    CONSTRAINT PK_{name} PRIMARY KEY ({primaryKey})</tpl>
);
</tpl>

/*=============================================================================
 *                                  Indexes  
 *===========================================================================*/
<tpl for="indexTables">
/*-----------------------------------------------------------------------------
 * Indexes for: {name}
 *---------------------------------------------------------------------------*/
<tpl for="indexes">
BEGIN
CREATE <tpl if="unique">UNIQUE </tpl>INDEX {name} ON {tableName} ({columnsString})
END
;
</tpl></tpl>

/*=============================================================================
 *                               Foreign Keys
 *===========================================================================*/
<tpl for="foreignKeyTables">
/*-----------------------------------------------------------------------------
 * Foreign Keys for: {name}
 *---------------------------------------------------------------------------*/
<tpl for="foreignKeys">
BEGIN
ALTER TABLE {parent.name}
    ADD CONSTRAINT {name} FOREIGN KEY ({[
        toJS(values.sourceColumnNames).join(',')
    ]}) REFERENCES {targetTableName} ({[
        toJS(values.targetColumnNames).join(',')
    ]}) <tpl if="onUpdateTODO">
    ON UPDATE {onUpdateTODO}
</tpl><tpl if="onDeleteTODO">
    ON DELETE {onDeleteTODO}
</tpl>
END
;
</tpl></tpl>

/*=============================================================================
 *                                   Views
 *===========================================================================*/
<tpl for="augmentedEntities"><tpl for="entity">
/*-----------------------------------------------------------------------------
 * Augmented view for: {name}
 *---------------------------------------------------------------------------*/
CREATE VIEW {auxViewName} AS
SELECT </tpl><tpl for="joins" between=", "><tpl for="mappings" between=", ">{parent.key}.{field} AS {as}</tpl></tpl>
FROM {entity.name} A
<tpl for="joins">LEFT OUTER JOIN {to.name} {key} ON A.{on} = {key}.{pk}
</tpl>;
</tpl>
