/*
 * Copyright (c) 2012. Sencha Inc.
 */

/**
 * This class wraps access to a metadata JSON file.
 */
Ext.define('Architect.meta.File', {
    /**
     * This is set to non-null value by the constructor if the file exists.
     */
    data: null,

    constructor: function (filename) {
        if (arguments.length > 1) {
            this.filename = joinPath.apply(null, arguments);
        } else {
            this.filename = filename;
        }

        if (exists(this.filename)) {
            console.info('Loading metadata file: ' + this.filename);
            this.data = readJson(this.filename);
        }
    },

    save: function () {
        if (this.data) {
            var json = JSON.stringify(this.data, null, '    ');
            console.info('Writing metadata file: ' + this.filename);
            //console.debug('Content: ' + json);
            writeFile(this.filename, json);
        }
    }
});

/**
 * This is a base class for any entity in a Sencha Architect project that has its own
 * metadata file associated with it.
 */
Ext.define('Architect.meta.Base', {
    constructor: function (config) {
        Ext.apply(this, config);

        if (this.init) {
            this.init();
        }
    }
});

/**
 * This class manages the top-level Sencha Architect project (the ".xds" file).
 */
Ext.define('Architect.meta.App', {
    extend: 'Architect.meta.Base',

    name: null,     // = ${app.name}
    path: null,     // = ${args.path}
    appPath: null,  // = ${args.path}/app
    metaPath: null, // = ${args.path}/metadata

    init: function () {
        this.appPath = joinPath(this.path, 'app');
        this.metaPath = joinPath(this.path, 'metadata');
    },

    addModel: function (model) {
        /*
            "topInstanceFileMap": {
                "6594f56f-f790-445e-bd01-f3caf7712d1c": {
                    "paths": [
                        "metadata/model/MyModel",
                        "app/model/override/MyModel.js",
                        "app/model/MyModel.js"
                    ],
                    "className": "MyModel"
                },
         */
        this.addTopInstance(model.guid, {
            className: model.name,
            paths: [
                'metadata/model/' + model.name,
                'app/model/' + model.name + '.js'
            ]
        });
    },
    
    addTopInstance: function (guid, data) {
        var meta = new Architect.meta.File(joinPath(this.path, this.name + '.xds'));

        if (meta.data && !meta.data.topInstanceFileMap[guid]) {
            meta.data.topInstanceFileMap[guid] = data;
            meta.save();
        }
    }
});

/**
 * This class mamange the metadata for a data Model class.
 */
Ext.define('Architect.meta.Model', {
    extend: 'Architect.meta.Base',

    isNew: true,

    app:       null, // Architect.meta.App instance
    pluginDir: null, // = ${basedir}/architect
    name:      null, // = @{name}
    fields:    null, // = @{fields}

    init: function () {
        this.path = joinPath(this.app.appPath, 'model');
        this.metaPath = joinPath(this.app.metaPath, 'model');

        var tpl = readFile(joinPath(this.pluginDir, 'meta-field.tpl'))+'';
        this.fieldTpl = new Ext.XTemplate(tpl);

        this.initMeta();
        this.initFields();
    },

    initFields: function () {
        var fields = this.fields.split(','); // field1,field2,field3,...
        
        this.fields = [];
        this.fieldsByName = {};

        for (var i = 0; i < fields.length; ++i) {
            var field = fields[i].split(':');  // name:type

            this.fields.push(field = {
                name: field[0],
                type: field[1],
                // This GUID is replaced by the existing GUID if we have a field with a
                // matching name in the model's metadata file:
                guid: generateGuid()
            });

            this.fieldsByName[field.name] = field;
        }
    },

    initMeta: function () {
        if (exists(this.metaPath)) {
            var metaFile = new Architect.meta.File(this.metaPath, this.name);
            var modelText, tpl;

            if (metaFile.data) {
                console.info('Loaded Architect metadata for existing Model');
                this.isNew = false;
            } else {
                console.info('Generating Architect metadata for new Model');
                tpl = readFile(joinPath(this.pluginDir, 'meta-model.tpl'))+'';

                tpl = new Ext.XTemplate(tpl);
                modelText = tpl.apply({
                    name: this.name,
                    guid: generateGuid()
                });

                metaFile.data = JSON.parse(modelText);
            }

            //console.log(modelText);
            this.metaFile = metaFile;
            this.guid = metaFile.data.designerId;
        } else {
            console.warn('No metadata folder (' + this.metaPath + ')');
        }
    },

    update: function () {
        console.debug('model name: ' + this.name);
        console.debug('model path: ' + joinPath(this.path, this.name + '.js'));

        if (this.metaFile) {
            var meta = this.metaFile.data;

            // Remove all datafield entries from the cn... but update the GUID's for any
            // fields we will be putting back...
            for (var i = meta.cn.length; i-- > 0;) {
                var child = meta.cn[i];
                if (child.type == 'datafield') {
                    meta.cn.splice(i, 1);
                    this.fieldsByName[child.userConfig.name].guid = child.designerId;
                }
            }
            
            // Now generate the field entries and maintain their GUID's from last time,
            // but we need to remove fields that are no longer present as well as get
            // them in the proper ordinal sequence...
            for (var index = this.fields.length; index-- > 0; ) {
                var fieldText = this.fieldTpl.apply(this.fields[index]);
                var fieldMeta = JSON.parse(fieldText);

                meta.cn.unshift(fieldMeta);
            }

            this.metaFile.save();
            this.app.addModel(this);
        }
    }
});
