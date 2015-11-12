#!/bin/bash

SUDO='sudo - root -c '
DEBUG='yes'

base_name=$(basename $0 .sh)
base_dir=$(cd `dirname $0` ; pwd)

log_file=/tmp/$base_name.$$.log

echo "log_file is $log_file"
stdout_file=/tmp/$base_name.$$.out
stderr_file=/tmp/$base_name.$$.err

touch $log_file
tail -f $log_file &

touch $stdout_file
tail -f $stdout_file &


print_to_log(){
  sev=$1 ; shift

  [[ $sev = 'DEBUG' ]] && [[ $DEBUG != 'yes' ]] && return 0

  formated_date=$(date '+%Y-%m-%d_%H:%M:%S')
  echo "[$formated_date] [$base_name] [$sev] [$*]" >> $log_file
  return 0
}

sudo_run(){
  sudo -n "$@" 1>>$stdout_file 2>$stderr_file
  if [[ $? -ne 0 ]]; then
    print_to_log "ERROR" "Failed to run $*"
    print_to_log "DEBUG" "stderr was: `cat $stderr_file`"
    exit 1
  else
    print_to_log "INFO" "Executed successfully: $*"
    return 0
  fi
}

sudo_test(){
  sudo -n "$@" 2>&1
  if [[ $? -ne 0 ]]; then
    print_to_log "WARNING" "Executed with with warnigns: $*"
    return 1
  else
    print_to_log "INFO" "Executed successfully: $*"
    return 0
  fi
}

download_puppet_cep_module(){
	git_base_url="https://raw.githubusercontent.com/ishkin/Proton/master/puppet/modules/cep"

	local_cep_folder="$base_dir/puppet/modules/cep"
	sudo_run mkdir -p "local_cep_folder"

	sudo_run mkdir -p "$base_dir/puppet/modules/cep/manifests"
	sudo_run mkdir -p "$base_dir/puppet/modules/cep/files"
	sudo_run mkdir -p "$base_dir/puppet/modules/cep/templates"

	for FILE in \
		"files/download_artifacts.ksh" \
		"manifests/init.pp" \
		"manifests/pkgs.pp" \
		"manifests/deploy.pp" \
		"manifests/config.pp" \
		"manifests/validation.pp" \
		; do
				sudo_run /usr/bin/curl --silent --show-error --fail --ssl-reqd --tlsv1 --connect-timeout 30 --location  "$git_base_url/$FILE" --output "$local_cep_folder/$FILE"
	done

	return 0
}


####################################################
####                                            ####
####                    MAIN                    ####
####                                            ####
####################################################

# Update Ubuntu
sudo_run apt-get update

#Install puppet
sudo_run apt-get -y install puppet-module-puppetlabs-apt

download_puppet_cep_module

sudo_run /usr/bin/puppet apply --parser future --modulepath=$base_dir/puppet/modules:/etc/puppet/modules:/usr/share/puppet/modules -e 'include cep' --debug
