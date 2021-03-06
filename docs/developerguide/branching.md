# The branching model

A "branching model" defines how, when, and where developers commit the changes. 

The WattDepot branch model is a simplified version of the [nvie branch model](http://nvie.com/posts/a-successful-git-branching-model/).

## Centralized "truth" repository

There is one central "truth" repository, origin, which is located at [http://github.com/wattdepot/wattdepot](http://github.com/wattdepot/wattdepot). It is the central push-pull location. Developers can of course clone their own repository and do whatever they want locally, but our model controls the structure of this repo.

## Two main branches: master and develop

There are two main branches:

  * [master](https://github.com/wattdepot/wattdepot/tree/master). The HEAD of master always reflects a production-ready state. 

  * [develop](https://github.com/wattdepot/wattdepot/tree/develop). The HEAD of develop always reflects a state with the latest delivered development changes for the next release.

When the source code in the develop branch reaches a stable point and
is ready to be released, all of the changes should be merged back into
master somehow and then tagged with a release number.

## Making releases

Most non-documentation implementation effort should be committed to the develop branch.  When appropriate, the develop branch will be merged into master and a release will be made.  This procedure is documented [here](developerguide/release).  For the time being, Cam is the only one who will make releases. 

## Optional branches

There are three other possible branch types that you can choose to use if you want: feature, release and hotfix. 

*Feature branches* are used to develop new features for the upcoming or
a distant future release.

*Release branches* support preparation of a new production release. They
allow for last-minute dotting of i’s and crossing t’s. Furthermore,
they allow for minor bug fixes and preparing meta-data for a release
(version number, build dates, etc.). By doing all of this work on a
release branch, the develop branch is cleared to receive features for
the next big release. 

*Hotfix branches* are very much like release branches in that they are
also meant to prepare for a new production release, albeit
unplanned. They arise from the necessity to act immediately upon an
undesired state of a live production version.

## Continuous integration

Both the master and develop branches are under [continuous integration control using Travis-CI](https://travis-ci.org/wattdepot/wattdepot).

All developers should be notified by email when the build fails on either branch. If you are able to commit changes to the WattDepot repository, be sure that you are notified of build failures. 

## The docs/ directory

Commits to the WattDepot documentation directory (/docs) deviates from the standard nvie branching model.

In WattDepot, it is fine to commit /docs changes directly to master if the updates immediately improve the public documentation (i.e. [http://wattdepot.org](http://wattdepot.org)).  On the other hand, if your changes are specific to an upcoming release (i.e. an API change), then you can commit the changes to the develop branch (or to a feature branch).





