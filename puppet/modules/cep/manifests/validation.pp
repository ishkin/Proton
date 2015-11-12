class cep::validation {
	service { 'tomcat7':
		ensure     => running,
		enable     => true,
	}
}
