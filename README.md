# which-side-service

Determines which side of the street the car should be parked on during alternate side parking restrictions.

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server
    
Specify the side of the street your house is on. For example, 1234 Main St. would be on the *even* side.

    $ curl localhost:3000/even
    in front of house.
    
    $ curl localhost:3000/odd
    across the street.
    
## Notes

If the current time is between noon and midnight, we are determining what side of the street the car should be parked on for *tomorrow*.

## License

Copyright © 2019 FIXME
