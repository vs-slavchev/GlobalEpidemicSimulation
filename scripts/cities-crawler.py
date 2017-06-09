import requests
import re
from lxml import html
import unicodedata

# textFile = open('countriesAverageTemperatures.txt', 'w')

countriesListPage = requests.get('http://data.mongabay.com/igapo/largest_cities.htm')
countriesListTree = html.fromstring(countriesListPage.content)

# 'a' tags of all the countries
countryEntries = countriesListTree.xpath('//tr/td/font/a')
for country in countryEntries[:2]:

    countryName = country.xpath('.//text()')[0]
    print(countryName)
    citiesListPage = requests.get(country.xpath('.//@href')[0])
    citiesListTree = html.fromstring(citiesListPage.content)

    cityRows = citiesListTree.xpath('//table[@id="myTable"]//tr')
    # 20% of len(cityRows) then min it with 3?
    for city in cityRows:
        cityPage = requests.get(city.xpath('.//td/a/@href')[0])
        cityTree = html.fromstring(cityPage.content)

        cityName = city.xpath('.//td/a/text()')[0]
        cityPopulation = city.xpath('.//td[2]/text()')[0]
        cityLocationLink = cityTree.xpath('//a[contains(text(), "Google Earth")]/@href')[0]

        # to extract the coordinates
        regexCaptureCoordinates = re.compile(".*&ll=([\d.,]+)&.*")
        cityCoordinates = regexCaptureCoordinates.match(cityLocationLink).group(1)

        print("    " + cityName + " " + cityPopulation + " " + cityCoordinates)




    # print(str(dataLine) + ' # Number of items: ' + str(len(dataLine)))
    # textFile.write(str(dataLine) + '\n')

# textFile.close()
