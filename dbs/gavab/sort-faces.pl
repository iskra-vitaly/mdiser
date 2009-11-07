#!/usr/bin/perl

use strict;
use warnings;
use File::Path qw(mkpath);

my $in_dir='extracted';
my $out_dir='sorted';

die("$in_dir/ not found") if ! -d $in_dir;

my @faces=grep {-d} glob("$in_dir/face*");

foreach my $facedir (@faces) {
	if ($facedir =~ m/[^\/\d]*(\d+)$/) {
		my $id=$1;
		print "Processing $facedir (id=$id)...\n";
		my @files = grep {-r} glob("$facedir/*.wrl");
		foreach my $file (@files) {
			if ($file =~ m/cara$id[_](.*)\.wrl/) {
				my $expr = $1;
				mkpath("$out_dir/byexpr/$expr");
				my $link = sprintf "%s/byexpr/%s/%02d.wrl", $out_dir, $expr, $id;
				link($file, $link);
				chmod 0444, $link;
			}	
		}
	}
}
