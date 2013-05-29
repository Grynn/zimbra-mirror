var _logger = SenchaLogManager.getLogger("app-upgrade");

function possiblyAddProps (propsToAdd, propObj) {
    for (var prop in propsToAdd) {
        if (propsToAdd[prop] != null) {
            propObj[prop] = propsToAdd[prop];
        }
    }
}

function runAppUpgrade (proj) {
    var //basedir = proj.getProperty("basedir"),
        newSdkPath = proj.getProperty("framework.dir"),
        appPath = proj.getProperty('app.dir'),
        //appConfigPath = proj.getProperty('app.config.dir'),
        workspacePath = proj.getProperty("workspace.dir"),
        hasSenchaDir = new File(appPath, ".sencha").exists(),
        noFramework = (proj.getProperty("args.noframework") + '') == "true",
        appId = proj.getProperty("app.id");

    if (!hasSenchaDir) {
        _logger.error("Unable to locate .sencha folder");
        _logger.error("Please ensure this folder is a valid v3 ExtJS application");
        throw new ExState("No .sencha directory found");
    }

    var frameworkName = proj.getProperty("framework.name"),
        appName = proj.getProperty("app.name"),
        appSdkPath = resolvePath(proj.getProperty(frameworkName + ".dir")),
        oldSdkVersion = proj.getProperty("base.framework.version"),
        appBackupPath = resolvePath(appPath, ".sencha_backup", appName, oldSdkVersion),
        noAppJs = proj.getProperty("args.noappjs"),
        generateCmd = [
            "generate",
            "app",
            "-upgrade",
            appName,
            appPath
        ],
        appBackupExcludes = [
            ".sencha_backup/**/*"
        ],
        appVerStr = proj.getProperty("app.cmd.version") + '' || "3.0.0.250";

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

    if (isChildPath(appPath, appSdkPath)) {
        _logger.debug("excluding framework files from app backup");
        appBackupExcludes.push(PathUtil.getRelativePath(appPath, appSdkPath) + "/**/*");
    }

    copyFiles(proj, appPath, appBackupPath, ["**/*"].join(','), appBackupExcludes.join(','));

    _logger.info("Updating application and workspace files");

    // if this is a pre 3.0.1 app, we'll need to update the theme structure
    if (new Version('3.0.1').compareTo(appVer) > 0) {

        var fwConfigDir = proj.getProperty("framework.config.dir"),
            appTemplatePath = resolvePath(
                fwConfigDir, 
                'templates', 
                'App', 
                'packages'),
            //appName = proj.getProperty('app.name'),
            //frameworkName = proj.getProperty('framework.name'),
            frameworkPath = PathUtil.convertPathCharsToUnix(
                PathUtil.getRelativePath(appPath, appSdkPath)),
            oldThemePath = resolvePath(appPath, "resources", "theme"),
            oldSassPath = resolvePath(appPath, "resources", "sass"),
            newThemePath = resolvePath(appPath, "packages"),
            files = new File(oldThemePath)
                .listFiles(), 
            len = files.length, 
            file, f,
            themeNames = [],
            themeName, newThemeName, t,
            srcLocation, dstLocation;
    
        for (f = 0; f < len; f++) {
            file = files[f];
            if (file.isDirectory()) {
                themeNames.push(file.getName());
            }
        }
        
        deleteFile(oldThemePath); // the ./resources/theme folder is no more
        len = themeNames.length;
        
        for (t = 0; t < len; t++) {
            themeName = themeNames[t];
            newThemeName = themeName;
            if (newThemeName == 'default') {
                newThemeName = 'theme';
            }
        
            _logger.info("Upgrading theme {}", themeName);
            
            srcLocation = resolvePath(oldSassPath, themeName);
            dstLocation = resolvePath(newThemePath, newThemeName, "sass");
            _logger.info("Upgrading sass theme location from {} to {}",
                srcLocation, dstLocation);

            moveFiles(proj, srcLocation, dstLocation);
            deleteFile(resolvePath(dstLocation, 'config.rb')); // not upgradable
            deleteFile(srcLocation);
            
            _logger.info("Regenerating theme files for theme {}", themeName);
            
            var props = {
                'name': newThemeName,
                'themeName': newThemeName,
                'appName': appName,
                'frameworkName': frameworkName,
                'frameworkPath': frameworkPath
            };
            
            generateTemplates(appTemplatePath, newThemePath, props);
            
            var appScssFile = resolvePath(dstLocation, "app.scss"),
                appScss = readFileContent(appScssFile) + '',
                themeNameSetter = "$theme-name: '" + newThemeName + "';";
            
            // ensure that the app scss file declares the name of the theme
            if (appScss.indexOf(themeNameSetter) === -1) {
                writeFileContent(
                    appScssFile, 
                    themeNameSetter + StringUtil.NewLine + appScss);
            }
        }
    }
    
    _logger.debug("running command : sencha " + generateCmd.join(" "));
    var props = {
        'workspace.config.dir': proj.getProperty("workspace.config.dir"),
        'workspace.dir': proj.getProperty("workspace.dir"),
        'workspace.build.dir': proj.getProperty("workspace.build.dir"),
        "ext.dir": noFramework ? appSdkPath : newSdkPath,
        "framework.name": proj.getProperty("framework.name")
    };
    
    var propsToAdd = {
        "args.noappjs": noAppJs,
        "app.id": appId
    };

    possiblyAddProps(propsToAdd, props);
    
    if(noFramework) {
        props['args.noframework'] = true;
    }
            
    runSencha(generateCmd, newSdkPath, true, props);

    _logger.info("A backup of pre-upgrade application files is available at {}",
            appBackupPath);
}

(function (proj) {
    _logger.info("upgrading application");
    runAppUpgrade(proj);
})(project);
