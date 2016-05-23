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

generate_random_password(){
	password_length=12
	random_password=''
	[[ -z "$random_password" ]] && random_password=$(/usr/bin/openssl rand -base64 $password_length)
	[[ -z "$random_password" ]] && random_password=$(/bin/dd if=/dev/urandom bs=128 count=3 2>/dev/null| /usr/bin/sha256sum 2>/dev/null | /usr/bin/cut -c1-$password_length 2>/dev/null)
	[[ -z "$random_password" ]] && random_password=$(/bin/date "+$$.d1wE4.%s") && print "WARNING: Faind to generate a real random password!"
	[[ -z "$random_password" ]] && random_password="zdKDsnHnE1r+9lnX" && print "WARNING: Faind to generate a random password! will use the default hardcoded password"
	print "$random_password"
}

update_ProtonOnWebServerAdmin_war(){
	tmp_folder="/tmp/ProtonAdmin.properties.$$.tmp"
	mkdir -p "$tmp_folder" || return 1
	cd "$tmp_folder"
	unzip "$destination_folder/ProtonOnWebServerAdmin.war"

	sed -i -e 's/$//'	-e 's|^\(definitions-repository\)=.*|\1=/var/lib/cep/ProtonDefinitions|' ProtonAdmin.properties

	sed -i -e 's/$//' -e "s|^\(manager-password\)=.*|\1=$manager_password|" ProtonAdmin.properties

	zip "$destination_folder/ProtonOnWebServerAdmin.war" * 
	cd -
	rm -rf "$tmp_folder"
}

update_tomcat_users_xml(){

	xmlstarlet ed -L --update "/tomcat-users/user[@username='manager']/@password" -v "$manager_password" "$destination_folder/tomcat-users.xml"
	xmlstarlet ed -L --update "/tomcat-users/user[@username='admin']/@password"   -v "$admin_password"   "$destination_folder/tomcat-users.xml"
}

#Generate new random passwords for the tomcat admin and manager login
manager_password=`generate_random_password` || exit 1
admin_password=`generate_random_password` || exit 1

download_artifacts || exit 1

# fix definitions repository path, and manager password in ProtonOnWebServerAdmin.war:
update_ProtonOnWebServerAdmin_war || exit 1

update_tomcat_users_xml || exit 1

exit 0
