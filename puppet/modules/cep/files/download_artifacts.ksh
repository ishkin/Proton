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

fix_definitions_repository_path(){
	tmp_folder="/tmp/ProtonAdmin.properties.$$.tmp"
	mkdir -p "$tmp_folder" || return 1
	cd "$tmp_folder"
	unzip "$destination_folder/ProtonOnWebServerAdmin.war"
	sed -i -e 's/$//'	-e 's|^\(definitions-repository\)=.*|\1=/var/lib/cep/ProtonDefinitions|' ProtonAdmin.properties
	zip "$destination_folder/ProtonOnWebServerAdmin.war" * 
	cd -
	rm -rf "$tmp_folder"
}

download_artifacts || exit 1
fix_definitions_repository_path || exit 1

exit 0

