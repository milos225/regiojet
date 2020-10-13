# This project contains 2 tests:
- ## searchingUsingWeb():
Test open page and filters routes by params, then expands routes for:
fastest arrival time,
shortest time travel,
lowest price.
Then you can check values in console output vs. values in browser.

SECONDS_COUNT contains value, how long time you need for check console vs. browser.

- ## searchingUsingApi():
In console output you can see all filtered routes with small details.

## If you have some problems, check:
JDK settings

proxy

version chromedriver (now is set chromedriver ver.84, if you have newer etc. 86, change this property

`props.setProperty("webdriver.chrome.driver", "src\\test\\java\\drivers\\chromedriver_86.exe");`

refresh gradle dependency

or contact me