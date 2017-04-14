import requests
import re
from lxml import html
import unicodedata

textFile = open('countriesAverageTemperatures.txt', 'w')

# the page containing the links to the pages with the content
startPage = requests.get('http://www.weatherbase.com/weather/countryall.php3')
startTree = html.fromstring(startPage.content)

# all the links we want to visit
followLinks = startTree.xpath('//div/ul/li/a')

# use the <a> tag to get the link
for aTag in followLinks:
    link = aTag.xpath('.//@href')

    # get the page of a specific country using the link; also request Celsius
    contentPage = requests.get(str('http://www.weatherbase.com' + link[0] + '&set=metric'))
    contentTree = html.fromstring(contentPage.content)

    # start the output line with the name of the country
    dataLine = contentTree.xpath('//div[@id="left-content"]//div[@id="headerfont"]/text()')
    listOfCells = contentTree.xpath('//table//tr[2]/td[@class="datac"]/text()')

    # go through the cells and add each one to the list, but skip the last 2 from the end
    for cell in listOfCells[:-2]:
        dataLine.append(cell)

    print(str(dataLine) + ' # Number of items: ' + str(len(dataLine)))
    textFile.write(str(dataLine) + '\n')

textFile.close()
