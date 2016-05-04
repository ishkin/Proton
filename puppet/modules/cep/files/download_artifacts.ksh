Last login: Tue May  3 15:36:42 on ttys002
-bash: jenv: command not found
shani-mac:~ shani$ cd SVN
-bash: cd: SVN: No such file or directory
shani-mac:~ shani$ pwd
/Users/shani
shani-mac:~ shani$ cd /SVN
-bash: cd: /SVN: No such file or directory
shani-mac:~ shani$ cd Documents/SVN/
shani-mac:SVN shani$ ls
Tomcat	cep	docker	puppet
shani-mac:SVN shani$ cd puppet
shani-mac:puppet shani$ ls
miscellaneous	modules
shani-mac:puppet shani$ ls miscellaneous/
CEP_Install_via_puppet.sh	tomcat-users.xml
smoketest.sh
shani-mac:puppet shani$ ls modules/
cep
shani-mac:puppet shani$ ls modules/cep/
files		manifests	templates
shani-mac:puppet shani$ pwd
/Users/shani/Documents/SVN/puppet
shani-mac:puppet shani$ cd ../cep
shani-mac:cep shani$ ls
Archive.zip
CEP_Install_via_puppet.old.sh
CEP_Install_via_puppet.sh
FIWARE - Complex Event Processing (CEP) - Proactive Technology Online - PROTON - Performance test. v0.1.docx
ProtonUserGuidev4.4.1.docx
ProtonUserGuidev4.4.1.pdf
files
manifests
metadata.json
puppet_instalation-README-fix1.docx
puppet_instalation-README.doc
puppet_instalation-README.docx
puppet_instalation-README.pdf
templates
test
shani-mac:cep shani$ ls -la
total 4832
drwxr-xr-x  19 shani  HAIFA\Domain Users      646 May  4 14:32 .
drwxr-xr-x   7 shani  HAIFA\Domain Users      238 Apr 27 13:48 ..
-rw-r--r--@  1 shani  HAIFA\Domain Users     6148 May  1 14:22 .DS_Store
drwxr-xr-x   7 shani  HAIFA\Domain Users      238 May  1 11:30 .svn
-rw-r--r--   1 shani  HAIFA\Domain Users     3000 May  1 11:28 Archive.zip
-rw-r--r--   1 shani  HAIFA\Domain Users     3170 Apr  3 13:28 CEP_Install_via_puppet.old.sh
-rw-r--r--   1 shani  HAIFA\Domain Users     3459 May  1 08:38 CEP_Install_via_puppet.sh
-rw-r--r--   1 shani  HAIFA\Domain Users   424425 Mar 21 18:51 FIWARE - Complex Event Processing (CEP) - Proactive Technology Online - PROTON - Performance test. v0.1.docx
-rw-r--r--@  1 shani  HAIFA\Domain Users   418829 Mar 26 20:49 ProtonUserGuidev4.4.1.docx
-rw-r--r--@  1 shani  HAIFA\Domain Users  1449712 Mar 26 20:43 ProtonUserGuidev4.4.1.pdf
drwxr-xr-x   3 shani  HAIFA\Domain Users      102 Feb 22 12:26 files
drwxr-xr-x   9 shani  HAIFA\Domain Users      306 Feb 22 12:26 manifests
-rw-r--r--   1 shani  HAIFA\Domain Users     1049 Feb 22 12:26 metadata.json
-rw-r--r--   1 shani  HAIFA\Domain Users     9845 May  4 11:44 puppet_instalation-README-fix1.docx
-rw-r--r--@  1 shani  HAIFA\Domain Users    31744 Mar  6 16:13 puppet_instalation-README.doc
-rw-r--r--@  1 shani  HAIFA\Domain Users    20897 May  4 14:32 puppet_instalation-README.docx
-rw-r--r--@  1 shani  HAIFA\Domain Users    70612 May  4 14:32 puppet_instalation-README.pdf
drwxr-xr-x   3 shani  HAIFA\Domain Users      102 Feb 22 12:26 templates
-rw-r--r--   1 shani  HAIFA\Domain Users        5 Feb 22 13:44 test
shani-mac:cep shani$ svn status
?       Archive.zip
?       CEP_Install_via_puppet.old.sh
?       CEP_Install_via_puppet.sh
?       FIWARE - Complex Event Processing (CEP) - Proactive Technology Online - PROTON - Performance test. v0.1.docx
?       ProtonUserGuidev4.4.1.docx
?       ProtonUserGuidev4.4.1.pdf
?       puppet_instalation-README-fix1.docx
?       puppet_instalation-README.doc
?       puppet_instalation-README.docx
M       puppet_instalation-README.pdf
shani-mac:cep shani$ svn update
Updating '.':
At revision 1297.
shani-mac:cep shani$ svn diff
Index: puppet_instalation-README.pdf
===================================================================
Cannot display: file marked as a binary type.
svn:mime-type = application/octet-stream
shani-mac:cep shani$ cd ../puppet/
shani-mac:puppet shani$ ls
miscellaneous	modules
shani-mac:puppet shani$ cd miscellaneous/
shani-mac:miscellaneous shani$ ls
CEP_Install_via_puppet.sh	smoketest.sh			tomcat-users.xml
shani-mac:miscellaneous shani$ ls -la
total 24
drwxr-xr-x  5 shani  HAIFA\Domain Users   170 Mar  6 15:35 .
drwxr-xr-x  6 shani  HAIFA\Domain Users   204 Apr 27 13:42 ..
-rw-r--r--  1 shani  HAIFA\Domain Users  3170 Apr  3 13:28 CEP_Install_via_puppet.sh
-rw-r--r--  1 shani  HAIFA\Domain Users   527 Mar  6 15:35 smoketest.sh
-rwxr-xr-x  1 shani  HAIFA\Domain Users  1884 Mar  6 15:35 tomcat-users.xml
shani-mac:miscellaneous shani$ svn status
M       CEP_Install_via_puppet.sh
shani-mac:miscellaneous shani$ svn update
Updating '.':
GU   CEP_Install_via_puppet.sh
Updated to revision 231.
shani-mac:miscellaneous shani$ diff ../CEP_Install_via_puppet.sh .
111,112c111
< #sudo_run /usr/bin/puppet apply --parser future --modulepath=$base_dir/puppet/modules:/etc/puppet/modules:/usr/share/puppet/modules -e 'include cep' --debug
< sudo_run /usr/bin/puppet apply --modulepath=$base_dir/puppet/modules:/etc/puppet/modules:/usr/share/puppet/modules -e 'include cep'
---
> sudo_run /usr/bin/puppet apply --parser future --modulepath=$base_dir/puppet/modules:/etc/puppet/modules:/usr/share/puppet/modules -e 'include cep' --debug
125c124,126
< 
---
> if [ $1 = "--docker"  ]; then
> 	/bin/rm -rf /var/lib/tomcat7/webapps/AuthoringTool /var/lib/tomcat7/webapps/ProtonOnWebServerAdmin/ /puppet/modules/cep/files/*.war
> fi
shani-mac:miscellaneous shani$ pwd
/Users/shani/Documents/SVN/puppet/miscellaneous
shani-mac:miscellaneous shani$ cd ../modules/cep/files/
shani-mac:files shani$ diff ../download_artifacts.ksh .
shani-mac:files shani$ cd ..
shani-mac:cep shani$ pwd
/Users/shani/Documents/SVN/puppet/modules/cep
shani-mac:cep shani$ cd manifests/
shani-mac:manifests shani$ diff ../config.pp .
8c8
< 		source  => "$pkgs::files_source/tomcat-users.xml",
---
> 		source  => "puppet:///modules/$module_name/tomcat-users.xml",
shani-mac:manifests shani$ diff ../deploy.pp .
34c34
< 		source  => "$pkgs::files_source/AuthoringTool.war",
---
> 		source  => "puppet:///modules/$module_name/AuthoringTool.war",
44c44
< 		source  => "$pkgs::files_source/AuthoringToolWebServer.war",
---
> 		source  => "puppet:///modules/$module_name/AuthoringToolWebServer.war",
54c54
< 		source  => "$pkgs::files_source/ProtonOnWebServer.war",
---
> 		source  => "puppet:///modules/$module_name/ProtonOnWebServer.war",
64c64
< 		source  => "$pkgs::files_source/ProtonOnWebServerAdmin.war",
---
> 		source  => "puppet:///modules/$module_name/ProtonOnWebServerAdmin.war",
shani-mac:manifests shani$ diff ../download.pp .
2c2,3
< 	#We are running without a master, we will execute the download_artifacts.ksh locally to download the required artifacts.
---
> 	#We are running without a master, we will locate and run the download_artifacts.ksh locally
> 	each(split($settings::modulepath, ':')) |$modules_folder| {
4,25c5,14
< 	#Define temporary storage location:
< 	$local_temp_folder = "/tmp/puppet_module_$module_name.tmp"
< 
< 
< 	#Create the temporary folder:
< 	file { "$local_temp_folder":
< 		ensure  => directory,
< 		owner  => 'root',
< 		group  => 'root',
< 		mode   => '750',
< 	}
< 
< 
< 	#copy the download script the temporary folder:
< 	file { "$local_temp_folder/download_artifacts.ksh":
< 		ensure  => file,
< 		source  => "puppet:///modules/$module_name/download_artifacts.ksh",
< 		owner   => 'root',
< 		group   => 'root',
< 		mode    => 0750,
< 		replace => true,
< 		require => File["$local_temp_folder"],
---
> 		exec {"exists $modules_folder/$module_name":
> 			 command => '/bin/true',
> 			 onlyif => "/usr/bin/test -e $modules_folder/$module_name",
> 		}
> 
> 		exec { "$modules_folder/$module_name/download_artifacts.ksh":
> 			command => "$modules_folder/$module_name/files/download_artifacts.ksh",
> 			onlyif => "/usr/bin/test -e $modules_folder/$module_name",
> 			require => [Package['ksh'],Package['xmlstarlet'],Package['zip'],Package['unzip'],Package['curl'],Exec["exists $modules_folder/$module_name"]],
> 		}
27,46d15
< 
< 
< 	#Execute the download artifacts script only once, e.g: check for the ".executed-flag" file:
< 	exec { "$local_temp_folder/download_artifacts.ksh":
< 		command => "$local_temp_folder/download_artifacts.ksh",
< 		creates => "$local_temp_folder/download_artifacts.executed-flag",
< 		require => [Package['ksh'],Package['xmlstarlet'],Package['zip'],Package['unzip'],Package['curl'],File["$local_temp_folder/download_artifacts.ksh"]],
< 	}
< 
< 	#We will only download the artifacts once, when executed successfully creates the flag file:
< 	file { "$local_temp_folder/download_artifacts.executed-flag":
< 		ensure  => file,
< 		content => '',
< 		owner   => 'root',
< 		group   => 'root',
< 		mode    => 0440,
< 		replace => false,
< 		require => [File["$local_temp_folder"],Exec["$local_temp_folder/download_artifacts.ksh"]],
< 	}
< 	
48d16
< 
shani-mac:manifests shani$ diff ../pkgs.pp .
56c56
< 		#We are running without a master, we shall run the download_artifacts.ksh locally
---
> 		#We are running without a master, we will locate and run the download_artifacts.ksh locally
58,60d57
< 		$files_source = "$cep::download::local_temp_folder"
< 	}else{
< 		$files_source = "puppet:///modules/$module_name"
shani-mac:manifests shani$ cd ..
shani-mac:cep shani$ pwd
/Users/shani/Documents/SVN/puppet/modules/cep
shani-mac:cep shani$ cd ..
shani-mac:modules shani$ cd ..
shani-mac:puppet shani$ ls
miscellaneous	modules
shani-mac:puppet shani$ svn status
M       miscellaneous/CEP_Install_via_puppet.sh
M       modules/cep/manifests/config.pp
M       modules/cep/manifests/deploy.pp
M       modules/cep/manifests/download.pp
M       modules/cep/manifests/pkgs.pp
shani-mac:puppet shani$ svn commit -m "Fixing problems with the puppet things"
Authentication realm: <https://github.com:443> GitHub
Password for 'shani': 
Authentication realm: <https://github.com:443> GitHub
Username: shani@il.ibm.com
Password for 'shani@il.ibm.com': 
Authentication realm: <https://github.com:443> GitHub
Username: urishani
Password for 'urishani': 
Sending        miscellaneous/CEP_Install_via_puppet.sh
Sending        modules/cep/manifests/config.pp
Sending        modules/cep/manifests/deploy.pp
Sending        modules/cep/manifests/download.pp
svn: E160042: Commit failed (details follow):
svn: E160042: File or directory 'modules/cep/manifests/download.pp' is out of date; try updating
svn: E160024: resource out of date; try updating
shani-mac:puppet shani$ svn update
Updating '.':
A    README.md
A    puppet_instalation-README.pdf
Updated to revision 231.
shani-mac:puppet shani$ svn commit -m "Fixing problems with the puppet things"
Sending        miscellaneous/CEP_Install_via_puppet.sh
Sending        modules/cep/manifests/config.pp
Sending        modules/cep/manifests/deploy.pp
Sending        modules/cep/manifests/download.pp
svn: E160042: Commit failed (details follow):
svn: E160042: File or directory 'modules/cep/manifests/download.pp' is out of date; try updating
svn: E160024: resource out of date; try updating
shani-mac:puppet shani$ svn update
Updating '.':
At revision 231.
shani-mac:puppet shani$ svn commit -m "Fixing problems with the puppet things"
Sending        miscellaneous/CEP_Install_via_puppet.sh
Sending        modules/cep/manifests/config.pp
Sending        modules/cep/manifests/deploy.pp
Sending        modules/cep/manifests/download.pp
svn: E160042: Commit failed (details follow):
svn: E160042: File or directory 'modules/cep/manifests/download.pp' is out of date; try updating
svn: E160024: resource out of date; try updating
shani-mac:puppet shani$ touch modules/cep/manifests/download.pp 
shani-mac:puppet shani$ svn commit -m "Fixing problems with the puppet things"
Sending        miscellaneous/CEP_Install_via_puppet.sh
Sending        modules/cep/manifests/config.pp
Sending        modules/cep/manifests/deploy.pp
Sending        modules/cep/manifests/download.pp
svn: E160042: Commit failed (details follow):
svn: E160042: File or directory 'modules/cep/manifests/download.pp' is out of date; try updating
svn: E160024: resource out of date; try updating
shani-mac:puppet shani$ svn reset
Unknown command: 'reset'
Type 'svn help' for usage.
shani-mac:puppet shani$ svn clean
Unknown command: 'clean'
Type 'svn help' for usage.
shani-mac:puppet shani$ svn help
usage: svn <subcommand> [options] [args]
Subversion command-line client, version 1.7.22.
Type 'svn help <subcommand>' for help on a specific subcommand.
Type 'svn --version' to see the program version and RA modules
  or 'svn --version --quiet' to see just the version number.

Most subcommands take file and/or directory arguments, recursing
on the directories.  If no arguments are supplied to such a
command, it recurses on the current directory (inclusive) by default.

Available subcommands:
   add
   blame (praise, annotate, ann)
   cat
   changelist (cl)
   checkout (co)
   cleanup
   commit (ci)
   copy (cp)
   delete (del, remove, rm)
   diff (di)
   export
   help (?, h)
   import
   info
   list (ls)
   lock
   log
   merge
   mergeinfo
   mkdir
   move (mv, rename, ren)
   patch
   propdel (pdel, pd)
   propedit (pedit, pe)
   propget (pget, pg)
   proplist (plist, pl)
   propset (pset, ps)
   relocate
   resolve
   resolved
   revert
   status (stat, st)
   switch (sw)
   unlock
   update (up)
   upgrade

Subversion is a tool for version control.
For additional information, see http://subversion.apache.org/
shani-mac:puppet shani$ svn diff
Index: miscellaneous/CEP_Install_via_puppet.sh
===================================================================
--- miscellaneous/CEP_Install_via_puppet.sh	(revision 231)
+++ miscellaneous/CEP_Install_via_puppet.sh	(working copy)
@@ -108,7 +108,8 @@
 handle_hostname_changes
 
 #Install the cep module using puppet
-sudo_run /usr/bin/puppet apply --parser future --modulepath=$base_dir/puppet/modules:/etc/puppet/modules:/usr/share/puppet/modules -e 'include cep' --debug
+#sudo_run /usr/bin/puppet apply --parser future --modulepath=$base_dir/puppet/modules:/etc/puppet/modules:/usr/share/puppet/modules -e 'include cep' --debug
+sudo_run /usr/bin/puppet apply --modulepath=$base_dir/puppet/modules:/etc/puppet/modules:/usr/share/puppet/modules -e 'include cep'
 
 print_to_log 'INFO' "The log file is saved as: $log_file"
 
Index: modules/cep/manifests/config.pp
===================================================================
--- modules/cep/manifests/config.pp	(revision 231)
+++ modules/cep/manifests/config.pp	(working copy)
@@ -5,7 +5,7 @@
 
 	file { '/etc/tomcat7/tomcat-users.xml':
 		ensure  => file,
-		source  => "puppet:///modules/$module_name/tomcat-users.xml",
+		source  => "$pkgs::files_source/tomcat-users.xml",
 		owner   => 'root',
 		group   => 'root',
 		mode    => 0644,
Index: modules/cep/manifests/deploy.pp
===================================================================
--- modules/cep/manifests/deploy.pp	(revision 231)
+++ modules/cep/manifests/deploy.pp	(working copy)
@@ -31,7 +31,7 @@
 
 	file { '/var/lib/tomcat7/webapps/AuthoringTool.war':
 		ensure  => file,
-		source  => "puppet:///modules/$module_name/AuthoringTool.war",
+		source  => "$pkgs::files_source/AuthoringTool.war",
 		owner   => 'tomcat7',
 		group   => 'tomcat7',
 		mode    => 0644,
@@ -41,7 +41,7 @@
 
 	file { '/var/lib/tomcat7/webapps/AuthoringToolWebServer.war':
 		ensure  => file,
-		source  => "puppet:///modules/$module_name/AuthoringToolWebServer.war",
+		source  => "$pkgs::files_source/AuthoringToolWebServer.war",
 		owner   => 'tomcat7',
 		group   => 'tomcat7',
 		mode    => 0644,
@@ -51,7 +51,7 @@
 
 	file { '/var/lib/tomcat7/webapps/ProtonOnWebServer.war':
 		ensure  => file,
-		source  => "puppet:///modules/$module_name/ProtonOnWebServer.war",
+		source  => "$pkgs::files_source/ProtonOnWebServer.war",
 		owner   => 'tomcat7',
 		group   => 'tomcat7',
 		mode    => 0644,
@@ -61,7 +61,7 @@
 
 	file { '/var/lib/tomcat7/webapps/ProtonOnWebServerAdmin.war':
 		ensure  => file,
-		source  => "puppet:///modules/$module_name/ProtonOnWebServerAdmin.war",
+		source  => "$pkgs::files_source/ProtonOnWebServerAdmin.war",
 		owner   => 'tomcat7',
 		group   => 'tomcat7',
 		mode    => 0644,
Index: modules/cep/manifests/download.pp
===================================================================
--- modules/cep/manifests/download.pp	(revision 231)
+++ modules/cep/manifests/download.pp	(working copy)
@@ -1,16 +1,48 @@
 class cep::download {
-	#We are running without a master, we will locate and run the download_artifacts.ksh locally
-	each(split($settings::modulepath, ':')) |$modules_folder| {
+	#We are running without a master, we will execute the download_artifacts.ksh locally to download the required artifacts.
 
-		exec {"exists $modules_folder/$module_name":
-			 command => '/bin/true',
-			 onlyif => "/usr/bin/test -e $modules_folder/$module_name",
-		}
+	#Define temporary storage location:
+	$local_temp_folder = "/tmp/puppet_module_$module_name.tmp"
 
-		exec { "$modules_folder/$module_name/download_artifacts.ksh":
-			command => "$modules_folder/$module_name/files/download_artifacts.ksh",
-			onlyif => "/usr/bin/test -e $modules_folder/$module_name",
-			require => [Package['ksh'],Package['xmlstarlet'],Package['zip'],Package['unzip'],Package['curl'],Exec["exists $modules_folder/$module_name"]],
-		}
+
+	#Create the temporary folder:
+	file { "$local_temp_folder":
+		ensure  => directory,
+		owner  => 'root',
+		group  => 'root',
+		mode   => '750',
 	}
+
+
+	#copy the download script the temporary folder:
+	file { "$local_temp_folder/download_artifacts.ksh":
+		ensure  => file,
+		source  => "puppet:///modules/$module_name/download_artifacts.ksh",
+		owner   => 'root',
+		group   => 'root',
+		mode    => 0750,
+		replace => true,
+		require => File["$local_temp_folder"],
+	}
+
+
+	#Execute the download artifacts script only once, e.g: check for the ".executed-flag" file:
+	exec { "$local_temp_folder/download_artifacts.ksh":
+		command => "$local_temp_folder/download_artifacts.ksh",
+		creates => "$local_temp_folder/download_artifacts.executed-flag",
+		require => [Package['ksh'],Package['xmlstarlet'],Package['zip'],Package['unzip'],Package['curl'],File["$local_temp_folder/download_artifacts.ksh"]],
+	}
+
+	#We will only download the artifacts once, when executed successfully creates the flag file:
+	file { "$local_temp_folder/download_artifacts.executed-flag":
+		ensure  => file,
+		content => '',
+		owner   => 'root',
+		group   => 'root',
+		mode    => 0440,
+		replace => false,
+		require => [File["$local_temp_folder"],Exec["$local_temp_folder/download_artifacts.ksh"]],
+	}
+	
 }
+
Index: modules/cep/manifests/pkgs.pp
===================================================================
--- modules/cep/manifests/pkgs.pp	(revision 231)
+++ modules/cep/manifests/pkgs.pp	(working copy)
@@ -53,7 +53,10 @@
 	}
 
 	if "$serverversion" == "" {
-		#We are running without a master, we will locate and run the download_artifacts.ksh locally
+		#We are running without a master, we shall run the download_artifacts.ksh locally
 		include cep::download
+		$files_source = "$cep::download::local_temp_folder"
+	}else{
+		$files_source = "puppet:///modules/$module_name"
 	}
 }
shani-mac:puppet shani$ svn commit -m "Fixing problems with the puppet things"
Sending        miscellaneous/CEP_Install_via_puppet.sh
Sending        modules/cep/manifests/config.pp
Sending        modules/cep/manifests/deploy.pp
Sending        modules/cep/manifests/download.pp
svn: E160042: Commit failed (details follow):
svn: E160042: File or directory 'modules/cep/manifests/download.pp' is out of date; try updating
svn: E160024: resource out of date; try updating
shani-mac:puppet shani$ svn commit -m "Fixing problems with the puppet things" miscellaneous/CEP_Install_via_puppet.sh 
Sending        miscellaneous/CEP_Install_via_puppet.sh
Transmitting file data .
Committed revision 232.
shani-mac:puppet shani$ svn commit -m "Fixing problems with the puppet things" modules/cep/manifests/config.pp 
Sending        modules/cep/manifests/config.pp
Transmitting file data .
Committed revision 233.
shani-mac:puppet shani$ svn commit -m "Fixing problems with the puppet things" modules/cep/manifests/deploy.pp 
Sending        modules/cep/manifests/deploy.pp
Transmitting file data .
Committed revision 234.
shani-mac:puppet shani$ svn commit -m "Fixing problems with the puppet things" modules/cep/manifests/download.pp 
Sending        modules/cep/manifests/download.pp
svn: E160042: Commit failed (details follow):
svn: E160042: File or directory 'download.pp' is out of date; try updating
svn: E160024: resource out of date; try updating
shani-mac:puppet shani$ svn update modules/cep/manifests/download.pp 
Updating 'modules/cep/manifests/download.pp':
At revision 234.
shani-mac:puppet shani$ svn commit -m "Fixing problems with the puppet things" modules/cep/manifests/download.pp 
Sending        modules/cep/manifests/download.pp
svn: E160042: Commit failed (details follow):
svn: E160042: File or directory 'download.pp' is out of date; try updating
svn: E160024: resource out of date; try updating
shani-mac:puppet shani$ svn up
Updating '.':
At revision 234.
shani-mac:puppet shani$ pwd
/Users/shani/Documents/SVN/puppet
shani-mac:puppet shani$ cd .svn/
shani-mac:.svn shani$ ls
entries		format		pristine	tmp		wc.db
shani-mac:.svn shani$ pwd
/Users/shani/Documents/SVN/puppet/.svn
shani-mac:.svn shani$ cd ..
shani-mac:puppet shani$ cd modules/cep/manifests/
shani-mac:manifests shani$ cd .svn
-bash: cd: .svn: No such file or directory
shani-mac:manifests shani$ ls -la
total 48
drwxr-xr-x  8 shani  HAIFA\Domain Users   272 May  4 14:43 .
drwxr-xr-x  6 shani  HAIFA\Domain Users   204 May  4 14:43 ..
-rwxr-xr-x  1 shani  HAIFA\Domain Users  2358 May  4 10:09 config.pp
-rwxr-xr-x  1 shani  HAIFA\Domain Users  1827 May  4 10:09 deploy.pp
-rw-r--r--  1 shani  HAIFA\Domain Users  1512 May  4 14:50 download.pp
-rwxr-xr-x  1 shani  HAIFA\Domain Users   226 Mar  6 15:35 init.pp
-rw-r--r--  1 shani  HAIFA\Domain Users  1685 May  4 10:09 pkgs.pp
-rwxr-xr-x  1 shani  HAIFA\Domain Users    98 Mar  6 15:35 validation.pp
shani-mac:manifests shani$ cd ..
shani-mac:cep shani$ ls -la
total 16
drwxr-xr-x  6 shani  HAIFA\Domain Users   204 May  4 14:43 .
drwxr-xr-x  3 shani  HAIFA\Domain Users   102 Mar  6 15:35 ..
-rw-r--r--@ 1 shani  HAIFA\Domain Users  6148 May  4 14:43 .DS_Store
drwxr-xr-x  3 shani  HAIFA\Domain Users   102 May  4 14:41 files
drwxr-xr-x  8 shani  HAIFA\Domain Users   272 May  4 14:43 manifests
drwxr-xr-x  3 shani  HAIFA\Domain Users   102 Mar  6 15:35 templates
shani-mac:cep shani$ ls .DS_Store 
.DS_Store
shani-mac:cep shani$ cat .DS_Store 
Bud%
  @? @ @ @
          E%DSDB` @ @ @shani-mac:cep shani$ 
shani-mac:cep shani$ 
shani-mac:cep shani$ cd ..
shani-mac:modules shani$ ls -la
total 0
drwxr-xr-x  3 shani  HAIFA\Domain Users  102 Mar  6 15:35 .
drwxr-xr-x  8 shani  HAIFA\Domain Users  272 May  4 14:48 ..
drwxr-xr-x  6 shani  HAIFA\Domain Users  204 May  4 14:43 cep
shani-mac:modules shani$ cd ..
shani-mac:puppet shani$ ls -la
total 168
drwxr-xr-x  8 shani  HAIFA\Domain Users    272 May  4 14:48 .
drwxr-xr-x  7 shani  HAIFA\Domain Users    238 Apr 27 13:48 ..
-rw-r--r--@ 1 shani  HAIFA\Domain Users   6148 May  4 14:39 .DS_Store
drwxr-xr-x  7 shani  HAIFA\Domain Users    238 May  4 14:56 .svn
-rw-r--r--  1 shani  HAIFA\Domain Users   2627 May  4 14:48 README.md
drwxr-xr-x  5 shani  HAIFA\Domain Users    170 May  4 14:39 miscellaneous
drwxr-xr-x  3 shani  HAIFA\Domain Users    102 Mar  6 15:35 modules
-rw-r--r--  1 shani  HAIFA\Domain Users  70190 May  4 14:48 puppet_instalation-README.pdf
shani-mac:puppet shani$ cd .svn/
shani-mac:.svn shani$ ls
entries		format		pristine	tmp		wc.db
shani-mac:.svn shani$ ls -la
total 272
drwxr-xr-x   7 shani  HAIFA\Domain Users     238 May  4 14:56 .
drwxr-xr-x   8 shani  HAIFA\Domain Users     272 May  4 14:48 ..
-rw-r--r--   1 shani  HAIFA\Domain Users       3 Mar  6 15:35 entries
-rw-r--r--   1 shani  HAIFA\Domain Users       3 Mar  6 15:35 format
drwxr-xr-x  17 shani  HAIFA\Domain Users     578 May  4 14:54 pristine
drwxr-xr-x   2 shani  HAIFA\Domain Users      68 May  4 14:54 tmp
-rw-r--r--   1 shani  HAIFA\Domain Users  131072 May  4 14:56 wc.db
shani-mac:.svn shani$ ls pristine/
27	46	61	63	68	6b	75	b6	cf	d5	da	df	e5	e7	e9
shani-mac:.svn shani$ cat wc.db 
SQLite format 3@  ?  ?-?
D??L
 C      ?

         	??C/H??qc!?viewNODES_BASENODES_BASECREATE VIEW NODES_BASE AS   SELECT * FROM nodes   WHERE op_depth = 0??QviewNODES_CURRENTNODES_CURRENTCREATE VIEW NODES_CURRENT AS   SELECT * FROM nodes AS n     WHERE op_depth = (SELECT MAX(op_depth) FROM nodes AS n2                       WHERE n2.wc_id = n.wc_id                         AND n2.local_relpath = n.local_relpath)f)?indexI_NODES_PARENTNODESCREATE INDEX I_NODES_PARENT ON NODES (wc_id, parent_relpath, op_depth)?9?QtableNODESNODESCREATE TABLE NODES (   wc_id  INTEGER NOT NULL REFERENCES WCROOT (id),   local_relpath  TEXT NOT NULL,   op_depth INTEGER NOT NULL,   parent_relpath  TEXT,   repos_id  INTEGER REFERENCES REPOSITORY (id),   repos_path  TEXT,   revision  INTEGER,   presence  TEXT NOT NULL,   moved_here  INTEGER,   moved_to  TEXT,   kind  TEXT NOT NULL,   properties  BLOB,   depth  TEXT,   checksum  TEXT REFERENCES PRISTINE (checksum),   symlink_target  TEXT,   changed_revision  INTEGER,   changed_date      INTEGER,   changed_author    TEXT,   translated_size  INTEGER,   last_mod_time  INTEGER,   dav_cache  BLOB,   file_external  TEXT,   PRIMARY KEY (wc_id, local_relpath, op_depth)   ))=indexsqlite_autoindex_NODES_1NODES?bableWC_LOCKWC_LOCKCREATE TABLE WC_LOCK (   wc_id  INTEGER NOT NULL  REFERENCES WCROOT (id),   local_dir_relpath  TEXT NOT NULL,   locked_levels  INTEGER NOT NULL DEFAULT -1,   PRIMARY KEY (wc_id, local_dir_relpath)  )-Andexsqlite_autoindex_WC_LOCK_1WC_LOCK}!!?EtableWORK_QUEUEWORK_QUEUECREATE TABLE WORK_QUEUE (   id  INTEGER PRIMARY KEY AUTOINCREMENT,   work  BLOB NOT NULL   )?
?wtableLOCKLOCKCREATE TABLE LOCK (   repos_id  INTEGER NOT NULL REFERENCES REPOSITORY (id),   repos_relpath  TEXT NOT NULL,   lock_token  TEXT NOT NULL,   lock_owner  TEXT,   lock_comment  TEXT,   lock_date  INTEGER,   PRIMARY KEY (repos_id, repos_relpath)   )';indexsqlite_autoindex_LOCK_1LOCKg3#?indexI_ACTUAL_CHANGELISTACTUAL_NODECREATE INDEX I_ACTUAL_CHANGELIST +#?indexI_ACTUAL_PARENTACTUAL_NODECREATE INDEX I_ACTUAL_PARENT ON ACTUAL_NODE (wc_id, parent_relpath)?<
                                                                                                       ##??tableACTUAL_NODEACTUAL_NODE
            CREATE TABLE ACTUAL_NODE (   wc_id  INTEGER NOT NULL REFERENCES WCROOT (id),   local_relpath  TEXT NOT NULL,   parent_relpath  TEXT,   properties  BLOB,   conflict_old  TEXT,   conflict_new  TEXT,   conflict_working  TEXT,   prop_reject  TEXT,   changelist  TEXT,   text_mod  TEXT,   tree_conflict_data  TEXT,   conflict_data  BLOB,   older_checksum  TEXT REFERENCES PRISTINE (checksum),   left_checksum  TEXT REFERENCES PRISTINE (checksum),   right_checksum  TEXT REFERENCES PRISTINE (checksum),   PRIMARY KEY (wc_id, local_relpath)   )5
?M      ?mtablePRISTINEPRISTINE                               I#indexsqlite_autoindex_ACTUAL_NODE_1ACTUAL_NODE
CREATE TABLE PRISTINE (   checksum  TEXT NOT NULL PRIMARY KEY,   compression  INTEGER,   size  INTEGER NOT NULL,   refcount  INTEGER NOT NULL,   md5_checksum  TEXT NOT NULL   )/
Cindexsqlite_autoindex_PRISTINE_1PRISTINE
                                         +?indexI_LOCAL_ABSPATHWCROOT	CREATE UNIQUE INDEX I_LOCAL_ABSPATH ON WCROOT (local_abspath)x?KtableWCROOTWCROOTCREATE TABLE WCROOT (   id  INTEGER PRIMARY KEY AUTOINCREMENT,   local_abspath  TEXT UNIQUE   )+?indexsqlite_autoindex_WCROOT_1WCROOD!]indexI_ROOTREPOSITORYCREATE INDEX I_ROOT ON REPOSITORY (root)D!]indexI_UUIDREPOSITORYCREATE INDEX I_UUID ON REPOSITORY (uuid)P++Ytablesqlite_sequencesqlite_sequenceCREATE TABLE sqlite_sequence(name,seq)?!!?tableREPOSITORYREPOSITORYCREATE TABLE REPOSITORY (   id INTEGER PRIMARY KEY AUTOINCREMENT,   root  TEXT UNIQUE NOT N??LUUhttps://github.com/ishkin/Proton.git1f444e3b-554c-1776-7a35-ce57b7db7bff
!???!WORREPOSITORYthub.cWCROOTkin/Proton.git
??'U	1f444e3b-554c-1776-7a35-ce57b7db7bff
??'U	https://github.com/ishkin/Proton.git
??	
n?	??D??*
?
 U
  ?
   ?
    =
?
?
"	?\i	Y$sha1$df7bfbd06c1261a01ae34bccbb2846d35c7cc31e#$md5 $d61900a6e5d4de40829f2a6eef007bef\i	Y$sha1$63685bbc975870f8f329e7f9a91109b6e712bcb2	6$md5 $dfe36e3a4e33fd777e0be6af05fc4182\i	Y$sha1$683e9064e82e680bf60c695ce0136c90917dcb4$md5 $f30cf808e62b173013941c8c53a98261]i	Y$sha1$61125cc03f966f4bad2dc9d5f513459a13c16782.$md5 $768e800bf882i78c00bfY$sha1$d5e757bef0660527b6bea795f867b381f4bc59cf
C$md5 $7ee513b54598e331a40abb3a30a3c060\
?$md5 $ae74db32d5eb708dacd0c5a5b70dd455ZY$sha1$b6b5f7557ac287490b5c3dfb0b52ebdfde03b6e9
                                        i       Y$sha1$da39a3ee5e6b4b0d3255bfef95601890afd80709$md5 $d41d8cd98f00b204e9800998ecf8427e[
i	Y$sha1$759dbfab384863a99158d7149eadff776fcef1d6b$md5 $172e188ac27eb8a55ae9dbe3dec8df03\	i	Y$sha1$6b73a9847b0a4229b30d5d26bd34605a1c54eadd.$md5 $1780be38aa028c54fd132fbe630de123i	Y$sha1$df20c75649fc21eaecb25b0b6784ffa17287c993?$md5 $23d51af953342ff34771db17eaf192a2\i	Y$sha1$e9dcf35d7acd6efaceedfb9b0a1ed44cabfbaf61?$md5 $493c2406f06fc05ad63abcaab235479a\Y$sha1$cfc2abd5864bd05f10940dc5b9cb922029e98da4O$md5 $1c3daa9cf9ecc5007444312e4e0e4eb8\Y$sha1$e5764a32039d879c125ef4b01125848b2dbd4a21	A$md5 $88ad3d9acf7c7602020055bd9b86d4de\i	Y$sha1$27cb8669923f5fca1743d3dce79b6f713ca5135a
                                                                                                                       ?$md5 $2285391db624d308785095e40407a50c\i	Y$sha1$e7bbc37aab154daf41a7e862b89d5c12bd9fb17e\$md5 $98bf5faab9a8cac849d35c7fe03aafe1\i	Y$sha1$4679869552bb806ec22ee4bb87744153ee50e789$md5 $4ae295bc76c31653ad480a387f231758\Y$sha1$68819233a1f35c7af12a6ba75a1feaacf11dfdbf
                   ?$md5 $5279cae7b127a1426843a368961ff50f

75?
?i
  ?h?2i$sha1$df7bfbd06c1261a01ae34bccbb2846d35c7cc31e2i$sha1$63685bbc975870f8f329e7f9a91109b6e712bcb22i$sha1$683e9064e82e680bf60c695ce0136c90917dcb442i$sha1$61125cc03f966f4bad2dc9d5f513459a13c167822i$sha1$d5e757bef0660527b6bea795f867b381f4bc59c2i$sha1$b6b5f7557ac287490b5c3dfb0b52ebdfde03b6e9
                                                2i$sha1$da39a3ee5e6b4b0d3255bfef95601890afd80709
                                                                                                2i$sha1$759dbfab384863a99158d7149eadff776fcef1d6
2i$sha1$6b73a9847b0a4229b30d5d26bd34605a1c54eadd	2i$sha1$df20c75649fc21eaecb25b0b6784ffa17287c992i$sha1$e9dcf35d7acd6efaceedfb9b0a1ed44cabfbaf612i$sha1$cfc2abd5864bd05f10940dc5b9cb922029e98da42i$sha1$e5764a32039d879c125ef4b01125848b2dbd4a212i$sha1$27cb8669923f5fca1743d3dce79b6f713ca5135a2i$sha1$e7bbc37aab154daf41a7e862b89d5c12bd9fb17e2i$sha1$4679869552bb806ec22ee4bb87744153ee50e7891i	$sha1$68819233a1f35c7af12a6ba75a1feaacf11dfdbf



?????>
?(file-install modules/cep/manifests/validation.pp 1 0 1 1)%t(file-install modules/cep/manifests/pkgs.pp 1 0 1 1)?<~(sync-file-flags miscellaneous/CEP_Install_via_puppet.sh)?z(file-remove 38 .svn/tmp/CEP_Install_via_puppet.sh.tmp)n?\(file-inst7?(file-i7modules/cep/man?(fi8v(file-commit modules/cep/manifests/deploy.pp 1 0 1 0)
?	???     modules/cep/mani	
?
?
??.??L??5	7	ai1?^modules/cep/manifests/pkgs.ppmodules/cep/manifeststrunk/puppet/modules/cep/manifests/pkgs.pp?normalfile()$sha1$6b73a9847b0a4229b30d5d26bd34605a1c54eadd?'i޴?@tali.yatzkar.haham(svn:wc:ra_dav:version-url 74 /ishkin/Proton.git/!svn/ver/155/trunk/puppet/modules/cep/manifests/pkgs.pp)?F	7	a4i1?^modules/cep/manifests/init.ppmodules/cep/manifeststrunk/puppet/modules/cep/manifests/init.pp?normalfile(svn:executable 1 *)$sha1$df20c75649fc21eaecb25b0b6784ffa17287c993u'i޴?@tali.yatzkar.haham(svn:wc:ra_dav:version-url 74 /ishkin/Proton.git/!svn/ver/117/trunk/puppet/modules/cep/manifests/init.pp)?A	7	ii1?fmodules/cep/manifests/download.ppmodules/cep/manifeststrunk/puppet/modules/cep/manifests/download.pp?normalfile()$sha1$e9dcf35d7acd6efaceedfb9b0a1ed44cabfbaf61?'i޴?@tali.yatzkar.haham(svn:wc:ra_dav:version-url 78 /ishkin/Proton.git/!svn/ver/155/trunk/puppet/modules/cep/manifests/download.pp)?L	7	e4i1?bmodules/cep/manifests/deploy.ppmodules/cep/manifeststrunk/puppet/modules/cep/manifests/deploy.pp?normalfile(svn:executable 1 *)$sha1$cfc2abd5864bd05f10940dc5b9cb922029e98da4u'i޴?@tali.yatzkar.haham(svn:wc:ra_dav:version-url 76 /ishkin/Proton.git/!svn/ver/117/trunk/puppet/7odules/e4i1?bmodules/cep/manifests/config.ppmodules/cep/manifeststrunk/puppet/modules/cep/manifests/config.pp?normalfile(svn:executable 1 *)$sha1$e5764a32039d879c125ef4b01125848b2dbd4a21u'i޴?@tali.yatzkar.haham(svn:wc:ra_dav:version-url 76 /ishkin/Proton.git/!svn/ver/117/trunk/puppet/modules/cep/manifests/config.pp)p
                                                                                   	#	Qmodules/cep/manifestsmodules/ceptrunk/puppet/modules/cep/manifests?incompletedirinfinity?`
                                                                 	#	I1?Fmodules/cep/filesmodules/ceptrunk/puppet/modules/cep/files?normaldir()infinity?'i޴?@tali.yatzkar.haham(svn:wc:ra_dav:version-url 62 /ishkin/Proton.git/!svn/ver/133/trunk/puppet/modules/cep/files)
                                   ?`	/	wi1?tmodules/cep/files/download_artifacts.kshmodules/cep/filestrunk/puppet?\
	/	wi1?tmodules/cep/files/download_artifacts.kshmodules/cep/filestrunk/puppet/modules/cep/files/download_artifacts.ksh?normalfile()$sha1$27cb8669923f5fca1743d3dce79b6f713ca5135a?'i޴?@tali.yatzkar.haham
                                                                                            ?-ak}:?(svn:wc:ra_dav:version-url 85 /ishkin/Proton.git/!svn/ver/133/trunk/puppet/modules/cep/files/download_artifacts.ksh)X  #       =modules/cepmodulestrunk/pA1?>miscellaneoustrunk/puppet/miscellaneous?normaldir()infinity?'i޴?@tali.yatzkar.haham(svn:wc:ra_dav:version-url 58 /ishkin/Proton.git/!svn/ver/156/trunk/puppet/miscellaneous)9	'	c4i1?`miscellaneous/tomcat-users.xml?L	'	c4i1?`miscellaneous/tomcat-users.xmlmiscellaneoustrunk/puppet/miscellaneous/tomcat-users.xml?normalfile(svn:executable 1 *)$sha1$e7bbc37aab154daf41a7e862b89d5c12bd9fb17e?'i޴?@tali.yatzkar.haham\-ak}:?(svn:wc:ra_dav:version-url 75 /ishkin/Proton.git/!svn/ver/131/trunk/puppet/miscellaneous/tomcat-users.xml)?.	'	[i1?Xmiscellaneous/smoketest.shmiscellaneoustrunk/puppet/miscellaneous/smoketest.sh?normalfile()$sha1$4679869552bb806ec22ee4bb87744153ee50e789?'i޴?@tali.yatzkar.haham-ak}:?(svn:wc:ra_dav:version-url 71 /ishkin/Proton.git/!svn/ver/132/trunk/puppet/miscellaneous/smoketest.sh)?U	'	ui1?rmiscellaneous/CEP_Install_via_puppet.shmiscellaneoustrunk/puppet/miscellaneous/CEP_Install_via_puppet.sh?normalfile()$sha1$68819233a1f35c7af12a6ba75a1feaacf11dfdbf?'i޴?@tali.yatzkar.haham
                                                                    ?-ak}:?(svn:wc:ra_dav:version-url 84 /ishkin/Proton.g%trunk/puppet?incompletedpet/miscellaneous/CEP_Install_via_puppet.sh):	
????rdR:
'"      modules/cep/templates/.emptyf  Rmodules/cep/templates)	modules/cep/manifests/validation.pp#	modules/cep/manifests/pkgs.pp#	modules/cep/manifests/init.pp'	modules/cep/manifests/download.pp%	modules/cep/manifests/deploy.pp"modules/cep/manifests/config.pp!	modules/cep/manifests$.	modules/cep/files/download_artifacts.ksh
	modules/cep/files
       odules&$ miscellaneous/tomcat-users.xml 	miscellaneous/smoketest.s-	miscellaneous/CEP_Install_via_puppet.sh miscellaneous#
?{??{??'?????fx?modules/cep/templates	modules/cep	modules/cep/manifests	modules/cep/manifests	modules/cep/manifests	modules/cep/manifests	modules/cep/manifests"	modules/cep/manifests!	modules/cep$	modules/cep/files
	modules/cep
D?*???Lomiscellaneous	miscellaneous   miscellaneous 	
 C      ?

         	??C/H??q???mtriggernodes_insert_triggernodesCREATE TRIGGER nodes_insert_trigger AFTER INSERT ON nodes WHEN NEW.checksum IS NOT NULL BEGIN   UPDATE pristine SET refcount = refcount + 1   WHERE checksum = NEW.checksum; ENDc!?viewNODES_BASENODES_BASECREATE VIEW NODES_BASE AS   SELECT * FROM nodes   WHERE op_depth = 0?'?QviewNODES_CURRENTNODES_CURRENTCREATE VIEW NODES_CURRENT AS   SELECT * FROM nodes AS n     WHERE op_depth = (SELECT MAX(op_depth) FROM nodes AS n2                       WHERE n2.wc_id = n.wc_id                         AND n2.local_relpath = n.local_relpath)f)?indexI_NODES_PARENTNODESCREATE INDEX I_NODES_PARENT ON NODES (wc_id, parent_relpath, op_depth)?z?QtableNODESNODESCREATE TABLE NODES (   wc_id  INTEGER NOT NULL REFERENCES WCROOT (id),   local_relpath  TEXT NOT NULL,   op_depth INTEGER NOT NULL,   parent_relpath  TEXT,   repos_id  INTEGER REFERENCES REPOSITORY (id),   repos_path  TEXT,   revision  INTEGER,   presence  TEXT NOT NULL,   moved_here  INTEGER,   moved_to  TEXT,   kind  TEXT NOT NULL,   properties  BLOB,   depth  TEXT,   checksum  TEXT REFERENCES PRISTINE (checksum),   symlink_target  TEXT,   changed_revision  INTEGER,   changed_date      INTEGER,   changed_author    TEXT,   translated_size  INTEGER,   last_mod_time  INTEGER,   dav_cache  BLOB,   file_external  TEXT,   PRIMARY KEY (wc_id, local_relpath, op_depth)   ))=indexsqlite_autoindex_NODES_1NODES??ableWC_LOCKWC_LOCKCREATE TABLE WC_LOCK (   wc_id  INTEGER NOT NULL  REFERENCES WCROOT (id),   local_dir_relpath  TEXT NOT NULL,   locked_levels  INTEGER NOT NULL DEFAULT -1,   PRIMARY KEY (wc_id, local_dir_relpath)  )-Andexsqlite_autoindex_WC_LOCK_1WC_LOCK?!!?EtableWORK_QUEUEWORK_QUEUECREATE TABLE WORK_QUEUE (   id  INTEGER PRIMARY KEY AUTOINCREMENT,   work  BLOB NOT NULL   )?
?wtableLOCKLOCKCREATE TABLE LOCK (   repos_id  INTEGER NOT NULL REFERENCES REPOSITORY (id),   repos_relpath  TEXT NOT NULL,   lock_token  TEXT NOT NULL,   lock_owner  TEXT,   lock_comment  TEXT,   lock_date  INTEGER,   PRIMARY KEY (repos_id, repos_relpath)   )';indexsqlite_autoindex_LOCK_1LOCKg3#?indexI_ACTUAL_CHANGELISTACTUAL_NODECREATE INDEX I_ACTUAL_CHANGELIST +#?indexI_ACTUAL_PARENTACTUAL_NODECREATE INDEX I_ACTUAL_PARENT ON ACTUAL_NODE (wc_id, parent_relpath)?<
                                                                                                       ##??tableACTUAL_NODEACTUAL_NODE
            CREATE TABLE ACTUAL_NODE (   wc_id  INTEGER NOT NULL REFERENCES WCROOT (id),   local_relpath  TEXT NOT NULL,   parent_relpath  TEXT,   properties  BLOB,   conflict_old  TEXT,   conflict_new  TEXT,   conflict_working  TEXT,   prop_reject  TEXT,   changelist  TEXT,   text_mod  TEXT,   tree_conflict_data  TEXT,   conflict_data  BLOB,   older_checksum  TEXT REFERENCES PRISTINE (checksum),   left_checksum  TEXT REFERENCES PRISTINE (checksum),   right_checksum  TEXT REFERENCES PRISTINE (checksum),   PRIMARY KEY (wc_id, local_relpath)   )5
?M      ?mtablePRISTINEPRISTINE                               I#indexsqlite_autoindex_ACTUAL_NODE_1ACTUAL_NODE
CREATE TABLE PRISTINE (   checksum  TEXT NOT NULL PRIMARY KEY,   compression  INTEGER,   size  INTEGER NOT NULL,   refcount  INTEGER NOT NULL,   md5_checksum  TEXT NOT NULL   )/
Cindexsqlite_autoindex_PRISTINE_1PRISTINE
                                         +?indexI_LOCAL_ABSPATHWCROOT	CREATE UNIQUE INDEX I_LOCAL_ABSPATH ON WCROOT (local_abspath)x?KtableWCROOTWCROOTCREATE TABLE WCROOT (   id  INTEGER PRIMARY KEY AUTOINCREMENT,   local_abspath  TEXT UNIQUE   )+?indexsqlite_autoindex_WCROOT_1WCROOD!]indexI_ROOTREPOSITORYCREATE INDEX I_ROOT ON REPOSITORY (root)D!]indexI_UUIDREPOSITORYCREATE INDEX I_UUID ON REPOSITORY (uuid)P++Ytablesqlite_sequencesqlite_sequenceCREATE TABLE sqlite_sequence(name,seq)?!!?tableREPOSITORYREPOSITORYCREATE TABLE REPOSITORY (   id INTEGER PRIMARY KEY AUTOINCREMENT,   root  TEXT UNIQUE NOT N?8??m  uuid  TEXT NOT NULL   )3G!indexsqlite_autoindex_REPOSITORY_1REPOSITORY
     ?
      ?

	????u.8?s3?!indexI_EXTERNALS_DEFINEDEXTERNALSCREATE UNIQUE INDEX I_EXTERNALS_DEFINED ON EXTERNALS (wc_id,                                                       def_local_relpath,                                                       local_relpath)l1?indexI_EXTERNALS_PARENTEXTERNALSCREATE INDEX I_EXTERNALS_PARENT ON EXTERNALS (wc_id, parent_relpath)?V?{tableEXTERNALSEXTERNALSCREATE TABLE EXTERNALS (   wc_id  INTEGER NOT NULL REFERENCES WCROOT (id),   local_relpath  TEXT NOT NULL,   parent_relpath  TEXT NOT NULL,   repos_id  INTEGER NOT NULL REFERENCES REPOSITORY (id),   presence  TEXT NOT NULL,   kind  TEXT NOT NULL,   def_local_relpath         TEXT NOT NULL,   def_repos_relpath         TEXT NOT NULL,   def_operational_revision  TEXT,   def_revision              TEXT,   PRIMARY KEY (wc_id, local_relpath) )1Eindexsqlite_autoindex_EXTERNALS_1EXTERNALS?Etriggernodes_update_checksum_triggernodesCREATE TRIGGER nodes_update_checksum_trigger AFTER UPDATE OF checksum ON nodes WHEN NEW.checksum IS NOT OLD.checksum BEGIN   UPDATE pristine SET refcount = refcount + 1   WHERE checksum = NEW.checksum;   UPDATE pristine SET refcount = refcount - 1   WHERE checksum = OLD.checksum; END??mtriggernodes_delete_triggernodesCREATE TRIGGER nodes_delete_trigger AFTER DELETE ON nodes WHEN OLD.checksum IS NOT NULL BEGIN   UPDATE pristine SET refcount = refcount - 1   WHERE checksum = OLD.checksum; END??mtriggernodes_insert_triggernodesCREATE TRIGGER nodes_insert_trigger AFTER INSERT ON nodes WHEN NEW.checksum IS NOT NULL BEGIN   UPDATE pristine SET refcount = refcount + 1   WHERE checksum = NEW.checksum; ENDc!?viewNODES_BASENODES_BASECREATE VIEW NODES_BASE AS   SELECT * FROM nodes   WHERE op_depth = 0?'?QviewNODES_CURRENTNODES_CURRENTCREATE VIEW NODES_CURRENT AS   SELECT * FROM nodes AS n     WHERE op_depth = (SELECT MAX(op_depth) FROM nodes AS n2                       WHERE n2.wc_id = n.wc_id                         AND n2.local_relpath = n.local_relpath)f)?indexI_NODES_PARENTNODESCREATE INDEX I_NODES_PARENT ON NODES (wc_id, parent_relpath, op_depth))=indexsqlite_autoindex_NODES_1NODES?9?QtableNODESNODESCREATE TABLE NODES (   wc_id  INTEGER NOT NULL REFERENCES WCROOT (id),   local_relpath  TEXT NOT NULL,   op_depth INTEGER NOT NULL,   parent_relpath  TEXT,   repos_id  INTEGER REFERENCES REPOSITORY (id),   repos_path  TEXT,   revision  INTEGER,   presence  TEXT NOT NULL,   moved_here  INTEGER,   moved_to  TEXT,   kind  TEXT NOT NULL,   properties  BLOB,   depth  TEXT,   checksum  TEXT REFERENCES PRISTINE (checksum),   symlink_target  TEXT,   changed_revision  INTEGER,   changed_date      INTEGER,   changed_author    TEXT,   translated_size  INTEGER,   last_mod_time  INTEGER,   dav_cache  BLOB,   file_external  TEXT,   PRIMARY KEY (wc_id, local_relpath, op_depth)   )-Andexsqlite_autoindex_WC_LOCK_1WC_LOCK?bableWC_LOCKWC_LOCKCREATE TABLE WC_LOCK (   wc_id  INTEGER NOT NULL  REFERENCES WCROOT (id),   local_dir_relpath  TEXT NOT NULL,   locked_levels  INTEGER NOT NULL DEFAULT -1,   PRIMARY KEY (wc_id, local_dir_relpath)  )}!!?EtableWORK_QUEUEWORK_QUEUECREATE TABLE WORK_QUEUE (   id  INTEGER PRIMARY KEY AUTOINCREMENT,   work  BLOB NOT NULL   )


?       \?
 1
y+?	???G??M
               ??5	7	ai1?^modules/cep/manifests/pkgs.ppmodules/??	7	ai1?^modules/cep/manifests/pkgs.ppmodules/cep/manifeststrunk/puppet/modules/cep/manifests/pkgs.pp?normalfile()$sha1$6b73a9847b0a4229b30d5d26bd34605a1c54eadd?'i޴?@tali.yatzkar.haham.-ak}:?(svn:wc:ra_dav:version-url 74 /ishkin/Proton.git/!svn/ver/155/trunk/puppet/modules/cep/manifests/pkgs.pp)?P	7	a4i1?^modules/cep/manifests/init.ppmodules/cep/manifeststrunk/puppet/modules/cep/manifests/init.pp?normalfile(svn:executable 1 *)$sha1$df20c75649fc21eaecb25b0b6784ffa17287c993u'i޴?@tali.yatzkar.haham?-ak}:?(svn:wc:ra_dav:version-url 74 /ishkin/Proton.git/!svn/ver/117/trunk/puppet/modules/cep/manifests/init.pp)?K	7	ii1?fmodules/cep/manifests/download.ppmodules/cep/manifeststrunk/puppet/modules/cep/manifests/download.pp?normalfile()$sha1$e9dcf35d7acd6efaceedfb9b0a1ed44cabfbaf61?'i޴?@tali.yatzkar.haham?-ak}:?(svn:wc:ra_dav:versio??	7	ai1?^modules/cep/manifests/pkgs.ppmodules/cep/manifeststrunk/puppet/modules/cep/manifests/pkgs.pp?normalfile()$sha1$6b73a9847b0a4229b30d5d26bd34605a1c54eadd?'i޴?@tali.yatzkar.haham.-ak}:?(svn:wc:ra_dav:version-url 74 /ishkin/Proton.git/!svn/ver/155/trunk/puppet/modules/cep/manifests/pkgs.pp)?P	7	a4i1?^modules/cep/manifests/init.ppmodules/cep/manifeststrunk/puppet/modules/cep/manifests/init.pp?normalfile(svn:executable 1 *)$sha1$df20c75649fc21eaecb25b0b6784ffa17287c993u'i޴?@tali.yatzkar.haham?-ak}:?(svn:wc:ra_dav:version-url 74 /ishkin/Proton.git/!svn/ver/117/trunk/puppet/modules/cep/manifests/init.pp)?K	7	ii1?fmodules/cep/manifests/download.ppmodules/cep/manifeststrunk/puppet/modules/cep/manifests/download.pp?normalfile()$sha1$e9dcf35d7acd6efaceedfb9b0a1ed44cabfbaf61?'i޴?@tali.yatzkar.haham?-ak}:?(svn:wc:ra_dav:version-url 78 /ishkin/Proton.git/!svn/ver/155/trunk/puppet/modules/cep/manifests/download.pp)?`
                                                            	#	I1?Fmodules/cep/filesmodules/ceptrunk/puppet/modules/cep/files?normaldir()infinity?'i޴?@tali.yatzkar.haham(svn:wc:ra_dav:version-url 62 /ishkin/Proton.git/!svn/ver/133/trunk/puppet/modules/cep/files)?4	/	wi1?tmodules/cep/files/downloa??	7	ai1?^modules/cep/manifests/pkgs.ppmodules/cep/manifeststrunk/puppet/modules/cep/manifests/pkgs.pp?normalfile()$sha1$6b73a9847b0a4229b30d5d26bd34605a1c54eadd?'i޴?@tali.yatzkar.haham.-ak}:?(svn:wc:ra_dav:version-url 74 /ishkin/Proton.git/!svn/ver/155/trunk/puppet/modules/cep/manifests/pkgs.pp)?\
	/	wi1?tmodules/cep/files/download_artifacts.kshmodules/cep/filestrunk/puppet/modules/cep/files/download_artifacts.ksh?normalfile()$sha1$27cb8669923f5fca1743d3dce79b6f713ca5135a?'i޴?@tali.yatzkar.haham
                                                                                            ?1??n]@(svn:wc:ra_dav:version-url 85 /ishkin/Proton.git/!svn/ver/133/trunk/puppet/modules/cep/files/download_artifacts.ksh)?L	'	c4i1?`miscellaneous/tomcat-users.xmlmiscellaneoustrunk/puppet/miscellaneous/tomcat-users.xml?normalfile(svn:executable 1 *)$sha1$e7bbc37aab154daf41a7e862b89d5c12bd9fb17e?'i޴?@tali.yatzkar.haham\-ak}:?(svn:wc:ra_dav:version-url 75 /ishkin/Proton.git/!svn/ver/131/trunk/puppet/miscellaneous/tomcat-users.xml)?.	'	[i1?Xmiscellaneous/smoketest.shmiscellaneoustrunk/puppet/miscellaneous/smoketest.sh?normalfile()$sha1$4679869552bb806ec22ee4bb87744153ee50e789?'i޴?@tali.yatzkar.haham-ak}:?(svn:wc:ra_dav:version-url 71 /ishkin/Proton.git/!svn/ver/132/trunk/puppet/miscellaneous/smoketest.sh)O	'	c4i1?`miscellaneous/tomcat-users.xmlmiscellaneoustrunk/puppet/miscellaneous/tomcat-users.xml?normalfile(svn:executable 1 *)$sha1$e7bbc37aab154daf41a7e862b89d5c12bd9fb17e?'i޴?@tali.yatzkar.haham\-ak}:?(svn:wc:ra_dav:version-url 75 /ishkin/Proton.git/!svn/ver/131/trunk/pu?pet/mise,5laneous/tomcat-users.xml)
??z
 zq?
    ?!?p	#	Q!1?Nmodules/cep/manifestsmodules/ceptrunk/puppet/modules/cep/manifests?incompletedir()infinity?'i޴?@tali.9i?6README.mdtrunk/puppet/README.md?normalfile()$sha1$d5e757bef0660527b6bea795f867b381f4bc59cf?1?Ǐcuri.shanis)?e	
C2Ή?(svnA?>miscellaneoustrunk/puppet/miscellaneous?normaldir()infinity?2???@uri.shani(svn:wc:ra_dav:version-url 58 /ishki%!trunk/puppet?incompletedir()infinity?1?Ǐcuri.shani?C 	'	ui?rmiscellaneous/CEP_Install_via_puppet.shmiscellaneoustrunk/puppet/mis?C 	'	ui?rmiscellaneous/CEP_Install_via_puppet.shmiscellaneoustrunk/puppet/miscellaneous/CEP_Install_via_puppet.sh?normalfile()$sha1$683e9064e82e680bf60c695ce0136c90917dcb44?2?#1??"(svn:wc:ra_dav:version-url 84 /ishkin/Proton.5?2modulestrunk/puppet/modules?normaldir()infinity?2???@uri.shani(svn:wc:ra_dav:version-url 52 /ishkin/Proton.git/!svn/ver/234/trunk/puppet/modules)?A%	#       =?:modules/cepmodulestrunk/puppet/modules/cep?normaldir()infinity?2???@uri.shani(svn:wc:ra_dav:version-url 56 /ishkin/Proton.git/!svn/ver/234/trunk/puppet/modules/cep)?k	#	Q1?Nmodules/cep/templatesmodules/ceptrunk/puppet/modules/cep/templates?normaldir()infinityu'i޴?@tali.yatzkar.haham(svn:wc:ra_dav:version-url 66 /ishkin/Proton.git/!svn/ver/117/trunk/puppet/modules/cep/templates)	`		7	?K	7	_4i?\modules/cep/templates/.emptymodules/cep/templatestrunk/puppet/modules/cep/templates/.empty?normalfile(svn:executable 1 *)$sha1$da39a3ee5e6b4b0d3255bfef95601890afd80709u'i޴?@tali.yatzkar.haham-ak}:?(svn:wc:ra_dav:version-url 73 /ishkin/Proton.git/!svn/ver/117/trunk/puppet/modules/cep/templates/.empty)?c$	#	Q?Nmodules/cep/manifestsmodules/ceptrunk/puppet/modules/cep/manifests?normaldir()infinity?2???@uri.shani(svn:wc:ra_dav:version-url 66 /ishkin/Proton.git/!svn/ver/234/trunk/puppet/modules/cep/manifests)	?a	7	m4i1?jmodules/cep/manifests/validation.ppmodules/cep/manifeststrunk/puppet/modules/cep/manifests/validation.pp?normalfile(svn:executable 1 *)$sha1$759dbfab384863a99158d7149eadff776fcef1d6u'i޴?@tali.yatzkar.hahamb-ak}:?(svn:wc:ra_dav:version-url 80 /ishkin/Proton.git/!svn/ver/117/trunk/puppet/modules/cep/manifests/validation.pp)?e	9i?6README.mdtrunk/puppet/README.md?normalfile()$sha1$d5e757bef0660527b6bea795f867b381f4bc59cf?1?Ǐcuri.shani
C2Ή?(svn%?"trunk/puppet?normaldir()infinity?2???@uri.shani(svn:wc:ra_dav:version-url 44 /ishkin/Proton.git/!svn/ver/234/trunk/puppet)?E"	7	e4i?bmodules/cep/manifests/deploy.ppmodules/cep/manifeststrunk/puppet/modules/cep/manifests/deploy.pp?normalfile(svn:executable 1 *)$sha1$df7bfbd06c1261a01ae34bccbb2846d35c7cc31e?2???@#1??%x@(svn:wc:ra_dav:version-url 76 /ishkin/Proton.git/!svn/ver/234/trunk/puppet/modules/cep/manifests/deploy.pp)?E!	7	e4i?bmodules/cep/manifests/config.ppmodules/cep/manifeststrunk/puppet/modules/cep/manifests/config.pp?normalfile(svn:executable 1 *)$sha1$63685bbc975870f8f329e7f9a91109b6e712bcb2?2??	61?溨?(svn:wc:ra_dav:version-url 76 /ishkin/Proton.git/!svn/ver/233/trunk/puppet/modules/cai?^puppet_instalation-README.pdftrunk/puppet/puppet_instalation-README.pdf?normalfile()$sha1$61125cc03f966f4bad2dc9d5f513459a13c16782?1?Ǐcuri.shani.2Ή?(svn:wc:ra_dav:version-url 74 /ishkin/Proton.git/!svn/ver/178/trunk/puppet/puppet_in'b?"atioai?^puppet_instalation-README.pdftrunk/puppet/puppet_instalation-README.pdf?normalfile()$sha1$61125cc03f966f4bad2dc9d5f513459a13c16782?1?Ǐcuri.shani.2Ή?(svn:wc:ra_dav:version-url 74 /ishkin/Proton.git/!svn/ver/178/trunk/puppet/puppet_i%?"trunk/puppet?normaldir()infinity?1?Ǐcuri.shani(svn:wc:ra_dav:version-url 44 /ishkin/Proton.git/!svn/ver/228/trunk/puppe%!?"trunk/puppet?incompletedir()infinity?1?Ǐcuri.shani(svn:wc:ra_dav:version-url 44 /ishkin/Proton.git/!svn/ver/228/trunk/puppet)shani-mac:.svn shani$ q
-bash: q: command not found
shani-mac:.svn shani$ 
shani-mac:.svn shani$ ls
entries		format		pristine	tmp		wc.db
shani-mac:.svn shani$ mv wc.db wc_.db_
shani-mac:.svn shani$ pwd
/Users/shani/Documents/SVN/puppet/.svn
shani-mac:.svn shani$ cd ..
shani-mac:puppet shani$ svn up
svn: E155036: Please see the 'svn upgrade' command
svn: E155036: Working copy '/Users/shani/Documents/SVN/puppet' is an old development version (format 12); to upgrade it, use a format 18 client, then use 'tools/dev/wc-ng/bump-to-19.py', then use the current client
shani-mac:puppet shani$ cd ,svn
-bash: cd: ,svn: No such file or directory
shani-mac:puppet shani$ cd .svn
shani-mac:.svn shani$ ls
entries		format		pristine	tmp		wc_.db_
shani-mac:.svn shani$ mv wc_.db_ wc.db
shani-mac:.svn shani$ svn up
Updating '.':
At revision 234.
shani-mac:.svn shani$ svn commit
svn: E200009: Commit failed (details follow):
svn: E200009: '/Users/shani/Documents/SVN/puppet/.svn' is not under version control
shani-mac:.svn shani$ ls
entries		format		pristine	tmp		wc.db
shani-mac:.svn shani$ cd ..
shani-mac:puppet shani$ ls
README.md			modules
miscellaneous			puppet_instalation-README.pdf
shani-mac:puppet shani$ pwd
/Users/shani/Documents/SVN/puppet
shani-mac:puppet shani$ svn up
Updating '.':
At revision 234.
shani-mac:puppet shani$ svn commit
svn: E205007: Commit failed (details follow):
svn: E205007: Could not use external editor to fetch log message; consider setting the $SVN_EDITOR environment variable or using the --message (-m) or --file (-F) options
svn: E205007: None of the environment variables SVN_EDITOR, VISUAL or EDITOR are set, and no 'editor-cmd' run-time configuration option was found
shani-mac:puppet shani$ svn commit -m "fixing puppet"
Sending        modules/cep/manifests/download.pp
svn: E160042: Commit failed (details follow):
svn: E160042: File or directory 'download.pp' is out of date; try updating
svn: E160024: resource out of date; try updating
shani-mac:puppet shani$ 
shani-mac:puppet shani$ svn up
Updating '.':
At revision 234.
shani-mac:puppet shani$ svn resolved .
shani-mac:puppet shani$ svn commit
svn: E205007: Commit failed (details follow):
svn: E205007: Could not use external editor to fetch log message; consider setting the $SVN_EDITOR environment variable or using the --message (-m) or --file (-F) options
svn: E205007: None of the environment variables SVN_EDITOR, VISUAL or EDITOR are set, and no 'editor-cmd' run-time configuration option was found
shani-mac:puppet shani$ svn commit -m "fixing puppet"
Sending        modules/cep/manifests/download.pp
svn: E160042: Commit failed (details follow):
svn: E160042: File or directory 'download.pp' is out of date; try updating
svn: E160024: resource out of date; try updating
shani-mac:puppet shani$ svn up --force 
.DS_Store                      README.md                      modules/
.svn/                          miscellaneous/                 puppet_instalation-README.pdf
shani-mac:puppet shani$ svn up --force modules/cep/manifests/download.pp 
Updating 'modules/cep/manifests/download.pp':
At revision 234.
shani-mac:puppet shani$ svn commit -m "fixing puppet"
Sending        modules/cep/manifests/download.pp
svn: E160042: Commit failed (details follow):
svn: E160042: File or directory 'download.pp' is out of date; try updating
svn: E160024: resource out of date; try updating
shani-mac:puppet shani$ svn commit --force -m "fixing puppet"
Subcommand 'commit' doesn't accept option '--force'
Type 'svn help commit' for usage.
shani-mac:puppet shani$ pwd
/Users/shani/Documents/SVN/puppet
shani-mac:puppet shani$ cd modules/cep/manifests/
shani-mac:manifests shani$ svn commit pkgs.pp -m "Fixing puppet problems"
Sending        pkgs.pp
svn: E160042: Commit failed (details follow):
svn: E160042: File or directory 'pkgs.pp' is out of date; try updating
svn: E160024: resource out of date; try updating
shani-mac:manifests shani$ svn update
Updating '.':
At revision 234.
shani-mac:manifests shani$ svn commit pkgs.pp -m "Fixing puppet problems"
Sending        pkgs.pp
svn: E160042: Commit failed (details follow):
svn: E160042: File or directory 'pkgs.pp' is out of date; try updating
svn: E160024: resource out of date; try updating
shani-mac:manifests shani$ cd ..
shani-mac:cep shani$ pwd
/Users/shani/Documents/SVN/puppet/modules/cep
shani-mac:cep shani$ svn checkout https://github.com/ishkin/Proton.git/trunk/puppet/modules/cep/manifests
Checked out revision 234.
shani-mac:cep shani$ ls
files		manifests	templates
shani-mac:cep shani$ cd manifests/
shani-mac:manifests shani$ ls
config.pp	deploy.pp	download.pp	init.pp		pkgs.pp		validation.pp
shani-mac:manifests shani$ ls -la
total 48
drwxr-xr-x  8 shani  HAIFA\Domain Users   272 May  4 14:43 .
drwxr-xr-x  6 shani  HAIFA\Domain Users   204 May  4 14:43 ..
-rwxr-xr-x  1 shani  HAIFA\Domain Users  2358 May  4 10:09 config.pp
-rwxr-xr-x  1 shani  HAIFA\Domain Users  1827 May  4 10:09 deploy.pp
-rw-r--r--  1 shani  HAIFA\Domain Users  1512 May  4 14:50 download.pp
-rwxr-xr-x  1 shani  HAIFA\Domain Users   226 Mar  6 15:35 init.pp
-rw-r--r--  1 shani  HAIFA\Domain Users  1685 May  4 10:09 pkgs.pp
-rwxr-xr-x  1 shani  HAIFA\Domain Users    98 Mar  6 15:35 validation.pp
shani-mac:manifests shani$ svn commit -m "fixing puppet problems"
Sending        download.pp
svn: E160042: Commit failed (details follow):
svn: E160042: File or directory 'download.pp' is out of date; try updating
svn: E160024: resource out of date; try updating
shani-mac:manifests shani$ cat download.pp 
class cep::download {
	#We are running without a master, we will execute the download_artifacts.ksh locally to download the required artifacts.

	#Define temporary storage location:
	$local_temp_folder = "/tmp/puppet_module_$module_name.tmp"


	#Create the temporary folder:
	file { "$local_temp_folder":
		ensure  => directory,
		owner  => 'root',
		group  => 'root',
		mode   => '750',
	}


	#copy the download script the temporary folder:
	file { "$local_temp_folder/download_artifacts.ksh":
		ensure  => file,
		source  => "puppet:///modules/$module_name/download_artifacts.ksh",
		owner   => 'root',
		group   => 'root',
		mode    => 0750,
		replace => true,
		require => File["$local_temp_folder"],
	}


	#Execute the download artifacts script only once, e.g: check for the ".executed-flag" file:
	exec { "$local_temp_folder/download_artifacts.ksh":
		command => "$local_temp_folder/download_artifacts.ksh",
		creates => "$local_temp_folder/download_artifacts.executed-flag",
		require => [Package['ksh'],Package['xmlstarlet'],Package['zip'],Package['unzip'],Package['curl'],File["$local_temp_folder/download_artifacts.ksh"]],
	}

	#We will only download the artifacts once, when executed successfully creates the flag file:
	file { "$local_temp_folder/download_artifacts.executed-flag":
		ensure  => file,
		content => '',
		owner   => 'root',
		group   => 'root',
		mode    => 0440,
		replace => false,
		require => [File["$local_temp_folder"],Exec["$local_temp_folder/download_artifacts.ksh"]],
	}
	
}

shani-mac:manifests shani$ cat download.pp 
class cep::download {
	#We are running without a master, we will execute the download_artifacts.ksh locally to download the required artifacts.

	#Define temporary storage location:
	$local_temp_folder = "/tmp/puppet_module_$module_name.tmp"


	#Create the temporary folder:
	file { "$local_temp_folder":
		ensure  => directory,
		owner  => 'root',
		group  => 'root',
		mode   => '750',
	}


	#copy the download script the temporary folder:
	file { "$local_temp_folder/download_artifacts.ksh":
		ensure  => file,
		source  => "puppet:///modules/$module_name/download_artifacts.ksh",
		owner   => 'root',
		group   => 'root',
		mode    => 0750,
		replace => true,
		require => File["$local_temp_folder"],
	}


	#Execute the download artifacts script only once, e.g: check for the ".executed-flag" file:
	exec { "$local_temp_folder/download_artifacts.ksh":
		command => "$local_temp_folder/download_artifacts.ksh",
		creates => "$local_temp_folder/download_artifacts.executed-flag",
		require => [Package['ksh'],Package['xmlstarlet'],Package['zip'],Package['unzip'],Package['curl'],File["$local_temp_folder/download_artifacts.ksh"]],
	}

	#We will only download the artifacts once, when executed successfully creates the flag file:
	file { "$local_temp_folder/download_artifacts.executed-flag":
		ensure  => file,
		content => '',
		owner   => 'root',
		group   => 'root',
		mode    => 0440,
		replace => false,
		require => [File["$local_temp_folder"],Exec["$local_temp_folder/download_artifacts.ksh"]],
	}
	
}

shani-mac:manifests shani$ clear

shani-mac:manifests shani$ clear












































shani-mac:manifests shani$ cat download.pp 
class cep::download {
	#We are running without a master, we will execute the download_artifacts.ksh locally to download the required artifacts.

	#Define temporary storage location:
	$local_temp_folder = "/tmp/puppet_module_$module_name.tmp"


	#Create the temporary folder:
	file { "$local_temp_folder":
		ensure  => directory,
		owner  => 'root',
		group  => 'root',
		mode   => '750',
	}


	#copy the download script the temporary folder:
	file { "$local_temp_folder/download_artifacts.ksh":
		ensure  => file,
		source  => "puppet:///modules/$module_name/download_artifacts.ksh",
		owner   => 'root',
		group   => 'root',
		mode    => 0750,
		replace => true,
		require => File["$local_temp_folder"],
	}


	#Execute the download artifacts script only once, e.g: check for the ".executed-flag" file:
	exec { "$local_temp_folder/download_artifacts.ksh":
		command => "$local_temp_folder/download_artifacts.ksh",
		creates => "$local_temp_folder/download_artifacts.executed-flag",
		require => [Package['ksh'],Package['xmlstarlet'],Package['zip'],Package['unzip'],Package['curl'],File["$local_temp_folder/download_artifacts.ksh"]],
	}

	#We will only download the artifacts once, when executed successfully creates the flag file:
	file { "$local_temp_folder/download_artifacts.executed-flag":
		ensure  => file,
		content => '',
		owner   => 'root',
		group   => 'root',
		mode    => 0440,
		replace => false,
		require => [File["$local_temp_folder"],Exec["$local_temp_folder/download_artifacts.ksh"]],
	}
	
}

shani-mac:manifests shani$ pwd
/Users/shani/Documents/SVN/puppet/modules/cep/manifests
shani-mac:manifests shani$ cd ..
shani-mac:cep shani$ ls
files		manifests	templates
shani-mac:cep shani$ cd files
shani-mac:files shani$ ls
download_artifacts.ksh
shani-mac:files shani$ svn commit -m "fixing problems with puppet"
shani-mac:files shani$ 
shani-mac:files shani$ pwd
/Users/shani/Documents/SVN/puppet/modules/cep/files
shani-mac:files shani$ ls -la
total 8
drwxr-xr-x  3 shani  HAIFA\Domain Users   102 May  4 14:41 .
drwxr-xr-x  6 shani  HAIFA\Domain Users   204 May  4 14:43 ..
-rw-r--r--  1 shani  HAIFA\Domain Users  3070 May  4 10:09 download_artifacts.ksh
shani-mac:files shani$ vi download_artifacts.ksh 

#!/bin/ksh93

if [[ -n "$1" ]]; then
  destination_folder="$1"
else
        destination_folder=$( cd `dirname $0` ; pwd)
fi

if [[ ! -w $destination_folder ]]; then
  print "Can not write to the destination_folder $destination_folder !"
  exit 1
fi


typeset -A download_list
git_base_url="https://raw.githubusercontent.com/ishkin/Proton/master"

download_list["tomcat-users.xml"]="$git_base_url/puppet/miscellaneous/tomcat-users.xml"

download_list["AuthoringTool.war"]="$git_base_url/artifacts/AuthoringTool.war"
download_list["AuthoringToolWebServer.war"]="$git_base_url/artifacts/AuthoringToolWebServer.war"
download_list["ProtonOnWebServer.war"]="$git_base_url/artifacts/ProtonOnWebServer.war"
download_list["ProtonOnWebServerAdmin.war"]="$git_base_url/artifacts/ProtonOnWebServerAdmin.war"

download_artifacts(){
        integer rc=0
        umask 0177
        for file_name in "${!download_list[@]}"; do
                print "Downloading ${download_list[$file_name]} as $file_name"
                /usr/bin/curl --silent --show-error --tlsv1 --connect-timeout 30 --location  "${download_list[$file_name]}" --output "$destination_folder/$file_name"
                rc=$rc+$?

                /bin/chmod 644 "$destination_folder/$file_name"
                rc=$rc+$?
        done
        return $rc
}

generate_random_password(){
        password_length=12
        random_password=''
        [[ -z "$random_password" ]] && random_password=$(/usr/bin/openssl rand -base64 $password_length)
@                                                                                                                         
"download_artifacts.ksh" 81L, 3070C
