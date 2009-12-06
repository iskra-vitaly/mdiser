#!/usr/bin/perl

use File::Path qw(make_path remove_tree);

$#ARGV >= 1 || die("No enough arguments"); 


my($wd) = $ARGV[0];
my($od) = $ARGV[1];

print "Work dir: $wd\nDest dir: $od\n";

foreach(glob("$wd/*.jpg")) {
	if (/(\d{2})\.(\d{4})\.jpg$/) {
		my($subj, $cond) = ($1, $2);
		my($subjdir,$conddir) = ("$od/bysubj/$subj", "$od/bycond/$cond");
		File::Path::make_path($subjdir, $conddir);
		link($_, "$subjdir/$cond.jpg");
		link($_, "$conddir/$subj.jpg");

	}
}
