class cep::pkgs {

	# Add oracle java 7 repository
	apt::ppa { 'ppa:webupd8team/java': }

	exec { 'set-licence-oracle-java7':
		command => '/bin/echo "oracle-java7-installer shared/accepted-oracle-license-v1-1 boolean true" | /usr/bin/debconf-set-selections',
		unless  => '/usr/bin/debconf-get-selections | /bin/egrep "oracle-java7-installer.*shared/accepted-oracle-license-v1-1.*boolean.*true"',
	}

	exec { 'apt-get update':
		command => '/usr/bin/apt-get update',
		onlyif  => "/usr/bin/awk -v last_updat=`/usr/bin/stat -c '%Y' /var/lib/apt/periodic/update-success-stamp` -v date=`date '+%s'` 'END{if ( date - last_updat > 1*24*60*60 ){exit 0}else{exit 1}}' /dev/null",
	}

	package { 'oracle-java7-installer':
		ensure  => latest,
	}

	package { 'puppet-module-puppetlabs-apt':
		ensure  => latest,
	}

	Package['puppet-module-puppetlabs-apt'] -> Apt::Ppa['ppa:webupd8team/java'] ->  Exec['set-licence-oracle-java7'] ->  Exec['apt-get update'] -> Package['oracle-java7-installer']

	host { "$fqdn":
		ensure  => present,
		comment => "Added by puppet $module_name module",
		ip      => '127.0.0.1',
	}
	host { "$hostname":
		ensure  => present,
		comment => "Added by puppet $module_name module",
		ip      => '127.0.0.1',
		require => Host["$fqdn"],
	}


	package { ['tomcat7','tomcat7-admin']:
		ensure  => latest,
		require => [Package['oracle-java7-installer'],Host["$hostname"]],
	}

	file_line { 'tomcat7_java_home':
		path    => '/etc/default/tomcat7',
		match   => '^JAVA_HOME=',
		line    => 'JAVA_HOME=/usr/lib/jvm/java-7-oracle',
		ensure  => present,
		require => File['/etc/default/tomcat7'],
	}

	file { '/etc/default/tomcat7':
		ensure  => present,
		owner   => 'root',
		group   => 'root',
		mode    => 0644,
		require => Package['tomcat7'],
	}

	package { ['zip','unzip','curl','ksh','xmlstarlet']:
		ensure => latest,
		require => Exec['apt-get update'],
	}

	if "$serverversion" == "" {
		#We are running without a master, we shall run the download_artifacts.ksh locally
		include cep::download
		$files_source = "$cep::download::local_temp_folder"
	}else{
		$files_source = "puppet:///modules/$module_name"
	}
}
