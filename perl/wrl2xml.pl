#!/usr/bin/perl

@tag = ();
@indent = ();


sub btag {
	my ($tag, $indent) = @_;
	# print STDERR "btag indent:$indent|\n";
	push @tag, $tag;
	push @indent, $indent;
	print "$indent<$tag>\n";
}

sub etag {
	my $tag = pop @tag;
	my $indent = pop @indent;
	# print STDERR "etag: indent:$indent|\n";
	print "$indent</$tag>\n";
}

sub tag {
	my($tag, $indent, $value) = @_;
	
	if ($value) {print "$indent<$tag>$value</$tag>\n"}
	else {print "$indent<$tag />\n"}
}

$object = "";
$objind = "";

@mesh = ();

sub mesh {join ' ', @_}

foreach (<STDIN>) {
	chop;
	s/\r//gm;
	$objind = $object = ""  if /]\s*$/;

	if ($object) {

		if ($object eq "point")	{
				tr/,/;/;
				print "$_\n"
		}

		if ($object eq "coordIndex") {
			foreach $id (split /,/) {
				next unless $id;
			#print "#id: '$id'\n";
				if ($id == -1) {
					my ($n, $i, $j) = (scalar(@mesh), 0, -2);
					unless ($n == 3) {
					#print "###n: $n\n";
					#print "### $i $j\n";
					#print "### mesh: ".join('|', @mesh)."\n";
						while ($i < $n) {
							print "$objind\t".mesh(@mesh[$j..$i]).";\n";
							++$i;++$j;
						}
					} else {
						print "$objind\t".mesh(@mesh).";\n";
					}
					@mesh = ();
				} else {
					push @mesh, $id;
				}
			}
		}

		next;
	} 


	if(/^(\s*)(\w+)\s*\[/) {
		($object, $objind) = ($2, $1);
		btag($object, $objind);
		next;
	}

	print("<!-- $1 -->\n") && next if /^#(.*)$/;
	btag($2, $1) && next if /^(\s*)(\w+)\s*{/;
	etag() && next if /[\]}]/;
	tag($2, $1, $3) && next if /^(\s*)(\w+)\s*([^\]]*)$/;
}
