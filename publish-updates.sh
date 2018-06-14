set -e

REQUIRED_BRANCH=master

if [ $# = 0 ]
then
    echo >&2 "Pass relative paths to repos that are to be published as args"
    exit 1
fi

require_clean_work_tree () {
    # Update the index
    git update-index -q --ignore-submodules --refresh
    err=0

    # Disallow unstaged changes in the working tree
    if ! git diff-files --quiet --ignore-submodules --
    then
        echo >&2 "Cannot $1: you have unstaged changes."
        git diff-files --name-status -r --ignore-submodules -- >&2
        err=1
    fi

    # Disallow uncommitted changes in the index
    if ! git diff-index --cached --quiet HEAD --ignore-submodules --
    then
        echo >&2 "Cannot $1: your index contains uncommitted changes."
        git diff-index --cached --name-status -r --ignore-submodules HEAD -- >&2
        err=1
    fi

    if [ $err = 1 ]
    then
        echo >&2 "Please commit or stash them."
        #exit 1
    fi
}

echo "Checking if the state of repos is OK before publishing"
echo "---------------------------"


# validate inputs
for path in "$@"
do
    START_PATH=`pwd`
    echo
    echo "Checking '$path'"
    
    EXISTS=$(find "." -maxdepth 1 -type d -name "$path")
    #echo "EXISTS is [$EXISTS]"
    if [[ -z "$EXISTS" ]]
        then
            echo >&2 "    Path - Not OK"
            echo "Stopping publishing process"
            exit 1
        else
            echo "    Path - OK"
    fi
    
    cd $path
    BRANCH=$(git rev-parse --abbrev-ref HEAD)
    if [[ "$BRANCH" != "$REQUIRED_BRANCH" ]]
        then
            echo >&2 "    On branch '$BRANCH' - Not OK"
            echo "Stopping publishing process"
            exit 1
        else
            echo "    On branch '$BRANCH' - OK"
    fi

    require_clean_work_tree "publish updates"

    cd $START_PATH
done

if [ $err = 1 ]
then
    echo
    echo >&2 "Errors encountered, not publishing"
    exit 1
fi

# publish
for path in "$@"
do
    START_PATH=`pwd`
    echo "Publishing '$path'"
    cd $path
    git push
    cd $START_PATH
done



