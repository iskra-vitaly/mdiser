#!/usr/bin/perl

$data='./extracted';
$todir='./extracted';

system("mkdir -p $todir");

@files=glob("$data/face*.zip");

foreach(@files) {
	my $dn = $1 if /(face\d+)\.zip$/;
	next unless $dn;	
	print "Processing $_=>$dn\n";
	mkdir "$todir/$dn";
	print `unzip -x $_ -d $todir/$dn`;
	unlink $_;
}
