class cep::deploy {
	file { '/var/lib/cep':
		ensure => directory,
		owner  => 'root',
		group  => 'tomcat7',
		mode   => '750',
	}

	#This following folder is changed using the download_artifacts.ksh scrip:
	file { '/var/lib/cep/ProtonDefinitions':
		ensure  => directory,
		owner  => 'tomcat7',
		group  => 'tomcat7',
		mode   => '750',
		require => File['/var/lib/cep'],
	}

	file { '/var/lib/tomcat7/webapps':
		ensure  => directory,
		require => Package['tomcat7'],
	}

	file { '/usr/share/tomcat7/webapps':
		ensure  => link,
		owner   => 'root',
		group   => 'root',
		target  => '/var/lib/tomcat7/webapps',
		replace => true,
		require => File['/var/lib/tomcat7/webapps'],
	}

	file { '/var/lib/tomcat7/webapps/AuthoringTool.war':
		ensure  => file,
		source  => "puppet:///modules/$module_name/AuthoringTool.war",
		owner   => 'tomcat7',
		group   => 'tomcat7',
		mode    => 0644,
		replace => true,
		require => File['/var/lib/tomcat7/webapps'],
	}

	file { '/var/lib/tomcat7/webapps/AuthoringToolWebServer.war':
		ensure  => file,
		source  => "puppet:///modules/$module_name/AuthoringToolWebServer.war",
		owner   => 'tomcat7',
		group   => 'tomcat7',
		mode    => 0644,
		replace => true,
		require => File['/var/lib/tomcat7/webapps'],
	}

	file { '/var/lib/tomcat7/webapps/ProtonOnWebServer.war':
		ensure  => file,
		source  => "puppet:///modules/$module_name/ProtonOnWebServer.war",
		owner   => 'tomcat7',
		group   => 'tomcat7',
		mode    => 0644,
		replace => true,
		require => File['/var/lib/tomcat7/webapps'],
	}

	file { '/var/lib/tomcat7/webapps/ProtonOnWebServerAdmin.war':
		ensure  => file,
		source  => "puppet:///modules/$module_name/ProtonOnWebServerAdmin.war",
		owner   => 'tomcat7',
		group   => 'tomcat7',
		mode    => 0644,
		replace => true,
		require => [File['/var/lib/tomcat7/webapps'],File['/var/lib/cep/ProtonDefinitions']],
	}
}
