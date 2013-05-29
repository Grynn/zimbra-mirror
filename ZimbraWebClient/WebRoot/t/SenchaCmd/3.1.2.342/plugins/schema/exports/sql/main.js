/*
 * Copyright (c) 2012. Sencha Inc.
 * 
 * This file contains the SQL exporter for the Schema plugin. This export writes
 * SQL create script for the schema.
 */

//@require ../../../../js/all.js

function main (args) {
    var alpha = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
    var src = args.file.substring(args.workspaceDir.length);
    var skipRe = /^(false|0|no|off)$/i;
    var tables = [];
    var augmentedEntities = [];
    var entities = [];
    var foreignKeyTables = [];
    var indexTables = [];

    function makeAlpha (num) {
        var ret = '';
        var i = num + 1;
        var rem;

        do {
            --i; // corrects for 'A' being the 0-digit but also the 1-digit (e.g., "AA")
            rem = i % alpha.length;
            ret = alpha.charAt(rem) + ret;
            i = (i - rem) / alpha.length;
        } while (i);

        return ret;
    }
//    console.error('makeAlpha(0): ' + makeAlpha(0));
//    for (var x = 0; x < 30*26; x += 26) {
//        var y = [];
//        while (y.length < 26) {
//            y.push(makeAlpha(x + y.length));
//        }
//        console.error('makeAlpha: ' + y.join(','));
//    }

    toJS(args.db.tables).forEach(function (table) {
        var opt = table.getOption('generate.sql') + '';
        var aux;

        if (skipRe.test(opt)) {
            console.info('Skip table: ' + table.name + ' (generate.sql = '+opt+')');
        } else {
            var indexes = table.getIndexes();
            var fkeys = table.getForeignKeys();

            console.debug('Include table: ' + table.name + 
                ' (indexes: ' + indexes.size() + ', foreignKeys: ' + fkeys.size() + ')');

            tables.push(table);

            if (!indexes.isEmpty()) {
                indexTables.push(table);
            }
            if (!fkeys.isEmpty()) {
                foreignKeyTables.push(table);
            }

            if (table.isEntity()) {
                entities.push(table);

                if (table.augmented) {
                    //console.info('Entity ' + table.name + ' is augmented');
                    augmentedEntities.push(aux = {
                        entity: table,
                        joins: []
                    });

                    toJS(table.columns).forEach(function (col) {
                        //console.info('Column ' + col.name);
                        if (col.reference) {
                            //console.info(' => ' + col.reference.targetName);
                            var join;

                            toJS(col.reference.mappings).forEach(function (map) {
                                if (!join) {
                                    aux.joins.push(join = {
                                        on: col.name,
                                        pk: col.reference.target.primaryKey,
                                        to: col.reference.target,
                                        key: makeAlpha(aux.joins.length+1),
                                        mappings: []
                                    });
                                }

                                join.mappings.push(map);
                            });
                        }
                    });
                }
            }
        }
    });

    generateTpl({
        file: joinPath(args.exporterDir, args.dbType + '.tpl'),
        tofile: args.out,

        params: {
            db: args.db,
            file: args.file,
            augmentedEntities: augmentedEntities,
            entities: entities,
            foreignKeyTables: foreignKeyTables,
            indexTables: indexTables,
            src: src,
            tables: tables
        }
    });
}
