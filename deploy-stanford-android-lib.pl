#!/usr/bin/perl -w
my @jars = split(/\r?\n/, `find /home/stepp/AndroidStudioProjects/ -name stanford-android-lib.jar | sort`);
foreach my $jar (@jars) {
	systemp("cp /home/stepp/stanford/StanfordAndroidLibrary/stanford-android-lib.jar \"$jar\"");
}

sub systemp {
	my $command = shift;
	print("$command\n");
	system($command);
}
