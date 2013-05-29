/*
 * Copyright (c) 2013. Sencha Inc.
 */

// This implements the x-load-properties task.
//
//      <x-load-properties file="foo" required="false" prefix="foo"/>
//
function loadProperties (attributes) {
    var file = new java.io.File(attributes.get('file'));
    var prefix = attributes.get('prefix');
    var req = attributes.get('required');

    req = req && (req + '').toLowerCase();
    req = (req === 'yes' || req === 'true' || req === '1');

    if (!req && !file.exists()) {
        self.log('Optional properties file not present (skipping) - ' + file.absolutePath);
        return;
    }

    self.log('Loading ' + (req ? 'required' : 'optional') + ' properties file ' + file.absolutePath);

    var task = project.createTask('loadproperties');

    prefix = prefix && (prefix + '');
    if (prefix) {
        task.setPrefix(prefix);
    }
    task.setSrcFile(new java.io.File(file));
    task.execute();
}
