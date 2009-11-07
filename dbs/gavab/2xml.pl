#!/usr/bin/perl

use strict;
use warnings;

my $dir='sorted';
my $transform = '../../perl/wrl2xml.pl';

my @files=`find $dir -regex '.*\.wrl'`;
@files = map {chop;$_} @files;
#foreach (@files) {print "File: $_\n"}
my %map = map {/^(.*[^\/]*)\.wrl$/ ? ($&=>"$1.xml") : ('','')} @files;

foreach (grep {defined $map{$_}} (sort @files)) {
	my $out = $map{$_};
	print "Process: $_=>$out\n";
	system("$transform <$_ >$out"); 
}
