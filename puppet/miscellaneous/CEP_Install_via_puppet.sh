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
##tail -f $log_file &

touch $stdout_file
##tail -f $stdout_file &


print_to_log(){
  sev=$1 ; shift

  [[ $sev = 'DEBUG' ]] && [[ $DEBUG != 'yes' ]] && return 0

  formated_date=$(date '+%Y-%m-%d_%H:%M:%S')
  echo "[$formated_date] [$base_name] [$sev] [$*]" | /usr/bin/tee -a $log_file
  return 0
}

sudo_run(){
#  sudo -n "$@" 1>>$stdout_file 2>$stderr_file
  sudo -n "$@" 2>$stderr_file | /usr/bin/tee -a $stdout_file
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
	sudo_run mkdir -p "$local_cep_folder"
	sudo_run mkdir -p "$local_cep_folder/manifests"
	sudo_run mkdir -p "$local_cep_folder/files"
	sudo_run mkdir -p "$local_cep_folder/templates"

	for FILE in \
		"files/download_artifacts.ksh" \
		"manifests/init.pp" \
		"manifests/pkgs.pp" \
		"manifests/deploy.pp" \
		"manifests/download.pp" \
		"manifests/config.pp" \
		"manifests/validation.pp" \
		; do
				sudo_run /usr/bin/curl --silent --show-error --fail --tlsv1 --connect-timeout 30 --location  "$git_base_url/$FILE" --output "$local_cep_folder/$FILE"
				if [[ $FILE = *'.ksh' ]] || [[ $FILE = *'.sh' ]] ; then
					new_mode=755
				else
					new_mode=644
				fi
				sudo_run /bin/chmod $new_mode "$local_cep_folder/$FILE"
	done

	return 0
}

handle_hostname_changes(){
	#This fix will instruce cloud-init to control the /etc/hosts file
	#to ensure we can resolve the hostname using this file.
	if [[ -d /etc/cloud/cloud.cfg.d ]]; then
		sudo_run sh -c 'echo "manage_etc_hosts: True" > /etc/cloud/cloud.cfg.d/06_manage_etc_hosts.cfg'
	fi
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

handle_hostname_changes

sudo_run /usr/bin/puppet apply --parser future --modulepath=$base_dir/puppet/modules:/etc/puppet/modules:/usr/share/puppet/modules -e 'include cep' --debug

print_to_log 'INFO' 'Done!'

/bin/rm -f $stderr_file
/bin/rm -f $stdout_file
if [ $1 = "--docker"  ]; then
	/bin/rm -rf /var/lib/tomcat7/webapps/AuthoringTool /var/lib/tomcat7/webapps/ProtonOnWebServerAdmin/ /puppet/modules/cep/files/*.war
fi
exit 0
