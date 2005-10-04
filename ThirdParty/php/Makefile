PHP-VERSION := 5.0.5
HTTPD-VERSION := 2.0.54
ASPELL-VERSION := 0.60.3

all:
	(cd php-$(PHP-VERSION); ./configure --prefix=/opt/zimbra/php-$(PHP-VERSION) --with-apxs2=/opt/zimbra/httpd-$(HTTPD-VERSION)/bin/apxs --with-config-file=/opt/zimbra/conf/php.ini --with-pspell=/opt/zimbra/aspell-$(ASPELL-VERSION); make)

