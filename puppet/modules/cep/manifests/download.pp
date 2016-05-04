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

