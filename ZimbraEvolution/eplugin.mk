%.eplug: %.eplug.in
	sed -e 's|\@PLUGINDIR\@|$(eplugindir)|;s|\@I18N_DOMAIN\@|$(GETTEXT_PACKAGE)|;s|\@SX_LOCALEDIR\@|$(localedir)|' $< > $@
