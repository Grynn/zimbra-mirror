# The name of the application
app.name={name}

# The name of the framework used by the application (ext / touch)
app.framework={frameworkName}

# The path(s) to application javascript sources (comma separated)
app.classpath=$\u007Bapp.dir}/app

# Output location for application build artifacts
app.build.dir=$\u007Bworkspace.build.dir}/$\u007Bapp.name}

# The root namespace to use when mapping scss resources to js classes
# in the sass/src and sass/var directories
app.sass.namespace={name}

# Path to sass rule definition files corresponding to JavaScript classes.
app.sass.srcpath=$\u007Bapp.dir}/sass/src

# Path to sass variable definition files corresponding to JavaScript classes.
app.sass.varpath=$\u007Bapp.dir}/sass/var

# Path to sass function and mixin files.
app.sass.etcpath=$\u007Bapp.dir}/sass/etc/all.scss

# Path to extra ruby files to include into the generated sass config.rb,
# <approot>/sass/config.rb will be included automatically if present and does
# not need to be specified.
# app.sass.rubypath=

# The name of the package containing the theme scss for the app
app.theme=ext-theme-classic

#==============================================================================
# Custom Properties - Place customizations below this line to avoid merge
# conflicts with newer versions
