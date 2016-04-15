# BatMass website HowTo
The site is built with [Hugo](https://gohugo.io/) (currently Hugo v.0.15) using [Material Docs](https://github.com/digitalcraftsman/hugo-material-docs) theme.


# How to assemble the site
In a command line change to the website directory.  
`hugo --theme=hugo-material-docs`  
The static assembled website will be in `<website-path>/public`.  
__IMPORTANT:__ in the `config.toml` file change `baseurl` to wherever it will be hosted, e.g. to `baseurl = "http://chhh.github.io/hugo-website-test/"`. The trailing slash is important.


# To run/test the website locally
No webserver install is needed, Hugo has one built in. In a command line change to the website directory.  
`hugo serve --theme=hugo-material-docs` to start a local server, the website will be available at http://localhost:1313.  
It's that simple.
__IMPORTANT:__ in the `config.toml` file change `baseurl` to `baseurl = "http://localhost:1313/"`. The trailing slash is important.


# To host the site on GitHub
Create a repository, create an orphaned branch named `gh-pages` and push the contents of the assembled site (from `<website-path>/public`) to that branch on github.  
```bash
# Create a new orphand branch (no commit history) named gh-pages
git checkout --orphan gh-pages

# Unstage all files
git rm --cached $(git ls-files)

# Grab one file from the master branch so we can make a commit
git checkout master README.md

# Add and commit that file
git add .
git commit -m "INIT: initial commit on gh-pages branch"

cp <website-path>/public .
git add .
git commit -m "New version of the website"

# Push to remote gh-pages branch
git push origin gh-pages
```
Here is a link to a more detailed tutorial: https://gohugo.io/tutorials/github-pages-blog/
