var _logger = SenchaLogManager.getLogger("app-upgrade");

function possiblyAddProps (propsToAdd, propObj) {
    for (var prop in propsToAdd) {
        if (propsToAdd[prop] != null) {
            propObj[prop] = propsToAdd[prop];
        }
    }
}

function runAppUpgrade(proj) {
    var //basedir = proj.getProperty("basedir"),
        newSdkPath = proj.getProperty("framework.dir"),
        appPath = proj.getProperty('app.dir'),
        //appConfigPath = proj.getProperty('app.config.dir'),
        workspacePath = proj.getProperty("workspace.dir"),
        hasSenchaDir = new File(appPath, ".sencha").exists(),
        noFramework = (proj.getProperty("args.noframework") + '') === "true",
        appId = proj.getProperty("app.id");

    _logger.info("Upgrading to sdk at {}", newSdkPath);

    if (!hasSenchaDir) {
        _logger.error("Unable to locate .sencha folder");
        _logger.error("Please ensure this folder is a valid v3 ExtJS application");
        throw new ExState("No .sencha directory found");
    }

    var frameworkName = proj.getProperty("framework.name"),
        appName = proj.getProperty("app.name"),
        appSdkPath = resolvePath(proj.getProperty(frameworkName + ".dir")),
        oldSdkVersion = proj.getProperty("base.framework.version"),
        legacySdkVersion = proj.getProperty("legacy.framework.version"),
        appBackupPath = resolvePath(appPath, ".sencha_backup", appName, oldSdkVersion),
        noAppJs = proj.getProperty("args.noappjs"),
        appBackupExcludes = [
            ".sencha_backup/**/*"
        ],
        appVerStr = proj.getProperty("app.cmd.version") + '' || "3.0.0.250",
        getLegacyPath = function(ver) {
            return resolvePath(
                    project.getProperty("cmd.config.dir"),
                    "legacy",
                    ver,
                    frameworkName,
                    legacySdkVersion,
                    "templates"
            );
        },
        generateArgs = [],
        appFrameworkVerStr = proj.getProperty("app.framework.version") + '';
    
    if(appVerStr == "null") {
        appVerStr = "3.0.0.250";
    }
    
    if(!appFrameworkVerStr || appFrameworkVerStr == "null") {
        var appThemeName = proj.getProperty("app.theme") + '';
        
        if(appThemeName && appThemeName != "null") {
            appFrameworkVerStr = "4.1.1";
        } else {
            appFrameworkVerStr = "4.2.0";
        }
    }
    
    var appVer = new Version(appVerStr),
        appFrameworkVer = new Version(appFrameworkVerStr),
        workspacePkgPath = project.getProperty("workspace.packages.dir") + '';

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

    // 4.1 apps need some theme conversions
    if (new Version('4.2.0').compareTo(appFrameworkVer) > 0) {

        // if this is a pre 3.0.2 app, we'll need to update the theme structure
        if (new Version('3.0.2').compareTo(appVer) > 0) {
            _logger.info("upgrading extjs 4.1 pre-3.0.2 app structure");

            _logger.debug("removing unused app.json file");
            FileUtil['delete'](resolvePath(appPath, "app.json"));

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
                newThemePath = resolvePath(workspacePkgPath),
                files = new File(oldThemePath).listFiles(),
                len = files ? files.length : 0,
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
            
            var appIndex = resolvePath(appPath, "index.html"),
                appIndexFile = new File(appIndex);

            if (appIndexFile.exists()) {
                deleteFile(appIndex);
            }

            generateArgs = [        
                "generate",
                "-legacy",
                getLegacyPath(appVerStr),
                "app",
                "-upgrade",
                appName,
                appPath
            ];
            
            _logger.debug("running command : sencha " + generateArgs.join(" "));
            var props = {
                'workspace.config.dir': proj.getProperty("workspace.config.dir"),
                'workspace.dir': proj.getProperty("workspace.dir"),
                'workspace.build.dir': proj.getProperty("workspace.build.dir"),
                "ext.dir": newSdkPath
            };
            
            var propsToAdd = {
                "args.noappjs": noAppJs,
                "app.id": appId
            };

            possiblyAddProps(propsToAdd, props);
            
            if(noFramework) {
                props['args.noframework'] = true;
            }
            
            runSencha(generateArgs, newSdkPath, false, props);

            if (themeNames.length > 0) {
                _logger.info([
                    'Detected multiple non-package themes.',
                    'To control the current theme package used by the app,',
                    'set the "app.theme" variable in ' + appPath + '/.sencha/app/sencha.cfg',
                    'to the name of the theme package to use for this application.'
                ].join(StringUtil.NewLine));
            }

            deleteFile(oldThemePath); // the ./resources/theme folder is no more
            len = themeNames.length;

            for (t = 0; t < len; t++) {
                themeName = themeNames[t];
                newThemeName = appName + "-" + themeName;
                if (newThemeName == appName + '-default') {
                    newThemeName = appName + '-theme';
                }

                _logger.info("Regenerating theme files for theme {}", themeName);

                runSencha([
                    "generate",
                    "theme",
                    "-name=" + newThemeName
                ], appPath);

                _logger.info("Upgrading theme {}", themeName);

                srcLocation = resolvePath(oldSassPath, themeName);
                dstLocation = resolvePath(newThemePath, newThemeName, "sass", "etc");
                _logger.info("Upgrading sass theme sources location from {} to {}",
                        srcLocation, dstLocation);

                moveFiles(proj, srcLocation, dstLocation, "**/*", "**/.sass-cache,.sass-cache");
                deleteFile(resolvePath(dstLocation, 'config.rb')); // not upgradable
                deleteFile(srcLocation);

            }
            
        } else {
            _logger.info("upgrading extjs 4.1 app structure");

            var appIndex = resolvePath(appPath, "index.html"),
                appIndexFile = new File(appIndex);

            if (appIndexFile.exists()) {
                deleteFile(appIndex);
            }

            _logger.debug("removing unused app.json file");
            FileUtil['delete'](resolvePath(appPath, "app.json"));

            var fwConfigDir = proj.getProperty("framework.config.dir"),
                appTemplatePath = resolvePath(
                fwConfigDir,
                'templates',
                'App',
                'packages'),
                frameworkPath = PathUtil.convertPathCharsToUnix(
                PathUtil.getRelativePath(appPath, appSdkPath)),
                    packagesPath = resolvePath(appPath, "packages"),
                files = new File(packagesPath).listFiles(),
                len = files.length,
                file, f,
                themeNames = [],
                themeName, newThemeName, t,
                srcLocation, dstLocation;

            _logger.debug("Checking for legacy theme packages at {}", packagesPath);

            for (f = 0; f < len; f++) {
                file = files[f];
                if (file.isDirectory()) {
                    themeNames.push(file.getName());
                }
            }

            generateArgs = [        
                "generate",
                "-legacy",
                getLegacyPath(appVerStr),
                "app",
                "-upgrade",
                appName,
                appPath
            ];

            _logger.debug("running command : sencha " + generateArgs.join(" "));
            var props = {
                'workspace.config.dir': proj.getProperty("workspace.config.dir"),
                'workspace.dir': proj.getProperty("workspace.dir"),
                'workspace.build.dir': proj.getProperty("workspace.build.dir"),
                "ext.dir": newSdkPath
            };
            
            var propsToAdd = {
                "args.noappjs": noAppJs,
                "app.id": appId
            };

            possiblyAddProps(propsToAdd, props);
            
            if(noFramework) {
                props['args.noframework'] = true;
            }
            
            runSencha(generateArgs, newSdkPath, false, props);

            if (themeNames.length > 0) {
                _logger.info([
                    'Detected multiple non-package themes.',
                    'To control the current theme package used by the app,',
                    'set the "app.theme" variable in ' + appPath + '/.sencha/app/sencha.cfg',
                    'to the name of the theme package to use for this application.'
                ].join(StringUtil.NewLine));
            }

            for (t = 0; t < len; t++) {
                themeName = themeNames[t];
                newThemeName = appName + "-" + themeName;
                if (newThemeName == appName + '-default') {
                    newThemeName = appName + '-theme';
                }

                var oldThemePath = resolvePath(packagesPath, themeName),
                    oldResourcesPath = resolvePath(appPath, "resources", themeName),
                    newResourcesPath = resolvePath(workspacePkgPath, newThemeName, "resources"),
                    oldThemeSources = resolvePath(oldThemePath, "sass"),
                    newThemeSources = resolvePath(workspacePkgPath, newThemeName, "sass", "etc");

                runSencha([
                    "generate",
                    "theme",
                    "-name=" + newThemeName
                ], appPath);

                _logger.info("Regenerating theme files for theme {}", themeName);
                _logger.info("Upgrading sass theme sources location from {} to {}",
                        oldThemeSources, newThemeSources);

                moveFiles(proj, oldThemeSources, newThemeSources, "**/*", "**/.sass-cache,.sass-cache");
                moveFiles(proj, oldResourcesPath, newResourcesPath);
                deleteFile(resolvePath(newThemeSources, 'config.rb')); // not upgradable

                _logger.info("removing legacy theme files at {}", oldThemePath);
                deleteFile(oldThemePath);
                _logger.info("removing legacy theme files at {}", oldResourcesPath);
                deleteFile(oldResourcesPath);

            }
        }

        setProperty(
            resolvePath(appPath, ".sencha", "app", "sencha.cfg"),
            'app.theme',
            'ext-theme-classic');

    } else {
        if(new Version('3.1.0.131').isGreaterThan(appVer)) {
            var filename = resolvePath(appPath, "app.json"),
                file = new java.io.File(filename),
                appCmdVer = new Version(project.getProperty("app.cmd.version")),
                upgradeVer = new Version("3.1.0.131"),
                appThemeName = project.getProperty("app.theme");

            _logger.info("checking for app.json at : " + filename);
            if (file.exists() && upgradeVer.isGreaterThan(appCmdVer)) {
                ("loading app.json config");
                var config = readConfig(filename),
                    newRequires = [];
                if(config.requires && config.requires.length > 0) {
                    _logger.info("converting app requires");
                    for(var r = 0; r < config.requires.length; r++) {
                        var req = config.requires[r];
                        if(!isPrimitive(req)) {
                            var name = req.name,
                                ver = req.version,
                                minver = req.minver;

                            // skip the app theme
                            if(name.indexOf(appThemeName) > -1) {
                                continue;
                            }

                            if(minver) {
                                name = name + "/" + ver + "-" + minver;
                            } else {
                                if(ver) {
                                    name = name + "/" + ver + "?";
                                }
                            }
                            newRequires.push(name);
                        } else {
                            newRequires.push(req);
                        }
                    }
                }
                _logger.info("saving updated app.json");
                config.requires = newRequires;
                writeJson(filename, config);
            }
        }

        generateArgs = [        
                "generate",
                "-legacy",
                getLegacyPath(appVerStr),
                "app",
                "-upgrade",
                appName,
                appPath
            ];
            
        _logger.debug("running command : sencha " + generateArgs.join(" "));
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

        runSencha(generateArgs, newSdkPath, false, props);
    }


    _logger.info("A backup of pre-upgrade application files is available at {}",
            appBackupPath);
}

(function(proj) {
    _logger.info("upgrading application");
    runAppUpgrade(proj);
})(project);
