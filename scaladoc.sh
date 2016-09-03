#!/usr/bin/env bash

sbt doc

scp -r saul-core/target/scala-2.11/api/*  khashab2@bilbo.cs.illinois.edu:/mounts/bilbo/disks/0/www/cogcomp/html/software/doc/saul

# in case of having permission issues, try:
#
# >  chmod -R 775 *
# >  chgrp -R cs_danr *