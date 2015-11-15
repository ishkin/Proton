class cep::config {
	file { '/etc/tomcat7':
		ensure => directory,
	}

	file { '/etc/tomcat7/tomcat-users.xml':
		ensure  => file,
		source  => "puppet:///modules/$module_name/tomcat-users.xml",
		owner   => 'root',
		group   => 'root',
		mode    => 0644,
		replace => true,
		require => File['/etc/tomcat7'],
	}

	file { '/usr/share/tomcat7/bin':
		ensure => directory,
	}

	file { '/usr/share/tomcat7/bin/setenv.sh':
		ensure  => file,
		content => '',
		owner   => 'root',
		group   => 'root',
		mode    => 0755,
		replace => false,
		require => File['/usr/share/tomcat7/bin'],
	}

	file_line { '/usr/share/tomcat7/bin/setenv.sh_CATALINA_OPTS':
		path    => '/usr/share/tomcat7/bin/setenv.sh',
		match   => '^CATALINA_OPTS=',
		line    => 'CATALINA_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=8686 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Djava.security.egd=file:/dev/./urandom" #Remove the java.security.egd for extra security',
		ensure  => present,
		require => File['/usr/share/tomcat7/bin/setenv.sh'],
		notify  => Service['tomcat7'],
	}


	file_line { '/usr/share/tomcat7/bin/setenv.sh_CATALINA_BASE':
		path    => '/usr/share/tomcat7/bin/setenv.sh',
		match   => '^CATALINA_BASE=',
		line    => 'CATALINA_BASE=/var/lib/tomcat7',
		ensure  => present,
		require => File['/usr/share/tomcat7/bin/setenv.sh'],
		notify  => Service['tomcat7'],
	}
	
	file_line { '/usr/share/tomcat7/bin/setenv.sh_CATALINA_HOME':
		path    => '/usr/share/tomcat7/bin/setenv.sh',
		match   => '^CATALINA_HOME=',
		line    => 'CATALINA_HOME=/usr/share/tomcat7',
		ensure  => present,
		require => File['/usr/share/tomcat7/bin/setenv.sh'],
		notify  => Service['tomcat7'],
	}

	file_line { '/usr/share/tomcat7/bin/setenv.sh_CATALINA_TMPDIR':
		path    => '/usr/share/tomcat7/bin/setenv.sh',
		match   => '^CATALINA_TMPDIR=',
		line    => 'CATALINA_TMPDIR=/tmp/tomcat7-tomcat7-tmp',
		ensure  => present,
		require => File['/usr/share/tomcat7/bin/setenv.sh'],
		notify  => Service['tomcat7'],
	}

	file_line { '/usr/share/tomcat7/bin/setenv.sh_CATALINA_PID':
		path    => '/usr/share/tomcat7/bin/setenv.sh',
		match   => '^CATALINA_PID=',
		line    => 'CATALINA_PID="/var/run/tomcat7.pid"',
		ensure  => present,
		require => File['/usr/share/tomcat7/bin/setenv.sh'],
		notify  => Service['tomcat7'],
	}
}
