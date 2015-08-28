
echo `pwd` 
git fetch --all
git pull --ff-only
cd ..

echo 
echo =================================
echo

cd Umpire
echo `pwd` 
git fetch --all
git pull --ff-only
cd ..

echo 
echo =================================
echo

cd BatMassExternalSuite
echo `pwd` 
git fetch --all
git pull --ff-only

echo 
echo =================================
echo

cd MSFileToolBoxModule
echo `pwd` 
git fetch --all
git pull --ff-only

echo 
echo =================================
echo

cd MSFileToolboxLibs
echo `pwd` 
git fetch --all
git pull --ff-only
cd ../../..

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

cd msgui
echo `pwd` 
git fetch --all
git pull --ff-only
cd ..
