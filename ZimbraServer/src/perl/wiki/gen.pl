#!/usr/bin/perl

my @tokens = (0,1,2,3,4,5,6,7,8,9);
my $datadir = "/Users/jylee/ws/main/ZimbraServer/data/wiki/loadtest/";

my $buf;

sub readTemplate($) {
    my ($name) = @_;
    open (TEMPLATE, $name) or die "can't open $name";
    my @lines = <TEMPLATE>;
    $buf = join ("\n", @lines);
    close (TEMPLATE);
}

sub writeFile($) {
    my ($name) = @_;
    open (F, ">$name") or die "can't open $name";
    print F $buf;
    close (F);
}

sub createDirs() {
    my $d1, d2, $f;
    for my $dir (@tokens) {
	$d1 = $datadir . $dir;
	mkdir $d1;
	for my $subdir (@tokens) {
	    $d2 = $d1 . "/" . $subdir;
	    mkdir $d2;
	    for my $file (@tokens) {
		$f = $d2 . "/" . $file;
		writeFile($f);
	    }
	}
    }
}

sub main() {
    readTemplate($datadir . "template");
    createDirs();
}


main();
