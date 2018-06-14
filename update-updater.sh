set -e      # exit immediately if any command fails

from=build
target=updates
if [ -z "$1" ]
then
    path=`pwd`
else
    path=$1
fi

echo "Updating Update-Center in directory: '$path'"

in_dest=$(find "$path" -maxdepth 1 -type d -name "$target")
if [[ -n $in_dest ]]
then 
    echo "Dir '$target' found in '$path'. ($in_dest)"
else 
    echo "Dir you're running this script from must contain '$target' subdir. Exiting."
    exit 1
fi

in_src=$(find "$path/$from" -maxdepth 1 -type d -name "$target")
if [[ -n $in_src ]]
then 
    echo "Dir '$target' found in '$path/$from'. ($in_src)"
else 
    echo "Dir you're running this script from must contain './$from/$target' subdir. Exiting."
    exit 1
fi

echo "Moving existing '$in_dest' dir to '$in_dest-backup'"
mv $in_dest $in_dest-backup

echo "Copying from '$in_src' to '$in_dest'"
cp -r $in_src $in_dest

echo "You can now add all files to git and publish to master, the updates will go live immediately."
