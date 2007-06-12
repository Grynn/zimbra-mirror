PHP_VERSION ?= 5.0.5
HTTPD_VERSION ?= 2.2.4
ASPELL_VERSION ?= 0.60.5

all:
	tar xzf php-$(PHP_VERSION).tar.gz
	(cd php-$(PHP_VERSION); ./configure --prefix=/opt/zimbra/php-$(PHP_VERSION) --with-apxs2=/opt/zimbra/httpd-$(HTTPD_VERSION)/bin/apxs --with-config-file=/opt/zimbra/conf/php.ini --with-pspell=/opt/zimbra/aspell-$(ASPELL_VERSION); make; make install)

clean:
	rm -rf php-$(PHP_VERSION)

allclean: clean
	rm -rf /opt/zimbra/php-$(PHP_VERSION)
