# Installation options of CEP Proton

The Proton utility can be deployed in several ways on Ubuntu 14:
## 1 Using a puppet master.
 * Download the puppet folder for Proton, and work in the puppet/modules/cep folder there.
You can download single folder from github using svn:
svn checkout https://github.com/ishkin/Proton.git/trunk/puppet
 * We assume the puppet master environment is already installed and working.
 * Change the files permissions: chmod 644 ./manifests/*.pp ./files/* and chmod
+x ./files/download_artifacts.ksh.
 * Install the APT module on your master (e.g. puppet module install puppetlabs-apt),
make sure its location is accessible to the client (via the modulepath puppet settings). It is
possible that you will need to copy, or make soft links to that location.
 * Run once the script ./files/download_artifacts.ksh on the puppet master - to get the updated
version of CEP *.war files into the same folder, e.g. ./files/.
 * Add the CEP module to the client manifests (e.g. “include cep”).
 * The tomcat admin/manager passwords are randomly generated during the artifacts download
stage (e.g. when running the ./files/download_artifacts.ksh in the puppet master). They are
used during the clients’ deployment. Steps should be taken if different password for each
machine is needed.
 * Rerunning the ./files/download_artifacts.ksh in the puppet master will download a new
version of CEP which will be pushed to all the puppet clients.

## 2 Using a standalone puppet client
 * Download the puppet folder for Proton (see fist bullet above, e.g. into the /tmp/ folder, than
work in the puppet/modules/cep folder).
 * Install the puppet-module-puppetlabs-apt package (e.g. run sudo apt-get install
puppet-module-puppetlabs-apt)
 * Change the files permissions: chmod 644 ./manifests/*.pp ./files/* and chmod
    +x ./files/download_artifacts.ksh.
 * Make sure the /etc/hosts file contain an entry with the local machine hostname
 * Install CEP using puppet. Note that the <puppet_modules_path> should point to the full path
of your newly created module folder (e.g “/tmp/puppet/modules”):
```
        sudo /usr/bin/puppet apply --parser future --
        modulepath=<puppet_modules_path>:/etc/puppet/modules:/usr/share/puppe
        t/modules -e 'include cep' --debug
```
## 3. Install via a shell script:
 * The user running the script should be able to run sudo commands without a password prompt.
 * Download the CEP_Install_via_puppet.sh script file from gitub at puppet/miscellaneous/
folder and execute it.
 * The script will download all the necessary files to the same folder it's on, install and configure
