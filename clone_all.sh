# assuming you're in BatMass directory
cur_dir_name=${PWD##*/} 
repo_hostname="141.214.4.18"
repo_port="12022"
repo_user="dmitriya"

echo "Current dir: " `pwd`
if [ $cur_dir_name != "BatMass" ]; then
	echo "You must be in BatMass directory to execute this script."
	exit 1
fi


echo 
echo =================================
echo


cd ..
echo "Current dir: " `pwd`
repo_path=ssh://$repo_user@$repo_hostname:$repo_port/repos/Umpire.git
echo Cloning repo from: $repo_path
#mkdir Umpire
git clone $repo_path
cd Umpire
git for-each-ref --sort=-committerdate refs/heads/


echo 
echo =================================
echo


cd ..
echo "Current dir: " `pwd`
repo_path=ssh://$repo_user@$repo_hostname:$repo_port/repos/BatMassExternalSuite.git
echo Cloning repo from: $repo_path
#mkdir BatMassExternalSuite
git clone $repo_path
cd BatMassExternalSuite
git for-each-ref --sort=-committerdate refs/heads/

echo 
echo =================================
echo


cd .
echo "Current dir: " `pwd`
repo_path=ssh://$repo_user@$repo_hostname:$repo_port/repos/MSFileToolboxModule.git
echo Cloning repo from: $repo_path
#mkdir MSFileToolBoxModule
git clone $repo_path
cd MSFileToolBoxModule
git for-each-ref --sort=-committerdate refs/heads/

echo 
echo =================================
echo


cd ../..
echo "Current dir: " `pwd`
repo_path=ssh://$repo_user@$repo_hostname:$repo_port/repos/BatMassLibs.git
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
repo_path=ssh://$repo_user@$repo_hostname:$repo_port/repos/msgui.git
echo Cloning repo from: $repo_path
#mkdir msgui
git clone $repo_path
cd msgui
git for-each-ref --sort=-committerdate refs/heads/

echo 
echo =================================
echo

cd ..
echo "Current dir: " `pwd`
local_path=./NBP_Platform_Harness/nbp801
repo_path=$repo_user@$repo_hostname:/repos/NBP_Platform_Harness
scp_command_params="-r -P $repo_port $repo_path $local_path"
echo "Copying the Platform and Harness (scp $scp_command_params)"
if [ -d $local_path ]
then
	echo "Local path: '$local_path' already exists, won't copy from remote."
else
	mkdir -p $local_path
	scp $scp_command_params
fi

echo 
echo =================================
echo


echo Done