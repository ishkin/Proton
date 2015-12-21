class cep::download {
	#We are running without a master, we will locate and run the download_artifacts.ksh locally
	each(split($settings::modulepath, ':')) |$modules_folder| {

		exec {"exists $modules_folder/$module_name":
			 command => '/bin/true',
			 onlyif => "/usr/bin/test -e $modules_folder/$module_name",
		}

		exec { "$modules_folder/$module_name/download_artifacts.ksh":
			command => "$modules_folder/$module_name/files/download_artifacts.ksh",
			onlyif => "/usr/bin/test -e $modules_folder/$module_name",
			require => [Package['ksh'],Package['xmlstarlet'],Package['zip'],Package['unzip'],Package['curl'],Exec["exists $modules_folder/$module_name"]],
		}
	}
}
