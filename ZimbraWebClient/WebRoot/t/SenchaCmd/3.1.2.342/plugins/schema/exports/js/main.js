/*
 * Copyright (c) 2012-2013. Sencha Inc.
 * 
 * This file contains the JS exporter for the Schema plugin. This export writes
 * Ext JS / Sencha Touch model definitions for each entity in the schema.
 */

//@require ../../../../js/all.js

function getColumnType (col) {
    var type = col.typeDef.getOption('extjs.type');
    //console.info('Field ' + col.name + (type ? type : '<auto>'));
    return type;
}

function main (args) {
    var db = args.db;
    //var sencha = new com.sencha.command.Sencha();

    var namespace = args.namespace;
    if (!namespace) {
        self.fail('Must specify --namespace or set schema.namespace');
    }

    //echo('Yo ' + db.databaseType);
    var tplDir = project.getProperty('package.schema.js.tpl');
    if (!tplDir) {
        var sdkPluginDir = project.getProperty('framework.config.dir');
        //echo('framework.config.dir: ' + sdkPluginDir);
        if (!sdkPluginDir) {
            self.fail('Cannot determine Sencha SDK from current directory');
        }
        tplDir = joinPath(sdkPluginDir+'', 'templates/Model');
    }
    echo('Model template dir: ' + tplDir);

    for (var i = 0, n = db.entities.size(); i < n; ++i) {
        var entity = db.entities.get(i);
        var baseClass = toJS(entity.getExtends());
        var fields = [];
        var auxFields = [];
        baseClass = baseClass ? (namespace + '.' + baseClass) : 'Ext.data.Model';

        if ('entity' === toJS(entity.name)) {
            continue;
        }

        toJS(entity.columns).forEach(function (col) {
            var type = getColumnType(col);
            fields.push(type ? (col.name + ':' + type) : col.name + '');

            if (col.reference) {
                //console.info(' => ' + col.reference.targetName);

                toJS(col.reference.mappings).forEach(function (map) {
                    var target = map.target,
                        type = getColumnType(target);

                    //console.info('AS ' + map.as + '(' + target.name + ':' + type + ')');
                    auxFields.push(type ? (map.as + ':' + type) : map.as);
                });
            }
        });
        if (auxFields.length) {
            fields.push(auxFields.join(','));
        }
        fields = fields.join(',');

        echo('Entity: '+namespace+'.'+entity.name+' extends '+baseClass);//+ ' ('+fields+')');

//TODO - use "sencha.dispatch(['generate', 'model', ...])" for apps to pick up Architect
// but also configure in schema-plugin.disabled so that does not regenerate the XML since
// that is what we are iterating over now!
        generateTpl({
            dir: tplDir,
            todir: args.out,
            basedir: args.basedir,
            store: args.configDir + '/codegen.json',

            params: {
                //db: args.db,
                name: entity.name,
                modelFileName: entity.name,
                modelNamespace: namespace,
                baseClass: baseClass,
                fields: fields
            }
        });
    }
}
