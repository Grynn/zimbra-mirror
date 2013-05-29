/*
 * Copyright (c) 2012. Sencha Inc.
 * 
 * This file contains helper methods and the control logic for the Schema plugin.
 */

//@require ../../js/all.js

importPackage(com.sencha.database.model);

function generateTpl (props) {
    //console.info('Generating template from ' + (props.file || props.dir));

    var generator = project.createTask('x-generate');
    var param;

    for (var prop in props) {
        if (prop !== 'params') {
            generator[prop] = props[prop];
        } else {
            for (var key in props.params) {
                param = generator.createParam();
                param.name = key;
                param.value = props.params[key];
            }
        }
    }

    generator.execute();
}

function fixFileName (file) {
    return PathUtil.convertPathCharsToUnix(PathUtil.getCanonicalPath(file)) + '';
}

function doExport (attributes) {
    var baseDir = project.getProperty('app.dir') ||
                  project.getProperty('package.dir') ||
                  project.getProperty('workspace.dir');
    baseDir = baseDir && (baseDir + '');
    //console.log('baseDir: ' + baseDir);

    var configDir = project.getProperty('app.config.dir') ||
                    project.getProperty('package.config.dir') ||
                    project.getProperty('workspace.config.dir');
    configDir = configDir && (configDir + '');
    //console.log('configDir: ' + configDir);

    var file = attributes.get('file') + '';
    //echo('schema: ' + schema);

    var db = new Database(attributes.get('dbtype'));
    // db.addOption("foo", "bar");
    db.loadXml(file);
    db.markComplete();

    // Call the main method of the main.js file that was pulled in for the export:
    main({
        db: db,
        basedir: baseDir,
        configDir: configDir,
        exporterDir: project.getProperty('exporterDir'),
        dbType: attributes.get('dbtype') + '',
        out: attributes.get('out') + '',
        file: fixFileName(file),
        namespace: toJS(attributes.get('namespace')),
        workspaceDir: fixFileName(project.getProperty('workspace.dir'))
    });
}
