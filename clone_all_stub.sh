# assuming you're in BatMass directory (create one locally if you don't yet have one)
cur_dir_name=${PWD##*/}
repo_hostname=""
repo_port=""
repo_user=""
repo_path_on_remote=""   # absolute path on the remote server hosting the repositories
local_enclosing_dir="" # the name of the local directory where all repositories sit side by side
repo_path_stub=ssh://$repo_user@$repo_hostname:$repo_port$repo_path_on_remote

echo "Current dir: " `pwd`
if [ $cur_dir_name != $local_enclosing_dir ]; then
	echo "You must be in $local_enclosing_dir directory to execute this script."
	exit 1
fi


echo
echo =================================
echo


echo "Current dir: " `pwd`
repo_path=$repo_path_stub/BatMass.git
echo Cloning repo from: $repo_path
#mkdir BatMass
git clone $repo_path
cd BatMass
git for-each-ref --sort=-committerdate refs/heads/


echo
echo =================================
echo


echo "Current dir: " `pwd`
repo_path=$repo_path_stub/MSFTBX.git
echo Cloning repo from: $repo_path
#mkdir MSFTBX
git clone $repo_path
cd MSFTBX
git for-each-ref --sort=-committerdate refs/heads/


echo
echo =================================
echo


cd ..
echo "Current dir: " `pwd`
repo_path=$repo_path_stub/BatMassLibs.git
echo Cloning repo from: $repo_path
#mkdir BatMassLibs
git clone $repo_path
cd BatMassLibs
git for-each-ref --sort=-committerdate refs/heads/


echo
echo =================================
echo


cd ..
echo "Current dir: " `pwd`
repo_path=$repo_path_stub/BatMassExt.git
echo Cloning repo from: $repo_path
#mkdir BatMassExt
git clone $repo_path
cd BatMassExt
git for-each-ref --sort=-committerdate refs/heads/


echo
echo =================================
echo


cd ..
echo "Current dir: " `pwd`
local_path="./NBP_Harness"
repo_path=$repo_path_stub/NBP_Harness/nb81_dist
scp_command_params="-r -P$repo_port $repo_user@$repo_hostname:$repo_path_on_remote/NBP_Harness ."

echo "Copying the Platform and Harness (scp $scp_command_params)"
if [ -d $local_path ]
then
	echo "Local path: '$local_path' already exists, won't copy from remote."
else
	scp $scp_command_params
fi


echo
echo =================================
echo


echo Done
