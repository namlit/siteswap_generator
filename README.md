# Description

This is an Andoid App for generating juggling [Siteswaps](https://en.wikipedia.org/wiki/Siteswap), especially designed for passing Siteswaps. The following features are supported by the App:

- Find all possible Siteswaps for a given period length and number of objects
- Easy filtering of zips, zaps and holds via checkboxes
- Automatic filtering of non-passable patterns
- Support for pattern and interface filters
- Generation of local Siteswaps and calculation of start position
- Automatical determination of good start positions
- Support for asynchronouos passing patters with more than two jugglers
- Generation of very long patters in a random mode

# Get the App

The Siteswap Generator App is currently available as a beta version in the google Playstore. You can enter the beta testing and download the App with the following link: <https://play.google.com/apps/testing/namlit.siteswapgenerator>
# How to use the App

## Generating Siteswaps

For the generation of Siteswaps the following basic parameters can be adjusted:

*Number of Objects:* The number of objects for the passing pattern. The default value on first App startup is seven.

*Period Length:* The period length of the juggling pattern. The default value is five, but also very long periods are possible. For long periods the [Random Generation Mode](#using-the-random-generation-mode) can be used.

*Max Throw:* The maximum throw heigh to be considered.

*Min Throw:* The minimum throw heigh to be considered. For passing siteswaps with two jugglers and no empty hands this can usually be set to two.

*Number of Jugglers:* The number of jugglers for the passing pattern. The number of jugglers is only used for correct filtering and generation of local Siteswaps. For the actual pattern generation, the number of siteswaps not used.

*Max Results:* The maximum number of juggling patterns to be generated. The Siteswap generation algorithm will be aborted, when the maximum number of results is reached. Generating a very large number of Siteswaps can lead to long generation times.

*Timeout:* The maximum time for Siteswap generation. When the time limit is reached, the Siteswap generation algorithm will be aborted. For generation of a large number of Siteswaps, this value can be increased

## Filtering Siteswaps

The generated Siteswaps are filted during the generation process according to a list of Siteswap filters. This filters consists of a set of default filters and can be extended by custom filters.

### Default Filters

Some filters are always added automatically. Especially threes (respectively passes with local value smaller when four for patterns with more than two jugglers) are automatically excluded and only patterns with at least one pass are considered. Additionally zips, zaps and holds can be included or excluded by checkboxes and the filters are added automatically. By pressing the "Reset Filters" button, all custom filters are removed and the default filters are reapplied according to the checkboxes.

The default filters can be removed by selecting the respective entry in the filter table and press the remove button in the opened filter dialog. This is for example useful, if you want to generate passing Siteswaps including threes.

### Number Filters

With number filters, it is possible, to specify a minimum, maximum or exact number for a specific throw height. This is also possible for passes and selfs. For example you could filter for Siteswaps with at least two passes, not more than three single selfs (6) and exactly one heff (8).

### Pattern filters

With pattern filters it is possible to filter for patterns like "pass pass self pass selt". Generally there is a differentiation between local and global patters. Note that that the throw height always needs to be given in global notation here. If you for example want to exclude patterns with two consecutive holds, you can exclude the local pattern "44". If you never want to have more than two passes in a row, you can exclude the local pattern "pass pass pass".

#### Compatible Interfaces

While a Siteswap normally describes when the objects are thrown, the interface describes, when the objects are landing. What makes interfaces interesting is, that all patterns with the same interface are compatible and can therefore be combinded. This is very useful for jugglers with a different juggling level. For example you could agree on the global interface "ppsss" and while juggler one chooses a challanging 8-club pattern, juggler two could stick to a 6-club pattern.

It is also possible to setup feeds with compatible interfaces. For example the feeder could choose the global interface "ppsps" and choose an according 7-club pattern. One feedy could then choose a 7-club pattern with the interface "pssss" and the other juggler a 6-club pattern with the interface "spsps". Note that for every pass of the feeder, exactly one feedy needs to have a pass.

When creating interfaces take into account that, for an odd period length, an odd number of objects results in an odd number of passes. Therefore there are for example no 7-club patterns with the inteface "ppsss".

Another interesting fact about Intefaces is, that an inteface read backwards is always a valid Siteswap. This is similar to playing a video backwards.

## Using the Random Generation Mode

Generating very long Siteswaps usually results in very long generation times. Additionally the number of possible Siteswaps increases exponentionally. For example there are 463.000 passing Siteswaps of length 13 with seven objects and a maximum throw height of 10. When those Siteswaps are generated in a systematically way, the first Siteswaps are typically quite boring. For the above example the list of generated Siteswaps would start with "8677777777777, 8677778677777, 8677786777777, 8677867777777, ...". When you would just generate a hundred patterns, all would probably loog similar boring. Therefore the Siteswap Generator App supports a Random Generation Mode, which can be activated with a checkbox. In the random mode, the patterns are generated in a random order, resulting in much more interesting patterns. This way, the maximum number of results can be limited to for example 10 to get short generation times even for very long patterns. I could easily generate patterns of length 100 or even more with this mode (Who ever is going to juggle this).

Note that the Random Generation Mode does not check for duplicate Siteswaps. The algorithm is only aborted, when the maximum number of results or the timeout is reached. Therefore it makes no sense to use this mode for short periods, where all possible patterns can easily be generated and displayed. This would result in many duplicate patterns.

## Detailed Siteswap View with Local Siteswaps

When clicking on a Siteswap, a detailed view of the Siteswap including a local representation is given. A good starting position is automitically determined, but the Siteswap can also manually be rotated to altenative starting positions. To simplify finding a starting position for compatible Siteswaps, the interface is calculated as well. All patterns are started from juggler A.
