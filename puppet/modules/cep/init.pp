class cep {
	include apt
	include stdlib
	include cep::pkgs
	include cep::deploy
	include cep::config
	include cep::validation

	Class['apt'] -> Class['cep::pkgs'] -> Class['cep::deploy'] -> Class['cep::config'] -> Class['cep::validation']
}
