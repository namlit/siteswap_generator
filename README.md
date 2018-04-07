# Description

This is an Andoid App for generating juggling [Siteswaps](https://en.wikipedia.org/wiki/Siteswap), especially designed for passing. The following features are supported:

- Find all possible Siteswaps for a given period length and number of objects
- Easy filtering of zips, zaps and holds via check boxes
- Automatic filtering of non-passable patterns
- Support for pattern and interface filters
- Generation of local Siteswaps and calculation of start position
- Support for asynchronous passing patters with more than two jugglers
- Generation of very long patterns in a random mode

# Get the App

<a href="https://f-droid.org/packages/namlit.siteswapgenerator/" target="_blank">
<img src="https://f-droid.org/badge/get-it-on.png" alt="Get it on F-Droid" height="80"/></a>
<a href="https://play.google.com/store/apps/details?id=namlit.siteswapgenerator" target="_blank">
<img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" alt="Get it on Google Play" height="80"/></a>

## Becoming a Beta Tester

App updates are usually released as beta versions first. To get the newest features as soon as they are available you can enter the beta testing with the following link: <https://play.google.com/apps/testing/namlit.siteswapgenerator>

# How to use the App

## Generating Siteswaps

The following parameters need to be adjusted for Siteswap generation:

<dl>
  <dt>Number of Objects:</dt>
  <dd>
    The number of objects for the passing pattern. The default value on first App startup is seven.
  </dd>

  <dt>Period Length:</dt>
  <dd>
    The period length of the juggling pattern. The default value is five, but also very long periods are possible. For long periods the [Random Generation Mode](#using-the-random-generation-mode) can be used.
  </dd>

  <dt>Max Throw:</dt>
  <dd>
    The maximum throw height to be considered.
  </dd>

  <dt>Min Throw:</dt>
  <dd>
    The minimum throw height to be considered. For passing Siteswaps with two jugglers and no empty hands this can usually be set to two.
  </dd>

  <dt>Number of Jugglers:</dt>
  <dd>
    The number of jugglers for the passing pattern. The number of jugglers is only used for correct filtering and generation of local Siteswaps. The Siteswap generation algorithm itself does not use this parameter.
  </dd>

  <dt>Max Results:</dt>
  <dd>
    The maximum number of juggling patterns to be generated. The Siteswap generation algorithm will be aborted, when the maximum number of results is reached. Generating a very large number of Siteswaps can lead to long generation times.
  </dd>

  <dt>Timeout:</dt>
  <dd>
    The maximum time for Siteswap generation. When the time limit is reached, the Siteswap generation algorithm is aborted. For generation of a large number of Siteswaps, this value can be increased
  </dd>
</dl>

## Filtering Siteswaps

The generated Siteswaps are filtered during the generation process according to a list of Siteswap filters. A set of default filters exists, which can be extended by custom filters.

### Default Filters

The default filters are always added automatically. Passes faster when a zap are excluded and only patterns with at least one pass are considered. Additionally zips, zaps and holds can be included or excluded by check boxes. By pressing the "Reset Filters" button, all custom filters are removed and only the default filters remain in the filter list.

The default filters can be removed by selecting the respective entry in the filter list and pushing the remove button in the now opened dialog. One use case might be to generate passing Siteswaps including threes.

### Number Filters

With number filters, it is possible, to specify a minimum, maximum or exact number for a specific throw height. This is also possible for passes and selfs. For example you could filter for Siteswaps with at least two passes, not more than three single selfs (6) and exactly one heff (8).

### Pattern filters

With pattern filters it is possible to filter for patterns like "pass pass self pass self". Generally there is a differentiation between local and global patters. Note that the throw height always needs to be given in global notation here. If you for example want to exclude patterns with two consecutive holds, you can exclude the local pattern "44". If you never want to have more than two passes in a row, you can exclude the local pattern "ppp".

Generally for patterns with an odd period length, an odd number of objects always results in an odd number of passes. Therefore there are for example no 7-club patterns with the pattern "ppsss".

#### Compatible Interfaces

While a Siteswap normally describes when the objects are thrown, the interface describes, when the objects are caught. What makes interfaces interesting is that all patterns with the same interface are compatible and can therefore be combined. This is very useful for jugglers with a different juggling level. For example you could agree on the global interface "ppsss" and while one juggler chooses a challenging 8-club pattern, the other juggler could stick to a 6-club pattern.

It is also possible to setup feeds with compatible interfaces. For example the feeder could choose the global interface "ppsps" and juggle an according 7-club pattern. One feedy could then choose a 7-club pattern with the interface "pssss" and the other juggler a 6-club pattern with the interface "spsps". Note that for every pass of the feeder, exactly one feedy needs to have a pass.

Another interesting fact about interfaces is, that an interface read backwards is always a valid Siteswap. This is similar to playing a video backwards.

## Using the Random Generation Mode

Generating very long Siteswaps usually results in very long generation times. Additionally the number of possible Siteswaps increases exponentially. For example there are 463.000 passing Siteswaps of length 13 with seven objects and a maximum throw height of 10. When those Siteswaps are generated in a systematically way, the first Siteswaps are typically quite boring. For the above example the list of generated Siteswaps would start with "8677777777777, 8677778677777, 8677786777777, 8677867777777, ...". When you would just generate a hundred patterns, all would probably look similar boring. Therefore the Siteswap Generator App supports a Random Generation Mode. In this mode, the patterns are generated in a random order, resulting in much more interesting patterns. This way, the maximum number of results can be limited to a small value like ten to get short generation times even for very long patterns. This way the generation of interesting patterns with lengths of more than a hundred is easily possible (Who ever is going to juggle this).

Note that the Random Generation Mode does not check for duplicate Siteswaps. The algorithm is only aborted, when the maximum number of results or the timeout is reached. Therefore it makes no sense to use this mode for short periods, where all possible patterns can easily be generated and displayed. The result would be many duplicate patterns.

## Detailed Siteswap View with Local Siteswaps

When clicking on a Siteswap, a detailed view including a local representation is given. A good starting position is automatically determined, but the Siteswap can also manually be rotated to alternative starting positions. To simplify finding a starting position for compatible Siteswaps, the interface is calculated as well. All patterns are started from juggler A.
