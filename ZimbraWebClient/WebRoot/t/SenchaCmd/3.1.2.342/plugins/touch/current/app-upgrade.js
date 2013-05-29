var _logger = SenchaLogManager.getLogger("app-upgrade");

function possiblyAddProps (propsToAdd, propObj) {
    for (var prop in propsToAdd) {
        if (propsToAdd[prop] != null) {
            propObj[prop] = propsToAdd[prop];
        }
    }
}

function removeBuildPathsFromConfig(configFile) {
    var configData = readConfig(configFile);
    if(configData.buildPaths) {
        delete configData.buildPaths;
    }
    writeFileContent(configFile, jsonEncode(configData, true));
}

function runAppUpgrade(proj) {
    var basedir = proj.getProperty("basedir"),
        newSdkPath = proj.getProperty("args.path"),
        appPath = resolvePath("."),
        hasSenchaSdkFile = new File(appPath, ".senchasdk").exists(),
        hasSenchaDir = new File(appPath, ".sencha").exists(),
        noFramework = (proj.getProperty("args.noframework") + '') === "true",
        appId = proj.getProperty("app.id");

    // v2 app
    if(hasSenchaSdkFile && !hasSenchaDir) {

        // backup packager.json
        // backup app.json
        // backup .senchasdk target directory
        // generate app locally from new framework
        // update app.json paths to .senchasdk stuff to framework.dir
        // delete .senchasdk

        var appSdkFile = resolvePath(appPath, '.senchasdk'),
            appSdkPtr =  FileUtil.readFile(appSdkFile).trim(),
            appSdkPath = resolvePath(appPath, appSdkPtr),
            appConfigFile = resolvePath(appPath, "app.json"),
            appConfig = readConfig(appConfigFile),
            oldSdkVersion = FileUtil.readFile(resolvePath(appSdkPath, "version.txt")).trim(),
            appBackupPath = resolvePath(appPath, ".sencha_backup", appName, oldSdkVersion),
            sdkBackupPath = resolvePath(appBackupPath, appSdkPtr),
            appName = appConfig.name,
            generateCmd = [
                "generate",
                "app",
                "-upgrade",
                appName,
                appPath
            ],
            newAppConfig,
            frameworkPath,
            relativePath,
            appFiles = [
                ".senchasdk",
                "app.js",
                "app.json",
                "packager.json",
                "index.html",
                "resources/sass/config.rb"
            ];

        _logger.debug("Backing up application sdk from {} to {}",
            appSdkPath,
            sdkBackupPath);

        moveFiles(proj, appSdkPath, sdkBackupPath);

        _logger.info("Renamed {} to {} for backup", appSdkPath, sdkBackupPath);

        _logger.debug("Backing up application specific files");

        copyFiles(proj, appPath, appBackupPath, "**/*", ".sencha_backup/**/*");

        _logger.info("Creating new application structure");

        var generateCmd = [
            "generate",
            "app",
            "-upgrade",
            appName,
            appPath
        ];

        var props = {
            'touch.dir': newSdkPath,
            'workspace.config.dir': proj.getProperty("workspace.config.dir"),
            'workspace.dir': proj.getProperty("workspace.dir"),
            'cmd.architect.mode': proj.getProperty('cmd.architect.mode')
        };
        
        var propsToAdd = {
            "app.id": appId
        };
        
        possiblyAddProps(propsToAdd, props);
        
        _logger.debug(generateCmd.join(" "));
        runSencha(generateCmd, newSdkPath, false, props);

        _logger.info("Updating references to framework files");

        newAppConfig = new SenchaConfigManager(appPath).getConfig();
        frameworkPath = newAppConfig.get("framework.dir");
        relativePath =
            (PathUtil.getRelativePath(appPath, frameworkPath) + '').replace("\\", "/");

        if(endsWith(relativePath, "/")) {
            relativePath = relativePath.substring(0, relativePath.length - 1);
        }

        _logger.debug("Updating file references from path '{}' to path '{}'",
            appSdkPtr,
            relativePath)

        each(appFiles, function(file){
            var fileData = readFileContent(file);

            // prop: "sdk/...
            fileData = StringUtil.replace(
                fileData,
                "\"" + appSdkPtr,
                "\"" + relativePath);

            // prop: 'sdk/...
            fileData = StringUtil.replace(
                fileData,
                "'" + appSdkPtr,
                "'" + relativePath);

            writeFileContent(file, fileData);
        });

        deleteFile(appSdkFile);

        _logger.debug("Removing deprecated 'buildPaths' property from app.json");
        removeBuildPathsFromConfig(appConfigFile);

        // set the app config path for sencha.cfg update downstream
        proj.setProperty("app.ir", appPath);
        proj.setProperty("app.config.dir", [appPath, '.sencha', 'app'].join(File.separator));

    }
    // v3 app
    else if(hasSenchaDir) {

        var frameworkName = proj.getProperty("framework.name"),
            appName = proj.getProperty("app.name"),
            appConfigFile = resolvePath(appPath, "app.json"),
            workspacePath = proj.getProperty("workspace.dir"),
            appSdkPath = resolvePath(proj.getProperty(frameworkName + ".dir")),
            oldSdkVersion = proj.getProperty("base.framework.version"),
            legacySdkVersion = proj.getProperty("legacy.framework.version"),
            appBackupPath = resolvePath(appPath, ".sencha_backup", appName, oldSdkVersion),
            sdkBackupPath = resolvePath(workspacePath, ".sencha_backup", frameworkName, oldSdkVersion),
            noAppJs = proj.getProperty("args.noappjs"),
            appBackupExcludes = [
                ".sencha_backup/**/*"
            ],
            appVerStr = proj.getProperty("app.cmd.version") + '' || "3.0.0.250",
            getLegacyPath = function(ver) {
                return resolvePath(
                        proj.getProperty("cmd.config.dir"),
                        "legacy",
                        ver,
                        frameworkName,
                        legacySdkVersion,
                        "templates"
                );
            };

        if(appVerStr == "null") {
            appVerStr = "3.0.0.250";
        }

        var appVer = new Version(appVerStr);

        if(new Version(proj.getProperty("cmd.version")).compareTo(appVer) === 0) {
            _logger.info("Application structure already at current cmd version");
            return;
        }


        _logger.info("Backing up application files from {} to {}",
            appPath,
            appBackupPath);

        if(isChildPath(appPath, appSdkPath)) {
            _logger.debug("excluding framework files from app backup");
            appBackupExcludes.push(PathUtil.getRelativePath(appPath, appSdkPath) + "/**/*");
        }

        copyFiles(proj, appPath, appBackupPath, ["**/*"].join(','), appBackupExcludes.join(','));

        _logger.info("Updating application and workspace files");
        
        var props = {
            'touch.dir': noFramework ? appSdkPath : newSdkPath,
            'workspace.config.dir': proj.getProperty("workspace.config.dir"),
            'workspace.dir': proj.getProperty("workspace.dir"),
            'workspace.build.dir': proj.getProperty("workspace.build.dir"),
            'cmd.architect.mode': proj.getProperty('cmd.architect.mode'),
            "framework.name": proj.getProperty("framework.name")
        };

        if(noFramework) {
            props['args.noframework'] = true;
        }
        
        var propsToAdd = {
            "args.noappjs": noAppJs,
            "app.id": appId
        };
        
        possiblyAddProps(propsToAdd, props);
        
        var generateCmd = [
            "generate",
            "-legacy",
            getLegacyPath(appVerStr),
            "app",
            "-upgrade",
            appName,
            appPath
        ];

        _logger.debug("running command : sencha " + generateCmd.join(" "));
        runSencha(generateCmd, newSdkPath, false, props);

        _logger.debug("Removing deprecated 'buildPaths' property from app.json");
        removeBuildPathsFromConfig(appConfigFile);

        _logger.info("A backup of pre-upgrade application files is available at {}", 
            appBackupPath);
        
    } else if(!hasSenchaSdkFile) {

        _logger.error("Unable to locate .senchasdk file or .sencha folder");
        _logger.error("Please ensure this folder is a valid v2 or v3 Sencha Touch application");
        throw new ExState("No .senchasdk file or .sencha directory found");

    }

};

(function (proj) {
    _logger.info("building application");
    runAppUpgrade(proj);
})(project);