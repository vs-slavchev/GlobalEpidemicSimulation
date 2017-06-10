import requests
import re
from lxml import html
import unicodedata

def cleanPopulation(text):
    return ''.join(list(filter(lambda ch: ch not in [",", "."], text)))

textFile = open('cities.txt', 'w')

countriesListPage = requests.get('http://data.mongabay.com/igapo/largest_cities.htm')
countriesListTree = html.fromstring(countriesListPage.content)

# 'a' tags of all the countries
countryEntries = countriesListTree.xpath('//tr/td/font/a')
for country in countryEntries:

    countryName = country.xpath('.//text()')[0]
    dataLine = "country#" + countryName + '\n'
    textFile.write(dataLine)
    print(dataLine)

    citiesListPage = requests.get(country.xpath('.//@href')[0])
    citiesListTree = html.fromstring(citiesListPage.content)

    cityRows = citiesListTree.xpath('//table[@id="myTable"]//tr')
    # 20% of len(cityRows) or at least 5
    for city in cityRows[:max(int(len(cityRows)/5), 5)]:
        cityPage = requests.get(city.xpath('.//td/a/@href')[0])
        cityTree = html.fromstring(cityPage.content)

        cityName = city.xpath('.//td/a/text()')[0]
        cityPopulation = city.xpath('.//td[2]/text()')[0]
        cityLocationLink = ""
        try:
            cityLocationLink = cityTree.xpath('//a[contains(text(), "Google Earth")]/@href')[0]
        except IndexError:
            continue

        # to extract the coordinates
        regexCaptureCoordinates = re.compile(".*&ll=([\d.-]+),([\d.-]+)&.*")
        cityLatitude = regexCaptureCoordinates.match(cityLocationLink).group(1)
        cityLongitude = regexCaptureCoordinates.match(cityLocationLink).group(2)

        dataLine = "city#" + cityName + "#" + cleanPopulation(cityPopulation) + "#" + cityLatitude + "#" + cityLongitude + '\n'
        textFile.write(dataLine)
        print(dataLine)

textFile.close()
