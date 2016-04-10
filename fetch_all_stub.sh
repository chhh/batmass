# execute this script from BatMass directory only
local_enclosing_dir="" # the name of the local directory where all repositories sit side by side

echo "Fetching all BatMass repositories, current dir: "
echo "  "`pwd`
cur_dir_name=${PWD##*/}
if [ $cur_dir_name != $local_enclosing_dir ]; then
	echo "You must be in $local_enclosing_dir directory to execute this script."
	exit 1
fi


cd BatMass
echo `pwd`
git fetch --all
git pull --ff-only
cd ..

echo
echo =================================
echo

cd BatMassLibs
echo `pwd`
git fetch --all
git pull --ff-only
cd ..

echo
echo =================================
echo


cd BatMassExt
echo `pwd`
git fetch --all
git pull --ff-only
cd ..

echo
echo =================================
echo

cd MSFTBX
echo `pwd`
git fetch --all
git pull --ff-only
cd ..

echo
echo =================================
echo
