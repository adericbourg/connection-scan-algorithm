Connection scan algorithm
=========================

## Current algorithm

A Scala implementation based on [Capitaine Train's challenge](https://github.com/capitainetrain/csa-challenge). I'm using their own data (provided on their Git repository) for testing purpose.

To run the application using the provided data, untar them:

    tar xzf bench_data_48h.tar.gz


For more information abou this algorithm, you ay [read the paper on the Karlsruhe University website](http://i11www.iti.uni-karlsruhe.de/extra/publications/dpsw-isftr-13.pdf) (PDF) or the thesis [Delay-Robust Stochastic Routing In Timetable Networks](http://i11www.iti.uni-karlsruhe.de/_media/teaching/theses/da-strasser12.pdf) (PDF). Quite hard to read for an humble developper like me, though.

## RATP data

Parsers are written for [RATP data](http://data.ratp.fr/). Since it is more interesting to work on real data, I should use them soon into the CSA.
